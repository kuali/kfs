/*
 * Copyright 2006 The Kuali Foundation
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
