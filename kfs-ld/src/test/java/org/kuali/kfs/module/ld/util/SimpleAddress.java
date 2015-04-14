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
