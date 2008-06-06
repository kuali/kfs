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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.gl.AssetRetirementGeneralLedgerPendingEntrySource;
import org.kuali.module.cams.service.AssetRetirementService;
import org.kuali.module.cams.service.AssetService;

/**
 * Business rules applicable to AssetLocationGlobal documents.
 */
public class AssetRetirementGlobalRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementGlobalRule.class);

    private static AssetService assetService = SpringContext.getBean(AssetService.class);
    private static AssetRetirementService assetRetirementService = SpringContext.getBean(AssetRetirementService.class);

    /**
     * Constructs a AssetLocationGlobalRule
     */
    public AssetRetirementGlobalRule() {
    }

    /**
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
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
     * Processes rules when saving this global.
     * 
     * @param document MaintenanceDocument type of document to be processed.
     * @return boolean true when success
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = true;
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();

        setupConvenienceObjects();
        valid &= assetRetirementValidation(assetRetirementGlobal);

        if ((valid & super.processCustomSaveDocumentBusinessRules(document)) && !assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
            // create poster
            AssetRetirementGeneralLedgerPendingEntrySource assetRetirementGlPoster = new AssetRetirementGeneralLedgerPendingEntrySource(document.getDocumentHeader());
            // create postables
            if (!(valid = assetRetirementService.createGLPostables(assetRetirementGlobal, assetRetirementGlPoster))) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.VERSION_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_OBJECT_CODE_FROM_ASSET_OBJECT_CODE);
                return valid;
            }
            if (SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(assetRetirementGlPoster)) {
                assetRetirementGlobal.setGeneralLedgerPendingEntries(assetRetirementGlPoster.getPendingEntries());
            }
            else {
                assetRetirementGlPoster.getPendingEntries().clear();
            }
        }

        return valid;
    }


    /**
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.core.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.core.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean success = true;
        AssetRetirementGlobalDetail assetRetirementGlobalDetail = (AssetRetirementGlobalDetail) line;
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getDocumentBusinessObject();

        if (!checkEmptyValue(assetRetirementGlobalDetail.getCapitalAssetNumber())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_BLANK_CAPITAL_ASSET_NUMBER);
            success = false;
        }
        else {
            success &= checkRetirementDetailOneLine(assetRetirementGlobalDetail, assetRetirementGlobal);
            success &= checkRetireMultipleAssets(assetRetirementGlobal.getRetirementReasonCode(), assetRetirementGlobal.getAssetRetirementGlobalDetails(), new Integer(0));
        }

        // Calculate summary fields in order to show the values even though add new line fails.
        for (AssetRetirementGlobalDetail detail : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            assetService.setAssetSummaryFields(detail.getAsset());
        }
        return success & super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
    }

    /**
     * 
     * Check if only single asset is allowed to retire.
     * @param retirementReasonCode
     * @param assetRetirementDetails
     * @param maxNumber
     * @return
     */
    private boolean checkRetireMultipleAssets(String retirementReasonCode, List<AssetRetirementGlobalDetail> assetRetirementDetails, Integer maxNumber) {
        boolean success = true;

        if (assetRetirementDetails.size() > maxNumber && !assetRetirementService.isAllowedRetireMultipleAssets(retirementReasonCode) ) {
            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.VERSION_NUMBER, CamsKeyConstants.Retirement.ERROR_MULTIPLE_ASSET_RETIRED);
            success = false;
        }

        return success;
    }

    /**
     * 
     * This method validates each asset to be retired.
     * 
     * @param assetRetirementGlobalDetails
     * @return
     */
    private boolean validateRetirementDetails(AssetRetirementGlobal assetRetirementGlobal) {
        boolean success = true;

        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        if (assetRetirementGlobalDetails.size() == 0) {
            success = false;
            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.VERSION_NUMBER, CamsKeyConstants.Retirement.ERROR_ASSET_RETIREMENT_GLOBAL_NO_ASSET);
        }
        else {
            // validate each asset retirement detail
            int index = 0;
            for (AssetRetirementGlobalDetail detail : assetRetirementGlobalDetails) {
                String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "[" + index++ + "]";
                GlobalVariables.getErrorMap().addToErrorPath(errorPath);

                success &= checkRetirementDetailOneLine(detail, assetRetirementGlobal);

                GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
            }
        }
        return success;
    }


    /**
     * 
     * This method validates one asset is a valid asset and no duplicate with target asset when merge.
     * 
     * @param detail
     * @return
     */
    private boolean checkRetirementDetailOneLine(AssetRetirementGlobalDetail assetRetirementGlobalDetail, AssetRetirementGlobal assetRetirementGlobal) {
        boolean success = true;

        assetRetirementGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET);

        Asset asset = assetRetirementGlobalDetail.getAsset();

        if (ObjectUtils.isNull(asset)) {
            success = false;
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_CAPITAL_ASSET_NUMBER, assetRetirementGlobalDetail.getCapitalAssetNumber().toString());
        }
        else {
            success &= validateActiveCapitalAsset(asset);
            success &= validateNonMoveableAsset(asset);

            if (assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
                success &= validateDuplicateAssetNumber(assetRetirementGlobal.getMergedTargetCapitalAssetNumber(), assetRetirementGlobalDetail.getCapitalAssetNumber());
            }

            // Set asset non persistent fields
            assetService.setAssetSummaryFields(asset);
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
    private boolean validateDuplicateAssetNumber(Long targetAssetNumber, Long sourceAssetNumber) {
        boolean success = true;

        if (assetService.isCapitalAssetNumberDuplicate(targetAssetNumber, sourceAssetNumber)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_DUPLICATE_CAPITAL_ASSET_NUMBER_WITH_TARGET, sourceAssetNumber.toString());
            success = false;
        }

        return success;
    }

    /**
     * 
     * User must be in work group CM_SUPER_USERS to retire a non-moveable asset.
     * 
     * @return
     */
    private boolean validateNonMoveableAsset(Asset asset) {
        boolean success = true;

        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
        if (!assetService.isAssetMovable(asset) && !user.isMember(CamsConstants.Workgroups.WORKGROUP_CM_SUPER_USERS)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_USER_GROUP_FOR_NON_MOVEABLE_ASSET, asset.getCapitalAssetNumber().toString());
            success = false;
        }

        return success;
    }


    /**
     * 
     * Validate Asset Retirement Global and Details.
     * 
     * @param assetRetirementGlobal
     * @return
     */
    protected boolean assetRetirementValidation(AssetRetirementGlobal assetRetirementGlobal) {
        boolean valid = true;

        valid &= validateRequiredSharedInfoFields(assetRetirementGlobal);
        if (assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
            valid &= validateMergeTargetAsset(assetRetirementGlobal);
        }
        valid &= validateRetirementDetails(assetRetirementGlobal);
        valid &= checkRetireMultipleAssets(assetRetirementGlobal.getRetirementReasonCode(), assetRetirementGlobal.getAssetRetirementGlobalDetails(), new Integer(1));

        return valid;
    }

    /**
     * 
     * Validate mergedTargetCapitalAsset. Only valid and active capital asset is allowed.
     * 
     * @param assetRetirementGlobal
     * @return
     */
    private boolean validateMergeTargetAsset(AssetRetirementGlobal assetRetirementGlobal) {
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
            if (!assetService.isCapitalAsset(targetAsset)) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_CAPITAL_ASSET_RETIREMENT, targetAssetNumber.toString());
                valid = false;
            }
            if (assetService.isAssetRetired(targetAsset)) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_ACTIVE_ASSET_RETIREMENT, targetAssetNumber.toString());
                valid = false;
            }
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
    private boolean validateActiveCapitalAsset(Asset asset) {
        boolean valid = true;

        if (!assetService.isCapitalAsset(asset)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_CAPITAL_ASSET_RETIREMENT, asset.getCapitalAssetNumber().toString());
            valid = false;
        }
        else if (assetService.isAssetRetired(asset)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_ACTIVE_ASSET_RETIREMENT, asset.getCapitalAssetNumber().toString());
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
    private boolean validateRequiredSharedInfoFields(AssetRetirementGlobal assetRetirementGlobal) {
        boolean valid = true;

        AssetRetirementGlobalDetail sharedRetirementInfo = assetRetirementGlobal.getSharedRetirementInfo();

        if (assetRetirementService.isAssetRetiredBySoldOrAuction(assetRetirementGlobal)) {
            if (StringUtils.isBlank(sharedRetirementInfo.getBuyerDescription())) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.BUYER_DESCRIPTION, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.BUYER_DESCRIPTION, assetRetirementService.getAssetRetirementReasonName(assetRetirementGlobal) });
                valid = false;
            }
            if (sharedRetirementInfo.getSalePrice() == null) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.SALE_PRICE, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.SALE_PRICE, assetRetirementService.getAssetRetirementReasonName(assetRetirementGlobal) });
                valid = false;
            }
        }
        else if (assetRetirementService.isAssetRetiredByExternalTransferOrGift(assetRetirementGlobal) && StringUtils.isBlank(sharedRetirementInfo.getRetirementInstitutionName())) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.RETIREMENT_INSTITUTION_NAME, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.RETIREMENT_INSTITUTION_NAME, assetRetirementService.getAssetRetirementReasonName(assetRetirementGlobal) });
            valid = false;
        }
        else if (assetRetirementService.isAssetRetiredByTheft(assetRetirementGlobal) && StringUtils.isBlank(sharedRetirementInfo.getPaidCaseNumber())) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.PAID_CASE_NUMBER, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.PAID_CASE_NUMBER, assetRetirementService.getAssetRetirementReasonName(assetRetirementGlobal) });
            valid = false;
        }
        return valid;
    }
}
