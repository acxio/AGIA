package fr.acxio.tools.agia.alfresco.dao;

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

import fr.acxio.tools.agia.alfresco.domain.Node;

/**
 * <p>
 * DAO for {@link fr.acxio.tools.agia.alfresco.domain.Node Node}s.
 * </p>
 * 
 * @author pcollardez
 *
 */
public interface NodeDao {

    /**
     * <p>
     * Gets a Node from its ID
     * </p>
     * 
     * @param sId
     *            the ID of the Node to retrieve
     * @return the Node matching the given ID
     * @throws NodeDaoException
     *             if the Node cannot be retrieved
     */
    Node findById(long sId) throws NodeDaoException;

    /**
     * <p>
     * Saves or updates the given Node
     * </p>
     * 
     * @param sNode
     *            the Node to save or update
     * @throws NodeDaoException
     *             if the Node cannot be saved or updated
     */
    void saveOrUpdate(Node sNode) throws NodeDaoException;

    /**
     * <p>
     * Deletes the given Node
     * </p>
     * 
     * @param sNode
     *            the Node to delete
     * @throws NodeDaoException
     *             if the Node cannot be deleted
     */
    void delete(Node sNode) throws NodeDaoException;

    /**
     * <p>
     * Marks the Node matching the given ID with the given error code
     * </p>
     * 
     * @param sId
     *            the ID of the Node to mark
     * @param sError
     *            the error code to apply to the Node
     * @throws NodeDaoException
     *             if the Node cannot be updated
     */
    void markError(long sId, int sError) throws NodeDaoException;
}
