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

import static org.kuali.kfs.KFSConstants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.KFSConstants.ACCOUNTING_PERIOD_STATUS_CLOSED;
import static org.kuali.kfs.KFSConstants.ACCOUNTING_PERIOD_STATUS_CODE_FIELD;
import static org.kuali.kfs.KFSConstants.AUXILIARY_LINE_HELPER_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.CREDIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.DEBIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.DOCUMENT_ERRORS;
import static org.kuali.kfs.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.kfs.KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.SQUARE_BRACKET_LEFT;
import static org.kuali.kfs.KFSConstants.SQUARE_BRACKET_RIGHT;
import static org.kuali.kfs.KFSConstants.VOUCHER_LINE_HELPER_CREDIT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.VOUCHER_LINE_HELPER_DEBIT_PROPERTY_NAME;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_BALANCE;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_BALANCE_CONSIDERING_CREDIT_AND_DEBIT_AMOUNTS;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT;
import static org.kuali.kfs.KFSKeyConstants.AuxiliaryVoucher.ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE;
import static org.kuali.kfs.KFSKeyConstants.AuxiliaryVoucher.ERROR_DIFFERENT_CHARTS;
import static org.kuali.kfs.KFSKeyConstants.AuxiliaryVoucher.ERROR_DIFFERENT_SUB_FUND_GROUPS;
import static org.kuali.kfs.KFSKeyConstants.AuxiliaryVoucher.ERROR_INVALID_ACCRUAL_REVERSAL_DATE;
import static org.kuali.kfs.KFSPropertyConstants.REVERSAL_DATE;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.AUXILIARY_VOUCHER_ACCOUNTING_PERIOD_GRACE_PERIOD;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.GENERAL_LEDGER_PENDING_ENTRY_OFFSET_CODE;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_COMBINED_CODES;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_PERIOD_CODES;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.financial.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.service.SufficientFundsService;

/**
 * Business rule(s) applicable to <code>{@link AuxiliaryVoucherDocument}</code>.
 */
public class AuxiliaryVoucherDocumentRule extends AccountingDocumentRuleBase {
    private static Log LOG = LogFactory.getLog(AuxiliaryVoucherDocumentRule.class);

    /**
     * Overrides the parent to display correct error message for a single sided document
     * 
     * @param financialDocument submitted accounting document
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(AccountingDocument financialDocument) {
        if (0 == financialDocument.getSourceAccountingLines().size()) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Overrides the parent to return true, because Auxiliary Voucher documents only use the SourceAccountingLines data structures.
     * The list that holds TargetAccountingLines should be empty. This will be checked when the document is "routed" or submitted to
     * post - it's called automatically by the parent's processRouteDocument method.
     * 
     * @param financialDocument submitted accounting document
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(AccountingDocument financialDocument) {
        return true;
    }

    /**
     * Overrides the parent to return true, because Auxiliary Voucher documents aren't restricted from using any object code. This
     * is part of the "save" check that gets done. This method is called automatically by the parent's processSaveDocument method.
     * 
     * @param documentClass submitted document class (not used in overriden method)
     * @param accountingLine accountingLine where object code is being checked (not used in overriden method)
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isObjectCodeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectCodeAllowed(Class documentClass, AccountingLine accountingLine) {
        return true;
    }

    /**
     * Accounting lines for Auxiliary Vouchers can only be positive non-zero numbers
     * 
     * @param document submitted AccountingDocument
     * @param accountingLine accountingLine where amount is being validated
     * @return true if amount is NOT 0 or negative
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isAmountValid(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        boolean retval = true;
        KualiDecimal amount = accountingLine.getAmount();

        AuxiliaryVoucherDocument avDoc = (AuxiliaryVoucherDocument) document;

        // check for negative or zero amounts
        if (KualiDecimal.ZERO.equals(amount)) { // if 0
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(true), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(false), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");

            retval = false;
        }
        else if (amount.isNegative()) { // entered a negative number
            String debitCreditCode = accountingLine.getDebitCreditCode();
            if (StringUtils.isNotBlank(debitCreditCode) && GL_DEBIT_CODE.equals(debitCreditCode)) {
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(true), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
            }
            else {
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(false), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
            }

            retval = false;
        }

        return retval;
    }

    /**
     * This method looks at the current full key path that exists in the ErrorMap structure to determine how to build the error map
     * for the special journal voucher credit and debit fields since they don't conform to the standard pattern of accounting lines.
     * 
     * @param isDebit boolean to determine whether or not value isDebit or not
     * @return String represents error map key to use
     */
    private String buildErrorMapKeyPathForDebitCreditAmount(boolean isDebit) {
        // determine if we are looking at a new line add or an update
        boolean isNewLineAdd = GlobalVariables.getErrorMap().getErrorPath().contains(NEW_SOURCE_ACCT_LINE_PROPERTY_NAME);
        isNewLineAdd |= GlobalVariables.getErrorMap().getErrorPath().contains(NEW_SOURCE_ACCT_LINE_PROPERTY_NAME);

        if (isNewLineAdd) {
            if (isDebit) {
                return DEBIT_AMOUNT_PROPERTY_NAME;
            }
            else {
                return CREDIT_AMOUNT_PROPERTY_NAME;
            }
        }
        else {
            String index = StringUtils.substringBetween(GlobalVariables.getErrorMap().getKeyPath("", true), SQUARE_BRACKET_LEFT, SQUARE_BRACKET_RIGHT);
            String indexWithParams = SQUARE_BRACKET_LEFT + index + SQUARE_BRACKET_RIGHT;
            if (isDebit) {
                return AUXILIARY_LINE_HELPER_PROPERTY_NAME + indexWithParams + VOUCHER_LINE_HELPER_DEBIT_PROPERTY_NAME;
            }
            else {
                return AUXILIARY_LINE_HELPER_PROPERTY_NAME + indexWithParams + VOUCHER_LINE_HELPER_CREDIT_PROPERTY_NAME;
            }
        }
    }

