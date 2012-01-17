/*
 * Copyright 2005 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.fp.document;

import static org.kuali.kfs.fp.document.validation.impl.AuxiliaryVoucherDocumentRuleConstants.AUXILIARY_VOUCHER_ACCOUNTING_PERIOD_GRACE_PERIOD;
import static org.kuali.kfs.fp.document.validation.impl.AuxiliaryVoucherDocumentRuleConstants.GENERAL_LEDGER_PENDING_ENTRY_OFFSET_CODE;
import static org.kuali.kfs.sys.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.sys.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.kfs.sys.KFSConstants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE;
import static org.kuali.kfs.sys.KFSConstants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE;
import static org.kuali.kfs.sys.KFSConstants.AuxiliaryVoucher.RECODE_DOC_TYPE;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.document.Copyable;

/**
 * This is the business object that represents the AuxiliaryVoucherDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines: Expense
 * and target. Expense is the expense and target is the income lines.
 */
public class AuxiliaryVoucherDocument extends AccountingDocumentBase implements VoucherDocument, Copyable, Correctable, AmountTotaling {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AuxiliaryVoucherDocument.class);

    protected String typeCode = ADJUSTMENT_DOC_TYPE;
    protected java.sql.Date reversalDate;
    
    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#documentPerformsSufficientFundsCheck()
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
        Iterator<?> iter = sourceAccountingLines.iterator();
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
        Iterator<?> iter = sourceAccountingLines.iterator();
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
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTotalDollarAmount()
     * @return KualiDecimal
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return getCreditTotal().equals(KualiDecimal.ZERO) ? getDebitTotal() : getCreditTotal();
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#toCopy()
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
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        LOG.debug("In doRouteStatusChange() for AV documents");
        super.doRouteStatusChange(statusChangeEvent);

        if (this.getDocumentHeader().getWorkflowDocument().isProcessed()) { // only do this stuff if the document has been
            // processed and approved
            // update the reversal data accoringdingly
            updateReversalDate();
        }
    }

    /**
     * This method handles updating the reversal data on the document in addition to all of the GLPEs, but only for the accrual and
     * recode types.
     */
    protected void updateReversalDate() {
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
    protected boolean refreshReversalDate() {
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
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#toErrorCorrection()
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
    protected void processAuxiliaryVoucherErrorCorrections() {
        Iterator<?> i = getSourceAccountingLines().iterator();
        
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
     * @see org.kuali.rice.krad.rule.AccountingLineRule#isDebit(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine)
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
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processExplicitGeneralLedgerPendingEntry(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, org.kuali.rice.krad.bo.AccountingLine,
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
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#customizeOffsetGeneralLedgerPendingEntry(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        // set the document type to that of a Distrib. Of Income and Expense if it's a recode
        if (isRecodeType()) {
            offsetEntry.setFinancialDocumentTypeCode(KFSConstants.FinancialDocumentTypeCodes.DISTRIBUTION_OF_INCOME_AND_EXPENSE);

            // set the posting period
            java.sql.Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
            offsetEntry.setUniversityFiscalPeriodCode(SpringContext.getBean(AccountingPeriodService.class).getByDate(today).getUniversityFiscalPeriodCode()); // use
        }

        // now set the offset entry to the specific offset object code for the AV generated offset fund balance; only if it's an
        // accrual or adjustment
        if (isAccrualType() || isAdjustmentType()) {
            String glpeOffsetObjectCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(this.getClass(), getGeneralLedgerPendingEntryOffsetObjectCode());
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

        //KFSMI-798 - refreshNonUpdatableReferences() used instead of refresh(), 
        //GeneralLedgerPendingEntry does not have any updatable references
        offsetEntry.refreshNonUpdateableReferences(); // may have changed foreign keys here; need accurate object code and account BOs at least
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
        SystemOptions options = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();
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
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processOffsetGeneralLedgerPendingEntry(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, org.kuali.rice.krad.bo.AccountingLine,
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
        explicitEntryDeepCopy.setFinancialDocumentTypeCode(KFSConstants.FinancialDocumentTypeCodes.DISTRIBUTION_OF_INCOME_AND_EXPENSE);

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
        recodeGlpe.setFinancialDocumentTypeCode(KFSConstants.FinancialDocumentTypeCodes.DISTRIBUTION_OF_INCOME_AND_EXPENSE);

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
        SystemOptions options = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();
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
     * This method checks if a given moment of time is within an accounting period, or its auxiliary voucher grace period.
     * 
     * @param today a date to check if it is within the period
     * @param periodToCheck the account period to check against
     * @return true if a given moment in time is within an accounting period or an auxiliary voucher grace period
     */
    public boolean calculateIfWithinGracePeriod(Date today, AccountingPeriod periodToCheck) {
        boolean result = false;
        final int todayAsComparableDate = comparableDateForm(today);
        final int periodClose = new Integer(comparableDateForm(periodToCheck.getUniversityFiscalPeriodEndDate()));
        final int periodBegin = comparableDateForm(calculateFirstDayOfMonth(periodToCheck.getUniversityFiscalPeriodEndDate()));
        final int gracePeriodClose = periodClose + new Integer(SpringContext.getBean(ParameterService.class).getParameterValueAsString(getClass(), AUXILIARY_VOUCHER_ACCOUNTING_PERIOD_GRACE_PERIOD)).intValue();
        return (todayAsComparableDate >= periodBegin && todayAsComparableDate <= gracePeriodClose);
    }
    
    /**
     * This method returns a date as an approximate count of days since the BCE epoch.
     * 
     * @param d the date to convert
     * @return an integer count of days, very approximate
     */
    public int comparableDateForm(Date d) {
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
    public Date calculateFirstDayOfMonth(Date d) {
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
    public boolean isEndOfPreviousFiscalYear(AccountingPeriod acctPeriod) {
        final UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        final Date firstDayOfCurrFiscalYear = new Date(universityDateService.getFirstDateOfFiscalYear(universityDateService.getCurrentFiscalYear()).getTime());
        final Date periodClose = acctPeriod.getUniversityFiscalPeriodEndDate();
        java.util.Calendar cal = new java.util.GregorianCalendar();
        cal.setTime(periodClose);
        cal.add(java.util.Calendar.DATE, 1);
        return (firstDayOfCurrFiscalYear.equals(new Date(cal.getTimeInMillis())));
    }
    
}
