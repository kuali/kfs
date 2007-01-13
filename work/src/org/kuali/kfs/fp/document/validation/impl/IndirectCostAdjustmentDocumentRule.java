/*
 * Copyright 2005-2006 The Kuali Foundation.
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
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjectCode;

/**
 * Business rule(s) applicable to IndirectCostAdjustment documents.
 * 
 * 
 */
public class IndirectCostAdjustmentDocumentRule extends TransactionalDocumentRuleBase implements IndirectCostAdjustmentDocumentRuleConstants {

    /**
     * Overrides to only disallow zero
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isAmountValid(TransactionalDocument, AccountingLine)
     */
    @Override
    public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
        boolean isValid = accountingLine.getAmount().isNonZero();
        if (!isValid) {
            GlobalVariables.getErrorMap().putError(Constants.AMOUNT_PROPERTY_NAME, KeyConstants.ERROR_ZERO_AMOUNT, "an accounting line");
            LOG.info("failing isAmountValid - zero check");
        }
        return isValid;
    }

    /**
     * same logic as
     * <code>IsDebitUtils#isDebitConsideringType(TransactionalDocumentRuleBase, TransactionalDocument, AccountingLine)</code> but
     * has the following accounting line restrictions: for grant lines(source):
     * <ol>
     * <li>only allow expense object type codes
     * </ol>
     * for receipt lines(target):
     * <ol>
     * <li>only allow income object type codes
     * </ol>
     * 
     * @see IsDebitUtils#isDebitConsideringType(TransactionalDocumentRuleBase, TransactionalDocument, AccountingLine)
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) throws IllegalStateException {

        if (!(accountingLine.isSourceAccountingLine() && isExpense(accountingLine)) && !(accountingLine.isTargetAccountingLine() && isIncome(accountingLine))) {
            throw new IllegalStateException(IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage);
        }

        return IsDebitUtils.isDebitConsideringType(this, transactionalDocument, accountingLine);
    }

    /**
     * @see org.kuali.core.rule.AccountingLineRule#isObjectSubTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        boolean valid = super.isObjectSubTypeAllowed(accountingLine);
        if (valid) {
            KualiParameterRule rule = getParameterRule(INDIRECT_COST_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING, RESTRICTED_SUB_TYPE_GROUP_CODES);
            String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubTypeCode();

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            valid = !rule.failsRule(objectSubTypeCode);

            if (!valid) {
                reportError(PropertyConstants.FINANCIAL_OBJECT_CODE, KeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_INVALID_OBJ_SUB_TYPE, objectCode.getFinancialObjectCode(), objectSubTypeCode);
            }
        }
        return valid;
    }

    /**
     * @see org.kuali.core.rule.AccountingLineRule#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        boolean valid = super.isObjectTypeAllowed(accountingLine);

        if (valid) {
            KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(INDIRECT_COST_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING, RESTRICTED_OBJECT_TYPE_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();

            String objectTypeCode = objectCode.getFinancialObjectTypeCode();

            valid = !rule.failsRule(objectTypeCode);
            if (!valid) {
                // add message
                GlobalVariables.getErrorMap().putError(PropertyConstants.FINANCIAL_OBJECT_CODE, KeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_INVALID_OBJECT_TYPE_CODE, new String[] { objectCode.getFinancialObjectCode(), objectTypeCode });
            }
        }

        return valid;
    }

    /**
     * KULEDOCS-1406: show account not allowed message before expired account
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        boolean valid = processCommonCustomAccountingLineBusinessRules(accountingLine);
        if (valid) {
            valid = super.processAddAccountingLineBusinessRules(transactionalDocument, accountingLine);
        }
        return valid;
    }

    /**
     * KULEDOCS-1406: show account not allowed message before expired account
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processReviewAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processReviewAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        boolean valid = processCommonCustomAccountingLineBusinessRules(accountingLine);
        if (valid) {
            valid = super.processReviewAccountingLineBusinessRules(transactionalDocument, accountingLine);
        }
        return valid;
    }

    /**
     * KULEDOCS-1406: show account not allowed message before expired account
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processUpdateAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processUpdateAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine, AccountingLine updatedAccountingLine) {
        boolean valid = processCommonCustomAccountingLineBusinessRules(accountingLine);
        if (valid) {
            valid = super.processUpdateAccountingLineBusinessRules(transactionalDocument, accountingLine, updatedAccountingLine);
        }
        return valid;
    }

    /**
     * provides centralized entry point to perform custom common accounting line validation
     * 
     * @param accountingLine
     * @return boolean
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
     * checks to see if source (grant) account references an ICR account
     * 
     * @param accountingLine
     * @return true if the grant account references an ICR account
     */
    private boolean isAccountAllowed(AccountingLine accountingLine) {
        boolean isValid = true;
        if (isValid && accountingLine.isSourceAccountingLine()) {
            String icrAccount = accountingLine.getAccount().getIndirectCostRecoveryAcctNbr();
            isValid &= StringUtils.isNotBlank(icrAccount);
            if (!isValid) {
                reportError(PropertyConstants.ACCOUNT, KeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_GRANT_INVALID_ACCOUNT, accountingLine.getAccountNumber());
            }
        }
        return isValid;
    }

    /**
     * ensures that:
     * <ol>
     * <li>"GRANT" chart of accounts reference an ICR expense object
     * <li>"RECEIPT" chart of accounts reference an ICR income object
     * </ol>
     * 
     * @param accountingLine
     * @return true if the chart of account code is allowed on the ICA
     */
    private boolean isChartOfAccountsAllowed(AccountingLine accountingLine) {
        boolean isValid = true;

        if (accountingLine.isSourceAccountingLine()) {
            String icrExpense = accountingLine.getChart().getIcrExpenseFinancialObjectCd();
            isValid &= StringUtils.isNotBlank(icrExpense);
            if (!isValid) {
                reportError(PropertyConstants.CHART_OF_ACCOUNTS_CODE, KeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_GRANT_INVALID_CHART_OF_ACCOUNTS, new String[] { accountingLine.getChartOfAccountsCode() });
            }
        }
        else {
            String icrIncome = accountingLine.getChart().getIcrIncomeFinancialObjectCode();
            isValid &= StringUtils.isNotBlank(icrIncome);
            if (!isValid) {
                reportError(PropertyConstants.CHART_OF_ACCOUNTS_CODE, KeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_RECEIPT_INVALID_CHART_OF_ACCOUNTS, new String[] { accountingLine.getChartOfAccountsCode() });
            }
        }

        return isValid;
    }
}