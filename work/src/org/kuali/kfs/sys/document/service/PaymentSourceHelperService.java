/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.service;

import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.WireCharge;
import org.kuali.kfs.sys.document.PaymentSource;

/**
 * Methods to aid PaymentSources do various things they need to do, such as create correct GLPE entries
 */
public interface PaymentSourceHelperService {
    /**
     * Retrieves the wire transfer information for the current fiscal year.
     *
     * @return <code>WireCharge</code>
     */
    public WireCharge retrieveCurrentYearWireCharge();

    /**
     * Builds an explicit and offset for the wire charge debit. The account associated with the first accounting is used for the
     * debit. The explicit and offset entries for the first accounting line and copied and customized for the wire charge.
     *
     * @param paymentSource the payment source to generate bank offset entries for
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @param wireCharge wireCharge object from current fiscal year
     * @return GeneralLedgerPendingEntry generated wire charge debit
     */
    public GeneralLedgerPendingEntry processWireChargeDebitEntries(PaymentSource paymentSource, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge);

    /**
     * Builds an explicit and offset for the wire charge credit. The account and income object code found in the wire charge table
     * is used for the entry.
     *
     * @param paymentSource the payment source to generate bank offset entries for
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @param chargeEntry GLPE charge
     * @param wireCharge wireCharge object from current fiscal year
     */
    public void processWireChargeCreditEntries(PaymentSource paymentSource, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge, GeneralLedgerPendingEntry chargeEntry);

    /**
     * If bank specification is enabled generates bank offsetting entries for the document amount
     *
     * @param paymentSource the payment source to generate bank offset entries for
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @param paymentMethodCode the payment method of the given PaymentSource
     * @param wireTransferOrForeignDraftEntryDocumentType the FSLO document type for wire transfer or foreign draft entries associated with the given payment source
     */
    public abstract boolean generateDocumentBankOffsetEntries(PaymentSource paymentSource, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String wireTransferOrForeignDraftEntryDocumentType);

}
