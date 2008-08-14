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
package org.kuali.kfs.module.bc.document.service.impl;

import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.LWPA;
import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.LWPF;
import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.NONE;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.service.HumanResourcesPayrollService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * provide a set of rule elements for salary setting.
 */
public class SalarySettingRuleHelperServiceImpl implements SalarySettingRuleHelperService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingRuleHelperServiceImpl.class);

    private SalarySettingService salarySettingService;
    private HumanResourcesPayrollService humanResourcesPayrollService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasActiveJob(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.kns.util.ErrorMap)
     */
    public boolean hasActiveJob(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();
        String emplid = appointmentFunding.getEmplid();
        String positionNumber = appointmentFunding.getPositionNumber();
        String syncCheckType = KFSConstants.EMPTY_STRING;

        boolean hasActiveJob = humanResourcesPayrollService.isActiveJob(emplid, positionNumber, fiscalYear, syncCheckType);
        if (!hasActiveJob) {
            String errorMessage = MessageBuilder.buildMessageWithPlaceHolder(BCKeyConstants.ERROR_NO_ACTIVE_JOB_FOUND, appointmentFunding.getEmplid(), appointmentFunding.getPositionNumber()).toString();
            errorMap.putError(KFSConstants.EMPTY_STRING, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasObjectCodeMatchingDefaultOfPosition(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    public boolean hasObjectCodeMatchingDefaultOfPosition(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        String defaultObjectCode = appointmentFunding.getBudgetConstructionPosition().getIuDefaultObjectCode();
        String objectCode = appointmentFunding.getFinancialObjectCode();

        if (!StringUtils.equals(objectCode, defaultObjectCode)) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(SubObjCd.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
            errorMap.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasRequestedAmountZeroWhenFullYearLeave(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    public boolean hasRequestedAmountZeroWhenFullYearLeave(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        // Request Salary Amount must be zero because these leave codes are for full year leave without pay.
        if (StringUtils.equals(leaveDurationCode, LWPA.durationCode) || StringUtils.equals(leaveDurationCode, LWPF.durationCode)) {
            KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();
            return requestedAmount == null || requestedAmount.isZero();
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasRequestedFteQuantityZeroWhenFullYearLeave(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    public boolean hasRequestedFteQuantityZeroWhenFullYearLeave(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        // Request Salary Amount must be zero because these leave codes are for full year leave without pay.
        if (StringUtils.equals(leaveDurationCode, LWPA.durationCode) || StringUtils.equals(leaveDurationCode, LWPF.durationCode)) {
            BigDecimal requestedFteQuantity = appointmentFunding.getAppointmentRequestedFteQuantity();
            return requestedFteQuantity == null || requestedFteQuantity.compareTo(BigDecimal.ZERO) == 0;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasSameExistingLine(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding, org.kuali.core.util.ErrorMap)
     */
    public boolean hasSameExistingLine(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        return salarySettingService.findAppointmentFunding(appointmentFundings, appointmentFunding) != null;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidRequestedAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    public boolean hasValidRequestedAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();

        return requestedAmount != null && !requestedAmount.isNegative();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidRequestedCsfAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    public boolean hasValidRequestedCsfAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        KualiInteger csfAmount = appointmentFunding.getAppointmentRequestedCsfAmount();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        if (csfAmount == null) {
            return false;
        }

        // Requested csf amount must be greater than 0 if there is a leave
        if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
            return csfAmount.isPositive();
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidRequestedCsfTimePercent(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    public boolean hasValidRequestedCsfTimePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        BigDecimal csfTimePercent = appointmentFunding.getAppointmentRequestedCsfTimePercent();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        if (csfTimePercent == null) {
            return false;
        }

        // Requested csf amount must be greater than 0 if there is a leave
        if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
            return csfTimePercent.compareTo(BigDecimal.ZERO) > 0;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidRequestedFteQuantity(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    public boolean hasValidRequestedFteQuantity(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        BigDecimal requestedFteQuantity = appointmentFunding.getAppointmentRequestedFteQuantity();

        return requestedFteQuantity != null && requestedFteQuantity.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidRequestedFundingMonth(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    public boolean hasValidRequestedFundingMonth(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        Integer fundingMonths = appointmentFunding.getAppointmentFundingMonth();
        Integer normalWorkMonths = appointmentFunding.getBudgetConstructionPosition().getIuNormalWorkMonths();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        if (fundingMonths == null) {
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
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService#hasValidRequestedTimePercent(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    public boolean hasValidRequestedTimePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();
        BigDecimal requestedTimePercent = appointmentFunding.getAppointmentRequestedTimePercent();

        if (requestedAmount == null || requestedTimePercent == null) {
            return false;
        }

        if (requestedAmount.isPositive()) {
            return requestedTimePercent.compareTo(BigDecimal.ZERO) > 0;
        }
        else if (requestedAmount.isZero()) {
            return requestedTimePercent.compareTo(BigDecimal.ZERO) == 0;
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
