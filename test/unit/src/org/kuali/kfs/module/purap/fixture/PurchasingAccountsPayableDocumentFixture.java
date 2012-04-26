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

import org.kuali.kfs.module.purap.PurapConstants.CreditMemoStatuses;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

public enum PurchasingAccountsPayableDocumentFixture {

    // REQUISITION FIXTURES
    REQ_ONLY_REQUIRED_FIELDS(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            null, // vendorHeaderGeneratedIdentifier
            null, // vendorDetailAssignedIdentifier
            null, // vendorName
            null, // vendorLine1Address
            null, // vendorLine2Address
            null, // vendorCityName
            null, // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            null, // vendorPostalCode
            null, // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator
    REQ_WITH_MANUALLY_ENTERED_VENDOR(null, RequisitionStatuses.APPDOC_IN_PROCESS, null, null, "Colts Gear Shop", "111 Champs St", null, "Indy Rocks", "IN", null, "11111", "US", null, null,false),
    REQ_TAX(null, 
            RequisitionStatuses.APPDOC_IN_PROCESS, 
            1000, 
            0, 
            "ABC CLEANING SERVICES", 
            "123456 BROAD ST", 
            null, 
            "TRUMANSBURG", 
            "SC", 
            null, 
            "11111", 
            "US", 
            null, 
            null,
            false),
    // APO FIXTURES
    REQ_VALID_APO(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            1002, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "ABC Cleaning Services", // vendorName
            "123456 BROAD ST", // vendorLine1Address
            null, // vendorLine2Address
            "TRUMANSBURG", // vendorCityName
            "NY", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "14886", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator
    REQ_ALTERNATE_APO(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            1016, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "Physik Instrument L. P.", // vendorName
            "16 AUBURN ST", // vendorLine1Address
            null, // vendorLine2Address
            "AUBURN", // vendorCityName
            "MA", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "01501", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator

    REQ_WITH_RESTRICTED_VENDOR(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            1005, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "RESTRICTED LEGAL SERVICES VENDOR", // vendorName
            "123 Hagadorn Rd", // vendorLine1Address
            null, // vendorLine2Address
            "EAST LANSING", // vendorCityName
            "MI", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "48823", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator
                    
    REQ_WITH_VENDOR_NOT_IN_DATABASE(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            -999999999, // vendorHeaderGeneratedIdentifier
            -9, // vendorDetailAssignedIdentifier
            "MY UNEXISTING VENDOR", // vendorName
            "123 Hagadorn Rd", // vendorLine1Address
            null, // vendorLine2Address
            "EAST LANSING", // vendorCityName
            "MI", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "48823", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)

    REQ_WITH_DEBARRED_VENDOR(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            1004, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "DEBARRED VENDOR", // vendorName
            "123 Hagadorn Rd", // vendorLine1Address
            null, // vendorLine2Address
            "EAST LANSING", // vendorCityName
            "MI", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "48823", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)

    REQ_WITH_INACTIVE_VENDOR(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            1019, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "INACTIVE PO VENDOR", // vendorName
            "123 Hagadorn Rd", // vendorLine1Address
            null, // vendorLine2Address
            "EAST LANSING", // vendorCityName
            "MI", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "48823", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)

    REQ_WITH_DV_VENDOR(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            1003, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "BASIC CORP ACTIVE", // vendorName
            "123 Hagadorn Rd", // vendorLine1Address
            null, // vendorLine2Address
            "EAST LANSING", // vendorCityName
            "MI", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "48823", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)
            
    REQ_WITH_INVALID_US_VENDOR_ZIP_CODE_CONTAINS_LETTERS(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            1002, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "BASIC CORP ACTIVE", // vendorName
            "123 Hagadorn Rd", // vendorLine1Address
            null, // vendorLine2Address
            "EAST LANSING", // vendorCityName
            "MI", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "ABC12", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)
                    
    REQ_WITH_INVALID_US_VENDOR_ZIP_CODE_BAD_FORMAT(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            1002, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "BASIC CORP ACTIVE", // vendorName
            "123 Hagadorn Rd", // vendorLine1Address
            null, // vendorLine2Address
            "EAST LANSING", // vendorCityName           
            "MI", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "123456", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)
                    
    REQ_WITH_INVALID_NON_US_VENDOR_ZIP_CODE_CONTAINS_LETTERS(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            1002, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "BASIC CORP ACTIVE", // vendorName
            "123 Hagadorn Rd", // vendorLine1Address
            null, // vendorLine2Address
            "TOKYO", // vendorCityName
            null, // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "ABC12", // vendorPostalCode
            "JP", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)
                    
    REQ_WITH_VALID_US_VENDOR_ZIP_CODE_WITH_4_TRAILING_NUMBERS(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            1002, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "BASIC CORP ACTIVE", // vendorName
            "123 Hagadorn Rd", // vendorLine1Address
            null, // vendorLine2Address
            "East Lansing", // vendorCityName
            null, // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "48823-1234", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)
                    
    // PURCHASE ORDER FIXTURES
    // TODO f2f: fix the PO one because actually, the vendor must be selected from the database
    PO_ONLY_REQUIRED_FIELDS(null, // purapDocumentIdentifier
            PurchaseOrderStatuses.APPDOC_IN_PROCESS, // statusCode
            1000, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "ABC Cleaning Services", // vendorName
            "123456 BROAD ST", // vendorLine1Address
            null, // vendorLine2Address
            "TRUMANSBURG", // vendorCityName
            "NY", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "14886", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)
    PO_WITH_MANUALLY_ENTERED_VENDOR(null, PurchaseOrderStatuses.APPDOC_IN_PROCESS, 1000, 0, "ABC Cleaning Services", "123456 BROAD ST", null, "TRUMANSBURG", "NY", null, "14886", "US", null, null, false),

    // PAYMENT REQUEST FIXTURES
    PREQ_ONLY_REQUIRED_FIELDS(null, // purapDocumentIdentifier 
            PaymentRequestStatuses.APPDOC_IN_PROCESS,  // statusCode
            1010,   // vendorHeaderGeneratedIdentifier
            2,   // vendorDetailAssignedIdentifier
            "DIVISION 2 OF PO BASIC",   // vendorName
            "9988 8TH STREET",   // vendorLine1Address
            null,   // vendorLine2Address
            "A PLACE IN THE SUN",   // vendorCityName
            "CA",   // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "99888",   // vendorPostalCode
            "US",   // vendorCountryCode
            null,   // vendorCustomerNumber
            null,    // accountsPayablePurchasingDocumentLinkIdentifier
            false), //useTaxIndicator)
    PREQ_VENDOR_FOR_PO_CLOSE_DOC(null, // purapDocumentIdentifier
            PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED, // statusCode
            1000, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "ABC Cleaning Services", // vendorName
            "123456 BROAD ST", // vendorLine1Address
            null, // vendorLine2Address
            "TRUMANSBURG", // vendorCityName
            "NY", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "14886", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier
            false), //useTaxIndicator)
            
    // CREDIT MEMO FIXTURES
    CM_ONLY_REQUIRED_FIELDS(null, // purapDocumentIdentifier
            CreditMemoStatuses.APPDOC_IN_PROCESS, // statusCode
            1000, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "ABC Cleaning Services", // vendorName
            "123456 BROAD ST", // vendorLine1Address
            null, // vendorLine2Address
            "TRUMANSBURG", // vendorCityName
            "NY", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "14886", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)
    REQ_MULTI_QUANTITY(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            1002, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "MK CORPORATION ACTIVE", // vendorName
            "3894 SOUTH ST", // vendorLine1Address
            "P.O. BOX 3455", // vendorLine2Address
            "SPRINGFIELD", // vendorCityName
            "IL", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "33555", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)
    REQ_MULTI_NON_QUANTITY(null, // purapDocumentIdentifier
            RequisitionStatuses.APPDOC_IN_PROCESS, // statusCode
            1016, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "PHYSIK INSTRUMENT L.P.", // vendorName
            "16 AUBURN ST", // vendorLine1Address
            null, // vendorLine2Address
            "AUBURN", // vendorCityName
            "MA", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "01501", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)
     EINVOICE_PO(null, // purapDocumentIdentifier
            PurchaseOrderStatuses.APPDOC_IN_PROCESS, // statusCode
            1001, // vendorHeaderGeneratedIdentifier
            0, // vendorDetailAssignedIdentifier
            "KUALI UNIVERSITY", // vendorName
            "123456 BROAD ST", // vendorLine1Address
            null, // vendorLine2Address
            "TRUMANSBURG", // vendorCityName
            "NY", // vendorStateCode
            null, // vendorAddressInternationalProvinceName
            "14886", // vendorPostalCode
            "US", // vendorCountryCode
            null, // vendorCustomerNumber
            null, // accountsPayablePurchasingDocumentLinkIdentifier, 
            false), //useTaxIndicator)       
    ;

    public final Integer purapDocumentIdentifier;
    public final String statusCode;
    public final Integer vendorHeaderGeneratedIdentifier;
    public final Integer vendorDetailAssignedIdentifier;
    public final String vendorName;
    public final String vendorLine1Address;
    public final String vendorLine2Address;
    public final String vendorCityName;
    public final String vendorStateCode;
    public final String vendorAddressInternationalProvinceName;
    public final String vendorPostalCode;
    public final String vendorCountryCode;
    public final String vendorCustomerNumber;
    public final Integer accountsPayablePurchasingDocumentLinkIdentifier;
    public final boolean useTaxIndicator;

    private PurchasingAccountsPayableDocumentFixture(Integer purapDocumentIdentifier, String statusCode, Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, String vendorName, String vendorLine1Address, String vendorLine2Address, String vendorCityName, String vendorStateCode, String vendorAddressInternationalProvinceName, String vendorPostalCode, String vendorCountryCode, String vendorCustomerNumber, Integer accountsPayablePurchasingDocumentLinkIdentifier,boolean useTaxIndicator) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
        this.statusCode = statusCode;
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
        this.vendorName = vendorName;
        this.vendorLine1Address = vendorLine1Address;
        this.vendorLine2Address = vendorLine2Address;
        this.vendorCityName = vendorCityName;
        this.vendorStateCode = vendorStateCode;
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
        this.vendorPostalCode = vendorPostalCode;
        this.vendorCountryCode = vendorCountryCode;
        this.vendorCustomerNumber = vendorCustomerNumber;
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
        this.useTaxIndicator = useTaxIndicator;
    }

    public PurchasingAccountsPayableDocument createPurchasingAccountsPayableDocument(Class clazz) {
        PurchasingAccountsPayableDocument doc = null;
        try {
            doc = (PurchasingAccountsPayableDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), clazz);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        doc.setPurapDocumentIdentifier(this.purapDocumentIdentifier);
        doc.setApplicationDocumentStatus(this.statusCode);
        doc.setVendorHeaderGeneratedIdentifier(this.vendorHeaderGeneratedIdentifier);
        doc.setVendorDetailAssignedIdentifier(this.vendorDetailAssignedIdentifier);
        doc.setVendorName(this.vendorName);
        doc.setVendorLine1Address(this.vendorLine1Address);
        doc.setVendorLine2Address(this.vendorLine2Address);
        doc.setVendorCityName(this.vendorCityName);
        doc.setVendorStateCode(this.vendorStateCode);
        doc.setVendorAddressInternationalProvinceName(this.vendorAddressInternationalProvinceName);
        doc.setVendorPostalCode(this.vendorPostalCode);
        doc.setVendorCountryCode(this.vendorCountryCode);
        doc.setVendorCustomerNumber(this.vendorCustomerNumber);
        doc.setAccountsPayablePurchasingDocumentLinkIdentifier(this.accountsPayablePurchasingDocumentLinkIdentifier);


        // TODO f2f: (chris) add items and accounts

        return doc;
    }

}
