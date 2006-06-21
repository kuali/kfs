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

import static org.kuali.Constants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.Constants.ACCOUNTING_PERIOD_STATUS_CLOSED;
import static org.kuali.Constants.ACCOUNTING_PERIOD_STATUS_CODE_FIELD;
import static org.kuali.Constants.AUXILIARY_LINE_HELPER_PROPERTY_NAME;
import static org.kuali.Constants.CREDIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.Constants.DEBIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.Constants.DOCUMENT_ERRORS;
import static org.kuali.Constants.JOURNAL_LINE_HELPER_CREDIT_PROPERTY_NAME;
import static org.kuali.Constants.JOURNAL_LINE_HELPER_DEBIT_PROPERTY_NAME;
import static org.kuali.Constants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
import static org.kuali.Constants.SQUARE_BRACKET_LEFT;
import static org.kuali.Constants.SQUARE_BRACKET_RIGHT;
import static org.kuali.Constants.ZERO;
import static org.kuali.Constants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE;
import static org.kuali.Constants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE;
import static org.kuali.Constants.AuxiliaryVoucher.RECODE_DOC_TYPE;
import static org.kuali.KeyConstants.ERROR_CUSTOM;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_THREE_OPEN;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_INCORRECT_REVERSAL_DATE;
import static org.kuali.KeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT;
import static org.kuali.KeyConstants.AuxiliaryVoucher.ERROR_DOCUMENT_AUXILIARY_VOUCHER_INVALID_OBJECT_SUB_TYPE_CODE;
import static org.kuali.PropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.AUXILIARY_VOUCHER_SECURITY_GROUPING;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_COMBINED_CODES;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_OBJECT_SUB_TYPE_CODES;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_PERIOD_CODES;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.KualiCodeBase;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjSubTyp;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

