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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.hibernate.annotations.NaturalId;

/**
 * <p>
 * Alfresco qname local representation.
 * </p>
 * 
 * @author pcollardez
 *
 */
@Entity
@Table(name = DatabaseConstants.TABLE_NAME_QNAME)
public class QName implements Serializable {

    private static final long serialVersionUID = -5409148771698848178L;

    private static final Pattern QNAME_PATTERN = Pattern.compile("^(?:\\{([^}]*)\\})?([^{}]+)$");
    private static final Pattern SHORT_QNAME_PATTERN = Pattern.compile("^(?:([^:]*):)?(.+)$");

    @Id
    @GeneratedValue
    @Column(name = DatabaseConstants.COLUMN_NAME_QNAME_ID)
    private long id;

    @NaturalId(mutable = false)
    @Column(name = DatabaseConstants.COLUMN_NAME_NAMESPACE_URI, length = DatabaseConstants.COLUMN_LENGTH_NAMESPACE_URI)
    private String namespaceURI;

    @NaturalId(mutable = false)
    @Column(name = DatabaseConstants.COLUMN_NAME_LOCALNAME, length = DatabaseConstants.COLUMN_LENGTH_LOCALNAME)
    private String localName;

    @Transient
    private String prefix;

    QName() {
    }

    void setNamespaceURI(String sNamespaceURI) {
        namespaceURI = sNamespaceURI;
    }

    void setLocalName(String sLocalName) {
        localName = sLocalName;
    }

    public QName(String sNamespace, String sName) {
        initializeWithStrings(sNamespace, sName, null);
    }

    public QName(String sNamespace, String sName, String sPrefix) {
        initializeWithStrings(sNamespace, sName, sPrefix);
    }

    private void initializeWithStrings(String sNamespace, String sName, String sPrefix) {
        if ((sName == null) || (sName.length() == 0)) {
            throw new IllegalArgumentException("LocalName cannot be null nor empty");
        }
        namespaceURI = (sNamespace == null) ? XMLConstants.NULL_NS_URI : sNamespace;
        prefix = sPrefix;
        localName = sName;
    }

    public QName(String sQName) {
        initializeWithQName(sQName);
    }

    private void initializeWithQName(String sQName) {
        Matcher aMatcher = QNAME_PATTERN.matcher(sQName);
        if (aMatcher.matches()) {
            namespaceURI = (aMatcher.group(1) == null) ? XMLConstants.NULL_NS_URI : aMatcher.group(1);
            localName = aMatcher.group(2);
        } else {
            throw new IllegalArgumentException("Malformed QName");
        }
    }

    public QName(String sPrefix, String sName, NamespaceContext sNamespaceContext) {
        if (sNamespaceContext == null) {
            throw new IllegalArgumentException("NamespaceContext is mandatory");
        }
        if ((sName == null) || (sName.length() == 0)) {
            throw new IllegalArgumentException("LocalName cannot be null nor empty");
        }
        String aNamespaceURI = sNamespaceContext.getNamespaceURI((sPrefix == null) ? XMLConstants.DEFAULT_NS_PREFIX : sPrefix);
        if ((sPrefix != null) && (sPrefix.length() > 0) && ((aNamespaceURI == null) || (aNamespaceURI.length() == 0))) {
            throw new IllegalArgumentException("Prefix unknown: " + sPrefix);
        }
        namespaceURI = (aNamespaceURI == null) ? XMLConstants.NULL_NS_URI : aNamespaceURI;
        localName = sName;
        prefix = sPrefix;
    }

    public QName(String sName, NamespaceContext sNamespaceContext) {
        if (sNamespaceContext == null) {
            throw new IllegalArgumentException("NamespaceResolver is mandatory");
        }
        if ((sName == null) || (sName.length() == 0)) {
            throw new IllegalArgumentException("LocalName cannot be null nor empty");
        }
        if (sName.startsWith("{")) {
            initializeWithQName(sName);
            prefix = sNamespaceContext.getPrefix(namespaceURI);
            prefix = (prefix == null) ? XMLConstants.DEFAULT_NS_PREFIX : prefix;
        } else {
            Matcher aMatcher = SHORT_QNAME_PATTERN.matcher(sName);
            if (aMatcher.matches()) {
                prefix = (aMatcher.group(1) == null) ? XMLConstants.DEFAULT_NS_PREFIX : aMatcher.group(1);
                localName = aMatcher.group(2);
                String aNamespaceURI = sNamespaceContext.getNamespaceURI(prefix);
                if ((prefix != null) && (prefix.length() > 0) && ((aNamespaceURI == null) || (aNamespaceURI.length() == 0))) {
                    throw new IllegalArgumentException("Prefix unknown: " + prefix);
                }
                namespaceURI = (aNamespaceURI == null) ? XMLConstants.NULL_NS_URI : aNamespaceURI;
            } else {
                throw new IllegalArgumentException("Malformed QName");
            }
        }
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public String getLocalName() {
        return localName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getShortName() {
        return String.format("%s:%s", prefix, localName);
    }

    @Override
    public String toString() {
        return String.format("{%s}%s", namespaceURI, localName);
    }

    @Override
    public boolean equals(Object sObj) {
        if ((sObj == null) || !(sObj instanceof QName)) {
            return false;
        }
        QName aOther = (QName) sObj;
        return toString().equals(aOther.toString());
    }

    @Override
    public int hashCode() {
        return namespaceURI.hashCode() ^ localName.hashCode();
    }

}
