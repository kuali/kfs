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

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchaseOrderAmendmentAccountValidation extends GenericValidation {

    @Override
    public boolean validate(AttributedDocumentEvent event) {

        boolean valid = true;
        PurchaseOrderDocument poaDocument = (PurchaseOrderDocument)event.getDocument();
        List<PurApItem> items = poaDocument.getItemsActiveOnly();

        PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(poaDocument.getPurapDocumentIdentifier());
        List<PurApItem> poItems = po.getItems();

        for (PurApItem item : items) {
            String identifierString = item.getItemIdentifierString();

            // check to see if the account has been expired or inactive
            if (item.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE) && item.getSourceAccountingLines() != null && item.getSourceAccountingLines().size() > 0) {

                // check only if there are changes on the item
                if (isItemChanged(item, poItems)) {
                    List<PurApAccountingLine> accountingLines = item.getSourceAccountingLines();
                    for (PurApAccountingLine accountingLine : accountingLines) {
                        if (accountingLine.getAccount().isExpired()) {
                            valid = false;
                            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNT_EXPIRED, accountingLine.getAccount().getAccountNumber());
                            break;
                        }
                        if (!accountingLine.getAccount().isActive()) {
                            valid = false;
                            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNT_INACTIVE, accountingLine.getAccount().getAccountNumber());
                            break;
                        }
                    }
                }
            }
        }

        return valid;
    }

    private boolean isItemChanged(PurApItem poaItem, List<PurApItem> poItems) {

        boolean changed = false;

        int poaItemId = poaItem.getItemLineNumber().intValue();

        for (PurApItem poItem : poItems) {

            if (poItem.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE) && poaItemId == poItem.getItemLineNumber().intValue()) {
                // required fields so they cannot be null
                if (poaItem.getItemQuantity() == null ||  poaItem.getItemQuantity().intValue() != poItem.getItemQuantity().intValue()) {
                    changed = true;
                }
                if (!poaItem.getItemUnitOfMeasureCode().equals(poItem.getItemUnitOfMeasureCode())) {
                    changed = true;
                }
                if  (poaItem.getItemUnitPrice() == null || poaItem.getItemUnitPrice().floatValue() != poItem.getItemUnitPrice().floatValue()) {
                    changed = true;
                }

                if (poaItem.getTotalAmount().floatValue() != poItem.getTotalAmount().floatValue()) {
                    changed = true;
                }
                if (poaItem.getItemAssignedToTradeInIndicator() != poItem.getItemAssignedToTradeInIndicator()) {
                    changed = true;
                }
                // optional fields
                if ((poaItem.getItemCatalogNumber() != null && !poaItem.getItemCatalogNumber().equals(poItem.getItemCatalogNumber())) ||
                    (poItem.getItemCatalogNumber() != null && !poItem.getItemCatalogNumber().equals(poaItem.getItemCatalogNumber()))) {
                    changed = true;
                }
                if ((poaItem.getItemDescription() != null && !poaItem.getItemDescription().equals(poItem.getItemDescription())) ||
                    (poItem.getItemDescription() != null && !poItem.getItemDescription().equals(poaItem.getItemDescription()))) {
                    changed = true;
                }
                if ((poaItem.getExtendedPrice() != null && poItem.getExtendedPrice() != null && poaItem.getExtendedPrice().floatValue() != poItem.getExtendedPrice().floatValue()) ||
                    (poaItem.getExtendedPrice() != null && poaItem.getExtendedPrice().floatValue() != 0 && poItem.getExtendedPrice() == null) ||
                    (poaItem.getExtendedPrice() == null && poItem.getExtendedPrice() != null && poItem.getExtendedPrice().floatValue() != 0) ) {
                    changed = true;
                }
                if ((poaItem.getItemTaxAmount() != null && poItem.getItemTaxAmount() != null && poaItem.getItemTaxAmount().floatValue() != poItem.getItemTaxAmount().floatValue()) ||
                    (poaItem.getItemTaxAmount() != null && poaItem.getItemTaxAmount().floatValue() != 0 && poItem.getItemTaxAmount() != null) ||
                    (poaItem.getItemTaxAmount() == null && poItem.getItemTaxAmount() != null && poItem.getItemTaxAmount().floatValue() != 0)) {
                    changed = true;
                }

                break;
            }
        }

        return changed;
    }
}
