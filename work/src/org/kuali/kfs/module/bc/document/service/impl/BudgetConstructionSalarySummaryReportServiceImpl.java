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
package org.kuali.module.budget.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.bo.BudgetConstructionAdministrativePost;
import org.kuali.module.budget.bo.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.module.budget.bo.BudgetConstructionIntendedIncumbent;
import org.kuali.module.budget.bo.BudgetConstructionObjectDump;
import org.kuali.module.budget.bo.BudgetConstructionObjectPick;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountFundingDetailReportTotal;
import org.kuali.module.budget.bo.BudgetConstructionOrgSalarySummaryReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgSalarySummaryReportTotal;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.BudgetConstructionReportThresholdSettings;
import org.kuali.module.budget.bo.BudgetConstructionSalaryFunding;
import org.kuali.module.budget.bo.BudgetConstructionSalarySocialSecurityNumber;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.dao.BudgetConstructionSalarySummaryReportDao;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.service.BudgetConstructionSalarySummaryReportService;
import org.kuali.module.budget.util.BudgetConstructionReportHelper;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionSalarySummaryReportServiceImpl implements BudgetConstructionSalarySummaryReportService {

    BudgetConstructionSalarySummaryReportDao budgetConstructionSalarySummaryReportDao;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    KualiConfigurationService kualiConfigurationService;
    BusinessObjectService businessObjectService;


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
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionSalaryFunding> salarySummaryList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionSalaryFunding.class, searchCriteria, orderList);

        // get BudgetConstructionSalarySocialSecurityNumber and put into map
        searchCriteria.clear();
        
        Map administrativePostMap = new HashMap();
        Map positionMap = new HashMap();
        Map budgetSsnMap = new HashMap();
        Map intendedIncumbentMap = new HashMap();
        
        for (BudgetConstructionSalaryFunding salaryFundingEntry : salarySummaryList) {
            BudgetConstructionAdministrativePost budgetConstructionAdministrativePost = getBudgetConstructionAdministrativePost(salaryFundingEntry.getPendingAppointmentFunding());
            BudgetConstructionPosition budgetConstructionPosition = getBudgetConstructionPosition(universityFiscalYear, salaryFundingEntry.getPendingAppointmentFunding());
            BudgetConstructionSalarySocialSecurityNumber budgetConstructionSalarySocialSecurityNumber = getBudgetConstructionSalarySocialSecurityNumber(personUserIdentifier, salaryFundingEntry);
            BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent = getBudgetConstructionIntendedIncumbent(salaryFundingEntry.getPendingAppointmentFunding());
            
            administrativePostMap.put(salaryFundingEntry, budgetConstructionAdministrativePost);
            positionMap.put(salaryFundingEntry, budgetConstructionPosition);
            budgetSsnMap.put(salaryFundingEntry, budgetConstructionSalarySocialSecurityNumber);
            intendedIncumbentMap.put(salaryFundingEntry, budgetConstructionIntendedIncumbent);
        }

        List<BudgetConstructionSalaryFunding> listForCalculateTotalPerson = deleteDuplicated((List) salarySummaryList, budgetSsnMap, 1);
        List<BudgetConstructionSalaryFunding> listForCalculateTotalOrg = deleteDuplicated((List) salarySummaryList, budgetSsnMap, 2);

        //Calculate Total Section
        Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalPerson = calculatePersonTotal(universityFiscalYear, salarySummaryList, listForCalculateTotalPerson, positionMap, budgetSsnMap);
        Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalOrg = calculateOrgTotal(salarySummaryTotalPerson, listForCalculateTotalOrg, budgetSsnMap);
        
        
        searchCriteria.clear();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        Collection<BudgetConstructionObjectPick> objectPickList = businessObjectService.findMatching(BudgetConstructionObjectPick.class, searchCriteria);
        String objectCodes = "";
        for (BudgetConstructionObjectPick objectPick : objectPickList) {
            objectCodes += objectPick.getFinancialObjectCode() + " ";
        }

        for (BudgetConstructionSalaryFunding salaryFundingEntry : salarySummaryList) {
            BudgetConstructionSalarySocialSecurityNumber budgetSsnEntry = (BudgetConstructionSalarySocialSecurityNumber) budgetSsnMap.get(salaryFundingEntry);
            orgSalarySummaryReportEntry = new BudgetConstructionOrgSalarySummaryReport();
            buildReportsHeader(universityFiscalYear, objectCodes, orgSalarySummaryReportEntry, salaryFundingEntry, budgetSsnEntry, budgetConstructionReportThresholdSettings);
            buildReportsBody(universityFiscalYear, orgSalarySummaryReportEntry, salaryFundingEntry, budgetSsnEntry);
            buildReportsTotal(orgSalarySummaryReportEntry, salaryFundingEntry, salarySummaryTotalPerson, salarySummaryTotalOrg, budgetSsnMap);
            reportSet.add(orgSalarySummaryReportEntry);

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
        
        if(budgetConstructionReportThresholdSettings.isUseThreshold()){
            if(budgetConstructionReportThresholdSettings.isUseGreaterThanOperator()){
                orgSalarySummaryReportEntry.setThreshold(BCConstants.Report.THRESHOLD + BCConstants.Report.THRESHOLD_GREATER + budgetConstructionReportThresholdSettings.getThresholdPercent().toString() + BCConstants.Report.PERCENT);
            } else {
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

        budgetConstructionIntendedIncumbent = getBudgetConstructionIntendedIncumbent(appointmentFundingEntry);
        if (budgetConstructionIntendedIncumbent != null) {
            orgSalarySummaryReportEntry.setIuClassificationLevel(budgetConstructionIntendedIncumbent.getIuClassificationLevel());
        }
        orgSalarySummaryReportEntry.setPersonName(bcSSN.getPersonName());
        // get budgetConstructionIntendedIncumbent, budgetConstructionAdministrativePost, budgetConstructionPosition objects
        budgetConstructionAdministrativePost = getBudgetConstructionAdministrativePost(appointmentFundingEntry);
        budgetConstructionPosition = getBudgetConstructionPosition(universityFiscalYear, appointmentFundingEntry);

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
            orgSalarySummaryReportEntry.setPositionFte(budgetConstructionPosition.getPositionFullTimeEquivalency().setScale(5, 5).toString());
            orgSalarySummaryReportEntry.setPositionSalaryPlanDefault(budgetConstructionPosition.getPositionSalaryPlanDefault());
            orgSalarySummaryReportEntry.setPositionGradeDefault(budgetConstructionPosition.getPositionGradeDefault());
        }
        if (appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
            budgetConstructionCalculatedSalaryFoundationTracker = appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0);
            orgSalarySummaryReportEntry.setCsfTimePercent(budgetConstructionCalculatedSalaryFoundationTracker.getCsfTimePercent().setScale(2, 2));
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
            orgSalarySummaryReportEntry.setAppointmentTotalIntendedAmount(new Integer(appointmentFundingEntry.getAppointmentTotalIntendedAmount().intValue()));
            orgSalarySummaryReportEntry.setAppointmentTotalIntendedFteQuantity(appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity().setScale(5, 5).toString());
            if (appointmentFundingEntry.getAppointmentFundingDurationCode().equals(BCConstants.Report.NONE)) {

                orgSalarySummaryReportEntry.setSalaryAmount(new Integer(appointmentFundingEntry.getAppointmentRequestedAmount().intValue()));
                orgSalarySummaryReportEntry.setPercentAmount(appointmentFundingEntry.getAppointmentRequestedTimePercent());
                orgSalarySummaryReportEntry.setSalaryMonths(appointmentFundingEntry.getAppointmentFundingMonth());

            }
            else {
                orgSalarySummaryReportEntry.setSalaryAmount(new Integer(appointmentFundingEntry.getAppointmentRequestedCsfAmount().intValue()));
                orgSalarySummaryReportEntry.setPercentAmount(appointmentFundingEntry.getAppointmentRequestedCsfTimePercent());

                if (budgetConstructionPosition != null) {
                    orgSalarySummaryReportEntry.setSalaryMonths(budgetConstructionPosition.getIuNormalWorkMonths());
                }
            }

            // group
            orgSalarySummaryReportEntry.setEmplid(appointmentFundingEntry.getEmplid());
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
    
    
    
    
    
    
    
    
    public void buildReportsTotal(BudgetConstructionOrgSalarySummaryReport orgSalarySummaryReportEntry, BudgetConstructionSalaryFunding salaryFundingEntry, Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalPerson, Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalOrg, Map budgetSsnMap) {
        
        for (BudgetConstructionOrgSalarySummaryReportTotal totalPersonEntry : salarySummaryTotalPerson){
            if (isSameSalaryFundingEntryForTotalPerson(totalPersonEntry.getBudgetConstructionSalaryFunding(), salaryFundingEntry, budgetSsnMap)){
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
            for (BudgetConstructionOrgSalarySummaryReportTotal totalOrgEntry : salarySummaryTotalOrg){
                if (isSameSalaryFundingEntryForTotalOrg(totalPersonEntry.getBudgetConstructionSalaryFunding(), salaryFundingEntry, budgetSsnMap)){
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
    
    
    private Collection<BudgetConstructionOrgSalarySummaryReportTotal> calculatePersonTotal(Integer universityFiscalYear, Collection<BudgetConstructionSalaryFunding> salarySummaryList, List<BudgetConstructionSalaryFunding> listForCalculateTotalPerson, Map positionMap, Map budgetSsnMap) {
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
        
        
        for (BudgetConstructionSalaryFunding totalPersonEntry: listForCalculateTotalPerson){
            BudgetConstructionOrgSalarySummaryReportTotal budgetConstructionOrgSalarySummaryReportTotal = new BudgetConstructionOrgSalarySummaryReportTotal();
            for(BudgetConstructionSalaryFunding salaryfundingEntry: salarySummaryList) {
                if(isSameSalaryFundingEntryForTotalPerson(totalPersonEntry, salaryfundingEntry, budgetSsnMap)){
                    PendingBudgetConstructionAppointmentFunding appointmentFundingEntry = salaryfundingEntry.getPendingAppointmentFunding();
                    BudgetConstructionPosition budgetConstructionPosition = (BudgetConstructionPosition) positionMap.get(salaryfundingEntry);
                    BudgetConstructionCalculatedSalaryFoundationTracker budgetConstructionCalculatedSalaryFoundationTracker = null;
                    
                    if (appointmentFundingEntry.getAppointmentFundingDurationCode().equals(BCConstants.Report.NONE)) {
                        salaryAmount += appointmentFundingEntry.getAppointmentRequestedAmount().intValue();
                        salaryPercent = salaryPercent.add(appointmentFundingEntry.getAppointmentRequestedTimePercent());
                        tempSalaryMonth = appointmentFundingEntry.getAppointmentFundingMonth();
                    }
                    else {
                        salaryAmount += appointmentFundingEntry.getAppointmentRequestedCsfAmount().intValue();
                        salaryPercent = salaryPercent.add(appointmentFundingEntry.getAppointmentRequestedCsfTimePercent());
                        tempSalaryMonth = budgetConstructionPosition.getIuNormalWorkMonths();
                    }
                    if (salaryAmount > maxSalaryAmount){
                        maxSalaryAmount = salaryAmount;
                        salaryPayMonth = budgetConstructionPosition.getIuPayMonths();
                        salaryNormalMonths = tempSalaryMonth;
                    }
                    if (appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
                        budgetConstructionCalculatedSalaryFoundationTracker = appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0);
                        if (budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount() == null && budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue() != 0 ){
                            csfAmount += budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue();
                            csfPercent = csfPercent.add(budgetConstructionCalculatedSalaryFoundationTracker.getCsfTimePercent());
                            if (budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue() > maxCsfAmount){
                                maxCsfAmount = budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue();
                            }
                        }
                    }
                    //data for previous year, position table has two data, one is for current year and another is for previous year. 
                    Integer previousFiscalYear = new Integer(universityFiscalYear.intValue() - 1);
                    BudgetConstructionPosition previousYearBudgetConstructionPosition = getBudgetConstructionPosition(previousFiscalYear, appointmentFundingEntry);
                    csfPayMonths = previousYearBudgetConstructionPosition.getIuPayMonths();
                    csfNormalMonths = previousYearBudgetConstructionPosition.getIuNormalWorkMonths();
                    //positioNumber is using current year position
                    positionNumber = budgetConstructionPosition.getPositionNumber();
                    fiscalYearTag = previousFiscalYear.toString() + ":";
                    
                    if (appointmentFundingEntry.isAppointmentFundingDeleteIndicator()) {
                        if (curToInt == -1) {
                            curToInt = appointmentFundingEntry.getAppointmentTotalIntendedAmount().intValue();
                        }
                        if (curFteInt == -1.00) {
                            curFteInt = appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity().doubleValue();
                        }
                    }
                }
            }
            // person break
            if (salaryPayMonth == 0 || csfPayMonths == 0  || csfPercent.equals(BigDecimal.ZERO) || csfNormalMonths == 0){
                resCsfAmount = 0;
            } else {
                BigDecimal decimalOne = salaryPercent.multiply(new BigDecimal(salaryNormalMonths)).divide(new BigDecimal(salaryPayMonth));
                BigDecimal decimalTwo = csfPercent.multiply(new BigDecimal(csfNormalMonths)).divide(new BigDecimal(csfPayMonths));
                resCsfAmount = csfAmount * decimalOne.divide(decimalTwo).intValue(); 
            }
            if (salaryPayMonth ==0){
                salaryFte = BigDecimal.ZERO;
            } else {
                salaryFte = (salaryPercent.multiply(new BigDecimal(salaryNormalMonths)).divide(new BigDecimal(salaryPayMonth))).divide(new BigDecimal(100));
            }
            if (salaryPayMonth != csfPayMonths) {
                if(csfPayMonths == 0){
                    resCsfAmount = 0;
                } else {
                    resCsfAmount = BudgetConstructionReportHelper.setDecimalDigit(new BigDecimal(resCsfAmount).multiply((new BigDecimal(salaryPayMonth / csfPayMonths))), 0).intValue();
                }
            }
            csfAmount = resCsfAmount;
            amountChange = salaryAmount - csfAmount;
            if (csfAmount != 0){
                percentChange = BudgetConstructionReportHelper.setDecimalDigit(new BigDecimal((amountChange / csfAmount) * 100), 0);
            }
            if (curToInt != 0 && curToInt != -1 && curToInt != salaryAmount.intValue() || curFteInt != 0 && curFteInt != -1.00 && curFteInt != salaryFte.doubleValue()){
                tiFlag = BCConstants.Report.PLUS;
            } else {
                tiFlag = BCConstants.Report.BLANK;
            }

            budgetConstructionOrgSalarySummaryReportTotal.setBudgetConstructionSalaryFunding(totalPersonEntry);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonPositionNumber(positionNumber); 
            budgetConstructionOrgSalarySummaryReportTotal.setPersonFiscalYearTag(fiscalYearTag);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonCsfNormalMonths(csfNormalMonths);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonCsfPayMonths(csfPayMonths);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonCsfAmount(csfAmount);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonCsfPercent(csfPercent);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonSalaryNormalMonths(salaryNormalMonths);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonSalaryAmount(salaryAmount);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonSalaryPercent(salaryPercent);
            budgetConstructionOrgSalarySummaryReportTotal.setPersonSalaryFte(BudgetConstructionReportHelper.setDecimalDigit(salaryFte, 5));
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
        }
        return returnCollection;
    }
    
    
    private Collection<BudgetConstructionOrgSalarySummaryReportTotal> calculateOrgTotal(Collection<BudgetConstructionOrgSalarySummaryReportTotal> salarySummaryTotalPerson, List<BudgetConstructionSalaryFunding> listForCalculateTotalOrg, Map budgetSsnMap) {
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
        
        for (BudgetConstructionSalaryFunding totalOrgEntry : listForCalculateTotalOrg){
            BudgetConstructionOrgSalarySummaryReportTotal budgetConstructionOrgSalarySummaryReportTotal = new BudgetConstructionOrgSalarySummaryReportTotal();
            for (BudgetConstructionOrgSalarySummaryReportTotal reportTotalPersonEntry : salarySummaryTotalPerson){
                if(isSameSalaryFundingEntryForTotalOrg(totalOrgEntry, reportTotalPersonEntry.getBudgetConstructionSalaryFunding(), budgetSsnMap)){
                    if (reportTotalPersonEntry.getPersonCsfAmount() == 0){
                        newFte = newFte.add(reportTotalPersonEntry.getPersonSalaryFte());
                        newTotalAmount += reportTotalPersonEntry.getPersonSalaryAmount();
                    } else {
                        conTotalBaseAmount += reportTotalPersonEntry.getPersonCsfAmount();
                        conFte = conFte.add(reportTotalPersonEntry.getPersonSalaryFte());
                        conTotalRequestAmount += reportTotalPersonEntry.getPersonSalaryAmount();
                    }
                }
            }
            //calculate average and change
            if(!newFte.equals(BigDecimal.ZERO)) {
                newAverageAmount = BudgetConstructionReportHelper.setDecimalDigit(new BigDecimal(newTotalAmount / newFte.intValue()), 0).intValue();
            }
            if (!conFte.equals(BigDecimal.ZERO)) {
                conAverageBaseAmount = BudgetConstructionReportHelper.setDecimalDigit(new BigDecimal(conTotalBaseAmount / conFte.intValue()), 0).intValue();
                conAverageRequestAmount = BudgetConstructionReportHelper.setDecimalDigit(new BigDecimal(conTotalRequestAmount / conFte.intValue()), 0).intValue();
            }
            conAveragechange = conAverageRequestAmount - conAverageBaseAmount;
            if (conAverageBaseAmount != 0) {
                conPercentChange = BudgetConstructionReportHelper.setDecimalDigit(new BigDecimal((conAveragechange / conAverageBaseAmount) * 100), 1);
            }
            budgetConstructionOrgSalarySummaryReportTotal.setBudgetConstructionSalaryFunding(totalOrgEntry);
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
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        /*
         * returnList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
         * returnList.add(KFSPropertyConstants.ORGANIZATION_CODE); returnList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
         * returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE); returnList.add(KFSPropertyConstants.INCOME_EXPENSE_CODE);
         * returnList.add(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_SORT_CODE);
         * returnList.add(KFSPropertyConstants.FINANCIAL_LEVEL_SORT_CODE);
         */
        returnList.add(KFSPropertyConstants.EMPLID);
        return returnList;
    }


    /**
     * Deletes duplicated entry from list
     * 
     * @param List list
     * @return a list that all duplicated entries were deleted
     */
    private List deleteDuplicated(List list, Map map, int mode) {
        // mode 1 is for getting a list of total object
        // mode 2 is for getting a list of total account
        int count = 0;
        BudgetConstructionSalaryFunding salaryFundingEntry = null;
        BudgetConstructionSalaryFunding salaryFundingEntryAux = null;
        List returnList = new ArrayList();
        if ((list != null) && (list.size() > 0)) {
            salaryFundingEntry = (BudgetConstructionSalaryFunding) list.get(count);
            salaryFundingEntryAux = (BudgetConstructionSalaryFunding) list.get(count);
            returnList.add(salaryFundingEntry);
            count++;
            while (count < list.size()) {
                salaryFundingEntry = (BudgetConstructionSalaryFunding) list.get(count);
                switch (mode) {
                    case 1: {
                        if (!isSameSalaryFundingEntryForTotalPerson(salaryFundingEntry, salaryFundingEntryAux, map)) {
                            returnList.add(salaryFundingEntry);
                            salaryFundingEntryAux = salaryFundingEntry;
                        }
                    }
                    case 2: {
                        if (!isSameSalaryFundingEntryForTotalOrg(salaryFundingEntry, salaryFundingEntryAux, map)) {
                            returnList.add(salaryFundingEntry);
                            salaryFundingEntryAux = salaryFundingEntry;
                        }
                    }
                }
                count++;
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
        else
            return false;
    }

    private boolean isSameSalaryFundingEntryForTotalOrg(BudgetConstructionSalaryFunding firstbcsf, BudgetConstructionSalaryFunding secondbcsf, Map map) {
        BudgetConstructionSalarySocialSecurityNumber firstBcssn = (BudgetConstructionSalarySocialSecurityNumber) map.get(firstbcsf);
        BudgetConstructionSalarySocialSecurityNumber secondBcssn = (BudgetConstructionSalarySocialSecurityNumber) map.get(secondbcsf);
        if (firstBcssn.getOrganizationChartOfAccountsCode().equals(secondBcssn.getOrganizationChartOfAccountsCode()) && firstBcssn.getOrganizationCode().equals(secondBcssn.getOrganizationCode())) {
            return true;
        }
        else
            return false;
    }

    private BudgetConstructionSalarySocialSecurityNumber getBudgetConstructionSalarySocialSecurityNumber(String personUserIdentifier, BudgetConstructionSalaryFunding salaryFunding){
        
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        searchCriteria.put(KFSPropertyConstants.EMPLID, salaryFunding.getEmplid());
        return (BudgetConstructionSalarySocialSecurityNumber) businessObjectService.findByPrimaryKey(BudgetConstructionSalarySocialSecurityNumber.class, searchCriteria);
    }

    private BudgetConstructionIntendedIncumbent getBudgetConstructionIntendedIncumbent(PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.EMPLID, appointmentFundingEntry.getEmplid());
        return (BudgetConstructionIntendedIncumbent) businessObjectService.findByPrimaryKey(BudgetConstructionIntendedIncumbent.class, searchCriteria);
    }

    private BudgetConstructionAdministrativePost getBudgetConstructionAdministrativePost(PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.EMPLID, appointmentFundingEntry.getEmplid());
        searchCriteria.put(KFSPropertyConstants.POSITION_NUMBER, appointmentFundingEntry.getPositionNumber());
        return (BudgetConstructionAdministrativePost) businessObjectService.findByPrimaryKey(BudgetConstructionAdministrativePost.class, searchCriteria);
    }

    private BudgetConstructionPosition getBudgetConstructionPosition(Integer universityFiscalYear, PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.POSITION_NUMBER, appointmentFundingEntry.getPositionNumber());
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        return (BudgetConstructionPosition) businessObjectService.findByPrimaryKey(BudgetConstructionPosition.class, searchCriteria);
    }


    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }


    public void setBudgetConstructionSalarySummaryReportDao(BudgetConstructionSalarySummaryReportDao budgetConstructionSalarySummaryReportDao) {
        this.budgetConstructionSalarySummaryReportDao = budgetConstructionSalarySummaryReportDao;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}
