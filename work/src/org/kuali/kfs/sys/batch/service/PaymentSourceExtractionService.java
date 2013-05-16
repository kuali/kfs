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
package org.kuali.kfs.sys.batch.service;

import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.document.PaymentSource;

/**
 *
 * This service interface defines the methods that a PaymentSourceExtractionService implementation must provide.
 *
 */
public interface PaymentSourceExtractionService {

    /**
     * Extract all disbursement vouchers that need to be paid from the database and prepares them for payment.
     *
     * @return True if the extraction of payments is successful, false if not.
     */
    public boolean extractPayments();

    /**
     * Pulls all disbursement voucher which pay checks and which are marked as "immediate payment" from the database and builds payment information for them
     */
    public void extractImmediatePayments();

    /**
     * Cancels a disbursement voucher completely, because its payment has been canceled
     * @param dv the disbursement voucher to cancel
     */
    public abstract void cancelExtractedPaymentSource(PaymentSource paymentSource, java.sql.Date processDate);

    /**
     * Resets the disbursement voucher so that it can be reextracted
     * @param dv the disbursement voucher to reset for reextraction
     */
    public abstract void resetExtractedPaymentSource(PaymentSource paymentSource, java.sql.Date processDate);

    /**
     * Marks a disbursement voucher as paid
     * @param dv the disbursement voucher to mark
     * @param processDate the date when the dv was paid
     */
    public abstract void markPaymentSourceAsPaid(PaymentSource paymentSource, java.sql.Date processDate);

    /**
     * Creates a batch payment for a single disbursement voucher
     * @param disbursementVoucher the voucher to immediately extract
     */
    public abstract void extractSingleImmediatePayment(PaymentSource paymentSource);

    /**
     * Builds a note for the check stub text, wrapping words appropriately
     * @param checkStubText the text for the check note
     * @param previousLineCount the number of lines already on this document
     * @return a PDP PaymentNoteText with the check stub text well-formatted
     */
    public abstract PaymentNoteText buildNoteForCheckStubText(String checkStubText, int previousLineCount);

    /**
     * When a payment source is cancelled, its entries need to be reversed under certain circumstances.  This method will reverse those entries
     * @param paymentSource the cancelled payment source to reverse entries for
     */
    public abstract void handleEntryCancellation(PaymentSource paymentSource);

    /**
     * Updates the given general ledger pending entry so that it will have the opposite effect of what it was created to do; this,
     * in effect, undoes the entries that were already posted for this document
     *
     * @param glpe the general ledger pending entry to undo
     */
    public abstract void oppositifyAndSaveEntry(GeneralLedgerPendingEntry glpe, GeneralLedgerPendingEntrySequenceHelper glpeSeqHelper);
}
