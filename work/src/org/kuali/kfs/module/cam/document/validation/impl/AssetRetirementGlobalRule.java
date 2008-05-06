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
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.service.AssetRetirementService;
import org.kuali.module.cams.service.AssetService;

/**
 * Business rules applicable to AssetLocationGlobal documents.
 */
public class AssetRetirementGlobalRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementGlobalRule.class);

    private static AssetService assetService = SpringContext.getBean(AssetService.class);
    private static AssetRetirementService assetRetirementService = SpringContext.getBean(AssetRetirementService.class);
    private static Integer oneAsset = new Integer(1);

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
        return valid & super.processCustomSaveDocumentBusinessRules(document);
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
        else  {
            success &= checkRetireMultipleAssets(assetRetirementGlobal.getRetirementReasonCode(), assetRetirementGlobal.getAssetRetirementGlobalDetails(), new Integer(oneAsset.intValue() - 1));
            success &= checkRetirementDetailOneLine(assetRetirementGlobalDetail, assetRetirementGlobal);
        }

        return success & super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
    }

    /**
     * 
     * Check if the retirement reason code allows to retire multiple assets
     * @param retirementReasonCode
     * @param assetDetails
     * @return
     */
    private boolean checkRetireMultipleAssets(String retirementReasonCode, List<AssetRetirementGlobalDetail> assetDetails, Integer maxNumber) {
        boolean success = true;
        if (assetRetirementService.isRetirementReasonCodeInGroup(CamsConstants.AssetRetirementReasonCodeGroup.SINGLE_RETIRED_ASSET, retirementReasonCode) && assetDetails.size() > maxNumber) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_MULTIPLE_ASSET_RETIRED);
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
            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_ASSET_RETIREMENT_GLOBAL_NO_ASSET);
        }
        else {
            // validate each asset retirement detail
            int index = 0;
            for (AssetRetirementGlobalDetail detail : assetRetirementGlobalDetails) {
                String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "[" + index++ + "]";
                GlobalVariables.getErrorMap().addToErrorPath(errorPath);

                success &= checkRetirementDetailOneLine(detail, assetRetirementGlobal);

                GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);

                success &= validatePendingDocument(detail);
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

        assetRetirementGlobalDetail.refreshReferenceObject("asset");

        if (ObjectUtils.isNull(assetRetirementGlobalDetail.getAsset())) {
            success = false;
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_CAPITAL_ASSET_NUMBER, assetRetirementGlobalDetail.getCapitalAssetNumber().toString());
        }
        else {
            success &= validateActiveCapitalAsset(assetRetirementGlobalDetail.getAsset());
            success &= validateNonMoveableAsset(assetRetirementGlobalDetail.getAsset());

            // Check for Merge Asset, no duplicate capitalAssetNumber between "from" and "to".
            if (assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal) && assetRetirementGlobal.getMergedTargetCapitalAssetNumber() != null && assetService.isCapitalAssetNumberDuplicate(assetRetirementGlobalDetail.getCapitalAssetNumber(), assetRetirementGlobal.getMergedTargetCapitalAssetNumber())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_DUPLICATE_CAPITAL_ASSET_NUMBER_WITH_TARGET, assetRetirementGlobalDetail.getCapitalAssetNumber().toString());
                success = false;
            }
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
     * This method...
     * 
     * @param assetRetirementGlobal
     * @return
     */
    private boolean assetRetirementValidation(AssetRetirementGlobal assetRetirementGlobal) {
        boolean valid = true;

        String errorPath = MAINTAINABLE_ERROR_PREFIX + KFSConstants.MAINTENANCE_ADD_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS;
        GlobalVariables.getErrorMap().addToErrorPath(errorPath);
        valid &= checkRetireMultipleAssets(assetRetirementGlobal.getRetirementReasonCode(), assetRetirementGlobal.getAssetRetirementGlobalDetails(), oneAsset);
        GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
        
        if (assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
            valid &= validateMergeTargetAsset(assetRetirementGlobal);
        }
        valid &= validateRequiredSharedInfoFields(assetRetirementGlobal);

        valid &= validateRetirementDetails(assetRetirementGlobal);

        return valid;
    }

    /**
     * 
     * Validate mergedTargetCapitalAsset is valid and active capital asset.
     * 
     * @param assetRetirementGlobal
     * @return
     */
    private boolean validateMergeTargetAsset(AssetRetirementGlobal assetRetirementGlobal) {
        boolean valid = true;
        if (ObjectUtils.isNull(assetRetirementGlobal.getMergedTargetCapitalAsset())) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_INVALID_MERGED_TARGET_ASSET_NUMBER, assetRetirementGlobal.getMergedTargetCapitalAssetNumber() == null ? "" : assetRetirementGlobal.getMergedTargetCapitalAssetNumber().toString());
            valid = false;
        }
        else {
            valid &= validateActiveCapitalAsset(assetRetirementGlobal.getMergedTargetCapitalAsset());
        }
        return valid;
    }

    private boolean validatePendingDocument(AssetRetirementGlobalDetail assetRetirementGlobalDetail) {
        // TODO check for pending entry, may be resolved by asset maint lock.
        return true;
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
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "," + CamsPropertyConstants.AssetRetirementGlobalDetail.BUYER_DESCRIPTION, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.BUYER_DESCRIPTION, assetRetirementService.getAssetRetirementReasonName(assetRetirementGlobal) });
                valid = false;
            }
            if (sharedRetirementInfo.getSalePrice() == null) {
                putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "," + CamsPropertyConstants.AssetRetirementGlobalDetail.SALE_PRICE, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.SALE_PRICE, assetRetirementService.getAssetRetirementReasonName(assetRetirementGlobal) });
                valid = false;
            }
        }
        else if (assetRetirementService.isAssetRetiredByExternalTransferOrGift(assetRetirementGlobal) && StringUtils.isBlank(sharedRetirementInfo.getRetirementInstitutionName())) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "," + CamsPropertyConstants.AssetRetirementGlobalDetail.RETIREMENT_INSTITUTION_NAME, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.RETIREMENT_INSTITUTION_NAME, assetRetirementService.getAssetRetirementReasonName(assetRetirementGlobal) });
            valid = false;
        }
        else if (assetRetirementService.isAssetRetiredByTheft(assetRetirementGlobal) && StringUtils.isBlank(sharedRetirementInfo.getPaidCaseNumber())) {
            putFieldError(CamsPropertyConstants.AssetRetirementGlobal.SHARED_RETIREMENT_INFO + "," + CamsPropertyConstants.AssetRetirementGlobalDetail.PAID_CASE_NUMBER, CamsKeyConstants.Retirement.ERROR_RETIREMENT_DETAIL_INFO_NULL, new String[] { CamsConstants.RetirementLabel.PAID_CASE_NUMBER, assetRetirementService.getAssetRetirementReasonName(assetRetirementGlobal) });
            valid = false;
        }
        return valid;
    }
}
