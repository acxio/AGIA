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
 * Alfresco local representation of properties.</br> The PropertyDefinition
 * describes how a property will be created in Alfresco: </br>
 * <ul>
 * <li>A property has a name (localName) which is a qualified name.</li>
 * <li>A property may have format converter (converter).</li>
 * <li>A property should have values (value).</li>
 * </ul>
 * 
 * @author pcollardez
 *
 */
public interface PropertyDefinition {

    String getLocalName();

    FormatConverter getConverter();

    List<String> getValues();

    boolean isMultiValued();

}
