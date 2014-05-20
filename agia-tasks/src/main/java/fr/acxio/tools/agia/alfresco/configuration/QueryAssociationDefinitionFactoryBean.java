package fr.acxio.tools.agia.alfresco.configuration;

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
 
import org.springframework.util.Assert;

/**
 * <p>Factory of
 * {@link fr.acxio.tools.agia.alfresco.configuration.QueryAssociationDefinition QueryAssociationDefinition}
 * .</p>
 * 
 * @author pcollardez
 *
 */
public class QueryAssociationDefinitionFactoryBean extends
	AssociationDefinitionFactoryBean<QueryAssociationDefinition> {

	private String queryLanguage;
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

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		Assert.hasText(query, "'query' must not be empty.");
		if ((queryLanguage == null) || (queryLanguage.isEmpty())) {
			queryLanguage = "lucene";
		}
	}

	@Override
	public QueryAssociationDefinition getObject() {
		SimpleQueryAssociationDefinition aQueryAssociationDefinition = new SimpleQueryAssociationDefinition();
		aQueryAssociationDefinition.setType(getType());
		aQueryAssociationDefinition.setQuery(getQuery());
		aQueryAssociationDefinition.setQueryLanguage(getQueryLanguage());
		return aQueryAssociationDefinition;
	}

	@Override
	public Class<?> getObjectType() {
		return QueryAssociationDefinition.class;
	}
	
}
