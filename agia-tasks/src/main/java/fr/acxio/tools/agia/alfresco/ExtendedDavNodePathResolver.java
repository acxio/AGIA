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
import java.util.ArrayList;
import java.util.List;

import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryFault;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSet;
import org.alfresco.webservice.types.ResultSetRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

public class ExtendedDavNodePathResolver implements ExtendedNodePathResolver {
	
	private static Logger logger = LoggerFactory.getLogger(ExtendedDavNodePathResolver.class);
	
	private static final String QUERY_PATH_AND_NAME = "PATH:\"%s/*\" AND @cm\\:name:\"%s\"";
	
	private static final String CACHE_KEY = "#sPath.toString() + '/' + #sName.toString()";
	private static final String CACHE_NAME = "drnodes";

	@Override
	@Cacheable(value=CACHE_NAME, key=CACHE_KEY)
	public Node[] getRepositoryMatchingNodes(
			RepositoryServiceSoapBindingStub sRepositoryService, String sPath,
			String sName) throws NodePathException {
		if (logger.isDebugEnabled()) {
        	logger.debug("Querying Alfresco for path: " + sPath + " and name " + sName);
        }
		
		Query aQuery = new Query("lucene", String.format(QUERY_PATH_AND_NAME, sPath, sName));
		
		Reference reference = new Reference(AlfrescoServicesConsumer.STORE, null, sPath);
        Predicate predicate = new Predicate(new Reference[]{reference}, null, null);
        List<Node> aNodeList = new ArrayList<Node>(1);
       	try {
			QueryResult aQueryResult = sRepositoryService.query(AlfrescoServicesConsumer.STORE, aQuery, true);
			ResultSet aResultSet = aQueryResult.getResultSet();
			for(ResultSetRow aRow : aResultSet.getRows()) {
				Node aNewNode = new Node();
				aNewNode.setReference(new Reference(AlfrescoServicesConsumer.STORE, aRow.getNode().getId(), null));
				aNewNode.setAspects(aRow.getNode().getAspects());
				aNewNode.setType(aRow.getNode().getType());
				
				List<NamedValue> aProperties = new ArrayList<NamedValue>();
				
				for(NamedValue aCol : aRow.getColumns()) {
					aProperties.add(aCol);
				}
				
				aNewNode.setProperties(aProperties.toArray(new NamedValue[]{}));
				
				aNodeList.add(aNewNode);
			}
		} catch (RepositoryFault e) {
			throw new NodePathException(e);
		} catch (RemoteException e) {
			throw new NodePathException(e);
		}
       	
        return aNodeList.toArray(new Node[]{});
	}

}
