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
import java.util.Collection;

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.validation.DetailSalarySettingSaveRule;

public class SalarySettingRules implements DetailSalarySettingSaveRule {
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.document.validation.DetailSalarySettingSaveRule#processSave(java.util.Collection)
     */
    public boolean processSave(Collection<PendingBudgetConstructionAppointmentFunding> appointmentFundingRecords) {
        boolean isValid = true;
        
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundingRecords) {
            //requested amount required
            if (!SalarySettingRuleUtil.appointmentRequestedAmountIsNotEmpty(appointmentFunding)) {
                GlobalVariables.getErrorMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_REQUESTED_AMOUNT_REQUIRED, new String[]{null});
                isValid = false;
            } else if (!SalarySettingRuleUtil.appointmentRequestedAmountIsNonNegative(appointmentFunding)) {
                GlobalVariables.getErrorMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_REQUESTED_AMOUNT_NONNEGATIVE_REQUIRED, new String[]{null});
                isValid = false;
            } else if (!SalarySettingRuleUtil.appointmentRequestedFteQuantityIsGreaterThanZero(appointmentFunding)) {
                GlobalVariables.getErrorMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_FTE_QUANTITY, BCKeyConstants.ERROR_FTE_GREATER_THAN_ZERO_REQUIRED, new String[]{null});
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.document.validation.DetailSalarySettingSaveRule#processAdjustSalaraySettingLinePercent(java.util.Collection)
     */
    public boolean processAdjustSalaraySettingLinePercent(Collection<PendingBudgetConstructionAppointmentFunding> appointmentFundingRecords) {
        boolean isValid = true;
        
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundingRecords) {
            if (!SalarySettingRuleUtil.adjustmentAmountIsNotEmpty(appointmentFunding)) {
                GlobalVariables.getErrorMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_ADJUSTMENT_PERCENT_REQUIRED, new String[]{null});
                isValid = false;
            }  
        }
        
        return isValid;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.document.validation.DetailSalarySettingSaveRule#processNormalizePayrateAndAmount(java.util.Collection)
     */
    public boolean processNormalizePayrateAndAmount(Collection<PendingBudgetConstructionAppointmentFunding> appointmentFundingRecords) {
        boolean isValid = true;
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundingRecords) {
            if (!SalarySettingRuleUtil.adjustmentAmountIsNotEmpty(appointmentFunding)) {
                GlobalVariables.getErrorMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_REQUESTED_AMOUNT_REQUIRED, new String[]{null});
                isValid = false;
            }
            
            if (!SalarySettingRuleUtil.appointmentRequestedPayRateIsNotEmpty(appointmentFunding)) {
                GlobalVariables.getErrorMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_PAY_RATE, BCKeyConstants.ERROR_PAYRATE_AMOUNT_REQUIRED, new String[]{null});
                isValid = false;
            }
        }
        
        return isValid;
    }
   
}
