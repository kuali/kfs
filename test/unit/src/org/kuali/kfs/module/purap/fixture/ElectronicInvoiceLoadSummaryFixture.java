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

import java.sql.Timestamp;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum ElectronicInvoiceLoadSummaryFixture {

    EILS_BASIC(
            "123456789", // vendorDunsNumber
            1000, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "Test Vendor", //vendorName
            new Integer(1), // invoiceLoadSuccessCount
            new KualiDecimal(10.00), // invoiceLoadSuccessAmount
            new Integer(0), // invoiceLoadFailCount
            new KualiDecimal(0.00), // invoiceLoadFailAmount
            Boolean.TRUE, // isEmpty
            new Timestamp(new java.util.Date().getTime()) // fileProcessTimestamp
    ),
    EILS_MATCHING(
            "002254837", // vendorDunsNumber
            1001, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "Kuali University", //vendorName
            new Integer(1), // invoiceLoadSuccessCount
            new KualiDecimal(10.00), // invoiceLoadSuccessAmount
            new Integer(0), // invoiceLoadFailCount
            new KualiDecimal(0.00), // invoiceLoadFailAmount
            Boolean.TRUE, // isEmpty
            new Timestamp(new java.util.Date().getTime()) // fileProcessTimestamp
    ),
    ;

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
    private Timestamp fileProcessTimestamp;

    private ElectronicInvoiceLoadSummaryFixture(String vendorDunsNumber,
        Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, String vendorName, Integer invoiceLoadSuccessCount,
        KualiDecimal invoiceLoadSuccessAmount, Integer invoiceLoadFailCount, KualiDecimal invoiceLoadFailAmount, Boolean isEmpty, Timestamp fileProcessTimestamp) {

        this.vendorDunsNumber = vendorDunsNumber;
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
        this.vendorName = vendorName;
        this.invoiceLoadSuccessCount = invoiceLoadSuccessCount;
        this.invoiceLoadSuccessAmount = invoiceLoadSuccessAmount;
        this.invoiceLoadFailCount = invoiceLoadFailCount;
        this.invoiceLoadFailAmount = invoiceLoadFailAmount;
        this.isEmpty = isEmpty;
        this.fileProcessTimestamp = fileProcessTimestamp;
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
        eils.setFileProcessTimestamp(fileProcessTimestamp);

        return eils;
    }

}
