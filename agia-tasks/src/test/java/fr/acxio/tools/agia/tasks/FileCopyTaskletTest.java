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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileLock;
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

@RunWith(JUnit4.class)
public class FileCopyTaskletTest {
	
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
	public void testExecute() throws Exception {
		FileCopyTasklet aTasklet = new FileCopyTasklet();
		aTasklet.setOrigin(new FileSystemResource("src/test/resources/testFiles/input.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy.csv"));
		StepContribution aStepContribution = mock(StepContribution.class);
		assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
		verify(aStepContribution, times(1)).incrementReadCount();
		verify(aStepContribution, times(1)).incrementWriteCount(1);
	}
	
	@Test
	public void testExecuteFileNotFound() {
		FileCopyTasklet aTasklet = new FileCopyTasklet();
		aTasklet.setOrigin(new FileSystemResource("src/test/resources/testFiles/notfound.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy.csv"));
		try {
			aTasklet.execute(null, null);
			fail("Must return an exception if the input file is not found");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testExecuteCannotReplace() throws Exception {
		exception.expect(FileCopyException.class);
		FileCopyTasklet aTasklet = new FileCopyTasklet();
		aTasklet.setOrigin(new FileSystemResource("src/test/resources/testFiles/input.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy.csv"));
		aTasklet.execute(null, null);
		aTasklet.setForceReplace(false);
		aTasklet.execute(null, null);
	}

	@Test
	public void testExecuteDeleteOrigin() throws Exception {
		FileCopyTasklet aTasklet = new FileCopyTasklet();
		aTasklet.setOrigin(new FileSystemResource("src/test/resources/testFiles/input.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy2.csv"));
		aTasklet.execute(null, null);
		
		assertTrue(aTasklet.getDestination().getFile().exists());
		
		aTasklet.setDeleteOrigin(true);
		aTasklet.setOrigin(new FileSystemResource("target/input-copy2.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy3.csv"));
		aTasklet.execute(null, null);
		
		assertTrue(aTasklet.getDestination().getFile().exists());
		assertFalse(aTasklet.getOrigin().getFile().exists());
	}
	
	@Test
	public void testCannotDeleteOrigin() throws Exception {
		FileCopyTasklet aTasklet = new FileCopyTasklet();
		aTasklet.setOrigin(new FileSystemResource("src/test/resources/testFiles/input.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy4.csv"));
		aTasklet.execute(null, null);
		
		File aOrigin = aTasklet.getDestination().getFile();
		assertTrue(aOrigin.exists());
		FileInputStream aInputStream = new FileInputStream(aOrigin);
		FileLock aLock = aInputStream.getChannel().lock(0L, Long.MAX_VALUE, true); // shared lock
		
		aTasklet.setDeleteOrigin(true);
		aTasklet.setOrigin(new FileSystemResource("target/input-copy4.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy5.csv"));
		try {
			aTasklet.execute(null, null);
			fail("Must throw a FileCopyException");
		} catch (FileCopyException e) {
			// Fallthrough
		} finally {
			aLock.release();
			aInputStream.close();
		}		
	}
	
	@Test
	public void testExecuteEmptyOrigin() throws Exception {
		FileCopyTasklet aTasklet = new FileCopyTasklet();
		aTasklet.setOrigin(new FileSystemResource("src/test/resources/testFiles/input.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy6.csv"));
		aTasklet.execute(null, null);
		
		assertTrue(aTasklet.getDestination().getFile().exists());
		assertTrue(aTasklet.getDestination().getFile().length() > 0);
		
		aTasklet.setEmptyOrigin(true);
		aTasklet.setOrigin(new FileSystemResource("target/input-copy6.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy7.csv"));
		aTasklet.execute(null, null);
		
		assertTrue(aTasklet.getDestination().getFile().exists());
		assertTrue(aTasklet.getOrigin().getFile().exists());
		assertEquals(0, aTasklet.getOrigin().getFile().length());
	}
	
	@Test
	public void testCannotEmptyOrigin() throws Exception {
		FileCopyTasklet aTasklet = new FileCopyTasklet();
		aTasklet.setOrigin(new FileSystemResource("src/test/resources/testFiles/input.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy8.csv"));
		aTasklet.execute(null, null);
		
		File aOrigin = aTasklet.getDestination().getFile();
		assertTrue(aOrigin.exists());
		FileInputStream aInputStream = new FileInputStream(aOrigin);
		FileLock aLock = aInputStream.getChannel().lock(0L, Long.MAX_VALUE, true); // shared lock
		
		aTasklet.setEmptyOrigin(true);
		aTasklet.setOrigin(new FileSystemResource("target/input-copy8.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copy9.csv"));
		try {
			aTasklet.execute(null, null);
			fail("Must throw a FileCopyException");
		} catch (FileCopyException e) {
			// Fallthrough
		} finally {
			aLock.release();
			aInputStream.close();
		}		
	}
	
	@Test
	public void testExecuteDeleteAndEmptyOrigin() throws Exception {
		FileCopyTasklet aTasklet = new FileCopyTasklet();
		aTasklet.setOrigin(new FileSystemResource("src/test/resources/testFiles/input.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copyA.csv"));
		aTasklet.execute(null, null);
		
		assertTrue(aTasklet.getDestination().getFile().exists());
		assertTrue(aTasklet.getDestination().getFile().length() > 0);
		
		aTasklet.setDeleteOrigin(true);
		aTasklet.setEmptyOrigin(true); // Empty has higher priority
		aTasklet.setOrigin(new FileSystemResource("target/input-copyA.csv"));
		aTasklet.setDestination(new FileSystemResource("target/input-copyB.csv"));
		aTasklet.execute(null, null);
		
		assertTrue(aTasklet.getDestination().getFile().exists());
		assertTrue(aTasklet.getOrigin().getFile().exists());
		assertEquals(0, aTasklet.getOrigin().getFile().length());
	}
}
