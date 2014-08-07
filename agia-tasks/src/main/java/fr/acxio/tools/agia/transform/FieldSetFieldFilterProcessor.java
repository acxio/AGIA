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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * <p>
 * This is a FieldSet filter based on a field name and allowed values.
 * </p>
 * <p>
 * If the FieldSet does not have one of the allowed value in the specified
 * field, {@code null} is returned, which indicates that the FieldSet must be
 * ignored.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class FieldSetFieldFilterProcessor implements ItemProcessor<FieldSet, FieldSet>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldSetFieldFilterProcessor.class);

    private String fieldName;

    private List<String> fieldValues;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String sFieldName) {
        fieldName = sFieldName;
    }

    public List<String> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(List<String> sFieldValues) {
        fieldValues = sFieldValues;
    }

    public void afterPropertiesSet() {
        Assert.hasText(fieldName, "You must provide a field name.");
        Assert.notEmpty(fieldValues, "You must provide at least one value.");
    }

    public FieldSet process(FieldSet sFieldSet) {
        return (((sFieldSet != null) && fieldValues.contains(sFieldSet.readString(fieldName))) ? sFieldSet : null);
    }

}
