package fr.acxio.tools.agia.google;

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
 
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.util.Assert;

import com.google.api.services.drive.model.File;

import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.alfresco.domain.Property;
import fr.acxio.tools.agia.convert.AlfrescoPathToPathConverter;
import fr.acxio.tools.agia.convert.ConversionException;
import fr.acxio.tools.agia.convert.FormatConverter;

public class GoogleDriveWriter implements ItemWriter<NodeList> {
	// NOT TRANSACTION COMPLIANT
	
	private static Logger logger = LoggerFactory.getLogger(GoogleDriveWriter.class);
	
	private static final FormatConverter pathConverter = new AlfrescoPathToPathConverter();
	
	private GoogleDriveService service;
	private boolean writeProperties = false;
	
	public void setService(GoogleDriveService sService) {
		service = sService;
	}

	public void setWriteProperties(boolean sWriteProperties) {
		writeProperties = sWriteProperties;
	}

	@Override
	public void write(List<? extends NodeList> sData) throws GoogleException, IOException, ConversionException {
		if (!sData.isEmpty()) {
			init();
			String aCurrentNodePath;
			
			for(NodeList aNodeList : sData) { // each NodeList represents an input record
				for(Node aNode : aNodeList) {
					aCurrentNodePath = pathConverter.convert(aNode.getPath()).get(0);
					
					if (logger.isDebugEnabled()) {
						logger.debug("Will create: " + aCurrentNodePath);
					}
					
					boolean aIsFileExist = false;
					try {
						File aFile = service.getFileByPath(aCurrentNodePath); // TODO : cache
						aIsFileExist = (aFile != null);
					} catch (IOException e) {
						aIsFileExist = false;
					}
					if (!aIsFileExist) {
						createNode(aCurrentNodePath, aNode);
					}
				}
			}
		}
	}

	protected void createNode(String sCurrentNodePath, Node sNode) throws IOException {
		Map<String, String> aProperties = (writeProperties) ? getProperties(sNode) : null;
		if (sNode instanceof Document) {
			Document aDocument = (Document)sNode;
			java.io.File aFile = new java.io.File(aDocument.getContentPath());
			if (aFile.exists() && aFile.isFile()) {
				service.createFile(sCurrentNodePath, aFile, aDocument.getMimeType(), aProperties);
			} else {
				throw new IOException("Cannot find content file: " + aDocument.getContentPath());
			}
		} else if (sNode instanceof Folder) {
			service.createDirectory(sCurrentNodePath, aProperties);
		}
	}

	protected Map<String, String> getProperties(Node sNode) {
		List<Property> aProperties = sNode.getProperties();
		Map<String, String> aProps = new HashMap<String, String>(aProperties.size());
        List<String> aPropValues;
        for(Property aProperty : aProperties) {
        	aPropValues = aProperty.getValues();
        	if (aPropValues.size() > 1) {
        		String[] aValues = aPropValues.toArray(new String[] {});
        		aProps.put(aProperty.getName().toString(), Arrays.toString(aValues));
        	} else {
        		String aValue = (aPropValues.size() == 0) ? null : aPropValues.get(0);
        		aProps.put(aProperty.getName().toString(), aValue);
        	}
        }
        return aProps;
	}
	
	public void init() throws GoogleException {
		Assert.notNull(service, "GoogleDriveService is required.");
		
		service.connect();
	}
}
