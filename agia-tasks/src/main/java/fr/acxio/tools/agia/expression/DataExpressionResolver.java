package fr.acxio.tools.agia.expression;

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

import org.springframework.expression.EvaluationContext;

/**
 * <p>
 * Expression resolver aimed at data handling, ie without the bean context.
 * </p>
 * 
 * @author pcollardez
 * @see org.springframework.context.expression.StandardBeanExpressionResolver
 */
public interface DataExpressionResolver {

    /**
     * <p>
     * Evaluates the given expression with the given context and return the
     * result cast to the given class.
     * </p>
     * 
     * @param sExpression
     *            the expression to evaluate
     * @param sEvalContext
     *            the context to use for evaluation
     * @param sReturnedClass
     *            the class of the result
     * @return the evaluated expression cast to {@code sReturnedClass}
     */
    <T> T evaluate(String sExpression, EvaluationContext sEvalContext, Class<T> sReturnedClass);
}
