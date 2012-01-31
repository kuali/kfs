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

import org.kuali.kfs.module.ar.businessobject.InvoiceAgencyAddressDetail;

/**
 * Fixture class for InvoiceAgencyAddressDetail
 */
public enum InvoiceAgencyAddressDetailFixture {
    INV_AGENCY_ADDRESS_DETAIL1("documentNumber", "agencyNumber", new Long(111), "agencyAddressTypeCode", "agencyAddressName", "preferredAgencyInvoiceTemplateCode", "agencyInvoiceTemplateCode", "invoiceIndicatorCode", "preferredInvoiceIndicatorCode");

    private String documentNumber;
    private String agencyNumber;
    private Long agencyAddressIdentifier;
    private String agencyAddressTypeCode;
    private String agencyAddressName;
    private String preferredAgencyInvoiceTemplateCode;
    private String agencyInvoiceTemplateCode;
    private String invoiceIndicatorCode;
    private String preferredInvoiceIndicatorCode;

    private InvoiceAgencyAddressDetailFixture(String documentNumber, String agencyNumber, Long agencyAddressIdentifier, String agencyAddressTypeCode, String agencyAddressName, String preferredAgencyInvoiceTemplateCode, String agencyInvoiceTemplateCode, String invoiceIndicatorCode, String preferredInvoiceIndicatorCode) {

        this.documentNumber = documentNumber;
        this.agencyNumber = agencyNumber;
        this.agencyAddressIdentifier = agencyAddressIdentifier;
        this.agencyAddressTypeCode = agencyAddressTypeCode;
        this.agencyAddressName = agencyAddressName;
        this.preferredAgencyInvoiceTemplateCode = preferredAgencyInvoiceTemplateCode;
        this.agencyInvoiceTemplateCode = agencyInvoiceTemplateCode;
        this.invoiceIndicatorCode = invoiceIndicatorCode;
        this.preferredInvoiceIndicatorCode = preferredInvoiceIndicatorCode;
    }

    public InvoiceAgencyAddressDetail createInvoiceAgencyAddressDetail() {
        InvoiceAgencyAddressDetail invoiceAgencyAddressDetail = new InvoiceAgencyAddressDetail();
        invoiceAgencyAddressDetail.setDocumentNumber(this.documentNumber);
        invoiceAgencyAddressDetail.setAgencyNumber(this.agencyNumber);
        invoiceAgencyAddressDetail.setAgencyAddressIdentifier(this.agencyAddressIdentifier);
        invoiceAgencyAddressDetail.setAgencyAddressTypeCode(this.agencyAddressTypeCode);
        invoiceAgencyAddressDetail.setAgencyAddressName(this.agencyAddressName);
        invoiceAgencyAddressDetail.setPreferredAgencyInvoiceTemplateCode(this.preferredAgencyInvoiceTemplateCode);
        invoiceAgencyAddressDetail.setAgencyInvoiceTemplateCode(this.agencyInvoiceTemplateCode);
        invoiceAgencyAddressDetail.setInvoiceIndicatorCode(this.invoiceIndicatorCode);
        invoiceAgencyAddressDetail.setPreferredInvoiceIndicatorCode(this.preferredInvoiceIndicatorCode);

        return invoiceAgencyAddressDetail;
    }
}
