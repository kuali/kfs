/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.VendorUtils;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class VendorCreditMemoContinueValidation extends GenericValidation {

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;

        VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument) event.getDocument();
        valid = validateInitTabRequiredFields(cmDocument);

        if (valid) {
            valid = validateInitTabReferenceNumbers(cmDocument);
        }

        if (valid && cmDocument.isSourceDocumentPurchaseOrder()) {
            valid = checkPurchaseOrderForInvoicedItems(cmDocument);
        }

        return valid;
    }

    /**
     * Validates the necessary fields on the init tab were given and credit memo date is valid. (NOTE: formats for cm date and
     * number already performed by pojo conversion)
     * 
     * @param cmDocument - credit memo document which contains the fields that need checked
     * @return boolean - true if validation was ok, false if there were errors
     */
    protected boolean validateInitTabRequiredFields(VendorCreditMemoDocument cmDocument) {
        boolean valid = true;

        valid = validateRequiredField(cmDocument, PurapPropertyConstants.CREDIT_MEMO_NUMBER);
        valid = valid && validateRequiredField(cmDocument, PurapPropertyConstants.CREDIT_MEMO_AMOUNT);
        boolean creditMemoDateExist = validateRequiredField(cmDocument, PurapPropertyConstants.CREDIT_MEMO_DATE);

        if (creditMemoDateExist) {
            if (SpringContext.getBean(PaymentRequestService.class).isInvoiceDateAfterToday(cmDocument.getCreditMemoDate())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(VendorCreditMemoDocument.class, PurapPropertyConstants.CREDIT_MEMO_DATE);
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_DATE, PurapKeyConstants.ERROR_INVALID_INVOICE_DATE, label);
                valid = false;
            }
        }

        return valid;

    }

    /**
     * Validates only one of preq, po, or vendor number was given. Then validates the existence of that number.
     * 
     * @param cmDocument - credit memo document which contains init reference numbers
     * @return boolean - true if validation was ok, false if there were errors
     */
    protected boolean validateInitTabReferenceNumbers(VendorCreditMemoDocument cmDocument) {
        boolean valid = true;
        // GlobalVariables.getErrorMap().clearErrorPath();
        // GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants..DOCUMENT);

        if (!(ObjectUtils.isNotNull(cmDocument.getPaymentRequestIdentifier()) ^ StringUtils.isNotEmpty(cmDocument.getVendorNumber()) ^ ObjectUtils.isNotNull(cmDocument.getPurchaseOrderIdentifier())) || (ObjectUtils.isNotNull(cmDocument.getPaymentRequestIdentifier()) && StringUtils.isNotEmpty(cmDocument.getVendorNumber()) && ObjectUtils.isNotNull(cmDocument.getPurchaseOrderIdentifier()))) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_CREDIT_MEMO_REQUIRED_FIELDS);
            valid = false;
        }
        else {
            // Make sure PREQ is valid if entered
            Integer preqNumber = cmDocument.getPaymentRequestIdentifier();
            if (ObjectUtils.isNotNull(preqNumber)) {
                PaymentRequestDocument preq = SpringContext.getBean(PaymentRequestService.class).getPaymentRequestById(preqNumber);
                if (ObjectUtils.isNull(preq)) {
                    GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(PurapPropertyConstants.PAYMENT_REQUEST_ID, PurapKeyConstants.ERROR_CREDIT_MEMO_PAYMENT_REQEUEST_INVALID, preqNumber.toString());
                    valid = false;
                }
                else if ((PurapConstants.PaymentRequestStatuses.IN_PROCESS.equals(preq.getStatusCode())) || (PurapConstants.PaymentRequestStatuses.CANCELLED_STATUSES.contains(preq.getStatusCode()))) {
                    GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(PurapPropertyConstants.PAYMENT_REQUEST_ID, PurapKeyConstants.ERROR_CREDIT_MEMO_PAYMENT_REQEUEST_INVALID_SATATUS, preqNumber.toString());
                    valid = false;
                }
            }

            // Make sure PO # is valid if entered
            Integer purchaseOrderID = cmDocument.getPurchaseOrderIdentifier();
            if (ObjectUtils.isNotNull(purchaseOrderID)) {
                PurchaseOrderDocument purchaseOrder = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(purchaseOrderID);
                if (ObjectUtils.isNull(purchaseOrder)) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_CREDIT_MEMO_PURCHASE_ORDER_INVALID, purchaseOrderID.toString());
                    valid = false;
                }
                else if (purchaseOrder.isPendingActionIndicator()) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_PENDING_ACTION);
                    valid &= false;
                }
                else if (!(StringUtils.equals(purchaseOrder.getStatusCode(), PurapConstants.PurchaseOrderStatuses.OPEN) || StringUtils.equals(purchaseOrder.getStatusCode(), PurapConstants.PurchaseOrderStatuses.CLOSED))) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_CREDIT_MEMO_PURCAHSE_ORDER_INVALID_STATUS, purchaseOrderID.toString());
                    valid = false;
                }
            }

            // Make sure vendorNumber is valid if entered
            String vendorNumber = cmDocument.getVendorNumber();
            if (StringUtils.isNotEmpty(vendorNumber)) {
                VendorDetail vendor = SpringContext.getBean(VendorService.class).getVendorDetail(VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber));
                if (ObjectUtils.isNull(vendor)) {
                    GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(PurapPropertyConstants.VENDOR_NUMBER, PurapKeyConstants.ERROR_CREDIT_MEMO_VENDOR_NUMBER_INVALID, vendorNumber);
                    valid = false;
                }
            }
        }
        // GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

    /**
     * Verifies the purchase order for the credit memo has at least one invoiced item. If no invoiced items are found, a credit memo
     * cannot be processed against the document.
     * 
     * @param cmDocument - credit memo document which contains the po reference
     * @return boolean - true if validation was ok, false if there were errors
     */
    protected boolean checkPurchaseOrderForInvoicedItems(VendorCreditMemoDocument cmDocument) {
        boolean hasInvoicedItems = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        PurchaseOrderDocument poDocument = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier());
        List<PurchaseOrderItem> invoicedItems = SpringContext.getBean(CreditMemoService.class).getPOInvoicedItems(poDocument);

        if (invoicedItems == null || invoicedItems.isEmpty()) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_CREDIT_MEMO_PURCAHSE_ORDER_NOITEMS);
            hasInvoicedItems = false;
        }

        GlobalVariables.getErrorMap().clearErrorPath();
        return hasInvoicedItems;
    }

    /**
     * Helper method to perform required field checks add error messages if the validation fails. Adds an error required to
     * GlobalVariables.errorMap using the given fieldName as the error key and retrieving the error label from the data dictionary
     * for the error required message param.
     * 
     * @param businessObject - Business object to check for value
     * @param fieldName - Name of the property in the business object
     */
    private boolean validateRequiredField(BusinessObject businessObject, String fieldName) {
        boolean valid = true;

        Object fieldValue = ObjectUtils.getPropertyValue(businessObject, fieldName);
        if (fieldValue == null || (fieldValue instanceof String && StringUtils.isBlank(fieldName))) {
            String label = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(businessObject.getClass(), fieldName);
            GlobalVariables.getErrorMap().putError(fieldName, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        return valid;
    }

}
