package fr.acxio.tools.agia.alfresco;

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
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.ResultSet;
import org.alfresco.webservice.types.ResultSetRow;
import org.alfresco.webservice.types.ResultSetRowNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import fr.acxio.tools.agia.alfresco.configuration.AlfrescoNamespaceContext;
import fr.acxio.tools.agia.convert.ConversionException;

@RunWith(JUnit4.class)
public class AlfrescoCategoryConverterTest {

	private AlfrescoNamespaceContext namespaceContext = new AlfrescoNamespaceContext();
	private AlfrescoCategoryConverter categoryConverter = new AlfrescoCategoryConverter();
		
	private static final String NODE_ID1 = "00000000-0000-0000-0000-000000000001";
	private static final String NODE_REF1 = "workspace://SpacesStore/" + NODE_ID1;
	private static final String CATEGORYNAME_FRANCE = "France";
	private static final String CATEGORYNAME_UNKNOWN = "FranceXXX";
	private static final String BASEPATH_UNKNOWN = "/cm:generalclassifiable/cm:RegionsXXX//*";
	private static final String BASEPATH_REGIONS = "/cm:generalclassifiable/cm:Regions//*";
	private static final String CATEGORY_FRANCE_LONGQNAMES = "/{http://www.alfresco.org/model/content/1.0}generalclassifiable/{http://www.alfresco.org/model/content/1.0}Regions/{http://www.alfresco.org/model/content/1.0}EUROPE/{http://www.alfresco.org/model/content/1.0}Western Europe/{http://www.alfresco.org/model/content/1.0}France";
	private static final String CATEGORY_FRANCE_SHORTQNAMES = "/cm:generalclassifiable/cm:Regions/cm:EUROPE/cm:Western Europe/cm:France";
	private static final String CATEGORY_FRANCE_ENCODED_SHORTQNAMES = "/cm:generalclassifiable/cm:Regions/cm:EUROPE/cm:Western_x0020_Europe/cm:France";

	@Before
	public void setUp() throws Exception {
		RepositoryServiceSoapBindingStub aRepoService = mock(RepositoryServiceSoapBindingStub.class);
		
		AlfrescoServiceImpl aAlfrescoService = mock(AlfrescoServiceImpl.class);
		when(aAlfrescoService.getRepositoryService()).thenReturn(aRepoService);
		when(aAlfrescoService.getEndpointAddress()).thenReturn("endpoint");
		when(aAlfrescoService.getUsername()).thenReturn("username");
		
		QueryResult aQueryResult = new QueryResult();
		ResultSet aResultSet = new ResultSet();
		aResultSet.setTotalRowCount(1);
		ResultSetRow aResultSetRow = new ResultSetRow();
		aResultSetRow.setRowIndex(0);
		ResultSetRowNode aNode = new ResultSetRowNode();
		aNode.setId(NODE_ID1);
		aResultSetRow.setNode(aNode);
		aResultSet.setRows(new ResultSetRow[] {aResultSetRow});
		aQueryResult.setResultSet(aResultSet);
		
		QueryResult aEmptyQueryResult = new QueryResult();
		ResultSet aEmptyQueryResultSet = new ResultSet();
		aEmptyQueryResultSet.setTotalRowCount(0);
		aEmptyQueryResult.setResultSet(aEmptyQueryResultSet);
		
		Query aQueryShortQNames = new Query("lucene", "+PATH:\"" + CATEGORY_FRANCE_ENCODED_SHORTQNAMES + "\"");
		when(aRepoService.query(eq(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE), eq(aQueryShortQNames), eq(false))).thenReturn(aQueryResult);
		
		Query aQueryNameBasePath = new Query("lucene", "+TYPE:\"cm:category\" +@cm\\:name:\"" + CATEGORYNAME_FRANCE + "\" +PATH:\"" + BASEPATH_REGIONS + "\"");
		when(aRepoService.query(eq(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE), eq(aQueryNameBasePath), eq(false))).thenReturn(aQueryResult);
		
		Query aQueryUNameBasePath = new Query("lucene", "+TYPE:\"cm:category\" +@cm\\:name:\"" + CATEGORYNAME_UNKNOWN + "\" +PATH:\"" + BASEPATH_REGIONS + "\"");
		when(aRepoService.query(eq(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE), eq(aQueryUNameBasePath), eq(false))).thenReturn(aEmptyQueryResult);

		Query aQueryNameUBasePath = new Query("lucene", "+TYPE:\"cm:category\" +@cm\\:name:\"" + CATEGORYNAME_FRANCE + "\" +PATH:\"" + BASEPATH_UNKNOWN + "\"");
		when(aRepoService.query(eq(fr.acxio.tools.agia.alfresco.AlfrescoServicesConsumer.STORE), eq(aQueryNameUBasePath), eq(false))).thenReturn(aEmptyQueryResult);
		
		categoryConverter.setNamespaceContext(namespaceContext);
		categoryConverter.setAlfrescoService(aAlfrescoService);
		categoryConverter.init();
	}
	
