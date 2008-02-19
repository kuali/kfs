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

import static org.kuali.kfs.KFSConstants.AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_BASE_BUDGET;
import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_BUDGET_STATISTICS;
import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_CURRENT_BUDGET;
import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE;
import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_MONTHLY_BUDGET;
import static org.kuali.kfs.KFSConstants.BLANK_SPACE;
import static org.kuali.kfs.KFSConstants.CREDIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.DEBIT_AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.GENERIC_CODE_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.kfs.KFSConstants.JOURNAL_LINE_HELPER_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.OBJECT_TYPE_CODE_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.SQUARE_BRACKET_LEFT;
import static org.kuali.kfs.KFSConstants.SQUARE_BRACKET_RIGHT;
import static org.kuali.kfs.KFSConstants.VOUCHER_LINE_HELPER_CREDIT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.VOUCHER_LINE_HELPER_DEBIT_PROPERTY_NAME;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES;
import static org.kuali.kfs.KFSKeyConstants.ERROR_REQUIRED;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ZERO_AMOUNT;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT;
import static org.kuali.kfs.KFSKeyConstants.JournalVoucher.ERROR_NEGATIVE_NON_BUDGET_AMOUNTS;
import static org.kuali.kfs.KFSPropertyConstants.ACCOUNTING_PERIOD;
import static org.kuali.kfs.KFSPropertyConstants.BALANCE_TYPE;
import static org.kuali.kfs.KFSPropertyConstants.BALANCE_TYPE_CODE;
import static org.kuali.kfs.KFSPropertyConstants.REFERENCE_NUMBER;
import static org.kuali.kfs.KFSPropertyConstants.REFERENCE_ORIGIN_CODE;
import static org.kuali.kfs.KFSPropertyConstants.REFERENCE_TYPE_CODE;
import static org.kuali.kfs.KFSPropertyConstants.REVERSAL_DATE;
import static org.kuali.kfs.KFSPropertyConstants.SELECTED_ACCOUNTING_PERIOD;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.document.Document;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPostable;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.financial.bo.VoucherSourceAccountingLine;
import org.kuali.module.financial.document.JournalVoucherDocument;

/**
 * This class holds document specific business rules for the Journal Voucher. It overrides methods in the base rule class to apply
 * specific checks.
 */
public class JournalVoucherDocumentRule extends AccountingDocumentRuleBase {

    /**
     * Constructs a JournalVoucherDocumentRule instance.
     */
    public JournalVoucherDocumentRule() {
    }

    /**
     * Performs additional Journal Voucher specific checks every time an accounting line is added. These include 
     * checking to make sure that encumbrance reference fields are filled in under certain circumstances.
     * 
     * @param financialDocument The document the new line is being added to.
     * @param accountingLine The new accounting line being added.
     * @return True if the business rules all pass, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        return super.processCustomAddAccountingLineBusinessRules(document, accountingLine) && validateAccountingLine(accountingLine);
    }

    /**
     * This method performs business rule checks on the accounting line being provided to ensure the accounting line
     * is valid and appropriate for the document.  
     * 
     * @param transactionalDocument The document associated with the accounting line being validated.
     * @param accountingLine The accounting line being validated.
     * @return True if the business rules all pass, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomReviewAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        return super.processCustomReviewAccountingLineBusinessRules(document, accountingLine) && validateAccountingLine(accountingLine);
    }

    /**
     * This method performs business rule checks on the accounting line being updated to the document to ensure the accounting line
     * is valid and appropriate for the document.  Performs additional Journal Voucher specific checks every time an accounting 
     * line is updated.  
     * 
     * @param transactionalDocument The document the accounting line being updated resides within.
     * @param accountingLine The original accounting line.
     * @param updatedAccoutingLine The updated version of the accounting line.
     * @return True if the business rules all pass for the update, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return super.processCustomUpdateAccountingLineBusinessRules(financialDocument, originalAccountingLine, updatedAccountingLine) && validateAccountingLine(updatedAccountingLine);
    }

    /**
     * Implements Journal Voucher specific accountingLine validity checks. These include checking to make sure that encumbrance
     * reference fields are filled in under certain circumstances.
     * 
     * @param accountingLine The accounting line to be validated.
     * @return True if the given accountingLine is valid, false otherwise.
     * 
     * @see #isExternalEncumbranceSpecificBusinessRulesValid(AccountingLine)
     */
    boolean validateAccountingLine(AccountingLine accountingLine) {
        // validate business rules specific to having the balance type set to EXTERNAL ENCUMBRANCE
        return isExternalEncumbranceSpecificBusinessRulesValid(accountingLine);
    }

