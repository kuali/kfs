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
import java.util.Iterator;
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
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.bo.Room;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetLocationGlobal;
import org.kuali.module.cams.bo.AssetLocationGlobalDetail;
import org.kuali.module.cams.bo.Pretag;
import org.kuali.module.cams.bo.PretagDetail;

/**
 * Business rules applicable to AssetLocationGlobal documents.
 */
public class AssetLocationGlobalRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLocationGlobalRule.class);
    
    //private Asset newAsset;
    private AssetLocationGlobal newAssetLocationGlobal;
    
    /**
     * Constructs a AssetLocationGlobalRule
     */
    public AssetLocationGlobalRule() {
    }
    
    /**
     * This method sets the convenience objects, so you have short and easy handles to the new and old objects
     * contained in the maintenance document.
     */
    @Override
    public void setupConvenienceObjects() {
        // setup a AssetLocationGlobal convenience object, make sure all possible sub-objects are populated
        //newAsset = (Asset) super.getNewBo();
        newAssetLocationGlobal = (AssetLocationGlobal) super.getNewBo();
        
        // forces refreshes on all the sub-objects in the lists
        for (AssetLocationGlobalDetail assetLocationGlobalDetail : newAssetLocationGlobal.getAssetLocationGlobalDetails()) {
            newAssetLocationGlobal.refreshNonUpdateableReferences();
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
        LOG.info("LEO processCustomSaveDocumentBusinessRules()");
        boolean success = true;
        //setupConvenienceObjects(); // needed?
        
        // process rules
        //success &= checkRules(document);

        return success;
    }

    /**
     * Processes rules when routing this global.
     * 
     * @param document MaintenanceDocument type of document to be processed.
     * @return boolean true when success
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("LEO processCustomRouteDocumentBusinessRules()");
        boolean success = true;
        //setupConvenienceObjects(); // needed?
        
        // process rules
        //success &= checkRules(document);
        
        return success;
    }

    /**
     * Process rules whenever a new {@link AssetLocationGlobalDetail} is added to this global.
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.core.document.MaintenanceDocument, java.lang.String, org.kuali.core.bo.PersistableBusinessObject)
     */
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo ) {
        LOG.info("LEO processCustomAddCollectionLineBusinessRules() START");
        boolean success = true;
        
        if (bo instanceof AssetLocationGlobalDetail) {
            
            //Asset asset = (Asset) document.getNewMaintainableObject().getBusinessObject();
            AssetLocationGlobalDetail assetLocationGlobalDetail = (AssetLocationGlobalDetail) bo;

            // process rules
            //success &= validateAssetLocationGlobalDetails(assetLocationGlobalDetail);
            success &= validateCapitalAssetNumber(assetLocationGlobalDetail);
            success &= validateCampusCode(assetLocationGlobalDetail);
            success &= validateBuildingCode(assetLocationGlobalDetail);
            success &= validateBuildingRoomNumber(assetLocationGlobalDetail);
            success &= hasDuplicateTagNumber(assetLocationGlobalDetail);
        }
        
        LOG.info("LEO processCustomAddCollectionLineBusinessRules() END");
        return success;
    }
    
    /**
     * Validate {@link AssetLocationGlobalDetail} details.
     * 
     * @param assetLocationGlobalDetail
     * @return true if the AssetLocationGlobalDetail object contains a valid Asset
     */
    protected boolean validateAssetLocationGlobalDetails(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        LOG.info("LEO checkAssetLocationGlobalDetails() START");
        
        boolean success = true;
        
        if (ObjectUtils.isNull(assetLocationGlobalDetail.getCapitalAssetNumber())) {
            GlobalVariables.getErrorMap().putError("capitalAssetNumber", KFSKeyConstants.ERROR_REQUIRED, "Capital Asset Number");
            success &= false;
        }

        /*
        if (ObjectUtils.isNull(assetLocationGlobalDetail.getCampusCode())) {
            GlobalVariables.getErrorMap().putError("campusCode", KFSKeyConstants.ERROR_REQUIRED, "Campus Code");
            success &= false;
        }
        
        if (ObjectUtils.isNull(assetLocationGlobalDetail.getBuildingCode())) {
            GlobalVariables.getErrorMap().putError("buildingCode", KFSKeyConstants.ERROR_REQUIRED, "Building Code");
            success &= false;
        }
        
        if (ObjectUtils.isNull(assetLocationGlobalDetail.getBuildingRoomNumber())) {
            GlobalVariables.getErrorMap().putError("buildingRoomNumber", KFSKeyConstants.ERROR_REQUIRED, "Building Room Number");
            success &= false;
        }
        
        if (ObjectUtils.isNull(assetLocationGlobalDetail.getBuildingSubRoomNumber())) {
            GlobalVariables.getErrorMap().putError("buildingSubRoomNumber", KFSKeyConstants.ERROR_REQUIRED, "Building Sub Room Number");
            success &= false;
        }
        
        if (ObjectUtils.isNull(assetLocationGlobalDetail.getCampusTagNumber())) {
            GlobalVariables.getErrorMap().putError("campusTagNumber", KFSKeyConstants.ERROR_REQUIRED, "Campus Tag Number");
            success &= false;
        }
        */
        
        LOG.info("LEO checkAssetLocationGlobalDetails() END");
        return success;
    }     
    
    /**
     * Validate the Capital {@link Asset} number.
     * 
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateCapitalAssetNumber(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        LOG.info("LEO checkCapitalAssetNumber() START");
        
        boolean success = true;
        LOG.info("LEO checkCapitalAssetNumber() ASSET LOC GLOBAL '" + assetLocationGlobalDetail.getCapitalAssetNumber() +"'");
        
        if (ObjectUtils.isNotNull(assetLocationGlobalDetail.getCapitalAssetNumber())) {
            HashMap map = new HashMap();
            map.put("capitalAssetNumber", assetLocationGlobalDetail.getCapitalAssetNumber());  
            LOG.info("LEO checkCapitalAssetNumber() MAP");
            
            Asset asset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, map);
            if (ObjectUtils.isNull(asset)) {
                LOG.info("LEO checkCapitalAssetNumber() IN...");
                GlobalVariables.getErrorMap().putError("capitalAssetNumber", CamsKeyConstants.Retirement.ERROR_BLANK_CAPITAL_ASSET_NUMBER);
                success &= false;
            }
        }
        
        LOG.info("LEO checkCapitalAssetNumber() END");
        return success;
    }

    /**
     * Validate {@link Campus} code.
     * 
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateCampusCode(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        LOG.info("LEO validateCampusCode() START");
        
        boolean success = true;
        LOG.info("LEO validateCampusCode() CAMPUS CODE '" + assetLocationGlobalDetail.getCampusCode() +"'");
        
        if (StringUtils.isNotBlank(assetLocationGlobalDetail.getCampusCode())) {
            HashMap map = new HashMap();
            map.put("campusCode", assetLocationGlobalDetail.getCampusCode());  
            LOG.info("LEO validateCampusCode() MAP");
            
            Campus campus = (Campus) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Campus.class, map);
            if (ObjectUtils.isNull(campus)) {
                LOG.info("LEO validateCampusCode() IN...");
                GlobalVariables.getErrorMap().putError("campusCode", CamsKeyConstants.AssetLocation.ERROR_INVALID_BUILDING_CODE, new String[] { assetLocationGlobalDetail.getCampusCode() });
                //GlobalVariables.getErrorMap().putError("campusCode", CamsKeyConstants.AssetLocation.ERROR_INVALID_CAMPUS_CODE);
                success &= false;
            }
        }
        
        LOG.info("LEO validateCampusCode() END");
        return success;
    }
    
    /**
     * Validate {@link Building} code.
     * 
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateBuildingCode(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        LOG.info("LEO validateBuildingCode() START");
        
        boolean success = true;
        LOG.info("LEO validateBuildingCode() CAMPUS CODE '" + assetLocationGlobalDetail.getCampusCode() +"'");
        LOG.info("LEO validateBuildingCode() BUILDING CODE '" + assetLocationGlobalDetail.getBuildingCode() +"'");
        
        if (StringUtils.isNotBlank(assetLocationGlobalDetail.getCampusCode()) && StringUtils.isNotBlank(assetLocationGlobalDetail.getBuildingCode())) {
            HashMap map = new HashMap();
            map.put("campusCode", assetLocationGlobalDetail.getCampusCode());
            map.put("buildingCode", assetLocationGlobalDetail.getBuildingCode());
            LOG.info("LEO validateBuildingCode() MAP");
        
            Building building = (Building) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Building.class, map);
            if (ObjectUtils.isNull(building)) {
                LOG.info("LEO validateBuildingCode() IN...");
                GlobalVariables.getErrorMap().putError("buildingCode", CamsKeyConstants.AssetLocation.ERROR_INVALID_BUILDING_CODE, new String[] { assetLocationGlobalDetail.getCampusCode(), assetLocationGlobalDetail.getBuildingCode() });
                success &= false;
            }
        }
        
        LOG.info("LEO validateBuildingCode() END");
        return success;
    }
    
    /**
     * Validate {@link Building} code.
     * 
     * @param assetLocationGlobalDetail
     * @return boolean
     */
    protected boolean validateBuildingRoomNumber(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        LOG.info("LEO validateBuildingRoom() START");
        
        boolean success = true;
        LOG.info("LEO validateBuildingRoom() BUILDING ROOM '" + assetLocationGlobalDetail.getBuildingRoomNumber() +"'");
        
        if (StringUtils.isNotBlank(assetLocationGlobalDetail.getBuildingRoomNumber())) {
            HashMap map = new HashMap();
            map.put("buildingRoomNumber", assetLocationGlobalDetail.getBuildingRoomNumber());
            LOG.info("LEO validateBuildingRoom() MAP");
            
            Room room = (Room) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Room.class, map);
            if (ObjectUtils.isNull(room)) {
                GlobalVariables.getErrorMap().putError("buildingRoomNumber", CamsKeyConstants.AssetLocation.ERROR_INVALID_ROOM_NUMBER, new String[] { assetLocationGlobalDetail.getCampusCode(), assetLocationGlobalDetail.getBuildingCode(), assetLocationGlobalDetail.getBuildingRoomNumber() });
                success &= false;
            }
        }
        
        LOG.info("LEO validateBuildingRoom() END");
        return success;
    }
    
    //TODO: Check for duplicate tag number
    protected boolean hasDuplicateTagNumber(AssetLocationGlobalDetail assetLocationGlobalDetail) {
    //protected boolean checkDuplicateTagNumber(Pretag pretag, String tagNumber) {
        LOG.info("LEO hasDuplicateTagNumber - START");
        
        boolean success = true;
        LOG.info("LEO hasDuplicateTagNumber() TAG NUMBER '" + assetLocationGlobalDetail.getCampusTagNumber() +"'");
        
        if (ObjectUtils.isNotNull(assetLocationGlobalDetail.getCampusTagNumber())) {
            HashMap map = new HashMap();
            map.put("campusTagNumber", assetLocationGlobalDetail.getCampusTagNumber());  
            LOG.info("LEO hasDuplicateTagNumber() MAP");

            //Asset asset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, map);
            Collection<AssetLocationGlobalDetail> tagNumbers = SpringContext.getBean(BusinessObjectService.class).findMatching(Asset.class, map);
            LOG.info("LEO hasDuplicateTagNumber() TAG NUMBER SIZE '" + tagNumbers.size() + "'");
            
            //Asset asset = (Asset) document.getNewMaintainableObject().getBusinessObject();
            //Pretag pretag = (Pretag) document.getNewMaintainableObject().getBusinessObject();
            
            for (AssetLocationGlobalDetail dtl : tagNumbers) {
                if (dtl.getCampusTagNumber().equals(tagNumbers)) {
                    LOG.info("LEO hasDuplicateTagNumber() DTL TAG NUMBER '" + dtl.getCampusTagNumber() + "'");
                    putFieldError(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsKeyConstants.ERROR_TAG_NUMBER_DUPLICATE);
                    GlobalVariables.getErrorMap().putError(CamsKeyConstants.ERROR_TAG_NUMBER_DUPLICATE, CamsKeyConstants.ERROR_TAG_NUMBER_DUPLICATE, new String[] { assetLocationGlobalDetail.getCampusTagNumber() });
                    success &= false;
                }
            }
        }

        return success;
    }    
    
    
    /*
    //TODO: Check for duplicate tag number
    protected boolean hasDuplicateTagNumber(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        LOG.info("LEO hasDuplicateTagNumber() START");
        
        boolean success = true;
        boolean duplicateFound = false;
        LOG.info("LEO hasDuplicateTagNumber() DUP TAG '" + assetLocationGlobalDetail.getCampusTagNumber() +"'");
        
        if (ObjectUtils.isNotNull(assetLocationGlobalDetail.getCampusTagNumber())) {
            HashMap map = new HashMap();
            map.put("campusTagNumber", assetLocationGlobalDetail.getCampusTagNumber());  
            LOG.info("LEO hasDuplicateTagNumber() MAP");
            
            Collection<AssetLocationGlobalDetail> tagNumbers = SpringContext.getBean(BusinessObjectService.class).findMatching(Asset.class, map);
            LOG.info("LEO hasDuplicateTagNumber() TAG NUMBERS '" + tagNumbers.size() + "'");
            int matchDetailCount = countActive(tagNumbers);
            LOG.info("LEO hasDuplicateTagNumber() MATCH DETAIL COUNT '" + matchDetailCount + "'");
            
            //int matchDetailCount = getMatchDetailCount(map);
            
            if ((getBoService().countMatching(Asset.class, map) != 0) || (matchDetailCount > 0)) {
                //GlobalVariables.getErrorMap().putError("campusTagNumber", CamsKeyConstants.ERROR_PRE_TAG_NUMBER, new String[] { assetLocationGlobalDetail.getCampusTagNumber() });
                putFieldError("campusTagNumber", CamsKeyConstants.ERROR_TAG_NUMBER_DUPLICATE, new String[] { assetLocationGlobalDetail.getCampusTagNumber() });
                success &= false;
            }
           
        }
        
        LOG.info("LEO validateBuildingRoom() END");
        return success;
    }
    */

    /**
     * Returns any number of matched campus tag numbers
     * 
     * @param map
     * @return assetLocationGlobalDetail with matching campus tag number
     */

    public int getMatchDetailCount(Map map) {
        Collection<AssetLocationGlobalDetail> assetLocationGlobalDetail = SpringContext.getBean(BusinessObjectService.class).findMatching(AssetLocationGlobalDetail.class, map);
        return countActive(assetLocationGlobalDetail);
    }
    
    /**
     * Returns total count {@link AssetLocationGlobalDetail} of active detail lines
     * 
     * @param collection
     * @return active assetLocationGlobalDetail count
     */
    public int countActive(Collection<AssetLocationGlobalDetail> assetLocationGlobalDetail) {
        LOG.info("LEO countActive() START");
        int activeCount = 0;
        
        
        for (AssetLocationGlobalDetail dtl : assetLocationGlobalDetail) {
            LOG.info("LEO countActive() IN LOOP... CAMPUS TAG NBR '" + dtl.getCampusTagNumber() + "'");
            //if (dtl.isActive()) {
            //    activeCount++;
            //}
        }

        LOG.info("LEO countActive() END");
        return activeCount;
    }
    
}