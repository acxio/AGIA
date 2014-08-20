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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.Resource;

import fr.acxio.tools.agia.io.ResourceFactory;
import fr.acxio.tools.agia.io.ResourceFactoryConstants;
import fr.acxio.tools.agia.io.ResourcesFactory;

public class MergePDFProcessor implements ItemProcessor<Map<String, Object>, Map<String, Object>> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MergePDFProcessor.class);
    
    private ResourcesFactory sourceFactory;
    private ResourceFactory destinationFactory;
    private boolean forceReplace = false;
    private PDDocumentFactory documentFactory;
    private String key;

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

    public void setKey(String sKey) {
        key = sKey;
    }

    @Override
    public Map<String, Object> process(Map<String, Object> sItem) throws Exception {
        Map<String, Object> aResult = null;
        if (sourceFactory != null) {
            Map<String, Object> aSourceParams = new HashMap<String, Object>(sItem);
            aSourceParams.put(ResourceFactoryConstants.PARAM_STEP_EXEC, getStepExecution());
            PDDocumentContainer aContainer = null;
            try {
                aContainer = documentFactory.fromParts(sourceFactory, aSourceParams);
        
                if (LOGGER.isInfoEnabled()) {
                    if ((aContainer == null) || (aContainer.getParts() == null)) {
                        LOGGER.info("No file to merge");
                    } else {
                        LOGGER.info("{} file(s) to merge", aContainer.getParts().size());
                    }
                }
                
                int aPartsCount = aContainer.getParts().size();
                if (aPartsCount > 0) {
                    aResult = new HashMap<String, Object>(sItem);
                
                    Resource aDestinationResource = destinationFactory.getResource(aSourceParams);
                    
                    File aDestFile = aDestinationResource.getFile();
                    if (aDestFile.exists()) {
                        if (forceReplace && !aDestFile.delete()) {
                            throw new IOException("Destination '" + aDestFile + "' cannot be replaced");
                        }
                    } else if (aDestFile.getParentFile() != null && !aDestFile.getParentFile().exists()
                                && !aDestFile.getParentFile().mkdirs()) {
                        throw new IOException("Destination '" + aDestFile + "' directory cannot be created");
                    }
                    
                    PDDocument destination = aContainer.getParts().get(0);
                    PDFMergerUtility merger = new PDFMergerUtility();
                    for(int i = 1; i < aPartsCount; i++) {
                        merger.appendDocument(destination, aContainer.getParts().get(i));
                    }
                    
                    destination.save(aDestFile.getAbsolutePath());
                    
                    aResult.put(key, aDestFile.getAbsolutePath());
                    
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Merged to : {}", aResult.get(key));
                    }
                }
            } finally {
                if (aContainer != null) {
                    aContainer.close();
                }
            }
        }
        return aResult;
    }
    
    private StepExecution getStepExecution() {
        StepContext context = StepSynchronizationManager.getContext();
        if (context==null) {
            return null;
        }
        return context.getStepExecution();
    }

}
