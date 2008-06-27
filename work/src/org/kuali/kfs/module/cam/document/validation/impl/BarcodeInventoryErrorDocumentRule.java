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
package org.kuali.kfs.module.cam.document.validation.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;

/**
 * Business rule(s) applicable to Asset Payment.
 */
public class BarcodeInventoryErrorDocumentRule extends TransactionalDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorDocumentRule.class);

    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);    
    private static AssetService assetService = SpringContext.getBean(AssetService.class);
    private static DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
    private static KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        validateBarcodeInventoryErrorDetail(((BarcodeInventoryErrorDocument)document).getBarcodeInventoryErrorDetail());
        return true;
    }


    /**
     * 
     * This method invokes several methods that validates each barcode error record
     *  
     * @param barcodeInventoryErrorDetails
     * @return boolean
     */
    public boolean validateBarcodeInventoryErrorDetail(List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails) {
        boolean valid=true;
        List<BarcodeInventoryErrorDetail> inventory = new ArrayList<BarcodeInventoryErrorDetail>();
        
        Long lineNumber=new Long(1);
        for(BarcodeInventoryErrorDetail barcodeInventoryErrorDetail:barcodeInventoryErrorDetails) {
            valid=true;
            String errorPath = CamsConstants.DOCUMENT_PATH + "."+CamsPropertyConstants.BarcodeInventory.BARCODE_INVENTORY_DETAIL + "[" + (lineNumber.intValue()-1) + "]";
            GlobalVariables.getErrorMap().addToErrorPath(errorPath);
                        
            valid&=this.validateTagNumber(barcodeInventoryErrorDetail.getAssetTagNumber());
            valid&=this.validateBuildingCode(barcodeInventoryErrorDetail.getBuildingCode(), barcodeInventoryErrorDetail);
            valid&=this.validateBuildingRoomNumber(barcodeInventoryErrorDetail.getBuildingRoomNumber(), barcodeInventoryErrorDetail,lineNumber.intValue()-1);
            valid&=this.validateCampusCode(barcodeInventoryErrorDetail.getCampusCode(), barcodeInventoryErrorDetail);
            valid&=this.validateConditionCode(barcodeInventoryErrorDetail.getAssetConditionCode(), barcodeInventoryErrorDetail,lineNumber.intValue()-1);
            valid&=this.validateInventoryDate(barcodeInventoryErrorDetail.getUploadScanTimestamp());
                    
            if (!valid) {
                barcodeInventoryErrorDetail.setErrorCorrectionStatusCode(CamsConstants.BarcodeInventoryError.STATUS_CODE_ERROR);
                barcodeInventoryErrorDetail.setUploadRowNumber(lineNumber);

                //Getting the errors from the GlobalVariables.
                barcodeInventoryErrorDetail.setErrorDescription(getErrorMessages(errorPath));
                lineNumber++;
            } else {
                barcodeInventoryErrorDetail.setErrorCorrectionStatusCode(CamsConstants.BarcodeInventoryError.STATUS_CODE_CORRECTED);
            }
            inventory.add(barcodeInventoryErrorDetail);
            
            GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
        }        
        //Cleaning collection.
        barcodeInventoryErrorDetails.clear();
        
        //Adding back all elements including those that had modifications.
        barcodeInventoryErrorDetails.addAll(inventory);

        //Deleting the errors because I'm not going to display them on the page as a regular error.
        //The errors will be displayed as part of the records.
        //GlobalVariables.getErrorMap().clear();

        return true;
    }


    /**
     * 
     * This method iterates over the list of errors each records might have and returns a single string with all the errors
     * @param errorPath
     * @return String
     */
    private String getErrorMessages(String errorPath) {
        String message="";
        String[] fields = {CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER,CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE,
                           CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE,CamsPropertyConstants.BarcodeInventory.BUILDING_CODE,
                           CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER,CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE};

        for(int i=0;i<fields.length;i++) {
            String propertyName=errorPath+"."+fields[i];
            if (GlobalVariables.getErrorMap().containsKey(propertyName)) {
                for (Object errorMessage : GlobalVariables.getErrorMap().getMessages(propertyName)) {
                    String errorMsg = kualiConfigurationService.getPropertyString(((ErrorMessage) errorMessage).getErrorKey());
                    message += ", "+MessageFormat.format(errorMsg, (Object[]) ((ErrorMessage) errorMessage).getMessageParameters());
                }
            }                        
        }
        return (StringUtils.isEmpty(message) ? message : message.substring(2));
    }
    /**
     * 
     * validates the asset tag number exists in only one active asset.
     *  
     * @param tagNumber
     * @return boolean
     */
    private boolean validateTagNumber(String tagNumber) {
        boolean result=true;
        //Getting a list of active assets. 
        List<Asset> assets = assetService.findActiveAssetsMatchingTagNumber(tagNumber);        

        //Getting the label of the campus tag number field from the DD.
        //String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(Asset.class.getName()).getAttributeDefinition(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER).getLabel();

        if (assets == null || assets.isEmpty()) {            
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_CAPITAL_ASSET_DOESNT_EXISTS);
            result=false;
        } else if (assets.size() > 1) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER,CamsKeyConstants.BarcodeInventory.ERROR_DUPLICATED_TAG_NUMBER); 
            result=false;
        }
        //LOG.info("****ValidateTagNumber - tag#:"+tagNumber+ " Result:"+result);
        return result;
    }

    /**
     * 
     * Validates the inventory date is not equals null 
     * 
     * @param inventoryDate
     * @return boolean
     */
    private boolean validateInventoryDate(Timestamp inventoryDate) {
        boolean result=true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE).getLabel();

        if (inventoryDate == null) {        
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE,CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);            
            result=false;
        } 
        return result;
    }


    /**
     * 
     * Validates the campus code exists in the campus code table
     * 
     * @param campusCode
     * @param detail
     * @return
     */
    private boolean validateCampusCode(String campusCode, BarcodeInventoryErrorDetail detail) {        
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE).getLabel();

        detail.refreshReferenceObject(CamsPropertyConstants.BarcodeInventory.CAMPUS_REFERENCE);
        if (ObjectUtils.isNull(detail.getCampus())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE,CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);            
            result=false;
        }
        return result;
    }


    /**
     * 
     * Validates the building code exists
     * 
     * @param buildingCode
     * @param detail
     * @return boolean
     */
    private boolean validateBuildingCode(String buildingCode, BarcodeInventoryErrorDetail detail) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE).getLabel();

        detail.refreshReferenceObject(CamsPropertyConstants.BarcodeInventory.BUILDING_REFERENCE);
        if (ObjectUtils.isNull(detail.getBuilding())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE,CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);                    
            result&=false;
        }
        //LOG.info("****validateBuildingCode - buildingCode:"+buildingCode+ " Result:"+result);        
        return result;
    }


    /**
     * 
     * Validates the building room number exists 
     * 
     * @param roomNumber
     * @param detail
     * @return boolean
     */
    private boolean validateBuildingRoomNumber(String roomNumber, BarcodeInventoryErrorDetail detail, int line) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE).getLabel();
        
        detail.refreshReferenceObject(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_REFERENCE);
        if (ObjectUtils.isNull(detail.getBuildingRoom())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER,CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);                    
            result=false;
        }
        //LOG.info("****validateBuildingRoomNumber - buildingRoom#:"+roomNumber+ " Result:"+result);                        
        return result;
    }


    /**
     * 
     * Validates the condition code exists in the condition table
     * 
     * @param conditionCode
     * @param detail
     * @return boolean
     */
    private boolean validateConditionCode(String conditionCode, BarcodeInventoryErrorDetail detail,int line) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE).getLabel();

        detail.refreshReferenceObject(CamsPropertyConstants.BarcodeInventory.CONDITION_REFERENCE);
        if (ObjectUtils.isNull(detail.getCondition())) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE,CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);
            result&=false;
        }        
        //LOG.info("****validateConditionCode - conditionCode:"+conditionCode+" Result:"+result);                                        
        return result;
    }
}