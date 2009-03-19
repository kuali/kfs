package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.bo.PostalCode;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.rice.kns.bo.State;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.PostalCodeService;
import org.kuali.rice.kns.service.StateService;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.GlobalBusinessObjectDetailBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;

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
    private String organizationAssetTypeIdentifier;
    private String offCampusName;
    private String offCampusAddress;
    private String offCampusCityName;
    private String offCampusStateCode;
    private String offCampusZipCode;
    private String offCampusCountryCode;
    private String governmentTagNumber;
    private String nationalStockNumber;

    private Asset asset;
    private Campus campus;
    private Building building;
    private Room buildingRoom;
    private State offCampusState;
    private Country offCampusCountry;
    private PostalCode postalZipCode;


    private Integer locationQuantity;
    private String representativeUniversalIdentifier;
    private String capitalAssetTypeCode;
    private String capitalAssetDescription;
    private String manufacturerName;
    private String organizationText;
    private String manufacturerModelNumber;
    private KualiDecimal separateSourceAmount; 

    // Non persistent
    private List<AssetGlobalDetail> assetGlobalUniqueDetails;
    private Person assetRepresentative;
    private transient String hiddenFieldForError;
    
    public Person getAssetRepresentative() {
        assetRepresentative = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(representativeUniversalIdentifier, assetRepresentative);
        return assetRepresentative;
    }

    public void setAssetRepresentative(Person assetRepresentative) {
        this.assetRepresentative = assetRepresentative;
    }

    /**
     * Gets the hiddenFieldForError attribute. 
     * @return Returns the hiddenFieldForError.
     */
    public String getHiddenFieldForError() {
        return hiddenFieldForError;
    }

    /**
     * Sets the hiddenFieldForError attribute value.
     * @param hiddenFieldForError The hiddenFieldForError to set.
     */
    public void setHiddenFieldForError(String hiddenFieldForError) {
        this.hiddenFieldForError = hiddenFieldForError;
    }

    /**
     * Gets the locationQuantity attribute.
     * 
     * @return Returns the locationQuantity.
     */
    public Integer getLocationQuantity() {
        return locationQuantity;
    }

    /**
     * Sets the locationQuantity attribute value.
     * 
     * @param locationQuantity The locationQuantity to set.
     */
    public void setLocationQuantity(Integer locationQuantity) {
        this.locationQuantity = locationQuantity;
    }

    /**
     * Default constructor.
     */
    public AssetGlobalDetail() {
        assetGlobalUniqueDetails = new TypedArrayList(AssetGlobalDetail.class);

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute.
     * 
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute.
     * 
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }


    /**
     * Gets the buildingCode attribute.
     * 
     * @return Returns the buildingCode
     */
    public String getBuildingCode() {
        return buildingCode;
    }

    /**
     * Sets the buildingCode attribute.
     * 
     * @param buildingCode The buildingCode to set.
     */
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
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
     * Gets the buildingRoomNumber attribute.
     * 
     * @return Returns the buildingRoomNumber
     */
    public String getBuildingRoomNumber() {
        return buildingRoomNumber;
    }

    /**
     * Sets the buildingRoomNumber attribute.
     * 
     * @param buildingRoomNumber The buildingRoomNumber to set.
     */
    public void setBuildingRoomNumber(String buildingRoomNumber) {
        this.buildingRoomNumber = buildingRoomNumber;
    }


    /**
     * Gets the buildingSubRoomNumber attribute.
     * 
     * @return Returns the buildingSubRoomNumber
     */
    public String getBuildingSubRoomNumber() {
        return buildingSubRoomNumber;
    }

    /**
     * Sets the buildingSubRoomNumber attribute.
     * 
     * @param buildingSubRoomNumber The buildingSubRoomNumber to set.
     */
    public void setBuildingSubRoomNumber(String buildingSubRoomNumber) {
        this.buildingSubRoomNumber = buildingSubRoomNumber;
    }


    /**
     * Gets the campusTagNumber attribute.
     * 
     * @return Returns the campusTagNumber
     */
    public String getCampusTagNumber() {
        return campusTagNumber;
    }

    /**
     * Sets the campusTagNumber attribute.
     * 
     * @param campusTagNumber The campusTagNumber to set.
     */
    public void setCampusTagNumber(String campusTagNumber) {
        this.campusTagNumber = campusTagNumber;
    }


    /**
     * Gets the organizationInventoryName attribute.
     * 
     * @return Returns the organizationInventoryName
     */
    public String getOrganizationInventoryName() {
        return organizationInventoryName;
    }

    /**
     * Sets the organizationInventoryName attribute.
     * 
     * @param organizationInventoryName The organizationInventoryName to set.
     */
    public void setOrganizationInventoryName(String organizationInventoryName) {
        this.organizationInventoryName = organizationInventoryName;
    }


    /**
     * Gets the organizationAssetTypeIdentifier attribute.
     * 
     * @return Returns the organizationAssetTypeIdentifier
     */
    public String getOrganizationAssetTypeIdentifier() {
        return organizationAssetTypeIdentifier;
    }

    /**
     * Sets the organizationAssetTypeIdentifier attribute.
     * 
     * @param organizationAssetTypeIdentifier The organizationAssetTypeIdentifier to set.
     */
    public void setOrganizationAssetTypeIdentifier(String organizationAssetTypeIdentifier) {
        this.organizationAssetTypeIdentifier = organizationAssetTypeIdentifier;
    }


    /**
     * Gets the offCampusAddress attribute.
     * 
     * @return Returns the offCampusAddress
     */
    public String getOffCampusAddress() {
        return offCampusAddress;
    }

    /**
     * Sets the offCampusAddress attribute.
     * 
     * @param offCampusAddress The offCampusAddress to set.
     */
    public void setOffCampusAddress(String offCampusAddress) {
        this.offCampusAddress = offCampusAddress;
    }


    /**
     * Gets the offCampusCityName attribute.
     * 
     * @return Returns the offCampusCityName
     */
    public String getOffCampusCityName() {
        return offCampusCityName;
    }

    /**
     * Sets the offCampusCityName attribute.
     * 
     * @param offCampusCityName The offCampusCityName to set.
     */
    public void setOffCampusCityName(String offCampusCityName) {
        this.offCampusCityName = offCampusCityName;
    }


    /**
     * Gets the offCampusStateCode attribute.
     * 
     * @return Returns the offCampusStateCode
     */
    public String getOffCampusStateCode() {
        return offCampusStateCode;
    }

    /**
     * Sets the offCampusStateCode attribute.
     * 
     * @param offCampusStateCode The offCampusStateCode to set.
     */
    public void setOffCampusStateCode(String offCampusStateCode) {
        this.offCampusStateCode = offCampusStateCode;
    }


    /**
     * Gets the offCampusZipCode attribute.
     * 
     * @return Returns the offCampusZipCode
     */
    public String getOffCampusZipCode() {
        return offCampusZipCode;
    }

    /**
     * Sets the offCampusZipCode attribute.
     * 
     * @param offCampusZipCode The offCampusZipCode to set.
     */
    public void setOffCampusZipCode(String offCampusZipCode) {
        this.offCampusZipCode = offCampusZipCode;
    }

    /**
     * Gets the offCampusCountryCode attribute.
     * 
     * @return Returns the offCampusCountryCode.
     */
    public String getOffCampusCountryCode() {
        return offCampusCountryCode;
    }

    /**
     * Sets the offCampusCountryCode attribute value.
     * 
     * @param offCampusCountryCode The offCampusCountryCode to set.
     */
    public void setOffCampusCountryCode(String offCampusCountryCode) {
        this.offCampusCountryCode = offCampusCountryCode;
    }

    /**
     * Gets the offCampusName attribute.
     * 
     * @return Returns the offCampusName.
     */
    public String getOffCampusName() {
        return offCampusName;
    }

    /**
     * Sets the offCampusName attribute value.
     * 
     * @param offCampusName The offCampusName to set.
     */
    public void setOffCampusName(String offCampusName) {
        this.offCampusName = offCampusName;
    }

    /**
     * Gets the governmentTagNumber attribute.
     * 
     * @return Returns the governmentTagNumber
     */
    public String getGovernmentTagNumber() {
        return governmentTagNumber;
    }

    /**
     * Sets the governmentTagNumber attribute.
     * 
     * @param governmentTagNumber The governmentTagNumber to set.
     */
    public void setGovernmentTagNumber(String governmentTagNumber) {
        this.governmentTagNumber = governmentTagNumber;
    }


    /**
     * Gets the nationalStockNumber attribute.
     * 
     * @return Returns the nationalStockNumber
     */
    public String getNationalStockNumber() {
        return nationalStockNumber;
    }

    /**
     * Sets the nationalStockNumber attribute.
     * 
     * @param nationalStockNumber The nationalStockNumber to set.
     */
    public void setNationalStockNumber(String nationalStockNumber) {
        this.nationalStockNumber = nationalStockNumber;
    }

    /**
     * Gets the campus attribute.
     * 
     * @return Returns the campus
     */
    public Campus getCampus() {
        return campus = (Campus) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Campus.class).retrieveExternalizableBusinessObjectIfNecessary(this, campus, "campus");
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
     * @deprecated
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * Gets the buildingRoom attribute.
     * 
     * @return Returns the buildingRoom.
     */
    public Room getBuildingRoom() {
        return buildingRoom;
    }

    /**
     * Sets the buildingRoom attribute value.
     * 
     * @param buildingRoom The buildingRoom to set.
     * @deprecated
     */
    public void setBuildingRoom(Room buildingRoom) {
        this.buildingRoom = buildingRoom;
    }

    /**
     * Gets the offCampusState attribute.
     * 
     * @return Returns the offCampusState.
     */
    public State getOffCampusState() {
        offCampusState = SpringContext.getBean(StateService.class).getByPrimaryIdIfNecessary(this, offCampusCountryCode, offCampusStateCode, offCampusState);
        return offCampusState;
    }

    /**
     * Sets the offCampusState attribute value.
     * 
     * @param offCampusState The offCampusState to set.
     * @deprecated
     */
    public void setOffCampusState(State offCampusState) {
        this.offCampusState = offCampusState;
    }

    /**
     * Gets the postalZipCode attribute.
     * 
     * @return Returns the postalZipCode.
     */
    public PostalCode getPostalZipCode() {
        postalZipCode = SpringContext.getBean(PostalCodeService.class).getByPrimaryIdIfNecessary(this, offCampusCountryCode, offCampusZipCode, postalZipCode);
        return postalZipCode;
    }

    /**
     * Sets the postalZipCode attribute value.
     * 
     * @param postalZipCode The postalZipCode to set.
     * @deprecated
     */
    public void setPostalZipCode(PostalCode postalZipCode) {
        this.postalZipCode = postalZipCode;
    }

    /**
     * Gets the offCampusCountry attribute.
     * 
     * @return Returns the offCampusCountry.
     */
    public Country getOffCampusCountry() {
        offCampusCountry = SpringContext.getBean(CountryService.class).getByPrimaryIdIfNecessary(this, offCampusCountryCode, offCampusCountry);
        return offCampusCountry;
    }

    /**
     * Sets the offCampusCountry attribute value.
     * 
     * @param offCampusCountry The offCampusCountry to set.
     * @deprecated
     */
    public void setOffCampusCountry(Country offCampusCountry) {
        this.offCampusCountry = offCampusCountry;
    }

    /**
     * Gets the asset attribute.
     * 
     * @return Returns the asset.
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute value.
     * 
     * @param asset The asset to set.
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }


    /**
     * Gets the assetGlobalUniqueDetails attribute.
     * 
     * @return Returns the assetGlobalUniqueDetails.
     */
    public List<AssetGlobalDetail> getAssetGlobalUniqueDetails() {
        return assetGlobalUniqueDetails;
    }

    /**
     * Sets the assetGlobalUniqueDetails attribute value.
     * 
     * @param assetGlobalUniqueDetails The assetGlobalUniqueDetails to set.
     */
    public void setAssetGlobalUniqueDetails(List<AssetGlobalDetail> assetGlobalUniqueDetails) {
        this.assetGlobalUniqueDetails = assetGlobalUniqueDetails;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        return m;
    }

    public String getCapitalAssetDescription() {
        return capitalAssetDescription;
    }

    public void setCapitalAssetDescription(String capitalAssetDescription) {
        this.capitalAssetDescription = capitalAssetDescription;
    }

    public String getCapitalAssetTypeCode() {
        return capitalAssetTypeCode;
    }

    public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
        this.capitalAssetTypeCode = capitalAssetTypeCode;
    }

    public String getManufacturerModelNumber() {
        return manufacturerModelNumber;
    }

    public void setManufacturerModelNumber(String manufacturerModelNumber) {
        this.manufacturerModelNumber = manufacturerModelNumber;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getOrganizationText() {
        return organizationText;
    }

    public KualiDecimal getSeparateSourceAmount() {
        return separateSourceAmount;
    }

    public void setSeparateSourceAmount(KualiDecimal separateSourceAmount) {
        this.separateSourceAmount = separateSourceAmount;
    }

    public void setOrganizationText(String organizationText) {
        this.organizationText = organizationText;
    }

    public String getRepresentativeUniversalIdentifier() {
        return representativeUniversalIdentifier;
    }

    public void setRepresentativeUniversalIdentifier(String representativeUniversalIdentifier) {
        this.representativeUniversalIdentifier = representativeUniversalIdentifier;
    }

}
