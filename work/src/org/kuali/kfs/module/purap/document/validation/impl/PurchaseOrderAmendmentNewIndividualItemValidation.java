/*
 * Copyright 2008-2009 The Kuali Foundation
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

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchaseOrderAmendmentNewIndividualItemValidation extends PurchaseOrderNewIndividualItemValidation {

    private PurchaseOrderService purchaseOrderService;
    
    /**
     * Overrides the method in PurchaseOrderNewIndividualItemValidation to add additional validations that are specific to Amendment.
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchaseOrderDocumentRule#newIndividualItemValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument, java.lang.String, org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = super.validate(event);
        PurchaseOrderItem item = (PurchaseOrderItem) getItemForValidation();
        String identifierString = item.getItemIdentifierString();
        if ((item.getItemInvoicedTotalQuantity() != null) && (!(item.getItemInvoicedTotalQuantity()).isZero())) {
            if (item.getItemQuantity() == null) {
                valid = false;
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMND_NULL, "Item Quantity", identifierString);
            }
            else if (item.getItemQuantity().compareTo(item.getItemInvoicedTotalQuantity()) < 0) {
                valid = false;
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMND_INVALID, "Item Quantity", identifierString);
            }
        }

        if (item.getItemInvoicedTotalAmount() != null) {
            KualiDecimal total = item.getTotalAmount().abs();
            if ((total == null) || total.compareTo(item.getItemInvoicedTotalAmount().abs()) < 0) {
                valid = false;
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMND_INVALID_AMT, "Item Extended Price", identifierString);
            }
        }
        
        PurchaseOrderAmendmentDocument document = (PurchaseOrderAmendmentDocument)event.getDocument();
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        
        // run additional accounting line check for items added to POA via receiving line, only after document in enroute
        if ( !(workflowDocument.isInitiated() || workflowDocument.isSaved()) && purchaseOrderService.isNewUnorderedItem(item) ) {
            
            // check to see if the account lines are empty
            if (item.getSourceAccountingLines() == null || item.getSourceAccountingLines().size() == 0) {            
                valid = false;
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_INCOMPLETE, identifierString, identifierString);            
            }
        }
        
        return valid;
    }

    /**
     * Overrides to provide validation for PurchaseOrderAmendmentDocument. 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingDocumentRuleBase#validateCommodityCodes(org.kuali.kfs.module.purap.businessobject.PurApItem, boolean)
     */
    @Override
    protected boolean validateCommodityCodes(PurApItem item, boolean commodityCodeRequired) {
        //If the item is inactive then don't need any of the following validations.
        if (!((PurchaseOrderItem)item).isItemActiveIndicator()) {
            return true;
        }
        else {
            return super.validateCommodityCodes(item, commodityCodeRequired);
        }
    }

    /**
     * Overrides the method in PurchasingDocumentRuleBase so that we'll return true
     * if the item has been previously saved to the database and we'll only check for
     * the commodity code active flag if the item has not been previously saved to
     * the database. 
     * 
     * @param item
     * @param commodityCodeRequired
     * @return
     */
    protected boolean validateThatCommodityCodeIsActive(PurApItem item) {
        if (item.getVersionNumber() != null) {
            return true;
        }
        else {
            if (!((PurchasingItemBase)item).getCommodityCode().isActive()) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_COMMODITY_CODE, PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE, " in " + item.getItemIdentifierString());
                return false;
            }
            return true;
        }
    }

    public PurchaseOrderService getPurchaseOrderService() {
        return purchaseOrderService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }
}
