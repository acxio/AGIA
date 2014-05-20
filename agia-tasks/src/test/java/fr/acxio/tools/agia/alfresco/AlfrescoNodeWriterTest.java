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
 
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLCreateAssociation;
import org.alfresco.webservice.types.CMLDelete;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.CMLWriteContent;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import fr.acxio.tools.agia.alfresco.configuration.AlfrescoNamespaceContext;
import fr.acxio.tools.agia.alfresco.domain.Aspect;
import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.alfresco.domain.Property;
import fr.acxio.tools.agia.alfresco.domain.QName;
import fr.acxio.tools.agia.alfresco.domain.QueryAssociation;
import fr.acxio.tools.agia.alfresco.domain.RefAssociation;

@RunWith(JUnit4.class)
public class AlfrescoNodeWriterTest {
	
	private AlfrescoNamespaceContext namespaceContext = new AlfrescoNamespaceContext();
	private AlfrescoNodeWriter nodeWriter = new AlfrescoNodeWriter();
	private NodePathResolver nodePathResolver = new DefaultNodePathResolver();

	@Before
	public void setUp() throws Exception {
		RepositoryServiceSoapBindingStub aRepoService = mock(RepositoryServiceSoapBindingStub.class);
		
		AlfrescoServiceImpl aAlfrescoService = mock(AlfrescoServiceImpl.class);
		when(aAlfrescoService.getRepositoryService()).thenReturn(aRepoService);
		when(aAlfrescoService.getEndpointAddress()).thenReturn("endpoint");
		when(aAlfrescoService.getUsername()).thenReturn("username");
		
		UpdateResult[] aUpdateResult = new UpdateResult[1];
		aUpdateResult[0] = new UpdateResult();
		aUpdateResult[0].setDestination(null);
		when(aRepoService.update(any(CML.class))).thenReturn(aUpdateResult); // FIXME : add a CML matcher that helps to verify the send CML
		
		Node[] aNode = new Node[1];
		aNode[0] = new Node();
		aNode[0].setReference(new Reference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, "00000000-0000-0000-0000-000000000001", null));

		Reference reference = new Reference(AlfrescoServicesConsumer.STORE, null, "/app:company_home");
        Predicate predicate = new Predicate(new Reference[]{reference}, null, null);
		
		when(aRepoService.get(eq(predicate))).thenReturn(aNode);
		
