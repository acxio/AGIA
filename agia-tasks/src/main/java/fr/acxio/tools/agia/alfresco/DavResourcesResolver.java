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
 
import java.io.IOException;
import java.util.List;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;

public interface DavResourcesResolver {

	List<DavResource> getDirectoryList(Sardine sSardine, String sPath) throws IOException;
	
	List<DavResource> setLocalDirectoryList(String sPath, List<DavResource> sResources);
	
	void evictDirectoryList(String sPath);
	
	void evictDictoriesLists();
}
