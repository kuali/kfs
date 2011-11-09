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

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PurchasingAccountsPayableLineItemIndicatorBranchingValidation extends BranchingValidation{

    protected static final String IS_LINE_ITEM_INDICATOR = "isLineItemIndicator";
    protected static final String IS_NOT_LINE_ITEM_INDICATOR = "isNotLineItemIndicator";
    private PurApItem itemForValidation;
    
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        if (itemForValidation.getItemType().isLineItemIndicator()) {
            return IS_LINE_ITEM_INDICATOR;
        } else {
            return IS_NOT_LINE_ITEM_INDICATOR;
        }
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

}
