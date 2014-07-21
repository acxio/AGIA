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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;

public class DefaultDavResourcesResolver implements DavResourcesResolver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDavResourcesResolver.class);
	
	private static final String CACHE_KEY = "#sPath";
	private static final String CACHE_NAME = "rdirlist";

	@Override
	@Cacheable(value=CACHE_NAME, key=CACHE_KEY)
	public List<DavResource> getDirectoryList(Sardine sSardine, String sPath) throws IOException {
		if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("Calling WebDav for path: " + sPath);
        }
		
		return sSardine.list(sPath);
	}

	@Override
	@CachePut(value=CACHE_NAME, key=CACHE_KEY)
	public List<DavResource> setLocalDirectoryList(String sPath,
			List<DavResource> sResources) {
		if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("Set local for WebDav path: " + sPath);
        }
		return sResources;
	}

	@Override
	@CacheEvict(value=CACHE_NAME)
	public void evictDirectoryList(String sPath) {
		if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("Evict WebDav path: " + sPath);
        }
	}

	@Override
	@CacheEvict(value=CACHE_NAME, allEntries=true)
	public void evictDictoriesLists() {
		if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("Evict all WebDav directories lists");
        }
	}

}
