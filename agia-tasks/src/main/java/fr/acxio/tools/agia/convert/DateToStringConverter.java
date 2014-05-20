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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import fr.acxio.tools.agia.convert.ConversionException;

public class DateToStringConverter {
	
	private String destinationPattern;
	private DateTimeFormatter destinationFormatter;
	
	public DateToStringConverter() {
	}
	
	public DateToStringConverter(String sDestinationPattern) {
		destinationPattern = sDestinationPattern;
		destinationFormatter = DateTimeFormat.forPattern(destinationPattern);
	}

	public String getDestinationPattern() {
		return destinationPattern;
	}

	public void setDestinationPattern(String sDestinationPattern) {
		destinationPattern = sDestinationPattern;
		destinationFormatter = DateTimeFormat.forPattern(destinationPattern);
	}
	
	public List<String> convert(Calendar sSource) throws ConversionException {
		return convert(new DateTime(sSource));
	}
	
	public List<String> convert(Date sSource) throws ConversionException {
		return convert(new DateTime(sSource));
	}

	public List<String> convert(DateTime sSource) throws ConversionException {
		List<String> aResult = new ArrayList<String>(1);
		try {
			aResult.add(destinationFormatter.print(sSource));
		} catch (Exception e) {
			throw new ConversionException("Error converting: " + sSource, e);
		}
		return aResult;
	}
	
}
