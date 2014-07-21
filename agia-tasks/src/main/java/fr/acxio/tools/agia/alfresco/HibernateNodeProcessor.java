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
 
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.alfresco.dao.NodeDao;
import fr.acxio.tools.agia.alfresco.dao.NodeDaoException;
import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.alfresco.domain.NodeStatus;
import fr.acxio.tools.agia.common.ProcessIndicatorItemWrapper;

/**
 * <p>A specific
 * {@link org.springframework.batch.item.ItemProcessor ItemProcessor} for the
 * Hibernate store of
 * {@link fr.acxio.tools.agia.alfresco.domain.Node Node}s that marks the
 * nodes with a given lifecyle status (success or error).</p>
 * 
 * <p>It follows the process indicator pattern.</p>
 * 
 * @author pcollardez
 *
 */
public class HibernateNodeProcessor implements ItemProcessor<ProcessIndicatorItemWrapper<Node>, NodeList>, ItemWriteListener<NodeList>, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(HibernateNodeProcessor.class);
	
	private NodeDao nodeDao;
	
	private int nextStep = NodeStatus.DONE;
	
	private int errorStep = NodeStatus.ERROR;
	
	public void setNodeDao(NodeDao sNodeDao) {
		nodeDao = sNodeDao;
	}
	
	public void setNextStep(int sNextStep) {
		nextStep = sNextStep;
	}

	public void setErrorStep(int sErrorStep) {
		errorStep = sErrorStep;
	}

	public void afterPropertiesSet() {
		Assert.notNull(nodeDao, "You must provide a NodeDao.");
	}

	public NodeList process(ProcessIndicatorItemWrapper<Node> sWrapper) {
		NodeList aNodeList = new NodeList();
		Node aNode = sWrapper.getItem();
		addAndMarkNodes(aNodeList, aNode);
		return aNodeList;
	}

	private void addAndMarkNodes(NodeList sNodeList, Node sParentNode) {
		sNodeList.add(sParentNode);
		sParentNode.setInjectedTimestamp(new Date());
		sParentNode.setJobStep(nextStep);
		if (sParentNode instanceof Folder) {
			Folder sFolder = (Folder)sParentNode;
			for(Folder aFolder : sFolder.getFolders()) {
				addAndMarkNodes(sNodeList, aFolder);
			}
			for(Document aDocument : sFolder.getDocuments()) {
				addAndMarkNodes(sNodeList, aDocument);
			}
		}
	}

	public void beforeWrite(List<? extends NodeList> sItems) {
	}

	public void afterWrite(List<? extends NodeList> sItems) {
		try {
			for(NodeList aNodeList : sItems) {
				for(Node aNode : aNodeList) {
					if (aNode.getParent() == null) {
						nodeDao.saveOrUpdate(aNode);
					}
				}
			}
		} catch (NodeDaoException e) {
			LOGGER.error("Cannot update nodes", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void onWriteError(Exception sException, List<? extends NodeList> sItems) {
		LOGGER.info("Write error occured");
		for(NodeList aNodeList : sItems) {
			for(Node aNode : aNodeList) {
				try {
					nodeDao.markError(aNode.getId(), errorStep);
				} catch (Exception e) {
					LOGGER.error("Unable to mark node on error with id " + aNode.getId(), e);
				}
			}
		}
	}
	
}
