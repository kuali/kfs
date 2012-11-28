/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCfda;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * Instances of this class refer to Catalog of Federal Domestic Assistance codes. Some of these codes are taken directly from a
 * government web-site. Additional codes can be created manually however. Codes can be updated automatically via the CfdaBatchStep.
 */
public class CFDA extends PersistableBusinessObjectBase implements ContractsAndGrantsCfda, MutableInactivatable {

    private String cfdaNumber;
    private String cfdaProgramTitleName;
    private String cfdaMaintenanceTypeId;
    private boolean active;

    /**
     * Default constructor.
     */
    public CFDA() {
    }

    /**
     * Gets the cfdaNumber attribute.
     *
     * @return Returns the cfdaNumber
     */
    @Override
    public String getCfdaNumber() {
        return cfdaNumber;
    }

    /**
     * Sets the cfdaNumber attribute.
     *
     * @param cfdaNumber The cfdaNumber to set.
     */
    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }


    /**
     * Gets the cfdaProgramTitleName attribute.
     *
     * @return Returns the cfdaProgramTitleName
     */
    @Override
    public String getCfdaProgramTitleName() {
        return cfdaProgramTitleName;
    }

    /**
     * Sets the cfdaProgramTitleName attribute.
     *
     * @param cfdaProgramTitleName The cfdaProgramTitleName to set.
     */
    public void setCfdaProgramTitleName(String cfdaProgramTitleName) {
        this.cfdaProgramTitleName = cfdaProgramTitleName;
    }

    /**
     * Gets the cfdaMaintenanceTypeId attribute.
     *
     * @return Returns the cfdaMaintenanceTypeId
     */
    @Override
    public String getCfdaMaintenanceTypeId() {
        return cfdaMaintenanceTypeId;
    }

    /**
     * Sets the cfdaMaintenanceTypeId attribute.
     *
     * @param cfdaMaintenanceTypeId The cfdaMaintenanceTypeId to set.
     */
    public void setCfdaMaintenanceTypeId(String cfdaMaintenanceTypeId) {
        this.cfdaMaintenanceTypeId = cfdaMaintenanceTypeId;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("cfdaNumber", this.cfdaNumber);
        return m;
    }

    /**
     * This method gets the active value.
     *
     * @return The value of the active attribute.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * This method sets the active for this object.
     *
     * @param active The value to be assigned to the active attribute.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
