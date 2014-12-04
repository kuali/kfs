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
