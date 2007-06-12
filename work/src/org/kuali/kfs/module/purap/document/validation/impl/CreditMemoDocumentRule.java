/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.util.VendorUtils;



public class CreditMemoDocumentRule extends AccountsPayableDocumentRuleBase {

    /**
     * Tabs included on Payment Request Documents are:
     *   Credit Memo
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
      //  boolean valid = super.processValidation(purapDocument);
        boolean valid = processCreditMemoValidation((CreditMemoDocument)purapDocument);
        return valid;
    }
    
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = true;
        CreditMemoDocument  creditMemoDocument = (CreditMemoDocument) document;
        return isValid &= processValidation(creditMemoDocument);
    }
    
    /**
     * This method performs any validation for the Credit Memo tab.
     * 
     * @param cmDocument
     * @return
     */
    public boolean processCreditMemoValidation(CreditMemoDocument cmDocument) {
        boolean valid = true;
        //TODO code validation here
        valid &= validateCreditMemoInitTab(cmDocument);
        return valid;
    }
    
    /**
     * This method performs  validation for the Credit Memo Init tab.
     * 
     * @param cmDocument
     * @return
     */
    public boolean validateCreditMemoInitTab(CreditMemoDocument cmDocument) {
        boolean valid = true;
       
        if(!(ObjectUtils.isNotNull(cmDocument.getPaymentRequestIdentifier()) ^  StringUtils.isNotEmpty(cmDocument.getVendorNumber()) ^ 
                ObjectUtils.isNotNull(cmDocument.getPurchaseOrderIdentifier()))) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_INIT_REQUIRED_FIELDS, PurapKeyConstants.ERROR_CREDIT_MEMO_REQUIRED_FIELDS);
               valid &= false;
           }
       
        // Make sure PREQ is valid if entered
        Integer preqNumber = cmDocument.getPaymentRequestIdentifier();
        Integer poId = cmDocument.getPurchaseOrderIdentifier();
        if (ObjectUtils.isNotNull(preqNumber)) {
           
            PaymentRequestDocument preq = SpringServiceLocator.getPaymentRequestService().getPaymentRequestById(cmDocument.getPaymentRequestIdentifier());
            if (ObjectUtils.isNotNull(preq)) {
                PurchaseOrderDocument po = preq.getPurchaseOrderDocument();

                if ((PurapConstants.PaymentRequestStatuses.IN_PROCESS.equals(preq.getStatus().getStatusCode())) ||
                        (PurapConstants.PaymentRequestStatuses.CANCELLED_POST_APPROVE.equals(preq.getStatus().getStatusCode()) ||
                        (PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS.equals(preq.getStatus().getStatusCode())))) {
                    
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_PAYMENT_REQUEST_ID, PurapKeyConstants.ERROR_PAYMENT_REQEUEST_INVALID_SATATUS, preqNumber.toString());
                    
                    valid &= false;
                } 
                else {
                    cmDocument.setPaymentRequest(preq);
                    cmDocument.setPurchaseOrder(preq.getPurchaseOrderDocument());
                    cmDocument.setVendorDetail(SpringServiceLocator.getVendorService().getVendorDetail(preq.getVendorAddressGeneratedIdentifier(), preq.getVendorDetailAssignedIdentifier()));
                    cmDocument.setVendorCustomerNumber(preq.getVendorCustomerNumber());
                }
            }
            else {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_PAYMENT_REQUEST_ID, PurapKeyConstants.ERROR_PAYMENT_REQEUEST_INVALID, preqNumber.toString());
                valid &= false;
            }
        }
        // Make sure PO # is valid if entered
        if (ObjectUtils.isNotNull(poId)) {
                PurchaseOrderDocument po = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier());
                if (ObjectUtils.isNull(po)) {

                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_EXIST, poId.toString());
                    valid &= false;
                }
                else if (!(StringUtils.equals(po.getStatusCode(), PurapConstants.PurchaseOrderStatuses.OPEN)) || (StringUtils.equals(po.getStatusCode(), PurapConstants.PurchaseOrderStatuses.CLOSED))) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCAHSE_ORDER_INVALID_STATUS, poId.toString());
                    valid &= false;
                }
                else {
                    cmDocument.setPurchaseOrder(po);
                    cmDocument.setVendorDetail(po.getVendorDetail());
                    cmDocument.setVendorCustomerNumber(po.getVendorCustomerNumber());
                }

         }
         // Make sure vendorNumber is valid if entered
         if (StringUtils.isNotEmpty(cmDocument.getVendorNumber())) {
                VendorDetail vd = SpringServiceLocator.getVendorService().getVendorDetail(VendorUtils.getVendorHeaderId(cmDocument.getVendorNumber()), VendorUtils.getVendorDetailId(cmDocument.getVendorNumber()));
                if (ObjectUtils.isNotNull(vd)) {
                    cmDocument.setVendorDetail(vd);
                }
                else {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_VENDOR_NUMBER, PurapKeyConstants.ERROR_VENDOR_NUMBER_INVALID, cmDocument.getVendorNumber());
                    valid &= false;
                 }
         }
         
         return valid;
    }

}

