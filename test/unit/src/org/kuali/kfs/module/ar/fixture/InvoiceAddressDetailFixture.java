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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;

/**
 * Fixture class for InvoiceAddressDetail
 */
public enum InvoiceAddressDetailFixture {
    INV_ADDRESS_DETAIL1("documentNumber", "customerNumber",111, "P", "customerAddressName", "customerInvoiceTemplateCode", "invoiceTransmissionMethodCode"),
    INV_ADDRESS_DETAIL2("documentNumber", "customerNumber",111, "A", "customerAddressName", "customerInvoiceTemplateCode", "invoiceTransmissionMethodCode");

    private String documentNumber;
    private String customerNumber;
    private Integer customerAddressIdentifier;
    private String customerAddressTypeCode;
    private String customerAddressName;
    private String customerInvoiceTemplateCode;
    private String invoiceTransmissionMethodCode;

    private InvoiceAddressDetailFixture(String documentNumber, String customerNumber, Integer customerAddressIdentifier, String customerAddressTypeCode, String customerAddressName, String customerInvoiceTemplateCode, String invoiceTransmissionMethodCode) {
        this.documentNumber = documentNumber;
        this.customerNumber = customerNumber;
        this.customerAddressIdentifier = customerAddressIdentifier;
        this.customerAddressTypeCode = customerAddressTypeCode;
        this.customerAddressName = customerAddressName;
        this.customerInvoiceTemplateCode = customerInvoiceTemplateCode;
        this.invoiceTransmissionMethodCode = invoiceTransmissionMethodCode;
    }

    public InvoiceAddressDetail createInvoiceAddressDetail() {
        InvoiceAddressDetail invoiceAddressDetail = new InvoiceAddressDetail();
        invoiceAddressDetail.setDocumentNumber(this.documentNumber);
        invoiceAddressDetail.setCustomerNumber(this.customerNumber);
        invoiceAddressDetail.setCustomerAddressIdentifier(this.customerAddressIdentifier);
        invoiceAddressDetail.setCustomerAddressTypeCode(this.customerAddressTypeCode);
        invoiceAddressDetail.setCustomerAddressName(this.customerAddressName);
        invoiceAddressDetail.setCustomerInvoiceTemplateCode(this.customerInvoiceTemplateCode);
        invoiceAddressDetail.setInvoiceTransmissionMethodCode(this.invoiceTransmissionMethodCode);

        return invoiceAddressDetail;
    }
}
