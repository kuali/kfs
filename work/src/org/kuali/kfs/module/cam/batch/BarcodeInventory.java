/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.batch;

import java.io.Serializable;
import java.sql.Date;

/**
 * Object representation of collector xml input.
 */
public class BarcodeInventory implements Serializable {
    private String campusTagNumber;
    private String inventoryScannedCode;
    private Date   createDate;
    private String campusCode;
    private String buildingCode;
    private String buildingRoomNumber;
    private String buildingSubRoomNumber;
    private String conditionCode;
    private String inventoryStatusCode;


    /**
     * Constructs a CollectorBatch
     */
    public BarcodeInventory() {
    }
    
    public String getCampusTagNumber() {
        return campusTagNumber;
    }

    public void setCampusTagNumber(String campusTagNumber) {
        this.campusTagNumber = campusTagNumber;
    }

    public String getInventoryScannedCode() {
        return inventoryScannedCode;
    }

    public void setInventoryScannedCode(String inventoryScannedCode) {
        this.inventoryScannedCode = inventoryScannedCode;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getBuildingRoomNumber() {
        return buildingRoomNumber;
    }

    public void setBuildingRoomNumber(String buildingRoomNumber) {
        this.buildingRoomNumber = buildingRoomNumber;
    }

    public String getBuildingSubRoomNumber() {
        return buildingSubRoomNumber;
    }

    public void setBuildingSubRoomNumber(String buildingSubRoomNumber) {
        this.buildingSubRoomNumber = buildingSubRoomNumber;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }


    public String getInventoryStatusCode() {
        return inventoryStatusCode;
    }

    public void setInventoryStatusCode(String inventoryStatusCode) {
        this.inventoryStatusCode = inventoryStatusCode;
    }
}
