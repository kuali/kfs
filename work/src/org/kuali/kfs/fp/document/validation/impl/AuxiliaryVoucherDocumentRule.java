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

import static org.kuali.Constants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.Constants.ACCOUNTING_PERIOD_STATUS_CLOSED;
import static org.kuali.Constants.ACCOUNTING_PERIOD_STATUS_CODE_FIELD;
import static org.kuali.Constants.AUXILIARY_LINE_HELPER_PROPERTY_NAME;
import static org.kuali.Constants.CREDIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.Constants.DEBIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.Constants.DOCUMENT_ERRORS;
import static org.kuali.Constants.GL_CREDIT_CODE;
import static org.kuali.Constants.GL_DEBIT_CODE;
import static org.kuali.Constants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
import static org.kuali.Constants.SQUARE_BRACKET_LEFT;
import static org.kuali.Constants.SQUARE_BRACKET_RIGHT;
import static org.kuali.Constants.VOUCHER_LINE_HELPER_CREDIT_PROPERTY_NAME;
import static org.kuali.Constants.VOUCHER_LINE_HELPER_DEBIT_PROPERTY_NAME;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_THREE_OPEN;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_BALANCE;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_BALANCE_CONSIDERING_CREDIT_AND_DEBIT_AMOUNTS;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE;
import static org.kuali.KeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT;
import static org.kuali.KeyConstants.AuxiliaryVoucher.ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE;
import static org.kuali.KeyConstants.AuxiliaryVoucher.ERROR_DIFFERENT_CHARTS;
import static org.kuali.KeyConstants.AuxiliaryVoucher.ERROR_DIFFERENT_SUB_FUND_GROUPS;
import static org.kuali.KeyConstants.AuxiliaryVoucher.ERROR_DOCUMENT_AUXILIARY_VOUCHER_INVALID_OBJECT_SUB_TYPE_CODE;
import static org.kuali.KeyConstants.AuxiliaryVoucher.ERROR_INVALID_ACCRUAL_REVERSAL_DATE;
import static org.kuali.PropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.PropertyConstants.REVERSAL_DATE;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;
import static org.kuali.kfs.util.SpringServiceLocator.getAccountingPeriodService;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.AUXILIARY_VOUCHER_SECURITY_GROUPING;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.GENERAL_LEDGER_PENDING_ENTRY_OFFSET_CODE;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_COMBINED_CODES;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_OBJECT_SUB_TYPE_CODES;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_PERIOD_CODES;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.financial.document.DistributionOfIncomeAndExpenseDocument;

/**
 * Business rule(s) applicable to <code>{@link AuxiliaryVoucherDocument}</code>.
 */
public class AuxiliaryVoucherDocumentRule extends AccountingDocumentRuleBase {
    private static Log LOG = LogFactory.getLog(AuxiliaryVoucherDocumentRule.class);

    /**
     * Convenience method for accessing the most-likely requested security grouping
     * 
     * @return String
     */
    @Override
    protected String getDefaultSecurityGrouping() {
        return AUXILIARY_VOUCHER_SECURITY_GROUPING;
    }

    /**
     * Get from APC the offset object code that is used for the <code>{@link GeneralLedgerPendingEntry}</code>
     * 
     * @return String
     */
    protected String getGeneralLedgerPendingEntryOffsetObjectCode() {
        return GENERAL_LEDGER_PENDING_ENTRY_OFFSET_CODE;
    }

