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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;

public class MultiLineNodeListItemReaderTest {
    
    private interface FieldSetItemReader extends ItemStreamReader<FieldSet> {
    }

    @Test
    public void testRead() throws Exception {
        MultiLineNodeListItemReader aReader = new MultiLineNodeListItemReader();
        
        ItemReader<FieldSet> aDelegate = mock(FieldSetItemReader.class);
        
        when(aDelegate.read()).thenReturn(
                new DefaultFieldSet(new String[]{"Type1", "123"}, new String[]{"Type", "Value"}),
                new DefaultFieldSet(new String[]{"Type2", "ABC", "2014-08-14"}, new String[]{"Type", "Value1", "Value2"}),
                new DefaultFieldSet(new String[]{"Type1", "345"}, new String[]{"Type", "Value"}),
                new DefaultFieldSet(new String[]{"Type2", "DEF", "2014-08-13"}, new String[]{"Type", "Value1", "Value2"}),
                new DefaultFieldSet(new String[]{"Type3", "789GHI", null}, new String[]{"Type", "Value1", "Value2"}),
                null
        );
        
        aReader.setDelegate(aDelegate);
        aReader.setNewRecordCondition("@{#next == null or #next['Type'].equals('Type1')}");
        
        aReader.open(new ExecutionContext());
        List<FieldSet> aRecord1 = aReader.read();
        List<FieldSet> aRecord2 = aReader.read();
        List<FieldSet> aRecord3 = aReader.read();
        aReader.close();
        
        assertNotNull(aRecord1);
        assertNotNull(aRecord2);
        assertNull(aRecord3);
        assertEquals(2, aRecord1.size());
        assertEquals(3, aRecord2.size());
        assertEquals("123", aRecord1.get(0).getValues()[1]);
        assertEquals("ABC", aRecord1.get(1).getValues()[1]);
        assertEquals("345", aRecord2.get(0).getValues()[1]);
        assertEquals("DEF", aRecord2.get(1).getValues()[1]);
        assertEquals("789GHI", aRecord2.get(2).getValues()[1]);
    }

}
