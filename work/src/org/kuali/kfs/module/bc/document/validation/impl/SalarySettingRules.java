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

import java.util.List;

import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.validation.SalarySettingRule;

public class SalarySettingRules implements SalarySettingRule {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingRules.class);

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processSave(java.util.List)
     */
    public boolean processSave(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings) {
        boolean isValid = true;

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            if (!SalarySettingRuleUtil.isValidRequestedAmount(appointmentFunding)) {
                GlobalVariables.getErrorMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_REQUESTED_AMOUNT_NONNEGATIVE_REQUIRED);
                isValid = false;
            }
            else if (!SalarySettingRuleUtil.isRequestedFteQuantityGreaterThanZero(appointmentFunding)) {
                GlobalVariables.getErrorMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_FTE_QUANTITY, BCKeyConstants.ERROR_FTE_GREATER_THAN_ZERO_REQUIRED);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processNormalizePayrateAndAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processNormalizePayrateAndAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        boolean isValid = true;

        /*if (!SalarySettingRuleUtil.isAdjustmentAmountNotEmpty(appointmentFunding)) {
            GlobalVariables.getErrorMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_REQUESTED_AMOUNT_REQUIRED);
            isValid = false;
        }

        if (!SalarySettingRuleUtil.isRequestedPayRateNotEmpty(appointmentFunding)) {
            GlobalVariables.getErrorMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_PAY_RATE, BCKeyConstants.ERROR_PAYRATE_AMOUNT_REQUIRED);
            isValid = false;
        }*/

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processAdjustAllSalarySettingLinesPercent(java.util.List)
     */
    public boolean processAdjustAllSalarySettingLinesPercent(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings) {
        boolean isValid = true;

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            isValid = isValid && this.processAdjustSalaraySettingLinePercent(appointmentFunding);
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processAdjustSalaraySettingLinePercent(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processAdjustSalaraySettingLinePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        boolean isValid = true;

        /*if (!SalarySettingRuleUtil.isAdjustmentAmountNotEmpty(appointmentFunding)) {
            GlobalVariables.getErrorMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_ADJUSTMENT_PERCENT_REQUIRED);
            isValid = false;
        }*/
        return isValid;
    }

}
