package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class CapitalAssetInformation extends PersistableBusinessObjectBase{
	
	private String documentNumber;
    private String campusCode;
    private String buildingCode;
    private String buildingRoomNumber;
    private String buildingSubroomNumber;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private Integer capitalAssetNumber;
    private String capitalAssetTagNumber;
    private Integer capitalAssetQuantity;
    private String capitalAssetTypeCode;
    private String capitalAssetManufacturerName;
    private String capitalAssetDescription;
    private String capitalAssetManufacturerModelNumber;
    private String capitalAssetSerialNumber;
	
	protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("documentNumber", getDocumentNumber());

        return m;
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

	public String getBuildingSubroomNumber() {
		return buildingSubroomNumber;
	}

	public void setBuildingSubroomNumber(String buildingSubroomNumber) {
		this.buildingSubroomNumber = buildingSubroomNumber;
	}

	public String getCampusCode() {
		return campusCode;
	}

	public void setCampusCode(String campusCode) {
		campusCode = campusCode;
	}

	public String getCapitalAssetDescription() {
		return capitalAssetDescription;
	}

	public void setCapitalAssetDescription(String capitalAssetDescription) {
		this.capitalAssetDescription = capitalAssetDescription;
	}

	public String getCapitalAssetManufacturerModelNumber() {
		return capitalAssetManufacturerModelNumber;
	}

	public void setCapitalAssetManufacturerModelNumber(
			String capitalAssetManufacturerModelNumber) {
		this.capitalAssetManufacturerModelNumber = capitalAssetManufacturerModelNumber;
	}

	public String getCapitalAssetManufacturerName() {
		return capitalAssetManufacturerName;
	}

	public void setCapitalAssetManufacturerName(String capitalAssetManufacturerName) {
		this.capitalAssetManufacturerName = capitalAssetManufacturerName;
	}

	public Integer getCapitalAssetNumber() {
		return capitalAssetNumber;
	}

	public void setCapitalAssetNumber(Integer capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
	}

	public Integer getCapitalAssetQuantity() {
		return capitalAssetQuantity;
	}

	public void setCapitalAssetQuantity(Integer capitalAssetQuantity) {
		this.capitalAssetQuantity = capitalAssetQuantity;
	}

	public String getCapitalAssetSerialNumber() {
		return capitalAssetSerialNumber;
	}

	public void setCapitalAssetSerialNumber(String capitalAssetSerialNumber) {
		this.capitalAssetSerialNumber = capitalAssetSerialNumber;
	}

	public String getCapitalAssetTagNumber() {
		return capitalAssetTagNumber;
	}

	public void setCapitalAssetTagNumber(String capitalAssetTagNumber) {
		this.capitalAssetTagNumber = capitalAssetTagNumber;
	}

	public String getCapitalAssetTypeCode() {
		return capitalAssetTypeCode;
	}

	public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
		this.capitalAssetTypeCode = capitalAssetTypeCode;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public Integer getVendorDetailAssignedIdentifier() {
		return vendorDetailAssignedIdentifier;
	}

	public void setVendorDetailAssignedIdentifier(
			Integer vendorDetailedAssignedIdentifier) {
		this.vendorDetailAssignedIdentifier = vendorDetailedAssignedIdentifier;
	}

	public Integer getVendorHeaderGeneratedIdentifier() {
		return vendorHeaderGeneratedIdentifier;
	}

	public void setVendorHeaderGeneratedIdentifier(
			Integer vendorHeaderGeneratedIdentifier) {
		this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
	}

	
	
}