    /**
     * Performs Journal Voucher document specific save rules. These include checking to make sure that a valid and 
     * active balance type is chosen and that a valid open accounting period is chosen.
     * 
     * @param document The document being saved.
     * @return True if the document being saved passed all the business rules, false otherwise.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    public boolean processCustomSaveDocumentBusinessRules(Document document) {
        JournalVoucherDocument jvDoc = (JournalVoucherDocument) document;
        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);

        boolean valid = true;

        // check the selected balance type
        jvDoc.refreshReferenceObject(BALANCE_TYPE);
        BalanceTyp balanceType = jvDoc.getBalanceType();
        valid &= accountingDocumentRuleUtil.isValidBalanceType(balanceType, JournalVoucherDocument.class, BALANCE_TYPE_CODE, DOCUMENT_ERROR_PREFIX + BALANCE_TYPE_CODE);

        // check the selected accounting period
        jvDoc.refreshReferenceObject(ACCOUNTING_PERIOD);
        AccountingPeriod accountingPeriod = jvDoc.getAccountingPeriod();
        // KFSPropertyConstants.SELECTED_ACCOUNTING_PERIOD is on JournalVoucherForm, not JournalVoucherDocument
        valid &= accountingDocumentRuleUtil.isValidOpenAccountingPeriod(accountingPeriod, JournalVoucherDocument.class, ACCOUNTING_PERIOD, DOCUMENT_ERROR_PREFIX + SELECTED_ACCOUNTING_PERIOD);

        // check the chosen reversal date, only if they entered a value
        if (null != jvDoc.getReversalDate()) {
            java.sql.Date reversalDate = jvDoc.getReversalDate();
            valid &= accountingDocumentRuleUtil.isValidReversalDate(reversalDate, DOCUMENT_ERROR_PREFIX + REVERSAL_DATE);
        }

        return valid;
    }

    // Overridden Helper Methods
    /**
     * Overrides the parent to return true, because Journal Voucher documents do not have to balance in order to be submitted for
     * routing.
     * 
     * @param financialDocument The document being validated.
     * @return True if the balance of the document is valid, false otherwise.
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument financialDocument) {
        return true;
    }

    /**
     * Accounting lines for Journal Vouchers can be positive or negative, just not "$0.00".  
     * 
     * Additionally, accounting lines cannot have negative dollar amounts if the balance type of the 
     * journal voucher allows for general ledger pending entry offset generation or the balance type 
     * is not a budget type code.  
     * 
     * @param document The document when contains the accounting line being validated.
     * @param acocuntingLine The accounting line to be validated.
     * @return True if the accounting line amount is valid, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isAmountValid(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
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
                if (StringUtils.isNotBlank(debitCreditCode) && GL_DEBIT_CODE.equals(debitCreditCode)) {
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
     * This method looks at the current full key path that exists in the ErrorMap structure to determine how to build 
     * the error map for the special journal voucher credit and debit fields since they don't conform to the standard 
     * pattern of accounting lines.
     * 
     * The error map key path is also dependent on whether or not the accounting line containing an error is a new 
     * accounting line or an existing line that is being updated.  This determination is made by searching for 
     * NEW_SOURCE_ACCT_LINE_PROPERTY_NAME in the error path of the global error map.
     * 
     * @param isDebit Identifies whether or not the line we are returning an error path for is a debit accounting line or not.
     * @return The full error map key path for the appropriate amount type.
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
                return JOURNAL_LINE_HELPER_PROPERTY_NAME + indexWithParams + VOUCHER_LINE_HELPER_DEBIT_PROPERTY_NAME;
            }
            else {
                return JOURNAL_LINE_HELPER_PROPERTY_NAME + indexWithParams + VOUCHER_LINE_HELPER_CREDIT_PROPERTY_NAME;
            }

        }

    }

    /**
     * Overrides the parent to return true, because Journal Voucher documents only use the SourceAccountingLines data structures.
     * The list that holds TargetAccountingLines should be empty. This will be checked when the document is "routed" or submitted to
     * post - it's called automatically by the parent's processRouteDocument method.
     * 
     * @param financialDocument The document containing the target accounting lines being validated.
     * @return This method always returns true because Journal Vouchers do not contain target accounting lines.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(AccountingDocument financialDocument) {
        return true;
    }

    /**
     * This method will check to make sure that the required number of source accounting lines for routing, exist in the document.
     * The Journal Voucher only has one set of accounting lines; therefore, we need to use a different message for this.
     * 
     * @param financialDocument The document containing the source accounting lines being validated.
     * @return True if there is one or more source accounting lines in the given document.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(AccountingDocument financialDocument) {
        if (0 == financialDocument.getSourceAccountingLines().size()) {
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
     * @param documentClass The document type class used to retrieve the rules defining if the object code is allowed.
     * @param accountingLine The accounting line containing the object code being validated.
     * @return This method always returns true.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isObjectCodeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectCodeAllowed(Class documentClass, AccountingLine accountingLine) {
        return true;
    }

    // Other Helper Methods Local To This Class
    /**
     * This method checks that values exist in the three reference fields (referenceOriginCode, referenceTypeCode, referenceNumber)
     * that are required if the balance type is set to EXTERNAL ENCUMBRANCE.
     * 
     * @param accountingLine The accounting line being validated.
     * @return True if the fields are filled in, true if the balance type is not EXTERNAL ENCUMBRANCE, false otherwise.
     */
    protected boolean isExternalEncumbranceSpecificBusinessRulesValid(AccountingLine accountingLine) {
        // make sure that the line contains a proper balance type like it should
        BalanceTyp balanceType = accountingLine.getBalanceTyp();
        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        if (!accountingDocumentRuleUtil.isValidBalanceType(balanceType, GENERIC_CODE_PROPERTY_NAME)) {
            return false;
        }
        else if (BALANCE_TYPE_EXTERNAL_ENCUMBRANCE.equals(balanceType.getCode())) {
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
     * @param accountingLine The accounting line being validated.
     * @return True if all of the required external encumbrance reference fields are valid, false otherwise.
     */
    protected boolean isRequiredReferenceFieldsValid(AccountingLine accountingLine) {
        boolean valid = true;

        BusinessObjectEntry boe = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(VoucherSourceAccountingLine.class.getName());
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
     * @param documentClass The type of document the accounting line is contained within.
     * @param accountingLine The accounting line the object type code will be retrieved from.
     * @return True if the object type code exists, false otherwise.
     * 
     * @see org.kuali.core.rule.AddAccountingLineRule#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectTypeAllowed(Class documentClass, AccountingLine accountingLine) {
        String objectTypeCode = accountingLine.getObjectTypeCode();
        if (StringUtils.isNotBlank(objectTypeCode)) {
            return true;
        }
        else {
            String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(ObjectType.class.getName()).getAttributeDefinition(GENERIC_CODE_PROPERTY_NAME).getLabel();
            GlobalVariables.getErrorMap().putError(OBJECT_TYPE_CODE_PROPERTY_NAME, ERROR_REQUIRED, label);
            return false;
        }

    }

    // /**
    //  * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processSourceAccountingLineSufficientFundsCheckingPreparation(FinancialDocument,
    //  *      org.kuali.core.bo.SourceAccountingLine)
    // */
    // @Override
    // protected SufficientFundsItem processSourceAccountingLineSufficientFundsCheckingPreparation(FinancialDocument
    // financialDocument, SourceAccountingLine sourceAccountingLine) {
    // SufficientFundsItem item = null;
    // String balanceTypeCode = ((JournalVoucherDocument) financialDocument).getBalanceTypeCode();
    // // fp_dvj:lp_create_ple.10-1...11-1
    // if (StringUtils.equals(BALANCE_TYPE_ACTUAL, balanceTypeCode) || StringUtils.equals(BALANCE_TYPE_CURRENT_BUDGET,
    // balanceTypeCode)) {
    //
    // String financialObjectCode = sourceAccountingLine.getFinancialObjectCode();
    // String accountSufficientFundsCode = sourceAccountingLine.getAccount().getAccountSufficientFundsCode();
    //
    //
    // // fi_djv:lp_proc_jrnl_ln.37-1
    // String debitCreditCode = null;
    //
    // // fi_djv:lp_proc_jrnl_ln.39-2...51-2
    // if (StringUtils.equals(accountSufficientFundsCode, SF_TYPE_CASH_AT_ACCOUNT) && StringUtils.equals(financialObjectCode,
    // sourceAccountingLine.getChart().getFinancialCashObjectCode())) {
    // // use debit credit code
    // debitCreditCode = sourceAccountingLine.getDebitCreditCode();
    // }
    // else if (!StringUtils.equals(accountSufficientFundsCode, SF_TYPE_CASH_AT_ACCOUNT)) {
    // // fi_djv:lp+proc_jrnl_ln.39-2...43-2
    // if (StringUtils.equals(sourceAccountingLine.getDebitCreditCode(), GL_DEBIT_CODE)) {
    // debitCreditCode = GL_CREDIT_CODE;
    // }
    // else {
    // debitCreditCode = GL_DEBIT_CODE;
    // }
    // }
    // if (StringUtils.isNotBlank(debitCreditCode)) {
    //
    // String chartOfAccountsCode = sourceAccountingLine.getChartOfAccountsCode();
    // String accountNumber = sourceAccountingLine.getAccountNumber();
    // String financialObjectLevelCode = sourceAccountingLine.getObjectCode().getFinancialObjectLevelCode();
    // Integer fiscalYear = sourceAccountingLine.getPostingYear();
    // KualiDecimal lineAmount = sourceAccountingLine.getAmount();
    // String financialObjectTypeCode = sourceAccountingLine.getObjectTypeCode();
    // String sufficientFundsObjectCode =
    // SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(sourceAccountingLine.getObjectCode(),accountSufficientFundsCode);
    //
    // item = buildSufficentFundsItem(accountNumber, accountSufficientFundsCode, lineAmount, chartOfAccountsCode,
    // sufficientFundsObjectCode, debitCreditCode, financialObjectCode, financialObjectLevelCode, fiscalYear,
    // financialObjectTypeCode);
    // }
    // }
    //
    // return item;
    // }
    //
    // /**
    // *
    // * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processTargetAccountingLineSufficientFundsCheckingPreparation(FinancialDocument,
    // *      org.kuali.core.bo.TargetAccountingLine)
    // */
    // @Override
    // protected SufficientFundsItem processTargetAccountingLineSufficientFundsCheckingPreparation(FinancialDocument
    // financialDocument, TargetAccountingLine targetAccountingLine) {
    // if (targetAccountingLine != null) {
    //
    // throw new IllegalArgumentException("JV document doesn't have target accounting lines. This method should have never been
    // entered");
    // }
    // return null;
    // }
}
