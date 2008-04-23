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

import org.apache.ojb.broker.PersistenceBrokerException;
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
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountFundingDetailReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountFundingDetailReportTotal;
import org.kuali.module.budget.bo.BudgetConstructionOrgPositionFundingDetailReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgPositionFundingDetailReportTotal;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.BudgetConstructionPositionFunding;
import org.kuali.module.budget.bo.BudgetConstructionReportThresholdSettings;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.dao.BudgetConstructionPositionFundingDetailReportDao;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.service.BudgetConstructionPositionFundingDetailReportService;
import org.kuali.module.budget.util.BudgetConstructionReportHelper;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionPositionFundingDetailReportServiceImpl implements BudgetConstructionPositionFundingDetailReportService {

    BudgetConstructionPositionFundingDetailReportDao budgetConstructionPositionFundingDetailReportDao;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    KualiConfigurationService kualiConfigurationService;
    BusinessObjectService businessObjectService;


    public void updatePositionFundingDetailReport(String personUserIdentifier, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {

        boolean applyAThreshold = budgetConstructionReportThresholdSettings.isUseThreshold();
        boolean selectOnlyGreaterThanOrEqualToThreshold = budgetConstructionReportThresholdSettings.isUseGreaterThanOperator();
        KualiDecimal thresholdPercent = budgetConstructionReportThresholdSettings.getThresholdPercent();

        budgetConstructionPositionFundingDetailReportDao.updateReportsPositionFundingDetailTable(personUserIdentifier, applyAThreshold, selectOnlyGreaterThanOrEqualToThreshold, thresholdPercent);
    }


    public Collection<BudgetConstructionOrgPositionFundingDetailReport> buildReports(Integer universityFiscalYear, String personUserIdentifier) {
        Collection<BudgetConstructionOrgPositionFundingDetailReport> reportSet = new ArrayList();


        // List<BudgetConstructionOrgPositionFundingDetailReportTotal> orgPositionFundingDetailReportTotalList;
        BudgetConstructionOrgPositionFundingDetailReport orgPositionFundingDetailReportEntry;
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionPositionFunding> positionFundingDetailList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionPositionFunding.class, searchCriteria, orderList);

        /*
         * Map appointmentFundingEntireMap = new HashMap(); for (BudgetConstructionPositionFunding positionFundingDetailEntry :
         * positionFundingDetailList) { appointmentFundingEntireMap.put(positionFundingDetailEntry,
         * getPendingBudgetConstructionAppointmentFundingList(universityFiscalYear, positionFundingDetailEntry)); }
         */

        searchCriteria.clear();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        Collection<BudgetConstructionObjectPick> objectPickList = businessObjectService.findMatching(BudgetConstructionObjectPick.class, searchCriteria);
        String objectCodes = "";
        for (BudgetConstructionObjectPick objectPick : objectPickList) {
            objectCodes += objectPick.getFinancialObjectCode() + " ";
        }

        List<BudgetConstructionPositionFunding> listForCalculateTotalPerson = deleteDuplicated((List) positionFundingDetailList, 1);
        List<BudgetConstructionPositionFunding> listForCalculateTotalOrg = deleteDuplicated((List) positionFundingDetailList, 2);

        // Calculate Total Section
        Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> fundingDetailTotalPerson = calculatePersonTotal(positionFundingDetailList, listForCalculateTotalPerson);
        Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> fundingDetailTotalOrg = calculateOrgTotal(positionFundingDetailList, listForCalculateTotalOrg);


        for (BudgetConstructionPositionFunding positionFundingDetailEntry : positionFundingDetailList) {
            orgPositionFundingDetailReportEntry = new BudgetConstructionOrgPositionFundingDetailReport();
            buildReportsHeader(universityFiscalYear, objectCodes, orgPositionFundingDetailReportEntry, positionFundingDetailEntry);
            buildReportsBody(universityFiscalYear, orgPositionFundingDetailReportEntry, positionFundingDetailEntry);
            buildReportsTotal(orgPositionFundingDetailReportEntry, positionFundingDetailEntry, fundingDetailTotalPerson, fundingDetailTotalOrg);
            reportSet.add(orgPositionFundingDetailReportEntry);

        }


        return reportSet;
    }


    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsHeader(Integer universityFiscalYear, String objectCodes, BudgetConstructionOrgPositionFundingDetailReport orgPositionFundingDetailReportEntry, BudgetConstructionPositionFunding positionFundingDetail) {
        String chartDesc = positionFundingDetail.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = positionFundingDetail.getSelectedOrganization().getOrganizationName();


        Integer prevFiscalyear = universityFiscalYear - 1;
        orgPositionFundingDetailReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));

        orgPositionFundingDetailReportEntry.setOrganizationCode(positionFundingDetail.getSelectedOrganizationCode());
        if (orgName == null) {
            orgPositionFundingDetailReportEntry.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgPositionFundingDetailReportEntry.setOrganizationName(orgName);
        }

        orgPositionFundingDetailReportEntry.setChartOfAccountsCode(positionFundingDetail.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgPositionFundingDetailReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgPositionFundingDetailReportEntry.setChartOfAccountDescription(chartDesc);
        }


        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgPositionFundingDetailReportEntry.setReqFy(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgPositionFundingDetailReportEntry.setFinancialObjectCode(positionFundingDetail.getFinancialObjectCode());
       
    }


    /**
     * builds report body
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgPositionFundingDetailReport orgPositionFundingDetailReportEntry, BudgetConstructionPositionFunding positionFundingDetailEntry) {
        BudgetConstructionAdministrativePost budgetConstructionAdministrativePost;
        BudgetConstructionPosition budgetConstructionPosition;
        BudgetConstructionCalculatedSalaryFoundationTracker budgetConstructionCalculatedSalaryFoundationTracker;
        PendingBudgetConstructionAppointmentFunding appointmentFundingEntry = positionFundingDetailEntry.getPendingAppointmentFunding();

        // get budgetConstructionIntendedIncumbent, budgetConstructionAdministrativePost, budgetConstructionPosition objects
        budgetConstructionAdministrativePost = getBudgetConstructionAdministrativePost(appointmentFundingEntry);
        budgetConstructionPosition = getBudgetConstructionPosition(universityFiscalYear, appointmentFundingEntry);

        // set report body
        orgPositionFundingDetailReportEntry.setAccountNumber(positionFundingDetailEntry.getAccountNumber());
        orgPositionFundingDetailReportEntry.setSubAccountNumber(positionFundingDetailEntry.getSubAccountNumber());
        orgPositionFundingDetailReportEntry.setFinancialSubObjectCode(positionFundingDetailEntry.getFinancialSubObjectCode());
        
        if (positionFundingDetailEntry.getPersonName() != null) {
            orgPositionFundingDetailReportEntry.setPersonName(positionFundingDetailEntry.getPersonName());
        }
        orgPositionFundingDetailReportEntry.setPersonName(BCConstants.Report.VACANT);


        if (budgetConstructionAdministrativePost != null) {
            orgPositionFundingDetailReportEntry.setAdministrativePost(budgetConstructionAdministrativePost.getAdministrativePost());
        }

        if (budgetConstructionPosition != null) {
            orgPositionFundingDetailReportEntry.setPositionNumber(budgetConstructionPosition.getPositionNumber());
            orgPositionFundingDetailReportEntry.setNormalWorkMonthsAndiuPayMonths(budgetConstructionPosition.getIuNormalWorkMonths() + "/" + budgetConstructionPosition.getIuPayMonths());
            orgPositionFundingDetailReportEntry.setPositionFte(budgetConstructionPosition.getPositionFullTimeEquivalency().setScale(5, 5).toString());
            orgPositionFundingDetailReportEntry.setPositionSalaryPlanDefault(budgetConstructionPosition.getPositionSalaryPlanDefault());
            orgPositionFundingDetailReportEntry.setPositionGradeDefault(budgetConstructionPosition.getPositionGradeDefault());
            orgPositionFundingDetailReportEntry.setPositionStandardHoursDefault(budgetConstructionPosition.getPositionStandardHoursDefault());
        }
        if (appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
            budgetConstructionCalculatedSalaryFoundationTracker = appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0);
            orgPositionFundingDetailReportEntry.setCsfFundingStatusCode(budgetConstructionCalculatedSalaryFoundationTracker.getCsfFundingStatusCode());
            orgPositionFundingDetailReportEntry.setCsfTimePercent(budgetConstructionCalculatedSalaryFoundationTracker.getCsfTimePercent().setScale(2, 2));
            orgPositionFundingDetailReportEntry.setCsfAmount(new Integer(budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue()));
            orgPositionFundingDetailReportEntry.setCsfFullTimeEmploymentQuantity(budgetConstructionCalculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity().setScale(5, 5).toString());

            // calculate amountChange and percentChange
            if (appointmentFundingEntry.getAppointmentRequestedFteQuantity().equals(budgetConstructionCalculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity())) {
                Integer amountChange = appointmentFundingEntry.getAppointmentRequestedAmount().subtract(budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount()).intValue();
                orgPositionFundingDetailReportEntry.setAmountChange(amountChange);
                orgPositionFundingDetailReportEntry.setPercentChange(BudgetConstructionReportHelper.calculateChange(new BigDecimal(amountChange.intValue()), budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().bigDecimalValue()));
            }
        }

        if (appointmentFundingEntry != null) {
            if (appointmentFundingEntry.getFinancialSubObjectCode().equals(BCConstants.Report.BLANK_SUB_OBJECT_CODE)) {
                orgPositionFundingDetailReportEntry.setFinancialSubObjectCode(BCConstants.Report.BLANK);
            }
            else {
                orgPositionFundingDetailReportEntry.setFinancialSubObjectCode(appointmentFundingEntry.getFinancialSubObjectCode());
            }

            orgPositionFundingDetailReportEntry.setAppointmentFundingMonth(appointmentFundingEntry.getAppointmentFundingMonth());
            orgPositionFundingDetailReportEntry.setAppointmentRequestedAmount(new Integer(appointmentFundingEntry.getAppointmentRequestedAmount().intValue()));
            orgPositionFundingDetailReportEntry.setAppointmentRequestedTimePercent(appointmentFundingEntry.getAppointmentRequestedTimePercent().setScale(2, 2));
            orgPositionFundingDetailReportEntry.setAppointmentRequestedFteQuantity(appointmentFundingEntry.getAppointmentRequestedFteQuantity().setScale(5, 5).toString());
            orgPositionFundingDetailReportEntry.setAppointmentFundingDurationCode(appointmentFundingEntry.getAppointmentFundingDurationCode());
            orgPositionFundingDetailReportEntry.setAppointmentRequestedCsfAmount(new Integer(appointmentFundingEntry.getAppointmentRequestedCsfAmount().intValue()));
            orgPositionFundingDetailReportEntry.setAppointmentRequestedCsfTimePercent(appointmentFundingEntry.getAppointmentRequestedCsfTimePercent());
            orgPositionFundingDetailReportEntry.setAppointmentRequestedCsfFteQuantity(appointmentFundingEntry.getAppointmentRequestedCsfFteQuantity().setScale(5, 5).toString());
            orgPositionFundingDetailReportEntry.setAppointmentTotalIntendedAmount(new Integer(appointmentFundingEntry.getAppointmentTotalIntendedAmount().intValue()));
            orgPositionFundingDetailReportEntry.setAppointmentTotalIntendedFteQuantity(appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity().setScale(5, 5).toString());


            // group
            orgPositionFundingDetailReportEntry.setEmplid(appointmentFundingEntry.getEmplid());
        }


        if (appointmentFundingEntry.isAppointmentFundingDeleteIndicator()) {
            orgPositionFundingDetailReportEntry.setDeleteBox(BCConstants.Report.DELETE_MARK);
        }
        else {
            orgPositionFundingDetailReportEntry.setDeleteBox(BCConstants.Report.BLANK);
        }
    }


    public void buildReportsTotal(BudgetConstructionOrgPositionFundingDetailReport orgPositionFundingDetailReportEntry, BudgetConstructionPositionFunding positionFundingDetail, Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> fundingDetailTotalPerson, Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> fundingDetailTotalOrg) {
        for (BudgetConstructionOrgPositionFundingDetailReportTotal fundingDetailTotalPersonEntry : fundingDetailTotalPerson) {
            if (isSamePositionFundingDetailEntryForTotalPerson(fundingDetailTotalPersonEntry.getBudgetConstructionPositionFunding(), positionFundingDetail)) {

                orgPositionFundingDetailReportEntry.setTotalPersonPositionCsfAmount(fundingDetailTotalPersonEntry.getTotalPersonPositionCsfAmount());
                orgPositionFundingDetailReportEntry.setTotalPersonAppointmentRequestedAmount(fundingDetailTotalPersonEntry.getTotalPersonAppointmentRequestedAmount());
                orgPositionFundingDetailReportEntry.setTotalPersonPositionCsfFteQuantity(fundingDetailTotalPersonEntry.getTotalPersonPositionCsfFteQuantity().setScale(5, 5).toString());
                orgPositionFundingDetailReportEntry.setTotalPersonAppointmentRequestedFteQuantity(fundingDetailTotalPersonEntry.getTotalPersonAppointmentRequestedFteQuantity().setScale(5, 5).toString());
                // calculate amountChange and percentChange
                Integer amountChange = fundingDetailTotalPersonEntry.getTotalPersonAppointmentRequestedAmount() - fundingDetailTotalPersonEntry.getTotalPersonPositionCsfAmount();
                orgPositionFundingDetailReportEntry.setTotalPersonAmountChange(amountChange);
                orgPositionFundingDetailReportEntry.setTotalPersonPercentChange(BudgetConstructionReportHelper.calculateChange(new BigDecimal(amountChange.intValue()), new BigDecimal(fundingDetailTotalPersonEntry.getTotalPersonPositionCsfAmount().intValue())));
                
                orgPositionFundingDetailReportEntry.setPersonSortCode(fundingDetailTotalPersonEntry.getPersonSortCode());
                 
                
            }
        }

        for (BudgetConstructionOrgPositionFundingDetailReportTotal fundingDetailTotalOrgEntry : fundingDetailTotalOrg) {
            if (isSamePositionFundingDetailEntryForTotalOrg(fundingDetailTotalOrgEntry.getBudgetConstructionPositionFunding(), positionFundingDetail)) {

                orgPositionFundingDetailReportEntry.setTotalOrgPositionCsfAmount(fundingDetailTotalOrgEntry.getTotalOrgPositionCsfAmount());
                orgPositionFundingDetailReportEntry.setTotalOrgAppointmentRequestedAmount(fundingDetailTotalOrgEntry.getTotalOrgAppointmentRequestedAmount());
                orgPositionFundingDetailReportEntry.setTotalOrgPositionCsfFteQuantity(fundingDetailTotalOrgEntry.getTotalOrgPositionCsfFteQuantity().setScale(5, 5).toString());
                orgPositionFundingDetailReportEntry.setTotalOrgAppointmentRequestedFteQuantity(fundingDetailTotalOrgEntry.getTotalOrgAppointmentRequestedFteQuantity().setScale(5, 5).toString());
                Integer amountChange = fundingDetailTotalOrgEntry.getTotalOrgAppointmentRequestedAmount() - fundingDetailTotalOrgEntry.getTotalOrgPositionCsfAmount();
                orgPositionFundingDetailReportEntry.setTotalOrgAmountChange(amountChange);
                orgPositionFundingDetailReportEntry.setTotalOrgPercentChange(BudgetConstructionReportHelper.calculateChange(new BigDecimal(amountChange.intValue()), new BigDecimal(fundingDetailTotalOrgEntry.getTotalOrgPositionCsfAmount().intValue())));
                if (orgPositionFundingDetailReportEntry.getPersonSortCode() == null){
                    orgPositionFundingDetailReportEntry.setPersonSortCode(fundingDetailTotalOrgEntry.getPersonSortCode());
                }
            }
        }
        
        
        
        
    }


    private Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> calculatePersonTotal(Collection<BudgetConstructionPositionFunding> positionFundingDetailList, List<BudgetConstructionPositionFunding> listForCalculateTotalPerson) {
        Integer totalPersonPositionCsfAmount = new Integer(0);
        Integer totalPersonAppointmentRequestedAmount = new Integer(0);
        BigDecimal totalPersonPositionCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalPersonAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer personSortCode = new Integer(0);
        

        Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> returnCollection = new ArrayList();
        PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding = null;

        for (BudgetConstructionPositionFunding budgetConstructionPositionFunding : listForCalculateTotalPerson) {


            for (BudgetConstructionPositionFunding positionFundingEntry : positionFundingDetailList) {

                if (isSamePositionFundingDetailEntryForTotalPerson(budgetConstructionPositionFunding, positionFundingEntry)) {
                    pendingAppointmentFunding = positionFundingEntry.getPendingAppointmentFunding();
                    if (pendingAppointmentFunding.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
                        BudgetConstructionCalculatedSalaryFoundationTracker calculatedSalaryFoundationTracker = pendingAppointmentFunding.getBcnCalculatedSalaryFoundationTracker().get(0);
                        totalPersonPositionCsfAmount = totalPersonPositionCsfAmount + new Integer(calculatedSalaryFoundationTracker.getCsfAmount().intValue());
                        totalPersonPositionCsfFteQuantity = totalPersonPositionCsfFteQuantity.add(calculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity());
                    }
                    if (pendingAppointmentFunding != null) {
                        totalPersonAppointmentRequestedAmount = totalPersonAppointmentRequestedAmount + new Integer(pendingAppointmentFunding.getAppointmentRequestedAmount().intValue());
                        totalPersonAppointmentRequestedFteQuantity = totalPersonAppointmentRequestedFteQuantity.add(pendingAppointmentFunding.getAppointmentRequestedFteQuantity());
                    }
                    personSortCode += 1;
                    
                }
            }

            BudgetConstructionOrgPositionFundingDetailReportTotal budgetConstructionOrgPositionFundingDetailReportTotal = new BudgetConstructionOrgPositionFundingDetailReportTotal();

            budgetConstructionOrgPositionFundingDetailReportTotal.setBudgetConstructionPositionFunding(budgetConstructionPositionFunding);
            budgetConstructionOrgPositionFundingDetailReportTotal.setTotalPersonPositionCsfAmount(totalPersonPositionCsfAmount);
            budgetConstructionOrgPositionFundingDetailReportTotal.setTotalPersonPositionCsfFteQuantity(totalPersonPositionCsfFteQuantity);
            budgetConstructionOrgPositionFundingDetailReportTotal.setTotalPersonAppointmentRequestedAmount(totalPersonAppointmentRequestedAmount);
            budgetConstructionOrgPositionFundingDetailReportTotal.setTotalPersonAppointmentRequestedFteQuantity(totalPersonAppointmentRequestedFteQuantity);

            if (personSortCode.intValue() > 1){
                budgetConstructionOrgPositionFundingDetailReportTotal.setPersonSortCode(new Integer(1));
            }
            
            returnCollection.add(budgetConstructionOrgPositionFundingDetailReportTotal);

            totalPersonPositionCsfAmount = new Integer(0);
            totalPersonAppointmentRequestedAmount = new Integer(0);
            totalPersonPositionCsfFteQuantity = BigDecimal.ZERO;
            totalPersonAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            personSortCode = new Integer(0);
        }


        return returnCollection;
    }

    private Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> calculateOrgTotal(Collection<BudgetConstructionPositionFunding> positionFundingDetailList, List<BudgetConstructionPositionFunding> listForCalculateTotalOrg) {

        // private Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> calculateAccountTotal(Map
        // appointmentFundingEntireMap, Collection<BudgetConstructionObjectDump> accountFundingDetailList,
        // List<BudgetConstructionObjectDump> listForCalculateTotalAccount) {

        Integer totalOrgPositionCsfAmount = new Integer(0);
        Integer totalOrgAppointmentRequestedAmount = new Integer(0);
        BigDecimal totalOrgPositionCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalOrgAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        

        Collection<BudgetConstructionOrgPositionFundingDetailReportTotal> returnCollection = new ArrayList();
        PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding = null;

        for (BudgetConstructionPositionFunding budgetConstructionPositionFunding : listForCalculateTotalOrg) {
            for (BudgetConstructionPositionFunding positionFundingEntry : positionFundingDetailList) {

                if (isSamePositionFundingDetailEntryForTotalOrg(budgetConstructionPositionFunding, positionFundingEntry)) {
                    pendingAppointmentFunding = positionFundingEntry.getPendingAppointmentFunding();
                    if (pendingAppointmentFunding.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
                        BudgetConstructionCalculatedSalaryFoundationTracker calculatedSalaryFoundationTracker = pendingAppointmentFunding.getBcnCalculatedSalaryFoundationTracker().get(0);
                        totalOrgPositionCsfAmount = totalOrgPositionCsfAmount + new Integer(calculatedSalaryFoundationTracker.getCsfAmount().intValue());
                        totalOrgPositionCsfFteQuantity = totalOrgPositionCsfFteQuantity.add(calculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity());
                    }
                    if (pendingAppointmentFunding != null) {
                        totalOrgAppointmentRequestedAmount = totalOrgAppointmentRequestedAmount + new Integer(pendingAppointmentFunding.getAppointmentRequestedAmount().intValue());
                        totalOrgAppointmentRequestedFteQuantity = totalOrgAppointmentRequestedFteQuantity.add(pendingAppointmentFunding.getAppointmentRequestedFteQuantity());
                    }
                
                }
                

            }
            BudgetConstructionOrgPositionFundingDetailReportTotal budgetConstructionOrgOrgFundingDetailReportTotal = new BudgetConstructionOrgPositionFundingDetailReportTotal();
            budgetConstructionOrgOrgFundingDetailReportTotal.setBudgetConstructionPositionFunding(budgetConstructionPositionFunding);
            budgetConstructionOrgOrgFundingDetailReportTotal.setTotalOrgPositionCsfAmount(totalOrgPositionCsfAmount);
            budgetConstructionOrgOrgFundingDetailReportTotal.setTotalOrgPositionCsfFteQuantity(totalOrgPositionCsfFteQuantity);
            budgetConstructionOrgOrgFundingDetailReportTotal.setTotalOrgAppointmentRequestedAmount(totalOrgAppointmentRequestedAmount);
            budgetConstructionOrgOrgFundingDetailReportTotal.setTotalOrgAppointmentRequestedFteQuantity(totalOrgAppointmentRequestedFteQuantity);
            
            returnCollection.add(budgetConstructionOrgOrgFundingDetailReportTotal);

            totalOrgPositionCsfAmount = new Integer(0);
            totalOrgAppointmentRequestedAmount = new Integer(0);
            totalOrgPositionCsfFteQuantity = BigDecimal.ZERO;
            totalOrgAppointmentRequestedFteQuantity = BigDecimal.ZERO;
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
        returnList.add(KFSPropertyConstants.SELECTED_ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.SELECTED_ORGANIZATION_CODE);
        returnList.add(KFSPropertyConstants.PERSON_NAME);
        returnList.add(KFSPropertyConstants.EMPLID);
        returnList.add(KFSPropertyConstants.POSITION_NUMBER);
        returnList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        return returnList;
    }


    private boolean isSamePositionFundingDetailEntryForTotalPerson(BudgetConstructionPositionFunding firstbcpf, BudgetConstructionPositionFunding secondbcpf) {
        if (isSamePositionFundingDetailEntryForTotalOrg(firstbcpf, secondbcpf) && firstbcpf.getEmplid().equals(secondbcpf.getEmplid())) {
            return true;
        }
        else
            return false;
    }

    private boolean isSamePositionFundingDetailEntryForTotalOrg(BudgetConstructionPositionFunding firstbcpf, BudgetConstructionPositionFunding secondbcpf) {
        if (firstbcpf.getSelectedOrganizationChartOfAccounts().equals(secondbcpf.getSelectedOrganizationChartOfAccounts()) && firstbcpf.getSelectedOrganizationCode().equals(secondbcpf.getSelectedOrganizationCode())) {
            return true;
        }
        else
            return false;
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
        BudgetConstructionPositionFunding positionFundingEntry = null;
        BudgetConstructionPositionFunding positionFundingEntryAux = null;
        List returnList = new ArrayList();
        if ((list != null) && (list.size() > 0)) {
            positionFundingEntry = (BudgetConstructionPositionFunding) list.get(count);
            positionFundingEntryAux = (BudgetConstructionPositionFunding) list.get(count);
            returnList.add(positionFundingEntry);
            count++;
            while (count < list.size()) {
                positionFundingEntry = (BudgetConstructionPositionFunding) list.get(count);
                switch (mode) {
                    case 1: {
                        if (!isSamePositionFundingDetailEntryForTotalPerson(positionFundingEntry, positionFundingEntryAux)) {
                            returnList.add(positionFundingEntry);
                            positionFundingEntryAux = positionFundingEntry;
                        }
                    }
                    case 2: {
                        if (!isSamePositionFundingDetailEntryForTotalOrg(positionFundingEntry, positionFundingEntryAux)) {
                            returnList.add(positionFundingEntry);
                            positionFundingEntryAux = positionFundingEntry;
                        }
                    }
                }
                count++;
            }
        }
        return returnList;
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

    public void setBudgetConstructionPositionFundingDetailReportDao(BudgetConstructionPositionFundingDetailReportDao budgetConstructionPositionFundingDetailReportDao) {
        this.budgetConstructionPositionFundingDetailReportDao = budgetConstructionPositionFundingDetailReportDao;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}
