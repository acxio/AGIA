package fr.acxio.tools.agia.cmis;

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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.webservice.util.ISO9075;
import org.alfresco.webservice.util.Constants;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.alfresco.domain.Property;

public class CmisWriter implements ItemStreamWriter<NodeList> {
	
	private CmisService cmisService;
	private OperationContext operationContext;
	private Set<String> existingPaths = new HashSet<String>();

	public void setCmisService(CmisService sCmisService) {
		cmisService = sCmisService;
	}

	public void setOperationContext(OperationContext sOperationContext) {
		operationContext = sOperationContext;
	}

	@Override
    public void open(ExecutionContext sExecutionContext) throws ItemStreamException {
		cmisService.startSession();
    }

	@Override
    public void update(ExecutionContext sExecutionContext) throws ItemStreamException {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void close() throws ItemStreamException {
		cmisService.endSession();
		existingPaths.clear();
    }

	@Override
    public void write(List<? extends NodeList> sItems) throws Exception {
		if (!sItems.isEmpty()) {
			String aCurrentNodePath;
			
			for(NodeList aNodeList : sItems) {
				for(Node aNode : aNodeList) {
					aCurrentNodePath = ISO9075.decode(aNode.getPath().replaceAll("[^:/]*:", "")); // Strip namespaces
					
					if (!existingPaths.contains(aCurrentNodePath)) {
						createOrUpdateNode(aCurrentNodePath, aNode);
						existingPaths.add(aCurrentNodePath);
					}
				}
			}
		}
    }

	private void createOrUpdateNode(String sCurrentNodePath, Node sNode) throws IOException {
		CmisObject aExistingObject = null;
		try {
			aExistingObject = cmisService.getSession().getObjectByPath(sCurrentNodePath, operationContext);
		} catch (CmisObjectNotFoundException e) {
			aExistingObject = null;
		}
	    
		if (aExistingObject != null) {
			updateNode(aExistingObject, sCurrentNodePath, sNode);
		} else {
			createNewNode(sCurrentNodePath, sNode);
		}
    }

	private void updateNode(CmisObject sExistingObject, String sCurrentNodePath, Node sNode) throws IOException {
		Map<String, Object> aProperties = getProperties(sNode);
	    if (sNode instanceof Document) {
			sExistingObject.updateProperties(aProperties);
	    	ContentStream contentStream = createStreamForNode((Document) sNode);
	    	((org.apache.chemistry.opencmis.client.api.Document)sExistingObject).setContentStream(contentStream, true);
	    	
	    	((ContentStreamImpl)contentStream).getStream().close();
	    }
    }

	private void createNewNode(String sCurrentNodePath, Node sNode) throws IOException {
		org.apache.chemistry.opencmis.client.api.Folder aParent = getParent(sCurrentNodePath);
	    Map<String, Object> aProperties = getProperties(sNode);
	    if (sNode instanceof Document) {
	    	ContentStream contentStream = createStreamForNode((Document) sNode);
	    	aParent.createDocument(aProperties, contentStream, VersioningState.MAJOR);
	    	
	    	((ContentStreamImpl)contentStream).getStream().close();
	    } else if (sNode instanceof Folder) {
	    	aParent.createFolder(aProperties);
	    }
    }

	private org.apache.chemistry.opencmis.client.api.Folder getParent(String sNodePath) {
		String aParentPath = sNodePath.substring(0, sNodePath.lastIndexOf("/"));
		org.apache.chemistry.opencmis.client.api.Folder aParent = (org.apache.chemistry.opencmis.client.api.Folder) cmisService.getSession().getObjectByPath(aParentPath, operationContext);
		if (aParent == null) {
			aParent = cmisService.getSession().getRootFolder();
		}
		return aParent;
	}

	private Map<String, Object> getProperties(Node sNode) {
		Map<String, Object> aResult = new HashMap<String, Object>();
		List<Property> aProperties = sNode.getProperties();
		
		String aNodeType = (sNode.getType() == null) ? ((sNode instanceof Document) ? "cmis:document" : "cmis:folder") : sNode.getType().toString();
		if (Constants.TYPE_CONTENT.equals(aNodeType)) {
			aNodeType = "cmis:document";
		} else if (Constants.TYPE_FOLDER.equals(aNodeType)) {
			aNodeType = "cmis:folder";
		} else {
			aNodeType = sNode.getType().getLocalName();
		}
		aResult.put(PropertyIds.OBJECT_TYPE_ID, aNodeType);
		
		List<String> aPropValues;
		String aPropName;
		for(Property aProperty : aProperties) {
			aPropValues = aProperty.getValues();
			aPropName = aProperty.getName().getShortName();
			if (Constants.PROP_NAME.equals(aProperty.getName().toString())) {
				aPropName = "cmis:name";
			}
        	if (aPropValues.size() > 1) {
        		aResult.put(aPropName, scanValues(aPropValues));
        	} else if (aPropValues.size() == 1) {
        		aResult.put(aPropName, scanValue(aPropValues.get(0)));
        	}
		}
	    return aResult;
    }

	private ContentStream createStreamForNode(Document sDocument) throws IOException {
		ContentStream aResult = null;
		if ((sDocument.getContentPath() != null) && (sDocument.getContentPath().length() > 0)) {
			File aFile = new File(sDocument.getContentPath());
			if (aFile.exists() && aFile.isFile()) {
				FileInputStream aInputStream = new FileInputStream(aFile);
				aResult = new ContentStreamImpl(aFile.getName(), BigInteger.valueOf(aFile.length()), sDocument.getMimeType(), aInputStream);
			} else {
				throw new IOException("Cannot find content file: " + sDocument.getContentPath());
			}
		}
	    return aResult;
    }

	
	private Object scanValue(String sValue) {
		Object aResult = sValue;
		if (sValue.startsWith("%")) {
			String aFormatString = sValue.substring(1, sValue.indexOf("%", 1));
			String aValue = sValue.substring(2 + aFormatString.length());
			if ("B".equals(aFormatString)) {
				aResult = Boolean.valueOf(aValue);
			} else if ("D".equals(aFormatString)) {
				aResult = Long.valueOf(aValue);
			} else if ("F".equals(aFormatString)) {
				aResult = Double.valueOf(aValue);
			} else if (aFormatString.startsWith("T")) {
				DateTimeFormatter aFormatter = DateTimeFormat.forPattern(aFormatString.substring(1));
				aResult = aFormatter.parseDateTime(aValue).toDate();
			}
		}
		return aResult;
	}
	
	private List<Object> scanValues(List<String> sValues) {
		List<Object> aResult = null;
		if (sValues != null) {
			aResult = new ArrayList<Object>(sValues.size());
			for(String aValue : sValues) {
				aResult.add(scanValue(aValue));
			}
		}
		return aResult;
	}
}
