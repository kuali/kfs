/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.businessobject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.campus.CampusEbo;

/**
 * Class for the barcode inventory error detail
 */
public class BarcodeInventoryErrorDetail extends PersistableBusinessObjectBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorDetail.class);

    private String documentNumber;
    private Long uploadRowNumber;
    private String errorCorrectionStatusCode;
    private String correctorUniversalIdentifier;
    private Timestamp inventoryCorrectionTimestamp;
    private String assetTagNumber;
    private boolean uploadScanIndicator;
    private Timestamp uploadScanTimestamp;
    private String campusCode;
    private String buildingCode;
    private String buildingRoomNumber;
    private String buildingSubRoomNumber;
    private String assetConditionCode;

    // References
    private CampusEbo campus;
    private Room buildingRoom;
    private Building building;
    private AssetCondition condition;

    // error description. This field is not being saved in any table.
    private String errorDescription;

    private boolean rowSelected;

    /**
     * Default constructor.
     */
    public BarcodeInventoryErrorDetail() {

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the uploadRowNumber attribute.
     * 
     * @return Returns the uploadRowNumber
     */
    public Long getUploadRowNumber() {
        return uploadRowNumber;
    }

    /**
     * Sets the uploadRowNumber attribute.
     * 
     * @param uploadRowNumber The uploadRowNumber to set.
     */
    public void setUploadRowNumber(Long uploadRowNumber) {
        this.uploadRowNumber = uploadRowNumber;
    }


    /**
     * Gets the errorCorrectionStatusCode attribute.
     * 
     * @return Returns the errorCorrectionStatusCode
     */
    public String getErrorCorrectionStatusCode() {
        return errorCorrectionStatusCode;
    }

    /**
     * Sets the errorCorrectionStatusCode attribute.
     * 
     * @param errorCorrectionStatusCode The errorCorrectionStatusCode to set.
     */
    public void setErrorCorrectionStatusCode(String errorCorrectionStatusCode) {
        this.errorCorrectionStatusCode = errorCorrectionStatusCode;
    }


    /**
     * Gets the correctorUniversalIdentifier attribute.
     * 
     * @return Returns the correctorUniversalIdentifier
     */
    public String getCorrectorUniversalIdentifier() {
        return correctorUniversalIdentifier;
    }

    /**
     * Sets the correctorUniversalIdentifier attribute.
     * 
     * @param correctorUniversalIdentifier The correctorUniversalIdentifier to set.
     */
    public void setCorrectorUniversalIdentifier(String correctorUniversalIdentifier) {
        this.correctorUniversalIdentifier = correctorUniversalIdentifier;
    }


    /**
     * Gets the inventoryCorrectionTimestamp attribute.
     * 
     * @return Returns the inventoryCorrectionTimestamp
     */
    public Timestamp getInventoryCorrectionTimestamp() {
        return inventoryCorrectionTimestamp;
    }

    /**
     * Sets the inventoryCorrectionTimestamp attribute.
     * 
     * @param inventoryCorrectionTimestamp The inventoryCorrectionTimestamp to set.
     */
    public void setInventoryCorrectionTimestamp(Timestamp inventoryCorrectionTimestamp) {
        this.inventoryCorrectionTimestamp = inventoryCorrectionTimestamp;
    }


    /**
     * Gets the assetTagNumber attribute.
     * 
     * @return Returns the assetTagNumber
     */
    public String getAssetTagNumber() {
        return assetTagNumber;
    }

    /**
     * Sets the assetTagNumber attribute.
     * 
     * @param assetTagNumber The assetTagNumber to set.
     */
    public void setAssetTagNumber(String assetTagNumber) {
        this.assetTagNumber = assetTagNumber;
    }


    /**
     * Gets the uploadScanIndicator attribute.
     * 
     * @return Returns the uploadScanIndicator
     */
    public boolean isUploadScanIndicator() {
        return uploadScanIndicator;
    }

    /**
     * Sets the uploadScanIndicator attribute.
     * 
     * @param uploadScanIndicator The uploadScanIndicator to set.
     */
    public void setUploadScanIndicator(boolean uploadScanIndicator) {
        this.uploadScanIndicator = uploadScanIndicator;
    }


    /**
     * Gets the uploadScanTimestamp attribute.
     * 
     * @return Returns the uploadScanTimestamp
     */
    public Timestamp getUploadScanTimestamp() {
        return uploadScanTimestamp;
    }

    /**
     * Sets the uploadScanTimestamp attribute.
     * 
     * @param uploadScanTimestamp The uploadScanTimestamp to set.
     */
    public void setUploadScanTimestamp(Timestamp uploadScanTimestamp) {
        this.uploadScanTimestamp = uploadScanTimestamp;
    }


    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute.
     * 
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }


    /**
     * Gets the buildingCode attribute.
     * 
     * @return Returns the buildingCode
     */
    public String getBuildingCode() {
        return buildingCode;
    }

    /**
     * Sets the buildingCode attribute.
     * 
     * @param buildingCode The buildingCode to set.
     */
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }


    /**
     * Gets the buildingRoomNumber attribute.
     * 
     * @return Returns the buildingRoomNumber
     */
    public String getBuildingRoomNumber() {
        return buildingRoomNumber;
    }

    /**
     * Sets the buildingRoomNumber attribute.
     * 
     * @param buildingRoomNumber The buildingRoomNumber to set.
     */
    public void setBuildingRoomNumber(String buildingRoomNumber) {
        this.buildingRoomNumber = buildingRoomNumber;
    }


    /**
     * Gets the buildingSubRoomNumber attribute.
     * 
     * @return Returns the buildingSubRoomNumber
     */
    public String getBuildingSubRoomNumber() {
        return buildingSubRoomNumber;
    }

    /**
     * Sets the buildingSubRoomNumber attribute.
     * 
     * @param buildingSubRoomNumber The buildingSubRoomNumber to set.
     */
    public void setBuildingSubRoomNumber(String buildingSubRoomNumber) {
        this.buildingSubRoomNumber = buildingSubRoomNumber;
    }


    /**
     * Gets the assetConditionCode attribute.
     * 
     * @return Returns the assetConditionCode
     */
    public String getAssetConditionCode() {
        return assetConditionCode;
    }

    /**
     * Sets the assetConditionCode attribute.
     * 
     * @param assetConditionCode The assetConditionCode to set.
     */
    public void setAssetConditionCode(String assetConditionCode) {
        this.assetConditionCode = assetConditionCode;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.uploadRowNumber != null) {
            m.put("uploadRowNumber", this.uploadRowNumber.toString());
        }

        m.put("assetTagNumber", this.assetTagNumber);
        m.put("buildingRoomNumber", this.buildingRoomNumber);
        m.put("errorCorrectionStatusCode", this.getErrorCorrectionStatusCode());
        return m;
    }

    /**
     * Gets the campus code reference object
     * 
     * @return Campus
     */
    public CampusEbo getCampus() {
        if ( StringUtils.isBlank(campusCode) ) {
            campus = null;
        } else {
            if ( campus == null || !StringUtils.equals( campus.getCode(),campusCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CampusEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, campusCode);
                    campus = moduleService.getExternalizableBusinessObject(CampusEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        
        return campus;
     }

    /**
     * sets the campus code reference object
     * 
     * @param campus
     */
    public void setCampus(CampusEbo campus) {
        this.campus = campus;
    }

    /**
     * Gets the building room reference object
     * 
     * @return Room
     */
    public Room getBuildingRoom() {
        return buildingRoom;
    }

    /**
     * Sets the bulding room reference object
     * 
     * @param buildingRoom
     */
    public void setBuildingRoom(Room buildingRoom) {
        this.buildingRoom = buildingRoom;
    }

    /**
     * Gets the building reference object
     * 
     * @return Building
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Sets the building reference object
     * 
     * @param building
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * Gets the condition code reference object
     * 
     * @return AssetCondition
     */
    public AssetCondition getCondition() {
        return condition;
    }

    /**
     * sets the condition code reference object
     * 
     * @param condition
     */
    public void setCondition(AssetCondition condition) {
        this.condition = condition;
    }

    /**
     * Gets the error description of an asset
     * 
     * @return String
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * sets the field that will hold the error description of an asset
     * 
     * @param errorDescription
     */
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
