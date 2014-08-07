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
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Specific ISO date-time converter.
 * <p>
 * The input value can be parsed from any date format available to
 * {@link org.joda.time.format.DateTimeFormat joda time} and converted to the
 * ISO format.</br> This class is especially useful when converting dates for
 * Alfresco.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class ISODateFormatConverter implements FormatConverter {

    private String sourcePattern;
    private DateTimeFormatter sourceFormatter;
    private static final DateTimeFormatter ISO_FORMATTER = ISODateTimeFormat.dateTime();

    public ISODateFormatConverter() {
    }

    public ISODateFormatConverter(String sSourcePattern) {
        sourcePattern = sSourcePattern;
        sourceFormatter = DateTimeFormat.forPattern(sourcePattern);
    }

    public String getSourcePattern() {
        return sourcePattern;
    }

    public void setSourcePattern(String sSourcePattern) {
        sourcePattern = sSourcePattern;
        sourceFormatter = DateTimeFormat.forPattern(sourcePattern);
    }

    public List<String> convert(String sSource) throws ConversionException {
        List<String> aResult = new ArrayList<String>(1);
        try {
            DateTime aDateTime = sourceFormatter.parseDateTime(sSource);
            aResult.add(ISO_FORMATTER.print(aDateTime));
        } catch (Exception e) {
            throw new ConversionException("Error converting: " + sSource, e);
        }
        return aResult;
    }

}
