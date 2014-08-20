package fr.acxio.tools.agia.transform;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;

/**
 * <p>Transforms a list of {@code FieldSet} into a {@code Map}.</p>
 * <p>The names of the FieldSet will be used as the key in the map. If a name
 * is null or empty, the key is built from index of the FieldSet into the list
 * and from the index of the column into the FieldSet, with the format:
 * {@code rec%d_ucol%d}.</p>
 * <p>For example, if the list contains 2 FieldSet, the first one having the
 * names "Field1" and null, the second one having the names null, "Field2" and
 * "Field3", the resulting map will contain the keys: {@code rec0_Field1,
 * rec0_ucol1, rec1_ucol0, rec1_Field2, rec1_Field3}.</p>
 * <p>The prefixes used for the FieldSet index and the column index can be
 * set to other values.</p>
 * 
 * @author pcollardez
 *
 */
public class ListFieldSetToMapProcessor implements ItemProcessor<List<FieldSet>, Map<String, Object>> {
    
    protected String unnamedColumnFormat = "ucol%d";
    protected String fieldsetNameFormat = "rec%d_%s";
    
    public void setUnnamedColumnPrefix(String sUnnamedColumnPrefix) {
        unnamedColumnFormat = sUnnamedColumnPrefix + "%d";
    }

    public void setFieldsetNamePrefix(String sFieldsetNamePrefix) {
        fieldsetNameFormat = sFieldsetNamePrefix + "%d_%s";
    }
    

    @Override
    public Map<String, Object> process(List<FieldSet> sItem) throws Exception {
        Map<String, Object> aResult = null;
        if ((sItem != null) && !sItem.isEmpty()) {
            aResult = new HashMap<String, Object>();
            for(int aRecIdx = 0; aRecIdx < sItem.size(); aRecIdx++) {
                aResult.putAll(mapFieldSet(sItem.get(aRecIdx), aRecIdx));
            }
        }
        return aResult;
    }

    protected Map<String, Object> mapFieldSet(FieldSet sFieldSet, int sRecIdx) {
        Map<String, Object> aResult = new HashMap<String, Object>();
        if (sFieldSet != null) {
            boolean aHasNames = sFieldSet.hasNames();
            int aFieldCount = sFieldSet.getFieldCount();
            String[] aNames = aHasNames ? sFieldSet.getNames() : null;
            String[] aValues = sFieldSet.getValues();
            for(int i = 0; i < aFieldCount; i++) {
                aResult.put(String.format(fieldsetNameFormat, sRecIdx, (aHasNames && (aNames[i] != null) && !aNames[i].isEmpty()) ? aNames[i] : String.format(unnamedColumnFormat, i)), aValues[i]);
            }
        }
        return aResult;
    }

}
