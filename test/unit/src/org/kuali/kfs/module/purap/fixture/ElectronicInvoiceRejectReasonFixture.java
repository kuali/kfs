/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.fixture;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;

public enum ElectronicInvoiceRejectReasonFixture {

    EIRR_BASIC(
            null, // invoiceFileName;
            "TEST", // invoiceRejectReasonTypeCode;
            "Test Reason Description" // invoiceRejectReasonDescription;
    ), ;

    private String invoiceFileName;
    private String invoiceRejectReasonTypeCode;
    private String invoiceRejectReasonDescription;

    private ElectronicInvoiceRejectReasonFixture(String invoiceFileName, String invoiceRejectReasonTypeCode, String invoiceRejectReasonDescription) {

        this.invoiceFileName = invoiceFileName;
        this.invoiceRejectReasonTypeCode = invoiceRejectReasonTypeCode;
        this.invoiceRejectReasonDescription = invoiceRejectReasonDescription;

    }

    /**
     * Creates a Reject Reason from this fixture and adds the item to the specified Document.
     * 
     * @param receivingLineDocument the specified Receiving Line Document.
     */
    public void addTo(ElectronicInvoiceRejectDocument rejectDocument) {
        ElectronicInvoiceRejectReason rejectReason = null;
        rejectReason = this.createElectronicInvoiceRejectReason();
        rejectReason.setElectronicInvoiceRejectDocument(rejectDocument);
        rejectReason.setPurapDocumentIdentifier(rejectDocument.getPurapDocumentIdentifier());
        rejectDocument.addRejectReason(rejectReason);
    }

    public ElectronicInvoiceRejectReason createElectronicInvoiceRejectReason() {
        ElectronicInvoiceRejectReason eirr = new ElectronicInvoiceRejectReason();

        eirr.setInvoiceFileName(invoiceFileName);
        eirr.setInvoiceRejectReasonTypeCode(invoiceRejectReasonTypeCode);
        eirr.setInvoiceRejectReasonDescription(invoiceRejectReasonDescription);

        return eirr;
    }

}
