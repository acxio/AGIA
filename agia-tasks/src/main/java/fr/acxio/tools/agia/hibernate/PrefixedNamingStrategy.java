package fr.acxio.tools.agia.hibernate;

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

import org.hibernate.cfg.ImprovedNamingStrategy;

// Adapted from http://stackoverflow.com/questions/3617687/is-it-possible-to-dynamically-define-column-names-in-hibernate-jpa/3618315#3618315
public class PrefixedNamingStrategy extends ImprovedNamingStrategy {

    private static final String EMPTYSTRING = "";
    private static final String UNDERSCORE = "_";
    private static final long serialVersionUID = 9208231002550243729L;
    private String prefix = EMPTYSTRING;

    public void setPrefix(String sPrefix) {
        prefix = sPrefix;
    }

    @Override
    public String tableName(String sTableName) {
        return this.addPrefix(super.tableName(sTableName));
    }

    @Override
    public String classToTableName(final String className) {
        return this.addPrefix(super.classToTableName(className));
    }

    @Override
    public String collectionTableName(final String ownerEntity, final String ownerEntityTable, final String associatedEntity,
            final String associatedEntityTable, final String propertyName) {
        return this.addPrefix(super.collectionTableName(ownerEntity, ownerEntityTable, associatedEntity, associatedEntityTable, propertyName));
    }

    @Override
    public String logicalCollectionTableName(final String tableName, final String ownerEntityTable, final String associatedEntityTable,
            final String propertyName) {
        return this.addPrefix(super.logicalCollectionTableName(tableName, ownerEntityTable, associatedEntityTable, propertyName));
    }

    private String addPrefix(final String composedTableName) {
        return prefix + composedTableName.toUpperCase().replace(UNDERSCORE, EMPTYSTRING);

    }

}
