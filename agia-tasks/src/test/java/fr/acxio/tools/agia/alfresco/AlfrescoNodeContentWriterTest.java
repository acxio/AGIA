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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import fr.acxio.tools.agia.alfresco.configuration.AlfrescoNamespaceContext;
import fr.acxio.tools.agia.alfresco.domain.Aspect;
import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.alfresco.domain.Property;
import fr.acxio.tools.agia.alfresco.domain.QName;
import fr.acxio.tools.agia.alfresco.domain.Node.VersionOperation;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class AlfrescoNodeContentWriterTest {

	@Autowired
	private AlfrescoNamespaceContext namespaceContext;
	
	@Autowired
	private AlfrescoNodeWriter nodeWriter;
	
	@Autowired
	private AlfrescoNodeContentWriter contentWriter;
	
	@Autowired
	private ItemWriter<NodeList> compositeWriter;
	
	@Before
	public void setUp() throws Exception {
		RepositoryServiceSoapBindingStub aRepoService = mock(RepositoryServiceSoapBindingStub.class);
		
		AlfrescoServiceImpl aAlfrescoService = mock(AlfrescoServiceImpl.class);
		when(aAlfrescoService.getRepositoryService()).thenReturn(aRepoService);
		when(aAlfrescoService.getWebappAddress()).thenReturn("webappurl");
		when(aAlfrescoService.getEndpointAddress()).thenReturn("endpoint");
		when(aAlfrescoService.getUsername()).thenReturn("username");
		
		UpdateResult[] aUpdateResult = new UpdateResult[1];
		aUpdateResult[0] = new UpdateResult();
		aUpdateResult[0].setDestination(null);
		when(aRepoService.update(any(CML.class))).thenReturn(aUpdateResult); // FIXME : add a CML matcher that helps to verify the send CML
		
		Node[] aRootNode = new Node[1];
		aRootNode[0] = new Node();
		aRootNode[0].setReference(new Reference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, "00000000-0000-0000-0000-000000000001", null));

		Reference aRootReference = new Reference(AlfrescoServicesConsumer.STORE, null, "/app:company_home");
        Predicate aRootPredicate = new Predicate(new Reference[]{aRootReference}, null, null);
		
		when(aRepoService.get(eq(aRootPredicate))).thenReturn(aRootNode);
		
		nodeWriter.setAlfrescoService(aAlfrescoService);
		nodeWriter.init();
		
		RestTemplate aRestTemplate = mock(RestTemplate.class);
		
		contentWriter.setAlfrescoService(aAlfrescoService);
		contentWriter.setRestTemplate(aRestTemplate);
		contentWriter.init();
	}
	
	@Test
	public void testWrite() throws Exception {
		RepositoryServiceSoapBindingStub aRepoService = nodeWriter.getAlfrescoService().getRepositoryService();
		CML aCML = createDefaultMockResult(aRepoService, "Test Batch Writer Content", "Text content", "/app:company_home/cm:Test_x0020_Batch_x0020_Writer/cm:Test_x0020_Batch_x0020_Writer_x0020_Content", "00000000-0000-0000-0000-000000000003", false);
		List<NodeList> aData = createDefaultNodeListList("Test Batch Writer Content", "Text content", "cm:Test_x0020_Batch_x0020_Writer_x0020_Content", null, false);
		
		compositeWriter.write(aData);
		
		// Verify sent CML
		verify(aRepoService, times(1)).update(eq(aCML));
		verify(aRepoService, times(0)).query(any(Store.class), any(Query.class), anyBoolean());
		verify(aRepoService, times(3)).get(any(Predicate.class));
		
		RestTemplate aRestTemplate = contentWriter.getRestTemplate();
		
		verify(aRestTemplate, times(1)).put(any(String.class), any(Object.class), Matchers.<Map<String,?>>any());
	}
	
	private CML createDefaultMockResult(RepositoryServiceSoapBindingStub sRepoService, String sDocName, String sDocTitle, String sFullPath, String sId, boolean isVersionable) throws Exception {
		CML aCML = new CML();
		ParentReference aRootfolderReference = new ParentReference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, null, "/app:company_home", Constants.ASSOC_CONTAINS, "{http://www.alfresco.org/model/content/1.0}Test Batch Writer");
		NamedValue aFolderNameProperty = new NamedValue("{http://www.alfresco.org/model/content/1.0}name", false, "Test Batch Writer", null);
		CMLCreate aFolderCreate = new CMLCreate("1", aRootfolderReference, null, null, null, "{http://www.alfresco.org/model/content/1.0}folder", new NamedValue[] {aFolderNameProperty});
		
		ParentReference aFolderReference = new ParentReference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, null, "/app:company_home/cm:Test_x0020_Batch_x0020_Writer", Constants.ASSOC_CONTAINS, "{http://www.alfresco.org/model/content/1.0}" + sDocName);
		NamedValue aDocumentNameProperty = new NamedValue("{http://www.alfresco.org/model/content/1.0}name", false, sDocName, null);
		NamedValue aDocumentTitleProperty = new NamedValue("{http://www.alfresco.org/model/content/1.0}title", false, sDocTitle, null);
		CMLCreate aDocumentCreate = new CMLCreate("2", aFolderReference, null, null, null, "{http://www.alfresco.org/model/content/1.0}content", new NamedValue[] {aDocumentNameProperty, aDocumentTitleProperty});
		CMLAddAspect aDocumentAspect = new CMLAddAspect("{http://www.alfresco.org/model/content/1.0}titled", null, null, "2");
		CMLAddAspect aVersionAspect = new CMLAddAspect("{http://www.alfresco.org/model/content/1.0}versionable", null, null, "2");
		FileInputStream aInputStream = new FileInputStream("src/test/resources/testFiles/content1.pdf");
		
		if (isVersionable) {
			aCML.setAddAspect(new CMLAddAspect[] {aDocumentAspect, aVersionAspect});
		} else {
			aCML.setAddAspect(new CMLAddAspect[] {aDocumentAspect});
		}
		aCML.setCreate(new CMLCreate[] {aFolderCreate, aDocumentCreate});
		aCML.setWriteContent(new CMLWriteContent[] {});
		aCML.setCreateAssociation(new CMLCreateAssociation[] {});
		aCML.setUpdate(new CMLUpdate[] {});
		aCML.setDelete(new CMLDelete[] {});
		
		UpdateResult[] aUpdateResult = new UpdateResult[2];
