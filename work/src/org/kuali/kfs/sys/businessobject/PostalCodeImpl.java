/*
 * Copyright 2007 The Kuali Foundation.
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

package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class PostalCodeImpl extends PersistableBusinessObjectBase implements Inactivateable, PostalCode {

    private String postalCountryCode;
    private String postalCode;
    private String postalStateCode;
    private String postalCityName;
    private String buildingCode;
    private String buildingRoomNumber;
    private boolean active;
    private String countyCode;

    private State state;
    private Country country;

    /**
     * Default no-arg constructor.
     */
    public PostalCodeImpl() {

    }

    /**
     * Gets the postalCode attribute.
     * 
     * @return Returns the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postalCode attribute.
     * 
     * @param postalCode The postalZipCode to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the postalStateCode attribute.
     * 
     * @return Returns the postalStateCode
     */
    public String getPostalStateCode() {
        return postalStateCode;
    }

    /**
     * Sets the postalStateCode attribute.
     * 
     * @param postalStateCode The postalStateCode to set.
     */
    public void setPostalStateCode(String postalStateCode) {
        this.postalStateCode = postalStateCode;
    }

    /**
     * Gets the postalCityName attribute.
     * 
     * @return Returns the postalCityName
     */
    public String getPostalCityName() {
        return postalCityName;
    }

    /**
     * Sets the postalCityName attribute.
     * 
     * @param postalCityName The postalCityName to set.
     */
    public void setPostalCityName(String postalCityName) {
        this.postalCityName = postalCityName;
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
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("postalCode", this.postalCode);
        return m;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    /**
     * Gets the postalCountryCode attribute.
     * 
     * @return Returns the postalCountryCode.
     */
    public String getPostalCountryCode() {
        return postalCountryCode;
    }

    /**
     * Sets the postalCountryCode attribute value.
     * 
     * @param postalCountryCode The postalCountryCode to set.
     */
    public void setPostalCountryCode(String postalCountryCode) {
        this.postalCountryCode = postalCountryCode;
    }

    /**
     * Gets the country attribute.
     * 
     * @return Returns the country.
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Sets the country attribute value.
     * 
     * @param country The country to set.
     */
    public void setCountry(Country country) {
        this.country = country;
    }
}
