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

import org.kuali.kfs.module.endow.batch.service.IncomeDistributionForPooledFundService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

public class IncomeDistributionForPooledFundStep extends AbstractWrappedBatchStep {
    
    private IncomeDistributionForPooledFundService incomeDistributionForPooledFundService;
    protected String batchFileDirectoryName;

    /**
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                boolean success = true;
                success = incomeDistributionForPooledFundService.createIncomeDistributionForPooledFund();

                return success;
            }
        };
    }

    /**
     * Sets the incomeDistributionForPooledFundService attribute value.
     * @param incomeDistributionForPooledFundService The incomeDistributionForPooledFundService to set.
     */
    public void setIncomeDistributionForPooledFundService(IncomeDistributionForPooledFundService incomeDistributionForPooledFundService) {
        this.incomeDistributionForPooledFundService = incomeDistributionForPooledFundService;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
    
}
