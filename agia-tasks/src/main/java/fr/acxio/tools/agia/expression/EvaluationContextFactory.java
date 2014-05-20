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
 
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * <p>Factory of evaluation context for expression evaluation.</p>
 * 
 * @author pcollardez
 *
 */
public interface EvaluationContextFactory {

	/**
	 * <p>Creates or update a context with the given variable name and value.</p>
	 * 
	 * @param sName the name of the variable to create or update
	 * @param sValue the value to set into the variable
	 * @param sContext if not null, the context to update
	 * @return a new context if {@code sContext} is null, or {@code sContext}
	 *         otherwise
	 */
	StandardEvaluationContext createContext(String sName, Object sValue, StandardEvaluationContext sContext);

}
