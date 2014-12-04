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

import org.kuali.kfs.module.endow.batch.service.PooledFundControlTransactionsService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

public class PooledFundControlTransactionsStep extends AbstractWrappedBatchStep {
    
    private PooledFundControlTransactionsService pooledFundControlTransactionsService;
    protected String batchFileDirectoryName;
    
    /**
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                boolean success = true;
                success = pooledFundControlTransactionsService.generatePooledFundControlTransactions();

                return success;
            }
        };
    }

    /**
     * This method sets pooledFundControlTransactionsService
     * @param pooledFundControlTransactionsService
     */
    public void setPooledFundControlTransactionsService(PooledFundControlTransactionsService pooledFundControlTransactionsService) {
        this.pooledFundControlTransactionsService = pooledFundControlTransactionsService;
    }
    
    /**
     * Sets the batchFileDirectoryName.
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
    
}
