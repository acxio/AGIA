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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * <p>
 * Standard expression resolver.
 * </p>
 * <p>
 * This resolver uses the prefix "@{" and the suffix "}" by default.
 * </p>
 * <p>
 * It caches the expressions to prevent systematic recompilation of them.
 * </p>
 * <p>
 * If the result of the evaluation cannot be cast to the given class, an
 * exception is thrown.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class StandardDataExpressionResolver implements DataExpressionResolver {

    /** Default expression prefix: "@{" */
    public static final String DEFAULT_EXPRESSION_PREFIX = "@{";

    /** Default expression suffix: "}" */
    public static final String DEFAULT_EXPRESSION_SUFFIX = "}";

    private String expressionPrefix = DEFAULT_EXPRESSION_PREFIX;

    private String expressionSuffix = DEFAULT_EXPRESSION_SUFFIX;

    private ExpressionParser expressionParser = new SpelExpressionParser();

    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<String, Expression>();

    private final ParserContext expressionParserContext = new ParserContext() {
        public boolean isTemplate() {
            return true;
        }

        public String getExpressionPrefix() {
            return expressionPrefix;
        }

        public String getExpressionSuffix() {
            return expressionSuffix;
        }
    };

    /**
     * Set the prefix that an expression string starts with. The default is
     * "@{".
     * 
     * @see #DEFAULT_EXPRESSION_PREFIX
     */
    public void setExpressionPrefix(String expressionPrefix) {
        Assert.hasText(expressionPrefix, "Expression prefix must not be empty");
        this.expressionPrefix = expressionPrefix;
    }

    /**
     * Set the suffix that an expression string ends with. The default is "}".
     * 
     * @see #DEFAULT_EXPRESSION_SUFFIX
     */
    public void setExpressionSuffix(String expressionSuffix) {
        Assert.hasText(expressionSuffix, "Expression suffix must not be empty");
        this.expressionSuffix = expressionSuffix;
    }

    /**
     * <p>
     * Specify the EL parser to use for expression parsing.
     * </p>
     * <p>
     * Default is a
     * {@link org.springframework.expression.spel.standard.SpelExpressionParser
     * SpelExpressionParser}, compatible with standard Unified EL style
     * expression syntax.
     * </p>
     */
    public void setExpressionParser(ExpressionParser expressionParser) {
        Assert.notNull(expressionParser, "ExpressionParser must not be null");
        this.expressionParser = expressionParser;
    }

    @Override
    public <T> T evaluate(String sExpression, EvaluationContext sEvalContext, Class<T> sTargetType) {
        if (!StringUtils.hasLength(sExpression)) {
            if (sTargetType == null || ClassUtils.isAssignableValue(sTargetType, sExpression)) {
                return (T) sExpression;
            }
            throw new EvaluationException("Cannot convert value '" + sExpression + "' to type '" + sTargetType.getName() + "'");
        }
        try {
            Expression expr = this.expressionCache.get(sExpression);
            if (expr == null) {
                expr = this.expressionParser.parseExpression(sExpression, expressionParserContext);
                this.expressionCache.put(sExpression, expr);
            }
            return expr.getValue(sEvalContext, sTargetType);
        } catch (Exception ex) {
            throw new EvaluationException("Expression parsing failed", ex);
        }
    }

}
