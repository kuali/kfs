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
package org.kuali.module.financial.document;

import static org.kuali.Constants.EMPTY_STRING;
import static org.kuali.Constants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE;
import static org.kuali.Constants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE;
import static org.kuali.Constants.AuxiliaryVoucher.RECODE_DOC_TYPE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.bo.AccountingLineParser;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.AuxiliaryVoucherAccountingLineParser;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.util.SufficientFundsItem;

/**
 * This is the business object that represents the AuxiliaryVoucherDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines: Expense
 * and target. Expense is the expense and target is the income lines.
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class AuxiliaryVoucherDocument extends TransactionalDocumentBase implements VoucherDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AuxiliaryVoucherDocument.class);

    private String typeCode = ADJUSTMENT_DOC_TYPE;
    private java.sql.Date reversalDate;

    
    @Override
    public boolean documentPerformsSufficientFundsCheck() {
        return false;
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
}
