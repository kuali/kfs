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

import static org.kuali.Constants.AMOUNT_PROPERTY_NAME;
import static org.kuali.Constants.BALANCE_TYPE_ACTUAL;
import static org.kuali.Constants.BALANCE_TYPE_BASE_BUDGET;
import static org.kuali.Constants.BALANCE_TYPE_BUDGET_STATISTICS;
import static org.kuali.Constants.BALANCE_TYPE_CURRENT_BUDGET;
import static org.kuali.Constants.BALANCE_TYPE_MONTHLY_BUDGET;
import static org.kuali.Constants.CREDIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.Constants.DEBIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.Constants.GENERIC_CODE_PROPERTY_NAME;
import static org.kuali.Constants.GL_CREDIT_CODE;
import static org.kuali.Constants.GL_DEBIT_CODE;
import static org.kuali.Constants.JOURNAL_LINE_HELPER_CREDIT_PROPERTY_NAME;
import static org.kuali.Constants.JOURNAL_LINE_HELPER_DEBIT_PROPERTY_NAME;
import static org.kuali.Constants.JOURNAL_LINE_HELPER_PROPERTY_NAME;
import static org.kuali.Constants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
import static org.kuali.Constants.OBJECT_TYPE_CODE_PROPERTY_NAME;
import static org.kuali.Constants.SF_TYPE_CASH_AT_ACCOUNT;
import static org.kuali.Constants.SQUARE_BRACKET_LEFT;
import static org.kuali.Constants.SQUARE_BRACKET_RIGHT;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES;
import static org.kuali.KeyConstants.ERROR_REQUIRED;
import static org.kuali.KeyConstants.ERROR_ZERO_AMOUNT;
import static org.kuali.KeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT;
import static org.kuali.KeyConstants.JournalVoucher.ERROR_NEGATIVE_NON_BUDGET_AMOUNTS;
import static org.kuali.PropertyConstants.ACCOUNTING_PERIOD;
import static org.kuali.PropertyConstants.BALANCE_TYPE;
import static org.kuali.PropertyConstants.BALANCE_TYPE_CODE;
import static org.kuali.PropertyConstants.REFERENCE_NUMBER;
import static org.kuali.PropertyConstants.REFERENCE_ORIGIN_CODE;
import static org.kuali.PropertyConstants.REFERENCE_TYPE_CODE;
import static org.kuali.PropertyConstants.REVERSAL_DATE;
import static org.kuali.PropertyConstants.SELECTED_ACCOUNTING_PERIOD;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.BALANCE_TYPE_CODE.EXTERNAL_ENCUMBRANCE;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.financial.document.JournalVoucherDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.util.SufficientFundsItemHelper.SufficientFundsItem;

