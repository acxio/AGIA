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
 
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;

public class CmisServiceImpl implements CmisService {

	private Map<String, String> parameters;
	
	private Session session;
	
	public synchronized void setParameters(Map<String, String> sParameters) {
		parameters = sParameters;
		session = null;
	}

	@Override
	public synchronized Session startSession() {
		if (session == null) {
			SessionFactory aFactory = SessionFactoryImpl.newInstance();
			List<Repository> aRepositories = aFactory.getRepositories(parameters);
			session = aRepositories.get(0).createSession();
		}
		return session;
	}

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void endSession() {
	}

}
