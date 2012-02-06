/*
 * Copyright 2005 The Kuali Foundation
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
 * Org Type Business Object
 */
public class OrganizationType extends PersistableBusinessObjectBase implements MutableInactivatable {
    private String organizationTypeCode;
    private String organizationTypeName;
    private boolean active;


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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("organizationTypeCode", this.organizationTypeCode);

        return m;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
