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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.util.Assert;

/**
 * <p>Does a lookup query using a {@code JdbcCursorItemReader}. The result
 * is appended to the input map to produce the output.</p>
 * <p>The names of the result will be prefixed and used as the key in the map
 * with the format: {@code lkp%d_%s}.</p>
 * <p>Each row of the result will be inserted into the map: the index of the
 * row is used after the prefix. The names of the columns are used after the
 * prefix and the index of the row.</p>
 * <p>The prefix used for the row index and the column name can be
 * set to another value.</p>
 * 
 * @author pcollardez
 *
 */
public class MapJdbcLookupProcessor implements ItemProcessor<Map<String, Object>, Map<String, Object>> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MapJdbcLookupProcessor.class);
    
    private final JdbcCursorItemReader<Map<String, Object>> jdbcCursorItemReader = new JdbcCursorItemReader<Map<String, Object>>();
    private ExpressionsPreparedStatementSetter preparedStatementSetter = new ExpressionsPreparedStatementSetter();
    
    protected String lookupFieldFormat = "lkp%d_%s";

    public MapJdbcLookupProcessor() {
        jdbcCursorItemReader.setRowMapper(new ColumnMapRowMapper());
        jdbcCursorItemReader.setPreparedStatementSetter(preparedStatementSetter);
    }
    
    public void setLookupNamePrefix(String sLookupNamePrefix) {
        lookupFieldFormat = sLookupNamePrefix + "%d_%s";
    }
    
    public void setLookupFieldsExpressions(String[] sExpressions) {
        Assert.notNull(sExpressions, "Lookup expressions must be non-null");
        preparedStatementSetter.setLookupFieldsExpressions(sExpressions);
    }
    
    public void setSql(String sql) {
        jdbcCursorItemReader.setSql(sql);
    }
    
    public void setDataSource(DataSource dataSource) {
        jdbcCursorItemReader.setDataSource(dataSource);
    }
    
    public void setFetchSize(int fetchSize) {
        jdbcCursorItemReader.setFetchSize(fetchSize);
    }
    
    public void setMaxRows(int maxRows) {
        jdbcCursorItemReader.setMaxRows(maxRows);
    }
    
    public void setQueryTimeout(int queryTimeout) {
        jdbcCursorItemReader.setQueryTimeout(queryTimeout);
    }
    
    public void setIgnoreWarnings(boolean ignoreWarnings) {
        jdbcCursorItemReader.setIgnoreWarnings(ignoreWarnings);
    }
    
    public void setVerifyCursorPosition(boolean verifyCursorPosition) {
        jdbcCursorItemReader.setVerifyCursorPosition(verifyCursorPosition);
    }
    
    public void setDriverSupportsAbsolute(boolean driverSupportsAbsolute) {
        jdbcCursorItemReader.setDriverSupportsAbsolute(driverSupportsAbsolute);
    }
    
    public void setUseSharedExtendedConnection(boolean useSharedExtendedConnection) {
        jdbcCursorItemReader.setUseSharedExtendedConnection(useSharedExtendedConnection);
    }
    
    public void setCurrentItemCount(int count) {
        jdbcCursorItemReader.setCurrentItemCount(count);
    }

    public void setMaxItemCount(int count) {
        jdbcCursorItemReader.setMaxItemCount(count);
    }
    
    public void setName(String name) {
        jdbcCursorItemReader.setName(name);
    }
    
    public void setSaveState(boolean saveState) {
        jdbcCursorItemReader.setSaveState(saveState);
    }

    @Override
    public Map<String, Object> process(Map<String, Object> sItem) throws Exception {
        Map<String, Object> aResult = null;
        if ((sItem != null) && !sItem.isEmpty()) {
            aResult = new HashMap<String, Object>(sItem);
            
            preparedStatementSetter.updateContext(aResult);
            jdbcCursorItemReader.open(new ExecutionContext());
            Map<String, Object> aRecord = null;
            int aLkpIdx = 0;
            do {
                aRecord = jdbcCursorItemReader.read();
                if (aRecord != null) {
                    for(Entry<String, Object> aLookupCol : aRecord.entrySet()) {
                        aResult.put(String.format(lookupFieldFormat, aLkpIdx, aLookupCol.getKey()), aLookupCol.getValue());
                    }
                    aLkpIdx++;
                }
            } while (aRecord != null);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Lookup record(s) found : {}", aLkpIdx);
            }
            jdbcCursorItemReader.close();
            
        }
        return aResult;
    }

}
