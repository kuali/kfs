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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name = "TEM_PROFILE_ADDR_T")
public class TemProfileAddress extends PersistableBusinessObjectBase {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 7958366500696148370L;
	private Integer profileId;
    private String streetAddressLine1;
    private String streetAddressLine2;
    private String cityName;
    private String stateCode;
    private String zipCode;
    private String countryCode;

    private TemProfile profile;

    private String customerNumber;
    private Integer customerAddressIdentifier;
    private String principalId;


    public TemProfileAddress() {
        super();
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("streetAddressLine1", this.streetAddressLine1);
        m.put("cityName", this.cityName);
        m.put("zipCode", this.zipCode);
        return m;
    }


    /**
     * Gets the temProfileId attribute.
     * @return Returns the temProfileId.
     */
    @Column(name = "tem_profile_id", nullable = false, length=19)
    public Integer getProfileId() {
        return profileId;
    }


    /**
     * Sets the temProfileId attribute value.
     * @param temProfileId The temProfileId to set.
     */
    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }


    /**
     * Gets the streetAddressLine1 attribute.
     * @return Returns the streetAddressLine1.
     */
    @Column(name = "addr_line_1", length = 50)
    public String getStreetAddressLine1() {
        return streetAddressLine1;
    }


    /**
     * Sets the streetAddressLine1 attribute value.
     * @param streetAddressLine1 The streetAddressLine1 to set.
     */
    public void setStreetAddressLine1(String streetAddressLine1) {
        this.streetAddressLine1 = streetAddressLine1;
    }


    /**
     * Gets the streetAddressLine2 attribute.
     * @return Returns the streetAddressLine2.
     */
    @Column(name = "addr_line_2", length = 50)
    public String getStreetAddressLine2() {
        return streetAddressLine2;
    }


    /**
     * Sets the streetAddressLine2 attribute value.
     * @param streetAddressLine2 The streetAddressLine2 to set.
     */
    public void setStreetAddressLine2(String streetAddressLine2) {
        this.streetAddressLine2 = streetAddressLine2;
    }


    /**
     * Gets the cityName attribute.
     * @return Returns the cityName.
     */
    @Column(name = "city_nm", length = 30)
    public String getCityName() {
        return cityName;
    }


    /**
     * Sets the cityName attribute value.
     * @param cityName The cityName to set.
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }


    /**
     * Gets the state attribute.
     * @return Returns the state.
     */
    @Column(name = "state_cd", length = 40)
    public String getStateCode() {
        return stateCode;
    }


    /**
     * Sets the state attribute value.
     * @param state The state to set.
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }


    /**
     * Gets the zipCode attribute.
     * @return Returns the zipCode.
     */
    @Column(name = "zip_cd", length = 40)
    public String getZipCode() {
        return zipCode;
    }


    /**
     * Sets the zipCode attribute value.
     * @param zipCode The zipCode to set.
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }


    /**
     * Gets the country attribute.
     * @return Returns the country.
     */
    @Column(name = "country_cd", length = 40)
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the country attribute value.
     * @param country The country to set.
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


    /**
     * Gets the profile attribute.
     * @return Returns the profile.
     */
    public TemProfile getProfile() {
        return profile;
    }


    /**
     * Sets the profile attribute value.
     * @param profile The profile to set.
     */
    public void setProfile(TemProfile profile) {
        this.profile = profile;
    }


	/**
	 * Sets the customerNumber attribute value.
	 * @param customerNumber The customerNumber to set.
	 */
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}


	/**
	 * Gets the customerNumber attribute.
	 * @return Returns the customerNumber.
	 */
	public String getCustomerNumber() {
		return customerNumber;
	}


	/**
	 * Sets the principalId attribute value.
	 * @param principalId The principalId to set.
	 */
	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}


	/**
	 * Gets the principalId attribute.
	 * @return Returns the principalId.
	 */
	public String getPrincipalId() {
		return principalId;
	}


	/**
	 * Sets the customerAddressIdentifier attribute value.
	 * @param customerAddressIdentifier The customerAddressIdentifier to set.
	 */
	public void setCustomerAddressIdentifier(Integer customerAddressIdentifier) {
		this.customerAddressIdentifier = customerAddressIdentifier;
	}


	/**
	 * Gets the customerAddressIdentifier attribute.
	 * @return Returns the customerAddressIdentifier.
	 */
	public Integer getCustomerAddressIdentifier() {
		return customerAddressIdentifier;
	}

}
