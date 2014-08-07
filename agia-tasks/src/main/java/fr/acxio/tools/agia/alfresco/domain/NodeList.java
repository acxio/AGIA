package fr.acxio.tools.agia.alfresco.domain;

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
import java.util.Collection;

/**
 * <p>
 * Collection of {@link fr.acxio.tools.agia.alfresco.domain.Node Node}s used as
 * a container when a
 * {@link fr.acxio.tools.agia.alfresco.configuration.NodeFactory NodeFactory}
 * creates a collection of nodes from its configuration.
 * </p>
 * <p>
 * A NodeList matches a single item from an input source.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class NodeList extends ArrayList<Node> {

    private static final long serialVersionUID = 49168097805059942L;

    public NodeList() {
        super();
    }

    public NodeList(Collection<? extends Node> sArg0) {
        super(sArg0);
    }

    public NodeList(int sArg0) {
        super(sArg0);
    }

}
