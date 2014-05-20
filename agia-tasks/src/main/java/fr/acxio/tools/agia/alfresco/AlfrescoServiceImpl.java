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
 
import java.rmi.RemoteException;

import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.WebServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;

/**
 * <p>Wrapper for Alfresco webservices client API.</p>
 * <p>The purpose of this wrapper is to make the API available as a bean in
 * a Spring context, because the API is mainly made of static classes and
 * methods.</p>
 * 
 * @author pcollardez
 *
 */
@Service
public class AlfrescoServiceImpl implements AlfrescoService {
	
	private static Logger logger = LoggerFactory.getLogger(AlfrescoServiceImpl.class);
	
	/**
	 * WebServices URL
	 */
	private String endpointAddress;
	
	private String webappAddress;
	
	private String username;
	private String password;
	private long timeOutInterval = 3000000; // 50 min, server default to 1 hour
		
	public String getEndpointAddress() {
		return endpointAddress;
	}

	public void setEndpointAddress(String sEndpointAddress) {
		endpointAddress = sEndpointAddress;
	}

	public String getWebappAddress() {
		return webappAddress;
	}

	public void setWebappAddress(String sWebappAddress) {
		webappAddress = sWebappAddress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String sUsername) {
		username = sUsername;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String sPassword) {
		password = sPassword;
	}

	public long getTimeOutInterval() {
		return timeOutInterval;
	}

	public void setTimeOutInterval(long sTimeOutInterval) {
		timeOutInterval = sTimeOutInterval;
	}

	/**
	 * <p>Starts a new Alfresco session with the provided endpoint address,
	 * username and password.</p>
	 * <p>This implementation checks if a ticket is available into the static
	 * cache, and if so, re-use it instead of creating a new session.</p>
	 * 
	 * @throws RemoteException if the session cannot be started
	 */
	public void startSession() throws RemoteException {
		if ((AuthenticationUtils.getTicket() == null) || AuthenticationUtils.isCurrentTicketTimedOut()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Starting new Alfresco session...");
			}
			WebServiceFactory.setEndpointAddress(getEndpointAddress());
			AuthenticationUtils.startSession(getUsername(), getPassword(), getTimeOutInterval());
		}
	}
	
	/**
	 * <p>Puts an end to the session.</p>
	 * <p>Actually does nothing and let the session timeout.</p>
	 */
	public void endSession() {
	}
	
	public String getTicket() {
		return AuthenticationUtils.getTicket();
	}
	
	public Sardine startWebDavSession() {
		Sardine aSardine = SardineFactory.begin();
		aSardine.setCredentials(getUsername(), getPassword());
		return aSardine;
	}
	
	/**
	 * <p>Repository service accessor.</p>
	 * 
	 * @return the repository service stub.
	 */
	public RepositoryServiceSoapBindingStub getRepositoryService() {
		return WebServiceFactory.getRepositoryService();
	}
	
	public ContentServiceSoapBindingStub getContentService() {
		return WebServiceFactory.getContentService();
	}
}
