package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class PurchaseOrderCapitalAssetSystem extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer capitalAssetSystemIdentifier;
	private String capitalAssetSystemDescription;
	private boolean capitalAssetNotReceivedCurrentFiscalYearIndicator;
	private String capitalAssetTypeCode;
	private String capitalAssetManufacturerName;
	private String capitalAssetModelDescription;
    private String capitalAssetNoteText;
    
	private PurchaseOrderItemCapitalAsset purchaseOrderItemCapitalAsset;
	private PurchaseOrderCapitalAssetLocation purchaseOrderCapitalAssetLocation;
	
	/**
	 * Default constructor.
	 */
	public PurchaseOrderCapitalAssetSystem() {

	}

	public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getCapitalAssetSystemDescription() {
        return capitalAssetSystemDescription;
    }

    public void setCapitalAssetSystemDescription(String capitalAssetSystemDescription) {
        this.capitalAssetSystemDescription = capitalAssetSystemDescription;
    }

    public boolean isCapitalAssetNotReceivedCurrentFiscalYearIndicator() {
        return capitalAssetNotReceivedCurrentFiscalYearIndicator;
    }

    public void setCapitalAssetNotReceivedCurrentFiscalYearIndicator(boolean capitalAssetNotReceivedCurrentFiscalYearIndicator) {
        this.capitalAssetNotReceivedCurrentFiscalYearIndicator = capitalAssetNotReceivedCurrentFiscalYearIndicator;
    }

    public String getCapitalAssetTypeCode() {
        return capitalAssetTypeCode;
    }

    public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
        this.capitalAssetTypeCode = capitalAssetTypeCode;
    }

    public String getCapitalAssetManufacturerName() {
        return capitalAssetManufacturerName;
    }

    public void setCapitalAssetManufacturerName(String capitalAssetManufacturerName) {
        this.capitalAssetManufacturerName = capitalAssetManufacturerName;
    }

    public String getCapitalAssetModelDescription() {
        return capitalAssetModelDescription;
    }

    public void setCapitalAssetModelDescription(String capitalAssetModelDescription) {
        this.capitalAssetModelDescription = capitalAssetModelDescription;
    }

    public PurchaseOrderItemCapitalAsset getPurchaseOrderItemCapitalAsset() {
        return purchaseOrderItemCapitalAsset;
    }

    public void setPurchaseOrderItemCapitalAsset(PurchaseOrderItemCapitalAsset purchaseOrderItemCapitalAsset) {
        this.purchaseOrderItemCapitalAsset = purchaseOrderItemCapitalAsset;
    }

    public PurchaseOrderCapitalAssetLocation getPurchaseOrderCapitalAssetLocation() {
        return purchaseOrderCapitalAssetLocation;
    }

    public void setPurchaseOrderCapitalAssetLocation(PurchaseOrderCapitalAssetLocation purchaseOrderCapitalAssetLocation) {
        this.purchaseOrderCapitalAssetLocation = purchaseOrderCapitalAssetLocation;
    }

    public Integer getCapitalAssetSystemIdentifier() {
        return capitalAssetSystemIdentifier;
    }

    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier) {
        this.capitalAssetSystemIdentifier = capitalAssetSystemIdentifier;
    }

    public String getCapitalAssetNoteText() {
        return capitalAssetNoteText;
    }

    public void setCapitalAssetNoteText(String capitalAssetNoteText) {
        this.capitalAssetNoteText = capitalAssetNoteText;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        if (this.capitalAssetSystemIdentifier != null) {
            m.put("capitalAssetSystemIdentifier", this.capitalAssetSystemIdentifier.toString());
        }
	    return m;
    }
}
