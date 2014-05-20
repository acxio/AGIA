package fr.acxio.tools.agia.cmis;

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
 
import java.util.Calendar;
import java.util.Iterator;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import fr.acxio.tools.agia.convert.DateToStringConverter;

public class CmisReader implements ItemStreamReader<QueryResult> {
	
	private static Logger logger = LoggerFactory.getLogger(CmisReader.class);
	
	private static final String CONTEXT_KEY_LASTTIMESTAMP = "cmis.reader.lastTimestamp";
	private static final DateToStringConverter CONVERTER_TIMESTAMP = new DateToStringConverter("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
	
	private CmisService cmisService;
	private String query;
	private OperationContext operationContext;
	
	private String lastTimestamp;
	
	private ItemIterable<QueryResult> currentQueryResult;
	private Iterator<QueryResult> unreadObjects;

	public void setCmisService(CmisService sCmisService) {
		cmisService = sCmisService;
	}

	public void setQuery(String sQuery) {
		query = sQuery;
	}

	public void setOperationContext(OperationContext sOperationContext) {
		operationContext = sOperationContext;
	}

	@Override
	public void open(ExecutionContext sExecutionContext) throws ItemStreamException {
		cmisService.startSession();
		
		lastTimestamp = sExecutionContext.getString(CONTEXT_KEY_LASTTIMESTAMP, null);
		
		executeQuery();
	}

	@Override
	public void update(ExecutionContext sExecutionContext) throws ItemStreamException {
		sExecutionContext.putString(CONTEXT_KEY_LASTTIMESTAMP, lastTimestamp);
	}

	@Override
	public void close() throws ItemStreamException {
		unreadObjects = null;
		lastTimestamp = null;
		currentQueryResult = null;
	}

	@Override
	public QueryResult read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		QueryResult aResult = null;

		if (unreadObjects.hasNext()) {
			aResult = unreadObjects.next();
			if (aResult != null) {
				lastTimestamp = CONVERTER_TIMESTAMP.convert((Calendar)aResult.getPropertyByQueryName("cmis:creationDate").getFirstValue()).get(0);
			} else {
				executeQuery();
				aResult = read();
			}
		} else if (currentQueryResult.getHasMoreItems()) {
			executeQuery();
			aResult = read();
		}
		
		return aResult;
	}
	
	private void executeQuery() {
		StringBuilder aQuery = new StringBuilder(query);
		if (lastTimestamp != null) {
			aQuery.append(" and cmis:creationDate > TIMESTAMP '");
			aQuery.append(lastTimestamp);
			aQuery.append("'");
		}
		aQuery.append("order by cmis:creationDate");
		
		if (logger.isDebugEnabled()) {
			logger.debug("Execute query : " + aQuery.toString());
		}
		
		currentQueryResult = cmisService.getSession().query(aQuery.toString(), false, operationContext);
		unreadObjects = currentQueryResult.iterator();
	}

}
