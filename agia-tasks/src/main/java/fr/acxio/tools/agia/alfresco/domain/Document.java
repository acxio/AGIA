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
 * <p>Alfresco document local representation.</p>
 * 
 * @author pcollardez
 *
 */
@Entity
@Table(name=DatabaseConstants.TABLE_NAME_DOCUMENT)
@ForeignKey(name = DatabaseConstants.FK_DOCUMENT_NODE)
public class Document extends Node {

	private static final long serialVersionUID = 489557608272413831L;

	@Column(name=DatabaseConstants.COLUMN_NAME_CONTENT_PATH, length=DatabaseConstants.COLUMN_LENGTH_CONTENT_PATH)
	private String contentPath;
	
	@Column(name=DatabaseConstants.COLUMN_NAME_MIMETYPE, length=DatabaseConstants.COLUMN_LENGTH_MIMETYPE)
	private String mimeType;
	
	@Column(name=DatabaseConstants.COLUMN_NAME_ENCODING, length=DatabaseConstants.COLUMN_LENGTH_ENCODING)
	private String encoding;

	public String getContentPath() {
		return contentPath;
	}

	public void setContentPath(String sContentPath) {
		contentPath = sContentPath;
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

	@Override
	public String toString() {
		StringBuilder aString = new StringBuilder();
		aString.append("Document: { type:").append(getType());
		aString.append(", properties:").append(getProperties());
		aString.append(", aspects:").append(getAspects());
		aString.append(", contentPath:").append(contentPath);
		aString.append(", mimeType:").append(mimeType);
		aString.append(", encoding:").append(encoding);
		aString.append("}");
		return aString.toString();
	}
	
}
