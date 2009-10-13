/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch;

import java.util.Date;

import org.kuali.kfs.gl.batch.service.SufficientFundsFullRebuildService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.TestingStep;

/**
 * A step to run the sufficient funds sync process. One typically doesn't need to do this - which is why it's marked as TestingStep -
 * as Account, Chart, and Object Code records, when saved, will populate the sufficient funds tables, making this task redundant.
 * However, if that information has not been built, this job will generate that information.
 */
public class SufficientFundsFullRebuildStep extends AbstractStep implements TestingStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsFullRebuildStep.class);
    private SufficientFundsFullRebuildService sufficientFundsFullRebuildService;

    /**
     * Runs the sufficient funds sync service.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        sufficientFundsFullRebuildService.syncSufficientFunds();
        return true;
    }

    /**
     * Sets the sufficientFundsFullRebuildService, allowing the injection of an implementation of that service
     * 
     * @param sufficientFundsFullRebuildService an implementation sufficientFundsFullRebuildService to set
     * @see org.kuali.kfs.gl.batch.service.SufficientFundsFullRebuildService
     */
    public void setSufficientFundsFullRebuildService(SufficientFundsFullRebuildService sfss) {
        sufficientFundsFullRebuildService = sfss;
    }
}
