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
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;

public class TaxRegionPostalCode extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String postalCountryCode;
	protected String postalCode;
	protected String taxRegionCode;
	protected boolean active;

	protected CountryEbo country;
	protected PostalCodeEbo postalZip;
	protected TaxRegion taxRegion;

	public PostalCodeEbo getPostalZip() {
        if ( StringUtils.isBlank(postalCode) || StringUtils.isBlank(postalCountryCode) ) {
            postalZip = null;
        } else {
            if ( postalZip == null || !StringUtils.equals( postalZip.getCode(),postalCode) || !StringUtils.equals(postalZip.getCountryCode(), postalCountryCode ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, postalCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, postalCode);
                    postalZip = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
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
    @Override
    public boolean isActive() {
		return active;
	}
	@Override
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
    public void setCountry(CountryEbo country) {
        this.country = country;
    }
}
