/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

