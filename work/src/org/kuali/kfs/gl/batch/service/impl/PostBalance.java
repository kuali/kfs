/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.batch.service.BalanceCalculator;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

/**
 * This implementation of PostTransaction updates the appropriate Balance
 */
@Transactional
public class PostBalance implements PostTransaction, BalanceCalculator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostBalance.class);
    
    private AccountingCycleCachingService accountingCycleCachingService;
    private static final KualiDecimal NEGATIVE_ONE = new KualiDecimal(-1);
    /**
     * Constructs a PostBalance instance
     */
    public PostBalance() {
        super();
    }

    /**
     * This posts the effect of the transaction upon the appropriate balance record.
     * 
     * @param t the transaction which is being posted
     * @param mode the mode the poster is currently running in
     * @param postDate the date this transaction should post to
     * @param posterReportWriterService the writer service where the poster is writing its report
     * @return the accomplished post type
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public String post(Transaction t, int mode, Date postDate, ReportWriterService posterReportWriterService) {
        LOG.debug("post() started");

        String postType = "U";

        KualiDecimal amount = t.getTransactionLedgerEntryAmount();

        // Subtract the amount if offset generation indicator & the debit/credit code isn't the same
        // as the one in the object type code table
        if (t.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            if (!t.getTransactionDebitCreditCode().equals(t.getObjectType().getFinObjectTypeDebitcreditCd())) {
                amount = amount.multiply(NEGATIVE_ONE);
            }
        }

        Balance b = accountingCycleCachingService.getBalance(t);
        if (b == null) {
            postType = "I";
            b = new Balance(t);
        }
        String period = t.getUniversityFiscalPeriodCode();
        b.addAmount(period, amount);

        if (postType.equals("I")) {
            accountingCycleCachingService.insertBalance(b);
        } else {
            accountingCycleCachingService.updateBalance(b);
        }
        
        return postType;
    }

    /**
     * Given a list of balances, determines which one the given trsnaction should post to
     * 
     * @param balanceList a Collection of balances
     * @param t the transaction that is being posted
     * @return the balance, either found from the list, or, if not present in the list, newly created
     * @see org.kuali.kfs.gl.batch.service.BalanceCalculator#findBalance(java.util.Collection, org.kuali.kfs.gl.businessobject.Transaction)
     */
    public Balance findBalance(Collection balanceList, Transaction t) {
        // Try to find one that already exists
        for (Iterator iter = balanceList.iterator(); iter.hasNext();) {
            Balance b = (Balance) iter.next();

            if (b.getUniversityFiscalYear().equals(t.getUniversityFiscalYear()) && b.getChartOfAccountsCode().equals(t.getChartOfAccountsCode()) && b.getAccountNumber().equals(t.getAccountNumber()) && b.getSubAccountNumber().equals(t.getSubAccountNumber()) && b.getObjectCode().equals(t.getFinancialObjectCode()) && b.getSubObjectCode().equals(t.getFinancialSubObjectCode()) && b.getBalanceTypeCode().equals(t.getFinancialBalanceTypeCode()) && b.getObjectTypeCode().equals(t.getFinancialObjectTypeCode())) {
                return b;
            }
        }

        // If we couldn't find one that exists, create a new one
        Balance b = new Balance(t);
        balanceList.add(b);

        return b;
    }

    /**
     * @param t
     * @param enc
     */
    public void updateBalance(Transaction t, Balance b) {

        // The pending entries haven't been scrubbed so there could be
        // bad data. This won't update a balance if the data it needs
        // is invalid
        KualiDecimal amount = t.getTransactionLedgerEntryAmount();
        if (amount == null) {
            amount = KualiDecimal.ZERO;
        }

        if (t.getObjectType() == null) {
            LOG.error("updateBalance() Invalid object type (" + t.getFinancialObjectTypeCode() + ") in pending table");
            return;
        }

        if (t.getBalanceType() == null) {
            LOG.error("updateBalance() Invalid balance type (" + t.getFinancialBalanceTypeCode() + ") in pending table");
            return;
        }

        // Subtract the amount if offset generation indicator & the debit/credit code isn't the same
        // as the one in the object type code table
        if (t.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            if (!t.getTransactionDebitCreditCode().equals(t.getObjectType().getFinObjectTypeDebitcreditCd())) {
                amount = amount.multiply(new KualiDecimal(-1));
            }
        }

        // update the balance amount of the cooresponding period
        String period = t.getUniversityFiscalPeriodCode();
        if (period == null) {
            UniversityDate currentUniversityDate = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
            period = currentUniversityDate.getUniversityFiscalAccountingPeriod();
        }

        b.addAmount(period, amount);
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#getDestinationName()
     */
    public String getDestinationName() {
        return "GL_BALANCE_T";
    }

    public void setAccountingCycleCachingService(AccountingCycleCachingService accountingCycleCachingService) {
        this.accountingCycleCachingService = accountingCycleCachingService;
    }
}
