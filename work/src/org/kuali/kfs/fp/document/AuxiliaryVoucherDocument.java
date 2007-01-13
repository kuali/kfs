/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import static org.kuali.Constants.EMPTY_STRING;
import static org.kuali.Constants.GL_CREDIT_CODE;
import static org.kuali.Constants.GL_DEBIT_CODE;
import static org.kuali.Constants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE;
import static org.kuali.Constants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE;
import static org.kuali.Constants.AuxiliaryVoucher.RECODE_DOC_TYPE;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.bo.AccountingLineParser;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.AuxiliaryVoucherAccountingLineParser;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This is the business object that represents the AuxiliaryVoucherDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines: Expense
 * and target. Expense is the expense and target is the income lines.
 * 
 * 
 */
public class AuxiliaryVoucherDocument extends TransactionalDocumentBase implements VoucherDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AuxiliaryVoucherDocument.class);

    private String typeCode = ADJUSTMENT_DOC_TYPE;
    private java.sql.Date reversalDate;

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#documentPerformsSufficientFundsCheck()
     */
    @Override
    public boolean documentPerformsSufficientFundsCheck() {
        if (isRecodeType()) {
            return super.documentPerformsSufficientFundsCheck();
        } else {
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
        KualiDecimal debitTotal = new KualiDecimal(0);
        AccountingLineBase al = null;
        Iterator iter = sourceAccountingLines.iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();
            if (StringUtils.isNotBlank(al.getDebitCreditCode()) && al.getDebitCreditCode().equals(Constants.GL_DEBIT_CODE)) {
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
        KualiDecimal creditTotal = new KualiDecimal(0);
        AccountingLineBase al = null;
        Iterator iter = sourceAccountingLines.iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();
            if (StringUtils.isNotBlank(al.getDebitCreditCode()) && al.getDebitCreditCode().equals(Constants.GL_CREDIT_CODE)) {
                creditTotal = creditTotal.add(al.getAmount());
            }
        }
        return creditTotal;
    }

    /**
     * This method calculates the difference between the credit and debit total.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getTotal() {
        KualiDecimal total = new KualiDecimal(0);

        total = getCreditTotal().subtract(getDebitTotal());

        return total;
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
        if (isAccrualType() || isRecodeType()) {
            java.sql.Date today = SpringServiceLocator.getDateTimeService().getCurrentSqlDateMidnight();
            if (getReversalDate().before(today)) {
                // set the reversal date on the document
                setReversalDate(today);

                // set the reversal date on each GLPE for the document too
                List<GeneralLedgerPendingEntry> glpes = getGeneralLedgerPendingEntries();
                for (GeneralLedgerPendingEntry entry : glpes) {
                    entry.setFinancialDocumentReversalDate(getReversalDate());
                }
            }
        }
    }

    /**
     * Overrides the base implementation to return "From".
     * 
     * @see org.kuali.core.document.TransactionalDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return EMPTY_STRING;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new AuxiliaryVoucherAccountingLineParser();
    }

    /**
     * Checks for a reason why this document should not be copied or error corrected. This is overriden to remove posting year check
     * per KULEDOCS-1543.
     * 
     * @param actionGerund describes the action, "copying" or "error-correction"
     * @param ddAllows whether the DataDictionary allows this kind of copying for this document
     * @return a reason not to copy this document, or null if there is no reason
     */
    @Override
    protected String getNullOrReasonNotToCopy(String actionGerund, boolean ddAllows) {

        if (!ddAllows) {
            return this.getClass().getName() + " does not support document-level " + actionGerund;
        }

        // posting year not checked per KULEDOCS-1543.

        return null; // no reason not to copy
    }
    /**
     * Overrides to call super, and then makes sure this is an error correction. If it is an error correction, it calls the AV
     * specific error correction helper method.
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#performConversion(int)
     */
    @Override
    protected void performConversion(int operation) throws WorkflowException {
        super.performConversion(operation);

        // process special for error corrections
        if (ERROR_CORRECTING == operation) {
            processAuxiliaryVoucherErrorCorrections();
        }
    }

    /**
     * KULEDOCS-1700
     * This method iterates over each source line and flip the sign on the amount to
     * nullify the super's effect, then flip the debit/credit code b/c an error corrected AV flips the debit/credit code.
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
}
