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

/**
 * Billing Address Business Object.
 */
public class BillingAddress extends PersistableBusinessObjectBase {

	private String billingCampusCode;
	private String billingName;
	private String billingLine1Address;
	private String billingLine2Address;
	private String billingCityName;
	private String billingStateCode;
	private String billingPostalCode;
	private String billingCountryCode;
	private String billingPhoneNumber;
    private boolean active;
    
    private Campus billingCampus;

    /**
     * Default constructor.
     */
	public BillingAddress() {

	}

	public String getBillingCampusCode() { 
		return billingCampusCode;
	}

	public void setBillingCampusCode(String billingCampusCode) {
		this.billingCampusCode = billingCampusCode;
	}

	public String getBillingName() { 
		return billingName;
	}

	public void setBillingName(String billingName) {
		this.billingName = billingName;
	}

	public String getBillingLine1Address() { 
		return billingLine1Address;
	}

	public void setBillingLine1Address(String billingLine1Address) {
		this.billingLine1Address = billingLine1Address;
	}

	public String getBillingLine2Address() { 
		return billingLine2Address;
	}

	public void setBillingLine2Address(String billingLine2Address) {
		this.billingLine2Address = billingLine2Address;
	}

	public String getBillingCityName() { 
		return billingCityName;
	}

	public void setBillingCityName(String billingCityName) {
		this.billingCityName = billingCityName;
	}

	public String getBillingStateCode() { 
		return billingStateCode;
	}

	public void setBillingStateCode(String billingStateCode) {
		this.billingStateCode = billingStateCode;
	}

	public String getBillingPostalCode() { 
		return billingPostalCode;
	}

	public void setBillingPostalCode(String billingPostalCode) {
		this.billingPostalCode = billingPostalCode;
	}

    public String getBillingCountryCode() { 
		return billingCountryCode;
	}

	public void setBillingCountryCode(String billingCountryCode) {
		this.billingCountryCode = billingCountryCode;
	}

    public String getBillingPhoneNumber() { 
		return billingPhoneNumber;
	}

	public void setBillingPhoneNumber(String billingPhoneNumber) {
		this.billingPhoneNumber = billingPhoneNumber;
	}

	public Campus getBillingCampus() { 
		return billingCampus;
	}

	/**
	 * @deprecated
	 */
	public void setBillingCampus(Campus billingCampus) {
		this.billingCampus = billingCampus;
	}

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("billingCampusCode", this.billingCampusCode);
        return m;
    }

}
