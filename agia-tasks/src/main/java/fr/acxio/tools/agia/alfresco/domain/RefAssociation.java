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
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

/**
 * <p>Alfresco reference association local representation.</p>
 * 
 * @author pcollardez
 *
 */
@Entity
@Table(name=DatabaseConstants.TABLE_NAME_REFASSOCIATION)
@ForeignKey(name = DatabaseConstants.FK_REF_ASSOCIATION)
public class RefAssociation extends Association {

	private static final long serialVersionUID = -2258178126145913790L;

	@Column(name=DatabaseConstants.COLUMN_NAME_REFERENCE, length=DatabaseConstants.COLUMN_LENGTH_REFERENCE)
	private String reference;

	public String getReference() {
		return reference;
	}

	public void setReference(String sReference) {
		reference = sReference;
	}
	
	public RefAssociation() {
		super();
	}
	
	public RefAssociation(QName sType, String sReference) {
		super(sType);
		reference = sReference;
	}

	@Override
	public String toString() {
		StringBuilder aString = new StringBuilder();
		aString.append("RefAssociation: { type:").append(getType());
		aString.append(", reference:").append(reference);
		aString.append("}");
		return aString.toString();
	}
}
