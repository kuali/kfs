package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class RequisitionCapitalAssetSystem extends PersistableBusinessObjectBase {

    private Integer capitalAssetSystemIdentifier;
	private Integer requisitionIdentifier;
	private String capitalAssetSystemDescription;
	private boolean capitalAssetNotReceivedCurrentFiscalYearIndicator;
	private String capitalAssetTypeCode;
	private String capitalAssetManufacturerName;
	private String capitalAssetModelDescription;
	private String capitalAssetNoteText;
	
	private List<RequisitionItemCapitalAsset> requisitionItemCapitalAssets;
	private List<RequisitionCapitalAssetLocation> requisitionCapitalAssetLocations;
	
	/**
	 * Default constructor.
	 */
	public RequisitionCapitalAssetSystem() {

	}

	public Integer getRequisitionIdentifier() {
        return requisitionIdentifier;
    }

    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
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

    public List<RequisitionItemCapitalAsset> getRequisitionItemCapitalAssets() {
        return requisitionItemCapitalAssets;
    }

    public void setRequisitionItemCapitalAssets(List<RequisitionItemCapitalAsset> requisitionItemCapitalAssets) {
        this.requisitionItemCapitalAssets = requisitionItemCapitalAssets;
    }

    public List<RequisitionCapitalAssetLocation> getRequisitionCapitalAssetLocations() {
        return requisitionCapitalAssetLocations;
    }

    public void setRequisitionCapitalAssetLocations(List<RequisitionCapitalAssetLocation> requisitionCapitalAssetLocations) {
        this.requisitionCapitalAssetLocations = requisitionCapitalAssetLocations;
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
        if (this.requisitionIdentifier != null) {
            m.put("requisitionIdentifier", this.requisitionIdentifier.toString());
        }
        if (this.capitalAssetSystemIdentifier != null) {
            m.put("capitalAssetSystemIdentifier", this.capitalAssetSystemIdentifier.toString());
        }
	    return m;
    }

}
