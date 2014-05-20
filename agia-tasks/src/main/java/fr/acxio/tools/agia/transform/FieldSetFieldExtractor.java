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
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * <p>This is a field extractor for a
 * {@link org.springframework.batch.item.file.transform.FieldSet FieldSet}.
 * Given an array of property names, it will get the matching fields and return
 * an array of all the values.</p>
 * 
 * @author pcollardez
 *
 */
public class FieldSetFieldExtractor implements FieldExtractor<FieldSet>, InitializingBean {

	private String[] names;
	
	public void setNames(String[] names) {
		Assert.notNull(names, "Names must be non-null");
		this.names = Arrays.asList(names).toArray(new String[names.length]);
	}
	
	public Object[] extract(FieldSet sItem) {
		List<Object> values = new ArrayList<Object>();
		for (String propertyName : this.names) {
			values.add(sItem.readString(propertyName));
		}
		return values.toArray();
	}

	public void afterPropertiesSet() {
		Assert.notNull(names, "The 'names' property must be set.");
	}
}
