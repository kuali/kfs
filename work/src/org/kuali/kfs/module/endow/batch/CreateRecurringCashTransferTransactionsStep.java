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

import org.kuali.kfs.module.endow.batch.service.CreateRecurringCashTransferTransactionsService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

public class CreateRecurringCashTransferTransactionsStep extends AbstractWrappedBatchStep {

    private CreateRecurringCashTransferTransactionsService createRecurringCashTransferTransactionsService;
    protected String batchFileDirectoryName;

    /**
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                boolean success = true;
                success = createRecurringCashTransferTransactionsService.createRecurringCashTransferTransactions();

                return success;
            }
        };
    }

    /**
     * Sets the createCashSweepTransactionsService attribute value.
     * @param createCashSweepTransactionsService The createCashSweepTransactionsService to set.
     */
    public void setCreateRecurringCashTransferTransactionsService(CreateRecurringCashTransferTransactionsService createRecurringCashTransferTransactionsService) {
        this.createRecurringCashTransferTransactionsService = createRecurringCashTransferTransactionsService;
    }

    /**
     * Sets the batchFileDirectoryName.
     * 
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
