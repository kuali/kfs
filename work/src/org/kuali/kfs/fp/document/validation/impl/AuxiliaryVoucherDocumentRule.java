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
     * Get from APC the offset object code that is used for the <code>{@link GeneralLedgerPendingEntry}</code>
     * 
     * @return String returns GLPE parameter name
     */
    protected String getGeneralLedgerPendingEntryOffsetObjectCode() {
        return GENERAL_LEDGER_PENDING_ENTRY_OFFSET_CODE;
    }

    /**
     * Returns true if an accounting line is a debit or credit The following are credits (return false)
     * <ol>
     * <li> debitCreditCode != 'D'
     * </ol>
     * the following are debits (return true)
     * <ol>
     * <li> debitCreditCode == 'D'
     * </ol>
     * the following are invalid ( throws an <code>IllegalStateException</code>)
     * <ol>
     * <li> debitCreditCode isBlank
     * </ol>
     * 
     * @param financialDocument submitted accounting document
     * @param accounttingLine accounting line being tested if it is a debit or not
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(AccountingDocument FinancialDocument, AccountingLine accountingLine) throws IllegalStateException {
        String debitCreditCode = accountingLine.getDebitCreditCode();
        if (StringUtils.isBlank(debitCreditCode)) {
            throw new IllegalStateException(IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage);
        }
        return IsDebitUtils.isDebitCode(debitCreditCode);
    }

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
     * This method sets the appropriate document type and object type codes into the GLPEs based on the type of AV document chosen.
     * 
     * @param document submitted AccountingDocument
     * @param accountingLine represents accounting line where object type code is retrieved from
     * @param explicitEntry GeneralPendingLedgerEntry object that has its document type, object type, period code, and fiscal year
     *        set
     * @see FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(FinancialDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processExplicitGeneralLedgerPendingEntry(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper, org.kuali.core.bo.AccountingLine,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    protected void customizeExplicitGeneralLedgerPendingEntry(AccountingDocument document, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        AuxiliaryVoucherDocument auxVoucher = (AuxiliaryVoucherDocument) document;

        java.sql.Date reversalDate = auxVoucher.getReversalDate();
        if (reversalDate != null) {
            explicitEntry.setFinancialDocumentReversalDate(reversalDate);
        }
        else {
            explicitEntry.setFinancialDocumentReversalDate(null);
        }
        explicitEntry.setFinancialDocumentTypeCode(auxVoucher.getTypeCode()); // make sure to use the accrual type as the document
        // type
        explicitEntry.setFinancialObjectTypeCode(getObjectTypeCode(accountingLine));
        explicitEntry.setUniversityFiscalPeriodCode(auxVoucher.getPostingPeriodCode()); // use chosen posting period code
        explicitEntry.setUniversityFiscalYear(auxVoucher.getPostingYear()); // use chosen posting year
    }

    /**
     * An Accrual Voucher only generates offsets if it is a recode (AVRC). So this method overrides to do nothing more than return
     * true if it's not a recode. If it is a recode, then it is responsible for generating two offsets with a document type of DI.
     * 
     * @param financialDocument submitted accounting document
     * @param sequenceHelper helper class which will allows us to increment a reference without using an Integer
     * @param accountingLineCopy accounting line from accounting document
     * @param explicitEntry represents explicit entry
     * @param offsetEntry represents offset entry
     * @return true if general ledger pending entry is processed successfully for accurals, adjustments, and recodes
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processOffsetGeneralLedgerPendingEntry(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper, org.kuali.core.bo.AccountingLine,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected boolean processOffsetGeneralLedgerPendingEntry(AccountingDocument financialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        AuxiliaryVoucherDocument auxVoucher = (AuxiliaryVoucherDocument) financialDocument;

        // do not generate an offset entry if this is a normal or adjustment AV type
        if (auxVoucher.isAccrualType() || auxVoucher.isAdjustmentType()) {
            return processOffsetGeneralLedgerPendingEntryForAccrualsAndAdjustments(financialDocument, sequenceHelper, accountingLineCopy, explicitEntry, offsetEntry);
        }
        else if (auxVoucher.isRecodeType()) { // recodes generate offsets
            return processOffsetGeneralLedgerPendingEntryForRecodes(financialDocument, sequenceHelper, accountingLineCopy, explicitEntry, offsetEntry);
        }
        else {
            throw new IllegalStateException("Illegal auxiliary voucher type: " + auxVoucher.getTypeCode());
        }
    }

    /**
     * This method handles generating or not generating the appropriate offsets if the AV type is a recode.
     * 
     * @param financialDocument submitted accounting document
     * @param sequenceHelper helper class which will allows us to increment a reference without using an Integer
     * @param accountingLineCopy accounting line from accounting document
     * @param explicitEntry represents explicit entry
     * @param offsetEntry represents offset entry
     * @return true if offset general ledger pending entry is processed
     */
    private boolean processOffsetGeneralLedgerPendingEntryForRecodes(AccountingDocument financialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        // the explicit entry has already been generated and added to the list, so to get the right offset, we have to set the value
        // of the document type code on the explicit
        // to the type code for a DI document so that it gets passed into the next call and we retrieve the right offset definition
        // since these offsets are
        // specific to Distrib. of Income and Expense documents - we need to do a deep copy though so we don't do this by reference
        GeneralLedgerPendingEntry explicitEntryDeepCopy = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
        explicitEntryDeepCopy.setFinancialDocumentTypeCode(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class));

        // set the posting period to current, because DI GLPEs for recodes should post to the current period
        java.sql.Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
        explicitEntryDeepCopy.setUniversityFiscalPeriodCode(SpringContext.getBean(AccountingPeriodService.class).getByDate(today).getUniversityFiscalPeriodCode()); // use
                                                                                                                                                                    // current
                                                                                                                                                                    // period
                                                                                                                                                                    // code

        // call the super to process an offset entry; see the customize method below for AVRC specific attribute values
        // pass in the explicit deep copy
        boolean success = super.processOffsetGeneralLedgerPendingEntry(financialDocument, sequenceHelper, accountingLineCopy, explicitEntryDeepCopy, offsetEntry);

        // increment the sequence appropriately
        sequenceHelper.increment();

        // now generate the AVRC DI entry
        // pass in the explicit deep copy
        success &= processAuxiliaryVoucherRecodeDistributionOfIncomeAndExpenseGeneralLedgerPendingEntry(financialDocument, sequenceHelper, explicitEntryDeepCopy);

        return success;
    }

    /**
     * This method handles generating or not generating the appropriate offsets if the AV type is accrual or adjustment.
     * 
     * @param financialDocument submitted accounting document
     * @param sequenceHelper helper class which will allows us to increment a reference without using an Integer
     * @param accountingLineCopy accounting line from accounting document
     * @param explicitEntry represents explicit entry
     * @param offsetEntry represents offset entry
     * @return true if offset general ledger pending entry is processed successfully
     */
    private boolean processOffsetGeneralLedgerPendingEntryForAccrualsAndAdjustments(AccountingDocument financialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean success = true;

        if (isDocumentForMultipleAccounts(financialDocument)) {
            success &= super.processOffsetGeneralLedgerPendingEntry(financialDocument, sequenceHelper, accountingLineCopy, explicitEntry, offsetEntry);
        }
        else {
            sequenceHelper.decrement(); // the parent already increments; b/c it assumes that all documents have offset entries all
            // of the time
        }

        return success;
    }

    /**
     * This method is responsible for iterating through all of the accounting lines in the document (source only) and checking to
     * see if they are all for the same account or not. It recognizes the first account element as the base, and then it iterates
     * through the rest. If it comes across one that doesn't match, then we know it's for multiple accounts.
     * 
     * @param financialDocument submitted accounting document
     * @return true if multiple accounts are being used
     */
    private boolean isDocumentForMultipleAccounts(AccountingDocument financialDocument) {
        String baseAccountNumber = "";

        int index = 0;
        List<AccountingLine> lines = financialDocument.getSourceAccountingLines();
        for (AccountingLine line : lines) {
            if (index == 0) {
                baseAccountNumber = line.getAccountNumber();
            }
            else if (!baseAccountNumber.equals(line.getAccountNumber())) {
                return true;
            }
            index++;
        }

        return false;
    }

    /**
     * Offset entries are created for recodes (AVRC) always, so this method is one of 2 offsets that get created for an AVRC. Its
     * document type is set to DI. This uses the explicit entry as its model. In addition, an offset is generated for accruals
     * (AVAE) and adjustments (AVAD), but only if the document contains accounting lines for more than one account.
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine accounting line from accounting document
     * @param explicitEntry represents explicit entry
     * @param offsetEntry represents offset entry
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#customizeOffsetGeneralLedgerPendingEntry(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    protected boolean customizeOffsetGeneralLedgerPendingEntry(AccountingDocument financialDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        AuxiliaryVoucherDocument auxDoc = (AuxiliaryVoucherDocument) financialDocument;

        // set the document type to that of a Distrib. Of Income and Expense if it's a recode
        if (auxDoc.isRecodeType()) {
            offsetEntry.setFinancialDocumentTypeCode(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class));

            // set the posting period
            java.sql.Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
            offsetEntry.setUniversityFiscalPeriodCode(SpringContext.getBean(AccountingPeriodService.class).getByDate(today).getUniversityFiscalPeriodCode()); // use
                                                                                                                                                                // current
                                                                                                                                                                // period
                                                                                                                                                                // code
        }

        // now set the offset entry to the specific offset object code for the AV generated offset fund balance; only if it's an
        // accrual or adjustment
        if (auxDoc.isAccrualType() || auxDoc.isAdjustmentType()) {
            String glpeOffsetObjectCode = SpringContext.getBean(ParameterService.class).getParameterValue(AuxiliaryVoucherDocument.class, getGeneralLedgerPendingEntryOffsetObjectCode());
            offsetEntry.setFinancialObjectCode(glpeOffsetObjectCode);

            // set the posting period
            offsetEntry.setUniversityFiscalPeriodCode(auxDoc.getPostingPeriodCode()); // use chosen posting period code
        }

        // set the reversal date to null
        offsetEntry.setFinancialDocumentReversalDate(null);

        // set the year to current
        offsetEntry.setUniversityFiscalYear(auxDoc.getPostingYear()); // use chosen posting year

        // although they are offsets, we need to set the offset indicator to false
        offsetEntry.setTransactionEntryOffsetIndicator(false);

        offsetEntry.refresh(); // may have changed foreign keys here; need accurate object code and account BOs at least
        offsetEntry.setAcctSufficientFundsFinObjCd(SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(offsetEntry.getFinancialObject(), offsetEntry.getAccount().getAccountSufficientFundsCode()));

        return true;
    }

    /**
     * This method creates an AV recode specific GLPE with a document type of DI. The sequence is managed outside of this method. It
     * uses the explicit entry as its model and then tweaks values appropriately.
     * 
     * @param financialDocument submitted accounting document
     * @param sequenceHelper helper class which will allows us to increment a reference without using an Integer
     * @param explicitEntry represents explicit entry
     * @return true if recode GLPE is added to the financial document successfully
     */
    private boolean processAuxiliaryVoucherRecodeDistributionOfIncomeAndExpenseGeneralLedgerPendingEntry(AccountingDocument financialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry explicitEntry) {
        // create a new instance based off of the explicit entry
        GeneralLedgerPendingEntry recodeGlpe = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);

        // set the sequence number according to what was passed in - this is managed external to this method
        recodeGlpe.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));

        // set the document type to that of a Distrib. Of Income and Expense
        recodeGlpe.setFinancialDocumentTypeCode(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class));

        // set the object type code base on the value of the explicit entry
        recodeGlpe.setFinancialObjectTypeCode(getObjectTypeCodeForRecodeDistributionOfIncomeAndExpenseEntry(explicitEntry));

        // set the reversal date to null
        recodeGlpe.setFinancialDocumentReversalDate(null);

        // although this is an offsets, we need to set the offset indicator to false
        recodeGlpe.setTransactionEntryOffsetIndicator(false);

        // add the new recode offset entry to the document now
        financialDocument.getGeneralLedgerPendingEntries().add(recodeGlpe);

        return true;
    }

    /**
     * This method examines the accounting line passed in and returns the appropriate object type code. This rule converts specific
     * objects types from an object code on an accounting line to more general values. This is specific to the AV document.
     * 
     * @param line accounting line where object type code is retrieved from
     * @return object type from a accounting line ((either financial object type code, financial object type not expenditure code,
     *         or financial object type income not cash code))
     */
    protected String getObjectTypeCode(AccountingLine line) {
        Options options = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();
        String objectTypeCode = line.getObjectCode().getFinancialObjectTypeCode();

        if (options.getFinObjTypeExpenditureexpCd().equals(objectTypeCode) || options.getFinObjTypeExpendNotExpCode().equals(objectTypeCode)) {
            objectTypeCode = options.getFinObjTypeExpNotExpendCode();
        }
        else if (options.getFinObjectTypeIncomecashCode().equals(objectTypeCode) || options.getFinObjTypeExpendNotExpCode().equals(objectTypeCode)) {
            objectTypeCode = options.getFinObjTypeIncomeNotCashCd();
        }

        return objectTypeCode;
    }

    /**
     * This method examines the explicit entry's object type and returns the appropriate object type code. This is specific to AV
     * recodes (AVRCs).
     * 
     * @param explicitEntry
     * @return object type code from explicit entry (either financial object type code, financial object type expenditure code, or
     *         financial object type income cash code)
     */
    protected String getObjectTypeCodeForRecodeDistributionOfIncomeAndExpenseEntry(GeneralLedgerPendingEntry explicitEntry) {
        Options options = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();
        String objectTypeCode = explicitEntry.getFinancialObjectTypeCode();

        if (options.getFinObjTypeExpNotExpendCode().equals(objectTypeCode)) {
            objectTypeCode = options.getFinObjTypeExpenditureexpCd();
        }
        else if (options.getFinObjTypeIncomeNotCashCd().equals(objectTypeCode)) {
            objectTypeCode = options.getFinObjectTypeIncomecashCode();
        }

        return objectTypeCode;
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