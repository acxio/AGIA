package fr.acxio.tools.agia.alfresco.configuration;

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
 
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;

import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ISO9075;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.alfresco.domain.Aspect;
import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.alfresco.domain.Property;
import fr.acxio.tools.agia.alfresco.domain.QName;
import fr.acxio.tools.agia.alfresco.domain.QueryAssociation;
import fr.acxio.tools.agia.alfresco.domain.RefAssociation;
import fr.acxio.tools.agia.convert.ConversionException;
import fr.acxio.tools.agia.convert.FormatConverter;
import fr.acxio.tools.agia.expression.DataExpressionResolver;
import fr.acxio.tools.agia.expression.StandardDataExpressionResolver;

/**
 * <p>Simple implementation of
 * {@link fr.acxio.tools.agia.alfresco.configuration.NodeFactory NodeFactory}
 * .</p>
 * <p>The expressions than can be used in the template tree are
 * delimited by @{ and }.</p>
 * <p>In an expression, the references to variables are
 * prefixed by #.</p>
 * 
 * @author pcollardez
 *
 */
public class SimpleNodeFactory implements NodeFactory, InitializingBean {

	private static final String ALFRESCO_PATH_SPLIT_REGEX = "/";
	
	private DataExpressionResolver expressionResolver = new StandardDataExpressionResolver();
	
	private NodeDefinition nodeDefinition;
	private NamespaceContext namespaceContext;
	
	public void setNodeDefinition(NodeDefinition sNodeDefinition) {
		nodeDefinition = sNodeDefinition;
	}

	public NodeDefinition getNodeDefinition() {
		return nodeDefinition;
	}
	
	public NamespaceContext getNamespaceContext() {
		return namespaceContext;
	}

	public void setNamespaceContext(NamespaceContext sNamespaceContext) {
		namespaceContext = sNamespaceContext;
	}

	public NodeList getNodes(EvaluationContext sContext) throws ConversionException {
		Assert.notNull(sContext, "The evaluation context is mandatory.");
		ManagedList<Node> aResult = new ManagedList<Node>();
		
		if (nodeDefinition instanceof FolderDefinition) {
			// Regular usecase
			FolderDefinition aFolderDefinition = (FolderDefinition) nodeDefinition;
			addFolderNode(aResult, aFolderDefinition, null, sContext);
		}
		// TODO : special usecase: document with a path in its name. Folders should be created as default ones _without_ coyping document properties.
		// If the document has no path in its name, then a default app:company_home is added.
		
		return new NodeList(aResult);
	}
	
	public void afterPropertiesSet() {
		Assert.notNull(nodeDefinition, "The node factory must have a node definition");
	}

	private Folder createFolderNode(String sFolderName, FolderDefinition sFolderDefinition, Node sParentFolder, EvaluationContext sContext) throws ConversionException {
		Folder aResult = new Folder();
		aResult.setAddedTimestamp(new Date());
		aResult.setType(getQNameWithDefault(expressionResolver.evaluate(sFolderDefinition.getNodeType(), sContext, String.class), Constants.TYPE_FOLDER, namespaceContext));
		aResult.setVersionOperation(mapVersionOperation(expressionResolver.evaluate(sFolderDefinition.getVersionOperation(), sContext, String.class)));
		if (sFolderDefinition.getAssocTargetId() != null) {
			aResult.setAssocTargetId(expressionResolver.evaluate(sFolderDefinition.getAssocTargetId(), sContext, String.class));
		}
		
		addPropertiesToNode(aResult, sFolderDefinition, sContext);
		addAspectsToNode(aResult, sFolderDefinition, sContext);
		addAssociationsToNode(aResult, sFolderDefinition, sContext);
		setName(aResult, sFolderName);
		
		aResult.setParent(sParentFolder);
		aResult.setPathElement(getPathElement(sFolderName, aResult.getType().getPrefix(), namespaceContext));
		return aResult;
	}
	
	private boolean evaluateCondition(String sCondition, EvaluationContext sContext) {
		boolean aResult = true;
		if ((sCondition != null) && (sCondition.length() > 0) && (sContext != null)) {
			aResult = expressionResolver.evaluate(sCondition, sContext, Boolean.class);
		}
		return aResult;
	}
	
