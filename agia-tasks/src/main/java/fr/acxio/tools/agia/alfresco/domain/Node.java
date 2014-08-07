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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * <p>
 * Alfresco node local representation.
 * </p>
 * 
 * @author pcollardez
 *
 */
@Entity
@Table(name = DatabaseConstants.TABLE_NAME_NODE)
@Inheritance(strategy = InheritanceType.JOINED)
public class Node implements Serializable {

    private static final long serialVersionUID = -4316127215310911993L;

    private static final String ALFRESCO_PATH_SEPARATOR = "/";
    private static final String CM_NAME = "{http://www.alfresco.org/model/content/1.0}name";

    public enum VersionOperation {
        RAISEERROR, VERSION, REPLACE, UPDATE
    };

    @Id
    @GeneratedValue
    @Column(name = DatabaseConstants.COLUMN_NAME_NODE_ID)
    private long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = DatabaseConstants.COLUMN_NAME_TYPE_ID)
    private QName type;

    @Column(name = DatabaseConstants.COLUMN_NAME_NAME, nullable = false, length = DatabaseConstants.COLUMN_LENGTH_NAME)
    private String name;

    @Column(name = DatabaseConstants.COLUMN_NAME_PATH_ELEMENT, nullable = false, length = DatabaseConstants.COLUMN_LENGTH_PATH_ELEMENT)
    private String pathElement;

    @OneToMany(cascade = { CascadeType.ALL })
    @JoinColumn(name = DatabaseConstants.COLUMN_NAME_NODE_ID)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Property> properties = new ArrayList<Property>();

    @OneToMany(cascade = { CascadeType.ALL })
    @JoinColumn(name = DatabaseConstants.COLUMN_NAME_NODE_ID)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Association> associations = new ArrayList<Association>();

    @OneToMany(cascade = { CascadeType.ALL })
    @JoinColumn(name = DatabaseConstants.COLUMN_NAME_NODE_ID)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Aspect> aspects = new ArrayList<Aspect>();

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = DatabaseConstants.COLUMN_NAME_PARENT_ID, referencedColumnName = DatabaseConstants.COLUMN_NAME_NODE_ID)
    private Node parent;

    @Column(name = DatabaseConstants.COLUMN_NAME_ADDED_TIMESTAMP, nullable = false)
    private Date addedTimestamp;

    @Column(name = DatabaseConstants.COLUMN_NAME_INJECTED_TIMESTAMP, nullable = true)
    private Date injectedTimestamp;

    @Column(name = DatabaseConstants.COLUMN_NAME_LASTERROR_TIMESTAMP, nullable = true)
    private Date lastErrorTimestamp;

    @Column(name = DatabaseConstants.COLUMN_NAME_JOBSTEP, nullable = true)
    private int jobStep = NodeStatus.NEW;

    @Column(name = DatabaseConstants.COLUMN_NAME_VERSION_OPERATION, nullable = true)
    private VersionOperation versionOperation = VersionOperation.RAISEERROR;

    @Column(name = DatabaseConstants.COLUMN_NAME_ASSOC_TARGET_ID, nullable = true, length = DatabaseConstants.COLUMN_LENGTH_ASSOC_TARGET_ID)
    private String assocTargetId;

    @Column(name = DatabaseConstants.COLUMN_NAME_SCHEME, nullable = true, length = DatabaseConstants.COLUMN_LENGTH_SCHEME)
    private String scheme;

    @Column(name = DatabaseConstants.COLUMN_NAME_ADDRESS, nullable = true, length = DatabaseConstants.COLUMN_LENGTH_ADDRESS)
    private String address;

    @Column(name = DatabaseConstants.COLUMN_NAME_UUID, nullable = true, length = DatabaseConstants.COLUMN_LENGTH_UUID)
    private String uuid;

    public long getId() {
        return id;
    }

    public void setParent(Node sNode) {
        parent = sNode;
    }

    public Node getParent() {
        return parent;
    }

    public QName getType() {
        return type;
    }

    public void setType(QName sType) {
        type = sType;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void addProperty(Property sProperty) {
        if (CM_NAME.equals(sProperty.getName().toString()) && sProperty.getValues().size() > 0) {
            name = sProperty.getValues().get(0);
        }
        properties.add(sProperty);
    }

    public List<Aspect> getAspects() {
        return aspects;
    }

    public void addAspect(Aspect sAspect) {
        aspects.add(sAspect);
    }

    public List<Association> getAssociations() {
        return associations;
    }

    public void addAssociation(Association sAssociation) {
        associations.add(sAssociation);
    }

    public void setName(String sName) {
        Iterator<Property> aPropertyIterator = properties.iterator();
        boolean notDone = true;
        Property aProperty;
        while (notDone && (aPropertyIterator.hasNext())) {
            aProperty = aPropertyIterator.next();
            if (CM_NAME.equals(aProperty.getName().toString())) {
                aProperty.getValues().clear();
                aProperty.addValue(sName);
                notDone = false;
            }
        }
        if (notDone) {
            Property aNameProperty = new Property();
            aNameProperty.setName(new QName(CM_NAME));
            aNameProperty.addValue(sName);
            properties.add(aNameProperty);
        }
        name = sName;
    }

    public String getName() {
        return name;
    }

    public void setPathElement(String sPathElement) {
        pathElement = sPathElement;
    }

    public String getPathElement() {
        return pathElement;
    }

    public String getPath() {
        StringBuilder aFullPath = new StringBuilder();
        aFullPath.append(ALFRESCO_PATH_SEPARATOR).append(getPathElement());
        Node aParentNode = getParent();
        while (aParentNode != null) {
            aFullPath.insert(0, aParentNode.getPathElement()).insert(0, ALFRESCO_PATH_SEPARATOR);
            aParentNode = aParentNode.getParent();
        }
        return aFullPath.toString();
    }

    public Date getAddedTimestamp() {
        return addedTimestamp;
    }

    public void setAddedTimestamp(Date sAddedTimestamp) {
        addedTimestamp = sAddedTimestamp;
    }

    public Date getInjectedTimestamp() {
        return injectedTimestamp;
    }

    public void setInjectedTimestamp(Date sInjectedTimestamp) {
        injectedTimestamp = sInjectedTimestamp;
    }

    public Date getLastErrorTimestamp() {
        return lastErrorTimestamp;
    }

    public void setLastErrorTimestamp(Date sLastErrorTimestamp) {
        lastErrorTimestamp = sLastErrorTimestamp;
    }

    public int getJobStep() {
        return jobStep;
    }

    public void setJobStep(int sJobStep) {
        jobStep = sJobStep;
    }

    public VersionOperation getVersionOperation() {
        return versionOperation;
    }

    public void setVersionOperation(VersionOperation sVersionOperation) {
        versionOperation = sVersionOperation;
    }

    public String getAssocTargetId() {
        return assocTargetId;
    }

    public void setAssocTargetId(String sAssocTargetId) {
        assocTargetId = sAssocTargetId;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String sScheme) {
        scheme = sScheme;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String sAddress) {
        address = sAddress;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String sUuid) {
        uuid = sUuid;
    }

}
