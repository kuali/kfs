/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.budget.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Business object that represents a selected/unselected funding reason code for a user.
 */
public class BudgetConstructionReasonCodePick extends PersistableBusinessObjectBase {

    private String appointmentFundingReasonCode;
    private Integer selectFlag;
    private String personUniversalIdentifier;

    BudgetConstructionAppointmentFundingReasonCode appointmentFundingReason;

    /**
     * Default constructor.
     */
    public BudgetConstructionReasonCodePick() {
        selectFlag = new Integer(0);
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
     * Gets the selectFlag attribute.
     * 
     * @return Returns the selectFlag
     */
    public Integer getSelectFlag() {
        return selectFlag;
    }

    /**
     * Sets the selectFlag attribute.
     * 
     * @param selectFlag The selectFlag to set.
     */
    public void setSelectFlag(Integer selectFlag) {
        this.selectFlag = selectFlag;
    }


    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier.
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * Sets the personUniversalIdentifier attribute value.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }

    /**
     * Gets the appointmentFundingReason attribute.
     * 
     * @return Returns the appointmentFundingReason.
     */
    public BudgetConstructionAppointmentFundingReasonCode getAppointmentFundingReason() {
        return appointmentFundingReason;
    }

    /**
     * Sets the appointmentFundingReason attribute value.
     * 
     * @param appointmentFundingReason The appointmentFundingReason to set.
     * @deprecated
     */
    public void setAppointmentFundingReason(BudgetConstructionAppointmentFundingReasonCode appointmentFundingReason) {
        this.appointmentFundingReason = appointmentFundingReason;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        m.put("appointmentFundingReasonCode", this.appointmentFundingReasonCode);
        return m;
    }

}
