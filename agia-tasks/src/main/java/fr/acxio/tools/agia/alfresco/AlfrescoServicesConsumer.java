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

import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * <p>Abstract Alfresco service consumer.</p>
 * 
 * @author pcollardez
 *
 */
public class AlfrescoServicesConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoServicesConsumer.class);
	
	public static final Store STORE = new Store(Constants.WORKSPACE_STORE, "SpacesStore");
	
	private AlfrescoService alfrescoService;
	private NodePathResolver nodePathResolver;
	
	public NodePathResolver getNodePathResolver() {
		return nodePathResolver;
	}

	public void setNodePathResolver(NodePathResolver sNodePathResolver) {
		nodePathResolver = sNodePathResolver;
	}

	public AlfrescoService getAlfrescoService() {
		return alfrescoService;
	}

	public void setAlfrescoService(AlfrescoService sAlfrescoService) {
		alfrescoService = sAlfrescoService;
	}

	public void init() throws RemoteException {
		Assert.notNull(alfrescoService, "AlfrescoService is required.");
		Assert.notNull(alfrescoService.getEndpointAddress(), "A endpoint address is required.");
		Assert.notNull(alfrescoService.getUsername(), "A username is required.");
		
		alfrescoService.startSession();		
	}

	public void cleanup() {
		alfrescoService.endSession();
	}
	
	public Node[] getRepositoryMatchingNodes(RepositoryServiceSoapBindingStub sRepositoryService, String sPath) throws NodePathException {
		return nodePathResolver.getRepositoryMatchingNodes(sRepositoryService, sPath);
	}
	
	public Node[] setLocalMatchingNodes(String sPath, Node[] sNodes) throws NodePathException {
		return nodePathResolver.setLocalMatchingNodes(sPath, sNodes);
	}
	
	public void evictRepositoryNode(String sPath) {
		nodePathResolver.evictRepositoryNode(sPath);
	}
	
	public void evictRepositoryNodes() {
		nodePathResolver.evictRepositoryNodes();
	}

}
