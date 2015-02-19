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

package org.kuali.kfs.module.bc.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionDuration extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String appointmentDurationCode;
    private String appointmentDurationDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public BudgetConstructionDuration() {

    }

    /**
     * Gets the appointmentDurationCode attribute.
     * 
     * @return Returns the appointmentDurationCode
     */
    public String getAppointmentDurationCode() {
        return appointmentDurationCode;
    }

    /**
     * Sets the appointmentDurationCode attribute.
     * 
     * @param appointmentDurationCode The appointmentDurationCode to set.
     */
    public void setAppointmentDurationCode(String appointmentDurationCode) {
        this.appointmentDurationCode = appointmentDurationCode;
    }


    /**
     * Gets the appointmentDurationDescription attribute.
     * 
     * @return Returns the appointmentDurationDescription
     */
    public String getAppointmentDurationDescription() {
        return appointmentDurationDescription;
    }

    /**
     * Sets the appointmentDurationDescription attribute.
     * 
     * @param appointmentDurationDescription The appointmentDurationDescription to set.
     */
    public void setAppointmentDurationDescription(String appointmentDurationDescription) {
        this.appointmentDurationDescription = appointmentDurationDescription;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
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
        LinkedHashMap m = new LinkedHashMap();
        m.put("appointmentDurationCode", this.appointmentDurationCode);
        return m;
    }
}
