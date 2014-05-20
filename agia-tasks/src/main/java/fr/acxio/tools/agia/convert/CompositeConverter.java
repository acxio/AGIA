package fr.acxio.tools.agia.convert;

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
import java.util.Collections;
import java.util.List;

/**
 * Applies the given list of converters to the value.
 * <p>
 * The converters are applied in the given order.</br>
 * If a converter returns more than one value, following converters will
 * be fed with each one. 
 * </p>
 * 
 * @author pcollardez
 *
 */
public class CompositeConverter implements FormatConverter {
	
	private List<FormatConverter> converters;
	
	public CompositeConverter() {
	}
	
	public CompositeConverter(List<FormatConverter> sConverters) {
		converters = sConverters;
	}

	public List<FormatConverter> getConverters() {
		return converters;
	}

	public void setConverters(List<FormatConverter> sConverters) {
		converters = sConverters;
	}

	public List<String> convert(String sSource) throws ConversionException {
		List<String> aResult = Collections.singletonList(sSource);
		try {
			if (converters != null) {
				List<String> aTmpResult;
				for(FormatConverter aFormatConverter : converters) {
					aTmpResult = new ArrayList<String>(1);
					for(String aValue : aResult) {
						aTmpResult.addAll(aFormatConverter.convert(aValue));
					}
					aResult = aTmpResult;
				}
			}
		} catch (Exception e) {
			throw new ConversionException("Error converting: " + sSource, e);
		}
		return aResult;
	}

}
