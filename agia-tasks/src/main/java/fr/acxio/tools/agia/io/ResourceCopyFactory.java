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
 
import java.io.File;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class ResourceCopyFactory implements ResourceFactory {

	private Resource parentFolder;
	private String prefix;
	private String suffix;
	private String extension;
	
	public void setParentFolder(Resource sParentFolder) {
		parentFolder = sParentFolder;
	}

	public void setPrefix(String sPrefix) {
		prefix = sPrefix;
	}

	public void setSuffix(String sSuffix) {
		suffix = sSuffix;
	}

	public void setExtension(String sExtension) {
		extension = sExtension;
	}

	@Override
	public Resource getResource() throws ResourceCreationException {
		return null;
	}

	@Override
	public Resource getResource(Map<? extends Object, ? extends Object> sParameters) throws ResourceCreationException {
		Resource aResult = null;
		try {
			Resource aSourceResource = (Resource) sParameters.get("SOURCE");
			String aFilename = aSourceResource.getFilename();
			String aFileNameWOExtension = aFilename.substring(0, aFilename.lastIndexOf('.'));
			String aExtension = extension;
			if (aExtension == null) {
				aExtension = aFilename.substring(aFilename.lastIndexOf('.'));
			}
			File aParentPath = (parentFolder != null) ? parentFolder.getFile() : null;
			if (aParentPath == null) {
				aParentPath = aSourceResource.getFile().getParentFile();
			}
			StringBuilder aNewFilename = new StringBuilder();
			if (prefix != null) {
				aNewFilename.append(prefix);
			}
			aNewFilename.append(aFileNameWOExtension);
			if (suffix != null) {
				aNewFilename.append(suffix);
			}
			aNewFilename.append(aExtension);
			File aNewFile = new File(aParentPath, aNewFilename.toString());
			aResult = new FileSystemResource(aNewFile);
		} catch (Exception e) {
			throw new ResourceCreationException(e);
		}
		return aResult;
	}

}
