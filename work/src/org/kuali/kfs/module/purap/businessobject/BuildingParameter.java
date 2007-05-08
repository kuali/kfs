package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.Building;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BuildingParameter extends PersistableBusinessObjectBase {

	private String campusCode;
	private String buildingCode;
	private String buildingOverrideCode;
	private String buildingOverrideName;
	private String buildingOverrideStreetAddress;
	private String buildingOverrideAddressCityName;
	private String buildingOverrideAddressStateCode;
	private String buildingOverrideAddressZipCode;

    private Campus campus;
    private Building building;
    
	/**
	 * Default constructor.
	 */
	public BuildingParameter() {

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
	 * Gets the buildingOverrideName attribute.
	 * 
	 * @return Returns the buildingOverrideName
	 * 
	 */
	public String getBuildingOverrideName() { 
		return buildingOverrideName;
	}

	/**
	 * Sets the buildingOverrideName attribute.
	 * 
	 * @param buildingOverrideName The buildingOverrideName to set.
	 * 
	 */
	public void setBuildingOverrideName(String buildingOverrideName) {
		this.buildingOverrideName = buildingOverrideName;
	}


	/**
	 * Gets the buildingOverrideStreetAddress attribute.
	 * 
	 * @return Returns the buildingOverrideStreetAddress
	 * 
	 */
	public String getBuildingOverrideStreetAddress() { 
		return buildingOverrideStreetAddress;
	}

	/**
	 * Sets the buildingOverrideStreetAddress attribute.
	 * 
	 * @param buildingOverrideStreetAddress The buildingOverrideStreetAddress to set.
	 * 
	 */
	public void setBuildingOverrideStreetAddress(String buildingOverrideStreetAddress) {
		this.buildingOverrideStreetAddress = buildingOverrideStreetAddress;
	}


	/**
	 * Gets the buildingOverrideAddressCityName attribute.
	 * 
	 * @return Returns the buildingOverrideAddressCityName
	 * 
	 */
	public String getBuildingOverrideAddressCityName() { 
		return buildingOverrideAddressCityName;
	}

	/**
	 * Sets the buildingOverrideAddressCityName attribute.
	 * 
	 * @param buildingOverrideAddressCityName The buildingOverrideAddressCityName to set.
	 * 
	 */
	public void setBuildingOverrideAddressCityName(String buildingOverrideAddressCityName) {
		this.buildingOverrideAddressCityName = buildingOverrideAddressCityName;
	}


	/**
	 * Gets the buildingOverrideAddressStateCode attribute.
	 * 
	 * @return Returns the buildingOverrideAddressStateCode
	 * 
	 */
	public String getBuildingOverrideAddressStateCode() { 
		return buildingOverrideAddressStateCode;
	}

	/**
	 * Sets the buildingOverrideAddressStateCode attribute.
	 * 
	 * @param buildingOverrideAddressStateCode The buildingOverrideAddressStateCode to set.
	 * 
	 */
	public void setBuildingOverrideAddressStateCode(String buildingOverrideAddressStateCode) {
		this.buildingOverrideAddressStateCode = buildingOverrideAddressStateCode;
	}


	/**
	 * Gets the buildingOverrideAddressZipCode attribute.
	 * 
	 * @return Returns the buildingOverrideAddressZipCode
	 * 
	 */
	public String getBuildingOverrideAddressZipCode() { 
		return buildingOverrideAddressZipCode;
	}

	/**
	 * Sets the buildingOverrideAddressZipCode attribute.
	 * 
	 * @param buildingOverrideAddressZipCode The buildingOverrideAddressZipCode to set.
	 * 
	 */
	public void setBuildingOverrideAddressZipCode(String buildingOverrideAddressZipCode) {
		this.buildingOverrideAddressZipCode = buildingOverrideAddressZipCode;
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
