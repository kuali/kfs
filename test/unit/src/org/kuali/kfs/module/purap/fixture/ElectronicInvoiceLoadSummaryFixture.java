/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.fixture;

import java.sql.Timestamp;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.rice.kns.util.KualiDecimal;

public enum ElectronicInvoiceLoadSummaryFixture {

    EILS_BASIC(
            null, // invoiceLoadSummaryIdentifier;
            "150982189", // vendorDunsNumber
            null, // vendorHeaderGeneratedIdentifier
            null, // vendorDetailAssignedIdentifier
            "Test Vendor", //vendorName
            new Integer(1), // invoiceLoadSuccessCount
            new KualiDecimal(10.00), // invoiceLoadSuccessAmount
            new Integer(0), // invoiceLoadFailCount
            new KualiDecimal(0.00), // invoiceLoadFailAmount
            Boolean.TRUE, // isEmpty
            new Timestamp(new java.util.Date().getTime()) // fileProcessDate
    ), ;

    private Integer invoiceLoadSummaryIdentifier;
    private String vendorDunsNumber; // this is string constant if DUNS not found
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorName;
    private Integer invoiceLoadSuccessCount = new Integer(0);
    private KualiDecimal invoiceLoadSuccessAmount = new KualiDecimal(0.00);
    private Integer invoiceLoadFailCount = new Integer(0);
    private KualiDecimal invoiceLoadFailAmount = new KualiDecimal(0.00);
    private Boolean isEmpty = Boolean.TRUE;
    private Timestamp fileProcessDate;

    private ElectronicInvoiceLoadSummaryFixture(Integer invoiceLoadSummaryIdentifier, String vendorDunsNumber,
        Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, String vendorName, Integer invoiceLoadSuccessCount,
        KualiDecimal invoiceLoadSuccessAmount, Integer invoiceLoadFailCount, KualiDecimal invoiceLoadFailAmount, Boolean isEmpty, Timestamp fileProcessDate) {

        this.invoiceLoadSummaryIdentifier = invoiceLoadSummaryIdentifier;
        this.vendorDunsNumber = vendorDunsNumber;
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
        this.vendorName = vendorName;
        this.invoiceLoadSuccessCount = invoiceLoadSuccessCount;
        this.invoiceLoadSuccessAmount = invoiceLoadSuccessAmount;
        this.invoiceLoadFailCount = invoiceLoadFailCount;
        this.invoiceLoadFailAmount = invoiceLoadFailAmount;
        this.isEmpty = isEmpty;
        this.fileProcessDate = fileProcessDate;
    }

    public ElectronicInvoiceLoadSummary createElectronicInvoiceLoadSummary() {
        ElectronicInvoiceLoadSummary eils = new ElectronicInvoiceLoadSummary();

        eils.setInvoiceLoadSummaryIdentifier(invoiceLoadSummaryIdentifier);
        eils.setVendorDunsNumber(vendorDunsNumber);
        eils.setVendorHeaderGeneratedIdentifier(vendorHeaderGeneratedIdentifier);
        eils.setVendorDetailAssignedIdentifier(vendorDetailAssignedIdentifier);
        eils.setVendorName(vendorName);
        eils.setInvoiceLoadSuccessCount(invoiceLoadSuccessCount);
        eils.setInvoiceLoadSuccessAmount(invoiceLoadSuccessAmount);
        eils.setInvoiceLoadFailCount(invoiceLoadFailCount);
        eils.setInvoiceLoadFailAmount(invoiceLoadFailAmount);
        eils.setIsEmpty(isEmpty);
        eils.setFileProcessDate(fileProcessDate);

        return eils;
    }

}
