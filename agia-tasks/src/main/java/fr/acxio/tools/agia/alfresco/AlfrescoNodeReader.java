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
 
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;

import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.types.NamedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;

import fr.acxio.tools.agia.alfresco.domain.Aspect;
import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.Property;
import fr.acxio.tools.agia.alfresco.domain.QName;

public class AlfrescoNodeReader extends AlfrescoServicesConsumer implements ItemReader<Node>, ItemStream, StepExecutionListener, InitializingBean, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(AlfrescoNodeReader.class);
	
	private static final Pattern PATH_EXTRACT_PATTERN = Pattern.compile("^(?:(.*/)[^/]*/|(?:(.*/))?[^/]*)$");
	private static final Pattern CMCONTENT_PATTERN = Pattern.compile("([^|=]+)=([^|=]+)");
	
	private static final String CONTEXT_KEY_CURRENTINDEXES = "alfresco.reader.currentIndexdes";
	private static final String CONTEXT_KEY_CURRENTPATH = "alfresco.reader.currentPath";
	
	private static final String SUBPROP_ENCODING = "encoding";
	private static final String SUBPROP_CONTENT_URL = "contentUrl";
	private static final String SUBPROP_MIMETYPE = "mimetype";
	private static final String PROP_CM_CONTENT = "cm:content";
	private static final String WEBDAV_PATH = "webdav";

	private NamespaceContext namespaceContext;
	private DavResourcesResolver davResourcesResolver;
	private String path;

	private String currentDirPath;
	private Deque<Integer> currentIndexes;

	private Sardine sardine;
	private URI baseURI;

	public void setNamespaceContext(NamespaceContext sNamespaceContext) {
		namespaceContext = sNamespaceContext;
	}
	
	public void setDavResourcesResolver(DavResourcesResolver sDavResourcesResolver) {
		davResourcesResolver = sDavResourcesResolver;
	}

	public void setPath(String sPath) {
		path = sPath;
	}

	@Override
	public void destroy() throws Exception {
		currentDirPath = null;
		currentIndexes = null;
		sardine = null;
		baseURI = null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public void beforeStep(StepExecution sStepExecution) {
	}

	@Override
	public ExitStatus afterStep(StepExecution sStepExecution) {
		return ExitStatus.COMPLETED;
	}

	@Override
	public void open(ExecutionContext sExecutionContext)
			throws ItemStreamException {
		
		String aFullPath = null;
		
		try {
			baseURI = new URI(getAlfrescoService().getWebappAddress()).resolve(WEBDAV_PATH);
			aFullPath = getWebDavDirectoryURI(baseURI.getPath() + path).getPath();
		} catch (URISyntaxException e) {
			throw new ItemStreamException(e);
		}
		
		currentDirPath = sExecutionContext.getString(CONTEXT_KEY_CURRENTPATH, aFullPath);
		Object aCurrentIndexes = sExecutionContext.get(CONTEXT_KEY_CURRENTINDEXES);
		if (aCurrentIndexes == null) {
			currentIndexes = new ArrayDeque<Integer>();
			currentIndexes.addFirst(0);
		} else {
			Integer[] aArray = (Integer[])aCurrentIndexes;
			currentIndexes = new ArrayDeque<Integer>(Arrays.asList(aArray));
		}
		
		sardine = getAlfrescoService().startWebDavSession();
	}

	@Override
	public void update(ExecutionContext sExecutionContext)
			throws ItemStreamException {
		sExecutionContext.putString(CONTEXT_KEY_CURRENTPATH, currentDirPath);
		sExecutionContext.put(CONTEXT_KEY_CURRENTINDEXES, currentIndexes.toArray(new Integer[]{}));
	}

	@Override
	public void close() throws ItemStreamException {
	}

	protected URI getWebDavDirectoryURI(String sAbsolutePath) throws URISyntaxException {
		return new URI(baseURI.getScheme(), baseURI.getUserInfo(), baseURI.getHost(), baseURI.getPort(), sAbsolutePath, null, null);
	}
	
	@Override
	public Node read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		
		Node aResult = null;
		
		init();
		RepositoryServiceSoapBindingStub repositoryService = getAlfrescoService().getRepositoryService();
		
		List<DavResource> aResources = davResourcesResolver.getDirectoryList(sardine, getWebDavDirectoryURI(currentDirPath).toASCIIString());
		
		boolean isFolderItself;
		
		do {
			isFolderItself = false;
		
			boolean hasMore = !currentIndexes.isEmpty();
			int aLength = aResources.size();
			
			int aCurrentIndex = currentIndexes.removeFirst();
			
			if ((aLength == 0) || (aCurrentIndex >= aLength)) {
				// Go 1 step upper
				hasMore = !currentIndexes.isEmpty();
				
				if (hasMore) {
					aCurrentIndex = currentIndexes.removeFirst() + 1;
					
					Matcher aPathMatcher = PATH_EXTRACT_PATTERN.matcher(currentDirPath);
					if (aPathMatcher.matches()) {
						currentDirPath = (aPathMatcher.group(1) != null) ? aPathMatcher.group(1) : aPathMatcher.group(2);
					}
					
					aResources = davResourcesResolver.getDirectoryList(sardine, getWebDavDirectoryURI(currentDirPath).toASCIIString());
					
					isFolderItself = true; // FIXME : change the name of this variable
				}
			} else {
				DavResource aResource = aResources.get(aCurrentIndex);
				
				if (aResource.isDirectory()) {
					if (!currentDirPath.equals(aResource.getPath())) {
						
						if (logger.isDebugEnabled()) {
							logger.debug("Row " + aCurrentIndex + ": " + aResource.getPath());
						}
						
						String aPath = aResource.getPath().substring(baseURI.getPath().length());
						aResult = buildNode(repositoryService, aResource, aPath);
						
						// Go 1 step deeper
						currentDirPath = aResource.getPath();
						currentIndexes.addFirst(aCurrentIndex);
						aCurrentIndex = 0;
						aResources = davResourcesResolver.getDirectoryList(sardine, getWebDavDirectoryURI(currentDirPath).toASCIIString());
					} else {
						// Skip current dir (webdav node lists itself)
						aCurrentIndex++;
						isFolderItself = true;
					}
				} else {
					// Handle content node
					if (logger.isDebugEnabled()) {
						logger.debug("Row " + aCurrentIndex + ": " + aResource.getPath());
					}
					
					String aPath = aResource.getPath().substring(baseURI.getPath().length());
					aResult = buildNode(repositoryService, aResource, aPath);
					
					aCurrentIndex++;
				}
	
			}
	
			if (hasMore) {
				currentIndexes.addFirst(aCurrentIndex);
			}
			
		} while (isFolderItself);

		return aResult;
	}

	protected Node buildNode(
			RepositoryServiceSoapBindingStub repositoryService,
			DavResource aResource, String aPath) throws NodePathException {
		Node aResult;
		org.alfresco.webservice.types.Node[] aNodes = getRepositoryMatchingNodes(repositoryService, aPath);
		
		if (aResource.isDirectory()) {
			Folder aFolder = new Folder();
			aResult = aFolder;
		} else {
			Document aDocument = new Document();
			aResult = aDocument;
		}
		
		if ((aNodes != null) && (aNodes.length > 0)) {
			
			aResult.setType(new QName(aNodes[0].getType(), namespaceContext));
			
			for(NamedValue aProperty : aNodes[0].getProperties()) {
				Property aNodeProperty = new Property();
				aNodeProperty.setName(new QName(aProperty.getName(), namespaceContext));
				aNodeProperty.addValue(aProperty.getValue());
				aResult.addProperty(aNodeProperty);
				
				if ((PROP_CM_CONTENT.equals(aNodeProperty.getName().getShortName())) && (!aResource.isDirectory())) {
					Document aDocument = (Document)aResult;
					Map<String,String> aValues = readCMContent(aProperty.getValue());
					aDocument.setMimeType(aValues.get(SUBPROP_MIMETYPE));
//					aDocument.setContentPath(aValues.get(SUBPROP_CONTENT_URL));
					try {
						aDocument.setContentPath(getWebDavDirectoryURI(aResource.getPath()).toASCIIString());
					} catch (URISyntaxException e) {
						throw new NodePathException(e);
					}
					aDocument.setEncoding(aValues.get(SUBPROP_ENCODING));
				}
			}
			
			for(String aAspect : aNodes[0].getAspects()) {
				Aspect aNodeAspect = new Aspect();
				aNodeAspect.setName(new QName(aAspect, namespaceContext));
				aResult.addAspect(aNodeAspect);
			}
		}
		return aResult;
	}

	// 1. Query Nodes via WebDav => no limit on result, but file-like result, without any property (we may use a query if the size of a dir is < 1000)
	// 2. ProcessIndicator will use the NodeRef => Processor can mark node (really necessary ??)
	// 3. Store file index in the job => allow restart and continue (see sbia/ch08/FilesInDirectoryItemReader)
	// 4. NodeProcessor will aggregate nodes into NodeList (Hibernate / Alf / Drive) or tranform nodes into a FieldSet (CSV)
	
	protected Map<String, String> readCMContent(String sValue) {
		Map<String, String> aResult = new HashMap<String, String>(5);
		Matcher aMatcher = CMCONTENT_PATTERN.matcher(sValue);
		while (aMatcher.find()) {
			aResult.put(aMatcher.group(1), aMatcher.group(2));
		}
		return aResult;
	}
}
