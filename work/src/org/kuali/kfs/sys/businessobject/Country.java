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


public interface Country extends ExternalizableBusinessObject{

    /**
     * @param postalCountryCode - The postalCountryCode to set
     */
    public abstract void setPostalCountryCode(String postalCountryCode);

    /**
     * @return Returns the postalCountryCode
     */
    public abstract String getPostalCountryCode();

    /**
     * @param postalCountryName - The postalCountryName to set
     */
    public abstract void setPostalCountryName(String postalCountryName);

    /**
     * @return Returns the postalCountryName
     */
    public abstract String getPostalCountryName();

    /**
     * Gets the postalCountryRestrictedIndicator attribute.
     * 
     * @return Returns the postalCountryRestrictedIndicator.
     */
    public abstract boolean isPostalCountryRestrictedIndicator();

    /**
     * Sets the postalCountryRestrictedIndicator attribute value.
     * 
     * @param postalCountryRestrictedIndicator The postalCountryRestrictedIndicator to set.
     */
    public abstract void setPostalCountryRestrictedIndicator(boolean postalCountryRestrictedIndicator);

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public abstract boolean isActive();

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public abstract void setActive(boolean active);

}