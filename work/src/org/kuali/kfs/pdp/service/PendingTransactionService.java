/*
 * Copyright 2007 The Kuali Foundation
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
/*
 * Created on Aug 30, 2004
 *
 */
package org.kuali.kfs.pdp.service;

import java.util.Iterator;

import org.kuali.kfs.pdp.businessobject.GlPendingTransaction;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;

/**
 * Creates GLPEs for PDP extracted and canceled payments.
 */
public interface PendingTransactionService {
    
    /**
     * Creates GLPE entries for the Payment record and stores to PDP pending entry table
     * 
     * @param paymentGroup payment group record to create GLPE for
     */
    public void generatePaymentGeneralLedgerPendingEntry(PaymentGroup paymentGroup);

    /**
     * Creates GLPE entries for a payment cancel and stores to PDP pending entry table. Debit/Credit codes are
     * reversed backing out the original GLPEs for the payment.
     * 
     * @param paymentGroup payment group record to create GLPE for
     */
    public void generateCancellationGeneralLedgerPendingEntry(PaymentGroup paymentGroup);

    /**
     * Creates GLPE entries for a reissue and stores to PDP pending entry table. Debit/Credit codes are
     * reversed backing out the original GLPEs for the payment.
     * 
     * @param paymentGroup payment group record to create GLPE for
     */  
    public void generateReissueGeneralLedgerPendingEntry(PaymentGroup paymentGroup);

    /**
     * Save a transaction
     * 
     * @param tran
     */
    public void save(GlPendingTransaction tran);

    /**
     * Get all of the GL transactions where the extract flag is null
     * 
     * @return Iterator of all the transactions
     */
    public Iterator<GlPendingTransaction> getUnextractedTransactions();
    
    /**
     * Deletes transactions records that have been copied to the GL
     */
    public void clearExtractedTransactions();
}
