package fr.acxio.tools.agia.tasks;

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
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.io.ResourceFactory;
import fr.acxio.tools.agia.io.ResourceFactoryConstants;
import fr.acxio.tools.agia.io.ResourcesFactory;

public class ZipFilesTasklet implements Tasklet, InitializingBean {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipFilesTasklet.class);
    
    protected Resource sourceBaseDirectory;
    protected ResourcesFactory sourceFactory;
    protected ResourceFactory destinationFactory;
    protected boolean recursive = true;
    protected String sourceBaseDirectoryPath;

    public void setSourceBaseDirectory(Resource sSourceBaseDirectory) {
        // FIXME : Root base dir like C:\ will not work correctly because the last \ is not stripped by File.getCanonicalPath()
        sourceBaseDirectory = sSourceBaseDirectory;
    }

    public void setSourceFactory(ResourcesFactory sSourceFactory) {
        sourceFactory = sSourceFactory;
    }
    
    public void setDestinationFactory(ResourceFactory sDestinationFactory) {
        destinationFactory = sDestinationFactory;
    }
    
    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean sRecursive) {
        recursive = sRecursive;
    }
    
    public void afterPropertiesSet() {
        Assert.notNull(sourceBaseDirectory, "Source base directory must be set. It is available has BASEDIR in the SourceFactory parameters.");
        Assert.notNull(sourceFactory, "SourceFactory must be set");
        Assert.notNull(destinationFactory, "DestinationFactory must be set");
    }

    @Override
    public RepeatStatus execute(StepContribution sContribution, ChunkContext sChunkContext) throws Exception {
        
        // 1. Destination exists
        //    a. Overwrite => default behaviour
        //    b. Update => copy to temporary file, open, read entries, merge with new entries, write merged entries and stream
        // 2. New destination => default behaviour
        
        Map<String, Object> aSourceParams = new HashMap<String, Object>();
        aSourceParams.put(ResourceFactoryConstants.PARAM_STEP_EXEC, ((sChunkContext != null) && (sChunkContext.getStepContext() != null)) ? sChunkContext
                .getStepContext().getStepExecution() : null);
        aSourceParams.put(ResourceFactoryConstants.PARAM_BASE_DIRECTORY, sourceBaseDirectory);
        Resource[] aSourceResources = sourceFactory.getResources(aSourceParams);
        Map<String, Object> aDestinationParams = new HashMap<String, Object>();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("{} file(s) to zip", aSourceResources.length);
        }
        
        if (aSourceResources.length > 0) {
            
            aDestinationParams.put(ResourceFactoryConstants.PARAM_BASE_DIRECTORY, sourceBaseDirectory);
            aDestinationParams.put(ResourceFactoryConstants.PARAM_STEP_EXEC,
                    ((sChunkContext != null) && (sChunkContext.getStepContext() != null)) ? sChunkContext.getStepContext().getStepExecution() : null);
            Resource aDestination = destinationFactory.getResource(aDestinationParams);
            
            ZipArchiveOutputStream aZipArchiveOutputStream = null;
            try {
                aZipArchiveOutputStream = new ZipArchiveOutputStream(aDestination.getFile());
                
                sourceBaseDirectoryPath = sourceBaseDirectory.getFile().getCanonicalPath();
                
                for (Resource aSourceResource : aSourceResources) {
                    zipResource(aSourceResource, aZipArchiveOutputStream, sContribution, sChunkContext);
                }
            } finally {
                if (aZipArchiveOutputStream != null) {
                    aZipArchiveOutputStream.finish();
                    aZipArchiveOutputStream.close();
                }
            }
        }
        
        return RepeatStatus.FINISHED;
    }

    protected void zipResource(Resource sSourceResource, ZipArchiveOutputStream sZipArchiveOutputStream, StepContribution sContribution, ChunkContext sChunkContext) throws IOException, ZipFilesException {
        // TODO : use a queue to reduce the callstack overhead
        if (sSourceResource.exists()) {
            File aSourceFile = sSourceResource.getFile();
            String aSourcePath = aSourceFile.getCanonicalPath();
            
            if (!aSourcePath.startsWith(sourceBaseDirectoryPath)) {
                throw new ZipFilesException("Source file " + aSourcePath + " does not match base directory " + sourceBaseDirectoryPath);
            }
            
            if (sContribution != null) {
                sContribution.incrementReadCount();
            }
            String aZipEntryName = aSourcePath.substring(sourceBaseDirectoryPath.length() + 1);
            sZipArchiveOutputStream.putArchiveEntry(new ZipArchiveEntry(aZipEntryName));
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Zipping {} to {}", sSourceResource.getFile().getCanonicalPath(), aZipEntryName);
            }
            if (aSourceFile.isFile()) {
                InputStream aInputStream = sSourceResource.getInputStream();
                IOUtils.copy(aInputStream, sZipArchiveOutputStream);
                aInputStream.close();
                sZipArchiveOutputStream.closeArchiveEntry();
            } else {
                sZipArchiveOutputStream.closeArchiveEntry();
                for(File aFile : aSourceFile.listFiles((FileFilter)(recursive ? TrueFileFilter.TRUE : FileFileFilter.FILE))) {
                    zipResource(new FileSystemResource(aFile), sZipArchiveOutputStream, sContribution, sChunkContext);
                }
            }
            if (sContribution != null) {
                sContribution.incrementWriteCount(1);
            }
        } else if (LOGGER.isInfoEnabled()) {
                LOGGER.info("{} does not exist", sSourceResource.getFilename());
        }
    }

}
