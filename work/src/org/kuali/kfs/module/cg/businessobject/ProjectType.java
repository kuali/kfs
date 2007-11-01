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

package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class ProjectType extends PersistableBusinessObjectBase {

    private String projectTypeCode;
    private boolean dataObjectMaintenanceCodeActiveIndicator;
    private String projectTypeDescription;
    private Integer sortNumber;

    /**
     * Default constructor.
     */
    public ProjectType() {

    }

    /**
     * Gets the projectTypeCode attribute.
     * 
     * @return Returns the projectTypeCode
     */
    public String getProjectTypeCode() {
        return projectTypeCode;
    }

    /**
     * Sets the projectTypeCode attribute.
     * 
     * @param projectTypeCode The projectTypeCode to set.
     */
    public void setProjectTypeCode(String projectTypeCode) {
        this.projectTypeCode = projectTypeCode;
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
     * Gets the projectTypeDescription attribute.
     * 
     * @return Returns the projectTypeDescription
     */
    public String getProjectTypeDescription() {
        return projectTypeDescription;
    }

    /**
     * Sets the projectTypeDescription attribute.
     * 
     * @param projectTypeDescription The projectTypeDescription to set.
     */
    public void setProjectTypeDescription(String projectTypeDescription) {
        this.projectTypeDescription = projectTypeDescription;
    }


    /**
     * Gets the sortNumber attribute.
     * 
     * @return Returns the sortNumber
     */
    public Integer getSortNumber() {
        return sortNumber;
    }

    /**
     * Sets the sortNumber attribute.
     * 
     * @param sortNumber The sortNumber to set.
     */
    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("projectTypeCode", this.projectTypeCode);
        return m;
    }
}
