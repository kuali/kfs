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
import org.kuali.module.budget.bo.BudgetConstructionObjectPick;
import org.kuali.module.budget.bo.BudgetConstructionOrgReasonSummaryReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgReasonSummaryReportTotal;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.BudgetConstructionReasonCodePick;
import org.kuali.module.budget.bo.BudgetConstructionReportThresholdSettings;
import org.kuali.module.budget.bo.BudgetConstructionSalaryFunding;
import org.kuali.module.budget.bo.BudgetConstructionSalarySocialSecurityNumber;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.dao.BudgetConstructionSalarySummaryReportDao;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.service.BudgetConstructionReasonSummaryReportService;
import org.kuali.module.budget.util.BudgetConstructionReportHelper;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionReasonSummaryReportServiceImpl implements BudgetConstructionReasonSummaryReportService {

    BudgetConstructionSalarySummaryReportDao budgetConstructionSalarySummaryReportDao;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    KualiConfigurationService kualiConfigurationService;
    BusinessObjectService businessObjectService;


    public void updateReasonSummaryReport(String personUserIdentifier, Integer universityFiscalYear, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {

        boolean applyAThreshold = budgetConstructionReportThresholdSettings.isUseThreshold();
        boolean selectOnlyGreaterThanOrEqualToThreshold = budgetConstructionReportThresholdSettings.isUseGreaterThanOperator();
        KualiDecimal thresholdPercent = budgetConstructionReportThresholdSettings.getThresholdPercent();
        if (applyAThreshold) {
            budgetConstructionSalarySummaryReportDao.updateSalaryAndReasonSummaryReportsWithThreshold(personUserIdentifier, universityFiscalYear, selectOnlyGreaterThanOrEqualToThreshold, thresholdPercent);
        }
        else {
            budgetConstructionSalarySummaryReportDao.updateSalaryAndReasonSummaryReportsWithoutThreshold(personUserIdentifier, true);
        }

    }

    public Collection<BudgetConstructionOrgReasonSummaryReport> buildReports(Integer universityFiscalYear, String personUserIdentifier, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        Collection<BudgetConstructionOrgReasonSummaryReport> reportSet = new ArrayList();

        BudgetConstructionOrgReasonSummaryReport orgReasonSummaryReportEntry;
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionSalaryFunding> reasonSummaryList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionSalaryFunding.class, searchCriteria, orderList);

        // get BudgetConstructionSalarySocialSecurityNumber and put into map
        searchCriteria.clear();
        
        Map administrativePostMap = new HashMap();
        Map positionMap = new HashMap();
        Map budgetSsnMap = new HashMap();
        Map intendedIncumbentMap = new HashMap();
        
        for (BudgetConstructionSalaryFunding salaryFundingEntry : reasonSummaryList) {
            BudgetConstructionAdministrativePost budgetConstructionAdministrativePost = getBudgetConstructionAdministrativePost(salaryFundingEntry.getPendingAppointmentFunding());
            BudgetConstructionPosition budgetConstructionPosition = getBudgetConstructionPosition(universityFiscalYear, salaryFundingEntry.getPendingAppointmentFunding());
            BudgetConstructionSalarySocialSecurityNumber budgetConstructionSalarySocialSecurityNumber = getBudgetConstructionSalarySocialSecurityNumber(personUserIdentifier, salaryFundingEntry);
            BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent = getBudgetConstructionIntendedIncumbent(salaryFundingEntry.getPendingAppointmentFunding());
            
            administrativePostMap.put(salaryFundingEntry, budgetConstructionAdministrativePost);
            positionMap.put(salaryFundingEntry, budgetConstructionPosition);
            budgetSsnMap.put(salaryFundingEntry, budgetConstructionSalarySocialSecurityNumber);
            intendedIncumbentMap.put(salaryFundingEntry, budgetConstructionIntendedIncumbent);
        }

        List<BudgetConstructionSalaryFunding> listForCalculateTotalPerson = deleteDuplicated((List) reasonSummaryList, budgetSsnMap, 1);
        List<BudgetConstructionSalaryFunding> listForCalculateTotalOrg = deleteDuplicated((List) reasonSummaryList, budgetSsnMap, 2);

        //Calculate Total Section
        Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalPerson = calculatePersonTotal(universityFiscalYear, reasonSummaryList, listForCalculateTotalPerson, positionMap, budgetSsnMap);
        Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalOrg = calculateOrgTotal(reasonSummaryTotalPerson, listForCalculateTotalOrg, budgetSsnMap);
        
        // object codes --> helper?
        searchCriteria.clear();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        Collection<BudgetConstructionObjectPick> objectPickList = businessObjectService.findMatching(BudgetConstructionObjectPick.class, searchCriteria);
        String objectCodes = "";
        for (BudgetConstructionObjectPick objectPick : objectPickList) {
            objectCodes += objectPick.getFinancialObjectCode() + " ";
        }
        
        // get reason codes  --> helper class?
        searchCriteria.clear();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        Collection<BudgetConstructionObjectPick> reasonCodePickList = businessObjectService.findMatching(BudgetConstructionReasonCodePick.class, searchCriteria);
        String reasonCodes = "";
        for (BudgetConstructionObjectPick reasonCode : reasonCodePickList) {
            reasonCodes += reasonCode.getFinancialObjectCode() + " ";
        }

        for (BudgetConstructionSalaryFunding salaryFundingEntry : reasonSummaryList) {
            BudgetConstructionSalarySocialSecurityNumber budgetSsnEntry = (BudgetConstructionSalarySocialSecurityNumber) budgetSsnMap.get(salaryFundingEntry);
            orgReasonSummaryReportEntry = new BudgetConstructionOrgReasonSummaryReport();
            buildReportsHeader(universityFiscalYear, objectCodes, reasonCodes, orgReasonSummaryReportEntry, salaryFundingEntry, budgetSsnEntry, budgetConstructionReportThresholdSettings);
            buildReportsBody(universityFiscalYear, orgReasonSummaryReportEntry, salaryFundingEntry, budgetSsnEntry);
            buildReportsTotal(orgReasonSummaryReportEntry, salaryFundingEntry, reasonSummaryTotalPerson, reasonSummaryTotalOrg, budgetSsnMap);
            reportSet.add(orgReasonSummaryReportEntry);

        }
        return reportSet;
    }


    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsHeader(Integer universityFiscalYear, String objectCodes, String reasonCodes, BudgetConstructionOrgReasonSummaryReport orgReasonSummaryReportEntry, BudgetConstructionSalaryFunding salaryFundingEntry, BudgetConstructionSalarySocialSecurityNumber bcSSN, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        String chartDesc = salaryFundingEntry.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = bcSSN.getOrganization().getOrganizationName();


        Integer prevFiscalyear = universityFiscalYear - 1;
        orgReasonSummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));

        orgReasonSummaryReportEntry.setOrganizationCode(bcSSN.getOrganizationCode());
        if (orgName == null) {
            orgReasonSummaryReportEntry.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgReasonSummaryReportEntry.setOrganizationName(orgName);
        }

        orgReasonSummaryReportEntry.setChartOfAccountsCode(salaryFundingEntry.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgReasonSummaryReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgReasonSummaryReportEntry.setChartOfAccountDescription(chartDesc);
        }


        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgReasonSummaryReportEntry.setReqFy(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgReasonSummaryReportEntry.setFinancialObjectCode(salaryFundingEntry.getFinancialObjectCode());

        orgReasonSummaryReportEntry.setObjectCodes(objectCodes);
        
        if(budgetConstructionReportThresholdSettings.isUseThreshold()){
            if(budgetConstructionReportThresholdSettings.isUseGreaterThanOperator()){
                orgReasonSummaryReportEntry.setThresholdOrReason(BCConstants.Report.THRESHOLD + BCConstants.Report.THRESHOLD_GREATER + budgetConstructionReportThresholdSettings.getThresholdPercent().toString() + BCConstants.Report.PERCENT);
            } else {
                orgReasonSummaryReportEntry.setThresholdOrReason(BCConstants.Report.THRESHOLD + BCConstants.Report.THRESHOLD_LESS + budgetConstructionReportThresholdSettings.getThresholdPercent().toString() + BCConstants.Report.PERCENT);
            }
        } else {
            orgReasonSummaryReportEntry.setThresholdOrReason(BCConstants.Report.SELECTED_REASONS + reasonCodes);
        }
        
        
    }


    /**
     * builds report body
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgReasonSummaryReport orgReasonSummaryReportEntry, BudgetConstructionSalaryFunding salaryFundingEntry, BudgetConstructionSalarySocialSecurityNumber bcSSN) {
        BudgetConstructionAdministrativePost budgetConstructionAdministrativePost;
        BudgetConstructionPosition budgetConstructionPosition;
        BudgetConstructionCalculatedSalaryFoundationTracker budgetConstructionCalculatedSalaryFoundationTracker;
        PendingBudgetConstructionAppointmentFunding appointmentFundingEntry = salaryFundingEntry.getPendingAppointmentFunding();
        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent;
        int curToInt = -1;
        double curFteInt = -1.00;

        budgetConstructionIntendedIncumbent = getBudgetConstructionIntendedIncumbent(appointmentFundingEntry);
        if (budgetConstructionIntendedIncumbent != null) {
            orgReasonSummaryReportEntry.setIuClassificationLevel(budgetConstructionIntendedIncumbent.getIuClassificationLevel());
        }
        orgReasonSummaryReportEntry.setPersonName(bcSSN.getPersonName());
        // get budgetConstructionIntendedIncumbent, budgetConstructionAdministrativePost, budgetConstructionPosition objects
        budgetConstructionAdministrativePost = getBudgetConstructionAdministrativePost(appointmentFundingEntry);
        budgetConstructionPosition = getBudgetConstructionPosition(universityFiscalYear, appointmentFundingEntry);

        // set report body
        orgReasonSummaryReportEntry.setAccountNumber(salaryFundingEntry.getAccountNumber());
        orgReasonSummaryReportEntry.setSubAccountNumber(salaryFundingEntry.getSubAccountNumber());
        orgReasonSummaryReportEntry.setFinancialSubObjectCode(salaryFundingEntry.getFinancialSubObjectCode());

        if (budgetConstructionAdministrativePost != null) {
            orgReasonSummaryReportEntry.setAdministrativePost(budgetConstructionAdministrativePost.getAdministrativePost());
        }

        if (budgetConstructionPosition != null) {
            orgReasonSummaryReportEntry.setPositionNumber(budgetConstructionPosition.getPositionNumber());
            orgReasonSummaryReportEntry.setNormalWorkMonthsAndiuPayMonths(budgetConstructionPosition.getIuNormalWorkMonths() + "/" + budgetConstructionPosition.getIuPayMonths());
            orgReasonSummaryReportEntry.setPositionFte(budgetConstructionPosition.getPositionFullTimeEquivalency().setScale(5, 5).toString());
            orgReasonSummaryReportEntry.setPositionSalaryPlanDefault(budgetConstructionPosition.getPositionSalaryPlanDefault());
            orgReasonSummaryReportEntry.setPositionGradeDefault(budgetConstructionPosition.getPositionGradeDefault());
        }
        if (appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
            budgetConstructionCalculatedSalaryFoundationTracker = appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0);
            orgReasonSummaryReportEntry.setCsfTimePercent(budgetConstructionCalculatedSalaryFoundationTracker.getCsfTimePercent().setScale(2, 2));
            orgReasonSummaryReportEntry.setCsfAmount(new Integer(budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue()));

            // calculate amountChange and percentChange
            if (appointmentFundingEntry.getAppointmentRequestedFteQuantity().equals(budgetConstructionCalculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity())) {
                Integer amountChange = appointmentFundingEntry.getAppointmentRequestedAmount().subtract(budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount()).intValue();
                orgReasonSummaryReportEntry.setAmountChange(amountChange);
                orgReasonSummaryReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(new BigDecimal(amountChange.intValue()), budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().bigDecimalValue()));
            }
        }

        if (appointmentFundingEntry != null) {
            if (appointmentFundingEntry.getFinancialSubObjectCode().equals(BCConstants.Report.BLANK_SUB_OBJECT_CODE)) {
                orgReasonSummaryReportEntry.setFinancialSubObjectCode(BCConstants.Report.BLANK);
            }
            else {
                orgReasonSummaryReportEntry.setFinancialSubObjectCode(appointmentFundingEntry.getFinancialSubObjectCode());
            }

            orgReasonSummaryReportEntry.setAppointmentFundingDurationCode(appointmentFundingEntry.getAppointmentFundingDurationCode());
            orgReasonSummaryReportEntry.setAppointmentTotalIntendedAmount(new Integer(appointmentFundingEntry.getAppointmentTotalIntendedAmount().intValue()));
            orgReasonSummaryReportEntry.setAppointmentTotalIntendedFteQuantity(appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity().setScale(5, 5).toString());
            if (appointmentFundingEntry.getAppointmentFundingDurationCode().equals(BCConstants.Report.NONE)) {

                orgReasonSummaryReportEntry.setSalaryAmount(new Integer(appointmentFundingEntry.getAppointmentRequestedAmount().intValue()));
                orgReasonSummaryReportEntry.setPercentAmount(appointmentFundingEntry.getAppointmentRequestedTimePercent());
                orgReasonSummaryReportEntry.setSalaryMonths(appointmentFundingEntry.getAppointmentFundingMonth());

            }
            else {
                orgReasonSummaryReportEntry.setSalaryAmount(new Integer(appointmentFundingEntry.getAppointmentRequestedCsfAmount().intValue()));
                orgReasonSummaryReportEntry.setPercentAmount(appointmentFundingEntry.getAppointmentRequestedCsfTimePercent());

                if (budgetConstructionPosition != null) {
                    orgReasonSummaryReportEntry.setSalaryMonths(budgetConstructionPosition.getIuNormalWorkMonths());
                }
            }

            // group
            orgReasonSummaryReportEntry.setEmplid(appointmentFundingEntry.getEmplid());
        }


        if (appointmentFundingEntry.isAppointmentFundingDeleteIndicator()) {
            orgReasonSummaryReportEntry.setDeleteBox(BCConstants.Report.DELETE_MARK);
        }
        else {
            orgReasonSummaryReportEntry.setDeleteBox(BCConstants.Report.BLANK);
        }

        // set tiFlag
        if (appointmentFundingEntry.isAppointmentFundingDeleteIndicator()) {
            if (curToInt == -1) {
                curToInt = appointmentFundingEntry.getAppointmentTotalIntendedAmount().intValue();
            }
            else if (curToInt != appointmentFundingEntry.getAppointmentTotalIntendedAmount().intValue()) {
                orgReasonSummaryReportEntry.setTiFlag(BCConstants.Report.PLUS);
            }
            if (curFteInt == -1.00) {
                curFteInt = appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity().doubleValue();
            }
            else if (curFteInt != appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity().doubleValue()) {
                orgReasonSummaryReportEntry.setTiFlag(BCConstants.Report.PLUS);
            }
        }
    }
    
    
    
    
    
    
    
    
    public void buildReportsTotal(BudgetConstructionOrgReasonSummaryReport orgReasonSummaryReportEntry, BudgetConstructionSalaryFunding salaryFundingEntry, Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalPerson, Collection<BudgetConstructionOrgReasonSummaryReportTotal> reasonSummaryTotalOrg, Map budgetSsnMap) {
        
        for (BudgetConstructionOrgReasonSummaryReportTotal totalPersonEntry : reasonSummaryTotalPerson){
            if (isSameSalaryFundingEntryForTotalPerson(totalPersonEntry.getBudgetConstructionSalaryFunding(), salaryFundingEntry, budgetSsnMap)){
                orgReasonSummaryReportEntry.setPersonPositionNumber(totalPersonEntry.getPersonPositionNumber());
                orgReasonSummaryReportEntry.setPersonFiscalYearTag(totalPersonEntry.getPersonFiscalYearTag());
                orgReasonSummaryReportEntry.setPersonNormalMonthsAndPayMonths(totalPersonEntry.getPersonCsfNormalMonths().toString() + "/" + totalPersonEntry.getPersonCsfPayMonths().toString());
                orgReasonSummaryReportEntry.setPersonCsfAmount(totalPersonEntry.getPersonCsfAmount());
                orgReasonSummaryReportEntry.setPersonCsfPercent(totalPersonEntry.getPersonCsfPercent());
                orgReasonSummaryReportEntry.setPersonSalaryNormalMonths(totalPersonEntry.getPersonSalaryNormalMonths());
                orgReasonSummaryReportEntry.setPersonSalaryAmount(totalPersonEntry.getPersonSalaryAmount());
                orgReasonSummaryReportEntry.setPersonSalaryPercent(totalPersonEntry.getPersonSalaryPercent());
                orgReasonSummaryReportEntry.setPersonSalaryFte(totalPersonEntry.getPersonSalaryFte());
                orgReasonSummaryReportEntry.setPersonTiFlag(totalPersonEntry.getPersonTiFlag());
                orgReasonSummaryReportEntry.setPersonAmountChange(totalPersonEntry.getPersonAmountChange());
                orgReasonSummaryReportEntry.setPersonPercentChange(totalPersonEntry.getPersonPercentChange());
            }
            for (BudgetConstructionOrgReasonSummaryReportTotal totalOrgEntry : reasonSummaryTotalOrg){
                if (isSameSalaryFundingEntryForTotalOrg(totalPersonEntry.getBudgetConstructionSalaryFunding(), salaryFundingEntry, budgetSsnMap)){
                    orgReasonSummaryReportEntry.setNewFte(totalOrgEntry.getNewFte());
                    orgReasonSummaryReportEntry.setNewTotalAmount(totalOrgEntry.getNewTotalAmount());
                    orgReasonSummaryReportEntry.setConTotalBaseAmount(totalOrgEntry.getConTotalBaseAmount());
                    orgReasonSummaryReportEntry.setConFte(totalOrgEntry.getConFte());
                    orgReasonSummaryReportEntry.setConTotalRequestAmount(totalOrgEntry.getConTotalRequestAmount());            
                    orgReasonSummaryReportEntry.setNewAverageAmount(totalOrgEntry.getNewAverageAmount());
                    orgReasonSummaryReportEntry.setConAverageBaseAmount(totalOrgEntry.getConAverageBaseAmount());
                    orgReasonSummaryReportEntry.setConAverageRequestAmount(totalOrgEntry.getConAverageRequestAmount());
                    orgReasonSummaryReportEntry.setConAveragechange(totalOrgEntry.getConAveragechange());
                    orgReasonSummaryReportEntry.setConPercentChange(totalOrgEntry.getConPercentChange());
                }
            }
        }
    }
    
    
    private Collection<BudgetConstructionOrgReasonSummaryReportTotal> calculatePersonTotal(Integer universityFiscalYear, Collection<BudgetConstructionSalaryFunding> reasonSummaryList, List<BudgetConstructionSalaryFunding> listForCalculateTotalPerson, Map positionMap, Map budgetSsnMap) {
        Collection<BudgetConstructionOrgReasonSummaryReportTotal> returnCollection = new ArrayList();
        
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
            BudgetConstructionOrgReasonSummaryReportTotal budgetConstructionOrgReasonSummaryReportTotal = new BudgetConstructionOrgReasonSummaryReportTotal();
            for(BudgetConstructionSalaryFunding salaryfundingEntry: reasonSummaryList) {
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

            budgetConstructionOrgReasonSummaryReportTotal.setBudgetConstructionSalaryFunding(totalPersonEntry);
            budgetConstructionOrgReasonSummaryReportTotal.setPersonPositionNumber(positionNumber); 
            budgetConstructionOrgReasonSummaryReportTotal.setPersonFiscalYearTag(fiscalYearTag);
            budgetConstructionOrgReasonSummaryReportTotal.setPersonCsfNormalMonths(csfNormalMonths);
            budgetConstructionOrgReasonSummaryReportTotal.setPersonCsfPayMonths(csfPayMonths);
            budgetConstructionOrgReasonSummaryReportTotal.setPersonCsfAmount(csfAmount);
            budgetConstructionOrgReasonSummaryReportTotal.setPersonCsfPercent(csfPercent);
            budgetConstructionOrgReasonSummaryReportTotal.setPersonSalaryNormalMonths(salaryNormalMonths);
            budgetConstructionOrgReasonSummaryReportTotal.setPersonSalaryAmount(salaryAmount);
            budgetConstructionOrgReasonSummaryReportTotal.setPersonSalaryPercent(salaryPercent);
            budgetConstructionOrgReasonSummaryReportTotal.setPersonSalaryFte(BudgetConstructionReportHelper.setDecimalDigit(salaryFte, 5));
            budgetConstructionOrgReasonSummaryReportTotal.setPersonTiFlag(tiFlag);
            budgetConstructionOrgReasonSummaryReportTotal.setPersonAmountChange(amountChange);
            budgetConstructionOrgReasonSummaryReportTotal.setPersonPercentChange(percentChange);
            
            returnCollection.add(budgetConstructionOrgReasonSummaryReportTotal);
            
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
        
        for (BudgetConstructionSalaryFunding totalOrgEntry : listForCalculateTotalOrg){
            BudgetConstructionOrgReasonSummaryReportTotal budgetConstructionOrgReasonSummaryReportTotal = new BudgetConstructionOrgReasonSummaryReportTotal();
            for (BudgetConstructionOrgReasonSummaryReportTotal reportTotalPersonEntry : reasonSummaryTotalPerson){
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
