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
package org.kuali.kfs.gl.report;

import java.util.Iterator;

import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class prints out a transaction listing report. This is different from a transaction report in that this lists all the
 * transactions and a total amount. The transaction report shows the primary key from transactions and a list of messages for each
 * one.
 */
public class TransactionListingReport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionListingReport.class);

    protected int transactionCount;
    protected KualiDecimal debitTotal;
    protected KualiDecimal creditTotal;
    protected KualiDecimal budgetTotal;
    
    public TransactionListingReport() {
        super();
        
        transactionCount = 0;
        debitTotal = KualiDecimal.ZERO;
        creditTotal = KualiDecimal.ZERO;
        budgetTotal = KualiDecimal.ZERO;
    }
    
    /**
     * This will write a transaction to the report. It collects data in order for this to be a listing, hence it is the developers responsibility
     * to call generateStatistics after printing the listing.
     * 
     * @param reportWriterService destination report
     * @param transaction Transaction to be printed
     */
    public void generateReport(ReportWriterService reportWriterService, Transaction transaction) {
        LOG.debug("generateReport() started");

        if (transaction != null) {
            if (transactionCount == 0) {
                reportWriterService.writeTableHeader(transaction);
            }
            
            reportWriterService.writeTableRow(transaction);
            
            if (KFSConstants.GL_DEBIT_CODE.equals(transaction.getTransactionDebitCreditCode())) {
                debitTotal = debitTotal.add(transaction.getTransactionLedgerEntryAmount());
            }
            if (KFSConstants.GL_CREDIT_CODE.equals(transaction.getTransactionDebitCreditCode())) {
                creditTotal = creditTotal.add(transaction.getTransactionLedgerEntryAmount());
            }
            if (!KFSConstants.GL_CREDIT_CODE.equals(transaction.getTransactionDebitCreditCode()) && !KFSConstants.GL_DEBIT_CODE.equals(transaction.getTransactionDebitCreditCode())) {
                budgetTotal = budgetTotal.add(transaction.getTransactionLedgerEntryAmount());
            }
            transactionCount++;
        }
    }
    
    /**
     * Writes the statistics to the report that were collected by this class
     * 
     * @param reportWriterService destination report
     */
    public void generateStatistics(ReportWriterService reportWriterService) {
        LOG.debug("generateStatistics() started");
        
        reportWriterService.writeStatisticLine("Total Transactions                %,9d", transactionCount);
        reportWriterService.writeStatisticLine("Total Debit Amount                %,9.2f", debitTotal.doubleValue());
        reportWriterService.writeStatisticLine("Total Credit Amount               %,9.2f", creditTotal.doubleValue());
        reportWriterService.writeStatisticLine("Total Budget Amount               %,9.2f", budgetTotal.doubleValue());
    }
    
    /**
     * This will generate a report on the transactions passed to it
     * 
     * @param reportWriterService destination report
     * @param transactions Transactions sorted properly
     */
    public void generateReport(ReportWriterService reportWriterService, Iterator<? extends Transaction> transactions) {
        LOG.debug("generateReport() started");

        if (transactions != null) {
            while (transactions.hasNext()) {
                Transaction tran = (Transaction) transactions.next();

                this.generateReport(reportWriterService, tran);
            }
        }

        this.generateStatistics(reportWriterService);
    }
}
