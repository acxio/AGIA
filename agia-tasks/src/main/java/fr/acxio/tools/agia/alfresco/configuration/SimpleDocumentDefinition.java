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
 * <p>
 * Simple {@link fr.acxio.tools.agia.alfresco.configuration.DocumentDefinition
 * DocumentDefinition} implementation.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class SimpleDocumentDefinition extends AbstractNodeDefinition implements DocumentDefinition {

    private String contentPath;
    private String mimeType;
    private String encoding;

    public void setContentPath(String sContentPath) {
        contentPath = sContentPath;
    }

    public String getContentPath() {
        return contentPath;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String sMimeType) {
        mimeType = sMimeType;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String sEncoding) {
        encoding = sEncoding;
    }

}
