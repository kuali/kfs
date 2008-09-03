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
package org.kuali.kfs.module.bc.document.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.integration.businessobject.LaborLedgerObject;
import org.kuali.kfs.integration.service.LaborModuleService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReason;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;
import org.kuali.kfs.module.bc.document.service.BenefitsCalculationService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.PermissionService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.module.bc.util.SalarySettingCalculator;
import org.kuali.kfs.module.bc.util.SalarySettingFieldsHolder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;
import org.springframework.transaction.annotation.Transactional;

/**
 * implements the service methods defined in the SalarySettingService
 * 
 * @see org.kuali.kfs.module.bc.document.service.SalarySettingService
 */
@Transactional
public class SalarySettingServiceImpl implements SalarySettingService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingServiceImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
    private LaborModuleService laborModuleService;
    private BudgetDocumentService budgetDocumentService;
    private BenefitsCalculationService benefitsCalculationService;
    private PermissionService permissionService;

    /**
     * for now just return false, implement application parameter if decision is made implement this functionality
     * 
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#isSalarySettingDisabled()
     */
    public boolean isSalarySettingDisabled() {
        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#calculateHourlyPayRate(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public BigDecimal calculateHourlyPayRate(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("calculateHourlyPayRate() start");

        KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();
        BigDecimal fteQuantity = this.calculateFteQuantityFromAppointmentFunding(appointmentFunding);

        BigDecimal annualWorkingHours = BigDecimal.valueOf(BudgetParameterFinder.getAnnualWorkingHours());
        BigDecimal totalPayHoursForYear = fteQuantity.multiply(annualWorkingHours);
        BigDecimal hourlyPayRate = requestedAmount.divide(totalPayHoursForYear).setScale(2, BigDecimal.ROUND_HALF_EVEN);

        return hourlyPayRate;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#calculateAnnualPayAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public KualiInteger calculateAnnualPayAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("calculateAnnualPayAmount() start");

        BigDecimal hourlyPayRate = appointmentFunding.getAppointmentRequestedPayRate();
        BigDecimal fteQuantity = this.calculateFteQuantityFromAppointmentFunding(appointmentFunding);
        BigDecimal annualWorkingHours = BigDecimal.valueOf(BudgetParameterFinder.getAnnualWorkingHours());
        BigDecimal totalPayHoursForYear = fteQuantity.multiply(annualWorkingHours);
        KualiInteger annualPayAmount = new KualiInteger(hourlyPayRate.multiply(totalPayHoursForYear));

        return annualPayAmount;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#normalizePayRateAndAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void normalizePayRateAndAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("normalizePayRateAndAmount() start");

        BigDecimal currentHourlyPayRate = appointmentFunding.getAppointmentRequestedPayRate();
        if (currentHourlyPayRate != null) {
            KualiInteger annualPayAmount = this.calculateAnnualPayAmount(appointmentFunding);
            appointmentFunding.setAppointmentRequestedAmount(annualPayAmount);
        }

        KualiInteger currentAnnualPayAmount = appointmentFunding.getAppointmentRequestedAmount();
        if (currentAnnualPayAmount != null && currentAnnualPayAmount.isNonZero()) {
            BigDecimal hourlyPayRate = this.calculateHourlyPayRate(appointmentFunding);
            appointmentFunding.setAppointmentRequestedPayRate(hourlyPayRate);
        }

        currentHourlyPayRate = appointmentFunding.getAppointmentRequestedPayRate();
        if (currentHourlyPayRate != null) {
            KualiInteger annualPayAmount = this.calculateAnnualPayAmount(appointmentFunding);
            appointmentFunding.setAppointmentRequestedAmount(annualPayAmount);
        }

        currentAnnualPayAmount = appointmentFunding.getAppointmentRequestedAmount();
        if (currentAnnualPayAmount != null && currentAnnualPayAmount.isNonZero()) {
            BigDecimal hourlyPayRate = this.calculateHourlyPayRate(appointmentFunding);
            appointmentFunding.setAppointmentRequestedPayRate(hourlyPayRate);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#calculateFteQuantityForAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public BigDecimal calculateFteQuantityFromAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("calculateFteQuantity() start");

        // appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION);
        BudgetConstructionPosition position = appointmentFunding.getBudgetConstructionPosition();
        if (position == null) {
            return BigDecimal.ZERO;
        }

        Integer payMonth = position.getIuPayMonths();
        Integer fundingMonth = appointmentFunding.getAppointmentFundingMonth();
        BigDecimal requestedTimePercent = appointmentFunding.getAppointmentRequestedTimePercent();

        return this.calculateFteQuantity(payMonth, fundingMonth, requestedTimePercent);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#calculateFteQuantity(java.lang.Integer, java.lang.Integer,
     *      java.math.BigDecimal)
     */
    public BigDecimal calculateFteQuantity(Integer payMonth, Integer fundingMonth, BigDecimal requestedTimePercent) {
        LOG.debug("calculateFteQuantity() start");

        if (payMonth == null || fundingMonth == null || requestedTimePercent == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal payMonthAsDecimal = BigDecimal.valueOf(payMonth);
        BigDecimal fundingMonthAsDecimal = BigDecimal.valueOf(fundingMonth);
        BigDecimal fundingMonthPercent = fundingMonthAsDecimal.divide(payMonthAsDecimal, 4, BigDecimal.ROUND_HALF_EVEN);

        return requestedTimePercent.multiply(fundingMonthPercent).divide(KFSConstants.ONE_HUNDRED.bigDecimalValue());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#isHourlyPaid(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger)
     */
    public boolean isHourlyPaid(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger) {
        LOG.debug("isHourlyPaid() start");

        Integer fiscalYear = pendingBudgetConstructionGeneralLedger.getUniversityFiscalYear();
        String chartOfAccountsCode = pendingBudgetConstructionGeneralLedger.getChartOfAccountsCode();
        String objectCode = pendingBudgetConstructionGeneralLedger.getFinancialObjectCode();

        return this.isHourlyPaidObject(fiscalYear, chartOfAccountsCode, objectCode);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#isHourlyPaid(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger)
     */
    public boolean isHourlyPaidObject(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        LOG.debug("isHourlyPaid() start");

        LaborLedgerObject laborLedgerObject = laborModuleService.retrieveLaborLedgerObject(fiscalYear, chartOfAccountsCode, objectCode);

        if (laborLedgerObject == null) {
            return false;
        }

        return BudgetParameterFinder.getBiweeklyPayTypeCodes().contains(laborLedgerObject.getFinancialObjectPayTypeCode());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#canBeVacant(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean canBeVacant(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("canBeVacant(List, PendingBudgetConstructionAppointmentFunding) start");

        if (!this.canBeVacant(appointmentFunding)) {
            return false;
        }

        return this.findVacantAppointmentFunding(appointmentFundings, appointmentFunding) == null;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#findVacantAppointmentFunding(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public PendingBudgetConstructionAppointmentFunding findVacantAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("findVacantAppointmentFunding() start");

        PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding = this.createVacantAppointmentFunding(appointmentFunding);

        return this.findAppointmentFunding(appointmentFundings, vacantAppointmentFunding);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#findAppointmentFunding(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public PendingBudgetConstructionAppointmentFunding findAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("findAppointmentFunding() start");

        Map<String, Object> keyFieldValues = appointmentFunding.getValuesMap();
        List<String> keyFields = new ArrayList<String>();
        keyFields.addAll(keyFieldValues.keySet());

        // determine whether there is vacant for the given appointment funding in its list
        for (PendingBudgetConstructionAppointmentFunding fundingLine : appointmentFundings) {
            if (ObjectUtil.equals(fundingLine, appointmentFunding, keyFields)) {
                return fundingLine;
            }
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#canBeVacant(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean canBeVacant(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("canBeVacant() start");

        if (appointmentFunding.isNewLineIndicator()) {
            return false;
        }

        // the given funding line has not been deleted
        if (appointmentFunding.isAppointmentFundingDeleteIndicator()) {
            return false;
        }

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
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#vacateAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public PendingBudgetConstructionAppointmentFunding vacateAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("vacateAppointmentFunding() start");

        PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding = this.createVacantAppointmentFunding(appointmentFunding);
        this.markAsDelete(appointmentFunding);

        return vacantAppointmentFunding;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#vacateAppointmentFunding(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public PendingBudgetConstructionAppointmentFunding vacateAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding = this.vacateAppointmentFunding(appointmentFunding);

        if (vacantAppointmentFunding != null) {
            appointmentFundings.add(vacantAppointmentFunding);
        }

        return vacantAppointmentFunding;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#purgeAppointmentFundings(java.util.List)
     */
    public void purgeAppointmentFundings(List<PendingBudgetConstructionAppointmentFunding> purgedAppointmentFundings) {        
        // remove the purged appointment funding lines and their referenced records
        for(PendingBudgetConstructionAppointmentFunding appointmentFunding : purgedAppointmentFundings) {
            if(!appointmentFunding.isNewLineIndicator()) {
                List<BudgetConstructionAppointmentFundingReason> fundingReasons = appointmentFunding.getBudgetConstructionAppointmentFundingReason();
                fundingReasons.get(0).setAppointmentFundingReasonCode(null);
                
                this.preprocessFundingReason(appointmentFunding);
                businessObjectService.delete(appointmentFunding);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#adjustRequestedSalaryByAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void adjustRequestedSalaryByAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("adjustRequestedSalaryByAmount() start");

        int inputAdjustmentAmount = appointmentFunding.getAdjustmentAmount().intValue();

        KualiInteger adjustmentAmount = new KualiInteger(inputAdjustmentAmount);
        KualiInteger csfAmount = this.getCsfAmount(appointmentFunding);
        KualiInteger appointmentRequestedAmount = csfAmount.add(adjustmentAmount);

        appointmentFunding.setAppointmentRequestedAmount(appointmentRequestedAmount);

        if (appointmentFunding.isHourlyPaid()) {
            this.normalizePayRateAndAmount(appointmentFunding);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#adjustRequestedSalaryByPercent(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void adjustRequestedSalaryByPercent(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("adjustRequestedSalaryByPercent() start");

        KualiInteger csfAmount = this.getCsfAmount(appointmentFunding);

        if (csfAmount.isNonZero()) {
            KualiDecimal percent = appointmentFunding.getAdjustmentAmount();
            BigDecimal adjustedAmount = csfAmount.multiply(percent).divide(KFSConstants.ONE_HUNDRED);

            KualiInteger appointmentRequestedAmount = new KualiInteger(adjustedAmount).add(csfAmount);
            appointmentFunding.setAppointmentRequestedAmount(appointmentRequestedAmount);
        }

        if (appointmentFunding.isHourlyPaid()) {
            this.normalizePayRateAndAmount(appointmentFunding);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#saveSalarySetting(org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion)
     */
    public void saveSalarySetting(SalarySettingExpansion salarySettingExpansion) {
        LOG.debug("saveSalarySetting() start");

        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding();
        this.resetDeletedFundingLines(appointmentFundings);
        this.updateAppointmentFundingsBeforeSaving(appointmentFundings);

        KualiInteger requestedAmountTotal = SalarySettingCalculator.getAppointmentRequestedAmountTotal(appointmentFundings);
        KualiInteger changes = KualiInteger.ZERO;

        if (requestedAmountTotal != null) {
            KualiInteger annualBalanceAmount = salarySettingExpansion.getAccountLineAnnualBalanceAmount();
            changes = (annualBalanceAmount != null) ? requestedAmountTotal.subtract(annualBalanceAmount) : requestedAmountTotal;
        }

        salarySettingExpansion.setAccountLineAnnualBalanceAmount(requestedAmountTotal);
        businessObjectService.save(salarySettingExpansion);

        // update or create plug line if the total amount has been changed
        if (changes.isNonZero()) {
            budgetDocumentService.updatePendingBudgetGeneralLedgerPlug(appointmentFundings.get(0), changes.negated());
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#saveSalarySetting(java.util.List)
     */
    public void saveSalarySetting(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings) {
        Set<SalarySettingExpansion> salarySettingExpansionSet = new HashSet<SalarySettingExpansion>();
        for (PendingBudgetConstructionAppointmentFunding fundingLine : appointmentFundings) {
            SalarySettingExpansion salarySettingExpansion = this.retriveSalarySalarySettingExpansion(fundingLine);

            if (salarySettingExpansion != null) {
                salarySettingExpansionSet.add(salarySettingExpansion);
            }
        }

        this.saveAppointmentFundings(appointmentFundings);

        for (SalarySettingExpansion salarySettingExpansion : salarySettingExpansionSet) {
            this.saveSalarySetting(salarySettingExpansion);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#saveAppointmentFundings(java.util.List)
     */
    public void saveAppointmentFundings(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings) {
        LOG.debug("saveAppointmentFundings() start");
        
        // remove the appointment funding lines being purged
        List<PendingBudgetConstructionAppointmentFunding> purgedAppointmentFundings = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
        for(PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            if(appointmentFunding.isPurged()) {
                purgedAppointmentFundings.add(appointmentFunding);
            }
        }
        this.purgeAppointmentFundings(purgedAppointmentFundings);

        // save the appointment funding lines that have been updated or newly created
        List<PendingBudgetConstructionAppointmentFunding> savableAppointmentFundings = new ArrayList<PendingBudgetConstructionAppointmentFunding>(appointmentFundings);
        savableAppointmentFundings.removeAll(purgedAppointmentFundings);        
        this.updateAppointmentFundingsBeforeSaving(savableAppointmentFundings);
        businessObjectService.save(savableAppointmentFundings);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#retriveSalarySalarySettingExpansion(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public SalarySettingExpansion retriveSalarySalarySettingExpansion(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        BudgetConstructionHeader budgetDocument = budgetDocumentService.getBudgetConstructionHeader(appointmentFunding);

        Map<String, Object> fieldValues = ObjectUtil.buildPropertyMap(appointmentFunding, SalarySettingExpansion.getPrimaryKeyFields());
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, budgetDocument.getDocumentNumber());

        return (SalarySettingExpansion) businessObjectService.findByPrimaryKey(SalarySettingExpansion.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#resetAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void resetAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        appointmentFunding.setAppointmentRequestedAmount(KualiInteger.ZERO);
        appointmentFunding.setAppointmentRequestedTimePercent(BigDecimal.ZERO);
        appointmentFunding.setAppointmentRequestedPayRate(BigDecimal.ZERO);
        appointmentFunding.setAppointmentRequestedFteQuantity(BigDecimal.ZERO);

        appointmentFunding.setAppointmentRequestedCsfAmount(KualiInteger.ZERO);
        appointmentFunding.setAppointmentRequestedCsfFteQuantity(BigDecimal.ZERO);
        appointmentFunding.setAppointmentRequestedCsfTimePercent(BigDecimal.ZERO);

        appointmentFunding.setAppointmentTotalIntendedAmount(KualiInteger.ZERO);
        appointmentFunding.setAppointmentTotalIntendedFteQuantity(BigDecimal.ZERO);

        appointmentFunding.setAppointmentFundingDurationCode(BCConstants.AppointmentFundingDurationCodes.NONE.durationCode);

        appointmentFunding.setPositionObjectChangeIndicator(false);
        appointmentFunding.setPositionSalaryChangeIndicator(false);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#markAsDelete(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void markAsDelete(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        this.resetAppointmentFunding(appointmentFunding);

        appointmentFunding.setAppointmentFundingDeleteIndicator(true);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#revert(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void revert(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        PendingBudgetConstructionAppointmentFunding vacantFunding = this.findVacantAppointmentFunding(appointmentFundings, appointmentFunding);

        if (vacantFunding != null) {
            appointmentFundings.remove(vacantFunding);
        }

        PendingBudgetConstructionAppointmentFunding newAppointmentFunding = (PendingBudgetConstructionAppointmentFunding) businessObjectService.retrieve(appointmentFunding);
        appointmentFundings.add(newAppointmentFunding);
        appointmentFundings.remove(appointmentFunding);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#updateAccessOfAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.kfs.module.bc.util.SalarySettingFieldsHolder, boolean, org.kuali.core.bo.user.UniversalUser)
     */
    public boolean updateAccessOfAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding, SalarySettingFieldsHolder salarySettingFieldsHolder, boolean budgetByObjectMode, UniversalUser universalUser) {
        String budgetChartOfAccountsCode = salarySettingFieldsHolder.getChartOfAccountsCode();
        String budgetAccountNumber = salarySettingFieldsHolder.getAccountNumber();
        String budgetSubAccountNumber = salarySettingFieldsHolder.getSubAccountNumber();
        String budgetObjectCode = salarySettingFieldsHolder.getFinancialObjectCode();
        String budgetSubObjectCode = salarySettingFieldsHolder.getFinancialSubObjectCode();

        String chartOfAccountsCode = appointmentFunding.getChartOfAccountsCode();
        String accountNumber = appointmentFunding.getAccountNumber();
        String subAccountNumber = appointmentFunding.getSubAccountNumber();
        String objectCode = appointmentFunding.getFinancialObjectCode();
        String subObjectCode = appointmentFunding.getFinancialSubObjectCode();
        
        // just allow edit if budget by object mode (general case of single account mode)
        if (budgetByObjectMode && StringUtils.equals(chartOfAccountsCode, budgetChartOfAccountsCode) && StringUtils.equals(accountNumber, budgetAccountNumber) && StringUtils.equals(subAccountNumber, budgetSubAccountNumber)) {           
            appointmentFunding.setDisplayOnlyMode(false);
            return true;
        }

        boolean isUpdatedByUserLevel = this.updateAccessOfAppointmentFundingByUserLevel(appointmentFunding, universalUser);
        if (isUpdatedByUserLevel) {
            return true;
        }
        
        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#updateAccessOfAppointmentFundingByUserLevel(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.kns.bo.user.UniversalUser)
     */
    public boolean updateAccessOfAppointmentFundingByUserLevel(PendingBudgetConstructionAppointmentFunding appointmentFunding, UniversalUser universalUser) {
        BudgetConstructionHeader budgetConstructionHeader = budgetDocumentService.getBudgetConstructionHeader(appointmentFunding);
        if (budgetConstructionHeader == null) {
            return false;
        }

        Integer documentOrganizationLevelCode = budgetConstructionHeader.getOrganizationLevelCode();
        Account account = appointmentFunding.getAccount();

        // account manager or delegate could edit the appointment funding if the document is in the beginning level
        if (documentOrganizationLevelCode == 0 && permissionService.isAccountManagerOrDelegate(account, universalUser)) {
            appointmentFunding.setDisplayOnlyMode(false);
            return true;
        }

        // get the organization review hierachy path for which the user could be an approver
        List<Org> organazationReviewHierachy = permissionService.getOrganizationReviewHierachy(universalUser);
        if (organazationReviewHierachy == null || organazationReviewHierachy.isEmpty()) {
            return false;
        }

        String accessMode = budgetDocumentService.getAccessMode(budgetConstructionHeader, universalUser);
        if (StringUtils.equals(accessMode, KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY)) {
            appointmentFunding.setDisplayOnlyMode(false);
        }
        else {
            appointmentFunding.setDisplayOnlyMode(true);
        }

        if (StringUtils.equals(accessMode, KfsAuthorizationConstants.BudgetConstructionEditMode.USER_BELOW_DOC_LEVEL)) {
            appointmentFunding.setExcludedFromTotal(true);
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#saveAppointmentFundings(java.util.List)
     */
    public void updateAppointmentFundingsBeforeSaving(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings) {
        LOG.debug("updateAppointmentFundingsBeforeSaving() start");

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {            
            this.preprocessFundingReason(appointmentFunding);
            this.preprocessLeaveRequest(appointmentFunding);

            if (appointmentFunding.isHourlyPaid()) {
                this.normalizePayRateAndAmount(appointmentFunding);
            }

            BigDecimal requestedFteQuantity = this.calculateFteQuantityFromAppointmentFunding(appointmentFunding);
            appointmentFunding.setAppointmentRequestedFteQuantity(requestedFteQuantity);
            
            appointmentFunding.setNewLineIndicator(false);
        }
    }

    /**
     * reset the amount values of each line in the given appointment fundings as zeros and remove the reason annotations if the line
     * is marked as deleted
     * 
     * @param pendingBudgetConstructionAppointmentFunding the given appointment fundings
     */
    private void resetDeletedFundingLines(List<PendingBudgetConstructionAppointmentFunding> pendingBudgetConstructionAppointmentFunding) {
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : pendingBudgetConstructionAppointmentFunding) {
            if (!appointmentFunding.isAppointmentFundingDeleteIndicator() || appointmentFunding.isPersistedDeleteIndicator()) {
                continue;
            }

            this.markAsDelete(appointmentFunding);
            List<BudgetConstructionAppointmentFundingReason> reasons = appointmentFunding.getBudgetConstructionAppointmentFundingReason();
            if (reasons != null) {
                reasons.clear();
            }
            
            appointmentFunding.setPersistedDeleteIndicator(true);
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

        BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = appointmentFunding.getEffectiveCSFTracker();
        if (csfTracker == null) {
            return KualiInteger.ZERO;
        }

        return csfTracker.getCsfAmount();
    }

    /**
     * determine whether there exists at lease one vacant funding line for the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if there exists at lease one vacant funding line for the given appointment funding; otherwise, return false
     */
    private boolean hasBeenVacated(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        Map<String, Object> keyFieldValues = appointmentFunding.getValuesMap();
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

        ObjectUtil.buildObjectWithoutReferenceFields(vacantAppointmentFunding, appointmentFunding);
        vacantAppointmentFunding.setEmplid(BCConstants.VACANT_EMPLID);
        vacantAppointmentFunding.setAppointmentFundingDeleteIndicator(false);
        vacantAppointmentFunding.setPersistedDeleteIndicator(false);
        vacantAppointmentFunding.setVersionNumber(null);

        return vacantAppointmentFunding;
    }

    /**
     * preprocess the funding reason of the given appointment funding before the funding is saved
     * 
     * @param appointmentFunding the given appointment funding
     */
    private void preprocessFundingReason(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        businessObjectService.deleteMatching(BudgetConstructionAppointmentFundingReason.class, appointmentFunding.getValuesMap());

        List<BudgetConstructionAppointmentFundingReason> fundingReasons = appointmentFunding.getBudgetConstructionAppointmentFundingReason();
        if (StringUtils.isBlank(fundingReasons.get(0).getAppointmentFundingReasonCode())) {
            fundingReasons.clear();
        }
    }

    /**
     * preprocess the leave request of the given appointment funding before the funding is saved
     * 
     * @param appointmentFunding the given appointment funding
     */
    private void preprocessLeaveRequest(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        String durationCode = appointmentFunding.getAppointmentFundingDurationCode();

        if (StringUtils.isEmpty(durationCode) || StringUtils.equals(durationCode, BCConstants.AppointmentFundingDurationCodes.NONE.durationCode)) {
            appointmentFunding.setAppointmentRequestedCsfAmount(KualiInteger.ZERO);
            appointmentFunding.setAppointmentRequestedCsfFteQuantity(BigDecimal.ZERO);
            appointmentFunding.setAppointmentRequestedCsfTimePercent(BigDecimal.ZERO);
        }
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

    /**
     * Sets the budgetDocumentService attribute value.
     * 
     * @param budgetDocumentService The budgetDocumentService to set.
     */
    public void setBudgetDocumentService(BudgetDocumentService budgetDocumentService) {
        this.budgetDocumentService = budgetDocumentService;
    }

    /**
     * Sets the benefitsCalculationService attribute value.
     * 
     * @param benefitsCalculationService The benefitsCalculationService to set.
     */
    public void setBenefitsCalculationService(BenefitsCalculationService benefitsCalculationService) {
        this.benefitsCalculationService = benefitsCalculationService;
    }

    /**
     * Sets the permissionService attribute value.
     * 
     * @param permissionService The permissionService to set.
     */
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
}