    /**
     * Validates added accounting line. Override calls parent method and also checks whether or not the document and line have a
     * valid sub object and object level
     * 
     * @param document submitted accounting document
     * @param accountingLine validated accounting line from accounting document
     * @return return true if accounting line is valid
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        boolean valid = super.processCustomAddAccountingLineBusinessRules(document, accountingLine);

        if (valid) {
            buildAccountingLineObjectType(accountingLine);
            valid &= isValidDocWithSubAndLevel(document, accountingLine);
        }

        return valid;
    }

    /**
     * Validates reviewed accounting line. Override calls parent method and also checks whether or not the document and line have a
     * valid sub object and object level
     * 
     * @param document submitted accounting document
     * @param accountingLine validated accounting line from accounting document
     * @return return true if accounting line is valid
     * @see FinancialDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomReviewAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        boolean valid = true;
        valid &= super.processCustomReviewAccountingLineBusinessRules(document, accountingLine);
        if (valid) {
            buildAccountingLineObjectType(accountingLine);
            valid &= isValidDocWithSubAndLevel(document, accountingLine);
        }
        return valid;
    }

    /**
     * This method performs common validation for Transactional Document routes. Note the rule framework will handle validating all
     * of the accounting lines and also those checks that would normally be done on a save, automatically for us.<br/> <br/>
     * <code>{@link AuxiliaryVoucherDocument}</code> is different from other <code>{@link Document}</code> instances because it
     * requires all <code>{@link AccountingLine}</code> instances belong to the same Fund Group. That's done here by iterating
     * through the <code>{@link AccountingLine}</code> instances.
     * 
     * @param document submitted document
     * @see FinancialDocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean valid = super.processCustomRouteDocumentBusinessRules(document);
        AuxiliaryVoucherDocument avDoc = (AuxiliaryVoucherDocument) document;

        if (valid) {
            valid = isPeriodAllowed(avDoc);
        }

        // make sure that a single chart is used for all accounting lines in the document
        if (valid) {
            valid = isSingleChartUsed((AccountingDocument) document);
        }

        // make sure that a single sub fund group is used for all accounting lines in the document
        if (valid) {
            valid = isSingleSubFundGroupUsed((AccountingDocument) document);
        }

        // make sure that a reversal date is entered for accruals
        if (valid) {
            valid = isValidReversalDate((AuxiliaryVoucherDocument) document);
        }

        return valid;
    }

    /**
     * Iterates <code>{@link AccountingLine}</code> instances in a given <code>{@link FinancialDocument}</code> instance and
     * compares them to see if they are all in the same Chart.
     * 
     * @param document submitted document
     * @return true if only one chart of accounts code is used in a document's source accounting lines
     */
    protected boolean isSingleChartUsed(AccountingDocument document) {
        boolean valid = true;

        String baseChartCode = null;
        int index = 0;

        List<AccountingLine> lines = document.getSourceAccountingLines();
        for (AccountingLine line : lines) {
            if (index == 0) {
                baseChartCode = line.getChartOfAccountsCode();
            }
            else {
                String currentChartCode = line.getChartOfAccountsCode();
                if (!currentChartCode.equals(baseChartCode)) {
                    reportError(ACCOUNTING_LINE_ERRORS, ERROR_DIFFERENT_CHARTS);
                    return false;
                }
            }
            index++;
        }
        return true;
    }

