/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.rice.krad.document.DocumentBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;

public class AssetLocationServiceImpl implements AssetLocationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLocationService.class);

    private BusinessObjectService businessObjectService;
    private DataDictionaryService DataDictionaryService;

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetLocationService#setOffCampusLocation(org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public void setOffCampusLocation(Asset asset) {
        List<AssetLocation> assetLocations = asset.getAssetLocations();
        AssetLocation offCampusLocation = null;

        for (AssetLocation location : assetLocations) {
            if (CamsConstants.AssetLocationTypeCode.OFF_CAMPUS.equalsIgnoreCase(location.getAssetLocationTypeCode())) {
                // We need a new instance for asset location. Otherwise, if we copy it from the assetLocations collection, it could
                // have newBO and oldBO pointing to the same AssetLocation instance which is bad.
                offCampusLocation = new AssetLocation(location);
                break;
            }
        }

        if (ObjectUtils.isNull(offCampusLocation)) {
            offCampusLocation = new AssetLocation(asset.getCapitalAssetNumber());
            offCampusLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.OFF_CAMPUS);
        }
        asset.setOffCampusLocation(offCampusLocation);
    }

    /**
     * update existing offCampusLocation
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetLocationService#updateOffCampusLocation(org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public void updateOffCampusLocation(Asset asset) {
        AssetLocation offLocation = asset.getOffCampusLocation();
        boolean isOffCampusEmpty = isOffCampusLocationEmpty(offLocation);
        AssetLocation removableOffCampusLocation = null;

        for (AssetLocation location : asset.getAssetLocations()) {
            if (CamsConstants.AssetLocationTypeCode.OFF_CAMPUS.equalsIgnoreCase(location.getAssetLocationTypeCode())) {
                if (isOffCampusEmpty) {
                    removableOffCampusLocation = location;
                }
                else {
                    location.setAssetLocationCityName(offLocation.getAssetLocationCityName());
                    location.setAssetLocationContactIdentifier(offLocation.getAssetLocationContactIdentifier());
                    location.setAssetLocationContactName(offLocation.getAssetLocationContactName());
                    location.setAssetLocationCountryCode(offLocation.getAssetLocationCountryCode());
                    location.setAssetLocationInstitutionName(offLocation.getAssetLocationInstitutionName());
                    location.setAssetLocationPhoneNumber(offLocation.getAssetLocationPhoneNumber());
                    location.setAssetLocationStateCode(offLocation.getAssetLocationStateCode());
                    location.setAssetLocationStreetAddress(offLocation.getAssetLocationStreetAddress());
                    location.setAssetLocationZipCode(offLocation.getAssetLocationZipCode());
                    return;
                }
            }
        }

        if (removableOffCampusLocation != null) {
            asset.getAssetLocations().remove(removableOffCampusLocation);
        }
        else if (!isOffCampusEmpty) { // add the check for off-campus empty  
            // new offCampusLocation, add it into assetLocation List
            asset.getAssetLocations().add(offLocation); 
        }

    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetLocationService#isOffCampusLocationExists(org.kuali.kfs.module.cam.businessobject.AssetLocation)
     */
    public boolean isOffCampusLocationExists(AssetLocation offCampusLocation) {
        if (ObjectUtils.isNotNull(offCampusLocation)) {
            if (CamsConstants.AssetLocationTypeCode.OFF_CAMPUS.equalsIgnoreCase(offCampusLocation.getAssetLocationTypeCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetLocationService#isOffCampusLocationEmpty(org.kuali.kfs.module.cam.businessobject.AssetLocation)
     */
    public boolean isOffCampusLocationEmpty(AssetLocation offCampusLocation) {
        if (ObjectUtils.isNotNull(offCampusLocation)) {
            if (StringUtils.isNotBlank(offCampusLocation.getAssetLocationCityName()) || StringUtils.isNotBlank(offCampusLocation.getAssetLocationContactIdentifier()) || StringUtils.isNotBlank(offCampusLocation.getAssetLocationContactName()) || StringUtils.isNotBlank(offCampusLocation.getAssetLocationCountryCode()) || StringUtils.isNotBlank(offCampusLocation.getAssetLocationInstitutionName()) || StringUtils.isNotBlank(offCampusLocation.getAssetLocationPhoneNumber()) || StringUtils.isNotBlank(offCampusLocation.getAssetLocationStateCode()) || StringUtils.isNotBlank(offCampusLocation.getAssetLocationStreetAddress()) || StringUtils.isNotBlank(offCampusLocation.getAssetLocationZipCode())) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetLocationService#validateLocation(java.lang.Object,
     *      org.kuali.kfs.module.cam.businessobject.Asset, java.util.Map)
     */
    public boolean validateLocation(Map<LocationField, String> fieldMap, BusinessObject businessObject, boolean isCapital, AssetType assetType) {
        String campusCode = readPropertyValue(businessObject, fieldMap, LocationField.CAMPUS_CODE);
        String buildingCode = readPropertyValue(businessObject, fieldMap, LocationField.BUILDING_CODE);
        String roomNumber = readPropertyValue(businessObject, fieldMap, LocationField.ROOM_NUMBER);
        String subRoomNumber = readPropertyValue(businessObject, fieldMap, LocationField.SUB_ROOM_NUMBER);
        String contactName = readPropertyValue(businessObject, fieldMap, LocationField.CONTACT_NAME);
        String streetAddress = readPropertyValue(businessObject, fieldMap, LocationField.STREET_ADDRESS);
        String cityName = readPropertyValue(businessObject, fieldMap, LocationField.CITY_NAME);
        String stateCode = readPropertyValue(businessObject, fieldMap, LocationField.STATE_CODE);
        String zipCode = readPropertyValue(businessObject, fieldMap, LocationField.ZIP_CODE);
        String countryCode = readPropertyValue(businessObject, fieldMap, LocationField.COUNTRY_CODE);

        // businessObject parameter could be BusinessObjectEntry or TransactionalDocumentEntry
        DataDictionaryService ddService = this.getDataDictionaryService();
        DataDictionaryEntryBase ddEntry = null;
        if (DocumentBase.class.isAssignableFrom(businessObject.getClass())) {
            String docTypeName = ddService.getDocumentTypeNameByClass(businessObject.getClass());
            ddEntry = ddService.getDataDictionary().getDocumentEntry(docTypeName);
        }
        else {
            ddEntry = this.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObject.getClass().getName());
        }

        boolean onCampus = StringUtils.isNotBlank(buildingCode) || StringUtils.isNotBlank(roomNumber) || StringUtils.isNotBlank(subRoomNumber);
        boolean offCampus = StringUtils.isNotBlank(contactName) || StringUtils.isNotBlank(streetAddress) || StringUtils.isNotBlank(cityName) || StringUtils.isNotBlank(stateCode) || StringUtils.isNotBlank(zipCode) || StringUtils.isNotBlank(countryCode);

        boolean valid = true;
        if (onCampus && offCampus) {
            putError(fieldMap, LocationField.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_CHOOSE_LOCATION_INFO);
            valid &= false;
        }
        else {
            if (isCapital) {
                valid &= validateCapitalAssetLocation(assetType, fieldMap, campusCode, buildingCode, roomNumber, subRoomNumber, contactName, streetAddress, cityName, stateCode, zipCode, countryCode, onCampus, offCampus, ddEntry);
            }
            else {
                valid &= validateNonCapitalAssetLocation(fieldMap, contactName, streetAddress, cityName, stateCode, zipCode, countryCode, onCampus, offCampus);
            }
        }
        return valid;
    }


    protected boolean validateCapitalAssetLocation(AssetType assetType, Map<LocationField, String> fieldMap, String campusCode, String buildingCode, String roomNumber, String subRoomNumber, String contactName, String streetAddress, String cityName, String stateCode, String zipCode, String countryCode, boolean onCampus, boolean offCampus, DataDictionaryEntryBase businessObjectEntry) {
        boolean valid = true;
        if (ObjectUtils.isNull(assetType)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.LOCATION_INFORMATION_SECTION_ID, CamsKeyConstants.AssetLocation.ERROR_CHOOSE_ASSET_TYPE);
            valid &= false;
        }
        else {
            String label;
            if (assetType.isRequiredBuildingIndicator() && offCampus) {
                // off campus information not allowed
                if (StringUtils.isNotBlank(contactName)) {
                    label = businessObjectEntry.getAttributeDefinition(fieldMap.get(LocationField.CONTACT_NAME)).getLabel();
                    putError(fieldMap, LocationField.CONTACT_NAME, CamsKeyConstants.AssetLocation.ERROR_LOCATION_NOT_PERMITTED_ASSET_TYPE, new String[] { label, assetType.getCapitalAssetTypeDescription() });
                    valid &= false;
                }
                if (StringUtils.isNotBlank(streetAddress)) {
                    label = businessObjectEntry.getAttributeDefinition(fieldMap.get(LocationField.STREET_ADDRESS)).getLabel();
                    putError(fieldMap, LocationField.STREET_ADDRESS, CamsKeyConstants.AssetLocation.ERROR_LOCATION_NOT_PERMITTED_ASSET_TYPE, new String[] { label, assetType.getCapitalAssetTypeDescription() });
                    valid &= false;
                }

                if (StringUtils.isNotBlank(cityName)) {
                    label = businessObjectEntry.getAttributeDefinition(fieldMap.get(LocationField.CITY_NAME)).getLabel();
                    putError(fieldMap, LocationField.CITY_NAME, CamsKeyConstants.AssetLocation.ERROR_LOCATION_NOT_PERMITTED_ASSET_TYPE, new String[] { label, assetType.getCapitalAssetTypeDescription() });
                    valid &= false;
                }

                if (StringUtils.isNotBlank(stateCode)) {
                    label = businessObjectEntry.getAttributeDefinition(fieldMap.get(LocationField.STATE_CODE)).getLabel();
                    putError(fieldMap, LocationField.STATE_CODE, CamsKeyConstants.AssetLocation.ERROR_LOCATION_NOT_PERMITTED_ASSET_TYPE, new String[] { label, assetType.getCapitalAssetTypeDescription() });
                    valid &= false;
                }

                if (StringUtils.isNotBlank(zipCode)) {
                    label = businessObjectEntry.getAttributeDefinition(fieldMap.get(LocationField.ZIP_CODE)).getLabel();
                    putError(fieldMap, LocationField.ZIP_CODE, CamsKeyConstants.AssetLocation.ERROR_LOCATION_NOT_PERMITTED_ASSET_TYPE, new String[] { label, assetType.getCapitalAssetTypeDescription() });
                    valid &= false;
                }

                if (StringUtils.isNotBlank(countryCode)) {
                    label = businessObjectEntry.getAttributeDefinition(fieldMap.get(LocationField.COUNTRY_CODE)).getLabel();
                    putError(fieldMap, LocationField.COUNTRY_CODE, CamsKeyConstants.AssetLocation.ERROR_LOCATION_NOT_PERMITTED_ASSET_TYPE, new String[] { label, assetType.getCapitalAssetTypeDescription() });
                    valid &= false;
                }
            }
            else if (!assetType.isMovingIndicator() && !assetType.isRequiredBuildingIndicator() && onCampus) {
                // land information cannot have on-campus
                if (StringUtils.isNotBlank(buildingCode)) {
                    label = businessObjectEntry.getAttributeDefinition(fieldMap.get(LocationField.BUILDING_CODE)).getLabel();
                    putError(fieldMap, LocationField.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_LOCATION_NOT_PERMITTED_ASSET_TYPE, new String[] { label, assetType.getCapitalAssetTypeDescription() });
                    valid &= false;
                }

                if (StringUtils.isNotBlank(roomNumber)) {
                    label = businessObjectEntry.getAttributeDefinition(fieldMap.get(LocationField.ROOM_NUMBER)).getLabel();
                    putError(fieldMap, LocationField.ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_LOCATION_NOT_PERMITTED_ASSET_TYPE, new String[] { label, assetType.getCapitalAssetTypeDescription() });
                    valid &= false;
                }

                if (StringUtils.isNotBlank(subRoomNumber)) {
                    label = businessObjectEntry.getAttributeDefinition(fieldMap.get(LocationField.SUB_ROOM_NUMBER)).getLabel();
                    putError(fieldMap, LocationField.SUB_ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_LOCATION_NOT_PERMITTED_ASSET_TYPE, new String[] { label, assetType.getCapitalAssetTypeDescription() });
                    valid &= false;
                }
            }
            else if (onCampus) {
                valid = validateOnCampusLocation(fieldMap, assetType, campusCode, buildingCode, roomNumber, subRoomNumber);
            }
            else if (offCampus) {
                valid = validateOffCampusLocation(fieldMap, contactName, streetAddress, cityName, stateCode, zipCode, countryCode);
            }
            else if (assetType.isMovingIndicator() || assetType.isRequiredBuildingIndicator()) {
                putError(fieldMap, LocationField.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_LOCATION_INFO_REQUIRED);
                valid &= false;
            }
        }
        return valid;
    }

    protected boolean validateNonCapitalAssetLocation(Map<LocationField, String> fieldMap, String contactName, String streetAddress, String cityName, String stateCode, String zipCode, String countryCode, boolean onCampus, boolean offCampus) {
        boolean valid = true;
        if (offCampus) {
            valid = validateOffCampusLocation(fieldMap, contactName, streetAddress, cityName, stateCode, zipCode, countryCode);
        }
        return valid;
    }


    /**
     * Convenience method to append the path prefix
     */
    protected void putError(Map<LocationField, String> fieldMap, LocationField field, String errorKey, String... errorParameters) {
        GlobalVariables.getMessageMap().putError(fieldMap.get(field), errorKey, errorParameters);
    }

    protected boolean validateOnCampusLocation(Map<LocationField, String> fieldMap, AssetType assetType, String campusCode, String buildingCode, String buildingRoomNumber, String subRoomNumber) {
        boolean valid = true;
        if (assetType.isMovingIndicator()) {
            if (StringUtils.isBlank(buildingCode)) {
                putError(fieldMap, LocationField.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_ONCAMPUS_BUILDING_CODE_REQUIRED, assetType.getCapitalAssetTypeDescription());
                valid &= false;
            }
            if (StringUtils.isBlank(buildingRoomNumber)) {
                putError(fieldMap, LocationField.ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_ONCAMPUS_BUILDING_ROOM_NUMBER_REQUIRED, assetType.getCapitalAssetTypeDescription());
                valid &= false;
            }
        }
        if (assetType.isRequiredBuildingIndicator()) {
            if (StringUtils.isBlank(buildingCode)) {
                putError(fieldMap, LocationField.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_ONCAMPUS_BUILDING_CODE_REQUIRED, assetType.getCapitalAssetTypeDescription());
                valid &= false;
            }
            if (StringUtils.isNotBlank(buildingRoomNumber)) {
                putError(fieldMap, LocationField.ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_ONCAMPUS_BUILDING_ROOM_NUMBER_NOT_PERMITTED, assetType.getCapitalAssetTypeDescription());
                valid &= false;
            }
            if (StringUtils.isNotBlank(subRoomNumber)) {
                putError(fieldMap, LocationField.SUB_ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_ONCAMPUS_SUB_ROOM_NUMBER_NOT_PERMITTED, assetType.getCapitalAssetTypeDescription());
                valid &= false;
            }
        }
        return valid;
    }

    protected boolean validateOffCampusLocation(Map<LocationField, String> fieldMap, String contactName, String streetAddress, String cityName, String stateCode, String zipCode, String countryCode) {
        boolean valid = true;
        boolean isCountryUS = false;
        if (isBlank(fieldMap, LocationField.COUNTRY_CODE, countryCode)) {
            putError(fieldMap, LocationField.COUNTRY_CODE, CamsKeyConstants.AssetLocation.ERROR_OFFCAMPUS_COUNTRY_REQUIRED);
            valid &= false;
        }
        else {
            isCountryUS = countryCode.equals(KFSConstants.COUNTRY_CODE_UNITED_STATES);
        }

        if (isBlank(fieldMap, LocationField.CONTACT_NAME, contactName)) {
            putError(fieldMap, LocationField.CONTACT_NAME, CamsKeyConstants.AssetLocation.ERROR_OFFCAMPUS_CONTACT_REQUIRED);
            valid &= false;
        }

        if (isBlank(fieldMap, LocationField.STREET_ADDRESS, streetAddress)) {
            putError(fieldMap, LocationField.STREET_ADDRESS, CamsKeyConstants.AssetLocation.ERROR_OFFCAMPUS_ADDRESS_REQUIRED);
            valid &= false;
        }
        if (isBlank(fieldMap, LocationField.CITY_NAME, cityName)) {
            putError(fieldMap, LocationField.CITY_NAME, CamsKeyConstants.AssetLocation.ERROR_OFFCAMPUS_CITY_REQUIRED);
            valid &= false;
        }


        if (isCountryUS) {
            if (isBlank(fieldMap, LocationField.STATE_CODE, stateCode)) {
                putError(fieldMap, LocationField.STATE_CODE, CamsKeyConstants.AssetLocation.ERROR_OFFCAMPUS_STATE_REQUIRED);
                valid &= false;
            }
            if (isBlank(fieldMap, LocationField.ZIP_CODE, zipCode)) {
                putError(fieldMap, LocationField.ZIP_CODE, CamsKeyConstants.AssetLocation.ERROR_OFFCAMPUS_ZIP_REQUIRED);
                valid &= false;
            }
            if (!isBlank(fieldMap, LocationField.STATE_CODE, stateCode)) {
                Map assetLocationMap = new HashMap();
                assetLocationMap.put(KFSPropertyConstants.POSTAL_STATE_CODE, stateCode);
                State locationState = SpringContext.getBean(StateService.class).getState(countryCode, stateCode);
                if (ObjectUtils.isNull(locationState)) {
                    putError(fieldMap, LocationField.STATE_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_OFF_CAMPUS_STATE, stateCode);
                    valid &= false;
                }
            }
        }

        return valid;
    }

    protected boolean isBlank(Map<LocationField, String> fieldMap, LocationField field, String countryCode) {
        return fieldMap.get(field) != null && StringUtils.isBlank(countryCode);
    }

    protected String readPropertyValue(BusinessObject currObject, Map<LocationField, String> fieldMap, LocationField field) {
        String stringValue = null;
        try {
            String propertyName = fieldMap.get(field);
            if (propertyName != null) {
                stringValue = (String) ObjectUtils.getNestedValue(currObject, propertyName);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return stringValue;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return DataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        DataDictionaryService = dataDictionaryService;
    }

}
