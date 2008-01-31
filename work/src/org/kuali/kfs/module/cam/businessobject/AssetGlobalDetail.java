package org.kuali.module.cams.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.GlobalBusinessObjectDetailBase;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.bo.Room;
import org.kuali.kfs.bo.State;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetGlobalDetail extends GlobalBusinessObjectDetailBase {

	private String documentNumber;
	private Long capitalAssetNumber;
	private String campusCode;
	private String buildingCode;
	private String serialNumber;
	private String buildingRoomNumber;
	private String buildingSubRoomNumber;
	private String campusTagNumber;
	private String organizationInventoryName;
	private String organizationCapitalAssetTypeIdentifier;
    private String offCampusName;
    private String offCampusAddress;
	private String offCampusCityName;
	private String offCampusStateCode;
	private String offCampusZipCode;
    private String offCampusCountryCode;
    
    private Campus campus;
    private Building building;
    private Room buildingRoom;
    private State offCampusState;
   
	/**
	 * Default constructor.
	 */
	public AssetGlobalDetail() {

	}

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	/**
	 * Gets the capitalAssetNumber attribute.
	 * 
	 * @return Returns the capitalAssetNumber
	 * 
	 */
	public Long getCapitalAssetNumber() { 
		return capitalAssetNumber;
	}

	/**
	 * Sets the capitalAssetNumber attribute.
	 * 
	 * @param capitalAssetNumber The capitalAssetNumber to set.
	 * 
	 */
	public void setCapitalAssetNumber(Long capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
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
	 * Gets the serialNumber attribute.
	 * 
	 * @return Returns the serialNumber
	 * 
	 */
	public String getSerialNumber() { 
		return serialNumber;
	}

	/**
	 * Sets the serialNumber attribute.
	 * 
	 * @param serialNumber The serialNumber to set.
	 * 
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


	/**
	 * Gets the buildingRoomNumber attribute.
	 * 
	 * @return Returns the buildingRoomNumber
	 * 
	 */
	public String getBuildingRoomNumber() { 
		return buildingRoomNumber;
	}

	/**
	 * Sets the buildingRoomNumber attribute.
	 * 
	 * @param buildingRoomNumber The buildingRoomNumber to set.
	 * 
	 */
	public void setBuildingRoomNumber(String buildingRoomNumber) {
		this.buildingRoomNumber = buildingRoomNumber;
	}


	/**
	 * Gets the buildingSubRoomNumber attribute.
	 * 
	 * @return Returns the buildingSubRoomNumber
	 * 
	 */
	public String getBuildingSubRoomNumber() { 
		return buildingSubRoomNumber;
	}

	/**
	 * Sets the buildingSubRoomNumber attribute.
	 * 
	 * @param buildingSubRoomNumber The buildingSubRoomNumber to set.
	 * 
	 */
	public void setBuildingSubRoomNumber(String buildingSubRoomNumber) {
		this.buildingSubRoomNumber = buildingSubRoomNumber;
	}


	/**
	 * Gets the campusTagNumber attribute.
	 * 
	 * @return Returns the campusTagNumber
	 * 
	 */
	public String getCampusTagNumber() { 
		return campusTagNumber;
	}

	/**
	 * Sets the campusTagNumber attribute.
	 * 
	 * @param campusTagNumber The campusTagNumber to set.
	 * 
	 */
	public void setCampusTagNumber(String campusTagNumber) {
		this.campusTagNumber = campusTagNumber;
	}


	/**
	 * Gets the organizationInventoryName attribute.
	 * 
	 * @return Returns the organizationInventoryName
	 * 
	 */
	public String getOrganizationInventoryName() { 
		return organizationInventoryName;
	}

	/**
	 * Sets the organizationInventoryName attribute.
	 * 
	 * @param organizationInventoryName The organizationInventoryName to set.
	 * 
	 */
	public void setOrganizationInventoryName(String organizationInventoryName) {
		this.organizationInventoryName = organizationInventoryName;
	}


	/**
	 * Gets the organizationCapitalAssetTypeIdentifier attribute.
	 * 
	 * @return Returns the organizationCapitalAssetTypeIdentifier
	 * 
	 */
	public String getOrganizationCapitalAssetTypeIdentifier() { 
		return organizationCapitalAssetTypeIdentifier;
	}

	/**
	 * Sets the organizationCapitalAssetTypeIdentifier attribute.
	 * 
	 * @param organizationCapitalAssetTypeIdentifier The organizationCapitalAssetTypeIdentifier to set.
	 * 
	 */
	public void setOrganizationCapitalAssetTypeIdentifier(String organizationCapitalAssetTypeIdentifier) {
		this.organizationCapitalAssetTypeIdentifier = organizationCapitalAssetTypeIdentifier;
	}


	/**
	 * Gets the offCampusAddress attribute.
	 * 
	 * @return Returns the offCampusAddress
	 * 
	 */
	public String getOffCampusAddress() { 
		return offCampusAddress;
	}

	/**
	 * Sets the offCampusAddress attribute.
	 * 
	 * @param offCampusAddress The offCampusAddress to set.
	 * 
	 */
	public void setOffCampusAddress(String offCampusAddress) {
		this.offCampusAddress = offCampusAddress;
	}


	/**
	 * Gets the offCampusCityName attribute.
	 * 
	 * @return Returns the offCampusCityName
	 * 
	 */
	public String getOffCampusCityName() { 
		return offCampusCityName;
	}

	/**
	 * Sets the offCampusCityName attribute.
	 * 
	 * @param offCampusCityName The offCampusCityName to set.
	 * 
	 */
	public void setOffCampusCityName(String offCampusCityName) {
		this.offCampusCityName = offCampusCityName;
	}


	/**
	 * Gets the offCampusStateCode attribute.
	 * 
	 * @return Returns the offCampusStateCode
	 * 
	 */
	public String getOffCampusStateCode() { 
		return offCampusStateCode;
	}

	/**
	 * Sets the offCampusStateCode attribute.
	 * 
	 * @param offCampusStateCode The offCampusStateCode to set.
	 * 
	 */
	public void setOffCampusStateCode(String offCampusStateCode) {
		this.offCampusStateCode = offCampusStateCode;
	}


	/**
	 * Gets the offCampusZipCode attribute.
	 * 
	 * @return Returns the offCampusZipCode
	 * 
	 */
	public String getOffCampusZipCode() { 
		return offCampusZipCode;
	}

	/**
	 * Sets the offCampusZipCode attribute.
	 * 
	 * @param offCampusZipCode The offCampusZipCode to set.
	 * 
	 */
	public void setOffCampusZipCode(String offCampusZipCode) {
		this.offCampusZipCode = offCampusZipCode;
	}

	/**
     * Gets the offCampusCountryCode attribute. 
     * @return Returns the offCampusCountryCode.
     */
    public String getOffCampusCountryCode() {
        return offCampusCountryCode;
    }

    /**
     * Sets the offCampusCountryCode attribute value.
     * @param offCampusCountryCode The offCampusCountryCode to set.
     */
    public void setOffCampusCountryCode(String offCampusCountryCode) {
        this.offCampusCountryCode = offCampusCountryCode;
    }

    /**
     * Gets the offCampusName attribute. 
     * @return Returns the offCampusName.
     */
    public String getOffCampusName() {
        return offCampusName;
    }

    /**
     * Sets the offCampusName attribute value.
     * @param offCampusName The offCampusName to set.
     */
    public void setOffCampusName(String offCampusName) {
        this.offCampusName = offCampusName;
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
     * Gets the buildingRoom attribute. 
     * @return Returns the buildingRoom.
     */
    public Room getBuildingRoom() {
        return buildingRoom;
    }

    /**
     * Sets the buildingRoom attribute value.
     * @param buildingRoom The buildingRoom to set.
     * @deprecated
     */
    public void setBuildingRoom(Room buildingRoom) {
        this.buildingRoom = buildingRoom;
    }

    /**
     * Gets the offCampusState attribute. 
     * @return Returns the offCampusState.
     */
    public State getOffCampusState() {
        return offCampusState;
    }

    /**
     * Sets the offCampusState attribute value.
     * @param offCampusState The offCampusState to set.
     * @deprecated
     */
    public void setOffCampusState(State offCampusState) {
        this.offCampusState = offCampusState;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        return m;
    }

}
