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
 
public interface DatabaseConstants {

	String TABLE_NAME_NODE = "xalfnode";
	String TABLE_NAME_DOCUMENT = "xalfdocument";
	String TABLE_NAME_FOLDER = "xalffolder";
	String TABLE_NAME_ASPECT = "xalfaspect";
	String TABLE_NAME_PROPERTY = "xalfproperty";
	String TABLE_NAME_PROPERTYVALUE = "xalfpropertyvalue";
	String TABLE_NAME_QNAME = "xalfqname";
	String TABLE_NAME_ASSOCIATION = "xalfassociation";
	String TABLE_NAME_QUERYASSOCIATION = "xalfqueryassociation";
	String TABLE_NAME_REFASSOCIATION = "xalfrefassociation";
	
	String COLUMN_NAME_NODE_ID = "node_id";
	String COLUMN_NAME_TYPE_ID = "type_id";
	String COLUMN_NAME_NAME = "name";
	int COLUMN_LENGTH_NAME = 1024;
	String COLUMN_NAME_PATH_ELEMENT = "pathElement";
	int COLUMN_LENGTH_PATH_ELEMENT = 1024;
	String COLUMN_NAME_PARENT_ID = "parent_id";
	String COLUMN_NAME_ADDED_TIMESTAMP = "addedTimestamp";
	String COLUMN_NAME_INJECTED_TIMESTAMP = "injectedTimestamp";
	String COLUMN_NAME_LASTERROR_TIMESTAMP = "lastErrorTimestamp";
	String COLUMN_NAME_JOBSTEP = "jobStep";
	String COLUMN_NAME_VERSION_OPERATION = "versionOperation";
	String COLUMN_NAME_ASSOC_TARGET_ID = "assocTargetId";
	int COLUMN_LENGTH_ASSOC_TARGET_ID = 1024;
	String COLUMN_NAME_SCHEME = "scheme";
	int COLUMN_LENGTH_SCHEME = 64;
	String COLUMN_NAME_ADDRESS = "address";
	int COLUMN_LENGTH_ADDRESS = 64;
	String COLUMN_NAME_UUID = "uuid";
	int COLUMN_LENGTH_UUID = 64;
	
	String FK_DOCUMENT_NODE = "fk_document_node";
	String COLUMN_NAME_CONTENT_PATH = "contentPath";
	int COLUMN_LENGTH_CONTENT_PATH = 1024;
	String COLUMN_NAME_MIMETYPE = "mimeType";
	int COLUMN_LENGTH_MIMETYPE = 1024;
	String COLUMN_NAME_ENCODING = "encoding";
	int COLUMN_LENGTH_ENCODING = 1024;
	
	String FK_FOLDER_NODE = "fk_folder_node";
	
	String COLUMN_NAME_ASPECT_ID = "aspect_id";
	String COLUMN_NAME_NAME_ID = "name_id";
	
	String COLUMN_NAME_ASSOCIATION_ID = "association_id";
	
	String COLUMN_NAME_QNAME_ID = "qname_id";
	String COLUMN_NAME_NAMESPACE_URI = "namespaceURI";
	int COLUMN_LENGTH_NAMESPACE_URI = 1024;
	String COLUMN_NAME_LOCALNAME = "localName";
	int COLUMN_LENGTH_LOCALNAME = 1024;
	
	String FK_QUERY_ASSOCIATION = "fk_query_association";
	String COLUMN_NAME_QUERY_LANGUAGE = "queryLanguage";
	int COLUMN_LENGTH_QUERY_LANGUAGE = 64;
	String COLUMN_NAME_QUERY = "query";
	int COLUMN_LENGTH_QUERY = 4096;
	
	String FK_REF_ASSOCIATION = "fk_ref_association";
	String COLUMN_NAME_REFERENCE = "reference";
	int COLUMN_LENGTH_REFERENCE = 1024;
	
	String COLUMN_NAME_PROPERTY_ID = "property_id";
	String COLUMN_NAME_VALUE = "value";
	
}
