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
package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class CapitalAssetInformationDetail  extends PersistableBusinessObjectBase {

    private  String documentNumber;
    private  int itemLineNumber;
    private  String campusCode;
    private  String buildingCode;
    private  String buildingRoomNumber;
    private  String buildingSubroomNumber;
    private  String capitalAssetTagNumber;
    private  String capitalAssetSerialNumber;
    
    protected LinkedHashMap toStringMapper() {
            LinkedHashMap m = new LinkedHashMap();
            m.put("documentNumber", this.documentNumber);
            return m;
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

    public String getBuildingSubroomNumber() {
        return buildingSubroomNumber;
    }

    public void setBuildingSubroomNumber(String buildingSubroomNumber) {
        this.buildingSubroomNumber = buildingSubroomNumber;
    }

    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public String getCapitalAssetSerialNumber() {
        return capitalAssetSerialNumber;
    }

    public void setCapitalAssetSerialNumber(String capitalAssetSerialNumber) {
        this.capitalAssetSerialNumber = capitalAssetSerialNumber;
    }

    public String getCapitalAssetTagNumber() {
        return capitalAssetTagNumber;
    }

    public void setCapitalAssetTagNumber(String capitalAssetTagNumber) {
        this.capitalAssetTagNumber = capitalAssetTagNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public int getItemLineNumber() {
        return itemLineNumber;
    }

    public void setItemLineNumber(int itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

}
