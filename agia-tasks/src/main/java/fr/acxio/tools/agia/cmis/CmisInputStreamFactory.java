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
 
import java.io.IOException;
import java.io.InputStream;

import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;

import fr.acxio.tools.agia.io.InputStreamFactory;

public class CmisInputStreamFactory implements InputStreamFactory<String> {

	private CmisService cmisService;
	
	public void setCmisService(CmisService sCmisService) {
		cmisService = sCmisService;
	}

	@Override
	public InputStream getInputStream(String sObjectId) throws IOException {
		Session aSession = cmisService.startSession();
		ObjectId aObjectId = aSession.createObjectId(sObjectId);
		ContentStream aContentStream = aSession.getContentStream(aObjectId);
		return aContentStream.getStream();
	}

}
