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
package org.kuali.module.cams.service;

import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.module.cams.bo.Asset;

public interface AssetLocationService {

    public static enum LocationField {
        CAMPUS_CODE, BUILDING_CODE, ROOM_NUMBER, SUB_ROOM_NUMBER, CONTACT_NAME, STREET_ADDRESS, CITY_NAME, STATE_CODE, ZIP_CODE, COUNTRY_CODE, LOCATION_TAB_KEY;
    }

    /**
     * The method will set Off Campus Location from the assetLocations collection
     */
    public void setOffCampusLocation(Asset asset);

    /**
     * Update user input into reference of Asset Location
     */
    public void updateOffCampusLocation(Asset newAsset);

    boolean validateLocation(BusinessObject businessObject, Asset asset, Map<LocationField, String> fieldMap);
}