    /**
     * the following are credits (return false)
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
     * overrides the parent to display correct error message for a single sided document
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(AccountingDocument FinancialDocument) {
        if (0 == FinancialDocument.getSourceAccountingLines().size()) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + PropertyConstants.SOURCE_ACCOUNTING_LINES, KeyConstants.ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES);
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
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(AccountingDocument FinancialDocument) {
        return true;
    }

    /**
     * Overrides the parent to return true, because Auxiliary Voucher documents aren't restricted from using any object code. This
     * is part of the "save" check that gets done. This method is called automatically by the parent's processSaveDocument method.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isObjectCodeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectCodeAllowed(AccountingLine accountingLine) {
        return true;
    }

    /**
     * Accounting lines for Auxiliary Vouchers can be positive or negative, just not "$0.00".
     * 
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
     * @param isDebit
     * @return String
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
     * @see FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(FinancialDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     * 
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
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processOffsetGeneralLedgerPendingEntry(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper, org.kuali.core.bo.AccountingLine,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected boolean processOffsetGeneralLedgerPendingEntry(AccountingDocument FinancialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        AuxiliaryVoucherDocument auxVoucher = (AuxiliaryVoucherDocument) FinancialDocument;

        // do not generate an offset entry if this is a normal or adjustment AV type
        if (auxVoucher.isAccrualType() || auxVoucher.isAdjustmentType()) {
            return processOffsetGeneralLedgerPendingEntryForAccrualsAndAdjustments(FinancialDocument, sequenceHelper, accountingLineCopy, explicitEntry, offsetEntry);
        }
        else if (auxVoucher.isRecodeType()) { // recodes generate offsets
            return processOffsetGeneralLedgerPendingEntryForRecodes(FinancialDocument, sequenceHelper, accountingLineCopy, explicitEntry, offsetEntry);
        }
        else {
            throw new IllegalStateException("Illegal auxiliary voucher type: " + auxVoucher.getTypeCode());
        }
    }

    /**
     * This method handles generating or not generating the appropriate offsets if the AV type is a recode.
     * 
     * @param FinancialDocument
     * @param sequenceHelper
     * @param accountingLineCopy
     * @param explicitEntry
     * @param offsetEntry
     * @return boolean
     */
    private boolean processOffsetGeneralLedgerPendingEntryForRecodes(AccountingDocument FinancialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        // the explicit entry has already been generated and added to the list, so to get the right offset, we have to set the value
        // of the document type code on the explicit
        // to the type code for a DI document so that it gets passed into the next call and we retrieve the right offset definition
        // since these offsets are
        // specific to Distrib. of Income and Expense documents - we need to do a deep copy though so we don't do this by reference
        GeneralLedgerPendingEntry explicitEntryDeepCopy = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
        explicitEntryDeepCopy.setFinancialDocumentTypeCode(SpringServiceLocator.getDocumentTypeService().getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class));

        // call the super to process an offset entry; see the customize method below for AVRC specific attribute values
        // pass in the explicit deep copy
        boolean success = super.processOffsetGeneralLedgerPendingEntry(FinancialDocument, sequenceHelper, accountingLineCopy, explicitEntryDeepCopy, offsetEntry);

        // increment the sequence appropriately
        sequenceHelper.increment();

        // now generate the AVRC DI entry
        // pass in the explicit deep copy
        success &= processAuxiliaryVoucherRecodeDistributionOfIncomeAndExpenseGeneralLedgerPendingEntry(FinancialDocument, sequenceHelper, explicitEntryDeepCopy);

