/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.financial.service;

import java.util.Date;

import org.kuali.module.financial.document.DisbursementVoucherDocument;

/**
 * 
 * This service interface defines the methods that a DisbursementVoucherExtractService implementation must provide.
 * 
 */
public interface DisbursementVoucherExtractService {
    
    /**
     * Extract all disbursement vouchers that need to be paid from the database and prepares them for payment.
     * 
     * @return True if the extraction of payments is successful, false if not.
     */
    public boolean extractPayments();
    
    /**
     * Cancels a disbursement voucher completely, because its payment has been canceled
     * @param dv the disbursement voucher to cancel
     */
    public abstract void cancelExtractedDisbursementVoucher(DisbursementVoucherDocument dv, java.sql.Date processDate);
    
    /**
     * Resets the disbursement voucher so that it can be reextracted
     * @param dv the disbursement voucher to reset for reextraction
     */
    public abstract void resetExtractedDisbursementVoucher(DisbursementVoucherDocument dv, java.sql.Date processDate);
    
    /**
     * Returns the disbursement voucher document associated with the given document number
     * @param documentNumber the id of the document to retrieve
     * @return the DV document if found, or null
     */
    public abstract DisbursementVoucherDocument getDocumentById(String documentNumber);
    
    /**
     * Marks a disbursement voucher as paid
     * @param dv the disbursement voucher to mark
     * @param processDate the date when the dv was paid
     */
    public abstract void markDisbursementVoucherAsPaid(DisbursementVoucherDocument dv, java.sql.Date processDate);
}
