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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.core.io.Resource;

import fr.acxio.tools.agia.expression.StandardEvaluationContextFactory;

public class ExpressionResourceFactoryTest {

    @Test
    public void testConstantExpression() throws Exception {
        ExpressionResourceFactory aFactory = new ExpressionResourceFactory();
        aFactory.setExpression("file.ext");
        Resource aResource = aFactory.getResource();
        assertNotNull(aResource);
        assertEquals("file.ext", aResource.getFilename());
    }

    @Test
    public void testExpression() throws Exception {
        ExpressionResourceFactory aFactory = new ExpressionResourceFactory();
        StandardEvaluationContextFactory aEvaluationContextFactory = new StandardEvaluationContextFactory();
        Map<String, Object> aVariables = new HashMap<String, Object>();
        aVariables.put("afile", "input.csv");
        aEvaluationContextFactory.setCommonObjects(aVariables);
        aFactory.setEvaluationContextFactory(aEvaluationContextFactory);
        aFactory.setExpression("file:src/test/resources/testFiles/@{#afile}");
        Resource aResource = aFactory.getResource();
        assertNotNull(aResource);
        assertEquals("input.csv", aResource.getFilename());
    }
    
    @Test
    public void testEmptyExpression() throws Exception {
        ExpressionResourceFactory aFactory = new ExpressionResourceFactory();
        aFactory.setExpression("");
        Resource aResource = aFactory.getResource();
        assertNotNull(aResource);
        assertEquals("", aResource.getFilename());
    }
    
    @Test
    public void testNullExpression() throws Exception {
        ExpressionResourceFactory aFactory = new ExpressionResourceFactory();
        aFactory.setExpression(null);
        Resource aResource = aFactory.getResource();
        assertNull(aResource);
    }
}
