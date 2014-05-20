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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.io.ResourceCreationException;
import fr.acxio.tools.agia.io.ResourceFactory;

/**
 * <p>Specific file manipulation tasklet based on the TrueZip library.</p>
 * 
 * @author pcollardez
 *
 */
public class FileOperationTasklet implements Tasklet, InitializingBean {
	
	private static Logger logger = LoggerFactory.getLogger(FileOperationTasklet.class);

	public enum Operation {
		COPY,
		MOVE,
		REMOVE
	}
	
	private Operation operation;
	private Resource origin;
	private Resource destination;
	private ResourceFactory destinationFactory;
	private boolean preserveAttributes = false;
	private boolean recursive = false;
	
	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation sOperation) {
		operation = sOperation;
	}

	public Resource getOrigin() {
		return origin;
	}

	public void setOrigin(Resource sOrigin) {
		origin = sOrigin;
	}

	public Resource getDestination() {
		return destination;
	}

	public void setDestination(Resource sDestination) {
		destination = sDestination;
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

	public void afterPropertiesSet() {
		Assert.notNull(origin, "Origin must be set");
		if ((operation != Operation.REMOVE) && (destinationFactory == null)) {
			Assert.notNull(destination, "Destination must be set");
		}
		// TODO : add more tests
	}
	
	public RepeatStatus execute(StepContribution sContribution,	ChunkContext sChunkContext) throws IOException, ResourceCreationException, FileOperationException {
		File aOriginFile = origin.getFile();
		if (aOriginFile.exists()) {
			if (operation == Operation.REMOVE) {
				
				if (logger.isInfoEnabled()) {
					logger.info("Deleting : {}", aOriginFile.getAbsolutePath());
				}
				
				removeFile(aOriginFile);
			} else {
				if (destinationFactory != null) {
					destination = destinationFactory.getResource();
				}
				if (destination != null) {
					File aDestinationFile = destination.getFile();
					if (operation == Operation.COPY) {
						
						if (logger.isInfoEnabled()) {
							logger.info("Copying : {} => {}", aOriginFile.getAbsolutePath(), aDestinationFile.getAbsolutePath());
						}
						
						copyFile(aOriginFile, aDestinationFile);
					} else if (operation == Operation.MOVE) {
						
						if (logger.isInfoEnabled()) {
							logger.info("Moving : {} => {}", aOriginFile.getAbsolutePath(), aDestinationFile.getAbsolutePath());
						}
						
						moveFile(aOriginFile, aDestinationFile);
					} else {
						throw new FileOperationException("Unknown operation");
					}
				} else {
					throw new FileOperationException("No destination specified");
				}
			}
		} else {
			throw new FileOperationException("File not found: " + origin);
		}
		return RepeatStatus.FINISHED;
	}

	protected void moveFile(File sOriginFile, File sDestinationFile)
			throws IOException {
		
		if (sOriginFile.isFile()) {
			if (sDestinationFile.isFile()) {
				FileUtils.moveFile(sOriginFile, sDestinationFile);				
			} else {
				FileUtils.moveFileToDirectory(sOriginFile, sDestinationFile, true);
			}
		} else {
			FileUtils.moveDirectoryToDirectory(sOriginFile, sDestinationFile, true);
		}
	}

	protected void copyFile(File sOriginFile, File sDestinationFile)
			throws IOException {
		if (sOriginFile.isFile() && sDestinationFile.isFile()) {
				FileUtils.copyFile(sOriginFile, sDestinationFile, preserveAttributes);				
		} else if (recursive) {
			if (sOriginFile.isFile()) {
				FileUtils.copyFileToDirectory(sOriginFile, sDestinationFile, preserveAttributes);
			} else {
				FileUtils.copyDirectory(sOriginFile, sDestinationFile, preserveAttributes);
			}
		} else {
			FileUtils.copyDirectory(sOriginFile, sDestinationFile, FileFileFilter.FILE, preserveAttributes);
		}
	}

	protected void removeFile(File sOriginFile) throws IOException {
		FileUtils.forceDelete(sOriginFile);
	}
}
