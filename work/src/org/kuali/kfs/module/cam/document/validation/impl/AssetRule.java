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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetComponent;
import org.kuali.module.cams.bo.AssetLocation;
import org.kuali.module.cams.bo.AssetWarranty;
import org.kuali.module.cams.lookup.valuefinder.NextAssetNumberFinder;
import org.kuali.module.cams.service.AssetComponentService;
import org.kuali.module.cams.service.AssetDateService;
import org.kuali.module.cams.service.AssetDispositionService;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.EquipmentLoanInfoService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.service.RetirementInfoService;
import org.kuali.module.cams.service.AssetLocationService.LocationField;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * AssetRule for Asset edit.
 */
public class AssetRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRule.class);
    private static final Map<LocationField, String> LOCATION_FIELD_MAP = new HashMap<LocationField, String>();
    static {
        LOCATION_FIELD_MAP.put(LocationField.CAMPUS_CODE, CamsPropertyConstants.Asset.CAMPUS_CODE);
        LOCATION_FIELD_MAP.put(LocationField.BUILDING_CODE, CamsPropertyConstants.Asset.BUILDING_CODE);
        LOCATION_FIELD_MAP.put(LocationField.ROOM_NUMBER, CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER);
        LOCATION_FIELD_MAP.put(LocationField.SUB_ROOM_NUMBER, CamsPropertyConstants.Asset.BUILDING_SUB_ROOM_NUMBER);
        LOCATION_FIELD_MAP.put(LocationField.CONTACT_NAME, CamsPropertyConstants.Asset.AssetLocation.CONTACT_NAME);
        LOCATION_FIELD_MAP.put(LocationField.STREET_ADDRESS, CamsPropertyConstants.Asset.AssetLocation.STREET_ADDRESS);
        LOCATION_FIELD_MAP.put(LocationField.CITY_NAME, CamsPropertyConstants.Asset.AssetLocation.CITY_NAME);
        LOCATION_FIELD_MAP.put(LocationField.STATE_CODE, CamsPropertyConstants.Asset.AssetLocation.STATE_CODE);
        LOCATION_FIELD_MAP.put(LocationField.ZIP_CODE, CamsPropertyConstants.Asset.AssetLocation.ZIP_CODE);
        LOCATION_FIELD_MAP.put(LocationField.COUNTRY_CODE, CamsPropertyConstants.Asset.AssetLocation.COUNTRY_CODE);
        LOCATION_FIELD_MAP.put(LocationField.LOCATION_TAB_KEY, CamsPropertyConstants.Asset.AssetLocation.VERSION_NUM);
    }

    private AssetService assetService = SpringContext.getBean(AssetService.class);
    private ParameterService parameterService = SpringContext.getBean(ParameterService.class);
    private PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
    private AssetDispositionService assetDispService = SpringContext.getBean(AssetDispositionService.class);
    private RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
    private EquipmentLoanInfoService equipmentLoanInfoService = SpringContext.getBean(EquipmentLoanInfoService.class);
    private AssetDateService assetDateService = SpringContext.getBean(AssetDateService.class);
    private AssetComponentService assetComponentService = SpringContext.getBean(AssetComponentService.class);
    private UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
    private AssetLocationService assetLocationService = SpringContext.getBean(AssetLocationService.class);

    private Asset newAsset;
    private Asset oldAsset;
    private boolean isFabrication;


    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        initializeAttributes(document);
        boolean valid = true;
        if (document.isNew()) {
            this.isFabrication = true;
            valid &= validateAccount();
            valid &= validateLocation();
            valid &= validateFabricationDetails();
        }
        else {
            setAssetComponentNumbers(newAsset);
            paymentSummaryService.calculateAndSetPaymentSummary(oldAsset);
            paymentSummaryService.calculateAndSetPaymentSummary(newAsset);

            assetDispService.setAssetDispositionHistory(oldAsset);
            assetDispService.setAssetDispositionHistory(newAsset);

            retirementInfoService.setRetirementInfo(oldAsset);
            retirementInfoService.setRetirementInfo(newAsset);

            equipmentLoanInfoService.setEquipmentLoanInfo(oldAsset);
            equipmentLoanInfoService.setEquipmentLoanInfo(newAsset);

            valid &= validateAccount();
            valid = processAssetValidation(document);
            valid &= validateWarrantyInformation(newAsset);

            valid &= super.processCustomSaveDocumentBusinessRules(document);
            if (valid) {
                assetDateService.checkAndUpdateLastInventoryDate(oldAsset, newAsset);
                assetDateService.checkAndUpdateDepreciationDate(oldAsset, newAsset);
            }
        }
        return valid;
    }


    private boolean validateFabricationDetails() {
        boolean valid = true;
        if (newAsset.getFabricationEstimatedTotalAmount() == null) {
            putFieldError(CamsPropertyConstants.Asset.FABRICATION_ESTIMATED_TOTAL_AMOUNT, CamsKeyConstants.ERROR_FABRICATION_ESTIMATED_TOTAL_AMOUNT_REQUIRED);
            valid &= false;
        }
        if (newAsset.getFabricationEstimatedTotalAmount() != null && newAsset.getFabricationEstimatedTotalAmount().isNegative()) {
            putFieldError(CamsPropertyConstants.Asset.FABRICATION_ESTIMATED_TOTAL_AMOUNT, CamsKeyConstants.ERROR_FABRICATION_ESTIMATED_TOTAL_AMOUNT_NEGATIVE);
            valid &= false;
        }
        if (newAsset.getEstimatedFabricationCompletionDate() == null) {
            putFieldError(CamsPropertyConstants.Asset.ESTIMATED_FABRICATION_COMPLETION_DATE, CamsKeyConstants.ERROR_ESTIMATED_FABRICATION_COMPLETION_DATE_REQUIRED);
            valid &= false;
        }
        if (newAsset.getEstimatedFabricationCompletionDate() != null && newAsset.getEstimatedFabricationCompletionDate().before(DateUtils.clearTimeFields(new Date()))) {
            putFieldError(CamsPropertyConstants.Asset.ESTIMATED_FABRICATION_COMPLETION_DATE, CamsKeyConstants.ERROR_ESTIMATED_FABRICATION_COMPLETION_DATE_PAST);
            valid &= false;
        }

        if (newAsset.getFabricationEstimatedRetentionYears() == null) {
            putFieldError(CamsPropertyConstants.Asset.FABRICATION_ESTIMATED_RETENTION_YEARS, CamsKeyConstants.ERROR_ESTIMATED_FABRICATION_LIFE_LIMIT_REQUIRED);
            valid &= false;
        }
        if (newAsset.getFabricationEstimatedRetentionYears() != null && newAsset.getFabricationEstimatedRetentionYears().intValue() < 0) {
            putFieldError(CamsPropertyConstants.Asset.FABRICATION_ESTIMATED_RETENTION_YEARS, CamsKeyConstants.ERROR_ESTIMATED_FABRICATION_LIFE_LIMIT_NEGATIVE);
            valid &= false;
        }
        return valid;
    }


    private boolean validateAccount() {
        boolean valid = true;
        Account organizationOwnerAccount = newAsset.getOrganizationOwnerAccount();
        if (organizationOwnerAccount != null && (organizationOwnerAccount.isExpired() || organizationOwnerAccount.isAccountClosedIndicator())) {
            // Account is not active
            putFieldError(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.ORGANIZATION_OWNER_ACCOUNT_INACTIVE);
            valid &= false;
        }
        return valid;
    }


    private void setAssetComponentNumbers(Asset asset) {

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

        // validate In-service Date
        if (assetService.isInServiceDateChanged(oldAsset, newAsset)) {
            valid &= validateInServiceDate();
        }
        return valid;
    }


    /**
     * Check if the new In-service Date is a valid University Date
     * 
     * @return
     */
    private boolean validateInServiceDate() {
        if (universityDateService.getFiscalYear(newAsset.getCapitalAssetInServiceDate()) == null) {
            putFieldError(CamsPropertyConstants.Asset.ASSET_DATE_OF_SERVICE, CamsKeyConstants.ERROR_INVALID_IN_SERVICE_DATE);
            return false;
        }
        return true;
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

            Map<String, Object> fieldValues = new HashMap<String, Object>();
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
        GlobalVariables.getErrorMap().addToErrorPath("document.newMaintainableObject");
        boolean isCapitalAsset = assetService.isCapitalAsset(newAsset);
        boolean valid = assetLocationService.validateLocation(LOCATION_FIELD_MAP, newAsset, isCapitalAsset, newAsset.getCapitalAssetType());
        GlobalVariables.getErrorMap().removeFromErrorPath("document.newMaintainableObject");

        if (valid && (this.isFabrication || isOffCampusLocationChanged())) {
            assetLocationService.updateOffCampusLocation(newAsset);
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

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        initializeAttributes(document);
        if (document.isNew() && newAsset.getCapitalAssetNumber() == null) {
            newAsset.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
            oldAsset.setCapitalAssetNumber(newAsset.getCapitalAssetNumber());
        }
        return true;
    }
}
