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

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Subcontractor extends BusinessObjectBase {

	private String number;
	private String name;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String stateCode;
	private String zipCode;
	private String countryCode;

	/**
	 * Default no-arg constructor.
	 */
	public Subcontractor() {

	}

	/**
	 * Gets the number attribute.
	 * 
	 * @return - Returns the number
	 * 
	 */
	public String getNumber() { 
		return number;
	}
	
	/**
	 * Sets the number attribute.
	 * 
	 * @param - number The number to set.
	 * 
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * Gets the name attribute.
	 * 
	 * @return - Returns the name
	 * 
	 */
	public String getName() { 
		return name;
	}
	
	/**
	 * Sets the name attribute.
	 * 
	 * @param - name The name to set.
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the addressLine attribute.
	 * 
	 * @return - Returns the addressLine
	 * 
	 */
	public String getAddressLine1() { 
		return addressLine1;
	}
	
	/**
	 * Sets the addressLine attribute.
	 * 
	 * @param - addressLine The addressLine to set.
	 * 
	 */
	public void setAddressLine1(String addressLine) {
		this.addressLine1 = addressLine;
	}

	/**
	 * Gets the addressLine2 attribute.
	 * 
	 * @return - Returns the addressLine2
	 * 
	 */
	public String getAddressLine2() { 
		return addressLine2;
	}
	
	/**
	 * Sets the addressLine2 attribute.
	 * 
	 * @param - addressLine2 The addressLine2 to set.
	 * 
	 */
	public void setAddressLine2(String addressLine) {
		this.addressLine2 = addressLine;
	}

	/**
	 * Gets the city attribute.
	 * 
	 * @return - Returns the city
	 * 
	 */
	public String getCity() { 
		return city;
	}
	
	/**
	 * Sets the city attribute.
	 * 
	 * @param - city The city to set.
	 * 
	 */
	public void setCity(String city) {
		this.city = city;
	}

    /**
     * Gets the stateCode attribute. 
     * @return Returns the stateCode.
     */
    public String getStateCode() {
        return stateCode;
    }
    
    /**
     * Sets the stateCode attribute value.
     * @param stateCode The stateCode to set.
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }
    
	/**
	 * Gets the zipCode attribute.
	 * 
	 * @return - Returns the zipCode
	 * 
	 */
	public String getZipCode() { 
		return zipCode;
	}
	
	/**
	 * Sets the zipCode attribute.
	 * 
	 * @param - zipCode The zipCode to set.
	 * 
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * Gets the countryCode attribute.
	 * 
	 * @return - Returns the countryCode
	 * 
	 */
	public String getCountryCode() { 
		return countryCode;
	}
	
	/**
	 * Sets the countryCode attribute.
	 * 
	 * @param - countryCode The countryCode to set.
	 * 
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
			LinkedHashMap m = new LinkedHashMap();

			m.put("number", getNumber());
			return m;
	}
}
