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

import org.springframework.expression.spel.support.StandardEvaluationContext;

import fr.acxio.tools.agia.expression.DataExpressionResolver;
import fr.acxio.tools.agia.expression.EvaluationContextFactory;
import fr.acxio.tools.agia.expression.StandardDataExpressionResolver;
import fr.acxio.tools.agia.expression.StandardEvaluationContextFactory;

public abstract class AbstractExpressionEvaluator {

    /**
     * The evaluation context factory which may contain extra beans.
     */
    private EvaluationContextFactory evaluationContextFactory;
    
    /**
     * The instantiated evaluation context.
     */
    private StandardEvaluationContext evaluationContext;
    private DataExpressionResolver expressionResolver = new StandardDataExpressionResolver();
    
    /**
     * Returns the evaluation context factory.
     * 
     * @return the evaluation context factory.
     */
    public synchronized EvaluationContextFactory getEvaluationContextFactory() {
        if (evaluationContextFactory == null) {
            evaluationContextFactory = new StandardEvaluationContextFactory();
        }
        return evaluationContextFactory;
    }

    /**
     * Sets the evaluation context factory.
     * 
     * @param sEvaluationContextFactory
     *            an evaluation context factory.
     */
    public synchronized void setEvaluationContextFactory(EvaluationContextFactory sEvaluationContextFactory) {
        evaluationContextFactory = sEvaluationContextFactory;
    }

    protected synchronized StandardEvaluationContext getEvaluationContext() {
        return evaluationContext;
    }

    protected synchronized void setEvaluationContext(StandardEvaluationContext sEvaluationContext) {
        evaluationContext = sEvaluationContext;
    }

    public synchronized DataExpressionResolver getExpressionResolver() {
        return expressionResolver;
    }

    public synchronized void setExpressionResolver(DataExpressionResolver sExpressionResolver) {
        expressionResolver = sExpressionResolver;
    }
    
    protected synchronized StandardEvaluationContext updateContext(String sName, Object sValue, StandardEvaluationContext sContext) {
        evaluationContext = getEvaluationContextFactory().createContext(sName, sValue, sContext);
        return evaluationContext;
    }
}
