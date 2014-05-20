package fr.acxio.tools.agia.io;

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
 
import java.util.Map;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * <p>A simple filesystem resource factory using a prefix, a suffix and a
 * date format to build the descriptor.</p>
 * 
 * @author pcollardez
 *
 */
public class ChronoFileSystemResourceFactory implements ResourceFactory, InitializingBean {

	private String prefix;
	private String suffix;
	private String dateFormat;
	
	public ChronoFileSystemResourceFactory(String sPrefix, String sDateFormat, String sSuffix) {
		prefix = sPrefix;
		suffix = sSuffix;
		dateFormat = sDateFormat;
	}

	public void setPrefix(String sPrefix) {
		prefix = sPrefix;
	}

	public void setSuffix(String sSuffix) {
		suffix = sSuffix;
	}

	public void setDateFormat(String sDateFormat) {
		dateFormat = sDateFormat;
	}

	public void afterPropertiesSet() {
		Assert.hasText(dateFormat, "The date format must not be empty.");
	}

	public Resource getResource() throws ResourceCreationException {
		FileSystemResource aFileSystemResource = null;
		try {
			DateTimeFormatter aFormatter = DateTimeFormat.forPattern(dateFormat);
			StringBuilder aFilename = new StringBuilder();
			aFilename.append(prefix).append(aFormatter.print(new Instant())).append(suffix);
			aFileSystemResource = new FileSystemResource(aFilename.toString());
		} catch (Exception e) {
			throw new ResourceCreationException(e);
		}
		return aFileSystemResource;
	}

	public Resource getResource(Map<? extends Object, ? extends Object> sParameters) throws ResourceCreationException {
		return getResource();
	}

}
