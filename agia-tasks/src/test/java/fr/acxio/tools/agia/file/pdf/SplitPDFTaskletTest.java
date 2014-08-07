package fr.acxio.tools.agia.file.pdf;

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
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.repeat.RepeatStatus;

import fr.acxio.tools.agia.convert.DateToStringConverter;
import fr.acxio.tools.agia.expression.StandardEvaluationContextFactory;
import fr.acxio.tools.agia.io.ExpressionResourceFactory;
import fr.acxio.tools.agia.io.FileSystemResourcesFactory;

@RunWith(JUnit4.class)
public class SplitPDFTaskletTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @After
    public void tearDown() throws Exception {
        Collection<File> aFilesToDelete = FileUtils.listFiles(new File("target"), new WildcardFileFilter("ST-*.pdf"), null);
        for(File aFile : aFilesToDelete) {
            FileUtils.deleteQuietly(aFile);
        }
    }

    @Test
    public void testSplit() throws Exception {
        SplitPDFTasklet aTasklet = new SplitPDFTasklet();
        
        PageSplittingPDDocumentFactory aDocumentFactory = new PageSplittingPDDocumentFactory();
        aDocumentFactory.setSplitAtPage(1);
        aTasklet.setDocumentFactory(aDocumentFactory);
        
        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
        aSourceFactory.setPattern("file:src/test/resources/testFiles/content*.pdf");
        
        aTasklet.setSourceFactory(aSourceFactory);
        
        StandardEvaluationContextFactory aContextFactory = new StandardEvaluationContextFactory();
        
        Map<String, Object> aCommonObjects = new HashMap<String, Object>();
        aCommonObjects.put("fu", new FilenameUtils());
        
        DateToStringConverter aDTSConverter = new DateToStringConverter();
        aDTSConverter.setDestinationPattern("yyyyMMddhhmmssSSS");
        
        aCommonObjects.put("dc", aDTSConverter);
        
        aCommonObjects.put("sr", new SecureRandom());
        
        aContextFactory.setCommonObjects(aCommonObjects);
        
        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
        aDestinationFactory.setEvaluationContextFactory(aContextFactory);
        aDestinationFactory.setExpression("target/ST-N-@{#fu.getBaseName(#in.SOURCE.filename)}-@{#dc.convert(new java.util.Date())}-@{#sr.nextInt(10000)}.pdf");
        
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setForceReplace(false);
        
        StepContribution aStepContribution = mock(StepContribution.class);
        
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(aStepContribution, null));
        
        Collection<File> aFilesTocheck = FileUtils.listFiles(new File("target"), new WildcardFileFilter("ST-N-*.pdf"), null);
        assertEquals(4, aFilesTocheck.size());
        
        verify(aStepContribution, times(2)).incrementReadCount();
        verify(aStepContribution, times(1)).incrementWriteCount(1); // content1.pdf
        verify(aStepContribution, times(1)).incrementWriteCount(3); // content2.pdf
    }
    
    @Test
    public void testSplitOverwriteException() throws Exception {
        exception.expect(SplitPDFException.class);
        SplitPDFTasklet aTasklet = new SplitPDFTasklet();
        
        PageSplittingPDDocumentFactory aDocumentFactory = new PageSplittingPDDocumentFactory();
        aDocumentFactory.setSplitAtPage(1);
        aTasklet.setDocumentFactory(aDocumentFactory);
        
        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
        aSourceFactory.setPattern("file:src/test/resources/testFiles/*.pdf");
        
        aTasklet.setSourceFactory(aSourceFactory);
        
        StandardEvaluationContextFactory aContextFactory = new StandardEvaluationContextFactory();
        
        Map<String, Object> aCommonObjects = new HashMap<String, Object>();
        aCommonObjects.put("fu", new FilenameUtils());
        
        DateToStringConverter aDTSConverter = new DateToStringConverter();
        aDTSConverter.setDestinationPattern("yyyyMMddhhmm");
        
        aCommonObjects.put("dc", aDTSConverter);
                
        aContextFactory.setCommonObjects(aCommonObjects);
        
        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
        aDestinationFactory.setEvaluationContextFactory(aContextFactory);
        aDestinationFactory.setExpression("target/ST-OE-@{#fu.getBaseName(#in.SOURCE.filename)}-@{#dc.convert(new java.util.Date())}.pdf");
        
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setForceReplace(false);
        
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(null, null));
    }
    
    @Test
    public void testSplitOverwrite() throws Exception {
        SplitPDFTasklet aTasklet = new SplitPDFTasklet();
        
        PageSplittingPDDocumentFactory aDocumentFactory = new PageSplittingPDDocumentFactory();
        aDocumentFactory.setSplitAtPage(1);
        aTasklet.setDocumentFactory(aDocumentFactory);
        
        FileSystemResourcesFactory aSourceFactory = new FileSystemResourcesFactory();
        aSourceFactory.setPattern("file:src/test/resources/testFiles/*.pdf");
        
        aTasklet.setSourceFactory(aSourceFactory);
        
        StandardEvaluationContextFactory aContextFactory = new StandardEvaluationContextFactory();
        
        Map<String, Object> aCommonObjects = new HashMap<String, Object>();
        aCommonObjects.put("fu", new FilenameUtils());
        
        aContextFactory.setCommonObjects(aCommonObjects);
        
        ExpressionResourceFactory aDestinationFactory = new ExpressionResourceFactory();
        aDestinationFactory.setEvaluationContextFactory(aContextFactory);
        aDestinationFactory.setExpression("target/ST-O-@{#fu.getBaseName(#in.SOURCE.filename)}-1.pdf");
        
        aTasklet.setDestinationFactory(aDestinationFactory);
        aTasklet.setForceReplace(true);
        
        assertEquals(RepeatStatus.FINISHED, aTasklet.execute(null, null));
        
        Collection<File> aFilesTocheck = FileUtils.listFiles(new File("target"), new WildcardFileFilter("ST-O-*.pdf"), null);
        assertEquals(2, aFilesTocheck.size());
    }

}
