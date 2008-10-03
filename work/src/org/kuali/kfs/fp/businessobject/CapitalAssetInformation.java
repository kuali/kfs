package org.kuali.kfs.fp.businessobject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cam.CapitalAssetManagementAsset;
import org.kuali.kfs.integration.cam.CapitalAssetManagementAssetType;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;

public class CapitalAssetInformation extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String campusCode;
    private String buildingCode;
    private String buildingRoomNumber;
    private String buildingSubRoomNumber;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorNumber;
    private Long capitalAssetNumber;
    private String capitalAssetTagNumber;
    private Integer capitalAssetQuantity;
    private String capitalAssetTypeCode;
    private String capitalAssetManufacturerName;
    private String capitalAssetDescription;
    private String capitalAssetManufacturerModelNumber;
    private String capitalAssetSerialNumber;

    private CapitalAssetManagementAsset capitalAssetManagementAsset;
    private CapitalAssetManagementAssetType capitalAssetManagementAssetType;

    private Campus campus;
    private Building building;
    private Room room;
    private VendorDetail vendorDetail;

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

    public String getBuildingSubRoomNumber() {
        return buildingSubRoomNumber;
    }

    public void setBuildingSubRoomNumber(String buildingSubRoomNumber) {
        this.buildingSubRoomNumber = buildingSubRoomNumber;
    }

    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
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

    public void setCapitalAssetManufacturerModelNumber(String capitalAssetManufacturerModelNumber) {
        this.capitalAssetManufacturerModelNumber = capitalAssetManufacturerModelNumber;
    }

    public String getCapitalAssetManufacturerName() {
        return capitalAssetManufacturerName;
    }

    public void setCapitalAssetManufacturerName(String capitalAssetManufacturerName) {
        this.capitalAssetManufacturerName = capitalAssetManufacturerName;
    }

    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    public void setCapitalAssetNumber(Long capitalAssetNumber) {
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

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailedAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailedAssignedIdentifier;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }
    
    /**
     * Gets the vendorNumber attribute. 
     * @return Returns the vendorNumber.
     */
    public String getVendorNumber() {
        if(this.vendorHeaderGeneratedIdentifier != null && this.vendorDetailAssignedIdentifier != null) {
            vendorNumber = this.vendorHeaderGeneratedIdentifier + "-" + this.vendorDetailAssignedIdentifier;
        }
            
        return vendorNumber;
    }

    /**
     * Sets the vendorNumber attribute value.
     * @param vendorNumber The vendorNumber to set.
     */
    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    /**
     * Gets the capitalAssetManagementAsset attribute.
     * 
     * @return Returns the capitalAssetManagementAsset.
     */
    public CapitalAssetManagementAsset getCapitalAssetManagementAsset() {
        capitalAssetManagementAsset = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CapitalAssetManagementAsset.class).retrieveExternalizableBusinessObjectIfNecessary(this, capitalAssetManagementAsset, KFSPropertyConstants.CAPITAL_ASSET_MANAGEMENT_ASSET);
        return capitalAssetManagementAsset;
    }

    /**
     * Sets the capitalAssetManagementAsset attribute value.
     * 
     * @param capitalAssetManagementAsset The capitalAssetManagementAsset to set.
     */
    public void setCapitalAssetManagementAsset(CapitalAssetManagementAsset capitalAssetManagementAsset) {
        this.capitalAssetManagementAsset = capitalAssetManagementAsset;
    }

    /**
     * Gets the capitalAssetManagementAssetType attribute.
     * 
     * @return Returns the capitalAssetManagementAssetType.
     */
    public CapitalAssetManagementAssetType getCapitalAssetManagementAssetType() {
        capitalAssetManagementAssetType = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CapitalAssetManagementAssetType.class).retrieveExternalizableBusinessObjectIfNecessary(this, capitalAssetManagementAssetType, KFSPropertyConstants.CAPITAL_ASSET_MANAGEMENT_ASSET_TYPE);
        return capitalAssetManagementAssetType;
    }

    /**
     * Sets the capitalAssetManagementAssetType attribute value.
     * 
     * @param capitalAssetManagementAssetType The capitalAssetManagementAssetType to set.
     */
    @Deprecated
    public void setCapitalAssetManagementAssetType(CapitalAssetManagementAssetType capitalAssetManagementAssetType) {
        this.capitalAssetManagementAssetType = capitalAssetManagementAssetType;
    }

    /**
     * Gets the building attribute.
     * 
     * @return Returns the building.
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Sets the building attribute value.
     * 
     * @param building The building to set.
     */
    @Deprecated
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * Gets the vendorDetail attribute.
     * 
     * @return Returns the vendorDetail.
     */
    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    /**
     * Sets the vendorDetail attribute value.
     * 
     * @param vendorDetail The vendorDetail to set.
     */
    @Deprecated
    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    /**
     * Gets the room attribute.
     * 
     * @return Returns the room.
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Sets the room attribute value.
     * 
     * @param room The room to set.
     */
    @Deprecated
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Gets the campus attribute.
     * 
     * @return Returns the campus.
     */
    public Campus getCampus() {
        return campus;
    }

    /**
     * Sets the campus attribute value.
     * 
     * @param campus The campus to set.
     */
    @Deprecated
    public void setCampus(Campus campus) {
        this.campus = campus;
    }
    
    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     * 
     * @return Map a map with the primitive field names as the key and the primitive values as the map value.
     */
    public Map<String, Object> getValuesMap() {
        Map<String, Object> simpleValues = new HashMap<String, Object>();

        simpleValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        simpleValues.put(KFSPropertyConstants.CAMPUS_CODE, this.getCampusCode());
        simpleValues.put(KFSPropertyConstants.BUILDING_CODE, this.getBuildingCode());
        simpleValues.put(KFSPropertyConstants.BUILDING_ROOM_NUMBER, this.getBuildingRoomNumber());
        
        simpleValues.put(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, this.getVendorHeaderGeneratedIdentifier());
        simpleValues.put(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, this.getVendorDetailAssignedIdentifier());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, this.getCapitalAssetNumber());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE, this.getCapitalAssetTypeCode());        

        return simpleValues;
    }
}
