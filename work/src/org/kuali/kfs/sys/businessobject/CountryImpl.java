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

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 *
 */

public class CountryImpl extends PersistableBusinessObjectBase implements Inactivateable, Country{

    private String postalCountryCode;
    private String postalCountryName;
    private boolean postalCountryRestrictedIndicator;
    private boolean active;

    /**
     * Default no-arg constructor.
     */

    public CountryImpl() {
        super();
    }

    /**
     * @param postalCountryCode - The postalCountryCode to set
     */
    public void setPostalCountryCode(String postalCountryCode) {
        this.postalCountryCode = postalCountryCode;
    }

    /**
     * @return Returns the postalCountryCode
     */
    public String getPostalCountryCode() {
        return postalCountryCode;
    }

    /**
     * @param postalCountryName - The postalCountryName to set
     */
    public void setPostalCountryName(String postalCountryName) {
        this.postalCountryName = postalCountryName;
    }

    /**
     * @return Returns the postalCountryName
     */
    public String getPostalCountryName() {
        return postalCountryName;
    }

    /**
     * Gets the postalCountryRestrictedIndicator attribute.
     * 
     * @return Returns the postalCountryRestrictedIndicator.
     */
    public boolean isPostalCountryRestrictedIndicator() {
        return postalCountryRestrictedIndicator;
    }

    /**
     * Sets the postalCountryRestrictedIndicator attribute value.
     * 
     * @param postalCountryRestrictedIndicator The postalCountryRestrictedIndicator to set.
     */
    public void setPostalCountryRestrictedIndicator(boolean postalCountryRestrictedIndicator) {
        this.postalCountryRestrictedIndicator = postalCountryRestrictedIndicator;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("postalCountryCode", getPostalCountryCode());

        return m;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

}
