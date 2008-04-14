/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.gl.batch;

import java.util.Date;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.gl.service.SufficientFundsRebuilderService;

/**
 * A step to run the process that rebuilds information that supports sufficient funds inquiries
 */
public class SufficientFundsRebuilderStep extends AbstractStep {
    private SufficientFundsRebuilderService sufficientFundsRebuilderService;

    /**
     * Runs the sufficient funds rebuilder step.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        sufficientFundsRebuilderService.rebuildSufficientFunds();
        return true;
    }

    /**
     * Sets the sufficientFundsRebuilderService, allowing the injection of an implementation of that service
     * 
     * @param sufficientFundsRebuilderService an implementation sufficientFundsRebuilderService to set
     * @see org.kuali.module.gl.service.SufficientFundsRebuilderService
     */
    public void setSufficientFundsRebuilderService(SufficientFundsRebuilderService sfrs) {
        sufficientFundsRebuilderService = sfrs;
    }
}
