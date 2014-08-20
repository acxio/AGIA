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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;

import fr.acxio.tools.agia.io.ResourceFactory;
import fr.acxio.tools.agia.io.ResourcesFactory;
import fr.acxio.tools.agia.io.AbstractFileOperations.Operation;

@RunWith(JUnit4.class)
public class FilesOperationTaskletTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @After
    public void tearDown() throws Exception {
        Collection<File> aFilesToDelete = FileUtils.listFiles(new File("target"), new WildcardFileFilter("*-input*.csv"), null);
        for(File aFile : aFilesToDelete) {
            FileUtils.deleteQuietly(aFile);
        }
        FileUtils.deleteDirectory(new File("target/CP-testfiles"));
    }
    
    @Test
    public void testExecuteCopy() throws Exception {
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-input.csv"));
        when(aDestResource.exists()).thenReturn(false);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertFalse(aDestResource.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        assertTrue(aDestResource.getFile().exists());
    }
    
    @Test
    public void testExecuteCopyTwoFiles() throws Exception {
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        Resource aFileResource2 = mock(Resource.class);
        when(aFileResource2.getFile()).thenReturn(new File("src/test/resources/testFiles/input1000.csv"));
        when(aFileResource2.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1, aFileResource2});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource1 = mock(Resource.class);
        when(aDestResource1.getFile()).thenReturn(new File("target/CP-input.csv"));
        when(aDestResource1.exists()).thenReturn(false);
        Resource aRelativeResource1 = mock(Resource.class);
        when(aRelativeResource1.getFile()).thenReturn(new File("target"));
        when(aDestResource1.createRelative("/.")).thenReturn(aRelativeResource1);
        Resource aDestResource2 = mock(Resource.class);
        when(aDestResource2.getFile()).thenReturn(new File("target/CP-input1000.csv"));
        when(aDestResource2.exists()).thenReturn(false);
        Resource aRelativeResource2 = mock(Resource.class);
        when(aRelativeResource2.getFile()).thenReturn(new File("target"));
        when(aDestResource2.createRelative("/.")).thenReturn(aRelativeResource2);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource1, aDestResource2);
        assertFalse(aDestResource1.getFile().exists());
        assertFalse(aDestResource2.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(2)).incrementReadCount();
        verify(aStepContribution, times(2)).incrementWriteCount(1);
        assertTrue(aDestResource1.getFile().exists());
        assertTrue(aDestResource2.getFile().exists());
    }

    @Test
    public void testExecuteMove() throws Exception {
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/CP-input.csv"));
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("target/CP-input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/MV-input.csv"));
        when(aDestResource.exists()).thenReturn(false);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertFalse(aDestResource.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.MOVE);
        aTasklet.afterPropertiesSet();
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        assertTrue(aDestResource.getFile().exists());
        assertFalse(aFileResource1.getFile().exists());
    }
    
    @Test
    public void testExecuteMoveToDir() throws Exception {
        FileUtils.forceMkdir(new File("target/CP-testfiles"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/CP-input.csv"));
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("target/CP-input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-testfiles/"));
        when(aDestResource.exists()).thenReturn(true);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target/CP-testfiles/"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertEquals(0, aDestResource.getFile().list().length);
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.MOVE);
        aTasklet.afterPropertiesSet();
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        assertTrue(aDestResource.getFile().exists());
        assertFalse(aFileResource1.getFile().exists());
        assertEquals("CP-input.csv", aDestResource.getFile().list()[0]);
    }
    
    @Test
    public void testExecuteMoveDirToDir() throws Exception {
        FileUtils.forceMkdir(new File("target/CP-testfiles/source"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/CP-testfiles/source/CP-input.csv"));
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("target/CP-testfiles/source/"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-testfiles/destination/"));
        when(aDestResource.exists()).thenReturn(false);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target/CP-testfiles/destination/"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertFalse(aDestResource.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.MOVE);
        aTasklet.afterPropertiesSet();
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        assertTrue(aDestResource.getFile().exists());
        assertFalse(aFileResource1.getFile().exists());
        assertEquals("source", aDestResource.getFile().list()[0]);
        assertEquals("CP-input.csv", aDestResource.getFile().listFiles()[0].list()[0]);
    }
    
    @Test
    public void testExecuteRemove() throws Exception {
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/CP-input.csv"));
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("target/CP-input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setOperation(Operation.REMOVE);
        aTasklet.afterPropertiesSet();
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        assertFalse(aFileResource1.getFile().exists());
    }
    
    @Test
    public void testExecuteCopyNoStepContribution() throws Exception {
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-input.csv"));
        when(aDestResource.exists()).thenReturn(false);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertFalse(aDestResource.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(null, null));
        verify(aStepContribution, times(0)).incrementReadCount();
        verify(aStepContribution, times(0)).incrementWriteCount(1);
        assertTrue(aDestResource.getFile().exists());
    }
    
    @Test
    public void testExecuteCopyWithEmptyChunkContext() throws Exception {
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-input.csv"));
        when(aDestResource.exists()).thenReturn(false);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertFalse(aDestResource.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        ChunkContext aChunkContext = mock(ChunkContext.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(null, aChunkContext));
        verify(aChunkContext, times(1)).getStepContext();
        assertTrue(aDestResource.getFile().exists());
    }

    @Test
    public void testExecuteCopyWithChunkContext() throws Exception {
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-input.csv"));
        when(aDestResource.exists()).thenReturn(false);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertFalse(aDestResource.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        ChunkContext aChunkContext = mock(ChunkContext.class);
        StepContext aStepContext = mock(StepContext.class);
        when(aChunkContext.getStepContext()).thenReturn(aStepContext);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(null, aChunkContext));
        verify(aChunkContext, times(2)).getStepContext();
        verify(aStepContext, times(1)).getStepExecution();
        assertTrue(aDestResource.getFile().exists());
    }

    @Test
    public void testExecuteCopyEmptySource() throws Exception {
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-input.csv"));
        when(aDestResource.exists()).thenReturn(false);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertFalse(aDestResource.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(null, null));
        assertFalse(aDestResource.getFile().exists());
    }
    
    @Test
    public void testExecuteCopySourceDoesNotExist() throws Exception {
        exception.expect(FileOperationException.class);
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/unknown.file"));
        when(aFileResource1.exists()).thenReturn(false);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-unknown.file"));
        when(aDestResource.exists()).thenReturn(false);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertFalse(aDestResource.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        aTasklet.execute(null, null);
    }
    
    @Test
    public void testExecuteCopyNullDestination() throws Exception {
        exception.expect(FileOperationException.class);
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        aTasklet.execute(null, null);
    }
    
    @Test
    public void testExecuteCopyNullDestinationFactory() throws Exception {
        exception.expect(IllegalArgumentException.class);
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(null);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        aTasklet.execute(null, null);
    }
    
    @Test
    public void testExecuteCopyEmptyDestination() throws Exception {
        exception.expect(FileOperationException.class);
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        // aDestResource.getFile() returns null
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        aTasklet.execute(null, null);
    }

    @Test
    public void testExecuteCopyDirectory() throws Exception {
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-testfiles/"));
        when(aDestResource.exists()).thenReturn(false);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target/CP-testfiles/"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertFalse(aDestResource.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        assertTrue(aDestResource.getFile().exists());
        assertArrayEquals(aFileResource1.getFile().list(), aDestResource.getFile().list());
    }
    
    @Test
    public void testExecuteCopyRecursiveDirectory() throws Exception {
        FileUtils.forceMkdir(new File("target/CP-testfiles/source/subdir"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/CP-testfiles/source/CP0-input.csv"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/CP-testfiles/source/subdir/CP1-input.csv"));
        
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("target/CP-testfiles/source/"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-testfiles/destination/"));
        when(aDestResource.exists()).thenReturn(false);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target/CP-testfiles/destination/"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertFalse(aDestResource.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.setRecursive(true);
        aTasklet.afterPropertiesSet();
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        assertTrue(aDestResource.getFile().exists());
        assertEquals(2, aDestResource.getFile().list().length);
        assertArrayEquals(aFileResource1.getFile().list(), aDestResource.getFile().list());
        assertArrayEquals(aFileResource1.getFile().listFiles()[0].list(), aDestResource.getFile().listFiles()[0].list());
    }
    
    @Test
    public void testExecuteCopyNonRecursiveDirectory() throws Exception {
        FileUtils.forceMkdir(new File("target/CP-testfiles/source/subdir"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/CP-testfiles/source/CP0-input.csv"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/CP-testfiles/source/subdir/CP1-input.csv"));
        
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("target/CP-testfiles/source/"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-testfiles/destination/"));
        when(aDestResource.exists()).thenReturn(false);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target/CP-testfiles/destination/"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertFalse(aDestResource.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.setRecursive(false);
        aTasklet.afterPropertiesSet();
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        assertTrue(aDestResource.getFile().exists());
        assertEquals(1, aDestResource.getFile().list().length);
        assertEquals("CP0-input.csv", aDestResource.getFile().list()[0]);
    }

    @Test
    public void testExecuteCopyReplace() throws Exception {
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-input.csv"));
        when(aDestResource.exists()).thenReturn(true);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target/"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        FileUtils.copyFile(aFileResource1.getFile(), aDestResource.getFile());
        assertTrue(aDestResource.getFile().exists());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        assertTrue(aDestResource.getFile().exists());
    }

    @Test
    public void testExecuteCopyFileToExistingDir() throws Exception {
        FileUtils.forceMkdir(new File("target/CP-testfiles"));
        FilesOperationTasklet aTasklet = new FilesOperationTasklet();
        ResourcesFactory aSourceFactory = mock(ResourcesFactory.class);
        Resource aFileResource1 = mock(Resource.class);
        when(aFileResource1.getFile()).thenReturn(new File("src/test/resources/testFiles/input.csv"));
        when(aFileResource1.exists()).thenReturn(true);
        when(aSourceFactory.getResources(anyMapOf(Object.class, Object.class))).thenReturn(new Resource[]{aFileResource1});
        ResourceFactory aDestinationFactory = mock(ResourceFactory.class);
        Resource aDestResource = mock(Resource.class);
        when(aDestResource.getFile()).thenReturn(new File("target/CP-testfiles/"));
        when(aDestResource.exists()).thenReturn(true);
        Resource aRelativeResource = mock(Resource.class);
        when(aRelativeResource.getFile()).thenReturn(new File("target/CP-testfiles/"));
        when(aDestResource.createRelative("/.")).thenReturn(aRelativeResource);
        when(aDestinationFactory.getResource(anyMapOf(Object.class, Object.class))).thenReturn(aDestResource);
        assertTrue(aDestResource.getFile().exists());
        assertTrue(aDestResource.getFile().isDirectory());
        aTasklet.setSourceFactory(aSourceFactory);
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setOperation(Operation.COPY);
        aTasklet.afterPropertiesSet();
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        assertTrue(aDestResource.getFile().exists());
        assertTrue(aDestResource.getFile().isDirectory());
        assertEquals(1, aDestResource.getFile().list().length);
    }

}
