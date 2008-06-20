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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
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
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.bo.SalarySettingExpansion;
import org.kuali.module.budget.service.SalarySettingService;
import org.kuali.module.integration.bo.LaborLedgerObject;
import org.kuali.module.integration.service.LaborModuleService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the SalarySettingService interface
 */
@Transactional
public class SalarySettingServiceImpl implements SalarySettingService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingServiceImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
    private LaborModuleService laborModuleService;

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
     * @see org.kuali.module.budget.service.SalarySettingService#calculateHourlyPayRate(org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding)
     */
    public BigDecimal calculateHourlyPayRate(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.info("calculateHourlyPayRate() start");
        
        KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();
        BigDecimal fteQuantity = this.calculateFteQuantity(appointmentFunding);
        BigDecimal totalPayHoursForYear = fteQuantity.multiply(BCConstants.TOTAL_WORKING_HOUR_IN_YEAR.bigDecimalValue());
        BigDecimal hourlyPayRate = requestedAmount.divide(totalPayHoursForYear).setScale(2, BigDecimal.ROUND_HALF_EVEN);

        return hourlyPayRate;
    }

    /**
     * @see org.kuali.module.budget.service.SalarySettingService#calculateAnnualPayAmount(org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding)
     */
    public KualiInteger calculateAnnualPayAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.info("calculateAnnualPayAmount() start");
        
        BigDecimal hourlyPayRate = appointmentFunding.getAppointmentRequestedPayRate();
        BigDecimal fteQuantity = this.calculateFteQuantity(appointmentFunding);
        BigDecimal totalPayHoursForYear = fteQuantity.multiply(BCConstants.TOTAL_WORKING_HOUR_IN_YEAR.bigDecimalValue());
        KualiInteger annualPayAmount = new KualiInteger(hourlyPayRate.multiply(totalPayHoursForYear));

        return annualPayAmount;
    }
    
    /**
     * calculate the fte quantity based on the information of the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return the fte quantity calculated from the information of the given appointment funding
     */
    public BigDecimal calculateFteQuantity(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        BudgetConstructionPosition position = appointmentFunding.getBudgetConstructionPosition();
        BigDecimal payMonth = BigDecimal.valueOf(position.getIuPayMonths());
        BigDecimal fundingMonth = BigDecimal.valueOf(appointmentFunding.getAppointmentFundingMonth());
        BigDecimal requestedTimePercent = appointmentFunding.getAppointmentRequestedTimePercent();

        BigDecimal fundingMonthPercent = fundingMonth.divide(payMonth);
        BigDecimal fteQuantity = requestedTimePercent.multiply(fundingMonthPercent).divide(KFSConstants.ONE_HUNDRED.bigDecimalValue());

        return fteQuantity;
    }

    /**
     * @see org.kuali.module.budget.service.SalarySettingService#isHourlyPaid(org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger)
     */
    public boolean isHourlyPaid(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger) {
        LOG.info("isHourlyPaid() start");
        
        Integer fiscalYear = pendingBudgetConstructionGeneralLedger.getUniversityFiscalYear();
        String chartOfAccountsCode = pendingBudgetConstructionGeneralLedger.getChartOfAccountsCode();
        String objectCode = pendingBudgetConstructionGeneralLedger.getFinancialObjectCode();

        LaborLedgerObject laborLedgerObject = laborModuleService.retrieveLaborLedgerObject(fiscalYear, chartOfAccountsCode, objectCode);

        if (laborLedgerObject == null) {
            return false;
        }

        return StringUtils.equals(laborLedgerObject.getFinancialObjectPayTypeCode(), BCConstants.HOURLY_PAY_TYPE_CODE);
    }

    /**
     * @see org.kuali.module.budget.service.SalarySettingService#canBeVacant(org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean canBeVacant(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.info("canBeVacant() start");

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
        LOG.info("vacateAppointmentFunding() start");
        
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
        LOG.info("adjustRequestedSalaryByAmount() start");
        
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
        LOG.info("adjustRequestedSalaryByPercent() start");
        
        KualiInteger csfAmount = this.getCsfAmount(appointmentFunding);

        if (csfAmount.isNonZero()) {
            KualiDecimal percent = appointmentFunding.getAdjustmentAmount();
            BigDecimal adjustedAmount = csfAmount.multiply(percent).divide(KFSConstants.ONE_HUNDRED);

            KualiInteger appointmentRequestedAmount = new KualiInteger(adjustedAmount).add(csfAmount);
            appointmentFunding.setAppointmentRequestedAmount(appointmentRequestedAmount);
        }
    }

    /**
     * @see org.kuali.module.budget.service.SalarySettingService#saveSalarySetting(org.kuali.module.budget.bo.SalarySettingExpansion)
     */
    public void saveSalarySetting(SalarySettingExpansion salarySettingExpansion) {
        LOG.info("saveSalarySetting() start");

        KualiInteger requestedAmountTotal = salarySettingExpansion.getAppointmentRequestedAmountTotal();
        KualiInteger changes = KualiInteger.ZERO;

        if (requestedAmountTotal != null) {
            KualiInteger annualBalanceAmount = salarySettingExpansion.getAccountLineAnnualBalanceAmount();
            changes = (annualBalanceAmount != null) ? requestedAmountTotal.subtract(annualBalanceAmount) : requestedAmountTotal;
        }

        if (changes.isNonZero()) {
            salarySettingExpansion.setAccountLineAnnualBalanceAmount(requestedAmountTotal);
            businessObjectService.save(salarySettingExpansion);

            List<BudgetConstructionMonthly> budgetConstructionMonthly = this.updateMonthlyAmounts(salarySettingExpansion, changes);
            businessObjectService.save(budgetConstructionMonthly);
        }
    }

    /**
     * @see org.kuali.module.budget.service.SalarySettingService#updateSalarySettingExpansion(org.kuali.module.budget.bo.SalarySettingExpansion)
     */
    @SuppressWarnings("deprecation")
    public void updateSalarySettingExpansion(SalarySettingExpansion salarySettingExpansion) {
        KualiInteger requestedAmountTotal = salarySettingExpansion.getAppointmentRequestedAmountTotal();
        KualiInteger changes = KualiInteger.ZERO;

        if (requestedAmountTotal != null) {
            KualiInteger annualBalanceAmount = salarySettingExpansion.getAccountLineAnnualBalanceAmount();
            changes = (annualBalanceAmount != null) ? annualBalanceAmount.subtract(requestedAmountTotal) : requestedAmountTotal;
        }

        if (changes.isNonZero()) {
            salarySettingExpansion.setAccountLineAnnualBalanceAmount(requestedAmountTotal);
            salarySettingExpansion.setBudgetConstructionMonthly(this.updateMonthlyAmounts(salarySettingExpansion, changes));
        }
    }

    /**
     * adjust existing monthly request amounts using an even spread of the changes
     * 
     * @param pendingBudgetConstructionGeneralLedger the given pending Budget Construction General Ledger record
     * @param changes the given changes that will be used to adjust monthly request amounts using an even spread
     */
    private List<BudgetConstructionMonthly> updateMonthlyAmounts(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, KualiInteger changes) {
        BigInteger countOfMonth = BigInteger.valueOf(BCConstants.BC_MONTHLY_AMOUNTS.size());
        BigInteger[] distribution = changes.bigIntegerValue().divideAndRemainder(countOfMonth);

        KualiInteger monthlyAdjustment = new KualiInteger(distribution[0]);
        int remainder = distribution[1].intValue();

        Map<String, Object> keyMap = pendingBudgetConstructionGeneralLedger.buildPrimaryKeyMap();
        Collection<BudgetConstructionMonthly> monthlyGLRecordsCollection = businessObjectService.findMatching(BudgetConstructionMonthly.class, keyMap);

        List<BudgetConstructionMonthly> monthlyGLRecords = new ArrayList<BudgetConstructionMonthly>();
        for (BudgetConstructionMonthly monthlyGLRecord : monthlyGLRecordsCollection) {
            this.updateMonthlyAmounts(monthlyGLRecord, monthlyAdjustment, remainder);
            monthlyGLRecords.add(monthlyGLRecord);
        }

        return monthlyGLRecords;
    }

    /**
     * update the monthly amounts of the given monthly GL record with the given amount
     * 
     * @param monthlyGLRecord the given montly GL record that will be updated
     * @param adjustment the amount used to adjust the monthly amounts
     * @param remainder the sulpus that can be used to adjust the monthly amounts util it is used up
     */
    private void updateMonthlyAmounts(BudgetConstructionMonthly monthlyGLRecord, KualiInteger adjustment, int remainder) {
        int indexOfMonth = 1;
        for (String[] monthlyAmountFieldPair : BCConstants.BC_MONTHLY_AMOUNTS) {
            String monthlyAmountField = monthlyAmountFieldPair[0];
            if (PropertyUtils.isReadable(monthlyGLRecord, monthlyAmountField) && PropertyUtils.isWriteable(monthlyGLRecord, monthlyAmountField)) {
                try {
                    KualiInteger currentMonthlyAmount = (KualiInteger) PropertyUtils.getProperty(monthlyGLRecord, monthlyAmountField);
                    KualiInteger newMonthlyAmount = this.getNewMonthlyAmount(currentMonthlyAmount, adjustment, indexOfMonth++, remainder);

                    PropertyUtils.setProperty(monthlyGLRecord, monthlyAmountField, newMonthlyAmount);
                }
                catch (Exception e) {
                    LOG.fatal("Cannot update the monthly amount." + e);
                    throw new RuntimeException("Cannot update the monthly amount." + e);
                }
            }
        }
    }

    /**
     * calculate the new monthly amount based on the given information
     * 
     * @param currentMonthlyAmount the current monthly amount
     * @param adjustment the given adjustment that will be added into currentLineAmount
     * @param indexOfMonth the traversing index of the current month, which is used to determine whether current monthly amount can
     *        get additional adjustment from surplus amount
     * @param remainder the sulpus that can be used to adjust the monthly amounts util it is used up. If the surplus is greater than
     *        indexOfMonth, the monthly amount can increase by 1.
     * @return the new monthly amount calculated from the given information
     */
    private KualiInteger getNewMonthlyAmount(KualiInteger currentMonthlyAmount, KualiInteger adjustment, int indexOfMonth, int remainder) {
        KualiInteger newMonthlyAmount = currentMonthlyAmount.add(adjustment);

        return remainder < indexOfMonth ? newMonthlyAmount : newMonthlyAmount.add(KFSConstants.ONE);
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

        BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = appointmentFunding.getEffectiveCSFTracker();
        if (csfTracker == null) {
            return KualiInteger.ZERO;
        }

        return csfTracker.getCsfAmount();
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

    /**
     * Sets the laborModuleService attribute value.
     * 
     * @param laborModuleService The laborModuleService to set.
     */
    public void setLaborModuleService(LaborModuleService laborModuleService) {
        this.laborModuleService = laborModuleService;
    }
}
