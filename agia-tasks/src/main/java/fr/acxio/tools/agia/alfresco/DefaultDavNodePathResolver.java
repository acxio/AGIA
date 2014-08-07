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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;

import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.util.ISO9075;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import fr.acxio.tools.agia.alfresco.domain.QName;

public class DefaultDavNodePathResolver implements NodePathResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDavNodePathResolver.class);

    private static final String PATH_SEPARATOR = "/";
    private static final String APP_COMPANY_HOME_PATH = "/app:company_home";

    private static final String PATH_FORMAT = "%s/%s:%s";

    private static final Pattern PATH_EXTRACT_PATTERN = Pattern.compile("^(?:(/?.*)/)?([^/]*)$");

    private NamespaceContext namespaceContext;

    private ExtendedNodePathResolver extendedDavNodePathResolver;

    private static final String CACHE_KEY = "#sPath";
    private static final String CACHE_NAME = "dnodes";

    public void setNamespaceContext(NamespaceContext sNamespaceContext) {
        namespaceContext = sNamespaceContext;
    }

    public void setExtendedDavNodePathResolver(ExtendedNodePathResolver sExtendedDavNodePathResolver) {
        extendedDavNodePathResolver = sExtendedDavNodePathResolver;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = CACHE_KEY)
    public Node[] getRepositoryMatchingNodes(RepositoryServiceSoapBindingStub sRepositoryService, String sPath) throws NodePathException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Querying Alfresco for DAV path: " + sPath);
        }

        NodeAndPath aResult = null;

        aResult = getRepositoryMatchingPath(sRepositoryService, sPath, null);

        return new Node[] { aResult.node };
    }

    @Override
    @CachePut(value = CACHE_NAME, key = CACHE_KEY)
    public Node[] setLocalMatchingNodes(String sPath, Node[] sNodes) throws NodePathException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @CacheEvict(value = CACHE_NAME)
    public void evictRepositoryNode(String sPath) {
        // TODO Auto-generated method stub

    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void evictRepositoryNodes() {
        // TODO Auto-generated method stub

    }

    private class NodeAndPath {
        public Node node;
        public String path;
    }

    private NodeAndPath getRepositoryMatchingPath(RepositoryServiceSoapBindingStub sRepositoryService, String sPath, String sName) throws NodePathException {
        NodeAndPath aResult = null;

        if ((sPath == null) || sPath.isEmpty() || PATH_SEPARATOR.equals(sPath)) {
            aResult = new NodeAndPath();
            aResult.node = null;
            aResult.path = APP_COMPANY_HOME_PATH;
        } else {
            Matcher aPathMatcher = PATH_EXTRACT_PATTERN.matcher(sPath);
            if (aPathMatcher.matches()) {
                String aDirPath = aPathMatcher.group(1);
                String aFilename = aPathMatcher.group(2);
                NodeAndPath aAlfrescoPath = getRepositoryMatchingPath(sRepositoryService, aDirPath, aFilename);
                if ((aFilename != null) && (!aFilename.isEmpty())) {
                    Node[] aNodes = extendedDavNodePathResolver.getRepositoryMatchingNodes(sRepositoryService, aAlfrescoPath.path, aFilename);
                    if ((aNodes != null) && (aNodes.length > 0)) {
                        aResult = new NodeAndPath();
                        aResult.node = aNodes[0];
                        QName aTypeQName = new QName(aNodes[0].getType(), namespaceContext);
                        aResult.path = String.format(PATH_FORMAT, aAlfrescoPath.path, aTypeQName.getPrefix(), ISO9075.encode(aFilename));
                    }
                } else {
                    aResult = aAlfrescoPath;
                }
            }
        }

        return aResult;
    }

}
