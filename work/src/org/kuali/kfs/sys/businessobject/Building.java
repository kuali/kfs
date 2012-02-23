/*
 * Copyright 2007 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.location.api.campus.CampusService;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.postalcode.PostalCodeService;
import org.kuali.rice.location.api.state.StateService;
import org.kuali.rice.location.framework.campus.CampusEbo;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;

/**
 * 
 */
public class Building extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String campusCode;
    protected String buildingCode;
    protected String buildingName;
    protected String buildingStreetAddress;
    protected String buildingAddressCityName;
    protected String buildingAddressStateCode;
    protected String buildingAddressZipCode;
    protected String alternateBuildingCode;
    protected boolean active;
    protected String buildingAddressCountryCode;
    
    protected CampusEbo campus;
    protected StateEbo buildingAddressState;
    protected PostalCodeEbo buildingAddressZip;
    protected CountryEbo buildingAddressCountry;
 
    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute.
     * 
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }


    /**
     * Gets the buildingCode attribute.
     * 
     * @return Returns the buildingCode
     */
    public String getBuildingCode() {
        return buildingCode;
    }

    /**
     * Sets the buildingCode attribute.
     * 
     * @param buildingCode The buildingCode to set.
     */
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }


    /**
     * Gets the buildingName attribute.
     * 
     * @return Returns the buildingName
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * Sets the buildingName attribute.
     * 
     * @param buildingName The buildingName to set.
     */
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    /**
     * Gets the campus attribute.
     * 
     * @return Returns the campus.
     */
    public CampusEbo getCampus() {
        return campus = StringUtils.isBlank( campusCode)?null:((campus!=null && campus.getCode().equals( campusCode))?campus:CampusEbo.from(SpringContext.getBean(CampusService.class).getCampus( campusCode)));
    }

    /**
     * Sets the campus attribute value.
     * 
     * @param campus The campus to set.
     */
    public void setCampus(CampusEbo campus) {
        this.campus = campus;
    }

    /**
     * Gets the alternateBuildingCode attribute.
     * 
     * @return Returns the alternateBuildingCode.
     */
    public String getAlternateBuildingCode() {
        return alternateBuildingCode;
    }

    /**
     * Sets the alternateBuildingCode attribute value.
     * 
     * @param alternateBuildingCode The alternateBuildingCode to set.
     */
    public void setAlternateBuildingCode(String alternateBuildingCode) {
        this.alternateBuildingCode = alternateBuildingCode;
    }

    /**
     * Gets the buildingAddressCityName attribute.
     * 
     * @return Returns the buildingAddressCityName.
     */
    public String getBuildingAddressCityName() {
        return buildingAddressCityName;
    }

    /**
     * Sets the buildingAddressCityName attribute value.
     * 
     * @param buildingAddressCityName The buildingAddressCityName to set.
     */
    public void setBuildingAddressCityName(String buildingAddressCityName) {
        this.buildingAddressCityName = buildingAddressCityName;
    }

    /**
     * Gets the buildingAddressStateCode attribute.
     * 
     * @return Returns the buildingAddressStateCode.
     */
    public String getBuildingAddressStateCode() {
        return buildingAddressStateCode;
    }

    /**
     * Sets the buildingAddressStateCode attribute value.
     * 
     * @param buildingAddressStateCode The buildingAddressStateCode to set.
     */
    public void setBuildingAddressStateCode(String buildingAddressStateCode) {
        this.buildingAddressStateCode = buildingAddressStateCode;
    }

    /**
     * Gets the buildingAddressZipCode attribute.
     * 
     * @return Returns the buildingAddressZipCode.
     */
    public String getBuildingAddressZipCode() {
        return buildingAddressZipCode;
    }

    /**
     * Sets the buildingAddressZipCode attribute value.
     * 
     * @param buildingAddressZipCode The buildingAddressZipCode to set.
     */
    public void setBuildingAddressZipCode(String buildingAddressZipCode) {
        this.buildingAddressZipCode = buildingAddressZipCode;
    }

    /**
     * Gets the buildingStreetAddress attribute.
     * 
     * @return Returns the buildingStreetAddress.
     */
    public String getBuildingStreetAddress() {
        return buildingStreetAddress;
    }

    /**
     * Sets the buildingStreetAddress attribute value.
     * 
     * @param buildingStreetAddress The buildingStreetAddress to set.
     */
    public void setBuildingStreetAddress(String buildingStreetAddress) {
        this.buildingStreetAddress = buildingStreetAddress;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the buildingAddressState attribute.
     * 
     * @return Returns the buildingAddressState.
     */
    public StateEbo getBuildingAddressState() {
        buildingAddressState = (StringUtils.isBlank(buildingAddressCountryCode) || StringUtils.isBlank( buildingAddressStateCode))?null:( buildingAddressState == null || !StringUtils.equals( buildingAddressState.getCountryCode(),buildingAddressCountryCode)|| !StringUtils.equals( buildingAddressState.getCode(), buildingAddressStateCode))?StateEbo.from(SpringContext.getBean(StateService.class).getState(buildingAddressCountryCode, buildingAddressStateCode)): buildingAddressState;
        return buildingAddressState;
    }

    /**
     * Sets the buildingAddressState attribute value.
     * 
     * @param buildingAddressState The buildingAddressState to set.
     * @deprecated
     */
    public void setBuildingAddressState(StateEbo buildingAddressState) {
        this.buildingAddressState = buildingAddressState;
    }

    /**
     * Gets the buildingAddressZip attribute.
     * 
     * @return Returns the buildingAddressZip.
     */
    public PostalCodeEbo getBuildingAddressZip() {
        buildingAddressZip = (StringUtils.isBlank(buildingAddressCountryCode) || StringUtils.isBlank( buildingAddressZipCode))?null:( buildingAddressZip == null || !StringUtils.equals( buildingAddressZip.getCountryCode(),buildingAddressCountryCode)|| !StringUtils.equals( buildingAddressZip.getCode(), buildingAddressZipCode))? PostalCodeEbo.from(SpringContext.getBean(PostalCodeService.class).getPostalCode(buildingAddressCountryCode, buildingAddressZipCode)): buildingAddressZip;
        return buildingAddressZip;
    }

    /**
     * Sets the buildingAddressZip attribute value.
     * 
     * @param buildingAddressZip The buildingAddressZip to set.
     * @deprecated
     */
    public void setBuildingAddressZip(PostalCodeEbo buildingAddressZip) {
        this.buildingAddressZip = buildingAddressZip;
    }
    
    /**
     * Gets the buildingAddressCountryCode attribute. 
     * @return Returns the buildingAddressCountryCode.
     */
    public String getBuildingAddressCountryCode() {
        return buildingAddressCountryCode;
    }

    /**
     * Sets the buildingAddressCountryCode attribute value.
     * @param buildingAddressCountryCode The buildingAddressCountryCode to set.
     */
    public void setBuildingAddressCountryCode(String buildingAddressCountryCode) {
        this.buildingAddressCountryCode = buildingAddressCountryCode;
    }

    /**
     * Gets the buildingAddressCountry attribute. 
     * @return Returns the buildingAddressCountry.
     */
    public CountryEbo getBuildingAddressCountry() {
        buildingAddressCountry = (buildingAddressCountryCode == null)?null:( buildingAddressCountry == null || !StringUtils.equals( buildingAddressCountry.getCode(),buildingAddressCountryCode))?CountryEbo.from(SpringContext.getBean(CountryService.class).getCountry(buildingAddressCountryCode)): buildingAddressCountry;
        return buildingAddressCountry;
    }

    /**
     * Sets the buildingAddressCountry attribute value.
     * @param buildingAddressCountry The buildingAddressCountry to set.
     */
    public void setBuildingAddressCountry(CountryEbo buildingAddressCountry) {
        this.buildingAddressCountry = buildingAddressCountry;
    }

}
