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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Campus;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.bo.Room;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetLocationGlobal;
import org.kuali.module.cams.bo.AssetLocationGlobalDetail;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.service.AssetService;

/**
 * Business rules applicable to AssetLocationGlobal documents.
 */
public class AssetLocationGlobalRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLocationGlobalRule.class);
    
    private static AssetService assetService = SpringContext.getBean(AssetService.class);
    
    /**
     * Constructs an AssetLocationGlobalRule
     */
    public AssetLocationGlobalRule() {
        //LOG.info("AssetLocationGlobalRule constructor");
    }
    
    /**
     * This method sets the convenience objects, so you have short and easy handles to the new and old objects contained in
     * the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects
     * from the DB by their primary keys, if available.
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     * 
     * @param document - the maintenanceDocument being evaluated
     * @return none
     */
    @Override
    public void setupConvenienceObjects() {
        AssetLocationGlobal newAssetLocationGlobal = (AssetLocationGlobal) super.getNewBo();
        newAssetLocationGlobal.refreshNonUpdateableReferences();
        for (AssetLocationGlobalDetail detail : newAssetLocationGlobal.getAssetLocationGlobalDetails()) {
            detail.refreshNonUpdateableReferences();
        }
    }
    
    /**
     * Processes rules when saving this global.
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument documentCopy) {
        LOG.info("Begin AssetLocationGlobalRule.processCustomSaveDocumentBusinessRules");
        boolean success = true;
        
        AssetLocationGlobal assetLocationGlobal = (AssetLocationGlobal) documentCopy.getNewMaintainableObject().getBusinessObject();
        setupConvenienceObjects();
        
        // process rules
        success &= hasAssetLocationGlobalDetails(assetLocationGlobal);
        
        //return success & super.processCustomSaveDocumentBusinessRules(documentCopy);
        return success;
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
        
        // process rules
        success &= hasAssetLocationGlobalDetails(assetLocationGlobal);
        
        //return success & super.processCustomSaveDocumentBusinessRules(documentCopy);
        return success;
    }

    /**
     * Process rules whenever a new {@link AssetLocationGlobalDetail} is added to this global.
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.core.document.MaintenanceDocument, java.lang.String, org.kuali.core.bo.PersistableBusinessObject)
     */
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument documentCopy, String collectionName, PersistableBusinessObject bo ) {
        boolean success = true;
        
        if (bo instanceof AssetLocationGlobalDetail) {
            AssetLocationGlobalDetail assetLocationGlobalDetail = (AssetLocationGlobalDetail) bo;

            // process rules
            //success &= validateAssetLocationGlobalDetails(assetLocationGlobalDetail);
            success &= validateActiveCapitalAsset(assetLocationGlobalDetail);
            success &= validateCampusCode(assetLocationGlobalDetail);
            success &= validateBuildingCode(assetLocationGlobalDetail);
            success &= validateBuildingRoomNumber(assetLocationGlobalDetail);
            success &= hasDuplicateTagNumber(assetLocationGlobalDetail);
        }
        
        return success & super.processCustomAddCollectionLineBusinessRules(documentCopy, collectionName, bo);
    }
    
    /**
     * Validate if any {@link AssetLocationGlobalDetail} exist.
     * 
     * @param assetLocationGlobal
     * @return boolean
     */
    private boolean hasAssetLocationGlobalDetails(AssetLocationGlobal assetLocationGlobal) {
        boolean success = true;

        List<AssetLocationGlobalDetail> assetLocationGlobalDetails = assetLocationGlobal.getAssetLocationGlobalDetails();
        if (assetLocationGlobalDetails.size() == 0) {
            success = false;
            putFieldError(CamsPropertyConstants.AssetLocationGlobal.CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_ASSET_LOCATION_GLOBAL_NO_ASSET);
        }
 
        return success;
    }    
    
    
    /**
     * Validate {@link AssetLocationGlobalDetail} details.
     * 
     * @param assetLocationGlobalDetail
     * @return true if the AssetLocationGlobalDetail object contains a valid Asset
     */
    protected boolean validateAssetLocationGlobalDetails(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        
        boolean success = true;
        
        if (ObjectUtils.isNull(assetLocationGlobalDetail.getCapitalAssetNumber())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAPITAL_ASSET_NUMBER, KFSKeyConstants.ERROR_REQUIRED, "Capital Asset Number");
            success &= false;
        }

        /* possibly needed later?
        if (ObjectUtils.isNull(assetLocationGlobalDetail.getCampusCode())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_CODE, KFSKeyConstants.ERROR_REQUIRED, "Campus Code");
            success &= false;
        }
        
        if (ObjectUtils.isNull(assetLocationGlobalDetail.getBuildingCode())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.BUILDING_CODE, KFSKeyConstants.ERROR_REQUIRED, "Building Code");
            success &= false;
        }
        
        if (ObjectUtils.isNull(assetLocationGlobalDetail.getBuildingRoomNumber())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.BUILDING_ROOM_NUMBER, KFSKeyConstants.ERROR_REQUIRED, "Building Room Number");
            success &= false;
        }
        
        if (ObjectUtils.isNull(assetLocationGlobalDetail.getBuildingSubRoomNumber())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.BUILDING_SUB_ROOM_NUMBER, KFSKeyConstants.ERROR_REQUIRED, "Building Sub Room Number");
            success &= false;
        }
        
        if (ObjectUtils.isNull(assetLocationGlobalDetail.getCampusTagNumber())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_TAG_NUMBER, KFSKeyConstants.ERROR_REQUIRED, "Campus Tag Number");
            success &= false;
        }
        */
        
        return success;
    }
    
    /**
     * Validate the capital {@link Asset} number.
     * 
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateActiveCapitalAsset(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        
        boolean success = true;
        
        if (ObjectUtils.isNotNull(assetLocationGlobalDetail.getCapitalAssetNumber())) {
            HashMap map = new HashMap();
            map.put(CamsPropertyConstants.AssetLocationGlobal.CAPITAL_ASSET_NUMBER, assetLocationGlobalDetail.getCapitalAssetNumber());  
            
            Asset asset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, map);
            if (ObjectUtils.isNull(asset)) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAPITAL_ASSET_NUMBER, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_CAPITAL_ASSET_NUMBER, new String[] { assetLocationGlobalDetail.getCapitalAssetNumber().toString() });
                success = false;
            }
            else if (assetService.isAssetRetired(asset)) {
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
            HashMap map = new HashMap();
            map.put(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_CODE, assetLocationGlobalDetail.getCampusCode());  
            
            Campus campus = (Campus) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Campus.class, map);
            if (ObjectUtils.isNull(campus)) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_CAMPUS_CODE, new String[] { assetLocationGlobalDetail.getCampusCode() });
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
            HashMap map = new HashMap();
            map.put(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_CODE, assetLocationGlobalDetail.getCampusCode());
            map.put(CamsPropertyConstants.AssetLocationGlobal.BUILDING_CODE, assetLocationGlobalDetail.getBuildingCode());
        
            Building building = (Building) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Building.class, map);
            if (ObjectUtils.isNull(building)) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_BUILDING_CODE, new String[] { assetLocationGlobalDetail.getCampusCode(), assetLocationGlobalDetail.getBuildingCode() });
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
            HashMap map = new HashMap();
            map.put(CamsPropertyConstants.AssetLocationGlobal.BUILDING_ROOM_NUMBER, assetLocationGlobalDetail.getBuildingRoomNumber());
            
            Room room = (Room) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Room.class, map);
            if (ObjectUtils.isNull(room)) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.BUILDING_ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_INVALID_ROOM_NUMBER, new String[] { assetLocationGlobalDetail.getCampusCode(), assetLocationGlobalDetail.getBuildingCode(), assetLocationGlobalDetail.getBuildingRoomNumber() });
                success = false;
            }
        }
        
        return success;
    }
    
    /**
     * Validate duplicate tag numbers in {@link Asset}
     * 
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean hasDuplicateTagNumber(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        
        boolean success = true;
        
        if (ObjectUtils.isNotNull(assetLocationGlobalDetail.getCampusTagNumber())) {
            Map map = new HashMap();
            map.put(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_TAG_NUMBER, assetLocationGlobalDetail.getCampusTagNumber()); 
            
            // retrieve all tag numbers
            Collection<Asset> tagNumbers = getBoService().findMatching(Asset.class, map);

            // does tag number exist in table?
            for (Asset asset : tagNumbers) {
                if (asset.getCampusTagNumber().equals(assetLocationGlobalDetail.getCampusTagNumber())) {
                    success = false;
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetLocationGlobal.CAMPUS_TAG_NUMBER, CamsKeyConstants.ERROR_TAG_NUMBER_DUPLICATE);
                    break;
                }
            }
        }        

        return success;
    }  
    
}