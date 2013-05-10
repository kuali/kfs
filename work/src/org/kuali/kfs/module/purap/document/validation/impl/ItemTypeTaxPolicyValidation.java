/*
 * Copyright 2010 The Kuali Foundation.
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
