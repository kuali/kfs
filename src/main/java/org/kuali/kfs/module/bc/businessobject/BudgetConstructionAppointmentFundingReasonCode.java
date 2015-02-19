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
public class BudgetConstructionAppointmentFundingReasonCode extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String appointmentFundingReasonCode;
    private String appointmentFundingReasonDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public BudgetConstructionAppointmentFundingReasonCode() {

    }

    /**
     * Gets the appointmentFundingReasonCode attribute.
     * 
     * @return Returns the appointmentFundingReasonCode
     */
    public String getAppointmentFundingReasonCode() {
        return appointmentFundingReasonCode;
    }

    /**
     * Sets the appointmentFundingReasonCode attribute.
     * 
     * @param appointmentFundingReasonCode The appointmentFundingReasonCode to set.
     */
    public void setAppointmentFundingReasonCode(String appointmentFundingReasonCode) {
        this.appointmentFundingReasonCode = appointmentFundingReasonCode;
    }


    /**
     * Gets the appointmentFundingReasonDescription attribute.
     * 
     * @return Returns the appointmentFundingReasonDescription
     */
    public String getAppointmentFundingReasonDescription() {
        return appointmentFundingReasonDescription;
    }

    /**
     * Sets the appointmentFundingReasonDescription attribute.
     * 
     * @param appointmentFundingReasonDescription The appointmentFundingReasonDescription to set.
     */
    public void setAppointmentFundingReasonDescription(String appointmentFundingReasonDescription) {
        this.appointmentFundingReasonDescription = appointmentFundingReasonDescription;
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
        m.put("appointmentFundingReasonCode", this.appointmentFundingReasonCode);
        return m;
    }


}
