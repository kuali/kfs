/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.dto;

public class KcObjectCode {
    String  objectCodeName;
    String  budgetCategoryCode;
    String  description;
    Boolean onOffCampusFlag;
   
     /**
     * Gets the objectCodeName attribute. 
     * @return Returns the objectCodeName.
     */
    public String getObjectCodeName() {
        return objectCodeName;
    }
    /**
     * Sets the objectCodeName attribute value.
     * @param objectCodeName The objectCodeName to set.
     */
    public void setObjectCodeName(String objectCodeName) {
        this.objectCodeName = objectCodeName;
    }
    /**
     * Gets the budgetCategoryCode attribute. 
     * @return Returns the budgetCategoryCode.
     */
    public String getBudgetCategoryCode() {
        return budgetCategoryCode;
    }
    /**
     * Sets the budgetCategoryCode attribute value.
     * @param budgetCategoryCode The budgetCategoryCode to set.
     */
    public void setBudgetCategoryCode(String budgetCategoryCode) {
        this.budgetCategoryCode = budgetCategoryCode;
    }
    /**
     * Gets the description attribute. 
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * Sets the description attribute value.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Gets the onOffCampusFlag attribute. 
     * @return Returns the onOffCampusFlag.
     */
    public Boolean getOnOffCampusFlag() {
        return onOffCampusFlag;
    }
    /**
     * Sets the onOffCampusFlag attribute value.
     * @param onOffCampusFlag The onOffCampusFlag to set.
     */
    public void setOnOffCampusFlag(Boolean onOffCampusFlag) {
        this.onOffCampusFlag = onOffCampusFlag;
    }

}
