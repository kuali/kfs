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

import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.KualiDecimal;

/**
 * Interface for voucher type documents that require debit/credit support
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public interface VoucherDocument extends TransactionalDocument {

    /**
     * This method retrieves the reversal date associated with this document.
     * 
     * @return Timestamp
     */
    public Timestamp getReversalDate();

    /**
     * This method sets the reversal date associated with this document.
     * 
     * @param reversalDate
     */
    public void setReversalDate(Timestamp reversalDate);

    /**
     * This method calculates the debit total for a voucher document keying off of the debit/debit code, only summing the accounting
     * lines with a debitDebitCode that matched the debit constant, and returns the results.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getDebitTotal();

    /**
     * This method calculates the credit total for a voucher document keying off of the debit/credit code, only summing the
     * accounting lines with a debitCreditCode that matched the debit constant, and returns the results.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getCreditTotal();

    /**
     * This method determines the "total" for the voucher document. If the selected balance type is an offset generation, then the
     * total is calculated by subtracting the debit accounting lines from the credit accounting lines. Otherwise, the total is just
     * the sum of all accounting line amounts.
     * 
     * @return KualiDecimal the total of the voucher document.
     */
    public KualiDecimal getTotal();
}