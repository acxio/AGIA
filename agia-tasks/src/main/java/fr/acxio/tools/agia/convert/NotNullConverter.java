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

import java.util.Collections;
import java.util.List;

/**
 * The NotNullConverter removes null value by returning an empty list if the
 * value is null, or a singleton list otherwise.
 * 
 * @author pcollardez
 *
 */
public class NotNullConverter implements FormatConverter {

    public List<String> convert(String sSource) throws ConversionException {
        return ((sSource == null) ? Collections.<String> emptyList() : Collections.singletonList(sSource));
    }

}
