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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class FilesOperationProcessor extends AbstractFileOperations implements ItemProcessor<Map<String, Object>, Map<String, Object>>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesOperationProcessor.class);
    
    private ResourcesFactory sourceFactory;
    private String key;
    
    public void setSourceFactory(ResourcesFactory sSourceFactory) {
        sourceFactory = sSourceFactory;
    }
    
    public void setKey(String sKey) {
        key = sKey;
    }

    public void afterPropertiesSet() {
        Assert.notNull(sourceFactory, "SourceFactory must be set");
        if (operation != Operation.REMOVE) {
            Assert.notNull(destinationFactory, "DestinationFactory must be set");
        }
        Assert.hasText(key, "Key must be set");
    }

    @Override
    public Map<String, Object> process(Map<String, Object> sItem) throws Exception {
        Map<String, Object> aSourceParams = new HashMap<String, Object>(sItem);
        aSourceParams.put(ResourceFactoryConstants.PARAM_STEP_EXEC, getStepExecution());
        Resource[] aSourceResources = sourceFactory.getResources(aSourceParams);
        Map<String, Object> aDestinationParams = aSourceParams;

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("{} file(s) to {}", aSourceResources.length, operation.toString().toLowerCase());
        }

        List<String> aNewFiles = new ArrayList<String>();
        
        for (Resource aSourceResource : aSourceResources) {
            aNewFiles.addAll(doOperation(aSourceResource, aDestinationParams, null, getStepExecution()));
        }
        
        Map<String, Object> aResult = new HashMap<String, Object>(sItem);
        aResult.put(key, aNewFiles);
        
        return aResult;
    }

    @Override
    protected Resource getDefaultDestination() {
        return null;
    }

    private StepExecution getStepExecution() {
        StepContext context = StepSynchronizationManager.getContext();
        if (context==null) {
            return null;
        }
        StepExecution stepExecution = context.getStepExecution();
        return stepExecution;
    }
}
