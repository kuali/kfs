/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service;

import java.util.List;

import org.kuali.kfs.module.bc.BCConstants.SynchronizationCheckType;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.rice.krad.util.MessageMap;

/**
 * define a set of validations methods for salary setting
 */
public interface SalarySettingRuleHelperService {

    /**
     * determine whether the salary amount of the given appointment funding can be adjusted
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if the salary amount of the given appointment funding can be adjusted; otherwise, false
     */
    public boolean canBeAdjusted(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine whether the given appointment funding is associated with an active job
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if the given appointment funding is associated with an active job; otherwise, false
     */
    public boolean hasActiveJob(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap, SynchronizationCheckType synchronizationCheckType);

    /**
     * determine whether the object code of the given appointment funding matches the position default object code
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if the object code of the given appointment funding matches the position default object code; otherwise, false
     */
    public boolean hasObjectCodeMatchingDefaultOfPosition(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * request salary amount must be zero for full year leave
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if request salary amount is zero for full year leave; otherwise, false
     */
    public boolean hasRequestedAmountZeroWhenFullYearLeave(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * request fte quantity must be zero for full year leave
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if the request fte quantity is zero for full year leave; otherwise, false
     */
    public boolean hasRequestedFteQuantityZeroWhenFullYearLeave(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine if there is an appointment funding in the given list that has the same key information as the specified appointment
     * funding
     * 
     * @param appointmentFundings the given appointment funding collection
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if there is no an appointment funding in the given list that has the same key information as the specified
     *         appointment funding; otherwise, false
     */
    public boolean hasNoExistingLine(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine whether the requested salary amount of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the requested salary amount of the given appointment funding is valid; otherwise, false
     */
    public boolean hasValidRequestedAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine whether the requested salary amount of the given appointment funding is valid for the quick salary setting screen
     * 
     * @param appointmentFunding
     * @param errorMap
     * @return
     */
    public boolean hasValidRequestedAmountQuickSalarySetting(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine whether the requested leave csf amount of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if the requested leave csf amount of the given appointment funding is valid; otherwise, false
     */
    public boolean hasValidRequestedCsfAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine whether the requested leave csf time percent of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if the requested leave csf time percent of the given appointment funding is valid; otherwise, false
     */
    public boolean hasValidRequestedCsfTimePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine whether the requested FTE is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if the requested FTE is valid; otherwise, false
     */
    public boolean hasValidRequestedFteQuantity(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine whether the requested funding month of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if the requested funding month of the given appointment funding is valid; otherwise, false
     */
    public boolean hasValidRequestedFundingMonth(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine whether the requested FTE is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if the requested FTE is valid; otherwise, false
     */
    public boolean hasValidRequestedTimePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine whether the adjustment amount is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if the adjustment amount is valid; otherwise, false
     */
    public boolean hasValidAdjustmentAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine whether either pay rate or annual amount is not empty, and both of them have valid formats if not empty
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if either pay rate or annual amount is valid; otherwise, false
     */
    public boolean hasValidPayRateOrAnnualAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);
}
