/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch;

import org.kuali.kfs.module.endow.batch.service.CreateCashSweepTransactionsService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

public class CreateCashSweepTransactionsStep extends AbstractWrappedBatchStep {

    private CreateCashSweepTransactionsService createCashSweepTransactionsService;

    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                boolean success = true;
                success = createCashSweepTransactionsService.createCashSweepTransactions();                
                
                return success;            
            }
        };
    }
    
    /**
     * Sets the createCashSweepTransactionsService attribute value.
     * @param createCashSweepTransactionsService The createCashSweepTransactionsService to set.
     */
    public void setCreateCashSweepTransactionsService(CreateCashSweepTransactionsService createCashSweepTransactionsService) {
        this.createCashSweepTransactionsService = createCashSweepTransactionsService;
    }
}
