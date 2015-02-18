/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cam.document.service;

import java.util.Map;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.rice.krad.bo.BusinessObject;

public interface AssetLocationService {

    public static enum LocationField {
        CAMPUS_CODE, BUILDING_CODE, ROOM_NUMBER, SUB_ROOM_NUMBER, CONTACT_NAME, STREET_ADDRESS, CITY_NAME, STATE_CODE, ZIP_CODE, COUNTRY_CODE; 
    }

    /**
     * The method will set Off Campus Location from the assetLocations collection
     */
    public void setOffCampusLocation(Asset asset);

    /**
     * Update user input into reference of Asset Location
     */
    public void updateOffCampusLocation(Asset newAsset);

    boolean validateLocation(Map<LocationField, String> fieldMap, BusinessObject businessObject, boolean isCapital, AssetType assetType);

    /** 
     * check if offCampusLocation is holding any location information.
     * 
     * @param offCampusLocation
     */
    public boolean isOffCampusLocationExists(AssetLocation offCampusLocation);
    
    /**
     * check if offCampusLocation is empty
     * 
     * @param offCampusLocation
     */
    public boolean isOffCampusLocationEmpty(AssetLocation offCampusLocation);
}
