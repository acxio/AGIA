package fr.acxio.tools.agia.alfresco;

/*
 * Copyright 2014 Acxio
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLCreateAssociation;
import org.alfresco.webservice.types.CMLDelete;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.CMLWriteContent;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import fr.acxio.tools.agia.alfresco.domain.Aspect;
import fr.acxio.tools.agia.alfresco.domain.Association;
import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.alfresco.domain.Property;
import fr.acxio.tools.agia.alfresco.domain.QName;
import fr.acxio.tools.agia.alfresco.domain.QueryAssociation;
import fr.acxio.tools.agia.alfresco.domain.RefAssociation;

/**
 * <p>A advanced {@link org.springframework.batch.item.ItemWriter ItemWriter}
 * for Alfresco that can write
 * {@link fr.acxio.tools.agia.alfresco.domain.NodeList NodeList} to an Alfresco
 * instance.</p>
 * 
 * <p>Stateless, so restartable.</p>
 * 
 * <p>WARNING: using an async executor may induce race condition on folders
 * creation because one can be created in a first transaction when the same
 * one is also created in a second transaction. The first transaction may
 * succeed, and then the second one will fail because the folder already
 * exists.</p>
 * 
 * <p>If an object is set to be replaced, a first transaction is executed to
 * delete it, then a second one is executed to create it.</br>
 * If the second transaction fails, the deleted objects are not restored.</p>
 * 
 * <p>If a document does not have the versionable aspect but is set to be
 * versioned, the new version will overwrite the current one and it will
 * have the versionable aspect so that next version will not overwrite it.</p>
 * 
 * <p>The contents are send like properties into the transaction.</p>
 * 
 * @author pcollardez
 *
 */
public class AlfrescoNodeWriter extends AlfrescoServicesConsumer implements ItemWriter<NodeList> {

	// WARNING : using an Async Executor may induce race condition on folders creation
	// Some documents may be created, some other may not (probably a pb with the cache)
	// Since a nice fix is done, use a SyncTaskExecutor !
	
	private static Logger logger = LoggerFactory.getLogger(AlfrescoNodeWriter.class);
	
	private boolean sendContents = true;
	
	public void setSendContents(boolean sSendContents) {
		sendContents = sSendContents;
	}

	@Override
	public void write(List<? extends NodeList> sData) throws NodePathException, VersionOperationException, IOException {
		if (!sData.isEmpty()) {
			init();
			RepositoryServiceSoapBindingStub repositoryService = getAlfrescoService().getRepositoryService();
			CMLHelper aCMLHelper = new CMLHelper(); // Prepare only one transaction
			
			// Maps ID of each node and References used by associations
			Map<Node, Integer> aNodesIndexes = buildNodesIndexes(sData);
			String aCurrentNodePath;
			Map<String, List<Integer>> aNodesRefIndexes;
			
			for(NodeList aNodeList : sData) { // each NodeList represents an input record
				aNodesRefIndexes = buildNodesRefIndexes(sData, aNodesIndexes, aNodeList);
				
				for(Node aNode : aNodeList) {
					aCurrentNodePath = aNode.getPath();
					
					// 1. L'objet n'est pas dans Alf et donc pas dans le cache => création en toute circonstance
					// 2. L'object est dans Alf mais pas dans le cache => cache mis à jour, opération quelconque
					// 3. L'object est dans Alf et dans le cache => on a besoin d'un cache dans le context de la transaction pour réutiliser le même objet CML
					// 4. L'object n'est plus dans Alf mais est dans le cache => on aura une erreur au commit, auquel cas il faut purger le cache et recommencer
					//    et si on a une 2eme erreur, alors l'opération est réellement en erreur
					
					if (!aCMLHelper.isPathExist(aCurrentNodePath)) { // If the path already exists in the current transaction, dont add the node another time
		                createOrUpdateNode(repositoryService, aCMLHelper,
								aNodesIndexes, aNodesRefIndexes,
								aCurrentNodePath, aNode);
		                aCMLHelper.addExistingPath(aCurrentNodePath);
					}
				}
			}
	
			if (logger.isDebugEnabled()) {
				logger.debug("Will commit");
			}
			
	    	UpdateResult[] result = repositoryService.update(aCMLHelper.getCML());
	    	
	    	// Update the cache of paths and references
	    	updateDataWithResult(sData, aNodesIndexes, result);
	        
	        if (logger.isDebugEnabled()) {
	        	logger.debug("Commited");
	        }
	        
	        cleanup();
		}
	}

