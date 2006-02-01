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

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.datadictionary.BusinessObjectEntry;
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
        LOG = org.apache.log4j.Logger.getLogger(JournalVoucherDocumentRule.class);
    }

    /**
     * Performs additional Journal Voucher specific checks every time an accounting line is added. These include checking to make
     * sure that encumbrance reference fields are filled in under certain circumstances.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomAddAccountingLineBusinessRules( document, accountingLine) && validateAccountingLine(document, accountingLine);
    }

    /**
     * Performs additional Journal Voucher specific checks every time an accounting line is updated.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomReviewAccountingLineBusinessRules( document, accountingLine) && validateAccountingLine(document, accountingLine);
    }
    
    /**
     * Performs additional Journal Voucher specific checks every time an accounting line is updated.
     * 
     * @see org.kuali.core.rule.UpdateAccountingLineRule#processCustomUpdateAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    public boolean processCustomUpdateAccountingLineBusinessRules(TransactionalDocument transactionalDocument,
            AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return super.processCustomUpdateAccountingLineBusinessRules( transactionalDocument, originalAccountingLine, updatedAccountingLine) && 
            validateAccountingLine(transactionalDocument, updatedAccountingLine);
    }

    /**
     * Implements Journal Voucher specific accountingLine validity checks.  These include checking to make
     * sure that encumbrance reference fields are filled in under certain circumstances.

     * @param document
     * @param accountingLine
     * @return true if the given accountingLine is valid
     */
    public boolean validateAccountingLine(TransactionalDocument document, AccountingLine accountingLine) {
        // validate business rules specific to having the balance type set to EXTERNAL ENCUMBRANCE
        // TODO - we may not need this when the two business objects get tied in properly
        return isExternalEncumbranceSpecificBusinessRulesValid(accountingLine);
    }

    /**
     * Performs Journal Voucher document specific save rules. These include checking to make sure that a valid and active balance
     * type is chosen and that a valid open accounting period is chosen.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.TransactionalDocument)
     */
    public boolean processCustomSaveDocumentBusinessRules(TransactionalDocument document) {
        JournalVoucherDocument jvDoc = (JournalVoucherDocument) document;

        boolean valid = true;

        // check the selected balance type
        jvDoc.refreshReferenceObject("balanceType");
        BalanceTyp balanceType = jvDoc.getBalanceType();
        valid &= TransactionalDocumentRuleUtil.isValidBalanceType(balanceType, Constants.JOURNAL_VOUCHER_BALANCE_TYPE_PROPERTY_NAME);

        // check the selected accounting period
        jvDoc.refreshReferenceObject("accountingPeriod");
        AccountingPeriod accountingPeriod = jvDoc.getAccountingPeriod();
        valid &= TransactionalDocumentRuleUtil.isValidOpenAccountingPeriod(accountingPeriod, Constants.JOURNAL_VOUCHER_SELECTED_ACCOUNTING_PERIOD_PROPERTY_NAME);

        // check the chosen reversal date, only if they entered a value
        if(null != jvDoc.getReversalDate()) {
            Timestamp reversalDate = jvDoc.getReversalDate();
            valid &= TransactionalDocumentRuleUtil.isValidReversalDate(reversalDate, Constants.JOURNAL_VOUCHER_REVERSAL_DATE_PROPERTY_NAME);
        }

        return valid;
    }

    /**
     * This method contains Journal Voucher document specific GLPE explicit entry attribute sets.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument,
            AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        JournalVoucherDocument jvDoc = (JournalVoucherDocument) transactionalDocument;

        // set the appropriate accounting period values according to the values chosen by the user
        explicitEntry.setUniversityFiscalPeriodCode(jvDoc.getPostingPeriodCode());
        explicitEntry.setUniversityFiscalYear(jvDoc.getPostingYear());
        
        //set the object type code directly from what was entered in the interface
        explicitEntry.setFinancialObjectTypeCode(accountingLine.getObjectTypeCode());
        
        //set the balance type code directly from what was entered in the interface
        explicitEntry.setFinancialBalanceTypeCode(accountingLine.getBalanceTypeCode());

        // set the debit/credit code appropriately
        jvDoc.refreshReferenceObject("balanceType");
        if (jvDoc.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            explicitEntry.setTransactionDebitCreditCode(getEntryValue(accountingLine.getDebitCreditCode(), 
                    GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SPACE));
        }
        else {
            explicitEntry.setTransactionDebitCreditCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SPACE);
        }

        // set the encumbrance update code
        explicitEntry.setTransactionEncumbranceUpdtCd(getEntryValue(accountingLine.getEncumbranceUpdateCode(), 
                GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SPACE));

        // set the reversal date to what what specified at the document level
        if ( jvDoc.getReversalDate() != null ) {
          explicitEntry.setFinancialDocumentReversalDate(new java.sql.Date(jvDoc.getReversalDate().getTime()));
        }
    }
    
    /**
     * Needed to overrided to set the amount of the GLPE to the amount of the accounting line.  It's a straight mapping.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#getGeneralLedgerPendingEntryAmountForAccountingLine(org.kuali.core.bo.AccountingLine)
     */
    protected KualiDecimal getGeneralLedgerPendingEntryAmountForAccountingLine(AccountingLine accountingLine) {
        return accountingLine.getAmount();
    }

    /**
     * A Journal Voucher document doesn't generate an offset entry at all, so this method overrides to do nothing more than return
     * true. This will be called by the parent's processGeneralLedgerPendingEntries method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processOffsetGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper, org.kuali.core.bo.AccountingLine,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    protected boolean processOffsetGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument,
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy,
            GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
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
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * Accounting lines for Journal Vouchers can be positive or negative, just not "$0.00".
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isAmountValid(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
        KualiDecimal ZERO = new KualiDecimal(0);
        KualiDecimal amount = accountingLine.getAmount();

        JournalVoucherDocument jvDoc = (JournalVoucherDocument) document;
        jvDoc.refreshReferenceObject("balanceType");

        if (jvDoc.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            // check for negative or zero amounts
            if (ZERO.compareTo(amount) == 0) { // if 0
                GlobalVariables.getErrorMap().putWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(true), 
                        KeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
                GlobalVariables.getErrorMap().putWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(false), 
                        KeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
                
                return false;
            } else if(ZERO.compareTo(amount) == 1) {  //entered a negative number
                String debitCreditCode = accountingLine.getDebitCreditCode();
                if(StringUtils.isNotBlank(debitCreditCode) && GENERAL_LEDGER_PENDING_ENTRY_CODE.DEBIT.equals(debitCreditCode)) {
                    GlobalVariables.getErrorMap().putWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(true), 
                            KeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
                } else {
                    GlobalVariables.getErrorMap().putWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(false), 
                            KeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
                }
                
                return false;
            }
        }
        else {
            // Check for zero amounts
            if (ZERO.compareTo(amount) == 0) { // amount == 0
                GlobalVariables.getErrorMap().put(Constants.AMOUNT_PROPERTY_NAME, KeyConstants.ERROR_ZERO_AMOUNT,
                        "an accounting line");
                return false;
            }
        }

        return true;
    }
    
    /**
     * This method looks at the current full key path that exists in the ErrorMap structure to determine 
     * how to build the error map for the special journal voucher credit and debit fields since they don't 
     * conform to the standard pattern of accounting lines.
     * @param isDebit
     * @return String
     */
    private String buildErrorMapKeyPathForDebitCreditAmount(boolean isDebit) {
        // determine if we are looking at a new line add or an update
        boolean isNewLineAdd = GlobalVariables.getErrorMap().getErrorPath().contains(Constants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME);
        isNewLineAdd |= GlobalVariables.getErrorMap().getErrorPath().contains(Constants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME);
        
        if(isNewLineAdd) {
            if(isDebit) {
                return Constants.DEBIT_AMOUNT_PROPERTY_NAME;
            } else {
                return Constants.CREDIT_AMOUNT_PROPERTY_NAME;
            }
        } else {
            String index = StringUtils.substringBetween(GlobalVariables.getErrorMap().getKeyPath("", true), 
                    Constants.SQUARE_BRACKET_LEFT,
                    Constants.SQUARE_BRACKET_RIGHT);
            String indexWithParams = Constants.SQUARE_BRACKET_LEFT + index + Constants.SQUARE_BRACKET_RIGHT;
            if(isDebit) {
                return Constants.JOURNAL_LINE_HELPER_PROPERTY_NAME + indexWithParams + Constants.JOURNAL_LINE_HELPER_DEBIT_PROPERTY_NAME;
            } else {
                return Constants.JOURNAL_LINE_HELPER_PROPERTY_NAME + indexWithParams + Constants.JOURNAL_LINE_HELPER_CREDIT_PROPERTY_NAME;
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
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * This method will check to make sure that the required number of source accounting lines for routing, exist in the document.
     * The Journal Voucher only has one set of accounting lines; therefore, we need to use a differe message for this.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        if (0 == transactionalDocument.getSourceAccountingLines().size()) {
            GlobalVariables.getErrorMap().put("document.sourceAccountingLines",
                    KeyConstants.ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES);
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
        if (!TransactionalDocumentRuleUtil.isValidBalanceType(balanceType, Constants.DOCUMENT_ERRORS)) {
            return false;
        }
        else if (BALANCE_TYPE_CODE.EXTERNAL_ENCUMBRANCE.equals(balanceType.getCode())) {
            // now check to make sure that the three extra fields (referenceOriginCode, referenceTypeCode, referenceNumber) have
            // values in them
            if (!isRequiredReferenceFieldsValid(accountingLine)) {
                return false;
            }
            else {
                return true;
            }
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

        BusinessObjectEntry boe = SpringServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(
                SourceAccountingLine.class);
        // TODO: Eventually these will be objects to check, not strings and these should be handled by "is" methods
        // in the AddAccountingLineRule interface
        if (StringUtils.isEmpty(accountingLine.getReferenceOriginCode())) {
            String label = boe.getAttributeDefinition(Constants.REFERENCE_ORIGIN_CODE_PROPERTY_NAME).getLabel();
            GlobalVariables.getErrorMap().put(Constants.REFERENCE_ORIGIN_CODE_PROPERTY_NAME, KeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }
        if (StringUtils.isEmpty(accountingLine.getReferenceNumber())) {
            String label = boe.getAttributeDefinition(Constants.REFERENCE_NUMBER_PROPERTY_NAME).getLabel();
            GlobalVariables.getErrorMap().put(Constants.REFERENCE_NUMBER_PROPERTY_NAME, KeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }
        if (StringUtils.isEmpty(accountingLine.getReferenceTypeCode())) {
            String label = boe.getAttributeDefinition(Constants.REFERENCE_TYPE_CODE_PROPERTY_NAME).getLabel();
            GlobalVariables.getErrorMap().put(Constants.REFERENCE_TYPE_CODE_PROPERTY_NAME, KeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }
        return valid;
    }

    /**
     * The JV allows any object type b/c it is up to the user to enter it into the interface, but it is 
     * required.  The existence check is done for us automatically by the data dictionary validation if 
     * a value exists; beforehand so we can assume that any value in that field is valid.
     * 
     * @see org.kuali.core.rule.AddAccountingLineRule#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        String objectTypeCode = accountingLine.getObjectTypeCode();
        if(StringUtils.isNotBlank(objectTypeCode)) {
            return true;
        } else {
            String label = SpringServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(ObjectType.class).getAttributeDefinition(Constants.GENERIC_CODE_PROPERTY_NAME).getLabel();
            GlobalVariables.getErrorMap().put(Constants.OBJECT_TYPE_CODE_PROPERTY_NAME, KeyConstants.ERROR_REQUIRED, label);
            return false;
        }
        
    }

    /**
     * Overrides the parent b/c the JV allows user to directly manipulate whether a document is a debit or a credit in the
     * interface.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDebit(org.kuali.core.bo.AccountingLine)
     */
    protected boolean isDebit(AccountingLine accountingLine) throws IllegalStateException {
        if(StringUtils.isNotBlank(accountingLine.getDebitCreditCode())) {
            return accountingLine.getDebitCreditCode().equals(GENERAL_LEDGER_PENDING_ENTRY_CODE.DEBIT);
        } else {
            return true;
        }
    }
}