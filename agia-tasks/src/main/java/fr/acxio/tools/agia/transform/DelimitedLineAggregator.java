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
 
import org.springframework.batch.item.file.transform.ExtractorLineAggregator;

/**
 * <p>A
 * {@link org.springframework.batch.item.file.transform.LineAggregator LineAggregator}
 * implementation that converts an object into a delimited list of strings.
 * The default delimiter is a comma and the default
 * quote character is a double-quote.</p>
 * 
 * @author pcollardez
 */
public class DelimitedLineAggregator<T> extends ExtractorLineAggregator<T> {

	private String delimiter = ",";
	
	private char quoteCharacter = '"';

	/**
	 * Public setter for the delimiter.
	 * @param sDelimiter the delimiter to set
	 */
	public void setDelimiter(String sDelimiter) {
		delimiter = sDelimiter;
	}

	public void setQuoteCharacter(char sQuoteCharacter) {
		quoteCharacter = sQuoteCharacter;
	}

	@Override
	public String doAggregate(Object[] fields) {
		StringBuilder aStringBuilder = new StringBuilder();
		int nbFields = (fields.length - 1);
		for(int i = 0; i <= nbFields; i++) {
			aStringBuilder.append(quoteCharacter).append(fields[i]).append(quoteCharacter);
			if (i < nbFields) {
				aStringBuilder.append(delimiter);
			}
		}
		return aStringBuilder.toString();
	}
}
