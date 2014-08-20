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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.expression.support.AbstractSingleVariableExpressionEvaluator;

/**
 * <p>Sets values on a {@code PreparedStatement} provided by the
 * JdbcTemplate class, for each of a number of updates in a batch using the
 * same SQL. The parameters are set from expressions evaluated in a context.</p>
 * <p>The SQL statement should contain placeholders ({@code ?}). The list
 * of expressions matches the order of the placeholders.</p>
 * <p>IMPORTANT: The context is updated in another method than the one updating
 * the PreparedStatement. If the same instance of this class is used by more
 * than one JdbcTemplate class, the result can be corrupted.</p>
 * 
 * @author pcollardez
 *
 */
public class ExpressionsPreparedStatementSetter extends AbstractSingleVariableExpressionEvaluator implements PreparedStatementSetter {
    
    private String[] lookupFieldsExpressions;

    public synchronized void setLookupFieldsExpressions(String[] sExpressions) {
        Assert.notNull(sExpressions, "Lookup expressions must be non-null");
        lookupFieldsExpressions = Arrays.asList(sExpressions).toArray(new String[sExpressions.length]);
    }
    
    public synchronized void updateContext(Map<? extends Object, ? extends Object> sParameters) {
        updateContext(getVariableName(), sParameters, getEvaluationContext());
    }

    @Override
    public synchronized void setValues(PreparedStatement sPs) throws SQLException {
        try {
            String aResolvedValue;
            for(int i = 0; i < lookupFieldsExpressions.length; i++) {
                aResolvedValue = getExpressionResolver().evaluate(lookupFieldsExpressions[i], getEvaluationContext(), String.class);
                StatementCreatorUtils.setParameterValue(sPs, i + 1, SqlTypeValue.TYPE_UNKNOWN, aResolvedValue);
            }
        } catch (Exception e) {
            throw new SQLException(new PreparedStatementCreationException(e));
        }
    }


}
