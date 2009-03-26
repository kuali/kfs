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
package org.kuali.kfs.module.cam.document.validation.impl;

import static org.kuali.kfs.module.cam.CamsKeyConstants.ERROR_INVALID_ASSET_WARRANTY_NO;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.Asset.ASSET_WARRANTY_WARRANTY_NUMBER;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetComponent;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.module.cam.businessobject.AssetRepairHistory;
import org.kuali.kfs.module.cam.businessobject.AssetWarranty;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.service.AssetComponentService;
import org.kuali.kfs.module.cam.document.service.AssetDateService;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.module.cam.document.service.RetirementInfoService;
import org.kuali.kfs.module.cam.document.service.AssetLocationService.LocationField;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

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
    }

    private AssetService assetService = SpringContext.getBean(AssetService.class);
    private ParameterService parameterService = SpringContext.getBean(ParameterService.class);
    private PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
    private RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
    private EquipmentLoanOrReturnService equipmentLoanOrReturnService = SpringContext.getBean(EquipmentLoanOrReturnService.class);
    private AssetDateService assetDateService = SpringContext.getBean(AssetDateService.class);
    private AssetComponentService assetComponentService = SpringContext.getBean(AssetComponentService.class);
    private UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
    private AssetLocationService assetLocationService = SpringContext.getBean(AssetLocationService.class);
    private DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

    private Asset newAsset;
    private Asset oldAsset;
    private boolean isFabrication;


    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        initializeAttributes(document);
        boolean valid = true;
        if (SpringContext.getBean(AssetService.class).isAssetFabrication(document)) {
            this.isFabrication = true;
            valid &= validateAccount();
            valid &= validateLocation();
            valid &= validateFabricationDetails();
        }
        else {
            setAssetComponentNumbers(newAsset);
            paymentSummaryService.calculateAndSetPaymentSummary(oldAsset);
            paymentSummaryService.calculateAndSetPaymentSummary(newAsset);

            assetService.setSeparateHistory(oldAsset);
            assetService.setSeparateHistory(newAsset);

            retirementInfoService.setRetirementInfo(oldAsset);
            retirementInfoService.setRetirementInfo(newAsset);

            equipmentLoanOrReturnService.setEquipmentLoanInfo(oldAsset);
            equipmentLoanOrReturnService.setEquipmentLoanInfo(newAsset);

            valid &= processAssetValidation(document);
            valid &= validateWarrantyInformation(newAsset);
            valid &= validateDepreciationData(newAsset);

            valid &= super.processCustomSaveDocumentBusinessRules(document);
            if (valid) {
                assetDateService.checkAndUpdateLastInventoryDate(oldAsset, newAsset);
                assetDateService.checkAndUpdateDepreciationDate(oldAsset, newAsset);
            }
        }
        return valid;
    }

    /**
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument documentCopy, String collectionName, PersistableBusinessObject bo) {
        boolean success = true;
        
        // get all incidentDates from AssetRepairHistory collection and check for duplicate dates.
        if (collectionName.equals(CamsConstants.Asset.COLLECTION_ID_ASSET_REPAIR_HISTORY)) {
            
            Asset asset = (Asset) documentCopy.getNewMaintainableObject().getBusinessObject();
            Set<Date> incidentDateSet = new HashSet<Date>();
            
            for (AssetRepairHistory assetRepairHistory : asset.getAssetRepairHistory()) {
                if (assetRepairHistory.getIncidentDate() != null) {
                    incidentDateSet.add(assetRepairHistory.getIncidentDate());
                }
            }
    
            AssetRepairHistory assetRepairHistoryDetails = (AssetRepairHistory) bo;
            
            success &= checkDuplicateIncidentDate(assetRepairHistoryDetails, incidentDateSet);
    
            return success & super.processCustomAddCollectionLineBusinessRules(documentCopy, collectionName, bo);
        }
        
        return success ;
    }
    
    /**
     * Check for duplicate incident dates within the Repair History section
     * 
     * @param assetRepairHistory
     * @param incidentDateSet
     * @return boolean
     */
    private boolean checkDuplicateIncidentDate(AssetRepairHistory assetRepairHistory, Set<Date> incidentDateSet) {
        boolean success = true;
        
        if (!incidentDateSet.add(assetRepairHistory.getIncidentDate())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRepairHistory.INCIDENT_DATE, CamsKeyConstants.AssetRepairHistory.ERROR_DUPLICATE_INCIDENT_DATE);
            success &= false;
        }

        return success;
    }
    
    /**
     * Validate fabrication details
     * 
     * @return boolean
     */
    private boolean validateFabricationDetails() {
        /**
         * Please don't remove this validation, forcing required fields from DD file is not possible and will break asset edit
         * screen, so please leave this validation here.
         */
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
        if (newAsset.getEstimatedFabricationCompletionDate() != null && newAsset.getEstimatedFabricationCompletionDate().before(DateUtils.clearTimeFields(dateTimeService.getCurrentDate()))) {
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

    /**
     * Validate account
     * 
     * @return boolean
     */
    private boolean validateAccount() {
        boolean valid = true;
        Account currentOwnerAccount = newAsset.getOrganizationOwnerAccount();
        Account previoudOwnerAccount = oldAsset.getOrganizationOwnerAccount();
        // check if values changed, if not return
        if (ObjectUtils.isNotNull(previoudOwnerAccount) && ObjectUtils.isNotNull(currentOwnerAccount) && previoudOwnerAccount.getChartOfAccountsCode().equals(currentOwnerAccount.getChartOfAccountsCode()) && previoudOwnerAccount.getAccountNumber().equals(currentOwnerAccount.getAccountNumber())) {
            return valid;

        }
        else if (ObjectUtils.isNotNull(currentOwnerAccount) && (currentOwnerAccount.isExpired() || !currentOwnerAccount.isActive())) {
            // Account is not active
            putFieldError(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.ORGANIZATION_OWNER_ACCOUNT_INACTIVE);
            valid &= false;
        }
        return valid;
    }

    /**
     * Set asset component numbers
     * 
     * @param asset
     */
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
            valid &= validateInventoryStatusCode(oldAsset.getInventoryStatusCode(), newAsset.getInventoryStatusCode());
        }

        // validate Organization Owner Account Number
        if (!StringUtils.equalsIgnoreCase(oldAsset.getOrganizationOwnerAccountNumber(), newAsset.getOrganizationOwnerAccountNumber())) {
            valid &= validateAccount();
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
    private boolean validateInventoryStatusCode(String oldInventoryStatusCode, String newInventoryStatusCode) {
        boolean valid = true;
        if (assetService.isCapitalAsset(oldAsset) && assetService.isAssetRetired(newAsset)) {
            // disallow retire capital asset.
            putFieldError(CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS, CamsKeyConstants.ERROR_ASSET_RETIRED_CAPITAL);
            valid = false;
        }
        else {
            // validate inventory status change per system parameter.
            GlobalVariables.getErrorMap().addToErrorPath(MAINTAINABLE_ERROR_PATH);
            valid &= parameterService.getParameterEvaluator(Asset.class, CamsConstants.Parameters.VALID_INVENTROY_STATUS_CODE_CHANGE, CamsConstants.Parameters.INVALID_INVENTROY_STATUS_CODE_CHANGE, oldAsset.getInventoryStatusCode(), newAsset.getInventoryStatusCode()).evaluateAndAddError(newAsset.getClass(), CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS);
            GlobalVariables.getErrorMap().removeFromErrorPath(MAINTAINABLE_ERROR_PATH);
        }
        return valid;
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
                    putFieldError(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_DUPLICATE_TAG_NUMBER_FOUND, new String[] { newAsset.getCampusTagNumber(), asset.getCapitalAssetNumber().toString(), newAsset.getCapitalAssetNumber().toString() });
                    valid &= false;
                    break;
                }
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

    /**
     * 
     * validates depreciation data
     * @param asset
     * @return boolean
     */
    private boolean validateDepreciationData(Asset asset) {
        if (asset.getSalvageAmount() == null) {
            asset.setSalvageAmount(KualiDecimal.ZERO);
        }
        
        if (asset.getBaseAmount() == null) {
            asset.setBaseAmount(KualiDecimal.ZERO);
        }

        //If the salvage amount is greater than the base amount, then data is invalid
        if (asset.getSalvageAmount().compareTo(asset.getBaseAmount()) > 0 ) {
            putFieldError(CamsPropertyConstants.Asset.SALVAGE_AMOUNT, CamsKeyConstants.Asset.ERROR_INVALID_SALVAGE_AMOUNT);
            return false;
        } else {
            return true;
        }
    }
    
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        initializeAttributes(document);
        if (SpringContext.getBean(AssetService.class).isAssetFabrication(document) && newAsset.getCapitalAssetNumber() == null) {
            newAsset.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
            oldAsset.setCapitalAssetNumber(newAsset.getCapitalAssetNumber());
            newAsset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));
            oldAsset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));
        }
        return true;
    }

    /**
     * Convenience method to append the path prefix
     */
    public TypedArrayList putError(String propertyName, String errorKey, String... errorParameters) {
        return GlobalVariables.getErrorMap().putError(CamsConstants.DOCUMENT_PATH + "." + propertyName, errorKey, errorParameters);
    }

}
