/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
