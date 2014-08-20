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
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.core.io.Resource;

import fr.acxio.tools.agia.io.ResourceFactory;
import fr.acxio.tools.agia.io.ResourcesFactory;

public class MergePDFProcessorTest {

    @Test
    public void testMerge() throws Exception {
        MergingPDDocumentFactory aFactory = new MergingPDDocumentFactory();
        ResourcesFactory aResourcesFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/content1.pdf"));
        Resource aFileResource2 = mock(Resource.class);
        when(aFileResource2.getFile()).thenReturn(new File("src/test/resources/testFiles/content2.pdf"));
        
        when(aResourcesFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1, aFileResource2});
        
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/M-content.pdf"));
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        
        MergePDFProcessor aProcessor = new MergePDFProcessor();
        aProcessor.setSourceFactory(aResourcesFactory);
        aProcessor.setDestinationFactory(aDestinationFactory);
        aProcessor.setDocumentFactory(aFactory);
        aProcessor.setKey("mergedPDF");
        
        Map<String, Object> aResult = aProcessor.process(new HashMap<String, Object>());
        assertNotNull(aResult);
        assertEquals(aDestResource.getFile().getAbsolutePath(), aResult.get("mergedPDF"));
        assertTrue(new File((String)aResult.get("mergedPDF")).exists());
        
        aDestResource.getFile().delete();
    }

}
