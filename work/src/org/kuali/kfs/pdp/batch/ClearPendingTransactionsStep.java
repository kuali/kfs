/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.pdp.batch;

import java.util.Date;

import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * A step to clear pdp pending ledger entries.
 */
public class ClearPendingTransactionsStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClearPendingTransactionsStep.class);
    
    private PendingTransactionService pendingTransactionService;

    /**
     * Runs the process of deleting copied pdp ledger entries
     * 
     * @param jobName the name of the job that this step is being run as part of
     * @param jobRunDate the time/date the job is run
     * @return that the job completed successfully
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        pendingTransactionService.clearExtractedTransactions();
        
        return true;
    }

    /**
     * Sets the pendingTransactionService attribute value.
     * @param pendingTransactionService The pendingTransactionService to set.
     */
    public void setPendingTransactionService(PendingTransactionService pendingTransactionService) {
        this.pendingTransactionService = pendingTransactionService;
    }

}
