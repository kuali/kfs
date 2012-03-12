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
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;



/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class PurchaseOrderCapitalAssetSystem extends PurchasingCapitalAssetSystemBase {

	private String documentNumber;
	
	/**
	 * Default constructor.
	 */
	public PurchaseOrderCapitalAssetSystem() {
       super();
	}
	
	public PurchaseOrderCapitalAssetSystem(CapitalAssetSystem originalSystem) {
	    super();
	    if (originalSystem != null) {
            this.setCapitalAssetSystemDescription(originalSystem.getCapitalAssetSystemDescription());
            this.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(originalSystem.isCapitalAssetNotReceivedCurrentFiscalYearIndicator());
            this.setCapitalAssetTypeCode(originalSystem.getCapitalAssetTypeCode());
            this.setCapitalAssetManufacturerName(originalSystem.getCapitalAssetManufacturerName());
            this.setCapitalAssetModelDescription(originalSystem.getCapitalAssetModelDescription());
            this.setCapitalAssetNoteText(originalSystem.getCapitalAssetNoteText());
            populatePurchaseOrderItemCapitalAssets(originalSystem);
            populateCapitalAssetLocations(originalSystem);
            this.setCapitalAssetCountAssetNumber(originalSystem.getCapitalAssetCountAssetNumber());
        }
	}
	
	private void populatePurchaseOrderItemCapitalAssets(CapitalAssetSystem originalSystem) {
	    for (ItemCapitalAsset reqAsset : originalSystem.getItemCapitalAssets()) {
	        PurchaseOrderItemCapitalAsset poAsset = new PurchaseOrderItemCapitalAsset(reqAsset.getCapitalAssetNumber());
	        this.getItemCapitalAssets().add(poAsset);
	    }
	}
	
	private void populateCapitalAssetLocations(CapitalAssetSystem originalSystem) {
	    for (CapitalAssetLocation reqLocation : originalSystem.getCapitalAssetLocations()) {
	        PurchaseOrderCapitalAssetLocation poLocation = new PurchaseOrderCapitalAssetLocation();
	        poLocation.setItemQuantity(reqLocation.getItemQuantity());
	        poLocation.setCampusCode(reqLocation.getCampusCode());
	        poLocation.setOffCampusIndicator(reqLocation.isOffCampusIndicator());
	        poLocation.setBuildingCode(reqLocation.getBuildingCode());
	        poLocation.setBuildingRoomNumber(reqLocation.getBuildingRoomNumber());
	        poLocation.setCapitalAssetLine1Address(reqLocation.getCapitalAssetLine1Address());
	        poLocation.setCapitalAssetCityName(reqLocation.getCapitalAssetCityName());
	        poLocation.setCapitalAssetStateCode(reqLocation.getCapitalAssetStateCode());
	        poLocation.setCapitalAssetPostalCode(reqLocation.getCapitalAssetPostalCode());
	        poLocation.setCapitalAssetCountryCode(reqLocation.getCapitalAssetCountryCode());
	        this.getCapitalAssetLocations().add(poLocation);
	    }
	}
	
	public String getDocumentNumber() {
        return documentNumber;
    }

    
    public Class getCapitalAssetLocationClass() {
        return PurchaseOrderCapitalAssetLocation.class;
    }

    @Override
    public Class getItemCapitalAssetClass() {
        return PurchaseOrderItemCapitalAsset.class;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
    @Override
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.documentNumber != null) {
            m.put("documentNumber", this.documentNumber.toString());
        }
        if (this.getCapitalAssetSystemIdentifier() != null) {
            m.put("capitalAssetSystemIdentifier", this.getCapitalAssetSystemIdentifier().toString());
        }
        return m;
    }
}
