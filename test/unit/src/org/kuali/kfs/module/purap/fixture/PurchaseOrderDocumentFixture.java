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

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.fixtures.PurapTestConstants.PO;

/**
 * Fixture class for Purchase Order Document.
 */
public enum PurchaseOrderDocumentFixture {

    // TODO f2f: fix the REQ id
    
    PO_ONLY_REQUIRED_FIELDS(
            PO.CREATE_DATE, // purchaseOrderCreateDate
            PO.REQ_ID, // requisitionIdentifier
            "LPRC", // purchaseOrderVendorChoiceCode
            null, // recurringPaymentFrequencyCode
            null, // recurringPaymentAmount
            null, // recurringPaymentDate
            null, // initialPaymentAmount
            null, // initialPaymentDate
            null, // finalPaymentAmount
            null, // finalPaymentDate
            null, // purchaseOrderInitialOpenDate
            null, // purchaseOrderLastTransmitDate
            null, // purchaseOrderQuoteDueDate
            null, // purchaseOrderQuoteTypeCode
            null, // purchaseOrderQuoteVendorNoteText
            false, // purchaseOrderConfirmedIndicator
            null, // purchaseOrderCommodityDescription
            null, // purchaseOrderPreviousIdentifier
            null, // alternateVendorHeaderGeneratedIdentifier
            null, // alternateVendorDetailAssignedIdentifier
            null, // newQuoteVendorHeaderGeneratedIdentifier
            null, // newQuoteVendorDetailAssignedIdentifier
            null, // alternateVendorName
            true, // purchaseOrderCurrentIndicator
            false, // pendingActionIndicator
            null, // purchaseOrderFirstTransmissionDate
            PurchasingAccountsPayableDocumentFixture.PO_ONLY_REQUIRED_FIELDS, // purapDocumentFixture
            PurchasingDocumentFixture.PO_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new PurchaseOrderItemFixture[] { // purchaseOrderItemMultiFixtures
                    PurchaseOrderItemFixture.PO_QTY_UNRESTRICTED_ITEM_1 }
    ),             
    PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS(
            PO.CREATE_DATE, // purchaseOrderCreateDate
            PO.REQ_ID, // requisitionIdentifier
            "LPRC", // purchaseOrderVendorChoiceCode
            null, // recurringPaymentFrequencyCode
            null, // recurringPaymentAmount
            null, // recurringPaymentDate
            null, // initialPaymentAmount
            null, // initialPaymentDate
            null, // finalPaymentAmount
            null, // finalPaymentDate
            null, // purchaseOrderInitialOpenDate
            null, // purchaseOrderLastTransmitDate
            null, // purchaseOrderQuoteDueDate
            null, // purchaseOrderQuoteTypeCode
            null, // purchaseOrderQuoteVendorNoteText
            false, // purchaseOrderConfirmedIndicator
            null, // purchaseOrderCommodityDescription
            null, // purchaseOrderPreviousIdentifier
            null, // alternateVendorHeaderGeneratedIdentifier
            null, // alternateVendorDetailAssignedIdentifier
            null, // newQuoteVendorHeaderGeneratedIdentifier
            null, // newQuoteVendorDetailAssignedIdentifier
            null, // alternateVendorName
            true, // purchaseOrderCurrentIndicator
            false, // pendingActionIndicator
            null, // purchaseOrderFirstTransmissionDate
            PurchasingAccountsPayableDocumentFixture.PO_ONLY_REQUIRED_FIELDS, // purapDocumentFixture
            PurchasingDocumentFixture.PO_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new PurchaseOrderItemFixture[] { // purchaseOrderItemMultiFixtures
                    PurchaseOrderItemFixture.PO_QTY_UNRESTRICTED_ITEM_1, 
                    PurchaseOrderItemFixture.PO_QTY_UNRESTRICTED_ITEM_2 }
    );    
    
