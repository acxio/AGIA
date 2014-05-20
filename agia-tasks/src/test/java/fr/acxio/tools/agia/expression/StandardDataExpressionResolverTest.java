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
 
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import fr.acxio.tools.agia.convert.DateFormatConverter;

public class StandardDataExpressionResolverTest {

	@Test
	public void testStandardDataExpressionResolver() {
		StandardEvaluationContextFactory aFactory = new StandardEvaluationContextFactory();
		Map<String, Object> aCommons = new HashMap<String, Object>();
		aCommons.put("o1", "data");
		
		DateFormatConverter aDateFormatConverter = new DateFormatConverter();
		aDateFormatConverter.setSourcePattern("dd/MM/yyyy");
		aDateFormatConverter.setDestinationPattern("yyyy-MM-dd");
		aCommons.put("df", aDateFormatConverter);
		
		aFactory.setCommonObjects(aCommons);
		
		Map<String, Object> aValues = new HashMap<String, Object>();
		aValues.put("v1", "d1");
		aValues.put("v2", "d2");
		aValues.put("v3", "01/02/2013");
		
		StandardEvaluationContext aStandardEvaluationContext = aFactory.createContext("in", aValues, null);
		
		StandardDataExpressionResolver aStandardDataExpressionResolver = new StandardDataExpressionResolver();
		String aResult = aStandardDataExpressionResolver.evaluate("any", aStandardEvaluationContext, String.class);
		assertEquals("any", aResult);
		
		aResult = aStandardDataExpressionResolver.evaluate("@{#in.v1}", aStandardEvaluationContext, String.class);
		assertEquals("d1", aResult);
		
		aResult = aStandardDataExpressionResolver.evaluate("@{#o1}", aStandardEvaluationContext, String.class);
		assertEquals("data", aResult);
		
		aResult = aStandardDataExpressionResolver.evaluate("@{#df.convert(#in.v3)}", aStandardEvaluationContext, String.class);
		assertEquals("2013-02-01", aResult);
	}

}
