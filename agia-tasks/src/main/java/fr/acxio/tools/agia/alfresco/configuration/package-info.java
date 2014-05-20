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
 
/**
 * SPI interfaces and configuration-related classes for the Alfresco domain.
 * </br>
 * <p>In order to write data to Alfresco, a mapping a necessary between a
 * foreign representation and the Alfresco representation.</br>
 * The purpose of the present Spring configuration definition is to describe
 * this mapping from the Alfresco point of view.
 * </p><p>
 * The mapping definition is a beans tree with a XML representation compatible
 * with Spring:</p>
 * <pre>
 * {@code
 * <alf:folder id="someId" nodeType="custom:someType" versionOperation="error"
 *             condition="true==true">
 *   <alf:properties>
 *     <alf:property localName="cm:name" converterRef="someConverterId">
 *       <alf:value>Name of folder</alf:value>
 *       ...
 *     </alf:property>
 *     ...
 *   </alf:properties>
 *   <alf:aspects>
 *     <alf:aspect name="custom:someaspect" />
 *     ...
 *   </alf:aspects>
 *   <alf:folder ... />
 *   ...
 *   <alf:document id="otherId" nodeType="custom:someType" versionOperation="error"
 *                 contentPath="/some/local/folder/content.pdf"
 *                 mimeType="application/pdf"
 *                 encoding="UTF-8">
 *     <alf:properties>
 *       <alf:property localName="cm:name">
 *         <alf:value>Name of document</alf:value>
 *         ...
 *       </alf:property>
 *       ...
 *     </alf:properties>
 *     <alf:aspects>
 *         <alf:aspect name="custom:otheraspect" />
 *         ...
 *     </alf:aspects>
 *   </alf:document>
 *   <alf:document ... />
 *   ...
 * </alf:folder>
 * }
 * </pre>
 * 
 */
package fr.acxio.tools.agia.alfresco.configuration;