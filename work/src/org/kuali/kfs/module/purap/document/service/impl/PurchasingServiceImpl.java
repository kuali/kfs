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
package org.kuali.kfs.module.purap.document.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemCapitalAsset;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.sys.service.ParameterService;

public class PurchasingServiceImpl implements PurchasingService {

    private ParameterService parameterService;
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setupCAMSItems(PurchasingDocument purDoc) {

//        List<PurchasingItemCapitalAsset> camsItemsList = purDoc.getCamsItemsList();
//        List<PurchasingItemCapitalAsset> newCamsItemsList = new ArrayList();
//        
//        for (PurApItem purapItem : purDoc.getItems()) {
//            if (purapItem.getItemType().isItemTypeAboveTheLineIndicator()) {
//                if ( doesItemNeedCAMS(purapItem) ) {
//                    PurchasingItemCapitalAsset camsItem = getItemIfAlreadyInCamsItemsList(purapItem, camsItemsList);
//                    if (camsItem == null) {
//                        newCamsItemsList.add( createCamsItem(purapItem) );
//                    }
//                    else {
//                        newCamsItemsList.add(camsItem);
//                    }
//                }
//                else {
//                    // If item does not need CAMS, need to check whether this is the case
//                    // when the item had been in the CAMS tabs but some editing happened so that
//                    // its object code sub type no longer needs CAMS ?
//                    PurchasingItemCapitalAsset camsItem = getItemIfAlreadyInCamsItemsList(purapItem, camsItemsList);
//                    if (camsItem != null) {
//                        // This is when we have to display error that the user have to blank out the fields
//                        // in the cams tab for this item because it's no longer needing CAMS.
//                        GlobalVariables.getErrorMap().put("somekey", "Please blank out those fields in cams tab for this item");
//                    }
//                }
//            }
//        }
//        
//        purDoc.setCamsItemsList(newCamsItemsList);
        
    }
    
//    private boolean doesItemNeedCAMS (PurApItem item) {
//        List<String> capitalAssetSubTypes = parameterService.getParameterValues(PurchasingDocument.class, PurapParameterConstants.CapitalAsset.CAPITAL_ASSET_SUB_TYPES);
//        for (PurApAccountingLine accountingLine : item.getSourceAccountingLines()) {
//            String subTypeCode = accountingLine.getObjectCode().getFinancialObjectSubTypeCode();
//            if (capitalAssetSubTypes.contains(subTypeCode)) {
//                return true;
//            }
//        }
//        return false;
//    }
//    
//    private PurchasingItemCapitalAsset getItemIfAlreadyInCamsItemsList (PurApItem item, List camsItemsList) {
//        for (PurchasingItemCapitalAsset camsItem : camsItemsList) {
//            if (camsItem.getItemIdentifierString().equals(item.getItemIdentifierString())) {
//                return camsItem;                     
//            }
//        }
//    }
//    
//    private PurchasingItemCapitalAsset createCamsItem(PurApItem purapItem) {
//        PurchasingItemCapitalAsset camsItem = new PurchasingItemCapitalAsset(purapItem);
//        return camsItem;
//    }
}
