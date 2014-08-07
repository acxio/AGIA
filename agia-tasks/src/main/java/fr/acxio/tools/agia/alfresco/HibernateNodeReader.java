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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.alfresco.dao.NodeDao;
import fr.acxio.tools.agia.alfresco.dao.NodeDaoException;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeStatus;
import fr.acxio.tools.agia.common.ProcessIndicatorItemWrapper;

/**
 * <p>
 * Item reader for hibernate stored nodes.
 * </p>
 * <p>
 * Item readers for hibernate use to lack efficiency because the whole objects
 * trees is stored into the step context.</br> The HibernateNodeReader will read
 * nodes' ids only, and a dedicated processor will retrieve the nodes
 * themselves.
 * </p>
 * <p>
 * The {@code currentStep} property represents the node lifecycle steps.</br> It
 * can take any integer value, and is used to select the nodes at the given
 * step.</br> By convention, positives values represent successful steps and
 * negatives ones represent failed steps.</br> Some default values are provided
 * by {@link fr.acxio.tools.agia.alfresco.domain.NodeStatus NodeStatus}.
 * </p>
 * <p>
 * The {@code sqlQuery} property can be altered with extreme care to get a
 * specific collection of nodes.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class HibernateNodeReader implements ItemReader<ProcessIndicatorItemWrapper<Node>>, StepExecutionListener, InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateNodeReader.class);

    private static final String JOBSTEP_PARAM = "jobStep";

    /**
     * Thread monitor
     */
    private final Object lock = new Object();

    /**
     * Initialization flag
     */
    private volatile boolean initialized = false;

    /**
     * Collection of nodes' IDs
     */
    private volatile Iterator<Long> keys;

    private NamedParameterJdbcTemplate jdbcTemplate;

    private NodeDao nodeDao;

    /**
     * SQL query used to retrieve nodes' IDs
     */
    private String sqlQuery = "SELECT node_id FROM xalfnode WHERE parent_id IS NULL AND jobStep=:jobStep ORDER BY node_id";

    /**
     * Lifecyle step of the nodes to retrieve
     */
    private int currentStep = NodeStatus.NEW;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public void setNodeDao(NodeDao sNodeDao) {
        nodeDao = sNodeDao;
    }

    /**
     * Sets the SQL query. USE WITH EXTREME CARE.
     * 
     * @param sSqlQuery
     *            the new SQL query.
     */
    public void setSqlQuery(String sSqlQuery) {
        sqlQuery = sSqlQuery;
    }

    /**
     * Sets the lifecyle step of the nodes to retrieve
     * 
     * @param sCurrentStep
     */
    public void setCurrentStep(int sCurrentStep) {
        currentStep = sCurrentStep;
    }

    public void destroy() {
        initialized = false;
        keys = null;
    }

    public void afterPropertiesSet() {
        Assert.notNull(jdbcTemplate, "You must provide a DataSource.");
        Assert.notNull(nodeDao, "You must provide a NodeDao.");
    }

    /**
     * Retrieves nodes' IDs
     * 
     * @return a collection of IDs
     */
    private List<Long> retrieveKeys() {

        synchronized (lock) {
            MapSqlParameterSource aParams = new MapSqlParameterSource();
            aParams.addValue(JOBSTEP_PARAM, currentStep);
            return jdbcTemplate.query(sqlQuery, aParams, new ParameterizedRowMapper<Long>() {
                public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getLong(1);
                }
            });
        }

    }

    public ProcessIndicatorItemWrapper<Node> read() throws NodeDaoException {
        if (!initialized) {
            throw new ReaderNotOpenException("Reader must be open before it can be used.");
        }

        Long id = null;
        synchronized (lock) {
            if (keys.hasNext()) {
                id = keys.next();
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Retrieved key from list: " + id);
        }

        if (id == null) {
            return null;
        }

        return new ProcessIndicatorItemWrapper<Node>(id, nodeDao.findById(id));
    }

    public void beforeStep(StepExecution sStepExecution) {
        synchronized (lock) {
            if (keys == null) {
                keys = retrieveKeys().iterator();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Keys obtained for staging.");
                }
                initialized = true;
            }
        }
    }

    public ExitStatus afterStep(StepExecution sStepExecution) {
        synchronized (lock) {
            initialized = false;
            keys = null;
        }
        return ExitStatus.COMPLETED;
    }

}
