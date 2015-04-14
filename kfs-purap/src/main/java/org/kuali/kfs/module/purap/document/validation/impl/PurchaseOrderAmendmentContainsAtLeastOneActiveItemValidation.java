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
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchaseOrderAmendmentContainsAtLeastOneActiveItemValidation extends GenericValidation {

    /**
     * Validates that the given document contains at least one active item.
     * 
     * @param purapDocument A PurchasingAccountsPayableDocument. (Should contain PurchaseOrderItems.)
     * @return True if the document contains at least one active item
     */
    public boolean validate(AttributedDocumentEvent event) {
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument)event.getDocument();
        List<PurApItem> items = purapDocument.getItems();
        for (PurApItem item : items) {
            if (((PurchaseOrderItem) item).isItemActiveIndicator() && (!((PurchaseOrderItem) item).isEmpty() && item.getItemType().isLineItemIndicator())) {
                return true;
            }
        }
        String documentType = getDocumentTypeLabel(purapDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
        GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_REQUIRED, documentType);

        return false;
    }

    protected String getDocumentTypeLabel(String documentTypeName) {
            return SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(documentTypeName).getLabel();

    }

}
