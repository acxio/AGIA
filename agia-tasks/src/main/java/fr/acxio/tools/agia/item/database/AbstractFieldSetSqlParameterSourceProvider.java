package fr.acxio.tools.agia.item.database;

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

import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public abstract class AbstractFieldSetSqlParameterSourceProvider<T> implements ItemSqlParameterSourceProvider<T> {

    protected String unnamedColumnFormat = "ucol%d";
    protected String fieldsetNameFormat = "rec%d_%s";

    public void setUnnamedColumnPrefix(String sUnnamedColumnPrefix) {
        unnamedColumnFormat = sUnnamedColumnPrefix + "%d";
    }

    public void setFieldsetNamePrefix(String sFieldsetNamePrefix) {
        fieldsetNameFormat = sFieldsetNamePrefix + "%d_%s";
    }

    protected MapSqlParameterSource mapFieldSet(MapSqlParameterSource sMapSqlParameterSource, FieldSet sFieldSet, int sRecIdx) {
        if (sFieldSet != null) {
            boolean aHasNames = sFieldSet.hasNames();
            int aFieldCount = sFieldSet.getFieldCount();
            String[] aNames = aHasNames ? sFieldSet.getNames() : null;
            String[] aValues = sFieldSet.getValues();
            for(int i = 0; i < aFieldCount; i++) {
                sMapSqlParameterSource.addValue(String.format(fieldsetNameFormat, sRecIdx, (aHasNames && (aNames[i] != null) && !aNames[i].isEmpty()) ? aNames[i] : String.format(unnamedColumnFormat, i)), aValues[i]);
            }
        }
        return sMapSqlParameterSource;
    }
}