        return success;
    }

    /**
     * This method handles generating or not generating the appropriate offsets if the AV type is accrual or adjustment.
     * 
     * @param FinancialDocument
     * @param sequenceHelper
     * @param accountingLineCopy
     * @param explicitEntry
     * @param offsetEntry
     * @return boolean
     */
    private boolean processOffsetGeneralLedgerPendingEntryForAccrualsAndAdjustments(AccountingDocument FinancialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean success = true;

        if (isDocumentForMultipleAccounts(FinancialDocument)) {
            success &= super.processOffsetGeneralLedgerPendingEntry(FinancialDocument, sequenceHelper, accountingLineCopy, explicitEntry, offsetEntry);
        }
        else {
            sequenceHelper.decrement(); // the parent already increments; b/c it assumes that all documents have offset entries all
            // of the time
        }

        return success;
    }

    /**
     * This method is responsible for iterating through all of the accounting lines in the document (source only) and checking to
     * see if they are all for the same account or not. It recognized the first account element as the base, and then it iterates
     * through the rest. If it comes across one that doesn't match, then we know it's for multiple accounts.
     * 
     * @param FinancialDocument
     * @return boolean
     */
    private boolean isDocumentForMultipleAccounts(AccountingDocument FinancialDocument) {
        String baseAccountNumber = "";

        int index = 0;
        List<AccountingLine> lines = FinancialDocument.getSourceAccountingLines();
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
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#customizeOffsetGeneralLedgerPendingEntry(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    protected boolean customizeOffsetGeneralLedgerPendingEntry(AccountingDocument FinancialDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        AuxiliaryVoucherDocument auxDoc = (AuxiliaryVoucherDocument) FinancialDocument;

        // set the document type to that of a Distrib. Of Income and Expense if it's a recode
        if (auxDoc.isRecodeType()) {
            offsetEntry.setFinancialDocumentTypeCode(SpringServiceLocator.getDocumentTypeService().getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class));
        }

        // now set the offset entry to the specific offset object code for the AV generated offset fund balance; only if it's an
        // accrual or adjustment
        if (auxDoc.isAccrualType() || auxDoc.isAdjustmentType()) {
            String glpeOffsetObjectCode = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(getDefaultSecurityGrouping(), getGeneralLedgerPendingEntryOffsetObjectCode());
            offsetEntry.setFinancialObjectCode(glpeOffsetObjectCode);
        }

        // set the reversal date to null
        offsetEntry.setFinancialDocumentReversalDate(null);

        // set the posting period and year to current
        offsetEntry.setUniversityFiscalPeriodCode(auxDoc.getPostingPeriodCode()); // use chosen posting period code
        offsetEntry.setUniversityFiscalYear(auxDoc.getPostingYear()); // use chosen posting year

        // although they are offsets, we need to set the offset indicator to false
        offsetEntry.setTransactionEntryOffsetIndicator(false);

        offsetEntry.refresh(); // may have changed foreign keys here; need accurate object code and account BOs at least
        offsetEntry.setAcctSufficientFundsFinObjCd(SpringServiceLocator.getSufficientFundsService().getSufficientFundsObjectCode(offsetEntry.getFinancialObject(), offsetEntry.getAccount().getAccountSufficientFundsCode()));

        return true;
    }

    /**
     * This method creates an AV recode specifc GLPE with a document type of DI. The sequence is managed outside of this method. It
     * uses the explicit entry as its model and then tweaks values appropriately.
     * 
     * @param FinancialDocument
     * @param sequenceHelper
     * @param explicitEntry
     * @return
     */
    private boolean processAuxiliaryVoucherRecodeDistributionOfIncomeAndExpenseGeneralLedgerPendingEntry(AccountingDocument FinancialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry explicitEntry) {
        // create a new instance based off of the explicit entry
        GeneralLedgerPendingEntry recodeGlpe = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);

        // set the sequence number according to what was passed in - this is managed external to this method
        recodeGlpe.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));

        // set the document type to that of a Distrib. Of Income and Expense
        recodeGlpe.setFinancialDocumentTypeCode(SpringServiceLocator.getDocumentTypeService().getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class));

        // set the object type code base on the value of the explicit entry
        recodeGlpe.setFinancialObjectTypeCode(getObjectTypeCodeForRecodeDistributionOfIncomeAndExpenseEntry(explicitEntry));

        // set the reversal date to null
        recodeGlpe.setFinancialDocumentReversalDate(null);

        // although this is an offsets, we need to set the offset indicator to false
        recodeGlpe.setTransactionEntryOffsetIndicator(false);

        // add the new recode offset entry to the document now
        FinancialDocument.getGeneralLedgerPendingEntries().add(recodeGlpe);

        return true;
    }

    /**
     * This method examines the accounting line passed in and returns the appropriate object type code. This rule converts specific
     * objects types from an object code on an accounting line to more general values. This is specific to the AV document.
     * 
     * @param line
     * @return object type for an AuxiliaryVoucher document
     */
    protected String getObjectTypeCode(AccountingLine line) {
        Options options = SpringServiceLocator.getOptionsService().getCurrentYearOptions();
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
     * @return object type code
     */
    protected String getObjectTypeCodeForRecodeDistributionOfIncomeAndExpenseEntry(GeneralLedgerPendingEntry explicitEntry) {
        Options options = SpringServiceLocator.getOptionsService().getCurrentYearOptions();
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
     * Overrides to call super and then validate that the
     * 
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
     * @param document
     * @return boolean
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
     * @param document
     * @return boolean
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
     * @param document
     * @return boolean
     */
    protected boolean isValidReversalDate(AuxiliaryVoucherDocument document) {
        if (document.isAccrualType() && document.getReversalDate() == null) {
            reportError(REVERSAL_DATE, ERROR_INVALID_ACCRUAL_REVERSAL_DATE);
            return false;
        }

        return true;
    }

    /**
     * Overrides the parent implemenation to sum all of the debit GLPEs up and sum all of the credit GLPEs up and then compare the
     * totals to each other, returning true if they are equal and false if they are not. The difference is that we ignore any DI
     * specific entries because while these are offsets, their offset indicators do not show this, so they would be counted in the
     * balancing and we don't want that.
     * 
     * Added a check for simple balance between credit and debit values as entered on the accountingLines, since that is also a
     * requirement.
     * 
     * @param FinancialDocument
     * @return boolean
     */
    @Override
    protected boolean isDocumentBalanceValidConsideringDebitsAndCredits(AccountingDocument FinancialDocument) {
        AuxiliaryVoucherDocument avDoc = (AuxiliaryVoucherDocument) FinancialDocument;

        return accountingLinesBalance(avDoc) && glpesBalance(avDoc);
    }

    /**
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
     * @param avDoc
     * @return true if the explicit, non-DI credit and debit GLPEs derived from the document's accountingLines are in balance
     */
    private boolean glpesBalance(AuxiliaryVoucherDocument avDoc) {
        // generate GLPEs specifically here so that we can compare debits to credits
        if (!SpringServiceLocator.getGeneralLedgerPendingEntryService().generateGeneralLedgerPendingEntries(avDoc)) {
            throw new ValidationException("general ledger GLPE generation failed");
        }

        // now loop through all of the GLPEs and calculate buckets for debits and credits
        KualiDecimal creditAmount = new KualiDecimal(0);
        KualiDecimal debitAmount = new KualiDecimal(0);

        for (GeneralLedgerPendingEntry glpe : avDoc.getGeneralLedgerPendingEntries()) {
            // make sure we are looking at only the explicit entries that aren't DI types
            if (!glpe.isTransactionEntryOffsetIndicator() && !glpe.getFinancialDocumentTypeCode().equals(SpringServiceLocator.getDocumentTypeService().getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class))) {
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
     * @param line
     */
    private void buildAccountingLineObjectType(AccountingLine line) {
        String objectTypeCode = line.getObjectCode().getFinancialObjectTypeCode();
        line.setObjectTypeCode(objectTypeCode);
        line.refresh();
    }

    /**
     * This method checks to see if there is a valid combination of sub type and object level
     * 
     * @param document
     * @param accountingLine
     * @return boolean
     */
    private boolean isValidDocWithSubAndLevel(AccountingDocument document, AccountingLine accountingLine) {
        boolean retval = true;

        StringBuffer combinedCodes = new StringBuffer("objectType=").append(accountingLine.getObjectType().getCode()).append(";objSubTyp=").append(accountingLine.getObjectCode().getFinancialObjectSubType().getCode()).append(";objLevel=").append(accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode());
        
        retval = !getParameterRule(RESTRICTED_COMBINED_CODES).getParameterText().equals(combinedCodes.toString());

        if (!retval) {
            String errorObjects[] = { accountingLine.getObjectCode().getFinancialObjectCode(), accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode(), accountingLine.getObjectCode().getFinancialObjectSubType().getCode(), accountingLine.getObjectType().getCode() };
            reportError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE, errorObjects);
        }

        return retval;
    }

    /**
     * This method determins if the posting period is valid for the document type
     * 
     * @param document
     * @param period
     * @param year
     * @return true if it is a valid period for posting into
     */
    protected boolean isPeriodAllowed(AuxiliaryVoucherDocument document) {
        // first we need to get the period itself to check these things
        boolean valid = true;
        Integer period = new Integer(document.getPostingPeriodCode());
        Integer year = document.getPostingYear();
        AccountingPeriod acctPeriod = getAccountingPeriodService().getByPeriod(document.getPostingPeriodCode(), year);

        // if current period is period 1 can't post back more than 3 open periods
        // grab the current period
        Timestamp ts = document.getDocumentHeader().getWorkflowDocument().getCreateDate();
        AccountingPeriod currPeriod = getAccountingPeriodService().getByDate(new Date(ts.getTime()));
        Integer currPeriodVal = new Integer(currPeriod.getUniversityFiscalPeriodCode());

        if (currPeriodVal == 1) {
            if (period < 11) {
                reportError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_PERIOD_THREE_OPEN);
                return false;
            }
        }

        // can't post back more than 2 periods
        if (period <= currPeriodVal) {
            if ((currPeriodVal - period) > 2) {
                reportError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                return false;
            }
        }
        else {
            // if currPeriodVal is less than period then that means it can only be
            // period 2 (see period 1 rule above)and period 13 as the only possible combination,
            // if not then error out
            if (currPeriodVal != 2) {
                reportError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                return false;
            }
            else {
                if (period != 13) {
                    reportError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                    return false;
                }
            }
        }

        valid = succeedsRule(RESTRICTED_PERIOD_CODES, document.getPostingPeriodCode());
        if (!valid) {
            reportError(ACCOUNTING_PERIOD_STATUS_CODE_FIELD, ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE);
        }

        // can't post into a closed period
        if (acctPeriod == null || acctPeriod.getUniversityFiscalPeriodStatusCode().equalsIgnoreCase(ACCOUNTING_PERIOD_STATUS_CLOSED)) {
            reportError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED);
            return false;
        }

        // check for specific posting issues
        if (document.isRecodeType()) {
            // can't post into a previous fiscal year
            Integer currFiscalYear = currPeriod.getUniversityFiscalYear();
            if (currFiscalYear > year) {
                reportError(DOCUMENT_ERRORS, ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC);
                return false;
            }
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
        return valid;
    }

    /**
     * Overrides to perform the universal rule in the super class in addition to Auxiliary Voucher specific rules. This method
     * leverages the APC for checking restricted object sub type values.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectSubTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        ObjectCode objectCode = accountingLine.getObjectCode();

        valid &= succeedsRule(RESTRICTED_OBJECT_SUB_TYPE_CODES, objectCode.getFinancialObjectSubTypeCode());
        if (!valid) {
            // add message
            reportError(FINANCIAL_OBJECT_CODE, ERROR_DOCUMENT_AUXILIARY_VOUCHER_INVALID_OBJECT_SUB_TYPE_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectSubTypeCode() });
        }

        return valid;
    }
}