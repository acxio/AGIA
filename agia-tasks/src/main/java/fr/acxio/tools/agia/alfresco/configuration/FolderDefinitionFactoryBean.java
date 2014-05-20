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
 
import java.util.List;

/**
 * <p>Factory of
 * {@link fr.acxio.tools.agia.alfresco.configuration.FolderDefinition FolderDefinition}
 * .</p>
 * 
 * @author pcollardez
 *
 */
public class FolderDefinitionFactoryBean extends NodeDefinitionFactoryBean<FolderDefinition> {

	private FolderDefinition parent;
	private List<FolderDefinition> children;
	private List<DocumentDefinition> documents;
	
	public void setParent(FolderDefinition sParent) {
		parent = sParent;
	}

	public void setChildren(List<FolderDefinition> sChildren) {
		children = sChildren;
	}

	public void setDocuments(List<DocumentDefinition> sDocuments) {
		documents = sDocuments;
	}

	public FolderDefinition getObject() {
		if (children != null && children.size() > 0) {
			for (FolderDefinition child : children) {
				parent.addFolder(child);
			}
		}
		if (documents != null && documents.size() > 0) {
			for (DocumentDefinition child : documents) {
				parent.addDocument(child);
			}
		}
		addPropertiesToNodeDefinition(parent);
		addAspectsToNodeDefinition(parent);
		addAssociationsToNodeDefinition(parent);
		return parent;
	}

	public Class<?> getObjectType() {
		return SimpleFolderDefinition.class;
	}

}
