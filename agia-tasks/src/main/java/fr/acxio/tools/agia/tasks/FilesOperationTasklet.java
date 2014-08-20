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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.io.AbstractFileOperations;
import fr.acxio.tools.agia.io.ResourceCreationException;
import fr.acxio.tools.agia.io.ResourceFactoryConstants;
import fr.acxio.tools.agia.io.ResourcesFactory;

/**
 * <p>
 * Files manipulation tasklet.
 * </p>
 * <p>
 * The source resources is set by a ResourcesFactory.
 * </p>
 * <p>
 * The destination of a copy or move operation must be a single resource. This
 * resource can be built by a ResourceFactory.
 * </p>
 * <p>
 * The destination is built for each source: it can be a single folder or
 * different filenames calculated from a complex expression.
 * </p>
 * <p>
 * Each source resource and the current StepExecution are sent in the evaluation
 * context of the destination factory.
 * </p>
 * <p>
 * These objects can be used to calculate a complex path. For example:
 * </p>
 * 
 * <pre>
 * ${config.destdir}/@{#in.STEP_EXEC.jobExecution.jobId}/@{#in.SOURCE.filename}
 * </pre>
 * <p>
 * In this expression, the base path is read from a properties file, the folder
 * just under the base path is named from the current job instance ID, and the
 * source filename is used as the destination filename.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class FilesOperationTasklet extends AbstractFileOperations implements Tasklet, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesOperationTasklet.class);

    private ResourcesFactory sourceFactory;

    public void setSourceFactory(ResourcesFactory sSourceFactory) {
        sourceFactory = sSourceFactory;
    }

    public void afterPropertiesSet() {
        Assert.notNull(sourceFactory, "SourceFactory must be set");
        if (operation != Operation.REMOVE) {
            Assert.notNull(destinationFactory, "DestinationFactory must be set");
        }
    }

    public RepeatStatus execute(StepContribution sContribution, ChunkContext sChunkContext) throws IOException, ResourceCreationException,
            FileOperationException {
        StepExecution aStepContext = ((sChunkContext != null) && (sChunkContext.getStepContext() != null)) ? sChunkContext
                .getStepContext().getStepExecution() : null;
        Map<String, Object> aSourceParams = new HashMap<String, Object>();
        aSourceParams.put(ResourceFactoryConstants.PARAM_STEP_EXEC, aStepContext);
        Resource[] aSourceResources = sourceFactory.getResources(aSourceParams);
        Map<String, Object> aDestinationParams = new HashMap<String, Object>();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("{} file(s) to {}", aSourceResources.length, operation.toString().toLowerCase());
        }

        for (Resource aSourceResource : aSourceResources) {
            doOperation(aSourceResource, aDestinationParams, sContribution, aStepContext);
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    protected Resource getDefaultDestination() {
        return null;
    }

}
