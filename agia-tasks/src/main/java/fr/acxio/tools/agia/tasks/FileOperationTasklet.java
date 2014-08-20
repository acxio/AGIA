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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.io.AbstractFileOperations;
import fr.acxio.tools.agia.io.FileOperationException;
import fr.acxio.tools.agia.io.ResourceCreationException;

/**
 * <p>Single file manipulation tasklet.</p>
 * <p>The destination of a copy or move operation must be a single resource.
 * This resource can be built by a ResourceFactory or can be set directly.</p>
 * 
 * @see AbstractFileOperations
 * @author pcollardez
 *
 */
public class FileOperationTasklet extends AbstractFileOperations implements Tasklet, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileOperationTasklet.class);

    private Resource origin;
    private Resource destination;


    public Resource getOrigin() {
        return origin;
    }

    public void setOrigin(Resource sOrigin) {
        origin = sOrigin;
    }

    public Resource getDestination() {
        return destination;
    }

    public void setDestination(Resource sDestination) {
        destination = sDestination;
    }

    public void afterPropertiesSet() {
        Assert.notNull(origin, "Origin must be set");
        if ((operation != Operation.REMOVE) && (destinationFactory == null)) {
            Assert.notNull(destination, "Destination must be set");
        }
        // TODO : add more tests
    }

    public RepeatStatus execute(StepContribution sContribution, ChunkContext sChunkContext) throws IOException, ResourceCreationException,
            FileOperationException {
        doOperation(origin, new HashMap<String, Object>(), sContribution, ((sChunkContext != null) && (sChunkContext.getStepContext() != null)) ? sChunkContext
                .getStepContext().getStepExecution() : null);
        return RepeatStatus.FINISHED;
    }

    @Override
    protected Resource getDefaultDestination() {
        return destination;
    }

}
