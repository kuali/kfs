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

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;

public enum PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture {
    PO_VALID_RETRANSMIT(
        PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN,  //statusCode
        new Timestamp(System.currentTimeMillis()), //purchaseOrderLastTransmitTimestamp
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        false, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.PRINT //transmissionMethodCode
    ),
    PO_VALID_FIRST_TRANSMIT_PRINT(
        PurapConstants.PurchaseOrderStatuses.APPDOC_IN_PROCESS,  //statusCode
        null, //purchaseOrderLastTransmitTimestamp
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        false, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.PRINT //transmissionMethodCode
    ),
    PO_VALID_REOPEN(
        PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED,  //statusCode
        new Timestamp(System.currentTimeMillis()), //purchaseOrderLastTransmitTimestamp
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        true, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.PRINT //transmissionMethodCode
    ),
    PO_VALID_CLOSE(
        PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN,  //statusCode
        new Timestamp(System.currentTimeMillis()), //purchaseOrderLastTransmitTimestamp
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        false, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.NOPRINT //transmissionMethodCode
    ),    
    PO_VALID_VOID_PENDING_PRINT (
        PurapConstants.PurchaseOrderStatuses.APPDOC_PENDING_PRINT, //statusCode
        new Timestamp(System.currentTimeMillis()), //purchaseOrderLastTransmitTimestamp
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        false, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.NOPRINT //transmissionMethodCode
    ),
    PO_VALID_VOID_OPEN_NO_PREQ (
        PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN, //statusCode
        new Timestamp(System.currentTimeMillis()), //purchaseOrderLastTransmitTimestamp
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        false, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.NOPRINT //transmissionMethodCode
    ),
    PO_VALID_REMOVE_HOLD (
        PurapConstants.PurchaseOrderStatuses.APPDOC_PAYMENT_HOLD, //statusCode
        new Timestamp(System.currentTimeMillis()), //purchaseOrderLastTransmitTimestamp
        true, //purchaseOrderCurrentIndicator
        false, //pendingActionIndicator
        false, //purchaseOrderAutomaticIndicator
        PurapConstants.POTransmissionMethods.NOPRINT //transmissionMethodCode
    )
    ;
    
    private String statusCode;
    private Timestamp purchaseOrderLastTransmitTimestamp;
    private boolean purchaseOrderCurrentIndicator;
    private boolean pendingActionIndicator;
    private boolean purchaseOrderAutomaticIndicator;
    private String transmissionMethodCode;
    
    private PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture(
            String statusCode,
            Timestamp purchaseOrderLastTransmitTimestamp,
            boolean purchaseOrderCurrentIndicator,
            boolean pendingActionIndicator,
            boolean purchaseOrderAutomaticIndicator,
            String transmissionMethodCode) {
        this.statusCode = statusCode;
        this.purchaseOrderLastTransmitTimestamp = purchaseOrderLastTransmitTimestamp;
        this.purchaseOrderCurrentIndicator = purchaseOrderCurrentIndicator;
        this.pendingActionIndicator = pendingActionIndicator;
        this.purchaseOrderAutomaticIndicator = purchaseOrderAutomaticIndicator;
        this.transmissionMethodCode = transmissionMethodCode;
    }
    
    public PurchaseOrderDocument createPurchaseOrderDocument() {
        PurchaseOrderDocument doc = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        doc.setApplicationDocumentStatus(this.statusCode);
        doc.setPurchaseOrderLastTransmitTimestamp(this.purchaseOrderLastTransmitTimestamp);
        doc.setPurchaseOrderCurrentIndicator(this.purchaseOrderCurrentIndicator);
        doc.setPendingActionIndicator(this.pendingActionIndicator);
        doc.setPurchaseOrderAutomaticIndicator(this.purchaseOrderAutomaticIndicator);
        doc.setPurchaseOrderTransmissionMethodCode(this.transmissionMethodCode);

        return doc;
    }
}
