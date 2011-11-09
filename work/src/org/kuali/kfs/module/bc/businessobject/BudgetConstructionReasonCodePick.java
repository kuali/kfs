/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.bc.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Business object that represents a selected/unselected funding reason code for a user.
 */
public class BudgetConstructionReasonCodePick extends PersistableBusinessObjectBase {

    private String appointmentFundingReasonCode;
    private Integer selectFlag;
    private String principalId;

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
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute value.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        m.put("appointmentFundingReasonCode", this.appointmentFundingReasonCode);
        return m;
    }

}

