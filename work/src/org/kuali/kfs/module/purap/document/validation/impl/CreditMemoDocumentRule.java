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
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.rule.ContinueAccountsPayableRule;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.util.VendorUtils;



public class CreditMemoDocumentRule extends AccountsPayableDocumentRuleBase implements ContinueAccountsPayableRule {

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
    
    public boolean processContinueAccountsPayableBusinessRules(AccountsPayableDocument document) {
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
                    cmDocument.setVendorHeaderGeneratedIdentifier(preq.getVendorHeaderGeneratedIdentifier());
                    cmDocument.setVendorDetailAssignedIdentifier(preq.getVendorDetailAssignedIdentifier());
                    cmDocument.setVendorAddressGeneratedIdentifier(preq.getVendorAddressGeneratedIdentifier());
                    cmDocument.setVendorDetailAssignedIdentifier(preq.getVendorDetailAssignedIdentifier());
                    cmDocument.setVendorCustomerNumber(preq.getVendorCustomerNumber());
                    
                    cmDocument.setVendorCustomerNumber(preq.getVendorCustomerNumber());
                    cmDocument.setVendorHeaderGeneratedIdentifier(preq.getVendorHeaderGeneratedIdentifier());
                    cmDocument.setVendorDetailAssignedIdentifier(preq.getVendorDetailAssignedIdentifier());
                    cmDocument.setVendorName(preq.getVendorName());
                    cmDocument.setVendorLine1Address(preq.getVendorLine1Address());
                    cmDocument.setVendorLine2Address(preq.getVendorLine2Address());
                    cmDocument.setVendorCityName(preq.getVendorCityName());
                    cmDocument.setVendorStateCode(preq.getVendorStateCode());
                    cmDocument.setVendorPostalCode(preq.getVendorPostalCode());
                    cmDocument.setVendorCountryCode(preq.getVendorCountryCode());
                    // Is this needed or not?:
                    cmDocument.setVendorDetail(SpringServiceLocator.getVendorService().getVendorDetail(preq.getVendorAddressGeneratedIdentifier(), preq.getVendorDetailAssignedIdentifier()));
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
                    cmDocument.setVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
                    cmDocument.setVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());
                    cmDocument.setVendorAddressGeneratedIdentifier(po.getVendorAddressGeneratedIdentifier());
                    cmDocument.setVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());
                    cmDocument.setVendorCustomerNumber(po.getVendorCustomerNumber());
                    
                    cmDocument.setVendorCustomerNumber(po.getVendorCustomerNumber());
                    cmDocument.setVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
                    cmDocument.setVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());
                    cmDocument.setVendorName(po.getVendorName());
                    cmDocument.setVendorLine1Address(po.getVendorLine1Address());
                    cmDocument.setVendorLine2Address(po.getVendorLine2Address());
                    cmDocument.setVendorCityName(po.getVendorCityName());
                    cmDocument.setVendorStateCode(po.getVendorStateCode());
                    cmDocument.setVendorPostalCode(po.getVendorPostalCode());
                    cmDocument.setVendorCountryCode(po.getVendorCountryCode());
                    // Is this needed or not?:
                    cmDocument.setVendorDetail(po.getVendorDetail());
                }

         }
         // Make sure vendorNumber is valid if entered
         if (StringUtils.isNotEmpty(cmDocument.getVendorNumber())) {
           
            VendorDetail vd = SpringServiceLocator.getVendorService().getVendorDetail(VendorUtils.getVendorHeaderId(cmDocument.getVendorNumber()), VendorUtils.getVendorDetailId(cmDocument.getVendorNumber()));
            if (ObjectUtils.isNotNull(vd)) {
                cmDocument.setVendorDetail(vd);
                cmDocument.setVendorHeaderGeneratedIdentifier(vd.getVendorHeaderGeneratedIdentifier());
                cmDocument.setVendorDetailAssignedIdentifier(vd.getVendorDetailAssignedIdentifier());
               // cmDocument.setVendorAddressGeneratedIdentifier(VendorUtils.getVendorHeaderId(cmDocument.getVendorNumber()));
               // cmDocument.setVendorDetailAssignedIdentifier(VendorUtils.getVendorDetailId(cmDocument.getVendorNumber()));
                cmDocument.setVendorCustomerNumber(vd.getVendorNumber());
                cmDocument.setVendorHeaderGeneratedIdentifier(vd.getVendorHeaderGeneratedIdentifier());
                cmDocument.setVendorDetailAssignedIdentifier(vd.getVendorDetailAssignedIdentifier());
                cmDocument.setVendorName(vd.getVendorName());
                cmDocument.setVendorLine1Address(vd.getDefaultAddressLine1());
                cmDocument.setVendorLine2Address(vd.getDefaultAddressLine1());
                cmDocument.setVendorCityName(vd.getDefaultAddressCity());
                cmDocument.setVendorStateCode(vd.getDefaultAddressStateCode());
                cmDocument.setVendorPostalCode(vd.getDefaultAddressPostalCode());
                cmDocument.setVendorCountryCode(vd.getDefaultAddressCountryCode());
            }
            else {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_VENDOR_NUMBER, PurapKeyConstants.ERROR_VENDOR_NUMBER_INVALID, cmDocument.getVendorNumber());
                valid &= false;
            }
        }
         
         return valid;
    }
/*
    public void assignVendorAddress(CreditMemoDocument cmDocument) {
        // now that we have the vendor ids, set address for po and vendor type
        VendorAddress defaultAddress = vendorService.getDefaultAddress(
            cm.getVendorHeaderGeneratedId(), cm.getVendorDetailAssignedId(),
            EpicConstants.VENDOR_ADDRESS_TYPE_REMIT, u.getCampusCd());
        // FIXME delyea: Is this if-else correct?
        if (poId != null) {
          //CreditMemo object returns address from PO if VendorAddress is null
          cm.setVendorAddress(defaultAddress);
        } else if (vendorNumber != null) {
          //if no remit type default, use PO default
          if (defaultAddress == null) {
            defaultAddress = vendorService.getDefaultAddress(
                cm.getVendorHeaderGeneratedId(), cm.getVendorDetailAssignedId(),
                EpicConstants.VENDOR_ADDRESS_TYPE_PO, u.getCampusCd());
          }
          cm.setVendorAddress(defaultAddress);
        }
    }*/
}

