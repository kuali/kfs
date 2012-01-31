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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsConstants.DocumentTypeName;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetLocationGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetLocationGlobalDetail;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business rules applicable to AssetLocationGlobal documents.
 */
public class AssetLocationGlobalRule extends MaintenanceDocumentRuleBase {

    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLocationGlobalRule.class);
    protected AssetService assetService = SpringContext.getBean(AssetService.class);

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = super.processCustomSaveDocumentBusinessRules(document);
        AssetLocationGlobal assetLocationGlobal = (AssetLocationGlobal) document.getNewMaintainableObject().getBusinessObject();
        valid &= !getCapitalAssetManagementModuleService().isAssetLocked(retrieveAssetNumberForLocking(assetLocationGlobal), DocumentTypeName.ASSET_LOCATION_GLOBAL, document.getDocumentNumber());
        return valid;
    }

    /**
     * Processes rules when routing this global.
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument documentCopy) {
        boolean success = true;

        AssetLocationGlobal assetLocationGlobal = (AssetLocationGlobal) documentCopy.getNewMaintainableObject().getBusinessObject();
        List<AssetLocationGlobalDetail> oldAssetLocationGlobalDetail = assetLocationGlobal.getAssetLocationGlobalDetails();

        // validate multi add w/red highlight (collection)
        int index = 0;

        if (hasAssetLocationGlobalDetails(oldAssetLocationGlobalDetail)) {
            Set<String> tags = new HashSet<String>();
            int pos = 0;
            for (AssetLocationGlobalDetail detail : assetLocationGlobal.getAssetLocationGlobalDetails()) {
                String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetLocationGlobal.ASSET_LOCATION_GLOBAL_DETAILS + "[" + index + "]";
                GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                success &= validateActiveCapitalAsset(detail);
                success &= validateCampusCode(detail);
                success &= validateBuildingCode(detail);
                success &= validateBuildingRoomNumber(detail);
                success &= validateTagNumber(detail);
                success &= validateTagDuplicationWithinDocument(detail, tags);
                success &= validateTagDuplication(detail.getCapitalAssetNumber(), detail.getCampusTagNumber());
                success &= checkRequiredFieldsAfterAdd(detail);
                GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
                index++;
            }
        }
        success &= super.processCustomRouteDocumentBusinessRules(documentCopy);

        WorkflowDocument workflowDoc = documentCopy.getDocumentHeader().getWorkflowDocument();
        // adding asset locks
        if (!GlobalVariables.getMessageMap().hasErrors() && (workflowDoc.isInitiated() || workflowDoc.isSaved())) {
            success &= setAssetLocks(documentCopy);
        }

        return success;
    }

    /**
     * retrieve asset numbers need to be locked.
     *
     * @param assetLocationGlobal
     * @return
     */
    protected List<Long> retrieveAssetNumberForLocking(AssetLocationGlobal assetLocationGlobal) {
        List<Long> capitalAssetNumbers = new ArrayList<Long>();
        for (AssetLocationGlobalDetail locationDetail : assetLocationGlobal.getAssetLocationGlobalDetails()) {
            if (locationDetail.getCapitalAssetNumber() != null) {
                capitalAssetNumbers.add(locationDetail.getCapitalAssetNumber());
            }
        }
        return capitalAssetNumbers;
    }

    /**
     * Locking for asset numbers.
     *
     * @param documentCopy
     * @param assetLocationGlobal
     * @return
     */
    private boolean setAssetLocks(MaintenanceDocument document) {
        AssetLocationGlobal assetLocationGlobal = (AssetLocationGlobal) document.getNewMaintainableObject().getBusinessObject();

        return this.getCapitalAssetManagementModuleService().storeAssetLocks(retrieveAssetNumberForLocking(assetLocationGlobal), document.getDocumentNumber(), DocumentTypeName.ASSET_LOCATION_GLOBAL, null);
    }

    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }

    /**
     * Process rules for any new {@link AssetLocationGlobalDetail} that is added to this global.
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument documentCopy, String collectionName, PersistableBusinessObject bo) {
        boolean success = true;
        AssetLocationGlobal assetLocationGlobal = (AssetLocationGlobal) documentCopy.getNewMaintainableObject().getBusinessObject();
        Set<String> tags = new HashSet<String>();
        for (AssetLocationGlobalDetail detail : assetLocationGlobal.getAssetLocationGlobalDetails()) {
            if (detail.getCampusTagNumber() != null) {
                tags.add(detail.getCampusTagNumber());
            }
        }

        AssetLocationGlobalDetail newLineDetail = (AssetLocationGlobalDetail) bo;
        success = validateActiveCapitalAsset(newLineDetail);
        if (success) {
            success &= authorizeCapitalAsset(newLineDetail);
            success &= validateCampusCode(newLineDetail);
            success &= validateBuildingCode(newLineDetail);
            success &= validateBuildingRoomNumber(newLineDetail);
            success &= validateTagDuplicationWithinDocument(newLineDetail, tags);
            if (success) {
                success &= validateTagDuplication(newLineDetail.getCapitalAssetNumber(), newLineDetail.getCampusTagNumber());
            }
        }
        return success & super.processCustomAddCollectionLineBusinessRules(documentCopy, collectionName, bo);
    }

    /**
     * Asset user authorization.
     *
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean authorizeCapitalAsset(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        boolean success = true;

        if (ObjectUtils.isNotNull(assetLocationGlobalDetail.getCapitalAssetNumber())) {
            assetLocationGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetLocationGlobalDetail.ASSET);
            assetLocationGlobalDetail.getAsset().refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);

            Map<String,String> qualification = new HashMap<String,String>();
            qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, assetLocationGlobalDetail.getAsset().getOrganizationOwnerChartOfAccountsCode());
            qualification.put(KfsKimAttributes.ORGANIZATION_CODE, assetLocationGlobalDetail.getAsset().getOrganizationOwnerAccount().getOrganizationCode());
            if (!SpringContext.getBean(IdentityManagementService.class).isAuthorized(GlobalVariables.getUserSession().getPerson().getPrincipalId(), CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.MAINTAIN_ASSET_LOCATION, qualification)) {
                success &= false;
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_ASSET_AUTHORIZATION, new String[] { GlobalVariables.getUserSession().getPerson().getPrincipalName(), assetLocationGlobalDetail.getCapitalAssetNumber().toString() });
            }
        }

        return success;
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
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_ASSET_LOCATION_GLOBAL_NO_ASSET_DETAIL);
        }

        return success;
    }

    /**
     * Validate the capital {@link Asset}. This method also calls {@link AssetService} while validating retired {@link Asset}.
     *
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateActiveCapitalAsset(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        boolean success = true;

        if (ObjectUtils.isNotNull(assetLocationGlobalDetail.getCapitalAssetNumber())) {
            assetLocationGlobalDetail.refreshReferenceObject(CamsPropertyConstants.AssetLocationGlobalDetail.ASSET);

            if (ObjectUtils.isNull(assetLocationGlobalDetail.getAsset())) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_CAPITAL_ASSET_NUMBER, new String[] { assetLocationGlobalDetail.getCapitalAssetNumber().toString() });
                success = false;
            }
            else if (assetService.isAssetRetired(assetLocationGlobalDetail.getAsset())) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAPITAL_ASSET_NUMBER, CamsKeyConstants.Retirement.ERROR_NON_ACTIVE_ASSET_RETIREMENT, new String[] { assetLocationGlobalDetail.getCapitalAssetNumber().toString() });
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
            // assetLocationGlobalDetail.refreshReferenceObject("campus");

            if (ObjectUtils.isNull(assetLocationGlobalDetail.getCampus())) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_CODE, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_CAMPUS_CODE, new String[] { assetLocationGlobalDetail.getCampusCode(), assetLocationGlobalDetail.getCapitalAssetNumber().toString() });
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
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.BUILDING_CODE, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_BUILDING_CODE, new String[] { assetLocationGlobalDetail.getBuildingCode(), assetLocationGlobalDetail.getCampusCode() });
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
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.BUILDING_ROOM_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_ROOM_NUMBER, new String[] { assetLocationGlobalDetail.getBuildingCode(), assetLocationGlobalDetail.getBuildingRoomNumber(), assetLocationGlobalDetail.getCampusCode() });
                success = false;
            }
        }

        return success;
    }

    /**
     * Validate tag number.
     *
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateTagNumber(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        boolean success = true;

        if (StringUtils.isBlank(assetLocationGlobalDetail.getCampusTagNumber())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_TAG_NUMBER_REQUIRED);
            success = false;
        }

        return success;
    }

    /**
     * Validate duplicate tag number. This method also calls {@link AssetService}.
     *
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateTagDuplication(Long capitalAssetNumber, String campusTagNumber) {
        boolean success = true;
        if (capitalAssetNumber != null && ObjectUtils.isNotNull(campusTagNumber) && !CamsConstants.Asset.NON_TAGGABLE_ASSET.equalsIgnoreCase(campusTagNumber)) {
            // call AssetService, get Assets from doc, gather all assets matching this tag number
            List<Asset> activeAssetsMatchingTagNumber = assetService.findActiveAssetsMatchingTagNumber(campusTagNumber);
            for (Asset asset : activeAssetsMatchingTagNumber) {
                // compare asset numbers
                if (!asset.getCapitalAssetNumber().equals(capitalAssetNumber)) {
                    GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_DUPLICATE_TAG_NUMBER_FOUND, new String[] { campusTagNumber, String.valueOf(asset.getCapitalAssetNumber()), capitalAssetNumber.toString() });
                    success = false;
                    break;
                }
            }
        }

        return success;
    }

    protected boolean validateTagDuplicationWithinDocument(AssetLocationGlobalDetail assetLocationGlobalDetail, Set<String> tagsList) {
        boolean success = true;
        Long capitalAssetNumber = assetLocationGlobalDetail.getCapitalAssetNumber();
        String campusTagNumber = assetLocationGlobalDetail.getCampusTagNumber();
        if (capitalAssetNumber != null && ObjectUtils.isNotNull(campusTagNumber) && !CamsConstants.Asset.NON_TAGGABLE_ASSET.equalsIgnoreCase(campusTagNumber)) {
            // if duplicate within list
            if (!tagsList.add(campusTagNumber)) {
                success &= false;
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_TAG_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_DUPLICATE_TAG_NUMBER_WITHIN_DOCUMENT, new String[] { campusTagNumber, capitalAssetNumber.toString() });
            }
        }
        return success;
    }

    /**
     * Check required fields after a new asset location has been added.
     *
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean checkRequiredFieldsAfterAdd(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        boolean success = true;

        if (StringUtils.isBlank(assetLocationGlobalDetail.getCampusCode())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_CODE, CamsKeyConstants.AssetLocationGlobal.ERROR_CAMPUS_CODE_REQUIRED);
            success = false;
        }

        if (StringUtils.isBlank(assetLocationGlobalDetail.getBuildingCode())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.BUILDING_CODE, CamsKeyConstants.AssetLocationGlobal.ERROR_BUILDING_CODE_REQUIRED);
            success = false;
        }

        if (StringUtils.isBlank(assetLocationGlobalDetail.getBuildingRoomNumber())) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetLocationGlobal.BUILDING_ROOM_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_ROOM_NUMBER_REQUIRED);
            success = false;
        }

        return success;
    }
}
