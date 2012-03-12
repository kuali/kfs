/*
 * Copyright 2007 The Kuali Foundation
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

import java.sql.Timestamp;

import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.KFSConstants;


public enum AccountsPayableDocumentFixture {

    // PAYMENT REQUEST FIXTURES
    PREQ_ONLY_REQUIRED_FIELDS(null,  // accountsPayableApprovalDate
            null,  // lastActionPerformedByPersonId
            null,  // accountsPayableProcessorIdentifier
            false, // holdIndicator
            null,  // extractedTimestamp
            1000,  // purchaseOrderIdentifier
            null,  // processingCampusCode
            null,  // noteLine1Text
            null,  // noteLine2Text
            null,  // noteLine3Text
            false, // continuationAccountIndicator
            false, // closePurchaseOrderIndicator
            false  // reopenPurchaseOrderIndicator
            ),
     PREQ_FOR_PO_CLOSE_DOC(null,  // accountsPayableApprovalDate
            null,  // lastActionPerformedByPersonId
            KFSConstants.SYSTEM_USER,    // accountsPayableProcessorIdentifier
            false, // holdIndicator
            null,  // extractedTimestamp
            1000,  // purchaseOrderIdentifier
            null,  // processingCampusCode
            null,  // noteLine1Text
            null,  // noteLine2Text
            null,  // noteLine3Text
            false, // continuationAccountIndicator
            false, // closePurchaseOrderIndicator
            false  // reopenPurchaseOrderIndicator
            ),
    CLOSE_PO_WITH_PREQ(null,  // accountsPayableApprovalDate
            null,  // lastActionPerformedByPersonId
            null,  // accountsPayableProcessorIdentifier
            false, // holdIndicator
            null,  // extractedTimestamp
            1000,  // purchaseOrderIdentifier
            null,  // processingCampusCode
            null,  // noteLine1Text
            null,  // noteLine2Text
            null,  // noteLine3Text
            false, // continuationAccountIndicator
            true, // closePurchaseOrderIndicator
            false  // reopenPurchaseOrderIndicator
            ),
    REOPEN_PO_WITH_PREQ(null,  // accountsPayableApprovalDate
            null,  // lastActionPerformedByPersonId
            null,  // accountsPayableProcessorIdentifier
            false, // holdIndicator
            null,  // extractedTimestamp
            1000,  // purchaseOrderIdentifier
            null,  // processingCampusCode
            null,  // noteLine1Text
            null,  // noteLine2Text
            null,  // noteLine3Text
            false, // continuationAccountIndicator
            false, // closePurchaseOrderIndicator
            true  // reopenPurchaseOrderIndicator
            ),
    REQUEST_CANCEL_PREQ(null,  // accountsPayableApprovalDate
            null,  // lastActionPerformedByPersonId
            null,  // accountsPayableProcessorIdentifier
            false, // holdIndicator
            null,  // extractedTimestamp
            1000,  // purchaseOrderIdentifier
            null,  // processingCampusCode
            null,  // noteLine1Text
            null,  // noteLine2Text
            null,  // noteLine3Text
            false, // continuationAccountIndicator
            false, // closePurchaseOrderIndicator
            false  // reopenPurchaseOrderIndicator
            ),
    REQUEST_HOLD_PREQ(null,  // accountsPayableApprovalDate
            null,  // lastActionPerformedByPersonId
            null,  // accountsPayableProcessorIdentifier
            true, // holdIndicator
            null,  // extractedTimestamp
            1000,  // purchaseOrderIdentifier
            null,  // processingCampusCode
            null,  // noteLine1Text
            null,  // noteLine2Text
            null,  // noteLine3Text
            false, // continuationAccountIndicator
            false, // closePurchaseOrderIndicator
            false  // reopenPurchaseOrderIndicator
            ),

    // Credit Memo FIXTURES
    CM_ONLY_REQUIRED_FIELDS(null, // accountsPayableApprovalDate
            null, // lastActionPerformedByPersonId
            null, // accountsPayableProcessorIdentifier
            false, // holdIndicator
            null, // extractedTimestamp
            null, // purchaseOrderIdentifier
            "BL", // processingCampusCode
            null, // noteLine1Text
            null, // noteLine2Text
            null, // noteLine3Text
            false, // continuationAccountIndicator
            false, // closePurchaseOrderIndicator
            false // reopenPurchaseOrderIndicator
	);

    // SHARED FIELDS BETWEEN PAYMENT REQUEST AND CREDIT MEMO
    public final Timestamp accountsPayableApprovalDate;
    public final String lastActionPerformedByPersonId;
    public final String accountsPayableProcessorIdentifier;
    public final boolean holdIndicator;
    public final Timestamp extractedTimestamp;
    public final Integer purchaseOrderIdentifier;
    public final String processingCampusCode;
    public final String noteLine1Text;
    public final String noteLine2Text;
    public final String noteLine3Text;
    public final boolean continuationAccountIndicator;
    public final boolean closePurchaseOrderIndicator;
    public final boolean reopenPurchaseOrderIndicator;

    // TODO: decide if we need to do anything for not persisted attributes
    /*
     * private boolean unmatchedOverride; // not persisted // NOT PERSISTED IN DB // BELOW USED BY ROUTING private String
     * chartOfAccountsCode; private String organizationCode; // NOT PERSISTED IN DB // BELOW USED BY GL ENTRY CREATION private
     * boolean generateEncumbranceEntries; private String debitCreditCodeForGLEntries;
     */
    private AccountsPayableDocumentFixture(Timestamp accountsPayableApprovalDate, String lastActionPerformedByPersonId, String accountsPayableProcessorIdentifier, boolean holdIndicator, Timestamp extractedTimestamp, Integer purchaseOrderIdentifier, String processingCampusCode, String noteLine1Text, String noteLine2Text, String noteLine3Text, boolean continuationAccountIndicator, boolean closePurchaseOrderIndicator, boolean reopenPurchaseOrderIndicator) {
        this.accountsPayableApprovalDate = accountsPayableApprovalDate;
        this.lastActionPerformedByPersonId = lastActionPerformedByPersonId;
        this.accountsPayableProcessorIdentifier = accountsPayableProcessorIdentifier;
        this.holdIndicator = holdIndicator;
        this.extractedTimestamp = extractedTimestamp;
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
        this.processingCampusCode = processingCampusCode;
        this.noteLine1Text = noteLine1Text;
        this.noteLine2Text = noteLine2Text;
        this.noteLine3Text = noteLine3Text;
        this.continuationAccountIndicator = continuationAccountIndicator;
        this.closePurchaseOrderIndicator = closePurchaseOrderIndicator;
        this.reopenPurchaseOrderIndicator = reopenPurchaseOrderIndicator;
    }

    public PaymentRequestDocument createPaymentRequestDocument(PurchasingAccountsPayableDocumentFixture purapFixture) {
        return (PaymentRequestDocument) createAccountsPayableDocument(PaymentRequestDocument.class, purapFixture);
    }

    public VendorCreditMemoDocument createCreditMemoDocument(PurchasingAccountsPayableDocumentFixture purapFixture) {
        return (VendorCreditMemoDocument) createAccountsPayableDocument(VendorCreditMemoDocument.class, purapFixture);
    }

    private AccountsPayableDocument createAccountsPayableDocument(Class clazz, PurchasingAccountsPayableDocumentFixture purapFixture) {
        AccountsPayableDocument doc = (AccountsPayableDocument) purapFixture.createPurchasingAccountsPayableDocument(clazz);
        doc.setAccountsPayableApprovalTimestamp(this.accountsPayableApprovalDate);
        doc.setLastActionPerformedByPersonId(this.lastActionPerformedByPersonId);
        doc.setAccountsPayableProcessorIdentifier(this.accountsPayableProcessorIdentifier);
        doc.setHoldIndicator(this.holdIndicator);
        doc.setExtractedTimestamp(this.extractedTimestamp);
        doc.setPurchaseOrderIdentifier(this.purchaseOrderIdentifier);
        doc.setProcessingCampusCode(this.processingCampusCode);
        doc.setNoteLine1Text(this.noteLine1Text);
        doc.setNoteLine2Text(this.noteLine2Text);
        doc.setNoteLine3Text(this.noteLine3Text);
        doc.setContinuationAccountIndicator(this.continuationAccountIndicator);
        // TODO: are these needed?
        /*
         * We don't have setters for these doc.setClosePurchaseOrderIndicator(this.closePurchaseOrderIndicator);
         * doc.setReopenPurchaseOrderIndicator(this.reopenPurchaseOrderIndicator);
         */
        return doc;
    }

}

