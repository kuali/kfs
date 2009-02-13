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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.authorization.AssetGlobalAuthorizer;
import org.kuali.kfs.module.cam.document.gl.AssetGlobalGeneralLedgerPendingEntrySource;
import org.kuali.kfs.module.cam.document.service.AssetAcquisitionTypeService;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.module.cam.document.service.AssetLocationService.LocationField;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.web.format.CurrencyFormatter;

/**
 * Rule implementation for Asset Global document.
 */
public class AssetGlobalRule extends MaintenanceDocumentRuleBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetGlobalRule.class);
    private static final Map<LocationField, String> LOCATION_FIELD_MAP = new HashMap<LocationField, String>();
    static {
        LOCATION_FIELD_MAP.put(LocationField.CAMPUS_CODE, CamsPropertyConstants.AssetGlobalDetail.CAMPUS_CODE);
        LOCATION_FIELD_MAP.put(LocationField.BUILDING_CODE, CamsPropertyConstants.AssetGlobalDetail.BUILDING_CODE);
        LOCATION_FIELD_MAP.put(LocationField.ROOM_NUMBER, CamsPropertyConstants.AssetGlobalDetail.BUILDING_ROOM_NUMBER);
        LOCATION_FIELD_MAP.put(LocationField.SUB_ROOM_NUMBER, CamsPropertyConstants.AssetGlobalDetail.BUILDING_SUB_ROOM_NUMBER);
        LOCATION_FIELD_MAP.put(LocationField.CONTACT_NAME, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_NAME);
        LOCATION_FIELD_MAP.put(LocationField.STREET_ADDRESS, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_ADDRESS);
        LOCATION_FIELD_MAP.put(LocationField.CITY_NAME, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_CITY_NAME);
        LOCATION_FIELD_MAP.put(LocationField.STATE_CODE, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_STATE_CODE);
        LOCATION_FIELD_MAP.put(LocationField.ZIP_CODE, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_ZIP_CODE);
        LOCATION_FIELD_MAP.put(LocationField.COUNTRY_CODE, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_COUNTRY_CODE);
        LOCATION_FIELD_MAP.put(LocationField.LOCATION_TAB_KEY, CamsPropertyConstants.AssetGlobalDetail.CAMPUS_CODE);
    }

    /**
     * This method checks reference fields when adding new payment into collection.
     * 
     * @param assetGlobal
     * @param assetPaymentDetail
     * @return
     */
    private boolean checkReferenceExists(AssetGlobal assetGlobal, AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;

        // Validate Financial Posted date
        if (assetPaymentDetail.getExpenditureFinancialDocumentPostedDate() != null) {
            valid &= validatePostedDate(assetPaymentDetail);

        }

        if (valid) {
            // Check for ObjectCode

            if (StringUtils.isNotBlank(assetPaymentDetail.getFinancialObjectCode())) {
                assetPaymentDetail.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
                if (ObjectUtils.isNull(assetPaymentDetail.getObjectCode())) {
                    String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE).getLabel();
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
                    valid &= false;
                }
            }
        }

        if (StringUtils.isBlank(assetGlobal.getAcquisitionTypeCode()) || ObjectUtils.isNull(assetGlobal.getAcquisitionType())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_ACQUISITION_TYPE_CODE_REQUIRED);
            valid &= false;
        }

        if (StringUtils.isBlank(assetGlobal.getInventoryStatusCode())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, CamsKeyConstants.AssetGlobal.ERROR_INVENTORY_STATUS_REQUIRED_FOR_PAYMENT);
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.SEQUENCE_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_ASSET_PAYMENT_DEPENDENCY);
            valid &= false;
        }
        if (StringUtils.isBlank(assetGlobal.getCapitalAssetTypeCode())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_ASSET_TYPE_REQUIRED);
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.SEQUENCE_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_ASSET_PAYMENT_DEPENDENCY);
            valid &= false;
        }

        // check for existance and active when not separating. This can't be done in the DD because we have a condition on it. Note: Even though
        // on separate the payment lines can't be edited we still can't force the rules because data may have gone inactive since then.
        if (!getAssetGlobalService().isAssetSeparate(assetGlobal)) {
            assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT);
            if (StringUtils.isBlank(assetPaymentDetail.getAccountNumber()) || isAccountInvalid(assetPaymentDetail.getAccount())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_PAYMENT_ACCT_NOT_VALID, new String[] { assetPaymentDetail.getChartOfAccountsCode(), assetPaymentDetail.getAccountNumber() });
                valid &= false;
            }
            else if (!StringUtils.isBlank(assetPaymentDetail.getAccountNumber()) && !assetPaymentDetail.getAccount().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }

            assetPaymentDetail.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);
            if (!StringUtils.isBlank(assetPaymentDetail.getSubAccountNumber()) && ObjectUtils.isNull(assetPaymentDetail.getSubAccount())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid &= false;
            }
            else if (!StringUtils.isBlank(assetPaymentDetail.getSubAccountNumber()) && !assetPaymentDetail.getSubAccount().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }

            assetPaymentDetail.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
            if (!StringUtils.isBlank(assetPaymentDetail.getFinancialObjectCode()) && ObjectUtils.isNull(assetPaymentDetail.getObjectCode())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid &= false;
            }
            else if (!StringUtils.isBlank(assetPaymentDetail.getFinancialObjectCode()) && !assetPaymentDetail.getObjectCode().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }

            assetPaymentDetail.refreshReferenceObject(KFSPropertyConstants.SUB_OBJECT_CODE);
            if (!StringUtils.isBlank(assetPaymentDetail.getFinancialSubObjectCode()) && ObjectUtils.isNull(assetPaymentDetail.getSubObjectCode())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.SUB_OBJECT_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.SUB_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid &= false;
            }
            else if (!StringUtils.isBlank(assetPaymentDetail.getFinancialSubObjectCode()) && !assetPaymentDetail.getSubObjectCode().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.SUB_OBJECT_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.SUB_OBJECT_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }

            assetPaymentDetail.refreshReferenceObject(KFSPropertyConstants.PROJECT);
            if (!StringUtils.isBlank(assetPaymentDetail.getProjectCode()) && ObjectUtils.isNull(assetPaymentDetail.getProject())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.PROJECT_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.PROJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid &= false;
            }
            else if (!StringUtils.isBlank(assetPaymentDetail.getProjectCode()) && !assetPaymentDetail.getProject().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.PROJECT_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.PROJECT_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }

            assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION);
            if (!StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode()) && ObjectUtils.isNull(assetPaymentDetail.getExpenditureFinancialSystemOrigination())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid &= false;
            }
            else if (!StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode()) && !assetPaymentDetail.getExpenditureFinancialSystemOrigination().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }

            assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE);
            if (!StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode()) && ObjectUtils.isNull(assetPaymentDetail.getExpenditureFinancialSystemDocumentTypeCode())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid &= false;
            }
            // **************************************************************************************
            // TODO Uncomment this validation once isActive method is implemented in DocumenType bo
            // **************************************************************************************
