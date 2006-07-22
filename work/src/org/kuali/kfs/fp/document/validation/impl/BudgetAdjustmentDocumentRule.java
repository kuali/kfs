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

import static org.kuali.Constants.SOURCE_ACCOUNTING_LINE_ERRORS;
import static org.kuali.Constants.TARGET_ACCOUNTING_LINE_ERRORS;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_TOTAL_CHANGED;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.BUDGET_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.GENERATE_TOF_GLPE_ENTRIES_PARM_NM;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.TRANSFER_OBJECT_CODE_PARM_NM;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.RESTRICTED_OBJECT_SUB_TYPE_CODES;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.RESTRICTED_OBJECT_CODES;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_1_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_2_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_3_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_4_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_5_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_6_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_7_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_8_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_9_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_10_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_11_PERIOD_CODE;
import static org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants.MONTH_12_PERIOD_CODE;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.OBJECT_TYPE_CODE.INCOME_CASH;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.OBJECT_TYPE_CODE.TRANSFER_INCOME;
import static org.kuali.module.financial.rules.TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_DOC_TYPE_CODE;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.FinancialDocument;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentTargetAccountingLine;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

/**
 * Business rule(s) applicable to Budget Adjustment Card document.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAdjustmentDocumentRule extends TransactionalDocumentRuleBase implements GenerateGeneralLedgerDocumentPendingEntriesRule {
    
    private static final String INCOME_STREAM_CHART_ACCOUNT_DELIMITER = "|";

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        boolean allow = true;
        BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) accountingLine;
        
        LOG.debug("validating accounting line # " + accountingLine.getSequenceNumber());
        
        /* if they have entered a base amount for line, verify it can be adjusted for the posting year */
        if (budgetAccountingLine.getBaseBudgetAdjustmentAmount().isNonZero() && !SpringServiceLocator.getFiscalYearFunctionControlService().isBaseAmountChangeAllowed(((BudgetAdjustmentDocument) transactionalDocument).getPostingYear())) {
            GlobalVariables.getErrorMap().putError(PropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, KeyConstants.ERROR_DOCUMENT_BA_BASE_AMOUNT_CHANGE_NOT_ALLOWED);
            allow = false;
        }

        LOG.debug("beginning monthly lines validation ");
        allow = allow && validateMonthlyLines(transactionalDocument, accountingLine);

        LOG.debug("beginning object code validation ");
        allow = allow && validateObjectCode(transactionalDocument, accountingLine);

        LOG.debug("beginning account number validation ");
        allow = allow && validateAccountNumber(transactionalDocument, accountingLine);

        LOG.debug("end validating accounting line, has errors: " + allow);

        return allow;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        BudgetAdjustmentDocument baDocument = (BudgetAdjustmentDocument) document;

        if (isValid) {
            isValid = isValid && isAllAccountingLinesMatchingBudgetYear((TransactionalDocument) document);
            isValid = isValid && validateFundGroupAdjustmentRestrictions(baDocument);
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

        // determine if we are on increase or decrease side
        KualiDecimal amountSign = null;
        if (accountingLine instanceof SourceAccountingLine) {
            amountSign = new KualiDecimal(-1);
        }
        else {
            amountSign = new KualiDecimal(1);
        }

        BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) accountingLine;
        Integer currentFiscalYear = SpringServiceLocator.getDateTimeService().getCurrentFiscalYear();
        /* Create Base Budget GLPE if base amount != 0 */
        if (budgetAccountingLine.getBaseBudgetAdjustmentAmount().isNonZero()) {
            GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
            populateExplicitGeneralLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, explicitEntry);

            /* D/C code is empty for BA, set correct balance type, correct amount */
            explicitEntry.setTransactionDebitCreditCode("");
            explicitEntry.setFinancialBalanceTypeCode(Constants.BALANCE_TYPE_BASE_BUDGET);
            explicitEntry.setTransactionLedgerEntryAmount(budgetAccountingLine.getBaseBudgetAdjustmentAmount().multiply(amountSign).kualiDecimalValue());
            // set fiscal period, if next fiscal year set to 01, else leave to current period
            if (currentFiscalYear.equals(transactionalDocument.getPostingYear() - 1)) {
                explicitEntry.setUniversityFiscalPeriodCode(MONTH_1_PERIOD_CODE);
            }

            // add the new explicit entry to the document now
            transactionalDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

            // increment the sequence counter
            sequenceHelper.increment();
        }

        /* Create Current Budget GLPE if current amount != 0 */
        if (budgetAccountingLine.getCurrentBudgetAdjustmentAmount().isNonZero()) {
            GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
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

            // create montly lines (MB)
            if (budgetAccountingLine.getFinancialDocumentMonth1LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, MONTH_1_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth1LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth2LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, MONTH_2_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth2LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth3LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, MONTH_3_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth3LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth4LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, MONTH_4_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth4LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth5LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, MONTH_5_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth5LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth6LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, MONTH_6_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth6LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth7LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, MONTH_7_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth7LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth8LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, MONTH_8_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth8LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth9LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, MONTH_9_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth9LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth10LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, MONTH_10_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth10LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth11LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, MONTH_11_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth11LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth12LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, MONTH_12_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth12LineAmount().multiply(amountSign));
            }
        }

        return success;
    }

    /**
     * Helper method for creating monthly buget pending entry lines.
     * 
     * @param transactionalDocument
     * @param sequenceHelper
     * @param fiscalPeriod
     * @param monthAmount
     * @return boolean
     */
    private void createMonthlyBudgetGLPE(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String fiscalPeriod, KualiDecimal monthAmount) {
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        populateExplicitGeneralLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, explicitEntry);

        /* D/C code is empty for BA, set correct balance type, correct amount */
        explicitEntry.setTransactionDebitCreditCode("");
        explicitEntry.setFinancialBalanceTypeCode(Constants.BALANCE_TYPE_MONTHLY_BUDGET);
        explicitEntry.setTransactionLedgerEntryAmount(monthAmount);
        explicitEntry.setUniversityFiscalPeriodCode(fiscalPeriod);

        // add the new explicit entry to the document now
        transactionalDocument.getGeneralLedgerPendingEntries().add(explicitEntry);
    }

    /**
     * Generates any necessary tof entries to transfer funds needed to make the budget adjustments. Based on income chart and
     * accounts. If there is a difference in funds between an income chart and account, a tof entry needs to be created, along with
     * a budget adjustment entry. Object code used is retrieved by a parameter.
     * 
     * @see org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateDocumentGeneralLedgerPendingEntries(FinancialDocument financialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        TransactionalDocument transactionalDocument = (TransactionalDocument) financialDocument;
        BudgetAdjustmentDocument baDocument = (BudgetAdjustmentDocument) transactionalDocument;

        boolean success = true;

        // check on-off tof flag
        boolean generateTransfer = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterIndicator(BUDGET_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING, GENERATE_TOF_GLPE_ENTRIES_PARM_NM);
        String transferObjectCode = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(BUDGET_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING, TRANSFER_OBJECT_CODE_PARM_NM);
        Integer currentFiscalYear = SpringServiceLocator.getDateTimeService().getCurrentFiscalYear();

        if (generateTransfer) {
            // map of income chart/accounts with balance as value
            Map incomeStreamMap = buildIncomeStreamBalanceMap(baDocument);
            for (Iterator iter = incomeStreamMap.keySet().iterator(); iter.hasNext();) {
                String chartAccount = (String) iter.next();
                KualiDecimal streamAmount = (KualiDecimal) incomeStreamMap.get(chartAccount);
                if (streamAmount.isNonZero()) {
                    // build dummy accounting line for gl population
                    AccountingLine accountingLine = null;
                    try {
                        accountingLine = (SourceAccountingLine) baDocument.getSourceAccountingLineClass().newInstance();
                    }
                    catch (IllegalAccessException e) {
                        throw new InfrastructureException("unable to access sourceAccountingLineClass", e);
                    }
                    catch (InstantiationException e) {
                        throw new InfrastructureException("unable to instantiate sourceAccountingLineClass", e);
                    }

                    // set income chart and account in line
                    String[] incomeString = StringUtils.split(chartAccount, INCOME_STREAM_CHART_ACCOUNT_DELIMITER);
                    accountingLine.setChartOfAccountsCode(incomeString[0]);
                    accountingLine.setAccountNumber(incomeString[1]);
                    accountingLine.setFinancialObjectCode(transferObjectCode);

                    // ////////////////// first create current budget entry/////////////////////////////////////////
                    GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
                    populateExplicitGeneralLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, explicitEntry);

                    /* override and set object type to income */
                    explicitEntry.setFinancialObjectTypeCode(INCOME_CASH);

                    /* D/C code is empty for BA, set correct balance type, correct amount */
                    explicitEntry.setTransactionDebitCreditCode("");
                    explicitEntry.setFinancialBalanceTypeCode(Constants.BALANCE_TYPE_CURRENT_BUDGET);
                    explicitEntry.setTransactionLedgerEntryAmount(streamAmount);

                    // set fiscal period, if next fiscal year set to 01, else leave to current period
                    if (currentFiscalYear.equals(transactionalDocument.getPostingYear() - 1)) {
                        explicitEntry.setUniversityFiscalPeriodCode(MONTH_1_PERIOD_CODE);
                    }

                    // add the new explicit entry to the document now
                    transactionalDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

                    // increment the sequence counter
                    sequenceHelper.increment();


                    // ////////////////// now create actual TOF entry //////////////////////////////////////////////
                    /* set amount in line so Debit/Credit code can be set correctly */
                    accountingLine.setAmount(streamAmount);
                    explicitEntry = new GeneralLedgerPendingEntry();
                    populateExplicitGeneralLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, explicitEntry);

                    /* override and set object type to transfer */
                    explicitEntry.setFinancialObjectTypeCode(TRANSFER_INCOME);

                    /* set document type to tof */
                    explicitEntry.setFinancialDocumentTypeCode(getTransferDocumentType());

                    // set fiscal period, if next fiscal year set to 01, else leave to current period
                    if (currentFiscalYear.equals(transactionalDocument.getPostingYear() - 1)) {
                        explicitEntry.setUniversityFiscalPeriodCode(MONTH_1_PERIOD_CODE);
                    }

                    // add the new explicit entry to the document now
                    transactionalDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

                    // increment the sequence counter
                    sequenceHelper.increment();

                    // ////////////////// now create actual TOF offset //////////////////////////////////////////////
                    GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
                    success &= processOffsetGeneralLedgerPendingEntry(transactionalDocument, sequenceHelper, accountingLine, explicitEntry, offsetEntry);

                    // increment the sequence counter
                    sequenceHelper.increment();
                }
            }
        }

        return success;
    }


    /**
     * Validates the total of the monthly amount fields (if not 0) equals the current budget amount. If current budget is 0, then
     * total of monthly fields must be 0.
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @return boolean
     */
    public boolean validateMonthlyLines(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        BudgetAdjustmentAccountingLine budgetAdjustmentAccountingLine = (BudgetAdjustmentAccountingLine) accountingLine;

        boolean validMonthlyLines = true;

        KualiDecimal monthlyTotal = budgetAdjustmentAccountingLine.getMonthlyLinesTotal();
        if (monthlyTotal.isNonZero() && monthlyTotal.compareTo(budgetAdjustmentAccountingLine.getCurrentBudgetAdjustmentAmount()) != 0) {
            GlobalVariables.getErrorMap().putError(PropertyConstants.CURRENT_BUDGET_ADJUSTMENT_AMOUNT, KeyConstants.ERROR_DOCUMENT_BA_MONTH_TOTAL_NOT_EQUAL_CURRENT);
            validMonthlyLines = false;
        }

        return validMonthlyLines;
    }

    /**
     * Retrieves the fund group and sub fund group for each accounting line. Then verifies that the codes associated with the
     * 'Budget Adjustment Restriction Code' field are met.
     * 
     * @param transactionalDocument
     * @return boolean
     */
    public boolean validateFundGroupAdjustmentRestrictions(TransactionalDocument transactionalDocument) {
        BudgetAdjustmentDocument baDocument = (BudgetAdjustmentDocument) transactionalDocument;
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
            if (!Constants.BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_NONE.equals(subFund.getFundGroupBudgetAdjustmentRestrictionLevelCode())) {
                restrictionLevel = subFund.getFundGroupBudgetAdjustmentRestrictionLevelCode();
                restrictedToSubFund = true;
                accountRestrictingSubFund = line.getAccountNumber();
            }
            else {
                restrictionLevel = subFund.getFundGroup().getFundGroupBudgetAdjustmentRestrictionLevelCode();
            }

            if (Constants.BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_CHART.equals(restrictionLevel)) {
                restrictedToChart = true;
                accountRestrictingChart = line.getAccountNumber();
            }
            else if (Constants.BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_ORGANIZATION.equals(restrictionLevel)) {
                restrictedToOrg = true;
                accountRestrictingOrg = line.getAccountNumber();
            }
            else if (Constants.BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_ACCOUNT.equals(restrictionLevel)) {
                restrictedToAccount = true;
                accountRestrictingAccount = line.getAccountNumber();
            }

            // if we have a sub fund restriction, this overrides anything coming later
            if (restrictedToSubFund) {
                break;
            }
        }
        
        String fundLabel = AccountingLineRuleUtil.getFundGroupCodeLabel();
        String subFundLabel = AccountingLineRuleUtil.getSubFundGroupCodeLabel();
        String chartLabel = AccountingLineRuleUtil.getChartLabel();
        String orgLabel = AccountingLineRuleUtil.getOrganizationCodeLabel();
        String acctLabel = AccountingLineRuleUtil.getAccountLabel();

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
                    errors.putErrorWithoutFullErrorPath(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BA_MIXED_FUND_GROUPS);
                    isAdjustmentAllowed = false;
                    break;
                }

                if (restrictedToSubFund) {
                    if (!line.getAccount().getSubFundGroupCode().equals(previousLine.getAccount().getSubFundGroupCode())) {
                        errors.putErrorWithoutFullErrorPath(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingSubFund, subFundLabel });
                        isAdjustmentAllowed = false;
                        break;
                    }
                }

                if (restrictedToChart) {
                    if (!line.getChartOfAccountsCode().equals(previousLine.getChartOfAccountsCode())) {
                        if (restrictedToSubFund) {
                            errors.putErrorWithoutFullErrorPath(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingChart, subFundLabel + " and " + chartLabel });
                        }
                        else {
                            errors.putErrorWithoutFullErrorPath(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingChart, fundLabel + " and " + chartLabel });
                        }
                        isAdjustmentAllowed = false;
                        break;
                    }
                }

                if (restrictedToOrg) {
                    if (!line.getAccount().getOrganizationCode().equals(previousLine.getAccount().getOrganizationCode())) {
                        if (restrictedToSubFund) {
                            errors.putErrorWithoutFullErrorPath(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingOrg, subFundLabel + " and " + orgLabel });
                        }
                        else {
                            errors.putErrorWithoutFullErrorPath(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingOrg, fundLabel + " and " + orgLabel });
                        }
                        isAdjustmentAllowed = false;
                        break;
                    }
                }

                if (restrictedToAccount) {
                    if (!line.getAccountNumber().equals(previousLine.getAccountNumber())) {
                        errors.putErrorWithoutFullErrorPath(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingAccount, acctLabel });
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
     * Checks object codes restrictions, including restrictions in parameters table.
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @return boolean
     */
    public boolean validateObjectCode(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        BudgetAdjustmentDocument baDocument = (BudgetAdjustmentDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        boolean objectCodeAllowed = true;
        
        String errorKey = PropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE;
        
        /* check object sub type global restrictions */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(BUDGET_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING, RESTRICTED_OBJECT_SUB_TYPE_CODES, accountingLine.getObjectCode().getFinancialObjectSubTypeCode(), errorKey, AccountingLineRuleUtil.getObjectSubTypeCodeLabel());

        /* check object code is in permitted list for payment reason */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(BUDGET_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING, RESTRICTED_OBJECT_CODES, accountingLine.getFinancialObjectCode(), errorKey, AccountingLineRuleUtil.getObjectCodeLabel());

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
            errors.putError(errorKey, KeyConstants.ERROR_DOCUMENT_BA_NON_BUDGETED_ACCOUNT, accountingLine.getAccountNumber());
            accountNumberAllowed = false;
        }

        // if current adjustment amount is non zero, account must have an associated income stream chart and account
        if (budgetAccountingLine.getCurrentBudgetAdjustmentAmount().isNonZero()) {
            if (ObjectUtils.isNull(accountingLine.getAccount().getIncomeStreamAccount())) {
                errors.putError(errorKey, KeyConstants.ERROR_DOCUMENT_BA_NO_INCOME_STREAM_ACCOUNT, accountingLine.getAccountNumber());
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
            GlobalVariables.getErrorMap().putError(PropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, KeyConstants.ERROR_BA_AMOUNT_ZERO);
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
            GlobalVariables.getErrorMap().putError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BA_BASE_AMOUNTS_BALANCED);
            balanced = false;
        }

        // check current amounts balance, income stream balance Map should add to 0
        Map incomeStreamMap = buildIncomeStreamBalanceMap(baDocument);
        KualiDecimal totalCurrentAmount = new KualiDecimal(0);
        for (Iterator iter = incomeStreamMap.values().iterator(); iter.hasNext();) {
            KualiDecimal streamAmount = (KualiDecimal) iter.next();
            totalCurrentAmount = totalCurrentAmount.add(streamAmount);
        }

        if (totalCurrentAmount.isNonZero()) {
            GlobalVariables.getErrorMap().putError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_BA_CURRENT_AMOUNTS_BALANCED);
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

        List accountingLines = new ArrayList();
        accountingLines.addAll(baDocument.getSourceAccountingLines());
        accountingLines.addAll(baDocument.getTargetAccountingLines());
        for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) iter.next();
            String incomeStreamKey = budgetAccountingLine.getAccount().getIncomeStreamFinancialCoaCode() + INCOME_STREAM_CHART_ACCOUNT_DELIMITER + budgetAccountingLine.getAccount().getIncomeStreamAccountNumber();

            KualiDecimal incomeStreamAmount = new KualiDecimal(0);
            if (incomeStreamBalance.containsKey(incomeStreamKey)) {
                incomeStreamAmount = (KualiDecimal) incomeStreamBalance.get(incomeStreamKey);
            }

            // amounts need reversed for source expense lines and target income lines
            if ((budgetAccountingLine instanceof BudgetAdjustmentSourceAccountingLine && super.isExpense((AccountingLine) budgetAccountingLine)) || (budgetAccountingLine instanceof BudgetAdjustmentTargetAccountingLine && super.isIncome((AccountingLine) budgetAccountingLine))) {
                incomeStreamAmount = incomeStreamAmount.subtract(budgetAccountingLine.getCurrentBudgetAdjustmentAmount());
            }
            else {
                incomeStreamAmount = incomeStreamAmount.add(budgetAccountingLine.getCurrentBudgetAdjustmentAmount());
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
            GlobalVariables.getErrorMap().putError(propertyName, KeyConstants.ERROR_BA_AMOUNT_NEGATIVE, label);
            correctSign = false;
        }
        else if (!positive && amount.isPositive()) {
            GlobalVariables.getErrorMap().putError(propertyName, KeyConstants.ERROR_BA_AMOUNT_POSITIVE, label);
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
            GlobalVariables.getErrorMap().putError(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_NO_ACCOUNTING_LINES);
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
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        try {
            return IsDebitUtils.isDebitConsideringType(this, transactionalDocument, accountingLine);
        }
        catch (IllegalStateException e) {
            // for all accounting lines except the transfer lines, the line amount will be 0 and this exception will be thrown
            return false;
        }
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isAccountingLineTotalsUnchanged(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isAccountingLineTotalsUnchanged(TransactionalDocument transactionalDocument) {
        boolean isUnchanged = true;

        BudgetAdjustmentDocument persistedDocument = (BudgetAdjustmentDocument) retrievePersistedDocument(transactionalDocument);
        BudgetAdjustmentDocument currentDocument = (BudgetAdjustmentDocument) transactionalDocument;

        if (persistedDocument == null) {
            handleNonExistentDocumentWhenApproving(transactionalDocument);
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
     * This method builds out the error message for when totals have changed.
     * 
     * @param propertyName
     * @param sectionTitle
     * @param persistedSourceLineTotal
     * @param currentSourceLineTotal
     */
    private void buildTotalChangeErrorMessage(String propertyName, String sectionTitle, KualiDecimal persistedSourceLineTotal, KualiDecimal currentSourceLineTotal) {
        String persistedTotal = (String) new CurrencyFormatter().format(persistedSourceLineTotal);
        String currentTotal = (String) new CurrencyFormatter().format(currentSourceLineTotal);

        GlobalVariables.getErrorMap().putError(propertyName, ERROR_DOCUMENT_ACCOUNTING_LINE_TOTAL_CHANGED, new String[] { sectionTitle, persistedTotal, currentTotal });
    }
    
    /**
     * @return the document type name to be used for the income stream transfer glpe
     */
    protected String getTransferDocumentType() {
        return TRANSFER_OF_FUNDS_DOC_TYPE_CODE;
    }


}