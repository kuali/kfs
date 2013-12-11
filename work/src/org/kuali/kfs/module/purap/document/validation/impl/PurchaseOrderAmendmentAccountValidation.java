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
