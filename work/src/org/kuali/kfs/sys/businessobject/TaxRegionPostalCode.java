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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.postalcode.PostalCodeService;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;

public class TaxRegionPostalCode extends PersistableBusinessObjectBase implements MutableInactivatable {
	
    private String postalCountryCode; 
	private String postalCode;
	private String taxRegionCode;
	private boolean active;
	
	private CountryEbo country;
	private PostalCodeEbo postalZip;
	private TaxRegion taxRegion;
	
	public PostalCodeEbo getPostalZip() {
	    postalZip = (StringUtils.isBlank(postalCountryCode) || StringUtils.isBlank( postalCode))?null:( postalZip == null || !StringUtils.equals( postalZip.getCountryCode(),postalCountryCode)|| !StringUtils.equals( postalZip.getCode(), postalCode))?PostalCodeEbo.from( SpringContext.getBean(PostalCodeService.class).getPostalCode(postalCountryCode, postalCode) ): postalZip;
		return postalZip;
	}
	public void setPostalZip(PostalCodeEbo postalZip) {
		this.postalZip = postalZip;
	}
	public TaxRegion getTaxRegion() {
        if(StringUtils.isNotBlank(taxRegionCode) && ObjectUtils.isNull(taxRegion)){
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
    public String getPostalCountryCode() {
        return postalCountryCode;
    }
    public void setPostalCountryCode(String postalCountryCode) {
        this.postalCountryCode = postalCountryCode;
    }
    public CountryEbo getCountry() {
        country = (postalCountryCode == null)?null:( country == null || !StringUtils.equals( country.getCode(),postalCountryCode))?CountryEbo.from( SpringContext.getBean(CountryService.class).getCountry(postalCountryCode) ): country;
        return country;
    }
    public void setCountry(CountryEbo country) {
        this.country = country;
    }
}
