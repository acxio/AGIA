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
 
import java.util.List;

import fr.acxio.tools.agia.convert.FormatConverter;

/**
 * <p>Simple
 * {@link fr.acxio.tools.agia.alfresco.configuration.PropertyDefinition PropertyDefinition}
 * implementation.</p>
 * 
 * @author pcollardez
 *
 */
public class SimplePropertyDefinition implements PropertyDefinition {

	private String localName;
	
	private FormatConverter converter;
	
	private List<String> values;
	
	public void setLocalName(String sLocalName) {
		localName = sLocalName;
	}

	public String getLocalName() {
		return localName;
	}

	public void setConverter(FormatConverter sConverter) {
		converter = sConverter;
	}

	public FormatConverter getConverter() {
		return converter;
	}

	public void setValues(List<String> sValues) {
		values = sValues;
	}

	public List<String> getValues() {
		return values;
	}

	public boolean isMultiValued() {
		return ((values != null) && (values.size() > 1));
	}

}
