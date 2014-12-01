/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
