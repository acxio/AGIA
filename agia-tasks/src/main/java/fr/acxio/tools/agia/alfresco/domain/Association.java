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

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

/**
 * <p>
 * Alfresco association local representation.
 * </p>
 * 
 * @author pcollardez
 *
 */
@Entity
@Table(name = DatabaseConstants.TABLE_NAME_ASSOCIATION)
@Inheritance(strategy = InheritanceType.JOINED)
public class Association implements Serializable {

    private static final long serialVersionUID = -2333171478603178081L;

    @Id
    @GeneratedValue
    @Column(name = DatabaseConstants.COLUMN_NAME_ASSOCIATION_ID)
    private long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = DatabaseConstants.COLUMN_NAME_TYPE_ID)
    private QName type;

    public Association() {
        super();
    }

    public Association(QName sType) {
        super();
        type = sType;
    }

    public QName getType() {
        return type;
    }

    public void setType(QName sType) {
        type = sType;
    }

}
