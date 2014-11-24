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
