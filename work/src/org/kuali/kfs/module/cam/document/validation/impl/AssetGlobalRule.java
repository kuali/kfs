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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsConstants.DocumentTypeName;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.document.gl.AssetGlobalGeneralLedgerPendingEntrySource;
import org.kuali.kfs.module.cam.document.service.AssetAcquisitionTypeService;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetLocationService.LocationField;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.ReferenceDefinition;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusService;

/**
 * Rule implementation for Asset Global document.
 */
public class AssetGlobalRule extends MaintenanceDocumentRuleBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetGlobalRule.class);
    protected static final Map<LocationField, String> LOCATION_FIELD_MAP = new HashMap<LocationField, String>();
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
    }

    /**
     * This method checks reference fields when adding new payment into collection.
     *
     * @param assetGlobal
     * @param assetPaymentDetail
     * @return
     */
    protected boolean checkReferenceExists(AssetGlobal assetGlobal, AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;

        // validate required field for object code. Skip the error message because the maintenance DD 'defaultExistenceChecks' can
        // generate the same error message. We won't duplicate it.
        if (StringUtils.isBlank(assetPaymentDetail.getFinancialObjectCode())) {
            valid = false;
        }

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
                    GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
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
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.SEQUENCE_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_ASSET_PAYMENT_DEPENDENCY);
            valid &= false;
        }
        if (StringUtils.isBlank(assetGlobal.getCapitalAssetTypeCode())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_ASSET_TYPE_REQUIRED);
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.SEQUENCE_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_ASSET_PAYMENT_DEPENDENCY);
            valid &= false;
        }

        // check for existence and active when not separating. This can't be done in the DD because we have a condition on it.
        // Note: Even though on separate the payment lines can't be edited we still can't force the rules because data may have
        // gone inactive since then.
        if (!getAssetGlobalService().isAssetSeparate(assetGlobal)) {
            assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT);
            if (StringUtils.isBlank(assetPaymentDetail.getAccountNumber()) || isAccountInvalid(assetPaymentDetail.getAccount())) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_PAYMENT_ACCT_NOT_VALID, new String[] { assetPaymentDetail.getChartOfAccountsCode(), assetPaymentDetail.getAccountNumber() });
                valid &= false;
            }
            else if (!StringUtils.isBlank(assetPaymentDetail.getAccountNumber()) && !assetPaymentDetail.getAccount().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }

            assetPaymentDetail.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);
            if (!StringUtils.isBlank(assetPaymentDetail.getSubAccountNumber()) && ObjectUtils.isNull(assetPaymentDetail.getSubAccount())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid &= false;
            }
            else if (!StringUtils.isBlank(assetPaymentDetail.getSubAccountNumber()) && !assetPaymentDetail.getSubAccount().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }

            assetPaymentDetail.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
            if (!StringUtils.isBlank(assetPaymentDetail.getFinancialObjectCode()) && ObjectUtils.isNull(assetPaymentDetail.getObjectCode())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid &= false;
            }
            else if (!StringUtils.isBlank(assetPaymentDetail.getFinancialObjectCode()) && !assetPaymentDetail.getObjectCode().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }

            assetPaymentDetail.refreshReferenceObject(KFSPropertyConstants.SUB_OBJECT_CODE);
            if (!StringUtils.isBlank(assetPaymentDetail.getFinancialSubObjectCode()) && ObjectUtils.isNull(assetPaymentDetail.getSubObjectCode())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.SUB_OBJECT_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.SUB_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid &= false;
            }
            else if (!StringUtils.isBlank(assetPaymentDetail.getFinancialSubObjectCode()) && !assetPaymentDetail.getSubObjectCode().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.SUB_OBJECT_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.SUB_OBJECT_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }

            assetPaymentDetail.refreshReferenceObject(KFSPropertyConstants.PROJECT);
            if (!StringUtils.isBlank(assetPaymentDetail.getProjectCode()) && ObjectUtils.isNull(assetPaymentDetail.getProject())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.PROJECT_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.PROJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid &= false;
            }
            else if (!StringUtils.isBlank(assetPaymentDetail.getProjectCode()) && !assetPaymentDetail.getProject().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.PROJECT_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.PROJECT_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }

            assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION);
            if (!StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode()) && ObjectUtils.isNull(assetPaymentDetail.getExpenditureFinancialSystemOrigination())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid &= false;
            }
            else if (!StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode()) && !assetPaymentDetail.getExpenditureFinancialSystemOrigination().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }

            if (!StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode()) && ObjectUtils.isNull(assetPaymentDetail.getExpenditureFinancialSystemDocumentTypeCode())) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
                valid &= false;
                return valid;
            }
            if (!StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode()) && !assetPaymentDetail.getExpenditureFinancialSystemDocumentTypeCode().isActive()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }
        }
        return valid;
    }

    /**
     * This method checks reference fields when adding one shared location information into collection.
     *
     * @param assetGlobalDetail
     * @return
     */
    protected boolean checkReferenceExists(AssetGlobalDetail assetGlobalDetail) {
        boolean valid = true;
        if (StringUtils.isNotBlank(assetGlobalDetail.getCampusCode())) {
            Campus campus = SpringContext.getBean(CampusService.class).getCampus(assetGlobalDetail.getCampusCode());

            if (ObjectUtils.isNull(campus)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_CAMPUS_CODE, assetGlobalDetail.getCampusCode());
                valid &= false;
            }
            else if (!campus.isActive()) {
                String label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(Campus.class.getName()).getAttributeDefinition(KFSPropertyConstants.CAMPUS_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }
        }

        if (StringUtils.isNotBlank(assetGlobalDetail.getBuildingCode())) {
            Map<String, String> objectKeys = new HashMap<String, String>();
            objectKeys.put(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_CODE, assetGlobalDetail.getCampusCode());
            objectKeys.put(CamsPropertyConstants.AssetGlobalDetail.BUILDING_CODE, assetGlobalDetail.getBuildingCode());
            Building building = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Building.class, objectKeys);

            if (ObjectUtils.isNull(building)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_BUILDING_CODE, assetGlobalDetail.getBuildingCode(), assetGlobalDetail.getCampusCode());
                valid &= false;
            }
            else if (!building.isActive()) {
                String label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(Building.class.getName()).getAttributeDefinition(KFSPropertyConstants.BUILDING_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.BUILDING_CODE, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }
        }

        if (StringUtils.isNotBlank(assetGlobalDetail.getBuildingRoomNumber())) {
            Map<String, String> objectKeys = new HashMap<String, String>();
            objectKeys.put(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_CODE, assetGlobalDetail.getCampusCode());
            objectKeys.put(CamsPropertyConstants.AssetGlobalDetail.BUILDING_CODE, assetGlobalDetail.getBuildingCode());
            objectKeys.put(CamsPropertyConstants.AssetGlobalDetail.BUILDING_ROOM_NUMBER, assetGlobalDetail.getBuildingRoomNumber());
            Room room = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Room.class, objectKeys);

            if (ObjectUtils.isNull(room)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.BUILDING_ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_INVALID_ROOM_NUMBER, assetGlobalDetail.getBuildingCode(), assetGlobalDetail.getBuildingRoomNumber(), assetGlobalDetail.getCampusCode());
                valid &= false;
            }
            else if (!room.isActive()) {
                String label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(Room.class.getName()).getAttributeDefinition(KFSPropertyConstants.BUILDING_ROOM_NUMBER).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.BUILDING_ROOM_NUMBER, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }
        }
        return valid;
    }


    protected boolean isCapitalStatus(AssetGlobal assetGlobal) {
        return getParameterService().getParameterValuesAsString(Asset.class, CamsConstants.Parameters.CAPITAL_ASSET_STATUS_CODES).contains(assetGlobal.getInventoryStatusCode());
    }

    protected boolean isStatusCodeRetired(String statusCode) {
        return getParameterService().getParameterValuesAsString(Asset.class, CamsConstants.Parameters.RETIRED_STATUS_CODES).contains(statusCode);
    }

    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean success = super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        if (GlobalVariables.getMessageMap().hasErrors()) {
            return false;
        }

        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        List<AssetGlobalDetail> assetSharedDetails = assetGlobal.getAssetSharedDetails();
        if (CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS.equals(collectionName)) {
            // handle location information
            AssetGlobalDetail assetGlobalDetail = (AssetGlobalDetail) line;
            success &= checkReferenceExists(assetGlobalDetail);
            success &= validateLocation(assetGlobal, assetGlobalDetail);

            // qty. of assets (unique) to be created
            success &= validateLocationQuantity(line);
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
                success &= checkNegativeOrZeroPayment(document, assetPaymentDetail);
            }
        }


        // only for "Asset Separate" document
        if (getAssetGlobalService().isAssetSeparate(assetGlobal)) {
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
    protected boolean validateLocationQuantity(PersistableBusinessObject line) {
        boolean success = true;
        AssetGlobalDetail assetGlobalDetail = (AssetGlobalDetail) line;
        if (assetGlobalDetail.getLocationQuantity() <= 0) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.LOCATION_QUANTITY, CamsKeyConstants.AssetSeparate.ERROR_ZERO_OR_NEGATIVE_LOCATION_QUANTITY, assetGlobalDetail.getLocationQuantity().toString());
            success &= false;
        }
        return success;
    }

    protected boolean validateTagDuplication(List<AssetGlobalDetail> assetSharedDetails, String campusTagNumber) {
        boolean success = true;
        if (!campusTagNumber.equalsIgnoreCase(CamsConstants.Asset.NON_TAGGABLE_ASSET)) {
            for (AssetGlobalDetail assetSharedDetail : assetSharedDetails) {
                List<AssetGlobalDetail> assetGlobalUniqueDetails = assetSharedDetail.getAssetGlobalUniqueDetails();
                for (AssetGlobalDetail assetSharedUniqueDetail : assetGlobalUniqueDetails) {
                    if (campusTagNumber.equalsIgnoreCase(assetSharedUniqueDetail.getCampusTagNumber())) {
                        success &= false;
                        GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, campusTagNumber);
                    }
                }
            }
            if (success) {
                List<Asset> tagMatches = getAssetService().findActiveAssetsMatchingTagNumber(campusTagNumber);
                if (!tagMatches.isEmpty()) {
                    GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, campusTagNumber);
                    success &= false;
                }
            }
        }
        return success;
    }

    protected boolean validateTagDuplication(List<AssetGlobalDetail> assetSharedDetails) {
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
                    GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                    GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, campusTagNumber);
                    GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);

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
                        GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                        GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, campusTagNumber);
                        GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
                    }
                }
            }
        }
        return success;
    }

    protected boolean validatePaymentLine(MaintenanceDocument maintenanceDocument, AssetGlobal assetGlobal, AssetPaymentDetail assetPaymentDetail) {
        boolean success = true;

        // If Acquisition type is "New" or "non-capital", check required fields including Document number, Document type code,
        // Posted date.

        if (getAssetGlobalService().existsInGroup(getAssetGlobalService().getNewAcquisitionTypeCode(), assetGlobal.getAcquisitionTypeCode()) || !getAssetGlobalService().existsInGroup(getAssetGlobalService().getCapitalObjectAcquisitionCodeGroup(), assetGlobal.getAcquisitionTypeCode())) {
            success &= checkRequiredFieldsForNewOrNonCapital(assetPaymentDetail);
        }
        else {
            // Validate Financial Document Type Code
            success &= validateDocumentTypeForNonNew(assetGlobal.getAcquisitionTypeCode(), assetPaymentDetail);
        }

        assetPaymentDetail.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
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
    protected boolean checkNegativeOrZeroPayment(MaintenanceDocument maintenanceDocument, AssetPaymentDetail assetPaymentDetail) {
        boolean success = true;
        FinancialSystemMaintenanceDocumentAuthorizerBase documentAuthorizer = (FinancialSystemMaintenanceDocumentAuthorizerBase) SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer(maintenanceDocument);
        boolean isAuthorized = documentAuthorizer.isAuthorized(maintenanceDocument, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.ADD_NEGATIVE_PAYMENTS, GlobalVariables.getUserSession().getPerson().getPrincipalId());

        if (!isAuthorized && assetPaymentDetail.getAmount() != null && assetPaymentDetail.getAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.AMOUNT, CamsKeyConstants.AssetGlobal.ERROR_INVALID_PAYMENT_AMOUNT);
            success = false;
        }

        // amount can not be zero for any user
        if (assetPaymentDetail.getAmount().isZero()) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.AMOUNT, CamsKeyConstants.AssetGlobal.ERROR_INVALID_PAYMENT_AMOUNT);
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
    protected boolean checkRequiredFieldsForNewOrNonCapital(AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;

        if (StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialDocumentNumber())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_EXPENDITURE_FINANCIAL_DOCUMENT_NUMBER_REQUIRED);
            valid &= false;
        }
        if (assetPaymentDetail.getExpenditureFinancialDocumentPostedDate() == null) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE, CamsKeyConstants.AssetGlobal.ERROR_DOCUMENT_POSTING_DATE_REQUIRED);
            valid &= false;
        }
        if (StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_EXPENDITURE_FINANCIAL_DOCUMENT_TYPE_CODE_REQUIRED);
            valid &= false;
        }
        return valid;
    }

    /**
     * Validates the posted date payment posted date can't be a future date
     *
     * @param assetPaymentDetail
     * @return boolean
     */
    protected boolean validatePostedDate(AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;
        Date currentDate = SpringContext.getBean(DateTimeService.class).getCurrentDate();

        if (!getAssetPaymentService().extractPostedDatePeriod(assetPaymentDetail)) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR, CamsKeyConstants.AssetGlobal.ERROR_UNIVERSITY_NOT_DEFINED_FOR_DATE, new String[] { assetPaymentDetail.getExpenditureFinancialDocumentPostedDate().toString() });
            valid &= false;
        }
        else if (assetPaymentDetail.getExpenditureFinancialDocumentPostedDate().compareTo(currentDate) > 0) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE, CamsKeyConstants.Payment.ERROR_INVALID_DOC_POST_DATE);
            valid = false;
        }
        return valid;
    }

    /**
     * When acquisition type code is Capital (Gifts, Transfer-in, State excess, and Found), payment document type code will be
     * assigned to AA for Add Asset Document.
     *
     * @param documentTypeCode
     * @return
     */
    protected boolean validateDocumentTypeForNonNew(String acquisitionTypeCode, AssetPaymentDetail assetPaymentDetail) {
        String documentTypeCode = assetPaymentDetail.getExpenditureFinancialDocumentTypeCode();

        boolean valid = true;
        if (StringUtils.isNotBlank(acquisitionTypeCode) && getAssetGlobalService().existsInGroup(getAssetGlobalService().getNonNewAcquisitionCodeGroup(), acquisitionTypeCode)) {

            if (StringUtils.isNotBlank(documentTypeCode) && !CamsConstants.DocumentTypeName.ASSET_ADD_GLOBAL.equalsIgnoreCase(documentTypeCode)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_DOCUMENT_TYPE_CODE_NOT_ALLOWED, documentTypeCode);
                valid = false;
            }
            else {
                // system set document type code as 'AA'
                assetPaymentDetail.setExpenditureFinancialDocumentTypeCode(CamsConstants.DocumentTypeName.ASSET_ADD_GLOBAL);
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
    protected boolean validateObjectCode(ObjectCode objectCode, AssetGlobal assetGlobal) {
        boolean valid = true;

        // The acquisition type code of (F, G, N, S, T) requires a capital object code.
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        ParameterEvaluator parameterEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(AssetGlobal.class, CamsConstants.Parameters.VALID_OBJECT_SUB_TYPES_BY_ACQUISITION_TYPE, CamsConstants.Parameters.INVALID_OBJECT_SUB_TYPES_BY_ACQUISITION_TYPE, assetGlobal.getAcquisitionTypeCode(), objectCode.getFinancialObjectSubTypeCode());
        valid &= parameterEvaluator.evaluateAndAddError(ObjectCode.class, CamsPropertyConstants.Asset.FINANCIAL_OBJECT_SUB_TYP_CODE, CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE);

        return valid;
    }

    protected boolean isAccountInvalid(Account account) {
        return ObjectUtils.isNull(account) || account.isExpired();
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        List<AssetGlobalDetail> assetSharedDetails = assetGlobal.getAssetSharedDetails();
        boolean success = super.processCustomRouteDocumentBusinessRules(document);
        if (GlobalVariables.getMessageMap().hasErrors()) {
            return false;
        }

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
        // check total amount for Asset Create
        if (!getAssetGlobalService().isAssetSeparate(assetGlobal)) {
            success &= validateAssetTotalAmount(document);
        }
        else {
            // only for "Asset Separate" document
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
                    if (StringUtils.isNotBlank(assetGlobal.getAcquisitionTypeCode())) {
                    putFieldError(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE, CamsKeyConstants.AssetSeparate.ERROR_NON_CAPITAL_ASSET_SEPARATE_REQUIRED);
                    success &= false;
                    }
                }
            }

            // validate required fields within "Asset Unique Information" tab
            int sharedIndex = 0;
            for (AssetGlobalDetail addLocationDetail : assetSharedDetails) {
                int uniqueIndex = 0;
                for (AssetGlobalDetail assetGlobalUniqueDetail : addLocationDetail.getAssetGlobalUniqueDetails()) {
                    String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + sharedIndex + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_GLOBAL_UNIQUE_DETAILS + "[" + uniqueIndex + "]";
                    GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                    success &= validateCapitalAssetTypeCode(assetGlobalUniqueDetail);
                    success &= validateAssetType(assetGlobalUniqueDetail, sharedIndex, uniqueIndex);
                    success &= validateAssetDescription(assetGlobalUniqueDetail);
                    success &= validateManufacturer(assetGlobalUniqueDetail);
                    success &= validateSeparateSourceAmount(assetGlobalUniqueDetail, document);
                    GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
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
     * Validate all separate source amount is above the capital asset threshold amount.
     *
     * @param document
     * @return
     */
    protected boolean validateSeparateSourceAmountAboveThreshold(MaintenanceDocument document, AssetGlobalDetail assetGlobalUniqueDetail) {
        boolean success = true;
        String capitalizationThresholdAmount = this.getCapitalizationThresholdAmount();
        KualiDecimal separateAmount = assetGlobalUniqueDetail.getSeparateSourceAmount();
        // check for the minimal amount only among all separate source amount. if it's above the threshold, safe...
        if (separateAmount != null && !getAssetService().isDocumentEnrouting(document) && !validateCapitalAssetAmountAboveThreshhold(document, separateAmount, capitalizationThresholdAmount)) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.SEPARATE_SOURCE_AMOUNT, CamsKeyConstants.AssetSeparate.ERROR_SEPARATE_ASSET_BELOW_THRESHOLD, new String[] { assetGlobalUniqueDetail.getCapitalAssetNumber().toString(), capitalizationThresholdAmount });
            success = false;
        }
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
    protected boolean validateAssetTotalAmount(MaintenanceDocument document) {
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
            putFieldError(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS, CamsKeyConstants.AssetGlobal.ERROR_CHANGE_ASSET_TOTAL_AMOUNT_DISALLOW, !minTotalPaymentByAsset.equals(assetGlobal.getMinAssetTotalAmount()) ? new String[] { (String) new CurrencyFormatter().format(assetGlobal.getMinAssetTotalAmount()), (String) new CurrencyFormatter().format(minTotalPaymentByAsset) } : new String[] { (String) new CurrencyFormatter().format(assetGlobal.getMaxAssetTotalAmount()), (String) new CurrencyFormatter().format(maxTotalPaymentByAsset) });
            success = false;
        }

        // run threshold checking before routing
        if (!getAssetService().isDocumentEnrouting(document)) {
            String capitalizationThresholdAmount = getCapitalizationThresholdAmount();
            if (isCapitalStatus(assetGlobal)) {
                // check if amount is above threshold for capital asset.
                if (!validateCapitalAssetAmountAboveThreshhold(document, minTotalPaymentByAsset, capitalizationThresholdAmount)) {
                    putFieldError(CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, CamsKeyConstants.AssetGlobal.ERROR_CAPITAL_ASSET_PAYMENT_AMOUNT_MIN, capitalizationThresholdAmount);
                    success = false;
                }
            }
            else {
                // check if amount is less than threshold for non-capital assets for all users
                success &= validateNonCapitalAssetAmountBelowThreshold(maxTotalPaymentByAsset, capitalizationThresholdAmount);
            }

        }
        return success;
    }

    /**
     * Get the capitalization threshold amount from the system parameter setting.
     *
     * @return
     */
    protected String getCapitalizationThresholdAmount() {
        return getParameterService().getParameterValueAsString(AssetGlobal.class, CamsConstants.Parameters.CAPITALIZATION_LIMIT_AMOUNT);
    }

    /**
     * Validate Capital Asset Amount above the threshold or below the amount for authorized user only.
     *
     * @param document
     * @param assetAmount
     * @param capitalizationThresholdAmount
     * @return
     */
    protected boolean validateCapitalAssetAmountAboveThreshhold(MaintenanceDocument document, KualiDecimal assetAmount, String capitalizationThresholdAmount) {
        boolean success = true;
        FinancialSystemMaintenanceDocumentAuthorizerBase documentAuthorizer = (FinancialSystemMaintenanceDocumentAuthorizerBase) SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer(document);
        boolean isOverrideAuthorized = documentAuthorizer.isAuthorized(document, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.OVERRIDE_CAPITALIZATION_LIMIT_AMOUNT, GlobalVariables.getUserSession().getPerson().getPrincipalId());

        if (assetAmount.isLessThan(new KualiDecimal(capitalizationThresholdAmount)) && !isOverrideAuthorized) {
            success = false;
        }
        return success;
    }

    /**
     * Validate non-capital asset amount below the threshold.
     *
     * @param assetAmount
     * @param capitalizationThresholdAmount
     * @return
     */
    protected boolean validateNonCapitalAssetAmountBelowThreshold(KualiDecimal assetAmount, String capitalizationThresholdAmount) {
        boolean success = true;
        if (assetAmount.isGreaterEqual(new KualiDecimal(capitalizationThresholdAmount))) {
            putFieldError(CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, CamsKeyConstants.AssetGlobal.ERROR_NON_CAPITAL_ASSET_PAYMENT_AMOUNT_MAX, capitalizationThresholdAmount);
            success &= false;
        }
        return success;
    }

    /**
     * Validate that the total cost of the source asset is not zero or a negative amount.
     *
     * @param assetGlobal
     * @return boolean
     */
    protected boolean validateTotalCostAmount(AssetGlobal assetGlobal) {
        boolean success = true;
        if (!assetGlobal.getTotalCostAmount().isGreaterThan(KualiDecimal.ZERO)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.AssetGlobal.SECTION_ID_ASSET_INFORMATION, CamsKeyConstants.AssetSeparate.ERROR_ZERO_OR_NEGATIVE_DOLLAR_AMOUNT);
            success &= false;
        }
        return success;
    }

    /**
     * Validates the capital asset type code. This only checks for the existence of some contents, not whether the contents are valid.
     *
     * @param uniqueLocationDetails
     * @return boolean
     */
    protected boolean validateCapitalAssetTypeCode(AssetGlobalDetail uniqueLocationDetails) {
        boolean success = true;
        if (StringUtils.isEmpty(uniqueLocationDetails.getCapitalAssetTypeCode())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_TYPE_CODE, CamsKeyConstants.AssetSeparate.ERROR_CAPITAL_ASSET_TYPE_CODE_REQUIRED, uniqueLocationDetails.getCapitalAssetTypeCode());
            success &= false;
        }
        return success;
    }

    /**
     * Validates the asset description.
     *
     * @param uniqueLocationDetails
     * @return boolean
     */
    protected boolean validateAssetDescription(AssetGlobalDetail uniqueLocationDetails) {
        boolean success = true;
        if (StringUtils.isEmpty(uniqueLocationDetails.getCapitalAssetDescription())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_DESCRIPTION, CamsKeyConstants.AssetSeparate.ERROR_ASSET_DESCRIPTION_REQUIRED, uniqueLocationDetails.getCapitalAssetTypeCode());
            success &= false;
        }
        return success;
    }

    /**
     * Validates the manufacturer.
     *
     * @param uniqueLocationDetails
     * @return boolean
     */
    protected boolean validateManufacturer(AssetGlobalDetail uniqueLocationDetails) {
        boolean success = true;
        if (StringUtils.isEmpty(uniqueLocationDetails.getManufacturerName())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.MANUFACTURER_NAME, CamsKeyConstants.AssetSeparate.ERROR_MANUFACTURER_REQUIRED, uniqueLocationDetails.getCapitalAssetTypeCode());
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
    protected boolean validateSeparateSourceAmount(AssetGlobalDetail uniqueLocationDetail, MaintenanceDocument document) {
        boolean success = true;
        KualiDecimal separateSourceAmount = uniqueLocationDetail.getSeparateSourceAmount();
        if (separateSourceAmount == null || separateSourceAmount.isLessEqual(KualiDecimal.ZERO)) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.SEPARATE_SOURCE_AMOUNT, CamsKeyConstants.AssetSeparate.ERROR_TOTAL_SEPARATE_SOURCE_AMOUNT_REQUIRED);
            success &= false;
        }
        else {
            // for capital asset separate, validate the minimal separate source amount above the threshold
            success &= validateSeparateSourceAmountAboveThreshold(document, uniqueLocationDetail);
        }
        return success;
    }

    protected boolean validateLocationCollection(AssetGlobal assetGlobal, List<AssetGlobalDetail> assetSharedDetails) {
        boolean success = true;
        // for each shared location info, validate
        boolean isCapitalAsset = isCapitalStatus(assetGlobal);
        int index = 0;
        for (AssetGlobalDetail assetLocationDetail : assetSharedDetails) {
            String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + index + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
            success &= SpringContext.getBean(AssetLocationService.class).validateLocation(LOCATION_FIELD_MAP, assetLocationDetail, isCapitalAsset, assetGlobal.getCapitalAssetType());
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            index++;
        }
        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        boolean success = true;
        success &= super.processCustomSaveDocumentBusinessRules(document);
        if (GlobalVariables.getMessageMap().hasErrors()) {
            return false;
        }

        String acquisitionTypeCode = assetGlobal.getAcquisitionTypeCode();
        String statusCode = assetGlobal.getInventoryStatusCode();

        // no need to validate specific fields if document is "Asset Separate"
        if (!getAssetGlobalService().isAssetSeparate(assetGlobal)) {
            success &= validateAccount(assetGlobal);
            if (StringUtils.isNotBlank(acquisitionTypeCode) && StringUtils.isNotBlank(statusCode)) {
                // check if status code and acquisition type code combination is valid
                success &= /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(AssetGlobal.class, CamsConstants.Parameters.VALID_ASSET_STATUSES_BY_ACQUISITION_TYPE, CamsConstants.Parameters.INVALID_ASSET_STATUSES_BY_ACQUISITION_TYPE, acquisitionTypeCode, statusCode).evaluateAndAddError(AssetGlobal.class, CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE);
            }
            success &= validateAssetType(assetGlobal);
            if (isCapitalStatus(assetGlobal)) {
                success &= validateVendorAndManufacturer(assetGlobal);
            }

            success &= validatePaymentCollection(document, assetGlobal);
        }
        else {
            // append doc type to existing doc header description
            if (!document.getDocumentHeader().getDocumentDescription().toLowerCase().contains(CamsConstants.AssetSeparate.SEPARATE_AN_ASSET_DESCRIPTION.toLowerCase())) {
                Integer maxDocumentDescription = ddService.getAttributeMaxLength(DocumentHeader.class, KRADPropertyConstants.DOCUMENT_DESCRIPTION);
                String documentDescription = CamsConstants.AssetSeparate.SEPARATE_AN_ASSET_DESCRIPTION + " " + document.getDocumentHeader().getDocumentDescription();
                documentDescription = StringUtils.left(documentDescription, maxDocumentDescription);
                document.getDocumentHeader().setDocumentDescription(documentDescription);
            }
        }

        // System shall only generate GL entries if we have an incomeAssetObjectCode for this acquisitionTypeCode and the statusCode
        // is for capital assets
        //  GLs should not be generated while separating assets too
        if ((success && !getAssetGlobalService().isAssetSeparate(assetGlobal) && super.processCustomSaveDocumentBusinessRules(document)) && getAssetAcquisitionTypeService().hasIncomeAssetObjectCode(acquisitionTypeCode) && this.isCapitalStatus(assetGlobal)) {
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
     * Locking on separate source asset number
     *
     * @param document
     * @param assetGlobal
     * @return
     */
    protected boolean setAssetLock(MaintenanceDocument document, AssetGlobal assetGlobal) {
        List<Long> capitalAssetNumbers = new ArrayList<Long>();
        if (assetGlobal.getSeparateSourceCapitalAssetNumber() != null) {
            capitalAssetNumbers.add(assetGlobal.getSeparateSourceCapitalAssetNumber());
        }

        return SpringContext.getBean(CapitalAssetManagementModuleService.class).storeAssetLocks(capitalAssetNumbers, document.getDocumentNumber(), DocumentTypeName.ASSET_SEPARATE, null);
    }

    /**
     * Validate offset object code
     *
     * @param assetGlobal
     * @return
     */
    protected boolean validateAcquisitionIncomeObjectCode(AssetGlobal assetGlobal) {
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

    protected boolean validatePaymentCollection(MaintenanceDocument maintenanceDocument, AssetGlobal assetGlobal) {
        boolean success = true;
        int index = 0;
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + index + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
            success &= validatePaymentLine(maintenanceDocument, assetGlobal, assetPaymentDetail);
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            index++;
        }
        return success;
    }

    protected boolean validateVendorAndManufacturer(AssetGlobal assetGlobal) {
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
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
            valid &= checkReferenceExists(assetLocationDetail);
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);

            // checks that all of the asset types entered in the collection of Unique Details actually exist in persistent storage
            int indexUniqueDetails = 0;
            for (AssetGlobalDetail assetGlobalUniqueDetails : assetLocationDetail.getAssetGlobalUniqueDetails()) {
                valid &= validateAssetType(assetGlobalUniqueDetails, index, indexUniqueDetails);
                indexUniqueDetails++;
            }
            index++;
        }


        // Creates locking representation for this global document. The locking is only applicable for assets that are being split.
        // The assets that are being created do not need to be locked since they don't exist yet.
        if (valid && getAssetGlobalService().isAssetSeparate(assetGlobal)) {
            valid &= setAssetLock(maintenanceDocument, assetGlobal);
        }

        return valid && super.processSaveDocument(document);
    }

    protected boolean validateAccount(AssetGlobal assetGlobal) {
        boolean success = true;
        assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT);
        Account organizationOwnerAccount = assetGlobal.getOrganizationOwnerAccount();

        boolean skipAccountAvailiablityCheck;
        if (StringUtils.isBlank(assetGlobal.getOrganizationOwnerChartOfAccountsCode()) || StringUtils.isBlank(assetGlobal.getOrganizationOwnerAccountNumber())) {
            skipAccountAvailiablityCheck = true;
        }
        else {
            skipAccountAvailiablityCheck = isOrgOwnerAccountFromCab(assetGlobal);
        }

        // when check if organizationOwnerAccount is existing, use ObjectUtils rather than comparing with 'null' since OJB proxy
        // object is used here.
        if (!skipAccountAvailiablityCheck && (ObjectUtils.isNull(organizationOwnerAccount) || !organizationOwnerAccount.isActive() || organizationOwnerAccount.isExpired())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_OWNER_ACCT_NOT_ACTIVE, new String[] { assetGlobal.getOrganizationOwnerChartOfAccountsCode(), assetGlobal.getOrganizationOwnerAccountNumber() });
            success &= false;
        }
        return success;
    }


    /**
     * Check if organization owner account is set from CAB. We honor all accounting lines from CAB are valid payments even thougth
     * they are expired.
     *
     * @param assetGlobal
     * @return
     */
    protected boolean isOrgOwnerAccountFromCab(AssetGlobal assetGlobal) {
        String orgOwnerChartCode = assetGlobal.getOrganizationOwnerChartOfAccountsCode();
        String orgOwnerAccountNbr = assetGlobal.getOrganizationOwnerAccountNumber();

        if (StringUtils.isBlank(assetGlobal.getOrganizationOwnerChartOfAccountsCode()) || StringUtils.isBlank(assetGlobal.getOrganizationOwnerAccountNumber())) {
            return true;
        }

        if (assetGlobal.isCapitalAssetBuilderOriginIndicator()) {
            // If CAB submits, allow use of inactive accounting line from the payments
            for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
                if (orgOwnerChartCode.equalsIgnoreCase(assetPaymentDetail.getChartOfAccountsCode()) && orgOwnerAccountNbr.equalsIgnoreCase(assetPaymentDetail.getAccountNumber())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Validate location
     *
     * @param assetGlobal
     * @return boolean
     */
    protected boolean validateLocation(AssetGlobal assetGlobal, AssetGlobalDetail assetGlobalDetail) {
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
     * Validate asset type at the AssetGlobal level. Only checks that there are contents in the object.
     *
     * @param assetGlobal
     * @return boolean
     */
    protected boolean validateAssetType(AssetGlobal assetGlobal) {
        boolean success = true;
        assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE);
        if (ObjectUtils.isNull(assetGlobal.getCapitalAssetType())) {
            putFieldError(CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS, CamsKeyConstants.AssetGlobal.ERROR_ASSET_TYPE_REQUIRED);
            success &= false;
        }

        return success;
    }

    /**
     * Validate asset type in the AssetGlobalUniqueDetails level, and ensures the value is in the list of valid types.
     * The incoming indices are for creation of the errorPath for the global variable message map, which will determine
     * what text field to mark as having a problem. This was written to be called within a loop.
     *
     * @param assetGlobalUniqueDetails
     * @param sharedIndex the index of the shared details within the AssetGlobal
     * @param uniqueIndex the index of the unique details within the shared details
     * @return boolean
     */
    protected boolean validateAssetType(AssetGlobalDetail assetGlobalUniqueDetails, Integer sharedIndex, Integer uniqueIndex) {
        boolean success = true;
        int sharedInd = 0;
        int uniqueInd = 0;

        // In hindsite, maybe this should have been written to perform the loop-within-loop fully contained within this
        // method, and not used within the typical double loop structure of processSaveDocument() and
        // processCustomRouteDecumentBusinessRules.

        // don't change the value of the incoming param!
        if (sharedIndex != null) {
            sharedInd = sharedIndex;
        }
        if (uniqueIndex != null) {
            uniqueInd = uniqueIndex;
        }

        if (ObjectUtils.isNull(assetGlobalUniqueDetails)) {
            putFieldError(CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS, CamsKeyConstants.AssetGlobal.ERROR_ASSET_LOCATION_DEPENDENCY);
            success &= false;
        }

        // validate asset type
        if (StringUtils.isNotBlank(assetGlobalUniqueDetails.getCapitalAssetTypeCode())) {
            AssetType assetType = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(AssetType.class, assetGlobalUniqueDetails.getCapitalAssetTypeCode());
            if (assetType == null || StringUtils.isBlank(assetType.getCapitalAssetTypeCode())) {
                String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + sharedInd + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + uniqueInd + "]";
                GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_TYPE_CODE, CamsKeyConstants.AssetSeparate.ERROR_CAPITAL_ASSET_TYPE_CODE_INVALID, assetGlobalUniqueDetails.getCapitalAssetTypeCode());
                GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
                success &= false;
            }
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
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_SEPARATE_ASSET_TOTAL_COST_NOT_MATCH_PAYMENT_TOTAL_COST);

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
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_SEPARATE_ASSET_ALREADY_SEPARATED, new String[] { documentNumbers.toString() });

            return false;
        }

        return true;
    }


    /**
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#dataDictionaryValidate(org.kuali.rice.kns.document.MaintenanceDocument)
     * Override this method to only validate reference exists for asset separate , otherwise do default Existence Checks.
     * KFSMI-6584
     */
    @Override
    protected boolean dataDictionaryValidate(MaintenanceDocument document) {

        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        LOG.debug("MaintenanceDocument validation beginning");

        // explicitly put the errorPath that the dictionaryValidationService requires
        GlobalVariables.getMessageMap().addToErrorPath("document.newMaintainableObject");

        // document must have a newMaintainable object
        Maintainable newMaintainable = document.getNewMaintainableObject();
        if (newMaintainable == null) {
            GlobalVariables.getMessageMap().removeFromErrorPath("document.newMaintainableObject");
            throw new ValidationException("Maintainable object from Maintenance Document '" + document.getDocumentTitle() + "' is null, unable to proceed.");
        }

        // document's newMaintainable must contain an object (ie, not null)
        PersistableBusinessObject businessObject = newMaintainable.getBusinessObject();
        if (businessObject == null) {
            GlobalVariables.getMessageMap().removeFromErrorPath("document.newMaintainableObject.");
            throw new ValidationException("Maintainable's component business object is null.");
        }

        // run required check from maintenance data dictionary
        maintDocDictionaryService.validateMaintenanceRequiredFields(document);

        //check for duplicate entries in collections if necessary
        maintDocDictionaryService.validateMaintainableCollectionsForDuplicateEntries(document);

        // run the DD DictionaryValidation (non-recursive)
        dictionaryValidationService.validateBusinessObjectOnMaintenanceDocument(businessObject,
                document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());

        // do default (ie, mandatory) existence checks
        if (!getAssetGlobalService().isAssetSeparate(assetGlobal)) {
            dictionaryValidationService.validateDefaultExistenceChecks(businessObject);
        }
        else {

            Collection references = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getDefaultExistenceChecks(businessObject.getClass());

            // walk through the references, doing the tests on each
            for (Iterator iter = references.iterator(); iter.hasNext();) {
                ReferenceDefinition reference = (ReferenceDefinition) iter.next();
                // do the existence and validation testing
                dictionaryValidationService.validateReferenceExists(assetGlobal,reference );
            }

        }

        // explicitly remove the errorPath we've added
        GlobalVariables.getMessageMap().removeFromErrorPath("document.newMaintainableObject");

        LOG.debug("MaintenanceDocument validation ending");
        return true;
    }

    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    protected AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    protected AssetPaymentService getAssetPaymentService() {
        return SpringContext.getBean(AssetPaymentService.class);
    }

    protected AssetAcquisitionTypeService getAssetAcquisitionTypeService() {
        return SpringContext.getBean(AssetAcquisitionTypeService.class);
    }

    protected AssetGlobalService getAssetGlobalService() {
        return SpringContext.getBean(AssetGlobalService.class);
    }
}
