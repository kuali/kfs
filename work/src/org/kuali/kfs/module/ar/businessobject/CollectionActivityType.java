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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Defines collection activity that can be taken on an outstanding debt.
 *
 */
public class CollectionActivityType extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String activityCode;
    private String activityDescription;
    private boolean active;

    /**
     * Gets the activityCode attribute.
     *
     * @return Returns the activityCode
     */
    public String getActivityCode() {
        return activityCode;
    }

    /**
     * Sets the activityCode attribute.
     *
     * @param activityCode The activityCode to set.
     */
    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    /**
     * Gets the activityDescription attribute.
     *
     * @return Returns the activityDescription
     */
    public String getActivityDescription() {
        return activityDescription;
    }

    /**
     * Sets the activityDescription attribute.
     *
     * @param activityDescription The activityDescription to set.
     */
    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("activityCode", this.activityCode);
        m.put("activityDescription", this.activityDescription);
        m.put(KFSPropertyConstants.ACTIVE, this.active);
        return m;
    }

}
