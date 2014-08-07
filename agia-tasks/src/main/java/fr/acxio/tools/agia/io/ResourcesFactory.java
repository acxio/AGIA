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
 * Factory for many resources at the same time.
 * </p>
 * 
 * @author pcollardez
 *
 */
public interface ResourcesFactory {

    /**
     * <p>
     * Returns an array of resources according to its internal implementation.
     * </p>
     * 
     * @return an array of resources
     * @throws ResourceCreationException
     *             if a resource cannot be created
     */
    Resource[] getResources() throws ResourceCreationException;

    /**
     * <p>
     * Returns an array of resources according to the given parameters.
     * </p>
     * <p>
     * If a specific implementation does not use the parameters, it should
     * return the same resources it would return without any parameters.
     * </p>
     * 
     * @param sParameters
     *            the parameters to use to create the resources
     * @return an array of resources
     * @throws ResourceCreationException
     *             if a resource cannot be created
     */
    Resource[] getResources(Map<? extends Object, ? extends Object> sParameters) throws ResourceCreationException;

}
