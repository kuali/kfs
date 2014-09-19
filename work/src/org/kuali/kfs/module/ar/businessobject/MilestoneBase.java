/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
