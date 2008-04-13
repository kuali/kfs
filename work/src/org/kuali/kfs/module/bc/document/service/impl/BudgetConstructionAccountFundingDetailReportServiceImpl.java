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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


        List listForCalculateTotalObject = deleteDuplicated((List) accountFundingDetailList, 1);
        List listForCalculateTotalAccount = deleteDuplicated((List) accountFundingDetailList, 2);

        // Calculate Total Section
        List<BudgetConstructionOrgAccountFundingDetailReport> fundingDetailTotalObject;
        List<BudgetConstructionOrgAccountFundingDetailReport> fundingDetailTotalAccount;

        // fundingDetailTotalObject = calculateObjectTotal((List) accountFundingDetailList, fundingDetailTotalObject);
        // fundingDetailTotalAccount = calculateAccountTotal((List) accountFundingDetailList, fundingDetailTotalAccount);

        for (BudgetConstructionObjectDump accountFundingDetailEntry : accountFundingDetailList) {


            // orgAccountFundingDetailReportEntry = new BudgetConstructionOrgAccountFundingDetailReport();
            // buildReportsHeader(universityFiscalYear, orgAccountFundingDetailReportEntry, accountFundingDetailEntry);
            // buildReportsBody(universityFiscalYear, orgAccountFundingDetailReportEntry, accountFundingDetailEntry);
            // buildReportsTotal(orgAccountFundingDetailReportEntry, accountFundingDetailEntry,
            // orgAccountFundingDetailReportTotalList);
            // reportSet.add(orgAccountFundingDetailReportEntry);
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
    }

    /**
     * builds report body
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgAccountFundingDetailReport orgAccountFundingDetailReportEntry, BudgetConstructionObjectDump accountFundingDetail) {
        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent;
        PendingBudgetConstructionAppointmentFunding pendingBudgetConstructionAppointmentFunding;
        BudgetConstructionAdministrativePost budgetConstructionAdministrativePost;
        BudgetConstructionPosition budgetConstructionPosition;
        BudgetConstructionCalculatedSalaryFoundationTracker budgetConstructionCalculatedSalaryFoundationTracker;


        UniversalUser user = new UniversalUser();
        String emplId;
        try {
            user = universalUserService.getUniversalUser(accountFundingDetail.getPersonUniversalIdentifier());
        }
        catch (UserNotFoundException e) {

        }
        emplId = user.getPersonPayrollIdentifier();


        // get budgetConstructionIntendedIncumbent
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.EMPLID, emplId);
        budgetConstructionIntendedIncumbent = (BudgetConstructionIntendedIncumbent) businessObjectService.findByPrimaryKey(BudgetConstructionIntendedIncumbent.class, searchCriteria);


    }


    /**
     * builds report total
     * 
     * @param BudgetConstructionObjectDump bcod
     * @param List reportTotalList
     */
    public void buildReportsTotal(BudgetConstructionOrgAccountFundingDetailReport orgAccountFundingDetailReportEntry, BudgetConstructionObjectDump accountFundingDetail, List reportTotalList) {
    }

    /**
     * Calculates total part of report
     * 
     * @param List bcodList
     * @param List simpleList
     */
    public List calculateTotal(List bcodList, List simpleList) {

        List returnList = new ArrayList();

        return returnList;
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
