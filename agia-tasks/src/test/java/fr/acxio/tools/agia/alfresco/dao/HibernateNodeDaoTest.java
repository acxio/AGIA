package fr.acxio.tools.agia.alfresco.dao;

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

import java.io.FileInputStream;
import java.util.Date;
import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fr.acxio.tools.agia.alfresco.configuration.AlfrescoNamespaceContext;
import fr.acxio.tools.agia.alfresco.domain.Aspect;
import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.Property;
import fr.acxio.tools.agia.alfresco.domain.QName;
import fr.acxio.tools.agia.alfresco.domain.QueryAssociation;
import fr.acxio.tools.agia.alfresco.domain.RefAssociation;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
public class HibernateNodeDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired
	private NodeDao nodeDao;
	
	@Autowired
	private DataSource businessDataSource;
	
	@Autowired
	private SessionFactory businessSessionFactory;
	
	protected IDatabaseTester dbTester;
	
	private AlfrescoNamespaceContext namespaceContext = new AlfrescoNamespaceContext();

	@Before
	public void setUp() throws Exception {
		dbTester = new DataSourceDatabaseTester(businessDataSource);
		FlatXmlDataSetBuilder aBuilder = new FlatXmlDataSetBuilder();
		FileInputStream aDTDStream = new FileInputStream("src/test/resources/db/dataset.dtd");
		FileInputStream aDataStream = new FileInputStream("src/test/resources/db/dataset.xml");
		aBuilder.setMetaDataSetFromDtd(aDTDStream);
		IDataSet dataSet = aBuilder.build(aDataStream);
		aDTDStream.close();
		aDataStream.close();
		dbTester.setDataSet(dataSet);
		dbTester.onSetup();
	}

	@Test
	@DirtiesContext
	public void testFindById() throws Exception {
		Node aNode = nodeDao.findById(1);
		assertEquals("company_home", aNode.getName());
		assertEquals("{http://www.alfresco.org/model/content/1.0}folder", aNode.getType().toString());
		assertEquals(0, aNode.getAspects().size());
		assertEquals(1, aNode.getProperties().size());
		assertEquals("{http://www.alfresco.org/model/content/1.0}name", aNode.getProperties().get(0).getName().toString());
		assertEquals("company_home", aNode.getProperties().get(0).getValues().get(0));
		assertTrue(aNode instanceof Folder);
		Folder aFolder = (Folder)aNode;
		assertEquals(0, aFolder.getDocuments().size());
		assertEquals(1, aFolder.getFolders().size());
		Folder aSubFolder = aFolder.getFolders().get(0);
		assertEquals("Test Batch Writer", aSubFolder.getName());
		assertEquals("{http://www.alfresco.org/model/content/1.0}folder", aSubFolder.getType().toString());
		assertEquals(1, aSubFolder.getDocuments().size());
		assertEquals(0, aSubFolder.getFolders().size());
		Document aDocument = aSubFolder.getDocuments().get(0);
		assertEquals("Test Batch Writer Content", aDocument.getName());
		assertEquals("{http://www.alfresco.org/model/content/1.0}content", aDocument.getType().toString());
		assertEquals(1, aDocument.getAspects().size());
		assertEquals(2, aDocument.getProperties().size());
		assertEquals(2, aDocument.getAssociations().size());
		assertEquals("{http://www.alfresco.org/model/content/1.0}titled", aDocument.getAspects().get(0).getName().toString());
		assertTrue(aDocument.getAssociations().get(0) instanceof RefAssociation);
		RefAssociation aRefAssociation = (RefAssociation)aDocument.getAssociations().get(0);
		assertEquals("{http://www.alfresco.org/model/content/1.0}rel", aRefAssociation.getType().toString());
		assertEquals("1", aRefAssociation.getReference().toString());
		assertTrue(aDocument.getAssociations().get(1) instanceof QueryAssociation);
		QueryAssociation aQueryAssociation = (QueryAssociation)aDocument.getAssociations().get(1);
		assertEquals("{http://www.alfresco.org/model/content/1.0}rel", aQueryAssociation.getType().toString());
		assertEquals("lucene", aQueryAssociation.getQueryLanguage().toString());
		assertEquals("ASPECT:\"custom:otheraspect\"", aQueryAssociation.getQuery().toString());
	}

	@Test
	@DirtiesContext
	public void testSaveOrUpdate() throws Exception {
		QName aQNFolder = new QName("cm:folder", namespaceContext);
		QName aQNName = new QName("cm:name", namespaceContext);
		QName aQNAssoc = new QName("cm:rel", namespaceContext);

		Folder aRootFolder = new Folder();
		aRootFolder.setParent(null);
		aRootFolder.setType(aQNFolder);
		aRootFolder.addProperty(new Property(aQNName, "company_home"));
		aRootFolder.setPathElement("app:company_home");
		aRootFolder.setAddedTimestamp(new Date());
		
		Folder aTestFolder = new Folder();
		aTestFolder.setParent(aRootFolder);
		aRootFolder.addFolder(aTestFolder);
		aTestFolder.setType(aQNFolder);
		aTestFolder.addProperty(new Property(aQNName, "Test Batch Writer"));
		aTestFolder.setPathElement("cm:Test_x0020_Batch_x0020_Writer");
		aTestFolder.setAddedTimestamp(new Date());
		
		Document aDocument = new Document();
		aDocument.setParent(aTestFolder);
		aTestFolder.addDocument(aDocument);
		aDocument.setType(new QName("cm:content", namespaceContext));
		aDocument.addProperty(new Property(aQNName, "Test Batch Writer Content"));
		aDocument.addProperty(new Property(new QName("cm:title", namespaceContext), "Text content"));
		aDocument.addAspect(new Aspect(new QName("cm:titled", namespaceContext)));
		aDocument.addAssociation(new RefAssociation(aQNAssoc, "1"));
		aDocument.addAssociation(new QueryAssociation(aQNAssoc, "lucene", "ASPECT:\"custom:otheraspect\""));
		aDocument.setContentPath("src/test/resources/testFiles/content1.pdf");
		aDocument.setMimeType("application/pdf");
		aDocument.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_Content");
		aDocument.setAddedTimestamp(new Date());
		
		nodeDao.saveOrUpdate(aRootFolder);
		businessSessionFactory.getCurrentSession().flush();
		businessSessionFactory.getCurrentSession().clear();
	}

	@Test
	@DirtiesContext
	public void testDelete() throws Exception {
		Node aNode = nodeDao.findById(1);
		nodeDao.delete(aNode);
	}

}
