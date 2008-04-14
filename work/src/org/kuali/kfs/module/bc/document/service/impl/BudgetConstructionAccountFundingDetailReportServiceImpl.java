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
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.bo.BudgetConstructionAdministrativePost;
import org.kuali.module.budget.bo.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.module.budget.bo.BudgetConstructionIntendedIncumbent;
import org.kuali.module.budget.bo.BudgetConstructionObjectDump;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountFundingDetailReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountFundingDetailReportTotal;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.dao.BudgetConstructionAccountFundingDetailReportDao;
import org.kuali.module.budget.service.BudgetConstructionAccountFundingDetailReportService;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountFundingDetailReportService.
 */
@Transactional
public class BudgetConstructionAccountFundingDetailReportServiceImpl implements BudgetConstructionAccountFundingDetailReportService {

    BudgetConstructionAccountFundingDetailReportDao budgetConstructionAccountFundingDetailReportDao;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    KualiConfigurationService kualiConfigurationService;
    BusinessObjectService businessObjectService;
    UniversalUserService universalUserService;

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateRepotsAccountFundingDetailTable(java.lang.String)
     */
    public void updateAccountFundingDetailTable(String personUserIdentifier) {
        budgetConstructionAccountFundingDetailReportDao.updateReportsAccountFundingDetailTable(personUserIdentifier);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetConstructionAccountFundingDetailReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    public Collection<BudgetConstructionOrgAccountFundingDetailReport> buildReports(Integer universityFiscalYear, String personUserIdentifier) {
        Collection<BudgetConstructionOrgAccountFundingDetailReport> reportSet = new ArrayList();
        List<BudgetConstructionOrgAccountFundingDetailReportTotal> orgAccountFundingDetailReportTotalList;
        BudgetConstructionOrgAccountFundingDetailReport orgAccountFundingDetailReportEntry;
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionObjectDump> accountFundingDetailList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionObjectDump.class, searchCriteria, orderList);

        // build entire map of appointmentfunding
        Map appointmentFundingEntireMap = new HashMap();
        for (BudgetConstructionObjectDump accountFundingDetailEntry : accountFundingDetailList) {
            appointmentFundingEntireMap.put(accountFundingDetailEntry, getPendingBudgetConstructionAppointmentFundingList(universityFiscalYear, accountFundingDetailEntry));
        }

        List<BudgetConstructionObjectDump> listForCalculateTotalObject = deleteDuplicated((List) accountFundingDetailList, 1);
        List<BudgetConstructionObjectDump> listForCalculateTotalAccount = deleteDuplicated((List) accountFundingDetailList, 2);

        // Calculate Total Section
        Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> fundingDetailTotalObject = calculateObjectTotal(appointmentFundingEntireMap, listForCalculateTotalObject);
        Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> fundingDetailTotalAccount = calculateAccountTotal(appointmentFundingEntireMap, listForCalculateTotalAccount);

        for (BudgetConstructionObjectDump accountFundingDetailEntry : accountFundingDetailList) {
            Collection<PendingBudgetConstructionAppointmentFunding> appointmentFundingCollection = (Collection<PendingBudgetConstructionAppointmentFunding>) appointmentFundingEntireMap.get(accountFundingDetailEntry);
            for (PendingBudgetConstructionAppointmentFunding appointmentFundingEntry : appointmentFundingCollection) {

                orgAccountFundingDetailReportEntry = new BudgetConstructionOrgAccountFundingDetailReport();
                buildReportsHeader(universityFiscalYear, orgAccountFundingDetailReportEntry, accountFundingDetailEntry);
                buildReportsBody(universityFiscalYear, orgAccountFundingDetailReportEntry, appointmentFundingEntry);
                buildReportsTotal(orgAccountFundingDetailReportEntry, accountFundingDetailEntry, fundingDetailTotalObject, fundingDetailTotalAccount);
                reportSet.add(orgAccountFundingDetailReportEntry);


            }

        }

        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsHeader(Integer universityFiscalYear, BudgetConstructionOrgAccountFundingDetailReport orgAccountFundingDetailReportEntry, BudgetConstructionObjectDump accountFundingDetail) {
        String orgChartDesc = accountFundingDetail.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = accountFundingDetail.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = accountFundingDetail.getOrganization().getOrganizationName();
        String reportChartDesc = accountFundingDetail.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        Integer prevFiscalyear = universityFiscalYear - 1;
        orgAccountFundingDetailReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgAccountFundingDetailReportEntry.setOrgChartOfAccountsCode(accountFundingDetail.getOrganizationChartOfAccountsCode());

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgAccountFundingDetailReportEntry.setBaseFy(prevPrevFiscalyear.toString() + " - " + prevFiscalyear.toString().substring(2, 4));
        orgAccountFundingDetailReportEntry.setReqFy(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgAccountFundingDetailReportEntry.setHeader1(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_ACCOUNT_SUB));
        orgAccountFundingDetailReportEntry.setHeader2(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_ACCOUNT_SUB_NAME));
        orgAccountFundingDetailReportEntry.setHeader3(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_BASE_AMOUNT));
        orgAccountFundingDetailReportEntry.setHeader4(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_REQ_AMOUNT));
        orgAccountFundingDetailReportEntry.setHeader5(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgAccountFundingDetailReportEntry.setHeader6(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgAccountFundingDetailReportEntry.setConsHdr("");

        // group
        orgAccountFundingDetailReportEntry.setFinancialObjectCode(accountFundingDetail.getFinancialObjectCode());
        orgAccountFundingDetailReportEntry.setAccountNumber(accountFundingDetail.getAccountNumber());

    }

    /**
     * builds report body
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgAccountFundingDetailReport orgAccountFundingDetailReportEntry, PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {
        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent;
        BudgetConstructionAdministrativePost budgetConstructionAdministrativePost;
        BudgetConstructionPosition budgetConstructionPosition;
        BudgetConstructionCalculatedSalaryFoundationTracker budgetConstructionCalculatedSalaryFoundationTracker = appointmentFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0);


        // get budgetConstructionIntendedIncumbent, budgetConstructionAdministrativePost, budgetConstructionPosition objects
        budgetConstructionIntendedIncumbent = getBudgetConstructionIntendedIncumbent(appointmentFundingEntry);
        budgetConstructionAdministrativePost = getBudgetConstructionAdministrativePost(appointmentFundingEntry);
        budgetConstructionPosition = getBudgetConstructionPosition(universityFiscalYear, appointmentFundingEntry);

        // set report body
        if (budgetConstructionIntendedIncumbent != null) {
            orgAccountFundingDetailReportEntry.setPersonName(budgetConstructionIntendedIncumbent.getPersonName());
            orgAccountFundingDetailReportEntry.setIuClassificationLevel(budgetConstructionIntendedIncumbent.getIuClassificationLevel());
        }


        if (budgetConstructionAdministrativePost != null) {
            orgAccountFundingDetailReportEntry.setAdministrativePost(budgetConstructionAdministrativePost.getAdministrativePost());
        }

        if (budgetConstructionPosition != null) {
            orgAccountFundingDetailReportEntry.setPositionNumber(budgetConstructionPosition.getPositionNumber());
            orgAccountFundingDetailReportEntry.setNormalWorkMonthsAndiuPayMonths(budgetConstructionPosition.getIuNormalWorkMonths() + "/" + budgetConstructionPosition.getIuPayMonths());
            orgAccountFundingDetailReportEntry.setPositionSalaryPlanDefault(budgetConstructionPosition.getPositionSalaryPlanDefault());
            orgAccountFundingDetailReportEntry.setPositionGradeDefault(budgetConstructionPosition.getPositionGradeDefault());
            orgAccountFundingDetailReportEntry.setPositionStandardHoursDefault(budgetConstructionPosition.getPositionStandardHoursDefault());
        }

        if (budgetConstructionCalculatedSalaryFoundationTracker != null) {
            orgAccountFundingDetailReportEntry.setCsfTimePercent(budgetConstructionCalculatedSalaryFoundationTracker.getCsfTimePercent());
            orgAccountFundingDetailReportEntry.setCsfAmount(new Integer(budgetConstructionCalculatedSalaryFoundationTracker.getCsfAmount().intValue()));
            orgAccountFundingDetailReportEntry.setCsfFullTimeEmploymentQuantity(budgetConstructionCalculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity().toString());
        }

        if (appointmentFundingEntry != null) {
            orgAccountFundingDetailReportEntry.setFinancialSubObjectCode(appointmentFundingEntry.getFinancialSubObjectCode());
            orgAccountFundingDetailReportEntry.setAppointmentFundingMonth(appointmentFundingEntry.getAppointmentFundingMonth());
            orgAccountFundingDetailReportEntry.setAppointmentRequestedAmount(new Integer(appointmentFundingEntry.getAppointmentRequestedAmount().intValue()));
            orgAccountFundingDetailReportEntry.setAppointmentRequestedTimePercent(appointmentFundingEntry.getAppointmentRequestedTimePercent());
            orgAccountFundingDetailReportEntry.setAppointmentRequestedFteQuantity(appointmentFundingEntry.getAppointmentRequestedFteQuantity().toString());
            orgAccountFundingDetailReportEntry.setAppointmentFundingDurationCode(appointmentFundingEntry.getAppointmentFundingDurationCode());
            orgAccountFundingDetailReportEntry.setAppointmentRequestedCsfAmount(new Integer(appointmentFundingEntry.getAppointmentRequestedCsfAmount().intValue()));
            orgAccountFundingDetailReportEntry.setAppointmentRequestedCsfTimePercent(appointmentFundingEntry.getAppointmentRequestedCsfTimePercent());
            orgAccountFundingDetailReportEntry.setAppointmentRequestedCsfFteQuantity(appointmentFundingEntry.getAppointmentRequestedCsfFteQuantity().toString());
            orgAccountFundingDetailReportEntry.setAppointmentTotalIntendedAmount(new Integer(appointmentFundingEntry.getAppointmentTotalIntendedAmount().intValue()));
            orgAccountFundingDetailReportEntry.setAppointmentTotalIntendedFteQuantity(appointmentFundingEntry.getAppointmentTotalIntendedFteQuantity().toString());

            // group
            orgAccountFundingDetailReportEntry.setEmplid(appointmentFundingEntry.getEmplid());

        }


    }


    public void buildReportsTotal(BudgetConstructionOrgAccountFundingDetailReport orgAccountFundingDetailReportEntry, BudgetConstructionObjectDump accountFundingDetail, Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> fundingDetailTotalObject, Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> fundingDetailTotalAccount) {
        for (BudgetConstructionOrgAccountFundingDetailReportTotal fundingDetailTotalObjectEntry : fundingDetailTotalObject) {
            if (isSameAccountFundingDetailEntryForTotalObject(fundingDetailTotalObjectEntry.getBudgetConstructionObjectDump(), accountFundingDetail)) {

                if (accountFundingDetail.getFinancialObject() != null) {
                    orgAccountFundingDetailReportEntry.setTotalObjectname(accountFundingDetail.getFinancialObject().getName());
                }


                orgAccountFundingDetailReportEntry.setTotalObjectPositionCsfAmount(fundingDetailTotalObjectEntry.getTotalObjectPositionCsfAmount());
                orgAccountFundingDetailReportEntry.setTotalObjectAppointmentRequestedAmount(fundingDetailTotalObjectEntry.getTotalObjectAppointmentRequestedAmount());
                orgAccountFundingDetailReportEntry.setTotalObjectPositionCsfFteQuantity(fundingDetailTotalObjectEntry.getTotalObjectPositionCsfFteQuantity().toString());
                orgAccountFundingDetailReportEntry.setTotalObjectAppointmentRequestedFteQuantity(fundingDetailTotalObjectEntry.getTotalObjectAppointmentRequestedFteQuantity().toString());
                // orgAccountFundingDetailReportEntry.setTotalObjectAmountChange();
                // orgAccountFundingDetailReportEntry.setTotalObjectPercentChange();


            }
        }


        for (BudgetConstructionOrgAccountFundingDetailReportTotal fundingDetailTotalAccountEntry : fundingDetailTotalAccount) {
            if (isSameAccountFundingDetailEntryForTotalAccount(fundingDetailTotalAccountEntry.getBudgetConstructionObjectDump(), accountFundingDetail)) {

                if (accountFundingDetail.getAccount() != null) {
                    orgAccountFundingDetailReportEntry.setTotalAccountname(accountFundingDetail.getAccount().getAccountName());
                }

                try {
                    if (accountFundingDetail.getSubAccount() != null) {
                        orgAccountFundingDetailReportEntry.setTotalSubAccountname(accountFundingDetail.getSubAccount().getSubAccountName());
                    }
                }
                catch (PersistenceBrokerException e) {
                    orgAccountFundingDetailReportEntry.setTotalSubAccountname("Error Getting sub-account description");
                }

                orgAccountFundingDetailReportEntry.setTotalAccountPositionCsfAmount(fundingDetailTotalAccountEntry.getTotalAccountPositionCsfAmount());
                orgAccountFundingDetailReportEntry.setTotalAccountAppointmentRequestedAmount(fundingDetailTotalAccountEntry.getTotalAccountAppointmentRequestedAmount());
                orgAccountFundingDetailReportEntry.setTotalAccountPositionCsfFteQuantity(fundingDetailTotalAccountEntry.getTotalAccountPositionCsfFteQuantity().toString());
                orgAccountFundingDetailReportEntry.setTotalAccountAppointmentRequestedFteQuantity(fundingDetailTotalAccountEntry.getTotalAccountAppointmentRequestedFteQuantity().toString());
                // orgAccountFundingDetailReportEntry.setTotalAccountAmountChange();
                // orgAccountFundingDetailReportEntry.setTotalAccountPercentChange();

            }
        }
    }


    /**
     * builds report total
     * 
     * @param BudgetConstructionObjectDump bcod
     * @param List reportTotalList
     */
    private Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> calculateObjectTotal(Map appointmentFundingEntireMap, List<BudgetConstructionObjectDump> listForCalculateTotalObject) {
        Integer totalObjectPositionCsfAmount = new Integer(0);
        Integer totalObjectAppointmentRequestedAmount = new Integer(0);
        BigDecimal totalObjectPositionCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalObjectAppointmentRequestedFteQuantity = BigDecimal.ZERO;

        Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> returnCollection = new ArrayList();

        for (BudgetConstructionObjectDump budgetConstructionObjectDump : listForCalculateTotalObject) {
            Collection<PendingBudgetConstructionAppointmentFunding> accountFundingCollection = new ArrayList();

            accountFundingCollection = (Collection<PendingBudgetConstructionAppointmentFunding>) appointmentFundingEntireMap.get(budgetConstructionObjectDump);
            for (PendingBudgetConstructionAppointmentFunding accountFundingEntry : accountFundingCollection) {
                BudgetConstructionCalculatedSalaryFoundationTracker calculatedSalaryFoundationTracker = accountFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0);
                totalObjectPositionCsfAmount = totalObjectPositionCsfAmount + new Integer(calculatedSalaryFoundationTracker.getCsfAmount().intValue());
                totalObjectPositionCsfFteQuantity = totalObjectPositionCsfFteQuantity.add(calculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity());
                totalObjectAppointmentRequestedAmount = totalObjectAppointmentRequestedAmount + new Integer(accountFundingEntry.getAppointmentRequestedAmount().intValue());
                totalObjectAppointmentRequestedFteQuantity = totalObjectAppointmentRequestedFteQuantity.add(accountFundingEntry.getAppointmentRequestedFteQuantity());
            }

            BudgetConstructionOrgAccountFundingDetailReportTotal budgetConstructionOrgAccountFundingDetailReportTotal = new BudgetConstructionOrgAccountFundingDetailReportTotal();

            budgetConstructionOrgAccountFundingDetailReportTotal.setBudgetConstructionObjectDump(budgetConstructionObjectDump);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalObjectPositionCsfAmount(totalObjectPositionCsfAmount);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalObjectPositionCsfFteQuantity(totalObjectPositionCsfFteQuantity);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalObjectAppointmentRequestedAmount(totalObjectAppointmentRequestedAmount);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalObjectAppointmentRequestedFteQuantity(totalObjectAppointmentRequestedFteQuantity);

            returnCollection.add(budgetConstructionOrgAccountFundingDetailReportTotal);

            totalObjectPositionCsfAmount = new Integer(0);
            totalObjectAppointmentRequestedAmount = new Integer(0);
            totalObjectPositionCsfFteQuantity = BigDecimal.ZERO;
            totalObjectAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        }


        return returnCollection;
    }


    private Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> calculateAccountTotal(Map appointmentFundingEntireMap, List<BudgetConstructionObjectDump> listForCalculateTotalAccount) {

        Integer totalAccountPositionCsfAmount = new Integer(0);
        Integer totalAccountAppointmentRequestedAmount = new Integer(0);
        BigDecimal totalAccountPositionCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalAccountAppointmentRequestedFteQuantity = BigDecimal.ZERO;

        Collection<BudgetConstructionOrgAccountFundingDetailReportTotal> returnCollection = new ArrayList();

        for (BudgetConstructionObjectDump budgetConstructionObjectDump : listForCalculateTotalAccount) {
            Collection<PendingBudgetConstructionAppointmentFunding> accountFundingCollection = new ArrayList();

            accountFundingCollection = (Collection<PendingBudgetConstructionAppointmentFunding>) appointmentFundingEntireMap.get(budgetConstructionObjectDump);
            for (PendingBudgetConstructionAppointmentFunding accountFundingEntry : accountFundingCollection) {
                BudgetConstructionCalculatedSalaryFoundationTracker calculatedSalaryFoundationTracker = accountFundingEntry.getBcnCalculatedSalaryFoundationTracker().get(0);
                totalAccountPositionCsfAmount = totalAccountPositionCsfAmount + new Integer(calculatedSalaryFoundationTracker.getCsfAmount().intValue());
                totalAccountPositionCsfFteQuantity = totalAccountPositionCsfFteQuantity.add(calculatedSalaryFoundationTracker.getCsfFullTimeEmploymentQuantity());
                totalAccountAppointmentRequestedAmount = totalAccountAppointmentRequestedAmount + new Integer(accountFundingEntry.getAppointmentRequestedAmount().intValue());
                totalAccountAppointmentRequestedFteQuantity = totalAccountAppointmentRequestedFteQuantity.add(accountFundingEntry.getAppointmentRequestedFteQuantity());
            }
            BudgetConstructionOrgAccountFundingDetailReportTotal budgetConstructionOrgAccountFundingDetailReportTotal = new BudgetConstructionOrgAccountFundingDetailReportTotal();
            budgetConstructionOrgAccountFundingDetailReportTotal.setBudgetConstructionObjectDump(budgetConstructionObjectDump);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalAccountPositionCsfAmount(totalAccountPositionCsfAmount);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalAccountPositionCsfFteQuantity(totalAccountPositionCsfFteQuantity);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalAccountAppointmentRequestedAmount(totalAccountAppointmentRequestedAmount);
            budgetConstructionOrgAccountFundingDetailReportTotal.setTotalAccountAppointmentRequestedFteQuantity(totalAccountAppointmentRequestedFteQuantity);

            returnCollection.add(budgetConstructionOrgAccountFundingDetailReportTotal);

            totalAccountPositionCsfAmount = new Integer(0);
            totalAccountAppointmentRequestedAmount = new Integer(0);
            totalAccountPositionCsfFteQuantity = BigDecimal.ZERO;
            totalAccountAppointmentRequestedFteQuantity = BigDecimal.ZERO;

        }

        return returnCollection;
    }


    /**
     * Checks wheather or not the entry of account is same
     * 
     * @param BudgetConstructionObjectDump firstbcod
     * @param BudgetConstructionObjectDump secondbcod
     * @return true if the given entries are same; otherwise, return false
     */
    private boolean isSameAccountFundingDetailEntryForTotalObject(BudgetConstructionObjectDump firstbcod, BudgetConstructionObjectDump secondbcod) {
        if (isSameAccountFundingDetailEntryForTotalAccount(firstbcod, secondbcod) && firstbcod.getFinancialObjectCode().equals(secondbcod.getFinancialObjectCode())) {
            return true;
        }
        else
            return false;
    }

    private boolean isSameAccountFundingDetailEntryForTotalAccount(BudgetConstructionObjectDump firstbcod, BudgetConstructionObjectDump secondbcod) {
        if (firstbcod.getChartOfAccountsCode().equals(secondbcod.getChartOfAccountsCode()) && firstbcod.getAccountNumber().equals(secondbcod.getAccountNumber()) && firstbcod.getSubAccountNumber().equals(secondbcod.getSubAccountNumber())) {
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
        BudgetConstructionObjectDump objectDumpEntry = null;
        BudgetConstructionObjectDump objectDumpEntryAux = null;
        List returnList = new ArrayList();
        if ((list != null) && (list.size() > 0)) {
            objectDumpEntry = (BudgetConstructionObjectDump) list.get(count);
            objectDumpEntryAux = (BudgetConstructionObjectDump) list.get(count);
            returnList.add(objectDumpEntry);
            count++;
            while (count < list.size()) {
                objectDumpEntry = (BudgetConstructionObjectDump) list.get(count);
                switch (mode) {
                    case 1: {
                        if (!isSameAccountFundingDetailEntryForTotalObject(objectDumpEntry, objectDumpEntryAux)) {
                            returnList.add(objectDumpEntry);
                            objectDumpEntryAux = objectDumpEntry;
                        }
                    }
                    case 2: {
                        if (!isSameAccountFundingDetailEntryForTotalAccount(objectDumpEntry, objectDumpEntryAux)) {
                            returnList.add(objectDumpEntry);
                            objectDumpEntryAux = objectDumpEntry;
                        }
                    }
                }
                count++;
            }
        }
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

        // returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE); returnList.add(KFSPropertyConstants.SUB_FUND_SORT_CODE);
        // returnList.add(KFSPropertyConstants.FUND_GROUP_CODE); returnList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
        // returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER); returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        // returnList.add(KFSPropertyConstants.INCOME_EXPENSE_CODE);

        return returnList;
    }


    private Collection<PendingBudgetConstructionAppointmentFunding> getPendingBudgetConstructionAppointmentFundingList(Integer universityFiscalYear, BudgetConstructionObjectDump budgetConstructionObjectDump) {
        Collection<PendingBudgetConstructionAppointmentFunding> pendingBudgetConstructionAppointmentFundingList = new ArrayList();
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, budgetConstructionObjectDump.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, budgetConstructionObjectDump.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, budgetConstructionObjectDump.getSubAccountNumber());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, budgetConstructionObjectDump.getFinancialObjectCode());
        pendingBudgetConstructionAppointmentFundingList = businessObjectService.findMatching(PendingBudgetConstructionAppointmentFunding.class, searchCriteria);

        return pendingBudgetConstructionAppointmentFundingList;
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

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionAccountFundingDetailReportDao(BudgetConstructionAccountFundingDetailReportDao budgetConstructionAccountFundingDetailReportDao) {
        this.budgetConstructionAccountFundingDetailReportDao = budgetConstructionAccountFundingDetailReportDao;
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
