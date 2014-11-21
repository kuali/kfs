/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.businessobject;


import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public abstract class PurchasingCapitalAssetLocationBase extends PersistableBusinessObjectBase implements CapitalAssetLocation {

    protected Integer capitalAssetLocationIdentifier;
    protected Integer capitalAssetSystemIdentifier;
    protected KualiDecimal itemQuantity;
    protected String campusCode;
    protected boolean offCampusIndicator;
    protected String buildingCode;
    protected String buildingRoomNumber;
    protected String capitalAssetLine1Address;
    protected String capitalAssetCityName;
    protected String capitalAssetStateCode;
    protected String capitalAssetPostalCode;
    protected String capitalAssetCountryCode;

    protected CampusParameter campus;

    public PurchasingCapitalAssetLocationBase() {
        super();
        if(GlobalVariables.getUserSession()!=null && GlobalVariables.getUserSession().getPerson()!=null){
            Person user = GlobalVariables.getUserSession().getPerson();
            this.campusCode = user.getCampusCode();
        }
    }

    @Override
    public Integer getCapitalAssetSystemIdentifier() {
        return capitalAssetSystemIdentifier;
    }

    @Override
    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier) {
        this.capitalAssetSystemIdentifier = capitalAssetSystemIdentifier;
    }

    @Override
    public Integer getCapitalAssetLocationIdentifier() {
        return capitalAssetLocationIdentifier;
    }

    @Override
    public void setCapitalAssetLocationIdentifier(Integer capitalAssetLocationIdentifier) {
        this.capitalAssetLocationIdentifier = capitalAssetLocationIdentifier;
    }

    @Override
    public KualiDecimal getItemQuantity() {
        return itemQuantity;
    }

    @Override
    public void setItemQuantity(KualiDecimal itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    @Override
    public String getCampusCode() {
        return campusCode;
    }

    @Override
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    @Override
    public boolean isOffCampusIndicator() {
        return offCampusIndicator;
    }

    @Override
    public void setOffCampusIndicator(boolean offCampusIndicator) {
        this.offCampusIndicator = offCampusIndicator;
    }

    @Override
    public String getBuildingCode() {
        return buildingCode;
    }

    @Override
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    @Override
    public String getBuildingRoomNumber() {
        return buildingRoomNumber;
    }

    @Override
    public void setBuildingRoomNumber(String buildingRoomNumber) {
        this.buildingRoomNumber = buildingRoomNumber;
    }

    @Override
    public String getCapitalAssetLine1Address() {
        return capitalAssetLine1Address;
    }

    @Override
    public void setCapitalAssetLine1Address(String capitalAssetLine1Address) {
        this.capitalAssetLine1Address = capitalAssetLine1Address;
    }

    @Override
    public String getCapitalAssetCityName() {
        return capitalAssetCityName;
    }

    @Override
    public void setCapitalAssetCityName(String capitalAssetCityName) {
        this.capitalAssetCityName = capitalAssetCityName;
    }

    @Override
    public String getCapitalAssetStateCode() {
        return capitalAssetStateCode;
    }

    @Override
    public void setCapitalAssetStateCode(String capitalAssetStateCode) {
        this.capitalAssetStateCode = capitalAssetStateCode;
    }

    @Override
    public String getCapitalAssetPostalCode() {
        return capitalAssetPostalCode;
    }

    @Override
    public void setCapitalAssetPostalCode(String capitalAssetPostalCode) {
        this.capitalAssetPostalCode = capitalAssetPostalCode;
    }

    @Override
    public String getCapitalAssetCountryCode() {
        return capitalAssetCountryCode;
    }

    @Override
    public void setCapitalAssetCountryCode(String capitalAssetCountryCode) {
        this.capitalAssetCountryCode = capitalAssetCountryCode;
    }

    @Override
    public CampusParameter getCampus() {
        return campus;
    }

    @Override
    public void setCampus(CampusParameter campus) {
    	this.campus = campus;
    }

    @Override
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

}
