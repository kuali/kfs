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

import org.kuali.kfs.module.ld.batch.service.LaborYearEndBalanceForwardService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * Labor End balance forward Batch Step.
 */
public class LaborYearEndBalanceForwardStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborYearEndBalanceForwardStep.class);
    private LaborYearEndBalanceForwardService laborYearEndBalanceForwardService;    

    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                laborYearEndBalanceForwardService.forwardBalance();
                return true;
            }
        };
    }

    /**
     * Sets the laborYearEndBalanceForwardService attribute value.
     * 
     * @param laborYearEndBalanceForwardService The laborYearEndBalanceForwardService to set.
     * @return none
     */
    public void setLaborYearEndBalanceForwardService(LaborYearEndBalanceForwardService laborYearEndBalanceForwardService) {
        this.laborYearEndBalanceForwardService = laborYearEndBalanceForwardService;
    }
}
