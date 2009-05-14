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
package org.kuali.kfs.module.purap.businessobject;


import java.util.LinkedHashMap;

import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public abstract class PurchasingCapitalAssetLocationBase extends PersistableBusinessObjectBase implements CapitalAssetLocation {

    private Integer capitalAssetLocationIdentifier;
    private Integer capitalAssetSystemIdentifier;
    private KualiDecimal itemQuantity;
    private String campusCode;
    private boolean offCampusIndicator;
    private String buildingCode;
    private String buildingRoomNumber;
    private String capitalAssetLine1Address;
    private String capitalAssetCityName;
    private String capitalAssetStateCode;
    private String capitalAssetPostalCode;
    private String capitalAssetCountryCode;
    
    private CampusParameter campus;

    public PurchasingCapitalAssetLocationBase() {
        super();
        if(GlobalVariables.getUserSession()!=null && GlobalVariables.getUserSession().getPerson()!=null){
            Person user = GlobalVariables.getUserSession().getPerson();
            this.campusCode = user.getCampusCode();
        }
    }

    public Integer getCapitalAssetSystemIdentifier() {
        return capitalAssetSystemIdentifier;
    }

    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier) {
        this.capitalAssetSystemIdentifier = capitalAssetSystemIdentifier;
    }

    public Integer getCapitalAssetLocationIdentifier() {
        return capitalAssetLocationIdentifier;
    }

    public void setCapitalAssetLocationIdentifier(Integer capitalAssetLocationIdentifier) {
        this.capitalAssetLocationIdentifier = capitalAssetLocationIdentifier;
    }

    public KualiDecimal getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(KualiDecimal itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public boolean isOffCampusIndicator() {
        return offCampusIndicator;
    }

    public void setOffCampusIndicator(boolean offCampusIndicator) {
        this.offCampusIndicator = offCampusIndicator;
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

    public String getCapitalAssetLine1Address() {
        return capitalAssetLine1Address;
    }

    public void setCapitalAssetLine1Address(String capitalAssetLine1Address) {
        this.capitalAssetLine1Address = capitalAssetLine1Address;
    }

    public String getCapitalAssetCityName() {
        return capitalAssetCityName;
    }

    public void setCapitalAssetCityName(String capitalAssetCityName) {
        this.capitalAssetCityName = capitalAssetCityName;
    }

    public String getCapitalAssetStateCode() {
        return capitalAssetStateCode;
    }

    public void setCapitalAssetStateCode(String capitalAssetStateCode) {
        this.capitalAssetStateCode = capitalAssetStateCode;
    }

    public String getCapitalAssetPostalCode() {
        return capitalAssetPostalCode;
    }

    public void setCapitalAssetPostalCode(String capitalAssetPostalCode) {
        this.capitalAssetPostalCode = capitalAssetPostalCode;
    }

    public String getCapitalAssetCountryCode() {
        return capitalAssetCountryCode;
    }

    public void setCapitalAssetCountryCode(String capitalAssetCountryCode) {
        this.capitalAssetCountryCode = capitalAssetCountryCode;
    }

    public CampusParameter getCampus() { 
        return campus;
    }

    public void setCampus(CampusParameter campus) {
    	this.campus = campus;
    }
    
    public void templateBuilding(Building building) {
        if(ObjectUtils.isNotNull(building)) {
            this.setOffCampusIndicator(false);
            this.setBuildingCode(building.getBuildingCode());
            this.setCampusCode(building.getCampusCode());
            this.setCapitalAssetLine1Address(building.getBuildingStreetAddress());
            this.setCapitalAssetCityName(building.getBuildingAddressCityName());
            this.setCapitalAssetStateCode(building.getBuildingAddressStateCode());
            this.setCapitalAssetPostalCode(building.getBuildingAddressZipCode());
            this.setCapitalAssetCountryCode(building.getBuildingAddressCountryCode());
        }
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();	    
        if (this.capitalAssetSystemIdentifier != null) {
            m.put("capitalAssetSystemIdentifier", this.capitalAssetSystemIdentifier.toString());
        }
        if (this.capitalAssetLocationIdentifier != null) {
            m.put("capitalAssetLocationIdentifier", this.capitalAssetLocationIdentifier.toString());
        }
        return m;
    }

}
