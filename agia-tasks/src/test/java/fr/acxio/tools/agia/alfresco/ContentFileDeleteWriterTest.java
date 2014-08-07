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
 
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.util.FileCopyUtils;

import fr.acxio.tools.agia.alfresco.configuration.AlfrescoNamespaceContext;
import fr.acxio.tools.agia.alfresco.domain.Aspect;
import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.alfresco.domain.Property;
import fr.acxio.tools.agia.alfresco.domain.QName;

@RunWith(JUnit4.class)
public class ContentFileDeleteWriterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private AlfrescoNamespaceContext namespaceContext = new AlfrescoNamespaceContext();
	
	@After
    public void tearDown() throws Exception {
        Collection<File> aFilesToDelete = FileUtils.listFiles(new File("target"), new WildcardFileFilter("content*.pdf"), null);
        for(File aFile : aFilesToDelete) {
            FileUtils.deleteQuietly(aFile);
        }
    }
	
	@Test
	public void testWriteNull() throws Exception {
		exception.expect(NullPointerException.class);
		ContentFileDeleteWriter aWriter = new ContentFileDeleteWriter();
		aWriter.write(null);
	}
	
	@Test
	public void testWriteEmpty() throws Exception {
		ContentFileDeleteWriter aWriter = new ContentFileDeleteWriter();
		List<NodeList> aData = new ArrayList<NodeList>();
		aWriter.write(aData);
	}
	
	@Test
	public void testWrite() throws Exception {
		ContentFileDeleteWriter aWriter = new ContentFileDeleteWriter();
		
		File aOriginFile = new File("src/test/resources/testFiles/content1.pdf");
		File aDestinationFile = new File("target/content1.pdf");
		FileCopyUtils.copy(aOriginFile, aDestinationFile);
		
		List<NodeList> aData = new ArrayList<NodeList>();
		aData.add(createNodeList(aDestinationFile.getAbsolutePath()));
		
		assertTrue(aDestinationFile.exists());
		
		aWriter.write(aData);
		
		assertFalse(aDestinationFile.exists());
	}
	
	@Test
	public void testWriteMany() throws Exception {
		ContentFileDeleteWriter aWriter = new ContentFileDeleteWriter();
		
		File aOriginFile = new File("src/test/resources/testFiles/content1.pdf");
		File aDestinationFile1 = new File("target/content2.pdf");
		FileCopyUtils.copy(aOriginFile, aDestinationFile1);
		File aDestinationFile2 = new File("target/content3.pdf");
		FileCopyUtils.copy(aOriginFile, aDestinationFile2);
		
		List<NodeList> aData = new ArrayList<NodeList>();
		aData.add(createNodeList(aDestinationFile1.getAbsolutePath()));
		aData.add(createNodeList(aDestinationFile2.getAbsolutePath()));
		
		assertTrue(aDestinationFile1.exists());
		assertTrue(aDestinationFile2.exists());
		
		aWriter.write(aData);
		
		assertFalse(aDestinationFile1.exists());
		assertFalse(aDestinationFile2.exists());
	}
	
	@Test
	public void testWriteCannotDelete() throws Exception {
		ContentFileDeleteWriter aWriter = new ContentFileDeleteWriter();
		
		File aOriginFile = new File("src/test/resources/testFiles/content1.pdf");
		File aDestinationFile1 = new File("target/content4.pdf");
		FileCopyUtils.copy(aOriginFile, aDestinationFile1);
		
		List<NodeList> aData = new ArrayList<NodeList>();
		aData.add(createNodeList(aDestinationFile1.getAbsolutePath()));
		
		assertTrue(aDestinationFile1.exists());
		
		FileInputStream aInputStream = new FileInputStream(aDestinationFile1);
		FileLock aLock = aInputStream.getChannel().lock(0L, Long.MAX_VALUE, true); // shared lock
		
		aWriter.write(aData);
		
		aLock.release();
		aInputStream.close();
		
		assertTrue(aDestinationFile1.exists());
	}
	
	@Test
	public void testWriteCannotDeleteThrowException() throws Exception {
		ContentFileDeleteWriter aWriter = new ContentFileDeleteWriter();
		aWriter.setIgnoreErrors(false);
		
		File aOriginFile = new File("src/test/resources/testFiles/content1.pdf");
		File aDestinationFile1 = new File("target/content5.pdf");
		FileCopyUtils.copy(aOriginFile, aDestinationFile1);
		
		List<NodeList> aData = new ArrayList<NodeList>();
		aData.add(createNodeList(aDestinationFile1.getAbsolutePath()));
		
		assertTrue(aDestinationFile1.exists());
		
		FileInputStream aInputStream = new FileInputStream(aDestinationFile1);
		FileLock aLock = aInputStream.getChannel().lock(0L, Long.MAX_VALUE, true); // shared lock
		
		try {
			aWriter.write(aData);
			assertTrue(aDestinationFile1.exists());
			fail("Must throw an exception");
		} catch (IOException e) {
			// Fall through
		} finally {
			aLock.release();
			aInputStream.close();
		}
	}

	private NodeList createNodeList(String sContentPath) {
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
		aDocument.setContentPath(sContentPath);
		aDocument.setMimeType("application/pdf");
		aDocument.setPathElement("cm:Test_x0020_Batch_x0020_Writer_x0020_Content");
		
		aNodeList.add(aRootFolder);
		aNodeList.add(aTestFolder);
		aNodeList.add(aDocument);
		
		return aNodeList;
	}
}
