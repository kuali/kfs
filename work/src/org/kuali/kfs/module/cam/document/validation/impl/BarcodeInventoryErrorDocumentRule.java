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

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryLoadService;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetCondition;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.service.AssetLockService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusService;

/**
 * Business rule(s) applicable to Asset Barcode Inventory upload and Barcode inventory error document.
 */
public class BarcodeInventoryErrorDocumentRule extends TransactionalDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorDocumentRule.class);

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.krad.document.Document)
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
    public boolean validateBarcodeInventoryErrorDetail(BarcodeInventoryErrorDocument document, boolean updateStatus) {
        String errorPath = "";
        boolean valid = true;
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail();
        List<BarcodeInventoryErrorDetail> inventory = new ArrayList<BarcodeInventoryErrorDetail>();

        Long lineNumber = new Long(0);
        for (BarcodeInventoryErrorDetail barcodeInventoryErrorDetail : barcodeInventoryErrorDetails) {
            barcodeInventoryErrorDetail.setErrorDescription("");
            if (barcodeInventoryErrorDetail.getErrorCorrectionStatusCode().equals(CamsConstants.BarCodeInventoryError.STATUS_CODE_ERROR)) {
                valid = true;
                errorPath = CamsConstants.DOCUMENT_PATH + "." + CamsPropertyConstants.BarcodeInventory.BARCODE_INVENTORY_DETAIL + "[" + (lineNumber.intValue()) + "]";
                GlobalVariables.getMessageMap().addToErrorPath(errorPath);

                Asset asset = validateTagNumberAndRetrieveActiveAsset(barcodeInventoryErrorDetail.getAssetTagNumber());
                valid &= ObjectUtils.isNotNull(asset);
                valid &= this.validateCampusCode(barcodeInventoryErrorDetail.getCampusCode(), barcodeInventoryErrorDetail);
                if (ObjectUtils.isNotNull(asset)) {
                    valid &= this.validateBuildingCodeAndRoomNumber(barcodeInventoryErrorDetail, asset);
                }
                //valid &= this.validateBuildingCode(barcodeInventoryErrorDetail.getBuildingCode(), barcodeInventoryErrorDetail, asset);
                //valid &= this.validateBuildingRoomNumber(barcodeInventoryErrorDetail.getBuildingRoomNumber(), barcodeInventoryErrorDetail, asset);
                valid &= this.validateConditionCode(barcodeInventoryErrorDetail.getAssetConditionCode(), barcodeInventoryErrorDetail);
                valid &= this.validateInventoryDate(barcodeInventoryErrorDetail.getUploadScanTimestamp());
                valid &= this.validateTaggingLock(barcodeInventoryErrorDetail.getAssetTagNumber(), document.getDocumentNumber());

                if (!valid) {
                    barcodeInventoryErrorDetail.setErrorCorrectionStatusCode(CamsConstants.BarCodeInventoryError.STATUS_CODE_ERROR);

                    // Getting the errors from GlobalVariables.
                    barcodeInventoryErrorDetail.setErrorDescription(getErrorMessages(errorPath));
                }
                else {
                    if (updateStatus) {
                        barcodeInventoryErrorDetail.setErrorCorrectionStatusCode(CamsConstants.BarCodeInventoryError.STATUS_CODE_CORRECTED);
                    }

                    barcodeInventoryErrorDetail.setErrorDescription("NONE");
                }
                GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            }
            lineNumber++;
        }

        /*
         * Since this document displays the asset lock error messages on the error description field, we don't want to display the
         * same message at the top of the document. Therefore, deleteLockErrorMessages method deletes such errors from the
         * GlobalVariables object.
         */
        deleteLockErrorMessages();

        return true;
    }

    /**
     * validates the asset tag number exists in only one active asset.
     *
     * @param tagNumber
     * @return boolean
     *
     * @deprecated this method is replaced by validateTagNumberAndRetrieveActiveAsset(String)
     */
    @Deprecated
    protected boolean validateTagNumber(String tagNumber) {
        boolean result = true;
        // Getting a list of active assets.
        Collection<Asset> assets = getAssetService().findAssetsMatchingTagNumber(tagNumber);

        if (ObjectUtils.isNull(assets) || assets.isEmpty()) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_CAPITAL_ASSET_DOESNT_EXIST);
            result = false;
        }
        else {
            int activeAssets = assets.size();
            for (Asset asset : assets) {
                if (getAssetService().isAssetRetired(asset)) {
                    activeAssets--;
                }
            }
            if (activeAssets == 0) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_CAPITAL_ASSET_IS_RETIRED);
                result = false;
            }
            else if (activeAssets > 1) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_DUPLICATED_TAG_NUMBER);
                result = false;
            }
        }
        return result;
    }

    /**
     * validates that the specified asset tag number exists in only one active asset;
     * and if so returns that asset; otherwise returns null.
     *
     * @param tagNumber the specified asset tag number
     * @return the only active asset with the specified tag number, or null if not existing.
     */
    protected Asset validateTagNumberAndRetrieveActiveAsset(String tagNumber) {
        // getting a list of active assets.
        List<Asset> assets = getAssetService().findActiveAssetsMatchingTagNumber(tagNumber);

        if (ObjectUtils.isNull(assets) || assets.isEmpty()) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_CAPITAL_ASSET_DOESNT_EXIST);
            return null;
        }
        else if (assets.size() > 1) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_DUPLICATED_TAG_NUMBER);
            return null;
        }

        return assets.get(0);
    }

    /**
     * Validates the inventory date does not equals null
     *
     * @param inventoryDate
     * @return boolean
     */
    protected boolean validateInventoryDate(Timestamp inventoryDate) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE).getLabel();

        if (inventoryDate == null) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);
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
    protected boolean validateCampusCode(String campusCode, BarcodeInventoryErrorDetail detail) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE).getLabel();

        Campus campus;
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(KFSPropertyConstants.CAMPUS_CODE, detail.getCampusCode());
        campus = SpringContext.getBean(CampusService.class).getCampus(campusCode/*RICE_20_REFACTORME  fields */);

        if (ObjectUtils.isNull(campus)) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);
            result = false;
        }
        else if (!campus.isActive()) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INACTIVE_FIELD, label);
            result &= false;
        }
        return result;
    }


    /**
     * Validates that the existance of the building code is consistent with the asset type requirements.
     *
     * @param buildingCode
     * @param detail
     * @prarm asset
     * @return boolean
     *
     * @deprecated this method is replaced by validateBuildingCodeAndRoomNumber(BarcodeInventoryErrorDetail, Asset)
     */
    @Deprecated
    protected boolean validateBuildingCode(String buildingCode, BarcodeInventoryErrorDetail detail, Asset asset) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE).getLabel();
        //String description = asset.getCapitalAssetType().getCapitalAssetTypeDescription();
        String description = asset.getCapitalAssetTypeCode();

        // if the asset has empty building code, then the BCIE should too
        if (StringUtils.isBlank(asset.getBuildingCode())) {
            if (StringUtils.isNotBlank(buildingCode)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsKeyConstants.BarcodeInventory.ERROR_NOT_ALLOWED_FIELD, label, description);
                result &= false;
            }
        }
        // otherwise the BCIE should have a non-empty and existing active building code
        else {
            HashMap<String, Object> fields = new HashMap<String, Object>();
            fields.put(KFSPropertyConstants.CAMPUS_CODE, detail.getCampusCode());
            fields.put(KFSPropertyConstants.BUILDING_CODE, detail.getBuildingCode());
            Building building = getBusinessObjectService().findByPrimaryKey(Building.class, fields);

            if (StringUtils.isBlank(buildingCode)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsKeyConstants.BarcodeInventory.ERROR_REQUIRED_FIELD, label, description);
                result &= false;
            }
            else if (ObjectUtils.isNull(building)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);
                result &= false;
            }
            else if (!building.isActive()) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INACTIVE_FIELD, label);
                result &= false;
            }
        }

        return result;
    }

    /**
     * Validates that the existance of the building room number is consistent with the asset type requirements.
     *
     * @param roomNumber
     * @param detail
     * @prarm asset
     * @return boolean
     *
     * @deprecated this method is replaced by validateBuildingCodeAndRoomNumber(BarcodeInventoryErrorDetail, Asset)
     */
    @Deprecated
    protected boolean validateBuildingRoomNumber(String roomNumber, BarcodeInventoryErrorDetail detail, Asset asset) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER).getLabel();
        //String description = asset.getCapitalAssetType().getCapitalAssetTypeDescription();
        String description = asset.getCapitalAssetTypeCode();

        // if the asset has empty building room number, then the BCIE should too
        if (StringUtils.isBlank(asset.getBuildingRoomNumber())) {
            if (StringUtils.isNotBlank(roomNumber)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_NOT_ALLOWED_FIELD, label, description);
                result &= false;
            }
        }
        // otherwise the BCIE should have a non-empty and existing active building room number
        else {
            HashMap<String, Object> fields = new HashMap<String, Object>();
            fields.put(KFSPropertyConstants.CAMPUS_CODE, detail.getCampusCode());
            fields.put(KFSPropertyConstants.BUILDING_CODE, detail.getBuildingCode());
            fields.put(KFSPropertyConstants.BUILDING_ROOM_NUMBER, detail.getBuildingRoomNumber());
            Room room = getBusinessObjectService().findByPrimaryKey(Room.class, fields);

            if (StringUtils.isBlank(roomNumber)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_REQUIRED_FIELD, label, description);
                result &= false;
            }
            else if (ObjectUtils.isNull(room)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);
                result = false;
            }
            else if (!room.isActive()) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_INACTIVE_FIELD, label);
                result &= false;
            }
        }

        return result;
    }

    /**
     * Validates that the existance of the building code and room number is consistent with the asset type requirements.
     *
     * @param detail
     * @prarm asset
     * @return boolean
     */
    protected boolean validateBuildingCodeAndRoomNumber(BarcodeInventoryErrorDetail detail, Asset asset) {
        boolean result = true;

        String campusCode = detail.getCampusCode();
        String buildingCode = detail.getBuildingCode();
        String roomNumber = detail.getBuildingRoomNumber();
        String labelBuilding = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE).getLabel();
        String labelRoom = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER).getLabel();

        String assetTypeCode = asset.getCapitalAssetTypeCode();
        AssetType assetType = asset.getCapitalAssetType();

        // retrieve building
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(KFSPropertyConstants.CAMPUS_CODE, campusCode);
        fields.put(KFSPropertyConstants.BUILDING_CODE, buildingCode);
        Building building = getBusinessObjectService().findByPrimaryKey(Building.class, fields);

        // retrieve room
        fields.put(KFSPropertyConstants.BUILDING_ROOM_NUMBER, roomNumber);
        Room room = getBusinessObjectService().findByPrimaryKey(Room.class, fields);

        // if movingIndicator is true and requiredBuildingIndicator is false, then both building and room are required
        if (assetType.isMovingIndicator() && !assetType.isRequiredBuildingIndicator()) {
            // check building
            if (StringUtils.isBlank(buildingCode)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsKeyConstants.BarcodeInventory.ERROR_REQUIRED_FIELD, labelBuilding, assetTypeCode);
                result &= false;
            }
            else if (ObjectUtils.isNull(building)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, labelBuilding);
                result &= false;
            }
            else if (!building.isActive()) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INACTIVE_FIELD, labelBuilding);
                result &= false;
            }

            // check room
            if (StringUtils.isBlank(roomNumber)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_REQUIRED_FIELD, labelRoom, assetTypeCode);
                result &= false;
            }
            else if (ObjectUtils.isNull(room)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, labelRoom);
                result = false;
            }
            else if (!room.isActive()) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_INACTIVE_FIELD, labelRoom);
                result &= false;
            }
        }

        // if movingIndicator is false and requiredBuildingIndicator is true, then building is required while room is not allowed
        else if (!assetType.isMovingIndicator() && assetType.isRequiredBuildingIndicator()) {
            // check building
            if (StringUtils.isBlank(buildingCode)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsKeyConstants.BarcodeInventory.ERROR_REQUIRED_FIELD, labelBuilding, assetTypeCode);
                result &= false;
            }
            else if (ObjectUtils.isNull(building)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, labelBuilding);
                result &= false;
            }
            else if (!building.isActive()) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INACTIVE_FIELD, labelBuilding);
                result &= false;
            }

            // check room
            if (StringUtils.isNotBlank(roomNumber)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_NOT_ALLOWED_FIELD, labelRoom, assetTypeCode);
                result &= false;
            }
        }

        // if both movingIndicator and requiredBuildingIndicator are false, then neither building nor room is allowed
        else if (!assetType.isMovingIndicator() && !assetType.isRequiredBuildingIndicator()) {
            // check building
            if (StringUtils.isNotBlank(buildingCode)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsKeyConstants.BarcodeInventory.ERROR_NOT_ALLOWED_FIELD, labelBuilding, assetTypeCode);
                result &= false;
            }

            // check room
            if (StringUtils.isNotBlank(roomNumber)) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_NOT_ALLOWED_FIELD, labelRoom, assetTypeCode);
                result &= false;
            }
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
    protected boolean validateConditionCode(String conditionCode, BarcodeInventoryErrorDetail detail) {
        boolean result = true;
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(BarcodeInventoryErrorDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE).getLabel();

        AssetCondition condition;
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE, detail.getAssetConditionCode());
        condition = getBusinessObjectService().findByPrimaryKey(AssetCondition.class, fields);

        if (ObjectUtils.isNull(condition)) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INVALID_FIELD, label);
            result &= false;
        }
        else if (!condition.isActive()) {
            GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE, CamsKeyConstants.BarcodeInventory.ERROR_INACTIVE_FIELD, label);
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
    protected boolean validateTaggingLock(String tagNumber, String documentNumber) {
        boolean result = true;
        boolean isAssetLocked = false;
        String skipAssetLockValidation;

        // Getting system parameter in order to determine whether or not the asset locks will be ignored.
        if (getParameterService().parameterExists(BarcodeInventoryErrorDocument.class, CamsConstants.Parameters.BAR_CODE_ERROR_DOCUMENT_IGNORES_LOCKS)) {
            skipAssetLockValidation = getParameterService().getParameterValueAsString(BarcodeInventoryErrorDocument.class, CamsConstants.Parameters.BAR_CODE_ERROR_DOCUMENT_IGNORES_LOCKS);
        }
        else {
            LOG.warn("CAMS Parameter '" + CamsConstants.Parameters.BAR_CODE_ERROR_DOCUMENT_IGNORES_LOCKS + "' not found! - Setting default value to 'N' ");
            skipAssetLockValidation = CamsConstants.BarCodeInventoryError.BAR_CODE_ERROR_DOCUMENT_IGNORES_LOCKS_NO;
        }

        if (skipAssetLockValidation == null || StringUtils.isEmpty(skipAssetLockValidation) || StringUtils.equals(skipAssetLockValidation, CamsConstants.BarCodeInventoryError.BAR_CODE_ERROR_DOCUMENT_IGNORES_LOCKS_NO)) {
            // Getting a list of active assets.
            List<Asset> assets = getAssetService().findActiveAssetsMatchingTagNumber(tagNumber);
            if (assets.size() > 1) {
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_DUPLICATED_TAG_NUMBER);
                result = false;
            }
            else if ((assets.size() > 0)) {
                // isAssetLocked = getAssetService().isAssetLocked("", assets.get(0).getCapitalAssetNumber());
                List<Long> assetNumbers = new ArrayList<Long>();
                assetNumbers.add(assets.get(0).getCapitalAssetNumber());
                List<String> lockingDocNumbers = SpringContext.getBean(AssetLockService.class).getAssetLockingDocuments(assetNumbers, CamsConstants.DocumentTypeName.ASSET_BARCODE_INVENTORY_ERROR, documentNumber);
                if (lockingDocNumbers != null && !lockingDocNumbers.isEmpty()) {
                    for (String lockingDocNumber : lockingDocNumbers) {
                        // String lockingDocumentId = getAssetService().getLockingDocumentId(documentNumber,
                        // assets.get(0).getCapitalAssetNumber());
                        GlobalVariables.getMessageMap().putError(CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsKeyConstants.BarcodeInventory.ERROR_ASSET_LOCKED, lockingDocNumber);
                    }
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
    protected String getErrorMessages(String errorPath) {
        String message = "";
        String[] fields = { CamsPropertyConstants.BarcodeInventory.ASSET_TAG_NUMBER, CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE, CamsPropertyConstants.BarcodeInventory.CAMPUS_CODE, CamsPropertyConstants.BarcodeInventory.BUILDING_CODE, CamsPropertyConstants.BarcodeInventory.BUILDING_ROOM_NUMBER, CamsPropertyConstants.BarcodeInventory.ASSET_CONDITION_CODE };

        for (int i = 0; i < fields.length; i++) {
            String propertyName = errorPath + "." + fields[i];
            if (GlobalVariables.getMessageMap().doesPropertyHaveError(propertyName)) {
                for (Object errorMessage : GlobalVariables.getMessageMap().getMessages(propertyName)) {
                    String errorMsg = getKualiConfigurationService().getPropertyValueAsString(((ErrorMessage) errorMessage).getErrorKey());
                    message += ", " + MessageFormat.format(errorMsg, (Object[]) ((ErrorMessage) errorMessage).getMessageParameters());
                }
            }
        }
        return (StringUtils.isEmpty(message) ? message : message.substring(2));
    }


    /**
     * Deletes the asset locking error messages from the GlobalVariables.
     *
     * @return none
     */
    protected void deleteLockErrorMessages() {
        // Finding locking error messages
        List<ErrorMessage> el = new ArrayList<ErrorMessage>();

        if (GlobalVariables.getMessageMap().getMessages(KFSConstants.GLOBAL_ERRORS) == null) {
            return;
        }

        for (Iterator<ErrorMessage> iterator = GlobalVariables.getMessageMap().getMessages(KFSConstants.GLOBAL_ERRORS).iterator(); iterator.hasNext();) {
            ErrorMessage errorMessage = iterator.next();
            if (errorMessage.getErrorKey().equals(KFSKeyConstants.ERROR_MAINTENANCE_LOCKED)) {
                el.add(errorMessage);
            }
        }

        // Deleting asset locked error messages from global variable.
        for (ErrorMessage em : el) {
            GlobalVariables.getMessageMap().getMessages(KFSConstants.GLOBAL_ERRORS).remove(em);
        }
    }

    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    protected AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    protected DateTimeService getDateTimeService() {
        return SpringContext.getBean(DateTimeService.class);
    }

    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    protected AssetBarcodeInventoryLoadService getAssetBarcodeInventoryLoadService() {
        return SpringContext.getBean(AssetBarcodeInventoryLoadService.class);
    }
}
