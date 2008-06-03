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
package org.kuali.module.financial.document;

import static org.kuali.kfs.KFSConstants.EMPTY_STRING;
import static org.kuali.kfs.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.kfs.KFSConstants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE;
import static org.kuali.kfs.KFSConstants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE;
import static org.kuali.kfs.KFSConstants.AuxiliaryVoucher.RECODE_DOC_TYPE;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.GENERAL_LEDGER_PENDING_ENTRY_OFFSET_CODE;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.Correctable;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineBase;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.kfs.service.DebitDeterminerService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.financial.bo.AuxiliaryVoucherAccountingLineParser;
import org.kuali.module.gl.service.SufficientFundsService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This is the business object that represents the AuxiliaryVoucherDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines: Expense
 * and target. Expense is the expense and target is the income lines.
 */
public class AuxiliaryVoucherDocument extends AccountingDocumentBase implements VoucherDocument, Copyable, Correctable, AmountTotaling {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AuxiliaryVoucherDocument.class);

    private String typeCode = ADJUSTMENT_DOC_TYPE;
    private java.sql.Date reversalDate;

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#documentPerformsSufficientFundsCheck()
     */
    @Override
    public boolean documentPerformsSufficientFundsCheck() {
        if (isRecodeType()) {
            return super.documentPerformsSufficientFundsCheck();
        }
        else {
            return false;
        }
    }

    /**
     * Initializes the array lists and some basic info.
     */
    public AuxiliaryVoucherDocument() {
        super();
    }

    /**
     * Read Accessor for Reversal Date
     * 
     * @return java.sql.Date
     */
    public java.sql.Date getReversalDate() {
        return reversalDate;
    }

    /**
     * Write Accessor for Reversal Date
     * 
     * @param reversalDate
     */
    public void setReversalDate(java.sql.Date reversalDate) {
        this.reversalDate = reversalDate;
    }

    /**
     * Read Accessor for Auxiliary Voucher Type
     * 
     * @return String
     */
    public String getTypeCode() {
        return typeCode;
    }

    /**
     * Write Accessor for Auxiliary Voucher Type
     * 
     * @param typeCode
     */
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    /**
     * A helper to test whether this document is an adjustment type AV.
     * 
     * @return boolean
     */
    public boolean isAdjustmentType() {
        return ADJUSTMENT_DOC_TYPE.equals(typeCode);
    }

    /**
     * A helper to test whether this document is an recode type AV.
     * 
     * @return boolean
     */
    public boolean isRecodeType() {
        return RECODE_DOC_TYPE.equals(typeCode);
    }

    /**
     * A helper to test whether this document is an accrual type AV.
     * 
     * @return boolean
     */
    public boolean isAccrualType() {
        return ACCRUAL_DOC_TYPE.equals(typeCode);
    }

    /**
     * This method calculates the debit total for a JV document keying off of the debit/debit code, only summing the accounting
     * lines with a debitDebitCode that matched the debit constant, and returns the results.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getDebitTotal() {
        KualiDecimal debitTotal = KualiDecimal.ZERO;
        AccountingLineBase al = null;
        Iterator iter = sourceAccountingLines.iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();
            if (StringUtils.isNotBlank(al.getDebitCreditCode()) && al.getDebitCreditCode().equals(KFSConstants.GL_DEBIT_CODE)) {
                debitTotal = debitTotal.add(al.getAmount());
            }
        }
        return debitTotal;
    }

    /**
     * This method calculates the credit total for a JV document keying off of the debit/credit code, only summing the accounting
     * lines with a debitCreditCode that matched the debit constant, and returns the results.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getCreditTotal() {
        KualiDecimal creditTotal = KualiDecimal.ZERO;
        AccountingLineBase al = null;
        Iterator iter = sourceAccountingLines.iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();
            if (StringUtils.isNotBlank(al.getDebitCreditCode()) && al.getDebitCreditCode().equals(KFSConstants.GL_CREDIT_CODE)) {
                creditTotal = creditTotal.add(al.getAmount());
            }
        }
        return creditTotal;
    }

    /**
     * Same as default implementation but uses debit / credit totals instead. Meaning it returns either credit or if 0, debit.
     * 
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTotalDollarAmount()
     * @return KualiDecimal
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return getCreditTotal().equals(KualiDecimal.ZERO) ? getDebitTotal() : getCreditTotal();
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();
        refreshReversalDate();
    }

    /**
     * Overrides to call super and then change the reversal date if the type is accrual and the date is greater than the set
     * reversal date.
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("In handleRouteStatusChange() for AV documents");
        super.handleRouteStatusChange();

        if (this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) { // only do this stuff if the document has been
            // processed and approved
            // update the reversal data accoringdingly
            updateReversalDate();
        }
    }

    /**
     * This method handles updating the reversal data on the document in addition to all of the GLPEs, but only for the accrual and
     * recode types.
     */
    private void updateReversalDate() {
        if (refreshReversalDate()) {
            // set the reversal date on each GLPE for the document too
            List<GeneralLedgerPendingEntry> glpes = getGeneralLedgerPendingEntries();
            for (GeneralLedgerPendingEntry entry : glpes) {
                entry.setFinancialDocumentReversalDate(getReversalDate());
            }
        }
    }
    
    /**
     * If the reversal date on this document is in need of refreshing, refreshes the reveral date.  THIS METHOD MAY CHANGE DOCUMENT STATE!
     * @return true if the reversal date ended up getting refreshed, false otherwise
     */
    private boolean refreshReversalDate() {
        boolean refreshed = false;
        if ((isAccrualType() || isRecodeType()) && getReversalDate() != null) {
            java.sql.Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
            if (getReversalDate().before(today)) {
                // set the reversal date on the document
                setReversalDate(today);
                refreshed = true;
            }
        }
        return refreshed;
    }

    /**
     * Overrides the base implementation to return "From".
     * 
     * @see org.kuali.kfs.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return EMPTY_STRING;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new AuxiliaryVoucherAccountingLineParser();
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException {
        super.toErrorCorrection();
        processAuxiliaryVoucherErrorCorrections();
    }

    /**
     * KULEDOCS-1700 This method iterates over each source line and flip the sign on the amount to nullify the super's effect, then
     * flip the debit/credit code b/c an error corrected AV flips the debit/credit code.
     */
    private void processAuxiliaryVoucherErrorCorrections() {
        Iterator i = getSourceAccountingLines().iterator();

        int index = 0;
        while (i.hasNext()) {
            SourceAccountingLine sLine = (SourceAccountingLine) i.next();

            String debitCreditCode = sLine.getDebitCreditCode();

            if (StringUtils.isNotBlank(debitCreditCode)) {
                // negate the amount to to nullify the effects of the super, b/c super flipped it the first time through
                sLine.setAmount(sLine.getAmount().negated()); // offsets the effect the super

                // now just flip the debit/credit code
                if (GL_DEBIT_CODE.equals(debitCreditCode)) {
                    sLine.setDebitCreditCode(GL_CREDIT_CODE);
                }
                else if (GL_CREDIT_CODE.equals(debitCreditCode)) {
                    sLine.setDebitCreditCode(GL_DEBIT_CODE);
                }
                else {
                    throw new IllegalStateException("SourceAccountingLine at index " + index + " does not have a debit/credit " + "code associated with it.  This should never have occured. Please contact your system administrator.");

                }
                index++;
            }
        }
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
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) throws IllegalStateException {
        String debitCreditCode = ((AccountingLine)postable).getDebitCreditCode();
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        if (StringUtils.isBlank(debitCreditCode)) {
            throw new IllegalStateException(isDebitUtils.getDebitCalculationIllegalStateExceptionMessage());
        }
        return isDebitUtils.isDebitCode(debitCreditCode);
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
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {

        java.sql.Date reversalDate = getReversalDate();
        if (reversalDate != null) {
            explicitEntry.setFinancialDocumentReversalDate(reversalDate);
        }
        else {
            explicitEntry.setFinancialDocumentReversalDate(null);
        }
        explicitEntry.setFinancialDocumentTypeCode(getTypeCode()); // make sure to use the accrual type as the document
        // type
        explicitEntry.setFinancialObjectTypeCode(getObjectTypeCode(postable));
        explicitEntry.setUniversityFiscalPeriodCode(getPostingPeriodCode()); // use chosen posting period code
        explicitEntry.setUniversityFiscalYear(getPostingYear()); // use chosen posting year
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
    @Override
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        // set the document type to that of a Distrib. Of Income and Expense if it's a recode
        if (isRecodeType()) {
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
        if (isAccrualType() || isAdjustmentType()) {
            String glpeOffsetObjectCode = SpringContext.getBean(ParameterService.class).getParameterValue(AuxiliaryVoucherDocument.class, getGeneralLedgerPendingEntryOffsetObjectCode());
            offsetEntry.setFinancialObjectCode(glpeOffsetObjectCode);

            // set the posting period
            offsetEntry.setUniversityFiscalPeriodCode(getPostingPeriodCode()); // use chosen posting period code
        }

        // set the reversal date to null
        offsetEntry.setFinancialDocumentReversalDate(null);

        // set the year to current
        offsetEntry.setUniversityFiscalYear(getPostingYear()); // use chosen posting year

        // although they are offsets, we need to set the offset indicator to false
        offsetEntry.setTransactionEntryOffsetIndicator(false);

        offsetEntry.refresh(); // may have changed foreign keys here; need accurate object code and account BOs at least
        offsetEntry.setAcctSufficientFundsFinObjCd(SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(offsetEntry.getFinancialObject(), offsetEntry.getAccount().getAccountSufficientFundsCode()));

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
    protected String getObjectTypeCode(GeneralLedgerPendingEntrySourceDetail line) {
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
     * Get from APC the offset object code that is used for the <code>{@link GeneralLedgerPendingEntry}</code>
     * 
     * @return String returns GLPE parameter name
     */
    protected String getGeneralLedgerPendingEntryOffsetObjectCode() {
        return GENERAL_LEDGER_PENDING_ENTRY_OFFSET_CODE;
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
    public boolean processOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        AccountingLine accountingLineCopy = (AccountingLine)glpeSourceDetail;
        boolean success = true;

        // do not generate an offset entry if this is a normal or adjustment AV type
        if (isAccrualType() || isAdjustmentType()) {
            success &= processOffsetGeneralLedgerPendingEntryForAccrualsAndAdjustments(sequenceHelper, accountingLineCopy, explicitEntry, offsetEntry);
        }
        else if (isRecodeType()) { // recodes generate offsets
            success &= processOffsetGeneralLedgerPendingEntryForRecodes(sequenceHelper, accountingLineCopy, explicitEntry, offsetEntry);
        }
        else {
            throw new IllegalStateException("Illegal auxiliary voucher type: " + getTypeCode());
        }
        return success;
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
    protected boolean processOffsetGeneralLedgerPendingEntryForRecodes(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        // the explicit entry has already been generated and added to the list, so to get the right offset, we have to set the value
        // of the document type code on the explicit
        // to the type code for a DI document so that it gets passed into the next call and we retrieve the right offset definition
        // since these offsets are
        // specific to Distrib. of Income and Expense documents - we need to do a deep copy though so we don't do this by reference
        GeneralLedgerPendingEntry explicitEntryDeepCopy = new GeneralLedgerPendingEntry(explicitEntry);
        explicitEntryDeepCopy.setFinancialDocumentTypeCode(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class));

        // set the posting period to current, because DI GLPEs for recodes should post to the current period
        java.sql.Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
        explicitEntryDeepCopy.setUniversityFiscalPeriodCode(SpringContext.getBean(AccountingPeriodService.class).getByDate(today).getUniversityFiscalPeriodCode()); // use

        // call the super to process an offset entry; see the customize method below for AVRC specific attribute values
        // pass in the explicit deep copy
        boolean success = super.processOffsetGeneralLedgerPendingEntry(sequenceHelper, accountingLineCopy, explicitEntryDeepCopy, offsetEntry);

        // increment the sequence appropriately
        sequenceHelper.increment();

        // now generate the AVRC DI entry
        // pass in the explicit deep copy
        processAuxiliaryVoucherRecodeDistributionOfIncomeAndExpenseGeneralLedgerPendingEntry( sequenceHelper, explicitEntryDeepCopy);
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
    protected boolean processOffsetGeneralLedgerPendingEntryForAccrualsAndAdjustments(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLineCopy, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean success = true;
        if (this.isDocumentForMultipleAccounts()) {
            success = super.processOffsetGeneralLedgerPendingEntry(sequenceHelper, accountingLineCopy, explicitEntry, offsetEntry);
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
    protected boolean isDocumentForMultipleAccounts() {
        String baseAccountNumber = "";

        int index = 0;
        List<AccountingLine> lines = this.getSourceAccountingLines();
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
     * This method creates an AV recode specific GLPE with a document type of DI. The sequence is managed outside of this method. It
     * uses the explicit entry as its model and then tweaks values appropriately.
     * 
     * @param financialDocument submitted accounting document
     * @param sequenceHelper helper class which will allows us to increment a reference without using an Integer
     * @param explicitEntry represents explicit entry
     * @return true if recode GLPE is added to the financial document successfully
     */
    protected void processAuxiliaryVoucherRecodeDistributionOfIncomeAndExpenseGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry explicitEntry) {
        // create a new instance based off of the explicit entry
        GeneralLedgerPendingEntry recodeGlpe = new GeneralLedgerPendingEntry(explicitEntry);

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
        addPendingEntry(recodeGlpe);
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
    
}
