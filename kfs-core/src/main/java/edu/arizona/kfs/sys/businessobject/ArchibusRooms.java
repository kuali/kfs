package edu.arizona.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.sys.KFSParameterKeyConstants;

public class ArchibusRooms extends PersistableBusinessObjectBase {
	private String buildingCode;
	private String buildingRoomNumber;
	private String buildingRoomType;
	private String buildingRoomDepartment;
	private String buildingRoomDescription;

	private ArchibusBuildings archibusBuildings;

	public boolean equals(Room room) {
		String kfsRoomValue = null;
		String archRoomValue = null;

		if (this.buildingRoomDepartment.equalsIgnoreCase(KFSParameterKeyConstants.HYPHEN) || StringUtils.isBlank(this.buildingRoomDepartment)) {
			archRoomValue = "";
		} 
		else {
			archRoomValue = this.buildingRoomDepartment;
		}
		if (StringUtils.isBlank(room.getBuildingRoomDepartment())) {
			kfsRoomValue = "";
		} 
		else {
			kfsRoomValue = room.getBuildingRoomDepartment();
		}
		if (!archRoomValue.equalsIgnoreCase(kfsRoomValue)) {
			return false;
		}

		if (this.buildingRoomType.equalsIgnoreCase(KFSParameterKeyConstants.HYPHEN) || StringUtils.isBlank(this.buildingRoomType)) {
			archRoomValue = "";
		} 
		else {
			archRoomValue = this.buildingRoomType;
		}
		if (StringUtils.isBlank(room.getBuildingRoomType())) {
			kfsRoomValue = "";
		} 
		else {
			kfsRoomValue = room.getBuildingRoomType();
		}
		if (!archRoomValue.equalsIgnoreCase(kfsRoomValue)) {
			return false;
		}

		if (this.buildingRoomDescription.equalsIgnoreCase(KFSParameterKeyConstants.HYPHEN) || StringUtils.isBlank(this.buildingRoomDescription)) {
			archRoomValue = "";
		} 
		else {
			archRoomValue = this.buildingRoomDescription;
		}
		if (StringUtils.isBlank(room.getBuildingRoomDescription())) {
			kfsRoomValue = "";
		}
		else {
			kfsRoomValue = room.getBuildingRoomDescription();
		}
		if (!archRoomValue.equalsIgnoreCase(kfsRoomValue)) {
			return false;
		}

		if (room.isActive()) {
			return true;
		} else {
			return false;
		}
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

	public String getBuildingRoomType() {
		return buildingRoomType;
	}

	public void setBuildingRoomType(String buildingRoomType) {
		this.buildingRoomType = buildingRoomType;
	}

	public String getBuildingRoomDepartment() {
		return buildingRoomDepartment;
	}

	public void setBuildingRoomDepartment(String buildingRoomDepartment) {
		this.buildingRoomDepartment = buildingRoomDepartment;
	}

	public String getBuildingRoomDescription() {
		return buildingRoomDescription;
	}

	public void setBuildingRoomDescription(String buildingRoomDescription) {
		this.buildingRoomDescription = buildingRoomDescription;
	}

	public ArchibusBuildings getArchibusBuildings() {
		return archibusBuildings;
	}

	public void setArchibusBuildings(ArchibusBuildings archibusBuildings) {
		this.archibusBuildings = archibusBuildings;
	}

	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("roomNumber", this.buildingRoomDepartment);
		m.put("buildingCode", this.buildingCode);
		return m;
	}

}
