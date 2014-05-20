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
import java.io.FileOutputStream;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;


public class FTPDownloadTasklet extends FTPFileOperationTasklet {
	
	private static Logger logger = LoggerFactory.getLogger(FTPDownloadTasklet.class);
	
	private static final String SEPARATOR = "/";
	private static final Pattern PATH_PATTERN = Pattern.compile("^(/|.*[^/])/*$");

	private String remoteBaseDir; // Factory ?
	private String regexFilename;
	private String localBaseDir; // Factory ?
	private boolean removeRemote = false;
	
	public void setRemoteBaseDir(String sRemoteBaseDir) {
		remoteBaseDir = sRemoteBaseDir;
		
		Matcher aMatcher = PATH_PATTERN.matcher(sRemoteBaseDir);
		if (aMatcher.matches()) {
			remoteBaseDir = aMatcher.group(1);
		}
	}

	public void setRegexFilename(String sRegexFilename) {
		regexFilename = sRegexFilename;
	}

	public void setLocalBaseDir(String sLocalBaseDir) {
		localBaseDir = sLocalBaseDir;
	}

	public void setRemoveRemote(boolean sRemoveRemote) {
		removeRemote = sRemoveRemote;
	}

	@Override
	public RepeatStatus execute(StepContribution sArg0, ChunkContext sArg1) throws Exception {
		FTPClient aClient = ftpClientFactory.getFtpClient();
		
		RegexFilenameFilter aFilter = new RegexFilenameFilter();
		aFilter.setRegex(regexFilename);
		try {
			URI aRemoteBaseURI = new URI(remoteBaseDir);
			URI aRemoteBasePath = new URI(aRemoteBaseURI.toASCIIString() + SEPARATOR);
			
			if (logger.isInfoEnabled()) {
				logger.info("Listing : [{}] {} ({})", aClient.getRemoteAddress().toString(), aRemoteBaseURI.toASCIIString(), regexFilename);
			}
			
			FTPFile[] aRemoteFiles = aClient.listFiles(aRemoteBaseURI.toASCIIString(), aFilter);
			
			if (logger.isInfoEnabled()) {
				logger.info("  {} file(s) found", aRemoteFiles.length);
			}
			
			for(FTPFile aRemoteFile : aRemoteFiles) {
				File aLocalFile = new File(localBaseDir, aRemoteFile.getName());
				URI aRemoteTFile = aRemoteBasePath.resolve(aRemoteFile.getName());

				FileOutputStream aOutputStream = new FileOutputStream(aLocalFile);
				try {
					
					if (logger.isInfoEnabled()) {
						logger.info(" Downloading : {} => {}", aRemoteTFile.toASCIIString(), aLocalFile.getAbsolutePath());
					}
					
					aClient.retrieveFile(aRemoteTFile.toASCIIString(), aOutputStream);
					if (removeRemote) {
						
						if (logger.isInfoEnabled()) {
							logger.info(" Deleting : {}", aRemoteTFile.toASCIIString());
						}
						
						aClient.deleteFile(aRemoteTFile.toASCIIString());
					}
				} finally {
					aOutputStream.close();
				}
			}
		} finally {
			aClient.logout();
			aClient.disconnect();
		}
		
		return RepeatStatus.FINISHED;
	}

}