	private Folder addFolderNode(List<Node> sResult, FolderDefinition sFolderDefinition, Node sParentFolder, EvaluationContext sContext) throws ConversionException {
		Folder aFolder = null;
		if (evaluateCondition(sFolderDefinition.getCondition(), sContext)) {
			Node aParentFolder = sParentFolder;
			// Parse the folder cm:name on path separators.
			String aPath = getName(sFolderDefinition, sContext);
			String[] aSubPaths = aPath.split(ALFRESCO_PATH_SPLIT_REGEX);
			String aFolderName = aSubPaths[aSubPaths.length - 1];
			
			if (aSubPaths.length > 1) {
				Folder aTmpFolder;
				for(int i = 0; i < (aSubPaths.length - 1); i++) {
					if (aSubPaths[i].length() > 0) {
						aTmpFolder = createFolderNode(aSubPaths[i], sFolderDefinition, aParentFolder, sContext);
						sResult.add(aTmpFolder);
						if (aParentFolder != null) {
							((Folder)aParentFolder).addFolder(aTmpFolder);
						}
						aParentFolder = aTmpFolder;
					}
				}
			}
			
			// Then add the current folder
			aFolder = createFolderNode(aFolderName, sFolderDefinition, aParentFolder, sContext);
			sResult.add(aFolder);
			if (aParentFolder != null) {
				((Folder)aParentFolder).addFolder(aFolder);
			}
			
			for(FolderDefinition aFolderDef : sFolderDefinition.getFolders()) {
				addFolderNode(sResult, aFolderDef, aFolder, sContext);
			}
			
			for(DocumentDefinition aDocumentDef : sFolderDefinition.getDocuments()) {
				aFolder.addDocument(addDocumentNode(sResult, aDocumentDef, aFolder, sContext));
			}
		}
		return aFolder;
	}

	private String getName(NodeDefinition sNodeDefinition, EvaluationContext sContext) {
		QName aCMName = new QName(Constants.PROP_NAME, namespaceContext);
		Iterator<PropertyDefinition> aPropertyIterator = sNodeDefinition.getPropertiesDefinitions().iterator();
		String aPropertyName = null;
		String aName = null;
		PropertyDefinition aProperty;
		while ((aName == null) && (aPropertyIterator.hasNext())) {
			aProperty = aPropertyIterator.next();
			aPropertyName = expressionResolver.evaluate(aProperty.getLocalName(), sContext, String.class);
			if ((aCMName.toString().equals(aPropertyName) || aCMName.getShortName().equals(aPropertyName)) && (aProperty.getValues().size() > 0)) {
				aName = expressionResolver.evaluate(aProperty.getValues().get(0), sContext, String.class);
			}
		}
		return aName;
	}
	
	private void setName(Node sNode, String sName) {
		sNode.setName(sName);
	}
	
	private void addPropertiesToNode(Node aNode, NodeDefinition sNodeDefinition, EvaluationContext sContext) throws ConversionException {
		Property aProperty;
		String aPropertyName = null;
		String aResolvedValue;
		List<String> aConvertedValue;
		for(PropertyDefinition aPropDef : sNodeDefinition.getPropertiesDefinitions()) {
			aProperty = new Property();
			aPropertyName = expressionResolver.evaluate(aPropDef.getLocalName(), sContext, String.class);
			aProperty.setName(new QName(aPropertyName, namespaceContext));
			for(String aValue : aPropDef.getValues()) {
				aResolvedValue = expressionResolver.evaluate(aValue, sContext, String.class);
				FormatConverter aConverter = aPropDef.getConverter();
				if (aConverter != null) {
					aConvertedValue = aConverter.convert(aResolvedValue);
					for(String aNewValue : aConvertedValue) {
						aProperty.addValue(aNewValue);
					}
				} else {
					aProperty.addValue(aResolvedValue);
				}
			}
			aNode.addProperty(aProperty);
		}
	}
	
	private void addAspectsToNode(Node sNode, NodeDefinition sNodeDefinition, EvaluationContext sContext) {
		Aspect aAspect;
		for(AspectDefinition aAspectDef : sNodeDefinition.getAspectsDefinitions()) {
			aAspect = new Aspect();
			aAspect.setName(new QName(expressionResolver.evaluate(aAspectDef.getName(), sContext, String.class), namespaceContext));
			sNode.addAspect(aAspect);
		}
	}
	
