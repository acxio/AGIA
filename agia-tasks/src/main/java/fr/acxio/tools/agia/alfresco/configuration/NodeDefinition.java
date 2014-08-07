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

import java.util.List;

/**
 * Alfresco local representation of nodes.</br> The NodeDefinition describes how
 * a node (folder or document) will be created in Alfresco:</br>
 * <ul>
 * <li>Each node may have a type (nodeType) which is a qualified name (
 * {@code custom:mytype} or <code>{http://custom/namespace/1.0}mytype</code>).
 * The default is {@code cm:folder} or {@code cm:document}, depending on the
 * type of node.</li>
 * <li>Each node may have a version operation (versionOperation) which can take
 * a value in: {@code error}, {@code version}, {@code replace} or {@code update}
 * . The default is {@code error}.</li>
 * <li>Each node must have a list of properties (properties), with at least the
 * {@code cm:name} property (property).</li>
 * <li>Each node must have a list of aspects (aspects), which can be empty.
 * </ul>
 * 
 * @author pcollardez
 *
 */
public interface NodeDefinition {

    /**
     * <p>
     * Returns the type of the Node as the String representation of a QName.
     * </p>
     * 
     * @return the type of the Node
     */
    String getNodeType();

    /**
     * <p>
     * Returns the version operation identifier.
     * </p>
     * <p>
     * It can be: RAISEERROR, VERSION, REPLACE or UPDATE.
     * </p>
     * 
     * @return the version operation identifier
     */
    String getVersionOperation();

    /**
     * <p>
     * Returns the ID designating the current Node that can be used into
     * associations.
     * </p>
     * 
     * @return the association target ID
     */
    String getAssocTargetId();

    /**
     * <p>
     * Returns the definitions of properties
     * </p>
     * 
     * @return the definitions of properties
     */
    List<PropertyDefinition> getPropertiesDefinitions();

    /**
     * <p>
     * Adds a definition of a property to the current Node definition.
     * </p>
     * 
     * @param sPropertyDefinition
     *            the definition of a property
     */
    void addPropertyDefinition(PropertyDefinition sPropertyDefinition);

    /**
     * <p>
     * Returns the definitions of aspects
     * </p>
     * 
     * @return the definitions of aspects
     */
    List<AspectDefinition> getAspectsDefinitions();

    /**
     * <p>
     * Adds a definition of an aspect to the current Node definition.
     * </p>
     * 
     * @param sAspectDefinition
     *            the definition of an aspect
     */
    void addAspectDefinition(AspectDefinition sAspectDefinition);

    /**
     * <p>
     * Returns the definitions of associations
     * </p>
     * 
     * @return the definitions of associations
     */
    List<AssociationDefinition> getAssociationsDefinitions();

    /**
     * <p>
     * Adds a definition of an association to the current Node definition.
     * </p>
     * 
     * @param sAssociationDefinition
     *            the definition of an association
     */
    void addAssociationDefinition(AssociationDefinition sAssociationDefinition);
}
