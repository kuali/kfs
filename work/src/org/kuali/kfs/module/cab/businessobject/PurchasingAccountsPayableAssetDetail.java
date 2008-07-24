package org.kuali.kfs.module.cab.businessobject;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;

public class PurchasingAccountsPayableAssetDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer accountsPayableLineItemIdentifier; 
    private Integer capitalAssetBuilderLineNumber;
    private Long capitalAssetNumber;
    private boolean newAssetIndicator;
    private String campusTagNumber;
    private String serialNumber;
    private String campusCode;
    private String buildingCode;
    private String buildingRoomNumber;
    private String buildingSubRoomNumber;
    
    private Asset asset;
    private Campus campus;
    private Building building;
    private Room buildingRoom;
    
  
    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    

    /**
     * Gets the accountsPayableLineItemIdentifier attribute. 
     * @return Returns the accountsPayableLineItemIdentifier.
     */
    public Integer getAccountsPayableLineItemIdentifier() {
        return accountsPayableLineItemIdentifier;
    }

    /**
     * Sets the accountsPayableLineItemIdentifier attribute value.
     * @param accountsPayableLineItemIdentifier The accountsPayableLineItemIdentifier to set.
     */
    public void setAccountsPayableLineItemIdentifier(Integer accountsPayableLineItemIdentifier) {
        this.accountsPayableLineItemIdentifier = accountsPayableLineItemIdentifier;
    }

    public Integer getCapitalAssetBuilderLineNumber() {
        return capitalAssetBuilderLineNumber;
    }

    /**
     * Sets the capitalAssetBuilderLineNumber attribute.
     * 
     * @param capitalAssetBuilderLineNumber The capitalAssetBuilderLineNumber to set.
     * 
     */
    public void setCapitalAssetBuilderLineNumber(Integer capitalAssetBuilderLineNumber) {
        this.capitalAssetBuilderLineNumber = capitalAssetBuilderLineNumber;
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
     * Gets the serialNumber attribute.
     * 
     * @return Returns the serialNumber
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serialNumber attribute.
     * 
     * @param serialNumber The serialNumber to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    /**
     * Gets the newAssetIndicator attribute.
     * 
     * @return Returns the newAssetIndicator
     * 
     */
    public boolean isNewAssetIndicator() { 
        return newAssetIndicator;
    }

    /**
     * Sets the newAssetIndicator attribute.
     * 
     * @param active The newAssetIndicator to set.
     * 
     */
    public void setNewAssetIndicator(boolean newAssetIndicator) {
        this.newAssetIndicator = newAssetIndicator;
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
     * Gets the asset attribute. 
     * @return Returns the asset.
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute value.
     * @param asset The asset to set.
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
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
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	 
	    m.put("documentNumber", this.documentNumber);
	    m.put("accountsPayableLineItemIdentifier", this.accountsPayableLineItemIdentifier);
        m.put("capitalAssetBuilderLineNumber", this.capitalAssetBuilderLineNumber);
        m.put("capitalAssetNumber", this.capitalAssetNumber);
        return m;
    }
}
