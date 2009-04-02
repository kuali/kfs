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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAdministrativePost;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReason;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReasonCode;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgReasonSummaryReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgReasonSummaryReportTotal;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalaryFunding;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalarySocialSecurityNumber;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionSalarySummaryReportDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReasonSummaryReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionReasonSummaryReportService.
 */
@Transactional
public class BudgetConstructionReasonSummaryReportServiceImpl implements BudgetConstructionReasonSummaryReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionReasonSummaryReportServiceImpl.class);

    BudgetConstructionSalarySummaryReportDao budgetConstructionSalarySummaryReportDao;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    private BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    KualiConfigurationService kualiConfigurationService;
    BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReasonSummaryReportService#updateReasonSummaryReport(java.lang.String,
     *      java.lang.Integer, org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings)
     */
    public void updateReasonSummaryReport(String principalId, Integer universityFiscalYear, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        boolean selectOnlyGreaterThanOrEqualToThreshold = budgetConstructionReportThresholdSettings.isUseGreaterThanOperator();
        KualiDecimal thresholdPercent = budgetConstructionReportThresholdSettings.getThresholdPercent();

        boolean applyAThreshold = budgetConstructionReportThresholdSettings.isUseThreshold();
        if (applyAThreshold) {
            budgetConstructionSalarySummaryReportDao.updateSalaryAndReasonSummaryReportsWithThreshold(principalId, universityFiscalYear - 1, selectOnlyGreaterThanOrEqualToThreshold, thresholdPercent);
        }
        else {
            budgetConstructionSalarySummaryReportDao.updateSalaryAndReasonSummaryReportsWithoutThreshold(principalId, true);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReasonSummaryReportService#buildReports(java.lang.Integer,
     *      java.lang.String, org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings)
     */
    public Collection<BudgetConstructionOrgReasonSummaryReport> buildReports(Integer universityFiscalYear, String principalId, BudgetConstructionReportThresholdSettings reportThresholdSettings) {
        Collection<BudgetConstructionOrgReasonSummaryReport> reportSet = new ArrayList<BudgetConstructionOrgReasonSummaryReport>();

        Map<String, Object> salaryFundingSearchCriteria = new HashMap<String, Object>();
        salaryFundingSearchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, principalId);

        List<String> orderList = this.buildOrderByList();
        List<BudgetConstructionSalaryFunding> reasonSummaryList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionSalaryFunding.class, salaryFundingSearchCriteria, orderList);

        // get BudgetConstructionSalarySocialSecurityNumber and put into map
        Map<BudgetConstructionSalaryFunding, BudgetConstructionSalarySocialSecurityNumber> salarySsnMap = new HashMap<BudgetConstructionSalaryFunding, BudgetConstructionSalarySocialSecurityNumber>();
        for (BudgetConstructionSalaryFunding salaryFunding : reasonSummaryList) {
            BudgetConstructionSalarySocialSecurityNumber salarySocialSecurityNumber = budgetConstructionReportsServiceHelper.getBudgetConstructionSalarySocialSecurityNumber(principalId, salaryFunding);
            salarySsnMap.put(salaryFunding, salarySocialSecurityNumber);
        }

        List<BudgetConstructionSalaryFunding> listForCalculateTotalPerson = this.retainUniqeSalaryFunding(reasonSummaryList, salarySsnMap, 1);
        List<BudgetConstructionSalaryFunding> listForCalculateTotalOrg = this.retainUniqeSalaryFunding(reasonSummaryList, salarySsnMap, 2);

        // Calculate Total Section
        Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalPerson = calculatePersonTotal(universityFiscalYear, reasonSummaryList, listForCalculateTotalPerson, salarySsnMap);
        Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalOrg = calculateOrgTotal(reasonSummaryTotalPerson, listForCalculateTotalOrg, salarySsnMap);

        // get object codes
        String objectCodes = budgetConstructionReportsServiceHelper.getSelectedObjectCodes(principalId);

        // get reason codes
        String reasonCodes = budgetConstructionReportsServiceHelper.getSelectedReasonCodes(principalId);

        for (BudgetConstructionSalaryFunding salaryFundingEntry : reasonSummaryList) {
            BudgetConstructionSalarySocialSecurityNumber budgetSsnEntry = salarySsnMap.get(salaryFundingEntry);
            BudgetConstructionOrgReasonSummaryReport reasonSummaryReport = new BudgetConstructionOrgReasonSummaryReport();

            buildReportsHeader(universityFiscalYear, objectCodes, reasonCodes, reasonSummaryReport, salaryFundingEntry, budgetSsnEntry, reportThresholdSettings);
            buildReportsBody(universityFiscalYear, reasonSummaryReport, salaryFundingEntry, budgetSsnEntry);
            buildReportsTotal(reasonSummaryReport, salaryFundingEntry, reasonSummaryTotalPerson, reasonSummaryTotalOrg, salarySsnMap);

            reportSet.add(reasonSummaryReport);
        }

        return reportSet;
    }

    /**
     * builds report Header
     */
    public void buildReportsHeader(Integer universityFiscalYear, String objectCodes, String reasonCodes, BudgetConstructionOrgReasonSummaryReport reasonSummaryReport, BudgetConstructionSalaryFunding salaryFundingEntry, BudgetConstructionSalarySocialSecurityNumber bcSSN, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        Integer prevFiscalyear = universityFiscalYear - 1;
        reasonSummaryReport.setFiscalYear(prevFiscalyear + "-" + universityFiscalYear.toString().substring(2, 4));

        reasonSummaryReport.setOrganizationCode(bcSSN.getOrganizationCode());
        String organizationName = bcSSN.getOrganization().getOrganizationName();
        if (organizationName == null) {
            String wrongOrganizationName = kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME);
            reasonSummaryReport.setOrganizationName(wrongOrganizationName);
        }
        else {
            reasonSummaryReport.setOrganizationName(organizationName);
        }

        reasonSummaryReport.setChartOfAccountsCode(salaryFundingEntry.getChartOfAccountsCode());
        String chartDescription = salaryFundingEntry.getChartOfAccounts().getFinChartOfAccountDescription();
        if (chartDescription == null) {
            String wrongChartDescription = kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION);
            reasonSummaryReport.setChartOfAccountDescription(wrongChartDescription);
        }
        else {
            reasonSummaryReport.setChartOfAccountDescription(chartDescription);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        reasonSummaryReport.setReqFy(prevFiscalyear + "-" + universityFiscalYear.toString().substring(2, 4));
        reasonSummaryReport.setFinancialObjectCode(salaryFundingEntry.getFinancialObjectCode());

        reasonSummaryReport.setObjectCodes(objectCodes);

        if (budgetConstructionReportThresholdSettings.isUseThreshold()) {
            if (budgetConstructionReportThresholdSettings.isUseGreaterThanOperator()) {
                reasonSummaryReport.setThresholdOrReason(BCConstants.Report.THRESHOLD + BCConstants.Report.THRESHOLD_GREATER + budgetConstructionReportThresholdSettings.getThresholdPercent().toString() + BCConstants.Report.PERCENT);
            }
            else {
                reasonSummaryReport.setThresholdOrReason(BCConstants.Report.THRESHOLD + BCConstants.Report.THRESHOLD_LESS + budgetConstructionReportThresholdSettings.getThresholdPercent().toString() + BCConstants.Report.PERCENT);
            }
        }
        else {
            reasonSummaryReport.setThresholdOrReason(BCConstants.Report.SELECTED_REASONS + reasonCodes);
        }

        // reason, amt, desc
        List<BudgetConstructionAppointmentFundingReason> appointmentFundingReasonList = salaryFundingEntry.getPendingAppointmentFunding().getBudgetConstructionAppointmentFundingReason();
        if (appointmentFundingReasonList != null || !appointmentFundingReasonList.isEmpty()) {
            BudgetConstructionAppointmentFundingReason appointmentFundingReason = appointmentFundingReasonList.get(0);

            Integer reasonAmount = BudgetConstructionReportHelper.convertKualiInteger(appointmentFundingReason.getAppointmentFundingReasonAmount());
            reasonSummaryReport.setAppointmentFundingReasonAmount(reasonAmount);

            BudgetConstructionAppointmentFundingReasonCode reasonCode = appointmentFundingReason.getAppointmentFundingReason();
            if (ObjectUtils.isNotNull(reasonCode)) {
                reasonSummaryReport.setAppointmentFundingReasonDescription(reasonCode.getAppointmentFundingReasonDescription());
            }
        }
    }

    /**
     * builds report body
     */
    public void buildReportsBody(Integer fiscalYear, BudgetConstructionOrgReasonSummaryReport reasonSummaryReport, BudgetConstructionSalaryFunding salaryFundingEntry, BudgetConstructionSalarySocialSecurityNumber salarySSN) {
        int curToInt = -1;
        double curFteInt = -1.00;

        PendingBudgetConstructionAppointmentFunding appointmentFunding = salaryFundingEntry.getPendingAppointmentFunding();
        BudgetConstructionIntendedIncumbent intendedIncumbent = budgetConstructionReportsServiceHelper.getBudgetConstructionIntendedIncumbent(appointmentFunding);
        if (intendedIncumbent != null) {
            reasonSummaryReport.setIuClassificationLevel(intendedIncumbent.getIuClassificationLevel());
        }

        reasonSummaryReport.setName(salarySSN.getName());

        BudgetConstructionAdministrativePost administrativePost = budgetConstructionReportsServiceHelper.getBudgetConstructionAdministrativePost(appointmentFunding);
        BudgetConstructionPosition budgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(fiscalYear, appointmentFunding);

        // set report body
        reasonSummaryReport.setAccountNumber(salaryFundingEntry.getAccountNumber());
        reasonSummaryReport.setSubAccountNumber(salaryFundingEntry.getSubAccountNumber());
        reasonSummaryReport.setFinancialSubObjectCode(salaryFundingEntry.getFinancialSubObjectCode());

        if (administrativePost != null) {
            reasonSummaryReport.setAdministrativePost(administrativePost.getAdministrativePost());
        }

        if (budgetConstructionPosition != null) {
            reasonSummaryReport.setPositionNumber(budgetConstructionPosition.getPositionNumber());
            reasonSummaryReport.setNormalWorkMonthsAndiuPayMonths(budgetConstructionPosition.getIuNormalWorkMonths() + "/" + budgetConstructionPosition.getIuPayMonths());
            reasonSummaryReport.setPositionFte(BudgetConstructionReportHelper.setDecimalDigit(budgetConstructionPosition.getPositionFullTimeEquivalency(), 5, false));
            reasonSummaryReport.setPositionSalaryPlanDefault(budgetConstructionPosition.getPositionSalaryPlanDefault());
            reasonSummaryReport.setPositionGradeDefault(budgetConstructionPosition.getPositionGradeDefault());
        }

        BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = appointmentFunding.getEffectiveCSFTracker();
        if (csfTracker != null) {
            reasonSummaryReport.setCsfTimePercent(BudgetConstructionReportHelper.setDecimalDigit(csfTracker.getCsfTimePercent(), 2, false));
            reasonSummaryReport.setCsfAmount(csfTracker.getCsfAmount().intValue());

            // calculate amountChange and percentChange
            if (appointmentFunding.getAppointmentRequestedFteQuantity().equals(csfTracker.getCsfFullTimeEmploymentQuantity())) {
                Integer amountChange = appointmentFunding.getAppointmentRequestedAmount().subtract(csfTracker.getCsfAmount()).intValue();
                reasonSummaryReport.setAmountChange(amountChange);

                BigDecimal percentChange = BudgetConstructionReportHelper.calculatePercent(new BigDecimal(amountChange), csfTracker.getCsfAmount().bigDecimalValue());
                reasonSummaryReport.setPercentChange(percentChange);
            }
        }

        if (StringUtils.equals(appointmentFunding.getFinancialSubObjectCode(), BCConstants.Report.BLANK_SUB_OBJECT_CODE)) {
            reasonSummaryReport.setFinancialSubObjectCode(BCConstants.Report.BLANK);
        }
        else {
            reasonSummaryReport.setFinancialSubObjectCode(appointmentFunding.getFinancialSubObjectCode());
        }

        reasonSummaryReport.setEmplid(appointmentFunding.getEmplid());
        reasonSummaryReport.setAppointmentFundingDurationCode(appointmentFunding.getAppointmentFundingDurationCode());
        reasonSummaryReport.setAppointmentTotalIntendedAmount(appointmentFunding.getAppointmentTotalIntendedAmount().intValue());

        BigDecimal totalIntendedFteQuantity = BudgetConstructionReportHelper.setDecimalDigit(appointmentFunding.getAppointmentTotalIntendedFteQuantity(), 5, false);
        reasonSummaryReport.setAppointmentTotalIntendedFteQuantity(totalIntendedFteQuantity);

        if (StringUtils.equals(appointmentFunding.getAppointmentFundingDurationCode(), BCConstants.Report.NONE)) {
            reasonSummaryReport.setSalaryAmount(appointmentFunding.getAppointmentRequestedAmount().intValue());
            reasonSummaryReport.setPercentAmount(appointmentFunding.getAppointmentRequestedTimePercent());
            reasonSummaryReport.setSalaryMonths(appointmentFunding.getAppointmentFundingMonth());
        }
        else {
            reasonSummaryReport.setSalaryAmount(appointmentFunding.getAppointmentRequestedCsfAmount().intValue());
            reasonSummaryReport.setPercentAmount(appointmentFunding.getAppointmentRequestedCsfTimePercent());

            if (budgetConstructionPosition != null) {
                reasonSummaryReport.setSalaryMonths(budgetConstructionPosition.getIuNormalWorkMonths());
            }
        }

        if (appointmentFunding.isAppointmentFundingDeleteIndicator()) {
            reasonSummaryReport.setDeleteBox(BCConstants.Report.DELETE_MARK);
        }
        else {
            reasonSummaryReport.setDeleteBox(BCConstants.Report.BLANK);
        }

        // set tiFlag
        if (appointmentFunding.isAppointmentFundingDeleteIndicator()) {
            if (curToInt == -1) {
                curToInt = appointmentFunding.getAppointmentTotalIntendedAmount().intValue();
            }
            else if (curToInt != appointmentFunding.getAppointmentTotalIntendedAmount().intValue()) {
                reasonSummaryReport.setTiFlag(BCConstants.Report.PLUS);
            }

            if (curFteInt == -1.00) {
                curFteInt = appointmentFunding.getAppointmentTotalIntendedFteQuantity().doubleValue();
            }
            else if (curFteInt != appointmentFunding.getAppointmentTotalIntendedFteQuantity().doubleValue()) {
                reasonSummaryReport.setTiFlag(BCConstants.Report.PLUS);
            }
        }
    }


    public void buildReportsTotal(BudgetConstructionOrgReasonSummaryReport reasonSummaryReportEntry, BudgetConstructionSalaryFunding salaryFundingEntry, Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalPerson, Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalOrg, Map budgetSsnMap) {

        for (BudgetConstructionOrgReasonSummaryReportTotal totalPersonEntry : reasonSummaryTotalPerson) {
            if (isSameSalaryFundingEntryForTotalPerson(totalPersonEntry.getBudgetConstructionSalaryFunding(), salaryFundingEntry, budgetSsnMap)) {
                reasonSummaryReportEntry.setPersonPositionNumber(totalPersonEntry.getPersonPositionNumber());
                reasonSummaryReportEntry.setPersonFiscalYearTag(totalPersonEntry.getPersonFiscalYearTag());
                reasonSummaryReportEntry.setPersonNormalMonthsAndPayMonths(totalPersonEntry.getPersonCsfNormalMonths().toString() + "/" + totalPersonEntry.getPersonCsfPayMonths().toString());
                reasonSummaryReportEntry.setPersonCsfAmount(totalPersonEntry.getPersonCsfAmount());
                reasonSummaryReportEntry.setPersonCsfPercent(totalPersonEntry.getPersonCsfPercent());
                reasonSummaryReportEntry.setPersonSalaryNormalMonths(totalPersonEntry.getPersonSalaryNormalMonths());
                reasonSummaryReportEntry.setPersonSalaryAmount(totalPersonEntry.getPersonSalaryAmount());
                reasonSummaryReportEntry.setPersonSalaryPercent(totalPersonEntry.getPersonSalaryPercent());
                reasonSummaryReportEntry.setPersonSalaryFte(totalPersonEntry.getPersonSalaryFte());
                reasonSummaryReportEntry.setPersonTiFlag(totalPersonEntry.getPersonTiFlag());
                reasonSummaryReportEntry.setPersonAmountChange(totalPersonEntry.getPersonAmountChange());
                reasonSummaryReportEntry.setPersonPercentChange(totalPersonEntry.getPersonPercentChange());
            }
            for (BudgetConstructionOrgReasonSummaryReportTotal totalOrgEntry : reasonSummaryTotalOrg) {
                if (isSameSalaryFundingEntryForTotalOrg(totalPersonEntry.getBudgetConstructionSalaryFunding(), salaryFundingEntry, budgetSsnMap)) {
                    reasonSummaryReportEntry.setNewFte(totalOrgEntry.getNewFte());
                    reasonSummaryReportEntry.setNewTotalAmount(totalOrgEntry.getNewTotalAmount());
                    reasonSummaryReportEntry.setConTotalBaseAmount(totalOrgEntry.getConTotalBaseAmount());
                    reasonSummaryReportEntry.setConFte(totalOrgEntry.getConFte());
                    reasonSummaryReportEntry.setConTotalRequestAmount(totalOrgEntry.getConTotalRequestAmount());
                    reasonSummaryReportEntry.setNewAverageAmount(totalOrgEntry.getNewAverageAmount());
                    reasonSummaryReportEntry.setConAverageBaseAmount(totalOrgEntry.getConAverageBaseAmount());
                    reasonSummaryReportEntry.setConAverageRequestAmount(totalOrgEntry.getConAverageRequestAmount());
                    reasonSummaryReportEntry.setConAveragechange(totalOrgEntry.getConAveragechange());
                    reasonSummaryReportEntry.setConPercentChange(totalOrgEntry.getConPercentChange());
                }
            }
        }
    }

    private Collection<BudgetConstructionOrgReasonSummaryReportTotal> calculatePersonTotal(Integer universityFiscalYear, Collection<BudgetConstructionSalaryFunding> reasonSummaryList, List<BudgetConstructionSalaryFunding> listForCalculateTotalPerson, Map budgetSsnMap) {
        Collection<BudgetConstructionOrgReasonSummaryReportTotal> returnCollection = new ArrayList<BudgetConstructionOrgReasonSummaryReportTotal>();

        for (BudgetConstructionSalaryFunding totalPersonEntry : listForCalculateTotalPerson) {
            PersonTotalHolder totalsHolder = this.calculateTotalForPerson(totalPersonEntry, reasonSummaryList, budgetSsnMap);
            this.adjustPersonTotal(totalsHolder);

            returnCollection.add(this.createReportTotal(totalPersonEntry, totalsHolder));
        }

        return returnCollection;
    }

    private PersonTotalHolder calculateTotalForPerson(BudgetConstructionSalaryFunding totalPersonEntry, Collection<BudgetConstructionSalaryFunding> reasonSummaryList, Map budgetSsnMap) {
        PersonTotalHolder totalsHolder = new PersonTotalHolder();

        int maxSalaryAmount = 0;
        int maxCsfAmount = 0;

        for (BudgetConstructionSalaryFunding salaryfundingEntry : reasonSummaryList) {
            PendingBudgetConstructionAppointmentFunding appointmentFunding = salaryfundingEntry.getPendingAppointmentFunding();
            Integer universityFiscalYear = appointmentFunding.getUniversityFiscalYear();
            BudgetConstructionPosition budgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(universityFiscalYear, appointmentFunding);

            if (isSameSalaryFundingEntryForTotalPerson(totalPersonEntry, salaryfundingEntry, budgetSsnMap)) {
                int tempSalaryMonth = 0;
                String durationCode = appointmentFunding.getAppointmentFundingDurationCode();

                if (StringUtils.equals(durationCode, BCConstants.Report.NONE)) {
                    totalsHolder.salaryAmount += appointmentFunding.getAppointmentRequestedAmount().intValue();
                    totalsHolder.salaryPercent = totalsHolder.salaryPercent.add(appointmentFunding.getAppointmentRequestedTimePercent());
                    tempSalaryMonth = appointmentFunding.getAppointmentFundingMonth();
                }
                else {
                    totalsHolder.salaryAmount += appointmentFunding.getAppointmentRequestedCsfAmount().intValue();
                    totalsHolder.salaryPercent = totalsHolder.salaryPercent.add(appointmentFunding.getAppointmentRequestedCsfTimePercent());
                    tempSalaryMonth = budgetConstructionPosition.getIuNormalWorkMonths();
                }

                if (totalsHolder.salaryAmount > maxSalaryAmount) {
                    maxSalaryAmount = totalsHolder.salaryAmount;
                    totalsHolder.salaryPayMonth = budgetConstructionPosition.getIuPayMonths();
                    totalsHolder.salaryNormalMonths = tempSalaryMonth;
                }

                BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = appointmentFunding.getEffectiveCSFTracker();
                if (csfTracker != null && csfTracker.getCsfAmount() != null && csfTracker.getCsfAmount().intValue() != 0) {
                    int csfAmount = csfTracker.getCsfAmount().intValue();

                    totalsHolder.csfAmount += csfAmount;
                    totalsHolder.csfPercent = totalsHolder.csfPercent.add(csfTracker.getCsfTimePercent());

                    if (csfAmount > maxCsfAmount) {
                        maxCsfAmount = csfAmount;
                    }
                }

                // data for previous year, position table has two data, one is for current year and another is for previous year.
                Integer previousFiscalYear = universityFiscalYear - 1;
                BudgetConstructionPosition previousYearBudgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(previousFiscalYear, appointmentFunding);

                totalsHolder.csfPayMonths = previousYearBudgetConstructionPosition.getIuPayMonths();
                totalsHolder.csfNormalMonths = previousYearBudgetConstructionPosition.getIuNormalWorkMonths();
                totalsHolder.fiscalYearTag = previousFiscalYear.toString() + ":";

                totalsHolder.positionNumber = budgetConstructionPosition.getPositionNumber();

                if (!appointmentFunding.isAppointmentFundingDeleteIndicator()) {
                    if (totalsHolder.curToInt <= -1) {
                        totalsHolder.curToInt = appointmentFunding.getAppointmentTotalIntendedAmount().intValue();
                    }

                    if (totalsHolder.curFteInt <= -1.00) {
                        totalsHolder.curFteInt = appointmentFunding.getAppointmentTotalIntendedFteQuantity().doubleValue();
                    }
                }
            }
        }

        return totalsHolder;
    }

    // adjust the total amount that just is held by the given holder
    private void adjustPersonTotal(PersonTotalHolder totalsHolder) {
        Integer restatementCsfAmount = 0;

        if (totalsHolder.salaryPayMonth == 0 || totalsHolder.csfPayMonths == 0 || BigDecimal.ZERO.compareTo(totalsHolder.csfPercent) == 0 || totalsHolder.csfNormalMonths == 0) {
            restatementCsfAmount = 0;
        }
        else {
            BigDecimal salaryMonthPercent = new BigDecimal(totalsHolder.salaryNormalMonths * 1.0 / totalsHolder.salaryPayMonth);
            BigDecimal salaryFteQuantity = totalsHolder.salaryPercent.multiply(salaryMonthPercent);

            BigDecimal csfMonthpercent = new BigDecimal(totalsHolder.csfNormalMonths * 1.0 / totalsHolder.csfPayMonths);
            BigDecimal csfFteQuantity = totalsHolder.csfPercent.multiply(csfMonthpercent);

            BigDecimal restatementCsfPercent = salaryFteQuantity.divide(csfFteQuantity);
            BigDecimal csfAmount = new BigDecimal(totalsHolder.csfAmount);
            restatementCsfAmount = csfAmount.multiply(restatementCsfPercent).intValue();
        }

        if (totalsHolder.salaryPayMonth == 0) {
            totalsHolder.salaryFte = BigDecimal.ZERO;
        }
        else {
            BigDecimal salaryFte = totalsHolder.salaryPercent.multiply(new BigDecimal(totalsHolder.salaryNormalMonths * 1.0 / (totalsHolder.salaryPayMonth * 100.0)));
            totalsHolder.salaryFte = BudgetConstructionReportHelper.setDecimalDigit(salaryFte, 5, false);
        }

        if (totalsHolder.salaryPayMonth != totalsHolder.csfPayMonths) {
            if (totalsHolder.csfPayMonths == 0) {
                restatementCsfAmount = 0;
            }
            else {
                BigDecimal amount = new BigDecimal(restatementCsfAmount * totalsHolder.salaryPayMonth * 1.0 / totalsHolder.csfPayMonths);
                restatementCsfAmount = BudgetConstructionReportHelper.setDecimalDigit(amount, 0, false).intValue();
            }
        }

        totalsHolder.csfAmount = restatementCsfAmount;
        totalsHolder.amountChange = totalsHolder.salaryAmount - totalsHolder.csfAmount;

        if (totalsHolder.csfAmount != 0) {
            totalsHolder.percentChange = BudgetConstructionReportHelper.calculatePercent(totalsHolder.amountChange, totalsHolder.csfAmount);
        }
        else {
            totalsHolder.percentChange = BigDecimal.ZERO;
        }

        if (totalsHolder.curToInt != 0 && totalsHolder.curToInt != -1 && totalsHolder.curToInt != totalsHolder.salaryAmount.intValue() || totalsHolder.curFteInt != 0 && totalsHolder.curFteInt != -1.00 && totalsHolder.curFteInt != totalsHolder.salaryFte.doubleValue()) {
            totalsHolder.tiFlag = BCConstants.Report.PLUS;
        }
        else {
            totalsHolder.tiFlag = BCConstants.Report.BLANK;
        }
    }

    // create a report total for the given person with the values in the given total holder
    private BudgetConstructionOrgReasonSummaryReportTotal createReportTotal(BudgetConstructionSalaryFunding totalPersonEntry, PersonTotalHolder totalsHolder) {
        BudgetConstructionOrgReasonSummaryReportTotal reportTotal = new BudgetConstructionOrgReasonSummaryReportTotal();

        reportTotal.setBudgetConstructionSalaryFunding(totalPersonEntry);
        reportTotal.setPersonPositionNumber(totalsHolder.positionNumber);
        reportTotal.setPersonFiscalYearTag(totalsHolder.fiscalYearTag);
        reportTotal.setPersonCsfNormalMonths(totalsHolder.csfNormalMonths);
        reportTotal.setPersonCsfPayMonths(totalsHolder.csfPayMonths);
        reportTotal.setPersonCsfAmount(totalsHolder.csfAmount);
        reportTotal.setPersonCsfPercent(totalsHolder.csfPercent);
        reportTotal.setPersonSalaryNormalMonths(totalsHolder.salaryNormalMonths);
        reportTotal.setPersonSalaryAmount(totalsHolder.salaryAmount);
        reportTotal.setPersonSalaryPercent(totalsHolder.salaryPercent);
        reportTotal.setPersonSalaryFte(BudgetConstructionReportHelper.setDecimalDigit(totalsHolder.salaryFte, 5, false));
        reportTotal.setPersonTiFlag(totalsHolder.tiFlag);
        reportTotal.setPersonAmountChange(totalsHolder.amountChange);
        reportTotal.setPersonPercentChange(totalsHolder.percentChange);

        return reportTotal;
    }

    // a total holder that contains the totals for a single person
    private class PersonTotalHolder {
        String emplid = StringUtils.EMPTY;
        String positionNumber = StringUtils.EMPTY;
        String fiscalYearTag = StringUtils.EMPTY;
        String tiFlag = StringUtils.EMPTY;

        Integer csfNormalMonths = 0;
        Integer csfPayMonths = 0;
        Integer csfAmount = 0;
        BigDecimal csfPercent = BigDecimal.ZERO;

        Integer salaryNormalMonths = 0;
        Integer salaryPayMonth = 0;
        Integer salaryAmount = 0;
        BigDecimal salaryPercent = BigDecimal.ZERO;
        BigDecimal salaryFte = BigDecimal.ZERO;

        Integer amountChange = 0;
        BigDecimal percentChange = BigDecimal.ZERO;

        int curToInt = -1;
        double curFteInt = -1.00;
    }

    private Collection<BudgetConstructionOrgReasonSummaryReportTotal> calculateOrgTotal(Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalPerson, List<BudgetConstructionSalaryFunding> listForCalculateTotalOrg, Map budgetSsnMap) {
        Collection<BudgetConstructionOrgReasonSummaryReportTotal> returnCollection = new ArrayList();

        BigDecimal newFte = BigDecimal.ZERO;
        Integer newTotalAmount = 0;
        Integer newAverageAmount = 0;
        BigDecimal conFte = BigDecimal.ZERO;
        Integer conTotalBaseAmount = 0;
        Integer conTotalRequestAmount = 0;
        Integer conAverageBaseAmount = 0;
        Integer conAverageRequestAmount = 0;
        Integer conAveragechange = 0;
        BigDecimal conPercentChange = BigDecimal.ZERO;

        for (BudgetConstructionSalaryFunding totalOrgEntry : listForCalculateTotalOrg) {
            BudgetConstructionOrgReasonSummaryReportTotal budgetConstructionOrgReasonSummaryReportTotal = new BudgetConstructionOrgReasonSummaryReportTotal();
            for (BudgetConstructionOrgReasonSummaryReportTotal reportTotalPersonEntry : reasonSummaryTotalPerson) {
                if (isSameSalaryFundingEntryForTotalOrg(totalOrgEntry, reportTotalPersonEntry.getBudgetConstructionSalaryFunding(), budgetSsnMap)) {
                    if (reportTotalPersonEntry.getPersonCsfAmount() == 0) {
                        newFte = newFte.add(reportTotalPersonEntry.getPersonSalaryFte());
                        newTotalAmount += reportTotalPersonEntry.getPersonSalaryAmount();
                    }
                    else {
                        conTotalBaseAmount += reportTotalPersonEntry.getPersonCsfAmount();
                        conFte = conFte.add(reportTotalPersonEntry.getPersonSalaryFte());
                        conTotalRequestAmount += reportTotalPersonEntry.getPersonSalaryAmount();
                    }
                }
            }

            // calculate average and change
            if (BigDecimal.ZERO.compareTo(newFte) != 0) {
                BigDecimal averageAmount = BudgetConstructionReportHelper.calculateDivide(new BigDecimal(newTotalAmount), newFte);
                newAverageAmount = BudgetConstructionReportHelper.setDecimalDigit(averageAmount, 0, false).intValue();
            }

            if (BigDecimal.ZERO.compareTo(conFte) != 0) {
                BigDecimal averageBaseAmount = BudgetConstructionReportHelper.calculateDivide(new BigDecimal(conTotalBaseAmount), conFte);
                conAverageBaseAmount = BudgetConstructionReportHelper.setDecimalDigit(averageBaseAmount, 0, false).intValue();

                BigDecimal averageRequestAmount = BudgetConstructionReportHelper.calculateDivide(new BigDecimal(conTotalRequestAmount), conFte);
                conAverageRequestAmount = BudgetConstructionReportHelper.setDecimalDigit(averageRequestAmount, 0, false).intValue();
            }

            conAveragechange = conAverageRequestAmount - conAverageBaseAmount;

            if (conAverageBaseAmount != 0) {
                BigDecimal percentChange = BudgetConstructionReportHelper.calculatePercent(conAveragechange, conAverageBaseAmount);
                conPercentChange = BudgetConstructionReportHelper.setDecimalDigit(percentChange, 1, false);
            }

            budgetConstructionOrgReasonSummaryReportTotal.setBudgetConstructionSalaryFunding(totalOrgEntry);
            budgetConstructionOrgReasonSummaryReportTotal.setNewFte(newFte);
            budgetConstructionOrgReasonSummaryReportTotal.setNewTotalAmount(newTotalAmount);
            budgetConstructionOrgReasonSummaryReportTotal.setConTotalBaseAmount(conTotalBaseAmount);
            budgetConstructionOrgReasonSummaryReportTotal.setConFte(conFte);
            budgetConstructionOrgReasonSummaryReportTotal.setConTotalRequestAmount(conTotalRequestAmount);
            budgetConstructionOrgReasonSummaryReportTotal.setNewAverageAmount(newAverageAmount);
            budgetConstructionOrgReasonSummaryReportTotal.setConAverageBaseAmount(conAverageBaseAmount);
            budgetConstructionOrgReasonSummaryReportTotal.setConAverageRequestAmount(conAverageRequestAmount);
            budgetConstructionOrgReasonSummaryReportTotal.setConAveragechange(conAveragechange);
            budgetConstructionOrgReasonSummaryReportTotal.setConPercentChange(conPercentChange);

            returnCollection.add(budgetConstructionOrgReasonSummaryReportTotal);

            newFte = BigDecimal.ZERO;
            newTotalAmount = 0;
            newAverageAmount = 0;
            conFte = BigDecimal.ZERO;
            conTotalBaseAmount = 0;
            conTotalRequestAmount = 0;
            conAverageBaseAmount = 0;
            conAverageRequestAmount = 0;
            conAveragechange = 0;
            conPercentChange = BigDecimal.ZERO;
        }
        return returnCollection;
    }

    /**
     * builds orderByList for sort order.
     * 
     * @return returnList
     */
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList<String>();
        returnList.add(KFSPropertyConstants.EMPLID);

        return returnList;
    }

    /**
     * Deletes duplicated entry from list
     * 
     * @param List list
     * @return a list that all duplicated entries were deleted
     */
    private List<BudgetConstructionSalaryFunding> retainUniqeSalaryFunding(List<BudgetConstructionSalaryFunding> salaryFundingList, Map map, int mode) {
        List<BudgetConstructionSalaryFunding> returnList = new ArrayList<BudgetConstructionSalaryFunding>();

        BudgetConstructionSalaryFunding salaryFundingEntryAux = null;
        for (BudgetConstructionSalaryFunding salaryFundingEntry : salaryFundingList) {
            switch (mode) {
                case 1: { // mode 1 is for getting a list of total object
                    if (salaryFundingEntryAux == null || !isSameSalaryFundingEntryForTotalPerson(salaryFundingEntry, salaryFundingEntryAux, map)) {
                        returnList.add(salaryFundingEntry);
                        salaryFundingEntryAux = salaryFundingEntry;
                    }

                    break;
                }
                case 2: {// mode 2 is for getting a list of total account
                    if (salaryFundingEntryAux == null || !isSameSalaryFundingEntryForTotalOrg(salaryFundingEntry, salaryFundingEntryAux, map)) {
                        returnList.add(salaryFundingEntry);
                        salaryFundingEntryAux = salaryFundingEntry;
                    }

                    break;
                }
            }
        }

        return returnList;
    }

    private boolean isSameSalaryFundingEntryForTotalPerson(BudgetConstructionSalaryFunding firstbcsf, BudgetConstructionSalaryFunding secondbcsf, Map map) {
        BudgetConstructionSalarySocialSecurityNumber firstBcssn = (BudgetConstructionSalarySocialSecurityNumber) map.get(firstbcsf);
        BudgetConstructionSalarySocialSecurityNumber secondBcssn = (BudgetConstructionSalarySocialSecurityNumber) map.get(secondbcsf);
        if (firstBcssn.getOrganizationChartOfAccountsCode().equals(secondBcssn.getOrganizationChartOfAccountsCode()) && firstBcssn.getOrganizationCode().equals(secondBcssn.getOrganizationCode()) && firstBcssn.getEmplid().equals(secondBcssn.getEmplid())) {
            return true;
        }

        return false;
    }

    private boolean isSameSalaryFundingEntryForTotalOrg(BudgetConstructionSalaryFunding firstbcsf, BudgetConstructionSalaryFunding secondbcsf, Map map) {
        BudgetConstructionSalarySocialSecurityNumber firstBcssn = (BudgetConstructionSalarySocialSecurityNumber) map.get(firstbcsf);
        BudgetConstructionSalarySocialSecurityNumber secondBcssn = (BudgetConstructionSalarySocialSecurityNumber) map.get(secondbcsf);
        if (firstBcssn.getOrganizationChartOfAccountsCode().equals(secondBcssn.getOrganizationChartOfAccountsCode()) && firstBcssn.getOrganizationCode().equals(secondBcssn.getOrganizationCode())) {
            return true;
        }

        return false;
    }

    /**
     * Sets the budgetConstructionSalarySummaryReportDao attribute value.
     * 
     * @param budgetConstructionSalarySummaryReportDao The budgetConstructionSalarySummaryReportDao to set.
     */
    public void setBudgetConstructionSalarySummaryReportDao(BudgetConstructionSalarySummaryReportDao budgetConstructionSalarySummaryReportDao) {
        this.budgetConstructionSalarySummaryReportDao = budgetConstructionSalarySummaryReportDao;
    }

    /**
     * Sets the budgetConstructionOrganizationReportsService attribute value.
     * 
     * @param budgetConstructionOrganizationReportsService The budgetConstructionOrganizationReportsService to set.
     */
    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }

    /**
     * Sets the budgetConstructionReportsServiceHelper attribute value.
     * 
     * @param budgetConstructionReportsServiceHelper The budgetConstructionReportsServiceHelper to set.
     */
    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
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
