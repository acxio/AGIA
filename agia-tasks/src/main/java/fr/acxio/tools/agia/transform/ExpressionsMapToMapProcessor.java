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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.batch.item.ItemProcessor;

import fr.acxio.tools.agia.expression.support.AbstractSingleVariableExpressionEvaluator;

public class ExpressionsMapToMapProcessor extends AbstractSingleVariableExpressionEvaluator implements ItemProcessor<Map<String, Object>, Map<String, Object>> {

    private Map<String, String> expressions;

    public synchronized void setExpressions(Map<String, String> sExpressions) {
        expressions = sExpressions;
    }

    @Override
    public synchronized Map<String, Object> process(Map<String, Object> sItem) throws Exception {
        Map<String, Object> aResult = null;
        if (sItem != null) {
            aResult = new HashMap<String, Object>(sItem);
            try {
                Object aResolvedValue;
                updateContext(getVariableName(), sItem, getEvaluationContext());
                for(Entry<String, String> aExpressionEntry : expressions.entrySet()) {
                    aResolvedValue = getExpressionResolver().evaluate(aExpressionEntry.getValue(), getEvaluationContext(), Object.class);
                    aResult.put(aExpressionEntry.getKey(), aResolvedValue);
                }
            } catch (Exception e) {
                throw new ExpressionsMapProcessorException(e);
            }
        }
        return aResult;
    }

    
}
