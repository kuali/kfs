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
package org.kuali.kfs.integration.ar.businessobject;

import org.kuali.kfs.integration.ar.AccountsReceivableCollectionStatus;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

/**
 * Business Object for Collection Status
 */
public class CollectionStatus implements MutableInactivatable, AccountsReceivableCollectionStatus {

    private String statusCode;
    private String statusDescription;
    private boolean active;

    /**
     * Default constructor
     */
    public CollectionStatus() {
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the statusCode.
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the statusCode attribute value.
     * 
     * @param statusCode The active to set.
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the statusDescription attribute.
     * 
     * @return Returns the statusDescription.
     */
    public String getStatusDescription() {
        return statusDescription;
    }

    /**
     * Sets the statusDescription attribute value.
     * 
     * @param statusDescription The statusDescription to set.
     */
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObject#prepareForWorkflow()
     */
    @Override
    public void prepareForWorkflow() {
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObject#refresh()
     */
    @Override
    public void refresh() {
    }
}
