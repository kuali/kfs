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
 * Clean Labor entry Batch Step.
 */
public class ClearLaborGLEntryStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClearLaborPendingStep.class);
    private LaborNightlyOutService laborNightlyOutService;

    /**
     * Deletes labor general ledger entries.
     * 
     * @param jobName String that contains the job that will be executed.
     * @param jobRunDate the time/date the job is run
     * @return boolean
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        laborNightlyOutService.deleteCopiedLaborGenerealLedgerEntries();
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
