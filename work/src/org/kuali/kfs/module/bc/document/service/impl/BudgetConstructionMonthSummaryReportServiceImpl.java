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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.bo.BudgetConstructionMonthSummary;
import org.kuali.module.budget.bo.BudgetConstructionOrgMonthSummaryReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgMonthSummaryReportTotal;
import org.kuali.module.budget.dao.BudgetConstructionMonthSummaryReportDao;
import org.kuali.module.budget.service.BudgetConstructionMonthSummaryReportService;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionLevelSummaryReportService.
 */
@Transactional
public class BudgetConstructionMonthSummaryReportServiceImpl implements BudgetConstructionMonthSummaryReportService {

    BudgetConstructionMonthSummaryReportDao budgetConstructionMonthSummaryReportDao;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateRepotsMonthSummaryTable(java.lang.String)
     */
    public void updateMonthSummaryReport(String personUserIdentifier, boolean consolidateToObjectCodeLevel) {
        budgetConstructionMonthSummaryReportDao.updateReportsMonthSummaryTable(personUserIdentifier, consolidateToObjectCodeLevel);

    }

    /**
     * sets budgetConstructionMonthSummaryReportDao
     * 
     * @param budgetConstructionMonthSummaryReportDao
     */
    public void setBudgetConstructionMonthSummaryReportDao(BudgetConstructionMonthSummaryReportDao budgetConstructionMonthSummaryReportDao) {
        this.budgetConstructionMonthSummaryReportDao = budgetConstructionMonthSummaryReportDao;
    }

