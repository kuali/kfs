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
import org.kuali.module.cams.service.EquipmentLoanInfoService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.service.RetirementInfoService;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * AssetRule for Asset edit.
 */
public class AssetRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRule.class);

    private Asset newAsset;
    private Asset oldAsset;

    private static Map<String, String[]> VALID_INVENTROY_STATUS_CODE_CHANGE = new HashMap<String, String[]>();
    static {
        try {
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE, new String[] { CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_NON_ACCESSIBLE, CamsConstants.InventoryStatusCode.CAPITAL_ASSET_SURPLUS_EQUIPEMENT });
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_NON_ACCESSIBLE, new String[] { CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE, CamsConstants.InventoryStatusCode.CAPITAL_ASSET_SURPLUS_EQUIPEMENT });
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_UNDER_CONSTRUCTION, null);
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_SURPLUS_EQUIPEMENT, new String[] { CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE, CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_NON_ACCESSIBLE });
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_RETIRED, null);
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_ACTIVE, new String[] { CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_RETIRED });
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_RETIRED, new String[] { CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_ACTIVE });
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_ACTIVE_2003, new String[] { CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_RETIRED_2003 });
            VALID_INVENTROY_STATUS_CODE_CHANGE.put(CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_RETIRED_2003, new String[] { CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_ACTIVE_2003 });
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        initializeAttributes(document);

        setAssetComponentNumbers(newAsset);

        PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
        paymentSummaryService.calculateAndSetPaymentSummary(oldAsset);
        paymentSummaryService.calculateAndSetPaymentSummary(newAsset);

        AssetDispositionService assetDispService = SpringContext.getBean(AssetDispositionService.class);
        assetDispService.setAssetDispositionHistory(oldAsset);
        assetDispService.setAssetDispositionHistory(newAsset);

        if (isOffCampusLocationChanged()) {
            AssetLocationService assetlocationService = SpringContext.getBean(AssetLocationService.class);
            assetlocationService.updateOffCampusLocation(newAsset);
        }

        RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
        retirementInfoService.setRetirementInfo(newAsset);
        retirementInfoService.setRetirementInfo(oldAsset);

        EquipmentLoanInfoService equipmentLoanInfoService = SpringContext.getBean(EquipmentLoanInfoService.class);
        equipmentLoanInfoService.setEquipmentLoanInfo(newAsset);
        equipmentLoanInfoService.setEquipmentLoanInfo(oldAsset);

        boolean valid = processAssetValidation(document);
        valid &= validateWarrantyInformation(newAsset);
        valid &= super.processCustomSaveDocumentBusinessRules(document);

        if (valid) {
            AssetDetailInformationService assetDetailInfoService = SpringContext.getBean(AssetDetailInformationService.class);
            assetDetailInfoService.checkAndUpdateLastInventoryDate(oldAsset, newAsset);
        }
        return valid;
    }


    private boolean isOffCampusLocationChanged() {
        boolean changed = false;
        AssetLocation copyLocation = oldAsset.getOffCampusLocation();
        AssetLocation newLocation = newAsset.getOffCampusLocation();

        if (!StringUtils.equalsIgnoreCase(newLocation.getAssetLocationContactName(), copyLocation.getAssetLocationContactName()) || !StringUtils.equalsIgnoreCase(newLocation.getAssetLocationStreetAddress(), copyLocation.getAssetLocationStreetAddress()) || !StringUtils.equalsIgnoreCase(newLocation.getAssetLocationCityName(), copyLocation.getAssetLocationCityName()) || !StringUtils.equalsIgnoreCase(newLocation.getAssetLocationStateCode(), copyLocation.getAssetLocationStateCode()) || !StringUtils.equalsIgnoreCase(newLocation.getAssetLocationZipCode(), copyLocation.getAssetLocationZipCode()) || !StringUtils.equalsIgnoreCase(newLocation.getAssetLocationCountryCode(), copyLocation.getAssetLocationCountryCode())) {
            changed = true;
        }
        return changed;
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
    boolean processAssetValidation(MaintenanceDocument document) {
        boolean valid = true;

        // validate Inventory Status Code.
        if (!StringUtils.equalsIgnoreCase(oldAsset.getInventoryStatusCode(), newAsset.getInventoryStatusCode())) {
            valid &= validateInventoryStatusCode();
        }

        // validate Asset Description
        if (!StringUtils.equalsIgnoreCase(oldAsset.getCapitalAssetDescription(), newAsset.getCapitalAssetDescription())) {
            valid &= validateAssetDescription();
        }

        // validate Asset Type Code
        if (!StringUtils.equalsIgnoreCase(oldAsset.getCapitalAssetTypeCode(), newAsset.getCapitalAssetTypeCode())) {
            valid &= validateAssetTypeCode();
        }

        // validate Vender Name.
        if (!StringUtils.equalsIgnoreCase(oldAsset.getVendorName(), newAsset.getVendorName())) {
            valid &= validateVenderName();
        }

        // validate Tag Number.
        if (!StringUtils.equalsIgnoreCase(oldAsset.getCampusTagNumber(), newAsset.getCampusTagNumber())) {
            valid &= validateTagNumber();
        }

        // validade On Campus location.
        if (isOnCampusLocationUpdated()) {
            valid &= validateOnCampusLocation(newAsset);
        }

        return valid;
    }

    /**
     * If asset is tagged and created in prior fiscal year, the Asset Type Code is not allowed to change
     * 
     * @return boolean
     */
    private boolean validateAssetTypeCode() {
        boolean valid = true;

        if (isAssetTaggedInPriorFiscalYear()) {
            putFieldError(CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE_CODE, CamsKeyConstants.ERROR_ASSET_TYPE_CODE_RESTRICT_CHANGE);
            valid &= false;
        }
        return valid;
    }

    /**
     * If asset is tagged and created in prior fiscal year, the Asset Description is not allowed to change
     * 
     * @return boolean
     */
    private boolean validateAssetDescription() {
        boolean valid = true;

        if (isAssetTaggedInPriorFiscalYear()) {
            putFieldError(CamsPropertyConstants.Asset.CAPITAL_ASSET_DESCRIPTION, CamsKeyConstants.ERROR_ASSET_DESCRIPTION_RESTRICT_CHANGE);
            valid &= false;
        }

        return valid;
    }


    private boolean isOnCampusLocationUpdated() {
        boolean updated = false;

        if (!StringUtils.equalsIgnoreCase(oldAsset.getCampusCode(), newAsset.getCampusCode()) || !StringUtils.equalsIgnoreCase(oldAsset.getBuildingCode(), newAsset.getBuildingCode()) || !StringUtils.equalsIgnoreCase(oldAsset.getBuildingRoomNumber(), newAsset.getBuildingRoomNumber())) {
            updated = true;
        }
        return updated;
    }


    /**
     * Validate Inventory Status Code Change
     */
    private boolean validateInventoryStatusCode() {
        boolean valid = true;

        if (!ArrayUtils.contains(VALID_INVENTROY_STATUS_CODE_CHANGE.get(oldAsset.getInventoryStatusCode()), newAsset.getInventoryStatusCode())) {
            putFieldError(CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS, CamsKeyConstants.ERROR_INVALID_ASSET_STATUS_CHANGE, new String[] { oldAsset.getInventoryStatusCode(), newAsset.getInventoryStatusCode() });
            valid &= false;
        }

        return valid;
    }


    /**
     * The Asset Type Code is allowed to be changed only: (1)If the tag number has not been assigned or (2)The asset is tagged, and
     * the asset created in the current fiscal year
     * 
     * @return
     */
    private boolean isAssetTaggedInPriorFiscalYear() {
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);

        return StringUtils.isNotBlank(oldAsset.getCampusTagNumber()) && ObjectUtils.isNotNull(oldAsset.getFinancialDocumentPostingYear()) && !universityDateService.getCurrentFiscalYear().equals(oldAsset.getFinancialDocumentPostingYear());
    }

    /**
     * @return boolean
     */
    private boolean isAssetTagged() {
        return ObjectUtils.isNotNull(oldAsset.getCampusTagNumber());
    }


    /**
     * The Tag Number check excludes value of "N" and retired assets. This method...
     * 
     * @return
     */
    private boolean isTagNumberCheckExclude() {
        String status = newAsset.getInventoryStatusCode();

        return StringUtils.equalsIgnoreCase(status, CamsConstants.InventoryStatusCode.CAPITAL_ASSET_RETIRED) || StringUtils.equalsIgnoreCase(status, CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_RETIRED) || StringUtils.equalsIgnoreCase(newAsset.getCampusTagNumber(), CamsConstants.NON_TAGGABLE_ASSET);
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


        if (isAssetTagged()) {
            putFieldError(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsKeyConstants.ERROR_TAG_NUMBER_RESTRICT_CHANGE);
            valid &= false;
        }
        else if (!isTagNumberCheckExclude()) {

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

    private boolean validateOnCampusLocation(Asset asset) {
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
     * Validates Asset On Campus loaction information when "moving" code is 'Y'. Campus, Building and Room are mandatory and shall
     * be validated.
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
     * Validates Asset On Campus loaction information when "req bldg" is 'Y'. Campus and Building are mandatory and shall be
     * validated.
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
     * Validates Asset On Campus loaction information when neither "moving" and "req bldg" are set. Campus is mandatory and shall be
     * validated.
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
