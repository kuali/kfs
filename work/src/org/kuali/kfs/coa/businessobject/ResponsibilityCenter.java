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
public class ResponsibilityCenter extends PersistableBusinessObjectBase implements MutableInactivatable {

    /**
     * Default no-arg constructor.
     */
    public ResponsibilityCenter() {

    }

    private String responsibilityCenterCode;
    private String responsibilityCenterName;
    private String responsibilityCenterShortName;
    private boolean active;

    /**
     * Gets the responsibilityCenterCode attribute.
     * 
     * @return Returns the responsibilityCenterCode
     */
    public String getResponsibilityCenterCode() {
        return responsibilityCenterCode;
    }

    /**
     * Sets the responsibilityCenterCode attribute.
     * 
     * @param responsibilityCenterCode The responsibilityCenterCode to set.
     */
    public void setResponsibilityCenterCode(String responsibilityCenterCode) {
        this.responsibilityCenterCode = responsibilityCenterCode;
    }

    /**
     * Gets the responsibilityCenterName attribute.
     * 
     * @return Returns the responsibilityCenterName
     */
    public String getResponsibilityCenterName() {
        return responsibilityCenterName;
    }

    /**
     * Sets the responsibilityCenterName attribute.
     * 
     * @param responsibilityCenterName The responsibilityCenterName to set.
     */
    public void setResponsibilityCenterName(String responsibilityCenterName) {
        this.responsibilityCenterName = responsibilityCenterName;
    }

    /**
     * Gets the responsibilityCenterShortName attribute.
     * 
     * @return Returns the responsibilityCenterShortName
     */
    public String getResponsibilityCenterShortName() {
        return responsibilityCenterShortName;
    }

    /**
     * Sets the responsibilityCenterShortName attribute.
     * 
     * @param responsibilityCenterShortName The responsibilityCenterShortName to set.
     */
    public void setResponsibilityCenterShortName(String responsibilityCenterShortName) {
        this.responsibilityCenterShortName = responsibilityCenterShortName;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the _active_ attribute.
     * 
     * @param _active_ The _active_ to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("responsibilityCenterCode", this.responsibilityCenterCode);

        return m;
    }
}