    public final Date purchaseOrderCreateDate;
    public final Integer requisitionIdentifier;
    public final String purchaseOrderVendorChoiceCode;
    public final String recurringPaymentFrequencyCode;
    public final KualiDecimal recurringPaymentAmount;
    public final Date recurringPaymentDate;
    public final KualiDecimal initialPaymentAmount;
    public final Date initialPaymentDate;
    public final KualiDecimal finalPaymentAmount;
    public final Date finalPaymentDate;
    public final Date purchaseOrderInitialOpenDate;
    public final Date purchaseOrderLastTransmitDate;
    public final Date purchaseOrderQuoteDueDate;
    public final String purchaseOrderQuoteTypeCode;
    public final String purchaseOrderQuoteVendorNoteText;
    public final boolean purchaseOrderConfirmedIndicator;
    public final String purchaseOrderCommodityDescription;
    public final Integer purchaseOrderPreviousIdentifier;
    public final Integer alternateVendorHeaderGeneratedIdentifier;
    public final Integer alternateVendorDetailAssignedIdentifier;
    public final Integer newQuoteVendorHeaderGeneratedIdentifier;
    public final Integer newQuoteVendorDetailAssignedIdentifier;
    public final String alternateVendorName;
    public final boolean purchaseOrderCurrentIndicator;
    public final boolean pendingActionIndicator;
    public final Date purchaseOrderFirstTransmissionDate;
    private PurchasingAccountsPayableDocumentFixture purapDocumentFixture;
    private PurchasingDocumentFixture purchasingDocumentFixture;
    private PurchaseOrderItemFixture[] purchaseOrderItemFixtures;

    /**
     * Private Constructor.
     */
    private PurchaseOrderDocumentFixture(Date purchaseOrderCreateDate, Integer requisitionIdentifier, String purchaseOrderVendorChoiceCode, String recurringPaymentFrequencyCode, KualiDecimal recurringPaymentAmount, Date recurringPaymentDate, KualiDecimal initialPaymentAmount, Date initialPaymentDate, KualiDecimal finalPaymentAmount, Date finalPaymentDate, Date purchaseOrderInitialOpenDate, Date purchaseOrderLastTransmitDate, Date purchaseOrderQuoteDueDate, String purchaseOrderQuoteTypeCode, String purchaseOrderQuoteVendorNoteText, boolean purchaseOrderConfirmedIndicator, String purchaseOrderCommodityDescription, Integer purchaseOrderPreviousIdentifier, Integer alternateVendorHeaderGeneratedIdentifier, Integer alternateVendorDetailAssignedIdentifier, Integer newQuoteVendorHeaderGeneratedIdentifier, Integer newQuoteVendorDetailAssignedIdentifier, String alternateVendorName, boolean purchaseOrderCurrentIndicator, boolean pendingActionIndicator, Date purchaseOrderFirstTransmissionDate,
            PurchasingAccountsPayableDocumentFixture purapDocumentFixture, PurchasingDocumentFixture purchasingDocumentFixture, PurchaseOrderItemFixture[] purchaseOrderItemFixtures) {
        this.purchaseOrderCreateDate = purchaseOrderCreateDate;
        this.requisitionIdentifier = requisitionIdentifier;
        this.purchaseOrderVendorChoiceCode = purchaseOrderVendorChoiceCode;
        this.recurringPaymentFrequencyCode = recurringPaymentFrequencyCode;
        this.recurringPaymentAmount = recurringPaymentAmount;
        this.recurringPaymentDate = recurringPaymentDate;
        this.initialPaymentAmount = initialPaymentAmount;
        this.initialPaymentDate = initialPaymentDate;
        this.finalPaymentAmount = finalPaymentAmount;
        this.finalPaymentDate = finalPaymentDate;
        this.purchaseOrderInitialOpenDate = purchaseOrderInitialOpenDate;
        this.purchaseOrderLastTransmitDate = purchaseOrderLastTransmitDate;
        this.purchaseOrderQuoteDueDate = purchaseOrderQuoteDueDate;
        this.purchaseOrderQuoteTypeCode = purchaseOrderQuoteTypeCode;
        this.purchaseOrderQuoteVendorNoteText = purchaseOrderQuoteVendorNoteText;
        this.purchaseOrderConfirmedIndicator = purchaseOrderConfirmedIndicator;
        this.purchaseOrderCommodityDescription = purchaseOrderCommodityDescription;
        this.purchaseOrderPreviousIdentifier = purchaseOrderPreviousIdentifier;
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
        this.newQuoteVendorHeaderGeneratedIdentifier = newQuoteVendorHeaderGeneratedIdentifier;
        this.newQuoteVendorDetailAssignedIdentifier = newQuoteVendorDetailAssignedIdentifier;
        this.alternateVendorName = alternateVendorName;
        this.purchaseOrderCurrentIndicator = purchaseOrderCurrentIndicator;
        this.pendingActionIndicator = pendingActionIndicator;
        this.purchaseOrderFirstTransmissionDate = purchaseOrderFirstTransmissionDate;
        this.purapDocumentFixture = purapDocumentFixture;
        this.purchasingDocumentFixture = purchasingDocumentFixture;
        this.purchaseOrderItemFixtures = purchaseOrderItemFixtures;
    }

