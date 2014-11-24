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
package org.kuali.kfs.module.purap.fixture;

import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum PurchasingCapitalAssetLocationFixture {
  
    LOCATION_BASIC ( 
            null,  //capitalAssetSystemIdentifier
            new KualiDecimal(1), //itemQuantity
            "BL", //campusCode
            false, //offCampusIndicator
            "HTC", //buildingCode
            "2", //buildingRoomNumber
            "123 Hagadorn Rd", //capitalAssetLine1Address
            "East Lansing", //capitalAssetCityName
            "MI", //capitalAssetStateCode
            "48823", //capitalAssetPostalCode
            "US" //capitalAssetCountryCode
            ),                       
            ;
    
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
    
    private PurchasingCapitalAssetLocationFixture (Integer capitalAssetSystemIdentifier, KualiDecimal itemQuantity, String campusCode, boolean offCampusIndicator, String buildingCode, String buildingRoomNumber, String capitalAssetLine1Address, String capitalAssetCityName, String capitalAssetStateCode, String capitalAssetPostalCode, String capitalAssetCountryCode) {
        this.capitalAssetSystemIdentifier = capitalAssetSystemIdentifier;
        this.itemQuantity = itemQuantity;
        this.campusCode = campusCode;
        this.offCampusIndicator = offCampusIndicator;
        this.buildingCode = buildingCode;
        this.buildingRoomNumber = buildingRoomNumber;
        this.capitalAssetLine1Address = capitalAssetLine1Address;
        this.capitalAssetCityName = capitalAssetCityName;
        this.capitalAssetStateCode = capitalAssetStateCode;
        this.capitalAssetPostalCode = capitalAssetPostalCode;
        this.capitalAssetCountryCode = capitalAssetCountryCode;
    }
    
    public CapitalAssetLocation createPurchasingCapitalAssetLocation(Class clazz) {
        CapitalAssetLocation location = null;
        try {
            location = (CapitalAssetLocation) clazz.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException("location creation failed. class = " + clazz);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("location creation failed. class = " + clazz);
        }
        location.setCapitalAssetSystemIdentifier(capitalAssetSystemIdentifier);
        location.setItemQuantity(itemQuantity);
        location.setCampusCode(campusCode);
        location.setOffCampusIndicator(offCampusIndicator);
        location.setBuildingCode(buildingCode);
        location.setBuildingRoomNumber(buildingRoomNumber);
        location.setCapitalAssetLine1Address(capitalAssetLine1Address);
        location.setCapitalAssetCityName(capitalAssetCityName);
        location.setCapitalAssetStateCode(capitalAssetStateCode);
        location.setCapitalAssetPostalCode(capitalAssetPostalCode);
        location.setCapitalAssetCountryCode(capitalAssetCountryCode);
        
        return location;
    }
}
