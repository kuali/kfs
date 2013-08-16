/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.math.BigDecimal;
import java.util.List;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestView;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

public class VendorCreditMemoPurchaseOrderForInvoicedItemsValidation extends GenericValidation {

    private PurchaseOrderService purchaseOrderService;
    private CreditMemoService creditMemoService;
    private PaymentRequestService paymentRequestService;

    /**
     * Verifies the purchase order for the credit memo has at least one invoiced item. If no invoiced items are found, a credit memo
     * cannot be processed against the document.
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;
        VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument) event.getDocument();

        if(cmDocument.isSourceDocumentPurchaseOrder()){
            GlobalVariables.getMessageMap().clearErrorPath();
            GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

            PurchaseOrderDocument poDocument = purchaseOrderService.getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier());

            KualiDecimal poTotalApprovedAmount = calculateTotalDollarAmountForRelatedValidPreqs(poDocument);
            if(calculateTotalAmountForCreditMemo(cmDocument).isGreaterThan(poTotalApprovedAmount)) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEMS, PurapKeyConstants.ERROR_CREDIT_MEMO_TOTAL_GREATERTHAN_APPROVED_PO_TOTAL, poTotalApprovedAmount.toString());
                isValid = false;
            }


            GlobalVariables.getMessageMap().clearErrorPath();
        }

        return isValid;
    }

    protected KualiDecimal calculateTotalAmountForCreditMemo(VendorCreditMemoDocument cmDocument) {
        KualiDecimal cmAmount = new KualiDecimal(BigDecimal.ZERO);

        // At credit memo initiation screen, the Credit Memo Amount can be used.  Once
        // past the initiation screen, the Credit Memo Amount doesn't get updated if the
        // item amounts are adjusted so the Total Dollar Amount is used.
        if (cmDocument.getItems().isEmpty()) {
            cmAmount = cmDocument.getCreditMemoAmount();
        }
        else {
            // Get pretax amount if it is use tax otherwise it's sales tax so get total
            // dollar amount, including tax.
            if (cmDocument.isUseTaxIndicator()) {
                cmAmount = cmDocument.getTotalPreTaxDollarAmount();
            }
            else {
                cmAmount = cmDocument.getTotalDollarAmount();
            }
        }

        return cmAmount == null ? KualiDecimal.ZERO : cmAmount;
    }

    protected boolean isPaymentRequestDocumentInValidStateForCreditMemo(PaymentRequestDocument preq) {
        return preq.getDocumentHeader().getWorkflowDocument().isApproved();
    }

    protected KualiDecimal calculateTotalDollarAmountForRelatedValidPreqs(PurchaseOrderDocument poDocument) {
        KualiDecimal dollarAmount = new KualiDecimal(BigDecimal.ZERO);

        List<PaymentRequestView> preqViews = poDocument.getRelatedViews().getRelatedPaymentRequestViews();
        for (PaymentRequestView preqView : preqViews) {
            PaymentRequestDocument preqDocument = paymentRequestService.getPaymentRequestById(preqView.getPurapDocumentIdentifier());
            if (isPaymentRequestDocumentInValidStateForCreditMemo(preqDocument)) {
                // Get pretax amount if it is use tax otherwise it's sales tax so get
                // total dollar amount, including tax.
                if (preqDocument.isUseTaxIndicator()) {
                    dollarAmount = dollarAmount.add(preqDocument.getTotalPreTaxDollarAmount());
                }
                else {
                    dollarAmount = dollarAmount.add(preqDocument.getTotalDollarAmount());
                }
            }
        }
        return dollarAmount == null ? KualiDecimal.ZERO : dollarAmount;
    }

    public PurchaseOrderService getPurchaseOrderService() {
        return purchaseOrderService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public CreditMemoService getCreditMemoService() {
        return creditMemoService;
    }

    public void setCreditMemoService(CreditMemoService creditMemoService) {
        this.creditMemoService = creditMemoService;
    }

    public PaymentRequestService getPaymentRequestService() {
        return paymentRequestService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

}
