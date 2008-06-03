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


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetGlobal;
import org.kuali.module.cams.bo.AssetGlobalDetail;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.AssetLocationService.LocationField;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;

/**
 * Rule implementation for Asset Global document.
 */
public class AssetGlobalRule extends MaintenanceDocumentRuleBase {
    // TODO: SYSTEM PARAMETER
    private static final String CAPITAL_OBJECT_SUB_TYPES = "CM;CF;CO;C1;C2;UC;UF;UO;AM;BD;BF;BI;ES;IF;LA;LE;LI;LF;LR;BR;BY;BX";

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
        LOCATION_FIELD_MAP.put(LocationField.LOCATION_TAB_KEY, CamsPropertyConstants.AssetGlobalDetail.VERSION_NUM);
    }
    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);

    private boolean checkRequiredFieldExists(AssetGlobal assetGlobal) {
        boolean valid = true;

        if (StringUtils.isBlank(assetGlobal.getAcquisitionTypeCode()) || ObjectUtils.isNull(assetGlobal.getAcquisitionType())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_ACQUISITION_TYPE_CODE_REQUIRED);
            valid = false;
        }

        if (StringUtils.isBlank(assetGlobal.getInventoryStatusCode())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, CamsKeyConstants.AssetGlobal.ERROR_INVENTORY_STATUS_REQUIRED);
            valid = false;
        }

        return valid;
    }

    private boolean checkReferenceExists(AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;
        // Check for account
        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT);
        if (StringUtils.isBlank(assetPaymentDetail.getAccountNumber()) || isAccountInvalid(assetPaymentDetail.getAccount())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_OWNER_ACCT_NOT_ACTIVE, new String[] { assetPaymentDetail.getChartOfAccountsCode(), assetPaymentDetail.getAccountNumber() });
            valid = false;
        }

        return valid;
    }

    private boolean checkReferenceExists(AssetGlobalDetail assetGlobalDetail) {
        boolean valid = true;
        if (StringUtils.isNotBlank(assetGlobalDetail.getCampusCode())) {
            assetGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetGlobalDetail.CAMPUS);
            if (ObjectUtils.isNull(assetGlobalDetail.getCampus())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_CAMPUS_CODE, assetGlobalDetail.getCampusCode());
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetGlobalDetail.getBuildingCode())) {
            assetGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetGlobalDetail.BUILDING);
            if (ObjectUtils.isNull(assetGlobalDetail.getBuilding()) || !assetGlobalDetail.getBuilding().isActive()) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_BUILDING_CODE, assetGlobalDetail.getBuildingCode(), assetGlobalDetail.getCampusCode());
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetGlobalDetail.getBuildingRoomNumber())) {
            assetGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetGlobalDetail.BUILDING_ROOM);
            if (ObjectUtils.isNull(assetGlobalDetail.getBuildingRoom()) || !assetGlobalDetail.getBuildingRoom().isActive()) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.BUILDING_ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_INVALID_ROOM_NUMBER, assetGlobalDetail.getBuildingCode(), assetGlobalDetail.getBuildingRoomNumber(), assetGlobalDetail.getCampusCode());
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetGlobalDetail.getOffCampusStateCode())) {
            assetGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_STATE);
            if (ObjectUtils.isNull(assetGlobalDetail.getOffCampusState())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_STATE_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_OFF_CAMPUS_STATE, assetGlobalDetail.getOffCampusStateCode());
                valid &= false;
            }
        }
        return valid;
    }

    private boolean checkReferenceExists(AssetHeader assetHeader) {
        boolean valid = true;
        if (StringUtils.isNotBlank(assetHeader.getOrganizationOwnerChartOfAccountsCode())) {
            assetHeader.refreshReferenceObject(CamsPropertyConstants.AssetHeader.ORGANIZATION_OWNER_CHART);
            if (ObjectUtils.isNull(assetHeader.getOrganizationOwnerChartOfAccounts())) {
                putFieldError(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS, CamsKeyConstants.AssetGlobal.ERROR_OWNER_CHART_INVALID, new String[] { assetHeader.getOrganizationOwnerChartOfAccountsCode() });
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetHeader.getOrganizationOwnerAccountNumber())) {
            assetHeader.refreshReferenceObject(CamsPropertyConstants.AssetHeader.ORGANIZATION_OWNER_ACCOUNT);
            if (ObjectUtils.isNull(assetHeader.getOrganizationOwnerAccount())) {
                putFieldError(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCT_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_OWNER_ACCT_NUMBER_INVALID, new String[] { assetHeader.getOrganizationOwnerChartOfAccountsCode(), assetHeader.getOrganizationOwnerAccountNumber() });
                valid &= false;
            }
        }

        return valid;
    }

    private boolean isCapitalStatus(AssetGlobal assetGlobal) {
        return parameterService.getParameterValues(Asset.class, CamsConstants.Parameters.CAPITAL_ASSET_STATUS_CODES).contains(assetGlobal.getInventoryStatusCode());
    }

    private boolean isStatusCodeRetired(String statusCode) {
        return parameterService.getParameterValues(Asset.class, CamsConstants.Parameters.RETIRED_STATUS_CODES).contains(statusCode);
    }

    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean success = super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        int pos = assetGlobal.getAssetSharedDetails().size() - 1;
        if (CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS.equals(collectionName)) {
            // handle location information
            AssetGlobalDetail assetGlobalDetail = (AssetGlobalDetail) line;
            success &= checkReferenceExists(assetGlobalDetail);
            success &= validateLocation(assetGlobal, assetGlobalDetail);
        }
        else if ((CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + pos + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS).equals(collectionName)) {
            // handle unique information
            AssetGlobalDetail assetUniqueDetail = (AssetGlobalDetail) line;
            if (StringUtils.isNotBlank(assetUniqueDetail.getCampusTagNumber())) {
                success &= validateTagDuplication(assetUniqueDetail.getCampusTagNumber());
            }
        }
        else if (CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS.equals(collectionName)) {
            if (success &= checkRequiredFieldExists(assetGlobal)) {
            AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) line;
                success = validatePaymentLine(assetGlobal, assetPaymentDetail);
            }
        }
        return success;
    }

    private boolean validatePaymentLine(AssetGlobal assetGlobal, AssetPaymentDetail assetPaymentDetail) {
        boolean success = true;

        success &= checkReferenceExists(assetPaymentDetail);

        success = validateObjectCode(assetGlobal, assetPaymentDetail);


        if (StringUtils.isNotBlank(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode())) {
            success = validateDocumentType(assetPaymentDetail);
        }
            // handle payment information
            // amount should be positive
            if (assetPaymentDetail.getAmount() != null && assetPaymentDetail.getAmount().isNegative()) {
                success = false;
            }
            // if financial doc num is not same as current doc number, then posting date is required
            if (assetPaymentDetail.getExpenditureFinancialDocumentPostedDate() == null && StringUtils.isNotBlank(assetPaymentDetail.getExpenditureFinancialDocumentNumber()) && !assetPaymentDetail.getExpenditureFinancialDocumentNumber().equalsIgnoreCase(assetGlobal.getDocumentNumber())) {
                success = false;
            }
        return success;
        }

    private boolean validateDocumentType(AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;
        // check document Type code if it is set
        if (!CamsConstants.AssetGlobal.DOCUMENT_TYPE_CODE_FOR_ADD_ASET.equalsIgnoreCase(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode())) {
            putFieldError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE, CamsKeyConstants.AssetGlobal.ERROR_DOCUMENT_TYPE_CODE_NOT_ALLOWED);
            valid = false;
        }

        return valid;
    }

    /**
     * 
     * This method...
     * 
     * @param assetGlobal
     * @param assetPaymentDetail
     * @param success
     * @return
     */
    private boolean validateObjectCode(AssetGlobal assetGlobal, AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;
        // Check Object Code
        if (!isCapitalStatus(assetGlobal) && isCapitablObjectCode(assetPaymentDetail.getObjectCode())) {
            // Capital object code shall not be used for a non-capital asset.
            putFieldError(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE, CamsKeyConstants.AssetGlobal.ERROR_CAPITAL_OBJECT_CODE_NOT_ALLOWED);
            valid = false;
        }

        // The acquisition type code of (C, F, G, N, P, S, T) requires a capital object code.
        if (Arrays.asList(CamsConstants.AssetGlobal.CAPITAL_OBJECT_ACCQUISITION_CODE_GROUP).contains(assetGlobal.getAcquisitionTypeCode()) && !isCapitablObjectCode(assetPaymentDetail.getObjectCode())) {
            putFieldError(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE, CamsKeyConstants.AssetGlobal.ERROR_CAPITAL_OBJECT_CODE_REQUIRED);
            valid = false;
        }
        return valid;
    }

    private boolean isAccountInvalid(Account account) {
        return ObjectUtils.isNull(account) || account.isAccountClosedIndicator() || account.isExpired();
    }

    private boolean isCapitablObjectCode(ObjectCode objectCode) {
        return ObjectUtils.isNotNull(objectCode) && StringUtils.isNotBlank(objectCode.getFinancialObjectSubTypeCode()) && Arrays.asList(CAPITAL_OBJECT_SUB_TYPES.split(";")).contains(objectCode.getFinancialObjectSubTypeCode());
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        boolean success = super.processCustomRouteDocumentBusinessRules(document);
        if (assetGlobal.getAssetSharedDetails().isEmpty() || assetGlobal.getAssetSharedDetails().get(0).getAssetGlobalUniqueDetails().isEmpty()) {
            // at least one asset should be added
            putFieldError(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS, CamsKeyConstants.AssetGlobal.MIN_ONE_ASSET_REQUIRED);
            success &= false;
        }

		 // Capital Asset must have payment zone.
        if (isCapitalStatus(assetGlobal) && assetGlobal.getAssetPaymentDetails().isEmpty()) {
            putFieldError(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS, CamsKeyConstants.AssetGlobal.MIN_ONE_PAYMENT_REQUIRED);
            success &= false;
        }
        KualiDecimal totalPaymentByAsset = totalPaymentByAsset(assetGlobal);
        // check if amount is above threshold for capital assets for normal user
        UniversalUser universalUser = GlobalVariables.getUserSession().getUniversalUser();
        if (isCapitalStatus(assetGlobal) && totalPaymentByAsset.isLessThan(new KualiDecimal(5000)) && !universalUser.isMember(CamsConstants.Workgroups.WORKGROUP_CM_ADMINISTRATORS)) {
            putFieldError(CamsPropertyConstants.AssetGlobal.VERSION_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAPITAL_ASSET_PAYMENT_AMOUNT_MIN, "5000");
            success &= false;
        }

        // check if amount is less than threshold for non-capital assets for all users
        if (!isCapitalStatus(assetGlobal) && totalPaymentByAsset.isGreaterEqual(new KualiDecimal(5000))) {
            putFieldError(CamsPropertyConstants.AssetGlobal.VERSION_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_NON_CAPITAL_ASSET_PAYMENT_AMOUNT_MAX);
            success &= false;
        }

        return success;
    }

    private KualiDecimal totalPaymentByAsset(AssetGlobal assetGlobal) {
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();
        KualiDecimal qty = new KualiDecimal(assetPaymentDetails.size());

        for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
            totalAmount.add(assetPaymentDetail.getAmount().divide(qty));
        }
        return totalAmount;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        boolean success = super.processCustomSaveDocumentBusinessRules(document);
        success = validateAccount(assetGlobal);
        if (isCapitalStatus(assetGlobal)) {
            if (StringUtils.isBlank(assetGlobal.getVendorName())) {
                putFieldError(CamsPropertyConstants.AssetGlobal.VENDOR_NAME, CamsKeyConstants.AssetGlobal.ERROR_VENDOR_NAME_REQUIRED);
                success &= false;
            }
            if (StringUtils.isBlank(assetGlobal.getManufacturerName())) {
                putFieldError(CamsPropertyConstants.AssetGlobal.MFR_NAME, CamsKeyConstants.AssetGlobal.ERROR_MFR_NAME_REQUIRED);
                success &= false;
            }
        }
        if (CamsConstants.ACQUISITION_TYPE_CODE_N.equals(assetGlobal.getAcquisitionTypeCode())) {
            UniversalUser universalUser = GlobalVariables.getUserSession().getUniversalUser();
            if (!universalUser.isMember(CamsConstants.Workgroups.WORKGROUP_CM_SUPER_USERS)) {
                putFieldError(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_ACQUISITION_TYPE_CODE_NOT_ALLOWED, new String[] { CamsConstants.Workgroups.WORKGROUP_CM_SUPER_USERS, assetGlobal.getAcquisitionTypeCode() });
                success &= false;
            }
        }
        String statusCode = assetGlobal.getInventoryStatusCode();
        if (StringUtils.isNotBlank(statusCode) && (CamsConstants.InventoryStatusCode.CAPITAL_ASSET_UNDER_CONSTRUCTION.equals(statusCode) || isStatusCodeRetired(statusCode))) {
            putFieldError(CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, CamsKeyConstants.AssetGlobal.ERROR_INVENTORY_STATUS_CODE_INVALID, new String[] { statusCode });
            success &= false;
        }
        return success;
    }

    @Override
    public boolean processSaveDocument(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        AssetGlobal assetGlobal = (AssetGlobal) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        if (!checkReferenceExists(assetGlobal.getAssetHeader())) {
            return false;
        }
        return super.processSaveDocument(document);
    }

    private boolean validateAccount(AssetGlobal assetGlobal) {
        boolean success = true;
        AssetHeader assetHeader = assetGlobal.getAssetHeader();
        assetHeader.refreshReferenceObject(CamsPropertyConstants.AssetHeader.ORGANIZATION_OWNER_ACCOUNT);
        Account organizationOwnerAccount = assetHeader.getOrganizationOwnerAccount();
        if (StringUtils.isNotBlank(assetHeader.getOrganizationOwnerAccountNumber()) && (organizationOwnerAccount == null || organizationOwnerAccount.isAccountClosedIndicator() || organizationOwnerAccount.isExpired())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_OWNER_ACCT_NOT_ACTIVE, new String[] { assetHeader.getOrganizationOwnerChartOfAccountsCode(), assetHeader.getOrganizationOwnerAccountNumber() });
            success &= false;
        }
        return success;
    }

    private boolean validateLocation(AssetGlobal assetGlobal, AssetGlobalDetail assetGlobalDetail) {
        boolean success = true;
        if (StringUtils.isBlank(assetGlobal.getInventoryStatusCode())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, CamsKeyConstants.AssetGlobal.ERROR_INVENTORY_STATUS_REQUIRED);
            success = false;
        }
        assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE);
        if (ObjectUtils.isNull(assetGlobal.getCapitalAssetType())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_ASSET_TYPE_REQUIRED);
            success = false;
        }
        if (success) {
            boolean isCapitalAsset = isCapitalStatus(assetGlobal);
            success = SpringContext.getBean(AssetLocationService.class).validateLocation(LOCATION_FIELD_MAP, assetGlobalDetail, isCapitalAsset, assetGlobal.getCapitalAssetType());
        }
        else {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.VERSION_NUM, CamsKeyConstants.AssetGlobal.ERROR_ASSET_LOCATION_DEPENDENCY);
        }
        return success;
    }

    private boolean validateTagDuplication(String campusTagNumber) {
        // find all assets matching this tag number
        Map<String, String> params = new HashMap<String, String>();
        params.put(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, campusTagNumber);
        Collection<Asset> tagMatches = SpringContext.getBean(BusinessObjectService.class).findMatching(Asset.class, params);
        if (tagMatches != null && !tagMatches.isEmpty()) {
            for (Asset asset : tagMatches) {
                // if found matching, check if status is not retired
                if (!isStatusCodeRetired(asset.getInventoryStatusCode())) {
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, campusTagNumber);
                    return false;
                }
            }
        }
        return true;
    }

}
