/*
 * Copyright 2006 The Kuali Foundation.
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

import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.KualiDecimal;

/**
 * Interface for voucher type documents that require debit/credit support
 * 
 * 
 */
public interface VoucherDocument extends TransactionalDocument {

    /**
     * This method retrieves the reversal date associated with this document.
     * 
     * @return java.sql.Date
     */
    public java.sql.Date getReversalDate();

    /**
     * This method sets the reversal date associated with this document.
     * 
     * @param reversalDate
     */
    public void setReversalDate(java.sql.Date reversalDate);

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