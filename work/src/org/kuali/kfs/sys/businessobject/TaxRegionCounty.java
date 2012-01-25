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
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.county.CountyService;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.county.CountyEbo;

public class TaxRegionCounty extends PersistableBusinessObjectBase implements MutableInactivatable {
	
    private String postalCountryCode;
	private String countyCode;
	private String stateCode;
	private String taxRegionCode;
	private boolean active;
	
	private CountryEbo country;
	private CountyEbo county;
	private TaxRegion taxRegion;
	
	public String getCountyCode() {
		return countyCode;
	}
	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public TaxRegion getTaxRegion() {
        return taxRegion;
    }
    public void setTaxRegion(TaxRegion taxRegion) {
        this.taxRegion = taxRegion;
    }
    public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public String getTaxRegionCode() {
		return taxRegionCode;
	}
	public void setTaxRegionCode(String taxRegionCode) {
		this.taxRegionCode = taxRegionCode;
	}
	
	public CountyEbo getCounty() {
	    county = (StringUtils.isBlank(postalCountryCode) || StringUtils.isBlank( stateCode) || StringUtils.isBlank( countyCode))?null:( county == null || !StringUtils.equals( county.getCountryCode(),postalCountryCode)|| !StringUtils.equals( county.getStateCode(), stateCode)|| !StringUtils.equals( county.getCode(), countyCode))?CountyEbo.from( SpringContext.getBean(CountyService.class).getCounty(postalCountryCode, stateCode, countyCode) ): county;
		return county;
	}
	public void setCounty(CountyEbo county) {
		this.county = county;
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
    public CountryEbo getCountry() {
        country = (postalCountryCode == null)?null:( country == null || !StringUtils.equals( country.getCode(),postalCountryCode))?CountryEbo.from( SpringContext.getBean(CountryService.class).getCountry(postalCountryCode) ): country;
        return country;
    }
    /**
     * Sets the country attribute value.
     * @param country The country to set.
     */
    public void setCountry(CountryEbo country) {
        this.country = country;
    }
}
