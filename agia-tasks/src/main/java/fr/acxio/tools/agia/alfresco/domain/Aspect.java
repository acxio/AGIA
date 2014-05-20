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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

/**
 * <p>Alfresco aspect local representation.</p>
 * 
 * @author pcollardez
 *
 */
@Entity
@Table(name=DatabaseConstants.TABLE_NAME_ASPECT)
public class Aspect implements Serializable {
	
	private static final long serialVersionUID = 4705838365696437436L;

	@Id
	@GeneratedValue
    @Column(name=DatabaseConstants.COLUMN_NAME_ASPECT_ID)
	private long id;
	
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinColumn(name=DatabaseConstants.COLUMN_NAME_NAME_ID)
	private QName name;

	public Aspect() {
	}
	
	public Aspect(QName sName) {
		name = sName;
	}
	
	public QName getName() {
		return name;
	}

	public void setName(QName sName) {
		name = sName;
	}

	@Override
	public String toString() {
		return name.getShortName();
	}
}
