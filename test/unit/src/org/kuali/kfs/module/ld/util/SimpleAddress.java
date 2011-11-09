/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.util;

import java.sql.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class SimpleAddress {
    private String street;
    private String city;
    private String state;
    private Integer zip;
    private KualiDecimal propertyValue;
    private Date licenseDate;

    /**
     * Constructs a SimpleAddress.java.
     */
    public SimpleAddress() {
        this("1000 Main Street", "Test City", "Kuali", 10000);
    }

    /**
     * Constructs a SimpleAddress.java.
     */
    public SimpleAddress(String street, String city, String state, Integer zip) {
        this(street, city, state, zip, KualiDecimal.ZERO, null);
    }

    /**
     * Constructs a SimpleAddress.java.
     * 
     * @param street
     * @param city
     * @param state
     * @param zip
     * @param propertyValue
     * @param licenseDate
     */
    public SimpleAddress(String street, String city, String state, Integer zip, KualiDecimal propertyValue, Date licenseDate) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.propertyValue = propertyValue;
        this.licenseDate = licenseDate;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SimpleAddress))
            return false;

        SimpleAddress that = (SimpleAddress) object;
        if (!StringUtils.equals(this.getStreet(), that.getStreet())) {
            return false;
        }
        else if (!StringUtils.equals(this.getCity(), that.getCity())) {
            return false;
        }
        else if (!StringUtils.equals(this.getState(), that.getState())) {
            return false;
        }
        else if (!StringUtils.equals(this.getZip().toString(), that.getZip().toString())) {
            return false;
        }
        return true;
    }

    /**
     * Gets the city attribute.
     * 
     * @return Returns the city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city attribute value.
     * 
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state attribute.
     * 
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state attribute value.
     * 
     * @param state The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the street attribute.
     * 
     * @return Returns the street.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the street attribute value.
     * 
     * @param street The street to set.
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Gets the zip attribute.
     * 
     * @return Returns the zip.
     */
    public Integer getZip() {
        return zip;
    }

    /**
     * Sets the zip attribute value.
     * 
     * @param zip The zip to set.
     */
    public void setZip(Integer zip) {
        this.zip = zip;
    }

    /**
     * Gets the licenseDate attribute.
     * 
     * @return Returns the licenseDate.
     */
    public Date getLicenseDate() {
        return licenseDate;
    }

    /**
     * Sets the licenseDate attribute value.
     * 
     * @param licenseDate The licenseDate to set.
     */
    public void setLicenseDate(Date licenseDate) {
        this.licenseDate = licenseDate;
    }

    /**
     * Gets the propertyValue attribute.
     * 
     * @return Returns the propertyValue.
     */
    public KualiDecimal getPropertyValue() {
        return propertyValue;
    }

    /**
     * Sets the propertyValue attribute value.
     * 
     * @param propertyValue The propertyValue to set.
     */
    public void setPropertyValue(KualiDecimal propertyValue) {
        this.propertyValue = propertyValue;
    }
}
