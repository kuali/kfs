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
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsConstants.DocumentTypeName;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.gl.AssetRetirementGeneralLedgerPendingEntrySource;
import org.kuali.kfs.module.cam.document.service.AssetObjectCodeService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business rules applicable to AssetLocationGlobal documents.
 */
public class AssetRetirementGlobalRule extends MaintenanceDocumentRuleBase {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementGlobalRule.class);

    protected PersistableBusinessObject bo;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        AssetRetirementGlobal newRetirementGlobal = (AssetRetirementGlobal) super.getNewBo();
        newRetirementGlobal.refreshNonUpdateableReferences();
        for (AssetRetirementGlobalDetail detail : newRetirementGlobal.getAssetRetirementGlobalDetails()) {
            detail.refreshNonUpdateableReferences();
        }
    }

    /**
     * Forces the processing of rules when saving.
     *
     * @param document MaintenanceDocument
     * @return boolean true when valid; Namely we need to enforce foreign key constraints else
     *         processCustomSaveDocumentBusinessRules does not force user back to the document for error correction.
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#isDocumentValidForSave(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean isDocumentValidForSave(MaintenanceDocument document) {
        boolean valid = super.isDocumentValidForSave(document);

        if (valid) {
            AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();

            setupConvenienceObjects();
            valid &= assetRetirementValidation(assetRetirementGlobal, document);
        }

        return valid;
    }


    /**
     * Processes rules when saving this global.
     *
     * @param document MaintenanceDocument type of document to be processed.
     * @return boolean true when success
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = true;
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();

        setupConvenienceObjects();
        valid &= assetRetirementValidation(assetRetirementGlobal, document);

        if ((valid && super.processCustomSaveDocumentBusinessRules(document)) && !getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal) && !allPaymentsFederalOwned(assetRetirementGlobal)) {
            // Check if Asset Object Code and Object code exists and active.
            if (valid &= validateObjectCodesForGLPosting(assetRetirementGlobal)) {
                // create poster
                AssetRetirementGeneralLedgerPendingEntrySource assetRetirementGlPoster = new AssetRetirementGeneralLedgerPendingEntrySource((FinancialSystemDocumentHeader) document.getDocumentHeader());
                // create postables
                getAssetRetirementService().createGLPostables(assetRetirementGlobal, assetRetirementGlPoster);

                if (SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(assetRetirementGlPoster)) {
                    assetRetirementGlobal.setGeneralLedgerPendingEntries(assetRetirementGlPoster.getPendingEntries());
                }
                else {
                    assetRetirementGlPoster.getPendingEntries().clear();
                }
            }
        }

        // add doc header description if retirement reason is "MERGED"
        if (CamsConstants.AssetRetirementReasonCode.MERGED.equals(assetRetirementGlobal.getRetirementReasonCode())) {
            if (!document.getDocumentHeader().getDocumentDescription().toLowerCase().contains(CamsConstants.AssetRetirementGlobal.MERGE_AN_ASSET_DESCRIPTION.toLowerCase())) {
                Integer maxDocumentDescription = ddService.getAttributeMaxLength(DocumentHeader.class, KRADPropertyConstants.DOCUMENT_DESCRIPTION);
                String documentDescription = CamsConstants.AssetRetirementGlobal.MERGE_AN_ASSET_DESCRIPTION + " " + document.getDocumentHeader().getDocumentDescription();
                documentDescription = StringUtils.left(documentDescription, maxDocumentDescription);
                document.getDocumentHeader().setDocumentDescription(documentDescription);
            }
        }
        // get asset locks
        List<Long> capitalAssetNumbers = retrieveAssetNumbersForLocking(assetRetirementGlobal);
        valid &= !this.getCapitalAssetManagementModuleService().isAssetLocked(capitalAssetNumbers, DocumentTypeName.ASSET_RETIREMENT_GLOBAL, document.getDocumentNumber());

        return valid;
    }

    protected List<Long> retrieveAssetNumbersForLocking(AssetRetirementGlobal assetRetirementGlobal) {
        List<Long> capitalAssetNumbers = new ArrayList<Long>();
        if (getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal) && assetRetirementGlobal.getMergedTargetCapitalAssetNumber() != null) {
            capitalAssetNumbers.add(assetRetirementGlobal.getMergedTargetCapitalAssetNumber());
        }

        for (AssetRetirementGlobalDetail retirementDetail : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            if (retirementDetail.getCapitalAssetNumber() != null) {
                capitalAssetNumbers.add(retirementDetail.getCapitalAssetNumber());
            }
        }
        return capitalAssetNumbers;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = super.processCustomRouteDocumentBusinessRules(document);

        WorkflowDocument workflowDoc = document.getDocumentHeader().getWorkflowDocument();
        // adding asset locks
        if (!GlobalVariables.getMessageMap().hasErrors() && (workflowDoc.isInitiated() || workflowDoc.isSaved())) {
            valid &= setAssetLocks(document);

        }
        return valid;
    }

    protected boolean setAssetLocks(MaintenanceDocument document) {
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();
        // get asset locks
        return this.getCapitalAssetManagementModuleService().storeAssetLocks(retrieveAssetNumbersForLocking(assetRetirementGlobal), document.getDocumentNumber(), DocumentTypeName.ASSET_RETIREMENT_GLOBAL, null);
    }

    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }


    /**
     * Check if all asset payments are federal owned.
     *
     * @param assetRetirementGlobal
     * @return
     */
    protected boolean allPaymentsFederalOwned(AssetRetirementGlobal assetRetirementGlobal) {
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();
        for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
            for (AssetPayment assetPayment : assetRetirementGlobalDetail.getAsset().getAssetPayments()) {
                if (!getAssetPaymentService().isPaymentFederalOwned(assetPayment)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Validate Asset Object Codes and Fin Object Codes eligible for GL Posting.
     *
     * @param assetRetirementGlobal
     * @return
     */
    protected boolean validateObjectCodesForGLPosting(AssetRetirementGlobal assetRetirementGlobal) {
        boolean valid = true;
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();
        Asset asset = null;
        List<AssetPayment> assetPayments = null;

        for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
            asset = assetRetirementGlobalDetail.getAsset();
            assetPayments = asset.getAssetPayments();
            for (AssetPayment assetPayment : assetPayments) {
                if (!getAssetPaymentService().isPaymentFederalOwned(assetPayment)) {
                    // validate Asset Object Code and Financial Object Codes respectively
                    if (valid &= validateAssetObjectCode(asset, assetPayment)) {
                        valid &= validateFinancialObjectCodes(asset, assetPayment);
                    }
                }
            }
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
    protected boolean validateFinancialObjectCodes(Asset asset, AssetPayment assetPayment) {
        AssetPaymentService assetPaymentService = getAssetPaymentService();
        boolean valid = true;
        ObjectCodeService objectCodeService = (ObjectCodeService) SpringContext.getBean(ObjectCodeService.class);
        ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(assetPayment.getChartOfAccountsCode(), assetPayment.getFinancialObjectCode());

        AssetObjectCode assetObjectCode = getAssetObjectCodeService().findAssetObjectCode(asset.getOrganizationOwnerChartOfAccountsCode(), objectCode.getFinancialObjectSubTypeCode());
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
            valid &= validateFinObjectCodeForGLPosting(
                    asset.getOrganizationOwnerChartOfAccountsCode(),
                    SpringContext.getBean(ParameterService.class).getParameterValueAsString(AssetRetirementGlobal.class, CamsConstants.Parameters.DEFAULT_GAIN_LOSS_DISPOSITION_OBJECT_CODE)
                    , getAssetRetirementService().getOffsetFinancialObject(asset.getOrganizationOwnerChartOfAccountsCode())
                    , CamsConstants.GLPostingObjectCodeType.OFFSET_AMOUNT);
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
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.AssetRetirementGlobal.SECTION_ID_ASSET_DETAIL_INFORMATION, CamsKeyConstants.GLPosting.ERROR_OBJECT_CODE_FROM_ASSET_OBJECT_CODE_NOT_FOUND, new String[] { glPostingType, chartOfAccountsCode });
            valid = false;
        }
        // check Object Code existing
        else if (ObjectUtils.isNull(finObject)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.AssetRetirementGlobal.SECTION_ID_ASSET_DETAIL_INFORMATION, CamsKeyConstants.GLPosting.ERROR_OBJECT_CODE_FROM_ASSET_OBJECT_CODE_INVALID, new String[] { glPostingType, finObjectCode, chartOfAccountsCode });
            valid = false;
        }
        // check Object Code active
        else if (!finObject.isActive()) {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.AssetRetirementGlobal.SECTION_ID_ASSET_DETAIL_INFORMATION, CamsKeyConstants.GLPosting.ERROR_OBJECT_CODE_FROM_ASSET_OBJECT_CODE_INACTIVE, new String[] { glPostingType, finObjectCode, chartOfAccountsCode });
            valid = false;
        }
        return valid;
    }

    /**
     * Asset Object Code must exist as an active status.
     *
     * @param asset
     * @param assetPayment
     * @return
     */
    protected boolean validateAssetObjectCode(Asset asset, AssetPayment assetPayment) {
        boolean valid = true;
        ObjectCodeService objectCodeService = (ObjectCodeService) SpringContext.getBean(ObjectCodeService.class);
        ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(assetPayment.getChartOfAccountsCode(), assetPayment.getFinancialObjectCode());

        AssetObjectCode assetObjectCode = getAssetObjectCodeService().findAssetObjectCode(asset.getOrganizationOwnerChartOfAccountsCode(), objectCode.getFinancialObjectSubTypeCode());
        // check Asset Object Code existing.
        if (ObjectUtils.isNull(assetObjectCode)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.AssetRetirementGlobal.SECTION_ID_ASSET_DETAIL_INFORMATION, CamsKeyConstants.GLPosting.ERROR_ASSET_OBJECT_CODE_NOT_FOUND, new String[] { asset.getOrganizationOwnerChartOfAccountsCode(), objectCode.getFinancialObjectSubTypeCode() });
            valid = false;
        }
        // check Asset Object Code active
        else if (!assetObjectCode.isActive()) {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.AssetRetirementGlobal.SECTION_ID_ASSET_DETAIL_INFORMATION, CamsKeyConstants.GLPosting.ERROR_ASSET_OBJECT_CODE_INACTIVE, new String[] { asset.getOrganizationOwnerChartOfAccountsCode(), objectCode.getFinancialObjectSubTypeCode() });
            valid = false;
        }

        return valid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean success = true;
        AssetRetirementGlobalDetail assetRetirementGlobalDetail = (AssetRetirementGlobalDetail) line;
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getDocumentBusinessObject();

        if (!checkEmptyValue(assetRetirementGlobalDetail.getCapitalAssetNumber())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_BLANK_CAPITAL_ASSET_NUMBER);
            return false;
        }
        assetRetirementGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetLocationGlobalDetail.ASSET);

        if (ObjectUtils.isNull(assetRetirementGlobalDetail.getAsset())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_CAPITAL_ASSET_NUMBER, assetRetirementGlobalDetail.getCapitalAssetNumber().toString());
            return false;
        }

        if (this.getAssetService().isAssetLoaned(assetRetirementGlobalDetail.getAsset())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_LOANED_ASSET_CANNOT_RETIRED);
            success = false;
        }
        else if (!getAssetService().isDocumentEnrouting(document)) {
            success &= checkRetirementDetailOneLine(assetRetirementGlobalDetail, assetRetirementGlobal, document);
            success &= checkRetireMultipleAssets(assetRetirementGlobal.getRetirementReasonCode(), assetRetirementGlobal.getAssetRetirementGlobalDetails(), new Integer(0), document);
        }

        // Calculate summary fields in order to show the values even though add new line fails.
        for (AssetRetirementGlobalDetail detail : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            getAssetService().setAssetSummaryFields(detail.getAsset());
        }
        return success & super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
    }

    /**
     * Check if only single asset is allowed to retire.
     *
     * @param retirementReasonCode
     * @param assetRetirementDetails
     * @param maxNumber
     * @param maintenanceDocument
     * @return
     */
    protected boolean checkRetireMultipleAssets(String retirementReasonCode, List<AssetRetirementGlobalDetail> assetRetirementDetails, Integer maxNumber, MaintenanceDocument maintenanceDocument) {
        boolean success = true;

        if (assetRetirementDetails.size() > maxNumber && !getAssetRetirementService().isAllowedRetireMultipleAssets(maintenanceDocument)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.AssetRetirementGlobal.SECTION_ID_ASSET_DETAIL_INFORMATION, CamsKeyConstants.Retirement.ERROR_MULTIPLE_ASSET_RETIRED);
            success = false;
        }

        return success;
    }

    /**
     * This method validates each asset to be retired.
     *
     * @param assetRetirementGlobalDetails
     * @param maintenanceDocument
     * @return
     */
    protected boolean validateRetirementDetails(AssetRetirementGlobal assetRetirementGlobal, MaintenanceDocument maintenanceDocument) {
        boolean success = true;

        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        if (assetRetirementGlobalDetails.size() == 0) {
            success = false;
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.AssetRetirementGlobal.SECTION_ID_ASSET_DETAIL_INFORMATION, CamsKeyConstants.Retirement.ERROR_ASSET_RETIREMENT_GLOBAL_NO_ASSET);
        }
        else {
            // validate each asset retirement detail
            int index = 0;
            for (AssetRetirementGlobalDetail detail : assetRetirementGlobalDetails) {
                String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "[" + index++ + "]";
                GlobalVariables.getMessageMap().addToErrorPath(errorPath);

                success &= checkRetirementDetailOneLine(detail, assetRetirementGlobal, maintenanceDocument);

                GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            }
        }
        return success;
    }


    /**
     * This method validates one asset is a valid asset and no duplicate with target asset when merge.
     *
     * @param assetRetirementGlobalDetail
     * @param assetRetirementGlobal
     * @param maintenanceDocument
     * @return
     */
    protected boolean checkRetirementDetailOneLine(AssetRetirementGlobalDetail assetRetirementGlobalDetail, AssetRetirementGlobal assetRetirementGlobal, MaintenanceDocument maintenanceDocument) {
        boolean success = true;

        assetRetirementGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET);

        Asset asset = assetRetirementGlobalDetail.getAsset();

        if (ObjectUtils.isNull(asset)) {
            success = false;
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.AssetRetirementGlobal.SECTION_ID_ASSET_DETAIL_INFORMATION, CamsKeyConstants.Retirement.ERROR_INVALID_CAPITAL_ASSET_NUMBER, assetRetirementGlobalDetail.getCapitalAssetNumber().toString());
        }
        else {
            success &= validateActiveCapitalAsset(asset);
            success &= validateNonMoveableAsset(asset, maintenanceDocument);

            if (getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal)) {
                success &= validateDuplicateAssetNumber(assetRetirementGlobal.getMergedTargetCapitalAssetNumber(), assetRetirementGlobalDetail.getCapitalAssetNumber());
            }

            // Set asset non persistent fields
            getAssetService().setAssetSummaryFields(asset);
        }

        return success;
    }

    /**
     * Check for Merge Asset, no duplicate capitalAssetNumber between "from" and "to".
     *
     * @param targetAssetNumber
     * @param sourceAssetNumber
     * @return
     */
    protected boolean validateDuplicateAssetNumber(Long targetAssetNumber, Long sourceAssetNumber) {
        boolean success = true;

        if (getAssetService().isCapitalAssetNumberDuplicate(targetAssetNumber, sourceAssetNumber)) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_DUPLICATE_CAPITAL_ASSET_NUMBER_WITH_TARGET, sourceAssetNumber.toString());
            success = false;
        }

        return success;
    }

    /**
     * User must be in work group CM_SUPER_USERS to retire a non-moveable asset.
     *
     * @param asset
     * @param maintenanceDocument
     * @return
     */
    protected boolean validateNonMoveableAsset(Asset asset, MaintenanceDocument maintenanceDocument) {
        boolean success = true;

        FinancialSystemMaintenanceDocumentAuthorizerBase documentAuthorizer = (FinancialSystemMaintenanceDocumentAuthorizerBase) SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer(maintenanceDocument);
        boolean isAuthorized = documentAuthorizer.isAuthorized(maintenanceDocument, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.RETIRE_NON_MOVABLE_ASSETS, GlobalVariables.getUserSession().getPerson().getPrincipalId());

        if (!getAssetService().isAssetMovableCheckByAsset(asset) && !isAuthorized) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_USER_GROUP_FOR_NON_MOVEABLE_ASSET, asset.getCapitalAssetNumber().toString());
            success = false;
        }

        return success;
    }


    /**
     * Validate Asset Retirement Global and Details.
     *
     * @param assetRetirementGlobal
     * @param maintenanceDocument
     * @return
     */
    protected boolean assetRetirementValidation(AssetRetirementGlobal assetRetirementGlobal, MaintenanceDocument maintenanceDocument) {
        boolean valid = true;

        valid &= validateRequiredGlobalFields(assetRetirementGlobal);
        if (getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal)) {
            valid &= validateMergeTargetAsset(assetRetirementGlobal);
        }

        if (!getAssetService().isDocumentEnrouting(maintenanceDocument)) {
            valid &= validateRetirementDetails(assetRetirementGlobal, maintenanceDocument);
            valid &= checkRetireMultipleAssets(assetRetirementGlobal.getRetirementReasonCode(), assetRetirementGlobal.getAssetRetirementGlobalDetails(), new Integer(1), maintenanceDocument);
        }
        return valid;
    }


    /**
     * Validate mergedTargetCapitalAsset. Only valid and active capital asset is allowed.
     *
     * @param assetRetirementGlobal
     * @return
     */
    protected boolean validateMergeTargetAsset(AssetRetirementGlobal assetRetirementGlobal) {
        boolean valid = true;
        Asset targetAsset = assetRetirementGlobal.getMergedTargetCapitalAsset();
        Long targetAssetNumber = assetRetirementGlobal.getMergedTargetCapitalAssetNumber();

        if (!checkEmptyValue(targetAssetNumber)) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_BLANK_CAPITAL_ASSET_NUMBER);
            valid = false;
        }
        else if (ObjectUtils.isNull(targetAsset)) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_MERGED_TARGET_ASSET_NUMBER, targetAssetNumber.toString());
            valid = false;
        }
        else {
            // Check asset of capital and active
            if (!getAssetService().isCapitalAsset(targetAsset)) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_CAPITAL_ASSET_RETIREMENT, targetAssetNumber.toString());
                valid = false;
            }
            if (getAssetService().isAssetRetired(targetAsset)) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_ACTIVE_ASSET_RETIREMENT, targetAssetNumber.toString());
                valid = false;
            }
        }

        // Validating the mergeAssetDescription is not blank
        if (!checkEmptyValue(assetRetirementGlobal.getMergedTargetCapitalAssetDescription())) {
            String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetRetirementGlobal.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_DESC).getLabel();
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_DESC, KFSKeyConstants.ERROR_REQUIRED, label);
            valid = false;
        }

        return valid;
    }


    /**
     * Only active capital equipment can be retired using the asset retirement document.
     *
     * @param valid
     * @param detail
     * @return
     */
    protected boolean validateActiveCapitalAsset(Asset asset) {
        boolean valid = true;

        if (!getAssetService().isCapitalAsset(asset)) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_CAPITAL_ASSET_RETIREMENT, asset.getCapitalAssetNumber().toString());
            valid = false;
        }
        else if (getAssetService().isAssetRetired(asset)) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_ACTIVE_ASSET_RETIREMENT, asset.getCapitalAssetNumber().toString());
            valid = false;
        }
        else if (!this.validateAssetOnLoan(asset)) {
            valid = false;
        }

        return valid;
    }


    /**
     * Validate required fields for given retirement reason code
     *
     * @param assetRetirementGlobal
     * @return
     */
    protected boolean validateRequiredGlobalFields(AssetRetirementGlobal assetRetirementGlobal) {
        boolean valid = true;

        if (getAssetRetirementService().isAssetRetiredBySold(assetRetirementGlobal)) {
            if (StringUtils.isBlank(assetRetirementGlobal.getBuyerDescription())) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobalDetail.BUYER_DESCRIPTION, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.BUYER_DESCRIPTION, getAssetRetirementService().getAssetRetirementReasonName(assetRetirementGlobal) });
                valid = false;
            }
            if (assetRetirementGlobal.getSalePrice() == null) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobalDetail.SALE_PRICE, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.SALE_PRICE, getAssetRetirementService().getAssetRetirementReasonName(assetRetirementGlobal) });
                valid = false;
            }
            valid &= validateCashReceiptFinancialDocumentNumber(assetRetirementGlobal.getCashReceiptFinancialDocumentNumber());
        }
        else if (getAssetRetirementService().isAssetRetiredByAuction(assetRetirementGlobal)) {
            valid = validateCashReceiptFinancialDocumentNumber(assetRetirementGlobal.getCashReceiptFinancialDocumentNumber());
        }
        else if (getAssetRetirementService().isAssetRetiredByExternalTransferOrGift(assetRetirementGlobal) && StringUtils.isBlank(assetRetirementGlobal.getRetirementInstitutionName())) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobalDetail.RETIREMENT_INSTITUTION_NAME, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.RETIREMENT_INSTITUTION_NAME, getAssetRetirementService().getAssetRetirementReasonName(assetRetirementGlobal) });
            valid = false;
        }
        else if (getAssetRetirementService().isAssetRetiredByTheft(assetRetirementGlobal) && StringUtils.isBlank(assetRetirementGlobal.getPaidCaseNumber())) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobalDetail.PAID_CASE_NUMBER, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.PAID_CASE_NUMBER, getAssetRetirementService().getAssetRetirementReasonName(assetRetirementGlobal) });
            valid = false;
        }
        return valid;
    }

    /**
     * validates Cash Receipt Financial Document Number
     *
     * @param sharedRetirementInfo
     * @return boolean
     */
    protected boolean validateCashReceiptFinancialDocumentNumber(String documentNumber) {
        boolean valid = true;
        if (StringUtils.isNotBlank(documentNumber)) {
            Map retirementInfoMap = new HashMap();
            retirementInfoMap.put(CamsPropertyConstants.AssetGlobal.DOCUMENT_NUMBER, documentNumber);
            bo = (FinancialSystemDocumentHeader) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(FinancialSystemDocumentHeader.class, retirementInfoMap);
            if (ObjectUtils.isNull(bo)) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobalDetail.CASH_RECEIPT_FINANCIAL_DOCUMENT_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_RETIREMENT_DETAIL_INFO, new String[] { CamsConstants.RetirementLabel.CASH_RECEIPT_FINANCIAL_DOCUMENT_NUMBER, documentNumber });
                valid = false;
            }
        }
        return valid;
    }


    /**
     * Validates whether or not asset is on loan status
     *
     * @param assetRetirementGlobalDetail
     * @return boolean
     */
    protected boolean validateAssetOnLoan(Asset asset) {
        boolean success = true;
        if (this.getAssetService().isAssetLoaned(asset)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.AssetRetirementGlobal.SECTION_ID_ASSET_DETAIL_INFORMATION, CamsKeyConstants.Retirement.ERROR_LOANED_ASSET_CANNOT_RETIRED);
            success = false;
        }
        return success;
    }


    protected AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    protected AssetRetirementService getAssetRetirementService() {
        return SpringContext.getBean(AssetRetirementService.class);
    }

    protected AssetPaymentService getAssetPaymentService() {
        return SpringContext.getBean(AssetPaymentService.class);
    }

    protected AssetObjectCodeService getAssetObjectCodeService() {
        return SpringContext.getBean(AssetObjectCodeService.class);
    }
}
