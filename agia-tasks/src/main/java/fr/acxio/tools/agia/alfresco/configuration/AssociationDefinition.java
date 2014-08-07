package fr.acxio.tools.agia.alfresco.configuration;

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

/**
 * <p>
 * Alfresco local representation of associations.
 * </p>
 * <p>
 * The AssociationDefinition describes how an association will be added to a
 * node in Alfresco:
 * </p>
 * <ul>
 * <li>An association has a type (type) which is a qualified name.</li>
 * </ul>
 * 
 * @author pcollardez
 *
 */
public interface AssociationDefinition {

    /**
     * <p>
     * Returns the type of the association
     * </p>
     * 
     * @return the type of the association
     */
    String getType();

}
