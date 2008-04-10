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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetComponent;
import org.kuali.module.cams.bo.AssetLocation;
import org.kuali.module.cams.bo.AssetType;
import org.kuali.module.cams.bo.AssetWarranty;
import org.kuali.module.cams.service.AssetComponentService;
import org.kuali.module.cams.service.AssetDetailInformationService;
import org.kuali.module.cams.service.AssetDispositionService;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.EquipmentLoanInfoService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.service.RetirementInfoService;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * AssetRule for Asset edit.
 */
public class AssetRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRule.class);
    private static AssetService assetService = SpringContext.getBean(AssetService.class);

    private Asset newAsset;
    private Asset oldAsset;
    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);


    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        initializeAttributes(document);

        boolean valid = false;
        
        if (newAsset.getCapitalAssetNumber() == null) {
            // TODO KULCAP-214 ... fabrication request rules go here
            valid = true;
        } else {
            setAssetComponentNumbers(newAsset);
    
            PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
            paymentSummaryService.calculateAndSetPaymentSummary(oldAsset);
            paymentSummaryService.calculateAndSetPaymentSummary(newAsset);
    
            AssetDispositionService assetDispService = SpringContext.getBean(AssetDispositionService.class);
            assetDispService.setAssetDispositionHistory(oldAsset);
            assetDispService.setAssetDispositionHistory(newAsset);
    
            RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
            retirementInfoService.setRetirementInfo(oldAsset);
            retirementInfoService.setRetirementInfo(newAsset);
    
            EquipmentLoanInfoService equipmentLoanInfoService = SpringContext.getBean(EquipmentLoanInfoService.class);
            equipmentLoanInfoService.setEquipmentLoanInfo(oldAsset);
            equipmentLoanInfoService.setEquipmentLoanInfo(newAsset);
    
            valid = processAssetValidation(document);
            valid &= validateWarrantyInformation(newAsset);
    
            valid &= super.processCustomSaveDocumentBusinessRules(document);
            if (valid) {
                AssetDetailInformationService assetDetailInfoService = SpringContext.getBean(AssetDetailInformationService.class);
                assetDetailInfoService.checkAndUpdateLastInventoryDate(oldAsset, newAsset);
                assetDetailInfoService.checkAndUpdateDepreciationDate(oldAsset, newAsset);
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
     * Validates Asset document.
     * 
     * @param document MaintenanceDocument instance
     * @return boolean false or true
     */
    private boolean processAssetValidation(MaintenanceDocument document) {
        boolean valid = true;

        // validate Inventory Status Code.
        if (!StringUtils.equalsIgnoreCase(oldAsset.getInventoryStatusCode(), newAsset.getInventoryStatusCode())) {
            valid &= validateInventoryStatusCode();
        }
        
        // validate Vendor Name.
        if (!StringUtils.equalsIgnoreCase(oldAsset.getVendorName(), newAsset.getVendorName())) {
            valid &= validateVendorName();
        }

        // validate Tag Number.
        if (!StringUtils.equalsIgnoreCase(oldAsset.getCampusTagNumber(), newAsset.getCampusTagNumber())) {
            valid &= validateTagNumber();
        }

        // validate location.
        if (isOnCampusLocationChanged() || isOffCampusLocationChanged()) {
            valid &= validateLocation();
        }

        return valid;
    }


    /**
     * Check if on campus fields got changed.
     * 
     * @return
     */
    private boolean isOnCampusLocationChanged() {
        boolean changed = false;

        if (!StringUtils.equalsIgnoreCase(oldAsset.getCampusCode(), newAsset.getCampusCode()) || !StringUtils.equalsIgnoreCase(oldAsset.getBuildingCode(), newAsset.getBuildingCode()) || !StringUtils.equalsIgnoreCase(oldAsset.getBuildingRoomNumber(), newAsset.getBuildingRoomNumber())) {
            changed = true;
        }
        return changed;
    }

    /**
     * Check if off campus fields got changed.
     * 
     * @return
     */
    private boolean isOffCampusLocationChanged() {
        boolean changed = false;
        AssetLocation oldLocation = oldAsset.getOffCampusLocation();
        AssetLocation newLocation = newAsset.getOffCampusLocation();

        if (!StringUtils.equalsIgnoreCase(newLocation.getAssetLocationContactName(), oldLocation.getAssetLocationContactName()) || !StringUtils.equalsIgnoreCase(newLocation.getAssetLocationStreetAddress(), oldLocation.getAssetLocationStreetAddress()) || !StringUtils.equalsIgnoreCase(newLocation.getAssetLocationCityName(), oldLocation.getAssetLocationCityName()) || !StringUtils.equalsIgnoreCase(newLocation.getAssetLocationStateCode(), oldLocation.getAssetLocationStateCode()) || !StringUtils.equalsIgnoreCase(newLocation.getAssetLocationZipCode(), oldLocation.getAssetLocationZipCode()) || !StringUtils.equalsIgnoreCase(newLocation.getAssetLocationCountryCode(), oldLocation.getAssetLocationCountryCode())) {
            changed = true;
        }
        return changed;
    }


    /**
     * Validate Inventory Status Code Change
     */
    private boolean validateInventoryStatusCode() {
        return parameterService.getParameterEvaluator(Asset.class, CamsConstants.Parameters.VALID_INVENTROY_STATUS_CODE_CHANGE, CamsConstants.Parameters.INVALID_INVENTROY_STATUS_CODE_CHANGE, oldAsset.getInventoryStatusCode(), newAsset.getInventoryStatusCode()).evaluateAndAddError(newAsset.getClass(), CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS);
    }


    private void initializeAttributes(MaintenanceDocument document) {
        if (newAsset == null) {
            newAsset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        }
        if (oldAsset == null) {
            oldAsset = (Asset) document.getOldMaintainableObject().getBusinessObject();
        }
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


        if (!assetService.isTagNumberCheckExclude(newAsset)) {

            Map fieldValues = new HashMap();
            fieldValues.put(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, newAsset.getCampusTagNumber());

            Collection<Asset> results = getBoService().findMatching(Asset.class, fieldValues);

            for (Asset asset : results) {
                if (!asset.getCapitalAssetNumber().equals(newAsset.getCapitalAssetNumber())) {
                    anyFound = true;
                    break;
                }
            }

            if (anyFound) {
                putFieldError(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsKeyConstants.ERROR_TAG_NUMBER_DUPLICATE);
                valid &= false;
            }
        }

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

        if (assetService.isCapitalAsset(newAsset) && StringUtils.isBlank(newAsset.getVendorName())) {
            putFieldError(CamsPropertyConstants.Asset.VENDOR_NAME, CamsKeyConstants.ERROR_CAPITAL_ASSET_VENDOR_NAME_REQUIRED);
            valid &= false;

        }
        return valid;
    }



    /**
     * Validate Asset Location fields
     * 
     * @param asset
     * @return
     */
    private boolean validateLocation() {
        boolean valid = true;

        if (assetService.isCapitalAsset(newAsset)) {
            valid &= validateCapitalAssetLocation(newAsset);
        }
        else {
            valid &= validateNonCapitalAssetLocation(newAsset);
        }

        if (valid && isOffCampusLocationChanged()) {
            AssetLocationService assetlocationService = SpringContext.getBean(AssetLocationService.class);
            assetlocationService.updateOffCampusLocation(newAsset);
        }
        return valid;
    }


    /**
     * Validates capital asset location fields.
     * 
     * @param asset the Asset object to be validated
     * @return boolean false if the on campus location information is invalid
     */

    private boolean validateCapitalAssetLocation(Asset asset) {
        boolean valid = true;

        AssetType assetType = (AssetType) asset.getCapitalAssetType();

        if (assetType.isMovingIndicator()) {
            valid &= validateLocationForMovable(asset);
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
     * Validate non capital asset location fields. All fields are optional except campus code. User can enter either on campus
     * fields or off campus fields, but not both.
     * 
     * @param asset
     * @return
     */
    private boolean validateNonCapitalAssetLocation(Asset asset) {
        boolean valid = true;

        if (StringUtils.isNotBlank(asset.getBuildingCode()) || StringUtils.isNotBlank(asset.getBuildingRoomNumber())) {
            valid &= validateOffCampusLocationNotEntered(asset);
        }
        else if (isOffCampusLocationChanged() && assetService.isOffCampusLocationEntered(asset)) {
            valid &= validateOffCampusLocationAllEntered(asset);
        }

        return valid;
    }


    /**
     * when asset type code with the flag "moving code" checked, this asset must have a location for it, either on campus or off
     * campus. i) if one of these two fields(building code and room number) is entered, it is supposed to be on campus. On campus
     * fields are: building code - mandatory, valid room number - mandatory, valid. At this situation, all off campus fields are not
     * allowed to enter. ii) if both of these fields:building code and room number are not entered, the off campus fields are
     * required.
     * 
     * @param asset the Asset object to be validated
     * @return boolean false if the on campus location information is invalid
     */
    private boolean validateLocationForMovable(Asset asset) {
        boolean valid = true;

        if (StringUtils.isBlank(asset.getBuildingCode()) && StringUtils.isBlank(asset.getBuildingRoomNumber())) {
            // off campus and only off campus location could entered
            valid &= validateOffCampusLocationAllEntered(asset);
        }
        else {
            // on campus and only on campus location could entered
            valid &= validateOnCampusLocationEntered(asset);
            valid &= validateOffCampusLocationNotEntered(asset);
        }
        return valid;
    }

    /**
     * Required fields for off campus location: contact name, street, city,state and zip code.
     * 
     * @param asset
     * @return
     */
    private boolean validateOffCampusLocationAllEntered(Asset asset) {
        boolean valid = true;
        AssetLocation offCampus = asset.getOffCampusLocation();

        if (StringUtils.isBlank(offCampus.getAssetLocationContactName())) {
            putFieldError(CamsPropertyConstants.AssetLocation.ASSET_LOCATION_CONTACT_NAME, CamsKeyConstants.ERROR_ASSET_OFF_CAMPUS_CONTACT_NAME_NULL);
            valid &= false;
        }

        if (StringUtils.isBlank(offCampus.getAssetLocationStreetAddress())) {
            putFieldError(CamsPropertyConstants.AssetLocation.ASSET_LOCATION_STREET_ADDRESS, CamsKeyConstants.ERROR_ASSET_OFF_CAMPUS_STREET_NULL);
            valid &= false;
        }

        if (StringUtils.isBlank(offCampus.getAssetLocationCityName())) {
            putFieldError(CamsPropertyConstants.AssetLocation.ASSET_LOCATION_CITY_NAME, CamsKeyConstants.ERROR_ASSET_OFF_CAMPUS_CITY_NULL);
            valid &= false;
        }

        if (StringUtils.isBlank(offCampus.getAssetLocationStateCode())) {
            putFieldError(CamsPropertyConstants.AssetLocation.ASSET_LOCATION_STATE_CODE, CamsKeyConstants.ERROR_ASSET_OFF_CAMPUS_STATE_NULL);
            valid &= false;
        }

        if (StringUtils.isBlank(offCampus.getAssetLocationZipCode())) {
            putFieldError(CamsPropertyConstants.AssetLocation.ASSET_LOCATION_ZIP_CODE, CamsKeyConstants.ERROR_ASSET_OFF_CAMPUS_ZIPCODE_NULL);
            valid &= false;
        }

        return valid;
    }

    /**
     * Validate none of the off campus location are set: contact name, street, city,state, zip code and country.
     * 
     * @param asset
     * @return
     */
    private boolean validateOffCampusLocationNotEntered(Asset asset) {
        boolean valid = true;
        AssetLocation offCampusLoc = asset.getOffCampusLocation();

        if (StringUtils.isNotBlank(offCampusLoc.getAssetLocationContactName())) {
            putFieldError(CamsPropertyConstants.AssetLocation.ASSET_LOCATION_CONTACT_NAME, CamsKeyConstants.ERROR_ASSET_OFF_CAMPUS_CONTACT_NAME_NOT_NULL);
            valid &= false;
        }

        if (StringUtils.isNotBlank(offCampusLoc.getAssetLocationStreetAddress())) {
            putFieldError(CamsPropertyConstants.AssetLocation.ASSET_LOCATION_STREET_ADDRESS, CamsKeyConstants.ERROR_ASSET_OFF_CAMPUS_STREET_NOT_NULL);
            valid &= false;
        }

        if (StringUtils.isNotBlank(offCampusLoc.getAssetLocationCityName())) {
            putFieldError(CamsPropertyConstants.AssetLocation.ASSET_LOCATION_CITY_NAME, CamsKeyConstants.ERROR_ASSET_OFF_CAMPUS_CITY_NOT_NULL);
            valid &= false;
        }

        if (StringUtils.isNotBlank(offCampusLoc.getAssetLocationStateCode())) {
            putFieldError(CamsPropertyConstants.AssetLocation.ASSET_LOCATION_STATE_CODE, CamsKeyConstants.ERROR_ASSET_OFF_CAMPUS_STATE_NOT_NULL);
            valid &= false;
        }

        if (StringUtils.isNotBlank(offCampusLoc.getAssetLocationZipCode())) {
            putFieldError(CamsPropertyConstants.AssetLocation.ASSET_LOCATION_ZIP_CODE, CamsKeyConstants.ERROR_ASSET_OFF_CAMPUS_ZIPCODE_NOT_NULL);
            valid &= false;
        }

        if (StringUtils.isNotBlank(offCampusLoc.getAssetLocationCountryCode())) {
            putFieldError(CamsPropertyConstants.AssetLocation.ASSET_LOCATION_COUNTRY_CODE, CamsKeyConstants.ERROR_ASSET_OFF_CAMPUS_COUNTRY_NOT_NULL);
            valid &= false;
        }

        return valid;
    }


    /**
     * Required fields for on campus: building code, building room number.
     * 
     * @param asset
     * @return
     */
    private boolean validateOnCampusLocationEntered(Asset asset) {
        boolean valid = true;

        if (StringUtils.isBlank(asset.getBuildingCode())) {
            putFieldError(CamsPropertyConstants.Asset.BUILDING_CODE, CamsKeyConstants.ERROR_ASSET_BUILDING_CODE_NULL);
            valid &= false;
        }

        if (StringUtils.isBlank(asset.getBuildingRoomNumber())) {
            putFieldError(CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER, CamsKeyConstants.ERROR_ASSET_BUILDING_ROOMNO_NULL);
            valid &= false;
        }
        return valid;
    }


    /**
     * when asset type code with the flag "reg bldg" checked, this asset must have a building code. No room number are allowed to
     * enter. Off campus fields are not allowed to enter either.
     * 
     * @param asset the Asset object to be validated
     * @return boolean false if the on campus location information is invalid
     */
    private boolean validateLocationForReqBuilding(Asset asset) {
        boolean valid = true;

        if (StringUtils.isBlank(asset.getBuildingCode())) {
            putFieldError(CamsPropertyConstants.Asset.BUILDING_CODE, CamsKeyConstants.ERROR_ASSET_BUILDING_CODE_NULL);
            valid &= false;
        }


        if (StringUtils.isNotBlank(asset.getBuildingRoomNumber())) {
            putFieldError(CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER, CamsKeyConstants.ERROR_ASSET_BUILDING_ROOMNO_NOT_NULL);
            valid &= false;
        }

        valid &= validateOffCampusLocationNotEntered(asset);

        return valid;
    }

    /**
     * when Any asst type code with the flags "moving code, and reg bldg" unchecked, no building code and room number are allowed to
     * enter. But campus code and off campus fields are allowed to enter together.
     * 
     * @param asset the Asset object to be validated
     * @return boolean false if the on campus location information is invalid
     */
    private boolean validateLocationForNonspecific(Asset asset) {
        boolean valid = true;

        if (StringUtils.isNotBlank(asset.getBuildingCode())) {
            putFieldError(CamsPropertyConstants.Asset.BUILDING_CODE, CamsKeyConstants.ERROR_ASSET_BUILDING_CODE_NOT_NULL);
            valid &= false;
        }

        if (StringUtils.isNotBlank(asset.getBuildingRoomNumber())) {
            putFieldError(CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER, CamsKeyConstants.ERROR_ASSET_BUILDING_ROOMNO_NOT_NULL);
            valid &= false;
        }

        if (isOffCampusLocationChanged() && assetService.isOffCampusLocationEntered(asset)) {
            valid &= validateOffCampusLocationAllEntered(asset);
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
