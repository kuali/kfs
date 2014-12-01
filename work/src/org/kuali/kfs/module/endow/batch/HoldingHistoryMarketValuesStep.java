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

import org.kuali.kfs.module.endow.batch.service.HoldingHistoryMarketValuesUpdateService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * This process appends current tax lot balance records to the holding history table as of each month.
 */
public class HoldingHistoryMarketValuesStep extends AbstractWrappedBatchStep {

    protected HoldingHistoryMarketValuesUpdateService holdingHistoryMarketValuesUpdateService;
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HoldingHistoryMarketValuesStep.class);
    protected String batchFileDirectoryName;

    /**
     * Overridden to run holding history market values update process.
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                boolean success = true;
                success = holdingHistoryMarketValuesUpdateService.updateHoldingHistoryMarketValues();                
                
                return success;            
            }
        };
    }
        
    /**
     * Sets the holdingHistoryMarketValuesUpdateService attribute value.
     * 
     * @param holdingHistoryMarketValuesUpdateService The holdingHistoryMarketValuesUpdateService to set.
     * @see org.kuali.kfs.sys.service.HoldingHistoryMarketValuesUpdateService
     */
    public void setHoldingHistoryMarketValuesUpdateService(HoldingHistoryMarketValuesUpdateService holdingHistoryMarketValuesUpdateService) {
        this.holdingHistoryMarketValuesUpdateService = holdingHistoryMarketValuesUpdateService;
    }
    
    /**
     * This method sets the batchFileDirectoryName
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