/**
 * Business rule(s) applicable to <code>{@link AuxiliaryVoucherDocument}</code> instances
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class AuxiliaryVoucherDocumentRule extends TransactionalDocumentRuleBase {

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
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) throws IllegalStateException {
        IsDebitUtils.disallowErrorCorrectionDocumentCheck(this, transactionalDocument);
        String debitCreditCode = accountingLine.getDebitCreditCode();
        if (StringUtils.isBlank(debitCreditCode)) {
            throw new IllegalStateException("invalid (blank) debitCreditCode");
        }
        return IsDebitUtils.isDebitCode(debitCreditCode);
    }

    /**
     * Overrides the parent to return true, because Auxiliary Voucher documents only use the SourceAccountingLines data structures.
     * The list that holds TargetAccountingLines should be empty. This will be checked when the document is "routed" or submitted to
     * post - it's called automatically by the parent's processRouteDocument method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * Overrides the parent to return true, because Auxiliary Voucher documents aren't restricted from using any object code. This
     * is part of the "save" check that gets done. This method is called automatically by the parent's processSaveDocument method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isObjectCodeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectCodeAllowed(AccountingLine accountingLine) {
        return true;
    }

    /**
     * Accounting lines for Auxiliary Vouchers can be positive or negative, just not "$0.00".
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isAmountValid(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
        boolean retval = true;
        KualiDecimal amount = accountingLine.getAmount();

        AuxiliaryVoucherDocument avDoc = (AuxiliaryVoucherDocument) document;

        // check for negative or zero amounts
        if (ZERO.compareTo(amount) == 0) { // if 0
            GlobalVariables.getErrorMap().putWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(true), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
            GlobalVariables.getErrorMap().putWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(false), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");

            retval = false;
        }
        else if (ZERO.compareTo(amount) == 1) { // entered a negative number
            String debitCreditCode = accountingLine.getDebitCreditCode();
            if (StringUtils.isNotBlank(debitCreditCode) && GENERAL_LEDGER_PENDING_ENTRY_CODE.DEBIT.equals(debitCreditCode)) {
                GlobalVariables.getErrorMap().putWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(true), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
            }
            else {
                GlobalVariables.getErrorMap().putWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(false), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
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
                return AUXILIARY_LINE_HELPER_PROPERTY_NAME + indexWithParams + JOURNAL_LINE_HELPER_DEBIT_PROPERTY_NAME;
            }
            else {
                return AUXILIARY_LINE_HELPER_PROPERTY_NAME + indexWithParams + JOURNAL_LINE_HELPER_CREDIT_PROPERTY_NAME;
            }

        }

    }

    /**
     * This is the default implementation for Transactional Documents, which sums the amounts of all of the Source Accounting Lines,
     * and compares it to the total of all of the Target Accounting Lines. In general, this algorithm works, but it does not work
     * for some specific documents such as the Auxiliary Voucher. The method name denotes not an expected behavior, but a more
     * general title so that some documents that don't use this default implementation, can override just this method without having
     * to override the calling method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        AuxiliaryVoucherDocument document = (AuxiliaryVoucherDocument) transactionalDocument;
        if (ZERO.compareTo(document.getTotal()) == 0 || ZERO.compareTo(document.getTotal()) == 1) {
            GlobalVariables.getErrorMap().putWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(true), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an Auxiliary Voucher");
            return false;
        }
        return true;
    }

    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processExplicitGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper, org.kuali.core.bo.AccountingLine,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected boolean processExplicitGeneralLedgerPendingEntry(TransactionalDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine line, GeneralLedgerPendingEntry explicitEntry) {
        AuxiliaryVoucherDocument auxVoucher = (AuxiliaryVoucherDocument) document;
        String voucherType = getVoucherTypeCode(document);
        AccountingPeriod postingPeriod = auxVoucher.getAccountingPeriod();
        String postingPeriodString = postingPeriod.getUniversityFiscalPeriodCode();

        // first check for the odd posting periods, AA, BB, CB and treat it like period 13
        if (isPeriodCodeAllowed(postingPeriod)) {
            return false;
        }
        Integer postingPeriodInt = new Integer(0);

        try {
            postingPeriodInt = Integer.valueOf(postingPeriodString);
        }
        catch (NumberFormatException nfe) {
            GlobalVariables.getErrorMap().put(ACCOUNTING_PERIOD_STATUS_CODE_FIELD, ERROR_CUSTOM, "You have entered an incorrect posting period, it must be a number between 1 and 13.");
            return false;
        }

        String objectType = getObjectTypeNormalAV(line);
        if (!setReversalDate(auxVoucher, voucherType, postingPeriod)) {
            return false;
        }
        if (!isValidPeriod(auxVoucher, postingPeriodInt.intValue(), auxVoucher.getAccountingPeriod().getUniversityFiscalYear().intValue())) {
            return false;
        }

        if (RECODE_DOC_TYPE.equals(voucherType)) {

            GeneralLedgerPendingEntry avrcEntry = new GeneralLedgerPendingEntry();
            populateExplicitGeneralLedgerPendingEntry(document, line, sequenceHelper, avrcEntry);
            avrcEntry.setFinancialDocumentReversalDate(new java.sql.Date(auxVoucher.getReversalDate().getTime()));
            avrcEntry.setFinancialDocumentTypeCode(RECODE_DOC_TYPE);
            avrcEntry.setFinancialObjectTypeCode(objectType);
            document.addGeneralLedgerPendingEntry(avrcEntry);

            // now we need to code our explicit entry (which will be used to create the offset entry
            // later
            String diObjectType = getObjectTypeRecodeAV(objectType);
            explicitEntry.setFinancialDocumentTypeCode(OBJECT_TYPE_CODE.DOCUMENT_TYPE_DISTRIBUTION_OF_INCOME_AND_EXPENSE);
            explicitEntry.setFinancialObjectTypeCode(diObjectType);

            document.addGeneralLedgerPendingEntry(explicitEntry);
            sequenceHelper.increment();
        }
        else if (ADJUSTMENT_DOC_TYPE.equals(voucherType)) {
            explicitEntry.setFinancialDocumentReversalDate(new java.sql.Date(auxVoucher.getReversalDate().getTime()));
            explicitEntry.setFinancialDocumentTypeCode(ADJUSTMENT_DOC_TYPE);
            explicitEntry.setFinancialObjectTypeCode(objectType);
            return true;
        }
        else if (ACCRUAL_DOC_TYPE.equals(voucherType)) {
            explicitEntry.setFinancialDocumentReversalDate(new java.sql.Date(auxVoucher.getReversalDate().getTime()));
            explicitEntry.setFinancialDocumentTypeCode(ACCRUAL_DOC_TYPE);
            explicitEntry.setFinancialObjectTypeCode(objectType);
            return true;
        }
        return true;
    }

    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processOffsetGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper, org.kuali.core.bo.AccountingLine,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected boolean processOffsetGeneralLedgerPendingEntry(TransactionalDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine line, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        String voucherType = getVoucherTypeCode(document);

        if (RECODE_DOC_TYPE.equals(voucherType)) {
            offsetEntry.setFinancialDocumentTypeCode(OBJECT_TYPE_CODE.DOCUMENT_TYPE_DISTRIBUTION_OF_INCOME_AND_EXPENSE);
            // we need to do this because the main class still thinks that there are only two entries
            sequenceHelper.increment();
        }
        else {
            // the offsets are automatically generated otherwise
            sequenceHelper.decrement();
            return true;
        }
        return true;
    }

    /**
     * 
     * This method returns the appropriate voucher type code based on the document passed in
     * 
     * @param document
     * @return voucher type code (AVAE, AVAD, AVRC)
     */
    protected String getVoucherTypeCode(TransactionalDocument document) {
        AuxiliaryVoucherDocument auxVoucher = (AuxiliaryVoucherDocument) document;
        String voucherType = auxVoucher.getTypeCode();

        if (voucherType.equals("") || !voucherType.equals(ACCRUAL_DOC_TYPE) || !voucherType.equals(ADJUSTMENT_DOC_TYPE) || !voucherType.equals(RECODE_DOC_TYPE)) {
            voucherType = ACCRUAL_DOC_TYPE;
        }
        return voucherType;
    }

    /**
     * 
     * This method examines the object type passed in and converts it to another object type string depending on what it is. This
     * value is then used when creating the explicit entry and offset entry
     * 
     * @param line
     * @return object type for a normal AuxiliaryVoucher document (not AVRC)
     */
    protected String getObjectTypeNormalAV(AccountingLine line) {
        String objectType = line.getObjectCode().getFinancialObjectType().getCode();
        String returnObjType = line.getObjectCode().getFinancialObjectType().getCode();
        if (OBJECT_TYPE_CODE.EXPENSE_EXPENDITURE.equals(objectType) || OBJECT_TYPE_CODE.EXPENDITURE_NOT_EXPENSE.equals(objectType)) {
            returnObjType = OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE;
        }
        else if (OBJECT_TYPE_CODE.INCOME_CASH.equals(objectType) || OBJECT_TYPE_CODE.EXPENDITURE_NOT_EXPENSE.equals(objectType)) {
            returnObjType = OBJECT_TYPE_CODE.INCOME_NOT_CASH;
        }
        return returnObjType;
    }

    /**
     * 
     * This method is almost identical in functionality to <code>String getObjectTypeNormalAV</code> but it calculates the proper
     * object type for a recode (AVRC) doc This method should be called with the results of the first method for proper calculations
     * 
     * @param objectType
     * @return object type for a AuxiliaryVoucher document (not AVRC)
     */
    protected String getObjectTypeRecodeAV(String objectType) {
        String returnObjType = objectType;
        if (OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE.equals(objectType)) {
            returnObjType = OBJECT_TYPE_CODE.EXPENSE_EXPENDITURE;
        }
        else if (OBJECT_TYPE_CODE.INCOME_NOT_CASH.equals(objectType)) {
            returnObjType = OBJECT_TYPE_CODE.INCOME_CASH;
        }
        return returnObjType;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        boolean valid = true;
        valid &= super.processCustomAddAccountingLineBusinessRules(document, accountingLine);

        if (valid) {
            buildAccountingLineObjectType(accountingLine);
            valid &= isValidDocWithSubAndLevel(document, accountingLine);
        }

        LOG.info("Returning:" + valid);
        return valid;
    }

    /**
     * @see TransactionalDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
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
     * @see TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        return processCustomRouteDocumentBusinessRules((TransactionalDocument) document);
    }

    /**
     * Special override method to handle <code>{@link TransactionalDocument}</code> custom routing rules.
     * 
     * @param document
     * @return boolean
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    protected boolean processCustomRouteDocumentBusinessRules(TransactionalDocument document) {
        boolean valid = isDocumentBalanceValid(document);

        if (valid) {
            validateFundGroupsInDocument(document);
        }

        return valid;
    }


    /**
     * Iterates <code>{@link AccountingLine}</code> instances in a given <code>{@link TransactionalDocument}</code> instance and
     * compares them to see if they are all in the same Fund Group.
     * 
     * @see #validateFundGroups(AccountingLine, AccountingLine)
     * @param document
     * @return boolean
     */
    protected boolean validateFundGroupsInDocument(TransactionalDocument document) {
        boolean valid = true;

        AccountingLine previous_line = null;
        for (Iterator line_it = document.getSourceAccountingLines().iterator(); line_it.hasNext() && valid;) {
            AccountingLine current_line = (AccountingLine) line_it.next();
            valid = validateFundGroups(previous_line, current_line);
        }
        return valid;
    }

    /**
     * Validates that two <code>{@link AccountingLine}</code> instances belong to the same Fund Group. Called by
     * <code>{@link #validateFundGroupsInDocument(TransactionalDocument)}</code>
     * 
     * @param previous
     * @param current
     * @return boolean
     */
    protected boolean validateFundGroups(AccountingLine previous, AccountingLine current) {
        boolean valid = true;

        if (previous != null) {
            if (previous.getAccount().getSubFundGroup().getFundGroupCode().equals(current.getAccount().getSubFundGroup().getFundGroupCode())) {
                valid = false;
            }
        }

        return valid;
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
     * 
     * This method checks to see if there is a valid combination of sub type and object level
     * 
     * @param document
     * @param accountingLine
     * @return boolean
     */
    private boolean isValidDocWithSubAndLevel(TransactionalDocument document, AccountingLine accountingLine) {
        boolean retval = true;

        try {
            retval &= succeedsRule(RESTRICTED_COMBINED_CODES, getMockCodeBaseInstance(ObjectType.class, accountingLine.getObjectCode().getFinancialObjectTypeCode()).toString());

            if (retval) {
                retval &= succeedsRule(RESTRICTED_COMBINED_CODES, getMockCodeBaseInstance(ObjSubTyp.class, accountingLine.getObjectCode().getFinancialObjectSubTypeCode()).toString());
            }

            if (retval) {
                retval &= succeedsRule(RESTRICTED_COMBINED_CODES, getMockObjLevelInstance(accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode()).toString());
            }
        }
        catch (Exception e) {
            retval = false;
        }

        if (!retval) {
            String errorObjects[] = { accountingLine.getObjectCode().getFinancialObjectCode(), accountingLine.getObjectCode().getFinancialObjectType().getCode(), accountingLine.getObjectType().getCode(), accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode() };
            GlobalVariables.getErrorMap().put(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE, errorObjects);
        }

        return retval;
    }


    /**
     * 
     * This method creates a reversal date for our document
     * 
     * @param doc
     * @param voucherType
     * @param postPeriod
     * @return if reversal date creation succeeded true
     */
    protected boolean setReversalDate(AuxiliaryVoucherDocument doc, String voucherType, AccountingPeriod postPeriod) {
        // reversal date in period 13 should have the same reversal date as period 12
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Timestamp ts = new Timestamp(date.getTime());
        if (voucherType.equals(RECODE_DOC_TYPE)) {
            // AVRC means the reversal date is the same as the creation date
            ts = doc.getDocumentHeader().getWorkflowDocument().getCreateDate();
        }
        else if (voucherType.equals(ADJUSTMENT_DOC_TYPE)) {
            // there is no reversal date for AVAD
            ts = null;
        }
        else {
            // it must be AVAE
            // first check to see if there is an existing reversal date
            Timestamp revDate = doc.getReversalDate();
            if (revDate != null) {
                // now we need to check to make sure that it is greater than today's date first
                if (revDate.compareTo(ts) >= 0) {
                    ts = revDate;
                }
                else {
                    GlobalVariables.getErrorMap().put("document.reversalDate", ERROR_DOCUMENT_INCORRECT_REVERSAL_DATE);
                    return false;
                }
            }
            else {
                // grab the actual date from the period
                java.sql.Date endDate = postPeriod.getUniversityFiscalPeriodEndDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.set(Calendar.DATE, 15);
                ts = new Timestamp(cal.getTimeInMillis());
            }
        }
        doc.setReversalDate(ts);
        return true;
    }

    /**
     * 
     * This method determins if the posting period is valid for the document type
     * 
     * @param document
     * @param period
     * @param year
     * @return true if it is a valid period for posting into
     */
    protected boolean isValidPeriod(AuxiliaryVoucherDocument document, int period, int year) {
        // first we need to get the period itself to check these things
        AccountingPeriodService perService = SpringServiceLocator.getAccountingPeriodService();
        AccountingPeriod acctPeriod = perService.getByPeriod(new Integer(period).toString(), new Integer(year));

        // can't post into a closed period
        if (acctPeriod.getUniversityFiscalPeriodStatusCode().equalsIgnoreCase(ACCOUNTING_PERIOD_STATUS_CLOSED)) {
            GlobalVariables.getErrorMap().put(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED);
            return false;
        }

        // if current period is period 1 can't post back more than 3 open periods
        // grab the current period
        Timestamp ts = document.getDocumentHeader().getWorkflowDocument().getCreateDate();
        AccountingPeriod currPeriod = perService.getByDate(new Date(ts.getTime()));
        int currPeriodVal = new Integer(currPeriod.getUniversityFiscalPeriodCode()).intValue();
        if (currPeriodVal == 1) {
            if (period < 11) {
                GlobalVariables.getErrorMap().put(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_PERIOD_THREE_OPEN);
                return false;
            }
        }

        // can't post back more than 2 periods
        if (period < currPeriodVal) {
            if ((currPeriodVal - period) > 2) {
                GlobalVariables.getErrorMap().put(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                return false;
            }
        }
        else {
            // if currPeriodVal is less than period then that means it can only be
            // period 2 (see period 1 rule above)and period 13 as the only possible combination,
            // if not then error out
            if (currPeriodVal != 2) {
                GlobalVariables.getErrorMap().put(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                return false;
            }
            else {
                if (period != 13) {
                    GlobalVariables.getErrorMap().put(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                    return false;
                }
            }
        }

        // check for specific posting issues
        String voucherType = getVoucherTypeCode(document);
        if (RECODE_DOC_TYPE.equals(voucherType)) {
            // can't post into a previous fiscal year
            int currFiscalYear = currPeriod.getUniversityFiscalYear().intValue();
            if (!(currFiscalYear < year)) {
                GlobalVariables.getErrorMap().put(DOCUMENT_ERRORS, ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC);
                return false;
            }
            // check the posting period, throw out if period 13
            if (period > 12) {
                GlobalVariables.getErrorMap().put(DOCUMENT_ERRORS, ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC);
                return false;
            }
            else if (period < 1) {
                GlobalVariables.getErrorMap().put(DOCUMENT_ERRORS, ERROR_CUSTOM, "You have entered an incorrect posting period, it must be a number between 1 and 13.");
                return false;
            }
        }
        return true;
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
            GlobalVariables.getErrorMap().put(FINANCIAL_OBJECT_CODE, ERROR_DOCUMENT_AUXILIARY_VOUCHER_INVALID_OBJECT_SUB_TYPE_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectSubTypeCode() });
        }

        return valid;
    }

    /**
     * Determines if period code used in primary key of <code>{@link AccountingPeriod}</code> is allowed.
     * 
     * @param accountingPeriod
     * @return boolean
     */
    protected boolean isPeriodCodeAllowed(AccountingPeriod accountingPeriod) {
        boolean valid = true;

        valid &= succeedsRule(RESTRICTED_PERIOD_CODES, accountingPeriod.getUniversityFiscalPeriodCode());
        if (!valid) {
            GlobalVariables.getErrorMap().put(ACCOUNTING_PERIOD_STATUS_CODE_FIELD, ERROR_CUSTOM, "You have entered an incorrect posting period, it must be a number between 1 and 13.");
        }

        return valid;
    }

    /**
     * Generic factory method for creating instances of <code>{@link KualiCodeBase}</code> like an <code>{@link ObjectType}</code>
     * instance or a <code>{@link ObjSubTyp}</code> instance.<br/>
     * 
     * <p>
     * The mock object method is needed to validate using APC. The parameter uses non-specific <code>{@link KualiCodeBase}</code>
     * codes, so things like chart code are unimportant.
     * </p>
     * 
     * <p>
     * This method uses reflections, so a <code>{@link ClassNotFoundException}</code>,
     * <code>{@link InstantiationException}</code>, <code>{@link IllegalAccessException}</code> may be thrown
     * </p>
     * 
     * @param type
     * @param code
     * @return KualiCodebase
     * @exception ClassNotFoundException
     * @exception InstantiationException
     * @exception IllegalAccessException
     */
    protected KualiCodeBase getMockCodeBaseInstance(Class type, String code) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        KualiCodeBase retval = (KualiCodeBase) type.newInstance();

        retval.setCode(code);

        return retval;
    }

    /**
     * Factory method for creating instances of <code>{@link ObjLevel}</code>. This method is more specific than
     * <code>{@link #getMockCodeBaseInstance(Class, String)}</code> because it is aimed specifically towards
     * <code>{@link ObjLevel}</code> which is not part of the <code>{@link KualiCodeBase}</code> class hieraarchy.<br/>
     * 
     * <p>
     * The mock object method is needed to validate using APC. The parameter uses non-specific <code>{@link ObjLevel}</code>
     * codes, so things like chart code are unimportant.
     * </p>
     * 
     * @param code
     * @return ObjLevel
     */
    protected ObjLevel getMockObjLevelInstance(String code) {
        ObjLevel retval = new ObjLevel();

        retval.setFinancialObjectLevelCode(code);

        return retval;
    }
}
