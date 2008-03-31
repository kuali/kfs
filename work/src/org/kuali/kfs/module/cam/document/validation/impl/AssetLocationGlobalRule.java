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

import static org.kuali.module.cams.CamsKeyConstants.ERROR_INVALID_ASSET_WARRANTY_NO;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.ASSET_WARRANTY_WARRANTY_NUMBER;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetLocationGlobal;
import org.kuali.module.cams.bo.AssetLocationGlobalDetail;
import org.kuali.module.cams.bo.AssetWarranty;

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
            AssetLocationGlobalDetail assetLocationGlobalDetail = (AssetLocationGlobalDetail) bo;

            // process rules
            success &= validateCapitalAssetNumber(assetLocationGlobalDetail);
            //success &= checkAssetLocationGlobalDetails(assetLocationGlobalDetail);
        }
        
        LOG.info("LEO processCustomAddCollectionLineBusinessRules() END");
        return success;
    }

    /**
     * 
     * Validate the Capital Asset Number.
     * 
     * @param assetLocationGlobalDetail
     * @param newAsset
     * @return false if the capital asset number is not valid.
     */
    protected boolean validateCapitalAssetNumber(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        LOG.info("LEO checkCapitalAssetNumber() START");
        
        boolean success = true;
        LOG.info("LEO checkCapitalAssetNumber() ASSET LOC GLOBAL '" + assetLocationGlobalDetail.getCapitalAssetNumber() +"'");
        
        
        // place PK into Map
        HashMap map = new HashMap();
        map.put("capitalAssetNumber", assetLocationGlobalDetail.getCapitalAssetNumber());  
        LOG.info("LEO checkCapitalAssetNumber() MAP");
        
        // retrieve Asset object by PK
        Asset asset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, map);
        //Asset asset = (Asset) getBoService().findByPrimaryKey(Asset.class, map);
        LOG.info("LEO checkCapitalAssetNumber() ASSET '" + asset.getCapitalAssetNumber() +"'");
        
        if (ObjectUtils.isNull(asset.getCapitalAssetNumber())) {
            LOG.info("LEO checkCapitalAssetNumber() IN...");
            GlobalVariables.getErrorMap().putError("capitalAssetNumber", CamsKeyConstants.ERROR_INVALID_CAPITAL_ASSET_NUMBER);
            //putFieldError("capitalAssetNumber", CamsKeyConstants.ERROR_INVALID_CAPITAL_ASSET_NUMBER);
            success &= false;
        }
        
        LOG.info("LEO checkCapitalAssetNumber() END");
        return success;
    }    


    /**
     * Validate {@link Building} code....IN PROGRESS....
     * 
     * @param asset Asset
     * @return validation success
     */
    private boolean validateBuildingCode(Asset asset) {
        boolean success = false;
        
        Building building = asset.getBuilding();
        
        if (ObjectUtils.isNull(building)) {
            if (StringUtils.isEmpty(building.getBuildingCode())) {
                // LEO - CHANGE THE MSG
                GlobalVariables.getErrorMap().putError("buildingCode", CamsKeyConstants.ERROR_INVALID_CAPITAL_ASSET_NUMBER);
                return success;
            }
        }
        return success;
    }
    
    /**
     * Check {@link AssetLocationGlobalDetail} details.
     * 
     * @param assetLocationGlobalDetail
     * @return true if the AssetLocationGlobalDetail object contains a valid Asset
     */
    public boolean checkAssetLocationGlobalDetails(AssetLocationGlobalDetail assetLocationGlobalDetail) {
        LOG.info("LEO checkAssetLocationGlobalDetails() START");
        
        boolean success = true;
        
        if (!checkEmptyValue(assetLocationGlobalDetail.getCapitalAssetNumber())) {
            GlobalVariables.getErrorMap().putError("capitalAssetNumber", KFSKeyConstants.ERROR_REQUIRED, "Capital Asset Number");
            success &= false;
        }
        
        if (!checkEmptyValue(assetLocationGlobalDetail.getCampusCode())) {
            GlobalVariables.getErrorMap().putError("campusCode", KFSKeyConstants.ERROR_REQUIRED, "Campus Code");
            success &= false;
        }
        
        if (!checkEmptyValue(assetLocationGlobalDetail.getBuildingCode())) {
            GlobalVariables.getErrorMap().putError("buildingCode", KFSKeyConstants.ERROR_REQUIRED, "Building Code");
            success &= false;
        }
        
        if (!checkEmptyValue(assetLocationGlobalDetail.getBuildingRoomNumber())) {
            GlobalVariables.getErrorMap().putError("buildingRoomNumber", KFSKeyConstants.ERROR_REQUIRED, "Building Room Number");
            success &= false;
        }
        
        if (!checkEmptyValue(assetLocationGlobalDetail.getBuildingSubRoomNumber())) {
            GlobalVariables.getErrorMap().putError("buildingSubRoomNumber", KFSKeyConstants.ERROR_REQUIRED, "Building Sub Room Number");
            success &= false;
        }
        
        if (!checkEmptyValue(assetLocationGlobalDetail.getCampusTagNumber())) {
            GlobalVariables.getErrorMap().putError("campusTagNumber", KFSKeyConstants.ERROR_REQUIRED, "Campus Tag Number");
            success &= false;
        }
        
        /* get error count
        int originalErrorCount = GlobalVariables.getErrorMap().getErrorCount();
        getDictionaryValidationService().validateBusinessObject(assetLocationGlobalDetail);
        
        // check for blank
        if (StringUtils.isNotBlank(assetLocationGlobalDetail.getCampusCode()) &&  StringUtils.isNotBlank(assetLocationGlobalDetail.getBuildingCode()) && StringUtils.isNotBlank(assetLocationGlobalDetail.getBuildingRoomNumber()) && StringUtils.isNotBlank(assetLocationGlobalDetail.getBuildingSubRoomNumber()) && StringUtils.isNotBlank(assetLocationGlobalDetail.getCampusTagNumber()) ) {
            assetLocationGlobalDetail.refreshReferenceObject("asset");
            
            if (assetLocationGlobalDetail.getCapitalAssetNumber() == null) {
                //GlobalVariables.getErrorMap().putError("accountNumber", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ACCOUNT, new String[] { assetLocationGlobalDetail.getChartOfAccountsCode(), assetLocationGlobalDetail.getAccountNumber() });
                GlobalVariables.getErrorMap().putError("capitalAssetNumber", "one", "two");
            }
        }
        success &= GlobalVariables.getErrorMap().getErrorCount() == originalErrorCount;
        */
        
        LOG.info("LEO checkAssetLocationGlobalDetails() END");
        
        return success;
    }    
    
}
