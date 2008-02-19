/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.financial.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;

/**
 * Business rule(s) applicable to IndirectCostAdjustment documents.
 */
public class IndirectCostAdjustmentDocumentRule extends AccountingDocumentRuleBase implements IndirectCostAdjustmentDocumentRuleConstants {

    /**
     * Overrides to only disallow zero.  Indirect Cost Adjustment documents can contain accounting lines with positive and 
     * negative amounts, but cannot contains lines with amounts of zero.
     * 
     * @param document The document associated with the accounting line being validated.
     * @param accountingLine The accounting line whose amount is being validated.
     * @return True if the amount is non zero, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isAmountValid(FinancialDocument, AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        boolean isValid = accountingLine.getAmount().isNonZero();
        if (!isValid) {
            GlobalVariables.getErrorMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, KFSKeyConstants.ERROR_ZERO_AMOUNT, "an accounting line");
            LOG.info("failing isAmountValid - zero check");
        }
        return isValid;
    }

    /**
     * This method is overridden to modify the order of the business rule checks performed when adding an accounting
     * line, to influence the order of any corresponding error messages.  This is accomplished by calling a custom 
     * accounting line rule method prior to calling the general rule checks in the parent.
     * 
     * KULEDOCS-1406: show "account not allowed" error message before "account is expired" error message
     * 
     * @param transactionalDocument The document the new accounting line will be added to.
     * @param accountingLine The new accounting line to add.
     * @return True if all the business rules passed, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processAddAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     * @see #processCommonCustomAccountingLineBusinessRules(AccountingLine)
     * @see #isChartOfAccountsAllowed(AccountingLine)
     * @see #isAccountAllowed(AccountingLine)
     */
    @Override
    public boolean processAddAccountingLineBusinessRules(AccountingDocument transactionalDocument, AccountingLine accountingLine) {
        boolean valid = processCommonCustomAccountingLineBusinessRules(accountingLine);
        if (valid) {
            valid = super.processAddAccountingLineBusinessRules(transactionalDocument, accountingLine);
        }
        return valid;
    }

    /**
     * This method is overridden to modify the order of the business rule checks performed when reviewing an 
     * accounting line, to influence the order of any corresponding error messages.  This is accomplished by 
     * calling a custom accounting line rule method prior to calling the general rule checks in the parent.
     * 
     * KULEDOCS-1406: show "account not allowed" error message before "account is expired" error message
     * 
     * @param transactionalDocument The document containing the accounting line to be reviewed.
     * @param accountingLine The accounting line being reviewed.
     * @return True if all the business rules passed, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processReviewAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     * @see #processCommonCustomAccountingLineBusinessRules(AccountingLine)
     * @see #isChartOfAccountsAllowed(AccountingLine)
     * @see #isAccountAllowed(AccountingLine)
     */
    @Override
    public boolean processReviewAccountingLineBusinessRules(AccountingDocument transactionalDocument, AccountingLine accountingLine) {
        boolean valid = processCommonCustomAccountingLineBusinessRules(accountingLine);
        if (valid) {
            valid = super.processReviewAccountingLineBusinessRules(transactionalDocument, accountingLine);
        }
        return valid;
    }

    /**
     * This method is overridden to modify the order of the business rule checks performed when updating an accounting
     * line, to influence the order of any corresponding error messages.  This is accomplished by calling a custom 
     * accounting line rule method prior to calling the general rule checks in the parent.
     * 
     * KULEDOCS-1406: show "account not allowed" error message before "account is expired" error message
     * 
     * @param transactionalDocument The document containing the accounting line to be updated.
     * @param accountingLine The accounting line being updated.
     * @return True if all the business rules passed, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processUpdateAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     * @see #processCommonCustomAccountingLineBusinessRules(AccountingLine)
     * @see #isChartOfAccountsAllowed(AccountingLine)
     * @see #isAccountAllowed(AccountingLine)
     */
    @Override
    public boolean processUpdateAccountingLineBusinessRules(AccountingDocument transactionalDocument, AccountingLine accountingLine, AccountingLine updatedAccountingLine) {
        boolean valid = processCommonCustomAccountingLineBusinessRules(accountingLine);
        if (valid) {
            valid = super.processUpdateAccountingLineBusinessRules(transactionalDocument, accountingLine, updatedAccountingLine);
        }
        return valid;
    }

    /**
     * This method provides a centralized entry point to perform custom common accounting line validation.
     * 
     * @param accountingLine The accounting line to run the business rule checks against.
     * @return True if all the custom business rules pass, false otherwise.
     */
    protected boolean processCommonCustomAccountingLineBusinessRules(AccountingLine accountingLine) {
        // refresh line since this document calls the custom rules first. KULEDOCS-1406
        accountingLine.refresh();
        boolean isValid = isChartOfAccountsAllowed(accountingLine);
        if (isValid) {
            isValid = isAccountAllowed(accountingLine);
        }
        return isValid;
    }

    /**
     * This method checks to see if the account associated with the accounting line given is allowed.  This is determined 
     * by checking to see if the source (grant) account references an indirect cost recovery (ICR) account.
     * 
     * @param accountingLine The accounting line which contains the account to be validated.
     * @return True if the grant account references an indirect cost recovery account.
     */
    private boolean isAccountAllowed(AccountingLine accountingLine) {
        boolean isValid = true;
        if (isValid && accountingLine.isSourceAccountingLine()) {
            String icrAccount = accountingLine.getAccount().getIndirectCostRecoveryAcctNbr();
            isValid &= StringUtils.isNotBlank(icrAccount);
            if (!isValid) {
                reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_GRANT_INVALID_ACCOUNT, accountingLine.getAccountNumber());
            }
        }
        return isValid;
    }

    /**
     * This method ensures that:
     * <ol>
     * <li>"GRANT" chart of accounts reference an indirect cost recovery (ICR) expense object
     * <li>"RECEIPT" chart of accounts reference an indirect cost recovery (ICR) income object
     * </ol>
     * 
     * @param accountingLine The accounting line the chard of accounts will be retrieved from.
     * @return True if the chart of account code is allowed on the indirect cost adjustment (ICA), false otherwise.
     */
    private boolean isChartOfAccountsAllowed(AccountingLine accountingLine) {
        boolean isValid = true;

        if (accountingLine.isSourceAccountingLine()) {
            String icrExpense = accountingLine.getChart().getIcrExpenseFinancialObjectCd();
            isValid &= StringUtils.isNotBlank(icrExpense);
            if (!isValid) {
                reportError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_GRANT_INVALID_CHART_OF_ACCOUNTS, new String[] { accountingLine.getChartOfAccountsCode() });
            }
        }
        else {
            String icrIncome = accountingLine.getChart().getIcrIncomeFinancialObjectCode();
            isValid &= StringUtils.isNotBlank(icrIncome);
            if (!isValid) {
                reportError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_RECEIPT_INVALID_CHART_OF_ACCOUNTS, new String[] { accountingLine.getChartOfAccountsCode() });
            }
        }

        return isValid;
    }
}