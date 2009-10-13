/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.batch;

import java.util.Date;

import org.kuali.kfs.module.ld.batch.service.LaborNightlyOutService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * Clean Labor Feed Batch Step.
 */
public class LaborFeedStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClearLaborPendingStep.class);
    private LaborNightlyOutService laborNightlyOutService;

    /**
     * Executes the service copyLaborGenerealLedgerEntries
     * 
     * @param jobName
     * @param jobRunDate
     * @return boolean true when success
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        laborNightlyOutService.copyLaborGenerealLedgerEntries();
        return true;
    }

    /**
     * Sets the laborNightlyOutService attribute value.
     * 
     * @param laborNightlyOutService The laborNightlyOutService to set.
     */
    public void setLaborNightlyOutService(LaborNightlyOutService laborNightlyOutService) {
        this.laborNightlyOutService = laborNightlyOutService;
    }
}
