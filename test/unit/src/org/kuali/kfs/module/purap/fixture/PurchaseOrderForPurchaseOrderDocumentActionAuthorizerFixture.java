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
package org.kuali.module.purap.fixtures;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;

public enum PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture {
    PO_VALID_RETRANSMIT(
        PurapConstants.PurchaseOrderStatuses.OPEN,  //statusCode
        new Date(System.currentTimeMillis()), //purchaseOrderLastTransmitDate
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        false, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.PRINT //transmissionMethodCode
    ),
    PO_VALID_FIRST_TRANSMIT_PRINT(
        PurapConstants.PurchaseOrderStatuses.IN_PROCESS,  //statusCode
        null, //purchaseOrderLastTransmitDate
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        false, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.PRINT //transmissionMethodCode
    ),
    PO_VALID_REOPEN(
        PurapConstants.PurchaseOrderStatuses.CLOSED,  //statusCode
        new Date(System.currentTimeMillis()), //purchaseOrderLastTransmitDate
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        true, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.PRINT //transmissionMethodCode
    ),
    PO_VALID_CLOSE(
        PurapConstants.PurchaseOrderStatuses.OPEN,  //statusCode
        new Date(System.currentTimeMillis()), //purchaseOrderLastTransmitDate
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        false, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.NOPRINT //transmissionMethodCode
    ),    
    PO_VALID_VOID_PENDING_PRINT (
        PurapConstants.PurchaseOrderStatuses.PENDING_PRINT, //statusCode
        new Date(System.currentTimeMillis()), //purchaseOrderLastTransmitDate
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        false, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.NOPRINT //transmissionMethodCode
    ),
    PO_VALID_VOID_OPEN_NO_PREQ (
        PurapConstants.PurchaseOrderStatuses.OPEN, //statusCode
        new Date(System.currentTimeMillis()), //purchaseOrderLastTransmitDate
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        false, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.NOPRINT //transmissionMethodCode
    ),
    PO_VALID_REMOVE_HOLD (
        PurapConstants.PurchaseOrderStatuses.PAYMENT_HOLD, //statusCode
        new Date(System.currentTimeMillis()), //purchaseOrderLastTransmitDate
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        false, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.NOPRINT //transmissionMethodCode
    )
    ;
    
    private String statusCode;
    private Date purchaseOrderLastTransmitDate;
    private boolean purchaseOrderCurrentIndicator;
    private boolean pendingActionIndicator;
    private boolean purchaseOrderAutomaticIndicator;
    private String transmissionMethodCode;
    
    private PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture(
            String statusCode,
            Date purchaseOrderLastTransmitDate,
            boolean purchaseOrderCurrentIndicator,
            boolean pendingActionIndicator,
            boolean purchaseOrderAutomaticIndicator,
            String transmissionMethodCode) {
        this.statusCode = statusCode;
        this.purchaseOrderLastTransmitDate = purchaseOrderLastTransmitDate;
        this.purchaseOrderCurrentIndicator = purchaseOrderCurrentIndicator;
        this.pendingActionIndicator = pendingActionIndicator;
        this.purchaseOrderAutomaticIndicator = purchaseOrderAutomaticIndicator;
        this.transmissionMethodCode = transmissionMethodCode;
    }
    
    public PurchaseOrderDocument createPurchaseOrderDocument() {
        PurchaseOrderDocument doc = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        doc.setStatusCode(this.statusCode);
        doc.setPurchaseOrderLastTransmitDate(this.purchaseOrderLastTransmitDate);
        doc.setPurchaseOrderCurrentIndicator(this.purchaseOrderCurrentIndicator);
        doc.setPendingActionIndicator(this.pendingActionIndicator);
        doc.setPurchaseOrderAutomaticIndicator(this.purchaseOrderAutomaticIndicator);
        doc.setPurchaseOrderTransmissionMethodCode(this.transmissionMethodCode);

        return doc;
    }
}
