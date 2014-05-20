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
 
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatListener;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import fr.acxio.tools.agia.alfresco.domain.Aspect;
import fr.acxio.tools.agia.alfresco.domain.Association;
import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.Property;
import fr.acxio.tools.agia.alfresco.domain.QName;

/**
 * <p>Hibernate implementation of
 * {@link fr.acxio.tools.agia.alfresco.dao.NodeDao NodeDao}.</p>
 * 
 * @author pcollardez
 *
 */
public class HibernateNodeDao extends HibernateDaoSupport implements NodeDao, RepeatListener {

	private List<Throwable> errors = new ArrayList<Throwable>();
	
	/**
	 * Public accessor for the errors property.
	 * 
	 * @return the errors - a list of Throwable instances
	 */
	public List<Throwable> getErrors() {
		return errors;
	}
	
	/**
	 * <p>Gets the list of stored QNames</p>
	 * 
	 * @return the current list of stored QNames
	 * @throws NodeDaoException if the list cannot be retrieved
	 */
	@Transactional(readOnly=true)
	public Collection<QName> getQNames() throws NodeDaoException {
		Collection<QName> aResult; 
		try {
//			DetachedCriteria aCriteria = DetachedCriteria.forClass(QName.class).addOrder(Order.asc("namespaceURI")).addOrder(Order.asc("localName"));
//			return getHibernateTemplate().findByCriteria(aCriteria);
			aResult = getSessionFactory().getCurrentSession().createCriteria(QName.class).addOrder(Order.asc("namespaceURI")).addOrder(Order.asc("localName")).list();
		} catch (Exception e) {
			throw new NodeDaoException(e);
		}
		return aResult;
	}
	
	@Transactional(readOnly=true)
	public Node findById(long sId) throws NodeDaoException {
		Node aResult;
		try {
			aResult = (Node)getHibernateTemplate().get(Node.class, sId);
		} catch (Exception e) {
			throw new NodeDaoException(e);
		}
		return aResult;
	}

	@Transactional
	public void saveOrUpdate(Node sNode) throws NodeDaoException {
		try {
			Collection<QName> aKnowQNames = getQNames();
			loadQNames(sNode, aKnowQNames);
			getSessionFactory().getCurrentSession().saveOrUpdate(sNode);
//			getHibernateTemplate().saveOrUpdate(sNode);
		} catch (Exception e) {
			throw new NodeDaoException(e);
		}
	}
	
	private void loadQNames(Node sNode, Collection<QName> sKnowQNames) throws NodeDaoException {
		// Replace QNames in the object graph when they are already stored in the database
		sNode.setType(getKnowQname(sNode.getType(), sKnowQNames));
		for(Aspect aAspect : sNode.getAspects()) {
			aAspect.setName(getKnowQname(aAspect.getName(), sKnowQNames));
		}
		for(Property aProperty : sNode.getProperties()) {
			aProperty.setName(getKnowQname(aProperty.getName(), sKnowQNames));
		}
		for(Association aAssociation : sNode.getAssociations()) {
			aAssociation.setType(getKnowQname(aAssociation.getType(), sKnowQNames));
		}
		if (sNode instanceof Folder) {
			Folder aFolder = (Folder)sNode;
			for(Document aDocument : aFolder.getDocuments()) {
				loadQNames(aDocument, sKnowQNames);
			}
			for(Folder aSubFolder : aFolder.getFolders()) {
				loadQNames(aSubFolder, sKnowQNames);
			}
		}
	}
	
	private QName getKnowQname(QName sQName, Collection<QName> sKnowQNames) {
		QName aResult = null;
		for(QName aQName : sKnowQNames) {
			if (aQName.equals(sQName)) {
				aResult = aQName;
			}
		}
		if (aResult == null) {
			aResult = sQName;
			sKnowQNames.add(sQName);
		}
		return aResult;
	}

	@Transactional
	public void delete(Node sNode) throws NodeDaoException {
		try {
			getHibernateTemplate().delete(sNode);
		} catch (Exception e) {
			throw new NodeDaoException(e);
		}
	}
	
	@Transactional
	public void markError(long sId, int sError) throws NodeDaoException {
		try {
			Node aNode = findById(sId);
			aNode.setLastErrorTimestamp(new Date());
			aNode.setJobStep(sError);
			getSessionFactory().getCurrentSession().update(aNode);
		} catch (Exception e) {
			throw new NodeDaoException(e);
		}
	}

	public void onError(RepeatContext sContext, Throwable sThrowable) {
		errors.add(sThrowable);
	}
	
	public void after(RepeatContext sArg0, RepeatStatus sArg1) {
	}

	public void before(RepeatContext sArg0) {
	}

	public void close(RepeatContext sArg0) {
	}

	public void open(RepeatContext sArg0) {
	}

}
