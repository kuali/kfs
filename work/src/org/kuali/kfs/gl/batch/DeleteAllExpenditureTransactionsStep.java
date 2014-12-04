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
