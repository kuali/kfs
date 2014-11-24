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
