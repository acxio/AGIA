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

public class StandardEvaluationContextFactoryTest {

	@Test
	public void testStandardEvaluationContextFactory() {
		StandardEvaluationContextFactory aFactory = new StandardEvaluationContextFactory();
		Map<String, Object> aCommons = new HashMap<String, Object>();
		aCommons.put("o1", "data");
		aFactory.setCommonObjects(aCommons);
		
		Map<String, Object> aValues = new HashMap<String, Object>();
		aValues.put("v1", "d1");
		aValues.put("v2", "d2");
		
		StandardEvaluationContext aStandardEvaluationContext = aFactory.createContext("in", aValues, null);
		
		assertNotNull(aStandardEvaluationContext);
		assertEquals(aValues, aStandardEvaluationContext.lookupVariable("in"));
		assertEquals("data", aStandardEvaluationContext.lookupVariable("o1"));
	}

}
