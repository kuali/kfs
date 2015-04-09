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
import org.kuali.rice.location.framework.state.StateEbo;

public class TaxRegionState extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String postalCountryCode;
    protected String stateCode;
    protected String taxRegionCode;
    protected boolean active;

    protected CountryEbo country;
    protected StateEbo state;
    protected TaxRegion taxRegion;

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
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

    public String getTaxRegionCode() {
        return taxRegionCode;
    }

    public void setTaxRegionCode(String taxRegionCode) {
        this.taxRegionCode = taxRegionCode;
    }

    public StateEbo getState() {
        if ( StringUtils.isBlank(stateCode) || StringUtils.isBlank(postalCountryCode ) ) {
            state = null;
        } else {
            if ( state == null || !StringUtils.equals( state.getCode(),stateCode) || !StringUtils.equals(state.getCountryCode(), postalCountryCode ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, postalCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, stateCode);
                    state = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return state;
    }

    public void setState(StateEbo state) {
        this.state = state;
    }

    /**
     * Gets the postalCountryCode attribute.
     *
     * @return Returns the postalCountryCode.
     */
    public String getPostalCountryCode() {
        return postalCountryCode;
    }

    /**
     * Sets the postalCountryCode attribute value.
     *
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
