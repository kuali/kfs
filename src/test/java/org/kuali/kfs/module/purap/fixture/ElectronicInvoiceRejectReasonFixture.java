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
