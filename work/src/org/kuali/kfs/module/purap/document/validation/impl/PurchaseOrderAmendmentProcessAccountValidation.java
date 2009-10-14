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

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PurchaseOrderAmendmentProcessAccountValidation extends BranchingValidation {
    
    protected final String PROCESS_ACCOUNT_VALIDATION="processAccountValidation";
    protected PurApItem itemForValidation;
    
    /**
     * Overrides the method in PurchasingProcessAccountValidation to provide additional
     * validation condition. If the accounts on the item are editable in the amendment document then
     * we should continue doing the processAccountValidation in the superclass, otherwise
     * we should just return true so that the account won't be validated, because if
     * the items contain accounts that aren't editable, it doesn't make sense to give
     * the user account validation errors.
     * 
     */
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        PurchaseOrderDocument document = (PurchaseOrderDocument)event.getDocument();
        PurchaseOrderItem itemLine = (PurchaseOrderItem) getItemForValidation();
        if (itemLine.isItemActiveIndicator() && (! (document.getContainsUnpaidPaymentRequestsOrCreditMemos() && itemLine.getItemInvoicedTotalAmount() != null))) {
            //This means the accounts on the item are editable, so we'll call super's processAccountValidation.
            return PROCESS_ACCOUNT_VALIDATION;
        }
        else {
            //This means the accounts on the item are not editable, so we'll return true so that it won't do any further validations on the accounts.
            return null;
        }
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }
    
}
