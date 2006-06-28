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

import java.sql.Timestamp;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.bo.AccountingLineParser;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.financial.bo.JournalVoucherAccountingLineParser;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This is the business object that represents the JournalVoucherDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow and contains a single group of accounting lines. The Journal
 * Voucher is unique in that we only make use of one accounting line list: the source accounting lines seeing as a JV only records
 * accounting lines as debits or credits.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class JournalVoucherDocument extends TransactionalDocumentBase implements VoucherDocument {
    // document specific attributes
    private String balanceTypeCode; // balanceType key
    private BalanceTyp balanceType;
    private Timestamp reversalDate;

    /**
     * Constructs a JournalVoucherDocument instance.
     */
    public JournalVoucherDocument() {
        super();
        this.balanceType = new BalanceTyp();
    }

    /**
     * This method retrieves the balance typ associated with this document.
     * 
     * @return BalanceTyp
     */
    public BalanceTyp getBalanceType() {
        return balanceType;
    }

    /**
     * This method sets the balance type associated with this document.
     * 
     * @param balanceType
     * @deprecated
     */
    @Deprecated
    public void setBalanceType(BalanceTyp balanceType) {
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
     * @return Timestamp
     */
    public Timestamp getReversalDate() {
        return reversalDate;
    }

    /**
     * This method sets the reversal date associated with this document.
     * 
     * @param reversalDate
     */
    public void setReversalDate(Timestamp reversalDate) {
        this.reversalDate = reversalDate;
    }

    /**
     * Overrides the base implementation to return an empty string.
     * 
     * @return String
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.EMPTY_STRING;
    }

    /**
     * Overrides the base implementation to return an empty string.
     * 
     * @return String
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.EMPTY_STRING;
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
     * This method determines the "total" for the JV document. If the selected balance type is an offset generation, then the total
     * is calculated by subtracting the debit accounting lines from the credit accounting lines. Otherwise, the total is just the
     * sum of all accounting line amounts.
     * 
     * @return KualiDecimal the total of the JV document.
     */
    public KualiDecimal getTotal() {

        KualiDecimal total = new KualiDecimal(0);
        AccountingLineBase al = null;

        this.refreshReferenceObject("balanceType");

        if (this.balanceType.isFinancialOffsetGenerationIndicator()) { // credits and debits mode
            total = getCreditTotal().subtract(getDebitTotal());
        }
        else { // single amount mode
            Iterator iter = sourceAccountingLines.iterator();
            while (iter.hasNext()) {
                al = (AccountingLineBase) iter.next();
                total = total.add(al.getAmount());
            }
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
     * Overrides to call super, and then makes sure this is an error correction. If it is an error correction, it calls the JV
     * specific error correction helper method.
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#performConversion(int)
     */
    @Override
    protected void performConversion(int operation) throws WorkflowException {
        super.performConversion(operation);

        // process special for error corrections
        if (ERROR_CORRECTING == operation) {
            processJournalVoucherErrorCorrections();
        }
    }

    /**
     * This method checks to make sure that the JV that we are dealing with was one that was created in debit/credit mode, not
     * single amount entry mode. If this is a debit/credit JV, then iterate over each source line and flip the sign on the amount to
     * nullify the super's effect, then flip the debit/credit code b/c an error corrected JV flips the debit/credit code.
     */
    private void processJournalVoucherErrorCorrections() {
        Iterator i = getSourceAccountingLines().iterator();

        this.refreshReferenceObject(PropertyConstants.BALANCE_TYPE);

        if (this.getBalanceType().isFinancialOffsetGenerationIndicator()) { // make sure this is not a single amount entered JV
            int index = 0;
            while (i.hasNext()) {
                SourceAccountingLine sLine = (SourceAccountingLine) i.next();

                String debitCreditCode = sLine.getDebitCreditCode();

                if (StringUtils.isNotBlank(debitCreditCode)) {
                    // negate the amount to to nullify the effects of the super, b/c super flipped it the first time through
                    sLine.setAmount(sLine.getAmount().negated()); // offsets the effect the super

                    // now just flip the debit/credit code
                    if (TransactionalDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.DEBIT.equals(debitCreditCode)) {
                        sLine.setDebitCreditCode(TransactionalDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.CREDIT);
                    }
                    else if (TransactionalDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.CREDIT.equals(debitCreditCode)) {
                        sLine.setDebitCreditCode(TransactionalDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.DEBIT);
                    }
                    else {
                        throw new IllegalStateException("SourceAccountingLine at index " + index + " does not have a debit/credit " + "code associated with it.  This should never have occured. Please contact your system administrator.");

                    }
                    index++;
                }
            }
        }
    }
}