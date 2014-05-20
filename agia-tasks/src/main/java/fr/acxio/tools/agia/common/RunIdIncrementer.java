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
 
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

/**
 * Simple incrementer maintaining the parameter {@code run.id} for a job.
 * <p>This incremeter will add the parameter {@code run.id} if it is not set for
 * the job, or it will increment it if the job already has this parameter.</p>
 *
 */
public class RunIdIncrementer implements JobParametersIncrementer {

	public JobParameters getNext(JobParameters sParameters) {
		if ((sParameters == null) || sParameters.isEmpty()) {
            return new JobParametersBuilder().addLong("run.id", 1L).toJobParameters();
        }
        long aId = sParameters.getLong("run.id", 1L) + 1;
        return new JobParametersBuilder().addLong("run.id", aId).toJobParameters();
	}

}
