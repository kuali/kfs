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

package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class ResearchTypeCode extends PersistableBusinessObjectBase {

    private String researchTypeCode;
    private boolean dataObjectMaintenanceCodeActiveIndicator;
    private String researchTypeDescription;

    /**
     * Default constructor.
     */
    public ResearchTypeCode() {

    }

    /**
     * Gets the researchTypeCode attribute.
     * 
     * @return Returns the researchTypeCode
     */
    public String getResearchTypeCode() {
        return researchTypeCode;
    }

    /**
     * Sets the researchTypeCode attribute.
     * 
     * @param researchTypeCode The researchTypeCode to set.
     */
    public void setResearchTypeCode(String researchTypeCode) {
        this.researchTypeCode = researchTypeCode;
    }


    /**
     * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
     * 
     * @return Returns the dataObjectMaintenanceCodeActiveIndicator
     */
    public boolean isDataObjectMaintenanceCodeActiveIndicator() {
        return dataObjectMaintenanceCodeActiveIndicator;
    }

    /**
     * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
     * 
     * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
     */
    public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
        this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
    }


    /**
     * Gets the researchTypeDescription attribute.
     * 
     * @return Returns the researchTypeDescription
     */
    public String getResearchTypeDescription() {
        return researchTypeDescription;
    }

    /**
     * Sets the researchTypeDescription attribute.
     * 
     * @param researchTypeDescription The researchTypeDescription to set.
     */
    public void setResearchTypeDescription(String researchTypeDescription) {
        this.researchTypeDescription = researchTypeDescription;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("researchTypeCode", this.researchTypeCode);
        return m;
    }
}
