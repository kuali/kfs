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
package org.kuali.kfs.integration.kc.businessobject;

import java.io.Serializable;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.kc.BudgetCategory;
import org.kuali.rice.kns.bo.BusinessObjectBase;

public class BudgetCategoryDTO extends BusinessObjectBase implements BudgetCategory,Serializable {
    
    protected String authorPersonName;
    protected String budgetCategoryCode;
    protected String budgetCategoryTypeCode;
    protected String budgetCategoryTypeDescription;
    protected String description;
    
    /**
     * Gets the value of the authorPersonName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorPersonName() {
        return authorPersonName;
    }

    /**
     * Sets the value of the authorPersonName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorPersonName(String value) {
        this.authorPersonName = value;
    }

    /**
     * Gets the value of the budgetCategoryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBudgetCategoryCode() {
        return budgetCategoryCode;
    }

    /**
     * Sets the value of the budgetCategoryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBudgetCategoryCode(String value) {
        this.budgetCategoryCode = value;
    }

    /**
     * Gets the value of the budgetCategoryTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBudgetCategoryTypeCode() {
        return budgetCategoryTypeCode;
    }

    /**
     * Sets the value of the budgetCategoryTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBudgetCategoryTypeCode(String value) {
        this.budgetCategoryTypeCode = value;
    }

    /**
     * Gets the value of the budgetCategoryTypeDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBudgetCategoryTypeDescription() {
        return budgetCategoryTypeDescription;
    }

    /**
     * Sets the value of the budgetCategoryTypeDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBudgetCategoryTypeDescription(String value) {
        this.budgetCategoryTypeDescription = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    public void prepareForWorkflow() {
        // TODO Auto-generated method stub       
    }

    public void refresh() {
        // TODO Auto-generated method stub        
    }

    protected LinkedHashMap<String,String> toStringMapper() {
        LinkedHashMap<String,String> m = new LinkedHashMap<String,String>();
        m.put("BudgetCategoryDTO", this.budgetCategoryCode);
        return m;
    }

}
