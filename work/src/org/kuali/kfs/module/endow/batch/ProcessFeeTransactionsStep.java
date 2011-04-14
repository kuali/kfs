/*
 * Copyright 2007-2008 The Kuali Foundation
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

import org.kuali.kfs.module.endow.batch.service.ProcessFeeTransactionsService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * The fee process is intended to provide as much flexibility to the institution as 
 * possible when designing the charges to be assessed against a KEMID
 */
public class ProcessFeeTransactionsStep extends AbstractWrappedBatchStep {

    protected ProcessFeeTransactionsService processFeeTransactionsService;
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessFeeTransactionsStep.class);
    protected String batchFileDirectoryName;

    /**
     * Overridden to run the process Fee Transactions.
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                boolean success = true;
                success = processFeeTransactionsService.processFeeTransactions();                
                
                return success;            
            }
        };
    }
        
    /**
     * Sets the processFeeTransactionsService attribute value.
     * @param processFeeTransactionsService The processFeeTransactionsService to set.
     */
    public void setProcessFeeTransactionsService(ProcessFeeTransactionsService processFeeTransactionsService) {
        this.processFeeTransactionsService = processFeeTransactionsService;
    }
    
    /**
     * This method sets the batchFileDirectoryName
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
