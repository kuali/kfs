/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/document/PurchaseOrderDocument.java,v $
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

package org.kuali.module.purap.document;

import java.util.Date;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.PaymentTermType;
import org.kuali.module.purap.bo.PurchaseOrderVendorChoice;
import org.kuali.module.purap.bo.RecurringPaymentFrequency;
import org.kuali.module.purap.bo.ShippingPaymentTerms;
import org.kuali.module.purap.bo.ShippingTitle;

/**
 * Requisition Document
 */
public class PurchaseOrderDocument extends PurchasingDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderDocument.class);

    private Date purchaseOrderCreateDate;
    private String purchaseOrderGeneralDescription;
    private Integer requisitionIdentifier;
    private String purchaseOrderVendorChoiceCode;
    private String vendorPaymentTermsCode;
    private String vendorShippingTitleCode;
    private String vendorShippingPaymentTermsCode;
    private KualiDecimal purchaseOrderTotalAmount;
    private String recurringPaymentFrequencyCode;
    private KualiDecimal recurringPaymentAmount;
    private Date recurringPaymentDate;
    private KualiDecimal initialPaymentAmount;
    private Date initialPaymentDate;
    private KualiDecimal finalPaymentAmount;
    private Date finalPaymentDate;
    private Date purchaseOrderInitialOpenDate;
    private Date purchaseOrderLastTransmitDate;
    private Date purchaseOrderQuoteDueDate;
    private String purchaseOrderQuoteTypeCode;
    private String purchaseOrderQuoteVendorNoteText;
    private boolean purchaseOrderConfirmedIndicator;
    private String purchaseOrderCommodityDescription;
    private Integer purchaseOrderPreviousIdentifier;
    private Integer alternateVendorHeaderGeneratedIdentifier;
    private Integer alternateVendorDetailAssignedIdentifier;
    private String alternateVendorName;

    private PurchaseOrderVendorChoice purchaseOrderVendorChoice;
    private PaymentTermType vendorPaymentTerms;
    private ShippingTitle vendorShippingTitle;
    private ShippingPaymentTerms vendorShippingPaymentTerms;
    private RecurringPaymentFrequency recurringPaymentFrequency;


    /**
	 * Default constructor.
	 */
	public PurchaseOrderDocument() {
        super();
    }

    public void refreshAllReferences() {
        super.refreshAllReferences();
    }
    
    /**
     * Perform logic needed to initiate PO Document
     */
    public void initiateDocument() {

    }

    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        super.handleRouteStatusChange();

        // DOCUMENT PROCESSED
        if (this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            // TODO code
        }
        // DOCUMENT DISAPPROVED
        else if (this.getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            // TODO code
        }
        // DOCUMENT CANCELED
        else if (this.getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            // TODO code
        }

    }

    @Override
    public void handleRouteLevelChange() {
        LOG.debug("handleRouteLevelChange() started");
        super.handleRouteLevelChange();


    }

    // SETTERS AND GETTERS
    public Integer getAlternateVendorDetailAssignedIdentifier() {
        return alternateVendorDetailAssignedIdentifier;
    }

    public void setAlternateVendorDetailAssignedIdentifier(Integer alternateVendorDetailAssignedIdentifier) {
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
    }

    public Integer getAlternateVendorHeaderGeneratedIdentifier() {
        return alternateVendorHeaderGeneratedIdentifier;
    }

    public void setAlternateVendorHeaderGeneratedIdentifier(Integer alternateVendorHeaderGeneratedIdentifier) {
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
    }

    public String getAlternateVendorName() {
        return alternateVendorName;
    }

    public void setAlternateVendorName(String alternateVendorName) {
        this.alternateVendorName = alternateVendorName;
    }

    public KualiDecimal getFinalPaymentAmount() {
        return finalPaymentAmount;
    }

    public void setFinalPaymentAmount(KualiDecimal finalPaymentAmount) {
        this.finalPaymentAmount = finalPaymentAmount;
    }

    public Date getFinalPaymentDate() {
        return finalPaymentDate;
    }

    public void setFinalPaymentDate(Date finalPaymentDate) {
        this.finalPaymentDate = finalPaymentDate;
    }

    public KualiDecimal getInitialPaymentAmount() {
        return initialPaymentAmount;
    }

    public void setInitialPaymentAmount(KualiDecimal initialPaymentAmount) {
        this.initialPaymentAmount = initialPaymentAmount;
    }

    public Date getInitialPaymentDate() {
        return initialPaymentDate;
    }

    public void setInitialPaymentDate(Date initialPaymentDate) {
        this.initialPaymentDate = initialPaymentDate;
    }

    public String getPurchaseOrderCommodityDescription() {
        return purchaseOrderCommodityDescription;
    }

    public void setPurchaseOrderCommodityDescription(String purchaseOrderCommodityDescription) {
        this.purchaseOrderCommodityDescription = purchaseOrderCommodityDescription;
    }

    public boolean isPurchaseOrderConfirmedIndicator() {
        return purchaseOrderConfirmedIndicator;
    }

    public void setPurchaseOrderConfirmedIndicator(boolean purchaseOrderConfirmedIndicator) {
        this.purchaseOrderConfirmedIndicator = purchaseOrderConfirmedIndicator;
    }

    public Date getPurchaseOrderCreateDate() {
        return purchaseOrderCreateDate;
    }

    public void setPurchaseOrderCreateDate(Date purchaseOrderCreateDate) {
        this.purchaseOrderCreateDate = purchaseOrderCreateDate;
    }

    public String getPurchaseOrderGeneralDescription() {
        return purchaseOrderGeneralDescription;
    }

    public void setPurchaseOrderGeneralDescription(String purchaseOrderGeneralDescription) {
        this.purchaseOrderGeneralDescription = purchaseOrderGeneralDescription;
    }

    public Date getPurchaseOrderInitialOpenDate() {
        return purchaseOrderInitialOpenDate;
    }

    public void setPurchaseOrderInitialOpenDate(Date purchaseOrderInitialOpenDate) {
        this.purchaseOrderInitialOpenDate = purchaseOrderInitialOpenDate;
    }

    public Date getPurchaseOrderLastTransmitDate() {
        return purchaseOrderLastTransmitDate;
    }

    public void setPurchaseOrderLastTransmitDate(Date purchaseOrderLastTransmitDate) {
        this.purchaseOrderLastTransmitDate = purchaseOrderLastTransmitDate;
    }

    public Integer getPurchaseOrderPreviousIdentifier() {
        return purchaseOrderPreviousIdentifier;
    }

    public void setPurchaseOrderPreviousIdentifier(Integer purchaseOrderPreviousIdentifier) {
        this.purchaseOrderPreviousIdentifier = purchaseOrderPreviousIdentifier;
    }

    public Date getPurchaseOrderQuoteDueDate() {
        return purchaseOrderQuoteDueDate;
    }

    public void setPurchaseOrderQuoteDueDate(Date purchaseOrderQuoteDueDate) {
        this.purchaseOrderQuoteDueDate = purchaseOrderQuoteDueDate;
    }

    public String getPurchaseOrderQuoteTypeCode() {
        return purchaseOrderQuoteTypeCode;
    }

    public void setPurchaseOrderQuoteTypeCode(String purchaseOrderQuoteTypeCode) {
        this.purchaseOrderQuoteTypeCode = purchaseOrderQuoteTypeCode;
    }

    public String getPurchaseOrderQuoteVendorNoteText() {
        return purchaseOrderQuoteVendorNoteText;
    }

    public void setPurchaseOrderQuoteVendorNoteText(String purchaseOrderQuoteVendorNoteText) {
        this.purchaseOrderQuoteVendorNoteText = purchaseOrderQuoteVendorNoteText;
    }

    public KualiDecimal getPurchaseOrderTotalAmount() {
        return purchaseOrderTotalAmount;
    }

    public void setPurchaseOrderTotalAmount(KualiDecimal purchaseOrderTotalAmount) {
        this.purchaseOrderTotalAmount = purchaseOrderTotalAmount;
    }

    public String getPurchaseOrderVendorChoiceCode() {
        return purchaseOrderVendorChoiceCode;
    }

    public void setPurchaseOrderVendorChoiceCode(String purchaseOrderVendorChoiceCode) {
        this.purchaseOrderVendorChoiceCode = purchaseOrderVendorChoiceCode;
    }

    public KualiDecimal getRecurringPaymentAmount() {
        return recurringPaymentAmount;
    }

    public void setRecurringPaymentAmount(KualiDecimal recurringPaymentAmount) {
        this.recurringPaymentAmount = recurringPaymentAmount;
    }

    public Date getRecurringPaymentDate() {
        return recurringPaymentDate;
    }

    public void setRecurringPaymentDate(Date recurringPaymentDate) {
        this.recurringPaymentDate = recurringPaymentDate;
    }

    public String getRecurringPaymentFrequencyCode() {
        return recurringPaymentFrequencyCode;
    }

    public void setRecurringPaymentFrequencyCode(String recurringPaymentFrequencyCode) {
        this.recurringPaymentFrequencyCode = recurringPaymentFrequencyCode;
    }

    public Integer getRequisitionIdentifier() {
        return requisitionIdentifier;
    }

    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }

    public String getVendorPaymentTermsCode() {
        return vendorPaymentTermsCode;
    }

    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
    }

    public String getVendorShippingPaymentTermsCode() {
        return vendorShippingPaymentTermsCode;
    }

    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }

    public String getVendorShippingTitleCode() {
        return vendorShippingTitleCode;
    }

    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
    }

    public PurchaseOrderVendorChoice getPurchaseOrderVendorChoice() {
        return purchaseOrderVendorChoice;
    }

    public void setPurchaseOrderVendorChoice(PurchaseOrderVendorChoice purchaseOrderVendorChoice) {
        this.purchaseOrderVendorChoice = purchaseOrderVendorChoice;
    }

    public RecurringPaymentFrequency getRecurringPaymentFrequency() {
        return recurringPaymentFrequency;
    }

    public void setRecurringPaymentFrequency(RecurringPaymentFrequency recurringPaymentFrequency) {
        this.recurringPaymentFrequency = recurringPaymentFrequency;
    }

    public PaymentTermType getVendorPaymentTerms() {
        return vendorPaymentTerms;
    }

    public void setVendorPaymentTerms(PaymentTermType vendorPaymentTerms) {
        this.vendorPaymentTerms = vendorPaymentTerms;
    }

    public ShippingPaymentTerms getVendorShippingPaymentTerms() {
        return vendorShippingPaymentTerms;
    }

    public void setVendorShippingPaymentTerms(ShippingPaymentTerms vendorShippingPaymentTerms) {
        this.vendorShippingPaymentTerms = vendorShippingPaymentTerms;
    }

    public ShippingTitle getVendorShippingTitle() {
        return vendorShippingTitle;
    }

    public void setVendorShippingTitle(ShippingTitle vendorShippingTitle) {
        this.vendorShippingTitle = vendorShippingTitle;
    }

}
