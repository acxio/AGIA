package fr.acxio.tools.agia.admin;

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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;

/**
 * <p>Service dedicated to running jobs, setting them to FAILED when the
 * context is created.</p>
 * <p>The purpose of this service is to set jobs to a restartable state.</p>
 * 
 * @author pcollardez
 *
 */
public class StaleRunningJobsService {

	private static Logger logger = LoggerFactory.getLogger(StaleRunningJobsService.class);
	
	private JobExplorer jobExplorer;
	private JobRepository jobRepository;
	
	public void setJobExplorer(JobExplorer sJobExplorer) {
		jobExplorer = sJobExplorer;
	}

	public void setJobRepository(JobRepository sJobRepository) {
		jobRepository = sJobRepository;
	}
	
	public void forceRunningJobsToFail() {
		if (logger.isInfoEnabled()) {
			logger.info("Reseting jobs...");
		}
		
		List<String> aJobNames = jobExplorer.getJobNames();
		for(String aJobName : aJobNames) {
			Set<JobExecution> aJobExecutions = jobExplorer.findRunningJobExecutions(aJobName);
			for (JobExecution aJobExecution : aJobExecutions) {
				if (logger.isInfoEnabled()) {
					logger.info("  " + aJobName + " (" + aJobExecution.getId() + ")");
				}
				aJobExecution.setEndTime(new Date());
				aJobExecution.setStatus(BatchStatus.FAILED);
				aJobExecution.setExitStatus(ExitStatus.FAILED);
				jobRepository.update(aJobExecution);
				for(StepExecution aStepExecution : aJobExecution.getStepExecutions()) {
					if (aStepExecution.getStatus().isGreaterThan(BatchStatus.COMPLETED)) {
						if (logger.isInfoEnabled()) {
							logger.info("    " + aStepExecution.getStepName());
						}
						aStepExecution.setEndTime(new Date());
						aStepExecution.setStatus(BatchStatus.FAILED);
						aStepExecution.setExitStatus(ExitStatus.FAILED);
						jobRepository.update(aStepExecution);
					}
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("Done.");
		}
	}
}