	protected void createOrUpdateNode(
			RepositoryServiceSoapBindingStub sRepositoryService,
			CMLHelper sCMLHelper, Map<Node, Integer> sNodesIndexes,
			Map<String, List<Integer>> sNodesRefIndexes,
			String sCurrentNodePath, Node sNode) throws NodePathException,
			VersionOperationException, IOException, RemoteException {
		org.alfresco.webservice.types.Node[] nodes = getRepositoryMatchingNodes(sRepositoryService, sCurrentNodePath);
		
		if ((nodes != null) && (nodes.length > 0)) {
			if (nodes.length > 1) {
				throw new VersionOperationException("Too many matching nodes");
			}
			org.alfresco.webservice.types.Node aRepositoryNode = nodes[0];
			Predicate sWhereNode = new Predicate(new Reference[] {aRepositoryNode.getReference()}, null, null);
			
			// If this is a folder, just ignore it or update props ?
			// If this is a document, replace, version or leave it as it is
			
			if (sNode instanceof Document) {
				
				updateDocument(sRepositoryService,
						sCMLHelper, sNodesIndexes,
						sCurrentNodePath, sNodesRefIndexes,
						sNode, sWhereNode);
				
			} else if (sNode instanceof Folder) {
				
				updateFolder(sRepositoryService,
						sCMLHelper, sNodesIndexes,
						sCurrentNodePath, sNodesRefIndexes,
						sNode, sWhereNode);
			}
			
		} else {
			createNewNode(sRepositoryService, sCMLHelper,
					sNodesIndexes, sCurrentNodePath,
					sNodesRefIndexes, sNode);
		}
	}

	protected void updateDataWithResult(List<? extends NodeList> sData,
			Map<Node, Integer> sNodesIndexes, UpdateResult[] sResult)
			throws NodePathException {
		Map<String, Node> aIndexes = buildIndexesNodesRef(sNodesIndexes);
		for(UpdateResult aUpdateResult : sResult) {
			if (aUpdateResult.getDestination() != null) {
				Reference aSource = aUpdateResult.getSource();
				Reference aDestination = aUpdateResult.getDestination();

				if (aUpdateResult.getSourceId() != null) {
					Node aNode = aIndexes.get(aUpdateResult.getSourceId());
					if (aNode != null) {
						// If the source is null, it is a "create" statement,
						// and the node reference is in the destination
						aNode.setScheme((aSource != null) ? aSource.getStore().getScheme() : aDestination.getStore().getScheme());
						aNode.setAddress((aSource != null) ? aSource.getStore().getAddress() : aDestination.getStore().getAddress());
						aNode.setUuid((aSource != null) ? aSource.getUuid() : aDestination.getUuid());
					}
				}
				for(NodeList aNodeList : sData) {
					for(Node aNode : aNodeList) {
						// TODO : cache the getPath result
						if ((aUpdateResult.getSourceId() == null) && (aSource != null) && aNode.getPath().equals(aSource.getPath())) {
							aNode.setScheme(aSource.getStore().getScheme());
	    					aNode.setAddress(aSource.getStore().getAddress());
	    					aNode.setUuid(aSource.getUuid());
						}
						if ((aDestination != null) && aNode.getPath().equals(aDestination.getPath())) {
							aNode.setScheme(aDestination.getStore().getScheme());
	    					aNode.setAddress(aDestination.getStore().getAddress());
	    					aNode.setUuid(aDestination.getUuid());
						}
					}
				}
				
				org.alfresco.webservice.types.Node aNewRepositoryNode = new org.alfresco.webservice.types.Node();
				aNewRepositoryNode.setReference(aDestination);
				setLocalMatchingNodes(aDestination.getPath(), new org.alfresco.webservice.types.Node[] {aNewRepositoryNode});
			}
		}
	}

