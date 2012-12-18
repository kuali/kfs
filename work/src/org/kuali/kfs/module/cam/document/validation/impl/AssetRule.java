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
package org.kuali.kfs.module.cam.document.validation.impl;

import static org.kuali.kfs.module.cam.CamsKeyConstants.ERROR_INVALID_ASSET_WARRANTY_NO;
import static org.kuali.kfs.module.cam.CamsKeyConstants.PreTag.ERROR_PRE_TAG_INVALID_REPRESENTATIVE_ID;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.Asset.ASSET_REPRESENTATIVE;
import static org.kuali.kfs.module.cam.CamsPropertyConstants.Asset.ASSET_WARRANTY_WARRANTY_NUMBER;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetComponent;
import org.kuali.kfs.module.cam.businessobject.AssetFabrication;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.module.cam.businessobject.AssetRepairHistory;
import org.kuali.kfs.module.cam.businessobject.AssetWarranty;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.service.AssetComponentService;
import org.kuali.kfs.module.cam.document.service.AssetDateService;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetLocationService.LocationField;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.module.cam.document.service.RetirementInfoService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.AutoPopulatingList;

/**
 * AssetRule for Asset edit.
 */
public class AssetRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRule.class);
    protected static final Map<LocationField, String> LOCATION_FIELD_MAP = new HashMap<LocationField, String>();
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

    // protected AgencyService agencyService = SpringContext.getBean(AgencyService.class);
    protected AssetService assetService = SpringContext.getBean(AssetService.class);
    protected ParameterService parameterService = SpringContext.getBean(ParameterService.class);
    protected PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
    protected RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
    protected EquipmentLoanOrReturnService equipmentLoanOrReturnService = SpringContext.getBean(EquipmentLoanOrReturnService.class);
    protected AssetDateService assetDateService = SpringContext.getBean(AssetDateService.class);
    protected AssetComponentService assetComponentService = SpringContext.getBean(AssetComponentService.class);
    protected UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
    protected AssetLocationService assetLocationService = SpringContext.getBean(AssetLocationService.class);
    protected DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

    protected Asset newAsset;
    protected Asset oldAsset;
    protected boolean isFabrication;


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
            valid &= checkAssetDepreciationMethodChange();

            valid &= super.processCustomSaveDocumentBusinessRules(document);
            if (valid) {
                assetDateService.checkAndUpdateLastInventoryDate(oldAsset, newAsset);
                assetDateService.checkAndUpdateDepreciationDate(oldAsset, newAsset);
                assetDateService.checkAndUpdateFiscalYearAndPeriod(oldAsset, newAsset);
            }

            valid &= checkAssetLocked(document);
        }
        valid &= validateManufacturer(newAsset);
        return valid;
    }

    /**
     * Check if asset is locked by other document.
     *
     * @param document
     * @param valid
     * @return
     */
    protected boolean checkAssetLocked(MaintenanceDocument document) {
        Asset asset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        return !getCapitalAssetManagementModuleService().isAssetLocked(retrieveAssetNumberForLocking(asset), CamsConstants.DocumentTypeName.ASSET_EDIT, document.getDocumentNumber());
    }

    /**
     * Retrieve asset numbers need to be locked.
     *
     * @return
     */
    protected List<Long> retrieveAssetNumberForLocking(Asset asset) {
        List<Long> capitalAssetNumbers = new ArrayList<Long>();
        if (asset.getCapitalAssetNumber() != null) {
            capitalAssetNumbers.add(asset.getCapitalAssetNumber());
        }
        return capitalAssetNumbers;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
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

        return success;
    }

    /**
     * Check for duplicate incident dates within the Repair History section
     *
     * @param assetRepairHistory
     * @param incidentDateSet
     * @return boolean
     */
    protected boolean checkDuplicateIncidentDate(AssetRepairHistory assetRepairHistory, Set<Date> incidentDateSet) {
        boolean success = true;

        if (!incidentDateSet.add(assetRepairHistory.getIncidentDate())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetRepairHistory.INCIDENT_DATE, CamsKeyConstants.AssetRepairHistory.ERROR_DUPLICATE_INCIDENT_DATE);
            success &= false;
        }

        return success;
    }

    /**
     * Validate fabrication details
     *
     * @return boolean
     */
    protected boolean validateFabricationDetails() {
        /**
         * Please don't remove this validation, forcing required fields from DD file is not possible and will break asset edit
         * screen, so please leave this validation here.
         */
        boolean valid = true;
        if (newAsset.getFabricationEstimatedTotalAmount() != null && newAsset.getFabricationEstimatedTotalAmount().isNegative()) {
            putFieldError(CamsPropertyConstants.Asset.FABRICATION_ESTIMATED_TOTAL_AMOUNT, CamsKeyConstants.ERROR_FABRICATION_ESTIMATED_TOTAL_AMOUNT_NEGATIVE);
            valid &= false;
        }
        if (newAsset.getEstimatedFabricationCompletionDate() != null && newAsset.getEstimatedFabricationCompletionDate().before(KfsDateUtils.clearTimeFields(dateTimeService.getCurrentDate()))) {
            putFieldError(CamsPropertyConstants.Asset.ESTIMATED_FABRICATION_COMPLETION_DATE, CamsKeyConstants.ERROR_ESTIMATED_FABRICATION_COMPLETION_DATE_PAST);
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
    protected boolean validateAccount() {
        boolean valid = true;
        Account currentOwnerAccount = newAsset.getOrganizationOwnerAccount();
        Account previoudOwnerAccount = oldAsset.getOrganizationOwnerAccount();

        // Account is valid?
        if (ObjectUtils.isNull(currentOwnerAccount) && StringUtils.isNotBlank(newAsset.getOrganizationOwnerAccountNumber())) {
            putFieldError(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.ORGANIZATION_OWNER_ACCOUNT_INVALID);
            valid &= false;
        }

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
     *
     * Validate asset representative
     * @return boolean
     */
    protected boolean validateAssetRepresentative() {
        boolean valid = true;
        Person assetRepresentative = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(newAsset.getAssetRepresentative().getPrincipalName());
        if(ObjectUtils.isNull(assetRepresentative)) {
            putFieldError(ASSET_REPRESENTATIVE, ERROR_PRE_TAG_INVALID_REPRESENTATIVE_ID);
            valid = false;
        }
        return valid;
    }

    /**
     * Set asset component numbers
     *
     * @param asset
     */
    protected void setAssetComponentNumbers(Asset asset) {
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
    protected boolean processAssetValidation(MaintenanceDocument document) {
        boolean valid = true;

        // validate Inventory Status Code.
        if (!StringUtils.equalsIgnoreCase(oldAsset.getInventoryStatusCode(), newAsset.getInventoryStatusCode())) {
            valid &= validateInventoryStatusCode(oldAsset.getInventoryStatusCode(), newAsset.getInventoryStatusCode());
        }

        // validate Organization Owner Account Number
        if (!StringUtils.equalsIgnoreCase(oldAsset.getOrganizationOwnerAccountNumber(), newAsset.getOrganizationOwnerAccountNumber())) {
            valid &= validateAccount();
        }

        // validate asset representative name
        if (!StringUtils.equalsIgnoreCase(oldAsset.getAssetRepresentative().getPrincipalName(), newAsset.getAssetRepresentative().getPrincipalName())) {
            valid &= validateAssetRepresentative();
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
        valid &= validateLocation();

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
    protected boolean validateInServiceDate() {
        boolean valid = true;
        // if asset already starts depreciation, the user can't blank in-service date.
        if (ObjectUtils.isNull(newAsset.getCapitalAssetInServiceDate()) && assetService.isAssetDepreciationStarted(oldAsset)) {
            putFieldError(CamsPropertyConstants.Asset.ASSET_DATE_OF_SERVICE, CamsKeyConstants.ERROR_BLANK_IN_SERVICE_DATE_DISALLOWED);
            valid = false;
        }

        else if (ObjectUtils.isNotNull(newAsset.getCapitalAssetInServiceDate()) && universityDateService.getFiscalYear(newAsset.getCapitalAssetInServiceDate()) == null) {
            putFieldError(CamsPropertyConstants.Asset.ASSET_DATE_OF_SERVICE, CamsKeyConstants.ERROR_INVALID_IN_SERVICE_DATE);
            valid = false;
        }
        return valid;
    }


    /**
     * Check if off campus fields has changed.
     *
     * @return
     */
    protected boolean isOffCampusLocationChanged() {
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
    protected boolean validateInventoryStatusCode(String oldInventoryStatusCode, String newInventoryStatusCode) {
        boolean valid = true;
        if (assetService.isCapitalAsset(oldAsset) && assetService.isAssetRetired(newAsset)) {
            // disallow retire capital asset.
            putFieldError(CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS, CamsKeyConstants.ERROR_ASSET_RETIRED_CAPITAL);
            valid = false;
        }
        else {
            // validate inventory status change per system parameter.
            GlobalVariables.getMessageMap().addToErrorPath(MAINTAINABLE_ERROR_PATH);
            valid &= /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Asset.class, CamsConstants.Parameters.VALID_INVENTROY_STATUS_CODE_CHANGE, CamsConstants.Parameters.INVALID_INVENTROY_STATUS_CODE_CHANGE, oldAsset.getInventoryStatusCode(), newAsset.getInventoryStatusCode()).evaluateAndAddError(newAsset.getClass(), CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS);
            GlobalVariables.getMessageMap().removeFromErrorPath(MAINTAINABLE_ERROR_PATH);
        }
        return valid;
    }

    protected void initializeAttributes(MaintenanceDocument document) {
        if (newAsset == null) {
            newAsset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        }
        if (oldAsset == null) {
            oldAsset = (Asset) document.getOldMaintainableObject().getBusinessObject();
        }
        // for fabrication
        if (oldAsset == null) {
            oldAsset = newAsset;
        }
    }

    /**
     * If the tag number has not been assigned, the departmental user will be able to update the tag number. The Tag Number shall be
     * verified that the tag number does not exist on another asset.
     *
     * @param asset
     * @return
     */
    protected boolean validateTagNumber() {
        boolean valid = true;
        boolean anyFound = false;

        if (!assetService.isTagNumberCheckExclude(newAsset)) {

            Map<String, Object> fieldValues = new HashMap<String, Object>();
            if (ObjectUtils.isNotNull(newAsset.getCampusTagNumber())) {
                fieldValues.put(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, newAsset.getCampusTagNumber().toUpperCase());
                Collection<Asset> results = getBoService().findMatching(Asset.class, fieldValues);

                for (Asset asset : results) {
                    if (!asset.getCapitalAssetNumber().equals(newAsset.getCapitalAssetNumber())) {
                        // KFSMI-6149 - do not invalidate if the asset from the database is retired
                        if (StringUtils.isBlank(asset.getRetirementReasonCode())) {
                            putFieldError(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_DUPLICATE_TAG_NUMBER_FOUND, new String[] { newAsset.getCampusTagNumber(), asset.getCapitalAssetNumber().toString(), newAsset.getCapitalAssetNumber().toString() });
                            valid &= false;
                            LOG.info("The asset tag number [" + newAsset.getCampusTagNumber().toUpperCase() + "] is a duplicate of asset number [" + asset.getCapitalAssetNumber().toString() + "]'s tag number");
                        }
                        else {
                            LOG.info("Although the asset tag number [" + newAsset.getCampusTagNumber().toUpperCase() + "] is a duplicate of asset number [" + asset.getCapitalAssetNumber().toString() + "]'s tag number, the old asset has already been retired");
                        }
                    }
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
    protected boolean validateVendorName() {
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
    protected boolean validateLocation() {
        GlobalVariables.getMessageMap().addToErrorPath("document.newMaintainableObject");
        boolean isCapitalAsset = assetService.isCapitalAsset(newAsset);
        boolean valid = assetLocationService.validateLocation(LOCATION_FIELD_MAP, newAsset, isCapitalAsset, newAsset.getCapitalAssetType());
        GlobalVariables.getMessageMap().removeFromErrorPath("document.newMaintainableObject");

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
    protected boolean validateWarrantyInformation(Asset asset) {
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
     * validates depreciation data
     *
     * @param asset
     * @return boolean
     */
    protected boolean validateDepreciationData(Asset asset) {
        //do not run the validation if the total cost amount is negative.
        //see KFSMI-9266 for details.
        if (ObjectUtils.isNotNull(asset.getTotalCostAmount()) && asset.getTotalCostAmount().compareTo(KualiDecimal.ZERO) < 0) {
            return true;
        }
        if (asset.getSalvageAmount() == null) {
            asset.setSalvageAmount(KualiDecimal.ZERO);
        }

        if (asset.getBaseAmount() == null) {
            asset.setBaseAmount(KualiDecimal.ZERO);
        }

        // If the salvage amount is greater than the base amount, then data is invalid
        if (asset.getSalvageAmount().compareTo(asset.getBaseAmount()) > 0) {
            GlobalVariables.getMessageMap().putWarning(MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.Asset.SALVAGE_AMOUNT, CamsKeyConstants.Asset.ERROR_INVALID_SALVAGE_AMOUNT);
        }

        // If book value is negative then depreciation data is invalid.
        if (asset.getBookValue().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError(CamsPropertyConstants.Asset.BOOK_VALUE, CamsKeyConstants.Asset.ERROR_INVALID_BOOKVALUE_AMOUNT);
            return false;
        }

        return true;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = super.processCustomRouteDocumentBusinessRules(document);
        initializeAttributes(document);
        if (SpringContext.getBean(AssetService.class).isAssetFabrication(document) && newAsset.getCapitalAssetNumber() == null) {
            newAsset.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
            oldAsset.setCapitalAssetNumber(newAsset.getCapitalAssetNumber());
            newAsset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));
            oldAsset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));
        }

        // check for change
        valid &= checkAcquisitionTypeCodeChange();
        valid &= checkConditionCodeChange();
        valid &= checkAssetStatusCodeChange();
        valid &= checkAssetTypeCodeChange();
        valid &= checkFinancialObjectSubtypeCodeChange();
        valid &= validateAccount();

        WorkflowDocument workflowDoc = document.getDocumentHeader().getWorkflowDocument();
        // adding asset locks for asset edit only
        if (newAsset instanceof Asset && !(newAsset instanceof AssetFabrication) && !GlobalVariables.getMessageMap().hasErrors() && (workflowDoc.isInitiated() || workflowDoc.isSaved())) {
            valid &= setAssetLock(document);
        }
        return valid;
    }

    /**
     * Locking asset number
     *
     * @param document
     * @return
     */
    protected boolean setAssetLock(MaintenanceDocument document) {
        Asset asset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        return this.getCapitalAssetManagementModuleService().storeAssetLocks(retrieveAssetNumberForLocking(asset), document.getDocumentNumber(), CamsConstants.DocumentTypeName.ASSET_EDIT, null);
    }

    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }

    /**
     * Convenience method to append the path prefix
     */
    public AutoPopulatingList<ErrorMessage> putError(String propertyName, String errorKey, String... errorParameters) {
        return GlobalVariables.getMessageMap().putError(CamsConstants.DOCUMENT_PATH + "." + propertyName, errorKey, errorParameters);
    }

    /**
     * Check if the Acquisition Type Code is valid or is inactive.
     *
     * @param oldAcquisitionTypeCode
     * @param newAcquisitionTypeCode
     * @return boolean
     */
    protected boolean checkAcquisitionTypeCodeChange() {
        if (ObjectUtils.isNull(newAsset.getAcquisitionType())) {
            putFieldError(CamsPropertyConstants.Asset.ACQUISITION_TYPE_CODE, CamsKeyConstants.Asset.ERROR_ACQUISITION_TYPE_CODE_INVALID);
            return false;
        }
        else if (!StringUtils.equalsIgnoreCase(newAsset.getAcquisitionTypeCode(), oldAsset.getAcquisitionTypeCode())) {
            newAsset.refreshReferenceObject(CamsPropertyConstants.Asset.REF_ACQUISITION_TYPE);
            if (ObjectUtils.isNotNull(newAsset.getAcquisitionType())) {
                if (!newAsset.getAcquisitionType().isActive()) {
                    putFieldError(CamsPropertyConstants.Asset.ACQUISITION_TYPE_CODE, CamsKeyConstants.Asset.ERROR_ACQUISITION_TYPE_CODE_INACTIVE);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if the Asset Condition is valid or is inactive.
     *
     * @param oldConditionCode
     * @param newConditionCode
     * @return boolean
     */
    protected boolean checkConditionCodeChange() {
        if (ObjectUtils.isNull(newAsset.getCondition())) {
            putFieldError(CamsPropertyConstants.Asset.CONDITION_CODE, CamsKeyConstants.Asset.ERROR_ASSET_CONDITION_INVALID);
            return false;
        }
        else if (!StringUtils.equalsIgnoreCase(newAsset.getConditionCode(), oldAsset.getConditionCode())) {
            newAsset.refreshReferenceObject(CamsPropertyConstants.Asset.REF_ASSET_CONDITION);
            if (ObjectUtils.isNotNull(newAsset.getCondition())) {
                if (!newAsset.getCondition().isActive()) {
                    putFieldError(CamsPropertyConstants.Asset.CONDITION_CODE, CamsKeyConstants.Asset.ERROR_ASSET_CONDITION_INACTIVE);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if the Asset Depreciation Method is valid or is inactive.
     *
     * @param oldAssetDepreciationMethod
     * @param newAssetDepreciationMethod
     * @return boolean
     */
    protected boolean checkAssetDepreciationMethodChange() {
        if (ObjectUtils.isNull(newAsset.getAssetPrimaryDepreciationMethod())) {
            putFieldError(CamsPropertyConstants.Asset.PRIMARY_DEPRECIATION_METHOD, CamsKeyConstants.Asset.ERROR_DEPRECATION_METHOD_CODE_INVALID);
            return false;
        }
        else if (!StringUtils.equalsIgnoreCase(newAsset.getPrimaryDepreciationMethodCode(), oldAsset.getPrimaryDepreciationMethodCode())) {
            newAsset.refreshReferenceObject(CamsPropertyConstants.Asset.REF_ASSET_DEPRECATION_METHOD);
            if (ObjectUtils.isNotNull(newAsset.getAssetPrimaryDepreciationMethod())) {
                if (!newAsset.getAssetPrimaryDepreciationMethod().isActive()) {
                    putFieldError(CamsPropertyConstants.Asset.PRIMARY_DEPRECIATION_METHOD, CamsKeyConstants.Asset.ERROR_DEPRECATION_METHOD_CODE_INACTIVE);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if the Asset Status Code is valid or is inactive.
     *
     * @param oldAssetStatus
     * @param newAssetStatus
     * @return boolean
     */
    protected boolean checkAssetStatusCodeChange() {
        if (ObjectUtils.isNull(newAsset.getInventoryStatus())) {
            putFieldError(CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS, CamsKeyConstants.Asset.ERROR_ASSET_STATUS_INVALID);
            return false;
        }
        else if (!StringUtils.equalsIgnoreCase(newAsset.getInventoryStatusCode(), oldAsset.getInventoryStatusCode())) {
            newAsset.refreshReferenceObject(CamsPropertyConstants.Asset.REF_ASSET_STATUS);
            if (ObjectUtils.isNotNull(newAsset.getInventoryStatus())) {
                if (!newAsset.getInventoryStatus().isActive()) {
                    putFieldError(CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS, CamsKeyConstants.Asset.ERROR_ASSET_STATUS_INACTIVE);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if the Asset Type Code is valid or is inactive.
     *
     * @param oldAssetType
     * @param newAssetType
     * @return boolean
     */
    protected boolean checkAssetTypeCodeChange() {
        if (ObjectUtils.isNull(newAsset.getCapitalAssetType())) {
            putFieldError(CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE_CODE, CamsKeyConstants.Asset.ERROR_TYPE_CODE_INVALID);
            return false;
        }
        else if (!StringUtils.equalsIgnoreCase(newAsset.getCapitalAssetTypeCode(), oldAsset.getCapitalAssetTypeCode())) {
            newAsset.refreshReferenceObject(CamsPropertyConstants.Asset.REF_ASSET_TYPE);
            if (ObjectUtils.isNotNull(newAsset.getCapitalAssetType())) {
                if (!newAsset.getCapitalAssetType().isActive()) {
                    putFieldError(CamsPropertyConstants.AssetType.CAPITAL_ASSET_TYPE_CODE, CamsKeyConstants.Asset.ERROR_TYPE_CODE_INACTIVE);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if the Financial Object Sub-Type Code is valid or is inactive.
     *
     * @param oldObjectSubType
     * @param newObjectSubType
     * @return boolean
     */
    protected boolean checkFinancialObjectSubtypeCodeChange() {
        if (ObjectUtils.isNotNull(newAsset.getFinancialObjectSubType()) || StringUtils.isNotBlank(newAsset.getFinancialObjectSubTypeCode())) {
            newAsset.refreshReferenceObject(CamsPropertyConstants.Asset.REF_OBJECT_SUB_TYPE);
            if (ObjectUtils.isNull(newAsset.getFinancialObjectSubType())) {
                putFieldError(CamsPropertyConstants.Asset.FINANCIAL_OBJECT_SUB_TYP_CODE, CamsKeyConstants.Asset.ERROR_FINANCIAL_OBJECT_SUBTYPE_CODE_INVALID);
                return false;
            }
            else if (!newAsset.getFinancialObjectSubType().isActive()) {
                putFieldError(CamsPropertyConstants.Asset.FINANCIAL_OBJECT_SUB_TYP_CODE, CamsKeyConstants.Asset.ERROR_FINANCIAL_OBJECT_SUBTYPE_CODE_INACTIVE);
                return false;
            }
        }
        return true;
    }

    protected boolean validateManufacturer(Asset asset) {
        boolean valid = true;
        if (assetService.isCapitalAsset(asset)) {
            if (parameterService.getParameterValueAsBoolean(CamsConstants.CAM_MODULE_CODE, "Asset", CamsConstants.Parameters.MANUFACTURER_REQUIRED_FOR_NON_MOVEABLE_ASSET_IND) &&
                    StringUtils.isEmpty(asset.getManufacturerName())){
                putFieldError(CamsPropertyConstants.Asset.MANUFACTURER_NAME, CamsKeyConstants.AssetGlobal.ERROR_MFR_NAME_REQUIRED);
                valid = false;
            }
        }
        return valid;
    }
}
