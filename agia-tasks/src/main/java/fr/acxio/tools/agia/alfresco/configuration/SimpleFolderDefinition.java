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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Simple {@link fr.acxio.tools.agia.alfresco.configuration.FolderDefinition
 * FolderDefinition} implementation.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class SimpleFolderDefinition extends AbstractNodeDefinition implements FolderDefinition {

    private String condition;

    private List<FolderDefinition> folders = new ArrayList<FolderDefinition>();

    private List<DocumentDefinition> documents = new ArrayList<DocumentDefinition>();

    public void addFolder(FolderDefinition sFolderDefinition) {
        folders.add(sFolderDefinition);
    }

    public List<FolderDefinition> getFolders() {
        return folders;
    }

    public List<String> getPaths() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getCondition() {
        return condition;
    }

    public void addDocument(DocumentDefinition sDocumentDefinition) {
        documents.add(sDocumentDefinition);
    }

    public List<DocumentDefinition> getDocuments() {
        return documents;
    }

    public void setCondition(String sCondition) {
        condition = sCondition;
    }

}
