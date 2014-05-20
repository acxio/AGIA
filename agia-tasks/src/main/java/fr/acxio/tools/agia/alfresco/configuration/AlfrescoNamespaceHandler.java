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
 
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * <p>Spring configuration NamespaceHandler for Alfresco specific domain.</p>
 * 
 * @author pcollardez
 *
 */
public class AlfrescoNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("document", new DocumentDefinitionParser());
		registerBeanDefinitionParser("folder", new FolderDefinitionParser());
	}

}
