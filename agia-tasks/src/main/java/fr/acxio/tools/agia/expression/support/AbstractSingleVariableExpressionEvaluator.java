package fr.acxio.tools.agia.expression.support;

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

public abstract class AbstractSingleVariableExpressionEvaluator extends AbstractExpressionEvaluator {

    /**
     * The variable name of the root of the object graph. Defaulted to "in".
     */
    private String variableName = "in";

    /**
     * Returns the name of the variable representing the root object.
     * 
     * @return the name of the variable representing the root object.
     */
    protected synchronized String getVariableName() {
        return variableName;
    }

    /**
     * Sets the name of the variable representing the root object.
     * 
     * @param sVariableName
     *            a name for the variable representing the root object.
     */
    public synchronized void setVariableName(String sVariableName) {
        variableName = sVariableName;
    }

}
