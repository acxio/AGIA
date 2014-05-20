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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

/**
 * <p>Alfresco property local representation.</p>
 * 
 * @author pcollardez
 *
 */
@Entity
@Table(name=DatabaseConstants.TABLE_NAME_PROPERTY)
public class Property implements Serializable {

	private static final long serialVersionUID = 73780822054117673L;

	@Id
	@GeneratedValue
    @Column(name=DatabaseConstants.COLUMN_NAME_PROPERTY_ID)
	private long id;
	
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinColumn(name=DatabaseConstants.COLUMN_NAME_NAME_ID)
	private QName name;
	
	@ElementCollection(targetClass = java.lang.String.class, fetch=FetchType.EAGER)
	@CollectionTable(name=DatabaseConstants.TABLE_NAME_PROPERTYVALUE, joinColumns=@JoinColumn(name=DatabaseConstants.COLUMN_NAME_PROPERTY_ID))
//	@CollectionId(columns={@Column(name="property_id")}, generator="", type=@Type(type="long"))
	@Column(name=DatabaseConstants.COLUMN_NAME_VALUE)
	@Lob
	private List<String> values = new ArrayList<String>();
	
	public Property() {
	}
	
	public Property(QName sName, String sValue) {
		name = sName;
		values.add(sValue);
	}
	
	public Property(QName sName, List<String> sValues) {
		name = sName;
		values.addAll(sValues);
	}
	
	public QName getName() {
		return name;
	}

	public void setName(QName sName) {
		name = sName;
	}

	public List<String> getValues() {
		return values;
	}
	
	public void addValue(String sValue) {
		values.add(sValue);
	}

	boolean isMultiValued() {
		return ((values != null) && (values.size() > 1));
	}

	@Override
	public String toString() {
		StringBuilder aString = new StringBuilder();
		aString.append(name.getShortName()).append(":").append(values.toString());
		return aString.toString();
	}
	
}
