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
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;

import fr.acxio.tools.agia.alfresco.configuration.AlfrescoNamespaceContext;
import fr.acxio.tools.agia.alfresco.configuration.DocumentDefinition;
import fr.acxio.tools.agia.alfresco.configuration.FolderDefinition;
import fr.acxio.tools.agia.alfresco.configuration.NodeFactoryUtils;
import fr.acxio.tools.agia.alfresco.configuration.SimpleDocumentDefinition;
import fr.acxio.tools.agia.alfresco.configuration.SimpleFolderDefinition;
import fr.acxio.tools.agia.alfresco.configuration.SimpleNodeFactory;
import fr.acxio.tools.agia.alfresco.configuration.SimplePropertyDefinition;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.NodeList;

@RunWith(JUnit4.class)
public class ListFieldSetToNodeProcessorTest {

	private AlfrescoNamespaceContext namespaceContext = new AlfrescoNamespaceContext();

	@Before
	public void setUp() throws Exception {
		namespaceContext.bindNamespaceUri("custom", "http://custom");
	}
	
	@Test
	public void testNull() throws Exception {
		ListFieldSetToNodeProcessor aProcessor = createSimpleProcessor();
		
		NodeList aResult = aProcessor.process(null);
		assertNull(aResult);
	}
	
	@Test
	public void testEmpty() throws Exception {
		ListFieldSetToNodeProcessor aProcessor = createSimpleProcessor();
		
		List<FieldSet> aEmptyList = new ArrayList<FieldSet>(1);
		NodeList aResult = aProcessor.process(aEmptyList);
		assertNull(aResult);
	}
	
	@Test
	public void testSingleFolder() throws Exception {
		ListFieldSetToNodeProcessor aProcessor = createSimpleProcessor();
		
		List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
		aFieldSetList.add(new DefaultFieldSet(new String[] {"test"}, new String[] {"name"}));
		NodeList aResult = aProcessor.process(aFieldSetList);
		assertNotNull(aResult);
		assertEquals(1, aResult.size());
		assertEquals("/cm:test", aResult.get(0).getPath());
	}
	
	@Test
	public void testNestedFolders() throws Exception {
		FolderDefinition aRootNodeDefinition = NodeFactoryUtils.createFolderDefinition("test", null, null, null);
		FolderDefinition aNestedNodeDefinition = NodeFactoryUtils.createFolderDefinition("nested", null, null, null);
		aRootNodeDefinition.addFolder(aNestedNodeDefinition);
		
		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aRootNodeDefinition);
		
		ListFieldSetToNodeProcessor aProcessor = new ListFieldSetToNodeProcessor();
		aProcessor.setNodeFactory(aNodeFactory);
		
