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
 
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.validation.BindException;

import fr.acxio.tools.agia.alfresco.configuration.AlfrescoNamespaceContext;
import fr.acxio.tools.agia.alfresco.configuration.FolderDefinition;
import fr.acxio.tools.agia.alfresco.configuration.NodeFactoryUtils;
import fr.acxio.tools.agia.alfresco.configuration.SimpleNodeFactory;
import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.alfresco.domain.Property;
import fr.acxio.tools.agia.alfresco.domain.QName;

@RunWith(JUnit4.class)
public class NodeToNodeProcessorTest {
	
	private AlfrescoNamespaceContext namespaceContext = new AlfrescoNamespaceContext();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSingleNode() throws Exception {
		FolderDefinition aNodeDefinition = NodeFactoryUtils.createFolderDefinition("@{#in.name}_@{#in.properties.?[name.shortName=='cm:title'][0].values[0]}", null, null, null);
		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aNodeDefinition);

		NodeToNodeProcessor aProcessor = new NodeToNodeProcessor();
		aProcessor.setNodeFactory(aNodeFactory);
		
		Node aNode = new Document();
		aNode.setName("node1");
		
		Property aProperty = new Property();
		aProperty.setName(new QName("cm:title", namespaceContext));
		aProperty.addValue("Some title");
		aNode.addProperty(aProperty);
		
		NodeList aNodeList = aProcessor.process(aNode);
		
		assertNotNull(aNodeList);
		assertEquals(1, aNodeList.size());
		assertEquals("node1_Some title", aNodeList.get(0).getName());
	}

}
