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
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PersonRole extends PersistableBusinessObjectBase {

    private String personRoleCode;
    private boolean dataObjectMaintenanceCodeActiveIndicator;
    private String personRoleDescription;
    private Integer personRoleSortNumber;

    /**
     * Default constructor.
     */
    public PersonRole() {

    }

    /**
     * Gets the personRoleCode attribute.
     * 
     * @return Returns the personRoleCode
     */
    public String getPersonRoleCode() {
        return personRoleCode;
    }

    /**
     * Sets the personRoleCode attribute.
     * 
     * @param personRoleCode The personRoleCode to set.
     */
    public void setPersonRoleCode(String personRoleCode) {
        this.personRoleCode = personRoleCode;
    }

    /**
     * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
     * 
     * @return Returns the dataObjectMaintenanceCodeActiveIndicator.
     */
    public boolean isDataObjectMaintenanceCodeActiveIndicator() {
        return dataObjectMaintenanceCodeActiveIndicator;
    }

    /**
     * Sets the dataObjectMaintenanceCodeActiveIndicator attribute value.
     * 
     * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
     */
    public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
        this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
    }

    /**
     * Gets the personRoleDescription attribute.
     * 
     * @return Returns the personRoleDescription
     */
    public String getPersonRoleDescription() {
        return personRoleDescription;
    }

    /**
     * Sets the personRoleDescription attribute.
     * 
     * @param personRoleDescription The personRoleDescription to set.
     */
    public void setPersonRoleDescription(String personRoleDescription) {
        this.personRoleDescription = personRoleDescription;
    }

    /**
     * Gets the personRoleSortNumber attribute.
     * 
     * @return Returns the personRoleSortNumber.
     */
    public Integer getPersonRoleSortNumber() {
        return personRoleSortNumber;
    }

    /**
     * Sets the personRoleSortNumber attribute value.
     * 
     * @param personRoleSortNumber The personRoleSortNumber to set.
     */
    public void setPersonRoleSortNumber(Integer personRoleSortNumber) {
        this.personRoleSortNumber = personRoleSortNumber;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("personRoleCode", this.personRoleCode);
        return m;
    }


}
