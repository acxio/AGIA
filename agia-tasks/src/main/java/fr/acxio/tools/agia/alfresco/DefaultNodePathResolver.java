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
 
import java.rmi.RemoteException;

import org.alfresco.webservice.repository.RepositoryFault;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * <p>Resolves nodes from their paths, and cache paths and nodes.</p>
 * 
 * @author pcollardez
 *
 */
public class DefaultNodePathResolver implements NodePathResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultNodePathResolver.class);

	private static final String CACHE_KEY = "#sPath";
	private static final String CACHE_NAME = "rnodes";
	
	@Override
	@Cacheable(value=CACHE_NAME, key=CACHE_KEY)
	public Node[] getRepositoryMatchingNodes(
			RepositoryServiceSoapBindingStub sRepositoryService, String sPath)
			throws NodePathException {
		if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("Calling Alfresco for path: " + sPath);
        }
		Reference reference = new Reference(AlfrescoServicesConsumer.STORE, null, sPath);
        Predicate predicate = new Predicate(new Reference[]{reference}, null, null);        
        Node[] nodes = null;
        try {
        	nodes = sRepositoryService.get(predicate);
        } catch (RepositoryFault e) {
        	nodes = null;
        } catch (RemoteException e) {
			throw new NodePathException(e);
		}
        return nodes;
	}

	@Override
	@CachePut(value=CACHE_NAME, key=CACHE_KEY)
	public Node[] setLocalMatchingNodes(String sPath, Node[] sNodes)
			throws NodePathException {
		if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("Set local for Alfresco path: " + sPath);
        }
		return sNodes;
	}

	@Override
	@CacheEvict(value=CACHE_NAME)
	public void evictRepositoryNode(String sPath) {
		if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("Evict Alfresco path: " + sPath);
        }
	}

	@Override
	@CacheEvict(value=CACHE_NAME, allEntries=true)
	public void evictRepositoryNodes() {
		if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("Evict all Alfresco pathes");
        }
	}

}
