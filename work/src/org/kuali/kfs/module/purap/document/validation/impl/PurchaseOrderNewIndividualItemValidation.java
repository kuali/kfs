/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
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
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;

public class PurchaseOrderNewIndividualItemValidation extends PurchasingNewIndividualItemValidation {

    private ParameterService parameterService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = super.validate(event);
        valid &= validateEmptyItemWithAccounts((PurchaseOrderItem) getItemForValidation(), getItemForValidation().getItemIdentifierString());
        
        return valid;
    }
        
    /**
     * Validates that the item detail must not be empty if its account is not empty and its item type is ITEM.
     * 
     * @param item the item to be validated
     * @param identifierString the identifier string of the item to be validated
     * @return boolean false if it is an above the line item and the item detail is empty and the account list is not empty.
     */
    boolean validateEmptyItemWithAccounts(PurchaseOrderItem item, String identifierString) {
        boolean valid = true;
        if (item.getItemType().isLineItemIndicator() && item.isItemDetailEmpty() && !item.isAccountListEmpty()) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_NOT_ALLOWED, identifierString);
        }

        return valid;
    }

    @Override
    protected boolean commodityCodeIsRequired() {
        return parameterService.getIndicatorParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND);
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
 
}
