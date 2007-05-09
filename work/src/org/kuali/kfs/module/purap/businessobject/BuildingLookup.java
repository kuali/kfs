package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.Building;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BuildingLookup extends PersistableBusinessObjectBase {

	private String campusCode;
	private String buildingCode;
	private String buildingOverrideCode;
	private String buildingLookupName;
	private String buildingLookupStreetAddress;
	private String buildingLookupAddressCityName;
	private String buildingLookupAddressStateCode;
	private String buildingLookupAddressZipCode;

    private Campus campus;
    private Building building;
    
	/**
	 * Default constructor.
	 */
	public BuildingLookup() {

	}

	/**
	 * Gets the campusCode attribute.
	 * 
	 * @return Returns the campusCode
	 * 
	 */
	public String getCampusCode() { 
		return campusCode;
	}

	/**
	 * Sets the campusCode attribute.
	 * 
	 * @param campusCode The campusCode to set.
	 * 
	 */
	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}


	/**
	 * Gets the buildingCode attribute.
	 * 
	 * @return Returns the buildingCode
	 * 
	 */
	public String getBuildingCode() { 
		return buildingCode;
	}

	/**
	 * Sets the buildingCode attribute.
	 * 
	 * @param buildingCode The buildingCode to set.
	 * 
	 */
	public void setBuildingCode(String buildingCode) {
		this.buildingCode = buildingCode;
	}


	/**
	 * Gets the buildingOverrideCode attribute.
	 * 
	 * @return Returns the buildingOverrideCode
	 * 
	 */
	public String getBuildingOverrideCode() { 
		return buildingOverrideCode;
	}

	/**
	 * Sets the buildingOverrideCode attribute.
	 * 
	 * @param buildingOverrideCode The buildingOverrideCode to set.
	 * 
	 */
	public void setBuildingOverrideCode(String buildingOverrideCode) {
		this.buildingOverrideCode = buildingOverrideCode;
	}


	/**
	 * Gets the buildingLookupName attribute.
	 * 
	 * @return Returns the buildingLookupName
	 * 
	 */
	public String getBuildingLookupName() { 
		return buildingLookupName;
	}

	/**
	 * Sets the buildingLookupName attribute.
	 * 
	 * @param buildingLookupName The buildingLookupName to set.
	 * 
	 */
	public void setBuildingLookupName(String buildingLookupName) {
		this.buildingLookupName = buildingLookupName;
	}


	/**
	 * Gets the buildingLookupStreetAddress attribute.
	 * 
	 * @return Returns the buildingLookupStreetAddress
	 * 
	 */
	public String getBuildingLookupStreetAddress() { 
		return buildingLookupStreetAddress;
	}

	/**
	 * Sets the buildingLookupStreetAddress attribute.
	 * 
	 * @param buildingLookupStreetAddress The buildingLookupStreetAddress to set.
	 * 
	 */
	public void setBuildingLookupStreetAddress(String buildingLookupStreetAddress) {
		this.buildingLookupStreetAddress = buildingLookupStreetAddress;
	}


	/**
	 * Gets the buildingLookupAddressCityName attribute.
	 * 
	 * @return Returns the buildingLookupAddressCityName
	 * 
	 */
	public String getBuildingLookupAddressCityName() { 
		return buildingLookupAddressCityName;
	}

	/**
	 * Sets the buildingLookupAddressCityName attribute.
	 * 
	 * @param buildingLookupAddressCityName The buildingLookupAddressCityName to set.
	 * 
	 */
	public void setBuildingLookupAddressCityName(String buildingLookupAddressCityName) {
		this.buildingLookupAddressCityName = buildingLookupAddressCityName;
	}


	/**
	 * Gets the buildingLookupAddressStateCode attribute.
	 * 
	 * @return Returns the buildingLookupAddressStateCode
	 * 
	 */
	public String getBuildingLookupAddressStateCode() { 
		return buildingLookupAddressStateCode;
	}

	/**
	 * Sets the buildingLookupAddressStateCode attribute.
	 * 
	 * @param buildingLookupAddressStateCode The buildingLookupAddressStateCode to set.
	 * 
	 */
	public void setBuildingLookupAddressStateCode(String buildingLookupAddressStateCode) {
		this.buildingLookupAddressStateCode = buildingLookupAddressStateCode;
	}


	/**
	 * Gets the buildingLookupAddressZipCode attribute.
	 * 
	 * @return Returns the buildingLookupAddressZipCode
	 * 
	 */
	public String getBuildingLookupAddressZipCode() { 
		return buildingLookupAddressZipCode;
	}

	/**
	 * Sets the buildingLookupAddressZipCode attribute.
	 * 
	 * @param buildingLookupAddressZipCode The buildingLookupAddressZipCode to set.
	 * 
	 */
	public void setBuildingLookupAddressZipCode(String buildingLookupAddressZipCode) {
		this.buildingLookupAddressZipCode = buildingLookupAddressZipCode;
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
     * Gets the building attribute. 
     * @return Returns the building.
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Sets the building attribute value.
     * @param building The building to set.
     * @deprecated
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("campusCode", this.campusCode);
        m.put("buildingCode", this.buildingCode);
        m.put("buildingOverrideCode", this.buildingOverrideCode);
	    return m;
    }
}
