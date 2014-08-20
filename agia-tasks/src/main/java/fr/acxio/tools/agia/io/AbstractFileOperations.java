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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.core.io.Resource;

import fr.acxio.tools.agia.tasks.FileOperationException;

public abstract class AbstractFileOperations {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFileOperations.class);
    
    private static final String SAME_RELATIVE_PATH = "/.";

    public enum Operation {
        COPY, MOVE, REMOVE
    }

    protected Operation operation;
    protected ResourceFactory destinationFactory;
    protected boolean preserveAttributes = false;
    protected boolean recursive = false;

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation sOperation) {
        operation = sOperation;
    }
    
    public void setDestinationFactory(ResourceFactory sDestinationFactory) {
        destinationFactory = sDestinationFactory;
    }

    public boolean isPreserveAttributes() {
        return preserveAttributes;
    }

    public void setPreserveAttributes(boolean sPreserveAttributes) {
        preserveAttributes = sPreserveAttributes;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean sRecursive) {
        recursive = sRecursive;
    }
    
    protected void moveFile(Resource sOriginFile, Resource sDestinationFile) throws IOException {
        File aOrigineFile = sOriginFile.getFile();
        if (aOrigineFile.isFile()) {
            if (!isDirectory(sDestinationFile)) {
                FileUtils.moveFile(aOrigineFile, sDestinationFile.getFile());
            } else {
                FileUtils.moveFileToDirectory(aOrigineFile, sDestinationFile.getFile(), true);
            }
        } else {
            FileUtils.moveDirectoryToDirectory(aOrigineFile, sDestinationFile.getFile(), true);
        }
    }

    protected void copyFile(Resource sOriginFile, Resource sDestinationFile) throws IOException {
        File aOrigineFile = sOriginFile.getFile();
        if (aOrigineFile.isFile()) {
            if (!isDirectory(sDestinationFile)) {
                FileUtils.copyFile(aOrigineFile, sDestinationFile.getFile(), preserveAttributes);
            } else {
                FileUtils.copyFileToDirectory(aOrigineFile, sDestinationFile.getFile(), preserveAttributes);
            }
        } else {
            if (recursive) {
                FileUtils.copyDirectory(aOrigineFile, sDestinationFile.getFile(), preserveAttributes);
            } else {
                FileUtils.copyDirectory(aOrigineFile, sDestinationFile.getFile(), FileFileFilter.FILE, preserveAttributes);
            }
        }
    }

    protected void removeFile(Resource sOriginFile) throws IOException {
        FileUtils.forceDelete(sOriginFile.getFile());
    }

    protected boolean isDirectory(Resource sResource) throws IOException {
        if (sResource.exists()) {
            return sResource.getFile().isDirectory();
        } else {
            // Dirty trick to tell if the resource is a directory or not when it does not exist yet
            return sResource.createRelative(SAME_RELATIVE_PATH).getFile().getCanonicalPath().equals(sResource.getFile().getCanonicalPath());
        }
    }
    
    protected List<String> doOperation(Resource aSourceResource, Map<String, Object> aDestinationParams, StepContribution sContribution, StepExecution sStepExecution)
            throws IOException, ResourceCreationException, FileOperationException {
        
        List<String> aDestinationFilesList = new ArrayList<String>();

        File aOriginFile = aSourceResource.getFile();
        if (aOriginFile.exists()) {
            if (sContribution != null) {
                sContribution.incrementReadCount();
            }

            if (operation == Operation.REMOVE) {

                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Deleting : {}", aOriginFile.getAbsolutePath());
                }

                removeFile(aSourceResource);
            } else {
                Resource aDestination = getDefaultDestination();
                if (destinationFactory != null) {
                    aDestinationParams.put(ResourceFactoryConstants.PARAM_SOURCE, aSourceResource);
                    aDestinationParams.put(ResourceFactoryConstants.PARAM_STEP_EXEC, sStepExecution);
                    aDestination = destinationFactory.getResource(aDestinationParams);
                }
                if ((aDestination != null) && (aDestination.getFile() != null)) {
                    File aDestinationFile = aDestination.getFile();
                    if (operation == Operation.COPY) {

                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("Copying : {} => {}", aOriginFile.getAbsolutePath(), aDestinationFile.getAbsolutePath());
                        }

                        copyFile(aSourceResource, aDestination);
                    } else if (operation == Operation.MOVE) {

                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("Moving : {} => {}", aOriginFile.getAbsolutePath(), aDestinationFile.getAbsolutePath());
                        }

                        moveFile(aSourceResource, aDestination);
                    } else {
                        throw new FileOperationException("Unknown operation");
                    }
                } else {
                    throw new FileOperationException("No destination specified");
                }
                aDestinationFilesList.add(aDestination.getFile().getCanonicalPath());
            }
            
            if (sContribution != null) {
                sContribution.incrementWriteCount(1);
            }
        } else {
            throw new FileOperationException("File not found: " + aOriginFile);
        }

        return aDestinationFilesList;
    }
    
    protected abstract Resource getDefaultDestination();
}
