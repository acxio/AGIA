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
 
import org.alfresco.webservice.util.Constants;
import org.springframework.util.xml.SimpleNamespaceContext;

/**
 * <p>Basic Alfresco namespaces context.</p>
 * <p>By default, it defines the following namespaces:
 * <ul>
 * <li>sys : http://www.alfresco.org/model/system/1.0</li>
 * <li>cm : http://www.alfresco.org/model/content/1.0</li>
 * <li>app : http://www.alfresco.org/model/application/1.0</li>
 * <li>alf : http://www.alfresco.org</li>
 * <li>d : http://www.alfresco.org/model/dictionary/1.0</li>
 * </ul>
 * </p>
 * <p>Other namespaces can be added through the {@code bindings} property.</p>
 * 
 * @author pcollardez
 *
 */
public class AlfrescoNamespaceContext extends SimpleNamespaceContext {

	public AlfrescoNamespaceContext() {
		super();
		bindNamespaceUri("sys", Constants.NAMESPACE_SYSTEM_MODEL);
		bindNamespaceUri("cm", Constants.NAMESPACE_CONTENT_MODEL);
		bindNamespaceUri("app", "http://www.alfresco.org/model/application/1.0");
		bindNamespaceUri("alf", "http://www.alfresco.org");
		bindNamespaceUri("d", "http://www.alfresco.org/model/dictionary/1.0");
	}
	
}
