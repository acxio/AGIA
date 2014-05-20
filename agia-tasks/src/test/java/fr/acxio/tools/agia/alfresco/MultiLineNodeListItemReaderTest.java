package fr.acxio.tools.agia.alfresco;

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
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.util.StopWatch;

import fr.acxio.tools.agia.item.MultiLineNodeListItemReader;

@ContextConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		StepScopeTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class MultiLineNodeListItemReaderTest {

	@Autowired
	MultiLineNodeListItemReader multiLineNodeListItemReader;

	public StepExecution getStepExection() {
		StepExecution execution = MetaDataInstanceFactory.createStepExecution();
		return execution;
	}

	@Before
	public void setUp() {
		((ItemStream) multiLineNodeListItemReader).open(new ExecutionContext());
	}

	@After
	public void tearDown() {
		((ItemStream) multiLineNodeListItemReader).close();
	}

	@Test
	@DirtiesContext
	public void testReadMixedIndex() throws Exception {
		StopWatch aStopWatch = new StopWatch("testReadMixedIndex");
		aStopWatch.start("Read first value");
		
		List<FieldSet> aRecord = multiLineNodeListItemReader.read();
		assertNotNull(aRecord);
		assertEquals(2, aRecord.size());
		
		aStopWatch.stop();
		aStopWatch.start("Read 2nd value");

		aRecord = multiLineNodeListItemReader.read();
		assertNotNull(aRecord);
		assertEquals(1, aRecord.size());
		
		aStopWatch.stop();
		aStopWatch.start("Read 3rd value");
		
		aRecord = multiLineNodeListItemReader.read();
		assertNotNull(aRecord);
		assertEquals(2, aRecord.size());
		
		aStopWatch.stop();
		aStopWatch.start("Read 4th value");
		
		aRecord = multiLineNodeListItemReader.read();
		assertNotNull(aRecord);
		assertEquals(1, aRecord.size());
		
		aStopWatch.stop();
		aStopWatch.start("Read 5th value");
		
		aRecord = multiLineNodeListItemReader.read();
		assertNull(aRecord);
		
		aStopWatch.stop();
		System.out.println(aStopWatch.prettyPrint());
	}

}
