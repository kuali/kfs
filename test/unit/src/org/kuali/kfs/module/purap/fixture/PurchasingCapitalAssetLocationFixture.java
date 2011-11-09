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
