package fr.acxio.tools.agia.io;

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

import java.util.Collections;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import fr.acxio.tools.agia.expression.support.AbstractSingleVariableExpressionEvaluator;

/**
 * <p>
 * A ResourceFactory using an expression to build a filename.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class ExpressionResourceFactory extends AbstractSingleVariableExpressionEvaluator implements ResourceFactory {

    private String expression;

    public synchronized void setExpression(String sExpression) {
        expression = sExpression;
    }

    @Override
    public Resource getResource() throws ResourceCreationException {
        return getResource(Collections.EMPTY_MAP);
    }

    @Override
    public synchronized Resource getResource(Map<? extends Object, ? extends Object> sParameters) throws ResourceCreationException {
        Resource aResult = null;
        if (expression != null) {
            try {
                updateContext(getVariableName(), sParameters, getEvaluationContext());
                String aPath = getExpressionResolver().evaluate(expression, getEvaluationContext(), String.class);
                aResult = new FileSystemResource(aPath);
            } catch (Exception e) {
                throw new ResourceCreationException(e);
            }
        }
        return aResult;
    }

}
