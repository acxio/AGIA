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

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * <p>Standard implementation of the context factory.</p>
 * <p>This factory allows to add common variables to the context. These
 * variables are not set at the data level, which allows to inject for example
 * converters into the context.</p>
 * 
 * @author pcollardez
 *
 */
public class StandardEvaluationContextFactory implements
		EvaluationContextFactory {

	private Map<String, Object> commonObjects;
	
	public void setCommonObjects(Map<String, Object> sCommonObjects) {
		commonObjects = sCommonObjects;
	}

	@Override
	public StandardEvaluationContext createContext(String sName, Object sValue,
			StandardEvaluationContext sContext) {
		StandardEvaluationContext aContext = sContext;
		if (sContext == null) {
			aContext = new StandardEvaluationContext();
			if (commonObjects != null) {
				aContext.setVariables(commonObjects);
			}
			aContext.addPropertyAccessor(new MapAccessor());
		}
		aContext.setVariable(sName, sValue);
		return aContext;
	}

}
