/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Defines the Categories on the Contracts and Grants Invoices.
 */
public class ContractsAndGrantsCategories extends PersistableBusinessObjectBase {

    private String categoryCode;
    private String categoryName;
    private String categoryDescription;
    private String categoryObjectCodes;
    private String categoryConsolidations;
    private String categoryLevels;
    private boolean indirectCostIndicator;
    private boolean active;


    /**
     * Gets the categoryCode attribute.
     * 
     * @return Returns the categoryCode.
     */
    public String getCategoryCode() {
        return categoryCode;
    }


    /**
     * Sets the categoryCode attribute value.
     * 
     * @param categoryCode The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }


    /**
     * Gets the categoryNumber attribute.
     * 
     * @return Returns the categoryNumber.
     */
    public String getCategoryName() {
        return categoryName;
    }


    /**
     * Sets the categoryNumber attribute value.
     * 
     * @param categoryNumber The categoryNumber to set.
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    /**
     * Gets the categoryDescription attribute.
     * 
     * @return Returns the categoryDescription.
     */
    public String getCategoryDescription() {
        return categoryDescription;
    }


    /**
     * Sets the categoryDescription attribute value.
     * 
     * @param categoryDescription The categoryDescription to set.
     */
    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }


    /**
     * Gets the objectCodes attribute.
     * 
     * @return Returns the objectCodes.
     */
    public String getCategoryObjectCodes() {
        return categoryObjectCodes;
    }


    /**
     * Sets the objectCodes attribute value.
     * 
     * @param objectCodes The objectCodes to set.
     */
    public void setCategoryObjectCodes(String objectCodes) {
        this.categoryObjectCodes = objectCodes;
    }


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the indirectCostIndicator attribute.
     * 
     * @return Returns the indirectCostIndicator.
     */
    public boolean isIndirectCostIndicator() {
        return indirectCostIndicator;
    }


    /**
     * Sets the indirectCostIndicator attribute value.
     * 
     * @param indirectCostIndicator The indirectCostIndicator to set.
     */
    public void setIndirectCostIndicator(boolean indirectCostIndicator) {
        this.indirectCostIndicator = indirectCostIndicator;
    }

    
    public String getCategoryConsolidations() {
        return categoryConsolidations;
    }


    public void setCategoryConsolidations(String categoryConsolidations) {
        this.categoryConsolidations = categoryConsolidations;
    }


    public String getCategoryLevels() {
        return categoryLevels;
    }


    public void setCategoryLevels(String categoryLevels) {
        this.categoryLevels = categoryLevels;
    }


    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("categoryCode", this.categoryCode);
        m.put("categoryDescription", this.categoryDescription);
        m.put("categoryName", this.categoryName);
        m.put("categoryObjectCodes", this.categoryObjectCodes);
        m.put("categoryConsolidations", this.categoryConsolidations);
        m.put("categoryLevels", this.categoryLevels);
        return m;
    }

}
