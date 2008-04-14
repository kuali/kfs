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
import org.kuali.module.gl.service.NightlyOutService;

/**
 * Runs the nightly out process, which is the process that preps general ledger pending entries to be fed to the scrubber.
 */
public class NightlyOutStep extends AbstractStep {
    private NightlyOutService nightlyOutService;

    /**
     * Runs the nightly out process.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job is being run
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        nightlyOutService.copyApprovedPendingLedgerEntries();
        return true;
    }

    /**
     * Sets the nightlyOutService attribute value.
     * 
     * @param nightlyOutService The nightlyOutService to set.
     * @see org.kuali.module.gl.service.NightlyOutService
     */
    public void setNightlyOutService(NightlyOutService nightlyOutService) {
        this.nightlyOutService = nightlyOutService;
    }
}
