package fr.acxio.tools.agia.alfresco.domain;

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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * <p>
 * Alfresco folder local representation.
 * </p>
 * 
 * @author pcollardez
 *
 */
@Entity
@Table(name = DatabaseConstants.TABLE_NAME_FOLDER)
@ForeignKey(name = DatabaseConstants.FK_FOLDER_NODE)
public class Folder extends Node {

    private static final long serialVersionUID = -6873864837412895811L;

    @OneToMany(cascade = { CascadeType.ALL })
    @JoinColumn(name = DatabaseConstants.COLUMN_NAME_PARENT_ID)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Folder> folders = new ArrayList<Folder>();

    @OneToMany(cascade = { CascadeType.ALL })
    @JoinColumn(name = DatabaseConstants.COLUMN_NAME_PARENT_ID)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Document> documents = new ArrayList<Document>();

    public List<Folder> getFolders() {
        return folders;
    }

    public void addFolder(Folder sFolder) {
        folders.add(sFolder);
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void addDocument(Document sDocument) {
        documents.add(sDocument);
    }

    @Override
    public String toString() {
        StringBuilder aString = new StringBuilder();
        aString.append("Folder: { type:").append(getType());
        aString.append(", properties:").append(getProperties());
        aString.append(", aspects:").append(getAspects());
        aString.append("}");
        return aString.toString();
    }
}
