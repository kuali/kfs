/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
