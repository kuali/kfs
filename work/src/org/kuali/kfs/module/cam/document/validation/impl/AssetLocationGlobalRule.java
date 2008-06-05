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
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetLocationGlobal;
import org.kuali.module.cams.bo.AssetLocationGlobalDetail;
import org.kuali.module.cams.service.AssetService;

/**
 * Business rules applicable to AssetLocationGlobal documents.
 */
public class AssetLocationGlobalRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLocationGlobalRule.class);
    private AssetService assetService = SpringContext.getBean(AssetService.class);

    /**
     * Constructs an AssetLocationGlobalRule
     */
    public AssetLocationGlobalRule() {
        // LOG.info("AssetLocationGlobalRule constructor");
    }

    /**
     * Processes rules when routing this global.
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument documentCopy) {
        boolean success = true;

        AssetLocationGlobal assetLocationGlobal = (AssetLocationGlobal) documentCopy.getNewMaintainableObject().getBusinessObject();
        List<AssetLocationGlobalDetail> assetLocationGlobalDetails = assetLocationGlobal.getAssetLocationGlobalDetails();

        // validate multi add w/red highlight (collection)
        int index = 0;
        String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetLocationGlobal.ASSET_LOCATION_GLOBAL_DETAILS + "[" + index + "]";

        if (hasAssetLocationGlobalDetails(assetLocationGlobalDetails)) {

            for (AssetLocationGlobalDetail detail : assetLocationGlobal.getAssetLocationGlobalDetails()) {
                GlobalVariables.getErrorMap().addToErrorPath(errorPath);
                success &= validateActiveCapitalAsset(detail);
                success &= validateCampusCode(detail);
                success &= validateBuildingCode(detail);
                success &= validateBuildingRoomNumber(detail);
                success &= validateTagNumber(detail);
                GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
                index++;
            }
        }

        return success & super.processCustomRouteDocumentBusinessRules(documentCopy);
    }

    /**
     * Process rules for any new {@link AssetLocationGlobalDetail} that is added to this global.
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.core.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.core.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument documentCopy, String collectionName, PersistableBusinessObject bo) {
        boolean success = true;

        AssetLocationGlobalDetail assetLocationGlobalDetail = (AssetLocationGlobalDetail) bo;

        success &= validateActiveCapitalAsset(assetLocationGlobalDetail);
        success &= validateCampusCode(assetLocationGlobalDetail);
        success &= validateBuildingCode(assetLocationGlobalDetail);
        success &= validateBuildingRoomNumber(assetLocationGlobalDetail);
        success &= validateTagNumber(assetLocationGlobalDetail);

        return success & super.processCustomAddCollectionLineBusinessRules(documentCopy, collectionName, bo);
    }

    /**
     * Validate if any {@link AssetLocationGlobalDetail} exist.
     * 
     * @param assetLocationGlobal
     * @return boolean
     */
    protected boolean hasAssetLocationGlobalDetails(List<AssetLocationGlobalDetail> assetLocationGlobalDetails) {
        boolean success = true;

        if (assetLocationGlobalDetails.size() == 0) {
            success = false;
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_ASSET_LOCATION_GLOBAL_NO_ASSET_DETAIL);
        }

        return success;
    }

    /**
     * Validate the capital {@link Asset}.  This method also calls {@link AssetService} while validating retired {@link Asset}.
     * 
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateActiveCapitalAsset(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        boolean success = true;

        if (ObjectUtils.isNotNull(assetLocationGlobalDetail.getCapitalAssetNumber())) {
            assetLocationGlobalDetail.refreshReferenceObject("asset");

            if (ObjectUtils.isNull(assetLocationGlobalDetail.getAsset())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_CAPITAL_ASSET_NUMBER, new String[] { assetLocationGlobalDetail.getCapitalAssetNumber().toString() });
                success = false;
            }
            else if (assetService.isAssetRetired(assetLocationGlobalDetail.getAsset())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_ACTIVE_ASSET_RETIREMENT, new String[] { assetLocationGlobalDetail.getCapitalAssetNumber().toString() });
                success = false;
            }
        }

        return success;
    }

    /**
     * Validate {@link Campus} code.
     * 
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateCampusCode(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        boolean success = true;

        if (StringUtils.isNotBlank(assetLocationGlobalDetail.getCampusCode())) {
            assetLocationGlobalDetail.refreshReferenceObject("campus");

            if (ObjectUtils.isNull(assetLocationGlobalDetail.getCampus())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_CODE, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_CAMPUS_CODE, new String[] { assetLocationGlobalDetail.getCampusCode(), assetLocationGlobalDetail.getCapitalAssetNumber().toString() });
                success = false;
            }
        }
        return success;
    }

    /**
     * Validate {@link Building} code.
     * 
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateBuildingCode(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        boolean success = true;

        if (StringUtils.isNotBlank(assetLocationGlobalDetail.getCampusCode()) && StringUtils.isNotBlank(assetLocationGlobalDetail.getBuildingCode())) {
            assetLocationGlobalDetail.refreshReferenceObject("building");

            if (ObjectUtils.isNull(assetLocationGlobalDetail.getBuilding())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.BUILDING_CODE, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_BUILDING_CODE, new String[] { assetLocationGlobalDetail.getBuildingCode(), assetLocationGlobalDetail.getCampusCode() });
                success = false;
            }
        }
        return success;
    }

    /**
     * Validate building {@link Room} number.
     * 
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateBuildingRoomNumber(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        boolean success = true;

        if (StringUtils.isNotBlank(assetLocationGlobalDetail.getBuildingRoomNumber())) {
            assetLocationGlobalDetail.refreshReferenceObject("buildingRoom");

            if (ObjectUtils.isNull(assetLocationGlobalDetail.getBuildingRoom())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.BUILDING_ROOM_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_ROOM_NUMBER, new String[] { assetLocationGlobalDetail.getBuildingCode(), assetLocationGlobalDetail.getBuildingRoomNumber(), assetLocationGlobalDetail.getCampusCode() });
                success = false;
            }
        }

        return success;
    }

    /**
     * Validate duplicate tag number.  This method also calls {@link AssetService}.
     * 
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateTagNumber(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        boolean success = true;

        if (ObjectUtils.isNotNull(assetLocationGlobalDetail.getCampusTagNumber())) {
            // call AssetService, get Assets from doc, gather all assets matching this tag number
            List<Asset> activeAssetsMatchingTagNumber = assetService.findActiveAssetsMatchingTagNumber(assetLocationGlobalDetail.getCampusTagNumber());

            for (Asset asset : activeAssetsMatchingTagNumber) {
                // compair asset numbers 
                if (!asset.getCapitalAssetNumber().equals(assetLocationGlobalDetail.getCapitalAssetNumber())) {
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_DUPLICATE_TAG_NUMBER_FOUND, new String[] { assetLocationGlobalDetail.getCampusTagNumber(), assetLocationGlobalDetail.getCapitalAssetNumber().toString() });
                    success = false;
                    break;
                }
            }
        }

        return success;
    }
}