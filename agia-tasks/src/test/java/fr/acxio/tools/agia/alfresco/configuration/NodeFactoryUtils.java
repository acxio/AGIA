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
 
import java.util.Collections;
import java.util.List;

import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.Property;

public class NodeFactoryUtils {

	public static FolderDefinition createFolderDefinition(String sName, String sTitle, String sCondition, String sNodeType) {
		SimpleFolderDefinition aDefaultNodeDefinition = new SimpleFolderDefinition();
		aDefaultNodeDefinition.setCondition((sCondition == null) ? "" : sCondition);
		aDefaultNodeDefinition.setNodeType((sNodeType == null) ? "" : sNodeType);
		aDefaultNodeDefinition.setVersionOperation("");
		
		SimplePropertyDefinition aPropertyDefinition = new SimplePropertyDefinition();
		aPropertyDefinition.setLocalName("cm:name");
		List<String> aValues = Collections.singletonList(sName);
		aPropertyDefinition.setValues(aValues);
		aDefaultNodeDefinition.addPropertyDefinition(aPropertyDefinition);
		
		if (sTitle != null) {
			SimplePropertyDefinition aTitlePropertyDefinition = new SimplePropertyDefinition();
			aTitlePropertyDefinition.setLocalName("cm:title");
			List<String> aTitleValues = Collections.singletonList(sTitle);
			aTitlePropertyDefinition.setValues(aTitleValues);
			aDefaultNodeDefinition.addPropertyDefinition(aTitlePropertyDefinition);
		}
		
		return aDefaultNodeDefinition;
	}
	
	public static DocumentDefinition createDocumentDefinition(String sName, String sTitle, String sContentPath, String sEncoding, String sMimeType, String sNodeType) {
		SimpleDocumentDefinition aDocumentDefinition = new SimpleDocumentDefinition();
		aDocumentDefinition.setContentPath((sContentPath == null) ? "" : sContentPath);
		aDocumentDefinition.setEncoding((sEncoding == null) ? "" : sEncoding);
		aDocumentDefinition.setMimeType((sMimeType == null) ? "" : sMimeType);
		aDocumentDefinition.setNodeType((sNodeType == null) ? "" : sNodeType);
		aDocumentDefinition.setVersionOperation("");
		SimplePropertyDefinition aDocPropertyDefinition = new SimplePropertyDefinition();
		aDocPropertyDefinition.setLocalName("cm:name");
		List<String> aDocValues = Collections.singletonList(sName);
		aDocPropertyDefinition.setValues(aDocValues);
		aDocumentDefinition.addPropertyDefinition(aDocPropertyDefinition);
		
		if (sTitle != null) {
			SimplePropertyDefinition aTitlePropertyDefinition = new SimplePropertyDefinition();
			aTitlePropertyDefinition.setLocalName("cm:title");
			List<String> aTitleValues = Collections.singletonList(sTitle);
			aTitlePropertyDefinition.setValues(aTitleValues);
			aDocumentDefinition.addPropertyDefinition(aTitlePropertyDefinition);
		}
		
		return aDocumentDefinition;
	}
	
	public static List<String> getPropertyValues(Node sNode, String sName) {
		List<String> aResult = null;
		for(Property aProperty : sNode.getProperties()) {
			if (aProperty.getName().toString().equals(sName)) {
				aResult = aProperty.getValues();
			}
		}
		return aResult;
	}
}
