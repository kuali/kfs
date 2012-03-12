/*
 * Copyright 2006 The Kuali Foundation
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

import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Interface for voucher type documents that require debit/credit support
 */
public interface VoucherDocument extends AccountingDocument {

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
}
