/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCPropertyConstants;
import org.kuali.module.budget.bo.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.service.SalarySettingService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the SalarySettingService interface
 */
@Transactional
public class SalarySettingServiceImpl implements SalarySettingService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingServiceImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.budget.service.SalarySettingService#getDisabled()
     */
    public boolean isSalarySettingDisabled() {
        // TODO for now just return false, implement application parameter if decision is made implement this functionality
        return false;

        // return kualiConfigurationService.getApplicationParameterIndicator(KFSConstants.ParameterGroups.SYSTEM,
        // BCConstants.DISABLE_SALARY_SETTING_FLAG);

    }

    /**
     * @see org.kuali.module.budget.service.SalarySettingService#canBeVacant(org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean canBeVacant(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        // the given funding line cannot be a vacant line
        String emplid = appointmentFunding.getEmplid();
        if (BCConstants.VACANT_EMPLID.equals(emplid)) {
            return false;
        }

        // check if the associated position is valid and active
        BudgetConstructionPosition position = appointmentFunding.getBudgetConstructionPosition();
        if (position == null || !position.isBudgetedPosition() || !position.isEffective()) {
            return false;
        }

        // check if there is an existing vacant appintment funcding for the given funding line
        boolean hasBeenVacated = this.hasBeenVacated(appointmentFunding);
        if (hasBeenVacated) {
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.module.budget.service.SalarySettingService#vacateAppointmentFunding(org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding)
     */
    public PendingBudgetConstructionAppointmentFunding vacateAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        boolean canBeVacant = this.canBeVacant(appointmentFunding);
        if (!canBeVacant) {
            return null;
        }

        PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding = this.createVacantAppointmentFunding(appointmentFunding);
        this.resetAppointmentFunding(appointmentFunding);

        return vacantAppointmentFunding;
    }

    /**
     * @see org.kuali.module.budget.service.SalarySettingService#adjustRequestedSalaryByAmount(org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding)
     */
    public void adjustRequestedSalaryByAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        int inputAdjustmentAmount = appointmentFunding.getAdjustmentAmount().intValue();

        KualiInteger adjustmentAmount = new KualiInteger(inputAdjustmentAmount);
        KualiInteger csfAmount = this.getCsfAmount(appointmentFunding);
        KualiInteger appointmentRequestedAmount = csfAmount.add(adjustmentAmount);

        appointmentFunding.setAppointmentRequestedAmount(appointmentRequestedAmount);
    }

    /**
     * @see org.kuali.module.budget.service.SalarySettingService#adjustRequestedSalaryByPercent(org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding)
     */
    public void adjustRequestedSalaryByPercent(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        KualiInteger csfAmount = this.getCsfAmount(appointmentFunding);

        if (csfAmount.isNonZero()) {
            KualiDecimal percent = appointmentFunding.getAdjustmentAmount();
            BigDecimal adjustedAmount = csfAmount.multiply(percent).divide(KFSConstants.ONE_HUNDRED);
            
            KualiInteger appointmentRequestedAmount = new KualiInteger(adjustedAmount).add(csfAmount);
            appointmentFunding.setAppointmentRequestedAmount(appointmentRequestedAmount);
        }
    }

    /**
     * get the csf tracker amount of the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return the csf tracker amount of the given appointment funding if any; otherwise, return zero
     */
    private KualiInteger getCsfAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        if (appointmentFunding == null) {
            return KualiInteger.ZERO;
        }

        List<BudgetConstructionCalculatedSalaryFoundationTracker> csfTracker = appointmentFunding.getBcnCalculatedSalaryFoundationTracker();
        if (csfTracker == null || csfTracker.isEmpty()) {
            return KualiInteger.ZERO;
        }

        return csfTracker.get(0).getCsfAmount();
    }

    /**
     * reset the given appointment funcding as deleted
     * 
     * @param appointmentFunding the given appointment funcding
     */
    private void resetAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        appointmentFunding.setAppointmentFundingDeleteIndicator(true);

        appointmentFunding.setAppointmentRequestedAmount(KualiInteger.ZERO);
        appointmentFunding.setAppointmentRequestedTimePercent(BigDecimal.ZERO);
        appointmentFunding.setAppointmentRequestedPayRate(BigDecimal.ZERO);

        appointmentFunding.setAppointmentRequestedCsfAmount(KualiInteger.ZERO);
        appointmentFunding.setAppointmentRequestedCsfFteQuantity(BigDecimal.ZERO);
        appointmentFunding.setAppointmentRequestedCsfTimePercent(BigDecimal.ZERO);
        appointmentFunding.setAppointmentRequestedFteQuantity(BigDecimal.ZERO);

        appointmentFunding.setAppointmentFundingDurationCode(BCConstants.APPOINTMENT_FUNDING_DURATION_DEFAULT);
        appointmentFunding.setAppointmentTotalIntendedAmount(KualiInteger.ZERO);
        appointmentFunding.setAppointmentTotalIntendedFteQuantity(BigDecimal.ZERO);
    }

    /**
     * determine whether there exists at lease one vacant funding line for the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if there exists at lease one vacant funding line for the given appointment funding; otherwise, return false
     */
    private boolean hasBeenVacated(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        Map<String, String> keyFieldValues = new HashMap<String, String>();
        keyFieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, appointmentFunding.getChartOfAccountsCode());
        keyFieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, appointmentFunding.getAccountNumber());
        keyFieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, appointmentFunding.getSubAccountNumber());
        keyFieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, appointmentFunding.getFinancialObjectCode());
        keyFieldValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, appointmentFunding.getFinancialSubObjectCode());
        keyFieldValues.put(KFSPropertyConstants.POSITION_NUMBER, appointmentFunding.getPositionNumber());
        keyFieldValues.put(KFSPropertyConstants.EMPLID, BCConstants.VACANT_EMPLID);

        return businessObjectService.countMatching(PendingBudgetConstructionAppointmentFunding.class, keyFieldValues) > 0;
    }

    /**
     * create a vacant appointment funding based on the given budget funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return a vacant appointment funding
     */
    private PendingBudgetConstructionAppointmentFunding createVacantAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding = new PendingBudgetConstructionAppointmentFunding();

        ObjectUtil.buildObject(vacantAppointmentFunding, appointmentFunding);
        vacantAppointmentFunding.setEmplid(BCConstants.VACANT_EMPLID);
        vacantAppointmentFunding.setAppointmentFundingDeleteIndicator(false);
        vacantAppointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_CALCULATED_SALARY_FOUNDATION_TRACKER);

        return vacantAppointmentFunding;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
