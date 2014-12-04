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

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.PurapConstants.ItemFields;
import org.kuali.kfs.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchasingItemUnitPriceValidation extends GenericValidation {

    private PurApItem itemForValidation;
    private DataDictionaryService dataDictionaryService;
    
    /**
     * Validates the unit price for all applicable item types. It validates that the unit price field was
     * entered on the item, and that the price is in the right range for the item type.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        if (itemForValidation.getItemType().isLineItemIndicator()) {
            if (ObjectUtils.isNull(itemForValidation.getItemUnitPrice())) {
                valid = false;
                String attributeLabel = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(itemForValidation.getClass().getName()).
                                        getAttributeDefinition(PurapPropertyConstants.ITEM_UNIT_PRICE).getLabel();
                String errorPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + (itemForValidation.getItemLineNumber() - 1) + "]." + PurapPropertyConstants.ITEM_UNIT_PRICE;
                GlobalVariables.getMessageMap().putError(errorPrefix, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + " in " + itemForValidation.getItemIdentifierString());
            }
        }    

        if (ObjectUtils.isNotNull(itemForValidation.getItemUnitPrice())) {
            if ((BigDecimal.ZERO.compareTo(itemForValidation.getItemUnitPrice()) > 0) && ((!itemForValidation.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) && (!itemForValidation.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)))) {
                // If the item type is not full order discount or trade in items, don't allow negative unit price.
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_AMOUNT_BELOW_ZERO, ItemFields.UNIT_COST, itemForValidation.getItemIdentifierString());
                valid = false;
            }
            else if ((BigDecimal.ZERO.compareTo(itemForValidation.getItemUnitPrice()) < 0) && ((itemForValidation.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) || (itemForValidation.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)))) {
                // If the item type is full order discount or trade in items, its unit price must be negative.
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_AMOUNT_NOT_BELOW_ZERO, ItemFields.UNIT_COST, itemForValidation.getItemIdentifierString());
                valid = false;
            }
        }

        return valid;
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
