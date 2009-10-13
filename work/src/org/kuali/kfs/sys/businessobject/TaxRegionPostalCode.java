/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.PostalCodeService;
import org.kuali.rice.kns.service.StateService;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.bo.PostalCode;
import org.kuali.rice.kns.util.ObjectUtils;

public class TaxRegionPostalCode extends PersistableBusinessObjectBase implements Inactivateable {
	
    private String postalCountryCode; 
	private String postalCode;
	private String taxRegionCode;
	private boolean active;
	
	private Country country;
	private PostalCode postalZip;
	private TaxRegion taxRegion;
	
	public PostalCode getPostalZip() {
	    postalZip = SpringContext.getBean(PostalCodeService.class).getByPrimaryIdIfNecessary(postalCountryCode, postalCode, postalZip);
		return postalZip;
	}
	public void setPostalZip(PostalCode postalZip) {
		this.postalZip = postalZip;
	}
	public TaxRegion getTaxRegion() {
        if(ObjectUtils.isNull(taxRegion)){
            this.refreshReferenceObject("taxRegion");
        }	    
        return taxRegion;
    }
    public void setTaxRegion(TaxRegion taxRegion) {
        this.taxRegion = taxRegion;
    }
    public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getTaxRegionCode() {
		return taxRegionCode;
	}
	public void setTaxRegionCode(String taxRegionCode) {
		this.taxRegionCode = taxRegionCode;
	}
	
	protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("postalCode", this.postalCode);
        m.put("taxRegionCode", this.taxRegionCode);
        return m;
    }
    /**
     * Gets the postalCountryCode attribute. 
     * @return Returns the postalCountryCode.
     */
    public String getPostalCountryCode() {
        return postalCountryCode;
    }
    /**
     * Sets the postalCountryCode attribute value.
     * @param postalCountryCode The postalCountryCode to set.
     */
    public void setPostalCountryCode(String postalCountryCode) {
        this.postalCountryCode = postalCountryCode;
    }
    /**
     * Gets the country attribute. 
     * @return Returns the country.
     */
    public Country getCountry() {
        country = SpringContext.getBean(CountryService.class).getByPrimaryIdIfNecessary(postalCountryCode, country);
        return country;
    }
    /**
     * Sets the country attribute value.
     * @param country The country to set.
     */
    public void setCountry(Country country) {
        this.country = country;
    }
}
