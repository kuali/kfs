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

import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;


public enum AccountsPayableDocumentFixture {

    // PAYMENT REQUEST FIXTURES
    PREQ_ONLY_REQUIRED_FIELDS(null,  // accountsPayableApprovalDate
            null,  // lastActionPerformedByUniversalUserId
            null,  // accountsPayableProcessorIdentifier
            false, // holdIndicator
            null,  // extractedDate
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
            null, // lastActionPerformedByUniversalUserId
            null, // accountsPayableProcessorIdentifier
            false, // holdIndicator
            null, // extractedDate
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
    public final Date accountsPayableApprovalDate;
    public final String lastActionPerformedByUniversalUserId;
    public final String accountsPayableProcessorIdentifier;
    public final boolean holdIndicator;
    public final Date extractedDate;
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
    private AccountsPayableDocumentFixture(Date accountsPayableApprovalDate, String lastActionPerformedByUniversalUserId, String accountsPayableProcessorIdentifier, boolean holdIndicator, Date extractedDate, Integer purchaseOrderIdentifier, String processingCampusCode, String noteLine1Text, String noteLine2Text, String noteLine3Text, boolean continuationAccountIndicator, boolean closePurchaseOrderIndicator, boolean reopenPurchaseOrderIndicator) {
        this.accountsPayableApprovalDate = accountsPayableApprovalDate;
        this.lastActionPerformedByUniversalUserId = lastActionPerformedByUniversalUserId;
        this.accountsPayableProcessorIdentifier = accountsPayableProcessorIdentifier;
        this.holdIndicator = holdIndicator;
        this.extractedDate = extractedDate;
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

    public CreditMemoDocument createCreditMemoDocument(PurchasingAccountsPayableDocumentFixture purapFixture) {
        return (CreditMemoDocument) createAccountsPayableDocument(CreditMemoDocument.class, purapFixture);
    }

    private AccountsPayableDocument createAccountsPayableDocument(Class clazz, PurchasingAccountsPayableDocumentFixture purapFixture) {
        AccountsPayableDocument doc = (AccountsPayableDocument) purapFixture.createPurchasingAccountsPayableDocument(clazz);
        doc.setAccountsPayableApprovalDate(this.accountsPayableApprovalDate);
        doc.setLastActionPerformedByUniversalUserId(this.lastActionPerformedByUniversalUserId);
        doc.setAccountsPayableProcessorIdentifier(this.accountsPayableProcessorIdentifier);
        doc.setHoldIndicator(this.holdIndicator);
        doc.setExtractedDate(this.extractedDate);
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
