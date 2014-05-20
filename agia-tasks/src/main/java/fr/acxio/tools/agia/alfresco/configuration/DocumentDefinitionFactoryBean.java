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
 
/**
 * <p>Factory of
 * {@link fr.acxio.tools.agia.alfresco.configuration.DocumentDefinition DocumentDefinition}
 * .</p>
 * 
 * @author pcollardez
 *
 */
public class DocumentDefinitionFactoryBean extends NodeDefinitionFactoryBean<DocumentDefinition> {

	private String contentPath;
	private String mimeType;
	private String encoding;
	
	public String getContentPath() {
		return contentPath;
	}

	public void setContentPath(String sContentPath) {
		contentPath = sContentPath;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String sMimeType) {
		mimeType = sMimeType;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String sEncoding) {
		encoding = sEncoding;
	}

	public DocumentDefinition getObject() {
		SimpleDocumentDefinition aDocumentDefinition = new SimpleDocumentDefinition();
		
		aDocumentDefinition.setNodeType(getNodeType());
		aDocumentDefinition.setVersionOperation(getVersionOperation());
		aDocumentDefinition.setAssocTargetId(getAssocTargetId());

		addPropertiesToNodeDefinition(aDocumentDefinition);
		addAspectsToNodeDefinition(aDocumentDefinition);
		addAssociationsToNodeDefinition(aDocumentDefinition);
		
		aDocumentDefinition.setContentPath(getContentPath());
		aDocumentDefinition.setMimeType(getMimeType());
		aDocumentDefinition.setEncoding(getEncoding());
		
		aDocumentDefinition.afterPropertiesSet();
		
		return aDocumentDefinition;
	}

	public Class<?> getObjectType() {
		return SimpleDocumentDefinition.class;
	}

}
