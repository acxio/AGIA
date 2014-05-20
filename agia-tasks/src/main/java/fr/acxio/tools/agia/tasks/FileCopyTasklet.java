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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import fr.acxio.tools.agia.io.ResourceCreationException;
import fr.acxio.tools.agia.io.ResourceFactory;

/**
 * <p>Simple tasklet that copies a file from one path to another path.</p>
 * <p>This tasklet can raise an error if it cannot replace the destination.</p>
 * <p>It can also remove or empty the origin.</p>
 * 
 * @author pcollardez
 *
 */
public class FileCopyTasklet implements Tasklet, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(FileCopyTasklet.class);
	
	private Resource origin;
	
	private Resource destination;
	private ResourceFactory destinationFactory;
	
	private Boolean forceReplace = Boolean.TRUE;
	
	private Boolean deleteOrigin = Boolean.FALSE;
	
	private Boolean emptyOrigin = Boolean.FALSE;
	
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
	
	public Boolean getForceReplace() {
		return forceReplace;
	}

	public void setForceReplace(Boolean sForceReplace) {
		forceReplace = sForceReplace;
	}

	public Boolean getDeleteOrigin() {
		return deleteOrigin;
	}

	public void setDeleteOrigin(Boolean sDeleteOrigin) {
		deleteOrigin = sDeleteOrigin;
	}

	public Boolean getEmptyOrigin() {
		return emptyOrigin;
	}

	public void setEmptyOrigin(Boolean sEmptyOrigin) {
		emptyOrigin = sEmptyOrigin;
	}

	public void afterPropertiesSet() {
		Assert.notNull(origin, "Origin must be set");
		if (destinationFactory == null) {
			Assert.notNull(destination, "Destination must be set");
		}
		// TODO : add more tests
	}

	public RepeatStatus execute(StepContribution sContribution,	ChunkContext sChunkContext) throws ResourceCreationException, IOException, FileCopyException {
		if (destinationFactory != null) {
			destination = destinationFactory.getResource();
		}
		File aOriginFile = origin.getFile();
		if (aOriginFile.exists() && aOriginFile.isFile()) {
			File aDestinationFile = destination.getFile();
			if (aDestinationFile.exists()) {
				if (forceReplace && aDestinationFile.isFile()) {
					if (!aDestinationFile.delete()) {
						throw new FileCopyException("Cannot remove: " + destination);
					}
				} else {
					throw new FileCopyException("Cannot replace: " + destination);
				}
			}
			
			if (logger.isInfoEnabled()) {
				logger.info("Copying : {} => {}", aOriginFile.getAbsolutePath(), aDestinationFile.getAbsolutePath());
			}
			
			FileCopyUtils.copy(aOriginFile, aDestinationFile);
			
			if ((emptyOrigin || deleteOrigin) && !aOriginFile.delete()) {
				throw new FileCopyException("Cannot delete: " + origin);
			}
			if (emptyOrigin && !aOriginFile.createNewFile()) {
				throw new FileCopyException("Cannot create: " + origin);
			}
		} else {
			throw new FileCopyException("File not found: " + origin);
		}
		
		return RepeatStatus.FINISHED;
	}

}
