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

import org.kuali.core.service.SequenceAccessorService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetSystem;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.sys.service.ParameterService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PurchasingServiceImpl implements PurchasingService {

    private ParameterService parameterService;
    private SequenceAccessorService sequenceAccessorService;
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    public void setupCAMSItems(PurchasingDocument purDoc) {

        List<PurchasingCapitalAssetItem> camsItemsList = purDoc.getPurchasingCapitalAssetItems();
        List<PurchasingCapitalAssetItem> newCamsItemsList = new TypedArrayList(purDoc.getPurchasingCapitalAssetItemClass());
        
        for (PurApItem purapItem : purDoc.getItems()) {
            if (purapItem.getItemType().isItemTypeAboveTheLineIndicator()) {
                if ( doesItemNeedCAMS(purapItem) ) {
                    PurchasingCapitalAssetItem camsItem = getItemIfAlreadyInCamsItemsList(purapItem, camsItemsList);
                    if (camsItem == null) {
                        PurchasingCapitalAssetItem newCamsItem = createCamsItem(purDoc, purapItem);
                        if (newCamsItem != null) {
                            newCamsItemsList.add( newCamsItem );
                        }
                    }
                    else {
                        newCamsItemsList.add(camsItem);
                    }
                }
                else {
                    // If item does not need CAMS, need to check whether this is the case
                    // when the item had been in the CAMS tabs but some editing happened so that
                    // its object code sub type no longer needs CAMS ?
                    PurchasingCapitalAssetItem camsItem = getItemIfAlreadyInCamsItemsList(purapItem, camsItemsList);
                    if (camsItem != null) {
                        // This is when we have to display error that the user have to blank out the fields
                        // in the cams tab for this item because it's no longer needing CAMS.
                        GlobalVariables.getErrorMap().put("somekey", "Please blank out those fields in cams tab for this item");
                    }
                }
            }
        }
        
        purDoc.setPurchasingCapitalAssetItems(newCamsItemsList);
        
    }
    
    private boolean doesItemNeedCAMS (PurApItem item) {
        //List<String> capitalAssetSubTypes = parameterService.getParameterValues(PurchasingDocument.class, PurapParameterConstants.CapitalAsset.CAPITAL_ASSET_SUB_TYPES);
        List<String> capitalAssetSubTypes = new ArrayList<String>();
        capitalAssetSubTypes.add("CL");
        for (PurApAccountingLine accountingLine : item.getSourceAccountingLines()) {
            accountingLine.refreshReferenceObject("objectCode");
            String subTypeCode = accountingLine.getObjectCode().getFinancialObjectSubTypeCode();
            if (capitalAssetSubTypes.contains(subTypeCode)) {
                return true;
            }
        }
        
        return false;
    }
    
    private PurchasingCapitalAssetItem getItemIfAlreadyInCamsItemsList (PurApItem item, List<PurchasingCapitalAssetItem> camsItemsList) {
        if (item.getItemIdentifier() == null) {
            Integer itemIdentifier = new Integer(sequenceAccessorService.getNextAvailableSequenceNumber("PO_ITM_ID").toString());
            item.setItemIdentifier(itemIdentifier);
        }
        for (PurchasingCapitalAssetItem camsItem : camsItemsList) {
            if (camsItem.getItemIdentifier() != null && camsItem.getItemIdentifier().equals(item.getItemIdentifier())) {
                return camsItem;                     
            }
        }
        
        return null;
    }
    
    private PurchasingCapitalAssetItem createCamsItem(PurchasingDocument purDoc, PurApItem purapItem) {
        Class camsItemClass = purDoc.getPurchasingCapitalAssetItemClass();
        PurchasingCapitalAssetItem camsItem;
        PurchasingCapitalAssetSystem resultSystem;
        try {
            camsItem = (PurchasingCapitalAssetItem)(camsItemClass.newInstance());
            camsItem.setItemIdentifier(purapItem.getItemIdentifier());
            //If the system type is INDIVIDUAL then for each of the capital asset items, we need a system attached to it.
            if (purDoc.getCapitalAssetSystemTypeCode().equals("IND")) {
                resultSystem = (PurchasingCapitalAssetSystem) purDoc.getPurchasingCapitalAssetSystemClass().newInstance();
                camsItem.setPurchasingCapitalAssetSystem(resultSystem);
                purDoc.getPurchasingCapitalAssetSystems().add(resultSystem);
            }
            camsItem.setDocument(purDoc);
        }
        catch (Exception e) {
            return null;
        }
        
        return camsItem;
    }
    
    public void deleteCAMSItems(PurchasingDocument purDoc, Integer itemIdentifier) {
        //delete the corresponding CAMS items.
        int index = 0;
        for (PurchasingCapitalAssetItem camsItem : purDoc.getPurchasingCapitalAssetItems()) {
            if (camsItem.getItemIdentifier().equals(itemIdentifier)) {
                break;
            }
            index++;
        }
        purDoc.getPurchasingCapitalAssetItems().remove(index);
    }
    
    public void setupCAMSSystem(PurchasingDocument purDoc) {
        PurchasingCapitalAssetSystem resultSystem;
        try {
            resultSystem = (PurchasingCapitalAssetSystem) purDoc.getPurchasingCapitalAssetSystemClass().newInstance();
            //If the system type is ONE or MULTIPLE then we need a system attached to the document.
            if (purDoc.getCapitalAssetSystemTypeCode().equals("ONE") || purDoc.getCapitalAssetSystemTypeCode().equals("MUL")) {
                if (purDoc.getPurchasingCapitalAssetSystems().size() == 0) {
                    purDoc.getPurchasingCapitalAssetSystems().add(resultSystem);
                }
            }
        }
        catch (Exception e) {
           
        }
    }
    
}
