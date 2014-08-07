package fr.acxio.tools.agia.common;

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
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * <p>
 * Quartz job details for a Spring Batch job.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class JobLauncherDetails extends QuartzJobBean {

    /**
     * Special key in job data map for the name of a job to run.
     */
    static final String JOB_NAME = "jobName";

    private static final Logger LOGGER = LoggerFactory.getLogger(JobLauncherDetails.class);

    private JobLocator jobLocator;

    private JobLauncher jobLauncher;

    private JobExplorer jobExplorer;

    /**
     * Public setter for the
     * {@link org.springframework.batch.core.configuration.JobLocator
     * JobLocator}.
     * 
     * @param jobLocator
     *            the JobLocator to set
     */
    public void setJobLocator(JobLocator jobLocator) {
        this.jobLocator = jobLocator;
    }

    /**
     * Public setter for the
     * {@link org.springframework.batch.core.launch.JobLauncher JobLauncher}.
     * 
     * @param jobLauncher
     *            the JobLauncher to set
     */
    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public void setJobExplorer(JobExplorer sJobExplorer) {
        jobExplorer = sJobExplorer;
    }

    @SuppressWarnings("unchecked")
    protected void executeInternal(JobExecutionContext context) {
        Map<String, Object> jobDataMap = context.getMergedJobDataMap();
        String jobName = (String) jobDataMap.get(JOB_NAME);
        LOGGER.info("Quartz trigger firing with Spring Batch jobName=" + jobName);

        try {
            Job job = jobLocator.getJob(jobName);

            JobParameters previousJobParameters = null;
            List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 1);
            if ((jobInstances != null) && (jobInstances.size() > 0)) {
                previousJobParameters = jobInstances.get(0).getJobParameters();
            }

            JobParameters jobParameters = getJobParametersFromJobMap(jobDataMap, previousJobParameters);

            if (job.getJobParametersIncrementer() != null) {
                jobParameters = job.getJobParametersIncrementer().getNext(jobParameters);
            }

            jobLauncher.run(jobLocator.getJob(jobName), jobParameters);
        } catch (JobExecutionException e) {
            LOGGER.error("Could not execute job.", e);
        }
    }

    /**
     * Copy parameters that are of the correct type over to
     * {@link org.springframework.batch.core.launch.JobLauncher JobParameters},
     * ignoring jobName.
     * 
     * @return a JobParameters instance
     */
    private JobParameters getJobParametersFromJobMap(Map<String, Object> jobDataMap, JobParameters sPreviousJobParameters) {

        JobParametersBuilder builder = (sPreviousJobParameters != null) ? new JobParametersBuilder(sPreviousJobParameters) : new JobParametersBuilder();

        for (Entry<String, Object> entry : jobDataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String && !key.equals(JOB_NAME)) {
                builder.addString(key, (String) value);
            } else if (value instanceof Float || value instanceof Double) {
                builder.addDouble(key, ((Number) value).doubleValue());
            } else if (value instanceof Integer || value instanceof Long) {
                builder.addLong(key, ((Number) value).longValue());
            } else if (value instanceof Date) {
                builder.addDate(key, (Date) value);
            } else {
                LOGGER.debug("JobDataMap contains values which are not job parameters (ignoring).");
            }
        }

        return builder.toJobParameters();

    }

}
