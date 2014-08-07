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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.Resource;

@RunWith(JUnit4.class)
public class IdentityResourceAwareItemReaderItemStreamTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testRead() throws Exception {
        IdentityResourceAwareItemReaderItemStream aReader = new IdentityResourceAwareItemReaderItemStream();
        aReader.setName("testReader");
        
        Resource aResource = mock(Resource.class);
        when(aResource.getFilename()).thenReturn("file1");
        when(aResource.getDescription()).thenReturn("file1");
        when(aResource.exists()).thenReturn(true);
        
        aReader.setResource(aResource);
        
        aReader.open(new ExecutionContext());
        assertEquals(aResource, aReader.read());
        assertNull(aReader.read());
        aReader.close();
    }
    
    @Test
    public void testReadNotExistsStrict() throws Exception {
        exception.expect(ItemStreamException.class);
        IdentityResourceAwareItemReaderItemStream aReader = new IdentityResourceAwareItemReaderItemStream();
        aReader.setName("testReader");
        aReader.setStrict(true);
        
        Resource aResource = mock(Resource.class);
        when(aResource.getFilename()).thenReturn("file1");
        when(aResource.getDescription()).thenReturn("file1");
        when(aResource.exists()).thenReturn(false);
        
        aReader.setResource(aResource);
        
        aReader.open(new ExecutionContext());
        assertNull(aReader.read());
        aReader.close();
    }
    
    @Test
    public void testReadNotExists() throws Exception {
        IdentityResourceAwareItemReaderItemStream aReader = new IdentityResourceAwareItemReaderItemStream();
        aReader.setName("testReader");
        aReader.setStrict(false);
        
        Resource aResource = mock(Resource.class);
        when(aResource.getFilename()).thenReturn("file1");
        when(aResource.getDescription()).thenReturn("file1");
        when(aResource.exists()).thenReturn(false);
        
        aReader.setResource(aResource);
        
        aReader.open(new ExecutionContext());
        assertNull(aReader.read());
        aReader.close();
    }
    
    @Test
    public void testReadNoResourceStrict() throws Exception {
        exception.expect(ItemStreamException.class);
        IdentityResourceAwareItemReaderItemStream aReader = new IdentityResourceAwareItemReaderItemStream();
        aReader.setName("testReader");
        aReader.setStrict(true);
        
        aReader.setResource(null);
        
        aReader.open(new ExecutionContext());
        assertNull(aReader.read());
        aReader.close();
    }

}
