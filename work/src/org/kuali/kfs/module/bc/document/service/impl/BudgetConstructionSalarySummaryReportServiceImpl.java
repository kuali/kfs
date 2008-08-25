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
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionSalarySummaryReportServiceImpl implements BudgetConstructionSalarySummaryReportService {

    private BudgetConstructionSalarySummaryReportDao budgetConstructionSalarySummaryReportDao;
    private BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    private KualiConfigurationService kualiConfigurationService;


    public void updateSalarySummaryReport(String personUserIdentifier, Integer universityFiscalYear, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {

        boolean applyAThreshold = budgetConstructionReportThresholdSettings.isUseThreshold();
        boolean selectOnlyGreaterThanOrEqualToThreshold = budgetConstructionReportThresholdSettings.isUseGreaterThanOperator();
        KualiDecimal thresholdPercent = budgetConstructionReportThresholdSettings.getThresholdPercent();
        if (applyAThreshold) {
            budgetConstructionSalarySummaryReportDao.updateSalaryAndReasonSummaryReportsWithThreshold(personUserIdentifier, universityFiscalYear, selectOnlyGreaterThanOrEqualToThreshold, thresholdPercent);
        }
        else {
            budgetConstructionSalarySummaryReportDao.updateSalaryAndReasonSummaryReportsWithoutThreshold(personUserIdentifier, false);
        }

    }

    public Collection<BudgetConstructionOrgSalarySummaryReport> buildReports(Integer universityFiscalYear, String personUserIdentifier, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        Collection<BudgetConstructionOrgSalarySummaryReport> reportSet = new ArrayList();

        BudgetConstructionOrgSalarySummaryReport orgSalarySummaryReportEntry;
        Collection<BudgetConstructionSalarySocialSecurityNumber> bcSalarySsnList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionSalarySocialSecurityNumber.class, personUserIdentifier, buildOrderByList());


        Map salaryFundingMap = new HashMap();

        // TODO: test performance with just getting data from DB whenever it needs, and if it is slow, then change using map.
        // Map administrativePostMap = new HashMap();
        // Map positionMap = new HashMap();
        // Map intendedIncumbentMap = new HashMap();
        // for (BudgetConstructionSalarySocialSecurityNumber ssnEntry : bcSalarySsnList) {
        // Collection<BudgetConstructionSalaryFunding> salaryFundingList =
        // budgetConstructionReportsServiceHelper.getSalaryFunding(personUserIdentifier, ssnEntry.getEmplid());
        // for (BudgetConstructionSalaryFunding salaryFundingEntry : salaryFundingList){
        // BudgetConstructionAdministrativePost budgetConstructionAdministrativePost =
        // budgetConstructionReportsServiceHelper.getBudgetConstructionAdministrativePost(salaryFundingEntry.getPendingAppointmentFunding());
        // BudgetConstructionPosition budgetConstructionPosition =
        // budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(universityFiscalYear,
        // salaryFundingEntry.getPendingAppointmentFunding());
        // BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent =
        // budgetConstructionReportsServiceHelper.getBudgetConstructionIntendedIncumbent(salaryFundingEntry.getPendingAppointmentFunding());
        // administrativePostMap.put(salaryFundingEntry, budgetConstructionAdministrativePost);
        // positionMap.put(salaryFundingEntry, budgetConstructionPosition);
        // intendedIncumbentMap.put(salaryFundingEntry, budgetConstructionIntendedIncumbent);
        // }
        // salaryFundingMap.put(ssnEntry, salaryFundingList);
        // }

        // now, use a map for salaryFunding
        for (BudgetConstructionSalarySocialSecurityNumber ssnEntry : bcSalarySsnList) {
            Collection<BudgetConstructionSalaryFunding> salaryFundingList = budgetConstructionReportsServiceHelper.getSalaryFunding(personUserIdentifier, ssnEntry.getEmplid());
            salaryFundingMap.put(ssnEntry, salaryFundingList);
        }

        List<BudgetConstructionSalarySocialSecurityNumber> listForCalculateTotalPerson = deleteDuplicated((List) bcSalarySsnList, 1);
        List<BudgetConstructionSalarySocialSecurityNumber> listForCalculateTotalOrg = deleteDuplicated((List) bcSalarySsnList, 2);

        // Calculate Total Section
        Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalPerson = calculatePersonTotal(universityFiscalYear, bcSalarySsnList, listForCalculateTotalPerson, salaryFundingMap);
        Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalOrg = calculateOrgTotal(salarySummaryTotalPerson, listForCalculateTotalOrg, salaryFundingMap);

        String objectCodes = budgetConstructionReportsServiceHelper.getSelectedObjectCodes(personUserIdentifier);
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
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsHeader(Integer universityFiscalYear, String objectCodes, BudgetConstructionOrgSalarySummaryReport orgSalarySummaryReportEntry, BudgetConstructionSalaryFunding salaryFundingEntry, BudgetConstructionSalarySocialSecurityNumber bcSSN, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        String chartDesc = salaryFundingEntry.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = bcSSN.getOrganization().getOrganizationName();


        Integer prevFiscalyear = universityFiscalYear - 1;
        orgSalarySummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));

        orgSalarySummaryReportEntry.setOrganizationCode(bcSSN.getOrganizationCode());
        if (orgName == null) {
            orgSalarySummaryReportEntry.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgSalarySummaryReportEntry.setOrganizationName(orgName);
        }

        orgSalarySummaryReportEntry.setChartOfAccountsCode(salaryFundingEntry.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgSalarySummaryReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgSalarySummaryReportEntry.setChartOfAccountDescription(chartDesc);
        }


        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgSalarySummaryReportEntry.setReqFy(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
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
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgSalarySummaryReport orgSalarySummaryReportEntry, BudgetConstructionSalaryFunding salaryFundingEntry, BudgetConstructionSalarySocialSecurityNumber bcSSN) {
        BudgetConstructionAdministrativePost budgetConstructionAdministrativePost;
        BudgetConstructionPosition budgetConstructionPosition;
        BudgetConstructionCalculatedSalaryFoundationTracker budgetConstructionCalculatedSalaryFoundationTracker;
        PendingBudgetConstructionAppointmentFunding appointmentFundingEntry = salaryFundingEntry.getPendingAppointmentFunding();
        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent;
        int curToInt = -1;
        double curFteInt = -1.00;

        budgetConstructionIntendedIncumbent = budgetConstructionReportsServiceHelper.getBudgetConstructionIntendedIncumbent(appointmentFundingEntry);
        if (budgetConstructionIntendedIncumbent != null) {
            orgSalarySummaryReportEntry.setIuClassificationLevel(budgetConstructionIntendedIncumbent.getIuClassificationLevel());
        }
        orgSalarySummaryReportEntry.setPersonName(bcSSN.getPersonName());
        // get budgetConstructionIntendedIncumbent, budgetConstructionAdministrativePost, budgetConstructionPosition objects
        budgetConstructionAdministrativePost = budgetConstructionReportsServiceHelper.getBudgetConstructionAdministrativePost(appointmentFundingEntry);
        budgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(universityFiscalYear, appointmentFundingEntry);

        // set report body
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
                orgSalarySummaryReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(new BigDecimal(amountChange.intValue()), budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().bigDecimalValue()));
            }
        }

        if (appointmentFundingEntry != null) {
            if (appointmentFundingEntry.getFinancialSubObjectCode().equals(BCConstants.Report.BLANK_SUB_OBJECT_CODE)) {
                orgSalarySummaryReportEntry.setFinancialSubObjectCode(BCConstants.Report.BLANK);
            }
            else {
                orgSalarySummaryReportEntry.setFinancialSubObjectCode(appointmentFundingEntry.getFinancialSubObjectCode());
            }

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
                
                if (appointmentFundingEntry.getAppointmentRequestedCsfTimePercent() == null){
                    orgSalarySummaryReportEntry.setPercentAmount(BigDecimal.ZERO);
                } else {
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
                if (isSameSsnEntryForTotalOrg(totalPersonEntry.getBudgetConstructionSalarySocialSecurityNumber(), ssnEntry)) {
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


    private Collection<BudgetConstructionOrgSalarySummaryReportTotal> calculatePersonTotal(Integer universityFiscalYear, Collection<BudgetConstructionSalarySocialSecurityNumber> bcSalarySsnList, List<BudgetConstructionSalarySocialSecurityNumber> listForCalculateTotalPerson, Map salaryFundingMap) {
        Collection<BudgetConstructionOrgSalarySummaryReportTotal> returnCollection = new ArrayList();

        String positionNumber = "";
        String fiscalYearTag = "";
        Integer csfNormalMonths = 0;
        Integer csfPayMonths = 0;
        Integer csfAmount = 0;
        BigDecimal csfPercent = BigDecimal.ZERO;
        Integer salaryNormalMonths = 0;
        Integer salaryAmount = 0;
        BigDecimal salaryPercent = BigDecimal.ZERO;
        BigDecimal salaryFte = BigDecimal.ZERO;
        String tiFlag = "";
        Integer amountChange = 0;
        BigDecimal percentChange = BigDecimal.ZERO;
        //
        Integer maxSalaryAmount = 0;
        Integer salaryPayMonth = 0;
        Integer tempSalaryMonth = 0;
        Integer maxCsfAmount = 0;
        Integer resCsfAmount = 0;

        int curToInt = -1;
        double curFteInt = -1.00;


        for (BudgetConstructionSalarySocialSecurityNumber totalPersonEntry : listForCalculateTotalPerson) {
            BudgetConstructionOrgSalarySummaryReportTotal budgetConstructionOrgSalarySummaryReportTotal = new BudgetConstructionOrgSalarySummaryReportTotal();
            for (BudgetConstructionSalarySocialSecurityNumber salaryfundingEntry : bcSalarySsnList) {
                if (isSameSsnEntryForTotalPerson(totalPersonEntry, salaryfundingEntry)) {

                    Collection<BudgetConstructionSalaryFunding> salaryFundingList = (Collection) salaryFundingMap.get(totalPersonEntry);

                    for (BudgetConstructionSalaryFunding salaryFundingEntry : salaryFundingList) {

                        PendingBudgetConstructionAppointmentFunding appointmentFundingEntry = salaryFundingEntry.getPendingAppointmentFunding();
                        BudgetConstructionPosition budgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(universityFiscalYear, appointmentFundingEntry);
                        BudgetConstructionCalculatedSalaryFoundationTracker budgetConstructionCalculatedSalaryFoundationTracker = null;


                        if (appointmentFundingEntry.getAppointmentFundingDurationCode() != null && appointmentFundingEntry.getAppointmentFundingDurationCode().equals(BCConstants.Report.NONE)) {
                            salaryAmount = appointmentFundingEntry.getAppointmentRequestedAmount().intValue();
                            salaryPercent = appointmentFundingEntry.getAppointmentRequestedTimePercent();
                            tempSalaryMonth = appointmentFundingEntry.getAppointmentFundingMonth();
                        }
                        else {
                            salaryAmount = BudgetConstructionReportHelper.convertKualiInteger(appointmentFundingEntry.getAppointmentRequestedCsfAmount());
                            if (appointmentFundingEntry.getAppointmentRequestedCsfTimePercent() == null){
                                salaryPercent = BigDecimal.ZERO;
                            } else {
                                salaryPercent = appointmentFundingEntry.getAppointmentRequestedCsfTimePercent();
                            }
                            tempSalaryMonth = budgetConstructionPosition.getIuNormalWorkMonths();
                        }


                        if (salaryAmount > maxSalaryAmount) {
                            maxSalaryAmount = salaryAmount;
                            salaryPayMonth = budgetConstructionPosition.getIuPayMonths();
                            //salaryNormalMonths = appointmentFundingEntry.get;
                            salaryNormalMonths = tempSalaryMonth;
                        }
                        if (appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
                            budgetConstructionCalculatedSalaryFoundationTracker = appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0);
                            if (budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount() != null && budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue() != 0) {
                                csfAmount += budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue();
                                csfPercent = csfPercent.add(budgetConstructionCalculatedSalaryFoundationTracker.getCsfTimePercent());
                                if (budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue() > maxCsfAmount) {
                                    maxCsfAmount = budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue();
                                }
                            }
                        }
                        // data for previous year, position table has two data, one is for current year and another is for previous
                        // year.
                        Integer previousFiscalYear = new Integer(universityFiscalYear.intValue() - 1);
                        BudgetConstructionPosition previousYearBudgetConstructionPosition = budgetConstructionReportsServiceHelper.getBudgetConstructionPosition(previousFiscalYear, appointmentFundingEntry);
                        csfPayMonths = previousYearBudgetConstructionPosition.getIuPayMonths();
                        csfNormalMonths = previousYearBudgetConstructionPosition.getIuNormalWorkMonths();
                        // positioNumber is using current year position
                        positionNumber = budgetConstructionPosition.getPositionNumber();
                        fiscalYearTag = previousFiscalYear.toString() + ":";

                        if (!appointmentFundingEntry.isAppointmentFundingDeleteIndicator()) {
                            if (curToInt == -1) {
                                curToInt = appointmentFundingEntry.getAppointmentTotalIntendedAmount().intValue();
                            }
                            if (curFteInt == -1.00) {
                                curFteInt = appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity().doubleValue();
                            }
                        }

                    }
                }
            }
            // person break
            if (salaryPayMonth == 0 || csfPayMonths == 0 || csfPercent.equals(BigDecimal.ZERO) || csfNormalMonths == 0) {
                resCsfAmount = 0;
            }
            else {
                BigDecimal decimalOne = salaryPercent.multiply(new BigDecimal(salaryNormalMonths)).divide(new BigDecimal(salaryPayMonth), 2);
                BigDecimal decimalTwo = csfPercent.multiply(new BigDecimal(csfNormalMonths)).divide(new BigDecimal(csfPayMonths), 2);
                resCsfAmount = csfAmount * decimalOne.divide(decimalTwo).intValue();
            }
            if (salaryPayMonth == 0) {
                salaryFte = BigDecimal.ZERO;
            }
            else {
                // salaryFte = (salaryPercent.multiply(new BigDecimal(salaryNormalMonths)).divide(new
                // BigDecimal(salaryPayMonth))).divide(new BigDecimal(100));
                salaryFte = new BigDecimal(((salaryPercent.intValue() * salaryNormalMonths.intValue()) / salaryPayMonth) / 100);
            }
            if (salaryPayMonth != csfPayMonths) {
                if (csfPayMonths == 0) {
                    resCsfAmount = 0;
                }
                else {
                    resCsfAmount = BudgetConstructionReportHelper.setDecimalDigit(new BigDecimal(resCsfAmount).multiply((new BigDecimal(salaryPayMonth / csfPayMonths))), 0, false).intValue();
                }
            }
            csfAmount = resCsfAmount;
            amountChange = salaryAmount - csfAmount;
            if (csfAmount != 0) {
                percentChange = BudgetConstructionReportHelper.calculatePercent(amountChange, csfAmount);
            }
            if (curToInt != 0 && curToInt != -1 && curToInt != salaryAmount.intValue() || curFteInt != 0 && curFteInt != -1.00 && curFteInt != salaryFte.doubleValue()) {
                tiFlag = BCConstants.Report.PLUS;
            }
            else {
                tiFlag = BCConstants.Report.BLANK;
            }

            budgetConstructionOrgSalarySummaryReportTotal.setBudgetConstructionSalarySocialSecurityNumber(totalPersonEntry);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonPositionNumber(positionNumber);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonFiscalYearTag(fiscalYearTag);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonCsfNormalMonths(csfNormalMonths);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonCsfPayMonths(csfPayMonths);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonCsfAmount(csfAmount);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonCsfPercent(csfPercent);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonSalaryNormalMonths(salaryNormalMonths);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonSalaryAmount(salaryAmount);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonSalaryPercent(salaryPercent);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonSalaryFte(BudgetConstructionReportHelper.setDecimalDigit(salaryFte, 5, false));
            budgetConstructionOrgSalarySummaryReportTotal.setPersonTiFlag(tiFlag);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonAmountChange(amountChange);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonPercentChange(percentChange);

            returnCollection.add(budgetConstructionOrgSalarySummaryReportTotal);

            positionNumber = "";
            fiscalYearTag = "";
            csfNormalMonths = 0;
            csfPayMonths = 0;
            csfAmount = 0;
            csfPercent = BigDecimal.ZERO;
            salaryNormalMonths = 0;
            salaryAmount = 0;
            salaryPercent = BigDecimal.ZERO;
            salaryFte = BigDecimal.ZERO;
            tiFlag = "";
            amountChange = 0;
            percentChange = BigDecimal.ZERO;
            
            curToInt = -1;
            curFteInt = -1.00;
        }
        return returnCollection;
    }

    // private Collection<BudgetConstructionOrgSalarySummaryReportTotal> calculatePersonTotal(Integer universityFiscalYear,
    // Collection<BudgetConstructionSalarySocialSecurityNumber> bcSalarySsnList, List<BudgetConstructionSalarySocialSecurityNumber>
    // listForCalculateTotalPerson, Map positionMap, Map salaryFundingMap) {
    private Collection<BudgetConstructionOrgSalarySummaryReportTotal> calculateOrgTotal(Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalPerson, List<BudgetConstructionSalarySocialSecurityNumber> listForCalculateTotalOrg, Map salaryFundingMap) {
        Collection<BudgetConstructionOrgSalarySummaryReportTotal> returnCollection = new ArrayList();

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

        for (BudgetConstructionSalarySocialSecurityNumber totalOrgEntry : listForCalculateTotalOrg) {
            BudgetConstructionOrgSalarySummaryReportTotal budgetConstructionOrgSalarySummaryReportTotal = new BudgetConstructionOrgSalarySummaryReportTotal();
            for (BudgetConstructionOrgSalarySummaryReportTotal reportTotalPersonEntry : salarySummaryTotalPerson) {
                if (isSameSsnEntryForTotalOrg(totalOrgEntry, reportTotalPersonEntry.getBudgetConstructionSalarySocialSecurityNumber())) {
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
            if (newFte.intValue() != 0) {
                newAverageAmount = BudgetConstructionReportHelper.setDecimalDigit(new BigDecimal(newTotalAmount / new Integer(newFte.intValue())), 0, false).intValue();
            }
            if (conFte.intValue() != 0) {
                conAverageBaseAmount = BudgetConstructionReportHelper.setDecimalDigit(new BigDecimal(conTotalBaseAmount / new Integer(conFte.intValue())), 0, false).intValue();
                conAverageRequestAmount = BudgetConstructionReportHelper.setDecimalDigit(new BigDecimal(conTotalRequestAmount / new Integer(conFte.intValue())), 0, false).intValue();
            }
            conAveragechange = conAverageRequestAmount - conAverageBaseAmount;
            if (conAverageBaseAmount != 0) {
                conPercentChange = BudgetConstructionReportHelper.calculatePercent(conAveragechange, conAverageBaseAmount);
            }
            budgetConstructionOrgSalarySummaryReportTotal.setBudgetConstructionSalarySocialSecurityNumber(totalOrgEntry);
            budgetConstructionOrgSalarySummaryReportTotal.setNewFte(newFte);
            budgetConstructionOrgSalarySummaryReportTotal.setNewTotalAmount(newTotalAmount);
            budgetConstructionOrgSalarySummaryReportTotal.setConTotalBaseAmount(conTotalBaseAmount);
            budgetConstructionOrgSalarySummaryReportTotal.setConFte(conFte);
            budgetConstructionOrgSalarySummaryReportTotal.setConTotalRequestAmount(conTotalRequestAmount);
            budgetConstructionOrgSalarySummaryReportTotal.setNewAverageAmount(newAverageAmount);
            budgetConstructionOrgSalarySummaryReportTotal.setConAverageBaseAmount(conAverageBaseAmount);
            budgetConstructionOrgSalarySummaryReportTotal.setConAverageRequestAmount(conAverageRequestAmount);
            budgetConstructionOrgSalarySummaryReportTotal.setConAveragechange(conAveragechange);
            budgetConstructionOrgSalarySummaryReportTotal.setConPercentChange(conPercentChange);

            returnCollection.add(budgetConstructionOrgSalarySummaryReportTotal);

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
    public List<String> buildOrderByListForSalaryFunding() {
        List<String> returnList = new ArrayList();
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
        List<String> returnList = new ArrayList();
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
    private List deleteDuplicated(List list, int mode) {
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


    private boolean isSameSsnEntryForTotalPerson(BudgetConstructionSalarySocialSecurityNumber firstSsn, BudgetConstructionSalarySocialSecurityNumber secondSsn) {
        if (firstSsn.getOrganizationChartOfAccountsCode().equals(secondSsn.getOrganizationChartOfAccountsCode()) && firstSsn.getOrganizationCode().equals(secondSsn.getOrganizationCode()) && firstSsn.getEmplid().equals(secondSsn.getEmplid())) {
            return true;
        }
        else
            return false;
    }

    private boolean isSameSsnEntryForTotalOrg(BudgetConstructionSalarySocialSecurityNumber firstSsn, BudgetConstructionSalarySocialSecurityNumber secondSsn) {
        if (firstSsn.getOrganizationChartOfAccountsCode().equals(secondSsn.getOrganizationChartOfAccountsCode()) && firstSsn.getOrganizationCode().equals(secondSsn.getOrganizationCode())) {
            return true;
        }
        else
            return false;
    }

    public void setBudgetConstructionSalarySummaryReportDao(BudgetConstructionSalarySummaryReportDao budgetConstructionSalarySummaryReportDao) {
        this.budgetConstructionSalarySummaryReportDao = budgetConstructionSalarySummaryReportDao;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }
}
