package edu.arizona.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.sys.KFSParameterKeyConstants;

public class ArchibusBuildings extends PersistableBusinessObjectBase {
	private String campusCode;
	private String buildingCode;
	private String buildingId;
	private String buildingName;
	private String buildingStreetAddress;
	private String buildingAddressCityName;
	private String buildingAddressStateCode;
	private String buildingAddressZipCode;
	private String buildingAddressCountryCode;
	private String active;
	private String routeCode;

	// equals() will compare if kfs building differs in any way to archibus
	// building
	public boolean equals(Building building) {
		String archBuild;
		String kfsBuild;

		if (!this.buildingStreetAddress.equalsIgnoreCase(building.getBuildingStreetAddress())) {
			return false;
		}
		if (!this.buildingAddressCityName.equalsIgnoreCase(building.getBuildingAddressCityName())) {
			return false;
		}
		if (!this.buildingAddressStateCode.equalsIgnoreCase(building.getBuildingAddressStateCode())) {
			return false;
		}
		if (!this.buildingAddressZipCode.equalsIgnoreCase(building.getBuildingAddressZipCode())) {
			return false;
		}
		if ("USA".equalsIgnoreCase(this.buildingAddressCountryCode) && "US".equalsIgnoreCase(building.getBuildingAddressCountryCode())) {
		} 
		else {
			if (!this.buildingAddressCountryCode.equalsIgnoreCase(building.getBuildingAddressCountryCode())) {
				return false;
			}
		}
		if (this.buildingName.length() > 40) {
			archBuild = this.buildingName.substring(0, 39);
		} 
		else {
			archBuild = this.buildingName;
		}
		if (building.getBuildingName().length() > 40) {
			kfsBuild = building.getBuildingName().substring(0, 39);
		} 
		else {
			kfsBuild = building.getBuildingName();
		}
		if (!archBuild.equalsIgnoreCase(kfsBuild)) {
			return false;
		}

		BuildingExtension buildingExt = (BuildingExtension) building.getExtension();
		if (StringUtils.isNotBlank(buildingExt.getRouteCode())) {
			kfsBuild = buildingExt.getRouteCode();
		} 
		else {
			kfsBuild = "";
		}
		if (KFSParameterKeyConstants.HYPHEN.equalsIgnoreCase(this.routeCode)) {
			archBuild = "";
		} 
		else {
			archBuild = this.routeCode;
		}
		if (!archBuild.equalsIgnoreCase(kfsBuild)) {
			return false;
		}
		if (this.active.equalsIgnoreCase("A") && building.isActive()) {
			return true;
		} 
		else {
			return false;
		}
	}

	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("campusCode", this.campusCode);
		m.put("buildingCode", this.buildingCode);
		return m;
	}

	public String getCampusCode() {
		return campusCode;
	}

	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}

	public String getBuildingCode() {
		return buildingCode;
	}

	public void setBuildingCode(String buildingCode) {
		this.buildingCode = buildingCode;
	}

	public String getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getBuildingStreetAddress() {
		return buildingStreetAddress;
	}

	public void setBuildingStreetAddress(String buildingStreetAddress) {
		this.buildingStreetAddress = buildingStreetAddress;
	}

	public String getBuildingAddressCityName() {
		return buildingAddressCityName;
	}

	public void setBuildingAddressCityName(String buildingAddressCityName) {
		this.buildingAddressCityName = buildingAddressCityName;
	}

	public String getBuildingAddressStateCode() {
		return buildingAddressStateCode;
	}

	public void setBuildingAddressStateCode(String buildingAddressStateCode) {
		this.buildingAddressStateCode = buildingAddressStateCode;
	}

	public String getBuildingAddressZipCode() {
		return buildingAddressZipCode;
	}

	public void setBuildingAddressZipCode(String buildingAddressZipCode) {
		this.buildingAddressZipCode = buildingAddressZipCode;
	}

	public String getBuildingAddressCountryCode() {
		return buildingAddressCountryCode;
	}

	public void setBuildingAddressCountryCode(String buildingAddressCountryCode) {
		this.buildingAddressCountryCode = buildingAddressCountryCode;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getRouteCode() {
		return routeCode;
	}

	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}

}
