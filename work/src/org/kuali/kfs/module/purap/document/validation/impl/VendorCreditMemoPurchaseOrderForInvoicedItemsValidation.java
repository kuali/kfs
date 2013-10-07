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

import java.util.List;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class VendorCreditMemoPurchaseOrderForInvoicedItemsValidation extends GenericValidation {

    private PurchaseOrderService purchaseOrderService;
    private CreditMemoService creditMemoService;
    
    /**
     * Verifies the purchase order for the credit memo has at least one invoiced item. If no invoiced items are found, a credit memo
     * cannot be processed against the document.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean hasInvoicedItems = true;       
        VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument) event.getDocument();
        
        if(cmDocument.isSourceDocumentPurchaseOrder()){
            GlobalVariables.getMessageMap().clearErrorPath();
            GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
    
            PurchaseOrderDocument poDocument = purchaseOrderService.getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier());
            List<PurchaseOrderItem> invoicedItems = creditMemoService.getPOInvoicedItems(poDocument);
    
            if (invoicedItems == null || invoicedItems.isEmpty()) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_CREDIT_MEMO_PURCAHSE_ORDER_NOITEMS);
                hasInvoicedItems = false;
            }
    
            GlobalVariables.getMessageMap().clearErrorPath();
        }
        
        return hasInvoicedItems;
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

}
