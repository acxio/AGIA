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

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

/**
 * <p>
 * Sets the exit code {@code NOREAD} to the step if no items have been
 * processed (item count is 0).
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 *       &lt;batch:step id="someStep"&gt;
 *           &lt;batch:tasklet ref="someTasklet"/&gt;
 *           &lt;batch:end on="NOREAD" exit-code="COMPLETED"/&gt;
 *           &lt;batch:fail on="FAILED"/&gt;
 *           &lt;batch:next on="*" to="nextStep"/&gt;
 *           &lt;batch:listeners&gt;
 *               &lt;batch:listener ref="noReadStepExecutionListener"/&gt;
 *           &lt;/batch:listeners&gt;
 *       &lt;/batch:step&gt;
 * </pre>
 * 
 * @author pcollardez
 *
 */
public class NoReadStepExecutionListener extends StepExecutionListenerSupport {

    @Override
    public ExitStatus afterStep(StepExecution sStepExecution) {
        if (!ExitStatus.FAILED.getExitCode().equals(sStepExecution.getExitStatus().getExitCode()) && sStepExecution.getReadCount() == 0) {
            return new ExitStatus("NOREAD");
        }
        return null;
    }

}
