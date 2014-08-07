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
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

public class FTPUploadTasklet extends FTPFileOperationTasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(FTPUploadTasklet.class);

    private String remoteBaseDir; // Factory ?
    private String regexFilename;
    private String localBaseDir; // Factory ?

    public void setRemoteBaseDir(String sRemoteBaseDir) {
        remoteBaseDir = sRemoteBaseDir;
    }

    public void setRegexFilename(String sRegexFilename) {
        regexFilename = sRegexFilename;
    }

    public void setLocalBaseDir(String sLocalBaseDir) {
        localBaseDir = sLocalBaseDir;
    }

    @Override
    public RepeatStatus execute(StepContribution sContribution, ChunkContext sChunkContext) throws Exception {
        FTPClient aClient = ftpClientFactory.getFtpClient();

        RegexFilenameFilter aFilter = new RegexFilenameFilter();
        aFilter.setRegex(regexFilename);

        try {
            File aLocalDir = new File(localBaseDir);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Listing : {} ({}) for upload to [{}]", localBaseDir, regexFilename, aClient.getRemoteAddress().toString());
            }

            File[] aLocalFiles = aLocalDir.listFiles(aFilter);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("  {} file(s) found", aLocalFiles.length);
            }

            for (File aLocalFile : aLocalFiles) {

                if (sContribution != null) {
                    sContribution.incrementReadCount();
                }

                URI aRemoteFile = new URI(remoteBaseDir).resolve(aLocalFile.getName());
                InputStream aInputStream;
                aInputStream = new FileInputStream(aLocalFile);
                try {

                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info(" Uploading : {} => {}", aLocalFile.getAbsolutePath(), aRemoteFile.toASCIIString());
                    }

                    aClient.storeFile(aRemoteFile.toASCIIString(), aInputStream);

                    if (sContribution != null) {
                        sContribution.incrementWriteCount(1);
                    }
                } finally {
                    aInputStream.close();
                }
            }
        } finally {
            aClient.logout();
            aClient.disconnect();
        }

        return RepeatStatus.FINISHED;
    }
}
