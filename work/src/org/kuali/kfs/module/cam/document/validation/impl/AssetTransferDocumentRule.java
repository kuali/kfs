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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.fp.document.TransferOfFundsDocument;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.AssetTransferDocument;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetLocationService.LocationField;
import org.kuali.kfs.module.cam.document.service.AssetObjectCodeService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.AssetTransferService;
import org.kuali.kfs.module.cam.service.AssetLockService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.validation.impl.GeneralLedgerPostingDocumentRuleBase;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.AutoPopulatingList;

public class AssetTransferDocumentRule extends GeneralLedgerPostingDocumentRuleBase {
    private static final Map<LocationField, String> LOCATION_FIELD_MAP = new HashMap<LocationField, String>();
    static {
        LOCATION_FIELD_MAP.put(LocationField.CAMPUS_CODE, CamsPropertyConstants.AssetTransferDocument.CAMPUS_CODE);
        LOCATION_FIELD_MAP.put(LocationField.BUILDING_CODE, CamsPropertyConstants.AssetTransferDocument.BUILDING_CODE);
        LOCATION_FIELD_MAP.put(LocationField.ROOM_NUMBER, CamsPropertyConstants.AssetTransferDocument.BUILDING_ROOM_NUMBER);
        LOCATION_FIELD_MAP.put(LocationField.SUB_ROOM_NUMBER, CamsPropertyConstants.AssetTransferDocument.BUILDING_SUB_ROOM_NUMBER);
        LOCATION_FIELD_MAP.put(LocationField.STREET_ADDRESS, CamsPropertyConstants.AssetTransferDocument.OFF_CAMPUS_ADDRESS);
        LOCATION_FIELD_MAP.put(LocationField.CITY_NAME, CamsPropertyConstants.AssetTransferDocument.OFF_CAMPUS_CITY);
        LOCATION_FIELD_MAP.put(LocationField.STATE_CODE, CamsPropertyConstants.AssetTransferDocument.OFF_CAMPUS_STATE_CODE);
        LOCATION_FIELD_MAP.put(LocationField.ZIP_CODE, CamsPropertyConstants.AssetTransferDocument.OFF_CAMPUS_ZIP);
        LOCATION_FIELD_MAP.put(LocationField.COUNTRY_CODE, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_COUNTRY_CODE);
        LOCATION_FIELD_MAP.put(LocationField.CONTACT_NAME, CamsPropertyConstants.AssetTransferDocument.OFF_CAMPUS_CONTACT_NAME);
    }

