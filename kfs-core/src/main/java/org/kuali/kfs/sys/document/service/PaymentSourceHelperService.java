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
package org.kuali.kfs.sys.document.service;

import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.WireCharge;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Methods to aid PaymentSources do various things they need to do, such as create correct GLPE entries
 */
public interface PaymentSourceHelperService {
    /**
     * Retrieves the wire transfer information for the current fiscal year.
     * @return <code>WireCharge</code>
     */
    public WireCharge retrieveCurrentYearWireCharge();

    /**
     * Retrieves the wire charge for fiscal year based on the given date or null if one cannot be found
     * @param date the date to find a wire charge for
     * @return the wire charge for the fiscal year of the given date, or null if the wire charge cannot be found
     */
    public WireCharge retrieveWireChargeForDate(java.sql.Date date);

    /**
     * Builds an explicit and offset for the wire charge debit. The account associated with the first accounting is used for the
     * debit. The explicit and offset entries for the first accounting line and copied and customized for the wire charge.
     * @param paymentSource the payment source to generate bank offset entries for
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @param wireCharge wireCharge object from current fiscal year
     * @return GeneralLedgerPendingEntry generated wire charge debit
     */
    public GeneralLedgerPendingEntry processWireChargeDebitEntries(PaymentSource paymentSource, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge);

    /**
     * Builds an explicit and offset for the wire charge credit. The account and income object code found in the wire charge table
     * is used for the entry.
     * @param paymentSource the payment source to generate bank offset entries for
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @param chargeEntry GLPE charge
     * @param wireCharge wireCharge object from current fiscal year
     */
    public void processWireChargeCreditEntries(PaymentSource paymentSource, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge, GeneralLedgerPendingEntry chargeEntry);

    /**
     * If bank specification is enabled generates bank offsetting entries for the document amount
     * @param paymentSource the payment source to generate bank offset entries for
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @param paymentMethodCode the payment method of the given PaymentSource
     * @param wireTransferOrForeignDraftEntryDocumentType the FSLO document type for wire transfer or foreign draft entries associated with the given payment source
     * @return true if the entries were successfully generated, false otherwise
     */
    public abstract boolean generateDocumentBankOffsetEntries(PaymentSource paymentSource, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String wireTransferOrForeignDraftEntryDocumentType);

    /**
     * If bank specification is enabled generates bank offsetting entries for the document amount
     * @param paymentSource the payment source to generate bank offset entries for
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @param paymentMethodCode the payment method of the given PaymentSource
     * @param wireTransferOrForeignDraftEntryDocumentType the FSLO document type for wire transfer or foreign draft entries associated with the given payment source
     * @param bankOffsetAmount the amount to offset
     * @return true if the entries were successfully generated, false otherwise
     */
    public abstract boolean generateDocumentBankOffsetEntries(PaymentSource paymentSource, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String wireTransferOrForeignDraftEntryDocumentType, KualiDecimal bankOffsetAmount);

    /**
     * Builds the URL where disbursement info for a given disbursement can be looked up
     * @return the disbursement info URL
     */
    public abstract String getDisbursementInfoUrl();

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
     * @param extractionService the service which will tell this service whether or not to cancel a given pending entry
     */
    public abstract void handleEntryCancellation(PaymentSource paymentSource, PaymentSourceToExtractService<?> extractionService);

    /**
     * Updates the given general ledger pending entry so that it will have the opposite effect of what it was created to do; this,
     * in effect, undoes the entries that were already posted for this document
     *
     * @param glpe the general ledger pending entry to undo
     */
    public abstract void oppositifyAndSaveEntry(GeneralLedgerPendingEntry glpe, GeneralLedgerPendingEntrySequenceHelper glpeSeqHelper);
}
