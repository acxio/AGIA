package fr.acxio.tools.agia.file;

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
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.core.io.Resource;

import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.convert.AlfrescoPathToPathConverter;
import fr.acxio.tools.agia.convert.FormatConverter;
import fr.acxio.tools.agia.io.InputStreamFactory;

public class FileDirectoriesNodeWriter implements ItemStreamWriter<NodeList> {
	
	private static Logger logger = LoggerFactory.getLogger(FileDirectoriesNodeWriter.class);
	
	private static final FormatConverter pathConverter = new AlfrescoPathToPathConverter();
	
	private ItemWriter<? super NodeList> delegate; // Can be a CSV writer
	private Resource basePath;
	private InputStreamFactory<String> inputStreamFactory;
	
	public void setDelegate(ItemWriter<? super NodeList> sDelegate) {
		delegate = sDelegate;
	}

	public void setBasePath(Resource sBasePath) {
		basePath = sBasePath;
	}

	public void setInputStreamFactory(InputStreamFactory<String> sInputStreamFactory) {
		inputStreamFactory = sInputStreamFactory;
	}

	@Override
	public void write(List<? extends NodeList> sItems) throws Exception {
		if (!sItems.isEmpty()) {
			
			File aBaseDir = basePath.getFile();
			aBaseDir.mkdirs();
			
			for(NodeList aNodeList : sItems) { // each NodeList represents an input record
				for(Node aNode : aNodeList) {
					File aDestination = new File(aBaseDir, pathConverter.convert(aNode.getPath()).get(0)); // Must decode the value, because Alfresco paths are encoded
					if (aNode instanceof Document) {
						Document aDocument = (Document)aNode;
						if (logger.isDebugEnabled()) {
							logger.debug("Copy " + aDocument.getContentPath() + " to " + aDestination.getAbsolutePath());
						}
						
						FileOutputStream aOutputstream = null;
						InputStream aInputStream = null;
						try {
							aInputStream = inputStreamFactory.getInputStream(aDocument.getContentPath());
							aOutputstream = new FileOutputStream(aDestination);
							IOUtils.copy(aInputStream, aOutputstream);
						} finally {
							IOUtils.closeQuietly(aInputStream);
							IOUtils.closeQuietly(aOutputstream);
						}
						
						aDocument.setContentPath(aDestination.getPath()); // SIDE EFFECT : replace remote path by local path
						
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("Create dirs " + aDestination.getAbsolutePath());
						}
						aDestination.mkdir();
					}
				}
			}
			
			if (delegate != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Call write on delegate...");
				}
				delegate.write(sItems);
			}
			
		}
		
	}
	
	@Override
	public void open(ExecutionContext sExecutionContext)
			throws ItemStreamException {
		if (delegate instanceof ItemStream) {
			((ItemStream) delegate).open(sExecutionContext);
		}
	}

	@Override
	public void update(ExecutionContext sExecutionContext)
			throws ItemStreamException {
		if (delegate instanceof ItemStream) {
			((ItemStream) delegate).update(sExecutionContext);
		}
	}

	@Override
	public void close() throws ItemStreamException {
		if (delegate instanceof ItemStream) {
			((ItemStream) delegate).close();
		}
	}

}
