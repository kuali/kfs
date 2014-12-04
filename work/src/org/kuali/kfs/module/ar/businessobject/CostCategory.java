/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.businessobject;

import java.util.ArrayList;
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

    private List<CostCategoryObjectCode> objectCodes = new ArrayList<>();
    private List<CostCategoryObjectLevel> objectLevels = new ArrayList<>();
    private List<CostCategoryObjectConsolidation> objectConsolidations = new ArrayList<>();

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
