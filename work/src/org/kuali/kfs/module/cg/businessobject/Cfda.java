/*
 * Copyright 2007 The Kuali Foundation.
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
import org.kuali.module.integration.bo.ContractsAndGrantsCfda;

/**
 * Instances of this class refer to Catalog of Federal Domestic Assistance codes. Some of these codes are taken directly from a
 * government web-site. Additional codes can be created manually however. Codes can be updated automatically via the CfdaBatchStep.
 */
public class Cfda extends PersistableBusinessObjectBase implements ContractsAndGrantsCfda {

    private String cfdaNumber;
    private String cfdaProgramTitleName;
    private boolean cfdaStatusCode;
    private String cfdaMaintenanceTypeId;
    private boolean rowActiveIndicator;

    /**
     * Default constructor.
     */
    public Cfda() {
    }

    /**
     * Gets the cfdaNumber attribute.
     * 
     * @return Returns the cfdaNumber
     */
    public String getCfdaNumber() {
        return cfdaNumber;
    }

    /**
     * Sets the cfdaNumber attribute.
     * 
     * @param cfdaNumber The cfdaNumber to set.
     */
    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }


    /**
     * Gets the cfdaProgramTitleName attribute.
     * 
     * @return Returns the cfdaProgramTitleName
     */
    public String getCfdaProgramTitleName() {
        return cfdaProgramTitleName;
    }

    /**
     * Sets the cfdaProgramTitleName attribute.
     * 
     * @param cfdaProgramTitleName The cfdaProgramTitleName to set.
     */
    public void setCfdaProgramTitleName(String cfdaProgramTitleName) {
        this.cfdaProgramTitleName = cfdaProgramTitleName;
    }


    /**
     * Gets the cfdaStatusCode attribute.
     * 
     * @return Returns the cfdaStatusCode
     */
    public boolean getCfdaStatusCode() {
        return cfdaStatusCode;
    }

    /**
     * Sets the cfdaStatusCode attribute.
     * 
     * @param cfdaStatusCode The cfdaStatusCode to set.
     */
    public void setCfdaStatusCode(boolean cfdaStatusCode) {
        this.cfdaStatusCode = cfdaStatusCode;
    }


    /**
     * Gets the cfdaMaintenanceTypeId attribute.
     * 
     * @return Returns the cfdaMaintenanceTypeId
     */
    public String getCfdaMaintenanceTypeId() {
        return cfdaMaintenanceTypeId;
    }

    /**
     * Sets the cfdaMaintenanceTypeId attribute.
     * 
     * @param cfdaMaintenanceTypeId The cfdaMaintenanceTypeId to set.
     */
    public void setCfdaMaintenanceTypeId(String cfdaMaintenanceTypeId) {
        this.cfdaMaintenanceTypeId = cfdaMaintenanceTypeId;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("cfdaNumber", this.cfdaNumber);
        return m;
    }

    /**
     * This method gets the row active indicator value.
     * 
     * @return The value of the row active indicator attribute.
     */
    public boolean isRowActiveIndicator() {
        return rowActiveIndicator;
    }

    /**
     * This method sets the row active indicator for this object.
     * 
     * @param rowActiveIndicator The value to be assigned to the rowActiveIndicator attribute.
     */
    public void setRowActiveIndicator(boolean rowActiveIndicator) {
        this.rowActiveIndicator = rowActiveIndicator;
    }
}
