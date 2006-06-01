/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.util.SufficientFundsItemHelper.SufficientFundsItem;

/**
 * Business rule(s) applicable to IndirectCostAdjustment documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class IndirectCostAdjustmentDocumentRule extends TransactionalDocumentRuleBase implements
        IndirectCostAdjustmentDocumentRuleConstants {

    /**
     * Overrides to only disallow zero
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isAmountValid(TransactionalDocument, AccountingLine)
     */
    @Override
    public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
        boolean isValid = accountingLine.getAmount().isNonZero();
        if (!isValid) {
            GlobalVariables.getErrorMap().put(Constants.AMOUNT_PROPERTY_NAME, KeyConstants.ERROR_ZERO_AMOUNT, "an accounting line");
            LOG.info("failing isAmountValid - zero check");
        }
        return isValid;
    }

    /**
     * Overrrides default implementation to do the following: a line is considered debit if
     * <ol>
     * <li> is a source line && isExpenseOrAsset && is positive amount
     * <li> is a source line && IncomeOrLiability && is positive amount
     * <li> is a target line && isExpenseOrAsset && is positive amount
     * <li> is a target line && IncomeOrLiability && is a negative amount
     * </ol>
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDebit(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isDebit(AccountingLine accountingLine) throws IllegalStateException {

        boolean isDebit = false;
        boolean isPositive = accountingLine.getAmount().isPositive();
        // source
        if (isSourceAccountingLine(accountingLine)) {
            if (isExpenseOrAsset(accountingLine)) {
                isDebit = isPositive;
            }
            else if (isIncomeOrLiability(accountingLine)) {
                isDebit = isPositive;
            }
            else {
                throw new IllegalStateException(objectTypeCodeIllegalStateExceptionMessage);
            }
        }
        // target line
        else {
            if (isExpenseOrAsset(accountingLine)) {
                isDebit = isPositive;
            }
            else if (isIncomeOrLiability(accountingLine)) {
                isDebit = !isPositive;
            }
            else {
                throw new IllegalStateException(objectTypeCodeIllegalStateExceptionMessage);
            }
        }
        return isDebit;
    }

    /**
     * @see org.kuali.core.rule.AccountingLineRule#isObjectSubTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        boolean valid = super.isObjectSubTypeAllowed(accountingLine);
        if (valid) {
            KualiParameterRule rule = getParameterRule(INDIRECT_COST_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING,
                    RESTRICTED_SUB_TYPE_GROUP_CODES);
            String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubTypeCode();

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            valid = !rule.failsRule(objectSubTypeCode);

            if (!valid) {
                reportError(PropertyConstants.FINANCIAL_OBJECT_CODE,
                        KeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_INVALID_OBJ_SUB_TYPE, new String[] {
                                objectCode.getFinancialObjectCode(), objectSubTypeCode });
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
            KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                    INDIRECT_COST_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING, RESTRICTED_OBJECT_TYPE_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();

            String objectTypeCode = objectCode.getFinancialObjectTypeCode();

            valid = !rule.failsRule(objectTypeCode);
            if (!valid) {
                // add message
                GlobalVariables.getErrorMap().put(PropertyConstants.FINANCIAL_OBJECT_CODE,
                        KeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_INVALID_OBJECT_TYPE_CODE,
                        new String[] { objectCode.getFinancialObjectCode(), objectTypeCode });
            }
        }

        return valid;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(TransactionalDocument,
     *      AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return processCommonCustomAccountingLineBusinessRules(accountingLine);
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(TransactionalDocument,
     *      AccountingLine)
     */
    @Override
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return processCommonCustomAccountingLineBusinessRules(accountingLine);
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(TransactionalDocument,
     *      AccountingLine, AccountingLine)
     */
    @Override
    public boolean processCustomUpdateAccountingLineBusinessRules(TransactionalDocument document,
            AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return processCommonCustomAccountingLineBusinessRules(updatedAccountingLine);
    }

    /**
     * provides centralized entry point to perform custom common accounting line validation
     * 
     * @param accountingLine
     * @return
     */

    protected boolean processCommonCustomAccountingLineBusinessRules(AccountingLine accountingLine) {
        boolean isValid = isChartOfAccountsAllowed(accountingLine);
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
     * @return
     */
    private boolean isChartOfAccountsAllowed(AccountingLine accountingLine) {
        boolean isValid = true;

        if (isSourceAccountingLine(accountingLine)) {
            String icrExpense=accountingLine.getChart().getIcrExpenseFinancialObjectCd();
            isValid &= StringUtils.isNotBlank(icrExpense);
            if (!isValid) {
                reportError(PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                        KeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_GRANT_INVALID_CHART_OF_ACCOUNTS,
                        new String[] { accountingLine.getChartOfAccountsCode() });
            }
        }
        else {
            String icrIncome=accountingLine.getChart().getIcrIncomeFinancialObjectCode();
            isValid &= StringUtils.isNotBlank(icrIncome);
            if (!isValid) {
                reportError(PropertyConstants.CHART_OF_ACCOUNTS_CODE,
                        KeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_RECEIPT_INVALID_CHART_OF_ACCOUNTS,
                        new String[] { accountingLine.getChartOfAccountsCode() });
            }
        }

        return isValid;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processSourceAccountingLineSufficientFundsCheckingPreparation(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.SourceAccountingLine)
     */
    @Override
    protected SufficientFundsItem processSourceAccountingLineSufficientFundsCheckingPreparation(
            TransactionalDocument transactionalDocument, SourceAccountingLine sourceAccountingLine) {
        return processAccountingLineSufficientFundsCheckingPreparation(sourceAccountingLine);
    }


    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processTargetAccountingLineSufficientFundsCheckingPreparation(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.TargetAccountingLine)
     */
    @Override
    protected SufficientFundsItem processTargetAccountingLineSufficientFundsCheckingPreparation(
            TransactionalDocument transactionalDocument, TargetAccountingLine targetAccountingLine) {
        return processAccountingLineSufficientFundsCheckingPreparation(targetAccountingLine);
    }

    /**
     * Prepares the input item that will be used for sufficient funds checking.
     * 
     * fi_dica:lp_proc_grant_ln,lp_proc_rcpt_ln conslidated
     * 
     * @param accountingLine
     * @return <code>SufficientFundsItem</code>
     */
    private final SufficientFundsItem processAccountingLineSufficientFundsCheckingPreparation(AccountingLine accountingLine) {
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String accountNumber = accountingLine.getAccountNumber();
        String accountSufficientFundsCode = accountingLine.getAccount().getAccountSufficientFundsCode();
        String financialObjectCode = accountingLine.getFinancialObjectCode();
        String financialObjectLevelCode = accountingLine.getObjectCode().getFinancialObjectLevelCode();
        Integer fiscalYear = accountingLine.getPostingYear();
        String financialObjectTypeCode = accountingLine.getObjectTypeCode();
        KualiDecimal lineAmount = accountingLine.getAmount();
        String offsetDebitCreditCode = null;
        // fi_dica:lp_proc_grant_ln.36-2...62-2
        // fi_dica:lp_proc_rcpt_ln.36-2...69-2
        if (isDebit(accountingLine)) {
            offsetDebitCreditCode = Constants.GL_CREDIT_CODE;
        }
        else {
            offsetDebitCreditCode = Constants.GL_DEBIT_CODE;
        }
        lineAmount = lineAmount.abs();

        String sufficientFundsObjectCode = SpringServiceLocator.getSufficientFundsService().getSufficientFundsObjectCode(
                chartOfAccountsCode, financialObjectCode, accountSufficientFundsCode, financialObjectLevelCode);
        SufficientFundsItem item = buildSufficentFundsItem(accountNumber, accountSufficientFundsCode, lineAmount,
                chartOfAccountsCode, sufficientFundsObjectCode, offsetDebitCreditCode, financialObjectCode,
                financialObjectLevelCode, fiscalYear, financialObjectTypeCode);
        return item;
    }
}