		Node[] aManyNodes = new Node[2];
		aManyNodes[0] = new Node();
		aManyNodes[0].setReference(new Reference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, "00000000-0000-0000-0000-000000000002", null));
		aManyNodes[1] = new Node();
		aManyNodes[1].setReference(new Reference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, "00000000-0000-0000-0000-000000000003", null));

		Reference referenceMany = new Reference(AlfrescoServicesConsumer.STORE, null, "/cm:many");
        Predicate predicateMany = new Predicate(new Reference[]{referenceMany}, null, null);
		
		when(aRepoService.get(eq(predicateMany))).thenReturn(aManyNodes);
		
		nodeWriter.setAlfrescoService(aAlfrescoService);
		nodeWriter.setNodePathResolver(nodePathResolver);
		nodeWriter.init();
	}

	@After
	public void tearDown() throws Exception {
		nodeWriter.cleanup();
	}

	@Test(expected=NullPointerException.class)
	public void testWriteNull() throws Exception {
		nodeWriter.write(null);
	}
	
	@Test
	public void testWriteEmpty() throws Exception {
		List<NodeList> aData = new ArrayList<NodeList>();
		nodeWriter.write(aData);
		
		RepositoryServiceSoapBindingStub aRepoService = nodeWriter.getAlfrescoService().getRepositoryService();
		verify(aRepoService, times(0)).update(any(CML.class));
		verify(aRepoService, times(0)).query(any(Store.class), any(Query.class), anyBoolean());
		verify(aRepoService, times(0)).get(any(Predicate.class));
	}
	
	@Test(expected=VersionOperationException.class)
	public void testWriteTooManyNodesForOnePath() throws Exception {
		List<NodeList> aData = new ArrayList<NodeList>();
		NodeList aNodeList = new NodeList();
		
		Folder aRootFolder = new Folder();
		aRootFolder.setParent(null);
		aRootFolder.setType(new QName("cm:folder", namespaceContext));
		aRootFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "many"));
		aRootFolder.setPathElement("cm:many");
		
		aNodeList.add(aRootFolder);
		aData.add(aNodeList);
		
		nodeWriter.write(aData);
	}
	
	@Test
	public void testWrite() throws Exception {
		List<NodeList> aData = new ArrayList<NodeList>();
		NodeList aNodeList = new NodeList();
		
		Folder aRootFolder = new Folder();
		aRootFolder.setParent(null);
		aRootFolder.setType(new QName("cm:folder", namespaceContext));
		aRootFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "company_home"));
		aRootFolder.setPathElement("app:company_home");
		
		Folder aTestFolder = new Folder();
		aTestFolder.setParent(aRootFolder);
		aRootFolder.addFolder(aTestFolder);
		aTestFolder.setType(new QName("cm:folder", namespaceContext));
		aTestFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer"));
		aTestFolder.setPathElement("cm:Test_x0020_Batch_x0020_Writer");
		
		Document aDocument = new Document();
		aDocument.setParent(aTestFolder);
		aTestFolder.addDocument(aDocument);
		aDocument.setType(new QName("cm:content", namespaceContext));
		aDocument.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer Content"));
		aDocument.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aDocument.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aDocument.setContentPath("src/test/resources/testFiles/content1.pdf");
		aDocument.setMimeType("application/pdf");
		aDocument.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_Content");
		
		aNodeList.add(aRootFolder);
		aNodeList.add(aTestFolder);
		aNodeList.add(aDocument);
		
		aData.add(aNodeList);
		
		nodeWriter.write(aData);
		
		// Verify sent CML
		RepositoryServiceSoapBindingStub aRepoService = nodeWriter.getAlfrescoService().getRepositoryService();
		CML aCML = new CML();
		ParentReference aRootfolderReference = new ParentReference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, null, "/app:company_home", Constants.ASSOC_CONTAINS, "{http://www.alfresco.org/model/content/1.0}Test Batch Writer");
		NamedValue aFolderNameProperty = new NamedValue("{http://www.alfresco.org/model/content/1.0}name", false, "Test Batch Writer", null);
		CMLCreate aFolderCreate = new CMLCreate("1", aRootfolderReference, null, null, null, "{http://www.alfresco.org/model/content/1.0}folder", new NamedValue[] {aFolderNameProperty});
		
		ParentReference aFolderReference = new ParentReference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, null, "/app:company_home/cm:Test_x0020_Batch_x0020_Writer", Constants.ASSOC_CONTAINS, "{http://www.alfresco.org/model/content/1.0}Test Batch Writer Content");
		NamedValue aDocumentNameProperty = new NamedValue("{http://www.alfresco.org/model/content/1.0}name", false, "Test Batch Writer Content", null);
		NamedValue aDocumentTitleProperty = new NamedValue("{http://www.alfresco.org/model/content/1.0}title", false, "Text content", null);
		CMLCreate aDocumentCreate = new CMLCreate("2", aFolderReference, null, null, null, "{http://www.alfresco.org/model/content/1.0}content", new NamedValue[] {aDocumentNameProperty, aDocumentTitleProperty});
		CMLAddAspect aDocumentAspect = new CMLAddAspect("{http://www.alfresco.org/model/content/1.0}titled", null, null, "2");
		FileInputStream aInputStream = new FileInputStream("src/test/resources/testFiles/content1.pdf");
		CMLWriteContent aDocumentContent = new CMLWriteContent(Constants.PROP_CONTENT, ContentUtils.convertToByteArray(aInputStream), new ContentFormat("application/pdf", null), null, "2");
		
		aCML.setAddAspect(new CMLAddAspect[] {aDocumentAspect});
		aCML.setCreate(new CMLCreate[] {aFolderCreate, aDocumentCreate});
		aCML.setWriteContent(new CMLWriteContent[] {aDocumentContent});
		aCML.setCreateAssociation(new CMLCreateAssociation[] {});
		aCML.setUpdate(new CMLUpdate[] {});
		aCML.setDelete(new CMLDelete[] {});
		
		verify(aRepoService, times(1)).update(eq(aCML));
		verify(aRepoService, times(0)).query(any(Store.class), any(Query.class), anyBoolean());
		verify(aRepoService, times(3)).get(any(Predicate.class));
	}
	
	@Test
	public void testWriteRefAssoc() throws Exception {
		List<NodeList> aData = new ArrayList<NodeList>();
		NodeList aNodeList = new NodeList();
		
		Folder aRootFolder = new Folder();
		aRootFolder.setParent(null);
		aRootFolder.setType(new QName("cm:folder", namespaceContext));
		aRootFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "company_home"));
		aRootFolder.setPathElement("app:company_home");
		
		Folder aTestFolder = new Folder();
		aTestFolder.setParent(aRootFolder);
		aRootFolder.addFolder(aTestFolder);
		aTestFolder.setType(new QName("cm:folder", namespaceContext));
		aTestFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer"));
		aTestFolder.setPathElement("cm:Test_x0020_Batch_x0020_Writer");
		
		Document aSourceDocument = new Document();
		aSourceDocument.setParent(aTestFolder);
		aTestFolder.addDocument(aSourceDocument);
		aSourceDocument.setType(new QName("cm:content", namespaceContext));
		aSourceDocument.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer Source"));
		aSourceDocument.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aSourceDocument.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aSourceDocument.addAspect(new Aspect(new QName("cm:referencing", namespaceContext)));
		aSourceDocument.setContentPath("src/test/resources/testFiles/content1.pdf");
		aSourceDocument.setMimeType("application/pdf");
		aSourceDocument.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_Source");
		aSourceDocument.addAssociation(new RefAssociation(new QName("cm:references", namespaceContext), "2"));
		
		Document aTargetDocument = new Document();
		aTargetDocument.setParent(aTestFolder);
		aTestFolder.addDocument(aTargetDocument);
		aTargetDocument.setAssocTargetId("2");
		aTargetDocument.setType(new QName("cm:content", namespaceContext));
		aTargetDocument.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer Target"));
		aTargetDocument.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aTargetDocument.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aTargetDocument.setContentPath("src/test/resources/testFiles/content1.pdf");
		aTargetDocument.setMimeType("application/pdf");
		aTargetDocument.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_Target");
		
		aNodeList.add(aRootFolder);
		aNodeList.add(aTestFolder);
		aNodeList.add(aSourceDocument);
		aNodeList.add(aTargetDocument);
		
		aData.add(aNodeList);
		
		nodeWriter.write(aData);
		
		// Verify sent CML
		RepositoryServiceSoapBindingStub aRepoService = nodeWriter.getAlfrescoService().getRepositoryService();
		CML aCML = new CML();
		ParentReference aRootfolderReference = new ParentReference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, null, "/app:company_home", Constants.ASSOC_CONTAINS, "{http://www.alfresco.org/model/content/1.0}Test Batch Writer");
		NamedValue aFolderNameProperty = new NamedValue("{http://www.alfresco.org/model/content/1.0}name", false, "Test Batch Writer", null);
		CMLCreate aFolderCreate = new CMLCreate("1", aRootfolderReference, null, null, null, "{http://www.alfresco.org/model/content/1.0}folder", new NamedValue[] {aFolderNameProperty});
		
		ParentReference aFolderReference = new ParentReference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, null, "/app:company_home/cm:Test_x0020_Batch_x0020_Writer", Constants.ASSOC_CONTAINS, "{http://www.alfresco.org/model/content/1.0}Test Batch Writer Source");
		
		NamedValue aDocumentNameProperty = new NamedValue("{http://www.alfresco.org/model/content/1.0}name", false, "Test Batch Writer Source", null);
		NamedValue aDocumentTitleProperty = new NamedValue("{http://www.alfresco.org/model/content/1.0}title", false, "Text content", null);
		CMLCreate aDocumentCreate = new CMLCreate("2", aFolderReference, null, null, null, "{http://www.alfresco.org/model/content/1.0}content", new NamedValue[] {aDocumentNameProperty, aDocumentTitleProperty});
		CMLAddAspect aDocumentTitledAspect = new CMLAddAspect("{http://www.alfresco.org/model/content/1.0}titled", null, null, "2");
		CMLAddAspect aDocumentReferencingAspect = new CMLAddAspect("{http://www.alfresco.org/model/content/1.0}referencing", null, null, "2");
		FileInputStream aInputStream = new FileInputStream("src/test/resources/testFiles/content1.pdf");
		CMLWriteContent aDocumentContent = new CMLWriteContent(Constants.PROP_CONTENT, ContentUtils.convertToByteArray(aInputStream), new ContentFormat("application/pdf", null), null, "2");
		
		ParentReference aFolderReference2 = new ParentReference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, null, "/app:company_home/cm:Test_x0020_Batch_x0020_Writer", Constants.ASSOC_CONTAINS, "{http://www.alfresco.org/model/content/1.0}Test Batch Writer Target");
		
		NamedValue aDocument2NameProperty = new NamedValue("{http://www.alfresco.org/model/content/1.0}name", false, "Test Batch Writer Target", null);
		NamedValue aDocument2TitleProperty = new NamedValue("{http://www.alfresco.org/model/content/1.0}title", false, "Text content", null);
		CMLCreate aDocument2Create = new CMLCreate("3", aFolderReference2, null, null, null, "{http://www.alfresco.org/model/content/1.0}content", new NamedValue[] {aDocument2NameProperty, aDocument2TitleProperty});
		CMLAddAspect aDocument2TitledAspect = new CMLAddAspect("{http://www.alfresco.org/model/content/1.0}titled", null, null, "3");
		FileInputStream aInputStream2 = new FileInputStream("src/test/resources/testFiles/content1.pdf");
		CMLWriteContent aDocument2Content = new CMLWriteContent(Constants.PROP_CONTENT, ContentUtils.convertToByteArray(aInputStream2), new ContentFormat("application/pdf", null), null, "3");
		
		CMLCreateAssociation aCMLCreateAssociation = new CMLCreateAssociation(null, "2", null, "3", "{http://www.alfresco.org/model/content/1.0}references");
		
		aCML.setAddAspect(new CMLAddAspect[] {aDocumentTitledAspect, aDocumentReferencingAspect, aDocument2TitledAspect});
		aCML.setCreate(new CMLCreate[] {aFolderCreate, aDocumentCreate, aDocument2Create});
		aCML.setWriteContent(new CMLWriteContent[] {aDocumentContent, aDocument2Content});
		aCML.setCreateAssociation(new CMLCreateAssociation[] {aCMLCreateAssociation});
		aCML.setUpdate(new CMLUpdate[] {});
		aCML.setDelete(new CMLDelete[] {});
		
		verify(aRepoService, times(1)).update(eq(aCML));
		verify(aRepoService, times(0)).query(any(Store.class), any(Query.class), anyBoolean());
		verify(aRepoService, times(4)).get(any(Predicate.class));
	}
	
	@Test
	public void testWriteManyRefAssoc() throws Exception {
		List<NodeList> aData = new ArrayList<NodeList>();
		NodeList aNodeList = new NodeList();
		
		Folder aRootFolder = new Folder();
		aRootFolder.setParent(null);
		aRootFolder.setType(new QName("cm:folder", namespaceContext));
		aRootFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "company_home"));
		aRootFolder.setPathElement("app:company_home");
		
		Folder aTestFolder = new Folder();
		aTestFolder.setParent(aRootFolder);
		aRootFolder.addFolder(aTestFolder);
		aTestFolder.setType(new QName("cm:folder", namespaceContext));
		aTestFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer"));
		aTestFolder.setPathElement("cm:Test_x0020_Batch_x0020_Writer");
		
		Document aSourceDocument = new Document();
		aSourceDocument.setParent(aTestFolder);
		aTestFolder.addDocument(aSourceDocument);
		aSourceDocument.setType(new QName("cm:content", namespaceContext));
		aSourceDocument.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer SourceMR"));
		aSourceDocument.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aSourceDocument.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aSourceDocument.addAspect(new Aspect(new QName("cm:referencing", namespaceContext)));
		aSourceDocument.setContentPath("src/test/resources/testFiles/content1.pdf");
		aSourceDocument.setMimeType("application/pdf");
		aSourceDocument.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_SourceMR");
		aSourceDocument.addAssociation(new RefAssociation(new QName("cm:references", namespaceContext), "2"));
		
		Document aTargetDocument1 = new Document();
		aTargetDocument1.setParent(aTestFolder);
		aTestFolder.addDocument(aTargetDocument1);
		aTargetDocument1.setAssocTargetId("2");
		aTargetDocument1.setType(new QName("cm:content", namespaceContext));
		aTargetDocument1.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer TargetM1"));
		aTargetDocument1.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aTargetDocument1.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aTargetDocument1.setContentPath("src/test/resources/testFiles/content1.pdf");
		aTargetDocument1.setMimeType("application/pdf");
		aTargetDocument1.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_TargetM1");
		
		Document aTargetDocument2 = new Document();
		aTargetDocument2.setParent(aTestFolder);
		aTestFolder.addDocument(aTargetDocument2);
		aTargetDocument2.setAssocTargetId("2"); // Same target id must create same association
		aTargetDocument2.setType(new QName("cm:content", namespaceContext));
		aTargetDocument2.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer TargetM2"));
		aTargetDocument2.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aTargetDocument2.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aTargetDocument2.setContentPath("src/test/resources/testFiles/content1.pdf");
		aTargetDocument2.setMimeType("application/pdf");
		aTargetDocument2.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_TargetM2");
		
		aNodeList.add(aRootFolder);
		aNodeList.add(aTestFolder);
		aNodeList.add(aSourceDocument);
		aNodeList.add(aTargetDocument1);
		aNodeList.add(aTargetDocument2);
		
		aData.add(aNodeList);
		
		nodeWriter.write(aData);
	}
	
	@Test
	public void testWriteRefAssocNoTarget() throws Exception {
		List<NodeList> aData = new ArrayList<NodeList>();
		NodeList aNodeList = new NodeList();
		
		Folder aRootFolder = new Folder();
		aRootFolder.setParent(null);
		aRootFolder.setType(new QName("cm:folder", namespaceContext));
		aRootFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "company_home"));
		aRootFolder.setPathElement("app:company_home");
		
		Folder aTestFolder = new Folder();
		aTestFolder.setParent(aRootFolder);
		aRootFolder.addFolder(aTestFolder);
		aTestFolder.setType(new QName("cm:folder", namespaceContext));
		aTestFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer"));
		aTestFolder.setPathElement("cm:Test_x0020_Batch_x0020_Writer");
		
		Document aSourceDocument = new Document();
		aSourceDocument.setParent(aTestFolder);
		aTestFolder.addDocument(aSourceDocument);
		aSourceDocument.setType(new QName("cm:content", namespaceContext));
		aSourceDocument.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer SourceRN"));
		aSourceDocument.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aSourceDocument.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aSourceDocument.addAspect(new Aspect(new QName("cm:referencing", namespaceContext)));
		aSourceDocument.setContentPath("src/test/resources/testFiles/content1.pdf");
		aSourceDocument.setMimeType("application/pdf");
		aSourceDocument.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_SourceRN");
		aSourceDocument.addAssociation(new RefAssociation(new QName("cm:references", namespaceContext), "2"));
		
		Document aTargetDocument = new Document();
		aTargetDocument.setParent(aTestFolder);
		aTestFolder.addDocument(aTargetDocument);
		aTargetDocument.setAssocTargetId("3");
		aTargetDocument.setType(new QName("cm:content", namespaceContext));
		aTargetDocument.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer TargetN"));
		aTargetDocument.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aTargetDocument.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aTargetDocument.setContentPath("src/test/resources/testFiles/content1.pdf");
		aTargetDocument.setMimeType("application/pdf");
		aTargetDocument.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_TargetN");
		
		aNodeList.add(aRootFolder);
		aNodeList.add(aTestFolder);
		aNodeList.add(aSourceDocument);
		aNodeList.add(aTargetDocument);
		
		aData.add(aNodeList);
		
		nodeWriter.write(aData);
		
	}
	
	@Test
	public void testWriteQueryAssoc() throws Exception {
		List<NodeList> aData = new ArrayList<NodeList>();
		NodeList aNodeList = new NodeList();
		
		Folder aRootFolder = new Folder();
		aRootFolder.setParent(null);
		aRootFolder.setType(new QName("cm:folder", namespaceContext));
		aRootFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "company_home"));
		aRootFolder.setPathElement("app:company_home");
		
		Folder aTestFolder = new Folder();
		aTestFolder.setParent(aRootFolder);
		aRootFolder.addFolder(aTestFolder);
		aTestFolder.setType(new QName("cm:folder", namespaceContext));
		aTestFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer"));
		aTestFolder.setPathElement("cm:Test_x0020_Batch_x0020_Writer");
		
		Document aTargetDocument = new Document();
		aTargetDocument.setParent(aTestFolder);
		aTestFolder.addDocument(aTargetDocument);
		aTargetDocument.setType(new QName("cm:content", namespaceContext));
		aTargetDocument.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer TargetQ"));
		aTargetDocument.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aTargetDocument.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aTargetDocument.setContentPath("src/test/resources/testFiles/content1.pdf");
		aTargetDocument.setMimeType("application/pdf");
		aTargetDocument.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_TargetQ");
		
		Document aTargetDocument2 = new Document();
		aTargetDocument2.setParent(aTestFolder);
		aTestFolder.addDocument(aTargetDocument2);
		aTargetDocument2.setType(new QName("cm:content", namespaceContext));
		aTargetDocument2.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer TargetQ2"));
		aTargetDocument2.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aTargetDocument2.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aTargetDocument2.setContentPath("src/test/resources/testFiles/content1.pdf");
		aTargetDocument2.setMimeType("application/pdf");
		aTargetDocument2.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_TargetQ2");
		
		aNodeList.add(aRootFolder);
		aNodeList.add(aTestFolder);
		aNodeList.add(aTargetDocument);
		aNodeList.add(aTargetDocument2);
		
		aData.add(aNodeList);
		nodeWriter.write(aData);
		
		aNodeList.clear();
		aData.clear();
		
		Document aSourceDocument = new Document();
		aSourceDocument.setParent(aTestFolder);
		aTestFolder.addDocument(aSourceDocument);
		aSourceDocument.setType(new QName("cm:content", namespaceContext));
		aSourceDocument.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer SourceQ"));
		aSourceDocument.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aSourceDocument.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aSourceDocument.addAspect(new Aspect(new QName("cm:referencing", namespaceContext)));
		aSourceDocument.setContentPath("src/test/resources/testFiles/content1.pdf");
		aSourceDocument.setMimeType("application/pdf");
		aSourceDocument.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_SourceQ");
		aSourceDocument.addAssociation(new QueryAssociation(new QName("cm:references", namespaceContext), "lucene", "PATH:\"/app:company_home/cm:Test_x0020_Batch_x0020_Writer/*\" AND @cm\\:name:\"Test Batch Writer TargetQ*\""));
		
		aNodeList.add(aRootFolder);
		aNodeList.add(aTestFolder);
		aNodeList.add(aSourceDocument);
		
		aData.add(aNodeList);
		
		nodeWriter.write(aData);
		
	}
	
	@Test
	public void testWriteQueryAssocNoTarget() throws Exception {
		List<NodeList> aData = new ArrayList<NodeList>();
		NodeList aNodeList = new NodeList();
		
		Folder aRootFolder = new Folder();
		aRootFolder.setParent(null);
		aRootFolder.setType(new QName("cm:folder", namespaceContext));
		aRootFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "company_home"));
		aRootFolder.setPathElement("app:company_home");
		
		Folder aTestFolder = new Folder();
		aTestFolder.setParent(aRootFolder);
		aRootFolder.addFolder(aTestFolder);
		aTestFolder.setType(new QName("cm:folder", namespaceContext));
		aTestFolder.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer"));
		aTestFolder.setPathElement("cm:Test_x0020_Batch_x0020_Writer");
		
		Document aSourceDocument = new Document();
		aSourceDocument.setParent(aTestFolder);
		aTestFolder.addDocument(aSourceDocument);
		aSourceDocument.setType(new QName("cm:content", namespaceContext));
		aSourceDocument.addProperty(new Property(new QName("cm:name", namespaceContext), "Test Batch Writer SourceQN"));
		aSourceDocument.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aSourceDocument.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aSourceDocument.addAspect(new Aspect(new QName("cm:referencing", namespaceContext)));
		aSourceDocument.setContentPath("src/test/resources/testFiles/content1.pdf");
		aSourceDocument.setMimeType("application/pdf");
		aSourceDocument.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_SourceQN");
		aSourceDocument.addAssociation(new QueryAssociation(new QName("cm:references", namespaceContext), "lucene", "PATH:\"/app:company_home/cm:Test_x0020_Batch_x0020_Writer/*\" AND @cm\\:name:\"UNKNOWN*\""));
		
		aNodeList.add(aRootFolder);
		aNodeList.add(aTestFolder);
		aNodeList.add(aSourceDocument);
		
		aData.add(aNodeList);
		
		nodeWriter.write(aData);
		
	}

}
