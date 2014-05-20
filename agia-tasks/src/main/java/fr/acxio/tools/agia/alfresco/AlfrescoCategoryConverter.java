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
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSet;
import org.alfresco.webservice.types.ResultSetRow;
import org.alfresco.webservice.util.ISO9075;
import org.alfresco.webservice.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import fr.acxio.tools.agia.alfresco.domain.QName;
import fr.acxio.tools.agia.convert.ConversionException;
import fr.acxio.tools.agia.convert.FormatConverter;

/**
 * <p>Specific value format converter which converts a value into an
 * Alfresco category reference.</p>
 * <p>
 * It takes path or names for categories, using "/" as a path separator.</br>
 * It can take either a full path without any specific encoding,
 * or a xpath base path and a name.</br>
 * </p><p>
 * An example of a full path would be:</br>
 * {@code /cm:generalclassifiable/cm:Regions/cm:EUROPE/cm:Western Europe/cm:France}</br>
 * </br>
 * An example of a base path and a name would be:</br>
 * {@code /cm:generalclassifiable/cm:Regions//*}</br>
 * {@code France}</br>
 * </p><p>
 * The full path or the name are the input value to be formatted.</br>
 * The base path, if any, is defined by the attribute {@code basepath}.
 * </p><p>
 * Note that the path elements can use the short or the long representation of
 * {@link fr.acxio.tools.agia.alfresco.domain.QName qualified names}.
 * The examples above use the short one.</br>
 * Being able to use both representations relies on the namespaceContext.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class AlfrescoCategoryConverter extends AlfrescoServicesConsumer implements FormatConverter {
	
	private static final String PATH_SPLIT_REGEX = "(/*)((?:\\{[^}]*})?[^/]*)";
	private static final Pattern PATH_SPLIT_PATTERN = Pattern.compile(PATH_SPLIT_REGEX);
	private static final String FULLPATH_QUERY = "+PATH:\"%s\"";
	private static final String BASEPATH_AND_NAME_QUERY = "+TYPE:\"cm:category\" +@cm\\:name:\"%s\" +PATH:\"%s\"";
	
	private static Logger logger = LoggerFactory.getLogger(AlfrescoCategoryConverter.class);
	
	private NamespaceContext namespaceContext;
	private String basePath;
	private boolean ignoreUnknown = true;
	
	public String getBasePath() {
		return basePath;
	}

	@CacheEvict(value="rcategories", allEntries=true)
	public void setBasePath(String sBasePath) {
		if (logger.isDebugEnabled()) {
			logger.debug("Evict all Alfresco categories");
		}
		basePath = sBasePath;
	}

	public NamespaceContext getNamespaceContext() {
		return namespaceContext;
	}

	@CacheEvict(value="rcategories", allEntries=true)
	public void setNamespaceContext(NamespaceContext sNamespaceContext) {
		if (logger.isDebugEnabled()) {
			logger.debug("Evict all Alfresco categories");
		}
		namespaceContext = sNamespaceContext;
	}

	public boolean isIgnoreUnknown() {
		return ignoreUnknown;
	}

	public void setIgnoreUnknown(boolean sIgnoreUnknown) {
		ignoreUnknown = sIgnoreUnknown;
	}
	
	@Cacheable(value="rcategories")
	public List<String> convert(String sSource) throws ConversionException {
		if (logger.isDebugEnabled()) {
			logger.debug("Calling Alfresco for category: " + sSource);
		}
		List<String> aResult = (List<String>) (ignoreUnknown ? Collections.emptyList() : Collections.singletonList(sSource));
		if ((sSource != null) && sSource.length() > 0) {
			try {
				init();
				
				String aQuery;
				
				if ((basePath == null) || (basePath.length() == 0)) {
					// sSource is a fullpath
					aQuery = String.format(FULLPATH_QUERY, encodePath(sSource));
				} else {
					aQuery = String.format(BASEPATH_AND_NAME_QUERY, sSource, encodePath(basePath));
				}
				
				RepositoryServiceSoapBindingStub repositoryService = getAlfrescoService().getRepositoryService();
				Query aLQuery = new Query("lucene", aQuery);
				
				ResultSet rs = repositoryService.query(STORE, aLQuery, false).getResultSet();
				if (rs.getTotalRowCount() > 0) {
					aResult = new ArrayList<String>();
					for(ResultSetRow aRSRow : rs.getRows()) {
						aResult.add(Utils.getNodeRef(new Reference(STORE, aRSRow.getNode().getId(), null)));
					}
				} else {
					logger.info("Category not found: " + sSource);
				}
				
			} catch (Exception e) {
				throw new ConversionException("Error retrieving the category: " + sSource, e);
			} finally {
				cleanup();
			}
		}
		return aResult;
	}

	/**
	 * Encode a path according to ISO 9075.</br>
	 * Each part of the path is encoded by itself, the path separators are preserved.</br>
	 * A part of a path is a
	 * {@link fr.acxio.tools.agia.alfresco.domain.QName QName}, therefore it
	 * can use the short or the long representation. 
	 * @param sPath a path to encode
	 * @return the encoded path
	 */
	private String encodePath(String sPath) {
		String aResult = sPath;
		if ((sPath != null) && (sPath.length() > 0)) {
			StringBuilder aBuilder = new StringBuilder();
			Matcher aMatcher = PATH_SPLIT_PATTERN.matcher(sPath);
			QName aQName;
			String aGroup2;
			while (aMatcher.find()) {
				if (aMatcher.group(1) != null) {
					aBuilder.append(aMatcher.group(1));
				}
				aGroup2 = aMatcher.group(2);
				if ((aGroup2 != null) && (aGroup2.length() > 0)) {
					try {
						aQName = new QName(aGroup2, namespaceContext);
						if (aQName.getPrefix().equals(XMLConstants.DEFAULT_NS_PREFIX) && aQName.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
							aBuilder.append(aGroup2); // Not a qname so just pass it
						} else {
							aBuilder.append(aQName.getPrefix()).append(":").append(ISO9075.encode(aQName.getLocalName()));
						}
					} catch (IllegalArgumentException e) {
						aBuilder.append(aGroup2);
					}
				}
			}
			aResult = aBuilder.toString();
			if (logger.isDebugEnabled()) {
				logger.debug("The path '" + sPath + "' is encoded as '" + aResult + "'");
			}
		}
		return aResult;
	}
}
