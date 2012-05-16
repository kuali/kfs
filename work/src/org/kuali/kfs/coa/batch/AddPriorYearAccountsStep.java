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
package org.kuali.kfs.coa.batch;

import org.kuali.kfs.coa.service.PriorYearAccountService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * This class defines the batch step that adds new prior year accounts defined in a parameter to the prior year account table. 
 * This job typically runs at year end, and should be run only after populatePriorYearDataJob and after users have updated the parameter.
 */
public class AddPriorYearAccountsStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AddPriorYearAccountsStep.class);

    private PriorYearAccountService priorYearAccountService;

    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                priorYearAccountService.addPriorYearAccountsFromParameter();
                return true;
            }                
        };
    }    

    /**
     * Service setter for Spring injection
     * 
     * @param priorYearAccountService
     */
    public void setPriorYearAccountService(PriorYearAccountService priorYearAccountService) {
        this.priorYearAccountService = priorYearAccountService;
    }

}
