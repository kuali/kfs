/*
 * Copyright 2007 The Kuali Foundation
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

import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.NONE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReason;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReasonCode;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BenefitsCalculationService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.module.bc.util.SalarySettingCalculator;
import org.kuali.kfs.module.bc.util.SalarySettingFieldsHolder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * implements the service methods defined in the SalarySettingService
 *
 * @see org.kuali.kfs.module.bc.document.service.SalarySettingService
 */
@Transactional
public class SalarySettingServiceImpl implements SalarySettingService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingServiceImpl.class);

    protected ConfigurationService kualiConfigurationService;
    protected BusinessObjectService businessObjectService;
    protected LaborModuleService laborModuleService;
    protected BudgetDocumentService budgetDocumentService;
    protected BenefitsCalculationService benefitsCalculationService;
    protected OptionsService optionsService;
    protected LockService lockService;
    protected DocumentHelperService documentHelperService;
    protected DocumentService documentService;
    protected BudgetConstructionProcessorService budgetConstructionProcessorService;

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
        BigDecimal hourlyPayRate = BigDecimal.ZERO;
        if (totalPayHoursForYear.compareTo(BigDecimal.ZERO) != 0) {
            hourlyPayRate = requestedAmount.divide(totalPayHoursForYear).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

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
        if (currentHourlyPayRate != null && !currentHourlyPayRate.equals(BigDecimal.ZERO)) {
            KualiInteger annualPayAmount = this.calculateAnnualPayAmount(appointmentFunding);
            appointmentFunding.setAppointmentRequestedAmount(annualPayAmount);
        } else {

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
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#calculateFteQuantityForAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public BigDecimal calculateFteQuantityFromAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("calculateFteQuantity() start");

        // appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION);
        BudgetConstructionPosition position = appointmentFunding.getBudgetConstructionPosition();
        if (ObjectUtils.isNull(position)) {
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
        BigDecimal fundingMonthPercent = fundingMonthAsDecimal.divide(payMonthAsDecimal, 5, BigDecimal.ROUND_HALF_UP);

        BigDecimal fteQuantity = requestedTimePercent.multiply(fundingMonthPercent).divide(KFSConstants.ONE_HUNDRED.bigDecimalValue());

        return fteQuantity.setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#calculateCSFFteQuantityFromAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public BigDecimal calculateCSFFteQuantityFromAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("calculateCSFFteQuantity() start");

        // appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION);
        BudgetConstructionPosition position = appointmentFunding.getBudgetConstructionPosition();
        if (position == null) {
            return BigDecimal.ZERO;
        }

        Integer payMonth = position.getIuPayMonths();
        Integer normalWorkMonth = position.getIuNormalWorkMonths();
        BigDecimal requestedCSFTimePercent = appointmentFunding.getAppointmentRequestedCsfTimePercent();

        return this.calculateCSFFteQuantity(payMonth, normalWorkMonth, requestedCSFTimePercent);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#calculateCSFFteQuantity(java.lang.Integer,
     *      java.lang.Integer, java.math.BigDecimal)
     */
    public BigDecimal calculateCSFFteQuantity(Integer payMonth, Integer normalWorkMonth, BigDecimal requestedCSFTimePercent) {
        LOG.debug("calculateCSFFteQuantity() start");

        if (payMonth == null || normalWorkMonth == null || requestedCSFTimePercent == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal payMonthAsDecimal = BigDecimal.valueOf(payMonth);
        BigDecimal normalMonthAsDecimal = BigDecimal.valueOf(normalWorkMonth);
        BigDecimal fundingMonthPercent = normalMonthAsDecimal.divide(payMonthAsDecimal, 5, BigDecimal.ROUND_HALF_UP);

        BigDecimal fteQuantity = requestedCSFTimePercent.multiply(fundingMonthPercent).divide(KFSConstants.ONE_HUNDRED.bigDecimalValue());

        return fteQuantity.setScale(5, BigDecimal.ROUND_HALF_UP);
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
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#isHourlyPaid(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean isHourlyPaid(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("isHourlyPaid() start");

        Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();
        String chartOfAccountsCode = appointmentFunding.getChartOfAccountsCode();
        String objectCode = appointmentFunding.getFinancialObjectCode();

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
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : purgedAppointmentFundings) {
            if (!appointmentFunding.isNewLineIndicator()) {
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
            appointmentFunding.setAppointmentRequestedPayRate(BigDecimal.ZERO);
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
            appointmentFunding.setAppointmentRequestedPayRate(BigDecimal.ZERO);
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

        // now create a pseudo funding line if the BCAF list is empty so we can pass it to create 2PLG below
        Boolean wasSalarySettingExpansionBCAFEmpty = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding().isEmpty();
        if (wasSalarySettingExpansionBCAFEmpty) {
            appointmentFundings.add(this.createPseudoAppointmentFundingLine(salarySettingExpansion));
        }

        // update or create plug line if the total amount has been changed
        if (changes.isNonZero()) {

            budgetDocumentService.updatePendingBudgetGeneralLedgerPlug(appointmentFundings.get(0), changes.negated());
        }
    }

    public void savePBGLSalarySetting(SalarySettingExpansion salarySettingExpansion) {
        LOG.debug("savePBGLSalarySetting() start");

        // gwp - added this method to handle detail salary setting PBGL updates
        // instead of using saveSalarySetting(SalarySettingExpansion salarySettingExpansion)

        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding();

        // this is already done in saveSalarySetting by a call to saveAppointmentFundings
        // this.resetDeletedFundingLines(appointmentFundings);
        // this.updateAppointmentFundingsBeforeSaving(appointmentFundings);

        KualiInteger requestedAmountTotal = SalarySettingCalculator.getAppointmentRequestedAmountTotal(appointmentFundings);
        KualiInteger changes = KualiInteger.ZERO;

        if (requestedAmountTotal != null) {
            KualiInteger annualBalanceAmount = salarySettingExpansion.getAccountLineAnnualBalanceAmount();
            changes = (annualBalanceAmount != null) ? requestedAmountTotal.subtract(annualBalanceAmount) : requestedAmountTotal;
        }

        // salarySettingExpansion.setAccountLineAnnualBalanceAmount(requestedAmountTotal);
        // businessObjectService.save(salarySettingExpansion);

        // now create a pseudo funding line if the BCAF list is empty so we can pass it to create 2PLG below
        Boolean wasSalarySettingExpansionBCAFEmpty = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding().isEmpty();
        if (wasSalarySettingExpansionBCAFEmpty) {
            appointmentFundings.add(this.createPseudoAppointmentFundingLine(salarySettingExpansion));
        }

        // For detail salary setting, we need to update existing or create new PBGL row for the BCAF set
        // We can't save salarySettingExpansion here since it will also save the associated BCAF rows
        // which in this case would be rows we might not have worked on.
        // Also, don't save PBGL if it is not in DB already and the BCAF set was empty (no doo doo).
        // that is, save if PBGL exists in DB or BCAF was not empty
        if (salarySettingExpansion.getVersionNumber() != null || !wasSalarySettingExpansionBCAFEmpty) {

            budgetDocumentService.updatePendingBudgetGeneralLedger(appointmentFundings.get(0), changes);
        }

        // update or create plug line if the total amount has been changed
        if (changes.isNonZero()) {

            budgetDocumentService.updatePendingBudgetGeneralLedgerPlug(appointmentFundings.get(0), changes.negated());
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#saveSalarySetting(java.util.List)
     */
    public void saveSalarySetting(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, Boolean isSalarySettingByIncumbent) {

        // Do the save/delete of BCAF rows from the salary setting detail screen first
        this.saveAppointmentFundings(appointmentFundings);

        // From the DB get the current unique set of SalarySettingExpansions (PBGL)
        // associated with our Incumbent or Position BCAF rows.
        // Each PBGL row from the DB will, in turn, have all the BCAF rows associated, including the
        // ones we just stored as part of this save operation (see above)
        // No one else would be updating these since we have a transaction lock on the account
        Set<SalarySettingExpansion> salarySettingExpansionSet = new HashSet<SalarySettingExpansion>();

        // these keep track of purged/unpurged used to unlock funding
        // when the last line for that account is purged
        Set<SalarySettingExpansion> purgedSseSet = new HashSet<SalarySettingExpansion>();
        Set<SalarySettingExpansion> unpurgedSseSet = new HashSet<SalarySettingExpansion>();

        // these keep track of purged/unpurged used to unlock positions
        // when the last line for that position is purged
        Set<BudgetConstructionPosition> purgedBPOSNSet = new HashSet<BudgetConstructionPosition>();
        Set<BudgetConstructionPosition> unpurgedBPOSNSet = new HashSet<BudgetConstructionPosition>();

        for (PendingBudgetConstructionAppointmentFunding fundingLine : appointmentFundings) {
            SalarySettingExpansion salarySettingExpansion = this.retriveSalarySalarySettingExpansion(fundingLine);

            if (salarySettingExpansion != null) {
                salarySettingExpansionSet.add(salarySettingExpansion);
            }
            else {
                // No PBGL row yet, create one to work with in memory only for now.
                // Don't set versionNumber, this will indicate this is in memory only,
                // so we can check for the case where there are no BCAF rows in the DB
                // and no PBGL row either. We don't want to create a new zero request PBGL row in this case.
                salarySettingExpansion = new SalarySettingExpansion();
                salarySettingExpansion.setUniversityFiscalYear(fundingLine.getUniversityFiscalYear());
                salarySettingExpansion.setChartOfAccountsCode(fundingLine.getChartOfAccountsCode());
                salarySettingExpansion.setAccountNumber(fundingLine.getAccountNumber());
                salarySettingExpansion.setSubAccountNumber(fundingLine.getSubAccountNumber());
                salarySettingExpansion.setFinancialObjectCode(fundingLine.getFinancialObjectCode());
                salarySettingExpansion.setFinancialSubObjectCode(fundingLine.getFinancialSubObjectCode());
                salarySettingExpansion.setFinancialBalanceTypeCode(optionsService.getOptions(fundingLine.getUniversityFiscalYear()).getBaseBudgetFinancialBalanceTypeCd());
                salarySettingExpansion.setFinancialObjectTypeCode(optionsService.getOptions(fundingLine.getUniversityFiscalYear()).getFinObjTypeExpenditureexpCd());
                salarySettingExpansion.setAccountLineAnnualBalanceAmount(KualiInteger.ZERO);
                salarySettingExpansion.setFinancialBeginningBalanceLineAmount(KualiInteger.ZERO);

                // If this has been created in memory already, the list should already be attached
                // and be in the current salarySettingExpansionSet.
                // This handles the case where at least 2 new BCAF rows for the same non-existent PBGL row
                // were saved earlier as part of this save operation.
                if (!salarySettingExpansionSet.contains(salarySettingExpansion)) {

                    // Get the BCAF rows from the DB that are associated with the
                    // newly created salarySettingExpansion and attach so the
                    // method savePBGLSalarySetting() called below can get the total.
                    List<PendingBudgetConstructionAppointmentFunding> bcafRows = this.retrievePendingBudgetConstructionAppointmentFundings(salarySettingExpansion);
                    salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding().addAll(bcafRows);
                    salarySettingExpansionSet.add(salarySettingExpansion);
                }
            }

            // collect the set of purge/notpurged SalarySettingExpansions here
            if (fundingLine.isPurged()) {
                purgedSseSet.add(salarySettingExpansion);
            }
            else {
                unpurgedSseSet.add(salarySettingExpansion);
            }

            // if SS by incumbent collect the set of purged/notpurged BudgetConstructionPositions here
            if (isSalarySettingByIncumbent) {
                BudgetConstructionPosition budgetConstructionPosition = fundingLine.getBudgetConstructionPosition();
                if (fundingLine.isPurged()) {
                    purgedBPOSNSet.add(budgetConstructionPosition);
                }
                else {
                    unpurgedBPOSNSet.add(budgetConstructionPosition);
                }
            }
        }

        // remove from set of purged SSEs the set of notpurged SSEs
        // leftover are those SSEs to release funding locks for after successful save
        purgedSseSet.removeAll(unpurgedSseSet);

        // if SS by incumbent, remove from set of purged BPOSNs the set of nonpurged BPOSNs
        if (isSalarySettingByIncumbent) {
            purgedBPOSNSet.removeAll(unpurgedBPOSNSet);
        }

        // Use the salarySettingExpansionSet to drive the update of PBGL rows (including any 2PLGs)
        for (SalarySettingExpansion salarySettingExpansion : salarySettingExpansionSet) {

            this.savePBGLSalarySetting(salarySettingExpansion);
        }

        // iterate leftover purged SSEs and release funding lock for each
        for (SalarySettingExpansion salarySettingExpansion : purgedSseSet) {
            String chartOfAccountsCode = salarySettingExpansion.getChartOfAccountsCode();
            String accountNumber = salarySettingExpansion.getAccountNumber();
            String subAccountNumber = salarySettingExpansion.getSubAccountNumber();
            Integer fiscalYear = salarySettingExpansion.getUniversityFiscalYear();
            String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();

            // release the associated funding lock
            lockService.unlockFunding(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, principalId);

        }

        // if SS by incumbent iterate leftover purged BPOSNs and release position lock for each
        for (BudgetConstructionPosition budgetConstructionPosition : purgedBPOSNSet) {
            Person person = GlobalVariables.getUserSession().getPerson();
            lockService.unlockPostion(budgetConstructionPosition, person);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#saveAppointmentFundings(java.util.List)
     */
    public void saveAppointmentFundings(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings) {
        LOG.debug("saveAppointmentFundings() start");

        // remove the appointment funding lines being purged
        List<PendingBudgetConstructionAppointmentFunding> purgedAppointmentFundings = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            if (appointmentFunding.isPurged()) {
                purgedAppointmentFundings.add(appointmentFunding);
            }
        }
        this.purgeAppointmentFundings(purgedAppointmentFundings);

        // save the appointment funding lines that have been updated or newly created
        List<PendingBudgetConstructionAppointmentFunding> savableAppointmentFundings = new ArrayList<PendingBudgetConstructionAppointmentFunding>(appointmentFundings);
        savableAppointmentFundings.removeAll(purgedAppointmentFundings);

        // gwp - added this as part of double save optimistic exception fix
        // since savePBGLSalarySetting does not call this like saveSalarySetting does
        this.resetDeletedFundingLines(appointmentFundings);

        this.updateAppointmentFundingsBeforeSaving(savableAppointmentFundings);

        // save each line so deletion aware reasons get removed when needed
        for (PendingBudgetConstructionAppointmentFunding savableAppointmentFunding : savableAppointmentFundings){
            businessObjectService.save(savableAppointmentFunding);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#retriveSalarySalarySettingExpansion(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public SalarySettingExpansion retriveSalarySalarySettingExpansion(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        BudgetConstructionHeader budgetDocument = budgetDocumentService.getBudgetConstructionHeader(appointmentFunding);

        Map<String, Object> fieldValues = ObjectUtil.buildPropertyMap(appointmentFunding, SalarySettingExpansion.getPrimaryKeyFields());
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, budgetDocument.getDocumentNumber());

        return businessObjectService.findByPrimaryKey(SalarySettingExpansion.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#retrievePendingBudgetConstructionAppointmentFundings(org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion)
     */
    public List<PendingBudgetConstructionAppointmentFunding> retrievePendingBudgetConstructionAppointmentFundings(SalarySettingExpansion salarySettingExpansion) {

        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, salarySettingExpansion.getUniversityFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, salarySettingExpansion.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, salarySettingExpansion.getAccountNumber());
        fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, salarySettingExpansion.getSubAccountNumber());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, salarySettingExpansion.getFinancialObjectCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, salarySettingExpansion.getFinancialSubObjectCode());

        return (List<PendingBudgetConstructionAppointmentFunding>) businessObjectService.findMatching(PendingBudgetConstructionAppointmentFunding.class, fieldValues);
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
     *      org.kuali.kfs.module.bc.util.SalarySettingFieldsHolder, boolean, java.util.Map, org.kuali.rice.kim.api.identity.Person)
     */
    public boolean updateAccessOfAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding, SalarySettingFieldsHolder salarySettingFieldsHolder, boolean budgetByObjectMode, boolean hasDocumentEditAccess, Person person) {
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
            // use the edit permission already calculated for the home account during document open
            appointmentFunding.setDisplayOnlyMode(!hasDocumentEditAccess);

            return true;
        }

        boolean isUpdatedByUserLevel = this.updateAccessOfAppointmentFundingByUserLevel(appointmentFunding, person);
        if (isUpdatedByUserLevel) {
            return true;
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#updateAccessOfAppointmentFundingByUserLevel(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    public boolean updateAccessOfAppointmentFundingByUserLevel(PendingBudgetConstructionAppointmentFunding appointmentFunding, Person user) {
        BudgetConstructionHeader budgetConstructionHeader = budgetDocumentService.getBudgetConstructionHeader(appointmentFunding);
        if (budgetConstructionHeader == null) {
            return false;
        }

        BudgetConstructionDocument document;
        try {
            document = (BudgetConstructionDocument) documentService.getByDocumentHeaderId(budgetConstructionHeader.getDocumentNumber());
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Fail to retrieve budget document for doc id " + budgetConstructionHeader.getDocumentNumber());
        }

        TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) getDocumentHelperService().getDocumentAuthorizer(document);

        boolean hasEditAccess = documentAuthorizer.isAuthorized(document, BCConstants.BUDGET_CONSTRUCTION_NAMESPACE, BCConstants.KimApiConstants.EDIT_BCAF_PERMISSION_NAME, user.getPrincipalId());
        appointmentFunding.setDisplayOnlyMode(!hasEditAccess);

        boolean hasViewAmountsAccess = documentAuthorizer.isAuthorized(document, BCConstants.BUDGET_CONSTRUCTION_NAMESPACE, BCConstants.KimApiConstants.VIEW_BCAF_AMOUNTS_PERMISSION_NAME, user.getPrincipalId());
        appointmentFunding.setExcludedFromTotal(!hasViewAmountsAccess);

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#updateDerivedInformationForAppointmentFundings(java.util.List)
     */
    public void updateAppointmentFundingsBeforeSaving(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings) {
        LOG.debug("updateDerivedInformationForAppointmentFundings() start");

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            this.recalculateDerivedInformation(appointmentFunding);

            appointmentFunding.setNewLineIndicator(false);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#updateDerivedInformationForAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void recalculateDerivedInformation(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        this.preprocessFundingReason(appointmentFunding);
        this.preprocessLeaveRequest(appointmentFunding);

        boolean isHourlyPaid = this.isHourlyPaid(appointmentFunding);
        appointmentFunding.setHourlyPaid(isHourlyPaid);

        if (appointmentFunding.isHourlyPaid()) {
            this.normalizePayRateAndAmount(appointmentFunding);
        }
        else {
            appointmentFunding.setAppointmentRequestedPayRate(BigDecimal.ZERO);
        }

        BigDecimal requestedFteQuantity = this.calculateFteQuantityFromAppointmentFunding(appointmentFunding);
        appointmentFunding.setAppointmentRequestedFteQuantity(requestedFteQuantity);

        if (!appointmentFunding.getAppointmentFundingDurationCode().equals(NONE.durationCode)) {
            BigDecimal requestedCSFFteQuantity = this.calculateCSFFteQuantityFromAppointmentFunding(appointmentFunding);
            appointmentFunding.setAppointmentRequestedCsfFteQuantity(requestedCSFFteQuantity);
        }
    }

    /**
     * reset the amount values of each line in the given appointment fundings as zeros and remove the reason annotations if the line
     * is marked as deleted
     *
     * @param pendingBudgetConstructionAppointmentFunding the given appointment fundings
     */
    protected void resetDeletedFundingLines(List<PendingBudgetConstructionAppointmentFunding> pendingBudgetConstructionAppointmentFunding) {
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
    protected KualiInteger getCsfAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
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
    protected boolean hasBeenVacated(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
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
    protected PendingBudgetConstructionAppointmentFunding createVacantAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding = new PendingBudgetConstructionAppointmentFunding();

        ObjectUtil.buildObjectWithoutReferenceFields(vacantAppointmentFunding, appointmentFunding);
        vacantAppointmentFunding.setEmplid(BCConstants.VACANT_EMPLID);
        vacantAppointmentFunding.setAppointmentFundingDeleteIndicator(false);
        vacantAppointmentFunding.setPersistedDeleteIndicator(false);
        vacantAppointmentFunding.setVersionNumber(null);
        vacantAppointmentFunding.setNewLineIndicator(true);

        return vacantAppointmentFunding;
    }

    /**
     * create a pseudo appointment funding for the salary setting expansion this is used when there are no funding lines for the
     * salary setting expansion to get a funding line to be used to pass primary key info
     *
     * @param salarySettingExpansion
     * @return a pseudo appointment funding
     */
    protected PendingBudgetConstructionAppointmentFunding createPseudoAppointmentFundingLine(SalarySettingExpansion salarySettingExpansion) {
        PendingBudgetConstructionAppointmentFunding pseudoAppointmentFunding = new PendingBudgetConstructionAppointmentFunding();

        pseudoAppointmentFunding.setUniversityFiscalYear(salarySettingExpansion.getUniversityFiscalYear());
        pseudoAppointmentFunding.setChartOfAccountsCode(salarySettingExpansion.getChartOfAccountsCode());
        pseudoAppointmentFunding.setAccountNumber(salarySettingExpansion.getAccountNumber());
        pseudoAppointmentFunding.setSubAccountNumber(salarySettingExpansion.getSubAccountNumber());
        pseudoAppointmentFunding.setFinancialObjectCode(salarySettingExpansion.getFinancialObjectCode());
        pseudoAppointmentFunding.setFinancialSubObjectCode(salarySettingExpansion.getFinancialSubObjectCode());
        pseudoAppointmentFunding.setAppointmentFundingDeleteIndicator(false);
        pseudoAppointmentFunding.setAppointmentRequestedAmount(KualiInteger.ZERO);
        pseudoAppointmentFunding.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);

        return pseudoAppointmentFunding;
    }

    /**
     * preprocess the funding reason of the given appointment funding before the funding is saved
     *
     * @param appointmentFunding the given appointment funding
     */
    public void preprocessFundingReason(PendingBudgetConstructionAppointmentFunding appointmentFunding) {

        List<BudgetConstructionAppointmentFundingReason> fundingReasons = appointmentFunding.getBudgetConstructionAppointmentFundingReason();

        // do special removal of any reason rows where the reason code is blank
        if (!fundingReasons.isEmpty() && StringUtils.isBlank(fundingReasons.get(0).getAppointmentFundingReasonCode())) {
            fundingReasons.clear();
        }
    }

    /**
     * preprocess the leave request of the given appointment funding before the funding is saved
     *
     * @param appointmentFunding the given appointment funding
     */
    public void preprocessLeaveRequest(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        String durationCode = appointmentFunding.getAppointmentFundingDurationCode();

        if (StringUtils.isEmpty(durationCode) || StringUtils.equals(durationCode, BCConstants.AppointmentFundingDurationCodes.NONE.durationCode)) {
            appointmentFunding.setAppointmentRequestedCsfAmount(KualiInteger.ZERO);
            appointmentFunding.setAppointmentRequestedCsfFteQuantity(BigDecimal.ZERO);
            appointmentFunding.setAppointmentRequestedCsfTimePercent(BigDecimal.ZERO);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#hasExistingFundingReason(org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReasonCode)
     */
    public boolean hasExistingFundingReason(BudgetConstructionAppointmentFundingReasonCode budgetConstructionAppointmentFundingReasonCode) {

        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("appointmentFundingReasonCode", budgetConstructionAppointmentFundingReasonCode.getAppointmentFundingReasonCode());

        return (businessObjectService.countMatching(BudgetConstructionAppointmentFundingReason.class, queryMap) > 0);
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     *
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
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
     * Sets the optionsService attribute value.
     *
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * Sets the lockService attribute value.
     *
     * @param lockService The lockService to set.
     */
    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }

    /**
     * Gets the documentHelperService attribute.
     *
     * @return Returns the documentHelperService.
     */
    public DocumentHelperService getDocumentHelperService() {
        if (documentHelperService == null) {
            documentHelperService = SpringContext.getBean(DocumentHelperService.class);
        }
        return documentHelperService;
    }

    /**
     * Sets the documentHelperService attribute value.
     *
     * @param documentHelperService The documentHelperService to set.
     */
    public void setDocumentHelperService(DocumentHelperService documentHelperService) {
        this.documentHelperService = documentHelperService;
    }

    /**
     * Sets the documentService attribute value.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the budgetConstructionProcessorService attribute value.
     *
     * @param budgetConstructionProcessorService The budgetConstructionProcessorService to set.
     */
    public void setBudgetConstructionProcessorService(BudgetConstructionProcessorService budgetConstructionProcessorService) {
        this.budgetConstructionProcessorService = budgetConstructionProcessorService;
    }
}
