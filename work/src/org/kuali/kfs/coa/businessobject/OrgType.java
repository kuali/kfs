/*
 * Copyright 2005-2006 The Kuali Foundation.
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
 * Org Type Business Object
 * 
 * 
 */
public class OrgType extends BusinessObjectBase {
    private String organizationTypeCode;
    private String organizationTypeName;


    /**
     * Gets the organizationTypeName attribute.
     * 
     * @return Returns the organizationTypeName.
     */
    public String getOrganizationTypeName() {
        return organizationTypeName;
    }

    /**
     * Sets the organizationTypeName attribute value.
     * 
     * @param organizationTypeName The organizationTypeName to set.
     */
    public void setOrganizationTypeName(String organizationTypeName) {
        this.organizationTypeName = organizationTypeName;
    }

    /**
     * Gets the organizationTypeCode attribute.
     * 
     * @return Returns the organizationTypeCode.
     */
    public String getOrganizationTypeCode() {
        return organizationTypeCode;
    }

    /**
     * Sets the organizationTypeCode attribute value.
     * 
     * @param organizationTypeCode The organizationTypeCode to set.
     */
    public void setOrganizationTypeCode(String organizationTypeCode) {
        this.organizationTypeCode = organizationTypeCode;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("organizationTypeCode", this.organizationTypeCode);

        return m;
    }
}
