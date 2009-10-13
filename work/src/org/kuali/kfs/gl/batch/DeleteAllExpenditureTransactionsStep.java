/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.gl.batch;

import java.util.Date;

import org.kuali.kfs.gl.service.ExpenditureTransactionService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.TestingStep;

/**
 * A step to remove all expenditure transactions held in the database
 */
public class DeleteAllExpenditureTransactionsStep extends AbstractStep implements TestingStep {
    private ExpenditureTransactionService expenditureTransactionService;

    /**
     * Runs the process of deleting all expenditure transactions
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date when the job was started
     * @return true if the job finished successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        expenditureTransactionService.deleteAllExpenditureTransactions();
        return true;
    }

    /**
     * Sets the expenditureTransactionService attribute, allowing the inject of an implementation of this service
     * 
     * @param expenditureTransactionService
     * @see org.kuali.kfs.gl.service.ExpenditureTransactionService
     */
    public void setExpenditureTransactionService(ExpenditureTransactionService expenditureTransactionService) {
        this.expenditureTransactionService = expenditureTransactionService;
    }
}
