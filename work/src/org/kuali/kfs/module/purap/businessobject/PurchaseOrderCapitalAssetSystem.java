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

    @Override
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
    protected LinkedHashMap toStringMapper() {
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
