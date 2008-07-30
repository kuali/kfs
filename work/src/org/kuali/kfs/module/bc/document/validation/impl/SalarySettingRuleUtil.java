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

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;

public class SalarySettingRuleUtil {
    
    /**
     * Checks that the appintment requested amount is not empty
     * 
     * @param appointmentFunding
     * @return
     */
    public static boolean appointmentRequestedAmountIsNotEmpty(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        if (appointmentFunding.getAppointmentRequestedAmount() == null) return false;
        
        return true;
    }
    
    /**
     * Checks that the appointment requested amount is non-negative
     * 
     * @param appointmentFunding
     * @return
     */
    public static boolean appointmentRequestedAmountIsNonNegative(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        if (appointmentFunding.getAppointmentRequestedAmount().isNegative()) return false;
        
        return true;
    }
    
    /**
     * Checks that when an fte is required it is greater than zero
     * 
     * @param appointmentFunding
     * @return
     */
    public static boolean appointmentRequestedFteQuantityIsGreaterThanZero(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        if ( appointmentFunding.getAppointmentRequestedAmount().isGreaterThan(new KualiInteger(0)) 
                && ( appointmentFunding.getAppointmentRequestedFteQuantity().compareTo(new BigDecimal(0)) != 1 )) return false;
        
        return true;
    }
    
    /**
     * Checks that adjustment amount is not empty
     * 
     * @param appointmentFunding
     * @return
     */
    public static boolean adjustmentAmountIsNotEmpty(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        if (appointmentFunding.getAdjustmentAmount() == null ) return false;
        
        return true;
    }
    
    /**
     * Checks that appointment requested payrate is not empty
     * 
     * @param appointmentFunding
     * @return
     */
    public static boolean appointmentRequestedPayRateIsNotEmpty(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        if (appointmentFunding.getAppointmentRequestedPayRate() == null) return false;
        
        return true;
    }
}
