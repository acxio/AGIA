package fr.acxio.tools.agia.ftp;

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
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

public class RegexFilenameFilter implements FTPFileFilter, FilenameFilter {

	private Pattern pattern;
	
	public void setRegex(String sRegex) {
		pattern = Pattern.compile(sRegex);
	}
	
	@Override
	public boolean accept(FTPFile sFile) {
		return pattern.matcher(sFile.getName()).matches();
	}

	@Override
	public boolean accept(File sDir, String sName) {
		return pattern.matcher(sName).matches();
	}

}
