/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;

/**
 * Fixture class for InvoiceAddressDetail
 */
public enum InvoiceAddressDetailFixture {
    INV_ADDRESS_DETAIL1("documentNumber", "customerNumber",111, "customerAddressTypeCode", "customerAddressName", "preferredCustomerInvoiceTemplateCode", "customerInvoiceTemplateCode", "invoiceTransmissionMethodCode", "preferredInvoiceTransmissionCode");

    private String documentNumber;
    private String customerNumber;
    private Integer customerAddressIdentifier;
    private String customerAddressTypeCode;
    private String customerAddressName;
    private String preferredCustomerInvoiceTemplateCode;
    private String customerInvoiceTemplateCode;
    private String invoiceTransmissionMethodCode;
    private String preferredInvoiceTransmissionCode;

    private InvoiceAddressDetailFixture(String documentNumber, String customerNumber, Integer customerAddressIdentifier, String customerAddressTypeCode, String customerAddressName, String preferredCustomerInvoiceTemplateCode, String customerInvoiceTemplateCode, String invoiceTransmissionMethodCode, String preferredInvoiceTransmissionCode) {

        this.documentNumber = documentNumber;
        this.customerNumber = customerNumber;
        this.customerAddressIdentifier = customerAddressIdentifier;
        this.customerAddressTypeCode = customerAddressTypeCode;
        this.customerAddressName = customerAddressName;
        this.preferredCustomerInvoiceTemplateCode = preferredCustomerInvoiceTemplateCode;
        this.customerInvoiceTemplateCode = customerInvoiceTemplateCode;
        this.invoiceTransmissionMethodCode = invoiceTransmissionMethodCode;
        this.preferredInvoiceTransmissionCode = preferredInvoiceTransmissionCode;
    }

    public InvoiceAddressDetail createInvoiceAddressDetail() {
        InvoiceAddressDetail invoiceAddressDetail = new InvoiceAddressDetail();
        invoiceAddressDetail.setDocumentNumber(this.documentNumber);
        invoiceAddressDetail.setCustomerNumber(this.customerNumber);
        invoiceAddressDetail.setCustomerAddressIdentifier(this.customerAddressIdentifier);
        invoiceAddressDetail.setCustomerAddressTypeCode(this.customerAddressTypeCode);
        invoiceAddressDetail.setCustomerAddressName(this.customerAddressName);
        invoiceAddressDetail.setPreferredCustomerInvoiceTemplateCode(this.preferredCustomerInvoiceTemplateCode);
        invoiceAddressDetail.setCustomerInvoiceTemplateCode(this.customerInvoiceTemplateCode);
        invoiceAddressDetail.setInvoiceTransmissionMethodCode(this.invoiceTransmissionMethodCode);
        invoiceAddressDetail.setPreferredInvoiceTransmissionMethodCode(this.preferredInvoiceTransmissionCode);

        return invoiceAddressDetail;
    }
}
