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

/**
 * Alfresco local representation of documents.</br> The DocumentDefinition
 * describes how a document will be created in Alfresco: </br>
 * <ul>
 * <li>A document is a node.</li>
 * <li>A document may have a content path (contentPath) indicating where to find
 * the content.</li>
 * <li>A document may have a MIME type (mimeType).</li>
 * <li>A document may have an encoding (encoding).</li>
 * </ul>
 * 
 * @author pcollardez
 *
 */
public interface DocumentDefinition extends NodeDefinition {

    String getContentPath();

    String getMimeType();

    String getEncoding();
}
