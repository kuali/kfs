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

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchasingProcessContainsAtLeastOneItemValidation extends GenericValidation {
    
    /**
     * Validates that the document contains at least one item.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if the document does not contain at least one item.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = false;
        PurchasingDocument purDocument = (PurchasingDocument)event.getDocument();
        for (PurApItem item : purDocument.getItems()) {
            if (!((PurchasingItemBase) item).isEmpty() && item.getItemType().isLineItemIndicator()) {

                return true;
            }
        }
        String documentType = getDocumentTypeLabel(purDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());

        if (!valid) {
            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_REQUIRED, documentType);
        }

        return valid;
    }

    protected String getDocumentTypeLabel(String documentTypeName) {
            return SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(documentTypeName).getLabel();
    }

}