/**
 * This class holds document specific business rules for the Journal Voucher. It overrides methods in the base rule class to apply
 * specific checks.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class JournalVoucherDocumentRule extends TransactionalDocumentRuleBase {

    /**
     * Constructs a JournalVoucherDocumentRule instance and sets the logger.
     */
    public JournalVoucherDocumentRule() {
    }

    /**
     * Performs additional Journal Voucher specific checks every time an accounting line is added. These include checking to make
     * sure that encumbrance reference fields are filled in under certain circumstances.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomAddAccountingLineBusinessRules(document, accountingLine) && validateAccountingLine(accountingLine);
    }

    /**
     * Performs additional Journal Voucher specific checks every time an accounting line is updated.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomReviewAccountingLineBusinessRules(document, accountingLine) && validateAccountingLine(accountingLine);
    }

    /**
     * Performs additional Journal Voucher specific checks every time an accounting line is updated.
     * @param transactionalDocument 
     * @param originalAccountingLine 
     * @param updatedAccountingLine 
     * @return boolean
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomUpdateAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return super.processCustomUpdateAccountingLineBusinessRules(transactionalDocument, originalAccountingLine, updatedAccountingLine) && validateAccountingLine(updatedAccountingLine);
    }

    /**
     * Implements Journal Voucher specific accountingLine validity checks. These include checking to make sure that encumbrance
     * reference fields are filled in under certain circumstances.
     * 
     * @param accountingLine
     * @return true if the given accountingLine is valid
     */
    boolean validateAccountingLine(AccountingLine accountingLine) {
        // validate business rules specific to having the balance type set to EXTERNAL ENCUMBRANCE
        return isExternalEncumbranceSpecificBusinessRulesValid(accountingLine);
    }

    /**
     * Performs Journal Voucher document specific save rules. These include checking to make sure that a valid and active balance
     * type is chosen and that a valid open accounting period is chosen.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    public boolean processCustomSaveDocumentBusinessRules(Document document) {
        JournalVoucherDocument jvDoc = (JournalVoucherDocument) document;

        boolean valid = true;

        // check the selected balance type
        jvDoc.refreshReferenceObject(BALANCE_TYPE);
        BalanceTyp balanceType = jvDoc.getBalanceType();
        // todo: avoid naming conflict between PropertyConstants.BALANCE_TYPE_CODE and
        // TransactionalDocumentsRuleBaseConstants.BALANCE_TYPE_CODE by renaming the latter to BALANCE_TYPE_CODES
        valid &= TransactionalDocumentRuleUtil.isValidBalanceType(balanceType, JournalVoucherDocument.class, BALANCE_TYPE_CODE, DOCUMENT_ERROR_PREFIX + BALANCE_TYPE_CODE);

        // check the selected accounting period
        jvDoc.refreshReferenceObject(ACCOUNTING_PERIOD);
        AccountingPeriod accountingPeriod = jvDoc.getAccountingPeriod();
        // PropertyConstants.SELECTED_ACCOUNTING_PERIOD is on JournalVoucherForm, not JournalVoucherDocument
        valid &= TransactionalDocumentRuleUtil.isValidOpenAccountingPeriod(accountingPeriod, JournalVoucherDocument.class, ACCOUNTING_PERIOD, DOCUMENT_ERROR_PREFIX + SELECTED_ACCOUNTING_PERIOD);

        // check the chosen reversal date, only if they entered a value
        if (null != jvDoc.getReversalDate()) {
            Timestamp reversalDate = jvDoc.getReversalDate();
            valid &= TransactionalDocumentRuleUtil.isValidReversalDate(reversalDate, DOCUMENT_ERROR_PREFIX + REVERSAL_DATE);
        }

        return valid;
    }

    /**
     * This method contains Journal Voucher document specific GLPE explicit entry attribute sets.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        JournalVoucherDocument jvDoc = (JournalVoucherDocument) transactionalDocument;

        // set the appropriate accounting period values according to the values chosen by the user
        explicitEntry.setUniversityFiscalPeriodCode(jvDoc.getPostingPeriodCode());
        explicitEntry.setUniversityFiscalYear(jvDoc.getPostingYear());

        // set the object type code directly from what was entered in the interface
        explicitEntry.setFinancialObjectTypeCode(accountingLine.getObjectTypeCode());

        // set the balance type code directly from what was entered in the interface
        explicitEntry.setFinancialBalanceTypeCode(accountingLine.getBalanceTypeCode());

        // set the debit/credit code appropriately
        jvDoc.refreshReferenceObject(BALANCE_TYPE);
        if (jvDoc.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            explicitEntry.setTransactionDebitCreditCode(getEntryValue(accountingLine.getDebitCreditCode(), GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SPACE));
        }
        else {
            explicitEntry.setTransactionDebitCreditCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SPACE);
        }

        // set the encumbrance update code
        explicitEntry.setTransactionEncumbranceUpdateCode(getEntryValue(accountingLine.getEncumbranceUpdateCode(), GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SPACE));

        // set the reversal date to what what specified at the document level
        if (jvDoc.getReversalDate() != null) {
            explicitEntry.setFinancialDocumentReversalDate(new java.sql.Date(jvDoc.getReversalDate().getTime()));
        }
    }

    /**
     * A Journal Voucher document doesn't generate an offset entry at all, so this method overrides to do nothing more than return
     * true. This will be called by the parent's processGeneralLedgerPendingEntries method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processOffsetGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper, org.kuali.core.bo.AccountingLine,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected boolean processOffsetGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        sequenceHelper.decrement(); // the parent already increments; assuming that all documents have offset entries
        return true;
    }

    // Overridden Helper Methods
    /**
     * Overrides the parent to return true, because Journal Voucher documents do not have to balance in order to be submitted for
     * routing.
     * 
     * @param transactionalDocument
     * @return boolean True if the balance of the document is valid, false other wise.
     */
    @Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * Accounting lines for Journal Vouchers can be positive or negative, just not "$0.00".
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isAmountValid(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
        KualiDecimal amount = accountingLine.getAmount();

        JournalVoucherDocument jvDoc = (JournalVoucherDocument) document;
        jvDoc.refreshReferenceObject(BALANCE_TYPE);

        if (jvDoc.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            // check for negative or zero amounts
            if (amount.isZero()) { // if 0
                    GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(true), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(false), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");

                return false;
            }
            else if (amount.isNegative()) { // entered a negative number
                String debitCreditCode = accountingLine.getDebitCreditCode();
                if (StringUtils.isNotBlank(debitCreditCode) && GENERAL_LEDGER_PENDING_ENTRY_CODE.DEBIT.equals(debitCreditCode)) {
                    GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(true), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
                }
                else {
                    GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(false), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
                }

                return false;
            }
        }
        else {
            // Check for zero amounts
            if (amount.isZero()) { // amount == 0
                GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ERROR_ZERO_AMOUNT, "an accounting line");
               return false;
            }
            else if (amount.isNegative()) {
                if (!accountingLine.getBalanceTypeCode().equals(BALANCE_TYPE_BASE_BUDGET) && !accountingLine.getBalanceTypeCode().equals(BALANCE_TYPE_CURRENT_BUDGET) && !accountingLine.getBalanceTypeCode().equals(BALANCE_TYPE_MONTHLY_BUDGET) && !accountingLine.getBalanceTypeCode().equals(BALANCE_TYPE_BUDGET_STATISTICS)) {
                    GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ERROR_NEGATIVE_NON_BUDGET_AMOUNTS);
                }
            }
        }

        return true;
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
                return JOURNAL_LINE_HELPER_PROPERTY_NAME + indexWithParams + JOURNAL_LINE_HELPER_DEBIT_PROPERTY_NAME;
            }
            else {
                return JOURNAL_LINE_HELPER_PROPERTY_NAME + indexWithParams + JOURNAL_LINE_HELPER_CREDIT_PROPERTY_NAME;
            }

        }

    }

    /**
     * Overrides the parent to return true, because Journal Voucher documents only use the SourceAccountingLines data structures.
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
     * This method will check to make sure that the required number of source accounting lines for routing, exist in the document.
     * The Journal Voucher only has one set of accounting lines; therefore, we need to use a differe message for this.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        if (0 == transactionalDocument.getSourceAccountingLines().size()) {
            GlobalVariables.getErrorMap().putError("document.sourceAccountingLines", ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Overrides the parent to return true, because Journal Voucher documents aren't restricted from using any object code. This is
     * part of the "save" check that gets done. This method is called automatically by the parent's processSaveDocument method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isObjectCodeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectCodeAllowed(AccountingLine accountingLine) {
        return true;
    }

    // Other Helper Methods Local To This Class
    /**
     * This method checks that values exist in the three reference fields (referenceOriginCode, referenceTypeCode, referenceNumber)
     * that are required if the balance type is set to EXTERNAL ENCUMBRANCE.
     * 
     * @param accountingLine
     * @return boolean True if the fields are filled in, true if the balance type is not EXTERNAL ENCUMBRANCE, false otherwise.
     */
    private boolean isExternalEncumbranceSpecificBusinessRulesValid(AccountingLine accountingLine) {
        // make sure that the line contains a proper balance type like it should
        BalanceTyp balanceType = accountingLine.getBalanceTyp();
        if (!TransactionalDocumentRuleUtil.isValidBalanceType(balanceType, GENERIC_CODE_PROPERTY_NAME)) {
            return false;
        }
        else if (EXTERNAL_ENCUMBRANCE.equals(balanceType.getCode())) {
            // now check to make sure that the three extra fields (referenceOriginCode, referenceTypeCode, referenceNumber) have
            // values in them
            return isRequiredReferenceFieldsValid(accountingLine);
        }
        else {
            return true;
        }
    }

    /**
     * This method checks that values exist in the three reference fields that are required if the balance type is set to EXTERNAL
     * ENCUMBRANCE.
     * 
     * @param accountingLine
     * @return True if all of the required external encumbrance reference fields are valid, false otherwise.
     */
    private boolean isRequiredReferenceFieldsValid(AccountingLine accountingLine) {
        boolean valid = true;

        BusinessObjectEntry boe = SpringServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(SourceAccountingLine.class);
        if (StringUtils.isEmpty(accountingLine.getReferenceOriginCode())) {
            putRequiredPropertyError(boe, REFERENCE_ORIGIN_CODE);
            valid = false;
        }
        if (StringUtils.isEmpty(accountingLine.getReferenceNumber())) {
            putRequiredPropertyError(boe, REFERENCE_NUMBER);
            valid = false;
        }
        if (StringUtils.isEmpty(accountingLine.getReferenceTypeCode())) {
            putRequiredPropertyError(boe, REFERENCE_TYPE_CODE);
            valid = false;
        }
        return valid;
    }

    /**
     * The JV allows any object type b/c it is up to the user to enter it into the interface, but it is required. The existence
     * check is done for us automatically by the data dictionary validation if a value exists; beforehand so we can assume that any
     * value in that field is valid.
     * 
     * @see org.kuali.core.rule.AddAccountingLineRule#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        String objectTypeCode = accountingLine.getObjectTypeCode();
        if (StringUtils.isNotBlank(objectTypeCode)) {
            return true;
        }
        else {
            String label = SpringServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(ObjectType.class).getAttributeDefinition(GENERIC_CODE_PROPERTY_NAME).getLabel();
            GlobalVariables.getErrorMap().putError(OBJECT_TYPE_CODE_PROPERTY_NAME, ERROR_REQUIRED, label);
            return false;
        }

    }

    /**
     * the following are credits (return false)
     * <ol>
     * <li> (debitCreditCode isNotBlank) && debitCreditCode != 'D'
     * </ol>
     * the following are debits (return true)
     * <ol>
     * <li> debitCreditCode == 'D'
     * <li> debitCreditCode isBlank
     * </ol>
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) throws IllegalStateException {
        String debitCreditCode = accountingLine.getDebitCreditCode();
        
        boolean isDebit = StringUtils.isBlank(debitCreditCode)||IsDebitUtils.isDebitCode(debitCreditCode);

        return isDebit;
    }

    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processSourceAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument,
     *      org.kuali.core.bo.SourceAccountingLine)
     */
    @Override
    protected SufficientFundsItem processSourceAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument transactionalDocument, SourceAccountingLine sourceAccountingLine) {
        SufficientFundsItem item = null;
        String balanceTypeCode = ((JournalVoucherDocument) transactionalDocument).getBalanceTypeCode();
        // fp_dvj:lp_create_ple.10-1...11-1
        if (StringUtils.equals(BALANCE_TYPE_ACTUAL, balanceTypeCode) || StringUtils.equals(BALANCE_TYPE_CURRENT_BUDGET, balanceTypeCode)) {

            String financialObjectCode = sourceAccountingLine.getFinancialObjectCode();
            String accountSufficientFundsCode = sourceAccountingLine.getAccount().getAccountSufficientFundsCode();


            // fi_djv:lp_proc_jrnl_ln.37-1
            String debitCreditCode = null;

            // fi_djv:lp_proc_jrnl_ln.39-2...51-2
            if (StringUtils.equals(accountSufficientFundsCode, SF_TYPE_CASH_AT_ACCOUNT) && StringUtils.equals(financialObjectCode, SpringServiceLocator.getSufficientFundsService().getFinancialObjectCodeForCashInBank())) {
                // use debit credit code
                debitCreditCode = sourceAccountingLine.getDebitCreditCode();
            }
            else if (!StringUtils.equals(accountSufficientFundsCode, SF_TYPE_CASH_AT_ACCOUNT)) {
                // fi_djv:lp+proc_jrnl_ln.39-2...43-2
                if (StringUtils.equals(sourceAccountingLine.getDebitCreditCode(), GL_DEBIT_CODE)) {
                    debitCreditCode = GL_CREDIT_CODE;
                }
                else {
                    debitCreditCode = GL_DEBIT_CODE;
                }
            }
            if (StringUtils.isNotBlank(debitCreditCode)) {

                String chartOfAccountsCode = sourceAccountingLine.getChartOfAccountsCode();
                String accountNumber = sourceAccountingLine.getAccountNumber();
                String financialObjectLevelCode = sourceAccountingLine.getObjectCode().getFinancialObjectLevelCode();
                Integer fiscalYear = sourceAccountingLine.getPostingYear();
                KualiDecimal lineAmount = sourceAccountingLine.getAmount();
                String financialObjectTypeCode = sourceAccountingLine.getObjectTypeCode();
                String sufficientFundsObjectCode = SpringServiceLocator.getSufficientFundsService().getSufficientFundsObjectCode(chartOfAccountsCode, financialObjectCode, accountSufficientFundsCode, financialObjectLevelCode);

                item = buildSufficentFundsItem(accountNumber, accountSufficientFundsCode, lineAmount, chartOfAccountsCode, sufficientFundsObjectCode, debitCreditCode, financialObjectCode, financialObjectLevelCode, fiscalYear, financialObjectTypeCode);
            }
        }

        return item;
    }

    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processTargetAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument,
     *      org.kuali.core.bo.TargetAccountingLine)
     */
    @Override
    protected SufficientFundsItem processTargetAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument transactionalDocument, TargetAccountingLine targetAccountingLine) {
        if (targetAccountingLine != null) {

            throw new IllegalArgumentException("JV document doesn't have target accounting lines. This method should have never been entered");
        }
        return null;
    }

    /**
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        if (isValid) {
            isValid &= isAllAccountingLinesMatchingBudgetYear((TransactionalDocument) document);
        }

        return isValid;
    }

}
