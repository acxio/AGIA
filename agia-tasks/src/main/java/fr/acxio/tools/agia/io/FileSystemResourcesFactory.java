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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * <p>
 * A filesystem resources factory using a pattern to build the resources.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class FileSystemResourcesFactory implements ResourcesFactory {

    private ResourcePatternResolver resourcePatternResolver;
    private String pattern;

    public FileSystemResourcesFactory() {
        this(new PathMatchingResourcePatternResolver(), null);
    }

    public FileSystemResourcesFactory(String sPattern) {
        this(new PathMatchingResourcePatternResolver(), sPattern);
    }

    public FileSystemResourcesFactory(ResourcePatternResolver sResourcePatternResolver, String sPattern) {
        resourcePatternResolver = sResourcePatternResolver;
        pattern = sPattern;
    }

    public void setResourcePatternResolver(ResourcePatternResolver sResourcePatternResolver) {
        resourcePatternResolver = sResourcePatternResolver;
    }

    public void setPattern(String sPattern) {
        pattern = sPattern;
    }

    public Resource[] getResources() throws ResourceCreationException {
        Resource[] aResources = null;
        try {
            aResources = resourcePatternResolver.getResources(pattern);
        } catch (Exception e) {
            throw new ResourceCreationException(e);
        }
        return aResources;
    }

    public Resource[] getResources(Map<? extends Object, ? extends Object> sParameters) throws ResourceCreationException {
        return getResources();
    }

}