	@After
	public void tearDown() throws Exception {
		categoryConverter.cleanup();
	}

	
	@Test
	public void testConvertFullPath() throws ConversionException {
		categoryConverter.setIgnoreUnknown(true);
		categoryConverter.setBasePath(null);
		List<String> aResult = categoryConverter.convert(CATEGORY_FRANCE_SHORTQNAMES);
		assertNotNull(aResult);
		assertEquals(1, aResult.size());
		assertEquals(NODE_REF1, aResult.get(0));
	}
	
	@Test
	public void testConvertFullPathNamespaces() throws ConversionException {
		categoryConverter.setIgnoreUnknown(true);
		categoryConverter.setBasePath(null);
		List<String> aResult = categoryConverter.convert(CATEGORY_FRANCE_LONGQNAMES);
		assertNotNull(aResult);
		assertEquals(1, aResult.size());
		assertEquals(NODE_REF1, aResult.get(0));
	}
	
	@Test
	public void testConvertBasePathAndName() throws ConversionException {
		categoryConverter.setIgnoreUnknown(true);
		categoryConverter.setBasePath(BASEPATH_REGIONS);
		List<String> aResult = categoryConverter.convert(CATEGORYNAME_FRANCE);
		assertNotNull(aResult);
		assertEquals(1, aResult.size());
		assertEquals(NODE_REF1, aResult.get(0));
	}
	
	@Test
	public void testConvertBasePathAndUnknownName() throws ConversionException {
		categoryConverter.setIgnoreUnknown(true);
		categoryConverter.setBasePath(BASEPATH_REGIONS);
		List<String> aResult = categoryConverter.convert(CATEGORYNAME_UNKNOWN);
		assertNotNull(aResult);
		assertEquals(0, aResult.size());
	}
	
	@Test
	public void testConvertBaseUnknownPathAndName() throws ConversionException {
		categoryConverter.setIgnoreUnknown(true);
		categoryConverter.setBasePath(BASEPATH_UNKNOWN);
		List<String> aResult = categoryConverter.convert(CATEGORYNAME_FRANCE);
		assertNotNull(aResult);
		assertEquals(0, aResult.size());
	}
	
	@Test
	public void testConvertBasePathAndNullName() throws ConversionException {
		categoryConverter.setIgnoreUnknown(true);
		categoryConverter.setBasePath(BASEPATH_REGIONS);
		List<String> aResult = categoryConverter.convert(null);
		assertNotNull(aResult);
		assertEquals(0, aResult.size());
	}
	
	@Test
	public void testConvertNullBasePathAndNullName() throws ConversionException {
		categoryConverter.setIgnoreUnknown(true);
		categoryConverter.setBasePath(null);
		List<String> aResult = categoryConverter.convert(null);
		assertNotNull(aResult);
		assertEquals(0, aResult.size());
	}

	@Test
	public void testConvertBasePathAndUnknownNameDontIgnore() throws ConversionException {
		categoryConverter.setIgnoreUnknown(false);
		categoryConverter.setBasePath(BASEPATH_REGIONS);
		List<String> aResult = categoryConverter.convert(CATEGORYNAME_UNKNOWN);
		assertNotNull(aResult);
		assertEquals(1, aResult.size());
		assertEquals(CATEGORYNAME_UNKNOWN, aResult.get(0));
	}
	
	@Test
	public void testConvertBaseUnknownPathAndNameDontIgnore() throws ConversionException {
		categoryConverter.setIgnoreUnknown(false);
		categoryConverter.setBasePath(BASEPATH_UNKNOWN);
		List<String> aResult = categoryConverter.convert(CATEGORYNAME_FRANCE);
		assertNotNull(aResult);
		assertEquals(1, aResult.size());
		assertEquals(CATEGORYNAME_FRANCE, aResult.get(0));
	}
	
	@Test
	public void testConvertBasePathAndNullNameDontIgnore() throws ConversionException {
		categoryConverter.setIgnoreUnknown(false);
		categoryConverter.setBasePath(BASEPATH_REGIONS);
		List<String> aResult = categoryConverter.convert(null);
		assertNotNull(aResult);
		assertEquals(1, aResult.size());
		assertEquals(null, aResult.get(0));
	}
	
	@Test
	public void testConvertNullBasePathAndNullNameDontIgnore() throws ConversionException {
		categoryConverter.setIgnoreUnknown(false);
		categoryConverter.setBasePath(null);
		List<String> aResult = categoryConverter.convert(null);
		assertNotNull(aResult);
		assertEquals(1, aResult.size());
		assertEquals(null, aResult.get(0));
	}

}
