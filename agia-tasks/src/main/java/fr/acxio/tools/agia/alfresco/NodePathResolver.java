package fr.acxio.tools.agia.alfresco;

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
 
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.types.Node;

/**
 * <p>Resolves nodes from their paths</p>
 * <p>The purpose of this interface is mainly to enable Spring cache over
 * the methods of the implementation.</p>
 * 
 * @author pcollardez
 *
 */
public interface NodePathResolver {

	/**
	 * <p>Returns the array of Nodes matching the given path.</p>
	 * 
	 * @param sRepositoryService the repository service to use to execute
	 *                           queries
	 * @param sPath the path of the node to fetch
	 * @return an array of nodes matching the given path
	 * @throws NodePathException if the repository cannot be reached or if the
	 *                           path is wrong
	 */
	Node[] getRepositoryMatchingNodes(RepositoryServiceSoapBindingStub sRepositoryService, String sPath) throws NodePathException;
	
	/**
	 * <p>Defines the array of local nodes matching a given path.</p>
	 * 
	 * @param sPath the path of the nodes
	 * @param sNodes the nodes matching the path
	 * @return the given nodes
	 * @throws NodePathException if the repository cannot be reached or if the
	 *                           path is wrong
	 */
	Node[] setLocalMatchingNodes(String sPath, Node[] sNodes) throws NodePathException;
	
	/**
	 * <p>Removes the given path from the cache.</p>
	 * 
	 * @param sPath the path to remove from the cache
	 */
	void evictRepositoryNode(String sPath);
	
	/**
	 * <p>Cleans up the cache.</p>
	 */
	void evictRepositoryNodes();
}
