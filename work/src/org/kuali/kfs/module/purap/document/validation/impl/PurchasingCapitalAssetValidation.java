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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetSystem;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchasingCapitalAssetValidation extends GenericValidation {
    CapitalAssetBuilderModuleService capitalAssetBuilderModuleService;
    PurchasingService purchasingService;
    protected static String ERROR_PATH_PREFIX_FOR_IND_SYSTEM = "document.purchasingCapitalAssetItems[";
    protected static String ERROR_PATH_SUFFIX_FOR_IND_SYSTEM = "].purchasingCapitalAssetSystem";
    protected static String ERROR_PATH_PREFIX_FOR_ONE_SYSTEM = "document.purchasingCapitalAssetSystems[0]";

    public boolean validate(AttributedDocumentEvent event) {
        GlobalVariables.getMessageMap().clearErrorPath();
        boolean valid = true;
        PurchasingDocument purchasingDocument = (PurchasingDocument) event.getDocument();

        boolean requiredByObjectSubType = !capitalAssetBuilderModuleService.validatePurchasingObjectSubType(purchasingDocument);
        boolean requiredByChart = !capitalAssetBuilderModuleService.validateAllFieldRequirementsByChart(purchasingDocument);
        boolean capitalAssetRequired = requiredByObjectSubType && requiredByChart;

        if (capitalAssetRequired) {
            // if capital asset required, check to see if the capital asset data are setup 
            String typeCode = purchasingDocument.getCapitalAssetSystemTypeCode();
            if (StringUtils.isBlank(typeCode) || StringUtils.isBlank(purchasingDocument.getCapitalAssetSystemStateCode()) ||
                    purchasingDocument.getPurchasingCapitalAssetSystems() == null || purchasingDocument.getPurchasingCapitalAssetItems() == null){
                valid = false;
            }
            else if ((typeCode.equals(PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM) || typeCode.equals(PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS)) &&
                    purchasingDocument.getPurchasingCapitalAssetSystems().size() == 0) {
                valid = false;
            }
            /* TODO
             * either complete the following with checking that capital asset items are correctly setup, or replace this whole part (and above)
             * with checking on a flag that indicates whether select/update capital asset has been done since last item changes 
             */   
            else if (purchasingDocument.getPurchasingCapitalAssetItems().isEmpty()) {
                valid = false;
            }
            else {
                int expectedCapAssetItems = 0;
                for (PurApItem purapItem : purchasingDocument.getItems()) {
                    if (purapItem.getItemType().isLineItemIndicator()) {
                        if (capitalAssetBuilderModuleService.doesItemNeedCapitalAsset(purapItem.getItemTypeCode(), purapItem.getSourceAccountingLines())) {
                            expectedCapAssetItems++;
                        }
                    }
                }
                if(purchasingDocument.getPurchasingCapitalAssetItems().size() < expectedCapAssetItems){
                    valid = false;
                }
            }            
            if (!valid) {
                GlobalVariables.getMessageMap().putError("newPurchasingItemCapitalAssetLine", PurapKeyConstants.ERROR_CAPITAL_ASSET_REQD_FOR_PUR_OBJ_SUB_TYPE);                
                return valid; 
            }
        }
        else {
            // if capital asset not required, reset system type and state code in case they are filled in
            // if capital asset items are empty, then set sytem type code and system state code to null
            // fix to jira KFSMI-5146
            if (purchasingDocument.getPurchasingCapitalAssetItems().isEmpty()) {
                purchasingDocument.setCapitalAssetSystemTypeCode(null);
                purchasingDocument.setCapitalAssetSystemStateCode(null);
            }
        }

        // We only need to do capital asset validations if the capital asset system type is not blank.
        if (StringUtils.isNotBlank(purchasingDocument.getCapitalAssetSystemTypeCode())) {
            valid &= capitalAssetBuilderModuleService.validatePurchasingData(purchasingDocument);

            // FIXME hjs move this to cab module service
            // validate complete location addresses
            if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
                for (CapitalAssetSystem system : purchasingDocument.getPurchasingCapitalAssetSystems()) {
                    for (CapitalAssetLocation location : system.getCapitalAssetLocations()) {
                        valid &= purchasingService.checkCapitalAssetLocation(location);
                    }
                }
            }
            else if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM)) {
                CapitalAssetSystem system = purchasingDocument.getPurchasingCapitalAssetSystems().get(0);
                for (CapitalAssetLocation location : system.getCapitalAssetLocations()) {
                    valid &= purchasingService.checkCapitalAssetLocation(location);
                }
            }

            // Validate asset type code if entered by user.
            valid &= validateAssetTypeExistence(purchasingDocument);
        }


        return valid;
    }

    /**
     * Validate user input asset type code.
     * 
     * @param purchasingDocument
     * @return
     */
    protected boolean validateAssetTypeExistence(PurchasingDocument purchasingDocument) {
        boolean valid = true;
        // validate for Individual system
        if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
            int i = 0;
            for (PurchasingCapitalAssetItem capitalAssetItem : purchasingDocument.getPurchasingCapitalAssetItems()) {
                if (ObjectUtils.isNotNull(capitalAssetItem) && ObjectUtils.isNotNull(capitalAssetItem.getPurchasingCapitalAssetSystem())) {
                    String assetTypeCode = capitalAssetItem.getPurchasingCapitalAssetSystem().getCapitalAssetTypeCode();
                    if (StringUtils.isNotBlank(assetTypeCode) && !capitalAssetBuilderModuleService.isAssetTypeExisting(assetTypeCode)) {
                        valid = false;
                        String errorPath = ERROR_PATH_PREFIX_FOR_IND_SYSTEM + new Integer(i).toString() + ERROR_PATH_SUFFIX_FOR_IND_SYSTEM;
                        addAssetTypeErrorWithFullErrorPath(errorPath);
                    }
                }
                i++;
            }
        }
        else if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM)) {
            // validate for One system
            if (ObjectUtils.isNotNull(purchasingDocument.getPurchasingCapitalAssetSystems())) {
                CapitalAssetSystem system = purchasingDocument.getPurchasingCapitalAssetSystems().get(0);
                if (ObjectUtils.isNotNull(system) && StringUtils.isNotBlank(system.getCapitalAssetTypeCode()) && !capitalAssetBuilderModuleService.isAssetTypeExisting(system.getCapitalAssetTypeCode())) {
                    valid = false;
                    String errorPath = ERROR_PATH_PREFIX_FOR_ONE_SYSTEM;
                    addAssetTypeErrorWithFullErrorPath(errorPath);
                }
            }
        }
        // Validate for Multiple system is ignored since currently it's not supported to enter. 
        return valid;
    }

    /**
     * Add asset type error to the global message map.
     * 
     * @param errorPath
     */
    protected void addAssetTypeErrorWithFullErrorPath(String errorPath) {
        GlobalVariables.getMessageMap().addToErrorPath(errorPath);
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(RequisitionCapitalAssetSystem.class.getName()).getAttributeDefinition(PurapPropertyConstants.CAPITAL_ASSET_TYPE_CODE).getLabel();
        GlobalVariables.getMessageMap().putError(PurapPropertyConstants.CAPITAL_ASSET_TYPE_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
        GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
    }

    public CapitalAssetBuilderModuleService getCapitalAssetBuilderModuleService() {
        return capitalAssetBuilderModuleService;
    }

    public void setCapitalAssetBuilderModuleService(CapitalAssetBuilderModuleService capitalAssetBuilderModuleService) {
        this.capitalAssetBuilderModuleService = capitalAssetBuilderModuleService;
    }

    public PurchasingService getPurchasingService() {
        return purchasingService;
    }

    public void setPurchasingService(PurchasingService purchasingService) {
        this.purchasingService = purchasingService;
    }

}
