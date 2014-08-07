package fr.acxio.tools.agia.file;

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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

public class ExtendedMultiResourceItemReaderTest {
    
    private interface IdentityResourceAwareItemReaderItemStream extends ResourceAwareItemReaderItemStream<Resource> {
    }

    @Test
    public void testReadResources() throws Exception {
        ExtendedMultiResourceItemReader<Resource> aReader = new ExtendedMultiResourceItemReader<Resource>();
        
        IdentityResourceAwareItemReaderItemStream aDelegate = mock(IdentityResourceAwareItemReaderItemStream.class);
        
        aReader.setDelegate(aDelegate);
        Resource[] aResources = new Resource[2];
        aResources[0] = mock(Resource.class);
        aResources[1] = mock(Resource.class);
        
        when(aResources[0].getFilename()).thenReturn("file1");
        when(aResources[1].getFilename()).thenReturn("file2");
        
        when(aDelegate.read()).thenReturn(aResources[0]).thenReturn(null).thenReturn(aResources[1]).thenReturn(null);
        
        aReader.setResources(aResources);
        
        aReader.open(new ExecutionContext());
        assertEquals(aResources[0], aReader.read());
        assertEquals(aResources[1], aReader.read());
        assertNull(aReader.read());
        aReader.close();
        
        verify(aDelegate, times(2)).open(any(ExecutionContext.class));
        verify(aDelegate, times(2)).close();
        verify(aDelegate, times(4)).read(); // 2 values + 2 nulls
    }

}
