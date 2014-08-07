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

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.expression.DataExpressionResolver;
import fr.acxio.tools.agia.expression.EvaluationContextFactory;
import fr.acxio.tools.agia.expression.StandardDataExpressionResolver;
import fr.acxio.tools.agia.expression.StandardEvaluationContextFactory;

/**
 * <p>
 * A ResourcesFactory using a location pattern (for example, an Ant-style path
 * pattern) built from an expression.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class ExpressionFileSystemResourcesFactory implements ResourcesFactory {

    private ResourcePatternResolver resourcePatternResolver;
    private String pattern;

    private String variableName = "in";

    private EvaluationContextFactory evaluationContextFactory;
    private StandardEvaluationContext evaluationContext;

    private DataExpressionResolver expressionResolver = new StandardDataExpressionResolver();

    public ExpressionFileSystemResourcesFactory() {
        this(new PathMatchingResourcePatternResolver(), null);
    }

    public ExpressionFileSystemResourcesFactory(String sPattern) {
        this(new PathMatchingResourcePatternResolver(), sPattern);
    }

    public ExpressionFileSystemResourcesFactory(ResourcePatternResolver sResourcePatternResolver, String sPattern) {
        resourcePatternResolver = sResourcePatternResolver;
        pattern = sPattern;
    }

    public synchronized void setResourcePatternResolver(ResourcePatternResolver sResourcePatternResolver) {
        resourcePatternResolver = sResourcePatternResolver;
    }

    public synchronized void setPattern(String sPattern) {
        pattern = sPattern;
    }

    public synchronized void setVariableName(String sVariableName) {
        Assert.hasText(sVariableName, "variableName must not be empty");
        variableName = sVariableName;
    }

    public synchronized EvaluationContextFactory getEvaluationContextFactory() {
        if (evaluationContextFactory == null) {
            evaluationContextFactory = new StandardEvaluationContextFactory();
        }
        return evaluationContextFactory;
    }

    public synchronized void setEvaluationContextFactory(EvaluationContextFactory sEvaluationContextFactory) {
        evaluationContextFactory = sEvaluationContextFactory;
    }

    public synchronized Resource[] getResources() throws ResourceCreationException {
        return getResources(Collections.EMPTY_MAP);
    }

    public synchronized Resource[] getResources(Map<? extends Object, ? extends Object> sParameters) throws ResourceCreationException {
        Resource[] aResources = null;
        try {
            evaluationContext = getEvaluationContextFactory().createContext(variableName, sParameters, evaluationContext);
            String aResolvedPattern = expressionResolver.evaluate(pattern, evaluationContext, String.class);
            aResources = resourcePatternResolver.getResources(aResolvedPattern);
        } catch (Exception e) {
            throw new ResourceCreationException(e);
        }
        return aResources;
    }

}
