/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import static org.kuali.kfs.KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.KFSConstants.TARGET_ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_TOTAL_CHANGED;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.GENERATE_TOF_GLPE_ENTRIES_PARM_NM;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_10_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_11_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_12_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_1_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_2_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_3_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_4_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_5_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_6_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_7_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_8_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_9_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.TRANSFER_OBJECT_CODE_PARM_NM;
import static org.kuali.module.financial.rules.TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_DOC_TYPE_CODE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.AccountingLineRuleHelperService;
import org.kuali.kfs.service.DebitDeterminerService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentTargetAccountingLine;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;
import org.kuali.module.financial.service.FiscalYearFunctionControlService;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * Business rule(s) applicable to Budget Adjustment Card document.
 */
public class BudgetAdjustmentDocumentRule extends AccountingDocumentRuleBase {

    /**
     * Validates when an accounting line is added to a budget adjustment document. Accounting line's budget adjustment amount must
     * be non zero and a budget adjustment must be allowed. In addition, the monthly amount fields must equal the current budget
     * amount and the accounting line's account must have a budget recording level and if current adjustment amount is non zero,
     * account must have an associated income stream chart and account.
     * 
     * @param financialDocument submitted document
     * @param accountingLine validated accouting line
     * @return true if validation criteria listed above is true
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean allow = true;
        BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) accountingLine;

        LOG.debug("validating accounting line # " + accountingLine.getSequenceNumber());

        /* if they have entered a base amount for line, verify it can be adjusted for the posting year */
        if (budgetAccountingLine.getBaseBudgetAdjustmentAmount().isNonZero() && !SpringContext.getBean(FiscalYearFunctionControlService.class).isBaseAmountChangeAllowed(((BudgetAdjustmentDocument) financialDocument).getPostingYear())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, KFSKeyConstants.ERROR_DOCUMENT_BA_BASE_AMOUNT_CHANGE_NOT_ALLOWED);
            allow = false;
        }

        LOG.debug("beginning monthly lines validation ");
        allow = allow && validateMonthlyLines(financialDocument, accountingLine);

        LOG.debug("beginning account number validation ");
        allow = allow && validateAccountNumber(financialDocument, accountingLine);

        LOG.debug("end validating accounting line, has errors: " + allow);

        return allow;
    }

    /**
     * Calls FinancialDocumentRuleBase.processCustomRouteDocumentBusinessRules() and also validates whether the document's
     * accounting lines' fund group and sub fund group codes associate with the 'Budget Adjustment Restriction Code'
     * 
     * @param document submitted document
     * @return true if criteria above is met
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        BudgetAdjustmentDocument baDocument = (BudgetAdjustmentDocument) document;

        if (isValid) {
            isValid = isValid && validateFundGroupAdjustmentRestrictions(baDocument);
        }

        return isValid;
    }

    /**
     * Validates the total of the monthly amount fields (if not 0) equals the current budget amount. If current budget is 0, then
     * total of monthly fields must be 0.
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine validated accounting line
     * @return true if monthly amount fields equals the current budget amount
     */
    public boolean validateMonthlyLines(AccountingDocument financialDocument, AccountingLine accountingLine) {
        BudgetAdjustmentAccountingLine budgetAdjustmentAccountingLine = (BudgetAdjustmentAccountingLine) accountingLine;

        boolean validMonthlyLines = true;

        KualiDecimal monthlyTotal = budgetAdjustmentAccountingLine.getMonthlyLinesTotal();
        if (monthlyTotal.isNonZero() && monthlyTotal.compareTo(budgetAdjustmentAccountingLine.getCurrentBudgetAdjustmentAmount()) != 0) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CURRENT_BUDGET_ADJUSTMENT_AMOUNT, KFSKeyConstants.ERROR_DOCUMENT_BA_MONTH_TOTAL_NOT_EQUAL_CURRENT);
            validMonthlyLines = false;
        }

        return validMonthlyLines;
    }

    /**
     * Retrieves the fund group and sub fund group for each accounting line. Then verifies that the codes associated with the
     * 'Budget Adjustment Restriction Code' field are met.
     * 
     * @param financialDocument submitted accounting document
     * @return true if fund group and sub fund group for each accounting line if restrictions are met
     */
    public boolean validateFundGroupAdjustmentRestrictions(AccountingDocument financialDocument) {
        BudgetAdjustmentDocument baDocument = (BudgetAdjustmentDocument) financialDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        boolean isAdjustmentAllowed = true;

        List accountingLines = new ArrayList();
        accountingLines.addAll(baDocument.getSourceAccountingLines());
        accountingLines.addAll(baDocument.getTargetAccountingLines());

        // fund group is global restriction
        boolean restrictedToSubFund = false;
        boolean restrictedToChart = false;
        boolean restrictedToOrg = false;
        boolean restrictedToAccount = false;

        // fields to help with error messages
        String accountRestrictingSubFund = "";
        String accountRestrictingChart = "";
        String accountRestrictingOrg = "";
        String accountRestrictingAccount = "";

        // first find the restriction level required by the fund or sub funds used on document
        String restrictionLevel = "";
        for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            SubFundGroup subFund = line.getAccount().getSubFundGroup();
            if (!KFSConstants.BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_NONE.equals(subFund.getFundGroupBudgetAdjustmentRestrictionLevelCode())) {
                restrictionLevel = subFund.getFundGroupBudgetAdjustmentRestrictionLevelCode();
                restrictedToSubFund = true;
                accountRestrictingSubFund = line.getAccountNumber();
            }
            else {
                restrictionLevel = subFund.getFundGroup().getFundGroupBudgetAdjustmentRestrictionLevelCode();
            }

            if (KFSConstants.BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_CHART.equals(restrictionLevel)) {
                restrictedToChart = true;
                accountRestrictingChart = line.getAccountNumber();
            }
            else if (KFSConstants.BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_ORGANIZATION.equals(restrictionLevel)) {
                restrictedToOrg = true;
                accountRestrictingOrg = line.getAccountNumber();
            }
            else if (KFSConstants.BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_ACCOUNT.equals(restrictionLevel)) {
                restrictedToAccount = true;
                accountRestrictingAccount = line.getAccountNumber();
            }

            // if we have a sub fund restriction, this overrides anything coming later
            if (restrictedToSubFund) {
                break;
            }
        }

        AccountingLineRuleHelperService accountingLineRuleUtil = SpringContext.getBean(AccountingLineRuleHelperService.class);
        String fundLabel = accountingLineRuleUtil.getFundGroupCodeLabel();
        String subFundLabel = accountingLineRuleUtil.getSubFundGroupCodeLabel();
        String chartLabel = accountingLineRuleUtil.getChartLabel();
        String orgLabel = accountingLineRuleUtil.getOrganizationCodeLabel();
        String acctLabel = accountingLineRuleUtil.getAccountLabel();

        /*
         * now iterate through the accounting lines again and check each record against the previous to verify the restrictions are
         * met
         */
        BudgetAdjustmentAccountingLine previousLine = null;
        for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();

            if (previousLine != null) {
                String currentFundGroup = line.getAccount().getSubFundGroup().getFundGroupCode();
                String previousFundGroup = previousLine.getAccount().getSubFundGroup().getFundGroupCode();

                if (!currentFundGroup.equals(previousFundGroup)) {
                    errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_MIXED_FUND_GROUPS);
                    isAdjustmentAllowed = false;
                    break;
                }

                if (restrictedToSubFund) {
                    if (!line.getAccount().getSubFundGroupCode().equals(previousLine.getAccount().getSubFundGroupCode())) {
                        errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingSubFund, subFundLabel });
                        isAdjustmentAllowed = false;
                        break;
                    }
                }

                if (restrictedToChart) {
                    if (!line.getChartOfAccountsCode().equals(previousLine.getChartOfAccountsCode())) {
                        if (restrictedToSubFund) {
                            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingChart, subFundLabel + " and " + chartLabel });
                        }
                        else {
                            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingChart, fundLabel + " and " + chartLabel });
                        }
                        isAdjustmentAllowed = false;
                        break;
                    }
                }

                if (restrictedToOrg) {
                    if (!line.getAccount().getOrganizationCode().equals(previousLine.getAccount().getOrganizationCode())) {
                        if (restrictedToSubFund) {
                            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingOrg, subFundLabel + " and " + orgLabel });
                        }
                        else {
                            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingOrg, fundLabel + " and " + orgLabel });
                        }
                        isAdjustmentAllowed = false;
                        break;
                    }
                }

                if (restrictedToAccount) {
                    if (!line.getAccountNumber().equals(previousLine.getAccountNumber())) {
                        errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingAccount, acctLabel });
                        isAdjustmentAllowed = false;
                        break;
                    }
                }
            }

            previousLine = line;
        }

        return isAdjustmentAllowed;
    }

    /**
     * Checks account number restrictions, including restrictions in parameters table.
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine accounting line with validated account number
     * @return true if account number has a budget recording level and if the current adjustment amount is non-zero, account must
     *         have an associated income stream chart and account
     */
    public boolean validateAccountNumber(AccountingDocument financialDocument, AccountingLine accountingLine) {
        BudgetAdjustmentDocument baDocument = (BudgetAdjustmentDocument) financialDocument;
        BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) accountingLine;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = KFSPropertyConstants.ACCOUNT_NUMBER;
        boolean accountNumberAllowed = true;

        // check account has a budget recording level
        if (StringUtils.isBlank(accountingLine.getAccount().getBudgetRecordingLevelCode()) || ACCOUNT_NUMBER.BUDGET_LEVEL_NO_BUDGET.equals(accountingLine.getAccount().getBudgetRecordingLevelCode())) {
            errors.putError(errorKey, KFSKeyConstants.ERROR_DOCUMENT_BA_NON_BUDGETED_ACCOUNT, accountingLine.getAccountNumber());
            accountNumberAllowed = false;
        }

        // if current adjustment amount is non zero, account must have an associated income stream chart and account
        if (budgetAccountingLine.getCurrentBudgetAdjustmentAmount().isNonZero()) {
            if (ObjectUtils.isNull(accountingLine.getAccount().getIncomeStreamAccount())) {
                errors.putError(errorKey, KFSKeyConstants.ERROR_DOCUMENT_BA_NO_INCOME_STREAM_ACCOUNT, accountingLine.getAccountNumber());
                accountNumberAllowed = false;
            }
        }

        return accountNumberAllowed;
    }

    /**
     * Override needed to check the current, base, and monthly amounts.
     * 
     * @param document submitted accounting document
     * @param accountingLine accounting line where current, base, and monthly amounts are being validated.
     * @see org.kuali.core.rule.AccountingLineRule#isAmountValid(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        boolean amountValid = true;

        BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) accountingLine;

        // check amounts both current and base amounts are not zero
        if (budgetAccountingLine.getCurrentBudgetAdjustmentAmount().isZero() && budgetAccountingLine.getBaseBudgetAdjustmentAmount().isZero()) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, KFSKeyConstants.ERROR_BA_AMOUNT_ZERO);
            amountValid = false;
        }

        // if not an error correction, all amounts must be positive
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        if (!isDebitUtils.isErrorCorrection(document)) {
            amountValid &= checkAmountSign(budgetAccountingLine.getCurrentBudgetAdjustmentAmount(), KFSPropertyConstants.CURRENT_BUDGET_ADJUSTMENT_AMOUNT, "Current");
            amountValid &= checkAmountSign(budgetAccountingLine.getBaseBudgetAdjustmentAmount().kualiDecimalValue(), KFSPropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, "Base");
            amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth1LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_1_LINE_AMOUNT, "Month 1");
            amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth2LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_2_LINE_AMOUNT, "Month 2");
            amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth3LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_3_LINE_AMOUNT, "Month 3");
            amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth4LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_4_LINE_AMOUNT, "Month 4");
            amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth5LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_5_LINE_AMOUNT, "Month 5");
            amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth6LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_6_LINE_AMOUNT, "Month 6");
            amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth7LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_7_LINE_AMOUNT, "Month 7");
            amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth8LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_8_LINE_AMOUNT, "Month 8");
            amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth8LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_9_LINE_AMOUNT, "Month 9");
            amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth10LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_10_LINE_AMOUNT, "Month 10");
            amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth10LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_11_LINE_AMOUNT, "Month 11");
            amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth12LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_12_LINE_AMOUNT, "Month 12");
        }

        return amountValid;
    }

    /**
     * In order for the BA document to balance: Total of Base Income Adjustments - Base Expense Adjustments on decrease side must
     * equal Total of Base Income Adjustments - Base Expense Adjustments on increase side Total of Current Income Adjustments - Base
     * Current Adjustments on decrease side must equal Total of Current Income Adjustments - Base Current Adjustments on increase
     * side
     * 
     * @param sumbmitted financial document
     * @return true if base amounts are equal and current amounts balance and income stream balance should equal 0
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument financialDocument) {
        BudgetAdjustmentDocument baDocument = (BudgetAdjustmentDocument) financialDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        boolean balanced = true;

        // check base amounts are equal
        if (baDocument.getSourceBaseBudgetTotal().compareTo(baDocument.getTargetBaseBudgetTotal()) != 0) {
            GlobalVariables.getErrorMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_BASE_AMOUNTS_BALANCED);
            balanced = false;
        }

        // check current amounts balance, income stream balance Map should add to 0
        Map incomeStreamMap = baDocument.buildIncomeStreamBalanceMap();
        KualiDecimal totalCurrentAmount = new KualiDecimal(0);
        for (Iterator iter = incomeStreamMap.values().iterator(); iter.hasNext();) {
            KualiDecimal streamAmount = (KualiDecimal) iter.next();
            totalCurrentAmount = totalCurrentAmount.add(streamAmount);
        }

        if (totalCurrentAmount.isNonZero()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_CURRENT_AMOUNTS_BALANCED);
            balanced = false;
        }

        return balanced;
    }

    /**
     * Helper method to check if an amount is negative and add an error if not.
     * 
     * @param amount to check
     * @param propertyName to add error under
     * @param label for error
     * @return boolean indicating if the value has the requested sign
     */
    private boolean checkAmountSign(KualiDecimal amount, String propertyName, String label) {
        boolean correctSign = true;

        if (amount.isNegative()) {
            GlobalVariables.getErrorMap().putError(propertyName, KFSKeyConstants.ERROR_BA_AMOUNT_NEGATIVE, label);
            correctSign = false;
        }

        return correctSign;
    }

    /**
     * BA document does not have to have source accounting lines. In the case of setting up a budget for a new account, only targets
     * line (increase section) are setup.
     * 
     * @param financialDocument submitted financial document
     * @return true if both source and target lines are NOT empty
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(AccountingDocument financialDocument) {
        // check that both source and target are not empty, in which case is an error
        if (financialDocument.getSourceAccountingLines().isEmpty() && financialDocument.getTargetAccountingLines().isEmpty()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_NO_ACCOUNTING_LINES);
            return false;
        }

        return true;
    }


    /**
     * BA document does not have to have source accounting lines. In the case of closing out an account, only source lines (decrease
     * section) are setup.
     * 
     * @param financialDocument submitted financial document
     * @return true
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(AccountingDocument FinancialDocument) {
        return true;
    }

    /**
     * Returns true if account line totals remains unchanged from what was entered and what was persisted before
     * 
     * @param financialDocument submitted accounting document
     * @return true if account line totals remains unchanged fromw what was entered and what was persisted before
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isAccountingLineTotalsUnchanged(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isAccountingLineTotalsUnchanged(AccountingDocument financialDocument) {
        boolean isUnchanged = true;

        BudgetAdjustmentDocument persistedDocument = (BudgetAdjustmentDocument) retrievePersistedDocument(financialDocument);
        BudgetAdjustmentDocument currentDocument = (BudgetAdjustmentDocument) financialDocument;

        if (persistedDocument == null) {
            handleNonExistentDocumentWhenApproving(financialDocument);
        }
        else {
            // retrieve the persisted totals
            KualiDecimal persistedSourceCurrentBudgetTotal = persistedDocument.getSourceCurrentBudgetTotal();
            KualiInteger persistedSourceBaseBudgetTotal = persistedDocument.getSourceBaseBudgetTotal();
            KualiDecimal persistedTargetCurrentBudgetTotal = persistedDocument.getTargetCurrentBudgetTotal();
            KualiInteger persistedTargetBaseBudgetTotal = persistedDocument.getTargetBaseBudgetTotal();

            // retrieve the updated totals
            KualiDecimal currentSourceCurrentBudgetTotal = currentDocument.getSourceCurrentBudgetTotal();
            KualiInteger currentSourceBaseBudgetTotal = currentDocument.getSourceBaseBudgetTotal();
            KualiDecimal currentTargetCurrentBudgetTotal = currentDocument.getTargetCurrentBudgetTotal();
            KualiInteger currentTargetBaseBudgetTotal = currentDocument.getTargetBaseBudgetTotal();

            // make sure that totals have remained unchanged, if not, recognize that, and
            // generate appropriate error messages
            if (persistedSourceCurrentBudgetTotal.compareTo(currentSourceCurrentBudgetTotal) != 0) {
                isUnchanged = false;
                buildTotalChangeErrorMessage(SOURCE_ACCOUNTING_LINE_ERRORS, "source current budget", persistedSourceCurrentBudgetTotal, currentSourceCurrentBudgetTotal);
            }
            if (persistedSourceBaseBudgetTotal.compareTo(currentSourceBaseBudgetTotal) != 0) {
                isUnchanged = false;
                buildTotalChangeErrorMessage(SOURCE_ACCOUNTING_LINE_ERRORS, "source base budget", persistedSourceBaseBudgetTotal.kualiDecimalValue(), currentSourceBaseBudgetTotal.kualiDecimalValue());
            }
            if (persistedTargetCurrentBudgetTotal.compareTo(currentTargetCurrentBudgetTotal) != 0) {
                isUnchanged = false;
                buildTotalChangeErrorMessage(TARGET_ACCOUNTING_LINE_ERRORS, "target current budget", persistedTargetCurrentBudgetTotal, currentTargetCurrentBudgetTotal);
            }
            if (persistedTargetBaseBudgetTotal.compareTo(currentTargetBaseBudgetTotal) != 0) {
                isUnchanged = false;
                buildTotalChangeErrorMessage(TARGET_ACCOUNTING_LINE_ERRORS, "target base budget", persistedTargetBaseBudgetTotal.kualiDecimalValue(), currentTargetBaseBudgetTotal.kualiDecimalValue());
            }
        }

        return isUnchanged;
    }

    /**
     * Builds the error message for when totals have changed.
     * 
     * @param propertyName name of property
     * @param sectionTitle title of section
     * @param persistedSourceLineTotal previously persisted source line total
     * @param currentSourceLineTotal current entered source line total
     */
    private void buildTotalChangeErrorMessage(String propertyName, String sectionTitle, KualiDecimal persistedSourceLineTotal, KualiDecimal currentSourceLineTotal) {
        String persistedTotal = (String) new CurrencyFormatter().format(persistedSourceLineTotal);
        String currentTotal = (String) new CurrencyFormatter().format(currentSourceLineTotal);

        GlobalVariables.getErrorMap().putError(propertyName, ERROR_DOCUMENT_ACCOUNTING_LINE_TOTAL_CHANGED, new String[] { sectionTitle, persistedTotal, currentTotal });
    }
}
