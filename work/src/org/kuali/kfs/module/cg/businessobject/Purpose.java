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
public class Purpose extends PersistableBusinessObjectBase {

    private String purposeCode;
    private boolean dataObjectMaintenanceCodeActiveIndicator;
    private String purposeDescription;
    private Integer userSortNumber;

    /**
     * Default constructor.
     */
    public Purpose() {

    }

    /**
     * Gets the purposeCode attribute.
     * 
     * @return Returns the purposeCode
     */
    public String getPurposeCode() {
        return purposeCode;
    }

    /**
     * Sets the purposeCode attribute.
     * 
     * @param purposeCode The purposeCode to set.
     */
    public void setPurposeCode(String routingFormPurposeCode) {
        this.purposeCode = routingFormPurposeCode;
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
     * Gets the purposeDescription attribute.
     * 
     * @return Returns the purposeDescription
     */
    public String getPurposeDescription() {
        return purposeDescription;
    }

    /**
     * Sets the purposeDescription attribute.
     * 
     * @param purposeDescription The purposeDescription to set.
     */
    public void setPurposeDescription(String routingFormPurposeDescription) {
        this.purposeDescription = routingFormPurposeDescription;
    }

    /**
     * Gets the userSortNumber attribute.
     * 
     * @return Returns the userSortNumber.
     */
    public Integer getUserSortNumber() {
        return userSortNumber;
    }

    /**
     * Sets the userSortNumber attribute value.
     * 
     * @param userSortNumber The userSortNumber to set.
     */
    public void setUserSortNumber(Integer userSortNumber) {
        this.userSortNumber = userSortNumber;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("purposeCode", this.purposeCode);
        return m;
    }
}
