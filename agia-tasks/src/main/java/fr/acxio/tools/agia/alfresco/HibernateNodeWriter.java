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

import java.util.List;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.HibernateOperations;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.alfresco.dao.NodeDao;
import fr.acxio.tools.agia.alfresco.dao.NodeDaoException;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;

/**
 * <p>
 * A simple {@link org.springframework.batch.item.ItemWriter ItemWriter} for the
 * Hibernate store of {@link fr.acxio.tools.agia.alfresco.domain.Node Node}s.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class HibernateNodeWriter implements ItemWriter<NodeList>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateNodeWriter.class);

    private NodeDao nodeDao;

    private HibernateOperations hibernateTemplate;

    public void write(List<? extends NodeList> sData) throws NodeDaoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Writing to Hibernate with " + sData.size() + " top nodes.");
        }

        if (!sData.isEmpty()) {
            for (NodeList aNodeList : sData) {
                for (Node aNode : aNodeList) {
                    if (aNode.getParent() == null) {
                        nodeDao.saveOrUpdate(aNode);
                    }
                }
            }
            try {
                hibernateTemplate.flush();
            } finally {
                hibernateTemplate.clear();
            }
        }
    }

    public void setNodeDao(NodeDao sNodeDao) {
        nodeDao = sNodeDao;
    }

    public void setSessionFactory(SessionFactory sSessionFactory) {
        hibernateTemplate = new HibernateTemplate(sSessionFactory);
    }

    public void afterPropertiesSet() {
        Assert.notNull(hibernateTemplate, "Hibernate session factory must be set");
        Assert.notNull(nodeDao, "Delegate DAO must be set");
    }

}
