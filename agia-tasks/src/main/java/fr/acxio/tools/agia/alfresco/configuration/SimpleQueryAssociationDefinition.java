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

/**
 * <p>
 * Simple
 * {@link fr.acxio.tools.agia.alfresco.configuration.QueryAssociationDefinition
 * QueryAssociationDefinition} implementation.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class SimpleQueryAssociationDefinition extends AbstractAssociationDefinition implements QueryAssociationDefinition {

    private String queryLanguage;
    private String query;

    @Override
    public String getQueryLanguage() {
        return queryLanguage;
    }

    @Override
    public String getQuery() {
        return query;
    }

    public void setQueryLanguage(String sQueryLanguage) {
        queryLanguage = sQueryLanguage;
    }

    public void setQuery(String sQuery) {
        query = sQuery;
    }

}