//            if (!StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode()) && !assetPaymentDetail.getExpenditureFinancialDocumentType().isActive()) {
//                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE).getLabel();
//                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, RiceKeyConstants.ERROR_INACTIVE, label);                
            // valid &= false;
            // }
        }

        return valid;
    }

    /**
     * This method checks reference fields when adding one shared location information into collection.
     * 
     * @param assetGlobalDetail
     * @return
     */
    private boolean checkReferenceExists(AssetGlobalDetail assetGlobalDetail) {
        boolean valid = true;
        if (StringUtils.isNotBlank(assetGlobalDetail.getCampusCode())) {
            assetGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetGlobalDetail.CAMPUS);
            String label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(Campus.class.getName()).getAttributeDefinition(KFSPropertyConstants.CAMPUS_CODE).getLabel();

            if (ObjectUtils.isNull(assetGlobalDetail.getCampus())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_CAMPUS_CODE, assetGlobalDetail.getCampusCode());
                valid &= false;
            }
            else if (!assetGlobalDetail.getCampus().isActive()) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }
        }

        if (StringUtils.isNotBlank(assetGlobalDetail.getBuildingCode())) {
            assetGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetGlobalDetail.BUILDING);
            String label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(Building.class.getName()).getAttributeDefinition(KFSPropertyConstants.BUILDING_CODE).getLabel();

            if (ObjectUtils.isNull(assetGlobalDetail.getBuilding())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_BUILDING_CODE, assetGlobalDetail.getBuildingCode(), assetGlobalDetail.getCampusCode());
                valid &= false;
            }
            else if (!assetGlobalDetail.getBuilding().isActive()) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.BUILDING_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }
        }

        if (StringUtils.isNotBlank(assetGlobalDetail.getBuildingRoomNumber())) {
            assetGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetGlobalDetail.BUILDING_ROOM);
            String label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(Room.class.getName()).getAttributeDefinition(KFSPropertyConstants.BUILDING_ROOM_NUMBER).getLabel();

            if (ObjectUtils.isNull(assetGlobalDetail.getBuildingRoom())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.BUILDING_ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_INVALID_ROOM_NUMBER, assetGlobalDetail.getBuildingCode(), assetGlobalDetail.getBuildingRoomNumber(), assetGlobalDetail.getCampusCode());
                valid &= false;
            }
            else if (!assetGlobalDetail.getBuildingRoom().isActive()) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.BUILDING_ROOM_NUMBER, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }
        }
        return valid;
    }


    private boolean isCapitalStatus(AssetGlobal assetGlobal) {
        return getParameterService().getParameterValues(Asset.class, CamsConstants.Parameters.CAPITAL_ASSET_STATUS_CODES).contains(assetGlobal.getInventoryStatusCode());
    }

    private boolean isStatusCodeRetired(String statusCode) {
        return getParameterService().getParameterValues(Asset.class, CamsConstants.Parameters.RETIRED_STATUS_CODES).contains(statusCode);
    }

    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean success = super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        List<AssetGlobalDetail> assetSharedDetails = assetGlobal.getAssetSharedDetails();
        if (CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS.equals(collectionName)) {
            // handle location information
            AssetGlobalDetail assetGlobalDetail = (AssetGlobalDetail) line;
            success &= checkReferenceExists(assetGlobalDetail);
            success &= validateLocation(assetGlobal, assetGlobalDetail);
        }
        else if (StringUtils.isNotBlank(collectionName) && collectionName.contains(CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS)) {
            // handle unique information
            AssetGlobalDetail assetUniqueDetail = (AssetGlobalDetail) line;
            String campusTagNumber = assetUniqueDetail.getCampusTagNumber();
            if (StringUtils.isNotBlank(campusTagNumber)) {
                success &= validateTagDuplication(assetSharedDetails, campusTagNumber);
            }
        }
        else if (CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS.equals(collectionName)) {
            AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) line;

            if (success &= checkReferenceExists(assetGlobal, assetPaymentDetail)) {
                success &= validatePaymentLine(document, assetGlobal, assetPaymentDetail);
                success &= checkNegativePayment(document, assetPaymentDetail);
            }
        }

        // only for "Asset Separate" document
        if (getAssetGlobalService().isAssetSeparate(assetGlobal)) {
            // qty. of assets (unique) to be created
            success &= validateLocationQuantity(line);
            // total cost must be > 0
            success &= validateTotalCostAmount(assetGlobal);
        }

        return success;
    }

    /**
     * Validated the location quantity
     * 
     * @param line
     * @return boolean
     */
    private boolean validateLocationQuantity(PersistableBusinessObject line) {
        boolean success = true;
        AssetGlobalDetail assetGlobalDetail = (AssetGlobalDetail) line;
        if (assetGlobalDetail.getLocationQuantity() <= 0) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.LOCATION_QUANTITY, CamsKeyConstants.AssetSeparate.ERROR_ZERO_OR_NEGATIVE_LOCATION_QUANTITY, assetGlobalDetail.getLocationQuantity().toString());
            success &= false;
        }
        return success;
    }

    private boolean validateTagDuplication(List<AssetGlobalDetail> assetSharedDetails, String campusTagNumber) {
        boolean success = true;
        if (!campusTagNumber.equalsIgnoreCase(CamsConstants.Asset.NON_TAGGABLE_ASSET)) {
            for (AssetGlobalDetail assetSharedDetail : assetSharedDetails) {
                List<AssetGlobalDetail> assetGlobalUniqueDetails = assetSharedDetail.getAssetGlobalUniqueDetails();
                for (AssetGlobalDetail assetSharedUniqueDetail : assetGlobalUniqueDetails) {
                    if (campusTagNumber.equalsIgnoreCase(assetSharedUniqueDetail.getCampusTagNumber())) {
                        success &= false;
                        GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, campusTagNumber);
                    }
                }
            }
            if (success) {
                List<Asset> tagMatches = getAssetService().findActiveAssetsMatchingTagNumber(campusTagNumber);
                if (!tagMatches.isEmpty()) {
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, campusTagNumber);
                    success &= false;
                }
            }
        }
        return success;
    }

    private boolean validateTagDuplication(List<AssetGlobalDetail> assetSharedDetails) {
        HashSet<String> assetTags = new HashSet<String>();
        boolean success = true;
        int parentIndex = -1;
        int childIndex = -1;
        for (AssetGlobalDetail assetSharedDetail : assetSharedDetails) {
            parentIndex++;
            List<AssetGlobalDetail> assetGlobalUniqueDetails = assetSharedDetail.getAssetGlobalUniqueDetails();
            for (AssetGlobalDetail assetSharedUniqueDetail : assetGlobalUniqueDetails) {
                childIndex++;
                String campusTagNumber = assetSharedUniqueDetail.getCampusTagNumber();
                if (StringUtils.isNotBlank(campusTagNumber) && !assetTags.add(campusTagNumber) && !campusTagNumber.equalsIgnoreCase(CamsConstants.Asset.NON_TAGGABLE_ASSET)) {
                    success &= false;
                    String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + parentIndex + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + childIndex + "]";
                    GlobalVariables.getErrorMap().addToErrorPath(errorPath);
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, campusTagNumber);
                    GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);

                }
            }
            childIndex = -1;
            for (AssetGlobalDetail assetSharedUniqueDetail : assetGlobalUniqueDetails) {
                childIndex++;
                String campusTagNumber = assetSharedUniqueDetail.getCampusTagNumber();
                if (StringUtils.isNotBlank(campusTagNumber) && !campusTagNumber.equalsIgnoreCase(CamsConstants.Asset.NON_TAGGABLE_ASSET)) {
                    List<Asset> tagMatches = getAssetService().findActiveAssetsMatchingTagNumber(campusTagNumber);
                    if (!tagMatches.isEmpty()) {
                        success &= false;
                        String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + parentIndex + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + childIndex + "]";
                        GlobalVariables.getErrorMap().addToErrorPath(errorPath);
                        GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, campusTagNumber);
                        GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
                    }
                }
            }
        }
        return success;
    }

    private boolean validatePaymentLine(MaintenanceDocument maintenanceDocument, AssetGlobal assetGlobal, AssetPaymentDetail assetPaymentDetail) {
        boolean success = true;

        if (getAssetGlobalService().existsInGroup(CamsConstants.AssetGlobal.NEW_ACQUISITION_TYPE_CODE, assetGlobal.getAcquisitionTypeCode())) {
            success &= checkRequiredFieldsForNew(assetPaymentDetail);
        }
        else {
            // Validate Financial Document Type Code
            success &= validateDocumentTypeForNonNew(assetGlobal.getAcquisitionTypeCode(), assetPaymentDetail);
        }

        success &= validateObjectCode(assetPaymentDetail.getObjectCode(), assetGlobal);
        return success;
    }

    /**
     * "Add Negative Payment" permission check.
     * 
     * @param maintenanceDocument
     * @param assetPaymentDetail
     * @return
     */
    private boolean checkNegativePayment(MaintenanceDocument maintenanceDocument, AssetPaymentDetail assetPaymentDetail) {
        boolean success = true;
        AssetGlobalAuthorizer documentAuthorizer = (AssetGlobalAuthorizer) KNSServiceLocator.getDocumentHelperService().getDocumentAuthorizer(maintenanceDocument);
        boolean isAuthorized = documentAuthorizer.isAuthorized(maintenanceDocument, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.ADD_NEGATIVE_PAYMENTS, GlobalVariables.getUserSession().getPerson().getPrincipalId());

        if (!isAuthorized && assetPaymentDetail.getAmount() != null && assetPaymentDetail.getAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.AMOUNT, CamsKeyConstants.AssetGlobal.ERROR_INVALID_PAYMENT_AMOUNT);
            success = false;
        }
        return success;
    }

    /**
     * This method check the required fields for acquisition type New .
     * 
     * @param assetPaymentDetail
     * @return
     */
    private boolean checkRequiredFieldsForNew(AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;

        if (StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialDocumentNumber())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_EXPENDITURE_FINANCIAL_DOCUMENT_NUMBER_REQUIRED);
            valid &= false;
        }
        if (assetPaymentDetail.getExpenditureFinancialDocumentPostedDate() == null) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE, CamsKeyConstants.AssetGlobal.ERROR_DOCUMENT_POSTING_DATE_REQUIRED);
            valid &= false;
        }
        if (StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_EXPENDITURE_FINANCIAL_DOCUMENT_TYPE_CODE_REQUIRED);
            valid &= false;
        }
        return valid;
    }

    /**
     * Validates the posted date
     * 
     * @param assetPaymentDetail
     * @return boolean
     */
    private boolean validatePostedDate(AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;
        if (!getAssetPaymentService().extractPostedDatePeriod(assetPaymentDetail)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR, CamsKeyConstants.AssetGlobal.ERROR_UNIVERSITY_NOT_DEFINED_FOR_DATE, new String[] { assetPaymentDetail.getExpenditureFinancialDocumentPostedDate().toString() });
            valid &= false;
        }
        // payment posted date can't be a future date, i.e. extracted fiscal year from posted date should be less than or equal the
        // current fiscal year.
        else if (assetPaymentDetail.getPostingYear().compareTo(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear()) > 0) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE, CamsKeyConstants.Payment.ERROR_INVALID_DOC_POST_DATE);
            valid = false;
        }

        return valid;
    }

    /**
     * When acquisition type code is Capital (Gifts, Transfer-in, State excess, and Found), payment document type code will be
     * assigned to “AA” for Add Asset Document.
     * 
     * @param documentTypeCode
     * @return
     */
    private boolean validateDocumentTypeForNonNew(String acquisitionTypeCode, AssetPaymentDetail assetPaymentDetail) {
        String documentTypeCode = assetPaymentDetail.getExpenditureFinancialDocumentTypeCode();

        boolean valid = true;
        if (StringUtils.isNotBlank(acquisitionTypeCode) && getAssetGlobalService().existsInGroup(CamsConstants.AssetGlobal.NON_NEW_ACQUISITION_CODE_GROUP, acquisitionTypeCode)) {

            if (StringUtils.isNotBlank(documentTypeCode) && !CamsConstants.AssetGlobal.DOCUMENT_TYPE_CODE.equalsIgnoreCase(documentTypeCode)) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_DOCUMENT_TYPE_CODE_NOT_ALLOWED, documentTypeCode);
                valid = false;
            }
            else {
                // system set document type code as 'AA'
                assetPaymentDetail.setExpenditureFinancialDocumentTypeCode(CamsConstants.AssetGlobal.DOCUMENT_TYPE_CODE);
            }
        }
        return valid;
    }

    /**
     * Check object code is set to capital only when the status is capital.
     * 
     * @param assetGlobal
     * @param assetPaymentDetail
     * @return valid
     */
    private boolean validateObjectCode(ObjectCode objectCode, AssetGlobal assetGlobal) {
        boolean valid = true;

        // The acquisition type code of (F, G, N, S, T) requires a capital object code.
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        ParameterEvaluator parameterEvaluator = getParameterService().getParameterEvaluator(AssetGlobal.class, CamsConstants.Parameters.VALID_OBJECT_SUB_TYPES_BY_ACQUISITION_TYPE, CamsConstants.Parameters.INVALID_OBJECT_SUB_TYPES_BY_ACQUISITION_TYPE, assetGlobal.getAcquisitionTypeCode(), objectCode.getFinancialObjectSubTypeCode());
        valid &= parameterEvaluator.evaluateAndAddError(ObjectCode.class, CamsPropertyConstants.Asset.FINANCIAL_OBJECT_SUB_TYP_CODE, CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE);

        return valid;
    }

    private boolean isAccountInvalid(Account account) {
        return ObjectUtils.isNull(account) || account.isExpired();
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        List<AssetGlobalDetail> assetSharedDetails = assetGlobal.getAssetSharedDetails();
        boolean success = super.processCustomRouteDocumentBusinessRules(document);

        // need at least one asset location
        if (assetSharedDetails.isEmpty() || assetSharedDetails.get(0).getAssetGlobalUniqueDetails().isEmpty()) {
            putFieldError(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS, CamsKeyConstants.AssetGlobal.MIN_ONE_ASSET_REQUIRED);
            success &= false;
        }

        // Capital Asset must have payment zone.
        if (isCapitalStatus(assetGlobal) && assetGlobal.getAssetPaymentDetails().isEmpty()) {
            putFieldError(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS, CamsKeyConstants.AssetGlobal.MIN_ONE_PAYMENT_REQUIRED);
            success &= false;
        }
        // check total amount
        success &= validateAssetTotalAmount(document);

        // only for "Asset Separate" document
        if (getAssetGlobalService().isAssetSeparate(assetGlobal)) {

            if (getAssetPaymentService().getAssetPaymentDetailQuantity(assetGlobal) >= 10) {
                /*
                 * @TODO KULCAP-828 putFieldError(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS,
                 * CamsKeyConstants.AssetSeparate.ERROR_ASSET_SPLIT_MAX_LIMIT); success &= false;
                 */
            }

            // new source payments must be greater than the capital asset cost amount
            KualiDecimal totalSeparateSourceAmount = getAssetGlobalService().getUniqueAssetsTotalAmount(assetGlobal);
            if (totalSeparateSourceAmount.isGreaterThan(assetGlobal.getTotalCostAmount())) {
                putFieldError(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS, CamsKeyConstants.AssetSeparate.ERROR_INVALID_TOTAL_SEPARATE_SOURCE_AMOUNT, new String[] { assetGlobal.getSeparateSourceCapitalAssetNumber().toString() });
                success &= false;
            }

            // only active capital assets can be separated
            assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET);
            if (ObjectUtils.isNotNull(assetGlobal.getSeparateSourceCapitalAsset())) {
                if (!getAssetService().isCapitalAsset(assetGlobal.getSeparateSourceCapitalAsset())) {
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetSeparate.ERROR_NON_CAPITAL_ASSET_SEPARATE_REQUIRED, assetGlobal.getSeparateSourceCapitalAssetNumber().toString());
                    success &= false;
                }
            }

            // validate required fields within "Asset Unique Information" tab
            int sharedIndex = 0;
            for (AssetGlobalDetail addLocationDetail : assetSharedDetails) {
                int uniqueIndex = 0;
                for (AssetGlobalDetail uniqueLocationDetails : addLocationDetail.getAssetGlobalUniqueDetails()) {
                    String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + sharedIndex + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_GLOBAL_UNIQUE_DETAILS + "[" + uniqueIndex + "]";
                    GlobalVariables.getErrorMap().addToErrorPath(errorPath);
                    success &= validateCapitalAssetTypeCode(uniqueLocationDetails);
                    success &= validateSeparateSourceAmount(uniqueLocationDetails);
                    GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
                    uniqueIndex++;
                }
                sharedIndex++;
            }

            // total cost must be > 0
            success &= validateTotalCostAmount(assetGlobal);

            success &= validateAssetTotalCostMatchesPaymentTotalCost(assetGlobal);

            if (getAssetGlobalService().isAssetSeparateByPayment(assetGlobal)) {
                AssetGlobalRule.validateAssetAlreadySeparated(assetGlobal.getSeparateSourceCapitalAssetNumber());
            }
        } // end ASEP

        success &= validateLocationCollection(assetGlobal, assetSharedDetails);
        success &= validateTagDuplication(assetSharedDetails);
        return success;
    }


    /**
     * check if amount is above threshold for capital assets for normal user. minTotalPaymentByAsset and maxTotalPaymentByAsset are
     * used to check against threshold. Due to the decimal rounding, the asset total amount could have 1 cent difference with each
     * other. We need to pick up the right value for different threshold check.
     * 
     * @param document
     * @return
     */
    private boolean validateAssetTotalAmount(MaintenanceDocument document) {
        boolean success = true;
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();

        KualiDecimal minTotalPaymentByAsset = getAssetGlobalService().totalPaymentByAsset(assetGlobal, false);
        KualiDecimal maxTotalPaymentByAsset = getAssetGlobalService().totalPaymentByAsset(assetGlobal, true);
        if (minTotalPaymentByAsset.isGreaterThan(maxTotalPaymentByAsset)) {
            // swap min and max
            KualiDecimal totalPayment = minTotalPaymentByAsset;
            minTotalPaymentByAsset = maxTotalPaymentByAsset;
            maxTotalPaymentByAsset = totalPayment;
        }

        // Disallow FO change asset total amount during routing. Asset total amount is derived from asset payments total and the
        // quantity of assets
        if (getAssetService().isDocumentEnrouting(document) && (!minTotalPaymentByAsset.equals(assetGlobal.getMinAssetTotalAmount()) || !maxTotalPaymentByAsset.equals(assetGlobal.getMaxAssetTotalAmount()))) {
            putFieldError(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS, CamsKeyConstants.AssetGlobal.ERROR_CHANGE_ASSET_TOTAL_AMOUNT_DISALLOW, !minTotalPaymentByAsset.equals(assetGlobal.getMinAssetTotalAmount())? new String[] { (String) new CurrencyFormatter().format(assetGlobal.getMinAssetTotalAmount()), (String) new CurrencyFormatter().format(minTotalPaymentByAsset) }: new String[] { (String) new CurrencyFormatter().format(assetGlobal.getMaxAssetTotalAmount()), (String) new CurrencyFormatter().format(maxTotalPaymentByAsset) });
            success = false;
        }

        if (!getAssetService().isDocumentEnrouting(document)) {
            // run threshold checking before routing 
            AssetGlobalAuthorizer documentAuthorizer = (AssetGlobalAuthorizer) KNSServiceLocator.getDocumentHelperService().getDocumentAuthorizer(document);
            boolean isOverrideAuthorized = documentAuthorizer.isAuthorized(document, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.OVERRIDE_CAPITALIZATION_LIMIT_AMOUNT, GlobalVariables.getUserSession().getPerson().getPrincipalId());

            String capitalizationThresholdAmount = getParameterService().getParameterValue(AssetGlobal.class, CamsConstants.Parameters.CAPITALIZATION_LIMIT_AMOUNT);
            if (isCapitalStatus(assetGlobal) && minTotalPaymentByAsset.isLessThan(new KualiDecimal(capitalizationThresholdAmount)) && !isOverrideAuthorized) {
                putFieldError(CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, CamsKeyConstants.AssetGlobal.ERROR_CAPITAL_ASSET_PAYMENT_AMOUNT_MIN, capitalizationThresholdAmount);
                success &= false;
            }

            // check if amount is less than threshold for non-capital assets for all users
            if (!isCapitalStatus(assetGlobal) && maxTotalPaymentByAsset.isGreaterEqual(new KualiDecimal(capitalizationThresholdAmount))) {
                putFieldError(CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, CamsKeyConstants.AssetGlobal.ERROR_NON_CAPITAL_ASSET_PAYMENT_AMOUNT_MAX, capitalizationThresholdAmount);
                success &= false;
            }
        }
        return success;
    }

    /**
     * Validate that the total cost of the source asset is not zero or a negative amount.
     * 
     * @param assetGlobal
     * @return boolean
     */
    private boolean validateTotalCostAmount(AssetGlobal assetGlobal) {
        boolean success = true;
        if (!assetGlobal.getTotalCostAmount().isGreaterThan(KualiDecimal.ZERO)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetSeparate.ERROR_ZERO_OR_NEGATIVE_DOLLAR_AMOUNT, assetGlobal.getSeparateSourceCapitalAssetNumber().toString());
            success &= false;
        }
        return success;
    }

    /**
     * Validates the capital asset type code.
     * 
     * @param uniqueLocationDetails
     * @return boolean
     */
    private boolean validateCapitalAssetTypeCode(AssetGlobalDetail uniqueLocationDetails) {
        boolean success = true;
        if (StringUtils.isEmpty(uniqueLocationDetails.getCapitalAssetTypeCode())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_TYPE_CODE, CamsKeyConstants.AssetSeparate.ERROR_CAPITAL_ASSET_TYPE_CODE_REQUIRED, uniqueLocationDetails.getCapitalAssetTypeCode());
            success &= false;
        }
        return success;
    }

    /**
     * Validates the separate source amount.
     * 
     * @param uniqueLocationDetails
     * @return boolean
     */
    private boolean validateSeparateSourceAmount(AssetGlobalDetail uniqueLocationDetails) {
        boolean success = true;
        KualiDecimal separateSourceAmount = uniqueLocationDetails.getSeparateSourceAmount();
        if (separateSourceAmount == null || separateSourceAmount.isLessEqual(KualiDecimal.ZERO)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.SEPARATE_SOURCE_AMOUNT, CamsKeyConstants.AssetSeparate.ERROR_TOTAL_SEPARATE_SOURCE_AMOUNT_REQUIRED);
            success &= false;
        }
        return success;
    }

    private boolean validateLocationCollection(AssetGlobal assetGlobal, List<AssetGlobalDetail> assetSharedDetails) {
        boolean success = true;
        // for each shared location info, validate
        boolean isCapitalAsset = isCapitalStatus(assetGlobal);
        int index = 0;
        for (AssetGlobalDetail assetLocationDetail : assetSharedDetails) {
            String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + index + "]";
            GlobalVariables.getErrorMap().addToErrorPath(errorPath);
            success &= SpringContext.getBean(AssetLocationService.class).validateLocation(LOCATION_FIELD_MAP, assetLocationDetail, isCapitalAsset, assetGlobal.getCapitalAssetType());
            GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
            index++;
        }
        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        boolean success = true;
        success &= super.processCustomSaveDocumentBusinessRules(document);

        String acquisitionTypeCode = assetGlobal.getAcquisitionTypeCode();
        String statusCode = assetGlobal.getInventoryStatusCode();

        // no need to validate specific fields if document is "Asset Separate"
        if (!getAssetGlobalService().isAssetSeparate(assetGlobal)) {
            success &= validateAccount(assetGlobal);
            if (StringUtils.isNotBlank(acquisitionTypeCode) && StringUtils.isNotBlank(statusCode)) {
                // check if status code and acquisition type code combination is valid
                success &= SpringContext.getBean(ParameterService.class).getParameterEvaluator(AssetGlobal.class, CamsConstants.Parameters.VALID_ASSET_STATUSES_BY_ACQUISITION_TYPE, CamsConstants.Parameters.INVALID_ASSET_STATUSES_BY_ACQUISITION_TYPE, acquisitionTypeCode, statusCode).evaluateAndAddError(AssetGlobal.class, CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE);
            }
            success &= validateAssetType(assetGlobal);
            if (isCapitalStatus(assetGlobal)) {
                success &= validateVendorAndManufacturer(assetGlobal);
            }

            success &= validatePaymentCollection(document, assetGlobal);
        } else {
            // append doc type to existing doc header description
            if (!document.getDocumentHeader().getDocumentDescription().toLowerCase().contains(CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE.toLowerCase())) {
                document.getDocumentHeader().setDocumentDescription(CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE + ":" + document.getDocumentHeader().getDocumentDescription());
            }    
        }

        // System shall only generate GL entries if we have an incomeAssetObjectCode for this acquisitionTypeCode and the statusCode
        // is for capital assets
        if ((success && super.processCustomSaveDocumentBusinessRules(document)) && getAssetAcquisitionTypeService().hasIncomeAssetObjectCode(acquisitionTypeCode) && this.isCapitalStatus(assetGlobal)) {
            if (success &= validateAcquisitionIncomeObjectCode(assetGlobal)) {
                // create poster
                AssetGlobalGeneralLedgerPendingEntrySource assetGlobalGlPoster = new AssetGlobalGeneralLedgerPendingEntrySource((FinancialSystemDocumentHeader) document.getDocumentHeader());
                // create postables
                getAssetGlobalService().createGLPostables(assetGlobal, assetGlobalGlPoster);

                if (SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(assetGlobalGlPoster)) {
                    assetGlobal.setGeneralLedgerPendingEntries(assetGlobalGlPoster.getPendingEntries());
                }
                else {
                    assetGlobalGlPoster.getPendingEntries().clear();
                }
            }
        }

        return success;
    }

    /**
     * Validate offset object code
     * 
     * @param assetGlobal
     * @return
     */
    private boolean validateAcquisitionIncomeObjectCode(AssetGlobal assetGlobal) {
        boolean valid = true;
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            // check offset object code existence
            ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(assetPaymentDetail.getPostingYear(), assetPaymentDetail.getChartOfAccountsCode(), assetGlobal.getAcquisitionType().getIncomeAssetObjectCode());
            if (ObjectUtils.isNull(objectCode)) {
                putFieldError(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_INVALID_ACQUISITION_INCOME_OBJECT_CODE, new String[] { assetGlobal.getAcquisitionType().getIncomeAssetObjectCode(), assetPaymentDetail.getPostingYear().toString(), assetPaymentDetail.getChartOfAccountsCode() });
                valid = false;
            }
            // check Object Code active
            else if (!objectCode.isActive()) {
                putFieldError(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE, CamsKeyConstants.GLPosting.ERROR_OBJECT_CODE_FROM_ASSET_OBJECT_CODE_INACTIVE, new String[] { CamsConstants.GLPostingObjectCodeType.INCOME, assetGlobal.getAcquisitionType().getIncomeAssetObjectCode(), assetPaymentDetail.getChartOfAccountsCode() });
                valid = false;
            }
        }
        return valid;
    }

    private boolean validatePaymentCollection(MaintenanceDocument maintenanceDocument, AssetGlobal assetGlobal) {
        boolean success = true;
        int index = 0;
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + index + "]";
            GlobalVariables.getErrorMap().addToErrorPath(errorPath);
            success &= validatePaymentLine(maintenanceDocument, assetGlobal, assetPaymentDetail);
            GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
            index++;
        }
        return success;
    }

    private boolean validateVendorAndManufacturer(AssetGlobal assetGlobal) {
        boolean success = true;
        if (StringUtils.isBlank(assetGlobal.getVendorName())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.VENDOR_NAME, CamsKeyConstants.AssetGlobal.ERROR_VENDOR_NAME_REQUIRED);
            success &= false;
        }
        if (StringUtils.isBlank(assetGlobal.getManufacturerName())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.MFR_NAME, CamsKeyConstants.AssetGlobal.ERROR_MFR_NAME_REQUIRED);
            success &= false;
        }
        return success;
    }

    @Override
    public boolean processSaveDocument(Document document) {
        boolean valid = true;
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        AssetGlobal assetGlobal = (AssetGlobal) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        List<AssetGlobalDetail> assetSharedDetails = assetGlobal.getAssetSharedDetails();
        int index = 0;
        for (AssetGlobalDetail assetLocationDetail : assetSharedDetails) {
            String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + index + "]";
            GlobalVariables.getErrorMap().addToErrorPath(errorPath);
            valid &= checkReferenceExists(assetLocationDetail);
            GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
            index++;
        }
        return valid && super.processSaveDocument(document);
    }

    private boolean validateAccount(AssetGlobal assetGlobal) {
        boolean success = true;
        assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT);
        Account organizationOwnerAccount = assetGlobal.getOrganizationOwnerAccount();
        if (StringUtils.isNotBlank(assetGlobal.getOrganizationOwnerAccountNumber()) && (organizationOwnerAccount == null || !organizationOwnerAccount.isActive() || organizationOwnerAccount.isExpired())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_OWNER_ACCT_NOT_ACTIVE, new String[] { assetGlobal.getOrganizationOwnerChartOfAccountsCode(), assetGlobal.getOrganizationOwnerAccountNumber() });
            success &= false;
        }
        return success;
    }

    /**
     * Validate location
     * 
     * @param assetGlobal
     * @return boolean
     */
    private boolean validateLocation(AssetGlobal assetGlobal, AssetGlobalDetail assetGlobalDetail) {
        boolean success = true;
        if (StringUtils.isBlank(assetGlobal.getInventoryStatusCode())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, CamsKeyConstants.AssetGlobal.ERROR_INVENTORY_STATUS_REQUIRED);
            success &= false;
        }
        success = validateAssetType(assetGlobal);
        if (success) {
            boolean isCapitalAsset = isCapitalStatus(assetGlobal);
            success &= SpringContext.getBean(AssetLocationService.class).validateLocation(LOCATION_FIELD_MAP, assetGlobalDetail, isCapitalAsset, assetGlobal.getCapitalAssetType());
        }
        else {
            putFieldError(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_ASSET_LOCATION_DEPENDENCY);
        }
        return success;
    }

    /**
     * Validate asset type.
     * 
     * @param assetGlobal
     * @return boolean
     */
    private boolean validateAssetType(AssetGlobal assetGlobal) {
        boolean success = true;
        assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE);
        if (ObjectUtils.isNull(assetGlobal.getCapitalAssetType())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_ASSET_TYPE_REQUIRED);
            success &= false;
        }
        return success;
    }

    /**
     * Give an error if this asset can't be separated due to mismatching amount on asset and AssetPayment records
     * 
     * @param assetGlobal
     * @return validation success of failure
     */
    public static boolean validateAssetTotalCostMatchesPaymentTotalCost(AssetGlobal assetGlobal) {
        PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
        assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET);
        KualiDecimal assetTotalCost = ObjectUtils.isNull(assetGlobal.getSeparateSourceCapitalAsset().getTotalCostAmount()) ? new KualiDecimal(0) : assetGlobal.getSeparateSourceCapitalAsset().getTotalCostAmount();
        KualiDecimal paymentTotalCost = paymentSummaryService.calculatePaymentTotalCost(assetGlobal.getSeparateSourceCapitalAsset());
        if (!paymentTotalCost.equals(assetTotalCost)) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_SEPARATE_ASSET_TOTAL_COST_NOT_MATCH_PAYMENT_TOTAL_COST);

            return false;
        }

        return true;
    }

    /**
     * Give an error if this asset has already been separated
     * 
     * @param assetGlobal
     * @return validation success of failure
     */
    public static boolean validateAssetAlreadySeparated(Long separateSourceCapitalAssetNumber) {
        AssetService assetService = SpringContext.getBean(AssetService.class);

        List<String> documentNumbers = assetService.getDocumentNumbersThatSeparatedThisAsset(separateSourceCapitalAssetNumber);

        if (!documentNumbers.isEmpty()) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_SEPARATE_ASSET_ALREADY_SEPARATED, new String[] { documentNumbers.toString() });

            return false;
        }

        return true;
    }

    private ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    private AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    private AssetPaymentService getAssetPaymentService() {
        return SpringContext.getBean(AssetPaymentService.class);
    }

    private AssetAcquisitionTypeService getAssetAcquisitionTypeService() {
        return SpringContext.getBean(AssetAcquisitionTypeService.class);
    }

    private AssetGlobalService getAssetGlobalService() {
        return SpringContext.getBean(AssetGlobalService.class);
    }

    private DataDictionaryService getDataDictionaryService() {
        return SpringContext.getBean(DataDictionaryService.class);
    }
}