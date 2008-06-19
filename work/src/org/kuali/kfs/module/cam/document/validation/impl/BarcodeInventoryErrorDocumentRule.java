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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.BarcodeInventoryErrorDetail;
import org.kuali.module.cams.document.BarcodeInventoryErrorDocument;
import org.kuali.module.cams.service.AssetService;

/**
 * Business rule(s) applicable to Asset Payment.
 */
public class BarcodeInventoryErrorDocumentRule extends TransactionalDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorDocumentRule.class);

    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);    
    private static AssetService assetService = SpringContext.getBean(AssetService.class);
    private static DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        return validateBarcodeInventoryErrorDetail(((BarcodeInventoryErrorDocument)document).getBarcodeInventoryErrorDetail());
    }


    /**
     * 
     * This method...
     * @param document
     * @return
     */
    public boolean validateBarcodeInventoryErrorDetail(List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails) {
        boolean valid=true;
        List<BarcodeInventoryErrorDetail> validInventory = new ArrayList<BarcodeInventoryErrorDetail>();
        
        int pos=0;
        for(BarcodeInventoryErrorDetail barcodeInventoryErrorDetail:barcodeInventoryErrorDetails) {
            
            //LOG.info("****BarcodeInventoryErrorDocument - Validating - asset tag#:"+barcodeInventoryErrorDetail.getAssetTagNumber());
            
            valid&=this.validateTagNumber(barcodeInventoryErrorDetail.getAssetTagNumber());
            valid&=this.validateBuildingCode(barcodeInventoryErrorDetail.getBuildingCode(), barcodeInventoryErrorDetail);
            valid&=this.validateBuildingRoomNumber(barcodeInventoryErrorDetail.getBuildingRoomNumber(), barcodeInventoryErrorDetail);
            valid&=this.validateCampusCode(barcodeInventoryErrorDetail.getCampusCode(), barcodeInventoryErrorDetail);
            valid&=this.validateConditionCode(barcodeInventoryErrorDetail.getAssetConditionCode(), barcodeInventoryErrorDetail);
            
            if (!valid) {
                //LOG.info("****BarcodeInventoryErrorDocument - Error found - tag#:"+barcodeInventoryErrorDetail.getAssetTagNumber());
                //TODO find out what should be the code for this field when an error is found.!!!!
                barcodeInventoryErrorDetail.setErrorCorrectionStatusCode("?");
                validInventory.add(barcodeInventoryErrorDetail);
            }
            pos++;
        }
        //Cleaning collection.
        barcodeInventoryErrorDetails.clear();
        
        //Adding back all elements including those that had modifications.
        barcodeInventoryErrorDetails.addAll(validInventory);
        return valid;
    }

    
    /**
     * 
     * validates the asset tag number exists in only one active asset.
     *  
     * @param tagNumber
     * @return boolean
     */
    public boolean validateTagNumber(String tagNumber) {
        boolean result=true;
        //Getting a list of active assets. 
        List<Asset> assets = assetService.findActiveAssetsMatchingTagNumber(tagNumber);        

        //Getting the label of the campus tag number field from the DD.
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(Asset.class.getName()).getAttributeDefinition(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER).getLabel();

        if (assets == null || assets.isEmpty()) {            
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, label);
            result=false;
        } else if (assets.size() > 1) {
            //TODO use constant
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, "Tag number is already in used in more than one active asset", label);
            result=false;
        }
        LOG.info("****ValidateTagNumber - tag#:"+tagNumber+ " Result:"+result);
        return result;
    }

    /**
     * 
     * Validates the inventory date is not empty and inventory date has a valid format 
     * @param inventoryDate
     * @return boolean
     */
    public boolean validateInventoryDate(String inventoryDate) {
        boolean result=true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE).getLabel();

        if (StringUtils.isEmpty(inventoryDate)) {
            //TODO use constant
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE, "Invalid", label);            
            result=false;
        } else {        
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            try {
                dateFormat.parse(inventoryDate);
            }
            catch (Exception e) {
                //TODO use constant            
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE, "Invalid format", label);                        
                result=false;
            }
        }
        LOG.info("****ValidateInventoryDate - date:"+inventoryDate+ " Result:"+result);
        return result;
    }


    /**
     * 
     * This method...
     * @param campusCode
     * @param detail
     * @return
     */
    public boolean validateCampusCode(String campusCode, BarcodeInventoryErrorDetail detail) {        
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE).getLabel();

        if (StringUtils.isEmpty(campusCode)) {
            //TODO use constant
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE, " cannot be blank", label);                    
            result&=false;
        } else {
            detail.refreshReferenceObject(CamsPropertyConstants.BarcodeInventory.CAMPUS_REFERENCE);
            if (ObjectUtils.isNull(detail.getCampus())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE,  CamsKeyConstants.AssetLocation.ERROR_INVALID_CAMPUS_CODE, label);
                result &= false;
            }            
        }
        LOG.info("****ValidateCampusCode - code:"+campusCode+ " Result:"+result);
        return result;
    }


    /**
     * 
     * This method...
     * @param buildingCode
     * @param detail
     * @return
     */
    public boolean validateBuildingCode(String buildingCode, BarcodeInventoryErrorDetail detail) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE).getLabel();

        if (StringUtils.isEmpty(buildingCode)) {
            //TODO use constant
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, " cannot be blank", label);                    
            result&=false;
        } else {
            detail.refreshReferenceObject(CamsPropertyConstants.BarcodeInventory.BUILDING_REFERENCE);
            if (ObjectUtils.isNull(detail.getBuilding())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE,  CamsKeyConstants.AssetLocation.ERROR_INVALID_BUILDING_CODE, label);
                result &= false;
            }            
        }
        LOG.info("****validateBuildingCode - buildingCode:"+buildingCode+ " Result:"+result);        
        return result;
    }


    /**
     * 
     * This method...
     * @param roomNumber
     * @param detail
     * @return
     */
    public boolean validateBuildingRoomNumber(String roomNumber, BarcodeInventoryErrorDetail detail) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE).getLabel();

        if (StringUtils.isEmpty(roomNumber)) {
            //TODO use constant
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, " cannot be blank", label);                    
            result&=false;
        } else {
            detail.refreshReferenceObject(CamsPropertyConstants.BarcodeInventory.BUILDING_REFERENCE);
            if (ObjectUtils.isNull(detail.getBuildingRoom())) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER,  CamsKeyConstants.AssetLocation.ERROR_INVALID_ROOM_NUMBER, label);
                result &= false;
            }            
        }
        LOG.info("****validateBuildingRoomNumber - buildingRoom#:"+roomNumber+ " Result:"+result);                        
        return result;
    }


    /**
     * 
     * This method...
     * @param conditionCode
     * @param detail
     * @return
     */
    public boolean validateConditionCode(String conditionCode, BarcodeInventoryErrorDetail detail) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE).getLabel();

        if (StringUtils.isEmpty(conditionCode)) {
            //TODO use constant
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE, " cannot be blank", label);                    
            result&=false;
        } else {
            detail.refreshReferenceObject(CamsPropertyConstants.BarcodeInventory.CONDITION_REFERENCE);
            if (ObjectUtils.isNull(detail.getBuildingRoom())) {
                //TODO use constants
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE,  "Invalid" , label);
                result &= false;
            }            
        }
        LOG.info("****validateConditionCode - conditionCode:"+conditionCode+" Result:"+result);                                        
        return result;
    }

    /*
     * The inventory status from the barcode file is not passed to the capital asset table. This is because users are allowed to
     * scan non-capital assets and they may not know that non-capital assets have an inventory status of “N” for non-capital. In
     * addition the assets batch retired would have an inventory status code of “D.” For these reasons we no longer allow the
     * inventory status code to be updated.
     *
    public boolean validateInventoryStatusCode(String statusCode) {
        boolean result = false;
        return result;
    }*/


}