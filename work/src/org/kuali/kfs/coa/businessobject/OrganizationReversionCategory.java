/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/businessobject/OrganizationReversionCategory.java,v $
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

package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class OrganizationReversionCategory extends BusinessObjectBase {

    private String organizationReversionCategoryCode;
    private String organizationReversionCategoryName;
    private String organizationReversionSortCode;
    private boolean organizationReversionCategoryActiveIndicator;
    
    /**
     * Default constructor.
     */
    public OrganizationReversionCategory() {

    }

    /**
     * Gets the organizationReversionCategoryCode attribute.
     * 
     * @return Returns the organizationReversionCategoryCode
     * 
     */
    public String getOrganizationReversionCategoryCode() {
        return organizationReversionCategoryCode;
    }

    /**
     * Sets the organizationReversionCategoryCode attribute.
     * 
     * @param organizationReversionCategoryCode The organizationReversionCategoryCode to set.
     * 
     */
    public void setOrganizationReversionCategoryCode(String organizationReversionCategoryCode) {
        this.organizationReversionCategoryCode = organizationReversionCategoryCode;
    }


    /**
     * Gets the organizationReversionCategoryName attribute.
     * 
     * @return Returns the organizationReversionCategoryName
     * 
     */
    public String getOrganizationReversionCategoryName() {
        return organizationReversionCategoryName;
    }

    /**
     * Sets the organizationReversionCategoryName attribute.
     * 
     * @param organizationReversionCategoryName The organizationReversionCategoryName to set.
     * 
     */
    public void setOrganizationReversionCategoryName(String organizationReversionCategoryName) {
        this.organizationReversionCategoryName = organizationReversionCategoryName;
    }


    /**
     * Gets the organizationReversionSortCode attribute.
     * 
     * @return Returns the organizationReversionSortCode
     * 
     */
    public String getOrganizationReversionSortCode() {
        return organizationReversionSortCode;
    }

    /**
     * Sets the organizationReversionSortCode attribute.
     * 
     * @param organizationReversionSortCode The organizationReversionSortCode to set.
     * 
     */
    public void setOrganizationReversionSortCode(String organizationReversionSortCode) {
        this.organizationReversionSortCode = organizationReversionSortCode;
    }

    /**
     * Gets the organizationReversionCategoryActiveIndicator attribute. 
     * @return Returns the organizationReversionCategoryActiveIndicator.
     */
    public boolean isOrganizationReversionCategoryActiveIndicator() {
        return organizationReversionCategoryActiveIndicator;
    }

    /**
     * Sets the organizationReversionCategoryActiveIndicator attribute value.
     * @param organizationReversionCategoryActiveIndicator The organizationReversionCategoryActiveIndicator to set.
     */
    public void setOrganizationReversionCategoryActiveIndicator(boolean organizationReversionCategoryActiveIndicator) {
        this.organizationReversionCategoryActiveIndicator = organizationReversionCategoryActiveIndicator;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        return m;
    }

}
