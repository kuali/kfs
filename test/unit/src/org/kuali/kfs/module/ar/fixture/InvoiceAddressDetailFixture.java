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
    INV_ADDRESS_DETAIL1("documentNumber", "agencyNumber",111, "agencyAddressTypeCode", "agencyAddressName", "preferredAgencyInvoiceTemplateCode", "agencyInvoiceTemplateCode", "invoiceIndicatorCode", "preferredInvoiceIndicatorCode");

    private String documentNumber;
    private String agencyNumber;
    private Integer customerAddressIdentifier;
    private String customerAddressTypeCode;
    private String agencyAddressName;
    private String preferredAgencyInvoiceTemplateCode;
    private String agencyInvoiceTemplateCode;
    private String invoiceIndicatorCode;
    private String preferredInvoiceIndicatorCode;

    private InvoiceAddressDetailFixture(String documentNumber, String agencyNumber, Integer customerAddressIdentifier, String customerAddressTypeCode, String agencyAddressName, String preferredAgencyInvoiceTemplateCode, String agencyInvoiceTemplateCode, String invoiceIndicatorCode, String preferredInvoiceIndicatorCode) {

        this.documentNumber = documentNumber;
        this.agencyNumber = agencyNumber;
        this.customerAddressIdentifier = customerAddressIdentifier;
        this.customerAddressTypeCode = customerAddressTypeCode;
        this.agencyAddressName = agencyAddressName;
        this.preferredAgencyInvoiceTemplateCode = preferredAgencyInvoiceTemplateCode;
        this.agencyInvoiceTemplateCode = agencyInvoiceTemplateCode;
        this.invoiceIndicatorCode = invoiceIndicatorCode;
        this.preferredInvoiceIndicatorCode = preferredInvoiceIndicatorCode;
    }

    public InvoiceAddressDetail createInvoiceAddressDetail() {
        InvoiceAddressDetail invoiceAddressDetail = new InvoiceAddressDetail();
        invoiceAddressDetail.setDocumentNumber(this.documentNumber);
        invoiceAddressDetail.setCustomerNumber(this.agencyNumber);
        invoiceAddressDetail.setCustomerAddressIdentifier(this.customerAddressIdentifier);
        invoiceAddressDetail.setCustomerAddressTypeCode(this.customerAddressTypeCode);
        invoiceAddressDetail.setCustomerAddressName(this.agencyAddressName);
        invoiceAddressDetail.setPreferredCustomerInvoiceTemplateCode(this.preferredAgencyInvoiceTemplateCode);
        invoiceAddressDetail.setCustomerInvoiceTemplateCode(this.agencyInvoiceTemplateCode);
        invoiceAddressDetail.setInvoiceIndicatorCode(this.invoiceIndicatorCode);
        invoiceAddressDetail.setPreferredInvoiceIndicatorCode(this.preferredInvoiceIndicatorCode);

        return invoiceAddressDetail;
    }
}
