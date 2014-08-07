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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;

import fr.acxio.tools.agia.io.ResourceFactory;
import fr.acxio.tools.agia.io.ResourceFactoryConstants;
import fr.acxio.tools.agia.io.ResourcesFactory;

/**
 * <p>
 * A tasklet which can split many PDF at once.
 * </p>
 * <p>
 * It can be configured through the Resource and PDDocument factories.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class SplitPDFTasklet implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SplitPDFTasklet.class);

    private ResourcesFactory sourceFactory;
    private ResourceFactory destinationFactory;
    private boolean forceReplace = false;
    private PDDocumentFactory documentFactory;

    public void setSourceFactory(ResourcesFactory sSourceFactory) {
        sourceFactory = sSourceFactory;
    }

    public void setDestinationFactory(ResourceFactory sDestinationFactory) {
        destinationFactory = sDestinationFactory;
    }

    public void setDocumentFactory(PDDocumentFactory sDocumentFactory) {
        documentFactory = sDocumentFactory;
    }

    public void setForceReplace(boolean sForceReplace) {
        forceReplace = sForceReplace;
    }

    @Override
    public RepeatStatus execute(StepContribution sContribution, ChunkContext sChunkContext) throws Exception {
        Map<String, Object> aSourceParams = new HashMap<String, Object>();
        aSourceParams.put(ResourceFactoryConstants.PARAM_STEP_EXEC, ((sChunkContext != null) && (sChunkContext.getStepContext() != null)) ? sChunkContext
                .getStepContext().getStepExecution() : null);
        Resource[] aSourceResources = sourceFactory.getResources(aSourceParams);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("{} file(s) to split", aSourceResources.length);
        }

        for (Resource aSourceResource : aSourceResources) {
            if (sContribution != null) {
                sContribution.incrementReadCount();
            }
            File aOriginFile = aSourceResource.getFile();
            if (aOriginFile.exists()) {
                int aOutputCount = splitFile(aSourceResource, sChunkContext);
                if (sContribution != null) {
                    sContribution.incrementWriteCount(aOutputCount);
                }
            } else {
                throw new SplitPDFException("File not found: " + aOriginFile);
            }
        }

        return RepeatStatus.FINISHED;
    }

    private int splitFile(Resource sSourceResource, ChunkContext sChunkContext) throws Exception {
        Map<String, Object> aDestinationParams = new HashMap<String, Object>();
        aDestinationParams.put(ResourceFactoryConstants.PARAM_SOURCE, sSourceResource);
        aDestinationParams.put(ResourceFactoryConstants.PARAM_STEP_EXEC, ((sChunkContext != null) && (sChunkContext.getStepContext() != null)) ? sChunkContext
                .getStepContext().getStepExecution() : null);
        Resource aDestination = null;
        int aResult = 0;

        PDDocumentContainer aDocumentContainer = null;

        try {
            aDocumentContainer = documentFactory.getDocument(sSourceResource.getFile());
            List<PDDocument> documents = aDocumentContainer.getParts();

            for (int i = 0; i < documents.size(); i++) {
                PDDocument doc = documents.get(i);
                // Output file factory
                int aTryCount = 10;
                do {
                    aDestination = destinationFactory.getResource(aDestinationParams);
                    aTryCount--;
                } while (!forceReplace && (aTryCount > 0) && (aDestination != null) && aDestination.exists());
                if ((aTryCount == 0) && !forceReplace) {
                    throw new SplitPDFException("Cannot create a new destination filename");
                }
                if (aDestination != null) {
                    if (aDestination.exists() && LOGGER.isWarnEnabled()) {
                        LOGGER.warn("Replacing {}", aDestination.getFile().getAbsolutePath());
                    }
                    writeDocument(doc, aDestination.getFile().getAbsolutePath());
                    doc.close();
                } else {
                    throw new SplitPDFException("No destination specified");
                }
                aResult++;
            }

        } finally {
            if (aDocumentContainer != null) {
                aDocumentContainer.close();
            }
        }
        return aResult;
    }

    private void writeDocument(PDDocument doc, String fileName) throws IOException, COSVisitorException {
        FileOutputStream output = null;
        COSWriter writer = null;
        try {
            FileUtils.forceMkdir(new File(fileName).getAbsoluteFile().getParentFile());
            output = new FileOutputStream(fileName);
            writer = new COSWriter(output);
            writer.write(doc);
        } finally {
            if (output != null) {
                output.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
}