		List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
		aFieldSetList.add(new DefaultFieldSet(new String[] {"test"}, new String[] {"name"}));
		NodeList aResult = aProcessor.process(aFieldSetList);
		assertNotNull(aResult);
		assertEquals(2, aResult.size());
		assertEquals("/cm:test", aResult.get(0).getPath());
		assertEquals("/cm:test/cm:nested", aResult.get(1).getPath());
	}
	
	@Test
	public void testNestedFoldersByName() throws Exception {
		FolderDefinition aRootNodeDefinition = NodeFactoryUtils.createFolderDefinition("test/nested", null, null, null);
		
		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aRootNodeDefinition);
		
		ListFieldSetToNodeProcessor aProcessor = new ListFieldSetToNodeProcessor();
		aProcessor.setNodeFactory(aNodeFactory);
		
		List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
		aFieldSetList.add(new DefaultFieldSet(new String[] {"test"}, new String[] {"name"}));
		NodeList aResult = aProcessor.process(aFieldSetList);
		assertNotNull(aResult);
		assertEquals(2, aResult.size());
		assertEquals("/cm:test", aResult.get(0).getPath());
		assertEquals("/cm:test/cm:nested", aResult.get(1).getPath());
	}
	
	@Test
	public void testSiblingsFolders() throws Exception {
		FolderDefinition aRootNodeDefinition = NodeFactoryUtils.createFolderDefinition("test", null, null, null);
		FolderDefinition aNestedNodeDefinition1 = NodeFactoryUtils.createFolderDefinition("nested1", null, null, null);
		FolderDefinition aNestedNodeDefinition2 = NodeFactoryUtils.createFolderDefinition("nested2", null, null, null);
		
		aRootNodeDefinition.addFolder(aNestedNodeDefinition1);
		aRootNodeDefinition.addFolder(aNestedNodeDefinition2);
		
		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aRootNodeDefinition);
		
		ListFieldSetToNodeProcessor aProcessor = new ListFieldSetToNodeProcessor();
		aProcessor.setNodeFactory(aNodeFactory);
		
		List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
		aFieldSetList.add(new DefaultFieldSet(new String[] {"test"}, new String[] {"name"}));
		NodeList aResult = aProcessor.process(aFieldSetList);
		assertNotNull(aResult);
		assertEquals(3, aResult.size());
		assertEquals("/cm:test", aResult.get(0).getPath());
		assertEquals("/cm:test/cm:nested1", aResult.get(1).getPath());
		assertEquals("/cm:test/cm:nested2", aResult.get(2).getPath());
	}
	
	@Test
	public void testSiblingsFoldersAndDocuments() throws Exception {
		FolderDefinition aRootNodeDefinition = NodeFactoryUtils.createFolderDefinition("test", null, null, null);
		FolderDefinition aNestedNodeDefinition1 = NodeFactoryUtils.createFolderDefinition("nested1", null, null, null);
		FolderDefinition aNestedNodeDefinition2 = NodeFactoryUtils.createFolderDefinition("nested2", null, null, null);
		
		aRootNodeDefinition.addFolder(aNestedNodeDefinition1);
		aRootNodeDefinition.addFolder(aNestedNodeDefinition2);
		
		DocumentDefinition aDocumentDefinition1 = NodeFactoryUtils.createDocumentDefinition("doc1", null, null, null, null, null);
		DocumentDefinition aDocumentDefinition2 = NodeFactoryUtils.createDocumentDefinition("doc2", null, null, null, null, null);
		
		aNestedNodeDefinition1.addDocument(aDocumentDefinition1);
		aNestedNodeDefinition2.addDocument(aDocumentDefinition2);
		
		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aRootNodeDefinition);
		
		ListFieldSetToNodeProcessor aProcessor = new ListFieldSetToNodeProcessor();
		aProcessor.setNodeFactory(aNodeFactory);
		
		List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
		aFieldSetList.add(new DefaultFieldSet(new String[] {"test"}, new String[] {"name"}));
		NodeList aResult = aProcessor.process(aFieldSetList);
		assertNotNull(aResult);
		assertEquals(5, aResult.size());
		assertEquals("/cm:test", aResult.get(0).getPath());
		assertEquals("/cm:test/cm:nested1", aResult.get(1).getPath());
		assertEquals("/cm:test/cm:nested1/cm:doc1", aResult.get(2).getPath());
		assertEquals("/cm:test/cm:nested2", aResult.get(3).getPath());
		assertEquals("/cm:test/cm:nested2/cm:doc2", aResult.get(4).getPath());
	}
	
	@Test
	public void testMergeSingleFolders() throws Exception {
		FolderDefinition aNodeDefinition = NodeFactoryUtils.createFolderDefinition("@{#in['name']}", null, null, null);
		
		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aNodeDefinition);
		
		ListFieldSetToNodeProcessor aProcessor = new ListFieldSetToNodeProcessor();
		aProcessor.setNodeFactory(aNodeFactory);
		
		List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
		aFieldSetList.add(new DefaultFieldSet(new String[] {"test1"}, new String[] {"name"}));
		aFieldSetList.add(new DefaultFieldSet(new String[] {"test2"}, new String[] {"name"}));
		
		NodeList aResult = aProcessor.process(aFieldSetList);
		assertNotNull(aResult);
		assertEquals(2, aResult.size());
		assertEquals("/cm:test1", aResult.get(0).getPath());
		assertEquals("/cm:test2", aResult.get(1).getPath());
	}
	
	@Test
	public void testMergeNestedFoldersWithDocuments() throws Exception {
		FolderDefinition aNodeDefinition = NodeFactoryUtils.createFolderDefinition("test/nested", null, null, null);
		DocumentDefinition aDocumentDefinition = NodeFactoryUtils.createDocumentDefinition("@{#in['name']}", null, null, null, null, null);
		aNodeDefinition.addDocument(aDocumentDefinition);
		
		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aNodeDefinition);
		
		ListFieldSetToNodeProcessor aProcessor = new ListFieldSetToNodeProcessor();
		aProcessor.setNodeFactory(aNodeFactory);
		
		List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
		aFieldSetList.add(new DefaultFieldSet(new String[] {"doc1"}, new String[] {"name"}));
		aFieldSetList.add(new DefaultFieldSet(new String[] {"doc2"}, new String[] {"name"}));
		
		NodeList aResult = aProcessor.process(aFieldSetList);
		assertNotNull(aResult);
		assertEquals(4, aResult.size());
		assertEquals("/cm:test", aResult.get(0).getPath());
		assertEquals("/cm:test/cm:nested", aResult.get(1).getPath());
		assertEquals("/cm:test/cm:nested/cm:doc1", aResult.get(2).getPath());
		assertEquals("/cm:test/cm:nested/cm:doc2", aResult.get(3).getPath());
		assertEquals(aResult.get(1), aResult.get(2).getParent());
		assertEquals(aResult.get(1), aResult.get(3).getParent());
		Folder aFolder = (Folder)aResult.get(1);
		assertEquals(2, aFolder.getDocuments().size());
	}
	
	@Test
	public void testComplexeMergeNestedFoldersWithDocuments() throws Exception {
		FolderDefinition aRootNodeDefinition = NodeFactoryUtils.createFolderDefinition("test", null, null, null);
		FolderDefinition aNestedNodeDefinition1 = NodeFactoryUtils.createFolderDefinition("nested1", null, null, null);
		FolderDefinition aNestedNodeDefinition2 = NodeFactoryUtils.createFolderDefinition("nested2", null, null, null);
		aRootNodeDefinition.addFolder(aNestedNodeDefinition1);
		aRootNodeDefinition.addFolder(aNestedNodeDefinition2);
		
		DocumentDefinition aDocumentDefinition0 = NodeFactoryUtils.createDocumentDefinition("@{#in['name']}_0", null, null, null, null, null);
		aRootNodeDefinition.addDocument(aDocumentDefinition0);
		
		DocumentDefinition aDocumentDefinition1 = NodeFactoryUtils.createDocumentDefinition("@{#in['name']}_1", null, null, null, null, null);
		aNestedNodeDefinition1.addDocument(aDocumentDefinition1);
		
		DocumentDefinition aDocumentDefinition2 = NodeFactoryUtils.createDocumentDefinition("@{#in['name']}_2", null, null, null, null, null);
		aNestedNodeDefinition2.addDocument(aDocumentDefinition2);
		
		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aRootNodeDefinition);
		
		ListFieldSetToNodeProcessor aProcessor = new ListFieldSetToNodeProcessor();
		aProcessor.setNodeFactory(aNodeFactory);
		
		List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
		aFieldSetList.add(new DefaultFieldSet(new String[] {"doc1"}, new String[] {"name"}));
		aFieldSetList.add(new DefaultFieldSet(new String[] {"doc2"}, new String[] {"name"}));
		
		NodeList aResult = aProcessor.process(aFieldSetList);
		assertNotNull(aResult);
		assertEquals(9, aResult.size());
		assertEquals("/cm:test", aResult.get(0).getPath());
		assertEquals("/cm:test/cm:nested1", aResult.get(1).getPath());
		assertEquals("/cm:test/cm:nested1/cm:doc1_1", aResult.get(2).getPath());
		assertEquals("/cm:test/cm:nested2", aResult.get(3).getPath());
		assertEquals("/cm:test/cm:nested2/cm:doc1_2", aResult.get(4).getPath());
		assertEquals("/cm:test/cm:doc1_0", aResult.get(5).getPath());
		assertEquals("/cm:test/cm:nested1/cm:doc2_1", aResult.get(6).getPath());
		assertEquals("/cm:test/cm:nested2/cm:doc2_2", aResult.get(7).getPath());
		assertEquals("/cm:test/cm:doc2_0", aResult.get(8).getPath());
		
		assertEquals(aResult.get(0), aResult.get(5).getParent());
		assertEquals(aResult.get(1), aResult.get(2).getParent());
		assertEquals(aResult.get(3), aResult.get(4).getParent());
		assertEquals(aResult.get(0), aResult.get(8).getParent());
		assertEquals(aResult.get(1), aResult.get(6).getParent());
		assertEquals(aResult.get(3), aResult.get(7).getParent());
		
		Folder aFolder = (Folder)aResult.get(0);
		assertEquals(2, aFolder.getDocuments().size());
		assertEquals(2, aFolder.getFolders().size());
		
		aFolder = (Folder)aResult.get(1);
		assertEquals(2, aFolder.getDocuments().size());
		assertEquals(0, aFolder.getFolders().size());
		
		aFolder = (Folder)aResult.get(3);
		assertEquals(2, aFolder.getDocuments().size());
		assertEquals(0, aFolder.getFolders().size());
	}
	
	private ListFieldSetToNodeProcessor createSimpleProcessor() {
		FolderDefinition aDefaultNodeDefinition = NodeFactoryUtils.createFolderDefinition("test", null, null, null);
		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aDefaultNodeDefinition);
		
		ListFieldSetToNodeProcessor aProcessor = new ListFieldSetToNodeProcessor();
		aProcessor.setNodeFactory(aNodeFactory);
		
		return aProcessor;
	}

}
