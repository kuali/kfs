/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cam.businessobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetailBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.campus.CampusEbo;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;

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
    private CampusEbo campus;
    private Building building;
    private Room buildingRoom;
    private StateEbo offCampusState;
    private CountryEbo offCampusCountry;
    private PostalCodeEbo postalZipCode;

    private Integer locationQuantity;
    private String representativeUniversalIdentifier;

    private String capitalAssetTypeCode;
    private AssetType capitalAssetType;

    private String capitalAssetDescription;
    private String manufacturerName;
    private String organizationText;
    private String manufacturerModelNumber;
    private KualiDecimal separateSourceAmount; 

    // Non persistent
    private List<AssetGlobalDetail> assetGlobalUniqueDetails;
    private Person assetRepresentative;
    
    public Person getAssetRepresentative() {
        assetRepresentative = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(representativeUniversalIdentifier, assetRepresentative);
        return assetRepresentative;
    }

    public void setAssetRepresentative(Person assetRepresentative) {
        this.assetRepresentative = assetRepresentative;
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
        assetGlobalUniqueDetails = new ArrayList<AssetGlobalDetail>();

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
    public CampusEbo getCampus() {
        if ( StringUtils.isBlank(campusCode) ) {
            campus = null;
        } else {
            if ( campus == null || !StringUtils.equals( campus.getCode(), campusCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CampusEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, campusCode);
                    campus = moduleService.getExternalizableBusinessObject(CampusEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return campus;
    }
    
    /**
     * Sets the campus attribute.
     * 
     * @param campus The campus to set.
     * @deprecated
     */
    public void setCampus(CampusEbo campus) {
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
     * Gets the offCampusState attribute
     *
     * @return Returns the offCampusState
     */
    public StateEbo getAccountState() {
        if ( StringUtils.isBlank(offCampusStateCode) || StringUtils.isBlank(KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
            offCampusState = null;
        } else {
            if ( offCampusState == null || !StringUtils.equals( offCampusState.getCode(), offCampusStateCode) || !StringUtils.equals(offCampusState.getCountryCode(), KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);/*RICE20_REFACTORME*/
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, offCampusStateCode);
                    offCampusState = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return offCampusState;
    }
    
    /**
     * Sets the offCampusState attribute value.
     * 
     * @param offCampusState The offCampusState to set.
     * @deprecated
     */
    public void setOffCampusState(StateEbo offCampusState) {
        this.offCampusState = offCampusState;
    }

    /**
     * Gets the postalZipCode attribute.
     * 
     * @return Returns the postalZipCode.
     */
    public PostalCodeEbo getPostalZipCode() {
        if ( StringUtils.isBlank(offCampusZipCode) || StringUtils.isBlank(KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
            postalZipCode = null;
        } else {
            if ( postalZipCode == null || !StringUtils.equals( postalZipCode.getCode(), offCampusZipCode) || !StringUtils.equals(postalZipCode.getCountryCode(), KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);/*RICE20_REFACTORME*/
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, offCampusZipCode);
                    postalZipCode = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        
        return postalZipCode;
    }
    
    /**
     * Sets the postalZipCode attribute value.
     * 
     * @param postalZipCode The postalZipCode to set.
     * @deprecated
     */
    public void setPostalZipCode(PostalCodeEbo postalZipCode) {
        this.postalZipCode = postalZipCode;
    }

    /**
     * Gets the offCampusCountry attribute.
     * 
     * @return Returns the offCampusCountry.
     */
    public CountryEbo getOffCampusCountry() {
        if ( StringUtils.isBlank(offCampusCountryCode) ) {
            offCampusCountry = null;
        } else {
            if ( offCampusCountry == null || !StringUtils.equals( offCampusCountry.getCode(), offCampusCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, offCampusCountryCode);
                    offCampusCountry = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return offCampusCountry;
    }
    
    /**
     * Sets the offCampusCountry attribute value.
     * 
     * @param offCampusCountry The offCampusCountry to set.
     * @deprecated
     */
    public void setOffCampusCountry(CountryEbo offCampusCountry) {
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
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

    public AssetType getCapitalAssetType() {
        return capitalAssetType;
    }

    public void setCapitalAssetType(AssetType capitalAssetType) {
        this.capitalAssetType = capitalAssetType;
    }

}
