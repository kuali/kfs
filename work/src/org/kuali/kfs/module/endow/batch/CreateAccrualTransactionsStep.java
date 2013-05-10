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

import org.kuali.kfs.module.endow.batch.service.CreateAccrualTransactionsService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * Batch step that executes the Create Accrual Transactions step.
 */
public class CreateAccrualTransactionsStep extends AbstractWrappedBatchStep {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateAccrualTransactionsStep.class);

    private CreateAccrualTransactionsService createAccrualTransactionsService;
    protected String batchFileDirectoryName;

    /**
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                boolean success = true;
                success = createAccrualTransactionsService.createAccrualTransactions();

                return success;
            }
        };
    }

    /**
     * Sets the createAccrualTransactionsService.
     * 
     * @param createAccrualTransactionsService
     */
    public void setCreateAccrualTransactionsService(CreateAccrualTransactionsService createAccrualTransactionsService) {
        this.createAccrualTransactionsService = createAccrualTransactionsService;
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