    /**
     * Creates a Purchase Order Document using this Fixture.
     * 
     * @return the created Purchase Order Document.
     */
    public PurchaseOrderDocument createPurchaseOrderDocument() {
        PurchaseOrderDocument doc = purchasingDocumentFixture.createPurchaseOrderDocument(purapDocumentFixture);
        doc.setPurchaseOrderCreateDate(this.purchaseOrderCreateDate);
        doc.setRequisitionIdentifier(this.requisitionIdentifier);
        doc.setPurchaseOrderVendorChoiceCode(this.purchaseOrderVendorChoiceCode);
        doc.setRecurringPaymentFrequencyCode(this.recurringPaymentFrequencyCode);
        doc.setRecurringPaymentAmount(this.recurringPaymentAmount);
        doc.setRecurringPaymentDate(this.recurringPaymentDate);
        doc.setInitialPaymentAmount(this.initialPaymentAmount);
        doc.setInitialPaymentDate(this.initialPaymentDate);
        doc.setFinalPaymentAmount(this.finalPaymentAmount);
        doc.setFinalPaymentDate(this.finalPaymentDate);
        doc.setPurchaseOrderInitialOpenDate(this.purchaseOrderInitialOpenDate);
        doc.setPurchaseOrderLastTransmitDate(this.purchaseOrderLastTransmitDate);
        doc.setPurchaseOrderQuoteDueDate(this.purchaseOrderQuoteDueDate);
        doc.setPurchaseOrderQuoteTypeCode(this.purchaseOrderQuoteTypeCode);
        doc.setPurchaseOrderQuoteVendorNoteText(this.purchaseOrderQuoteVendorNoteText);
        doc.setPurchaseOrderConfirmedIndicator(this.purchaseOrderConfirmedIndicator);
        doc.setPurchaseOrderCommodityDescription(this.purchaseOrderCommodityDescription);
        doc.setPurchaseOrderPreviousIdentifier(this.purchaseOrderPreviousIdentifier);
        doc.setAlternateVendorHeaderGeneratedIdentifier(this.alternateVendorHeaderGeneratedIdentifier);
        doc.setAlternateVendorDetailAssignedIdentifier(this.alternateVendorDetailAssignedIdentifier);
        doc.setNewQuoteVendorHeaderGeneratedIdentifier(this.newQuoteVendorHeaderGeneratedIdentifier);
        doc.setNewQuoteVendorDetailAssignedIdentifier(this.newQuoteVendorDetailAssignedIdentifier);
        doc.setAlternateVendorName(this.alternateVendorName);
        doc.setPurchaseOrderCurrentIndicator(this.purchaseOrderCurrentIndicator);
        doc.setPendingActionIndicator(this.pendingActionIndicator);
        doc.setPurchaseOrderFirstTransmissionDate(this.purchaseOrderFirstTransmissionDate);

        for (PurchaseOrderItemFixture purchaseOrderItemFixture : purchaseOrderItemFixtures) {
            purchaseOrderItemFixture.addTo(doc);
        }

        return doc;
    }
}
