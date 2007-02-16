/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.kfs.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class PostalZipCode extends PersistableBusinessObjectBase {

    private String postalZipCode;
    private String postalStateCode;
    private String postalCityName;
    private String buildingCode;
    private String buildingRoomNumber;
    private State state;

    /**
     * Default no-arg constructor.
     */
    public PostalZipCode() {

    }

    /**
     * Gets the postalZipCode attribute.
     * 
     * @return Returns the postalZipCode
     * 
     */
    public String getPostalZipCode() {
        return postalZipCode;
    }

    /**
     * Sets the postalZipCode attribute.
     * 
     * @param postalZipCode The postalZipCode to set.
     * 
     */
    public void setPostalZipCode(String postalZipCode) {
        this.postalZipCode = postalZipCode;
    }

    /**
     * Gets the postalStateCode attribute.
     * 
     * @return Returns the postalStateCode
     * 
     */
    public String getPostalStateCode() {
        return postalStateCode;
    }

    /**
     * Sets the postalStateCode attribute.
     * 
     * @param postalStateCode The postalStateCode to set.
     * 
     */
    public void setPostalStateCode(String postalStateCode) {
        this.postalStateCode = postalStateCode;
    }

    /**
     * Gets the postalCityName attribute.
     * 
     * @return Returns the postalCityName
     * 
     */
    public String getPostalCityName() {
        return postalCityName;
    }

    /**
     * Sets the postalCityName attribute.
     * 
     * @param postalCityName The postalCityName to set.
     * 
     */
    public void setPostalCityName(String postalCityName) {
        this.postalCityName = postalCityName;
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
     * Gets the state attribute.
     * 
     * @return Returns the state.
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the state attribute value.
     * 
     * @param state The state to set.
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("postalZipCode", this.postalZipCode);
        return m;
    }
}
