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


import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
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
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.AssetLocationService.LocationField;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.service.UniversityDateService;

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
    private static UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
    private static AssetService assetService = SpringContext.getBean(AssetService.class);

    private boolean checkReferenceExists(AssetGlobal assetGlobal, AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;

        if (StringUtils.isBlank(assetGlobal.getAcquisitionTypeCode()) || ObjectUtils.isNull(assetGlobal.getAcquisitionType())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_ACQUISITION_TYPE_CODE_REQUIRED);
            valid = false;
        }

        if (StringUtils.isBlank(assetGlobal.getInventoryStatusCode())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, CamsKeyConstants.AssetGlobal.ERROR_INVENTORY_STATUS_REQUIRED);
            valid = false;
        }

        // Check for account
        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT);
        if (StringUtils.isBlank(assetPaymentDetail.getAccountNumber()) || isAccountInvalid(assetPaymentDetail.getAccount())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_OWNER_ACCT_NOT_ACTIVE, new String[] { assetPaymentDetail.getChartOfAccountsCode(), assetPaymentDetail.getAccountNumber() });
            valid = false;
        }

        valid &= checkObjectCodeExists(assetPaymentDetail);
        return valid;
        }

    private boolean checkObjectCodeExists(AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;

        // Check for ObjectCode
        if (StringUtils.isNotBlank(assetPaymentDetail.getFinancialObjectCode())) {
            if (assetPaymentDetail.getFinancialDocumentPostingYear() == null) {
                Date postedDate = null;
                if (assetPaymentDetail.getExpenditureFinancialDocumentPostedDate() != null) {
                    postedDate = assetPaymentDetail.getExpenditureFinancialDocumentPostedDate();
                }
                else if (assetPaymentDetail.getPaymentApplicationDate() != null) {
                    postedDate = assetPaymentDetail.getPaymentApplicationDate();
                }
                Integer fiscalYear = universityDateService.getFiscalYear(postedDate);

                if (fiscalYear != null) {
                    assetPaymentDetail.setFinancialDocumentPostingYear(fiscalYear);
                }
            }
            assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);
            if (ObjectUtils.isNull(assetPaymentDetail.getObjectCode())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE, CamsKeyConstants.AssetGlobal.ERROR_CAPITAL_OBJECT_CODE_INVALID, assetPaymentDetail.getFinancialObjectCode());
                valid = false;
            }
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

    // TODO: Is this needed any more or not (we are deleting assetHeader)?
    private boolean checkReferenceExists(AssetGlobal assetGlobal) {
        boolean valid = true;
        if (StringUtils.isNotBlank(assetGlobal.getOrganizationOwnerChartOfAccountsCode())) {
            assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_CHART);
            if (ObjectUtils.isNull(assetGlobal.getOrganizationOwnerChartOfAccounts())) {
                putFieldError(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS, CamsKeyConstants.AssetGlobal.ERROR_OWNER_CHART_INVALID, new String[] { assetGlobal.getOrganizationOwnerChartOfAccountsCode() });
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetGlobal.getOrganizationOwnerAccountNumber())) {
            assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT);
            assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT);
            if (ObjectUtils.isNull(assetGlobal.getOrganizationOwnerAccount())) {
                putFieldError(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCT_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_OWNER_ACCT_NUMBER_INVALID, new String[] { assetGlobal.getOrganizationOwnerChartOfAccountsCode(), assetGlobal.getOrganizationOwnerAccountNumber() });
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
                AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) line;
            if (success &= checkReferenceExists(assetGlobal, assetPaymentDetail)) {
                success &= validatePaymentLine(assetGlobal, assetPaymentDetail);
                if (!success) {
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.VERSION_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_ASSET_LOCATION_DEPENDENCY);
            }
        }
        }
        return success;
    }

    private boolean validatePaymentLine(AssetGlobal assetGlobal, AssetPaymentDetail assetPaymentDetail) {
        boolean success = true;

        success &= validateObjectCode(assetPaymentDetail.getObjectCode(), assetGlobal);


        // Validate Financial Document Type Code
        if (StringUtils.isNotBlank(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode())) {
            success = validateDocumentType(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode(), assetGlobal);
        }

        // Validate Financial Posted date
        if (assetPaymentDetail.getExpenditureFinancialDocumentPostedDate() != null) {
            success &= validatePostedDate(assetPaymentDetail.getExpenditureFinancialDocumentPostedDate(), assetPaymentDetail.getFinancialDocumentPostingYear());
        }

        // handle payment information
        // amount should be positive
        if (assetPaymentDetail.getAmount() != null && !assetPaymentDetail.getAmount().isPositive()) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.AMOUNT, CamsKeyConstants.AssetGlobal.ERROR_INVALID_PAYMENT_AMOUNT);
            success &= false;
        }

        // TODO: what are the required fields when asset acquisition type is 'N'?
        // check the business rule: if financial doc num is not same as current doc number, then posting date is required
        // if (assetPaymentDetail.getExpenditureFinancialDocumentPostedDate() == null &&
        // StringUtils.isNotBlank(assetPaymentDetail.getExpenditureFinancialDocumentNumber()) &&
        // !assetPaymentDetail.getExpenditureFinancialDocumentNumber().equalsIgnoreCase(assetGlobal.getDocumentNumber())) {
        // success = false;
        // }

        return success;
    }

    private boolean validateFinanicalPostingYear(AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;
        if (assetPaymentDetail.getFinancialDocumentPostingYear() != null && assetPaymentDetail.getExpenditureFinancialDocumentPostedDate() != null) {

        }
        return valid;
    }

    private boolean validatePostedDate(Date postedDate, Integer financialPostingYear) {
        boolean valid = true;
        Integer fiscalYear = universityDateService.getFiscalYear(postedDate);
        if (fiscalYear == null) {
            throw new ValidationException("University Fiscal year is not defined for date - " + postedDate);
        }
        else if (!financialPostingYear.equals(fiscalYear)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE, CamsKeyConstants.AssetGlobal.ERROR_INVALID_POSTING_DATE_USED_FOR_OBJECT_CODE);
            valid = false;
        }
        return valid;
    }

    private boolean validateDocumentType(String documentTypeCode, AssetGlobal assetGlobal) {
        boolean valid = true;
        // check document Type code if it is set
        if (isCapitalStatus(assetGlobal)) {
            if (isMemberInGroup(CamsConstants.AssetGlobal.ADD_ASSET_ACQUISITION_CODE_GROUP, assetGlobal.getAcquisitionTypeCode()) && !CamsConstants.AssetGlobal.ADD_ASSET_DOCUMENT_TYPE_CODE.equalsIgnoreCase(documentTypeCode)) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE, CamsKeyConstants.AssetGlobal.ERROR_DOCUMENT_TYPE_CODE_NOT_ALLOWED);
                valid = false;
            }

            if (CamsConstants.AssetGlobal.ACQUISITION_TYPE_CODE_N.equalsIgnoreCase(assetGlobal.getAcquisitionTypeCode()) && !CamsConstants.AssetGlobal.NEW_CAPITAL_ASSET_DOCUMENT_TYPE_CODE_FOR.equalsIgnoreCase(documentTypeCode)) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE, CamsKeyConstants.AssetGlobal.ERROR_DOCUMENT_TYPE_CODE_NOT_ALLOWED);
                valid = false;
            }
        }
        else if (isMemberInGroup(CamsConstants.AssetGlobal.NON_CAPITAL_ASSET_DOCUMENT_TYPE_CODE, documentTypeCode)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE, CamsKeyConstants.AssetGlobal.ERROR_DOCUMENT_TYPE_CODE_NOT_ALLOWED);
            valid = false;
        }

        return valid;
    }

    /**
     * This method...
     * 
     * @param assetGlobal
     * @param assetPaymentDetail
     * @param success
     * @return
     */
    private boolean validateObjectCode(ObjectCode objectCode, AssetGlobal assetGlobal) {
        boolean valid = true;
        // Check Object Code
        if (!isCapitalStatus(assetGlobal) && isCapitablObjectCode(objectCode)) {
            // Capital object code shall not be used for a non-capital asset.
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE, CamsKeyConstants.AssetGlobal.ERROR_CAPITAL_OBJECT_CODE_NOT_ALLOWED);
            valid = false;
        }

        // The acquisition type code of (C, F, G, N, P, S, T) requires a capital object code.
        if (Arrays.asList(CamsConstants.AssetGlobal.CAPITAL_OBJECT_ACCQUISITION_CODE_GROUP).contains(assetGlobal.getAcquisitionTypeCode()) && !isCapitablObjectCode(objectCode)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE, CamsKeyConstants.AssetGlobal.ERROR_CAPITAL_OBJECT_CODE_REQUIRED);
            valid = false;
        }
        return valid;
    }

    public boolean isMemberInGroup(String groupName, String member) {
        if (StringUtils.isBlank(groupName) || StringUtils.isBlank(member)) {
            return false;
        }
        return Arrays.asList(groupName.split(";")).contains(member);
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
        String capitalizationThresholdAmount = parameterService.getParameterValue(AssetGlobal.class, CamsConstants.Parameters.CAPITALIZATION_LIMIT_AMOUNT);
        if (isCapitalStatus(assetGlobal) && totalPaymentByAsset.isLessThan(new KualiDecimal(capitalizationThresholdAmount)) && !universalUser.isMember(CamsConstants.Workgroups.WORKGROUP_CM_ADMINISTRATORS)) {
            putFieldError(CamsPropertyConstants.AssetGlobal.VERSION_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAPITAL_ASSET_PAYMENT_AMOUNT_MIN, capitalizationThresholdAmount);
            success &= false;
        }

        // check if amount is less than threshold for non-capital assets for all users
        if (!isCapitalStatus(assetGlobal) && totalPaymentByAsset.isGreaterEqual(new KualiDecimal(capitalizationThresholdAmount))) {
            putFieldError(CamsPropertyConstants.AssetGlobal.VERSION_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_NON_CAPITAL_ASSET_PAYMENT_AMOUNT_MAX);
            success &= false;
        }

        success &= validateObjectSubTypeCode(assetGlobal);

        if (success) {
            setSystemControlledFields(assetGlobal);
        }

        return success;
    }


    private boolean validateObjectSubTypeCode(AssetGlobal assetGlobal) {
        boolean valid = true;
        List<String> objectSubTypeList = new ArrayList<String>();
        // build object sub type list
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);
            if (ObjectUtils.isNotNull(assetPaymentDetail.getObjectCode())) {
                objectSubTypeList.add(assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode());
            }
        }

        if (!assetService.isObjectSubTypeCompatible(objectSubTypeList)) {
            putFieldError(CamsPropertyConstants.AssetGlobal.VERSION_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_INVALID_FIN_OBJECT_SUB_TYPE_CODE);
            valid = false;
        }
        return valid;
    }


    private void setSystemControlledFields(AssetGlobal assetGlobal) {
        List<AssetPaymentDetail> payments = assetGlobal.getAssetPaymentDetails();

        if (isMemberInGroup(CamsConstants.AssetGlobal.ADD_ASSET_ACQUISITION_CODE_GROUP, assetGlobal.getAcquisitionTypeCode())) {
            for (AssetPaymentDetail assetPaymentDetail : payments) {
                // Set for document number
                if (StringUtils.isBlank(assetPaymentDetail.getDocumentNumber())) {
                    assetPaymentDetail.setDocumentNumber(assetGlobal.getDocumentNumber());
                }
                // Set for document type code
                if (StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode())) {
                    assetPaymentDetail.setExpenditureFinancialDocumentTypeCode(CamsConstants.AssetGlobal.ADD_ASSET_DOCUMENT_TYPE_CODE);
                }
                // Set for financial posting year and posting period.
                if (assetPaymentDetail.getExpenditureFinancialDocumentPostedDate() != null) {
                    if (assetPaymentDetail.getFinancialDocumentPostingYear() == null) {
                        assetPaymentDetail.setFinancialDocumentPostingYear(universityDateService.getFiscalYear(assetPaymentDetail.getExpenditureFinancialDocumentPostedDate()));
                    }
                }

            }
        }
    }


    private KualiDecimal totalPaymentByAsset(AssetGlobal assetGlobal) {
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();
        KualiDecimal qty = new KualiDecimal(assetPaymentDetails.size());

        for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
            totalAmount = totalAmount.add(assetPaymentDetail.getAmount());
        }
        if (!qty.isZero()) {
            return totalAmount.divide(qty);
        }
        return totalAmount;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        boolean success = super.processCustomSaveDocumentBusinessRules(document);
        success = validateAccount(assetGlobal);

        String acquisitionTypeCode = assetGlobal.getAcquisitionTypeCode();
        String statusCode = assetGlobal.getInventoryStatusCode();

        if (CamsConstants.AssetGlobal.ACQUISITION_TYPE_CODE_N.equals(acquisitionTypeCode)) {
            UniversalUser universalUser = GlobalVariables.getUserSession().getUniversalUser();
            if (!universalUser.isMember(CamsConstants.Workgroups.WORKGROUP_CM_SUPER_USERS)) {
                putFieldError(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_ACQUISITION_TYPE_CODE_NOT_ALLOWED, new String[] { CamsConstants.Workgroups.WORKGROUP_CM_SUPER_USERS, acquisitionTypeCode });
                success &= false;
            }
        }
        if (StringUtils.isNotBlank(statusCode) && (CamsConstants.InventoryStatusCode.CAPITAL_ASSET_UNDER_CONSTRUCTION.equals(statusCode) || isStatusCodeRetired(statusCode))) {
            putFieldError(CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE, CamsKeyConstants.AssetGlobal.ERROR_INVENTORY_STATUS_CODE_INVALID, new String[] { statusCode });
            success &= false;
        }
        if (StringUtils.isNotBlank(acquisitionTypeCode) && StringUtils.isNotBlank(statusCode)) {
            // check if status code and acquisition type code combination is valid
            success = SpringContext.getBean(ParameterService.class).getParameterEvaluator(AssetGlobal.class, CamsConstants.Parameters.VALID_ASSET_ACQUISITION_TYPES_BY_ASSET_STATUS, CamsConstants.Parameters.INVALID_ASSET_ACQUISITION_TYPES_BY_ASSET_STATUS, statusCode, acquisitionTypeCode).evaluateAndAddError(AssetGlobal.class, MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE);
        }
        success = validateAssetType(assetGlobal);
        if (isCapitalStatus(assetGlobal)) {
            success = validateVendorAndManufacturer(assetGlobal);
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
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        AssetGlobal assetGlobal = (AssetGlobal) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        if (!checkReferenceExists(assetGlobal)) {
            return false;
        }
        return super.processSaveDocument(document);
    }

    private boolean validateAccount(AssetGlobal assetGlobal) {
        boolean success = true;
        assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT);
        Account organizationOwnerAccount = assetGlobal.getOrganizationOwnerAccount();
        if (StringUtils.isNotBlank(assetGlobal.getOrganizationOwnerAccountNumber()) && (organizationOwnerAccount == null || organizationOwnerAccount.isAccountClosedIndicator() || organizationOwnerAccount.isExpired())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_OWNER_ACCT_NOT_ACTIVE, new String[] { assetGlobal.getOrganizationOwnerChartOfAccountsCode(), assetGlobal.getOrganizationOwnerAccountNumber() });
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
        success = validateAssetType(assetGlobal);
        if (success) {
            boolean isCapitalAsset = isCapitalStatus(assetGlobal);
            success = SpringContext.getBean(AssetLocationService.class).validateLocation(LOCATION_FIELD_MAP, assetGlobalDetail, isCapitalAsset, assetGlobal.getCapitalAssetType());
        }
        else {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.VERSION_NUM, CamsKeyConstants.AssetGlobal.ERROR_ASSET_LOCATION_DEPENDENCY);
        }
        return success;
    }

    private boolean validateAssetType(AssetGlobal assetGlobal) {
        boolean success = true;
        assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE);
        if (ObjectUtils.isNull(assetGlobal.getCapitalAssetType())) {
            putFieldError(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE_CODE, CamsKeyConstants.AssetGlobal.ERROR_ASSET_TYPE_REQUIRED);
            success = false;
        }
        return success;
    }

    private boolean validateTagDuplication(String campusTagNumber) {
        // find all assets matching this tag number
        List<Asset> tagMatches = assetService.findActiveAssetsMatchingTagNumber(campusTagNumber);
        if (!tagMatches.isEmpty()) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetGlobal.ERROR_CAMPUS_TAG_NUMBER_DUPLICATE, campusTagNumber);
            return false;
        }
        return true;
    }

}
