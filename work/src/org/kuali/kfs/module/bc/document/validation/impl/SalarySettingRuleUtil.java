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

import java.math.BigDecimal;

import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;

public class SalarySettingRuleUtil {
    
    /**
     * Checks that the appintment requested amount is not empty
     * 
     * @param appointmentFunding
     * @return
     */
    public static boolean isRequestedAmountNotEmpty(PendingBudgetConstructionAppointmentFunding appointmentFunding) {        
        return appointmentFunding.getAppointmentRequestedAmount() != null;
    }
    
    /**
     * Checks that the appointment requested amount is non-negative
     * 
     * @param appointmentFunding
     * @return
     */
    public static boolean isRequestedAmountNonNegative(PendingBudgetConstructionAppointmentFunding appointmentFunding) {        
        return !appointmentFunding.getAppointmentRequestedAmount().isNegative();
    }
    
    /**
     * Checks that when an fte is required it is greater than zero
     * 
     * @param appointmentFunding
     * @return
     */
    public static boolean isRequestedFteQuantityGreaterThanZero(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        if ( appointmentFunding.getAppointmentRequestedAmount().isPositive()) {
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
}