//		aUpdateResult[0] = new UpdateResult();
//		aUpdateResult[0].setDestination(new Reference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, "00000000-0000-0000-0000-000000000001", "/app:company_home"));
		aUpdateResult[0] = new UpdateResult();
		aUpdateResult[0].setDestination(new Reference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, "00000000-0000-0000-0000-000000000002", "/app:company_home/cm:Test_x0020_Batch_x0020_Writer"));
		aUpdateResult[1] = new UpdateResult();
		aUpdateResult[1].setDestination(new Reference(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE, sId, sFullPath));
		
		when(sRepoService.update(eq(aCML))).thenReturn(aUpdateResult);
		
		return aCML;
	}
	
	private List<NodeList> createDefaultNodeListList(String sDocName, String sDocTitle, String sPathElement, VersionOperation sVersionOperation, boolean isVersionable) {
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
		if (sVersionOperation != null) {
			aDocument.setVersionOperation(sVersionOperation);
		}
		aDocument.addProperty(new Property(new QName("cm:name", namespaceContext), sDocName));
		aDocument.addProperty(new Property(new QName("cm:title", namespaceContext), sDocTitle));
		aDocument.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		if (isVersionable) {
			aDocument.addAspect(new Aspect(new QName(Constants.ASPECT_VERSIONABLE, namespaceContext)));
		}
		aDocument.setContentPath("src/test/resources/testFiles/content1.pdf");
		aDocument.setMimeType("application/pdf");
		aDocument.setPathElement(sPathElement);
		
		aNodeList.add(aRootFolder);
		aNodeList.add(aTestFolder);
		aNodeList.add(aDocument);
		
		aData.add(aNodeList);
		
		return aData;
	}
}
