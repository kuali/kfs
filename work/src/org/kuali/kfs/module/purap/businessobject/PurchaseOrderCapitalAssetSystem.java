package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.purap.CapitalAssetLocation;
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
	
	public PurchaseOrderCapitalAssetSystem(RequisitionCapitalAssetSystem reqSystem) {
	    super();
	    this.setCapitalAssetSystemDescription(reqSystem.getCapitalAssetSystemDescription());
	    this.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(reqSystem.isCapitalAssetNotReceivedCurrentFiscalYearIndicator());
	    this.setCapitalAssetTypeCode(reqSystem.getCapitalAssetTypeCode());
	    this.setCapitalAssetManufacturerName(reqSystem.getCapitalAssetManufacturerName());
	    this.setCapitalAssetModelDescription(reqSystem.getCapitalAssetModelDescription());
	    this.setCapitalAssetNoteText(reqSystem.getCapitalAssetNoteText());
	    populatePurchaseOrderItemCapitalAssets(reqSystem);
	    populateCapitalAssetLocations(reqSystem);
	    this.setCapitalAssetCountAssetNumber(reqSystem.getCapitalAssetCountAssetNumber());
	}

	private void populatePurchaseOrderItemCapitalAssets(RequisitionCapitalAssetSystem reqSystem) {
	    for (ItemCapitalAsset reqAsset : reqSystem.getItemCapitalAssets()) {
	        PurchaseOrderItemCapitalAsset poAsset = new PurchaseOrderItemCapitalAsset(reqAsset.getCapitalAssetNumber());
	        this.getItemCapitalAssets().add(poAsset);
	    }
	}
	
	private void populateCapitalAssetLocations(RequisitionCapitalAssetSystem reqSystem) {
	    for (CapitalAssetLocation reqLocation : reqSystem.getCapitalAssetLocations()) {
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
