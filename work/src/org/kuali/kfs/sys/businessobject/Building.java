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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
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
        if ( StringUtils.isBlank(campusCode) ) {
            campus = null;
        } else {
            if ( campus == null || !StringUtils.equals( campus.getCode(),campusCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CampusEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, campusCode);
                    campus = moduleService.getExternalizableBusinessObject(CampusEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return campus;
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
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the buildingAddressState attribute.
     *
     * @return Returns the buildingAddressState.
     */
    public StateEbo getBuildingAddressState() {
        if ( StringUtils.isBlank(buildingAddressStateCode) || StringUtils.isBlank(buildingAddressCountryCode ) ) {
            buildingAddressState = null;
        } else {
            if ( buildingAddressState == null || !StringUtils.equals( buildingAddressState.getCode(),buildingAddressStateCode) || !StringUtils.equals(buildingAddressState.getCountryCode(), buildingAddressCountryCode ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, buildingAddressCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, buildingAddressStateCode);
                    buildingAddressState = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
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
        if ( StringUtils.isBlank(buildingAddressZipCode) || StringUtils.isBlank(buildingAddressCountryCode) ) {
            buildingAddressZip = null;
        } else {
            if ( buildingAddressZip == null || !StringUtils.equals( buildingAddressZip.getCode(),buildingAddressZipCode) || !StringUtils.equals(buildingAddressZip.getCountryCode(), buildingAddressCountryCode ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, buildingAddressCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, buildingAddressZipCode);
                    buildingAddressZip = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
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
        if ( StringUtils.isBlank(buildingAddressCountryCode) ) {
            buildingAddressCountry = null;
        } else {
            if ( buildingAddressCountry == null || !StringUtils.equals( buildingAddressCountry.getCode(),buildingAddressCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, buildingAddressCountryCode);
                    buildingAddressCountry = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
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
