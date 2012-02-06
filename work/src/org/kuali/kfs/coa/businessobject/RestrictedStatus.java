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
 * 
 */
public class RestrictedStatus extends PersistableBusinessObjectBase implements MutableInactivatable {

    /**
     * Default no-arg constructor.
     */
    public RestrictedStatus() {

    }

    private String accountRestrictedStatusCode;
    private String accountRestrictedStatusName;
    private boolean active;

    /**
     * Gets the accountRestrictedStatusCode attribute.
     * 
     * @return Returns the accountRestrictedStatusCode
     */
    public String getAccountRestrictedStatusCode() {
        return accountRestrictedStatusCode;
    }

    /**
     * Sets the accountRestrictedStatusCode attribute.
     * 
     * @param accountRestrictedStatusCode The accountRestrictedStatusCode to set.
     */
    public void setAccountRestrictedStatusCode(String accountRestrictedStatusCode) {
        this.accountRestrictedStatusCode = accountRestrictedStatusCode;
    }

    /**
     * Gets the accountRestrictedStatusName attribute.
     * 
     * @return Returns the accountRestrictedStatusName
     */
    public String getAccountRestrictedStatusName() {
        return accountRestrictedStatusName;
    }

    /**
     * Sets the accountRestrictedStatusName attribute.
     * 
     * @param accountRestrictedStatusName The accountRestrictedStatusName to set.
     */
    public void setAccountRestrictedStatusName(String accountRestrictedStatusName) {
        this.accountRestrictedStatusName = accountRestrictedStatusName;
    }

    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public String getCodeAndDescription() {
        String theString = getAccountRestrictedStatusCode() + " - " + getAccountRestrictedStatusName();
        return theString;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("accountRestrictedStatusCode", this.accountRestrictedStatusCode);

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
