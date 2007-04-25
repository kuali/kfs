/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.State;

/**
 * 
 */
public class CampusParameter extends PersistableBusinessObjectBase {

	private String campusCode;
	private String campusPurchasingDirectorName;
	private String campusPurchasingDirectorTitle;
	private String campusAccountsPayableEmailAddress;
    private String purchasingInstitutionName;
    private String purchasingDepartmentName;
    private String purchasingDepartmentLine1Address;
    private String purchasingDepartmentLine2Address;
    private String purchasingDepartmentCityName;
    private String purchasingDepartmentStateCode;
    private String purchasingDepartmentZipCode;
    private String purchasingDepartmentCountryCode;   
    
    private Campus campus;
    private State purchasingDepartmentState;
    private Country purchasingDepartmentCountry;
    
	/**
	 * Default constructor.
	 */
	public CampusParameter() {

	}

	/**
	 * Gets the campusCode attribute.
	 * 
	 * @return Returns the campusCode
	 * 
	 */
	public String getCampusCode() { 
		return campusCode;
	}

	/**
	 * Sets the campusCode attribute.
	 * 
	 * @param campusCode The campusCode to set.
	 * 
	 */
	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}


	/**
	 * Gets the campusPurchasingDirectorName attribute.
	 * 
	 * @return Returns the campusPurchasingDirectorName
	 * 
	 */
	public String getCampusPurchasingDirectorName() { 
		return campusPurchasingDirectorName;
	}

	/**
	 * Sets the campusPurchasingDirectorName attribute.
	 * 
	 * @param campusPurchasingDirectorName The campusPurchasingDirectorName to set.
	 * 
	 */
	public void setCampusPurchasingDirectorName(String campusPurchasingDirectorName) {
		this.campusPurchasingDirectorName = campusPurchasingDirectorName;
	}


	/**
	 * Gets the campusPurchasingDirectorTitle attribute.
	 * 
	 * @return Returns the campusPurchasingDirectorTitle
	 * 
	 */
	public String getCampusPurchasingDirectorTitle() { 
		return campusPurchasingDirectorTitle;
	}

	/**
	 * Sets the campusPurchasingDirectorTitle attribute.
	 * 
	 * @param campusPurchasingDirectorTitle The campusPurchasingDirectorTitle to set.
	 * 
	 */
	public void setCampusPurchasingDirectorTitle(String campusPurchasingDirectorTitle) {
		this.campusPurchasingDirectorTitle = campusPurchasingDirectorTitle;
	}


	/**
	 * Gets the campusAccountsPayableEmailAddress attribute.
	 * 
	 * @return Returns the campusAccountsPayableEmailAddress
	 * 
	 */
	public String getCampusAccountsPayableEmailAddress() { 
		return campusAccountsPayableEmailAddress;
	}

	/**
	 * Sets the campusAccountsPayableEmailAddress attribute.
	 * 
	 * @param campusAccountsPayableEmailAddress The campusAccountsPayableEmailAddress to set.
	 * 
	 */
	public void setCampusAccountsPayableEmailAddress(String campusAccountsPayableEmailAddress) {
		this.campusAccountsPayableEmailAddress = campusAccountsPayableEmailAddress;
	}

    /**
     * Gets the purchasingDepartmentCityName attribute. 
     * @return Returns the purchasingDepartmentCityName.
     */
    public String getPurchasingDepartmentCityName() {
        return purchasingDepartmentCityName;
    }

    /**
     * Sets the purchasingDepartmentCityName attribute value.
     * @param purchasingDepartmentCityName The purchasingDepartmentCityName to set.
     */
    public void setPurchasingDepartmentCityName(String purchasingDepartmentCityName) {
        this.purchasingDepartmentCityName = purchasingDepartmentCityName;
    }

    /**
     * Gets the purchasingDepartmentCountryCode attribute. 
     * @return Returns the purchasingDepartmentCountryCode.
     */
    public String getPurchasingDepartmentCountryCode() {
        return purchasingDepartmentCountryCode;
    }

    /**
     * Sets the purchasingDepartmentCountryCode attribute value.
     * @param purchasingDepartmentCountryCode The purchasingDepartmentCountryCode to set.
     */
    public void setPurchasingDepartmentCountryCode(String purchasingDepartmentCountryCode) {
        this.purchasingDepartmentCountryCode = purchasingDepartmentCountryCode;
    }

    /**
     * Gets the purchasingDepartmentLine1Address attribute. 
     * @return Returns the purchasingDepartmentLine1Address.
     */
    public String getPurchasingDepartmentLine1Address() {
        return purchasingDepartmentLine1Address;
    }

    /**
     * Sets the purchasingDepartmentLine1Address attribute value.
     * @param purchasingDepartmentLine1Address The purchasingDepartmentLine1Address to set.
     */
    public void setPurchasingDepartmentLine1Address(String purchasingDepartmentLine1Address) {
        this.purchasingDepartmentLine1Address = purchasingDepartmentLine1Address;
    }

    /**
     * Gets the purchasingDepartmentLine2Address attribute. 
     * @return Returns the purchasingDepartmentLine2Address.
     */
    public String getPurchasingDepartmentLine2Address() {
        return purchasingDepartmentLine2Address;
    }

    /**
     * Sets the purchasingDepartmentLine2Address attribute value.
     * @param purchasingDepartmentLine2Address The purchasingDepartmentLine2Address to set.
     */
    public void setPurchasingDepartmentLine2Address(String purchasingDepartmentLine2Address) {
        this.purchasingDepartmentLine2Address = purchasingDepartmentLine2Address;
    }

    /**
     * Gets the purchasingDepartmentName attribute. 
     * @return Returns the purchasingDepartmentName.
     */
    public String getPurchasingDepartmentName() {
        return purchasingDepartmentName;
    }

    /**
     * Sets the purchasingDepartmentName attribute value.
     * @param purchasingDepartmentName The purchasingDepartmentName to set.
     */
    public void setPurchasingDepartmentName(String purchasingDepartmentName) {
        this.purchasingDepartmentName = purchasingDepartmentName;
    }

    /**
     * Gets the purchasingDepartmentStateCode attribute. 
     * @return Returns the purchasingDepartmentStateCode.
     */
    public String getPurchasingDepartmentStateCode() {
        return purchasingDepartmentStateCode;
    }

    /**
     * Sets the purchasingDepartmentStateCode attribute value.
     * @param purchasingDepartmentStateCode The purchasingDepartmentStateCode to set.
     */
    public void setPurchasingDepartmentStateCode(String purchasingDepartmentStateCode) {
        this.purchasingDepartmentStateCode = purchasingDepartmentStateCode;
    }

    /**
     * Gets the purchasingDepartmentZipCode attribute. 
     * @return Returns the purchasingDepartmentZipCode.
     */
    public String getPurchasingDepartmentZipCode() {
        return purchasingDepartmentZipCode;
    }

    /**
     * Sets the purchasingDepartmentZipCode attribute value.
     * @param purchasingDepartmentZipCode The purchasingDepartmentZipCode to set.
     */
    public void setPurchasingDepartmentZipCode(String purchasingDepartmentZipCode) {
        this.purchasingDepartmentZipCode = purchasingDepartmentZipCode;
    }

    /**
     * Gets the purchasingInstitutionName attribute. 
     * @return Returns the purchasingInstitutionName.
     */
    public String getPurchasingInstitutionName() {
        return purchasingInstitutionName;
    }

    /**
     * Sets the purchasingInstitutionName attribute value.
     * @param purchasingInstitutionName The purchasingInstitutionName to set.
     */
    public void setPurchasingInstitutionName(String purchasingInstitutionName) {
        this.purchasingInstitutionName = purchasingInstitutionName;
    }
    
    /**
     * Gets the campus attribute.
     * 
     * @return Returns the campus
     * 
     */
    public Campus getCampus() { 
        return campus;
    }

    /**
     * Sets the campus attribute.
     * 
     * @param campus The campus to set.
     * @deprecated
     */
    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    /**
     * Gets the purchasingDepartmentState attribute. 
     * @return Returns the purchasingDepartmentState.
     */
    public State getPurchasingDepartmentState() {
        return purchasingDepartmentState;
    }

    /**
     * Sets the purchasingDepartmentState attribute value.
     * @param purchasingDepartmentState The purchasingDepartmentState to set.
     * @deprecated
     */
    public void setPurchasingDepartmentState(State purchasingDepartmentState) {
        this.purchasingDepartmentState = purchasingDepartmentState;
    }

    /**
     * Gets the purchasingDepartmentCountry attribute. 
     * @return Returns the purchasingDepartmentCountry.
     */
    public Country getPurchasingDepartmentCountry() {
        return purchasingDepartmentCountry;
    }

    /**
     * Sets the purchasingDepartmentCountry attribute value.
     * @param purchasingDepartmentCountry The purchasingDepartmentCountry to set.
     * @deprecated
     */
    public void setPurchasingDepartmentCountry(Country purchasingDepartmentCountry) {
        this.purchasingDepartmentCountry = purchasingDepartmentCountry;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("campusCode", this.campusCode);
        return m;
    }    

}
