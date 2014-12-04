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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class RequisitionAssignToTradeInValidation extends GenericValidation 
{
    private DataDictionaryService dataDictionaryService;
    
    public boolean validate(AttributedDocumentEvent event) {
        
        // Initialize the valid and true, and get the requisition document.
        boolean foundTradeIn = false;
        boolean valid        = true;
        
        PurchasingAccountsPayableDocumentBase purapDoc = 
            (PurchasingAccountsPayableDocumentBase) event.getDocument();
        
        // First, get all the items from the requisition document.  For each of
        // the items, look for the ones that are assigned to a trade-in value.
        // For these trade-in items, validate that the trade-in line has a valid
        // description and amount.
        List<PurApItem> items = (List<PurApItem>)purapDoc.getItems();
        for (PurApItem item : items) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
            if (item.getItemAssignedToTradeInIndicator()) {
                foundTradeIn = true;
                break;
            }
        }
        
        // Was a trade-in found for any of the above items?
        if (foundTradeIn) {

            // Get the trade-in item.
            PurApItem tradeInItem = purapDoc.getTradeInItem();
            if (tradeInItem != null) {
                if (StringUtils.isEmpty(tradeInItem.getItemDescription())) {
                    
                    String attributeLabel = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(tradeInItem.getClass().getName()).getAttributeDefinition(PurapPropertyConstants.ITEM_DESCRIPTION).getLabel();
                    tradeInItem.getItemLineNumber();
                    GlobalVariables.getMessageMap().putError(
                            "document.item[" + (items.size() - 1) + "]." + PurapPropertyConstants.ITEM_DESCRIPTION, 
                            PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, 
                            "The item description of " + tradeInItem.getItemType().getItemTypeDescription(), 
                            "empty");
                    
                    valid = false;
                }
                else if (ObjectUtils.isNull(tradeInItem.getItemUnitPrice())) {
                    
                    String attributeLabel = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(tradeInItem.getClass().getName()).getAttributeDefinition(PurapPropertyConstants.ITEM_UNIT_PRICE).getLabel();
                    GlobalVariables.getMessageMap().putError(
                            "document.item[" + (items.size() - 1) + "]." + PurapPropertyConstants.ITEM_UNIT_PRICE,
                            PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE,
                            tradeInItem.getItemType().getItemTypeDescription(),
                            "zero");
                    
                    valid = false;
                }
            }
        }
        
        return valid;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
