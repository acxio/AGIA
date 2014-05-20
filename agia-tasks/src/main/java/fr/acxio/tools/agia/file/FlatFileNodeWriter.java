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
 
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;

import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;

public class FlatFileNodeWriter implements ItemStreamWriter<NodeList> {
	
	private ResourceAwareItemWriterItemStream<Node> delegate;

	public void setDelegate(ResourceAwareItemWriterItemStream<Node> sDelegate) {
		delegate = sDelegate;
	}

	@Override
	public void write(List<? extends NodeList> sItems) throws Exception {
		for(NodeList aNodeList : sItems) {
			delegate.write(aNodeList);
		}
	}

	@Override
	public void open(ExecutionContext sExecutionContext)
			throws ItemStreamException {
		delegate.open(sExecutionContext);
	}

	@Override
	public void update(ExecutionContext sExecutionContext)
			throws ItemStreamException {
		delegate.update(sExecutionContext);
	}

	@Override
	public void close() throws ItemStreamException {
		delegate.close();
	}

	
}