    /**
     * @see org.kuali.module.budget.service.BudgetConstructionMonthSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    public Collection<BudgetConstructionOrgMonthSummaryReport> buildReports(Integer universityFiscalYear, String personUserIdentifier) {
        Collection<BudgetConstructionOrgMonthSummaryReport> reportSet = new ArrayList();

        BudgetConstructionOrgMonthSummaryReport orgMonthSummaryReportEntry;
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionMonthSummary> monthSummaryList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionMonthSummary.class, searchCriteria, orderList);


        // Making a list with same organizationChartOfAccountsCode, organizationCode, chartOfAccountsCode, subFundGroupCode
        List listForCalculateLevel = deleteDuplicated((List) monthSummaryList, 1);
        List listForCalculateCons = deleteDuplicated((List) monthSummaryList, 2);
        List listForCalculateType = deleteDuplicated((List) monthSummaryList, 3);
        List listForCalculateIncexp = deleteDuplicated((List) monthSummaryList, 4);


        // Calculate Total Section
        List<BudgetConstructionOrgMonthSummaryReportTotal> monthSummaryTotalLevelList;
        List<BudgetConstructionOrgMonthSummaryReportTotal> monthSummaryTotalConsList;
        List<BudgetConstructionOrgMonthSummaryReportTotal> monthSummaryTotalTypeList;
        List<BudgetConstructionOrgMonthSummaryReportTotal> monthSummaryTotalIncexpList;

        monthSummaryTotalLevelList = calculateLevelTotal((List) monthSummaryList, listForCalculateLevel);
        monthSummaryTotalConsList = calculateConsTotal((List) monthSummaryList, listForCalculateCons);
        monthSummaryTotalTypeList = calculateTypeTotal((List) monthSummaryList, listForCalculateType);
        monthSummaryTotalIncexpList = calculateIncexpTotal((List) monthSummaryList, listForCalculateIncexp);


        for (BudgetConstructionMonthSummary monthSummaryEntry : monthSummaryList) {
            orgMonthSummaryReportEntry = new BudgetConstructionOrgMonthSummaryReport();
            buildReportsHeader(universityFiscalYear, orgMonthSummaryReportEntry, monthSummaryEntry);
            buildReportsBody(orgMonthSummaryReportEntry, monthSummaryEntry);
            buildReportsTotal(orgMonthSummaryReportEntry, monthSummaryEntry, monthSummaryTotalLevelList, monthSummaryTotalConsList, monthSummaryTotalTypeList, monthSummaryTotalIncexpList);
            reportSet.add(orgMonthSummaryReportEntry);
        }

        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionMonthSummary bcas
     */
    public void buildReportsHeader(Integer universityFiscalYear, BudgetConstructionOrgMonthSummaryReport orgMonthSummaryReportEntry, BudgetConstructionMonthSummary monthSummary) {
        String orgChartDesc = monthSummary.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = monthSummary.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = monthSummary.getOrganization().getOrganizationName();
        String reportChartDesc = monthSummary.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = monthSummary.getSubFundGroup().getSubFundGroupCode();
        String subFundGroupDes = monthSummary.getSubFundGroup().getSubFundGroupDescription();
        String fundGroupName = monthSummary.getSubFundGroup().getFundGroupCode();
        String fundGroupDes = monthSummary.getSubFundGroup().getFundGroup().getName();

        Integer prevFiscalyear = universityFiscalYear - 1;
        orgMonthSummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgMonthSummaryReportEntry.setOrgChartOfAccountsCode(monthSummary.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            orgMonthSummaryReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgMonthSummaryReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }

        orgMonthSummaryReportEntry.setOrganizationCode(monthSummary.getOrganizationCode());
        if (orgName == null) {
            orgMonthSummaryReportEntry.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgMonthSummaryReportEntry.setOrganizationName(orgName);
        }

        orgMonthSummaryReportEntry.setChartOfAccountsCode(monthSummary.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgMonthSummaryReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgMonthSummaryReportEntry.setChartOfAccountDescription(chartDesc);
        }

        orgMonthSummaryReportEntry.setFundGroupCode(monthSummary.getSubFundGroup().getFundGroupCode());
        if (fundGroupDes == null) {
            orgMonthSummaryReportEntry.setFundGroupName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            orgMonthSummaryReportEntry.setFundGroupName(fundGroupDes);
        }

        orgMonthSummaryReportEntry.setSubFundGroupCode(monthSummary.getSubFundGroupCode());
        if (subFundGroupDes == null) {
            orgMonthSummaryReportEntry.setSubFundGroupDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_SUBFUNDGROUP_DESCRIPTION));
        }
        else {
            orgMonthSummaryReportEntry.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgMonthSummaryReportEntry.setHeader1("Object Level Name");
        orgMonthSummaryReportEntry.setHeader2("FTE");
        orgMonthSummaryReportEntry.setHeader3("Amount");
        orgMonthSummaryReportEntry.setHeader4("Amount");
        orgMonthSummaryReportEntry.setHeader5(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgMonthSummaryReportEntry.setHeader6(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgMonthSummaryReportEntry.setConsHdr("");

        // For page break for objectMonthCode
        //orgMonthSummaryReportEntry.setFinancialObjectLevelCode(monthSummary.getFinancialObjectLevelCode());
        orgMonthSummaryReportEntry.setIncomeExpenseCode(monthSummary.getIncomeExpenseCode());
        orgMonthSummaryReportEntry.setFinancialConsolidationSortCode(monthSummary.getFinancialConsolidationSortCode());
        orgMonthSummaryReportEntry.setFinancialLevelSortCode(monthSummary.getFinancialLevelSortCode());
        orgMonthSummaryReportEntry.setFinancialObjectCode(monthSummary.getFinancialObjectCode());
        orgMonthSummaryReportEntry.setFinancialSubObjectCode(monthSummary.getFinancialSubObjectCode());
    }

    /**
     * builds report body
     * 
     * @param BudgetConstructionMonthSummary bcas
     */
    public void buildReportsBody(BudgetConstructionOrgMonthSummaryReport orgMonthSummaryReportEntry, BudgetConstructionMonthSummary monthSummary) {
       
        orgMonthSummaryReportEntry.setFinancialObjectCodeName("TEMP NAME");
        orgMonthSummaryReportEntry.setAccountLineAnnualBalanceAmount(new Integer(monthSummary.getAccountLineAnnualBalanceAmount().intValue()));
        orgMonthSummaryReportEntry.setFinancialDocumentMonth1LineAmount(new Integer(monthSummary.getFinancialDocumentMonth10LineAmount().intValue()));
        orgMonthSummaryReportEntry.setFinancialDocumentMonth2LineAmount(new Integer(monthSummary.getFinancialDocumentMonth2LineAmount().intValue()));
        orgMonthSummaryReportEntry.setFinancialDocumentMonth3LineAmount(new Integer(monthSummary.getFinancialDocumentMonth3LineAmount().intValue()));
        orgMonthSummaryReportEntry.setFinancialDocumentMonth4LineAmount(new Integer(monthSummary.getFinancialDocumentMonth4LineAmount().intValue()));
        orgMonthSummaryReportEntry.setFinancialDocumentMonth5LineAmount(new Integer(monthSummary.getFinancialDocumentMonth5LineAmount().intValue()));
        orgMonthSummaryReportEntry.setFinancialDocumentMonth6LineAmount(new Integer(monthSummary.getFinancialDocumentMonth6LineAmount().intValue()));
        orgMonthSummaryReportEntry.setFinancialDocumentMonth7LineAmount(new Integer(monthSummary.getFinancialDocumentMonth7LineAmount().intValue()));
        orgMonthSummaryReportEntry.setFinancialDocumentMonth8LineAmount(new Integer(monthSummary.getFinancialDocumentMonth8LineAmount().intValue()));
        orgMonthSummaryReportEntry.setFinancialDocumentMonth9LineAmount(new Integer(monthSummary.getFinancialDocumentMonth9LineAmount().intValue()));
        orgMonthSummaryReportEntry.setFinancialDocumentMonth10LineAmount(new Integer(monthSummary.getFinancialDocumentMonth10LineAmount().intValue()));
        orgMonthSummaryReportEntry.setFinancialDocumentMonth11LineAmount(new Integer(monthSummary.getFinancialDocumentMonth11LineAmount().intValue()));
        orgMonthSummaryReportEntry.setFinancialDocumentMonth12LineAmount(new Integer(monthSummary.getFinancialDocumentMonth12LineAmount().intValue()));

        
        
    }

    /**
     * builds report total
     * 
     * @param BudgetConstructionMonthSummary bcas
     * @param List reportTotalList
     */     
    public void buildReportsTotal(BudgetConstructionOrgMonthSummaryReport orgMonthSummaryReportEntry, BudgetConstructionMonthSummary monthSummary, List<BudgetConstructionOrgMonthSummaryReportTotal> monthSummaryTotalLevelList, List<BudgetConstructionOrgMonthSummaryReportTotal> monthSummaryTotalConsList, List<BudgetConstructionOrgMonthSummaryReportTotal> monthSummaryTotalTypeList, List<BudgetConstructionOrgMonthSummaryReportTotal> monthSummaryTotalIncexpList) {

        for (BudgetConstructionOrgMonthSummaryReportTotal levelTotal : monthSummaryTotalLevelList) {
            if (isSameMonthSummaryEntryForLevel(monthSummary, levelTotal.getBudgetConstructionMonthSummary())) {
                orgMonthSummaryReportEntry.setLevelAccountLineAnnualBalanceAmount(levelTotal.getLevelAccountLineAnnualBalanceAmount());
                orgMonthSummaryReportEntry.setLevelMonth1LineAmount(levelTotal.getLevelMonth1LineAmount());
                orgMonthSummaryReportEntry.setLevelMonth2LineAmount(levelTotal.getLevelMonth2LineAmount());
                orgMonthSummaryReportEntry.setLevelMonth3LineAmount(levelTotal.getLevelMonth3LineAmount());
                orgMonthSummaryReportEntry.setLevelMonth4LineAmount(levelTotal.getLevelMonth4LineAmount());
                orgMonthSummaryReportEntry.setLevelMonth5LineAmount(levelTotal.getLevelMonth5LineAmount());
                orgMonthSummaryReportEntry.setLevelMonth6LineAmount(levelTotal.getLevelMonth6LineAmount());
                orgMonthSummaryReportEntry.setLevelMonth7LineAmount(levelTotal.getLevelMonth7LineAmount());
                orgMonthSummaryReportEntry.setLevelMonth8LineAmount(levelTotal.getLevelMonth8LineAmount());
                orgMonthSummaryReportEntry.setLevelMonth9LineAmount(levelTotal.getLevelMonth9LineAmount());
                orgMonthSummaryReportEntry.setLevelMonth10LineAmount(levelTotal.getLevelMonth10LineAmount());
                orgMonthSummaryReportEntry.setLevelMonth11LineAmount(levelTotal.getLevelMonth11LineAmount());
                orgMonthSummaryReportEntry.setLevelMonth12LineAmount(levelTotal.getLevelMonth12LineAmount());
                
            }
        }

        for (BudgetConstructionOrgMonthSummaryReportTotal consTotal : monthSummaryTotalConsList) {
            if (isSameMonthSummaryEntryForCons(monthSummary, consTotal.getBudgetConstructionMonthSummary())) {
                orgMonthSummaryReportEntry.setConsAccountLineAnnualBalanceAmount(consTotal.getConsAccountLineAnnualBalanceAmount());
                orgMonthSummaryReportEntry.setConsMonth1LineAmount(consTotal.getConsMonth1LineAmount());
                orgMonthSummaryReportEntry.setConsMonth2LineAmount(consTotal.getConsMonth2LineAmount());
                orgMonthSummaryReportEntry.setConsMonth3LineAmount(consTotal.getConsMonth3LineAmount());
                orgMonthSummaryReportEntry.setConsMonth4LineAmount(consTotal.getConsMonth4LineAmount());
                orgMonthSummaryReportEntry.setConsMonth5LineAmount(consTotal.getConsMonth5LineAmount());
                orgMonthSummaryReportEntry.setConsMonth6LineAmount(consTotal.getConsMonth6LineAmount());
                orgMonthSummaryReportEntry.setConsMonth7LineAmount(consTotal.getConsMonth7LineAmount());
                orgMonthSummaryReportEntry.setConsMonth8LineAmount(consTotal.getConsMonth8LineAmount());
                orgMonthSummaryReportEntry.setConsMonth9LineAmount(consTotal.getConsMonth9LineAmount());
                orgMonthSummaryReportEntry.setConsMonth10LineAmount(consTotal.getConsMonth10LineAmount());
                orgMonthSummaryReportEntry.setConsMonth11LineAmount(consTotal.getConsMonth11LineAmount());
                orgMonthSummaryReportEntry.setConsMonth12LineAmount(consTotal.getConsMonth12LineAmount());
            }
        }

        for (BudgetConstructionOrgMonthSummaryReportTotal typeTotal : monthSummaryTotalTypeList) {
            if (isSameMonthSummaryEntryForType(monthSummary, typeTotal.getBudgetConstructionMonthSummary())) {
                orgMonthSummaryReportEntry.setTypeAccountLineAnnualBalanceAmount(typeTotal.getTypeAccountLineAnnualBalanceAmount());
                orgMonthSummaryReportEntry.setTypeMonth1LineAmount(typeTotal.getTypeMonth1LineAmount());
                orgMonthSummaryReportEntry.setTypeMonth2LineAmount(typeTotal.getTypeMonth2LineAmount());
                orgMonthSummaryReportEntry.setTypeMonth3LineAmount(typeTotal.getTypeMonth3LineAmount());
                orgMonthSummaryReportEntry.setTypeMonth4LineAmount(typeTotal.getTypeMonth4LineAmount());
                orgMonthSummaryReportEntry.setTypeMonth5LineAmount(typeTotal.getTypeMonth5LineAmount());
                orgMonthSummaryReportEntry.setTypeMonth6LineAmount(typeTotal.getTypeMonth6LineAmount());
                orgMonthSummaryReportEntry.setTypeMonth7LineAmount(typeTotal.getTypeMonth7LineAmount());
                orgMonthSummaryReportEntry.setTypeMonth8LineAmount(typeTotal.getTypeMonth8LineAmount());
                orgMonthSummaryReportEntry.setTypeMonth9LineAmount(typeTotal.getTypeMonth9LineAmount());
                orgMonthSummaryReportEntry.setTypeMonth10LineAmount(typeTotal.getTypeMonth10LineAmount());
                orgMonthSummaryReportEntry.setTypeMonth11LineAmount(typeTotal.getTypeMonth11LineAmount());
                orgMonthSummaryReportEntry.setTypeMonth12LineAmount(typeTotal.getTypeMonth12LineAmount());

            }
        }

        for (BudgetConstructionOrgMonthSummaryReportTotal incexpTotal : monthSummaryTotalIncexpList) {
            if (isSameMonthSummaryEntryForIncexp(monthSummary, incexpTotal.getBudgetConstructionMonthSummary())) {
                orgMonthSummaryReportEntry.setRevAccountLineAnnualBalanceAmount(incexpTotal.getRevAccountLineAnnualBalanceAmount());
                orgMonthSummaryReportEntry.setRevMonth1LineAmount(incexpTotal.getRevMonth1LineAmount());
                orgMonthSummaryReportEntry.setRevMonth2LineAmount(incexpTotal.getRevMonth2LineAmount());
                orgMonthSummaryReportEntry.setRevMonth3LineAmount(incexpTotal.getRevMonth3LineAmount());
                orgMonthSummaryReportEntry.setRevMonth4LineAmount(incexpTotal.getRevMonth4LineAmount());
                orgMonthSummaryReportEntry.setRevMonth5LineAmount(incexpTotal.getRevMonth5LineAmount());
                orgMonthSummaryReportEntry.setRevMonth6LineAmount(incexpTotal.getRevMonth6LineAmount());
                orgMonthSummaryReportEntry.setRevMonth7LineAmount(incexpTotal.getRevMonth7LineAmount());
                orgMonthSummaryReportEntry.setRevMonth8LineAmount(incexpTotal.getRevMonth8LineAmount());
                orgMonthSummaryReportEntry.setRevMonth9LineAmount(incexpTotal.getRevMonth9LineAmount());
                orgMonthSummaryReportEntry.setRevMonth10LineAmount(incexpTotal.getRevMonth10LineAmount());
                orgMonthSummaryReportEntry.setRevMonth11LineAmount(incexpTotal.getRevMonth11LineAmount());
                orgMonthSummaryReportEntry.setRevMonth12LineAmount(incexpTotal.getRevMonth12LineAmount());
                
                orgMonthSummaryReportEntry.setExpAccountLineAnnualBalanceAmount(incexpTotal.getExpAccountLineAnnualBalanceAmount());
                orgMonthSummaryReportEntry.setExpMonth1LineAmount(incexpTotal.getExpMonth1LineAmount());
                orgMonthSummaryReportEntry.setExpMonth2LineAmount(incexpTotal.getExpMonth2LineAmount());
                orgMonthSummaryReportEntry.setExpMonth3LineAmount(incexpTotal.getExpMonth3LineAmount());
                orgMonthSummaryReportEntry.setExpMonth4LineAmount(incexpTotal.getExpMonth4LineAmount());
                orgMonthSummaryReportEntry.setExpMonth5LineAmount(incexpTotal.getExpMonth5LineAmount());
                orgMonthSummaryReportEntry.setExpMonth6LineAmount(incexpTotal.getExpMonth6LineAmount());
                orgMonthSummaryReportEntry.setExpMonth7LineAmount(incexpTotal.getExpMonth7LineAmount());
                orgMonthSummaryReportEntry.setExpMonth8LineAmount(incexpTotal.getExpMonth8LineAmount());
                orgMonthSummaryReportEntry.setExpMonth9LineAmount(incexpTotal.getExpMonth9LineAmount());
                orgMonthSummaryReportEntry.setExpMonth10LineAmount(incexpTotal.getExpMonth10LineAmount());
                orgMonthSummaryReportEntry.setExpMonth11LineAmount(incexpTotal.getExpMonth11LineAmount());
                orgMonthSummaryReportEntry.setExpMonth12LineAmount(incexpTotal.getExpMonth12LineAmount());
                
                
            }
        }

    }


    public List calculateLevelTotal(List<BudgetConstructionMonthSummary> bcmsList, List<BudgetConstructionMonthSummary> simpleList) {
        Integer levelAccountLineAnnualBalanceAmount = new Integer(0);
        Integer levelMonth1LineAmount = new Integer(0);
        Integer levelMonth2LineAmount = new Integer(0);
        Integer levelMonth3LineAmount = new Integer(0);
        Integer levelMonth4LineAmount = new Integer(0);
        Integer levelMonth5LineAmount = new Integer(0);
        Integer levelMonth6LineAmount = new Integer(0);
        Integer levelMonth7LineAmount = new Integer(0);
        Integer levelMonth8LineAmount = new Integer(0);
        Integer levelMonth9LineAmount = new Integer(0);
        Integer levelMonth10LineAmount = new Integer(0);
        Integer levelMonth11LineAmount = new Integer(0);
        Integer levelMonth12LineAmount = new Integer(0);

        List returnList = new ArrayList();
        for (BudgetConstructionMonthSummary simpleBclsEntry : simpleList) {
            BudgetConstructionOrgMonthSummaryReportTotal bcMonthTotal = new BudgetConstructionOrgMonthSummaryReportTotal();
            for (BudgetConstructionMonthSummary bcmsListEntry : bcmsList) {
                if (isSameMonthSummaryEntryForLevel(simpleBclsEntry, bcmsListEntry)) {
                    levelAccountLineAnnualBalanceAmount += bcmsListEntry.getAccountLineAnnualBalanceAmount().intValue();
                    levelMonth1LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth1LineAmount().intValue());
                    levelMonth2LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth2LineAmount().intValue());
                    levelMonth3LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth3LineAmount().intValue());
                    levelMonth4LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth4LineAmount().intValue());
                    levelMonth5LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth5LineAmount().intValue());
                    levelMonth6LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth6LineAmount().intValue());
                    levelMonth7LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth7LineAmount().intValue());
                    levelMonth8LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth8LineAmount().intValue());
                    levelMonth9LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth9LineAmount().intValue());
                    levelMonth10LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth10LineAmount().intValue());
                    levelMonth11LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth11LineAmount().intValue());
                    levelMonth12LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth12LineAmount().intValue());     
                }
            }
            bcMonthTotal.setBudgetConstructionMonthSummary(simpleBclsEntry);
            bcMonthTotal.setLevelAccountLineAnnualBalanceAmount(levelAccountLineAnnualBalanceAmount);
            bcMonthTotal.setLevelMonth1LineAmount(levelMonth1LineAmount);
            bcMonthTotal.setLevelMonth2LineAmount(levelMonth1LineAmount);
            bcMonthTotal.setLevelMonth3LineAmount(levelMonth1LineAmount);
            bcMonthTotal.setLevelMonth4LineAmount(levelMonth1LineAmount);
            bcMonthTotal.setLevelMonth5LineAmount(levelMonth1LineAmount);
            bcMonthTotal.setLevelMonth6LineAmount(levelMonth1LineAmount);
            bcMonthTotal.setLevelMonth7LineAmount(levelMonth1LineAmount);
            bcMonthTotal.setLevelMonth8LineAmount(levelMonth1LineAmount);
            bcMonthTotal.setLevelMonth9LineAmount(levelMonth1LineAmount);
            bcMonthTotal.setLevelMonth10LineAmount(levelMonth1LineAmount);
            bcMonthTotal.setLevelMonth11LineAmount(levelMonth1LineAmount);
            bcMonthTotal.setLevelMonth12LineAmount(levelMonth1LineAmount);
            
            levelAccountLineAnnualBalanceAmount = new Integer(0);
            levelMonth1LineAmount = new Integer(0);
            levelMonth2LineAmount = new Integer(0);
            levelMonth3LineAmount = new Integer(0);
            levelMonth4LineAmount = new Integer(0);
            levelMonth5LineAmount = new Integer(0);
            levelMonth6LineAmount = new Integer(0);
            levelMonth7LineAmount = new Integer(0);
            levelMonth8LineAmount = new Integer(0);
            levelMonth9LineAmount = new Integer(0);
            levelMonth10LineAmount = new Integer(0);
            levelMonth11LineAmount = new Integer(0);
            levelMonth12LineAmount = new Integer(0);
        }
        return returnList;
    }


    public List calculateConsTotal(List<BudgetConstructionMonthSummary> bcmsList, List<BudgetConstructionMonthSummary> simpleList) {
        
        Integer consAccountLineAnnualBalanceAmount = new Integer(0);
        Integer consMonth1LineAmount = new Integer(0);
        Integer consMonth2LineAmount = new Integer(0);
        Integer consMonth3LineAmount = new Integer(0);
        Integer consMonth4LineAmount = new Integer(0);
        Integer consMonth5LineAmount = new Integer(0);
        Integer consMonth6LineAmount = new Integer(0);
        Integer consMonth7LineAmount = new Integer(0);
        Integer consMonth8LineAmount = new Integer(0);
        Integer consMonth9LineAmount = new Integer(0);
        Integer consMonth10LineAmount = new Integer(0);
        Integer consMonth11LineAmount = new Integer(0);
        Integer consMonth12LineAmount = new Integer(0);

        List returnList = new ArrayList();


        for (BudgetConstructionMonthSummary simpleBclsEntry : simpleList) {
            BudgetConstructionOrgMonthSummaryReportTotal bcMonthTotal = new BudgetConstructionOrgMonthSummaryReportTotal();
            for (BudgetConstructionMonthSummary bcmsListEntry : bcmsList) {
                if (isSameMonthSummaryEntryForCons(simpleBclsEntry, bcmsListEntry)) {
                    consAccountLineAnnualBalanceAmount += bcmsListEntry.getAccountLineAnnualBalanceAmount().intValue();
                    consMonth1LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth1LineAmount().intValue());
                    consMonth2LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth2LineAmount().intValue());
                    consMonth3LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth3LineAmount().intValue());
                    consMonth4LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth4LineAmount().intValue());
                    consMonth5LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth5LineAmount().intValue());
                    consMonth6LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth6LineAmount().intValue());
                    consMonth7LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth7LineAmount().intValue());
                    consMonth8LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth8LineAmount().intValue());
                    consMonth9LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth9LineAmount().intValue());
                    consMonth10LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth10LineAmount().intValue());
                    consMonth11LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth11LineAmount().intValue());
                    consMonth12LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth12LineAmount().intValue());   

                }
            }
            bcMonthTotal.setBudgetConstructionMonthSummary(simpleBclsEntry);
            bcMonthTotal.setConsAccountLineAnnualBalanceAmount(consAccountLineAnnualBalanceAmount);
            bcMonthTotal.setConsMonth1LineAmount(consMonth1LineAmount);
            bcMonthTotal.setConsMonth2LineAmount(consMonth1LineAmount);
            bcMonthTotal.setConsMonth3LineAmount(consMonth1LineAmount);
            bcMonthTotal.setConsMonth4LineAmount(consMonth1LineAmount);
            bcMonthTotal.setConsMonth5LineAmount(consMonth1LineAmount);
            bcMonthTotal.setConsMonth6LineAmount(consMonth1LineAmount);
            bcMonthTotal.setConsMonth7LineAmount(consMonth1LineAmount);
            bcMonthTotal.setConsMonth8LineAmount(consMonth1LineAmount);
            bcMonthTotal.setConsMonth9LineAmount(consMonth1LineAmount);
            bcMonthTotal.setConsMonth10LineAmount(consMonth1LineAmount);
            bcMonthTotal.setConsMonth11LineAmount(consMonth1LineAmount);
            bcMonthTotal.setConsMonth12LineAmount(consMonth1LineAmount);
            
            consAccountLineAnnualBalanceAmount = new Integer(0);
            consMonth1LineAmount = new Integer(0);
            consMonth2LineAmount = new Integer(0);
            consMonth3LineAmount = new Integer(0);
            consMonth4LineAmount = new Integer(0);
            consMonth5LineAmount = new Integer(0);
            consMonth6LineAmount = new Integer(0);
            consMonth7LineAmount = new Integer(0);
            consMonth8LineAmount = new Integer(0);
            consMonth9LineAmount = new Integer(0);
            consMonth10LineAmount = new Integer(0);
            consMonth11LineAmount = new Integer(0);
            consMonth12LineAmount = new Integer(0);

        }


        return returnList;
    }


    public List calculateTypeTotal(List<BudgetConstructionMonthSummary> bcmsList, List<BudgetConstructionMonthSummary> simpleList) {

        Integer typeAccountLineAnnualBalanceAmount = new Integer(0);
        Integer typeMonth1LineAmount = new Integer(0);
        Integer typeMonth2LineAmount = new Integer(0);
        Integer typeMonth3LineAmount = new Integer(0);
        Integer typeMonth4LineAmount = new Integer(0);
        Integer typeMonth5LineAmount = new Integer(0);
        Integer typeMonth6LineAmount = new Integer(0);
        Integer typeMonth7LineAmount = new Integer(0);
        Integer typeMonth8LineAmount = new Integer(0);
        Integer typeMonth9LineAmount = new Integer(0);
        Integer typeMonth10LineAmount = new Integer(0);
        Integer typeMonth11LineAmount = new Integer(0);
        Integer typeMonth12LineAmount = new Integer(0);

        List returnList = new ArrayList();
        for (BudgetConstructionMonthSummary simpleBclsEntry : simpleList) {
            BudgetConstructionOrgMonthSummaryReportTotal bcMonthTotal = new BudgetConstructionOrgMonthSummaryReportTotal();
            for (BudgetConstructionMonthSummary bcmsListEntry : bcmsList) {
                if (isSameMonthSummaryEntryForType(simpleBclsEntry, bcmsListEntry)) {
                    typeAccountLineAnnualBalanceAmount += bcmsListEntry.getAccountLineAnnualBalanceAmount().intValue();
                    typeMonth1LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth1LineAmount().intValue());
                    typeMonth2LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth2LineAmount().intValue());
                    typeMonth3LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth3LineAmount().intValue());
                    typeMonth4LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth4LineAmount().intValue());
                    typeMonth5LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth5LineAmount().intValue());
                    typeMonth6LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth6LineAmount().intValue());
                    typeMonth7LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth7LineAmount().intValue());
                    typeMonth8LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth8LineAmount().intValue());
                    typeMonth9LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth9LineAmount().intValue());
                    typeMonth10LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth10LineAmount().intValue());
                    typeMonth11LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth11LineAmount().intValue());
                    typeMonth12LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth12LineAmount().intValue());   
                }
            }
            
            
            bcMonthTotal.setBudgetConstructionMonthSummary(simpleBclsEntry);
            bcMonthTotal.setTypeAccountLineAnnualBalanceAmount(typeAccountLineAnnualBalanceAmount);
            bcMonthTotal.setTypeMonth1LineAmount(typeMonth1LineAmount);
            bcMonthTotal.setTypeMonth2LineAmount(typeMonth1LineAmount);
            bcMonthTotal.setTypeMonth3LineAmount(typeMonth1LineAmount);
            bcMonthTotal.setTypeMonth4LineAmount(typeMonth1LineAmount);
            bcMonthTotal.setTypeMonth5LineAmount(typeMonth1LineAmount);
            bcMonthTotal.setTypeMonth6LineAmount(typeMonth1LineAmount);
            bcMonthTotal.setTypeMonth7LineAmount(typeMonth1LineAmount);
            bcMonthTotal.setTypeMonth8LineAmount(typeMonth1LineAmount);
            bcMonthTotal.setTypeMonth9LineAmount(typeMonth1LineAmount);
            bcMonthTotal.setTypeMonth10LineAmount(typeMonth1LineAmount);
            bcMonthTotal.setTypeMonth11LineAmount(typeMonth1LineAmount);
            bcMonthTotal.setTypeMonth12LineAmount(typeMonth1LineAmount);
            
            typeAccountLineAnnualBalanceAmount = new Integer(0);
            typeMonth1LineAmount = new Integer(0);
            typeMonth2LineAmount = new Integer(0);
            typeMonth3LineAmount = new Integer(0);
            typeMonth4LineAmount = new Integer(0);
            typeMonth5LineAmount = new Integer(0);
            typeMonth6LineAmount = new Integer(0);
            typeMonth7LineAmount = new Integer(0);
            typeMonth8LineAmount = new Integer(0);
            typeMonth9LineAmount = new Integer(0);
            typeMonth10LineAmount = new Integer(0);
            typeMonth11LineAmount = new Integer(0);
            typeMonth12LineAmount = new Integer(0);
            
        }

        return returnList;
    }


    public List calculateIncexpTotal(List<BudgetConstructionMonthSummary> bcmsList, List<BudgetConstructionMonthSummary> simpleList) {

        Integer revAccountLineAnnualBalanceAmount = new Integer(0);
        Integer revMonth1LineAmount = new Integer(0);
        Integer revMonth2LineAmount = new Integer(0);
        Integer revMonth3LineAmount = new Integer(0);
        Integer revMonth4LineAmount = new Integer(0);
        Integer revMonth5LineAmount = new Integer(0);
        Integer revMonth6LineAmount = new Integer(0);
        Integer revMonth7LineAmount = new Integer(0);
        Integer revMonth8LineAmount = new Integer(0);
        Integer revMonth9LineAmount = new Integer(0);
        Integer revMonth10LineAmount = new Integer(0);
        Integer revMonth11LineAmount = new Integer(0);
        Integer revMonth12LineAmount = new Integer(0);
        
        Integer expAccountLineAnnualBalanceAmount = new Integer(0);
        Integer expMonth1LineAmount = new Integer(0);
        Integer expMonth2LineAmount = new Integer(0);
        Integer expMonth3LineAmount = new Integer(0);
        Integer expMonth4LineAmount = new Integer(0);
        Integer expMonth5LineAmount = new Integer(0);
        Integer expMonth6LineAmount = new Integer(0);
        Integer expMonth7LineAmount = new Integer(0);
        Integer expMonth8LineAmount = new Integer(0);
        Integer expMonth9LineAmount = new Integer(0);
        Integer expMonth10LineAmount = new Integer(0);
        Integer expMonth11LineAmount = new Integer(0);
        Integer expMonth12LineAmount = new Integer(0);
        

        List returnList = new ArrayList();

        for (BudgetConstructionMonthSummary simpleBclsEntry : simpleList) {
            BudgetConstructionOrgMonthSummaryReportTotal bcMonthTotal = new BudgetConstructionOrgMonthSummaryReportTotal();
            for (BudgetConstructionMonthSummary bcmsListEntry : bcmsList) {
                if (isSameMonthSummaryEntryForIncexp(simpleBclsEntry, bcmsListEntry)) {
                    if (bcmsListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)){
                        revAccountLineAnnualBalanceAmount += bcmsListEntry.getAccountLineAnnualBalanceAmount().intValue();
                        revMonth1LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth1LineAmount().intValue());
                        revMonth2LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth2LineAmount().intValue());
                        revMonth3LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth3LineAmount().intValue());
                        revMonth4LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth4LineAmount().intValue());
                        revMonth5LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth5LineAmount().intValue());
                        revMonth6LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth6LineAmount().intValue());
                        revMonth7LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth7LineAmount().intValue());
                        revMonth8LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth8LineAmount().intValue());
                        revMonth9LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth9LineAmount().intValue());
                        revMonth10LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth10LineAmount().intValue());
                        revMonth11LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth11LineAmount().intValue());
                        revMonth12LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth12LineAmount().intValue());
                    } else {
                        expAccountLineAnnualBalanceAmount += bcmsListEntry.getAccountLineAnnualBalanceAmount().intValue();
                        expMonth1LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth1LineAmount().intValue());
                        expMonth2LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth2LineAmount().intValue());
                        expMonth3LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth3LineAmount().intValue());
                        expMonth4LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth4LineAmount().intValue());
                        expMonth5LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth5LineAmount().intValue());
                        expMonth6LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth6LineAmount().intValue());
                        expMonth7LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth7LineAmount().intValue());
                        expMonth8LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth8LineAmount().intValue());
                        expMonth9LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth9LineAmount().intValue());
                        expMonth10LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth10LineAmount().intValue());
                        expMonth11LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth11LineAmount().intValue());
                        expMonth12LineAmount += new Integer(bcmsListEntry.getFinancialDocumentMonth12LineAmount().intValue());
                    }

                }
            }
            
            bcMonthTotal.setBudgetConstructionMonthSummary(simpleBclsEntry);
            bcMonthTotal.setRevAccountLineAnnualBalanceAmount(revAccountLineAnnualBalanceAmount);
            bcMonthTotal.setRevMonth1LineAmount(revMonth1LineAmount);
            bcMonthTotal.setRevMonth2LineAmount(revMonth1LineAmount);
            bcMonthTotal.setRevMonth3LineAmount(revMonth1LineAmount);
            bcMonthTotal.setRevMonth4LineAmount(revMonth1LineAmount);
            bcMonthTotal.setRevMonth5LineAmount(revMonth1LineAmount);
            bcMonthTotal.setRevMonth6LineAmount(revMonth1LineAmount);
            bcMonthTotal.setRevMonth7LineAmount(revMonth1LineAmount);
            bcMonthTotal.setRevMonth8LineAmount(revMonth1LineAmount);
            bcMonthTotal.setRevMonth9LineAmount(revMonth1LineAmount);
            bcMonthTotal.setRevMonth10LineAmount(revMonth1LineAmount);
            bcMonthTotal.setRevMonth11LineAmount(revMonth1LineAmount);
            bcMonthTotal.setRevMonth12LineAmount(revMonth1LineAmount);
            
            bcMonthTotal.setBudgetConstructionMonthSummary(simpleBclsEntry);
            bcMonthTotal.setExpAccountLineAnnualBalanceAmount(expAccountLineAnnualBalanceAmount);
            bcMonthTotal.setExpMonth1LineAmount(expMonth1LineAmount);
            bcMonthTotal.setExpMonth2LineAmount(expMonth1LineAmount);
            bcMonthTotal.setExpMonth3LineAmount(expMonth1LineAmount);
            bcMonthTotal.setExpMonth4LineAmount(expMonth1LineAmount);
            bcMonthTotal.setExpMonth5LineAmount(expMonth1LineAmount);
            bcMonthTotal.setExpMonth6LineAmount(expMonth1LineAmount);
            bcMonthTotal.setExpMonth7LineAmount(expMonth1LineAmount);
            bcMonthTotal.setExpMonth8LineAmount(expMonth1LineAmount);
            bcMonthTotal.setExpMonth9LineAmount(expMonth1LineAmount);
            bcMonthTotal.setExpMonth10LineAmount(expMonth1LineAmount);
            bcMonthTotal.setExpMonth11LineAmount(expMonth1LineAmount);
            bcMonthTotal.setExpMonth12LineAmount(expMonth1LineAmount);
            
            revAccountLineAnnualBalanceAmount = new Integer(0);
            revMonth1LineAmount = new Integer(0);
            revMonth2LineAmount = new Integer(0);
            revMonth3LineAmount = new Integer(0);
            revMonth4LineAmount = new Integer(0);
            revMonth5LineAmount = new Integer(0);
            revMonth6LineAmount = new Integer(0);
            revMonth7LineAmount = new Integer(0);
            revMonth8LineAmount = new Integer(0);
            revMonth9LineAmount = new Integer(0);
            revMonth10LineAmount = new Integer(0);
            revMonth11LineAmount = new Integer(0);
            revMonth12LineAmount = new Integer(0);
            
            expAccountLineAnnualBalanceAmount = new Integer(0);
            expMonth1LineAmount = new Integer(0);
            expMonth2LineAmount = new Integer(0);
            expMonth3LineAmount = new Integer(0);
            expMonth4LineAmount = new Integer(0);
            expMonth5LineAmount = new Integer(0);
            expMonth6LineAmount = new Integer(0);
            expMonth7LineAmount = new Integer(0);
            expMonth8LineAmount = new Integer(0);
            expMonth9LineAmount = new Integer(0);
            expMonth10LineAmount = new Integer(0);
            expMonth11LineAmount = new Integer(0);
            expMonth12LineAmount = new Integer(0);
        }


        return returnList;
    }


    public boolean isSameMonthSummaryEntryForLevel(BudgetConstructionMonthSummary firstBcms, BudgetConstructionMonthSummary secondBcms) {
        if (isSameMonthSummaryEntryForCons(firstBcms, secondBcms) && firstBcms.getFinancialLevelSortCode().equals(secondBcms.getFinancialLevelSortCode())) {
            return true;
        }
        else
            return false;
    }


    public boolean isSameMonthSummaryEntryForCons(BudgetConstructionMonthSummary firstBcms, BudgetConstructionMonthSummary secondBcms) {
        if (isSameMonthSummaryEntryForType(firstBcms, secondBcms) && firstBcms.getFinancialConsolidationSortCode().equals(secondBcms.getFinancialConsolidationSortCode())) {
            return true;
        }
        else
            return false;
    }


    public boolean isSameMonthSummaryEntryForType(BudgetConstructionMonthSummary firstBcms, BudgetConstructionMonthSummary secondBcms) {
        if (isSameMonthSummaryEntryForIncexp(firstBcms, secondBcms) && firstBcms.getIncomeExpenseCode().equals(secondBcms.getIncomeExpenseCode())) {
            return true;
        }

        else
            return false;
    }

    public boolean isSameMonthSummaryEntryForIncexp(BudgetConstructionMonthSummary firstBcms, BudgetConstructionMonthSummary secondBcms) {
        if (firstBcms.getOrganizationChartOfAccountsCode().equals(secondBcms.getOrganizationChartOfAccountsCode()) && firstBcms.getOrganizationCode().equals(secondBcms.getOrganizationCode()) && firstBcms.getSubFundGroupCode().equals(secondBcms.getSubFundGroupCode()) && firstBcms.getChartOfAccountsCode().equals(secondBcms.getChartOfAccountsCode())) {
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
    public List deleteDuplicated(List list, int mode) {

        // mode 1 is for getting a list of cons
        // mode 2 is for getting a list of gexp and type
        // mode 3 is for getting a list of total

        int count = 0;
        BudgetConstructionMonthSummary monthSummaryEntry = null;
        BudgetConstructionMonthSummary monthSummaryEntryAux = null;
        List returnList = new ArrayList();
        if ((list != null) && (list.size() > 0)) {
            monthSummaryEntry = (BudgetConstructionMonthSummary) list.get(count);
            monthSummaryEntryAux = (BudgetConstructionMonthSummary) list.get(count);
            returnList.add(monthSummaryEntry);
            count++;
            while (count < list.size()) {
                monthSummaryEntry = (BudgetConstructionMonthSummary) list.get(count);
                switch (mode) {
                    case 1: {
                        if (!isSameMonthSummaryEntryForLevel(monthSummaryEntry, monthSummaryEntryAux)) {
                            returnList.add(monthSummaryEntry);
                            monthSummaryEntryAux = monthSummaryEntry;
                        }
                    }
                    case 2: {
                        if (!isSameMonthSummaryEntryForCons(monthSummaryEntry, monthSummaryEntryAux)) {
                            returnList.add(monthSummaryEntry);
                            monthSummaryEntryAux = monthSummaryEntry;
                        }
                    }
                    case 3: {
                        if (!isSameMonthSummaryEntryForType(monthSummaryEntry, monthSummaryEntryAux)) {
                            returnList.add(monthSummaryEntry);
                            monthSummaryEntryAux = monthSummaryEntry;
                        }
                    }
                    case 4: {
                        if (!isSameMonthSummaryEntryForIncexp(monthSummaryEntry, monthSummaryEntryAux)) {
                            returnList.add(monthSummaryEntry);
                            monthSummaryEntryAux = monthSummaryEntry;
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
        returnList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.INCOME_EXPENSE_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_LEVEL_SORT_CODE);
        return returnList;
    }

    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
