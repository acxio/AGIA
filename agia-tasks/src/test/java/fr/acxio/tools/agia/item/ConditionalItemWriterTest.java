package fr.acxio.tools.agia.item;

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

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.batch.item.ItemWriter;

@RunWith(JUnit4.class)
public class ConditionalItemWriterTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    private interface MapItemWriter extends ItemWriter<Map<String, Object>> {
    }

    @Test
    public void testWrite() throws Exception {
        ConditionalItemWriter<Map<String, Object>> aWriter = new ConditionalItemWriter<Map<String, Object>>();
        MapItemWriter aDelegate = mock(MapItemWriter.class);
        
        aWriter.setDelegate(aDelegate);
        aWriter.setCondition("@{#in.key1.equals('value1')}");
        
        Map<String, Object> aItem1 = new HashMap<String, Object>(2);
        aItem1.put("key1", "value1");
        aItem1.put("key2", "value2");
        List<Map<String, Object>> aItems = new ArrayList<Map<String, Object>>(1);
        aItems.add(aItem1);
        
        aWriter.write(aItems);
        
        verify(aDelegate, times(1)).write(eq(aItems));
    }
    
    @Test
    public void testWrite1of2() throws Exception {
        ConditionalItemWriter<Map<String, Object>> aWriter = new ConditionalItemWriter<Map<String, Object>>();
        MapItemWriter aDelegate = mock(MapItemWriter.class);
        
        aWriter.setDelegate(aDelegate);
        aWriter.setCondition("@{#in.key1.equals('value1')}");
        
        Map<String, Object> aItem1 = new HashMap<String, Object>(2);
        aItem1.put("key1", "value1");
        aItem1.put("key2", "value2");
        Map<String, Object> aItem2 = new HashMap<String, Object>(2);
        aItem2.put("key1", "value21");
        aItem2.put("key2", "value22");
        List<Map<String, Object>> aItems = new ArrayList<Map<String, Object>>(2);
        aItems.add(aItem1);
        aItems.add(aItem2);
        
        aWriter.write(aItems);
        
        List<Map<String, Object>> aFilteredItems = new ArrayList<Map<String, Object>>(1);
        aFilteredItems.add(aItem1);
        
        verify(aDelegate, times(1)).write(eq(aFilteredItems));
    }
    
    @Test
    public void testWriteNone() throws Exception {
        ConditionalItemWriter<Map<String, Object>> aWriter = new ConditionalItemWriter<Map<String, Object>>();
        MapItemWriter aDelegate = mock(MapItemWriter.class);
        
        aWriter.setDelegate(aDelegate);
        aWriter.setCondition("@{#in.key1.equals('value1')}");
        
        Map<String, Object> aItem1 = new HashMap<String, Object>(2);
        aItem1.put("key1", "value11");
        aItem1.put("key2", "value12");
        Map<String, Object> aItem2 = new HashMap<String, Object>(2);
        aItem2.put("key1", "value21");
        aItem2.put("key2", "value22");
        List<Map<String, Object>> aItems = new ArrayList<Map<String, Object>>(2);
        aItems.add(aItem1);
        aItems.add(aItem2);
        
        aWriter.write(aItems);
        
        verify(aDelegate, times(0)).write(anyList());
    }
    
    @Test
    public void testNullExpression() throws Exception {
        exception.expect(IllegalArgumentException.class);
        ConditionalItemWriter<Map<String, Object>> aWriter = new ConditionalItemWriter<Map<String, Object>>();
        MapItemWriter aDelegate = mock(MapItemWriter.class);
        
        aWriter.setDelegate(aDelegate);
        aWriter.setCondition(null);
        
        aWriter.afterPropertiesSet();
        
    }
    
    @Test
    public void testEmptyExpression() throws Exception {
        exception.expect(IllegalArgumentException.class);
        ConditionalItemWriter<Map<String, Object>> aWriter = new ConditionalItemWriter<Map<String, Object>>();
        MapItemWriter aDelegate = mock(MapItemWriter.class);
        
        aWriter.setDelegate(aDelegate);
        aWriter.setCondition("");
        
        aWriter.afterPropertiesSet();
    }
    
    @Test
    public void testNotBooleanExpression() throws Exception {
        exception.expect(ConditionalItemWriterException.class);
        ConditionalItemWriter<Map<String, Object>> aWriter = new ConditionalItemWriter<Map<String, Object>>();
        MapItemWriter aDelegate = mock(MapItemWriter.class);
        
        aWriter.setDelegate(aDelegate);
        aWriter.setCondition("'somestring'");
        
        Map<String, Object> aItem1 = new HashMap<String, Object>(2);
        aItem1.put("key1", "value1");
        aItem1.put("key2", "value2");
        List<Map<String, Object>> aItems = new ArrayList<Map<String, Object>>(1);
        aItems.add(aItem1);
        
        aWriter.write(aItems);
    }

    @Test
    public void testEmptyWrite() throws Exception {
        ConditionalItemWriter<Map<String, Object>> aWriter = new ConditionalItemWriter<Map<String, Object>>();
        MapItemWriter aDelegate = mock(MapItemWriter.class);
        
        aWriter.setDelegate(aDelegate);
        aWriter.setCondition("@{#in.key1.equals('value1')}");
        
        List<Map<String, Object>> aItems = new ArrayList<Map<String, Object>>(1);
        
        aWriter.write(aItems);
        
        verify(aDelegate, times(0)).write(anyList());
    }
    
    @Test
    public void testNullWrite() throws Exception {
        exception.expect(NullPointerException.class);
        ConditionalItemWriter<Map<String, Object>> aWriter = new ConditionalItemWriter<Map<String, Object>>();
        MapItemWriter aDelegate = mock(MapItemWriter.class);
        
        aWriter.setDelegate(aDelegate);
        aWriter.setCondition("@{#in.key1.equals('value1')}");
        
        aWriter.write(null);
    }
}
