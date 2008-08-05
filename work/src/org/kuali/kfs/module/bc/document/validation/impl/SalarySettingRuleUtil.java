/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.document.validation.impl;

import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.LWPA;
import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.LWPF;
import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.NONE;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;

public class SalarySettingRuleUtil {

    /**
     * Checks that when an fte is required it is greater than zero
     * 
     * @param appointmentFunding
     * @return
     */
    public static boolean isRequestedFteQuantityGreaterThanZero(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        if (appointmentFunding.getAppointmentRequestedAmount().isPositive()) {
            return appointmentFunding.getAppointmentRequestedFteQuantity().compareTo(BigDecimal.ZERO) > 0;
        }

        return true;
    }

    /**
     * Checks that adjustment amount is not empty
     * 
     * @param appointmentFunding
     * @return
     */
    public static boolean isAdjustmentAmountNotEmpty(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        return appointmentFunding.getAdjustmentAmount() != null;
    }

    /**
     * Checks that appointment requested payrate is not empty
     * 
     * @param appointmentFunding
     * @return
     */
    public static boolean isRequestedPayRateNotEmpty(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        return appointmentFunding.getAppointmentRequestedPayRate() != null;
    }

    /**
     * determine whether the requested funding month of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the requested funding month of the given appointment funding is valid; otherwise, false
     */
    public static boolean isValidRequestedFundingMonth(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        Integer fundingMonths = appointmentFunding.getAppointmentFundingMonth();
        Integer normalWorkMonths = appointmentFunding.getBudgetConstructionPosition().getIuNormalWorkMonths();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();
        
        if(fundingMonths == null) {
            return false;
        }

        // Requested funding months must be between 0 and position normal work months if there is a leave
        if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
            return fundingMonths >= 0 && fundingMonths <= normalWorkMonths;
        }

        // Requested funding months must equal to position normal work months if no leave
        return fundingMonths == normalWorkMonths;
    }

    /**
     * determine whether the requested leave csf amount of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the requested leave csf amount of the given appointment funding is valid; otherwise, false
     */
    public static boolean isValidRequestedCsfAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        KualiInteger csfAmount = appointmentFunding.getAppointmentRequestedCsfAmount();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        if(csfAmount == null) {
            return false;
        }
        
        // Requested csf amount must be greater than 0 if there is a leave
        if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
            return csfAmount.isPositive();
        }

        return true;
    }

    /**
     * determine whether the requested leave csf time percent of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the requested leave csf time percent of the given appointment funding is valid; otherwise, false
     */
    public static boolean isValidRequestedCsfTimePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        BigDecimal csfTimePercent = appointmentFunding.getAppointmentRequestedCsfTimePercent();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();
        
        if(csfTimePercent == null) {
            return false;
        }

        // Requested csf amount must be greater than 0 if there is a leave
        if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
            return csfTimePercent.compareTo(BigDecimal.ZERO) > 0;
        }

        return true;
    }

    /**
     * determine whether the requested salary amount of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the requested salary amount of the given appointment funding is valid; otherwise, false
     */
    public static boolean isValidRequestedAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();
        
        if(requestedAmount == null) {
            return false;
        }

        // Request Salary Amount must be zero because these leave codes are for full year leave without pay.
        if (StringUtils.equals(leaveDurationCode, LWPA.durationCode) || StringUtils.equals(leaveDurationCode, LWPF.durationCode)) {
            return requestedAmount.isZero();
        }

        return !requestedAmount.isNegative();
    }
    
    /**
     * determine whether the requested fte quantity of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the requested fte quantity of the given appointment funding is valid; otherwise, false
     */
    public static boolean isValidRequestedFteQuantity(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        BigDecimal requestedFteQuantity = appointmentFunding.getAppointmentRequestedFteQuantity();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();
        
        if(requestedFteQuantity == null) {
            return false;
        }

        // Request Salary Amount must be zero because these leave codes are for full year leave without pay.
        if (StringUtils.equals(leaveDurationCode, LWPA.durationCode) || StringUtils.equals(leaveDurationCode, LWPF.durationCode)) {
            return requestedFteQuantity.compareTo(BigDecimal.ZERO) == 0;
        }

        return requestedFteQuantity.compareTo(BigDecimal.ZERO) > 0;
    }
}
