package fr.acxio.tools.agia.alfresco;

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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;

/**
 * <p>
 * A specific {@link org.springframework.batch.item.ItemWriter ItemWriter} that
 * deletes content files associated to
 * {@link fr.acxio.tools.agia.alfresco.domain.Node Node}s.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class ContentFileDeleteWriter implements ItemWriter<NodeList> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentFileDeleteWriter.class);

    private boolean ignoreErrors = true;

    public void setIgnoreErrors(boolean sIgnoreErrors) {
        ignoreErrors = sIgnoreErrors;
    }

    public void write(List<? extends NodeList> sData) throws IOException {
        for (NodeList aNodeList : sData) {
            for (Node aNode : aNodeList) {
                if (aNode instanceof Document) {
                    deleteContentFile(((Document) aNode));
                }
            }
        }
    }

    private void deleteContentFile(Document sDocument) throws IOException {
        if ((sDocument.getContentPath() != null) && (sDocument.getContentPath().length() > 0)) {
            File aFile = new File(sDocument.getContentPath());
            if (aFile.exists() && aFile.isFile() && !aFile.delete()) {
                if (ignoreErrors) {
                    LOGGER.warn("Cannot delete content file: " + sDocument.getContentPath());
                } else {
                    throw new IOException("Cannot delete content file: " + sDocument.getContentPath());
                }
            }
        }
    }
}
