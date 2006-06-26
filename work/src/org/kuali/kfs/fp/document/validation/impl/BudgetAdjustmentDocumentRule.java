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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentTargetAccountingLine;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;


/**
 * Business rule(s) applicable to Budget Adjustment Card document.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAdjustmentDocumentRule extends TransactionalDocumentRuleBase {

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        boolean allow = true;

        LOG.debug("validating accounting line # " + accountingLine.getSequenceNumber());

        LOG.debug("beginning monthly lines validation ");
        allow = allow && validateMonthlyLines(transactionalDocument, accountingLine);

        LOG.debug("beginning object code validation ");
        allow = allow && validateObjectCode(transactionalDocument, accountingLine);

        LOG.debug("beginning account number validation ");
        allow = allow & validateAccountNumber(transactionalDocument, accountingLine);

        LOG.debug("end validating accounting line, has errors: " + allow);

        return allow;
    }


    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        if (isValid) {
            isValid &= isAllAccountingLinesMatchingBudgetYear((TransactionalDocument) document);
        }

        return isValid;
    }


    /**
     * The budget adjustment document creates GL pending entries much differently that common tp-edocs. The glpes are created for
     * BB, CB, and MB balance types. Up to 14 entries per line can be created. Along with this, the BA will create TOF entries if
     * needed to move funding.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processGenerateGeneralLedgerPendingEntries(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean processGenerateGeneralLedgerPendingEntries(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry();

        // determine if we are on increase or decrease side
        KualiDecimal amountSign = new KualiDecimal(1);
        if (accountingLine instanceof SourceAccountingLine) {
            amountSign = new KualiDecimal(-1);
        }

        BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) accountingLine;
        Integer currentFiscalYear = SpringServiceLocator.getDateTimeService().getCurrentFiscalYear();
        /* Create Base Budget GLPE if base amount != 0 */
        if (budgetAccountingLine.getBaseBudgetAdjustmentAmount().isNonZero()) {
            populateExplicitGeneralLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, explicitEntry);

            /* D/C code is empty for BA, set correct balance type, correct amount */
            explicitEntry.setTransactionDebitCreditCode("");
            explicitEntry.setFinancialBalanceTypeCode(Constants.BALANCE_TYPE_BASE_BUDGET);
            explicitEntry.setTransactionLedgerEntryAmount(budgetAccountingLine.getBaseBudgetAdjustmentAmount().multiply(amountSign).kualiDecimalValue());
            // set fiscal period, if next fiscal year set to 01, else leave to current period
            if (currentFiscalYear.equals(transactionalDocument.getPostingYear() - 1)) {
                explicitEntry.setUniversityFiscalPeriodCode("01");
            }

            // add the new explicit entry to the document now
            transactionalDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

            // increment the sequence counter
            sequenceHelper.increment();
        }

        /* Create Current Budget GLPE if current amount != 0 */
        if (budgetAccountingLine.getCurrentBudgetAdjustmentAmount().isNonZero()) {
            populateExplicitGeneralLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, explicitEntry);

            /* D/C code is empty for BA, set correct balance type, correct amount */
            explicitEntry.setTransactionDebitCreditCode("");
            explicitEntry.setFinancialBalanceTypeCode(Constants.BALANCE_TYPE_CURRENT_BUDGET);
            explicitEntry.setTransactionLedgerEntryAmount(budgetAccountingLine.getCurrentBudgetAdjustmentAmount().multiply(amountSign));
            // set fiscal period, if next fiscal year set to 01, else leave to current period
            if (currentFiscalYear.equals(transactionalDocument.getPostingYear() - 1)) {
                explicitEntry.setUniversityFiscalPeriodCode("01");
            }

            // add the new explicit entry to the document now
            transactionalDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

            // increment the sequence counter
            sequenceHelper.increment();

            // create montly lines (MB)
            if (budgetAccountingLine.getFinancialDocumentMonth1LineAmount().isNonZero()) {
                success &= createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "01", budgetAccountingLine.getFinancialDocumentMonth1LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth2LineAmount().isNonZero()) {
                success &= createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "02", budgetAccountingLine.getFinancialDocumentMonth2LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth3LineAmount().isNonZero()) {
                success &= createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "03", budgetAccountingLine.getFinancialDocumentMonth3LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth4LineAmount().isNonZero()) {
                success &= createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "04", budgetAccountingLine.getFinancialDocumentMonth4LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth5LineAmount().isNonZero()) {
                success &= createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "05", budgetAccountingLine.getFinancialDocumentMonth5LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth6LineAmount().isNonZero()) {
                success &= createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "06", budgetAccountingLine.getFinancialDocumentMonth6LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth7LineAmount().isNonZero()) {
                success &= createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "07", budgetAccountingLine.getFinancialDocumentMonth7LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth8LineAmount().isNonZero()) {
                success &= createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "08", budgetAccountingLine.getFinancialDocumentMonth8LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth9LineAmount().isNonZero()) {
                success &= createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "09", budgetAccountingLine.getFinancialDocumentMonth9LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth10LineAmount().isNonZero()) {
                success &= createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "10", budgetAccountingLine.getFinancialDocumentMonth10LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth11LineAmount().isNonZero()) {
                success &= createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "11", budgetAccountingLine.getFinancialDocumentMonth11LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth12LineAmount().isNonZero()) {
                success &= createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "12", budgetAccountingLine.getFinancialDocumentMonth12LineAmount().multiply(amountSign));
            }
        }

        // create TOF entries
        success &= processTransferOfFundsGeneralLedgerPendingEntries(transactionalDocument, accountingLine, sequenceHelper);

        return success;
    }

    /**
     * Helper method for creating monthly buget pending entry lines.
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @param sequenceHelper
     * @param fiscalPeriod
     * @param monthAmount
     * @return boolean
     */
    private boolean createMonthlyBudgetGLPE(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String fiscalPeriod, KualiDecimal monthAmount) {
        boolean success = true;

        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        populateExplicitGeneralLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, explicitEntry);

        /* D/C code is empty for BA, set correct balance type, correct amount */
        explicitEntry.setTransactionDebitCreditCode("");
        explicitEntry.setFinancialBalanceTypeCode(Constants.BALANCE_TYPE_MONTHLY_BUDGET);
        explicitEntry.setTransactionLedgerEntryAmount(monthAmount);
        explicitEntry.setUniversityFiscalPeriodCode(fiscalPeriod);

        // add the new explicit entry to the document now
        transactionalDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

        // increment the sequence counter
        sequenceHelper.increment();

        return success;
    }

    /**
     * Generates any necessary tof entries to transfer funds needed to make the budget adjustments. Based on income chart and
     * accounts.
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @param sequenceHelper
     * @return boolean
     */
    public boolean processTransferOfFundsGeneralLedgerPendingEntries(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        return success;
    }


    /**
     * Validates the total of the monthly amount fields (if not 0) equals the current budget amount. If current budget is 0, then
     * total of monthly fields must be 0.
     * @param transactionalDocument 
     * @param accountingLine 
     * @return boolean
     */
    public boolean validateMonthlyLines(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        BudgetAdjustmentAccountingLine budgetAdjustmentAccountingLine = (BudgetAdjustmentAccountingLine) accountingLine;

        boolean validMonthlyLines = true;

        KualiDecimal monthlyTotal = budgetAdjustmentAccountingLine.getMonthlyLinesTotal();
        if (monthlyTotal.isNonZero() && monthlyTotal.compareTo(budgetAdjustmentAccountingLine.getCurrentBudgetAdjustmentAmount()) != 0) {
            GlobalVariables.getErrorMap().put(PropertyConstants.BA_CURRENT_BUDGET_ADJUSTMENT_AMOUNT, KeyConstants.ERROR_DOCUMENT_BA_MONTH_TOTAL_NOT_EQUAL_CURRENT);
            validMonthlyLines = false;
        }

        return validMonthlyLines;
    }

    /**
     * Checks object codes restrictions, including restrictions in parameters table.
     * @param transactionalDocument 
     * @param accountingLine 
     * @return boolean
     */
    public boolean validateObjectCode(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        BudgetAdjustmentDocument baDocument = (BudgetAdjustmentDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = PropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE;
        boolean objectCodeAllowed = true;


        return objectCodeAllowed;
    }

    /**
     * Checks account number restrictions, including restrictions in parameters table.
     */
    public boolean validateAccountNumber(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        BudgetAdjustmentDocument baDocument = (BudgetAdjustmentDocument) transactionalDocument;
        BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) accountingLine;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = PropertyConstants.ACCOUNT_NUMBER;
        boolean accountNumberAllowed = true;

        // check account has a budget recording level
        if (StringUtils.isBlank(accountingLine.getAccount().getBudgetRecordingLevelCode()) || ACCOUNT_NUMBER.BUDGET_LEVEL_NO_BUDGET.equals(accountingLine.getAccount().getBudgetRecordingLevelCode())) {
            GlobalVariables.getErrorMap().put(errorKey, KeyConstants.ERROR_DOCUMENT_BA_NON_BUDGETED_ACCOUNT, accountingLine.getAccountNumber());
            accountNumberAllowed = false;
        }

        // if current adjustment amount is non zero, account must have an associated income stream chart and account
        if (budgetAccountingLine.getCurrentBudgetAdjustmentAmount().isNonZero()) {
            if (accountingLine.getAccount().getIncomeStreamAccount() == null) {
                GlobalVariables.getErrorMap().put(errorKey, KeyConstants.ERROR_DOCUMENT_BA_NO_INCOME_STREAM_ACCOUNT, accountingLine.getAccountNumber());
                accountNumberAllowed = false;
            }
        }

        return accountNumberAllowed;
    }

    /**
     * Override needed to check the current, base, and monthly amounts.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isAmountValid(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
        boolean amountValid = true;

        BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) accountingLine;

        // check amounts both current and base amounts are not zero
        if (budgetAccountingLine.getCurrentBudgetAdjustmentAmount().isZero() && budgetAccountingLine.getBaseBudgetAdjustmentAmount().isZero()) {
            GlobalVariables.getErrorMap().put(PropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, KeyConstants.ERROR_BA_AMOUNT_ZERO);
            amountValid = false;
        }

        // if not an error correction, all amounts must be positive, else all amounts must be negative
        boolean isErrorCorrection = isErrorCorrection(document);
        amountValid &= checkAmountSign(budgetAccountingLine.getCurrentBudgetAdjustmentAmount(), PropertyConstants.CURRENT_BUDGET_ADJUSTMENT_AMOUNT, "Current", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getBaseBudgetAdjustmentAmount().kualiDecimalValue(), PropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, "Base", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth1LineAmount(), PropertyConstants.FINANCIAL_DOCUMENT_MONTH_1_LINE_AMOUNT, "Month 1", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth2LineAmount(), PropertyConstants.FINANCIAL_DOCUMENT_MONTH_2_LINE_AMOUNT, "Month 2", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth3LineAmount(), PropertyConstants.FINANCIAL_DOCUMENT_MONTH_3_LINE_AMOUNT, "Month 3", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth4LineAmount(), PropertyConstants.FINANCIAL_DOCUMENT_MONTH_4_LINE_AMOUNT, "Month 4", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth5LineAmount(), PropertyConstants.FINANCIAL_DOCUMENT_MONTH_5_LINE_AMOUNT, "Month 5", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth6LineAmount(), PropertyConstants.FINANCIAL_DOCUMENT_MONTH_6_LINE_AMOUNT, "Month 6", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth7LineAmount(), PropertyConstants.FINANCIAL_DOCUMENT_MONTH_7_LINE_AMOUNT, "Month 7", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth8LineAmount(), PropertyConstants.FINANCIAL_DOCUMENT_MONTH_8_LINE_AMOUNT, "Month 8", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth8LineAmount(), PropertyConstants.FINANCIAL_DOCUMENT_MONTH_9_LINE_AMOUNT, "Month 9", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth10LineAmount(), PropertyConstants.FINANCIAL_DOCUMENT_MONTH_10_LINE_AMOUNT, "Month 10", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth10LineAmount(), PropertyConstants.FINANCIAL_DOCUMENT_MONTH_11_LINE_AMOUNT, "Month 11", !isErrorCorrection);
        amountValid &= checkAmountSign(budgetAccountingLine.getFinancialDocumentMonth12LineAmount(), PropertyConstants.FINANCIAL_DOCUMENT_MONTH_12_LINE_AMOUNT, "Month 12", !isErrorCorrection);

        return amountValid;
    }

    /**
     * In order for the BA document to balance:
     * 
     * Total of Base Income Adjustments - Base Expense Adjustments on Descrease side must equal Total of Base Income Adjustments -
     * Base Expense Adjustments on increase side Total of Current Income Adjustments - Base Current Adjustments on Descrease side
     * must equal Total of Current Income Adjustments - Base Current Adjustments on increase side
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        BudgetAdjustmentDocument baDocument = (BudgetAdjustmentDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        boolean balanced = true;

        // check base amounts are equal
        if (baDocument.getSourceBaseBudgetTotal().compareTo(baDocument.getTargetBaseBudgetTotal()) != 0) {
            GlobalVariables.getErrorMap().put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BA_BASE_AMOUNTS_BALANCED);
            balanced = false;
        }
        
        // check current amounts balance, income stream balance Map should add to 0
        Map incomeStreamMap = buildIncomeStreamBalanceMap(baDocument);
        KualiDecimal totalCurrentAmount = new KualiDecimal(0);
        for (Iterator iter = incomeStreamMap.values().iterator(); iter.hasNext();) {
            KualiDecimal streamAmount = (KualiDecimal) iter.next();
            totalCurrentAmount.add(streamAmount);
        }
        
        if (totalCurrentAmount.isNonZero()) {
            GlobalVariables.getErrorMap().put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BA_CURRENT_AMOUNTS_BALANCED);
            balanced = false;
        }

        return balanced;
    }

    /**
     * Builds a map used for balancing current adjustment amounts. The map contains income chart and accounts contained on the
     * document as the keys, and transfer amounts as the values. The transfer amount is calculated from (curr_frm_inc -
     * curr_frm_exp) - (curr_to_inc - curr_to_exp)
     * 
     * @param baDocument
     * @return Map used to balance current amounts
     */
    public Map buildIncomeStreamBalanceMap(BudgetAdjustmentDocument baDocument) {
        Map incomeStreamBalance = new HashMap();

        List accountingLines = baDocument.getSourceAccountingLines();
        accountingLines.addAll(baDocument.getTargetAccountingLines());
        for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) iter.next();
            String incomeStreamKey = budgetAccountingLine.getAccount().getIncomeStreamAccount().getChartOfAccountsCode() + "|" 
              + budgetAccountingLine.getAccount().getIncomeStreamAccount().getAccountNumber();

            KualiDecimal incomeStreamAmount = new KualiDecimal(0);
            if (incomeStreamBalance.containsKey(incomeStreamKey)) { 
                incomeStreamAmount = (KualiDecimal) incomeStreamBalance.get(incomeStreamKey);
            }

            // amounts need reversed for source expense lines and target income lines
            if ((budgetAccountingLine instanceof BudgetAdjustmentSourceAccountingLine && super.isExpense((AccountingLine) budgetAccountingLine))
                    || (budgetAccountingLine instanceof BudgetAdjustmentTargetAccountingLine && super.isIncome((AccountingLine) budgetAccountingLine))) {
                incomeStreamAmount.subtract(budgetAccountingLine.getCurrentBudgetAdjustmentAmount());
            }
            else {
                incomeStreamAmount.add(budgetAccountingLine.getCurrentBudgetAdjustmentAmount());
            }

            // place record in balance map
            incomeStreamBalance.put(incomeStreamKey, incomeStreamAmount);
        }

        return incomeStreamBalance;
    }

    /**
     * Helper method to check if an amount is negative or positive and add an error if not.
     * 
     * @param amount to check
     * @param propertyName to add error under
     * @param label for error
     * @param positive where to check amount is positive (true) or negative (false)
     * @return boolean indicating if the value has the requested sign
     */
    private boolean checkAmountSign(KualiDecimal amount, String propertyName, String label, boolean positive) {
        boolean correctSign = true;

        if (positive && amount.isNegative()) {
            GlobalVariables.getErrorMap().put(propertyName, KeyConstants.ERROR_BA_AMOUNT_NEGATIVE, label);
            correctSign = false;
        }
        else if (!positive && amount.isPositive()) {
            GlobalVariables.getErrorMap().put(propertyName, KeyConstants.ERROR_BA_AMOUNT_POSITIVE, label);
            correctSign = false;
        }

        return correctSign;
    }

    /**
     * BA document does not have to have source accounting lines. In the case of setting up a budget for a new account, only targets
     * line (increase section) are setup.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        // check that both source and target are not empty, in which case is an error
        if (transactionalDocument.getSourceAccountingLines().isEmpty() && transactionalDocument.getTargetAccountingLines().isEmpty()) {
            GlobalVariables.getErrorMap().put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_NO_ACCOUNTING_LINES);
            return false;
        }

        return true;
    }


    /**
     * BA document does not have to have source accounting lines. In the case of closing out an account, only source lines (decrease
     * section) are setup.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return true;
    }
    
    /**
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        return IsDebitUtils.isDebitNotConsideringLineSection(this, transactionalDocument, accountingLine);
    }
    
    
}