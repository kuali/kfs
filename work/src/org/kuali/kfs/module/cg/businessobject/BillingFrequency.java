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
package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingFrequency;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * BillingFrequency under Contracts & Grants section.
 */

public class BillingFrequency extends PersistableBusinessObjectBase implements ContractsAndGrantsBillingFrequency, MutableInactivatable {

    private String frequency;
    private String frequencyDescription;
    private Integer gracePeriodDays;
    private boolean active;

    @Override
    public String getFrequency() {
        return frequency;
    }


    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }




    @Override
    public String getFrequencyDescription() {
        return frequencyDescription;
    }


    public void setFrequencyDescription(String frequencyDescription) {
        this.frequencyDescription = frequencyDescription;
    }

    /**
     * Gets the gracePeriodDays attribute.
     *
     * @return Returns the gracePeriodDays.
     */


    @Override
    public Integer getGracePeriodDays() {
        return gracePeriodDays;
    }


    /**
     * Sets the gracePeriodDays attribute value.
     *
     * @param gracePeriodDays The gracePeriodDays to set.
     */
    public void setGracePeriodDays(Integer gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }




    @Override
    public boolean isActive() {
        return active;
    }



    @Override
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("frequency", this.frequency);
        m.put("frequencyDescription", frequencyDescription);
        m.put("gracePeriodDays", gracePeriodDays);
        m.put(KFSPropertyConstants.ACTIVE, active);

        return m;
    }
}
