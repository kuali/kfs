/*
 * Copyright 2005 The Kuali Foundation
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

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.AccountBalanceCalculator;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PostAccountBalance implements PostTransaction, AccountBalanceCalculator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostAccountBalance.class);

    private AccountingCycleCachingService accountingCycleCachingService;
    private PersistenceStructureService persistenceStructureService;

    /**
     * Constructs a PostAccountBalance instance
     */
    public PostAccountBalance() {
        super();
    }

    /**
     * Posts the transaction to the appropriate account balance record.
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

        // Only post transactions where:
        // balance type code is AC or CB
        // or where object type isn't FB and balance type code is EX, IE, PE and CE
        if ((t.getFinancialBalanceTypeCode().equals(t.getOption().getActualFinancialBalanceTypeCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getBudgetCheckingBalanceTypeCd())) || (t.getFinancialBalanceTypeCode().equals(t.getOption().getExtrnlEncumFinBalanceTypCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getIntrnlEncumFinBalanceTypCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getPreencumbranceFinBalTypeCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getCostShareEncumbranceBalanceTypeCd())) && (!t.getFinancialObjectTypeCode().equals(t.getOption().getFinObjectTypeFundBalanceCd()))) {
            // We are posting this transaction
            String returnCode = GeneralLedgerConstants.UPDATE_CODE;

            // Load it
            AccountBalance ab = accountingCycleCachingService.getAccountBalance(t);

            if (ab == null) {
                returnCode = GeneralLedgerConstants.INSERT_CODE;
                ab = new AccountBalance(t);
            }

            ab.setTimestamp(new java.sql.Date(postDate.getTime()));

            if (!updateAccountBalanceReturn(t, ab)) {
                return GeneralLedgerConstants.EMPTY_CODE;
            }

            if (returnCode.equals(GeneralLedgerConstants.INSERT_CODE)) {
                accountingCycleCachingService.insertAccountBalance(ab);
            } else {
                accountingCycleCachingService.updateAccountBalance(ab);
            }

            return returnCode;
        }
        else {
            return GeneralLedgerConstants.EMPTY_CODE;
        }
    }

    public AccountBalance findAccountBalance(Collection balanceList, Transaction t) {

        // Try to find one that already exists
        for (Iterator iter = balanceList.iterator(); iter.hasNext();) {
            AccountBalance b = (AccountBalance) iter.next();

            if (b.getUniversityFiscalYear().equals(t.getUniversityFiscalYear()) && b.getChartOfAccountsCode().equals(t.getChartOfAccountsCode()) && b.getAccountNumber().equals(t.getAccountNumber()) && b.getSubAccountNumber().equals(t.getSubAccountNumber()) && b.getObjectCode().equals(t.getFinancialObjectCode()) && b.getSubObjectCode().equals(t.getFinancialSubObjectCode())) {
                return b;
            }
        }

        // If we couldn't find one that exists, create a new one
        AccountBalance b = new AccountBalance(t);
        balanceList.add(b);

        return b;
    }

    protected boolean updateAccountBalanceReturn(Transaction t, AccountBalance ab) {
        if (t.getFinancialBalanceTypeCode().equals(t.getOption().getBudgetCheckingBalanceTypeCd())) {
            ab.setCurrentBudgetLineBalanceAmount(ab.getCurrentBudgetLineBalanceAmount().add(t.getTransactionLedgerEntryAmount()));
        }
        else if (t.getFinancialBalanceTypeCode().equals(t.getOption().getActualFinancialBalanceTypeCd())) {
            if (t.getObjectType().getFinObjectTypeDebitcreditCd().equals(t.getTransactionDebitCreditCode()) || ((!t.getBalanceType().isFinancialOffsetGenerationIndicator()) && KFSConstants.GL_BUDGET_CODE.equals(t.getTransactionDebitCreditCode()))) {
                ab.setAccountLineActualsBalanceAmount(ab.getAccountLineActualsBalanceAmount().add(t.getTransactionLedgerEntryAmount()));
            }
            else {
                ab.setAccountLineActualsBalanceAmount(ab.getAccountLineActualsBalanceAmount().subtract(t.getTransactionLedgerEntryAmount()));
            }
        }
        else if (t.getFinancialBalanceTypeCode().equals(t.getOption().getExtrnlEncumFinBalanceTypCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getIntrnlEncumFinBalanceTypCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getPreencumbranceFinBalTypeCd()) || t.getFinancialBalanceTypeCode().equals(t.getOption().getCostShareEncumbranceBalanceTypeCd())) {
            if (t.getObjectType().getFinObjectTypeDebitcreditCd().equals(t.getTransactionDebitCreditCode()) || ((!t.getBalanceType().isFinancialOffsetGenerationIndicator()) && KFSConstants.GL_BUDGET_CODE.equals(t.getTransactionDebitCreditCode()))) {
                ab.setAccountLineEncumbranceBalanceAmount(ab.getAccountLineEncumbranceBalanceAmount().add(t.getTransactionLedgerEntryAmount()));
            }
            else {
                ab.setAccountLineEncumbranceBalanceAmount(ab.getAccountLineEncumbranceBalanceAmount().subtract(t.getTransactionLedgerEntryAmount()));
            }
        }
        else {
            return false;
        }
        return true;
    }

    /**
     * @param t
     * @param enc
     */
    public void updateAccountBalance(Transaction t, AccountBalance ab) {
        updateAccountBalanceReturn(t, ab);
    }

    public String getDestinationName() {
        return persistenceStructureService.getTableName(AccountBalance.class);
    }

    public void setAccountingCycleCachingService(AccountingCycleCachingService accountingCycleCachingService) {
        this.accountingCycleCachingService = accountingCycleCachingService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }
}
