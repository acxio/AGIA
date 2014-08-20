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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
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

import fr.acxio.tools.agia.io.ExpressionResourceFactory;
import fr.acxio.tools.agia.io.FileSystemResourcesFactory;

@RunWith(JUnit4.class)
public class ZipFilesTaskletTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @After
    public void tearDown() throws Exception {
        Collection<File> aFilesToDelete = FileUtils.listFiles(new File("target"), new WildcardFileFilter("Z*-input*.zip"), null);
        for(File aFile : aFilesToDelete) {
            aFile.setWritable(true);
            FileUtils.deleteQuietly(aFile);
        }
        FileUtils.deleteDirectory(new File("target/Z-testfiles"));
    }

    @Test
    public void testZipFile() throws Exception {
        String aTargetFilename = "target/Z0-input.zip";
        ZipFilesTasklet aTasklet = new ZipFilesTasklet();
        aTasklet.setSourceBaseDirectory(new FileSystemResource("src/test/resources/testFiles/"));
        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
        aSourceFactory.setPattern("file:src/test/resources/testFiles/input.csv");
        aTasklet.setSourceFactory(aSourceFactory );
        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
        aDestinationFactory.setExpression(aTargetFilename);
        aTasklet.setDestinationFactory(aDestinationFactory );
        
        assertFalse(new File(aTargetFilename).exists());
        
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        
        assertTrue(new File(aTargetFilename).exists());
        ZipFile aZipFile = new ZipFile(new File(aTargetFilename));
        Enumeration<ZipArchiveEntry> aEntries = aZipFile.getEntries();
        assertTrue(aEntries.hasMoreElements());
        assertEquals("input.csv", aEntries.nextElement().getName());
        assertFalse(aEntries.hasMoreElements());
        aZipFile.close();
    }
    
    @Test
    public void testZipTwoFile() throws Exception {
        FileUtils.forceMkdir(new File("target/Z-testfiles/source"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/Z-testfiles/source/CP0-input.csv"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/Z-testfiles/source/CP1-input.csv"));

        String aTargetFilename = "target/Z1-input.zip";
        ZipFilesTasklet aTasklet = new ZipFilesTasklet();
        aTasklet.setSourceBaseDirectory(new FileSystemResource("target/Z-testfiles/source/"));
        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
        aSourceFactory.setPattern("file:target/Z-testfiles/source/*input*.csv");
        aTasklet.setSourceFactory(aSourceFactory );
        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
        aDestinationFactory.setExpression(aTargetFilename);
        aTasklet.setDestinationFactory(aDestinationFactory );
        
        assertFalse(new File(aTargetFilename).exists());
        
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(2)).incrementReadCount();
        verify(aStepContribution, times(2)).incrementWriteCount(1);
        
        assertTrue(new File(aTargetFilename).exists());
        ZipFile aZipFile = new ZipFile(new File(aTargetFilename));
        Enumeration<ZipArchiveEntry> aEntries = aZipFile.getEntries();
        assertTrue(aEntries.hasMoreElements());
        assertEquals("CP0-input.csv", aEntries.nextElement().getName());
        assertTrue(aEntries.hasMoreElements());
        assertEquals("CP1-input.csv", aEntries.nextElement().getName());
        assertFalse(aEntries.hasMoreElements());
        aZipFile.close();
    }
    
    @Test
    public void testZipDirectory() throws Exception {
        FileUtils.forceMkdir(new File("target/Z-testfiles/source"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/Z-testfiles/source/CP0-input.csv"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/Z-testfiles/source/CP1-input.csv"));
        
        String aTargetFilename = "target/Z2-input.zip";
        ZipFilesTasklet aTasklet = new ZipFilesTasklet();
        aTasklet.setSourceBaseDirectory(new FileSystemResource("target/Z-testfiles/"));
        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
        aSourceFactory.setPattern("file:target/Z-testfiles/source/");
        aTasklet.setSourceFactory(aSourceFactory );
        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
        aDestinationFactory.setExpression(aTargetFilename);
        aTasklet.setDestinationFactory(aDestinationFactory );
        
        assertFalse(new File(aTargetFilename).exists());
        
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(3)).incrementReadCount();
        verify(aStepContribution, times(3)).incrementWriteCount(1);
        
        assertTrue(new File(aTargetFilename).exists());
        ZipFile aZipFile = new ZipFile(new File(aTargetFilename));
        Enumeration<ZipArchiveEntry> aEntries = aZipFile.getEntries();
        assertTrue(aEntries.hasMoreElements());
        assertEquals("source", aEntries.nextElement().getName());
        assertTrue(aEntries.hasMoreElements());
        assertEquals("source/CP0-input.csv", aEntries.nextElement().getName());
        assertTrue(aEntries.hasMoreElements());
        assertEquals("source/CP1-input.csv", aEntries.nextElement().getName());
        assertFalse(aEntries.hasMoreElements());
        aZipFile.close();
    }

    @Test
    public void testZipDirectories() throws Exception {
        FileUtils.forceMkdir(new File("target/Z-testfiles/source/subdir"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/Z-testfiles/source/CP0-input.csv"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/Z-testfiles/source/subdir/CP1-input.csv"));
        
        String aTargetFilename = "target/Z3-input.zip";
        ZipFilesTasklet aTasklet = new ZipFilesTasklet();
        aTasklet.setSourceBaseDirectory(new FileSystemResource("target/Z-testfiles/"));
        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
        aSourceFactory.setPattern("file:target/Z-testfiles/source/");
        aTasklet.setSourceFactory(aSourceFactory );
        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
        aDestinationFactory.setExpression(aTargetFilename);
        aTasklet.setDestinationFactory(aDestinationFactory );
        
        assertFalse(new File(aTargetFilename).exists());
        
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(4)).incrementReadCount();
        verify(aStepContribution, times(4)).incrementWriteCount(1);
        
        assertTrue(new File(aTargetFilename).exists());
        ZipFile aZipFile = new ZipFile(new File(aTargetFilename));
        Enumeration<ZipArchiveEntry> aEntries = aZipFile.getEntries();
        assertTrue(aEntries.hasMoreElements());
        assertEquals("source", aEntries.nextElement().getName());
        assertTrue(aEntries.hasMoreElements());
        assertEquals("source/CP0-input.csv", aEntries.nextElement().getName());
        assertTrue(aEntries.hasMoreElements());
        assertEquals("source/subdir", aEntries.nextElement().getName());
        assertTrue(aEntries.hasMoreElements());
        assertEquals("source/subdir/CP1-input.csv", aEntries.nextElement().getName());
        assertFalse(aEntries.hasMoreElements());
        aZipFile.close();
    }
    
    @Test
    public void testZipDirectoriesNotRecursive() throws Exception {
        FileUtils.forceMkdir(new File("target/Z-testfiles/source/subdir"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/Z-testfiles/source/CP0-input.csv"));
        FileUtils.copyFile(new File("src/test/resources/testFiles/input.csv"), new File("target/Z-testfiles/source/subdir/CP1-input.csv"));
        
        String aTargetFilename = "target/Z4-input.zip";
        ZipFilesTasklet aTasklet = new ZipFilesTasklet();
        aTasklet.setSourceBaseDirectory(new FileSystemResource("target/Z-testfiles/"));
        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
        aSourceFactory.setPattern("file:target/Z-testfiles/source/");
        aTasklet.setSourceFactory(aSourceFactory );
        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
        aDestinationFactory.setExpression(aTargetFilename);
        aTasklet.setDestinationFactory(aDestinationFactory );
        aTasklet.setRecursive(false);
        
        assertFalse(new File(aTargetFilename).exists());
        
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(2)).incrementReadCount();
        verify(aStepContribution, times(2)).incrementWriteCount(1);
        
        assertTrue(new File(aTargetFilename).exists());
        ZipFile aZipFile = new ZipFile(new File(aTargetFilename));
        Enumeration<ZipArchiveEntry> aEntries = aZipFile.getEntries();
        assertTrue(aEntries.hasMoreElements());
        assertEquals("source", aEntries.nextElement().getName());
        assertTrue(aEntries.hasMoreElements());
        assertEquals("source/CP0-input.csv", aEntries.nextElement().getName());
        assertFalse(aEntries.hasMoreElements());
        aZipFile.close();
    }
    
    @Test
    public void testUnmatchingBaseDir() throws Exception {
        exception.expect(ZipFilesException.class);
        String aTargetFilename = "target/Z5-input.zip";
        ZipFilesTasklet aTasklet = new ZipFilesTasklet();
        aTasklet.setSourceBaseDirectory(new FileSystemResource("target/"));
        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
        aSourceFactory.setPattern("file:src/test/resources/testFiles/input.csv");
        aTasklet.setSourceFactory(aSourceFactory );
        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
        aDestinationFactory.setExpression(aTargetFilename);
        aTasklet.setDestinationFactory(aDestinationFactory );
        
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(null, null));
    }
    
    @Test
    public void testEmptyBaseDirRelativeDir() throws Exception {
        String aTargetFilename = "target/Z6-input.zip";
        ZipFilesTasklet aTasklet = new ZipFilesTasklet();
        aTasklet.setSourceBaseDirectory(new FileSystemResource(""));
        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
        aSourceFactory.setPattern("file:src/test/resources/testFiles/input.csv");
        aTasklet.setSourceFactory(aSourceFactory );
        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
        aDestinationFactory.setExpression(aTargetFilename);
        aTasklet.setDestinationFactory(aDestinationFactory );
        
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(null, null));
        
        assertTrue(new File(aTargetFilename).exists());
        ZipFile aZipFile = new ZipFile(new File(aTargetFilename));
        Enumeration<ZipArchiveEntry> aEntries = aZipFile.getEntries();
        assertTrue(aEntries.hasMoreElements());
        assertEquals("src/test/resources/testFiles/input.csv", aEntries.nextElement().getName());
        assertFalse(aEntries.hasMoreElements());
        aZipFile.close();
    }
    
