/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.pdp.batch;

import org.kuali.kfs.pdp.batch.service.InactivatePayeeAchAccountsService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * Batch step to inactivate the Payee ACH Accounts for which thes payee are inactive. 
 * Payee active status is obtained from the associated Person or Vendor, depending on the payee ID type.
 */
public class InactivatePayeeAchAccountsStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InactivatePayeeAchAccountsStep.class);

    private InactivatePayeeAchAccountsService inactivatePayeeAchAccountsService;
    
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                return inactivatePayeeAchAccountsService.inactivatePayeeAchAccounts();
            }                
        };
    }

    public void setInactivatePayeeAchAccountsService(InactivatePayeeAchAccountsService inactivatePayeeAchAccountsService) {
        this.inactivatePayeeAchAccountsService = inactivatePayeeAchAccountsService;
    }
        
}
