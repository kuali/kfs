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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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


/**
 * Business rules for the Credit Memo Document.
 */
public class CreditMemoDocumentRule extends AccountsPayableDocumentRuleBase implements ContinueAccountsPayableRule {

    /**
     * Validation that occurs on Route of the document.
     * 
     * @param purapDocument - Credit Memo Document Instance
     * @return boolean - true if validation was ok, false if there were errors
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);

        if (valid) {
            valid = validateCreditMemoInitTab((CreditMemoDocument) purapDocument);
        }

        return valid;
    }

    /**
     * Validation that occurs when the continue action is selected from the initial screen.
     * 
     * @param document - Credit Memo Document Instance
     * @return boolean - true if validation was ok, false if there were errors
     */
    public boolean processContinueAccountsPayableBusinessRules(AccountsPayableDocument document) {
        boolean valid = true;

        valid = validateCreditMemoInitTab((CreditMemoDocument) document);

        return valid;
    }

    /**
     * Performs validation for the Credit Memo Init tab.
     * 
     * @param cmDocument - Credit Memo Document instance
     * @return boolean - true if validation was ok, false if there were errors
     */
    protected boolean validateCreditMemoInitTab(CreditMemoDocument cmDocument) {
        boolean valid = true;

        if (!(ObjectUtils.isNotNull(cmDocument.getPaymentRequestIdentifier()) ^ StringUtils.isNotEmpty(cmDocument.getVendorNumber()) ^ ObjectUtils.isNotNull(cmDocument.getPurchaseOrderIdentifier())) ||
                (ObjectUtils.isNotNull(cmDocument.getPaymentRequestIdentifier()) && StringUtils.isNotEmpty(cmDocument.getVendorNumber()) && ObjectUtils.isNotNull(cmDocument.getPurchaseOrderIdentifier())) ) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_INIT_REQUIRED_FIELDS, PurapKeyConstants.ERROR_CREDIT_MEMO_REQUIRED_FIELDS);
            return false;
        }

        // Make sure PREQ is valid if entered
        Integer preqNumber = cmDocument.getPaymentRequestIdentifier();
        if (ObjectUtils.isNotNull(preqNumber)) {
            PaymentRequestDocument preq = SpringServiceLocator.getPaymentRequestService().getPaymentRequestById(preqNumber);
            if (ObjectUtils.isNull(preq)) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_PAYMENT_REQUEST_ID, PurapKeyConstants.ERROR_PAYMENT_REQEUEST_INVALID, preqNumber.toString());
                valid = false;
            }    
            else if ((PurapConstants.PaymentRequestStatuses.IN_PROCESS.equals(preq.getStatus().getStatusCode())) || (PurapConstants.PaymentRequestStatuses.CANCELLED_POST_APPROVE.equals(preq.getStatus().getStatusCode()) || (PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS.equals(preq.getStatus().getStatusCode())))) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_PAYMENT_REQUEST_ID, PurapKeyConstants.ERROR_PAYMENT_REQEUEST_INVALID_SATATUS, preqNumber.toString());
                    valid = false;
            }
        }

        // Make sure PO # is valid if entered
        Integer purchaseOrderID = cmDocument.getPurchaseOrderIdentifier();
        if (ObjectUtils.isNotNull(purchaseOrderID)) {
            PurchaseOrderDocument purchaseOrder = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(purchaseOrderID);
            if (ObjectUtils.isNull(purchaseOrder)) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_INVALID, purchaseOrderID.toString());
                valid = false;
            }
            else if (!StringUtils.equals(purchaseOrder.getStatusCode(), PurapConstants.PurchaseOrderStatuses.OPEN)) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCAHSE_ORDER_INVALID_STATUS, purchaseOrderID.toString());
                valid = false;
            }
        }

        // Make sure vendorNumber is valid if entered
        String vendorNumber = cmDocument.getVendorNumber();
        if (StringUtils.isNotEmpty(vendorNumber)) {
            VendorDetail vendor = SpringServiceLocator.getVendorService().getVendorDetail(VendorUtils.getVendorHeaderId(vendorNumber), VendorUtils.getVendorDetailId(vendorNumber));
            if (ObjectUtils.isNull(vendor)) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_VENDOR_NUMBER, PurapKeyConstants.ERROR_VENDOR_NUMBER_INVALID, vendorNumber);
                valid = false;
            }
        }

        return valid;
    }
}