    protected UniversityDateService universityDateService;
    protected AssetPaymentService assetPaymentService;
    protected AssetService assetService;
    protected ObjectCodeService objectCodeService;
    protected AssetLockService assetLockService;

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) document;
        Asset asset = assetTransferDocument.getAsset();
        boolean valid = checkReferencesExist(assetTransferDocument);
        assetTransferDocument.clearGlPostables();
        if (valid && (valid &= validateAssetObjectCodeDefn(assetTransferDocument, asset))) {
            SpringContext.getBean(AssetTransferService.class).createGLPostables(assetTransferDocument);
            if (!SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(assetTransferDocument)) {
                throw new ValidationException("General Ledger GLPE generation failed");
            }
        }

        return valid;
    }

    /**
     * Retrieve asset number need to be locked.
     * 
     * @param document
     * @return
     */
    protected List<Long> retrieveAssetNumberForLocking(Document document) {
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) document;
        List<Long> assetNumbers = new ArrayList<Long>();
        if (assetTransferDocument.getAsset().getCapitalAssetNumber() != null) {
            assetNumbers.add(assetTransferDocument.getAsset().getCapitalAssetNumber());
        }
        return assetNumbers;
    }

    protected boolean validateAssetObjectCodeDefn(AssetTransferDocument assetTransferDocument, Asset asset) {

        if ( !isNonCapitalAsset(asset) && StringUtils.isNotBlank(assetTransferDocument.getOrganizationOwnerChartOfAccountsCode()) && StringUtils.isNotBlank(assetTransferDocument.getOrganizationOwnerAccountNumber())) {
            boolean valid = true;
            List<AssetPayment> assetPayments = asset.getAssetPayments();
            ObjectCodeService objectCodeService = (ObjectCodeService) SpringContext.getBean(ObjectCodeService.class);
            for (AssetPayment assetPayment : assetPayments) {
                if (SpringContext.getBean(AssetPaymentService.class).isPaymentEligibleForGLPosting(assetPayment) && !assetPayment.getAccountChargeAmount().isZero()) {
                    // validate for transfer source

                    ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(assetPayment.getChartOfAccountsCode(), assetPayment.getFinancialObjectCode());

                    AssetObjectCode originAssetObjectCode = SpringContext.getBean(AssetObjectCodeService.class).findAssetObjectCode(asset.getOrganizationOwnerChartOfAccountsCode(), objectCode.getFinancialObjectSubTypeCode());
                    if (valid &= validateAssetObjectCode(originAssetObjectCode, asset.getOrganizationOwnerChartOfAccountsCode(), objectCode.getFinancialObjectSubTypeCode())) {
                        // validate object codes used to generate Capitalization/Accumulated Depreciation/Offset GL Postings.
                        valid &= validateFinancialObjectCodes(asset, assetPayment, originAssetObjectCode);
                    }
                    // validate for transfer target
                    AssetObjectCode targetAssetObjectCode = SpringContext.getBean(AssetObjectCodeService.class).findAssetObjectCode(assetTransferDocument.getOrganizationOwnerChartOfAccountsCode(), objectCode.getFinancialObjectSubTypeCode());
                    if (valid &= validateAssetObjectCode(targetAssetObjectCode, assetTransferDocument.getOrganizationOwnerChartOfAccountsCode(), objectCode.getFinancialObjectSubTypeCode())) {
                        // validate object codes used to generate Capitalization/Accumulated Depreciation/Offset GL Postings.
                        valid &= validateFinancialObjectCodes(asset, assetPayment, targetAssetObjectCode);
                    }
                }
            }
            return valid;
        }
        else {
            return true;
        }

    }

    /**
     * Asset Object Code must exist as an active status.
     * 
     * @param asset
     * @param assetPayment
     * @return
     */
    protected boolean validateAssetObjectCode(AssetObjectCode assetObjectCode, String chartOfAccountsCode, String finObjectSubTypeCode) {
        boolean valid = true;
        if (ObjectUtils.isNull(assetObjectCode)) {
            putError(CamsConstants.DOCUMENT_NUMBER_PATH, CamsKeyConstants.GLPosting.ERROR_ASSET_OBJECT_CODE_NOT_FOUND, new String[] { chartOfAccountsCode, finObjectSubTypeCode });
            valid &= false;
        }// check Asset Object Code active
        else if (!assetObjectCode.isActive()) {
            putError(CamsConstants.DOCUMENT_NUMBER_PATH, CamsKeyConstants.GLPosting.ERROR_ASSET_OBJECT_CODE_INACTIVE, new String[] { chartOfAccountsCode, finObjectSubTypeCode });
            valid = false;
        }

        return valid;
    }

    /**
     * Check Financial Object Code for GLPE.
     * 
     * @param asset
     * @param assetPayment
     * @return
     */
    protected boolean validateFinancialObjectCodes(Asset asset, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
        AssetPaymentService assetPaymentService = getAssetPaymentService();
        boolean valid = true;

        if (assetPaymentService.isPaymentEligibleForCapitalizationGLPosting(assetPayment)) {
            // check for capitalization financial object code existing.
            assetObjectCode.refreshReferenceObject(CamsPropertyConstants.AssetObjectCode.CAPITALIZATION_FINANCIAL_OBJECT);
            valid &= validateFinObjectCodeForGLPosting(asset.getOrganizationOwnerChartOfAccountsCode(), assetObjectCode.getCapitalizationFinancialObjectCode(), assetObjectCode.getCapitalizationFinancialObject(), CamsConstants.GLPostingObjectCodeType.CAPITALIZATION);
        }
        if (assetPaymentService.isPaymentEligibleForAccumDeprGLPosting(assetPayment)) {
            // check for accumulate depreciation financial Object Code existing
            assetObjectCode.refreshReferenceObject(CamsPropertyConstants.AssetObjectCode.ACCUMULATED_DEPRECIATION_FINANCIAL_OBJECT);
            valid &= validateFinObjectCodeForGLPosting(asset.getOrganizationOwnerChartOfAccountsCode(), assetObjectCode.getAccumulatedDepreciationFinancialObjectCode(), assetObjectCode.getAccumulatedDepreciationFinancialObject(), CamsConstants.GLPostingObjectCodeType.ACCUMMULATE_DEPRECIATION);
        }
        if (assetPaymentService.isPaymentEligibleForOffsetGLPosting(assetPayment)) {
            // check for offset financial object code existing.
            OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getUniversityDateService().getCurrentFiscalYear(), assetObjectCode.getChartOfAccountsCode(), CamsConstants.AssetTransfer.DOCUMENT_TYPE_CODE, CamsConstants.Postable.GL_BALANCE_TYPE_CODE_AC);
            valid &= validateFinObjectCodeForGLPosting(asset.getOrganizationOwnerChartOfAccountsCode(), offsetDefinition.getFinancialObjectCode(), offsetDefinition.getFinancialObject(), CamsConstants.GLPostingObjectCodeType.OFFSET_AMOUNT);
        }
        return valid;
    }

    /**
     * check existence and active status for given financial Object Code BO.
     * 
     * @param chartCode
     * @param finObjectCode
     * @param finObject
     * @return
     */
    protected boolean validateFinObjectCodeForGLPosting(String chartOfAccountsCode, String finObjectCode, ObjectCode finObject, String glPostingType) {
        boolean valid = true;
        // not defined in Asset Object Code table
        if (StringUtils.isBlank(finObjectCode)) {
            putError(CamsConstants.DOCUMENT_NUMBER_PATH, CamsKeyConstants.GLPosting.ERROR_OBJECT_CODE_FROM_ASSET_OBJECT_CODE_NOT_FOUND, new String[] { glPostingType, chartOfAccountsCode });
            valid = false;
        }
        // check Object Code existing
        else if (ObjectUtils.isNull(finObject)) {
            putError(CamsConstants.DOCUMENT_NUMBER_PATH, CamsKeyConstants.GLPosting.ERROR_OBJECT_CODE_FROM_ASSET_OBJECT_CODE_INVALID, new String[] { glPostingType, finObjectCode, chartOfAccountsCode });
            valid = false;
        }
        // check Object Code active
        else if (!finObject.isActive()) {
            putError(CamsConstants.DOCUMENT_NUMBER_PATH, CamsKeyConstants.GLPosting.ERROR_OBJECT_CODE_FROM_ASSET_OBJECT_CODE_INACTIVE, new String[] { glPostingType, finObjectCode, chartOfAccountsCode });
            valid = false;
        }
        return valid;
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        if (!super.processCustomRouteDocumentBusinessRules(document) || GlobalVariables.getMessageMap().hasErrors())
            return false;

        Asset asset = ((AssetTransferDocument)document).getAsset();
        boolean valid = true;
        if (SpringContext.getBean(AssetService.class).isAssetRetired(asset)) {
            valid &= false;
            GlobalVariables.getMessageMap().putError(CamsConstants.DOC_HEADER_PATH, CamsKeyConstants.Transfer.ERROR_ASSET_RETIRED_NOTRANSFER, asset.getCapitalAssetNumber().toString(), asset.getRetirementReason().getRetirementReasonName());
        }

        if (valid) {
            valid &= applyRules(document);
        }

        // only generate error message if asset is locked by other document without stop saving
        valid &= !this.getAssetLockService().isAssetLocked(retrieveAssetNumberForLocking(document), CamsConstants.DocumentTypeName.ASSET_TRANSFER, document.getDocumentNumber());

        return valid;
    }

    /**
     * This method applies business rules
     * 
     * @param document Transfer Document
     * @return true if all rules are pass
     */
    protected boolean applyRules(Document document) {
        boolean valid = true;
        // check if selected account has plant fund accounts
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) document;

        // validate if asset status = N or D
        String inventoryStatusCode = assetTransferDocument.getAsset().getInventoryStatus().getInventoryStatusCode();
        if (inventoryStatusCode != null && !(StringUtils.equalsIgnoreCase(inventoryStatusCode, CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_ACTIVE) || StringUtils.equalsIgnoreCase(inventoryStatusCode, CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_ACTIVE_2003))) {
            valid &= validateOwnerAccount(assetTransferDocument);
        }

        // validate if location info is available, campus or off-campus
        valid &= validateLocation(assetTransferDocument);
        if (assetTransferDocument.isInterdepartmentalSalesIndicator()) {
            if (StringUtils.isBlank(assetTransferDocument.getTransferOfFundsFinancialDocumentNumber())) {
                putError(CamsPropertyConstants.AssetTransferDocument.TRANSFER_FUND_FINANCIAL_DOC_NUM, CamsKeyConstants.Transfer.ERROR_TRFR_FDOC_REQUIRED);
                valid &= false;
            }
        }

        valid &= validatePaymentObjectCodes(assetTransferDocument);

        return valid;
    }


    /**
     * This method validates location information provided by the user
     * 
     * @param assetTransferDocument Transfer Document
     * @return true is location information is valid for the asset type
     */
    protected boolean validateLocation(AssetTransferDocument assetTransferDocument) {
        GlobalVariables.getMessageMap().addToErrorPath(CamsConstants.DOCUMENT_PATH);
        Asset asset = assetTransferDocument.getAsset();
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE);
        boolean isCapitalAsset = this.getAssetService().isCapitalAsset(asset);
        boolean valid = SpringContext.getBean(AssetLocationService.class).validateLocation(LOCATION_FIELD_MAP, assetTransferDocument, isCapitalAsset, asset.getCapitalAssetType());
        GlobalVariables.getMessageMap().removeFromErrorPath(CamsConstants.DOCUMENT_PATH);
        return valid;
    }


    /**
     * This method checks if reference objects exist in the database or not
     * 
     * @param assetTransferDocument Transfer document
     * @return true if all objects exists in db
     */
    protected boolean checkReferencesExist(AssetTransferDocument assetTransferDocument) {
        boolean valid = true;

        assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ASSET);
        // If asset is loaned, ask a confirmation question
        if (this.getAssetService().isAssetLoaned(assetTransferDocument.getAsset())) {
            putError(CamsPropertyConstants.AssetTransferDocument.ASSET + "." + CamsPropertyConstants.AssetTransferDocument.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Transfer.ERROR_TRFR_LOANED);
            valid &= false;
        }

        if (StringUtils.isNotBlank(assetTransferDocument.getOrganizationOwnerChartOfAccountsCode())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS);
            if (ObjectUtils.isNull(assetTransferDocument.getOrganizationOwnerChartOfAccounts())) {
                putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE, CamsKeyConstants.Transfer.ERROR_OWNER_CHART_CODE_INVALID, assetTransferDocument.getOrganizationOwnerChartOfAccountsCode());
                valid &= false;
            }
        }

        if (StringUtils.isNotBlank(assetTransferDocument.getOrganizationOwnerAccountNumber())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT);
            if (ObjectUtils.isNull(assetTransferDocument.getOrganizationOwnerAccount())) {
                putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.Transfer.ERROR_OWNER_ACCT_INVALID, assetTransferDocument.getOrganizationOwnerAccountNumber(), assetTransferDocument.getOrganizationOwnerChartOfAccountsCode());
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetTransferDocument.getTransferOfFundsFinancialDocumentNumber())) {
            TransferOfFundsDocument transferOfFundsFinancialDocument = assetTransferDocument.getTransferOfFundsFinancialDocument();
            if (ObjectUtils.isNull(transferOfFundsFinancialDocument) || !KFSConstants.DocumentStatusCodes.APPROVED.equals(transferOfFundsFinancialDocument.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode())) {
                putError(CamsPropertyConstants.AssetTransferDocument.TRANSFER_FUND_FINANCIAL_DOC_NUM, CamsKeyConstants.Transfer.ERROR_TRFR_FDOC_INVALID);
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetTransferDocument.getCampusCode())) {
            // assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.CAMPUS);
            if (ObjectUtils.isNull(assetTransferDocument.getCampus())) {
                putError(CamsPropertyConstants.AssetTransferDocument.CAMPUS_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_CAMPUS_CODE, assetTransferDocument.getCampusCode());
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetTransferDocument.getBuildingCode())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.BUILDING);
            if (ObjectUtils.isNull(assetTransferDocument.getBuilding())) {
                putError(CamsPropertyConstants.AssetTransferDocument.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_BUILDING_CODE, assetTransferDocument.getBuildingCode(), assetTransferDocument.getCampusCode());
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetTransferDocument.getBuildingRoomNumber())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.BUILDING_ROOM);
            if (ObjectUtils.isNull(assetTransferDocument.getBuildingRoom())) {
                putError(CamsPropertyConstants.AssetTransferDocument.BUILDING_ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_INVALID_ROOM_NUMBER, assetTransferDocument.getBuildingCode(), assetTransferDocument.getBuildingRoomNumber(), assetTransferDocument.getCampusCode());
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetTransferDocument.getAssetRepresentative().getPrincipalName())) {
            PersonService personService = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class);
            Person person = personService.getPersonByPrincipalName(assetTransferDocument.getAssetRepresentative().getPrincipalName());
            if (person != null) {
                assetTransferDocument.setAssetRepresentative(person);
                assetTransferDocument.setRepresentativeUniversalIdentifier(person.getPrincipalId());
            }
            else {
                putError(CamsPropertyConstants.AssetTransferDocument.REP_USER_AUTH_ID, CamsKeyConstants.Transfer.ERROR_INVALID_USER_AUTH_ID, assetTransferDocument.getAssetRepresentative().getPrincipalName());
                valid &= false;
            }
        }
        return valid;
    }


    /**
     * This method validates the new owner organization and account provided
     * 
     * @param assetTransferDocument
     * @return
     */
    protected boolean validateOwnerAccount(AssetTransferDocument assetTransferDocument) {
        boolean valid = true;
        
        Asset asset = assetTransferDocument.getAsset();
        /*
        String finObjectSubTypeCode = asset.getFinancialObjectSubTypeCode();
        if (ObjectUtils.isNotNull(asset.getAssetPayments()) && !asset.getAssetPayments().isEmpty()) {
            AssetPayment firstAssetPayment = asset.getAssetPayments().get(0);
            firstAssetPayment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
            ObjectCodeService objectCodeService = (ObjectCodeService) SpringContext.getBean(ObjectCodeService.class);

            ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(firstAssetPayment.getChartOfAccountsCode(),firstAssetPayment.getFinancialObjectCode());
            finObjectSubTypeCode = objectCode.getFinancialObjectSubTypeCode();
        }
        boolean assetMovable = getAssetService().isAssetMovableCheckByPayment(finObjectSubTypeCode);
        */
        
        /*
         * Note: The following authorization checking on non-movable asset is already done when Transfer link is generated upon asset lookup.
         * It is redundant here. Consider removing it. 
         */        
        boolean assetMovable = getAssetService().isAssetMovableCheckByPayment(asset);        
        FinancialSystemTransactionalDocumentAuthorizerBase documentAuthorizer = (FinancialSystemTransactionalDocumentAuthorizerBase) SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer(assetTransferDocument);
        boolean isAuthorizedTransferMovable = documentAuthorizer.isAuthorized(assetTransferDocument, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.TRANSFER_NON_MOVABLE_ASSETS, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        
        // KFSMI-6169 - Not check permission for Transfer Non-Movable Assets when user approve the doc.
        if (!assetTransferDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested()){
            if (!assetMovable && !isAuthorizedTransferMovable) {
                GlobalVariables.getMessageMap().putErrorForSectionId(CamsPropertyConstants.COMMON_ERROR_SECTION_ID, CamsKeyConstants.Transfer.ERROR_INVALID_USER_GROUP_FOR_TRANSFER_NONMOVABLE_ASSET, asset.getCapitalAssetNumber().toString());
                valid &= false;
            }
        }

        // check if account is valid
        Account organizationOwnerAccount = assetTransferDocument.getOrganizationOwnerAccount();
        if (ObjectUtils.isNotNull(organizationOwnerAccount) && (organizationOwnerAccount.isExpired())) {
            putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.Transfer.ERROR_OWNER_ACCT_INVALID, assetTransferDocument.getOrganizationOwnerAccountNumber(), assetTransferDocument.getOrganizationOwnerChartOfAccountsCode());
            valid &= false;
        }
        else if (getAssetService().isCapitalAsset(asset) && !asset.getAssetPayments().isEmpty()) {
            // for a capital asset, check if plant account is defined
            Organization ownerOrg = organizationOwnerAccount.getOrganization();
            Account campusPlantAccount = ownerOrg.getCampusPlantAccount();
            Account organizationPlantAccount = ownerOrg.getOrganizationPlantAccount();

            if (assetMovable && ObjectUtils.isNull(organizationPlantAccount)) {
                putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.Transfer.ERROR_ORG_PLANT_FUND_UNKNOWN);
                valid &= false;
            }
            if (!assetMovable && ObjectUtils.isNull(campusPlantAccount)) {
                putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.Transfer.ERROR_CAMPUS_PLANT_FUND_UNKNOWN);
                valid &= false;
            }
        }
        return valid;
    }

    /**
     * checks that all the asset payments to be transfer has a valid object code in the new Chart of account code and current fiscal
     * year
     * 
     * @param assetTransferDocument
     * @return
     */
    protected boolean validatePaymentObjectCodes(AssetTransferDocument assetTransferDocument) {
        boolean valid = true;
        List<AssetPayment> assetPayments = assetTransferDocument.getAsset().getAssetPayments();

        String chartOfAccountsCode = assetTransferDocument.getOrganizationOwnerChartOfAccountsCode();
        Integer fiscalYear = getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear();

        for (AssetPayment assetPayment : assetPayments) {
            if (!CamsConstants.AssetPayment.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode())) {
                if (this.getObjectCodeService().getByPrimaryId(fiscalYear, chartOfAccountsCode, assetPayment.getFinancialObjectCode()) == null) {
                    putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE, CamsKeyConstants.Transfer.ERROR_PAYMENT_OBJECT_CODE_NOT_FOUND, new String[] { assetPayment.getFinancialObjectCode(), fiscalYear.toString() });
                    valid = false;
                }
            }
        }
        return valid;
    }

    /**
     * Convenience method to append the path prefix
     */
    public AutoPopulatingList<ErrorMessage> putError(String propertyName, String errorKey, String... errorParameters) {
        return GlobalVariables.getMessageMap().putError(CamsConstants.DOCUMENT_PATH + "." + propertyName, errorKey, errorParameters);
    }

    private boolean isNonCapitalAsset(Asset asset) {
        boolean isNonCapitalAsset = false;
        
        List<String> capitalAssetAquisitionTypeCodes = new ArrayList<String>();
        
        capitalAssetAquisitionTypeCodes.addAll(SpringContext.getBean(ParameterService.class).getParameterValuesAsString(AssetGlobal.class, CamsConstants.AssetGlobal.CAPITAL_OBJECT_ACQUISITION_CODE_PARAM));
        capitalAssetAquisitionTypeCodes.addAll(SpringContext.getBean(ParameterService.class).getParameterValuesAsString(AssetGlobal.class, CamsConstants.AssetGlobal.NON_NEW_ACQUISITION_GROUP_PARAM));
        capitalAssetAquisitionTypeCodes.addAll(SpringContext.getBean(ParameterService.class).getParameterValuesAsString(AssetGlobal.class, CamsConstants.AssetGlobal.NEW_ACQUISITION_CODE_PARAM));
        capitalAssetAquisitionTypeCodes.addAll(SpringContext.getBean(ParameterService.class).getParameterValuesAsString(Asset.class, CamsConstants.AssetGlobal.FABRICATED_ACQUISITION_CODE));
        capitalAssetAquisitionTypeCodes.addAll(SpringContext.getBean(ParameterService.class).getParameterValuesAsString(AssetGlobal.class, CamsConstants.AssetGlobal.PRE_TAGGING_ACQUISITION_CODE));
        
        if( ObjectUtils.isNotNull(asset.getAcquisitionTypeCode()) &&
                capitalAssetAquisitionTypeCodes.contains(asset.getAcquisitionTypeCode()) ) isNonCapitalAsset = false;
        else isNonCapitalAsset = true;
        
        return isNonCapitalAsset;
    }
    
    public UniversityDateService getUniversityDateService() {
        if (this.universityDateService == null) {
            this.universityDateService = SpringContext.getBean(UniversityDateService.class);
        }
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public AssetPaymentService getAssetPaymentService() {
        if (this.assetPaymentService == null) {
            this.assetPaymentService = SpringContext.getBean(AssetPaymentService.class);

        }
        return assetPaymentService;
    }

    public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
        this.assetPaymentService = assetPaymentService;
    }

    public AssetService getAssetService() {
        if (this.assetService == null) {
            this.assetService = SpringContext.getBean(AssetService.class);
        }
        return assetService;
    }

    public AssetLockService getAssetLockService() {
        if (this.assetLockService == null) {
            this.assetLockService = SpringContext.getBean(AssetLockService.class);
        }
        return assetLockService;
    }


    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    public ObjectCodeService getObjectCodeService() {
        if (this.objectCodeService == null) {
            this.objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        }
        return objectCodeService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }
}
