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
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

public class ItemTypeTaxPolicyValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ItemTypeTaxPolicyValidation.class);
    protected PurapService purapService;
    
    public static final String ERROR_INVALID_ITEM_TYPE_FOR_ACCOUNT_TAX_POLICY="error.itemTypeCode.taxPolicy";
    public static final String FIELD_NAME_ITEM_TYPE_CODE = "itemTypeCode";    
    

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;
        PurchasingDocument purchasingDocument = (PurchasingDocument)event.getDocument();

        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);
        boolean useTaxIndicator = purchasingDocument.isUseTaxIndicator();

        //FIXME: KFSMI-6006 remove call code variable once this code has been fixed
        boolean callCode = false;
        
        //if sales tax or use tax is enabled, attempt tax check
        if( callCode && (salesTaxInd || useTaxIndicator) ){
            String errorPath = "document.items";
            GlobalVariables.getMessageMap().clearErrorPath();
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);                    
            for ( PurApItem item : purchasingDocument.getItems() )
            {
                if ( getPurapService().isItemTypeConflictWithTaxPolicy(purchasingDocument, item)) {
                    String itemIdentifier = item.getItemIdentifierString();
                    
                    GlobalVariables.getMessageMap().putError(FIELD_NAME_ITEM_TYPE_CODE, ERROR_INVALID_ITEM_TYPE_FOR_ACCOUNT_TAX_POLICY, itemIdentifier );
                    isValid = false;
                }
            }
        }
        
        return isValid;
    }


    public PurapService getPurapService() {
        return purapService;
    }


    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

}
