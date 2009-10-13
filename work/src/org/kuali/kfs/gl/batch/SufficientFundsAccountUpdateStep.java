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

import org.kuali.kfs.gl.batch.service.SufficientFundsAccountUpdateService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * A step to run the process that rebuilds information that supports sufficient funds inquiries
 */
public class SufficientFundsAccountUpdateStep extends AbstractWrappedBatchStep {
    private SufficientFundsAccountUpdateService sufficientFundsAccountUpdateService;

    /**
     * Return a proper batch executor
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            /**
             * Runs the sufficient funds rebuilder step.
             * 
             * @return true if the job completed successfully, false if otherwise
             * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
             */
            public boolean execute() {
                sufficientFundsAccountUpdateService.rebuildSufficientFunds();
                return true;
            }
        };
    }


    /**
     * Sets the sufficientFundsAccountUpdateService, allowing the injection of an implementation of that service
     * 
     * @param sufficientFundsAccountUpdateService an implementation sufficientFundsAccountUpdateService to set
     * @see org.kuali.kfs.gl.batch.service.SufficientFundsAccountUpdateService
     */
    public void setSufficientFundsAccountUpdateService(SufficientFundsAccountUpdateService sfrs) {
        sufficientFundsAccountUpdateService = sfrs;
    }
}
