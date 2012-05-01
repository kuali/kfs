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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.county.CountyEbo;

public class TaxRegionCounty extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String postalCountryCode;
	protected String countyCode;
	protected String stateCode;
	protected String taxRegionCode;
	protected boolean active;

	protected CountryEbo country;
	protected CountyEbo county;
	protected TaxRegion taxRegion;

	public String getCountyCode() {
		return countyCode;
	}
	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}
	@Override
    public boolean isActive() {
		return active;
	}
	@Override
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
        if ( StringUtils.isBlank(postalCountryCode) || StringUtils.isBlank(stateCode) || StringUtils.isBlank(countyCode) ) {
            county = null;
        } else {
            if ( county == null
                    || !StringUtils.equals( county.getCode(),countyCode)
                    || !StringUtils.equals( county.getStateCode(), stateCode )
                    || !StringUtils.equals( county.getCountryCode(), postalCountryCode )) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountyEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(3);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, postalCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.STATE_CODE, stateCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, countyCode);
                    county = moduleService.getExternalizableBusinessObject(CountyEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
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
        if ( StringUtils.isBlank(postalCountryCode) ) {
            country = null;
        } else {
            if ( country == null || !StringUtils.equals( country.getCode(),postalCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, postalCountryCode);
                    country = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
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
