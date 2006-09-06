/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Campus;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BillingAddress extends BusinessObjectBase {

	private String billingCampusCode;
	private String billingName;
	private String billingLine1Address;
	private String billingLine2Address;
	private String billingCityName;
	private String billingStateCode;
	private String billingPostalCode;
	private String billingCountryCode;
	private String billingPhoneNumber;
    private boolean dataObjectMaintenanceCodeActiveIndicator;
    
    private Campus billingCampus;

	/**
	 * Default constructor.
	 */
	public BillingAddress() {

	}

	/**
	 * Gets the billingCampusCode attribute.
	 * 
	 * @return - Returns the billingCampusCode
	 * 
	 */
	public String getBillingCampusCode() { 
		return billingCampusCode;
	}

	/**
	 * Sets the billingCampusCode attribute.
	 * 
	 * @param - billingCampusCode The billingCampusCode to set.
	 * 
	 */
	public void setBillingCampusCode(String billingCampusCode) {
		this.billingCampusCode = billingCampusCode;
	}


	/**
	 * Gets the billingName attribute.
	 * 
	 * @return - Returns the billingName
	 * 
	 */
	public String getBillingName() { 
		return billingName;
	}

	/**
	 * Sets the billingName attribute.
	 * 
	 * @param - billingName The billingName to set.
	 * 
	 */
	public void setBillingName(String billingName) {
		this.billingName = billingName;
	}


	/**
	 * Gets the billingLine1Address attribute.
	 * 
	 * @return - Returns the billingLine1Address
	 * 
	 */
	public String getBillingLine1Address() { 
		return billingLine1Address;
	}

	/**
	 * Sets the billingLine1Address attribute.
	 * 
	 * @param - billingLine1Address The billingLine1Address to set.
	 * 
	 */
	public void setBillingLine1Address(String billingLine1Address) {
		this.billingLine1Address = billingLine1Address;
	}


	/**
	 * Gets the billingLine2Address attribute.
	 * 
	 * @return - Returns the billingLine2Address
	 * 
	 */
	public String getBillingLine2Address() { 
		return billingLine2Address;
	}

	/**
	 * Sets the billingLine2Address attribute.
	 * 
	 * @param - billingLine2Address The billingLine2Address to set.
	 * 
	 */
	public void setBillingLine2Address(String billingLine2Address) {
		this.billingLine2Address = billingLine2Address;
	}


	/**
	 * Gets the billingCityName attribute.
	 * 
	 * @return - Returns the billingCityName
	 * 
	 */
	public String getBillingCityName() { 
		return billingCityName;
	}

	/**
	 * Sets the billingCityName attribute.
	 * 
	 * @param - billingCityName The billingCityName to set.
	 * 
	 */
	public void setBillingCityName(String billingCityName) {
		this.billingCityName = billingCityName;
	}


	/**
	 * Gets the billingStateCode attribute.
	 * 
	 * @return - Returns the billingStateCode
	 * 
	 */
	public String getBillingStateCode() { 
		return billingStateCode;
	}

	/**
	 * Sets the billingStateCode attribute.
	 * 
	 * @param - billingStateCode The billingStateCode to set.
	 * 
	 */
	public void setBillingStateCode(String billingStateCode) {
		this.billingStateCode = billingStateCode;
	}


	/**
	 * Gets the billingPostalCode attribute.
	 * 
	 * @return - Returns the billingPostalCode
	 * 
	 */
	public String getBillingPostalCode() { 
		return billingPostalCode;
	}

	/**
	 * Sets the billingPostalCode attribute.
	 * 
	 * @param - billingPostalCode The billingPostalCode to set.
	 * 
	 */
	public void setBillingPostalCode(String billingPostalCode) {
		this.billingPostalCode = billingPostalCode;
	}


	/**
	 * Gets the billingCountryCode attribute.
	 * 
	 * @return - Returns the billingCountryCode
	 * 
	 */
	public String getBillingCountryCode() { 
		return billingCountryCode;
	}

	/**
	 * Sets the billingCountryCode attribute.
	 * 
	 * @param - billingCountryCode The billingCountryCode to set.
	 * 
	 */
	public void setBillingCountryCode(String billingCountryCode) {
		this.billingCountryCode = billingCountryCode;
	}


	/**
	 * Gets the billingPhoneNumber attribute.
	 * 
	 * @return - Returns the billingPhoneNumber
	 * 
	 */
	public String getBillingPhoneNumber() { 
		return billingPhoneNumber;
	}

	/**
	 * Sets the billingPhoneNumber attribute.
	 * 
	 * @param - billingPhoneNumber The billingPhoneNumber to set.
	 * 
	 */
	public void setBillingPhoneNumber(String billingPhoneNumber) {
		this.billingPhoneNumber = billingPhoneNumber;
	}


	/**
	 * Gets the billingCampus attribute.
	 * 
	 * @return - Returns the billingCampus
	 * 
	 */
	public Campus getBillingCampus() { 
		return billingCampus;
	}

	/**
	 * Sets the billingCampus attribute.
	 * 
	 * @param - billingCampus The billingCampus to set.
	 * @deprecated
	 */
	public void setBillingCampus(Campus billingCampus) {
		this.billingCampus = billingCampus;
	}

    /**
     * Gets the dataObjectMaintenanceCodeActiveIndicator attribute. 
     * @return Returns the dataObjectMaintenanceCodeActiveIndicator.
     */
    public boolean isDataObjectMaintenanceCodeActiveIndicator() {
        return dataObjectMaintenanceCodeActiveIndicator;
    }

    /**
     * Sets the dataObjectMaintenanceCodeActiveIndicator attribute value.
     * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
     */
    public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
        this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
    }

    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("billingCampusCode", this.billingCampusCode);
        return m;
    }


}
