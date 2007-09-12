/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.labor.rules;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.document.BenefitExpenseTransferDocument;
import org.kuali.module.labor.document.LaborExpenseTransferDocumentBase;
import org.kuali.module.labor.document.LaborLedgerPostingDocument;
import org.kuali.module.labor.util.LaborPendingEntryGenerator;

/**
 * Business rule(s) applicable to Benefit Expense Transfer documents.
 */
public class BenefitExpenseTransferDocumentRule extends LaborExpenseTransferDocumentRules {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BenefitExpenseTransferDocumentRule.class);

    /**
     * Constructs a BenefitExpenseTransferDocumentRule.java.
     */
    public BenefitExpenseTransferDocumentRule() {
        super();
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        boolean isValid = super.processCustomAddAccountingLineBusinessRules(accountingDocument, accountingLine);

        // only fringe benefit labor object codes are allowed on the benefit expense transfer document
        if (!isFringeBenefitObjectCode(accountingLine)) {
            reportError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.Labor.INVALID_FRINGE_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            isValid = false;
        }

        // ensure the accounts in source accounting lines are same
        if (!hasSameAccount(accountingDocument, accountingLine)) {
            reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.Labor.ERROR_ACCOUNT_NOT_SAME);
            return false;
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        BenefitExpenseTransferDocument benefitExpenseTransferDocument = (BenefitExpenseTransferDocument) document;

        // ensure the accounts in source accounting lines are same
        if (!hasSameAccount(benefitExpenseTransferDocument)) {
            reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.Labor.ERROR_ACCOUNT_NOT_SAME);
            isValid = false;
        }

        return isValid;
    }

    /**
     * @see org.kuali.module.labor.rules.LaborExpenseTransferDocumentRules#processGenerateLaborLedgerPendingEntries(org.kuali.module.labor.document.LaborLedgerPostingDocument,
     *      org.kuali.module.labor.bo.ExpenseTransferAccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean processGenerateLaborLedgerPendingEntries(LaborLedgerPostingDocument document, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.info("started processGenerateLaborLedgerPendingEntries()");
        
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;
        List<LaborLedgerPendingEntry> expensePendingEntries = LaborPendingEntryGenerator.generateExpesnePendingEntries(document, expenseTransferAccountingLine, sequenceHelper);
        document.getLaborLedgerPendingEntries().addAll(expensePendingEntries);

        return true;
    }

    /**
     * Determine whether the object code of given accounting line is a fringe benefit labor object code
     * 
     * @param accountingLine the given accounting line
     * @return true if the object code of given accounting line is a fringe benefit labor object code; otherwise, false
     */
    private boolean isFringeBenefitObjectCode(AccountingLine accountingLine) {
        LOG.debug("started isFringeBenefitObjectCode");
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        LaborObject laborObject = expenseTransferAccountingLine.getLaborObject();
        if (laborObject == null) {
            return false;
        }

        String fringeOrSalaryCode = laborObject.getFinancialObjectFringeOrSalaryCode();
        return StringUtils.equals(LaborConstants.BenefitExpenseTransfer.LABOR_LEDGER_BENEFIT_CODE, fringeOrSalaryCode);
    }

    /**
     * determine whether the accounts in the source accouting lines are same
     * 
     * @param accountingDocument the given accouting document
     * @return true if the accounts in the source accouting lines are same; otherwise, false
     */
    private boolean hasSameAccount(AccountingDocument accountingDocument) {
        LOG.debug("started hasSameAccount");

        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        List<ExpenseTransferSourceAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();

        Account cachedAccount = null;
        for (AccountingLine sourceAccountingLine : sourceAccountingLines) {
            Account account = sourceAccountingLine.getAccount();
            if (account == null) {
                return false;
            }

            // account number was not retrieved correctly, so the two statements are used to populate the fields manually
            account.setChartOfAccountsCode(sourceAccountingLine.getChartOfAccountsCode());
            account.setAccountNumber(sourceAccountingLine.getAccountNumber());
            
            cachedAccount = cachedAccount == null ? account : cachedAccount;
            if (!account.equals(cachedAccount)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * determine whether the given accouting line has the same account as the source accounting lines
     * 
     * @param accountingDocument the given accouting document
     * @param accountingLine the given accounting line
     * @return true if the given accouting line has the same account as the source accounting lines; otherwise, false
     */
    private boolean hasSameAccount(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        LOG.debug("started hasSameAccount(AccountingDocument, AccountingLine)");

        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        List<ExpenseTransferSourceAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();

        Account cachedAccount = accountingLine.getAccount();
        for (AccountingLine sourceAccountingLine : sourceAccountingLines) {
            Account account = sourceAccountingLine.getAccount();
            
            // account number was not retrieved correctly, so the two statements are used to populate the fields manually
            account.setChartOfAccountsCode(sourceAccountingLine.getChartOfAccountsCode());
            account.setAccountNumber(sourceAccountingLine.getAccountNumber());
            
            if (!account.equals(cachedAccount)) {
                return false;
            }
        }
        return true;
    }
}
