/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.purap.fixtures;

import org.kuali.core.service.DocumentService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.test.DocumentTestUtils;

import edu.iu.uis.eden.exception.WorkflowException;

public enum PurchasingAccountsPayableDocumentFixture {

    // REQUISITION FIXTURES
    REQ_ONLY_REQUIRED_FIELDS(null, RequisitionStatuses.IN_PROCESS, null, null, null, null, null, null, null, null, null, null, null),
    REQ_WITH_MANUALLY_ENTERED_VENDOR(null, RequisitionStatuses.IN_PROCESS, null, null, "Colts Gear Shop", "111 Champs St", null, "Indy Rocks", "IN", "11111", "US", null, null),

    // PURCHASE ORDER FIXTURES
    //TODO f2f: fix the PO one because actually, the vendor must be selected from the database
    PO_ONLY_REQUIRED_FIELDS(null, PurchaseOrderStatuses.IN_PROCESS, null, null, "Colts Gear Shop", "111 Champs St", null, "Indy Rocks", "IN", "11111", "US", null, null),
    PO_WITH_MANUALLY_ENTERED_VENDOR(null, PurchaseOrderStatuses.IN_PROCESS, null, null, "Colts Gear Shop", "111 Champs St", null, "Indy Rocks", "IN", "11111", "US", null, null),

    //PAYMENT REQUEST FIXTURES
    //CREDIT MEMO FIXTURES
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
    public final String vendorPostalCode;
    public final String vendorCountryCode;
    public final String vendorCustomerNumber;
    public final Integer accountsPayablePurchasingDocumentLinkIdentifier;

    private PurchasingAccountsPayableDocumentFixture(
            Integer purapDocumentIdentifier,
            String statusCode,
            Integer vendorHeaderGeneratedIdentifier,
            Integer vendorDetailAssignedIdentifier,
            String vendorName,
            String vendorLine1Address,
            String vendorLine2Address,
            String vendorCityName,
            String vendorStateCode,
            String vendorPostalCode,
            String vendorCountryCode,
            String vendorCustomerNumber,
            Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
        this.statusCode = statusCode;
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
        this.vendorName = vendorName;
        this.vendorLine1Address = vendorLine1Address;
        this.vendorLine2Address = vendorLine2Address;
        this.vendorCityName = vendorCityName;
        this.vendorStateCode = vendorStateCode;
        this.vendorPostalCode = vendorPostalCode;
        this.vendorCountryCode = vendorCountryCode;
        this.vendorCustomerNumber = vendorCustomerNumber;
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public PurchasingAccountsPayableDocument createPurchasingAccountsPayableDocument(Class clazz) {
        PurchasingAccountsPayableDocument doc = null;
        try {
            doc = (PurchasingAccountsPayableDocument)DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), clazz);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        doc.setPurapDocumentIdentifier(this.purapDocumentIdentifier);
        doc.setStatusCode(this.statusCode);
        doc.setVendorHeaderGeneratedIdentifier(this.vendorHeaderGeneratedIdentifier);
        doc.setVendorDetailAssignedIdentifier(this.vendorDetailAssignedIdentifier);
        doc.setVendorName(this.vendorName);
        doc.setVendorLine1Address(this.vendorLine1Address);
        doc.setVendorLine2Address(this.vendorLine2Address);
        doc.setVendorCityName(this.vendorCityName);
        doc.setVendorStateCode(this.vendorStateCode);
        doc.setVendorPostalCode(this.vendorPostalCode);
        doc.setVendorCountryCode(this.vendorCountryCode);
        doc.setVendorCustomerNumber(this.vendorCustomerNumber);
        doc.setAccountsPayablePurchasingDocumentLinkIdentifier(this.accountsPayablePurchasingDocumentLinkIdentifier);
        
        //TODO f2f: (chris) add items and accounts
        
        return doc;
    }

}
