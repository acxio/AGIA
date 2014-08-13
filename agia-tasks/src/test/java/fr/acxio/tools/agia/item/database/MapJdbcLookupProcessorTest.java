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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;

public class MapJdbcLookupProcessorTest {

    @Test
    public void test() throws Exception {
        MapJdbcLookupProcessor aProcessor = new MapJdbcLookupProcessor();
        DataSource aDataSource = mock(DataSource.class);
        Connection aConnection = mock(Connection.class);
        when(aDataSource.getConnection()).thenReturn(aConnection);
        PreparedStatement aPreparedStatement = mock(PreparedStatement.class);
        when(aConnection.prepareStatement(any(String.class), anyInt(), anyInt())).thenReturn(aPreparedStatement);
        ResultSet aResultSet = mock(ResultSet.class);
        when(aPreparedStatement.executeQuery()).thenReturn(aResultSet);
        
        when(aResultSet.isClosed()).thenReturn(false);
        when(aResultSet.isBeforeFirst()).thenReturn(true).thenReturn(false);
        when(aResultSet.isFirst()).thenReturn(false).thenReturn(true);
        when(aResultSet.next()).thenReturn(true).thenReturn(false);
        when(aResultSet.findColumn(eq("id"))).thenReturn(1);
        when(aResultSet.getRow()).thenReturn(1);
        when(aResultSet.getObject(eq(1))).thenReturn("123");
        when(aResultSet.getObject(eq("id"))).thenReturn("123");
        
        ResultSetMetaData aMetadata = mock(ResultSetMetaData.class);
        when(aResultSet.getMetaData()).thenReturn(aMetadata);
        when(aMetadata.getColumnCount()).thenReturn(1);
        when(aMetadata.getColumnName(eq(1))).thenReturn("id");
        
        aProcessor.setDataSource(aDataSource);
        aProcessor.setSql("select id from table where field1 = ? and field2 = ?");
        aProcessor.setLookupFieldsExpressions(new String[]{"@{#in['TypeDoc']}", "@{#in['Date']}"});
        
        Map<String, Object> aRecord = new HashMap<String, Object>();
        aRecord.put("TypeDoc", "Facture");
        aRecord.put("Date", "2014-08-01T12:00:00.000");
        
        Map<String, Object> aResult = aProcessor.process(aRecord);
        
        assertNotNull(aResult);
        assertEquals("123", aResult.get("lkp0_id"));

        verify(aPreparedStatement, times(1)).setString(1, "Facture");
        verify(aPreparedStatement, times(1)).setString(2, "2014-08-01T12:00:00.000");
    }

}
