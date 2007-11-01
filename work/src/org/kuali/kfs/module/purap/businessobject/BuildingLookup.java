/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.Building;

/**
 * Building Lookup Business Object. Defines searchable attributes for buliding lookup.
 */
public class BuildingLookup extends PersistableBusinessObjectBase {

    private String campusCode;
    private String buildingCode;
    private String buildingOverrideCode;
    private String buildingLookupName;
    private String buildingLookupStreetAddress;
    private String buildingLookupAddressCityName;
    private String buildingLookupAddressStateCode;
    private String buildingLookupAddressZipCode;

    private Campus campus;
    private Building building;

    /**
     * Default constructor.
     */
    public BuildingLookup() {

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

    public String getBuildingOverrideCode() {
        return buildingOverrideCode;
    }

    public void setBuildingOverrideCode(String buildingOverrideCode) {
        this.buildingOverrideCode = buildingOverrideCode;
    }

    public String getBuildingLookupName() {
        return buildingLookupName;
    }

    public void setBuildingLookupName(String buildingLookupName) {
        this.buildingLookupName = buildingLookupName;
    }

    public String getBuildingLookupStreetAddress() {
        return buildingLookupStreetAddress;
    }

    public void setBuildingLookupStreetAddress(String buildingLookupStreetAddress) {
        this.buildingLookupStreetAddress = buildingLookupStreetAddress;
    }

    public String getBuildingLookupAddressCityName() {
        return buildingLookupAddressCityName;
    }

    public void setBuildingLookupAddressCityName(String buildingLookupAddressCityName) {
        this.buildingLookupAddressCityName = buildingLookupAddressCityName;
    }

    public String getBuildingLookupAddressStateCode() {
        return buildingLookupAddressStateCode;
    }

    public void setBuildingLookupAddressStateCode(String buildingLookupAddressStateCode) {
        this.buildingLookupAddressStateCode = buildingLookupAddressStateCode;
    }

    public String getBuildingLookupAddressZipCode() {
        return buildingLookupAddressZipCode;
    }

    public void setBuildingLookupAddressZipCode(String buildingLookupAddressZipCode) {
        this.buildingLookupAddressZipCode = buildingLookupAddressZipCode;
    }

    public Campus getCampus() {
        return campus;
    }

    /**
     * @deprecated
     */
    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    public Building getBuilding() {
        return building;
    }

    /**
     * @deprecated
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("campusCode", this.campusCode);
        m.put("buildingCode", this.buildingCode);
        m.put("buildingOverrideCode", this.buildingOverrideCode);
        return m;
    }
}