    /**
     * Iterates <code>{@link AccountingLine}</code> instances in a given <code>{@link FinancialDocument}</code> instance and
     * compares them to see if they are all in the same Sub-Fund Group.
     * 
     * @param document submitted document
     * @return true if only one sub fund group code is used in a document's source accounting lines
     */
    protected boolean isSingleSubFundGroupUsed(AccountingDocument document) {
        boolean valid = true;

        String baseSubFundGroupCode = null;
        int index = 0;

        List<AccountingLine> lines = document.getSourceAccountingLines();
        for (AccountingLine line : lines) {
            if (index == 0) {
                baseSubFundGroupCode = line.getAccount().getSubFundGroupCode();
            }
            else {
                String currentSubFundGroup = line.getAccount().getSubFundGroupCode();
                if (!currentSubFundGroup.equals(baseSubFundGroupCode)) {
                    reportError(ACCOUNTING_LINE_ERRORS, ERROR_DIFFERENT_SUB_FUND_GROUPS);
                    return false;
                }
            }
            index++;
        }
        return true;
    }

    /**
     * This method verifies that the user entered a reversal date, but only if it's an accrual.
     * 
     * @param document submitted document
     * @return returns true if document is NOT an accrual type OR has a reversal date
     */
    protected boolean isValidReversalDate(AuxiliaryVoucherDocument document) {
        if (document.isAccrualType() && document.getReversalDate() == null) {
            reportError(REVERSAL_DATE, ERROR_INVALID_ACCRUAL_REVERSAL_DATE);
            return false;
        }

        return true;
    }

    /**
     * Overrides the parent implementation to sum all of the debit GLPEs up and sum all of the credit GLPEs up and then compare the
     * totals to each other, returning true if they are equal and false if they are not. The difference is that we ignore any DI
     * specific entries because while these are offsets, their offset indicators do not show this, so they would be counted in the
     * balancing and we don't want that. Added a check for simple balance between credit and debit values as entered on the
     * accountingLines, since that is also a requirement.
     * 
     * @param financialDocument submitted accounting document
     * @return true if a document's accounting lines credit/debit lines are in balance and a document's non-DI credit and debit
     *         GLPEs are also in balance
     */
    @Override
    protected boolean isDocumentBalanceValidConsideringDebitsAndCredits(AccountingDocument finanacialDocument) {
        AuxiliaryVoucherDocument avDoc = (AuxiliaryVoucherDocument) finanacialDocument;

        return accountingLinesBalance(avDoc) && glpesBalance(avDoc);
    }

