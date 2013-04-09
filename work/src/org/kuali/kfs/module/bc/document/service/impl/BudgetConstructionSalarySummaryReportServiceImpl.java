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
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgSalarySummaryReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgSalarySummaryReportTotal;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalaryFunding;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalarySocialSecurityNumber;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionSalarySummaryReportDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionSalarySummaryReportService;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionSalarySummaryReportServiceImpl implements BudgetConstructionSalarySummaryReportService {

    protected BudgetConstructionSalarySummaryReportDao budgetConstructionSalarySummaryReportDao;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    protected ConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionSalarySummaryReportService#updateSalarySummaryReport(java.lang.String, java.lang.Integer, org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings)
     */
    public void updateSalarySummaryReport(String principalName, Integer universityFiscalYear, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {

        boolean applyAThreshold = budgetConstructionReportThresholdSettings.isUseThreshold();
        boolean selectOnlyGreaterThanOrEqualToThreshold = budgetConstructionReportThresholdSettings.isUseGreaterThanOperator();

        KualiDecimal thresholdPercent = budgetConstructionReportThresholdSettings.getThresholdPercent();
        if (applyAThreshold) {
            budgetConstructionSalarySummaryReportDao.updateSalaryAndReasonSummaryReportsWithThreshold(principalName, universityFiscalYear-1, selectOnlyGreaterThanOrEqualToThreshold, thresholdPercent);
        }
        else {
            budgetConstructionSalarySummaryReportDao.updateSalaryAndReasonSummaryReportsWithoutThreshold(principalName, false);
        }

    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionSalarySummaryReportService#buildReports(java.lang.Integer, java.lang.String, org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings)
     */
    public Collection<BudgetConstructionOrgSalarySummaryReport> buildReports(Integer universityFiscalYear, String principalName, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        Collection<BudgetConstructionOrgSalarySummaryReport> reportSet = new ArrayList();

        BudgetConstructionOrgSalarySummaryReport orgSalarySummaryReportEntry;
        Collection<BudgetConstructionSalarySocialSecurityNumber> bcSalarySsnList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionSalarySocialSecurityNumber.class, principalName, buildOrderByList());

        Map salaryFundingMap = new HashMap();
        for (BudgetConstructionSalarySocialSecurityNumber ssnEntry : bcSalarySsnList) {
            Collection<BudgetConstructionSalaryFunding> salaryFundingList = budgetConstructionReportsServiceHelper.getSalaryFunding(principalName, ssnEntry.getEmplid());
            salaryFundingMap.put(ssnEntry, salaryFundingList);
        }

        List<BudgetConstructionSalarySocialSecurityNumber> listForCalculateTotalPerson = deleteDuplicated((List) bcSalarySsnList, 1);
        List<BudgetConstructionSalarySocialSecurityNumber> listForCalculateTotalOrg = deleteDuplicated((List) bcSalarySsnList, 2);

        // Calculate Total Section
        Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalPerson = calculatePersonTotal(universityFiscalYear, bcSalarySsnList, listForCalculateTotalPerson, salaryFundingMap);
        Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalOrg = calculateOrgTotal(salarySummaryTotalPerson, listForCalculateTotalOrg, salaryFundingMap);

        String objectCodes = budgetConstructionReportsServiceHelper.getSelectedObjectCodes(principalName);
        for (BudgetConstructionSalarySocialSecurityNumber ssnEntry : bcSalarySsnList) {
            Collection<BudgetConstructionSalaryFunding> salaryFundingList = (Collection) salaryFundingMap.get(ssnEntry);

            for (BudgetConstructionSalaryFunding salaryFundingEntry : salaryFundingList) {
                orgSalarySummaryReportEntry = new BudgetConstructionOrgSalarySummaryReport();
                buildReportsHeader(universityFiscalYear, objectCodes, orgSalarySummaryReportEntry, salaryFundingEntry, ssnEntry, budgetConstructionReportThresholdSettings);
                buildReportsBody(universityFiscalYear, orgSalarySummaryReportEntry, salaryFundingEntry, ssnEntry);
                buildReportsTotal(orgSalarySummaryReportEntry, ssnEntry, salarySummaryTotalPerson, salarySummaryTotalOrg);
                reportSet.add(orgSalarySummaryReportEntry);
            }
        }

        return reportSet;
    }

    /**
     * builds report Header
     */
    public void buildReportsHeader(Integer universityFiscalYear, String objectCodes, BudgetConstructionOrgSalarySummaryReport orgSalarySummaryReportEntry, BudgetConstructionSalaryFunding salaryFundingEntry, BudgetConstructionSalarySocialSecurityNumber bcSSN, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        String chartDesc = bcSSN.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = bcSSN.getOrganization().getOrganizationName();


        Integer prevFiscalyear = universityFiscalYear - 1;
        orgSalarySummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));

        orgSalarySummaryReportEntry.setOrganizationCode(bcSSN.getOrganizationCode());
        if (orgName == null) {
            orgSalarySummaryReportEntry.setOrganizationName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgSalarySummaryReportEntry.setOrganizationName(orgName);
        }

        orgSalarySummaryReportEntry.setOrgChartOfAccountsCode(bcSSN.getOrganizationChartOfAccountsCode());
        if (chartDesc == null) {
            orgSalarySummaryReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgSalarySummaryReportEntry.setOrgChartOfAccountDescription(chartDesc);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgSalarySummaryReportEntry.setReqFy(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgSalarySummaryReportEntry.setFinancialObjectCode(salaryFundingEntry.getFinancialObjectCode());

        orgSalarySummaryReportEntry.setObjectCodes(objectCodes);

        if (budgetConstructionReportThresholdSettings.isUseThreshold()) {
            if (budgetConstructionReportThresholdSettings.isUseGreaterThanOperator()) {
                orgSalarySummaryReportEntry.setThreshold(BCConstants.Report.THRESHOLD + BCConstants.Report.THRESHOLD_GREATER + budgetConstructionReportThresholdSettings.getThresholdPercent().toString() + BCConstants.Report.PERCENT);
            }
            else {
                orgSalarySummaryReportEntry.setThreshold(BCConstants.Report.THRESHOLD + BCConstants.Report.THRESHOLD_LESS + budgetConstructionReportThresholdSettings.getThresholdPercent().toString() + BCConstants.Report.PERCENT);
            }
        }
    }

    /**
     * builds report body
     */
    public void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgSalarySummaryReport orgSalarySummaryReportEntry, BudgetConstructionSalaryFunding salaryFundingEntry, BudgetConstructionSalarySocialSecurityNumber bcSSN) {
        BudgetConstructionAdministrativePost budgetConstructionAdministrativePost;
        BudgetConstructionPosition budgetConstructionPosition;
        BudgetConstructionCalculatedSalaryFoundationTracker budgetConstructionCalculatedSalaryFoundationTracker;
        PendingBudgetConstructionAppointmentFunding appointmentFundingEntry = salaryFundingEntry.getPendingAppointmentFunding();

        int curToInt = -1;
        double curFteInt = -1.00;

        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent = budgetConstructionReportsServiceHelper.getBudgetConstructionIntendedIncumbent(appointmentFundingEntry);
        if (budgetConstructionIntendedIncumbent != null) {
            orgSalarySummaryReportEntry.setIuClassificationLevel(budgetConstructionIntendedIncumbent.getIuClassificationLevel());
        }

        int nameLength = bcSSN.getName().length();
        orgSalarySummaryReportEntry.setName(bcSSN.getName().substring(0, (nameLength > 35) ? 35 : nameLength));
        budgetConstructionAdministrativePost = budgetConstructionReportsServiceHelper.getBudgetConstructionAdministrativePost(appointmentFundingEntry);
        budgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(universityFiscalYear, appointmentFundingEntry);

        // set report body
        orgSalarySummaryReportEntry.setChartOfAccountsCode(salaryFundingEntry.getChartOfAccountsCode());
        orgSalarySummaryReportEntry.setAccountNumber(salaryFundingEntry.getAccountNumber());
        orgSalarySummaryReportEntry.setSubAccountNumber(salaryFundingEntry.getSubAccountNumber());
        orgSalarySummaryReportEntry.setFinancialSubObjectCode(salaryFundingEntry.getFinancialSubObjectCode());

        if (budgetConstructionAdministrativePost != null) {
            orgSalarySummaryReportEntry.setAdministrativePost(budgetConstructionAdministrativePost.getAdministrativePost());
        }

        if (budgetConstructionPosition != null) {
            orgSalarySummaryReportEntry.setPositionNumber(budgetConstructionPosition.getPositionNumber());
            orgSalarySummaryReportEntry.setNormalWorkMonthsAndiuPayMonths(budgetConstructionPosition.getIuNormalWorkMonths() + "/" + budgetConstructionPosition.getIuPayMonths());
            orgSalarySummaryReportEntry.setPositionFte(BudgetConstructionReportHelper.setDecimalDigit(budgetConstructionPosition.getPositionFullTimeEquivalency(), 5, true));
            orgSalarySummaryReportEntry.setPositionSalaryPlanDefault(budgetConstructionPosition.getPositionSalaryPlanDefault());
            orgSalarySummaryReportEntry.setPositionGradeDefault(budgetConstructionPosition.getPositionGradeDefault());
        }
        if (appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
            budgetConstructionCalculatedSalaryFoundationTracker = appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0);
            orgSalarySummaryReportEntry.setCsfTimePercent(BudgetConstructionReportHelper.setDecimalDigit(budgetConstructionCalculatedSalaryFoundationTracker.getCsfTimePercent(), 2, false));
            orgSalarySummaryReportEntry.setCsfAmount(new Integer(budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue()));

            // calculate amountChange and percentChange
            if (appointmentFundingEntry.getAppointmentRequestedFteQuantity().equals(budgetConstructionCalculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity())) {
                Integer amountChange = appointmentFundingEntry.getAppointmentRequestedAmount().subtract(budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount()).intValue();
                orgSalarySummaryReportEntry.setAmountChange(amountChange);
                orgSalarySummaryReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(new BigDecimal(amountChange), budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().bigDecimalValue()));
            }
        }

        if (appointmentFundingEntry != null) {
            orgSalarySummaryReportEntry.setAppointmentFundingDurationCode(appointmentFundingEntry.getAppointmentFundingDurationCode());
            orgSalarySummaryReportEntry.setAppointmentTotalIntendedAmount(BudgetConstructionReportHelper.convertKualiInteger(appointmentFundingEntry.getAppointmentTotalIntendedAmount()));
            orgSalarySummaryReportEntry.setAppointmentTotalIntendedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity(), 5, false));

            if (appointmentFundingEntry.getAppointmentFundingDurationCode() != null && appointmentFundingEntry.getAppointmentFundingDurationCode().equals(BCConstants.Report.NONE)) {

                orgSalarySummaryReportEntry.setSalaryAmount(BudgetConstructionReportHelper.convertKualiInteger(appointmentFundingEntry.getAppointmentRequestedAmount()));
                orgSalarySummaryReportEntry.setPercentAmount(appointmentFundingEntry.getAppointmentRequestedTimePercent());
                orgSalarySummaryReportEntry.setSalaryMonths(appointmentFundingEntry.getAppointmentFundingMonth());

            }
            else {
                orgSalarySummaryReportEntry.setSalaryAmount(BudgetConstructionReportHelper.convertKualiInteger(appointmentFundingEntry.getAppointmentRequestedCsfAmount()));

                if (appointmentFundingEntry.getAppointmentRequestedCsfTimePercent() == null) {
                    orgSalarySummaryReportEntry.setPercentAmount(BigDecimal.ZERO);
                }
                else {
                    orgSalarySummaryReportEntry.setPercentAmount(appointmentFundingEntry.getAppointmentRequestedCsfTimePercent());
                }


                if (budgetConstructionPosition != null) {
                    orgSalarySummaryReportEntry.setSalaryMonths(budgetConstructionPosition.getIuNormalWorkMonths());
                }
            }

            // group
            orgSalarySummaryReportEntry.setEmplid(bcSSN.getEmplid());
        }

        if (appointmentFundingEntry.isAppointmentFundingDeleteIndicator()) {
            orgSalarySummaryReportEntry.setDeleteBox(BCConstants.Report.DELETE_MARK);
        }
        else {
            orgSalarySummaryReportEntry.setDeleteBox(BCConstants.Report.BLANK);
        }

        // set tiFlag
        if (appointmentFundingEntry.isAppointmentFundingDeleteIndicator()) {
            if (curToInt == -1) {
                curToInt = appointmentFundingEntry.getAppointmentTotalIntendedAmount().intValue();
            }
            else if (curToInt != appointmentFundingEntry.getAppointmentTotalIntendedAmount().intValue()) {
                orgSalarySummaryReportEntry.setTiFlag(BCConstants.Report.PLUS);
            }
            if (curFteInt == -1.00) {
                curFteInt = appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity().doubleValue();
            }
            else if (curFteInt != appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity().doubleValue()) {
                orgSalarySummaryReportEntry.setTiFlag(BCConstants.Report.PLUS);
            }
        }
    }

    /**
     * build the total sections for the report
     */
    public void buildReportsTotal(BudgetConstructionOrgSalarySummaryReport orgSalarySummaryReportEntry, BudgetConstructionSalarySocialSecurityNumber ssnEntry, Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalPerson, Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalOrg) {

        for (BudgetConstructionOrgSalarySummaryReportTotal totalPersonEntry : salarySummaryTotalPerson) {
            if (isSameSsnEntryForTotalPerson(totalPersonEntry.getBudgetConstructionSalarySocialSecurityNumber(), ssnEntry)) {
                orgSalarySummaryReportEntry.setPersonPositionNumber(totalPersonEntry.getPersonPositionNumber());
                orgSalarySummaryReportEntry.setPersonFiscalYearTag(totalPersonEntry.getPersonFiscalYearTag());
                orgSalarySummaryReportEntry.setPersonNormalMonthsAndPayMonths(totalPersonEntry.getPersonCsfNormalMonths().toString() + "/" + totalPersonEntry.getPersonCsfPayMonths().toString());
                orgSalarySummaryReportEntry.setPersonCsfAmount(totalPersonEntry.getPersonCsfAmount());
                orgSalarySummaryReportEntry.setPersonCsfPercent(totalPersonEntry.getPersonCsfPercent());
                orgSalarySummaryReportEntry.setPersonSalaryNormalMonths(totalPersonEntry.getPersonSalaryNormalMonths());
                orgSalarySummaryReportEntry.setPersonSalaryAmount(totalPersonEntry.getPersonSalaryAmount());
                orgSalarySummaryReportEntry.setPersonSalaryPercent(totalPersonEntry.getPersonSalaryPercent());
                orgSalarySummaryReportEntry.setPersonSalaryFte(totalPersonEntry.getPersonSalaryFte());
                orgSalarySummaryReportEntry.setPersonTiFlag(totalPersonEntry.getPersonTiFlag());
                orgSalarySummaryReportEntry.setPersonAmountChange(totalPersonEntry.getPersonAmountChange());
                orgSalarySummaryReportEntry.setPersonPercentChange(totalPersonEntry.getPersonPercentChange());
            }
            for (BudgetConstructionOrgSalarySummaryReportTotal totalOrgEntry : salarySummaryTotalOrg) {
                if (isSameSsnEntryForTotalOrg(totalOrgEntry.getBudgetConstructionSalarySocialSecurityNumber(), ssnEntry)) {
                    orgSalarySummaryReportEntry.setNewFte(totalOrgEntry.getNewFte());
                    orgSalarySummaryReportEntry.setNewTotalAmount(totalOrgEntry.getNewTotalAmount());
                    orgSalarySummaryReportEntry.setConTotalBaseAmount(totalOrgEntry.getConTotalBaseAmount());
                    orgSalarySummaryReportEntry.setConFte(totalOrgEntry.getConFte());
                    orgSalarySummaryReportEntry.setConTotalRequestAmount(totalOrgEntry.getConTotalRequestAmount());
                    orgSalarySummaryReportEntry.setNewAverageAmount(totalOrgEntry.getNewAverageAmount());
                    orgSalarySummaryReportEntry.setConAverageBaseAmount(totalOrgEntry.getConAverageBaseAmount());
                    orgSalarySummaryReportEntry.setConAverageRequestAmount(totalOrgEntry.getConAverageRequestAmount());
                    orgSalarySummaryReportEntry.setConAveragechange(totalOrgEntry.getConAveragechange());
                    orgSalarySummaryReportEntry.setConPercentChange(totalOrgEntry.getConPercentChange());
                }
            }
        }
    }

    // calculate the totals for the given person
    protected Collection<BudgetConstructionOrgSalarySummaryReportTotal> calculatePersonTotal(Integer universityFiscalYear, Collection<BudgetConstructionSalarySocialSecurityNumber> bcSalarySsnList, List<BudgetConstructionSalarySocialSecurityNumber> listForCalculateTotalPerson, Map salaryFundingMap) {
        Collection<BudgetConstructionOrgSalarySummaryReportTotal> returnCollection = new ArrayList<BudgetConstructionOrgSalarySummaryReportTotal>();

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

    // create a report total for the given person with the values in the given total holder
    protected BudgetConstructionOrgSalarySummaryReportTotal createReportTotal(BudgetConstructionSalarySocialSecurityNumber totalPersonEntry, PersonTotalHolder totalsHolder) {
        BudgetConstructionOrgSalarySummaryReportTotal reportTotal = new BudgetConstructionOrgSalarySummaryReportTotal();

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

            BigDecimal restatementCsfPercent = salaryFteQuantity.divide(csfFteQuantity, 6, BigDecimal.ROUND_HALF_UP);
            BigDecimal csfAmount = new BigDecimal(totalsHolder.csfAmount);
            restatementCsfAmount = csfAmount.multiply(restatementCsfPercent).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
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
            Integer salaryMonths = 0;
            BigDecimal salaryPercent = BigDecimal.ZERO;
            String durationCode = appointmentFunding.getAppointmentFundingDurationCode();

            if (StringUtils.equals(durationCode, BCConstants.Report.NONE)) {
                salaryAmount = appointmentFunding.getAppointmentRequestedAmount().intValue();
                salaryMonths = appointmentFunding.getAppointmentFundingMonth();
                salaryPercent = appointmentFunding.getAppointmentRequestedTimePercent();
            }
            else {
                salaryAmount = appointmentFunding.getAppointmentRequestedCsfAmount().intValue();
                salaryMonths = budgetConstructionPosition.getIuNormalWorkMonths();

                boolean hasRequestedCsfTimePercent = appointmentFunding.getAppointmentRequestedCsfTimePercent() != null;
                salaryPercent = hasRequestedCsfTimePercent ? appointmentFunding.getAppointmentRequestedCsfTimePercent() : BigDecimal.ZERO;
            }

            if (salaryAmount > maxSalaryAmount) {
                maxSalaryAmount = totalsHolder.salaryAmount;
                totalsHolder.salaryPayMonth = budgetConstructionPosition.getIuPayMonths();
                totalsHolder.salaryNormalMonths = salaryMonths;
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
    protected Collection<BudgetConstructionOrgSalarySummaryReportTotal> calculateOrgTotal(Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalPerson, List<BudgetConstructionSalarySocialSecurityNumber> listForCalculateTotalOrg, Map salaryFundingMap) {
        Collection<BudgetConstructionOrgSalarySummaryReportTotal> returnCollection = new ArrayList<BudgetConstructionOrgSalarySummaryReportTotal>();

        for (BudgetConstructionSalarySocialSecurityNumber totalOrgEntry : listForCalculateTotalOrg) {
            OrganizationTotalHolder totalsHolder = new OrganizationTotalHolder();

            for (BudgetConstructionOrgSalarySummaryReportTotal reportTotalPersonEntry : salarySummaryTotalPerson) {
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
    protected BudgetConstructionOrgSalarySummaryReportTotal createReportTotal(BudgetConstructionSalarySocialSecurityNumber totalOrgEntry, OrganizationTotalHolder totalsHolder) {
        BudgetConstructionOrgSalarySummaryReportTotal reportTotal = new BudgetConstructionOrgSalarySummaryReportTotal();

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
    public List<String> buildOrderByListForSalaryFunding() {
        List<String> returnList = new ArrayList<String>();
        returnList.add(KFSPropertyConstants.POSITION_NUMBER);
        returnList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        return returnList;
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
     * set the attribute budgetConstructionSalarySummaryReportDao
     *
     * @param budgetConstructionSalarySummaryReportDao
     */
    public void setBudgetConstructionSalarySummaryReportDao(BudgetConstructionSalarySummaryReportDao budgetConstructionSalarySummaryReportDao) {
        this.budgetConstructionSalarySummaryReportDao = budgetConstructionSalarySummaryReportDao;
    }

    /**
     * set the attribute kualiConfigurationService
     *
     * @param kualiConfigurationService
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * set the attribute budgetConstructionReportsServiceHelper
     *
     * @param budgetConstructionReportsServiceHelper
     */
    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }
}
