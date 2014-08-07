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

/**
 * Alfresco local representation of folders.</br> The FolderDefinition describes
 * how a folder will be created in Alfresco:</br>
 * <ul>
 * <li>A folder is a node.</li>
 * <li>A folder may have a condition (condition) determining if it must be
 * created or not. This condition is a Spring EL expression returning a boolean
 * value.</li>
 * <li>A folder may have sub-folders (folder).</li>
 * <li>A folder may have documents (document).</li>
 * </ul>
 * 
 * @author pcollardez
 *
 */
public interface FolderDefinition extends NodeDefinition {

    void addFolder(FolderDefinition sFolderDefinition);

    List<FolderDefinition> getFolders();

    List<String> getPaths();

    String getCondition();

    void addDocument(DocumentDefinition sDocumentDefinition);

    List<DocumentDefinition> getDocuments();
}
