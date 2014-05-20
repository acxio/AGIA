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
 
import static org.junit.Assert.*;

import javax.xml.namespace.NamespaceContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import fr.acxio.tools.agia.alfresco.configuration.AlfrescoNamespaceContext;

@RunWith(JUnit4.class)
public class QNameTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void TestQNameConstruction() {
		QName aQName = new QName("namespace1", "name1", "prefix1");
		assertEquals("namespace1", aQName.getNamespaceURI());
		assertEquals("name1", aQName.getLocalName());
		assertEquals("prefix1", aQName.getPrefix());
		
		aQName = new QName("namespace1", "name1");
		assertEquals("namespace1", aQName.getNamespaceURI());
		assertEquals("name1", aQName.getLocalName());
		assertEquals(null, aQName.getPrefix());
		
		aQName = new QName(null, "name1");
		assertEquals("", aQName.getNamespaceURI());
		assertEquals("name1", aQName.getLocalName());
		assertEquals(null, aQName.getPrefix());
		
		aQName = new QName("", "name1");
		assertEquals("", aQName.getNamespaceURI());
		assertEquals("name1", aQName.getLocalName());
		assertEquals(null, aQName.getPrefix());
		
		aQName = new QName("{namespace1}name1");
		assertEquals("namespace1", aQName.getNamespaceURI());
		assertEquals("name1", aQName.getLocalName());
		
		aQName = new QName("{}name1");
		assertEquals("", aQName.getNamespaceURI());
		assertEquals("name1", aQName.getLocalName());
		
