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
package org.kuali.kfs.sys.businessobject;

import org.kuali.rice.kns.bo.ExternalizableBusinessObject;


public interface PostalCode extends ExternalizableBusinessObject{

    /**
     * Gets the postalCode attribute.
     * 
     * @return Returns the postalCode
     */
    public abstract String getPostalCode();

    /**
     * Sets the postalCode attribute.
     * 
     * @param postalCode The postalCode to set.
     */
    public abstract void setPostalCode(String postalCode);

    /**
     * Gets the postalStateCode attribute.
     * 
     * @return Returns the postalStateCode
     */
    public abstract String getPostalStateCode();

    /**
     * Sets the postalStateCode attribute.
     * 
     * @param postalStateCode The postalStateCode to set.
     */
    public abstract void setPostalStateCode(String postalStateCode);

    /**
     * Gets the postalCityName attribute.
     * 
     * @return Returns the postalCityName
     */
    public abstract String getPostalCityName();

    /**
     * Sets the postalCityName attribute.
     * 
     * @param postalCityName The postalCityName to set.
     */
    public abstract void setPostalCityName(String postalCityName);

    /**
     * Gets the state attribute.
     * 
     * @return Returns the state.
     */
    public abstract State getState();

    /**
     * Sets the state attribute value.
     * 
     * @param state The state to set.
     */
    public abstract void setState(State state);

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public abstract boolean isActive();

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public abstract void setActive(boolean active);

    public abstract String getCountyCode();

    public abstract void setCountyCode(String countyCode);

    /**
     * Gets the postalCountryCode attribute.
     * 
     * @return Returns the postalCountryCode.
     */
    public abstract String getPostalCountryCode();

    /**
     * Sets the postalCountryCode attribute value.
     * 
     * @param postalCountryCode The postalCountryCode to set.
     */
    public abstract void setPostalCountryCode(String postalCountryCode);

    /**
     * Gets the country attribute.
     * 
     * @return Returns the country.
     */
    public abstract Country getCountry();

    /**
     * Sets the country attribute value.
     * 
     * @param country The country to set.
     */
    public abstract void setCountry(Country country);

}