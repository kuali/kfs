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

import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class PurchaseOrderNewIndividualItemValidation extends PurchasingNewIndividualItemValidation {
    
    PurchaseOrderEmptyItemWithAccountsValidation emptyItemsWithAccountsValidation;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = super.validate(event);        
        emptyItemsWithAccountsValidation.setItemForValidation((PurchaseOrderItem)super.getItemForValidation());
        valid &= emptyItemsWithAccountsValidation.validate(event);
        
        return valid;
    }
        
    @Override
    protected boolean commodityCodeIsRequired() {
        //if the ENABLE_COMMODITY_CODE_IND parameter is  N then we don't
        //need to check for the ITEMS_REQUIRE_COMMODITY_CODE_IND parameter anymore, just return false. 
        boolean enableCommodityCode = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_COMMODITY_CODE_IND);
        if (!enableCommodityCode) {
            return false;
        }
        else {        
            return getParameterService().getParameterValueAsBoolean(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND);
        }
    }
    public PurchaseOrderEmptyItemWithAccountsValidation getEmptyItemsWithAccountsValidation() {
        return emptyItemsWithAccountsValidation;
    }

    public void setEmptyItemsWithAccountsValidation(PurchaseOrderEmptyItemWithAccountsValidation emptyItemsWithAccountsValidation) {
        this.emptyItemsWithAccountsValidation = emptyItemsWithAccountsValidation;
    }

}