		aQName = new QName("name1");
		assertEquals("", aQName.getNamespaceURI());
		assertEquals("name1", aQName.getLocalName());
	}
	
	@Test
	public void TestQNameInvalidConstruction1() {
		exception.expect(IllegalArgumentException.class);
		new QName("");
	}
	
	@Test
	public void TestQNameInvalidConstruction2() {
		exception.expect(IllegalArgumentException.class);
		new QName("invalid{}name");
	}
	
	@Test
	public void TestQNameInvalidConstruction3() {
		exception.expect(IllegalArgumentException.class);
		new QName("{name");
	}
	
	@Test
	public void TestQNameInvalidConstruction4() {
		exception.expect(IllegalArgumentException.class);
		new QName("{}");
	}
	
	@Test
	public void TestQNameInvalidConstruction5() {
		exception.expect(IllegalArgumentException.class);
		new QName(null, (String)null);
	}
	
	@Test
	public void TestQNameInvalidConstruction6() {
		exception.expect(IllegalArgumentException.class);
		new QName(null, "");
	}
	
	@Test
	public void TestQNameInvalidConstruction7() {
		exception.expect(IllegalArgumentException.class);
		new QName("", "");
	}
	
	@Test
	public void TestQNameRepresentation() {
		QName aQName = new QName("namespace", "name1");
        assertEquals("{namespace}name1", aQName.toString());

        aQName = new QName("", "name2");
        assertEquals("{}name2", aQName.toString());

        aQName = new QName("{namespace}name3");
        assertEquals("{namespace}name3", aQName.toString());

        aQName = new QName("{}name4");
        assertEquals("{}name4", aQName.toString());

        aQName = new QName("name5");
        assertEquals("{}name5", aQName.toString());
	}
	
	@Test
	public void TestQNameComparison() {
		QName aQName1 = new QName("namespace", "name");
        QName aQName2 = new QName("namespace", "name");
        QName aQName3 = new QName("{namespace}name");
        assertEquals(aQName1, aQName2);
        assertEquals(aQName1, aQName3);

        QName aQName4 = new QName("", "name");
        QName aQName5 = new QName("", "name");
        QName aQName6 = new QName(null, "name");
        assertEquals(aQName4, aQName5);
        assertEquals(aQName4, aQName6);

        QName aQName7 = new QName("namespace", "name");
        QName aQName8 = new QName("namespace", "differentname");
        assertFalse(aQName7.equals(aQName8));

        QName aQName9 = new QName("namespace", "name");
        QName aQName10 = new QName("differentnamespace", "name");
        assertFalse(aQName9.equals(aQName10));
	}
	
	@Test
	public void TestQNamePrefixResolutionNull() {
		exception.expect(IllegalArgumentException.class);
		new QName("alf", "alfresco prefix", (NamespaceContext)null);
	}
	
	@Test
	public void TestQNamePrefixResolution() {
		AlfrescoNamespaceContext aNamespaceContext = new AlfrescoNamespaceContext();
		aNamespaceContext.bindNamespaceUri("alf", "http://www.alfresco.org");
			
        QName qname1 = new QName("alf", "alfresco prefix", aNamespaceContext);
        assertEquals("http://www.alfresco.org", qname1.getNamespaceURI());
        QName qname2 = new QName("", "default prefix", aNamespaceContext);
        assertEquals("", qname2.getNamespaceURI());
        QName qname3 = new QName(null, "null default prefix", aNamespaceContext);
        assertEquals("", qname3.getNamespaceURI());
	}
	
	@Test
	public void TestQNamePrefixResolutionUnknown() {
		AlfrescoNamespaceContext aNamespaceContext = new AlfrescoNamespaceContext();
        exception.expect(IllegalArgumentException.class);
		new QName("garbage", "garbage prefix", aNamespaceContext);
	}
	
	@Test
	public void TestShortQNamePrefixResolutionNull() {
		exception.expect(IllegalArgumentException.class);
		new QName("alf:alfresco prefix", (NamespaceContext)null);
	}
	
	@Test
	public void TestQNameShortPrefixResolutionUnknown() {
		AlfrescoNamespaceContext aNamespaceContext = new AlfrescoNamespaceContext();
        exception.expect(IllegalArgumentException.class);
        new QName("garbage:garbage prefix", aNamespaceContext);
	}
	
	@Test
	public void TestShortQNamePrefixResolution() {
		AlfrescoNamespaceContext aNamespaceContext = new AlfrescoNamespaceContext();
		aNamespaceContext.bindNamespaceUri("alf", "http://www.alfresco.org");
			
        QName qname1 = new QName("alf:alfresco prefix", aNamespaceContext);
        assertEquals("http://www.alfresco.org", qname1.getNamespaceURI());
        QName qname2 = new QName(":default prefix", aNamespaceContext);
        assertEquals("", qname2.getNamespaceURI());
        QName qname3 = new QName("null default prefix", aNamespaceContext);
        assertEquals("", qname3.getNamespaceURI());

        QName qname4 = new QName("{http://www.alfresco.org}alfresco prefix", aNamespaceContext);
        assertEquals("http://www.alfresco.org", qname4.getNamespaceURI());
        assertEquals("alfresco prefix", qname4.getLocalName());
        assertEquals("alf", qname4.getPrefix());
        
        QName qname5 = new QName("{garbage}garbage prefix", aNamespaceContext);
        assertEquals("garbage", qname5.getNamespaceURI());
        assertEquals("garbage prefix", qname5.getLocalName());
        assertEquals(0, qname5.getPrefix().length());
	}
	
	@Test
	public void TestQNameShortRepresentation()
	{
		AlfrescoNamespaceContext aNamespaceContext = new AlfrescoNamespaceContext();
		aNamespaceContext.bindNamespaceUri("alf", "http://www.alfresco.org");
		
		QName aQName = new QName("alf", "name1", aNamespaceContext);
        assertEquals("alf:name1", aQName.getShortName());

        aQName = new QName("alf:name1", aNamespaceContext);
        assertEquals("alf:name1", aQName.getShortName());
        
        aQName = new QName(":name1", aNamespaceContext);
        assertEquals(":name1", aQName.getShortName());

        aQName = new QName("name1", aNamespaceContext);
        assertEquals(":name1", aQName.getShortName());
        
        aQName = new QName("{namespace1}name1", aNamespaceContext);
        assertEquals(":name1", aQName.getShortName());
	}

}
