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
