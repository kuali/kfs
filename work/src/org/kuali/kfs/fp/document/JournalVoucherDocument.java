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

import static org.kuali.kfs.sys.KFSConstants.EMPTY_STRING;
import static org.kuali.kfs.sys.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.sys.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.BALANCE_TYPE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.fp.businessobject.JournalVoucherAccountingLineParser;
import org.kuali.kfs.fp.businessobject.VoucherSourceAccountingLine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Copyable;

/**
 * This is the business object that represents the JournalVoucherDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow and contains a single group of accounting lines. The Journal
 * Voucher is unique in that we only make use of one accounting line list: the source accounting lines seeing as a JV only records
 * accounting lines as debits or credits.
 */
public class JournalVoucherDocument extends AccountingDocumentBase implements VoucherDocument, Copyable, Correctable, AmountTotaling {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JournalVoucherDocument.class);

    // document specific attributes
    protected String balanceTypeCode; // balanceType key
    protected BalanceType balanceType;
    protected java.sql.Date reversalDate;

    /**
     * Constructs a JournalVoucherDocument instance.
     */
    public JournalVoucherDocument() {
        super();
        this.balanceType = new BalanceType();
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#checkSufficientFunds()
     */
    @Override
    public List<SufficientFundsItem> checkSufficientFunds() {
        LOG.debug("checkSufficientFunds() started");

        // This document does not do sufficient funds checking
        return new ArrayList<SufficientFundsItem>();
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
        return VoucherSourceAccountingLine.class;
    }

    /**
     * This method retrieves the balance typ associated with this document.
     * 
     * @return BalanceTyp
     */
    public BalanceType getBalanceType() {
        return balanceType;
    }

    /**
     * This method sets the balance type associated with this document.
     * 
     * @param balanceType
     * @deprecated
     */
    @Deprecated
    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * Gets the balanceTypeCode attribute.
     * 
     * @return Returns the balanceTypeCode.
     */
    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }

    /**
     * Sets the balanceTypeCode attribute value.
     * 
     * @param balanceTypeCode The balanceTypeCode to set.
     */
    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }

    /**
     * This method retrieves the reversal date associated with this document.
     * 
     * @return java.sql.Date
     */
    public java.sql.Date getReversalDate() {
        return reversalDate;
    }

    /**
     * This method sets the reversal date associated with this document.
     * 
     * @param reversalDate
     */
    public void setReversalDate(java.sql.Date reversalDate) {
        this.reversalDate = reversalDate;
    }

    /**
     * Overrides the base implementation to return an empty string.
     * 
     * @return String
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return EMPTY_STRING;
    }

    /**
     * Overrides the base implementation to return an empty string.
     * 
     * @return String
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return EMPTY_STRING;
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
            if (StringUtils.isNotBlank(al.getDebitCreditCode()) && al.getDebitCreditCode().equals(GL_DEBIT_CODE)) {
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
            if (StringUtils.isNotBlank(al.getDebitCreditCode()) && al.getDebitCreditCode().equals(GL_CREDIT_CODE)) {
                creditTotal = creditTotal.add(al.getAmount());
            }
        }
        return creditTotal;
    }

    /**
     * This method determines the "total" for the JV document. If the selected balance type is an offset generation, then the method
     * returns the total debits amount when it is greater than the total credit amount. otherwise, it returns total credit amount.
     * When selected balance type is not an offset generation, the method returns the sum of all accounting line debit amounts.
     * 
     * @return KualiDecimal the total of the JV document.
     */
    public KualiDecimal getTotalDollarAmount() {

        KualiDecimal total = KualiDecimal.ZERO;

        this.refreshReferenceObject("balanceType");

        if (this.balanceType.isFinancialOffsetGenerationIndicator()) {
            if (getCreditTotal().isGreaterThan(getDebitTotal())) {
                total = getCreditTotal();
            }
            else {
                total = getDebitTotal();
            }
        }
        else {
            total = getDebitTotal();
        }
        return total;
    }

    /**
     * Used to get the appropriate <code>{@link AccountingLineParser}</code> for the <code>Document</code>
     * 
     * @return AccountingLineParser
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new JournalVoucherAccountingLineParser(getBalanceTypeCode());
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException {
        super.toErrorCorrection();
        processJournalVoucherErrorCorrections();
    }

    /**
     * This method checks to make sure that the JV that we are dealing with was one that was created in debit/credit mode, not
     * single amount entry mode. If this is a debit/credit JV, then iterate over each source line and flip the sign on the amount to
     * nullify the super's effect, then flip the debit/credit code b/c an error corrected JV flips the debit/credit code.
     */
    protected void processJournalVoucherErrorCorrections() {
        Iterator i = getSourceAccountingLines().iterator();

        this.refreshReferenceObject(BALANCE_TYPE);

        if (this.getBalanceType().isFinancialOffsetGenerationIndicator()) { // make sure this is not a single amount entered JV
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
    }
    
    /**
     * The following are credits (return false)
     * <ol>
     * <li> (debitCreditCode isNotBlank) && debitCreditCode != 'D'
     * </ol>
     * 
     * The following are debits (return true)
     * <ol>
     * <li> debitCreditCode == 'D'
     * <li> debitCreditCode isBlank
     * </ol>
     * 
     * @param financialDocument The document which contains the accounting line being analyzed.
     * @param accountingLine The accounting line which will be analyzed to determine if it is a debit line.
     * @return True if the accounting line provided is a debit accounting line, false otherwise.
     * @throws IllegalStateException Thrown by method IsDebitUtiles.isDebitCode()
     * 
     * @see org.kuali.rice.krad.rule.AccountingLineRule#isDebit(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine)
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase.IsDebitUtils#isDebitCode(String)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) throws IllegalStateException {
        AccountingLine accountingLine = (AccountingLine)postable;
        String debitCreditCode = accountingLine.getDebitCreditCode();

        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        boolean isDebit = StringUtils.isBlank(debitCreditCode) || isDebitUtils.isDebitCode(debitCreditCode);

        return isDebit;
    }
    
    /**
     * This method sets attributes on the explicitly general ledger pending entry specific to JournalVoucher documents.
     * This includes setting the accounting period code and year (as selected by the user, the object type code,
     * the balance type code, the debit/credit code, the encumbrance update code and the reversal date.
     * 
     * @param financialDocument The document which contains the general ledger pending entry being modified.
     * @param accountingLine The accounting line the explicit entry was generated from.
     * @param explicitEntry The explicit entry being updated.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        AccountingLine accountingLine = (AccountingLine)postable;

        // set the appropriate accounting period values according to the values chosen by the user
        explicitEntry.setUniversityFiscalPeriodCode(getPostingPeriodCode());
        explicitEntry.setUniversityFiscalYear(getPostingYear());

        // set the object type code directly from what was entered in the interface
        explicitEntry.setFinancialObjectTypeCode(accountingLine.getObjectTypeCode());

        // set the balance type code directly from what was entered in the interface
        explicitEntry.setFinancialBalanceTypeCode(accountingLine.getBalanceTypeCode());

        // set the debit/credit code appropriately
        refreshReferenceObject(BALANCE_TYPE);
        if (getBalanceType().isFinancialOffsetGenerationIndicator()) {
            explicitEntry.setTransactionDebitCreditCode(StringUtils.isNotBlank(accountingLine.getDebitCreditCode()) ? accountingLine.getDebitCreditCode() : KFSConstants.BLANK_SPACE);
        }
        else {
            explicitEntry.setTransactionDebitCreditCode(KFSConstants.BLANK_SPACE);
        }

        // set the encumbrance update code
        explicitEntry.setTransactionEncumbranceUpdateCode(StringUtils.isNotBlank(accountingLine.getEncumbranceUpdateCode()) ? accountingLine.getEncumbranceUpdateCode() : KFSConstants.BLANK_SPACE);

        // set the reversal date to what what specified at the document level
        if (getReversalDate() != null) {
            explicitEntry.setFinancialDocumentReversalDate(getReversalDate());
        }
    }

    /**
     * A Journal Voucher document doesn't generate an offset entry at all, so this method overrides to do nothing more than return
     * true. This will be called by the parent's processGeneralLedgerPendingEntries method.
     * 
     * @param financialDocument The document the offset will be stored within.
     * @param sequenceHelper The sequence object to be modified.
     * @param accountingLineToCopy The accounting line the offset is generated for.
     * @param explicitEntry The explicit entry the offset will be generated for.
     * @param offsetEntry The offset entry to be processed.
     * @return This method always returns true.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processOffsetGeneralLedgerPendingEntry(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, org.kuali.rice.krad.bo.AccountingLine,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    public boolean processOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        sequenceHelper.decrement(); // the parent already increments; assuming that all documents have offset entries
        return true;
    }
    
    /**
     * 
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase#getGeneralLedgerPendingEntryAmountForAccountingLine(org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail accountingLine) {
        LOG.debug("getGeneralLedgerPendingEntryAmountForAccountingLine(AccountingLine) - start");
        KualiDecimal returnKualiDecimal;

        String budgetCodes = SpringContext.getBean(OptionsService.class).getOptions(accountingLine.getPostingYear()).getBudgetCheckingBalanceTypeCd();
        if (!this.balanceType.isFinancialOffsetGenerationIndicator()) {
            returnKualiDecimal = accountingLine.getAmount();
        }
        else {
            returnKualiDecimal = accountingLine.getAmount().abs();
        }
        LOG.debug("getGeneralLedgerPendingEntryAmountForAccountingLine(AccountingLine) - end");
        return returnKualiDecimal;
    }
}
