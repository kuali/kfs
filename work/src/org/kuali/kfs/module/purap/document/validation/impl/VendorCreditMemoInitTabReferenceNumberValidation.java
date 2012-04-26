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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.VendorUtils;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class VendorCreditMemoInitTabReferenceNumberValidation extends GenericValidation {

    private PaymentRequestService paymentRequestService;
    private PurapService purapService;
    private PurchaseOrderService purchaseOrderService;
    private VendorService vendorService;
    
    /**
     *  Validates only one of preq, po, or vendor number was given. Then validates the existence of that number.
     */ 
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument) event.getDocument();

        if (!(ObjectUtils.isNotNull(cmDocument.getPaymentRequestIdentifier()) ^ StringUtils.isNotEmpty(cmDocument.getVendorNumber()) ^ ObjectUtils.isNotNull(cmDocument.getPurchaseOrderIdentifier())) || (ObjectUtils.isNotNull(cmDocument.getPaymentRequestIdentifier()) && StringUtils.isNotEmpty(cmDocument.getVendorNumber()) && ObjectUtils.isNotNull(cmDocument.getPurchaseOrderIdentifier()))) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_CREDIT_MEMO_REQUIRED_FIELDS);
            valid = false;
        }
        else {
            // Make sure PREQ is valid if entered
            Integer preqNumber = cmDocument.getPaymentRequestIdentifier();
            if (ObjectUtils.isNotNull(preqNumber)) {
                PaymentRequestDocument preq = paymentRequestService.getPaymentRequestById(preqNumber);
                if (ObjectUtils.isNull(preq)) {
                    GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(PurapPropertyConstants.PAYMENT_REQUEST_ID, PurapKeyConstants.ERROR_CREDIT_MEMO_PAYMENT_REQEUEST_INVALID, preqNumber.toString());
                    valid = false;
                }
// RICE20 : !purapService.isFullDocumentEntryCompleted(preq) ||
                else if ((PurapConstants.PaymentRequestStatuses.APPDOC_IN_PROCESS.equals(preq.getApplicationDocumentStatus())) || (PurapConstants.PaymentRequestStatuses.CANCELLED_STATUSES.contains(preq.getApplicationDocumentStatus()))) {
                    GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(PurapPropertyConstants.PAYMENT_REQUEST_ID, PurapKeyConstants.ERROR_CREDIT_MEMO_PAYMENT_REQEUEST_INVALID_SATATUS, preqNumber.toString());
                    valid = false;
                }
            }

            // Make sure PO # is valid if entered
            Integer purchaseOrderID = cmDocument.getPurchaseOrderIdentifier();
            if (ObjectUtils.isNotNull(purchaseOrderID)) {
                PurchaseOrderDocument purchaseOrder = purchaseOrderService.getCurrentPurchaseOrder(purchaseOrderID);
                if (ObjectUtils.isNull(purchaseOrder)) {
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_CREDIT_MEMO_PURCHASE_ORDER_INVALID, purchaseOrderID.toString());
                    valid = false;
                }
                else if (purchaseOrder.isPendingActionIndicator()) {
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_PENDING_ACTION);
                    valid &= false;
                }
                else if (!(StringUtils.equals(purchaseOrder.getApplicationDocumentStatus(), PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN) || StringUtils.equals(purchaseOrder.getApplicationDocumentStatus(), PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED))) {
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_CREDIT_MEMO_PURCAHSE_ORDER_INVALID_STATUS, purchaseOrderID.toString());
                    valid = false;
                }
            }

            // Make sure vendorNumber is valid if entered
            String vendorNumber = cmDocument.getVendorNumber();
            if (StringUtils.isNotEmpty(vendorNumber)) {
                VendorDetail vendor = vendorService.getVendorDetail(VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber));
                if (ObjectUtils.isNull(vendor)) {
                    GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(PurapPropertyConstants.VENDOR_NUMBER, PurapKeyConstants.ERROR_CREDIT_MEMO_VENDOR_NUMBER_INVALID, vendorNumber);
                    valid = false;
                }
            }
        }
        return valid;
    }

    public PaymentRequestService getPaymentRequestService() {
        return paymentRequestService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    public PurchaseOrderService getPurchaseOrderService() {
        return purchaseOrderService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public VendorService getVendorService() {
        return vendorService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

}
