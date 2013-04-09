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
package org.kuali.kfs.module.bc.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BCConstants.SynchronizationCheckType;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService;
import org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService;
import org.kuali.kfs.module.bc.document.validation.SalarySettingRule;
import org.kuali.kfs.module.bc.service.HumanResourcesPayrollService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * the rule implementation for the actions of salary setting component
 */
public class SalarySettingRules implements SalarySettingRule {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingRules.class);

    protected BudgetConstructionRuleHelperService budgetConstructionRuleHelperService = SpringContext.getBean(BudgetConstructionRuleHelperService.class);
    protected SalarySettingRuleHelperService salarySettingRuleHelperService = SpringContext.getBean(SalarySettingRuleHelperService.class);
    protected HumanResourcesPayrollService humanResourcesPayrollService = SpringContext.getBean(HumanResourcesPayrollService.class);
    protected MessageMap errorMap = GlobalVariables.getMessageMap();

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processQuickSaveAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processQuickSaveAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("processQuickSaveAppointmentFunding() start");

        boolean hasValidFormat = budgetConstructionRuleHelperService.isFieldFormatValid(appointmentFunding, errorMap);
        if (!hasValidFormat) {
            return hasValidFormat;
        }

        // get a temp message map to allow marking salarySettingExpansion then merge this to errorMap
        MessageMap tempErrorMap = new MessageMap();
        tempErrorMap.addToErrorPath(BCPropertyConstants.SALARY_SETTING_EXPANSION);

        // check object, subobject, incumbent, position
        boolean hasValidReference = true;
        hasValidReference &= budgetConstructionRuleHelperService.hasValidObjectCode(appointmentFunding, tempErrorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidSubObjectCode(appointmentFunding, tempErrorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidIncumbentQuickSalarySetting(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidPosition(appointmentFunding, errorMap);
        if (!hasValidReference) {
            errorMap.merge(tempErrorMap);
            return hasValidReference;
        }

        // check position default object code
        String defaultObjectCode = appointmentFunding.getBudgetConstructionPosition().getIuDefaultObjectCode();
        String objectCode = appointmentFunding.getFinancialObjectCode();
        if (!StringUtils.equals(objectCode, defaultObjectCode)) {
            errorMap.putError(KFSPropertyConstants.POSITION_NUMBER, BCKeyConstants.ERROR_NOT_DEFAULT_OBJECT_CODE, defaultObjectCode);
            return false;
        }

        // check for active job
        if (!appointmentFunding.getEmplid().equalsIgnoreCase("VACANT")){
            Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();
            String emplid = appointmentFunding.getEmplid();
            String positionNumber = appointmentFunding.getPositionNumber();
            boolean hasActiveJob = humanResourcesPayrollService.isActiveJob(emplid, positionNumber, fiscalYear, SynchronizationCheckType.NONE);
            if (!hasActiveJob) {
                errorMap.putError(KFSPropertyConstants.POSITION_NUMBER, BCKeyConstants.ERROR_NO_ACTIVE_JOB_FOUND, appointmentFunding.getEmplid(), appointmentFunding.getPositionNumber());
                return hasActiveJob;
            }
        }

        // using request amount as property to light up on error
        boolean isAssociatedWithBudgetableDocument = budgetConstructionRuleHelperService.isAssociatedWithValidDocument(appointmentFunding, errorMap, BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT);
        if (!isAssociatedWithBudgetableDocument) {
            return isAssociatedWithBudgetableDocument;
        }

        boolean hasValidAmounts = this.hasValidAmountsQuickSalarySetting(appointmentFunding, errorMap);
        if (!hasValidAmounts) {
            return hasValidAmounts;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processSaveAppointmentFunding(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processSaveAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding, SynchronizationCheckType synchronizationCheckType) {
        LOG.debug("processSaveAppointmentFunding() start");

        boolean hasValidFormat = budgetConstructionRuleHelperService.isFieldFormatValid(appointmentFunding, errorMap);
        if (!hasValidFormat) {
            return hasValidFormat;
        }

        boolean hasValidReferences = this.hasValidRefences(appointmentFunding, errorMap);
        if (!hasValidReferences) {
            return hasValidReferences;
        }

        boolean isObjectCodeMatching = salarySettingRuleHelperService.hasObjectCodeMatchingDefaultOfPosition(appointmentFunding, errorMap);
        if (!isObjectCodeMatching) {
            return isObjectCodeMatching;
        }

        boolean hasActiveJob = salarySettingRuleHelperService.hasActiveJob(appointmentFunding, errorMap, synchronizationCheckType);
        if (!hasActiveJob) {
            return hasActiveJob;
        }

        // using request amount as property to light up on error
        boolean isAssociatedWithBudgetableDocument = budgetConstructionRuleHelperService.isAssociatedWithValidDocument(appointmentFunding, errorMap, BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT);
        if (!isAssociatedWithBudgetableDocument) {
            return isAssociatedWithBudgetableDocument;
        }

        boolean hasValidAmounts = this.hasValidAmounts(appointmentFunding, errorMap);
        if (!hasValidAmounts) {
            return hasValidAmounts;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processAddAppointmentFunding(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processAddAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> existingAppointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding, SynchronizationCheckType synchronizationCheckType) {
        LOG.debug("processAddAppointmentFunding() start");

        boolean hasNoExistingLine = salarySettingRuleHelperService.hasNoExistingLine(existingAppointmentFundings, appointmentFunding, errorMap);
        if (!hasNoExistingLine) {
            return hasNoExistingLine;
        }

        boolean hasValidFormat = budgetConstructionRuleHelperService.isFieldFormatValid(appointmentFunding, errorMap);
        if (!hasValidFormat) {
            return hasValidFormat;
        }

        boolean hasValidReferences = this.hasValidRefences(appointmentFunding, errorMap);
        if (!hasValidReferences) {
            return hasValidReferences;
        }

        boolean isObjectCodeMatching = salarySettingRuleHelperService.hasObjectCodeMatchingDefaultOfPosition(appointmentFunding, errorMap);
        if (!isObjectCodeMatching) {
            return isObjectCodeMatching;
        }

        boolean hasActiveJob = salarySettingRuleHelperService.hasActiveJob(appointmentFunding, errorMap, synchronizationCheckType);
        if (!hasActiveJob) {
            return hasActiveJob;
        }

        boolean isAssociatedWithBudgetableDocument = budgetConstructionRuleHelperService.isAssociatedWithValidDocument(appointmentFunding, errorMap, KFSPropertyConstants.ACCOUNT_NUMBER);
        if (!isAssociatedWithBudgetableDocument) {
            return isAssociatedWithBudgetableDocument;
        } else {
            appointmentFunding.setBudgetable(Boolean.TRUE);
        }

        boolean hasValidAmounts = this.hasValidAmounts(appointmentFunding, errorMap);
        if (!hasValidAmounts) {
            return hasValidAmounts;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processAdjustSalaraySettingLinePercent(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processAdjustSalaraySettingLinePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        boolean canBeAdjusted = salarySettingRuleHelperService.canBeAdjusted(appointmentFunding, errorMap);
        if (!canBeAdjusted) {
            return false;
        }

        boolean hasValidAdjustmentAmount = salarySettingRuleHelperService.hasValidAdjustmentAmount(appointmentFunding, errorMap);
        if (!hasValidAdjustmentAmount) {
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processNormalizePayrateAndAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processNormalizePayrateAndAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        return salarySettingRuleHelperService.hasValidPayRateOrAnnualAmount(appointmentFunding, errorMap);
    }

    // test if all references of the given appointment funding are valid
    private boolean hasValidRefences(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        boolean hasValidReference = budgetConstructionRuleHelperService.hasValidChart(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidAccount(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidObjectCode(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidSubAccount(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidSubObjectCode(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasDetailPositionRequiredObjectCode(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidPosition(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidIncumbent(appointmentFunding, errorMap);

        return hasValidReference;
    }

    // test if all amounts are legal
    private boolean hasValidAmounts(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        boolean hasValidAmounts = salarySettingRuleHelperService.hasValidRequestedAmount(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedFteQuantity(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedFundingMonth(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedTimePercent(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasRequestedAmountZeroWhenFullYearLeave(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasRequestedFteQuantityZeroWhenFullYearLeave(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedCsfAmount(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedCsfTimePercent(appointmentFunding, errorMap);

        return hasValidAmounts;
    }

    // test if request amount and FTE interaction is legal
    private boolean hasValidAmountsQuickSalarySetting(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        boolean hasValidAmounts = salarySettingRuleHelperService.hasValidRequestedAmountQuickSalarySetting(appointmentFunding, errorMap);

        return hasValidAmounts;
    }
}
