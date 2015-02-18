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
package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetDepreciationMethod extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String depreciationMethodCode;
    private String depreciationMethodName;
    private boolean active;

    /**
     * Default constructor.
     */
    public AssetDepreciationMethod() {

    }

    /**
     * Gets the depreciationMethodCode attribute.
     * 
     * @return Returns the depreciationMethodCode
     */
    public String getDepreciationMethodCode() {
        return depreciationMethodCode;
    }

    /**
     * Sets the depreciationMethodCode attribute.
     * 
     * @param depreciationMethodCode The depreciationMethodCode to set.
     */
    public void setDepreciationMethodCode(String depreciationMethodCode) {
        this.depreciationMethodCode = depreciationMethodCode;
    }


    /**
     * Gets the depreciationMethodName attribute.
     * 
     * @return Returns the depreciationMethodName
     */
    public String getDepreciationMethodName() {
        return depreciationMethodName;
    }

    /**
     * Sets the depreciationMethodName attribute.
     * 
     * @param depreciationMethodName The depreciationMethodName to set.
     */
    public void setDepreciationMethodName(String depreciationMethodName) {
        this.depreciationMethodName = depreciationMethodName;
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
     * Sets the active attribute.
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
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("depreciationMethodCode", this.depreciationMethodCode);
        return m;
    }
}
