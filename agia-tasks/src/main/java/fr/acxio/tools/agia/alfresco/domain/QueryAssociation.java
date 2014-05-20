package fr.acxio.tools.agia.alfresco.domain;

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
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

/**
 * <p>Alfresco query association local representation.</p>
 * 
 * @author pcollardez
 *
 */
@Entity
@Table(name=DatabaseConstants.TABLE_NAME_QUERYASSOCIATION)
@ForeignKey(name = DatabaseConstants.FK_QUERY_ASSOCIATION)
public class QueryAssociation extends Association {

	private static final long serialVersionUID = 3866349298359568417L;

	@Column(name=DatabaseConstants.COLUMN_NAME_QUERY_LANGUAGE, length=DatabaseConstants.COLUMN_LENGTH_QUERY_LANGUAGE)
	private String queryLanguage;
	
	@Column(name=DatabaseConstants.COLUMN_NAME_QUERY, length=DatabaseConstants.COLUMN_LENGTH_QUERY)
	private String query;

	public String getQueryLanguage() {
		return queryLanguage;
	}

	public void setQueryLanguage(String sQueryLanguage) {
		queryLanguage = sQueryLanguage;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String sQuery) {
		query = sQuery;
	}

	public QueryAssociation() {
		super();
	}
	
	public QueryAssociation(QName sType, String sQueryLanguage, String sQuery) {
		super(sType);
		queryLanguage = sQueryLanguage;
		query = sQuery;
	}

	@Override
	public String toString() {
		StringBuilder aString = new StringBuilder();
		aString.append("QueryAssociation: { type:").append(getType());
		aString.append(", queryLanguage:").append(queryLanguage);
		aString.append(", query:").append(query);
		aString.append("}");
		return aString.toString();
	}
}