	private void addAssociationsToNode(Node sNode, NodeDefinition sNodeDefinition, EvaluationContext sContext) {
		for(AssociationDefinition aAssociationDef : sNodeDefinition.getAssociationsDefinitions()) {
			if (aAssociationDef instanceof RefAssociationDefinition) {
				RefAssociation aRefAssociation = new RefAssociation();
				aRefAssociation.setType(new QName(expressionResolver.evaluate(aAssociationDef.getType(), sContext, String.class), namespaceContext));
				aRefAssociation.setReference(expressionResolver.evaluate(((RefAssociationDefinition) aAssociationDef).getTargetRef(), sContext, String.class));
				sNode.addAssociation(aRefAssociation);
			} else if (aAssociationDef instanceof QueryAssociationDefinition) {
				QueryAssociation aQueryAssociation = new QueryAssociation();
				aQueryAssociation.setType(new QName(expressionResolver.evaluate(aAssociationDef.getType(), sContext, String.class), namespaceContext));
				aQueryAssociation.setQueryLanguage(expressionResolver.evaluate(((QueryAssociationDefinition) aAssociationDef).getQueryLanguage(), sContext, String.class));
				aQueryAssociation.setQuery(expressionResolver.evaluate(((QueryAssociationDefinition) aAssociationDef).getQuery(), sContext, String.class));
				sNode.addAssociation(aQueryAssociation);
			}
		}
	}
	
	private Document addDocumentNode(List<Node> sResult, DocumentDefinition sDocumentDefinition, Node sParentFolder, EvaluationContext sContext) throws ConversionException {
		Document aDocument = new Document();
		aDocument.setAddedTimestamp(new Date());
		sResult.add(aDocument);
		aDocument.setParent(sParentFolder);
		
		aDocument.setType(getQNameWithDefault(expressionResolver.evaluate(sDocumentDefinition.getNodeType(), sContext, String.class), Constants.TYPE_CONTENT, namespaceContext));
		aDocument.setVersionOperation(mapVersionOperation(expressionResolver.evaluate(sDocumentDefinition.getVersionOperation(), sContext, String.class)));
		if (sDocumentDefinition.getAssocTargetId() != null) {
			aDocument.setAssocTargetId(expressionResolver.evaluate(sDocumentDefinition.getAssocTargetId(), sContext, String.class));
		}
		
		addPropertiesToNode(aDocument, sDocumentDefinition, sContext);
		addAspectsToNode(aDocument, sDocumentDefinition, sContext);
		addAssociationsToNode(aDocument, sDocumentDefinition, sContext);
		
		aDocument.setPathElement(getPathElement(aDocument.getName(), aDocument.getType().getPrefix(), namespaceContext));

		aDocument.setContentPath(expressionResolver.evaluate(sDocumentDefinition.getContentPath(), sContext, String.class));
		aDocument.setEncoding(expressionResolver.evaluate(sDocumentDefinition.getEncoding(), sContext, String.class));
		aDocument.setMimeType(expressionResolver.evaluate(sDocumentDefinition.getMimeType(), sContext, String.class));
		
		return aDocument;
	}

	private QName getQNameWithDefault(String sValue, String sDefault, NamespaceContext sContext) {
		return new QName(((sValue == null) || (sValue.length() == 0)) ? sDefault : sValue, sContext);
	}
	
	private String getPathElement(String sName, String sDefaultPrefix, NamespaceContext sContext) {
		StringBuilder aPathElement = new StringBuilder();
		QName aQName = new QName(sName, sContext);
		aPathElement.append(((aQName.getPrefix() != null) && (aQName.getPrefix().length() > 0)) ? aQName.getPrefix() : sDefaultPrefix);
		aPathElement.append(":");
		aPathElement.append(((aQName.getNamespaceURI() != null) && (aQName.getNamespaceURI().length() > 0)) ? aQName.getLocalName() : ISO9075.encode(aQName.getLocalName()));
		return aPathElement.toString();
	}
	
	private Node.VersionOperation mapVersionOperation(String sValue) {
		return ((sValue == null) || (sValue.length() == 0))
				? Node.VersionOperation.RAISEERROR
				: ("error".equalsIgnoreCase(sValue)
					? Node.VersionOperation.RAISEERROR
					: ("version".equalsIgnoreCase(sValue)
						? Node.VersionOperation.VERSION
						: ("update".equalsIgnoreCase(sValue)
							? Node.VersionOperation.UPDATE
							: ("replace".equalsIgnoreCase(sValue)
								? Node.VersionOperation.REPLACE
								: Node.VersionOperation.RAISEERROR))));
	}
}
