/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class PurchaseOrderSplitValidation extends GenericValidation {

    private PurchaseOrderService purchaseOrderService;
    private PurchaseOrderDocument accountingDocumentForValidation;
    
    /**
     * Applies rules for validation of the Split of PO and PO child documents
     * 
     * @param document  A PurchaseOrderDocument (or one of its children)
     * @return      True if all relevant validation rules are passed.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurchaseOrderDocument po = (PurchaseOrderDocument)event.getDocument();
        HashMap<String, List<PurchaseOrderItem>> categorizedItems = purchaseOrderService.categorizeItemsForSplit((List<PurchaseOrderItem>)po.getItems());
        List<PurchaseOrderItem> movingPOItems = categorizedItems.get(PODocumentsStrings.ITEMS_MOVING_TO_SPLIT);
        List<PurchaseOrderItem> remainingPOLineItems = categorizedItems.get(PODocumentsStrings.LINE_ITEMS_REMAINING);
        if (movingPOItems.isEmpty()) {
            GlobalVariables.getMessageMap().putError(PurapConstants.SPLIT_PURCHASE_ORDER_TAB_ERRORS, PurapKeyConstants.ERROR_PURCHASE_ORDER_SPLIT_ONE_ITEM_MUST_MOVE);
            valid &= false;
        }
        else if (remainingPOLineItems.isEmpty()) {
            GlobalVariables.getMessageMap().putError(PurapConstants.SPLIT_PURCHASE_ORDER_TAB_ERRORS, PurapKeyConstants.ERROR_PURCHASE_ORDER_SPLIT_ONE_ITEM_MUST_REMAIN);
            valid &= false;
        }
        return valid;
    }

    public PurchaseOrderService getPurchaseOrderService() {
        return purchaseOrderService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public PurchaseOrderDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    public void setAccountingDocumentForValidation(PurchaseOrderDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

}

