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

package org.kuali.kfs.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.chart.bo.Campus;

/**
 * 
 */
public class Building extends PersistableBusinessObjectBase {

    private String campusCode;
    private String buildingCode;
    private String buildingName;
    private String buildingStreetAddress;
    private String buildingAddressCityName;
    private String buildingAddressStateCode;
    private String buildingAddressZipCode;
    private String alternateBuildingCode;
    
    private Campus campus;
    private State buildingAddressState;
    private PostalZipCode buildingAddressZip;
    
    /**
     * Default constructor.
     */
    public Building() {

    }

    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode
     * 
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute.
     * 
     * @param campusCode The campusCode to set.
     * 
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }


    /**
     * Gets the buildingCode attribute.
     * 
     * @return Returns the buildingCode
     * 
     */
    public String getBuildingCode() {
        return buildingCode;
    }

    /**
     * Sets the buildingCode attribute.
     * 
     * @param buildingCode The buildingCode to set.
     * 
     */
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }


    /**
     * Gets the buildingName attribute.
     * 
     * @return Returns the buildingName
     * 
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * Sets the buildingName attribute.
     * 
     * @param buildingName The buildingName to set.
     * 
     */
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    /**
     * Gets the campus attribute.
     * 
     * @return Returns the campus.
     */
    public Campus getCampus() {
        return campus;
    }

    /**
     * Sets the campus attribute value.
     * 
     * @param campus The campus to set.
     */
    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    /**
     * Gets the alternateBuildingCode attribute. 
     * @return Returns the alternateBuildingCode.
     */
    public String getAlternateBuildingCode() {
        return alternateBuildingCode;
    }

    /**
     * Sets the alternateBuildingCode attribute value.
     * @param alternateBuildingCode The alternateBuildingCode to set.
     */
    public void setAlternateBuildingCode(String alternateBuildingCode) {
        this.alternateBuildingCode = alternateBuildingCode;
    }

    /**
     * Gets the buildingAddressCityName attribute. 
     * @return Returns the buildingAddressCityName.
     */
    public String getBuildingAddressCityName() {
        return buildingAddressCityName;
    }

    /**
     * Sets the buildingAddressCityName attribute value.
     * @param buildingAddressCityName The buildingAddressCityName to set.
     */
    public void setBuildingAddressCityName(String buildingAddressCityName) {
        this.buildingAddressCityName = buildingAddressCityName;
    }

    /**
     * Gets the buildingAddressStateCode attribute. 
     * @return Returns the buildingAddressStateCode.
     */
    public String getBuildingAddressStateCode() {
        return buildingAddressStateCode;
    }

    /**
     * Sets the buildingAddressStateCode attribute value.
     * @param buildingAddressStateCode The buildingAddressStateCode to set.
     */
    public void setBuildingAddressStateCode(String buildingAddressStateCode) {
        this.buildingAddressStateCode = buildingAddressStateCode;
    }

    /**
     * Gets the buildingAddressZipCode attribute. 
     * @return Returns the buildingAddressZipCode.
     */
    public String getBuildingAddressZipCode() {
        return buildingAddressZipCode;
    }

    /**
     * Sets the buildingAddressZipCode attribute value.
     * @param buildingAddressZipCode The buildingAddressZipCode to set.
     */
    public void setBuildingAddressZipCode(String buildingAddressZipCode) {
        this.buildingAddressZipCode = buildingAddressZipCode;
    }

    /**
     * Gets the buildingStreetAddress attribute. 
     * @return Returns the buildingStreetAddress.
     */
    public String getBuildingStreetAddress() {
        return buildingStreetAddress;
    }

    /**
     * Sets the buildingStreetAddress attribute value.
     * @param buildingStreetAddress The buildingStreetAddress to set.
     */
    public void setBuildingStreetAddress(String buildingStreetAddress) {
        this.buildingStreetAddress = buildingStreetAddress;
    }

    /**
     * Gets the buildingAddressState attribute. 
     * @return Returns the buildingAddressState.
     */
    public State getBuildingAddressState() {
        return buildingAddressState;
    }

    /**
     * Sets the buildingAddressState attribute value.
     * @param buildingAddressState The buildingAddressState to set.
     * @deprecated
     */
    public void setBuildingAddressState(State buildingAddressState) {
        this.buildingAddressState = buildingAddressState;
    }

    /**
     * Gets the buildingAddressZip attribute. 
     * @return Returns the buildingAddressZip.
     */
    public PostalZipCode getBuildingAddressZip() {
        return buildingAddressZip;
    }

    /**
     * Sets the buildingAddressZip attribute value.
     * @param buildingAddressZip The buildingAddressZip to set.
     * @deprecated
     */
    public void setBuildingAddressZip(PostalZipCode buildingAddressZip) {
        this.buildingAddressZip = buildingAddressZip;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("campusCode", this.campusCode);
        m.put("buildingCode", this.buildingCode);
        return m;
    }
    
    
}
