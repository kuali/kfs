/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.State;

/**
 * 
 */
public class Subcontractor extends PersistableBusinessObjectBase {

    private String subcontractorNumber;
    private String subcontractorName;
    private String subcontractorAddressLine1;
    private String subcontractorAddressLine2;
    private String subcontractorCity;
    private String subcontractorStateCode;
    private String subcontractorZipCode;
    private String subcontractorCountryCode;
    private boolean active;
    
    private State subcontractorState;
    private Country subcontractorCountry;
    
    /**
     * Default no-arg constructor.
     */
    public Subcontractor() {

    }

    /**
     * Gets the subcontractorNumber attribute.
     * 
     * @return Returns the subcontractorNumber
     * 
     */
    public String getSubcontractorNumber() {
        return subcontractorNumber;
    }

    /**
     * Sets the subcontractorNumber attribute.
     * 
     * @param subcontractorNumber The subcontractorNumber to set.
     * 
     */
    public void setSubcontractorNumber(String subcontractorNumber) {
        this.subcontractorNumber = subcontractorNumber;
    }

    /**
     * Gets the subcontractorName attribute.
     * 
     * @return Returns the subcontractorName
     * 
     */
    public String getSubcontractorName() {
        return subcontractorName;
    }

    /**
     * Sets the subcontractorName attribute.
     * 
     * @param subcontractorName The subcontractorName to set.
     * 
     */
    public void setSubcontractorName(String subcontractorName) {
        this.subcontractorName = subcontractorName;
    }

    /**
     * Gets the subcontractorAddressLine1 attribute.
     * 
     * @return Returns the subcontractorAddressLine1
     * 
     */
    public String getSubcontractorAddressLine1() {
        return subcontractorAddressLine1;
    }

    /**
     * Sets the subcontractorAddressLine1 attribute.
     * 
     * @param subcontractorAddressLine1 The subcontractorAddressLine1 to set.
     * 
     */
    public void setSubcontractorAddressLine1(String subcontractorAddressLine1) {
        this.subcontractorAddressLine1 = subcontractorAddressLine1;
    }

    /**
     * Gets the subcontractorAddressLine2 attribute.
     * 
     * @return Returns the subcontractorAddressLine2
     * 
     */
    public String getSubcontractorAddressLine2() {
        return subcontractorAddressLine2;
    }

    /**
     * Sets the subcontractorAddressLine2 attribute.
     * 
     * @param subcontractorAddressLine2 The subcontractorAddressLine2 to set.
     * 
     */
    public void setSubcontractorAddressLine2(String subcontractorAddressLine2) {
        this.subcontractorAddressLine2 = subcontractorAddressLine2;
    }

    /**
     * Gets the subcontractorCity attribute.
     * 
     * @return Returns the subcontractorCity
     * 
     */
    public String getSubcontractorCity() {
        return subcontractorCity;
    }

    /**
     * Sets the subcontractorCity attribute.
     * 
     * @param subcontractorCity The subcontractorCity to set.
     * 
     */
    public void setSubcontractorCity(String subcontractorCity) {
        this.subcontractorCity = subcontractorCity;
    }

    /**
     * Gets the subcontractorStateCode attribute.
     * 
     * @return Returns the subcontractorStateCode.
     */
    public String getSubcontractorStateCode() {
        return subcontractorStateCode;
    }

    /**
     * Sets the subcontractorStateCode attribute value.
     * 
     * @param subcontractorStateCode The subcontractorStateCode to set.
     */
    public void setSubcontractorStateCode(String subcontractorStateCode) {
        this.subcontractorStateCode = subcontractorStateCode;
    }

    /**
     * Gets the subcontractorZipCode attribute.
     * 
     * @return Returns the subcontractorZipCode
     * 
     */
    public String getSubcontractorZipCode() {
        return subcontractorZipCode;
    }

    /**
     * Sets the subcontractorZipCode attribute.
     * 
     * @param subcontractorZipCode The subcontractorZipCode to set.
     * 
     */
    public void setSubcontractorZipCode(String subcontractorZipCode) {
        this.subcontractorZipCode = subcontractorZipCode;
    }

    /**
     * Gets the subcontractorCountryCode attribute.
     * 
     * @return Returns the subcontractorCountryCode
     * 
     */
    public String getSubcontractorCountryCode() {
        return subcontractorCountryCode;
    }

    /**
     * Sets the subcontractorCountryCode attribute.
     * 
     * @param subcontractorCountryCode The subcontractorCountryCode to set.
     * 
     */
    public void setSubcontractorCountryCode(String subcontractorCountryCode) {
        this.subcontractorCountryCode = subcontractorCountryCode;
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("subcontractorNumber", this.getSubcontractorNumber());
        return m;
    }

    /**
     * 
     * This method...
     * @return
     */
    public Country getSubcontractorCountry() {
        return subcontractorCountry;
    }

    /**
     * 
     * This method...
     * @param country
     */
    public void setSubcontractorCountry(Country country) {
        this.subcontractorCountry = country;
    }

    /**
     * 
     * This method...
     * @return
     */
    public State getSubcontractorState() {
        return subcontractorState;
    }

    /**
     * 
     * This method...
     * @param state
     */
    public void setSubcontractorState(State state) {
        this.subcontractorState = state;
    }

}
