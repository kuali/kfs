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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * Base class so the Milestone and InvoiceMilestone BOs don't have to duplicate code.
 */
public abstract class MilestoneBase extends PersistableBusinessObjectBase  {

    protected Long milestoneNumber;
    protected Long milestoneIdentifier;
    protected String milestoneDescription;
    protected KualiDecimal milestoneAmount;
    protected Date milestoneActualCompletionDate;

    /**
     * Gets the milestoneActualCompletionDate attribute.
     *
     * @return Returns the milestoneActualCompletionDate.
     */
    public Date getMilestoneActualCompletionDate() {
        return milestoneActualCompletionDate;
    }

    /**
     * Gets the milestoneAmount attribute.
     *
     * @return Returns the milestoneAmount.
     */
    public KualiDecimal getMilestoneAmount() {
        return milestoneAmount;
    }

    /**
     * Gets the milestoneDescription attribute.
     *
     * @return Returns the milestoneDescription.
     */
    public String getMilestoneDescription() {
        return milestoneDescription;
    }

    /**
     * Gets the milestoneIdentifier attribute.
     *
     * @return Returns the milestoneIdentifier.
     */
    public Long getMilestoneIdentifier() {
        return milestoneIdentifier;
    }

    /**
     * Gets the milestoneNumber attribute.
     *
     * @return Returns the milestoneNumber.
     */
    public Long getMilestoneNumber() {
        return milestoneNumber;
    }

    /**
     * Sets the milestoneActualCompletionDate attribute value.
     *
     * @param milestoneActualCompletionDate The milestoneActualCompletionDate to set.
     */
    public void setMilestoneActualCompletionDate(Date milestoneActualCompletionDate) {
        this.milestoneActualCompletionDate = milestoneActualCompletionDate;
    }

    /**
     * Sets the milestoneAmount attribute value.
     *
     * @param milestoneAmount The milestoneAmount to set.
     */
    public void setMilestoneAmount(KualiDecimal milestoneAmount) {
        this.milestoneAmount = milestoneAmount;
    }


    /**
     * Sets the milestoneDescription attribute value.
     *
     * @param milestoneDescription The milestoneDescription to set.
     */
    public void setMilestoneDescription(String milestoneDescription) {
        this.milestoneDescription = milestoneDescription;
    }

    /**
     * Sets the milestoneIdentifier attribute value.
     *
     * @param milestoneIdentifier The milestoneIdentifier to set.
     */
    public void setMilestoneIdentifier(Long milestoneIdentifier) {
        this.milestoneIdentifier = milestoneIdentifier;
    }

    /**
     * Sets the milestoneNumber attribute value.
     *
     * @param milestoneNumber The milestoneNumber to set.
     */
    public void setMilestoneNumber(Long milestoneNumber) {
        this.milestoneNumber = milestoneNumber;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("milestoneDescription", this.milestoneDescription);
        if (this.milestoneNumber != null) {
            m.put("milestoneNumber", this.milestoneNumber.toString());
        }
        if (this.milestoneIdentifier != null) {
            m.put("milestoneIdentifier", this.milestoneIdentifier.toString());
        }
        if (this.milestoneAmount != null) {
            m.put("milestoneAmount", this.milestoneAmount.toString());
        }
        if (this.milestoneActualCompletionDate != null) {
            m.put("milestoneActualCompletionDate", this.milestoneActualCompletionDate.toString());
        }
        return m;
    }
}