	protected void createNewNode(
			RepositoryServiceSoapBindingStub sRepositoryService,
			CMLHelper sCMLHelper, Map<Node, Integer> sNodesIndexes,
			String sCurrentNodePath,
			Map<String, List<Integer>> sNodesRefIndexes, Node sNode)
			throws IOException {
		
		// Create a new node
		if (logger.isDebugEnabled()) {
			logger.debug("Will add node " + sNode.getId());
		}
		
		addCreateForNode(sCMLHelper, sNode, sNodesIndexes.get(sNode));
		addAssociationsForNode(sCMLHelper, sNode, null, sNodesRefIndexes, sNodesIndexes.get(sNode));
		if (sNode instanceof Document) {
			if (sNode.getVersionOperation().equals(Node.VersionOperation.VERSION)) {
				sCMLHelper.addAddAspect(new CMLAddAspect(Constants.ASPECT_VERSIONABLE, null, null, sNodesIndexes.get(sNode).toString()));
			}
			addContentForNode(sCMLHelper, (Document)sNode, null, sNodesIndexes.get(sNode));
		}
		
		// Clear the cache for the current node to force reload it
		evictRepositoryNode(sCurrentNodePath);
	}

	protected void updateFolder(
			RepositoryServiceSoapBindingStub sRepositoryService,
			CMLHelper sCMLHelper, Map<Node, Integer> sNodesIndexes,
			String sCurrentNodePath,
			Map<String, List<Integer>> sNodesRefIndexes, Node sNode,
			Predicate sWhereNode) throws RemoteException {
		
		// Node.VersionOperation.RAISEERROR : Do nothing
		if (sNode.getVersionOperation().equals(Node.VersionOperation.REPLACE)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Will replace folder node " + sNode.getId());
			}
			// WARNING : The node is deleted OUT OF THE CURRENT TRANSACTION !!!
			//           But they are sent to the deleted items of the user used to connect to Alfresco
			// FIXME : will fail if the folder is not empty
			CML aCML = new CML();
			aCML.setDelete(new CMLDelete[] { new CMLDelete(sWhereNode) });
			sRepositoryService.update(aCML);
			
			addCreateForNode(sCMLHelper, sNode, sNodesIndexes.get(sNode));
			addAssociationsForNode(sCMLHelper, sNode, null, sNodesRefIndexes, sNodesIndexes.get(sNode));
			
			// Clear the cache for the current node to force reload it
			evictRepositoryNode(sCurrentNodePath);
			
		} else if (sNode.getVersionOperation().equals(Node.VersionOperation.UPDATE) || sNode.getVersionOperation().equals(Node.VersionOperation.VERSION)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Will skip folder node " + sNode.getId());
			}
			// Folders are not versionable, just update them
			sCMLHelper.addUpdate(new CMLUpdate(getProperties(sNode), sWhereNode, null));
			addAspectsForNode(sCMLHelper, sNode, sWhereNode, 0);
			addAssociationsForNode(sCMLHelper, sNode, sWhereNode, sNodesRefIndexes, 0);
			