    /**
     * Returns true if credit/debit entries are in balance
     * 
     * @param avDoc submitted AuxiliaryVoucherDocument
     * @return true if the credit and debit entries from all accountingLines for the given document are in balance
     */
    private boolean accountingLinesBalance(AuxiliaryVoucherDocument avDoc) {
        KualiDecimal creditAmount = avDoc.getCreditTotal();
        KualiDecimal debitAmount = avDoc.getDebitTotal();

        boolean balanced = debitAmount.equals(creditAmount);
        if (!balanced) {
            String errorParams[] = { creditAmount.toString(), debitAmount.toString() };
            reportError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_BALANCE_CONSIDERING_CREDIT_AND_DEBIT_AMOUNTS, errorParams);
        }
        return balanced;
    }

    /**
     * Returns true if the explicit, non-DI credit and debit GLPEs derived from the document's accountingLines are in balance
     * 
     * @param avDoc submitted AuxiliaryVoucherDocument
     * @return true if the explicit, non-DI credit and debit GLPEs derived from the document's accountingLines are in balance
     */
    private boolean glpesBalance(AuxiliaryVoucherDocument avDoc) {
        // generate GLPEs specifically here so that we can compare debits to credits
        if (!SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(avDoc)) {
            throw new ValidationException("general ledger GLPE generation failed");
        }

        // now loop through all of the GLPEs and calculate buckets for debits and credits
        KualiDecimal creditAmount = new KualiDecimal(0);
        KualiDecimal debitAmount = new KualiDecimal(0);

        for (GeneralLedgerPendingEntry glpe : avDoc.getGeneralLedgerPendingEntries()) {
            // make sure we are looking at only the explicit entries that aren't DI types
            if (!glpe.isTransactionEntryOffsetIndicator() && !glpe.getFinancialDocumentTypeCode().equals(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class))) {
                if (GL_CREDIT_CODE.equals(glpe.getTransactionDebitCreditCode())) {
                    creditAmount = creditAmount.add(glpe.getTransactionLedgerEntryAmount());
                }
                else { // DEBIT
                    debitAmount = debitAmount.add(glpe.getTransactionLedgerEntryAmount());
                }
            }
        }

        boolean balanced = debitAmount.equals(creditAmount);
        if (!balanced) {
            String errorParams[] = { creditAmount.toString(), debitAmount.toString() };
            reportError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_BALANCE, errorParams);
        }
        return balanced;
    }

    /**
     * Fixes <code>{@link ObjectType}</code> for the given <code>{@link AccountingLine}</code> instance
     * 
     * @param line accounting line
     */
    private void buildAccountingLineObjectType(AccountingLine line) {
        String objectTypeCode = line.getObjectCode().getFinancialObjectTypeCode();
        line.setObjectTypeCode(objectTypeCode);
        line.refresh();
    }

    /**
     * This method checks to see if there is a valid combination of sub type and object level
     * 
     * @param document submitted accounting document
     * @param accountingLine validated accounting line
     * @return return true if line contains a valid combination of object sub type and object level
     */
    private boolean isValidDocWithSubAndLevel(AccountingDocument document, AccountingLine accountingLine) {
        boolean retval = true;

        StringBuffer combinedCodes = new StringBuffer(accountingLine.getObjectType().getCode()).append(',').append(accountingLine.getObjectCode().getFinancialObjectSubType().getCode()).append(',').append(accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode());
        ParameterEvaluator evalutator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(AuxiliaryVoucherDocument.class, RESTRICTED_COMBINED_CODES);

        retval = !evalutator.equals(combinedCodes.toString());

        if (!retval) {
            String errorObjects[] = { accountingLine.getObjectCode().getFinancialObjectCode(), accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode(), accountingLine.getObjectCode().getFinancialObjectSubType().getCode(), accountingLine.getObjectType().getCode() };
            reportError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE, errorObjects);
        }

        return retval;
    }

    /**
     * This method determines if the posting period is valid for the document type.
     * 
     * @param document submitted AuxiliaryVoucherDocument
     * @return true if it is a valid period for posting into
     */
    protected boolean isPeriodAllowed(AuxiliaryVoucherDocument document) {
        /*
         * Nota bene: a full summarization of these rules can be found in the comments to KULRNE-4634
         */
        // first we need to get the period itself to check these things
        boolean valid = true;
        AccountingPeriod acctPeriod = SpringContext.getBean(AccountingPeriodService.class).getByPeriod(document.getPostingPeriodCode(), document.getPostingYear());

        valid = SpringContext.getBean(ParameterService.class).getParameterEvaluator(AuxiliaryVoucherDocument.class, RESTRICTED_PERIOD_CODES, document.getPostingPeriodCode()).evaluationSucceeds();
        if (!valid) {
            reportError(ACCOUNTING_PERIOD_STATUS_CODE_FIELD, ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE);
        }

        // can't post into a closed period
        if (acctPeriod == null || acctPeriod.getUniversityFiscalPeriodStatusCode().equalsIgnoreCase(ACCOUNTING_PERIOD_STATUS_CLOSED)) {
            reportError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED);
            return false;
        }

        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        AccountingPeriod currPeriod = SpringContext.getBean(AccountingPeriodService.class).getByDate(new Date(ts.getTime()));

        if (acctPeriod.getUniversityFiscalYear().equals(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear())) {
            if (SpringContext.getBean(AccountingPeriodService.class).compareAccountingPeriodsByDate(acctPeriod, currPeriod) < 0) {
                // we've only got problems if the av's accounting period is earlier than now

                // are we in the grace period for this accounting period?
                if (!AuxiliaryVoucherDocumentRule.calculateIfWithinGracePeriod(new Date(ts.getTime()), acctPeriod)) {
                    reportError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                    return false;
                }
            }
        }
        else {
            // it's not the same fiscal year, so we need to test whether we are currently
            // in the grace period of the acctPeriod
            if (!AuxiliaryVoucherDocumentRule.calculateIfWithinGracePeriod(new Date(ts.getTime()), acctPeriod) && AuxiliaryVoucherDocumentRule.isEndOfPreviousFiscalYear(acctPeriod)) {
                reportError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                return false;
            }
        }

        boolean numericPeriod = true;
        Integer period = null;
        try {
            period = new Integer(document.getPostingPeriodCode());
        }
        catch (NumberFormatException nfe) {
            numericPeriod = false;
        }
        Integer year = document.getPostingYear();

        // check for specific posting issues
        if (document.isRecodeType()) {
            // can't post into a previous fiscal year
            Integer currFiscalYear = currPeriod.getUniversityFiscalYear();
            if (currFiscalYear > year) {
                reportError(DOCUMENT_ERRORS, ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC);
                return false;
            }
            if (numericPeriod) {
                // check the posting period, throw out if period 13
                if (period > 12) {
                    reportError(DOCUMENT_ERRORS, ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC);
                    return false;
                }
                else if (period < 1) {
                    reportError(DOCUMENT_ERRORS, ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE);
                    return false;
                }
            }
            else {
                // not a numeric period and this is a recode? Then we won't allow it; ref KULRNE-6001
                reportError(DOCUMENT_ERRORS, ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC);
                return false;
            }
        }
        return valid;
    }

    /**
     * This method checks if a given moment of time is within an accounting period, or its auxiliary voucher grace period.
     * 
     * @param today a date to check if it is within the period
     * @param periodToCheck the account period to check against
     * @return true if a given moment in time is within an accounting period or an auxiliary voucher grace period
     */
    public static boolean calculateIfWithinGracePeriod(Date today, AccountingPeriod periodToCheck) {
        boolean result = false;
        int todayAsComparableDate = AuxiliaryVoucherDocumentRule.comparableDateForm(today);
        int periodClose = new Integer(AuxiliaryVoucherDocumentRule.comparableDateForm(periodToCheck.getUniversityFiscalPeriodEndDate()));
        int periodBegin = AuxiliaryVoucherDocumentRule.comparableDateForm(AuxiliaryVoucherDocumentRule.calculateFirstDayOfMonth(periodToCheck.getUniversityFiscalPeriodEndDate()));
        int gracePeriodClose = periodClose + new Integer(SpringContext.getBean(ParameterService.class).getParameterValue(AuxiliaryVoucherDocument.class, AUXILIARY_VOUCHER_ACCOUNTING_PERIOD_GRACE_PERIOD)).intValue();
        return (todayAsComparableDate >= periodBegin && todayAsComparableDate <= gracePeriodClose);
    }

    /**
     * This method returns a date as an approximate count of days since the BCE epoch.
     * 
     * @param d the date to convert
     * @return an integer count of days, very approximate
     */
    public static int comparableDateForm(Date d) {
        java.util.Calendar cal = new java.util.GregorianCalendar();
        cal.setTime(d);
        return cal.get(java.util.Calendar.YEAR) * 365 + cal.get(java.util.Calendar.DAY_OF_YEAR);
    }

    /**
     * Given a day, this method calculates what the first day of that month was.
     * 
     * @param d date to find first of month for
     * @return date of the first day of the month
     */
    public static Date calculateFirstDayOfMonth(Date d) {
        java.util.Calendar cal = new java.util.GregorianCalendar();
        cal.setTime(d);
        int dayOfMonth = cal.get(java.util.Calendar.DAY_OF_MONTH) - 1;
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1 * dayOfMonth);
        return new Date(cal.getTimeInMillis());
    }

    /**
     * This method checks if the given accounting period ends on the last day of the previous fiscal year
     * 
     * @param acctPeriod accounting period to check
     * @return true if the accounting period ends with the fiscal year, false if otherwise
     */
    public static boolean isEndOfPreviousFiscalYear(AccountingPeriod acctPeriod) {
        UniversityDateService dateService = SpringContext.getBean(UniversityDateService.class);
        Date firstDayOfCurrFiscalYear = new Date(dateService.getFirstDateOfFiscalYear(dateService.getCurrentFiscalYear()).getTime());
        Date periodClose = acctPeriod.getUniversityFiscalPeriodEndDate();
        java.util.Calendar cal = new java.util.GregorianCalendar();
        cal.setTime(periodClose);
        cal.add(java.util.Calendar.DATE, 1);
        return (firstDayOfCurrFiscalYear.equals(new Date(cal.getTimeInMillis())));
    }
}