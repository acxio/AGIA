package fr.acxio.tools.agia.io;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.io.Resource;

import fr.acxio.tools.agia.io.AbstractFileOperations.Operation;

@RunWith(JUnit4.class)
public class FilesOperationProcessorTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @After
    public void tearDown() throws Exception {
        Collection<File> aFilesToDelete = FileUtils.listFiles(new File("target"), new WildcardFileFilter("*-input*.csv"), null);
        for(File aFile : aFilesToDelete) {
            FileUtils.deleteQuietly(aFile);
        }
    }

    @Test
    public void testExecuteCopy() throws Exception {
        FilesOperationProcessor aProcessor = new FilesOperationProcessor();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-input.csv"));
        when(aDestResource.exists()).thenReturn(false);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertFalse(aDestResource.getFile().exists());
        
        aProcessor.setSourceFactory(aSourceFactory);
        aProcessor.setDestinationFactory(aDestinationFactory);
        aProcessor.setOperation(Operation.COPY);
        aProcessor.setKey("outputFiles");
        aProcessor.afterPropertiesSet();
        
        Map<String, Object> aData = new HashMap<String, Object>();
        aData.put("k1", "v1");
        
        Map<String, Object> aResult = aProcessor.process(aData);
        assertNotNull(aResult);
        assertNotNull(aResult.get("outputFiles"));
        
        String aOutputFilePath = ((List<String>)aResult.get("outputFiles")).get(0);
        assertEquals(new File("target/CP-input.csv").getCanonicalPath(), aOutputFilePath);
        
        assertTrue(aDestResource.getFile().exists());
        assertTrue(new File(aOutputFilePath).exists());
    }

}
