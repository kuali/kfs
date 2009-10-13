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

import org.kuali.kfs.gl.service.NightlyOutService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * Runs the nightly out process, which is the process that preps general ledger pending entries to be fed to the scrubber.
 */
public class NightlyOutStep extends AbstractWrappedBatchStep {
    private NightlyOutService nightlyOutService;

    /**
     * @return a proper executor for the NightlyOutStep
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            /**
             * Runs the nightly out process.
             * 
             * @return true if the job completed successfully, false if otherwise
             * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
             */
            public boolean execute() {
                nightlyOutService.copyApprovedPendingLedgerEntries();
                return true;
            }
        };
    }

    /**
     * Sets the nightlyOutService attribute value.
     * 
     * @param nightlyOutService The nightlyOutService to set.
     * @see org.kuali.kfs.gl.service.NightlyOutService
     */
    public void setNightlyOutService(NightlyOutService nightlyOutService) {
        this.nightlyOutService = nightlyOutService;
    }
}
