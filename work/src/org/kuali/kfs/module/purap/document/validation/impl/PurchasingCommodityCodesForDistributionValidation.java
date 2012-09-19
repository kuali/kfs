/*
 * Copyright 2009 The Kuali Foundation
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchasingCommodityCodesForDistributionValidation extends GenericValidation {

    private String purchasingCommodityCode;
    private BusinessObjectService businessObjectService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        //Find out whether the commodity code has existed in the database
        Map<String,String> fieldValues = new HashMap<String, String>();
        fieldValues.put(PurapPropertyConstants.ITEM_COMMODITY_CODE, purchasingCommodityCode);
        
        Collection<CommodityCode> result = (Collection<CommodityCode>)businessObjectService.findMatching(CommodityCode.class, fieldValues);
        if (result != null && result.size() > 0) {
            CommodityCode commodityCode = (CommodityCode)(result.iterator().next());
            if (!commodityCode.isActive()) {
                //This is the case where the commodity code on the item is not active.
                valid = false;
                GlobalVariables.getMessageMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE, " in distribute commodity code" );
            }
        }
        else {
            //This is the case where the commodity code on the item does not exist in the database.
            valid = false;
            GlobalVariables.getMessageMap().clearErrorPath();
            GlobalVariables.getMessageMap().addToErrorPath(PurapConstants.ITEM_TAB_ERRORS);
            GlobalVariables.getMessageMap().putError(PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY, PurapKeyConstants.PUR_COMMODITY_CODE_INVALID,  " in distribute commodity code" );
        }
        return valid;
    }

    public String getPurchasingCommodityCode() {
        return purchasingCommodityCode;
    }

    public void setPurchasingCommodityCode(String purchasingCommodityCode) {
        this.purchasingCommodityCode = purchasingCommodityCode;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
