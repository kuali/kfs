/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cam.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.document.service.AssetLocationService.LocationField;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObjectBase;
import org.kuali.rice.kns.util.GlobalVariables;

@ConfigureContext(session = khuntley)
public class AssetLocationServiceTest extends KualiTestBase {
    private AssetLocationService assetLocationService;
    private static Map<LocationField, String> fieldMap = new HashMap<LocationField, String>();
    private MockBusinessObject onCampusObject;
    private MockBusinessObject offcampusObject;
    static {
        fieldMap.put(LocationField.CAMPUS_CODE, "campusCode");
        fieldMap.put(LocationField.BUILDING_CODE, "buildingCode");
        fieldMap.put(LocationField.ROOM_NUMBER, "roomNumber");
        fieldMap.put(LocationField.SUB_ROOM_NUMBER, "subRoomNumber");
        fieldMap.put(LocationField.CONTACT_NAME, "contactName");
        fieldMap.put(LocationField.STREET_ADDRESS, "streetAddress");
        fieldMap.put(LocationField.CITY_NAME, "city");
        fieldMap.put(LocationField.STATE_CODE, "stateCode");
        fieldMap.put(LocationField.ZIP_CODE, "zipCode");
        fieldMap.put(LocationField.COUNTRY_CODE, "countryCode");
        fieldMap.put(LocationField.ERROR_SECTION,CamsConstants.LOCATION_INFORMATION_SECTION_ID);        
    }

    public class MockBusinessObject extends BusinessObjectBase {
        private String campusCode;
        private String buildingCode;
        private String roomNumber;
        private String subRoomNumber;
        private String contactName;
        private String streetAddress;
        private String city;
        private String stateCode;
        private String zipCode;
        private String countryCode;

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

        public String getRoomNumber() {
            return roomNumber;
        }

        public void setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
        }

        public String getSubRoomNumber() {
            return subRoomNumber;
        }

        public void setSubRoomNumber(String subRoomNumber) {
            this.subRoomNumber = subRoomNumber;
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getStreetAddress() {
            return streetAddress;
        }

        public void setStreetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStateCode() {
            return stateCode;
        }

        public void setStateCode(String stateCode) {
            this.stateCode = stateCode;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }


        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        @Override
        protected LinkedHashMap<String, String> toStringMapper() {
            return null;
        }

        public void refresh() {

        }

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        this.assetLocationService = SpringContext.getBean(AssetLocationService.class);
        onCampusObject = new MockBusinessObject();
        onCampusObject.setCampusCode("BL");
        onCampusObject.setBuildingCode("BL001");
        onCampusObject.setRoomNumber("B009");
        onCampusObject.setSubRoomNumber("23");

        offcampusObject = new MockBusinessObject();
        offcampusObject.setContactName("eddsdsd");
        offcampusObject.setStreetAddress("Addreed");
        offcampusObject.setCity("City");
        offcampusObject.setStateCode("IN");
        offcampusObject.setZipCode("47401");
        offcampusObject.setCountryCode("US");

    }

    public void testValidateLocation_OnCampus() throws Exception {

        AssetType assetType = new AssetType();
        assetType.setMovingIndicator(true);
        // when conditions are valid
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertTrue(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when building code is null
        onCampusObject.setBuildingCode(null);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when room number is null
        onCampusObject.setBuildingCode("BL001");
        onCampusObject.setRoomNumber(null);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();


        // when sub room number is optional
        onCampusObject.setRoomNumber("B034F");
        onCampusObject.setSubRoomNumber(null);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertTrue(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // test condition when building required indicator is true
        assetType.setMovingIndicator(false);
        assetType.setRequiredBuildingIndicator(true);
        onCampusObject.setRoomNumber(null);
        onCampusObject.setSubRoomNumber(null);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertTrue(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when building code is null
        onCampusObject.setBuildingCode(null);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when room number is not null
        onCampusObject.setBuildingCode("BL001");
        onCampusObject.setRoomNumber("YYGBJGJH");
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();


        // when sub room number is not null
        onCampusObject.setRoomNumber(null);
        onCampusObject.setSubRoomNumber("HGBJHNGBJH");
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();
    }

    public void testValidateLocation_OffCampus() throws Exception {
        AssetType assetType = new AssetType();
        assetType.setMovingIndicator(false);
        assetType.setRequiredBuildingIndicator(false);
        // when conditions are valid
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertTrue(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when contact name is null
        this.offcampusObject.setContactName(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when street address is null
        this.offcampusObject.setContactName("me");
        this.offcampusObject.setStreetAddress(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when city name is null
        this.offcampusObject.setStreetAddress("Street");
        this.offcampusObject.setCity(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when state code is null
        this.offcampusObject.setCity("City");
        this.offcampusObject.setStateCode(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when zip code is null
        this.offcampusObject.setStateCode("MI");
        this.offcampusObject.setZipCode(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when country code is null
        this.offcampusObject.setZipCode("34343");
        this.offcampusObject.setCountryCode(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

    }

    public void testValidateLocation_CapitalAsset() throws Exception {

        // when asset type is not defined
        AssetType assetType = null;
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // required building and offcampus
        assetType = new AssetType();
        assetType.setRequiredBuildingIndicator(true);
        assetType.setMovingIndicator(false);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // req bldg and moving indicators value false, oncampus not allowed
        assetType.setRequiredBuildingIndicator(false);
        assetType.setMovingIndicator(false);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when no information is available for capital asset
        assetType.setRequiredBuildingIndicator(true);
        MockBusinessObject blankObject = new MockBusinessObject();
        this.assetLocationService.validateLocation(fieldMap, blankObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when both are available
        this.onCampusObject.setStreetAddress("Street");
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();
    }

    public void testValidateLocation_NonCapitalAsset() throws Exception {
        // when no information is available for capital asset
        MockBusinessObject blankObject = new MockBusinessObject();
        this.assetLocationService.validateLocation(fieldMap, blankObject, false, null);
        assertTrue(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when on-campus, all fields are optional
        this.onCampusObject.setBuildingCode(null);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, false, null);
        assertTrue(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when off-campus, validate location
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, false, null);
        assertTrue(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

        // when off-campus, validate location
        this.offcampusObject.setCountryCode(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, false, null);
        assertFalse(GlobalVariables.getErrorMap().isEmpty());
        GlobalVariables.getErrorMap().clear();

    }
}

