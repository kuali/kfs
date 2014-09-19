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
import java.util.List;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Defines a Category on Contracts and Grants Invoices.
 */
public class CostCategory extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String categoryCode;
    private String categoryName;
    private boolean indirectCostIndicator;
    private boolean active;

    private List<CostCategoryObjectCode> objectCodes;
    private List<CostCategoryObjectLevel> objectLevels;
    private List<CostCategoryObjectConsolidation> objectConsolidations;

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
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    @Override
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

    public List<CostCategoryObjectCode> getObjectCodes() {
        return objectCodes;
    }

    public void setObjectCodes(List<CostCategoryObjectCode> objectCodes) {
        this.objectCodes = objectCodes;
    }

    public List<CostCategoryObjectLevel> getObjectLevels() {
        return objectLevels;
    }

    public void setObjectLevels(List<CostCategoryObjectLevel> objectLevels) {
        this.objectLevels = objectLevels;
    }

    public List<CostCategoryObjectConsolidation> getObjectConsolidations() {
        return objectConsolidations;
    }

    public void setObjectConsolidations(List<CostCategoryObjectConsolidation> objectConsolidations) {
        this.objectConsolidations = objectConsolidations;
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("categoryCode", this.categoryCode);
        return m;
    }
}