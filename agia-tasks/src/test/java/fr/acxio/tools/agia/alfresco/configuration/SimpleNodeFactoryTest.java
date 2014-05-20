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
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StopWatch;

import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.alfresco.domain.Property;

@RunWith(JUnit4.class)
public class SimpleNodeFactoryTest {
	
	private AlfrescoNamespaceContext namespaceContext = new AlfrescoNamespaceContext();
	private StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		namespaceContext.bindNamespaceUri("custom", "http://custom");
		
		Properties aInValues = new Properties();
		aInValues.setProperty("date", "2012-07-05");
		aInValues.setProperty("level1", "s1");
		aInValues.setProperty("level2", "s2");
		aInValues.setProperty("content1", "doc1");
		aInValues.setProperty("content2", "doc2");
		aInValues.setProperty("path1", "file:/somepath/content1.pdf");
		aInValues.setProperty("filename2", "content2.pdf");
		aInValues.setProperty("encoding", "UTF-8");
		aInValues.setProperty("mimetype", "application/pdf");
		aInValues.setProperty("customdoctype", "custom:doc");
		aInValues.setProperty("customfoldertype", "custom:folder");
		evaluationContext.setVariable("in", aInValues);
	}

	@Test
	public void testGetNodes() throws Exception {
		FolderDefinition aDefaultNodeDefinition = NodeFactoryUtils.createFolderDefinition("test", null, null, null);

//		FieldSet aData = new DefaultFieldSet(new String[]{"A", "B", "C"}, new String[]{"A", "B", "C"});
		
		StopWatch aStopWatch = new StopWatch("testGetNodes");
		aStopWatch.start("Create a node list");
		
		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aDefaultNodeDefinition);
		NodeList aNodeList = aNodeFactory.getNodes(evaluationContext);
		
		aStopWatch.stop();
		
		assertNotNull(aNodeList);
		assertEquals(1, aNodeList.size());
		assertTrue(aNodeList.get(0) instanceof Folder);
		Folder aFolder = (Folder)aNodeList.get(0);
		assertEquals("test", aFolder.getName());
		assertNotNull(aFolder.getAspects());
		assertEquals(0, aFolder.getAspects().size());
		assertNotNull(aFolder.getAssociations());
		assertEquals(0, aFolder.getAssociations().size());
		assertNotNull(aFolder.getFolders());
		assertEquals(0, aFolder.getFolders().size());
		assertNotNull(aFolder.getDocuments());
		assertEquals(0, aFolder.getDocuments().size());
		assertNull(aFolder.getParent());
		assertEquals("/cm:test", aFolder.getPath());
		assertNotNull(aFolder.getType());
		assertEquals("{http://www.alfresco.org/model/content/1.0}folder", aFolder.getType().toString());
	
		System.out.println(aStopWatch.prettyPrint());
	}
	
	@Test
	public void testComplexPathsGetNodes() throws Exception {
		SimpleFolderDefinition aDefaultNodeDefinition = new SimpleFolderDefinition();
		aDefaultNodeDefinition.setCondition("");
		aDefaultNodeDefinition.setNodeType("");
		aDefaultNodeDefinition.setVersionOperation("");
		SimplePropertyDefinition aPropertyDefinition = new SimplePropertyDefinition();
		aPropertyDefinition.setLocalName("cm:name");
		List<String> aValues = Collections.singletonList("/test");
		aPropertyDefinition.setValues(aValues);
		aDefaultNodeDefinition.addPropertyDefinition(aPropertyDefinition);

		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aDefaultNodeDefinition);
		NodeList aNodeList = aNodeFactory.getNodes(evaluationContext);
		
		assertEquals(1, aNodeList.size());
		Folder aFolder = (Folder)aNodeList.get(0);
		assertEquals("/cm:test", aFolder.getPath());
		
		aValues = Collections.singletonList("//test");
		aPropertyDefinition.setValues(aValues);
		
		aNodeList = aNodeFactory.getNodes(evaluationContext);
		
		assertEquals(1, aNodeList.size());
		aFolder = (Folder)aNodeList.get(0);
		assertEquals("/cm:test", aFolder.getPath());
		
		aValues = Collections.singletonList("/test///test2");
		aPropertyDefinition.setValues(aValues);
		
		aNodeList = aNodeFactory.getNodes(evaluationContext);
		
		assertEquals(2, aNodeList.size());
		aFolder = (Folder)aNodeList.get(1);
		assertEquals("/cm:test/cm:test2", aFolder.getPath());
		
		aValues = Collections.singletonList("/test/test2//");
		aPropertyDefinition.setValues(aValues);
		
		aNodeList = aNodeFactory.getNodes(evaluationContext);
		
		assertEquals(2, aNodeList.size());
		aFolder = (Folder)aNodeList.get(1);
		assertEquals("/cm:test/cm:test2", aFolder.getPath());
	}

	@Test
	public void testComplexPathsWithSubNodesGetNodes() throws Exception {
		FolderDefinition aDefaultNodeDefinition = NodeFactoryUtils.createFolderDefinition("/test/test2//", null, null, null);
		DocumentDefinition aDocumentDefinition = NodeFactoryUtils.createDocumentDefinition("doc", null, null, null, null ,null);
		
		aDefaultNodeDefinition.addDocument(aDocumentDefinition);

		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aDefaultNodeDefinition);
		NodeList aNodeList = aNodeFactory.getNodes(evaluationContext);
		
		assertEquals(3, aNodeList.size());
		assertEquals("/cm:test", ((Folder)aNodeList.get(0)).getPath());
		assertEquals("/cm:test/cm:test2", ((Folder)aNodeList.get(1)).getPath());
		assertEquals("/cm:test/cm:test2/cm:doc", ((Document)aNodeList.get(2)).getPath());
		
	}

	@Test
	public void testSamePropsAlongPathGetNodes() throws Exception {
		FolderDefinition aDefaultNodeDefinition = NodeFactoryUtils.createFolderDefinition("/test/test2", "Test title", null, null);

		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aDefaultNodeDefinition);
		NodeList aNodeList = aNodeFactory.getNodes(evaluationContext);
		
		assertNotNull(aNodeList);
		assertEquals(2, aNodeList.size());
		Folder aFolder1 = (Folder)aNodeList.get(0);
		Folder aFolder2 = (Folder)aNodeList.get(1);
		
		assertEquals("test", aFolder1.getName());
		assertEquals("test2", aFolder2.getName());
		
		assertEquals("Test title", NodeFactoryUtils.getPropertyValues(aFolder1, "{http://www.alfresco.org/model/content/1.0}title").get(0));
		assertEquals("Test title", NodeFactoryUtils.getPropertyValues(aFolder2, "{http://www.alfresco.org/model/content/1.0}title").get(0));
	}
	
	@Test
	public void testConditionalTreeGetNodes() throws Exception {
		FolderDefinition aDefaultNodeDefinition = NodeFactoryUtils.createFolderDefinition("test", null, null, null);
		FolderDefinition aFolder1NodeDefinition = NodeFactoryUtils.createFolderDefinition("s1", null, "true", null);
		FolderDefinition aFolder2NodeDefinition = NodeFactoryUtils.createFolderDefinition("s2", null, "false", null);
		FolderDefinition aFolder3NodeDefinition = NodeFactoryUtils.createFolderDefinition("s3", null, "true", null);
		
		aDefaultNodeDefinition.addFolder(aFolder1NodeDefinition);
		aDefaultNodeDefinition.addFolder(aFolder2NodeDefinition);
		aDefaultNodeDefinition.addFolder(aFolder3NodeDefinition);
		
		DocumentDefinition aDocument1Definition = NodeFactoryUtils.createDocumentDefinition("doc1", null, null, null, null ,null);
		DocumentDefinition aDocument2Definition = NodeFactoryUtils.createDocumentDefinition("doc2", null, null, null, null ,null);
		DocumentDefinition aDocument3Definition = NodeFactoryUtils.createDocumentDefinition("doc3", null, null, null, null ,null);
		
		aFolder1NodeDefinition.addDocument(aDocument1Definition);
		aFolder1NodeDefinition.addDocument(aDocument2Definition);
		aFolder2NodeDefinition.addDocument(aDocument3Definition);

		StopWatch aStopWatch = new StopWatch("testConditionalTreeGetNodes");
		aStopWatch.start("Create a node list");
		
		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aDefaultNodeDefinition);
		NodeList aNodeList = aNodeFactory.getNodes(evaluationContext);
		
		aStopWatch.stop();
		
		assertEquals(5, aNodeList.size());
		assertEquals("/cm:test", ((Folder)aNodeList.get(0)).getPath());
		assertEquals("/cm:test/cm:s1", ((Folder)aNodeList.get(1)).getPath());
		assertEquals("/cm:test/cm:s1/cm:doc1", ((Document)aNodeList.get(2)).getPath());
		assertEquals("/cm:test/cm:s1/cm:doc2", ((Document)aNodeList.get(3)).getPath());
		assertEquals("/cm:test/cm:s3", ((Folder)aNodeList.get(4)).getPath());
		
		System.out.println(aStopWatch.prettyPrint());
	}
	
	@Test
	public void testEvaluationGetNodes() throws Exception {
		FolderDefinition aDefaultNodeDefinition = NodeFactoryUtils.createFolderDefinition("test", null, null, null);
		FolderDefinition aFolder1NodeDefinition = NodeFactoryUtils.createFolderDefinition("@{#in['level1']}", null, "@{#in['level1'].equals('s1')}", "@{#in['customfoldertype']}");
		FolderDefinition aFolder2NodeDefinition = NodeFactoryUtils.createFolderDefinition("@{#in['level2']}", null, "@{#in['level1'].equals('s2')}", null);
		FolderDefinition aFolder3NodeDefinition = NodeFactoryUtils.createFolderDefinition("@{#in['date']}", null, "@{#in['level2'].equals('s2')}", null);
		
		aDefaultNodeDefinition.addFolder(aFolder1NodeDefinition);
		aDefaultNodeDefinition.addFolder(aFolder2NodeDefinition);
		aDefaultNodeDefinition.addFolder(aFolder3NodeDefinition);
		
		DocumentDefinition aDocument1Definition = NodeFactoryUtils.createDocumentDefinition("my_@{#in['content1']}", "Some @{#in['content1']} title", "@{#in['path1']}", "@{#in['encoding']}", "@{#in['mimetype']}" ,"@{#in['customdoctype']}");
		DocumentDefinition aDocument2Definition = NodeFactoryUtils.createDocumentDefinition("my_@{#in['content2']}", "Some @{#in['content2']} title", "file:/otherpath/@{#in['filename2']}", null, null ,null);
		DocumentDefinition aDocument3Definition = NodeFactoryUtils.createDocumentDefinition("doc3", null, null, null, null ,null);
		
		aFolder1NodeDefinition.addDocument(aDocument1Definition);
		aFolder1NodeDefinition.addDocument(aDocument2Definition);
		aFolder2NodeDefinition.addDocument(aDocument3Definition);

		StopWatch aStopWatch = new StopWatch("testEvaluationGetNodes");
		aStopWatch.start("Create a node list");
		
		SimpleNodeFactory aNodeFactory = new SimpleNodeFactory();
		aNodeFactory.setNamespaceContext(namespaceContext);
		aNodeFactory.setNodeDefinition(aDefaultNodeDefinition);
		NodeList aNodeList = aNodeFactory.getNodes(evaluationContext);
		
		aStopWatch.stop();
		
		assertEquals(5, aNodeList.size());
		assertEquals("/cm:test", ((Folder)aNodeList.get(0)).getPath());
		assertEquals("/cm:test/custom:s1", ((Folder)aNodeList.get(1)).getPath());
		assertEquals("/cm:test/custom:s1/custom:my_doc1", ((Document)aNodeList.get(2)).getPath());
		assertEquals("/cm:test/custom:s1/cm:my_doc2", ((Document)aNodeList.get(3)).getPath());
		assertEquals("/cm:test/cm:_x0032_012-07-05", ((Folder)aNodeList.get(4)).getPath()); // TODO : check the encoding
		
		assertEquals("Some doc1 title", NodeFactoryUtils.getPropertyValues(aNodeList.get(2), "{http://www.alfresco.org/model/content/1.0}title").get(0));
		assertEquals("Some doc2 title", NodeFactoryUtils.getPropertyValues(aNodeList.get(3), "{http://www.alfresco.org/model/content/1.0}title").get(0));
		assertEquals("file:/somepath/content1.pdf", ((Document)aNodeList.get(2)).getContentPath());
		assertEquals("file:/otherpath/content2.pdf", ((Document)aNodeList.get(3)).getContentPath());
		assertEquals("UTF-8", ((Document)aNodeList.get(2)).getEncoding());
		assertEquals("application/pdf", ((Document)aNodeList.get(2)).getMimeType());
		assertEquals("{http://custom}doc", ((Document)aNodeList.get(2)).getType().toString());
		assertEquals("{http://custom}folder", ((Folder)aNodeList.get(1)).getType().toString());
		
		System.out.println(aStopWatch.prettyPrint());
	}
	
}
