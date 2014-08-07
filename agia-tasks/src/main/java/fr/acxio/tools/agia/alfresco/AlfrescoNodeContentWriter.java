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

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;

/**
 * <p>
 * A advanced {@link org.springframework.batch.item.ItemWriter ItemWriter} for
 * Alfresco that can write the contents of a
 * {@link fr.acxio.tools.agia.alfresco.domain.NodeList NodeList} to an Alfresco
 * instance.
 * </p>
 * 
 * <p>
 * Stateless, so restartable.
 * </p>
 * 
 * <p>
 * Uses the upload servlet of Alfresco.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class AlfrescoNodeContentWriter extends AlfrescoServicesConsumer implements ItemWriter<NodeList> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoNodeContentWriter.class);

    private static final String URL_TEMPLATE_UPLOAD = "/upload/{scheme}/{address}/{uuid}/{name}?ticket={ticket}&encoding={encoding}&mimetype={mimetype}";
    private static final String PARAM_SCHEME = "scheme";
    private static final String PARAM_ADDRESS = "address";
    private static final String PARAM_UUID = "uuid";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_TICKET = "ticket";
    private static final String PARAM_ENCODING = "encoding";
    private static final String PARAM_MIMETYPE = "mimetype";

    private RestTemplate restTemplate;
    private boolean failIfFileNotFound = false;

    public synchronized RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }

    public synchronized void setRestTemplate(RestTemplate sRestTemplate) {
        restTemplate = sRestTemplate;
    }

    public void setFailIfFileNotFound(boolean sFailIfFileNotFound) {
        failIfFileNotFound = sFailIfFileNotFound;
    }

    @Override
    public void write(List<? extends NodeList> sData) throws RemoteException, NodePathException, VersionOperationException, FileNotFoundException {
        if (!sData.isEmpty()) {
            init();
            RepositoryServiceSoapBindingStub aRepositoryService = getAlfrescoService().getRepositoryService();

            for (NodeList aNodeList : sData) {
                for (Node aNode : aNodeList) {
                    if (aNode instanceof Document) {
                        Document aDocument = (Document) aNode;
                        if ((aDocument.getContentPath() != null) && (aDocument.getContentPath().length() > 0)) {
                            // If the document has a content path, the file must
                            // exist
                            File aFile = new File(aDocument.getContentPath());
                            if (aFile.exists() && aFile.isFile()) {
                                String aCurrentNodePath = aNode.getPath();
                                String aScheme = null;
                                String aAddress = null;
                                String aUUID = null;

                                if ((aNode.getUuid() != null) && !aNode.getUuid().isEmpty()) {
                                    aScheme = aNode.getScheme();
                                    aAddress = aNode.getAddress();
                                    aUUID = aNode.getUuid();
                                }
                                if (aUUID == null) {
                                    org.alfresco.webservice.types.Node[] aMatchingNodes = getRepositoryMatchingNodes(aRepositoryService, aCurrentNodePath);
                                    if ((aMatchingNodes != null) && (aMatchingNodes.length > 0)) {
                                        if (aMatchingNodes.length > 1) {
                                            throw new VersionOperationException("Too many matching nodes");
                                        }
                                        org.alfresco.webservice.types.Node aRepositoryNode = aMatchingNodes[0];
                                        aScheme = aRepositoryNode.getReference().getStore().getScheme();
                                        aAddress = aRepositoryNode.getReference().getStore().getAddress();
                                        aUUID = aRepositoryNode.getReference().getUuid();
                                    }
                                }

                                if (aUUID != null) {
                                    if (LOGGER.isDebugEnabled()) {
                                        LOGGER.debug("Will upload content");
                                    }

                                    StringBuilder aURL = new StringBuilder(getAlfrescoService().getWebappAddress());
                                    aURL.append(URL_TEMPLATE_UPLOAD);

                                    Map<String, String> aURLVariables = new HashMap<String, String>();
                                    aURLVariables.put(PARAM_SCHEME, aScheme);
                                    aURLVariables.put(PARAM_ADDRESS, aAddress);
                                    aURLVariables.put(PARAM_UUID, aUUID);
                                    aURLVariables.put(PARAM_NAME, aFile.getName());
                                    aURLVariables.put(PARAM_TICKET, getAlfrescoService().getTicket());
                                    aURLVariables.put(PARAM_ENCODING, aDocument.getEncoding());
                                    aURLVariables.put(PARAM_MIMETYPE, aDocument.getMimeType());

                                    HttpHeaders aHeaders = new HttpHeaders();
                                    aHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                                    aHeaders.setContentLength(aFile.length());
                                    HttpEntity<FileSystemResource> aEntity = new HttpEntity<FileSystemResource>(new FileSystemResource(aFile), aHeaders);
                                    getRestTemplate().put(aURL.toString(), aEntity, aURLVariables);

                                    if (LOGGER.isDebugEnabled()) {
                                        LOGGER.debug("Content uploaded");
                                    }

                                } else {
                                    throw new NodePathException("Cannot find the node: " + aNode.getPath());
                                }
                            } else if (failIfFileNotFound) {
                                throw new FileNotFoundException(aDocument.getContentPath());
                            }
                        }
                    }
                }
            }

            cleanup();
        }
    }

}
