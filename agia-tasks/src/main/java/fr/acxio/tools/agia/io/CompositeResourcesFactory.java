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
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;

public class CompositeResourcesFactory implements ResourcesFactory {
    
    private List<ResourcesFactory> delegates;

    public void setDelegates(List<ResourcesFactory> sDelegates) {
        delegates = sDelegates;
    }

    @Override
    public Resource[] getResources() throws ResourceCreationException {
        return getResources(null);
    }

    @Override
    public Resource[] getResources(Map<? extends Object, ? extends Object> sParameters) throws ResourceCreationException {
        List<Resource> aResult = null;
        if ((delegates != null) && !delegates.isEmpty()) {
            aResult = new ArrayList<Resource>(delegates.size());
            Resource[] aResources;
            for(ResourcesFactory aFactory : delegates) {
                aResources = aFactory.getResources(sParameters);
                if (aResources != null) {
                    for(Resource aResource : aResources) {
                        aResult.add(aResource);
                    }
                }
            }
        }
        return (aResult == null) ? null : aResult.toArray(new Resource[]{});
    }

}
