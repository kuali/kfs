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