			// Clear the cache for the current node to force reload it
			evictRepositoryNode(sCurrentNodePath);
		}
	}

	protected void updateDocument(
			RepositoryServiceSoapBindingStub sRepositoryService,
			CMLHelper sCMLHelper, Map<Node, Integer> sNodesIndexes,
			String sCurrentNodePath,
			Map<String, List<Integer>> sNodesRefIndexes, Node sNode,
			Predicate sWhereNode) throws VersionOperationException,
			IOException {
		
		if (sNode.getVersionOperation().equals(Node.VersionOperation.RAISEERROR)) {
			throw new VersionOperationException("Node already exists " + sNode.getId());
		} else if (sNode.getVersionOperation().equals(Node.VersionOperation.REPLACE)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Will replace document node " + sNode.getId());
			}
//		    aCMLHelper.addDelete(new CMLDelete(sWhereNode)); // THIS DOES NOT WORK because every CMLCreate happen BEFORE every CMLDelete
			
			// WARNING : The node is deleted OUT OF THE CURRENT TRANSACTION !!!
			//           But they are sent to the deleted items of the user used to connect to Alfresco
			CML aCML = new CML();
			aCML.setDelete(new CMLDelete[] { new CMLDelete(sWhereNode) });
			sRepositoryService.update(aCML);
			
			addCreateForNode(sCMLHelper, sNode, sNodesIndexes.get(sNode));
			addAssociationsForNode(sCMLHelper, sNode, null, sNodesRefIndexes, sNodesIndexes.get(sNode));
			addContentForNode(sCMLHelper, (Document)sNode, null, sNodesIndexes.get(sNode));
		} else if (sNode.getVersionOperation().equals(Node.VersionOperation.UPDATE)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Will update document node " + sNode.getId());
			}
			// WARNING : Updating a versionable node will create a new version
			sCMLHelper.addUpdate(new CMLUpdate(getProperties(sNode), sWhereNode, null));
			addAspectsForNode(sCMLHelper, sNode, sWhereNode, 0);
			addAssociationsForNode(sCMLHelper, sNode, sWhereNode, sNodesRefIndexes, 0);
			addContentForNode(sCMLHelper, (Document)sNode, sWhereNode, 0);
		} else if (sNode.getVersionOperation().equals(Node.VersionOperation.VERSION)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Will create a new version of document node " + sNode.getId());
			}
			// WARNING : the update will not create a version if there is not property or content change
			// WARNING : the aspect is added AFTER the properties or content change
			//           so the document will not retain its current version and the new one will update it
			
			sCMLHelper.addAddAspect(new CMLAddAspect(Constants.ASPECT_VERSIONABLE, null, sWhereNode, null));
			sCMLHelper.addUpdate(new CMLUpdate(getProperties(sNode), sWhereNode, null));
			addAspectsForNode(sCMLHelper, sNode, sWhereNode, 0);
			addAssociationsForNode(sCMLHelper, sNode, sWhereNode, sNodesRefIndexes, 0);
			addContentForNode(sCMLHelper, (Document)sNode, sWhereNode, 0);
		}
		// Clear the cache for the current node to force reload it
		evictRepositoryNode(sCurrentNodePath);
	}

	protected Map<String, Node> buildIndexesNodesRef(Map<Node, Integer> sNodesRefIndexes) {
		Map<String, Node> aIndexesNodesRef = null;
		if (sNodesRefIndexes != null) {
			aIndexesNodesRef = new HashMap<String, Node>(sNodesRefIndexes.size());
			for(Map.Entry<Node, Integer> aEntry : sNodesRefIndexes.entrySet()) {
				aIndexesNodesRef.put(aEntry.getValue().toString(), aEntry.getKey());
			}
		}
		return aIndexesNodesRef;
	}
	
	protected Map<String, List<Integer>> buildNodesRefIndexes(
			List<? extends NodeList> sData, Map<Node, Integer> sNodesIndexes,
			NodeList sNodeList) {
		Map<String, List<Integer>> aNodesRefIndexes;
		List<Integer> aTargetIds;
		aNodesRefIndexes = new HashMap<String, List<Integer>>(sData.size());
		for(Node aNode : sNodeList) {
			if ((aNode.getAssocTargetId() != null) && !aNode.getAssocTargetId().isEmpty()) {
				aTargetIds = aNodesRefIndexes.get(aNode.getAssocTargetId());
				if (aTargetIds == null) {
					aTargetIds = new ArrayList<Integer>(1);
				}
				aTargetIds.add(sNodesIndexes.get(aNode));
				aNodesRefIndexes.put(aNode.getAssocTargetId(), aTargetIds);
			}
		}
		return aNodesRefIndexes;
	}

	protected Map<Node, Integer> buildNodesIndexes(List<? extends NodeList> sData) {
		int aNodeIndex = 0;
		Map<Node, Integer> aNodesIndexes = new HashMap<Node, Integer>(sData.size());
		for(NodeList aNodeList : sData) {
			for(Node aNode : aNodeList) {
				if (!aNodesIndexes.containsKey(aNode)) {
					aNodesIndexes.put(aNode, aNodeIndex);
					aNodeIndex++;
				}
			}
		}
		return aNodesIndexes;
	}
		
	private NamedValue[] getProperties(Node sNode) {
		List<Property> aProperties = sNode.getProperties();
        NamedValue[] aProps = new NamedValue[aProperties.size()];
        int i = 0;
        List<String> aPropValues;
        for(Property aProperty : aProperties) {
        	aPropValues = aProperty.getValues();
        	if (aPropValues.size() > 1) {
        		String[] aValues = aPropValues.toArray(new String[] {});
        		aProps[i] = Utils.createNamedValue(aProperty.getName().toString(), aValues);
        	} else {
        		// FIXME : if the property has no value, it is different than having a value set to null
        		String aValue = (aPropValues.size() == 0) ? null : aPropValues.get(0);
        		aProps[i] = Utils.createNamedValue(aProperty.getName().toString(), aValue);
        	}
        	i++;
        }
        return aProps;
	}
	
	private void addCreateForNode(CMLHelper sCMLHelper, Node sNode, int sNodeIndex) {
		NamedValue[] aProps = getProperties(sNode);
    	ParentReference aParentReference = new ParentReference(STORE, null, sNode.getParent().getPath(), Constants.ASSOC_CONTAINS, new QName(sNode.getType().getNamespaceURI(), sNode.getName()).toString());
    	String aNodeType = (sNode.getType() == null) ? Constants.TYPE_CONTENT : sNode.getType().toString();
    	sCMLHelper.addCreate(new CMLCreate(Integer.toString(sNodeIndex), aParentReference, null, null, null, aNodeType, aProps));
    	
    	addAspectsForNode(sCMLHelper, sNode, null, sNodeIndex);
	}
	
	private void addAspectsForNode(CMLHelper sCMLHelper, Node sNode, Predicate sPredicate, int sNodeIndex) {
		List<Aspect> aAspectsList = sNode.getAspects();
    	if (aAspectsList.size() > 0) {
    		for(Aspect aAspect : aAspectsList) {
    			sCMLHelper.addAddAspect(new CMLAddAspect(aAspect.getName().toString(), null, sPredicate, ((sPredicate != null) ? null : Integer.toString(sNodeIndex))));
    		}
    	}
	}
	
	private void addContentForNode(CMLHelper sCMLHelper, Document sDocument, Predicate sPredicate, int sNodeIndex) throws IOException {
		if (sendContents && (sDocument.getContentPath() != null) && (sDocument.getContentPath().length() > 0)) {
			// If the document has a content path, the file must exist
			File aFile = new File(sDocument.getContentPath());
			if (aFile.exists() && aFile.isFile()) {
				FileInputStream aInputStream = new FileInputStream(aFile);
				try {
					sCMLHelper.addWriteContent(new CMLWriteContent(Constants.PROP_CONTENT, ContentUtils.convertToByteArray(aInputStream), new ContentFormat(sDocument.getMimeType(), sDocument.getEncoding()), sPredicate, ((sPredicate != null) ? null : Integer.toString(sNodeIndex))));
				} catch (Exception e) {
					throw new IOException("Cannot convert and write content", e);
				}
			} else {
				throw new IOException("Cannot find content file: " + sDocument.getContentPath());
			}
		}
	}
	
	private void addAssociationsForNode(CMLHelper sCMHelper, Node sNode, Predicate sPredicate, Map<String, List<Integer>> sNodesRefIndexes, int sNodeIndex) {
		List<Association> aAssociationList = sNode.getAssociations();
		if (aAssociationList.size() > 0) {
			for(Association aAssociation : aAssociationList) {
				if (aAssociation instanceof RefAssociation) {
					List<Integer> aTargetIndex = sNodesRefIndexes.get(((RefAssociation) aAssociation).getReference());
					if (aTargetIndex != null) {
						for(Integer aIndex : aTargetIndex) {
							sCMHelper.addCreateAssociation(new CMLCreateAssociation(sPredicate, ((sPredicate != null) ? null : Integer.toString(sNodeIndex)), null, aIndex.toString(), aAssociation.getType().toString()));
						}
					}
				} else if (aAssociation instanceof QueryAssociation) {
					Query aQuery = new Query(((QueryAssociation) aAssociation).getQueryLanguage(), ((QueryAssociation) aAssociation).getQuery());
					Predicate aQueryPredicate = new Predicate(null, STORE, aQuery);
					sCMHelper.addCreateAssociation(new CMLCreateAssociation(sPredicate, ((sPredicate != null) ? null : Integer.toString(sNodeIndex)), aQueryPredicate, null, aAssociation.getType().toString()));
				}
			}
		}
	}
}
