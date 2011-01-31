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

import org.kuali.kfs.module.endow.batch.service.GeneralLedgerInterfaceBatchProcessService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * The process serves to consolidate the KEM activity for the day into valid 
 * general ledger debits and credits to update institution's records.
 */
public class GeneralLedgerInterfaceBatchProcessStep extends AbstractWrappedBatchStep {

    protected GeneralLedgerInterfaceBatchProcessService generalLedgerInterfaceBatchProcessService;
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerInterfaceBatchProcessStep.class);

    /**
     * Overridden to run the process Fee Transactions.
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                boolean success = true;
                success = generalLedgerInterfaceBatchProcessService.processKEMActivityToCreateGLEntries();                
                
                return success;            
            }
        };
    }
        
    /**
     * Sets the generalLedgerInterfaceBatchProcessService attribute value.
     * @param generalLedgerInterfaceBatchProcessService The generalLedgerInterfaceBatchProcessService to set.
     */
    public void setGeneralLedgerInterfaceBatchProcessService(GeneralLedgerInterfaceBatchProcessService generalLedgerInterfaceBatchProcessService) {
        this.generalLedgerInterfaceBatchProcessService = generalLedgerInterfaceBatchProcessService;
    }
}
