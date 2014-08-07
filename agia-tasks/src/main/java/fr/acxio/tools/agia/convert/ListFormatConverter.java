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

import java.util.Arrays;
import java.util.List;

/**
 * Parses a single value and split it according to a specified separator.
 * 
 * @author pcollardez
 *
 */
public class ListFormatConverter implements FormatConverter {

    private String separator;

    public ListFormatConverter() {
    }

    public ListFormatConverter(String sSeparator) {
        separator = sSeparator;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String sSeparator) {
        separator = sSeparator;
    }

    public List<String> convert(String sSource) throws ConversionException {
        List<String> aResult = null;
        try {
            aResult = Arrays.asList(sSource.split(separator));
        } catch (Exception e) {
            throw new ConversionException("Error converting: " + sSource, e);
        }
        return aResult;
    }

}
