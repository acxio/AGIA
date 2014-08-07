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

public final class DatabaseConstants {
    
    private DatabaseConstants() {
    }

    public static final String TABLE_NAME_NODE = "xalfnode";
    public static final String TABLE_NAME_DOCUMENT = "xalfdocument";
    public static final String TABLE_NAME_FOLDER = "xalffolder";
    public static final String TABLE_NAME_ASPECT = "xalfaspect";
    public static final String TABLE_NAME_PROPERTY = "xalfproperty";
    public static final String TABLE_NAME_PROPERTYVALUE = "xalfpropertyvalue";
    public static final String TABLE_NAME_QNAME = "xalfqname";
    public static final String TABLE_NAME_ASSOCIATION = "xalfassociation";
    public static final String TABLE_NAME_QUERYASSOCIATION = "xalfqueryassociation";
    public static final String TABLE_NAME_REFASSOCIATION = "xalfrefassociation";

    public static final String COLUMN_NAME_NODE_ID = "node_id";
    public static final String COLUMN_NAME_TYPE_ID = "type_id";
    public static final String COLUMN_NAME_NAME = "name";
    public static final int COLUMN_LENGTH_NAME = 1024;
    public static final String COLUMN_NAME_PATH_ELEMENT = "pathElement";
    public static final int COLUMN_LENGTH_PATH_ELEMENT = 1024;
    public static final String COLUMN_NAME_PARENT_ID = "parent_id";
    public static final String COLUMN_NAME_ADDED_TIMESTAMP = "addedTimestamp";
    public static final String COLUMN_NAME_INJECTED_TIMESTAMP = "injectedTimestamp";
    public static final String COLUMN_NAME_LASTERROR_TIMESTAMP = "lastErrorTimestamp";
    public static final String COLUMN_NAME_JOBSTEP = "jobStep";
    public static final String COLUMN_NAME_VERSION_OPERATION = "versionOperation";
    public static final String COLUMN_NAME_ASSOC_TARGET_ID = "assocTargetId";
    public static final int COLUMN_LENGTH_ASSOC_TARGET_ID = 1024;
    public static final String COLUMN_NAME_SCHEME = "scheme";
    public static final int COLUMN_LENGTH_SCHEME = 64;
    public static final String COLUMN_NAME_ADDRESS = "address";
    public static final int COLUMN_LENGTH_ADDRESS = 64;
    public static final String COLUMN_NAME_UUID = "uuid";
    public static final int COLUMN_LENGTH_UUID = 64;

    public static final String FK_DOCUMENT_NODE = "fk_document_node";
    public static final String COLUMN_NAME_CONTENT_PATH = "contentPath";
    public static final int COLUMN_LENGTH_CONTENT_PATH = 1024;
    public static final String COLUMN_NAME_MIMETYPE = "mimeType";
    public static final int COLUMN_LENGTH_MIMETYPE = 1024;
    public static final String COLUMN_NAME_ENCODING = "encoding";
    public static final int COLUMN_LENGTH_ENCODING = 1024;

    public static final String FK_FOLDER_NODE = "fk_folder_node";

    public static final String COLUMN_NAME_ASPECT_ID = "aspect_id";
    public static final String COLUMN_NAME_NAME_ID = "name_id";

    public static final String COLUMN_NAME_ASSOCIATION_ID = "association_id";

    public static final String COLUMN_NAME_QNAME_ID = "qname_id";
    public static final String COLUMN_NAME_NAMESPACE_URI = "namespaceURI";
    public static final int COLUMN_LENGTH_NAMESPACE_URI = 1024;
    public static final String COLUMN_NAME_LOCALNAME = "localName";
    public static final int COLUMN_LENGTH_LOCALNAME = 1024;

    public static final String FK_QUERY_ASSOCIATION = "fk_query_association";
    public static final String COLUMN_NAME_QUERY_LANGUAGE = "queryLanguage";
    public static final int COLUMN_LENGTH_QUERY_LANGUAGE = 64;
    public static final String COLUMN_NAME_QUERY = "query";
    public static final int COLUMN_LENGTH_QUERY = 4096;

    public static final String FK_REF_ASSOCIATION = "fk_ref_association";
    public static final String COLUMN_NAME_REFERENCE = "reference";
    public static final int COLUMN_LENGTH_REFERENCE = 1024;

    public static final String COLUMN_NAME_PROPERTY_ID = "property_id";
    public static final String COLUMN_NAME_VALUE = "value";

}
