package fr.acxio.tools.agia.item.database;

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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class ListFieldSetSqlParameterSourceProviderTest {

    @Test
    public void test() {
        ListFieldSetSqlParameterSourceProvider aProvider = new ListFieldSetSqlParameterSourceProvider();
        List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(2);
        aFieldSetList.add(new DefaultFieldSet(new String[]{"Facture", "2222.22", "2014-08-01T12:00:00.000"}, new String[]{"TypeDoc", "Montant", "Date"}));
        aFieldSetList.add(new DefaultFieldSet(new String[]{"Avoir", "-333.00", ""}, new String[]{"TypeDoc", "Montant", "Date"}));
        SqlParameterSource aResult = aProvider.createSqlParameterSource(aFieldSetList);
        assertNotNull(aResult);
        assertEquals("Facture", aResult.getValue("rec0_TypeDoc"));
        assertEquals("2222.22", aResult.getValue("rec0_Montant"));
        assertEquals("2014-08-01T12:00:00.000", aResult.getValue("rec0_Date"));
        assertEquals("Avoir", aResult.getValue("rec1_TypeDoc"));
        assertEquals("-333.00", aResult.getValue("rec1_Montant"));
        assertEquals("", aResult.getValue("rec1_Date"));
        assertFalse(aResult.hasValue("rec2_TypeDoc"));
    }
    
    @Test
    public void testEmpty() {
        ListFieldSetSqlParameterSourceProvider aProvider = new ListFieldSetSqlParameterSourceProvider();
        List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
        SqlParameterSource aResult = aProvider.createSqlParameterSource(aFieldSetList);
        assertNotNull(aResult);
        assertFalse(aResult.hasValue("rec0_TypeDoc"));
    }
    
    @Test
    public void testNull() {
        ListFieldSetSqlParameterSourceProvider aProvider = new ListFieldSetSqlParameterSourceProvider();
        SqlParameterSource aResult = aProvider.createSqlParameterSource(null);
        assertNotNull(aResult);
        assertFalse(aResult.hasValue("rec0_TypeDoc"));
    }
    
    @Test
    public void testEmptyFieldSet() {
        ListFieldSetSqlParameterSourceProvider aProvider = new ListFieldSetSqlParameterSourceProvider();
        List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
        aFieldSetList.add(new DefaultFieldSet(new String[]{}, new String[]{}));
        SqlParameterSource aResult = aProvider.createSqlParameterSource(aFieldSetList);
        assertNotNull(aResult);
        assertFalse(aResult.hasValue("rec0_TypeDoc"));
    }
    
    @Test
    public void testNullFieldSet() {
        ListFieldSetSqlParameterSourceProvider aProvider = new ListFieldSetSqlParameterSourceProvider();
        List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
        aFieldSetList.add(null);
        SqlParameterSource aResult = aProvider.createSqlParameterSource(aFieldSetList);
        assertNotNull(aResult);
        assertFalse(aResult.hasValue("rec0_TypeDoc"));
    }
    
    @Test
    public void testNoName() {
        ListFieldSetSqlParameterSourceProvider aProvider = new ListFieldSetSqlParameterSourceProvider();
        List<FieldSet> aFieldSetList = new ArrayList<FieldSet>(1);
        aFieldSetList.add(new DefaultFieldSet(new String[]{"Facture", "2222.22", "2014-08-01T12:00:00.000"}));
        aFieldSetList.add(new DefaultFieldSet(new String[]{"Avoir", "-333.00", ""}));
        SqlParameterSource aResult = aProvider.createSqlParameterSource(aFieldSetList);
        assertNotNull(aResult);
        assertEquals("Facture", aResult.getValue("rec0_ucol0"));
        assertEquals("2222.22", aResult.getValue("rec0_ucol1"));
        assertEquals("2014-08-01T12:00:00.000", aResult.getValue("rec0_ucol2"));
        assertEquals("Avoir", aResult.getValue("rec1_ucol0"));
        assertEquals("-333.00", aResult.getValue("rec1_ucol1"));
        assertEquals("", aResult.getValue("rec1_ucol2"));
        assertFalse(aResult.hasValue("rec2_ucol0"));
    }

}
