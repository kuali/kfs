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
package org.kuali.module.cams.rules;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetComponent;
import org.kuali.module.cams.bo.AssetLocation;
import org.kuali.module.cams.bo.AssetGlobal;
import org.kuali.module.cams.bo.AssetGlobalDetail;
import org.kuali.module.cams.bo.AssetWarranty;
import org.kuali.module.cams.service.AssetComponentService;
import org.kuali.module.cams.service.AssetDateService;
import org.kuali.module.cams.service.AssetDispositionService;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.EquipmentLoanInfoService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.service.RetirementInfoService;
import org.kuali.module.cams.service.AssetLocationService.LocationField;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * AssetRule for Asset edit.
 */
public class AssetGlobalRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetGlobalRule.class);
    private static AssetService assetService = SpringContext.getBean(AssetService.class);
    private AssetGlobal newAssetGlobal;    

    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);

    /**
     * Constructs an AssetGlobalRule
     */
    public AssetGlobalRule() {
        //LOG.info("AssetGlobalRule constructor");
        super();
    }
    
    /**
     * This method sets the convenience objects, so you have short and easy handles to the new and old objects contained in
     * the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects
     * from the DB by their primary keys, if available.
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     * 
     * @param document - the maintenanceDocument being evaluated
     * @return none
     */
    @Override
    public void setupConvenienceObjects() {
        AssetGlobal newAssetGlobal = (AssetGlobal) super.getNewBo();
        newAssetGlobal.refreshNonUpdateableReferences();
        for (AssetGlobalDetail detail : newAssetGlobal.getAssetGlobalDetails()) {
            detail.refreshNonUpdateableReferences();
        }
    }
    
    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = false;
        valid = true;
        return valid;
    }


    /**
     * Validates Asset document.
     * 
     * @param document MaintenanceDocument instance
     * @return boolean false or true
     */
    private boolean processAssetValidation(MaintenanceDocument document) {
        boolean valid = true;

        // validate Inventory Status Code.
            valid &= validateInventoryStatusCode();

        // validate Vendor Name.
            valid &= validateVendorName();

        // validate Tag Number.
            valid &= validateTagNumber();

        // validate location.
            valid &= validateLocation();
            
        // validate In-service Date
            valid &= validateInServiceDate();
        return valid;
    }


    /**
     * Check if the new In-service Date is a valid University Date
     * 
     * @return
     */
    private boolean validateInServiceDate() {
        if (SpringContext.getBean(UniversityDateService.class).getFiscalYear(newAssetGlobal.getCapitalAssetInServiceDate()) == null) {
            putFieldError(CamsPropertyConstants.Asset.ASSET_DATE_OF_SERVICE, CamsKeyConstants.ERROR_INVALID_IN_SERVICE_DATE);
            return false;
        }
        return true;
    }



    /**
     * Validate Inventory Status Code Change
     */
    private boolean validateInventoryStatusCode() {
 //       return parameterService.getParameterEvaluator(Asset.class, CamsConstants.Parameters.VALID_INVENTROY_STATUS_CODE_CHANGE, CamsConstants.Parameters.INVALID_INVENTROY_STATUS_CODE_CHANGE, oldAsset.getInventoryStatusCode(), newAssetGlobal.getInventoryStatusCode()).evaluateAndAddError(newAssetGlobal.getClass(), CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS);
        return true;
    }


    /**
     * If the tag number has not been assigned, the departmental user will be able to update the tag number. The Tag Number shall be
     * verified that the tag number does not exist on another asset.
     * 
     * @param asset
     * @return
     */
    private boolean validateTagNumber() {
        boolean valid = true;
        boolean anyFound = false;

/*
        if (!assetService.isTagNumberCheckExclude(newAssetGlobal)) {

            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, newAssetGlobal.getCampusTagNumber());

            Collection<Asset> results = getBoService().findMatching(Asset.class, fieldValues);

            for (Asset asset : results) {
                if (!asset.getCapitalAssetNumber().equals(newAssetGlobal.getCapitalAssetNumber())) {
                    anyFound = true;
                    break;
                }
            }

            if (anyFound) {
                putFieldError(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsKeyConstants.ERROR_TAG_NUMBER_DUPLICATE);
                valid &= false;
            }
        }
*/
        return valid;
    }


    /**
     * The Vendor Name is required for capital equipment and not required for non-capital assets.
     * 
     * @param asset
     * @return
     */
    private boolean validateVendorName() {
        boolean valid = true;

  //      if (assetService.isCapitalAsset(newAssetGlobal) && StringUtils.isBlank(newAssetGlobal.getVendorName())) {
            putFieldError(CamsPropertyConstants.Asset.VENDOR_NAME, CamsKeyConstants.ERROR_CAPITAL_ASSET_VENDOR_NAME_REQUIRED);
            valid &= false;

 //       }
        return valid;
    }


    /**
     * Validate Asset Location fields
     * 
     * @param asset
     * @return
     */
    private boolean validateLocation() {
        Map<LocationField, String> fieldMap = new HashMap<LocationField, String>();
        fieldMap.put(LocationField.CAMPUS_CODE, CamsPropertyConstants.Asset.CAMPUS_CODE);
        fieldMap.put(LocationField.BUILDING_CODE, CamsPropertyConstants.Asset.BUILDING_CODE);
        fieldMap.put(LocationField.ROOM_NUMBER, CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER);
        fieldMap.put(LocationField.SUB_ROOM_NUMBER, CamsPropertyConstants.Asset.BUILDING_SUB_ROOM_NUMBER);
        fieldMap.put(LocationField.CONTACT_NAME, CamsPropertyConstants.Asset.AssetLocation.CONTACT_NAME);
        fieldMap.put(LocationField.STREET_ADDRESS, CamsPropertyConstants.Asset.AssetLocation.STREET_ADDRESS);
        fieldMap.put(LocationField.CITY_NAME, CamsPropertyConstants.Asset.AssetLocation.CITY_NAME);
        fieldMap.put(LocationField.STATE_CODE, CamsPropertyConstants.Asset.AssetLocation.STATE_CODE);
        fieldMap.put(LocationField.ZIP_CODE, CamsPropertyConstants.Asset.AssetLocation.ZIP_CODE);
        fieldMap.put(LocationField.COUNTRY_CODE, CamsPropertyConstants.Asset.AssetLocation.COUNTRY_CODE);
        fieldMap.put(LocationField.LOCATION_TAB_KEY, CamsPropertyConstants.Asset.AssetLocation.VERSION_NUM);
/*
        GlobalVariables.getErrorMap().addToErrorPath("document.newMaintainableObject");
        boolean isCapitalAsset = SpringContext.getBean(AssetService.class).isCapitalAsset(newAssetGlobal);
        boolean valid = SpringContext.getBean(AssetLocationService.class).validateLocation(fieldMap, newAssetGlobal, isCapitalAsset, newAssetGlobal.getCapitalAssetType());
        GlobalVariables.getErrorMap().removeFromErrorPath("document.newMaintainableObject");

        if (valid && (this.isFabrication || isOffCampusLocationChanged())) {
            AssetLocationService assetlocationService = SpringContext.getBean(AssetLocationService.class);
            assetlocationService.updateOffCampusLocation(newAssetGlobal);
        }
        */
        return true;
        
    }

    /**
     * Validate warranty information if user enters value
     * 
     * @param asset Asset
     * @return validation result
     */
    private boolean validateWarrantyInformation(Asset asset) {
        AssetWarranty warranty = asset.getAssetWarranty();
        if (warranty != null) {
            if (!StringUtils.isEmpty(warranty.getWarrantyContactName()) || !StringUtils.isEmpty(warranty.getWarrantyPhoneNumber()) || !StringUtils.isEmpty(warranty.getWarrantyText()) || warranty.getWarrantyBeginningDate() != null || warranty.getWarrantyEndingDate() != null) {
                if (StringUtils.isEmpty(warranty.getWarrantyNumber())) {
                    // warranty number is mandatory when any other related info is known
//                    putFieldError(ASSET_WARRANTY_WARRANTY_NUMBER, ERROR_INVALID_ASSET_WARRANTY_NO);
                    return false;
                }
            }
        }
        return true;
    }
}
