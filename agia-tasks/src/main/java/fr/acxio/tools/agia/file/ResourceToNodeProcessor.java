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

import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.Resource;

import fr.acxio.tools.agia.alfresco.NodeMapper;
import fr.acxio.tools.agia.alfresco.domain.NodeList;

/**
 * <p>
 * This ItemProcessor transforms a Resource into a NodeList graph.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class ResourceToNodeProcessor extends NodeMapper<Resource> implements ItemProcessor<Resource, NodeList> {

    @Override
    public NodeList process(Resource sItem) throws Exception {
        return objectToNodeList(sItem);
    }

    @Override
    public Object transformData(Resource sData) {
        return sData;
    }

}
