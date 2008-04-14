package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class RequisitionCapitalAssetLocation extends PersistableBusinessObjectBase {

	private Integer requisitionIdentifier;
	private Integer capitalAssetSystemNumber;
	private Integer capitalAssetLocationNumber;
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

    private Campus campus;

	/**
	 * Default constructor.
	 */
	public RequisitionCapitalAssetLocation() {

	}

	public Integer getRequisitionIdentifier() {
        return requisitionIdentifier;
    }

    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }

    public Integer getCapitalAssetSystemNumber() {
        return capitalAssetSystemNumber;
    }

    public void setCapitalAssetSystemNumber(Integer capitalAssetSystemNumber) {
        this.capitalAssetSystemNumber = capitalAssetSystemNumber;
    }

    public Integer getCapitalAssetLocationNumber() {
        return capitalAssetLocationNumber;
    }

    public void setCapitalAssetLocationNumber(Integer capitalAssetLocationNumber) {
        this.capitalAssetLocationNumber = capitalAssetLocationNumber;
    }

    public KualiDecimal getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(KualiDecimal itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public boolean isOffCampusIndicator() {
        return offCampusIndicator;
    }

    public void setOffCampusIndicator(boolean offCampusIndicator) {
        this.offCampusIndicator = offCampusIndicator;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getBuildingRoomNumber() {
        return buildingRoomNumber;
    }

    public void setBuildingRoomNumber(String buildingRoomNumber) {
        this.buildingRoomNumber = buildingRoomNumber;
    }

    public String getCapitalAssetLine1Address() {
        return capitalAssetLine1Address;
    }

    public void setCapitalAssetLine1Address(String capitalAssetLine1Address) {
        this.capitalAssetLine1Address = capitalAssetLine1Address;
    }

    public String getCapitalAssetCityName() {
        return capitalAssetCityName;
    }

    public void setCapitalAssetCityName(String capitalAssetCityName) {
        this.capitalAssetCityName = capitalAssetCityName;
    }

    public String getCapitalAssetStateCode() {
        return capitalAssetStateCode;
    }

    public void setCapitalAssetStateCode(String capitalAssetStateCode) {
        this.capitalAssetStateCode = capitalAssetStateCode;
    }

    public String getCapitalAssetPostalCode() {
        return capitalAssetPostalCode;
    }

    public void setCapitalAssetPostalCode(String capitalAssetPostalCode) {
        this.capitalAssetPostalCode = capitalAssetPostalCode;
    }

    public String getCapitalAssetCountryCode() {
        return capitalAssetCountryCode;
    }

    public void setCapitalAssetCountryCode(String capitalAssetCountryCode) {
        this.capitalAssetCountryCode = capitalAssetCountryCode;
    }

    /**
	 * Gets the campus attribute.
	 * 
	 * @return Returns the campus
	 * 
	 */
	public Campus getCampus() { 
		return campus;
	}

	/**
	 * Sets the campus attribute.
	 * 
	 * @param campus The campus to set.
	 * @deprecated
	 */
	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.requisitionIdentifier != null) {
            m.put("requisitionIdentifier", this.requisitionIdentifier.toString());
        }
        if (this.capitalAssetSystemNumber != null) {
            m.put("capitalAssetSystemNumber", this.capitalAssetSystemNumber.toString());
        }
        if (this.capitalAssetLocationNumber != null) {
            m.put("capitalAssetLocationNumber", this.capitalAssetLocationNumber.toString());
        }
	    return m;
    }
}
