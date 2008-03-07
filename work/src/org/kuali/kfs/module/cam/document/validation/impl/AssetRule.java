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

import static org.kuali.module.cams.CamsKeyConstants.ERROR_INVALID_ASSET_WARRANTY_NO;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.ASSET_WARRANTY_WARRANTY_NUMBER;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetComponent;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetType;
import org.kuali.module.cams.bo.AssetWarranty;
import org.kuali.module.cams.service.AssetComponentService;
import org.kuali.module.cams.service.AssetDetailInformationService;
import org.kuali.module.cams.service.AssetDispositionService;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.EquipmentLoanInfoService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.service.RetirementInfoService;

/**
 * AssetRule for Asset edit.
 */
public class AssetRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRule.class);

    private Asset newAsset;
    private Asset copyAsset;
    
    private boolean isTagNumberChanged = false;
    
    private static Map<String, String> VALID_INVENTROY_STATUS_CODE_CHANGE = new HashMap<String, String>();
    static {
        try {
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE, StringUtils.join(new String[] {CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_NON_ACCESSIBLE,CamsConstants.InventoryStatusCode.CAPITAL_ASSET_SURPLUS_EQUIPEMENT}));
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_NON_ACCESSIBLE, StringUtils.join(new String[] {CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE,CamsConstants.InventoryStatusCode.CAPITAL_ASSET_SURPLUS_EQUIPEMENT}));
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_UNDER_CONSTRUCTION, null);
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_SURPLUS_EQUIPEMENT, StringUtils.join(new String[] {CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE,CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_NON_ACCESSIBLE}));
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_RETIRED, null);
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_ACTIVE, CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_RETIRED);
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_RETIRED, CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_ACTIVE);
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_ACTIVE_2003, CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_RETIRED_2003);
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_RETIRED_2003, CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_ACTIVE_2003);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isTagNumberChanged() {
        return isTagNumberChanged;
    }


    private void setTagNumberChanged(boolean isTagNumberChanged) {
        this.isTagNumberChanged = isTagNumberChanged;
    }


    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        Asset asset = (Asset) document.getDocumentBusinessObject();
        initializeAttributes(document);

        setAssetComponentNumbers(asset);

        PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
        paymentSummaryService.calculateAndSetPaymentSummary(copyAsset);
        paymentSummaryService.calculateAndSetPaymentSummary(newAsset);

        AssetDispositionService assetDispService = SpringContext.getBean(AssetDispositionService.class);
        assetDispService.setAssetDispositionHistory(copyAsset);
        assetDispService.setAssetDispositionHistory(newAsset);

        AssetLocationService assetlocationService = SpringContext.getBean(AssetLocationService.class);
        assetlocationService.setOffCampusLocation(copyAsset);
        assetlocationService.setOffCampusLocation(newAsset);
        
        RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
        retirementInfoService.setRetirementInfo(asset);

        EquipmentLoanInfoService equipmentLoanInfoService = SpringContext.getBean(EquipmentLoanInfoService.class);
        equipmentLoanInfoService.setEquipmentLoanInfo(asset);

        boolean valid = processValidation(document);
        valid &= validateWarrantyInformation(asset);
        valid &= super.processCustomSaveDocumentBusinessRules(document);

        if (valid) {
            AssetDetailInformationService assetDetailInfoService = SpringContext.getBean(AssetDetailInformationService.class);
            assetDetailInfoService.checkAndUpdateLastInventoryDate(copyAsset,newAsset);
            if (isTagNumberChanged()) {
                assetDetailInfoService.updateTagNumber(copyAsset,newAsset);
            }
        }
        return valid;
    }


    private void setAssetComponentNumbers(Asset asset) {
        AssetComponentService assetComponentService = SpringContext.getBean(AssetComponentService.class);
        List<AssetComponent> assetComponents = asset.getAssetComponents();
        Integer maxNo = null;
        for (AssetComponent assetComponent : assetComponents) {
            assetComponent.setCapitalAssetNumber(asset.getCapitalAssetNumber());
            if (maxNo == null) {
                maxNo = assetComponentService.getMaxSequenceNumber(assetComponent);
            }
            if (assetComponent.getComponentNumber() == null) {
                assetComponent.setComponentNumber(++maxNo);
            }
        }
    }

        
    /**
     * Validates Asset
     * 
     * @param document MaintenanceDocument instance
     * @return boolean false or true
     */
    private boolean processValidation(MaintenanceDocument document) {
        boolean valid = true;

        valid &= processAssetValidation(document);

        return valid;
    }

    /**
     * Validates Asset document.
     * 
     * @param document MaintenanceDocument instance
     * @return boolean false or true
     */
    boolean processAssetValidation(MaintenanceDocument document) {
        boolean valid = true;
        


        // validate Inventory Status Code.
        if (!StringUtils.equalsIgnoreCase(copyAsset.getInventoryStatusCode(), newAsset.getInventoryStatusCode())) {
            valid &= validateInventoryStatusCode();
        }

        // validate Vender Name.
        if (!StringUtils.equalsIgnoreCase(copyAsset.getVendorName(), newAsset.getVendorName())) {
            valid &= validateVenderName();
        }

        // validate Tag Number.
        if (!StringUtils.equalsIgnoreCase(copyAsset.getCampusTagNumber(), newAsset.getCampusTagNumber())) {
            valid &= validateTagNumber();
        }

        // validade location.
        if (isLocationUpdated()) {
            valid &= validateLocation(newAsset);
        }

        return valid;
    }


    private boolean isLocationUpdated() {
        boolean updated = false;
        
        if (!StringUtils.equalsIgnoreCase(copyAsset.getCampusCode(), newAsset.getCampusCode()) || !StringUtils.equalsIgnoreCase(copyAsset.getBuildingCode(), newAsset.getBuildingCode()) || ! StringUtils.equalsIgnoreCase(copyAsset.getBuildingRoomNumber(), newAsset.getBuildingRoomNumber())) {
            updated = true;
        }
        return updated;
    }


    /**
     * Validate Inventory Status Code Change
     * 
     */
    private boolean validateInventoryStatusCode() {
        boolean valid = true;
        
        if (!StringUtils.containsIgnoreCase(VALID_INVENTROY_STATUS_CODE_CHANGE.get(copyAsset.getInventoryStatusCode()), newAsset.getInventoryStatusCode())) {
            putFieldError(CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS, CamsKeyConstants.ERROR_INVALID_ASSET_STATUS_CHANGE, new String[] { copyAsset.getInventoryStatusCode(), newAsset.getInventoryStatusCode() });
            valid &= false;
        }
        
        return valid;
    }


    private void initializeAttributes(MaintenanceDocument document) {
        if (newAsset == null) {
            newAsset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        }
        if (copyAsset == null ) {
            copyAsset = (Asset) document.getOldMaintainableObject().getBusinessObject();
        }
    }

    /**
     * The Tag Number shall be verified that the tag number does not exist on another asset. 
     * 
     * @param asset
     * @return
     */
    private boolean validateTagNumber() {
        boolean valid = true;
        boolean anyFound = false;
       

        valid &= isTagNumberChangeAllowed();
        
        if (valid && !isTagNumberCheckExclude()){
                
            Map fieldValues = new HashMap();
            fieldValues.put(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, newAsset.getCampusTagNumber());

            Collection results = getBoService().findMatching(Asset.class, fieldValues);

            for (Iterator iter = results.iterator(); iter.hasNext();) {
                Asset assetFound = (Asset) iter.next();

                if (assetFound.getCapitalAssetNumber() != newAsset.getCapitalAssetNumber()) {
                    anyFound = true;
                    break;
                }
            }
            
            if (anyFound) {
                putFieldError(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsKeyConstants.ERROR_TAG_NUMBER_DUPLICATE);
                valid &= false;
            }else {
                setTagNumberChanged(true);
            }
        }
        
        return valid;
    }


    /**
     * 
     * The Tag Number can't be updated together with Asset Type Code.
     * @param valid
     * @return
     */
    private boolean isTagNumberChangeAllowed() {
        boolean valid = true;
        
        if (!StringUtils.equalsIgnoreCase(copyAsset.getInventoryStatusCode(),newAsset.getInventoryStatusCode())) {
            putFieldError(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER,CamsKeyConstants.ERROR_TAG_NUMBER_RESTRICT_CHANGE);
            valid &= false;
        }
        return valid;
    }

    
    /**
     * The Tag Number check excludes value of "N" and retired assets.
     * This method...
     * @return
     */
    private boolean isTagNumberCheckExclude() {
        boolean exclude = false;
        String status = newAsset.getInventoryStatusCode();
        
        if (StringUtils.equalsIgnoreCase(status, CamsConstants.InventoryStatusCode.CAPITAL_ASSET_RETIRED) || StringUtils.equalsIgnoreCase(status,CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_RETIRED) || StringUtils.equalsIgnoreCase(newAsset.getCampusTagNumber(), CamsConstants.NON_TAGGABLE_ASSET)) {
            exclude = true;
        }
        return exclude;
    }


    /**
     * The Vendor Name is required for capital equipment and not required for non-capital assets.
     * 
     * @param asset
     * @return
     */
    private boolean validateVenderName() {
        boolean valid = true;
        
        if (isCapitalEquipment(newAsset)) {
            if (StringUtils.isBlank(newAsset.getVendorName())) {
                putFieldError(CamsPropertyConstants.Asset.VENDOR_NAME, CamsKeyConstants.ERROR_CAPITAL_ASSET_VENDOR_NAME_REQUIRED);
                valid &= false;
            }
        }
        return valid;
    }
    
    private boolean isCapitalEquipment(Asset asset) {
        return StringUtils.contains(CamsConstants.CAPITAL_ASSET_STATUS_CODES, asset.getInventoryStatusCode());
    }
   


    /**
     * Validates Asset On Campus loaction information
     * 
     * @param asset the Asset object to be validated
     * @return boolean false if the on campus location information is invalid
     */

    private boolean validateLocation(Asset asset) {
        boolean valid = true;
        
        AssetType assetType = (AssetType) asset.getCapitalAssetType();

        if (assetType.isMovingIndicator()) {
            valid &= validateLocationForMoving(asset);
        }
        else if (assetType.isRequiredBuildingIndicator()) {
            valid &= validateLocationForReqBuilding(asset);
        }
        else {
            valid &= validateLocationForNonspecific(asset);
        }
        return valid;
    }


    /**
     * Validates Asset On Campus loaction information when "moving" code is 'Y'.
     * Campus, Building and Room are mandatory and shall be validated.
     * 
     * @param asset the Asset object to be validated
     * @return boolean false if the on campus location information is invalid
     */
    private boolean validateLocationForMoving(Asset asset) {
        boolean valid = true;
        
        if (ObjectUtils.isNull(asset.getCampus())) {
            putFieldError(CamsPropertyConstants.Asset.CAMPUS_CODE, CamsKeyConstants.ERROR_INVALID_ASSET_CAMPUS_CODE);
            valid &= false;
        }
        
        if (ObjectUtils.isNull(asset.getBuilding())) {
            putFieldError(CamsPropertyConstants.Asset.BUILDING_CODE, CamsKeyConstants.ERROR_MANDATORY_ASSET_BUILDING_CODE, "moving code");
            valid &= false;
        }
        
        if (ObjectUtils.isNull(asset.getBuildingRoom())) {
            putFieldError(CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER, CamsKeyConstants.ERROR_MANDATORY_ASSET_BUILDING_ROOM_NO, "moving code");
            valid &= false;
        }
        
        return valid;
    }
    
    /**
     * Validates Asset On Campus loaction information when "req bldg" is 'Y'.
     * Campus and Building are mandatory and shall be validated.
     * 
     * @param asset the Asset object to be validated
     * @return boolean false if the on campus location information is invalid
     */
    private boolean validateLocationForReqBuilding(Asset asset) {
        boolean valid = true;

        if (ObjectUtils.isNull(asset.getCampus())) {
            putFieldError(CamsPropertyConstants.Asset.CAMPUS_CODE, CamsKeyConstants.ERROR_INVALID_ASSET_CAMPUS_CODE);
            valid &= false;
        }


        if (ObjectUtils.isNull(asset.getBuilding())) {
            putFieldError(CamsPropertyConstants.Asset.BUILDING_CODE, CamsKeyConstants.ERROR_MANDATORY_ASSET_BUILDING_CODE, "Required Building");
            valid &= false;
        }


        if (StringUtils.isNotEmpty(asset.getBuildingRoomNumber())) {
            if (ObjectUtils.isNull(asset.getBuildingRoom())) {
                putFieldError(CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER, CamsKeyConstants.ERROR_INVALID_ASSET_BUILDING_ROOM_NO);
                valid &= false;
            }
        }

        return valid;
    }

    /**
     * Validates Asset On Campus loaction information when neither "moving" and "req bldg" are set.
     * Campus is mandatory and shall be validated.
     * 
     * @param asset the Asset object to be validated
     * @return boolean false if the on campus location information is invalid
     */
    private boolean validateLocationForNonspecific(Asset asset) {
        boolean valid = true;

        if (ObjectUtils.isNull(asset.getCampus())) {
            putFieldError(CamsPropertyConstants.Asset.CAMPUS_CODE, CamsKeyConstants.ERROR_INVALID_ASSET_CAMPUS_CODE);
            valid &= false;
        }

        if (StringUtils.isNotEmpty(asset.getBuildingCode())) {
            if (ObjectUtils.isNull(asset.getBuilding())) {
                putFieldError(CamsPropertyConstants.Asset.BUILDING_CODE, CamsKeyConstants.ERROR_INVALID_ASSET_BUILDING_CODE);
                valid &= false;
            }
        }

        if (StringUtils.isNotEmpty(asset.getBuildingRoomNumber())) {
            if (StringUtils.isNotEmpty(asset.getBuildingRoomNumber())) {
                if (ObjectUtils.isNull(asset.getBuildingRoom())) {
                    putFieldError(CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER, CamsKeyConstants.ERROR_INVALID_ASSET_BUILDING_ROOM_NO);
                    valid &= false;
                }
            }
        }

        return valid;
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
                    putFieldError(ASSET_WARRANTY_WARRANTY_NUMBER, ERROR_INVALID_ASSET_WARRANTY_NO);
                    return false;
                }
            }
        }
        return true;
    }
}
