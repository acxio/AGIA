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
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.springframework.core.io.Resource;

public class CompositeResourcesFactoryTest {

    @Test
    public void testSingleResourcesFactory() throws Exception {
        CompositeResourcesFactory aCompositeResourcesFactory = new CompositeResourcesFactory();
        
        ResourcesFactory aResourcesFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aResourcesFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        
        aCompositeResourcesFactory.setDelegates(Collections.singletonList(aResourcesFactory));
        Resource[] aResult = aCompositeResourcesFactory.getResources();
        
        assertNotNull(aResult);
        assertEquals(1, aResult.length);
        assertEquals(aFileResource1, aResult[0]);
    }

    @Test
    public void testManyResourcesFactory() throws Exception {
        CompositeResourcesFactory aCompositeResourcesFactory = new CompositeResourcesFactory();
        
        ResourcesFactory aResourcesFactory1 = mock(ResourcesFactory.class);
        Resource aFileResource11 = mock(Resource.class);
        when(aResourcesFactory1.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource11});
        
        ResourcesFactory aResourcesFactory2 = mock(ResourcesFactory.class);
        Resource aFileResource21 = mock(Resource.class);
        Resource aFileResource22 = mock(Resource.class);
        when(aResourcesFactory2.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource21, aFileResource22});

        ResourcesFactory aResourcesFactory3 = mock(ResourcesFactory.class);
        Resource aFileResource31 = mock(Resource.class);
        when(aResourcesFactory3.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource31});
        
        aCompositeResourcesFactory.setDelegates(Arrays.asList(new ResourcesFactory[]{aResourcesFactory1, aResourcesFactory2, aResourcesFactory3}));
        Resource[] aResult = aCompositeResourcesFactory.getResources();
        
        assertNotNull(aResult);
        assertEquals(4, aResult.length);
        assertEquals(aFileResource11, aResult[0]);
        assertEquals(aFileResource21, aResult[1]);
        assertEquals(aFileResource22, aResult[2]);
        assertEquals(aFileResource31, aResult[3]);
    }

    @Test
    public void testNoResourcesFactory() throws Exception {
        CompositeResourcesFactory aCompositeResourcesFactory = new CompositeResourcesFactory();
        Resource[] aResult = aCompositeResourcesFactory.getResources();
        assertNull(aResult);
    }
    
    @Test
    public void testEmptyResourcesFactoryList() throws Exception {
        CompositeResourcesFactory aCompositeResourcesFactory = new CompositeResourcesFactory();
        aCompositeResourcesFactory.setDelegates(Collections.EMPTY_LIST);
        Resource[] aResult = aCompositeResourcesFactory.getResources();
        assertNull(aResult);
    }
    
    @Test
    public void testEmptyResourcesFactory() throws Exception {
        CompositeResourcesFactory aCompositeResourcesFactory = new CompositeResourcesFactory();
        
        ResourcesFactory aResourcesFactory = mock(ResourcesFactory.class);
        
        aCompositeResourcesFactory.setDelegates(Collections.singletonList(aResourcesFactory));
        Resource[] aResult = aCompositeResourcesFactory.getResources();
        
        assertNotNull(aResult);
        assertEquals(0, aResult.length);
    }
}
