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
package org.kuali.kfs.module.ld.document;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.util.LaborPendingEntryGenerator;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Labor Document class for the Benefit Expense Transfer Document and a base class for the year end benefit expense transfer
 * document
 */

public class BenefitExpenseTransferDocument extends LaborExpenseTransferDocumentBase {
    protected static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BenefitExpenseTransferDocument.class);
    protected transient String chartOfAccountsCode;
    protected transient String accountNumber;
    protected transient Account account;
    /**
     * Default Constructor.
     */
    public BenefitExpenseTransferDocument() {
        super();
    }
    
    /**
     * @see org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase#generateLaborLedgerPendingEntries(org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateLaborLedgerPendingEntries(AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.debug("started processGenerateLaborLedgerPendingEntries()");
        boolean isSuccessful = true;
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;
        
        List<LaborLedgerPendingEntry> expensePendingEntries = LaborPendingEntryGenerator.generateExpensePendingEntries(this, expenseTransferAccountingLine, sequenceHelper);
        if (expensePendingEntries != null && !expensePendingEntries.isEmpty()) {
            isSuccessful &= this.getLaborLedgerPendingEntries().addAll(expensePendingEntries);
        }
        
        return isSuccessful;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase#generateLaborLedgerBenefitClearingPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateLaborLedgerBenefitClearingPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }
    
    public List getLaborLedgerPendingEntriesForSearching() {
        return super.getLaborLedgerPendingEntries();
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    
    public String getChartOfAccountsCode() {
        AccountService accountService = SpringContext.getBean(AccountService.class);
        if (!accountService.accountsCanCrossCharts()) {
            if (ObjectUtils.isNotNull(this.account)) this.chartOfAccountsCode = account.getChartOfAccountsCode();
        }
        return chartOfAccountsCode;
    }

    /** 
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    
    public String getAccountNumber() {
        return accountNumber;
    }

    /**	
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        if (StringUtils.isNotEmpty(accountNumber)) {
            AccountService accountService = SpringContext.getBean(AccountService.class);
            if (! accountService.accountsCanCrossCharts()) {
                Account acct = accountService.getUniqueAccountForAccountNumber(accountNumber);
                setChartOfAccountsCode(acct.getChartOfAccountsCode());
                this.setAccount(acct);
            }
        }
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     */
    
    public Account getAccount() {
        return account;
    }

    /**	
     * Sets the account attribute.
     * 
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }
    
}
