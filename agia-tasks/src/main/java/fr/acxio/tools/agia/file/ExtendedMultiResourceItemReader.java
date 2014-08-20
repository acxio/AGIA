package fr.acxio.tools.agia.file;

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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.util.ExecutionContextUserSupport;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import fr.acxio.tools.agia.io.ResourceCreationException;
import fr.acxio.tools.agia.io.ResourceFactoryConstants;
import fr.acxio.tools.agia.io.ResourcesFactory;

// Copy of MultiResourceItemReader because of private members not available to subclasses
public class ExtendedMultiResourceItemReader<T> implements ItemReader<T>, ItemStream {

    private static final Log LOGGER = LogFactory.getLog(ExtendedMultiResourceItemReader.class);

    private static final String RESOURCE_KEY = "resourceIndex";

    private final ExecutionContextUserSupport executionContextUserSupport = new ExecutionContextUserSupport();
    private StepExecution stepExecution;

    private ResourceAwareItemReaderItemStream<? extends T> delegate;

    private Resource[] resources;
    private ResourcesFactory resourcesFactory;
    private boolean isResourcesSet = true;

    private boolean saveState = true;

    private int currentResource = -1;

    // signals there are no resources to read -> just return null on first read
    private boolean noInput;

    private boolean strict = false;

    /**
     * In strict mode the reader will throw an exception on
     * {@link #open(org.springframework.batch.item.ExecutionContext)} if there
     * are no resources to read.
     * 
     * @param strict
     *            false by default
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    private Comparator<Resource> comparator = new Comparator<Resource>() {

        /**
         * Compares resource filenames.
         */
        public int compare(Resource r1, Resource r2) {
            return r1.getFilename().compareTo(r2.getFilename());
        }

    };

    public ExtendedMultiResourceItemReader() {
        executionContextUserSupport.setName(ClassUtils.getShortName(ExtendedMultiResourceItemReader.class));
    }

    /**
     * Reads the next item, jumping to next resource if necessary.
     */
    public T read() throws Exception, UnexpectedInputException, ParseException {

        if (noInput) {
            return null;
        }

        // If there is no resource, then this is the first item, set the current
        // resource to 0 and open the first delegate.
        if (currentResource == -1) {
            currentResource = 0;
            delegate.setResource(resources[currentResource]);
            delegate.open(new ExecutionContext());
        }

        return readNextItem();
    }

    /**
     * Use the delegate to read the next item, jump to next resource if current
     * one is exhausted. Items are appended to the buffer.
     * 
     * @return next item from input
     */
    private T readNextItem() throws Exception {

        T item = delegate.read();

        while (item == null) {

            currentResource++;

            if (currentResource >= resources.length) {
                return null;
            }

            delegate.close();
            delegate.setResource(resources[currentResource]);
            delegate.open(new ExecutionContext());

            item = delegate.read();
        }

        return item;
    }

    /**
     * Close the {@link #setDelegate(ResourceAwareItemReaderItemStream)} reader
     * and reset instance variable values.
     */
    public void close() throws ItemStreamException {
        delegate.close();
        doCloseResources(resources);
        noInput = false;
        if (!isResourcesSet) {
            resources = null;
        }
    }

    protected void doCloseResources(Resource[] sResources) throws ItemStreamException {
        // Override in subclasses
    }

    /**
     * Figure out which resource to start with in case of restart, open the
     * delegate and restore delegate's position in the resource.
     */
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        Assert.isTrue((resources != null) || (resourcesFactory != null), "Resources and ResourcesFactory must not be both null");
        try {
            if ((resources == null) && (resourcesFactory != null)) {
                Map<String, Object> aSourceParams = new HashMap<String, Object>();
                aSourceParams.put(ResourceFactoryConstants.PARAM_STEP_EXEC, stepExecution);
                resources = resourcesFactory.getResources(aSourceParams);
                isResourcesSet = false;
            }
        } catch (ResourceCreationException e) {
            throw new ItemStreamException(e);
        }

        noInput = false;
        if ((resources == null) || (resources.length == 0)) {
            if (strict) {
                throw new IllegalStateException("No resources to read. Set strict=false if this is not an error condition.");
            } else {
                LOGGER.warn("No resources to read. Set strict=true if this should be an error condition.");
                noInput = true;
                return;
            }
        }

        Arrays.sort(resources, comparator);

        if (executionContext.containsKey(executionContextUserSupport.getKey(RESOURCE_KEY))) {
            currentResource = executionContext.getInt(executionContextUserSupport.getKey(RESOURCE_KEY));

            // context could have been saved before reading anything
            if (currentResource == -1) {
                currentResource = 0;
            }

            delegate.setResource(resources[currentResource]);
            delegate.open(executionContext);
        } else {
            currentResource = -1;
        }
    }

    /**
     * Store the current resource index and position in the resource.
     */
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        if (saveState) {
            executionContext.putInt(executionContextUserSupport.getKey(RESOURCE_KEY), currentResource);
            delegate.update(executionContext);
        }
    }

    /**
     * @param delegate
     *            reads items from single {@link Resource}.
     */
    public void setDelegate(ResourceAwareItemReaderItemStream<? extends T> delegate) {
        this.delegate = delegate;
    }

    /**
     * Set the boolean indicating whether or not state should be saved in the
     * provided {@link ExecutionContext} during the {@link ItemStream} call to
     * update.
     * 
     * @param saveState
     */
    public void setSaveState(boolean saveState) {
        this.saveState = saveState;
    }

    /**
     * @param comparator
     *            used to order the injected resources, by default compares
     *            {@link Resource#getFilename()} values.
     */
    public void setComparator(Comparator<Resource> comparator) {
        this.comparator = comparator;
    }

    /**
     * @param resources
     *            input resources
     */
    public void setResources(Resource[] resources) {
        Assert.notNull(resources, "The resources must not be null");
        this.resources = Arrays.asList(resources).toArray(new Resource[resources.length]);
    }

    public Resource getCurrentResource() {
        if (currentResource >= resources.length || currentResource < 0) {
            return null;
        }
        return resources[currentResource];
    }

    public void setResourcesFactory(ResourcesFactory sResourcesFactory) {
        resourcesFactory = sResourcesFactory;
    }

    @BeforeStep
    public void saveStepExecution(StepExecution sStepExecution) {
        stepExecution = sStepExecution;
    }

}