// FIXME : Root base dir like C:\ will not work correctly because the last \ is not stripped by File.getCanonicalPath()
//    @Test
//    public void testRootBaseDirAbsoluteDir() throws Exception {
//        String aTargetFilename = "target/Z7-input.zip";
//        ZipFilesTasklet aTasklet = new ZipFilesTasklet();
//        aTasklet.setSourceBaseDirectory(new FileSystemResource(new File("/").getAbsolutePath()));
//        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
//        aSourceFactory.setPattern("file:///" + new File("src/test/resources/testFiles/input.csv").getAbsolutePath());
//        aTasklet.setSourceFactory(aSourceFactory );
//        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
//        aDestinationFactory.setExpression(aTargetFilename);
//        aTasklet.setDestinationFactory(aDestinationFactory );
//        
//        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(null, null));
//        
//        assertTrue(new File(aTargetFilename).exists());
//        ZipFile aZipFile = new ZipFile(new File(aTargetFilename));
//        Enumeration<ZipArchiveEntry> aEntries = aZipFile.getEntries();
//        assertTrue(aEntries.hasMoreElements());
//        assertEquals("src/test/resources/testFiles/input.csv", aEntries.nextElement().getName());
//        assertFalse(aEntries.hasMoreElements());
//        aZipFile.close();
//    }
    
    @Test
    public void testExistingZipFile() throws Exception {
        File aTargetFile = File.createTempFile("target/Z8-input", ".zip");
        String aTargetFilename = aTargetFile.getAbsolutePath();
        
        ZipFilesTasklet aTasklet = new ZipFilesTasklet();
        aTasklet.setSourceBaseDirectory(new FileSystemResource("src/test/resources/testFiles/"));
        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
        aSourceFactory.setPattern("file:src/test/resources/testFiles/input.csv");
        aTasklet.setSourceFactory(aSourceFactory );
        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
        aDestinationFactory.setExpression(aTargetFilename);
        aTasklet.setDestinationFactory(aDestinationFactory );
        
        assertTrue(new File(aTargetFilename).exists());
        
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        
        assertTrue(new File(aTargetFilename).exists());
        ZipFile aZipFile = new ZipFile(new File(aTargetFilename));
        Enumeration<ZipArchiveEntry> aEntries = aZipFile.getEntries();
        assertTrue(aEntries.hasMoreElements());
        assertEquals("input.csv", aEntries.nextElement().getName());
        assertFalse(aEntries.hasMoreElements());
        aZipFile.close();
    }
    
    @Test
    public void testExistingLockedZipFile() throws Exception {
        exception.expect(IOException.class);
        File aTargetFile = File.createTempFile("target/Z9-input", ".zip");
        aTargetFile.setWritable(false);
        String aTargetFilename = aTargetFile.getAbsolutePath();
        
        ZipFilesTasklet aTasklet = new ZipFilesTasklet();
        aTasklet.setSourceBaseDirectory(new FileSystemResource("src/test/resources/testFiles/"));
        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
        aSourceFactory.setPattern("file:src/test/resources/testFiles/input.csv");
        aTasklet.setSourceFactory(aSourceFactory );
        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
        aDestinationFactory.setExpression(aTargetFilename);
        aTasklet.setDestinationFactory(aDestinationFactory );
        
        assertTrue(new File(aTargetFilename).exists());
        
        StepContribution aStepContribution = mock(StepContribution.class);
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        verify(aStepContribution, times(1)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1);
        
        assertTrue(new File(aTargetFilename).exists());
        ZipFile aZipFile = new ZipFile(new File(aTargetFilename));
        Enumeration<ZipArchiveEntry> aEntries = aZipFile.getEntries();
        assertTrue(aEntries.hasMoreElements());
        assertEquals("input.csv", aEntries.nextElement().getName());
        assertFalse(aEntries.hasMoreElements());
        aZipFile.close();
    }
}
