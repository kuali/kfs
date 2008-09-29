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


public interface State extends ExternalizableBusinessObject{

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
     * Gets the postalStateName attribute.
     * 
     * @return Returns the postalStateName
     */
    public abstract String getPostalStateName();

    /**
     * Sets the postalStateName attribute.
     * 
     * @param postalStateName The postalStateName to set.
     */
    public abstract void setPostalStateName(String postalStateName);

    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public abstract String getCodeAndDescription();

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