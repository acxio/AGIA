package fr.acxio.tools.agia.file.pdf;

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

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.io.Resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyMapOf;
import fr.acxio.tools.agia.io.ResourcesFactory;

@RunWith(JUnit4.class)
public class MergingPDDocumentFactoryTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testSingleDocument() throws Exception {
        MergingPDDocumentFactory aFactory = new MergingPDDocumentFactory();
        ResourcesFactory aResourcesFactory = mock(ResourcesFactory.class);
        Resource aFileResource = mock(Resource.class);
        when(aFileResource.getFile()).thenReturn(new File("src/test/resources/testFiles/content1.pdf"));
        when(aResourcesFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource});
        
        PDDocumentContainer aContainer = aFactory.fromParts(aResourcesFactory);
        assertNotNull(aContainer);
        assertNotNull(aContainer.getParts());
        assertEquals(1, aContainer.getParts().size());
        assertNotNull(aContainer.getParts().get(0));
        aContainer.close();
    }
    
    @Test
    public void testDoubleDocument() throws Exception {
        MergingPDDocumentFactory aFactory = new MergingPDDocumentFactory();
        ResourcesFactory aResourcesFactory = mock(ResourcesFactory.class);
        Resource aFileResource = mock(Resource.class);
        when(aFileResource.getFile()).thenReturn(new File("src/test/resources/testFiles/content1.pdf"));
        when(aResourcesFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource, aFileResource});
        
        PDDocumentContainer aContainer = aFactory.fromParts(aResourcesFactory);
        assertNotNull(aContainer);
        assertNotNull(aContainer.getParts());
        assertEquals(2, aContainer.getParts().size());
        assertNotNull(aContainer.getParts().get(0));
        assertNotNull(aContainer.getParts().get(1));
        aContainer.close();
    }
    
    @Test
    public void testTwoDocuments() throws Exception {
        MergingPDDocumentFactory aFactory = new MergingPDDocumentFactory();
        ResourcesFactory aResourcesFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/content1.pdf"));
        Resource aFileResource2 = mock(Resource.class);
        when(aFileResource2.getFile()).thenReturn(new File("src/test/resources/testFiles/content2.pdf"));
        
        when(aResourcesFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1, aFileResource2});
        
        PDDocumentContainer aContainer = aFactory.fromParts(aResourcesFactory);
        assertNotNull(aContainer);
        assertNotNull(aContainer.getParts());
        assertEquals(2, aContainer.getParts().size());
        assertNotNull(aContainer.getParts().get(0));
        assertNotNull(aContainer.getParts().get(1));
        aContainer.close();
    }
    
    @Test
    public void testAddDocuments() throws Exception {
        MergingPDDocumentFactory aFactory = new MergingPDDocumentFactory();
        ResourcesFactory aResourcesFactory1 = mock(ResourcesFactory.class);
        ResourcesFactory aResourcesFactory2 = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/content1.pdf"));
        Resource aFileResource2 = mock(Resource.class);
        when(aFileResource2.getFile()).thenReturn(new File("src/test/resources/testFiles/content2.pdf"));
        
        when(aResourcesFactory1.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        when(aResourcesFactory2.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource2});
        
        PDDocumentContainer aContainer1 = aFactory.fromParts(aResourcesFactory1);
        PDDocumentContainer aContainer2 = aFactory.addParts(aContainer1, aResourcesFactory2);
        
        assertNotNull(aContainer1);
        assertNotNull(aContainer1.getParts());
        assertEquals(1, aContainer1.getParts().size());
        assertNotNull(aContainer1.getParts().get(0));
        
        assertNotNull(aContainer2);
        assertNotNull(aContainer2.getParts());
        assertEquals(2, aContainer2.getParts().size());
        assertNotNull(aContainer2.getParts().get(0));
        assertNotNull(aContainer2.getParts().get(1));
        
        aContainer1.close();
        aContainer2.close();
    }
    
    @Test
    public void testNoDocument() throws Exception {
        MergingPDDocumentFactory aFactory = new MergingPDDocumentFactory();
        ResourcesFactory aResourcesFactory = mock(ResourcesFactory.class);
        
        when(aResourcesFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{});
        
        PDDocumentContainer aContainer = aFactory.fromParts(aResourcesFactory);
        assertNotNull(aContainer);
        assertNotNull(aContainer.getParts());
        assertEquals(0, aContainer.getParts().size());
        aContainer.close();
    }
    
    @Test
    public void testNullDocument() throws Exception {
        MergingPDDocumentFactory aFactory = new MergingPDDocumentFactory();
        ResourcesFactory aResourcesFactory = mock(ResourcesFactory.class);
        
        PDDocumentContainer aContainer = aFactory.fromParts(aResourcesFactory);
        assertNotNull(aContainer);
        assertNotNull(aContainer.getParts());
        assertEquals(0, aContainer.getParts().size());
        aContainer.close();
    }

    @Test
    public void testDocumentDoesNotExist() throws Exception {
        exception.expect(PDDocumentFactoryException.class);
        MergingPDDocumentFactory aFactory = new MergingPDDocumentFactory();
        ResourcesFactory aResourcesFactory = mock(ResourcesFactory.class);
        Resource aFileResource = mock(Resource.class);
        when(aFileResource.getFile()).thenReturn(new File("src/test/resources/testFiles/contentX.pdf"));
        when(aResourcesFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource});
        
        PDDocumentContainer aContainer = aFactory.fromParts(aResourcesFactory);
        assertNotNull(aContainer);
        assertNotNull(aContainer.getParts());
        assertEquals(1, aContainer.getParts().size());
        assertNotNull(aContainer.getParts().get(0));
        aContainer.close();
    }
}
