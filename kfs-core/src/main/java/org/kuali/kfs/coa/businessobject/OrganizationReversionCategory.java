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

package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class OrganizationReversionCategory extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String organizationReversionCategoryCode;
    private String organizationReversionCategoryName;
    private String organizationReversionSortCode;
    private boolean active;

    /**
     * Default constructor.
     */
    public OrganizationReversionCategory() {

    }

    /**
     * Gets the organizationReversionCategoryCode attribute.
     * 
     * @return Returns the organizationReversionCategoryCode
     */
    public String getOrganizationReversionCategoryCode() {
        return organizationReversionCategoryCode;
    }

    /**
     * Sets the organizationReversionCategoryCode attribute.
     * 
     * @param organizationReversionCategoryCode The organizationReversionCategoryCode to set.
     */
    public void setOrganizationReversionCategoryCode(String organizationReversionCategoryCode) {
        this.organizationReversionCategoryCode = organizationReversionCategoryCode;
    }


    /**
     * Gets the organizationReversionCategoryName attribute.
     * 
     * @return Returns the organizationReversionCategoryName
     */
    public String getOrganizationReversionCategoryName() {
        return organizationReversionCategoryName;
    }

    /**
     * Sets the organizationReversionCategoryName attribute.
     * 
     * @param organizationReversionCategoryName The organizationReversionCategoryName to set.
     */
    public void setOrganizationReversionCategoryName(String organizationReversionCategoryName) {
        this.organizationReversionCategoryName = organizationReversionCategoryName;
    }


    /**
     * Gets the organizationReversionSortCode attribute.
     * 
     * @return Returns the organizationReversionSortCode
     */
    public String getOrganizationReversionSortCode() {
        return organizationReversionSortCode;
    }

    /**
     * Sets the organizationReversionSortCode attribute.
     * 
     * @param organizationReversionSortCode The organizationReversionSortCode to set.
     */
    public void setOrganizationReversionSortCode(String organizationReversionSortCode) {
        this.organizationReversionSortCode = organizationReversionSortCode;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        return m;
    }

}
