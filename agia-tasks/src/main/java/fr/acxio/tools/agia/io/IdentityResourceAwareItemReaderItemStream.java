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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.core.io.Resource;

/**
 * <p>
 * A Reader returning its Resource as its single record.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class IdentityResourceAwareItemReaderItemStream extends AbstractItemCountingItemStreamItemReader<Resource> implements
        ResourceAwareItemReaderItemStream<Resource> {

    private static final Log LOGGER = LogFactory.getLog(IdentityResourceAwareItemReaderItemStream.class);

    private Resource resource;
    private boolean noInput = false;
    private boolean strict = true;

    @Override
    public void setResource(Resource sResource) {
        resource = sResource;
    }

    /**
     * In strict mode the reader will throw an exception on
     * {@link #open(org.springframework.batch.item.ExecutionContext)} if the
     * input resource does not exist.
     * 
     * @param strict
     *            <code>true</code> by default
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    @Override
    protected Resource doRead() throws Exception {
        if (noInput) {
            return null;
        }
        // currentItemCount is incremented before doRead()
        return (getCurrentItemCount() == 1) ? resource : null;
    }

    @Override
    protected void doOpen() throws Exception {
        noInput = ((resource == null) || !resource.exists());
        if (noInput) {
            if (strict) {
                throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode): " + resource);
            }
            LOGGER.warn("Input resource does not exist " + resource.getDescription());
        }
        setMaxItemCount(1);
    }

    @Override
    protected void doClose() throws Exception {
        // Nothing to do
    }

}
