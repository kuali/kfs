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
package org.kuali.kfs.module.bc.document.service.impl;

import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.LWPA;
import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.LWPF;
import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.NONE;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCConstants.SynchronizationCheckType;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.service.HumanResourcesPayrollService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.util.MessageMap;

/**
 * provide a set of rule elements for salary setting.
 */
public class SalarySettingRuleHelperServiceImpl implements SalarySettingRuleHelperService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingRuleHelperServiceImpl.class);

    protected SalarySettingService salarySettingService;
    protected HumanResourcesPayrollService humanResourcesPayrollService;
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#canBeAdjusted(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding, org.kuali.rice.krad.util.MessageMap)
     */
    public boolean canBeAdjusted(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        if (appointmentFunding.getEffectiveCSFTracker() == null) {
            // These error messages should not be displayed since the reported condition is obvious and benign.
            //errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_CANNOT_ADJUST_FUNDING_WITHOUT_EFFECTIVE_CSF_TRACKER);
            return false;
        }
        
        if (appointmentFunding.isAppointmentFundingDeleteIndicator()) {
            // These error messages should not be displayed since the reported condition is obvious and benign.
            //errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_CANNOT_ADJUST_FUNDING_MARKED_AS_DELETE);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasActiveJob(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.krad.util.MessageMap)
     */
    public boolean hasActiveJob(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap, SynchronizationCheckType synchronizationCheckType) {
        Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();
        String emplid = appointmentFunding.getEmplid();
        String positionNumber = appointmentFunding.getPositionNumber();

        if (synchronizationCheckType.equals(SynchronizationCheckType.EID) && emplid.equals("VACANT")){
            return true;
        } else {
            boolean hasActiveJob = humanResourcesPayrollService.isActiveJob(emplid, positionNumber, fiscalYear, synchronizationCheckType);
            if (!hasActiveJob) {
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_NO_ACTIVE_JOB_FOUND, appointmentFunding.getEmplid(), appointmentFunding.getPositionNumber());
                return false;
            }

            return true;
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasObjectCodeMatchingDefaultOfPosition(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.MessageMap)
     */
    public boolean hasObjectCodeMatchingDefaultOfPosition(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        String defaultObjectCode = appointmentFunding.getBudgetConstructionPosition().getIuDefaultObjectCode();
        String objectCode = appointmentFunding.getFinancialObjectCode();

        if (!StringUtils.equals(objectCode, defaultObjectCode)) {
            errorMap.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, BCKeyConstants.ERROR_NOT_DEFAULT_OBJECT_CODE, defaultObjectCode);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasRequestedAmountZeroWhenFullYearLeave(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.MessageMap)
     */
    public boolean hasRequestedAmountZeroWhenFullYearLeave(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        // Request Salary Amount must be zero because these leave codes are for full year leave without pay.
        if (StringUtils.equals(leaveDurationCode, LWPA.durationCode) || StringUtils.equals(leaveDurationCode, LWPF.durationCode)) {
            KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();

            if (!requestedAmount.isZero()) {
                errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_REQUEST_AMOUNT_NOT_ZERO_WHEN_FULL_YEAR_LEAVE);
                return false;
            }
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasRequestedFteQuantityZeroWhenFullYearLeave(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.MessageMap)
     */
    public boolean hasRequestedFteQuantityZeroWhenFullYearLeave(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        // Request Salary Amount must be zero because these leave codes are for full year leave without pay.
        if (StringUtils.equals(leaveDurationCode, LWPA.durationCode) || StringUtils.equals(leaveDurationCode, LWPF.durationCode)) {
            BigDecimal requestedFteQuantity = appointmentFunding.getAppointmentRequestedFteQuantity();

            if (requestedFteQuantity.compareTo(BigDecimal.ZERO) != 0) {
                errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_FTE_QUANTITY, BCKeyConstants.ERROR_REQUEST_FTE_NOT_ZERO_WHEN_FULL_YEAR_LEAVE);
                return false;
            }
            
            return true;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasNoExistingLine(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding, org.kuali.rice.krad.util.MessageMap)
     */
    public boolean hasNoExistingLine(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        boolean hasNoExistingLine = salarySettingService.findAppointmentFunding(appointmentFundings, appointmentFunding) == null;
        if (!hasNoExistingLine) {
            errorMap.putError(BCPropertyConstants.NEW_BCAF_LINE, BCKeyConstants.ERROR_DUPLICATE_FUNDING_LINE);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidRequestedAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.MessageMap)
     */
    public boolean hasValidRequestedAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();
        if (requestedAmount == null || requestedAmount.isNegative()) {
            errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_REQUESTED_AMOUNT_NONNEGATIVE_REQUIRED);
            return false;
        }

        return true;
    }

    public boolean hasValidRequestedAmountQuickSalarySetting(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {

        if (this.hasValidRequestedAmount(appointmentFunding, errorMap)){
            KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();
            BigDecimal requestedFteQuantity = appointmentFunding.getAppointmentRequestedFteQuantity();
            if (requestedAmount.isPositive() && (requestedFteQuantity != null && requestedFteQuantity.compareTo(BigDecimal.ZERO) == 0)) {
                errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_REQUESTED_AMOUNT_NEEDS_FTE_FIRST);
                return false;
            }
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidRequestedCsfAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.MessageMap)
     */
    public boolean hasValidRequestedCsfAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        KualiInteger csfAmount = appointmentFunding.getAppointmentRequestedCsfAmount();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        // Requested csf amount must be greater than 0 if there is a leave
        if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
            if (csfAmount == null || !csfAmount.isPositive()) {
                errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_CSF_AMOUNT, BCKeyConstants.ERROR_FTE_GREATER_THAN_ZERO_REQUIRED);
                return false;
            }
            
            return true;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidRequestedCsfTimePercent(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.MessageMap)
     */
    public boolean hasValidRequestedCsfTimePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        BigDecimal csfTimePercent = appointmentFunding.getAppointmentRequestedCsfTimePercent();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        // Requested csf amount must be greater than 0 if there is a leave
        if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
            if (csfTimePercent == null || csfTimePercent.compareTo(BigDecimal.ZERO) <= 0 || csfTimePercent.compareTo(BCConstants.ONE_HUNDRED) > 0 ) {
                errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_CSF_TIME_PERCENT, BCKeyConstants.ERROR_LEAVE_TIME_PERCENT_NOT_IN_RANGE, BigDecimal.ZERO.toPlainString(), BCConstants.ONE_HUNDRED.toPlainString());
                return false;
            }
            
            return true;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidRequestedFteQuantity(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.MessageMap)
     */
    public boolean hasValidRequestedFteQuantity(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        BigDecimal requestedFteQuantity = appointmentFunding.getAppointmentRequestedFteQuantity();
        if (requestedFteQuantity == null || requestedFteQuantity.compareTo(BigDecimal.ZERO) < 0 || requestedFteQuantity.compareTo(BigDecimal.ONE) > 0) {
            errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_FTE_QUANTITY, BCKeyConstants.ERROR_FTE_QUANTITY_NOT_IN_RANGE, BigDecimal.ZERO.toPlainString(), BigDecimal.ONE.toPlainString());
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidRequestedFundingMonth(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.MessageMap)
     */
    public boolean hasValidRequestedFundingMonth(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        Integer fundingMonths = appointmentFunding.getAppointmentFundingMonth();
        if (fundingMonths == null) {
            errorMap.putError(BCPropertyConstants.APPOINTMENT_FUNDING_MONTH, BCKeyConstants.ERROR_EMPTY_FUNDIN_MONTH);
            return false;
        }

        // Requested funding months must be between 0 and position normal work months if there is a leave
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();
        Integer normalWorkMonths = appointmentFunding.getBudgetConstructionPosition().getIuNormalWorkMonths();
        if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
            if (fundingMonths < 0 || fundingMonths > normalWorkMonths) {
                errorMap.putError(BCPropertyConstants.APPOINTMENT_FUNDING_MONTH, BCKeyConstants.ERROR_FUNDIN_MONTH_NOT_IN_RANGE, ObjectUtils.toString(fundingMonths), "0", ObjectUtils.toString(normalWorkMonths));
                return false;
            }
            
            return true;
        }

        // Requested funding months must equal to position normal work months if no leave
        if (!fundingMonths.equals(normalWorkMonths)) {
            errorMap.putError(BCPropertyConstants.APPOINTMENT_FUNDING_MONTH, BCKeyConstants.ERROR_NOT_EQUAL_NORMAL_WORK_MONTHS, ObjectUtils.toString(fundingMonths), ObjectUtils.toString(normalWorkMonths));
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidRequestedTimePercent(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.MessageMap)
     */
    public boolean hasValidRequestedTimePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();
        BigDecimal requestedTimePercent = appointmentFunding.getAppointmentRequestedTimePercent();

        if (requestedTimePercent == null) {
            errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_TIME_PERCENT, BCKeyConstants.ERROR_EMPTY_REQUESTED_TIME_PERCENT);
            return false;
        }

        if (requestedTimePercent.compareTo(BigDecimal.ZERO) < 0 || requestedTimePercent.compareTo(BCConstants.ONE_HUNDRED) > 0 ) {
            errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_TIME_PERCENT, BCKeyConstants.ERROR_TIME_PERCENT_NOT_IN_RANGE, BigDecimal.ZERO.toPlainString(), BCConstants.ONE_HUNDRED.toPlainString());
            return false;
        }

        if (requestedAmount.isPositive() && requestedTimePercent.compareTo(BigDecimal.ZERO) <= 0) {
            errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_TIME_PERCENT, BCKeyConstants.ERROR_TIME_PERCENT_GREATER_THAN_ZERO_REQUIRED);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidAdjustmentAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.krad.util.MessageMap)
     */
    public boolean hasValidAdjustmentAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        KualiDecimal adjustmentAmount = appointmentFunding.getAdjustmentAmount();

        if (adjustmentAmount == null) {
            errorMap.putError(BCPropertyConstants.ADJUSTMENT_AMOUNT, BCKeyConstants.ERROR_ADJUSTMENT_AMOUNT_REQUIRED);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidAdjustmentAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.krad.util.MessageMap)
     */
    public boolean hasValidPayRateOrAnnualAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        BigDecimal payRate = appointmentFunding.getAppointmentRequestedPayRate();
        KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();

        if (requestedAmount == null && payRate == null) {
            errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_PAY_RATE, BCKeyConstants.ERROR_EMPTY_PAY_RATE_ANNUAL_AMOUNT);
            errorMap.putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_EMPTY_PAY_RATE_ANNUAL_AMOUNT);
            return false;
        }

        return true;
    }

    /**
     * Sets the salarySettingService attribute value.
     * 
     * @param salarySettingService The salarySettingService to set.
     */
    public void setSalarySettingService(SalarySettingService salarySettingService) {
        this.salarySettingService = salarySettingService;
    }

    /**
     * Sets the humanResourcesPayrollService attribute value.
     * 
     * @param humanResourcesPayrollService The humanResourcesPayrollService to set.
     */
    public void setHumanResourcesPayrollService(HumanResourcesPayrollService humanResourcesPayrollService) {
        this.humanResourcesPayrollService = humanResourcesPayrollService;
    }
}
