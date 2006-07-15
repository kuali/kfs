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
import static org.kuali.KeyConstants.ERROR_ACCOUNTING_LINES_DIFFERENT_BUDGET_YEAR;
import static org.kuali.KeyConstants.ERROR_CUSTOM;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_THREE_OPEN;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_BALANCE_CONSIDERING_CREDIT_AND_DEBIT_AMOUNTS;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE;
import static org.kuali.KeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT;
import static org.kuali.KeyConstants.AuxiliaryVoucher.ERROR_DOCUMENT_AUXILIARY_VOUCHER_INVALID_OBJECT_SUB_TYPE_CODE;
import static org.kuali.PropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.AUXILIARY_VOUCHER_SECURITY_GROUPING;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.GENERAL_LEDGER_PENDING_ENTRY_OFFSET_CODE;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_COMBINED_CODES;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_OBJECT_SUB_TYPE_CODES;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_PERIOD_CODES;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.KualiCodeBase;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjSubTyp;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.financial.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.util.SufficientFundsItemHelper.SufficientFundsItem;

/**
 * Business rule(s) applicable to <code>{@link AuxiliaryVoucherDocument}</code>.
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
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) throws IllegalStateException {
        IsDebitUtils.disallowErrorCorrectionDocumentCheck(this, transactionalDocument);
        String debitCreditCode = accountingLine.getDebitCreditCode();
        if (StringUtils.isBlank(debitCreditCode)) {
            throw new IllegalStateException(IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage);
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
        if (KualiDecimal.ZERO.equals(amount)) { // if 0
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(true), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(buildErrorMapKeyPathForDebitCreditAmount(false), ERROR_ZERO_OR_NEGATIVE_AMOUNT, "an accounting line");

            retval = false;
        }
        else if (amount.isNegative()) { // entered a negative number
            String debitCreditCode = accountingLine.getDebitCreditCode();
            if (StringUtils.isNotBlank(debitCreditCode) && GENERAL_LEDGER_PENDING_ENTRY_CODE.DEBIT.equals(debitCreditCode)) {
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
                return AUXILIARY_LINE_HELPER_PROPERTY_NAME + indexWithParams + JOURNAL_LINE_HELPER_DEBIT_PROPERTY_NAME;
            }
            else {
                return AUXILIARY_LINE_HELPER_PROPERTY_NAME + indexWithParams + JOURNAL_LINE_HELPER_CREDIT_PROPERTY_NAME;
            }
        }
    }

    /**
     * This method sets the appropriate document type and object type codes into the GLPEs based on the 
     * type of AV document chosen.
     * 		
     * @see TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine, GeneralLedgerPendingEntry)
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processExplicitGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper, org.kuali.core.bo.AccountingLine,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument document, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        AuxiliaryVoucherDocument auxVoucher = (AuxiliaryVoucherDocument) document;

        Timestamp reversalDate = auxVoucher.getReversalDate();
        if(reversalDate != null) {
            explicitEntry.setFinancialDocumentReversalDate(new java.sql.Date(reversalDate.getTime()));
        } else {
            explicitEntry.setFinancialDocumentReversalDate(null);
        }
        explicitEntry.setFinancialDocumentTypeCode(getDocumentTypeCodeBasedOnVoucherTypeCode(auxVoucher)); // make sure to use the accrual type as the document type
        explicitEntry.setFinancialObjectTypeCode(getObjectTypeCode(accountingLine));
        explicitEntry.setUniversityFiscalPeriodCode(auxVoucher.getPostingPeriodCode()); // use chosen posting period code
        explicitEntry.setUniversityFiscalYear(auxVoucher.getPostingYear()); // use chosen posting year
    }

    /**
     * An Accrual Voucher only generates offsets if it is a recode (AVRC).  So this method overrides to do nothing more than return
     * true if it's not a recode.  If it is a recode, then it is responsible for generating two offsets with a document type of DI.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processOffsetGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper, org.kuali.core.bo.AccountingLine,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected boolean processOffsetGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        AuxiliaryVoucherDocument auxVoucher = (AuxiliaryVoucherDocument) transactionalDocument;

        // do not generate an offset entry if this is a normal or adjustment AV type  
        if (auxVoucher.isAccrualType() || auxVoucher.isAdjustmentType()) {
            return processOffsetGeneralLedgerPendingEntryForAccrualsAndAdjustments(transactionalDocument, sequenceHelper, accountingLineCopy, explicitEntry, offsetEntry);
        }
        else if (auxVoucher.isRecodeType()) { // recodes generate offsets
            return processOffsetGeneralLedgerPendingEntryForRecodes(transactionalDocument, sequenceHelper, accountingLineCopy, explicitEntry, offsetEntry);
        }
        else {
            throw new IllegalStateException("Illegal auxiliary voucher type: " + auxVoucher.getTypeCode());
        }
    }

    /**
     * This method handles generating or not generating the appropriate offsets if the AV type is a recode.
     * 
     * @param transactionalDocument
     * @param sequenceHelper
     * @param accountingLineCopy
     * @param explicitEntry
     * @param offsetEntry
     * @return boolean
     */
    private boolean processOffsetGeneralLedgerPendingEntryForRecodes(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        // the explicit entry has already been generated and added to the list, so to get the right offset, we have to set the value of the document type code on the explicit 
        // to the type code for a DI document so that it gets passed into the next call and we retrieve the right offset definition since these offsets are 
        // specific to Distrib. of Income and Expense documents - we need to do a deep copy though so we don't do this by reference
        GeneralLedgerPendingEntry explicitEntryDeepCopy = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
        explicitEntryDeepCopy.setFinancialDocumentTypeCode(SpringServiceLocator.getDocumentTypeService().getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class));

        // call the super to process an offset entry; see the customize method below for AVRC specific attribute values
        // pass in the explicit deep copy
        boolean success = super.processOffsetGeneralLedgerPendingEntry(transactionalDocument, sequenceHelper, accountingLineCopy, explicitEntryDeepCopy, offsetEntry);

        // increment the sequence appropriately
        sequenceHelper.increment();

        // now generate the AVRC DI entry
        // pass in the explicit deep copy
        success &= processAuxiliaryVoucherRecodeDistributionOfIncomeAndExpenseGeneralLedgerPendingEntry(transactionalDocument, sequenceHelper, explicitEntryDeepCopy);

        return success;
    }

    /**
     * This method handles generating or not generating the appropriate offsets if the AV type is accrual or adjustment.
     * 
     * @param transactionalDocument
     * @param sequenceHelper
     * @param accountingLineCopy
     * @param explicitEntry
     * @param offsetEntry
     * @return boolean
     */
    private boolean processOffsetGeneralLedgerPendingEntryForAccrualsAndAdjustments(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean success = true;
        
        if(isDocumentForMultipleAccounts(transactionalDocument)) {
            success &= super.processOffsetGeneralLedgerPendingEntry(transactionalDocument, sequenceHelper, accountingLineCopy, explicitEntry, offsetEntry);
        } 
        else {
            sequenceHelper.decrement(); // the parent already increments; b/c it assumes that all documents have offset entries all of the time
        }
        
        return success;
    }
    
    /**
     * This method is responsible for iterating through all of the accounting lines in the document (source only) and
     * checking to see if they are all for the same account or not.  It recognized the first account element as the base, 
     * and then it iterates through the rest.  If it comes across one that doesn't match, then we know it's 
     * for multiple accounts.
     *  
     * @param transactionalDocument
     * @return boolean
     */
    private boolean isDocumentForMultipleAccounts(TransactionalDocument transactionalDocument) {
        String baseAccountNumber = "";
        
        int index = 0;
        List<AccountingLine> lines = transactionalDocument.getSourceAccountingLines();
        for (AccountingLine line : lines) {
            if(index == 0) {
                baseAccountNumber = line.getAccountNumber();
            } else if(!baseAccountNumber.equals(line.getAccountNumber())) {
                return true;
            }
            index++;
        }
        
        return false;
     }

    /**
     * Offset entries are created for recodes (AVRC) always, so this method is one of 2 offsets that get created for an AVRC.  Its document type is 
     * set to DI.  This uses the explicit entry as its model.  In addition, an offset is generated for accruals (AVAE) and adjustments (AVAD), but only 
     * if the document contains accounting lines for more than one account. 
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#customizeOffsetGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected boolean customizeOffsetGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        AuxiliaryVoucherDocument auxDoc = (AuxiliaryVoucherDocument) transactionalDocument;
        
        // set the document type to that of a Distrib. Of Income and Expense if it's a recode
        if(auxDoc.isRecodeType()) {
            offsetEntry.setFinancialDocumentTypeCode(SpringServiceLocator.getDocumentTypeService().getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class));
        }

        // now set the offset entry to the specific offset object code for the AV generated offset fund balance; only if it's an accrual or adjustment
        if(auxDoc.isAccrualType() || auxDoc.isAdjustmentType()) {
            String glpeOffsetObjectCode = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(getDefaultSecurityGrouping(), getGeneralLedgerPendingEntryOffsetObjectCode());
            offsetEntry.setFinancialObjectCode(glpeOffsetObjectCode);
        }

        // set the reversal date to null
        offsetEntry.setFinancialDocumentReversalDate(null);

        // set the posting period and year to current
        offsetEntry.setUniversityFiscalPeriodCode(null); // GL will assign to current automatically
        offsetEntry.setUniversityFiscalYear(SpringServiceLocator.getDateTimeService().getCurrentFiscalYear());

        // although they are offsets, we need to set the offset indicator to false
        offsetEntry.setTransactionEntryOffsetIndicator(false);

        return true;
    }

    /**
     * This method creates an AV recode specifc GLPE with a document type of DI.  The sequence is managed outside of this method.  It uses the explicit 
     * entry as its model and then tweaks values appropriately.
     * 
     * @param transactionalDocument
     * @param sequenceHelper
     * @param explicitEntry
     * @return
     */
    private boolean processAuxiliaryVoucherRecodeDistributionOfIncomeAndExpenseGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry explicitEntry) {
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

        // set the posting period and year to current
        recodeGlpe.setUniversityFiscalPeriodCode(null); // GL will assign to current automatically
        recodeGlpe.setUniversityFiscalYear(SpringServiceLocator.getDateTimeService().getCurrentFiscalYear());

        // although this is an offsets, we need to set the offset indicator to false
        recodeGlpe.setTransactionEntryOffsetIndicator(false);

        // add the new recode offset entry to the document now
        transactionalDocument.getGeneralLedgerPendingEntries().add(recodeGlpe);

        return true;
    }

    /**
     * This method returns the appropriate voucher type code based on the document passed in.
     * 
     * @param document
     * @return voucher type code (AVAE, AVAD, AVRC)
     */
    protected String getDocumentTypeCodeBasedOnVoucherTypeCode(TransactionalDocument document) {
        AuxiliaryVoucherDocument auxVoucher = (AuxiliaryVoucherDocument) document;

        String voucherType = auxVoucher.getTypeCode();

        if (StringUtils.isBlank(voucherType)) {
            throw new IllegalStateException("The accrual type for Auxiliary Voucher " + document.getDocumentHeader().getFinancialDocumentNumber() + " is NULL.");
        }

        return voucherType;
    }

    /**
     * This method examines the accounting line passed in and returns the appropriate object type code.  This rule 
     * converts specific objects types from an object code on an accounting line to more general values.  This is 
     * specific to the AV document.
     * 
     * @param line
     * @return object type for an AuxiliaryVoucher document
     */
    protected String getObjectTypeCode(AccountingLine line) {
        String objectTypeCode = line.getObjectCode().getFinancialObjectTypeCode();

        if (OBJECT_TYPE_CODE.EXPENSE_EXPENDITURE.equals(objectTypeCode) || OBJECT_TYPE_CODE.EXPENDITURE_NOT_EXPENSE.equals(objectTypeCode)) {
            objectTypeCode = OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE;
        }
        else if (OBJECT_TYPE_CODE.INCOME_CASH.equals(objectTypeCode) || OBJECT_TYPE_CODE.EXPENDITURE_NOT_EXPENSE.equals(objectTypeCode)) {
            objectTypeCode = OBJECT_TYPE_CODE.INCOME_NOT_CASH;
        }

        return objectTypeCode;
    }

    /**
     * This method examines the explicit entry's object type and returns the appropriate object type code.  This is 
     * specific to AV recodes (AVRCs).
     * 
     * @param explicitEntry
     * @return object type code
     */
    protected String getObjectTypeCodeForRecodeDistributionOfIncomeAndExpenseEntry(GeneralLedgerPendingEntry explicitEntry) {
        String objectTypeCode = explicitEntry.getFinancialObjectTypeCode();

        if (OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE.equals(objectTypeCode)) {
            objectTypeCode = OBJECT_TYPE_CODE.EXPENSE_EXPENDITURE;
        }
        else if (OBJECT_TYPE_CODE.INCOME_NOT_CASH.equals(objectTypeCode)) {
            objectTypeCode = OBJECT_TYPE_CODE.INCOME_CASH;
        }

        return objectTypeCode;
    }

    /**
     * Overrides to call super and then validate that the 
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        boolean valid = super.processCustomAddAccountingLineBusinessRules(document, accountingLine);

        if (valid) {
            buildAccountingLineObjectType(accountingLine);
            valid &= isValidDocWithSubAndLevel(document, accountingLine);
        }

        return valid;
    }

    /**
     * @see TransactionalDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine)
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
        boolean valid = super.processCustomRouteDocumentBusinessRules(document);

        // make sure that a single chart is used for all accounting lines in the document
        if(valid) {
            valid = isSingleChartUsed((TransactionalDocument) document);
        }
        
        // make sure that a single sub fund group is used for all accounting lines in the document
        if (valid) {
            valid = isSingleSubFundGroupUsed((TransactionalDocument) document);
        }
        
        // make sure that a reversal date is entered for accruals
        if (valid) {
            valid = isValidReversalDate((AuxiliaryVoucherDocument) document);
        }

        return valid;
    }
    
    /**
     * Iterates <code>{@link AccountingLine}</code> instances in a given <code>{@link TransactionalDocument}</code> instance and
     * compares them to see if they are all in the same Chart.
     * 
     * @param document
     * @return boolean
     */
    protected boolean isSingleChartUsed(TransactionalDocument document) {
        boolean valid = true;

        String baseChartCode = null;
        int index = 0;
        
        List<AccountingLine> lines = document.getSourceAccountingLines(); 
        for (AccountingLine line : lines) {
            if(index == 0) {
                baseChartCode = line.getChartOfAccountsCode();
            } else {
                String currentChartCode = line.getChartOfAccountsCode();
                if(!currentChartCode.equals(baseChartCode)) {
                    reportError(ACCOUNTING_LINE_ERRORS, KeyConstants.AuxiliaryVoucher.ERROR_DIFFERENT_CHARTS, new String[] {});
                    return false;
                }
            }
            index++;
        }
        return true;
    }

    /**
     * Iterates <code>{@link AccountingLine}</code> instances in a given <code>{@link TransactionalDocument}</code> instance and
     * compares them to see if they are all in the same Sub-Fund Group.
     * 
     * @param document
     * @return boolean
     */
    protected boolean isSingleSubFundGroupUsed(TransactionalDocument document) {
        boolean valid = true;

        String baseSubFundGroupCode = null;
        int index = 0;
        
        List<AccountingLine> lines = document.getSourceAccountingLines(); 
        for (AccountingLine line : lines) {
            if(index == 0) {
                baseSubFundGroupCode = line.getAccount().getSubFundGroupCode();
            } else {
                String currentSubFundGroup = line.getAccount().getSubFundGroupCode();
                if(!currentSubFundGroup.equals(baseSubFundGroupCode)) {
                    reportError(ACCOUNTING_LINE_ERRORS, KeyConstants.AuxiliaryVoucher.ERROR_DIFFERENT_SUB_FUND_GROUPS, new String[] {});
                    return false;
                }
            }
            index++;
        }
        return true;
    }
    
    /**
     * This method verifies that the user entered a reversal date, but only if it's an accrual.
     * @param document
     * @return boolean
     */
    protected boolean isValidReversalDate(AuxiliaryVoucherDocument document) {
        if(document.isAccrualType() && document.getReversalDate() == null) {
            reportError(PropertyConstants.REVERSAL_DATE, KeyConstants.AuxiliaryVoucher.ERROR_INVALID_ACCRUAL_REVERSAL_DATE, new String[] {});
            return false;
        }
        
        return true;
    }
    
    /**
     * Overrides the parent implemenation to sum all of the debit GLPEs up and sum all of the credit GLPEs up and then compare the totals to each other,
     * returning true if they are equal and false if they are not.  The difference is that we ignore any DI specific entries because while these are offsets, 
     * their offset indicators do not show this, so they would be counted in the balancing and we don't want that.
     * 
     * @param transactionalDocument
     * @return boolean
     */
    @Override
    protected boolean isDocumentBalanceValidConsideringDebitsAndCredits(TransactionalDocument transactionalDocument) {
        // generate GLPEs specifically here so that we can compare debits to credits
        if (!SpringServiceLocator.getGeneralLedgerPendingEntryService().generateGeneralLedgerPendingEntries(transactionalDocument)) {
            throw new ValidationException("general ledger GLPE generation failed");
        }
        
        // now loop through all of the GLPEs and calculate buckets for debits and credits
        KualiDecimal creditAmount = new KualiDecimal(0);
        KualiDecimal debitAmount = new KualiDecimal(0);
        Iterator i = transactionalDocument.getGeneralLedgerPendingEntries().iterator();
        while(i.hasNext()) {
            GeneralLedgerPendingEntry glpe = (GeneralLedgerPendingEntry) i.next();
            // make sure we are looking at only the explicit entries that aren't DI types
            if(!glpe.isTransactionEntryOffsetIndicator() && !glpe.getFinancialDocumentTypeCode().equals(SpringServiceLocator.getDocumentTypeService().getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class))) {
                if(GENERAL_LEDGER_PENDING_ENTRY_CODE.CREDIT.equals(glpe.getTransactionDebitCreditCode())) {
                    creditAmount = creditAmount.add(glpe.getTransactionLedgerEntryAmount());
                }
                else { // DEBIT
                    debitAmount = debitAmount.add(glpe.getTransactionLedgerEntryAmount());
                }
            }
        }
        boolean isValid = debitAmount.compareTo(creditAmount) == 0;

        if (!isValid) {
            GlobalVariables.getErrorMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_BALANCE_CONSIDERING_CREDIT_AND_DEBIT_AMOUNTS, new String[] { creditAmount.toString(), debitAmount.toString() });
        }

        return isValid;
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
    private boolean isValidDocWithSubAndLevel(TransactionalDocument document, AccountingLine accountingLine) {
        boolean retval = true;

        try {
            retval = succeedsRule(RESTRICTED_COMBINED_CODES, getMockCodeBaseInstance(ObjectType.class, accountingLine.getObjectCode().getFinancialObjectTypeCode()).toString());

            if (retval) {
                retval = succeedsRule(RESTRICTED_COMBINED_CODES, getMockCodeBaseInstance(ObjSubTyp.class, accountingLine.getObjectCode().getFinancialObjectSubTypeCode()).toString());
            }

            if (retval) {
                retval = succeedsRule(RESTRICTED_COMBINED_CODES, getMockObjLevelInstance(accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode()).toString());
            }
        }
        catch (Exception e) {
            retval = false;
        }

        if (!retval) {
            String errorObjects[] = { accountingLine.getObjectCode().getFinancialObjectCode(), accountingLine.getObjectCode().getFinancialObjectType().getCode(), accountingLine.getObjectType().getCode(), accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode() };
            GlobalVariables.getErrorMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE, errorObjects);
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
    protected boolean isValidPeriod(AuxiliaryVoucherDocument document, int period, int year) {
        //first we need to get the period itself to check these things
        AccountingPeriodService perService = SpringServiceLocator.getAccountingPeriodService();
        AccountingPeriod acctPeriod = perService.getByPeriod(new Integer(period).toString(), new Integer(year));

        //can't post into a closed period
        if (acctPeriod.getUniversityFiscalPeriodStatusCode().equalsIgnoreCase(ACCOUNTING_PERIOD_STATUS_CLOSED)) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED);
            return false;
        }

        //if current period is period 1 can't post back more than 3 open periods
        //grab the current period
        Timestamp ts = document.getDocumentHeader().getWorkflowDocument().getCreateDate();
        AccountingPeriod currPeriod = perService.getByDate(new Date(ts.getTime()));
        int currPeriodVal = new Integer(currPeriod.getUniversityFiscalPeriodCode()).intValue();
        if (currPeriodVal == 1) {
            if (period < 11) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_PERIOD_THREE_OPEN);
                return false;
            }
        }

        //can't post back more than 2 periods
        if (period < currPeriodVal) {
            if ((currPeriodVal - period) > 2) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                return false;
            }
        }
        else {
            //if currPeriodVal is less than period then that means it can only be
            //period 2 (see period 1 rule above)and period 13 as the only possible combination, 
            //if not then error out
            if (currPeriodVal != 2) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                return false;
            }
            else {
                if (period != 13) {
                    GlobalVariables.getErrorMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS);
                    return false;
                }
            }
        }

        //check for specific posting issues
        if (document.isRecodeType()) {
            //can't post into a previous fiscal year
            int currFiscalYear = currPeriod.getUniversityFiscalYear().intValue();
            if (!(currFiscalYear < year)) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC);
                return false;
            }
            //check the posting period, throw out if period 13
            if (period > 12) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC);
                return false;
            }
            else if (period < 1) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERRORS, ERROR_CUSTOM, "You have entered an incorrect posting period, it must be a number between 1 and 13.");
                return false;
            }
        }
        return true;
    }

    /**
     * Overrides to perform the universal rule in the super class in addition 
     * to Auxiliary Voucher specific rules. This method leverages the 
     * APC for checking restricted object sub type values.
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
            GlobalVariables.getErrorMap().putError(FINANCIAL_OBJECT_CODE, ERROR_DOCUMENT_AUXILIARY_VOUCHER_INVALID_OBJECT_SUB_TYPE_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectSubTypeCode() });
        }

        return valid;
    }

    /**
     * Determines if period code used in primary key of <code>{@link AccountingPeriod}</code> 
     * is allowed.
     *
     * @param accountingPeriod
     * @return boolean
     */
    protected boolean isPeriodCodeAllowed(AccountingPeriod accountingPeriod) {
        boolean valid = true;

        valid &= succeedsRule(RESTRICTED_PERIOD_CODES, accountingPeriod.getUniversityFiscalPeriodCode());
        if (!valid) {
            GlobalVariables.getErrorMap().putError(ACCOUNTING_PERIOD_STATUS_CODE_FIELD, ERROR_CUSTOM, "You have entered an incorrect posting period, it must be a number between 1 and 13.");
        }

        return valid;
    }

    /**
     * Generic factory method for creating instances of
     * <code>{@link KualiCodeBase}</code> like an <code>{@link ObjectType}</code> 
     * instance or a <code>{@link ObjSubTyp}</code> instance.<br/>
     * 
     * <p>The mock object method is needed to validate using APC. The parameter uses non-specific 
     * <code>{@link KualiCodeBase}</code> codes, so things like chart code are unimportant.</p>
     * 
     * <p>This method uses reflections, so a <code>{@link ClassNotFoundException}</code>,
     * <code>{@link InstantiationException}</code>, <code>{@link IllegalAccessException}</code> 
     * may be thrown</p>
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
     * Factory method for creating instances of <code>{@link ObjLevel}</code>. This 
     * method is more specific than <code>{@link #getMockCodeBaseInstance(Class, String)}</code> 
     * because it is aimed specifically towards <code>{@link ObjLevel}</code> which is
     * not part of the <code>{@link KualiCodeBase}</code> class hieraarchy.<br/>
     * 
     * <p>The mock object method is needed to validate using APC. The parameter uses non-specific 
     * <code>{@link ObjLevel}</code> codes, so things like chart code are unimportant.</p>
     *
     * @param code
     * @return ObjLevel
     */
    protected ObjLevel getMockObjLevelInstance(String code) {
        ObjLevel retval = new ObjLevel();

        retval.setFinancialObjectLevelCode(code);

        return retval;
    }
    
    /**
     * Implements the specific sufficient funds checking for the Auxiliary Voucher document.  
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processSourceAccountingLineSufficientFundsCheckingPreparation(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.SourceAccountingLine)
     */
    @Override
    protected SufficientFundsItem processSourceAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument transactionalDocument, SourceAccountingLine sourceAccountingLine) {
        String chartOfAccountsCode = sourceAccountingLine.getChartOfAccountsCode();
        String accountNumber = sourceAccountingLine.getAccountNumber();
        String accountSufficientFundsCode = sourceAccountingLine.getAccount().getAccountSufficientFundsCode();
        String financialObjectCode = sourceAccountingLine.getFinancialObjectCode();
        String financialObjectLevelCode = sourceAccountingLine.getObjectCode().getFinancialObjectLevelCode();
        Integer fiscalYear = sourceAccountingLine.getPostingYear();
        String financialObjectTypeCode = sourceAccountingLine.getObjectTypeCode();
        KualiDecimal lineAmount = sourceAccountingLine.getAmount();
        String offsetDebitCreditCode = null;
        // fi_dica:lp_proc_grant_ln.36-2...62-2
        // fi_dica:lp_proc_rcpt_ln.36-2...69-2
        if (isDebit(transactionalDocument, sourceAccountingLine)) {
            offsetDebitCreditCode = Constants.GL_CREDIT_CODE;
        }
        else {
            offsetDebitCreditCode = Constants.GL_DEBIT_CODE;
        }
        lineAmount = lineAmount.abs();

        String sufficientFundsObjectCode = SpringServiceLocator.getSufficientFundsService().getSufficientFundsObjectCode(chartOfAccountsCode, financialObjectCode, accountSufficientFundsCode, financialObjectLevelCode);
        SufficientFundsItem item = buildSufficentFundsItem(accountNumber, accountSufficientFundsCode, lineAmount, chartOfAccountsCode, sufficientFundsObjectCode, offsetDebitCreditCode, financialObjectCode, financialObjectLevelCode, fiscalYear, financialObjectTypeCode);
        return item;
    }


    /**
     * Auxiliary Voucher is one sided and should only have SourceAccountingLines.  This overridden method just throws an IllegalStateException b/c it 
     * should never have been called.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processTargetAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument,
     *      org.kuali.core.bo.TargetAccountingLine)
     */
    @Override
    protected SufficientFundsItem processTargetAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument transactionalDocument, TargetAccountingLine targetAccountingLine) {
        if (targetAccountingLine != null) {
            throw new IllegalArgumentException("AV document doesn't have target accounting lines. This method should have never been entered");
        }
        return null;
    }
}