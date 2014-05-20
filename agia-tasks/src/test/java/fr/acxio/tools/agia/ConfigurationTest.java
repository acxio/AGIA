package fr.acxio.tools.agia;

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
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.acxio.tools.agia.alfresco.configuration.DocumentDefinition;
import fr.acxio.tools.agia.alfresco.configuration.NodeFactory;
import fr.acxio.tools.agia.alfresco.domain.NodeList;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ConfigurationTest {
		
	@Autowired
	private NodeFactory defaultNodeFactory;
	
	@Autowired
	private NodeFactory tplNodeFactory;
	
	@Test
	public void testNodeFactory() throws Exception {
		assertNotNull(defaultNodeFactory);
		assertNotNull(defaultNodeFactory.getNodeDefinition());
		
		assertNotNull(tplNodeFactory);
		assertNotNull(tplNodeFactory.getNodeDefinition());
		DocumentDefinition aDocumentDefinition = (DocumentDefinition) tplNodeFactory.getNodeDefinition();
		assertNotNull(aDocumentDefinition.getNodeType());
		
		assertNotNull(defaultNodeFactory.getNamespaceContext());
		assertEquals("http://www.alfresco.org/model/application/1.0", defaultNodeFactory.getNamespaceContext().getNamespaceURI("app"));
		assertEquals("http://custom/model/objects/1.0", defaultNodeFactory.getNamespaceContext().getNamespaceURI("custom"));
		assertEquals(0, defaultNodeFactory.getNamespaceContext().getNamespaceURI("unknown").length());
	}
	
	@Test
	public void testSimpleNodeFactory() throws Exception {
		StandardEvaluationContext aContext = new StandardEvaluationContext();
		FieldSet aData = new DefaultFieldSet(new String[] {"V1", "V2", "V3"}, new String[] {"C1", "C2", "C3"});
		aContext.setVariable("in", aData.getProperties());
		NodeList aNodeList = defaultNodeFactory.getNodes(aContext);
		assertNotNull(aNodeList);
		assertEquals(5, aNodeList.size());
	}
}
