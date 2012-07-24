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
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionReasonSummaryReportService.
 */
@Transactional
public class BudgetConstructionReasonSummaryReportServiceImpl implements BudgetConstructionReasonSummaryReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionReasonSummaryReportServiceImpl.class);

    protected BudgetConstructionSalarySummaryReportDao budgetConstructionSalarySummaryReportDao;
    protected BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    protected ConfigurationService kualiConfigurationService;
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReasonSummaryReportService#updateReasonSummaryReport(java.lang.String,
     *      java.lang.Integer, org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings)
     */
    @Override
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
    @Override
    public Collection<BudgetConstructionOrgReasonSummaryReport> buildReports(Integer universityFiscalYear, String principalId, BudgetConstructionReportThresholdSettings reportThresholdSettings) {
        Collection<BudgetConstructionOrgReasonSummaryReport> reportSet = new ArrayList<BudgetConstructionOrgReasonSummaryReport>();

        BudgetConstructionOrgReasonSummaryReport reasonSummaryReport;
        Collection<BudgetConstructionSalarySocialSecurityNumber> bcSalarySsnList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionSalarySocialSecurityNumber.class, principalId, buildOrderByList());

        Map salaryFundingMap = new HashMap();
        for (BudgetConstructionSalarySocialSecurityNumber ssnEntry : bcSalarySsnList) {
            Collection<BudgetConstructionSalaryFunding> salaryFundingList = budgetConstructionReportsServiceHelper.getSalaryFunding(principalId, ssnEntry.getEmplid());
            salaryFundingMap.put(ssnEntry, salaryFundingList);
        }

        List<BudgetConstructionSalarySocialSecurityNumber> listForCalculateTotalPerson = deleteDuplicated((List) bcSalarySsnList, 1);
        List<BudgetConstructionSalarySocialSecurityNumber> listForCalculateTotalOrg = deleteDuplicated((List) bcSalarySsnList, 2);

        // Calculate Total Section
        Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalPerson = calculatePersonTotal(universityFiscalYear, bcSalarySsnList, listForCalculateTotalPerson, salaryFundingMap);
        Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalOrg = calculateOrgTotal(reasonSummaryTotalPerson, listForCalculateTotalOrg, salaryFundingMap);

        // get object codes
        String objectCodes = budgetConstructionReportsServiceHelper.getSelectedObjectCodes(principalId);

        // get reason codes
        String reasonCodes = budgetConstructionReportsServiceHelper.getSelectedReasonCodes(principalId);

        for (BudgetConstructionSalarySocialSecurityNumber ssnEntry : bcSalarySsnList) {
            Collection<BudgetConstructionSalaryFunding> salaryFundingList = (Collection) salaryFundingMap.get(ssnEntry);

            for (BudgetConstructionSalaryFunding salaryFundingEntry : salaryFundingList) {
                reasonSummaryReport = new BudgetConstructionOrgReasonSummaryReport();
                buildReportsHeader(universityFiscalYear, objectCodes, reasonCodes, reasonSummaryReport, salaryFundingEntry, ssnEntry, reportThresholdSettings);
                buildReportsBody(universityFiscalYear, reasonSummaryReport, salaryFundingEntry, ssnEntry);
                buildReportsTotal(reasonSummaryReport, ssnEntry, reasonSummaryTotalPerson, reasonSummaryTotalOrg);
                reportSet.add(reasonSummaryReport);
            }
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
            String wrongOrganizationName = kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME);
            reasonSummaryReport.setOrganizationName(wrongOrganizationName);
        }
        else {
            reasonSummaryReport.setOrganizationName(organizationName);
        }

        reasonSummaryReport.setOrgChartOfAccountsCode(bcSSN.getOrganizationChartOfAccountsCode());
        String chartDescription = bcSSN.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        if (chartDescription == null) {
            String wrongChartDescription = kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION);
            reasonSummaryReport.setOrgChartOfAccountDescription(wrongChartDescription);
        }
        else {
            reasonSummaryReport.setOrgChartOfAccountDescription(chartDescription);
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
        if (ObjectUtils.isNotNull(appointmentFundingReasonList) && !appointmentFundingReasonList.isEmpty()) {
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

        int nameLength = salarySSN.getName().length();
        reasonSummaryReport.setName(salarySSN.getName().substring(0, (nameLength > 35) ? 35 : nameLength));

        BudgetConstructionAdministrativePost administrativePost = budgetConstructionReportsServiceHelper.getBudgetConstructionAdministrativePost(appointmentFunding);
        BudgetConstructionPosition budgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(fiscalYear, appointmentFunding);

        // set report body
        reasonSummaryReport.setChartOfAccountsCode(salaryFundingEntry.getChartOfAccountsCode());
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
        if (ObjectUtils.isNotNull(csfTracker)) {
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

        if (StringUtils.equals(appointmentFunding.getFinancialSubObjectCode(), KFSConstants.getDashFinancialSubObjectCode())) {
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

    /**
     * build the total sections for the report
     */
    public void buildReportsTotal(BudgetConstructionOrgReasonSummaryReport reasonSummaryReportEntry, BudgetConstructionSalarySocialSecurityNumber ssnEntry, Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalPerson, Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalOrg) {

        for (BudgetConstructionOrgReasonSummaryReportTotal totalPersonEntry : reasonSummaryTotalPerson) {
            if (isSameSsnEntryForTotalPerson(totalPersonEntry.getBudgetConstructionSalarySocialSecurityNumber(), ssnEntry)) {
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
                if (isSameSsnEntryForTotalOrg(totalOrgEntry.getBudgetConstructionSalarySocialSecurityNumber(), ssnEntry)) {
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

    // calculate the totals for the given person
    protected Collection<BudgetConstructionOrgReasonSummaryReportTotal> calculatePersonTotal(Integer universityFiscalYear, Collection<BudgetConstructionSalarySocialSecurityNumber> bcSalarySsnList, List<BudgetConstructionSalarySocialSecurityNumber> listForCalculateTotalPerson, Map salaryFundingMap) {
        Collection<BudgetConstructionOrgReasonSummaryReportTotal> returnCollection = new ArrayList<BudgetConstructionOrgReasonSummaryReportTotal>();

        for (BudgetConstructionSalarySocialSecurityNumber personEntry : listForCalculateTotalPerson) {
            PersonTotalHolder totalsHolder = new PersonTotalHolder();
            totalsHolder.emplid = personEntry.getEmplid();

            for (BudgetConstructionSalarySocialSecurityNumber salaryFundingEntry : bcSalarySsnList) {
                if (isSameSsnEntryForTotalPerson(personEntry, salaryFundingEntry)) {
                    Collection<BudgetConstructionSalaryFunding> salaryFundings = (Collection<BudgetConstructionSalaryFunding>) salaryFundingMap.get(personEntry);
                    this.collectPersonTotal(universityFiscalYear, salaryFundings, totalsHolder);
                }
            }

            this.adjustPersonTotal(totalsHolder);
            returnCollection.add(this.createReportTotal(personEntry, totalsHolder));
        }

        return returnCollection;
    }

    // adjust the total amount that just is held by the given holder
    protected void adjustPersonTotal(PersonTotalHolder totalsHolder) {
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

    // collect the total amounts for a single person and save the totals in the given holder
    protected void collectPersonTotal(Integer universityFiscalYear, Collection<BudgetConstructionSalaryFunding> salaryFundings, PersonTotalHolder totalsHolder) {
        int maxSalaryAmount = 0;
        int maxCsfAmount = 0;

        for (BudgetConstructionSalaryFunding salaryFunding : salaryFundings) {
            PendingBudgetConstructionAppointmentFunding appointmentFunding = salaryFunding.getPendingAppointmentFunding();
            BudgetConstructionPosition budgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(universityFiscalYear, appointmentFunding);

            int salaryAmount = 0;
            BigDecimal salaryPercent = BigDecimal.ZERO;
            String durationCode = appointmentFunding.getAppointmentFundingDurationCode();

            if (StringUtils.equals(durationCode, BCConstants.Report.NONE)) {
                salaryAmount = appointmentFunding.getAppointmentRequestedAmount().intValue();
                totalsHolder.salaryNormalMonths = appointmentFunding.getAppointmentFundingMonth();
                salaryPercent = appointmentFunding.getAppointmentRequestedTimePercent();
            }
            else {
                salaryAmount = appointmentFunding.getAppointmentRequestedCsfAmount().intValue();
                totalsHolder.salaryNormalMonths = budgetConstructionPosition.getIuNormalWorkMonths();

                boolean hasRequestedCsfTimePercent = appointmentFunding.getAppointmentRequestedCsfTimePercent() != null;
                salaryPercent = hasRequestedCsfTimePercent ? appointmentFunding.getAppointmentRequestedCsfTimePercent() : BigDecimal.ZERO;
            }

            if (salaryAmount > maxSalaryAmount) {
                maxSalaryAmount = totalsHolder.salaryAmount;
                totalsHolder.salaryPayMonth = budgetConstructionPosition.getIuPayMonths();
                totalsHolder.salaryNormalMonths = appointmentFunding.getAppointmentFundingMonth();
            }

            totalsHolder.salaryAmount += salaryAmount;
            totalsHolder.salaryPercent = totalsHolder.salaryPercent.add(salaryPercent);

            BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = appointmentFunding.getEffectiveCSFTracker();
            if (csfTracker == null) {
                continue;
            }

            KualiInteger effectiveCsfAmount = csfTracker.getCsfAmount();
            if (effectiveCsfAmount == null || effectiveCsfAmount.isZero()) {
                continue;
            }

            if (effectiveCsfAmount.intValue() > maxCsfAmount) {
                maxCsfAmount = effectiveCsfAmount.intValue();
            }

            totalsHolder.csfAmount += effectiveCsfAmount.intValue();
            totalsHolder.csfPercent = totalsHolder.csfPercent.add(csfTracker.getCsfTimePercent());

            // data for previous year, position table has two data, one is for current year and another is for previous year.
            Integer previousFiscalYear = universityFiscalYear - 1;
            BudgetConstructionPosition previousYearBudgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(previousFiscalYear, appointmentFunding);

            totalsHolder.csfPayMonths = previousYearBudgetConstructionPosition.getIuPayMonths();
            totalsHolder.csfNormalMonths = previousYearBudgetConstructionPosition.getIuNormalWorkMonths();

            totalsHolder.positionNumber = budgetConstructionPosition.getPositionNumber();
            totalsHolder.fiscalYearTag = previousFiscalYear.toString() + ":";

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

    // calculate the totals for the given organization
    protected Collection<BudgetConstructionOrgReasonSummaryReportTotal> calculateOrgTotal(Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalPerson, List<BudgetConstructionSalarySocialSecurityNumber> listForCalculateTotalOrg, Map salaryFundingMap) {
        Collection<BudgetConstructionOrgReasonSummaryReportTotal> returnCollection = new ArrayList<BudgetConstructionOrgReasonSummaryReportTotal>();

        for (BudgetConstructionSalarySocialSecurityNumber totalOrgEntry : listForCalculateTotalOrg) {
            OrganizationTotalHolder totalsHolder = new OrganizationTotalHolder();

            for (BudgetConstructionOrgReasonSummaryReportTotal reportTotalPersonEntry : reasonSummaryTotalPerson) {
                if (isSameSsnEntryForTotalOrg(totalOrgEntry, reportTotalPersonEntry.getBudgetConstructionSalarySocialSecurityNumber())) {
                    if (reportTotalPersonEntry.getPersonCsfAmount() == 0) {
                        totalsHolder.newFte = totalsHolder.newFte.add(reportTotalPersonEntry.getPersonSalaryFte());
                        totalsHolder.newTotalAmount += reportTotalPersonEntry.getPersonSalaryAmount();
                    }
                    else {
                        totalsHolder.conTotalBaseAmount += reportTotalPersonEntry.getPersonCsfAmount();
                        totalsHolder.conFte = totalsHolder.conFte.add(reportTotalPersonEntry.getPersonSalaryFte());
                        totalsHolder.conTotalRequestAmount += reportTotalPersonEntry.getPersonSalaryAmount();
                    }
                }
            }

            // calculate average and change
            if (BigDecimal.ZERO.compareTo(totalsHolder.newFte) != 0) {
                BigDecimal averageAmount = BudgetConstructionReportHelper.calculateDivide(new BigDecimal(totalsHolder.newTotalAmount), totalsHolder.newFte);
                totalsHolder.newAverageAmount = BudgetConstructionReportHelper.setDecimalDigit(averageAmount, 0, false).intValue();
            }

            if (BigDecimal.ZERO.compareTo(totalsHolder.conFte) != 0) {
                BigDecimal averageAmount = BudgetConstructionReportHelper.calculateDivide(new BigDecimal(totalsHolder.conTotalBaseAmount), totalsHolder.conFte);
                totalsHolder.conAverageBaseAmount = BudgetConstructionReportHelper.setDecimalDigit(averageAmount, 0, false).intValue();

                BigDecimal averageRequestAmount = BudgetConstructionReportHelper.calculateDivide(new BigDecimal(totalsHolder.conTotalRequestAmount), totalsHolder.conFte);
                totalsHolder.conAverageRequestAmount = BudgetConstructionReportHelper.setDecimalDigit(averageRequestAmount, 0, false).intValue();
            }

            totalsHolder.conAveragechange = totalsHolder.conAverageRequestAmount - totalsHolder.conAverageBaseAmount;

            if (totalsHolder.conAverageBaseAmount != 0) {
                totalsHolder.conPercentChange = BudgetConstructionReportHelper.calculatePercent(totalsHolder.conAveragechange, totalsHolder.conAverageBaseAmount);
            }

            returnCollection.add(this.createReportTotal(totalOrgEntry, totalsHolder));
        }

        return returnCollection;
    }

    // create a report total for the given organization with the values in the given total holder
    protected BudgetConstructionOrgReasonSummaryReportTotal createReportTotal(BudgetConstructionSalarySocialSecurityNumber totalOrgEntry, OrganizationTotalHolder totalsHolder) {
        BudgetConstructionOrgReasonSummaryReportTotal reportTotal = new BudgetConstructionOrgReasonSummaryReportTotal();

        reportTotal.setBudgetConstructionSalarySocialSecurityNumber(totalOrgEntry);
        reportTotal.setNewFte(totalsHolder.newFte);
        reportTotal.setNewTotalAmount(totalsHolder.newTotalAmount);
        reportTotal.setConTotalBaseAmount(totalsHolder.conTotalBaseAmount);
        reportTotal.setConFte(totalsHolder.conFte);
        reportTotal.setConTotalRequestAmount(totalsHolder.conTotalRequestAmount);
        reportTotal.setNewAverageAmount(totalsHolder.newAverageAmount);
        reportTotal.setConAverageBaseAmount(totalsHolder.conAverageBaseAmount);
        reportTotal.setConAverageRequestAmount(totalsHolder.conAverageRequestAmount);
        reportTotal.setConAveragechange(totalsHolder.conAveragechange);
        reportTotal.setConPercentChange(totalsHolder.conPercentChange);

        return reportTotal;
    }


    // create a report total for the given person with the values in the given total holder
    protected BudgetConstructionOrgReasonSummaryReportTotal createReportTotal(BudgetConstructionSalarySocialSecurityNumber totalPersonEntry, PersonTotalHolder totalsHolder) {
        BudgetConstructionOrgReasonSummaryReportTotal reportTotal = new BudgetConstructionOrgReasonSummaryReportTotal();

        reportTotal.setBudgetConstructionSalarySocialSecurityNumber(totalPersonEntry);
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
    protected class PersonTotalHolder {
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

    // a total holder that contains the totals for an organization
    protected class OrganizationTotalHolder {
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
    }

    /**
     * builds orderByList for sort order.
     *
     * @return returnList
     */
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList<String>();
        returnList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        returnList.add(KFSPropertyConstants.PERSON_NAME);
        returnList.add(KFSPropertyConstants.EMPLID);

        return returnList;
    }

    /**
     * Deletes duplicated entry from list
     *
     * @param List list
     * @return a list that all duplicated entries were deleted
     */
    protected List deleteDuplicated(List list, int mode) {
        // mode 1 is for getting a list of total object
        // mode 2 is for getting a list of total account
        int count = 0;
        BudgetConstructionSalarySocialSecurityNumber ssnEntry = null;
        BudgetConstructionSalarySocialSecurityNumber ssnEntryAux = null;
        List returnList = new ArrayList();
        if ((list != null) && (list.size() > 0)) {
            ssnEntry = (BudgetConstructionSalarySocialSecurityNumber) list.get(count);
            ssnEntryAux = (BudgetConstructionSalarySocialSecurityNumber) list.get(count);
            returnList.add(ssnEntry);
            count++;
            while (count < list.size()) {
                ssnEntry = (BudgetConstructionSalarySocialSecurityNumber) list.get(count);
                switch (mode) {
                    case 1: {
                        if (!isSameSsnEntryForTotalPerson(ssnEntry, ssnEntryAux)) {
                            returnList.add(ssnEntry);
                            ssnEntryAux = ssnEntry;
                        }
                    }
                    case 2: {
                        if (!isSameSsnEntryForTotalOrg(ssnEntry, ssnEntryAux)) {
                            returnList.add(ssnEntry);
                            ssnEntryAux = ssnEntry;
                        }
                    }
                }
                count++;
            }
        }
        return returnList;
    }

    protected boolean isSameSsnEntryForTotalPerson(BudgetConstructionSalarySocialSecurityNumber firstSsn, BudgetConstructionSalarySocialSecurityNumber secondSsn) {
        if (firstSsn.getOrganizationChartOfAccountsCode().equals(secondSsn.getOrganizationChartOfAccountsCode()) && firstSsn.getOrganizationCode().equals(secondSsn.getOrganizationCode()) && firstSsn.getEmplid().equals(secondSsn.getEmplid())) {
            return true;
        }
        else {
            return false;
        }
    }

    protected boolean isSameSsnEntryForTotalOrg(BudgetConstructionSalarySocialSecurityNumber firstSsn, BudgetConstructionSalarySocialSecurityNumber secondSsn) {
        if (firstSsn.getOrganizationChartOfAccountsCode().equals(secondSsn.getOrganizationChartOfAccountsCode()) && firstSsn.getOrganizationCode().equals(secondSsn.getOrganizationCode())) {
            return true;
        }
        else {
            return false;
        }
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
}
