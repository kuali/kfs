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

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Campus;
import org.kuali.core.document.Document;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
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
import org.kuali.kfs.module.cam.businessobject.AssetCondition;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;

/**
 * Business rule(s) applicable to Asset Barcode Inventory upload and Barcode inventory error document.
 */
public class BarcodeInventoryErrorDocumentRule extends TransactionalDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorDocumentRule.class);

    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);
    private static AssetService assetService = SpringContext.getBean(AssetService.class);
    private static DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
    private static KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
    private static BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        return true;
    }

    /**
     * Invokes several methods that validates each barcode error record
     * 
     * @param barcodeInventoryErrorDetails
     * @return boolean
     */
    public boolean validateBarcodeInventoryErrorDetail(List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails) {
        String errorPath = "";
        boolean valid = true;
        List<BarcodeInventoryErrorDetail> inventory = new ArrayList<BarcodeInventoryErrorDetail>();

        // Deleting previous error messages
        GlobalVariables.getErrorMap().clear();

        Long lineNumber = new Long(0);
        for (BarcodeInventoryErrorDetail barcodeInventoryErrorDetail : barcodeInventoryErrorDetails) {
//            if (barcodeInventoryErrorDetail.getAssetTagNumber().equals("IU011029")) {
//                lineNumber = lineNumber;
//            }
            if (barcodeInventoryErrorDetail.getErrorCorrectionStatusCode().equals(CamsConstants.BarcodeInventoryError.STATUS_CODE_ERROR)) {
                valid = true;
                errorPath = CamsConstants.DOCUMENT_PATH + "." + CamsPropertyConstants.BarcodeInventory.BARCODE_INVENTORY_DETAIL + "[" + (lineNumber.intValue()) + "]";
                GlobalVariables.getErrorMap().addToErrorPath(errorPath);

                valid &= this.validateTagNumber(barcodeInventoryErrorDetail.getAssetTagNumber());
                valid &= this.validateBuildingCode(barcodeInventoryErrorDetail.getBuildingCode(), barcodeInventoryErrorDetail);
                valid &= this.validateBuildingRoomNumber(barcodeInventoryErrorDetail.getBuildingRoomNumber(), barcodeInventoryErrorDetail);
                valid &= this.validateCampusCode(barcodeInventoryErrorDetail.getCampusCode(), barcodeInventoryErrorDetail);
                valid &= this.validateConditionCode(barcodeInventoryErrorDetail.getAssetConditionCode(), barcodeInventoryErrorDetail);
                valid &= this.validateInventoryDate(barcodeInventoryErrorDetail.getUploadScanTimestamp());
                valid &= this.validateTaggingLock(barcodeInventoryErrorDetail.getAssetTagNumber());

                if (!valid) {
                    barcodeInventoryErrorDetail.setErrorCorrectionStatusCode(CamsConstants.BarcodeInventoryError.STATUS_CODE_ERROR);

                    // Getting the errors from GlobalVariables.
                    barcodeInventoryErrorDetail.setErrorDescription(getErrorMessages(errorPath));
                }
                else {
                    barcodeInventoryErrorDetail.setErrorCorrectionStatusCode(CamsConstants.BarcodeInventoryError.STATUS_CODE_CORRECTED);
                }
                GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
            }
            lineNumber++;
        }
        
        deleteLockErrorMessages();
        return true;
    }

    /**
     * validates the asset tag number exists in only one active asset.
     * 
     * @param tagNumber
     * @return boolean
     */
    private boolean validateTagNumber(String tagNumber) {
        boolean result = true;
        // Getting a list of active assets.
        List<Asset> assets = assetService.findActiveAssetsMatchingTagNumber(tagNumber);

        if (ObjectUtils.isNull(assets) || assets.isEmpty()) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_CAPITAL_ASSET_DOESNT_EXISTS);
            result = false;
        }
        else if (assets.size() > 1) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_DUPLICATED_TAG_NUMBER);
            result = false;
        }
        return result;
    }

    /**
     * Validates the inventory date is not equals null
     * 
     * @param inventoryDate
     * @return boolean
     */
    private boolean validateInventoryDate(Timestamp inventoryDate) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE).getLabel();

        if (inventoryDate == null) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);
            result = false;
        }
        return result;
    }


    /**
     * Validates the campus code exists in the campus code table
     * 
     * @param campusCode
     * @param detail
     * @return boolean
     */
    private boolean validateCampusCode(String campusCode, BarcodeInventoryErrorDetail detail) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE).getLabel();

        Campus campus;
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(KFSPropertyConstants.CAMPUS_CODE, detail.getCampusCode());
        campus = (Campus) businessObjectService.findByPrimaryKey(Campus.class, fields);

        if (ObjectUtils.isNull(campus)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);
            result = false;
        }
        return result;
    }


    /**
     * Validates the building code exists
     * 
     * @param buildingCode
     * @param detail
     * @return boolean
     */
    private boolean validateBuildingCode(String buildingCode, BarcodeInventoryErrorDetail detail) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE).getLabel();

        Building building;
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(KFSPropertyConstants.CAMPUS_CODE, detail.getCampusCode());
        fields.put(KFSPropertyConstants.BUILDING_CODE, detail.getBuildingCode());
        building = (Building) businessObjectService.findByPrimaryKey(Building.class, fields);

        if (ObjectUtils.isNull(building)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);
            result &= false;
        }
        return result;
    }


    /**
     * Validates the building room number exists
     * 
     * @param roomNumber
     * @param detail
     * @return boolean
     */
    private boolean validateBuildingRoomNumber(String roomNumber, BarcodeInventoryErrorDetail detail) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE).getLabel();

        Room room;
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(KFSPropertyConstants.CAMPUS_CODE, detail.getCampusCode());
        fields.put(KFSPropertyConstants.BUILDING_CODE, detail.getBuildingCode());
        fields.put(KFSPropertyConstants.BUILDING_ROOM_NUMBER, detail.getBuildingRoomNumber());
        room = (Room) businessObjectService.findByPrimaryKey(Room.class, fields);

        if (ObjectUtils.isNull(room)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);
            result = false;
        }
        return result;
    }

    /**
     * Validates the condition code exists in the condition table
     * 
     * @param conditionCode
     * @param detail
     * @return boolean
     */
    private boolean validateConditionCode(String conditionCode, BarcodeInventoryErrorDetail detail) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE).getLabel();

        AssetCondition condition;
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE, detail.getAssetConditionCode());
        condition = (AssetCondition) businessObjectService.findByPrimaryKey(AssetCondition.class, fields);

        if (ObjectUtils.isNull(condition)) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);
            result &= false;
        }
        return result;
    }

    /**
     * Validates the Asset doesn't have any tagging locks as result of an existing document in process where the same asset being
     * affected
     * 
     * @param tagNumber
     * @return boolean
     */
    private boolean validateTaggingLock(String tagNumber) {
        boolean result = true;
        boolean isAssetLocked = false;
        String skipAssetLockValidation;

        // Getting system parameter in order to determine whether or not the asset locks will be ignored.
        if (parameterService.parameterExists(BarcodeInventoryErrorDocument.class, CamsConstants.Parameters.BAR_CODE_ERROR_DOCUMENT_IGNORES_LOCKS)) {
            skipAssetLockValidation = parameterService.getParameterValue(BarcodeInventoryErrorDocument.class, CamsConstants.Parameters.BAR_CODE_ERROR_DOCUMENT_IGNORES_LOCKS);
        }
        else {
            LOG.warn("CAMS Parameter '" + CamsConstants.Parameters.BAR_CODE_ERROR_DOCUMENT_IGNORES_LOCKS + "' not found! - Setting default value to 'N' ");            
            skipAssetLockValidation = CamsConstants.BarcodeInventoryError.BAR_CODE_ERROR_DOCUMENT_IGNORES_LOCKS_NO;
        }

        if (skipAssetLockValidation == null || StringUtils.isEmpty(skipAssetLockValidation) || StringUtils.equals(skipAssetLockValidation, CamsConstants.BarcodeInventoryError.BAR_CODE_ERROR_DOCUMENT_IGNORES_LOCKS_NO)) {
            // Getting a list of active assets.
            List<Asset> assets = assetService.findActiveAssetsMatchingTagNumber(tagNumber);
            if (assets.size() > 1) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_DUPLICATED_TAG_NUMBER);
                result = false;
            }
            else if ((assets.size() > 0)) {
                isAssetLocked = assetService.isAssetLocked("", assets.get(0).getCapitalAssetNumber());
                if (isAssetLocked) {
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_ASSET_LOCKED);
                    result = false;                    
                }
            }
        }
        else {
            result = true;
        }
        return result;
    }


    /**
     * Iterates over the list of errors each records might have and returns a single string with all the errors for each asset
     * 
     * @param errorPath
     * @return String
     */
    private String getErrorMessages(String errorPath) {
        String message = "";
        String[] fields = { CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE, CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE, CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE };

        for (int i = 0; i < fields.length; i++) {
            String propertyName = errorPath + "." + fields[i];
            if (GlobalVariables.getErrorMap().containsKey(propertyName)) {
                for (Object errorMessage : GlobalVariables.getErrorMap().getMessages(propertyName)) {
                    String errorMsg = kualiConfigurationService.getPropertyString(((ErrorMessage) errorMessage).getErrorKey());
                    message += ", " + MessageFormat.format(errorMsg, (Object[]) ((ErrorMessage) errorMessage).getMessageParameters());
                }
            }
        }
        return (StringUtils.isEmpty(message) ? message : message.substring(2));
    }
    
    
    /**
     * 
     * Deletes the asset locking error messages from the GlobalVariables.
     * @return none 
     */
    private void deleteLockErrorMessages() {
        //Finding locking error messages
        List<ErrorMessage> el = new ArrayList<ErrorMessage>();                    
        for (Iterator<ErrorMessage> iterator = GlobalVariables.getErrorMap().getMessages(KFSConstants.GLOBAL_ERRORS).iterator(); iterator.hasNext();) {                        
            ErrorMessage errorMessage= (ErrorMessage)iterator.next();
            if (errorMessage.getErrorKey().equals(KFSKeyConstants.ERROR_MAINTENANCE_LOCKED)) {
                el.add(errorMessage);                        
            }
        }

        //Deleting lock error messages from global variable.
        for(ErrorMessage em : el) {
            GlobalVariables.getErrorMap().getMessages(KFSConstants.GLOBAL_ERRORS).remove(em);
        }        
    }
    
}