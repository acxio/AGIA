package fr.acxio.tools.agia.transform;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ExpressionsMapToMapProcessorTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void testExpressions() throws Exception {
        ExpressionsMapToMapProcessor aProcessor = new ExpressionsMapToMapProcessor();
        Map<String, String> aExpressions = new HashMap<String, String>(2);
        aExpressions.put("newkey1", "@{#in['key1'].substring(1,2)}_@{#in['key2']}");
        aExpressions.put("newkey2", "@{#in['key3'].getTime()}");
        aProcessor.setExpressions(aExpressions);
        Map<String, Object> aItem = new HashMap<String, Object>(3);
        aItem.put("key1", "value1");
        aItem.put("key2", 123);
        aItem.put("key3", new Date());
        Map<String, Object> aResult = aProcessor.process(aItem);
        assertNotNull(aResult);
        assertEquals(aItem.get("key1"), aResult.get("key1"));
        assertEquals(aItem.get("key2"), aResult.get("key2"));
        assertEquals(aItem.get("key3"), aResult.get("key3"));
        assertEquals("a_123", aResult.get("newkey1"));
        assertEquals(((Date)aItem.get("key3")).getTime(), aResult.get("newkey2"));
    }
    
    @Test
    public void testExpressionBadReference() throws Exception {
        ExpressionsMapToMapProcessor aProcessor = new ExpressionsMapToMapProcessor();
        Map<String, String> aExpressions = new HashMap<String, String>(2);
        aExpressions.put("newkey1", "@{#in['key4']}");
        aProcessor.setExpressions(aExpressions);
        Map<String, Object> aItem = new HashMap<String, Object>(3);
        aItem.put("key1", "value1");
        aItem.put("key2", 123);
        aItem.put("key3", new Date());
        Map<String, Object> aResult = aProcessor.process(aItem);
        assertNotNull(aResult);
        assertNull(aResult.get("newkey1"));
    }

    @Test
    public void testBadExpression() throws Exception {
        exception.expect(ExpressionsMapProcessorException.class);
        ExpressionsMapToMapProcessor aProcessor = new ExpressionsMapToMapProcessor();
        Map<String, String> aExpressions = new HashMap<String, String>(2);
        aExpressions.put("newkey1", "@{#in['key4'].isEmpty()}");
        aProcessor.setExpressions(aExpressions);
        Map<String, Object> aItem = new HashMap<String, Object>(3);
        aItem.put("key1", "value1");
        aItem.put("key2", 123);
        aItem.put("key3", new Date());
        Map<String, Object> aResult = aProcessor.process(aItem);
    }
    
    @Test
    public void testExpressionsEmpty() throws Exception {
        ExpressionsMapToMapProcessor aProcessor = new ExpressionsMapToMapProcessor();
        Map<String, String> aExpressions = new HashMap<String, String>(2);
        aProcessor.setExpressions(aExpressions);
        Map<String, Object> aItem = new HashMap<String, Object>(3);
        aItem.put("key1", "value1");
        aItem.put("key2", 123);
        aItem.put("key3", new Date());
        Map<String, Object> aResult = aProcessor.process(aItem);
        assertNotNull(aResult);
        assertEquals(aItem.get("key1"), aResult.get("key1"));
        assertEquals(aItem.get("key2"), aResult.get("key2"));
        assertEquals(aItem.get("key3"), aResult.get("key3"));
    }
    
    @Test
    public void testExpressionsNull() throws Exception {
        exception.expect(ExpressionsMapProcessorException.class);
        ExpressionsMapToMapProcessor aProcessor = new ExpressionsMapToMapProcessor();
        Map<String, Object> aItem = new HashMap<String, Object>(3);
        aItem.put("key1", "value1");
        aItem.put("key2", 123);
        aItem.put("key3", new Date());
        Map<String, Object> aResult = aProcessor.process(aItem);
    }
    
    @Test
    public void testItemNull() throws Exception {
        ExpressionsMapToMapProcessor aProcessor = new ExpressionsMapToMapProcessor();
        Map<String, Object> aResult = aProcessor.process(null);
        assertNull(aResult);
    }
}
