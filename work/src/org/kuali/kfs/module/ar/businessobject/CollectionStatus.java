/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.ar.AccountsReceivableCollectionStatus;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Defines the various statuses users wish to assign invoices to.
 *
 * @author mpritmani
 */
public class CollectionStatus extends PersistableBusinessObjectBase implements AccountsReceivableCollectionStatus {

    private String statusCode;
    private String statusDescription;
    private boolean active;

    /**
     * Default constructor
     */
    public CollectionStatus() {
        super();
    }

    /**
     * Gets the statusCode attribute.
     *
     * @return Returns statusCode.
     */
    @Override
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the statusCode.
     *
     * @param statusCode The statusCode to set.
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the statusDescription attribute.
     *
     * @return Returns statusDescription.
     */
    @Override
    public String getStatusDescription() {
        return statusDescription;
    }

    /**
     * Sets the statusDescription.
     *
     * @param statusDescription The statusDescription to set.
     */
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active atrribute.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     *
     * @param active The active attribute to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("statusCode", this.statusCode);
        m.put("statusDescription", this.statusDescription);
        m.put("active", this.active);
        return m;
    }

}
