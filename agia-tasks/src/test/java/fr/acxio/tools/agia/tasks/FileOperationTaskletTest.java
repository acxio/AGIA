package fr.acxio.tools.agia.tasks;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;

import fr.acxio.tools.agia.io.AbstractFileOperations.Operation;

@RunWith(JUnit4.class)
public class FileOperationTaskletTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@After
    public void tearDown() throws Exception {
        Collection<File> aFilesToDelete = FileUtils.listFiles(new File("target"), new WildcardFileFilter("input-copy*.csv"), null);
        for(File aFile : aFilesToDelete) {
            FileUtils.deleteQuietly(aFile);
        }
    }

	@Test
	public void testExecuteCopy() throws Exception {
		FileOperationTasklet aTasklet = new FileOperationTasklet();
		aTasklet.setOrigin(new FileSystemResource("src/test/resources/testFiles/input.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy.csv"));
		aTasklet.setOperation(Operation.COPY);
		aTasklet.afterPropertiesSet();
		StepContribution aStepContribution = mock(StepContribution.class);
		assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
		verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
	}
	
	@Test(expected=FileOperationException.class)
	public void testExecuteCopyFileNotFound() throws Exception {
		FileOperationTasklet aTasklet = new FileOperationTasklet();
		aTasklet.setOrigin(new FileSystemResource("src/test/resources/testFiles/notfound.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy.csv"));
		aTasklet.setOperation(Operation.COPY);
		aTasklet.afterPropertiesSet();
		StepContribution aStepContribution = mock(StepContribution.class);
		aTasklet.execute(aStepContribution, null);
		verify(aStepContribution, times(0)).incrementReadCount();
        verify(aStepContribution, times(0)).incrementWriteCount(1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testExecuteCopyDestinationFileNotFound() throws Exception {
		FileOperationTasklet aTasklet = new FileOperationTasklet();
		aTasklet.setOrigin(new FileSystemResource("src/test/resources/testFiles/notfound.csv"));
		aTasklet.setOperation(Operation.COPY);
		aTasklet.afterPropertiesSet();
	}	
}
