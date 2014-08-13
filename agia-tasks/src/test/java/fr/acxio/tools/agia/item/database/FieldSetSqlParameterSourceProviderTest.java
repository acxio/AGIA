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

import org.junit.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class FieldSetSqlParameterSourceProviderTest {

    @Test
    public void test() {
        FieldSetSqlParameterSourceProvider aProvider = new FieldSetSqlParameterSourceProvider();
        FieldSet aFieldSet = new DefaultFieldSet(new String[]{"Facture", "2222.22", "2014-08-01T12:00:00.000"}, new String[]{"TypeDoc", "Montant", "Date"});
        SqlParameterSource aResult = aProvider.createSqlParameterSource(aFieldSet);
        assertNotNull(aResult);
        assertEquals("Facture", aResult.getValue("rec0_TypeDoc"));
        assertEquals("2222.22", aResult.getValue("rec0_Montant"));
        assertEquals("2014-08-01T12:00:00.000", aResult.getValue("rec0_Date"));
        assertFalse(aResult.hasValue("rec1_TypeDoc"));
    }
    
    @Test
    public void testEmpty() {
        FieldSetSqlParameterSourceProvider aProvider = new FieldSetSqlParameterSourceProvider();
        FieldSet aFieldSet = new DefaultFieldSet(new String[]{}, new String[]{});
        SqlParameterSource aResult = aProvider.createSqlParameterSource(aFieldSet);
        assertNotNull(aResult);
        assertFalse(aResult.hasValue("rec0_TypeDoc"));
    }

    @Test
    public void testNull() {
        FieldSetSqlParameterSourceProvider aProvider = new FieldSetSqlParameterSourceProvider();
        SqlParameterSource aResult = aProvider.createSqlParameterSource(null);
        assertNotNull(aResult);
        assertFalse(aResult.hasValue("rec0_TypeDoc"));
    }
    
    @Test
    public void testNoName() {
        FieldSetSqlParameterSourceProvider aProvider = new FieldSetSqlParameterSourceProvider();
        FieldSet aFieldSet = new DefaultFieldSet(new String[]{"Facture", "2222.22", "2014-08-01T12:00:00.000"});
        SqlParameterSource aResult = aProvider.createSqlParameterSource(aFieldSet);
        assertNotNull(aResult);
        assertEquals("Facture", aResult.getValue("rec0_ucol0"));
        assertEquals("2222.22", aResult.getValue("rec0_ucol1"));
        assertEquals("2014-08-01T12:00:00.000", aResult.getValue("rec0_ucol2"));
        assertFalse(aResult.hasValue("rec1_ucol0"));
    }
    
    @Test
    public void testNoNameNewPrefix() {
        FieldSetSqlParameterSourceProvider aProvider = new FieldSetSqlParameterSourceProvider();
        aProvider.setFieldsetNamePrefix("field");
        aProvider.setUnnamedColumnPrefix("column");
        FieldSet aFieldSet = new DefaultFieldSet(new String[]{"Facture", "2222.22", "2014-08-01T12:00:00.000"});
        SqlParameterSource aResult = aProvider.createSqlParameterSource(aFieldSet);
        assertNotNull(aResult);
        assertEquals("Facture", aResult.getValue("field0_column0"));
        assertEquals("2222.22", aResult.getValue("field0_column1"));
        assertEquals("2014-08-01T12:00:00.000", aResult.getValue("field0_column2"));
        assertFalse(aResult.hasValue("field1_column0"));
    }

    @Test
    public void testEmptyNames() {
        FieldSetSqlParameterSourceProvider aProvider = new FieldSetSqlParameterSourceProvider();
        FieldSet aFieldSet = new DefaultFieldSet(new String[]{"Facture", "2222.22", "2014-08-01T12:00:00.000"}, new String[]{"", "", ""});
        SqlParameterSource aResult = aProvider.createSqlParameterSource(aFieldSet);
        assertNotNull(aResult);
        assertEquals("Facture", aResult.getValue("rec0_ucol0"));
        assertEquals("2222.22", aResult.getValue("rec0_ucol1"));
        assertEquals("2014-08-01T12:00:00.000", aResult.getValue("rec0_ucol2"));
        assertFalse(aResult.hasValue("rec1_ucol0"));
    }

    @Test
    public void testNullNames() {
        FieldSetSqlParameterSourceProvider aProvider = new FieldSetSqlParameterSourceProvider();
        FieldSet aFieldSet = new DefaultFieldSet(new String[]{"Facture", "2222.22", "2014-08-01T12:00:00.000"}, new String[]{null, null, null});
        SqlParameterSource aResult = aProvider.createSqlParameterSource(aFieldSet);
        assertNotNull(aResult);
        assertEquals("Facture", aResult.getValue("rec0_ucol0"));
        assertEquals("2222.22", aResult.getValue("rec0_ucol1"));
        assertEquals("2014-08-01T12:00:00.000", aResult.getValue("rec0_ucol2"));
        assertFalse(aResult.hasValue("rec1_ucol0"));
    }
}
