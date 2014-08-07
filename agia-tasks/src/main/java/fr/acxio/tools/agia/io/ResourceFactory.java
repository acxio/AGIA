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

import java.util.Map;

import org.springframework.core.io.Resource;

/**
 * <p>
 * Factory for a single resource at a time.
 * </p>
 * 
 * @author pcollardez
 *
 */
public interface ResourceFactory {

    /**
     * <p>
     * Returns a resource according to its internal implementation.
     * </p>
     * 
     * @return a single resource
     * @throws ResourceCreationException
     *             if the resource cannot be created
     */
    Resource getResource() throws ResourceCreationException;

    /**
     * <p>
     * Returns a resource according to the given parameters.
     * </p>
     * <p>
     * If a specific implementation does not use the parameters, it should
     * return the same resource it would return without any parameters.
     * </p>
     * 
     * @param sParameters
     *            the parameters to use to create the resource
     * @return a single resource
     * @throws ResourceCreationException
     *             if the resource cannot be created
     */
    Resource getResource(Map<? extends Object, ? extends Object> sParameters) throws ResourceCreationException;
}
