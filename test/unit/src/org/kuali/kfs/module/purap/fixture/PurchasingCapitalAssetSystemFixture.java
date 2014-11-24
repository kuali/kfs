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
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetLocation;

public enum PurchasingCapitalAssetSystemFixture {
  
    ASSET_SYSTEM_BASIC_1 ( 
            "Asset 1", //capitalAssetSystemDescription
            false, //capitalAssetNotReceivedCurrentFiscalYearIndicator
            "TYPE1", //capitalAssetTypeCode
            "manufacturer", //capitalAssetManufacturerName
            "description", //capitalAssetModelDescription
            "note text", //capitalAssetNoteText
            new PurchasingCapitalAssetLocationFixture[] {PurchasingCapitalAssetLocationFixture.LOCATION_BASIC} //location multifixtures
            ),       
    ASSET_SYSTEM_BASIC_2(
            "Asset 2", // capitalAssetSystemDescription
            false, // capitalAssetNotReceivedCurrentFiscalYearIndicator
            "TYPE2", // capitalAssetTypeCode
            "manufacturer", // capitalAssetManufacturerName
            "description", // capitalAssetModelDescription
            "note text", // capitalAssetNoteText
            new PurchasingCapitalAssetLocationFixture[] {PurchasingCapitalAssetLocationFixture.LOCATION_BASIC} //location multifixtures
    ),             
            ;
    
    private String capitalAssetSystemDescription;
    private boolean capitalAssetNotReceivedCurrentFiscalYearIndicator;
    private String capitalAssetTypeCode;
    private String capitalAssetManufacturerName;
    private String capitalAssetModelDescription;
    private String capitalAssetNoteText;
    private PurchasingCapitalAssetLocationFixture[] locations;
    
    private PurchasingCapitalAssetSystemFixture (String capitalAssetSystemDescription, boolean capitalAssetNotReceivedCurrentFiscalYearIndicator, String capitalAssetTypeCode, String capitalAssetManufacturerName, String capitalAssetModelDescription, String capitalAssetNoteText, PurchasingCapitalAssetLocationFixture[] locations) {
        this.capitalAssetSystemDescription = capitalAssetSystemDescription;
        this.capitalAssetNotReceivedCurrentFiscalYearIndicator = capitalAssetNotReceivedCurrentFiscalYearIndicator;
        this.capitalAssetTypeCode = capitalAssetTypeCode;
        this.capitalAssetManufacturerName = capitalAssetManufacturerName;
        this.capitalAssetModelDescription = capitalAssetModelDescription;
        this.capitalAssetNoteText = capitalAssetNoteText;
        this.locations = locations;
    }
    
    public CapitalAssetSystem createPurchasingCapitalAssetSystem(Class clazz) {
        CapitalAssetSystem assetSystem = null;
        try {
            assetSystem = (CapitalAssetSystem) clazz.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException("asset system creation failed. class = " + clazz);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("asset system creation failed. class = " + clazz);
        }

        assetSystem.setCapitalAssetSystemDescription(capitalAssetSystemDescription);
        assetSystem.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(capitalAssetNotReceivedCurrentFiscalYearIndicator);
        assetSystem.setCapitalAssetTypeCode(capitalAssetTypeCode);
        assetSystem.setCapitalAssetManufacturerName(capitalAssetManufacturerName);
        assetSystem.setCapitalAssetModelDescription(capitalAssetModelDescription);
        assetSystem.setCapitalAssetNoteText(capitalAssetNoteText);
        
        for (PurchasingCapitalAssetLocationFixture locationFixture : locations) {
            CapitalAssetLocation location = locationFixture.createPurchasingCapitalAssetLocation(RequisitionCapitalAssetLocation.class);
            assetSystem.getCapitalAssetLocations().add(location);
        }
        return assetSystem;
    }
}
