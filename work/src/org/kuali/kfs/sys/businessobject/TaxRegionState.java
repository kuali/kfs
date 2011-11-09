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
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;

public class TaxRegionState extends PersistableBusinessObjectBase implements Inactivatable {

    private String postalCountryCode;
    private String stateCode;
    private String taxRegionCode;
    private boolean active;

    private Country country;
    private State state;
    private TaxRegion taxRegion;

    public boolean isActive() {
        return active;
    }

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

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("stateCode", this.stateCode);
        m.put("taxRegionCode", this.taxRegionCode);
        return m;
    }

    public State getState() {
        state = (StringUtils.isBlank(postalCountryCode) || StringUtils.isBlank( stateCode))?null:( state == null || !StringUtils.equals( state.getCountryCode(),postalCountryCode)|| !StringUtils.equals( state.getCode(), stateCode))?SpringContext.getBean(StateService.class).getState(postalCountryCode, stateCode): state;
        return state;
    }

    public void setState(State state) {
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
    public Country getCountry() {
        country = (postalCountryCode == null)?null:( country == null || !StringUtils.equals( country.getCode(),postalCountryCode))?SpringContext.getBean(CountryService.class).getCountry(postalCountryCode): country;
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
