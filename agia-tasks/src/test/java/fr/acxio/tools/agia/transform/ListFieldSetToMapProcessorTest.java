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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;

public class ListFieldSetToMapProcessorTest {

    @Test
    public void test() throws Exception {
        ListFieldSetToMapProcessor aProcessor = new ListFieldSetToMapProcessor();
        
        List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(2);
        aFieldSetList.add(new DefaultFieldSet(new String[]{"Facture", "2222.22", "2014-08-01T12:00:00.000"}, new String[]{"TypeDoc", "Montant", "Date"}));
        aFieldSetList.add(new DefaultFieldSet(new String[]{"Avoir", "-333.00", ""}, new String[]{"TypeDoc", "Montant", "Date"}));
        
        Map<String, Object> aResult = aProcessor.process(aFieldSetList);
        
        assertNotNull(aResult);
        assertEquals("Facture", aResult.get("rec0_TypeDoc"));
        assertEquals("2222.22", aResult.get("rec0_Montant"));
        assertEquals("2014-08-01T12:00:00.000", aResult.get("rec0_Date"));
        assertEquals("Avoir", aResult.get("rec1_TypeDoc"));
        assertEquals("-333.00", aResult.get("rec1_Montant"));
        assertEquals("", aResult.get("rec1_Date"));
        assertFalse(aResult.containsKey("rec2_TypeDoc"));
    }
    
    @Test
    public void testEmpty() throws Exception {
        ListFieldSetToMapProcessor aProcessor = new ListFieldSetToMapProcessor();
        List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
        Map<String, Object> aResult = aProcessor.process(aFieldSetList);
        assertNull(aResult);
    }
    
    @Test
    public void testNull() throws Exception {
        ListFieldSetToMapProcessor aProcessor = new ListFieldSetToMapProcessor();
        Map<String, Object> aResult = aProcessor.process(null);
        assertNull(aResult);
    }
    
    @Test
    public void testEmptyFieldSet() throws Exception {
        ListFieldSetToMapProcessor aProcessor = new ListFieldSetToMapProcessor();
        List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
        aFieldSetList.add(new DefaultFieldSet(new String[]{}, new String[]{}));
        Map<String, Object> aResult = aProcessor.process(aFieldSetList);
        assertNotNull(aResult);
        assertFalse(aResult.containsKey("rec0_TypeDoc"));
    }
    
    @Test
    public void testNullFieldSet() throws Exception {
        ListFieldSetToMapProcessor aProcessor = new ListFieldSetToMapProcessor();
        List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
        aFieldSetList.add(null);
        Map<String, Object> aResult = aProcessor.process(aFieldSetList);
        assertNotNull(aResult);
        assertFalse(aResult.containsKey("rec0_TypeDoc"));
    }
    
    @Test
    public void testNoName() throws Exception {
        ListFieldSetToMapProcessor aProcessor = new ListFieldSetToMapProcessor();
        List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
        aFieldSetList.add(new DefaultFieldSet(new String[]{"Facture", "2222.22", "2014-08-01T12:00:00.000"}));
        aFieldSetList.add(new DefaultFieldSet(new String[]{"Avoir", "-333.00", ""}));
        Map<String, Object> aResult = aProcessor.process(aFieldSetList);
        assertNotNull(aResult);
        assertEquals("Facture", aResult.get("rec0_ucol0"));
        assertEquals("2222.22", aResult.get("rec0_ucol1"));
        assertEquals("2014-08-01T12:00:00.000", aResult.get("rec0_ucol2"));
        assertEquals("Avoir", aResult.get("rec1_ucol0"));
        assertEquals("-333.00", aResult.get("rec1_ucol1"));
        assertEquals("", aResult.get("rec1_ucol2"));
        assertFalse(aResult.containsKey("rec2_ucol0"));
    }

}
