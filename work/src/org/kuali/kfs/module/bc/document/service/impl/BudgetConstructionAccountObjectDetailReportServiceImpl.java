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

import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.bo.BudgetConstructionAccountBalance;
import org.kuali.module.budget.bo.BudgetConstructionObjectSummary;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountObjectDetailReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountObjectDetailReportTotal;


import org.kuali.module.budget.dao.BudgetConstructionAccountObjectDetailReportDao;
import org.kuali.module.budget.service.BudgetConstructionAccountObjectDetailReportService;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.util.BudgetConstructionReportHelper;
import org.kuali.module.chart.bo.ObjectCode;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionAccountObjectDetailReportServiceImpl implements BudgetConstructionAccountObjectDetailReportService {

    private BudgetConstructionAccountObjectDetailReportDao budgetConstructionAccountObjectDetailReportDao;
    private KualiConfigurationService kualiConfigurationService;
    private BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    private BusinessObjectService businessObjectService;
    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateSubFundSummaryReport(java.lang.String)
     */
    public void updateAccountObjectDetailReport(String personUserIdentifier, boolean consolidated) {
        if (consolidated){
            budgetConstructionAccountObjectDetailReportDao.updateReportsAccountObjectConsolidatedTable(personUserIdentifier);
        } else {
            budgetConstructionAccountObjectDetailReportDao.updateReportsAccountObjectDetailTable(personUserIdentifier);
        }
    }
   
    public Collection<BudgetConstructionOrgAccountObjectDetailReport> buildReports(Integer universityFiscalYear,  String personUserIdentifier){
        Collection<BudgetConstructionOrgAccountObjectDetailReport> reportSet = new ArrayList();
        Collection<BudgetConstructionAccountBalance> accountObjectDetailList;
        
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        List<String> orderList = buildOrderByList();
        accountObjectDetailList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionAccountBalance.class, searchCriteria, orderList);

        

        // 
        List listForCalculateObject = deleteDuplicated((List) accountObjectDetailList, 1);
        List listForCalculateLevel = deleteDuplicated((List) accountObjectDetailList, 2);
        List listForCalculateGexpAndType = deleteDuplicated((List) accountObjectDetailList, 3);
        List listForCalculateAccountTotal = deleteDuplicated((List) accountObjectDetailList, 4);
        List listForCalculateSubFundTotal = deleteDuplicated((List) accountObjectDetailList, 5);

        // Calculate Total Section
        List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailTotalObjectList;
        List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailTotalLevelList;
        List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailTotalGexpAndTypeList;
        List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailAccountTotalList;
        List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailSubFundTotalList;
        
        accountObjectDetailTotalObjectList = calculateObjectTotal((List) accountObjectDetailList, listForCalculateObject);
        accountObjectDetailTotalLevelList = calculateLevelTotal((List) accountObjectDetailList, listForCalculateLevel);
        accountObjectDetailTotalGexpAndTypeList = calculateGexpAndTypeTotal((List) accountObjectDetailList, listForCalculateGexpAndType);
        accountObjectDetailAccountTotalList = calculateAccountTotal((List) accountObjectDetailList, listForCalculateAccountTotal);
        accountObjectDetailSubFundTotalList = calculateSubFundTotal((List) accountObjectDetailList, listForCalculateSubFundTotal);

        for (BudgetConstructionAccountBalance accountObjectDetailEntry : accountObjectDetailList) {
            BudgetConstructionOrgAccountObjectDetailReport accountObjectDetailReport = new BudgetConstructionOrgAccountObjectDetailReport();
            buildReportsHeader(universityFiscalYear, accountObjectDetailReport, accountObjectDetailEntry);
            buildReportsBody(universityFiscalYear, accountObjectDetailReport, accountObjectDetailEntry);
            buildReportsTotal(accountObjectDetailReport, accountObjectDetailEntry, accountObjectDetailTotalObjectList, accountObjectDetailTotalLevelList, accountObjectDetailTotalGexpAndTypeList, accountObjectDetailAccountTotalList, accountObjectDetailSubFundTotalList);
            reportSet.add(accountObjectDetailReport);
        }
        return reportSet;
    }
    
    
    
    
    
    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectSummary bcas
     */
    private void buildReportsHeader(Integer universityFiscalYear, BudgetConstructionOrgAccountObjectDetailReport orgAccountObjectDetailReportEntry, BudgetConstructionAccountBalance accountBalance) {
        String orgChartDesc = accountBalance.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = accountBalance.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName ="";
        try {
            orgName = accountBalance.getOrganization().getOrganizationName();
        }
        catch (PersistenceBrokerException e) {
        }
        String reportChartDesc = accountBalance.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = accountBalance.getSubFundGroup().getSubFundGroupCode();
        String subFundGroupDes = accountBalance.getSubFundGroup().getSubFundGroupDescription();
        String fundGroupName = accountBalance.getSubFundGroup().getFundGroupCode();
        String fundGroupDes = accountBalance.getSubFundGroup().getFundGroup().getName();

        Integer prevFiscalyear = universityFiscalYear - 1;
        orgAccountObjectDetailReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgAccountObjectDetailReportEntry.setOrgChartOfAccountsCode(accountBalance.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            orgAccountObjectDetailReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgAccountObjectDetailReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }

        orgAccountObjectDetailReportEntry.setOrganizationCode(accountBalance.getOrganizationCode());
        if (orgName == null) {
            orgAccountObjectDetailReportEntry.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgAccountObjectDetailReportEntry.setOrganizationName(orgName);
        }

        orgAccountObjectDetailReportEntry.setChartOfAccountsCode(accountBalance.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgAccountObjectDetailReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgAccountObjectDetailReportEntry.setChartOfAccountDescription(chartDesc);
        }

        orgAccountObjectDetailReportEntry.setFundGroupCode(accountBalance.getSubFundGroup().getFundGroupCode());
        if (fundGroupDes == null) {
            orgAccountObjectDetailReportEntry.setFundGroupName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            orgAccountObjectDetailReportEntry.setFundGroupName(fundGroupDes);
        }

        orgAccountObjectDetailReportEntry.setSubFundGroupCode(accountBalance.getSubFundGroupCode());
        if (subFundGroupDes == null) {
            orgAccountObjectDetailReportEntry.setSubFundGroupDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_SUBFUNDGROUP_DESCRIPTION));
        }
        else {
            orgAccountObjectDetailReportEntry.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgAccountObjectDetailReportEntry.setBaseFy(prevPrevFiscalyear.toString() + " - " + prevFiscalyear.toString().substring(2, 4));
        orgAccountObjectDetailReportEntry.setReqFy(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgAccountObjectDetailReportEntry.setHeader1("Object Level Name");
        orgAccountObjectDetailReportEntry.setHeader2a("Lv. FTE");
        orgAccountObjectDetailReportEntry.setHeader2("FTE");
        orgAccountObjectDetailReportEntry.setHeader3("Amount");
        orgAccountObjectDetailReportEntry.setHeader31("FTE");
        orgAccountObjectDetailReportEntry.setHeader40("FTE");
        orgAccountObjectDetailReportEntry.setHeader4("Amount");
        orgAccountObjectDetailReportEntry.setHeader5(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgAccountObjectDetailReportEntry.setHeader6(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgAccountObjectDetailReportEntry.setConsHdr("");

        // For group
        orgAccountObjectDetailReportEntry.setSubAccountNumber(accountBalance.getSubAccountNumber() + accountBalance.getAccountNumber());
        orgAccountObjectDetailReportEntry.setFinancialObjectCode(accountBalance.getFinancialObjectCode());
        orgAccountObjectDetailReportEntry.setFinancialSubObjectCode(accountBalance.getFinancialSubObjectCode());
        //orgAccountObjectDetailReportEntry.setFinancialObjectLevelCode(accountBalance.getFinancialObjectLevelCode());
        orgAccountObjectDetailReportEntry.setIncomeExpenseCode(accountBalance.getIncomeExpenseCode());
        //orgAccountObjectDetailReportEntry.setFinancialConsolidationSortCode(accountBalance.getFinancialConsolidationSortCode());
        orgAccountObjectDetailReportEntry.setFinancialLevelSortCode(accountBalance.getFinancialLevelSortCode());
    }

    
    
    /**
     * builds report body
     * 
     * @param BudgetConstructionLevelSummary bcas
     */
    private void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgAccountObjectDetailReport orgAccountObjectDetailReportEntry, BudgetConstructionAccountBalance accountBalance) {
        
        orgAccountObjectDetailReportEntry.setFinancialObjectName(accountBalance.getFinancialObject().getFinancialObjectCodeName());
        
        if (accountBalance.getPositionCsfLeaveFteQuantity() != null && !accountBalance.getPositionCsfLeaveFteQuantity().equals(BigDecimal.ZERO)) {
            orgAccountObjectDetailReportEntry.setPositionCsfLeaveFteQuantity(accountBalance.getPositionCsfLeaveFteQuantity().setScale(2).toString());
        }

        if (accountBalance.getPositionFullTimeEquivalencyQuantity() != null && !accountBalance.getPositionFullTimeEquivalencyQuantity().equals(BigDecimal.ZERO)) {
            orgAccountObjectDetailReportEntry.setPositionFullTimeEquivalencyQuantity(accountBalance.getPositionFullTimeEquivalencyQuantity().setScale(2).toString());
        }

        if (accountBalance.getAppointmentRequestedCsfFteQuantity() != null && !accountBalance.getAppointmentRequestedCsfFteQuantity().equals(BigDecimal.ZERO)) {
            orgAccountObjectDetailReportEntry.setAppointmentRequestedCsfFteQuantity(accountBalance.getAppointmentRequestedCsfFteQuantity().setScale(2).toString());
        }

        if (accountBalance.getAppointmentRequestedFteQuantity() != null && !accountBalance.getAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
            orgAccountObjectDetailReportEntry.setAppointmentRequestedFteQuantity(accountBalance.getAppointmentRequestedFteQuantity().setScale(2).toString());
        }

        if (accountBalance.getAccountLineAnnualBalanceAmount() != null) {
            orgAccountObjectDetailReportEntry.setAccountLineAnnualBalanceAmount(new Integer(accountBalance.getAccountLineAnnualBalanceAmount().intValue()));
        }

        if (accountBalance.getFinancialBeginningBalanceLineAmount() != null) {
            orgAccountObjectDetailReportEntry.setFinancialBeginningBalanceLineAmount(new Integer(accountBalance.getFinancialBeginningBalanceLineAmount().intValue()));
        }

        if (accountBalance.getAccountLineAnnualBalanceAmount() != null && accountBalance.getFinancialBeginningBalanceLineAmount() != null) {
            int changeAmount = accountBalance.getAccountLineAnnualBalanceAmount().subtract(accountBalance.getFinancialBeginningBalanceLineAmount()).intValue();
            orgAccountObjectDetailReportEntry.setAmountChange(new Integer(changeAmount));
        }
        orgAccountObjectDetailReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(orgAccountObjectDetailReportEntry.getAmountChange(), orgAccountObjectDetailReportEntry.getFinancialBeginningBalanceLineAmount()));
            

    }

    
    /**
     * builds report total
     * 
     */

    private void buildReportsTotal(BudgetConstructionOrgAccountObjectDetailReport orgObjectSummaryReportEntry, BudgetConstructionAccountBalance accountBalance,
            List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailTotalObjectList,
            List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailTotalLevelList, 
            List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailTotalGexpAndTypeList, 
            List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailAccountTotalList,
            List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailSubFundTotalList) {
        
        for (BudgetConstructionOrgAccountObjectDetailReportTotal objectTotal : accountObjectDetailTotalObjectList) {
            if (isSameAccountObjectDetailEntryForObject(accountBalance, objectTotal.getBudgetConstructionAccountBalance())) {
                orgObjectSummaryReportEntry.setTotalObjectDescription(accountBalance.getFinancialObject().getFinancialObjectCodeName());

                // The total part shouldn't have null value, so just checking '0'
                if (!objectTotal.getTotalObjectPositionCsfLeaveFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalObjectPositionCsfLeaveFteQuantity(objectTotal.getTotalObjectPositionCsfLeaveFteQuantity().setScale(2).toString());
                }
                if (!objectTotal.getTotalObjectPositionFullTimeEquivalencyQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalObjectPositionFullTimeEquivalencyQuantity(objectTotal.getTotalObjectPositionFullTimeEquivalencyQuantity().setScale(2).toString());
                }
                orgObjectSummaryReportEntry.setTotalObjectFinancialBeginningBalanceLineAmount(objectTotal.getTotalObjectFinancialBeginningBalanceLineAmount());

                if (!objectTotal.getTotalObjectAppointmentRequestedCsfFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalObjectAppointmentRequestedCsfFteQuantity(objectTotal.getTotalObjectAppointmentRequestedCsfFteQuantity().setScale(2).toString());
                }
                if (!objectTotal.getTotalObjectAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalObjectAppointmentRequestedFteQuantity(objectTotal.getTotalObjectAppointmentRequestedFteQuantity().setScale(2).toString());
                }
                orgObjectSummaryReportEntry.setTotalObjectAccountLineAnnualBalanceAmount(objectTotal.getTotalObjectAccountLineAnnualBalanceAmount());

                orgObjectSummaryReportEntry.setTotalObjectAmountChange(objectTotal.getTotalObjectAmountChange());
                orgObjectSummaryReportEntry.setTotalObjectPercentChange(BudgetConstructionReportHelper.calculatePercent(objectTotal.getTotalObjectAmountChange(), objectTotal.getTotalObjectFinancialBeginningBalanceLineAmount()));
            }
        }
        
        for (BudgetConstructionOrgAccountObjectDetailReportTotal levelTotal : accountObjectDetailTotalLevelList) {
            if (isSameAccountObjectDetailEntryForLevel(accountBalance, levelTotal.getBudgetConstructionAccountBalance())) {
                orgObjectSummaryReportEntry.setTotalLevelDescription(accountBalance.getFinancialObjectLevel().getFinancialObjectLevelName());

                // The total part shouldn't have null value, so just checking '0'
                if (!levelTotal.getTotalLevelPositionCsfLeaveFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalLevelPositionCsfLeaveFteQuantity(levelTotal.getTotalLevelPositionCsfLeaveFteQuantity().setScale(2).toString());
                }
                if (!levelTotal.getTotalLevelPositionFullTimeEquivalencyQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalLevelPositionFullTimeEquivalencyQuantity(levelTotal.getTotalLevelPositionFullTimeEquivalencyQuantity().setScale(2).toString());
                }
                orgObjectSummaryReportEntry.setTotalLevelFinancialBeginningBalanceLineAmount(levelTotal.getTotalLevelFinancialBeginningBalanceLineAmount());

                if (!levelTotal.getTotalLevelAppointmentRequestedCsfFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalLevelAppointmentRequestedCsfFteQuantity(levelTotal.getTotalLevelAppointmentRequestedCsfFteQuantity().setScale(2).toString());
                }
                if (!levelTotal.getTotalLevelAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTotalLevelAppointmentRequestedFteQuantity(levelTotal.getTotalLevelAppointmentRequestedFteQuantity().setScale(2).toString());
                }
                orgObjectSummaryReportEntry.setTotalLevelAccountLineAnnualBalanceAmount(levelTotal.getTotalLevelAccountLineAnnualBalanceAmount());

                orgObjectSummaryReportEntry.setTotalLevelAmountChange(levelTotal.getTotalLevelAmountChange());
                orgObjectSummaryReportEntry.setTotalLevelPercentChange(BudgetConstructionReportHelper.calculatePercent(levelTotal.getTotalLevelAmountChange(), levelTotal.getTotalLevelFinancialBeginningBalanceLineAmount()));

            }
        }
        

        for (BudgetConstructionOrgAccountObjectDetailReportTotal gexpAndTypeTotal : accountObjectDetailTotalGexpAndTypeList) {
            if (isSameAccountObjectDetailEntryForGexpAndType(accountBalance, gexpAndTypeTotal.getBudgetConstructionAccountBalance())) {

                orgObjectSummaryReportEntry.setGrossFinancialBeginningBalanceLineAmount(gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setGrossAccountLineAnnualBalanceAmount(gexpAndTypeTotal.getGrossAccountLineAnnualBalanceAmount());
                orgObjectSummaryReportEntry.setGrossAmountChange(gexpAndTypeTotal.getGrossAmountChange());
                orgObjectSummaryReportEntry.setGrossPercentChange(BudgetConstructionReportHelper.calculatePercent(gexpAndTypeTotal.getGrossAmountChange(), gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount()));

                if (accountBalance.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
                    orgObjectSummaryReportEntry.setTypeDesc(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_UPPERCASE_REVENUE));
                }
                else {
                    orgObjectSummaryReportEntry.setTypeDesc(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_EXPENDITURE_NET_TRNFR));
                }

                if (!gexpAndTypeTotal.getTypePositionCsfLeaveFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTypePositionCsfLeaveFteQuantity(gexpAndTypeTotal.getTypePositionCsfLeaveFteQuantity().setScale(2).toString());
                }
                if (!gexpAndTypeTotal.getTypePositionFullTimeEquivalencyQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTypePositionFullTimeEquivalencyQuantity(gexpAndTypeTotal.getTypePositionFullTimeEquivalencyQuantity().setScale(2).toString());
                }
                orgObjectSummaryReportEntry.setTypeFinancialBeginningBalanceLineAmount(gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount());

                if (!gexpAndTypeTotal.getTypeAppointmentRequestedCsfFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTypeAppointmentRequestedCsfFteQuantity(gexpAndTypeTotal.getTypeAppointmentRequestedCsfFteQuantity().setScale(2).toString());
                }
                if (!gexpAndTypeTotal.getTypeAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
                    orgObjectSummaryReportEntry.setTypeAppointmentRequestedFteQuantity(gexpAndTypeTotal.getTypeAppointmentRequestedFteQuantity().setScale(2).toString());
                }

                orgObjectSummaryReportEntry.setTypeAccountLineAnnualBalanceAmount(gexpAndTypeTotal.getTypeAccountLineAnnualBalanceAmount());
                orgObjectSummaryReportEntry.setTypeAmountChange(gexpAndTypeTotal.getTypeAmountChange());
                orgObjectSummaryReportEntry.setTypePercentChange(BudgetConstructionReportHelper.calculatePercent(gexpAndTypeTotal.getTypeAmountChange(), gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount()));
            }
        }

        for (BudgetConstructionOrgAccountObjectDetailReportTotal accountTotal : accountObjectDetailAccountTotalList) {
            if (isSameAccountObjectDetailEntryForAccountTotal(accountBalance, accountTotal.getBudgetConstructionAccountBalance())) {
                
                orgObjectSummaryReportEntry.setAccountRevenueFinancialBeginningBalanceLineAmount(accountTotal.getAccountRevenueFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setAccountRevenueAccountLineAnnualBalanceAmount(accountTotal.getAccountRevenueAccountLineAnnualBalanceAmount());
                orgObjectSummaryReportEntry.setAccountExpenditureFinancialBeginningBalanceLineAmount(accountTotal.getAccountExpenditureFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setAccountExpenditureAccountLineAnnualBalanceAmount(accountTotal.getAccountExpenditureAccountLineAnnualBalanceAmount());

                orgObjectSummaryReportEntry.setAccountRevenueAmountChange(accountTotal.getAccountRevenueAmountChange());
                orgObjectSummaryReportEntry.setAccountRevenuePercentChange(BudgetConstructionReportHelper.calculatePercent(accountTotal.getAccountRevenueAmountChange(), accountTotal.getAccountRevenueFinancialBeginningBalanceLineAmount()));

                orgObjectSummaryReportEntry.setAccountExpenditureAmountChange(accountTotal.getAccountExpenditureAmountChange());
                orgObjectSummaryReportEntry.setAccountExpenditurePercentChange(BudgetConstructionReportHelper.calculatePercent(accountTotal.getAccountExpenditureAmountChange(), accountTotal.getAccountExpenditureFinancialBeginningBalanceLineAmount()));

                orgObjectSummaryReportEntry.setAccountDifferenceFinancialBeginningBalanceLineAmount(accountTotal.getAccountDifferenceFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setAccountDifferenceAccountLineAnnualBalanceAmount(accountTotal.getAccountDifferenceAccountLineAnnualBalanceAmount());

                orgObjectSummaryReportEntry.setAccountDifferenceAmountChange(accountTotal.getAccountDifferenceAmountChange());
                orgObjectSummaryReportEntry.setAccountDifferencePercentChange(BudgetConstructionReportHelper.calculatePercent(accountTotal.getAccountDifferenceAmountChange(), accountTotal.getAccountDifferenceFinancialBeginningBalanceLineAmount()));
            }
        }

        
        for (BudgetConstructionOrgAccountObjectDetailReportTotal subFundTotal : accountObjectDetailSubFundTotalList) {
            if (isSameAccountObjectDetailEntryForSubFundTotal(accountBalance, subFundTotal.getBudgetConstructionAccountBalance())) {
                
                orgObjectSummaryReportEntry.setSubFundRevenueFinancialBeginningBalanceLineAmount(subFundTotal.getSubFundRevenueFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setSubFundRevenueAccountLineAnnualBalanceAmount(subFundTotal.getSubFundRevenueAccountLineAnnualBalanceAmount());
                orgObjectSummaryReportEntry.setSubFundExpenditureFinancialBeginningBalanceLineAmount(subFundTotal.getSubFundExpenditureFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setSubFundExpenditureAccountLineAnnualBalanceAmount(subFundTotal.getSubFundExpenditureAccountLineAnnualBalanceAmount());

                orgObjectSummaryReportEntry.setSubFundRevenueAmountChange(subFundTotal.getSubFundRevenueAmountChange());
                orgObjectSummaryReportEntry.setSubFundRevenuePercentChange(BudgetConstructionReportHelper.calculatePercent(subFundTotal.getSubFundRevenueAmountChange(), subFundTotal.getSubFundRevenueFinancialBeginningBalanceLineAmount()));

                orgObjectSummaryReportEntry.setSubFundExpenditureAmountChange(subFundTotal.getSubFundExpenditureAmountChange());
                orgObjectSummaryReportEntry.setSubFundExpenditurePercentChange(BudgetConstructionReportHelper.calculatePercent(subFundTotal.getSubFundExpenditureAmountChange(), subFundTotal.getSubFundExpenditureFinancialBeginningBalanceLineAmount()));

                orgObjectSummaryReportEntry.setSubFundDifferenceFinancialBeginningBalanceLineAmount(subFundTotal.getSubFundDifferenceFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setSubFundDifferenceAccountLineAnnualBalanceAmount(subFundTotal.getSubFundDifferenceAccountLineAnnualBalanceAmount());

                orgObjectSummaryReportEntry.setSubFundDifferenceAmountChange(subFundTotal.getSubFundDifferenceAmountChange());
                orgObjectSummaryReportEntry.setSubFundDifferencePercentChange(BudgetConstructionReportHelper.calculatePercent(subFundTotal.getSubFundDifferenceAmountChange(), subFundTotal.getSubFundDifferenceFinancialBeginningBalanceLineAmount()));
            }
        }

     
    }
    
    
    private List calculateObjectTotal(List<BudgetConstructionAccountBalance> bcosList, List<BudgetConstructionAccountBalance> simpleList) {

        BigDecimal totalObjectPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalObjectPositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
        Integer totalObjectFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalObjectAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalObjectAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalObjectAccountLineAnnualBalanceAmount = new Integer(0);
        Integer totalObjectAmountChange = new Integer(0);
        BigDecimal totalObjectPercentChange = BigDecimal.ZERO;
        
        List returnList = new ArrayList();
        
        for (BudgetConstructionAccountBalance simpleBcosEntry : simpleList) {
            
            BudgetConstructionOrgAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionOrgAccountObjectDetailReportTotal();
            for (BudgetConstructionAccountBalance bcosListEntry : bcosList) {
                if (isSameAccountObjectDetailEntryForObject(simpleBcosEntry, bcosListEntry)) {
                    totalObjectFinancialBeginningBalanceLineAmount += new Integer(bcosListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    totalObjectAccountLineAnnualBalanceAmount += new Integer(bcosListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    totalObjectPositionCsfLeaveFteQuantity = totalObjectPositionCsfLeaveFteQuantity.add(bcosListEntry.getPositionCsfLeaveFteQuantity());
                    totalObjectPositionFullTimeEquivalencyQuantity = totalObjectPositionFullTimeEquivalencyQuantity.add(bcosListEntry.getPositionFullTimeEquivalencyQuantity());
                    totalObjectAppointmentRequestedCsfFteQuantity = totalObjectAppointmentRequestedCsfFteQuantity.add(bcosListEntry.getAppointmentRequestedCsfFteQuantity());
                    totalObjectAppointmentRequestedFteQuantity = totalObjectAppointmentRequestedFteQuantity.add(bcosListEntry.getAppointmentRequestedFteQuantity());
                }
            }
            bcObjectTotal.setBudgetConstructionAccountBalance(simpleBcosEntry);
            bcObjectTotal.setTotalObjectPositionCsfLeaveFteQuantity(totalObjectPositionCsfLeaveFteQuantity);
            bcObjectTotal.setTotalObjectPositionFullTimeEquivalencyQuantity(totalObjectPositionFullTimeEquivalencyQuantity);
            bcObjectTotal.setTotalObjectFinancialBeginningBalanceLineAmount(totalObjectFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTotalObjectAppointmentRequestedCsfFteQuantity(totalObjectAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTotalObjectAppointmentRequestedFteQuantity(totalObjectAppointmentRequestedFteQuantity);
            bcObjectTotal.setTotalObjectAccountLineAnnualBalanceAmount(totalObjectAccountLineAnnualBalanceAmount);

            totalObjectAmountChange = totalObjectAccountLineAnnualBalanceAmount - totalObjectFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setTotalObjectAmountChange(totalObjectAmountChange);
            returnList.add(bcObjectTotal);

            totalObjectPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalObjectPositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
            totalObjectFinancialBeginningBalanceLineAmount = new Integer(0);
            totalObjectAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalObjectAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalObjectAccountLineAnnualBalanceAmount = new Integer(0);
            totalObjectAmountChange = new Integer(0);
            totalObjectPercentChange = BigDecimal.ZERO;

        }
        return returnList;
        
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    private List calculateLevelTotal(List<BudgetConstructionAccountBalance> bcosList, List<BudgetConstructionAccountBalance> simpleList) {

        BigDecimal totalLevelPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalLevelPositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
        Integer totalLevelFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalLevelAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalLevelAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalLevelAccountLineAnnualBalanceAmount = new Integer(0);
        Integer totalLevelAmountChange = new Integer(0);
        BigDecimal totalLevelPercentChange = BigDecimal.ZERO;
        
        List returnList = new ArrayList();
        
        for (BudgetConstructionAccountBalance simpleBcosEntry : simpleList) {
            
            BudgetConstructionOrgAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionOrgAccountObjectDetailReportTotal();
            for (BudgetConstructionAccountBalance bcosListEntry : bcosList) {
                if (isSameAccountObjectDetailEntryForLevel(simpleBcosEntry, bcosListEntry)) {
                    totalLevelFinancialBeginningBalanceLineAmount += new Integer(bcosListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    totalLevelAccountLineAnnualBalanceAmount += new Integer(bcosListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity.add(bcosListEntry.getPositionCsfLeaveFteQuantity());
                    totalLevelPositionFullTimeEquivalencyQuantity = totalLevelPositionFullTimeEquivalencyQuantity.add(bcosListEntry.getPositionFullTimeEquivalencyQuantity());
                    totalLevelAppointmentRequestedCsfFteQuantity = totalLevelAppointmentRequestedCsfFteQuantity.add(bcosListEntry.getAppointmentRequestedCsfFteQuantity());
                    totalLevelAppointmentRequestedFteQuantity = totalLevelAppointmentRequestedFteQuantity.add(bcosListEntry.getAppointmentRequestedFteQuantity());
                }
            }
            bcObjectTotal.setBudgetConstructionAccountBalance(simpleBcosEntry);
            bcObjectTotal.setTotalLevelPositionCsfLeaveFteQuantity(totalLevelPositionCsfLeaveFteQuantity);
            bcObjectTotal.setTotalLevelPositionFullTimeEquivalencyQuantity(totalLevelPositionFullTimeEquivalencyQuantity);
            bcObjectTotal.setTotalLevelFinancialBeginningBalanceLineAmount(totalLevelFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTotalLevelAppointmentRequestedCsfFteQuantity(totalLevelAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTotalLevelAppointmentRequestedFteQuantity(totalLevelAppointmentRequestedFteQuantity);
            bcObjectTotal.setTotalLevelAccountLineAnnualBalanceAmount(totalLevelAccountLineAnnualBalanceAmount);
            totalLevelAmountChange = totalLevelAccountLineAnnualBalanceAmount - totalLevelFinancialBeginningBalanceLineAmount;

            bcObjectTotal.setTotalLevelAmountChange(totalLevelAmountChange);

            returnList.add(bcObjectTotal);

            totalLevelPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalLevelPositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
            totalLevelFinancialBeginningBalanceLineAmount = new Integer(0);
            totalLevelAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalLevelAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalLevelAccountLineAnnualBalanceAmount = new Integer(0);
            totalLevelAmountChange = new Integer(0);
            totalLevelPercentChange = BigDecimal.ZERO;

        }
        return returnList;
        
    }

    
    private List calculateGexpAndTypeTotal(List<BudgetConstructionAccountBalance> bcabList, List<BudgetConstructionAccountBalance> simpleList) {

        Integer grossFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer grossAccountLineAnnualBalanceAmount = new Integer(0);
        Integer grossAmountChange = new Integer(0);
        BigDecimal grossPercentChange = BigDecimal.ZERO;

        BigDecimal typePositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal typePositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
        Integer typeFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal typeAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal typeAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer typeAccountLineAnnualBalanceAmount = new Integer(0);
        Integer typeAmountChange = new Integer(0);
        BigDecimal typePercentChange = BigDecimal.ZERO;

        List returnList = new ArrayList();
        for (BudgetConstructionAccountBalance simpleBcosEntry : simpleList) {
            BudgetConstructionOrgAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionOrgAccountObjectDetailReportTotal();
            for (BudgetConstructionAccountBalance bcabListEntry : bcabList) {
                if (isSameAccountObjectDetailEntryForGexpAndType(simpleBcosEntry, bcabListEntry)) {

                    typeFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    typeAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity.add(bcabListEntry.getPositionCsfLeaveFteQuantity());
                    typePositionFullTimeEquivalencyQuantity = typePositionFullTimeEquivalencyQuantity.add(bcabListEntry.getPositionFullTimeEquivalencyQuantity());
                    typeAppointmentRequestedCsfFteQuantity = typeAppointmentRequestedCsfFteQuantity.add(bcabListEntry.getAppointmentRequestedCsfFteQuantity());
                    typeAppointmentRequestedFteQuantity = typeAppointmentRequestedFteQuantity.add(bcabListEntry.getAppointmentRequestedFteQuantity());

                    if (bcabListEntry.getIncomeExpenseCode().equals("B") && !bcabListEntry.getFinancialObjectLevelCode().equals("CORI") && !bcabListEntry.getFinancialObjectLevelCode().equals("TRIN")) {
                        grossFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        grossAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                }
            }
            bcObjectTotal.setBudgetConstructionAccountBalance(simpleBcosEntry);

            bcObjectTotal.setGrossFinancialBeginningBalanceLineAmount(grossFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setGrossAccountLineAnnualBalanceAmount(grossAccountLineAnnualBalanceAmount);
            grossAmountChange = grossAccountLineAnnualBalanceAmount - grossFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setGrossAmountChange(grossAmountChange);

            bcObjectTotal.setTypePositionCsfLeaveFteQuantity(typePositionCsfLeaveFteQuantity);
            bcObjectTotal.setTypePositionFullTimeEquivalencyQuantity(typePositionFullTimeEquivalencyQuantity);
            bcObjectTotal.setTypeFinancialBeginningBalanceLineAmount(typeFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTypeAppointmentRequestedCsfFteQuantity(typeAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTypeAppointmentRequestedFteQuantity(typeAppointmentRequestedFteQuantity);
            bcObjectTotal.setTypeAccountLineAnnualBalanceAmount(typeAccountLineAnnualBalanceAmount);

            typeAmountChange = typeAccountLineAnnualBalanceAmount - typeFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setTypeAmountChange(typeAmountChange);

            
            returnList.add(bcObjectTotal);
            grossFinancialBeginningBalanceLineAmount = new Integer(0);
            grossAccountLineAnnualBalanceAmount = new Integer(0);
            grossAmountChange = new Integer(0);
            grossPercentChange = BigDecimal.ZERO;

            typePositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            typePositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
            typeFinancialBeginningBalanceLineAmount = new Integer(0);
            typeAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            typeAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            typeAccountLineAnnualBalanceAmount = new Integer(0);
            typeAmountChange = new Integer(0);
            typePercentChange = BigDecimal.ZERO;
        }

        return returnList;
    }

    private List calculateAccountTotal(List<BudgetConstructionAccountBalance> bcabList, List<BudgetConstructionAccountBalance> simpleList) {
        
        Integer accountRevenueFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer accountRevenueAccountLineAnnualBalanceAmount = new Integer(0);
        Integer accountRevenueAmountChange = new Integer(0);
        BigDecimal accountRevenuePercentChange = BigDecimal.ZERO;

        Integer accountExpenditureFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer accountExpenditureAccountLineAnnualBalanceAmount = new Integer(0);
        Integer accountExpenditureAmountChange = new Integer(0);
        BigDecimal accountExpenditurePercentChange = BigDecimal.ZERO;

        Integer accountDifferenceFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer accountDifferenceAccountLineAnnualBalanceAmount = new Integer(0);
        Integer accountDifferenceAmountChange = new Integer(0);
        BigDecimal accountDifferencePercentChange = BigDecimal.ZERO;

        List returnList = new ArrayList();

        for (BudgetConstructionAccountBalance simpleBcosEntry : simpleList) {
            BudgetConstructionOrgAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionOrgAccountObjectDetailReportTotal();
            for (BudgetConstructionAccountBalance bcabListEntry : bcabList) {
                if (isSameAccountObjectDetailEntryForAccountTotal(simpleBcosEntry, bcabListEntry)) {

                    if (bcabListEntry.getIncomeExpenseCode().equals("A")) {
                        accountRevenueFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        accountRevenueAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                    else {
                        accountExpenditureFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        accountExpenditureAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                }
            }

            bcObjectTotal.setBudgetConstructionAccountBalance(simpleBcosEntry);

            bcObjectTotal.setAccountRevenueFinancialBeginningBalanceLineAmount(accountRevenueFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setAccountRevenueAccountLineAnnualBalanceAmount(accountRevenueAccountLineAnnualBalanceAmount);

            accountRevenueAmountChange = accountRevenueAccountLineAnnualBalanceAmount - accountRevenueFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setAccountRevenueAmountChange(accountRevenueAmountChange);

            bcObjectTotal.setAccountExpenditureFinancialBeginningBalanceLineAmount(accountExpenditureFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setAccountExpenditureAccountLineAnnualBalanceAmount(accountExpenditureAccountLineAnnualBalanceAmount);

            accountExpenditureAmountChange = accountExpenditureAccountLineAnnualBalanceAmount - accountExpenditureFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setAccountExpenditureAmountChange(accountExpenditureAmountChange);

            accountDifferenceFinancialBeginningBalanceLineAmount = accountRevenueFinancialBeginningBalanceLineAmount - accountExpenditureFinancialBeginningBalanceLineAmount;
            accountDifferenceAccountLineAnnualBalanceAmount = accountRevenueAccountLineAnnualBalanceAmount - accountExpenditureAccountLineAnnualBalanceAmount;
            bcObjectTotal.setAccountDifferenceFinancialBeginningBalanceLineAmount(accountDifferenceFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setAccountDifferenceAccountLineAnnualBalanceAmount(accountDifferenceAccountLineAnnualBalanceAmount);

            accountDifferenceAmountChange = accountDifferenceAccountLineAnnualBalanceAmount - accountDifferenceFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setAccountDifferenceAmountChange(accountDifferenceAmountChange);
            returnList.add(bcObjectTotal);

            accountRevenueFinancialBeginningBalanceLineAmount = new Integer(0);
            accountRevenueAccountLineAnnualBalanceAmount = new Integer(0);
            accountRevenueAmountChange = new Integer(0);
            accountRevenuePercentChange = BigDecimal.ZERO;

            accountExpenditureFinancialBeginningBalanceLineAmount = new Integer(0);
            accountExpenditureAccountLineAnnualBalanceAmount = new Integer(0);
            accountExpenditureAmountChange = new Integer(0);
            accountExpenditurePercentChange = BigDecimal.ZERO;

            accountDifferenceFinancialBeginningBalanceLineAmount = new Integer(0);
            accountDifferenceAccountLineAnnualBalanceAmount = new Integer(0);
            accountDifferenceAmountChange = new Integer(0);
            accountDifferencePercentChange = BigDecimal.ZERO;
        }

        
        return returnList;
    }

    

    
    private List calculateSubFundTotal(List<BudgetConstructionAccountBalance> bcabList, List<BudgetConstructionAccountBalance> simpleList) {

        Integer subFundRevenueFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer subFundRevenueAccountLineAnnualBalanceAmount = new Integer(0);
        Integer subFundRevenueAmountChange = new Integer(0);
        BigDecimal subFundRevenuePercentChange = BigDecimal.ZERO;

        Integer subFundExpenditureFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer subFundExpenditureAccountLineAnnualBalanceAmount = new Integer(0);
        Integer subFundExpenditureAmountChange = new Integer(0);
        BigDecimal subFundExpenditurePercentChange = BigDecimal.ZERO;

        Integer subFundDifferenceFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer subFundDifferenceAccountLineAnnualBalanceAmount = new Integer(0);
        Integer subFundDifferenceAmountChange = new Integer(0);
        BigDecimal subFundDifferencePercentChange = BigDecimal.ZERO;

        List returnList = new ArrayList();

        for (BudgetConstructionAccountBalance simpleBcosEntry : simpleList) {
            BudgetConstructionOrgAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionOrgAccountObjectDetailReportTotal();
            for (BudgetConstructionAccountBalance bcabListEntry : bcabList) {
                if (isSameAccountObjectDetailEntryForAccountTotal(simpleBcosEntry, bcabListEntry)) {

                    if (bcabListEntry.getIncomeExpenseCode().equals("A")) {
                        subFundRevenueFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        subFundRevenueAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                    else {
                        subFundExpenditureFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        subFundExpenditureAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                }
            }

            bcObjectTotal.setBudgetConstructionAccountBalance(simpleBcosEntry);

            bcObjectTotal.setSubFundRevenueFinancialBeginningBalanceLineAmount(subFundRevenueFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setSubFundRevenueAccountLineAnnualBalanceAmount(subFundRevenueAccountLineAnnualBalanceAmount);

            subFundRevenueAmountChange = subFundRevenueAccountLineAnnualBalanceAmount - subFundRevenueFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setSubFundRevenueAmountChange(subFundRevenueAmountChange);

            bcObjectTotal.setSubFundExpenditureFinancialBeginningBalanceLineAmount(subFundExpenditureFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setSubFundExpenditureAccountLineAnnualBalanceAmount(subFundExpenditureAccountLineAnnualBalanceAmount);

            subFundExpenditureAmountChange = subFundExpenditureAccountLineAnnualBalanceAmount - subFundExpenditureFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setSubFundExpenditureAmountChange(subFundExpenditureAmountChange);

            
            subFundDifferenceFinancialBeginningBalanceLineAmount = subFundRevenueFinancialBeginningBalanceLineAmount - subFundExpenditureFinancialBeginningBalanceLineAmount;
            subFundDifferenceAccountLineAnnualBalanceAmount = subFundRevenueAccountLineAnnualBalanceAmount - subFundExpenditureAccountLineAnnualBalanceAmount;
            bcObjectTotal.setSubFundDifferenceFinancialBeginningBalanceLineAmount(subFundDifferenceFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setSubFundDifferenceAccountLineAnnualBalanceAmount(subFundDifferenceAccountLineAnnualBalanceAmount);

            subFundDifferenceAmountChange = subFundDifferenceAccountLineAnnualBalanceAmount - subFundDifferenceFinancialBeginningBalanceLineAmount;
            bcObjectTotal.setSubFundDifferenceAmountChange(subFundDifferenceAmountChange);

            returnList.add(bcObjectTotal);

            subFundRevenueFinancialBeginningBalanceLineAmount = new Integer(0);
            subFundRevenueAccountLineAnnualBalanceAmount = new Integer(0);
            subFundRevenueAmountChange = new Integer(0);
            subFundRevenuePercentChange = BigDecimal.ZERO;

            subFundExpenditureFinancialBeginningBalanceLineAmount = new Integer(0);
            subFundExpenditureAccountLineAnnualBalanceAmount = new Integer(0);
            subFundExpenditureAmountChange = new Integer(0);
            subFundExpenditurePercentChange = BigDecimal.ZERO;

            subFundDifferenceFinancialBeginningBalanceLineAmount = new Integer(0);
            subFundDifferenceAccountLineAnnualBalanceAmount = new Integer(0);
            subFundDifferenceAmountChange = new Integer(0);
            subFundDifferencePercentChange = BigDecimal.ZERO;
        }
        return returnList;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private boolean isSameAccountObjectDetailEntryForObject(BudgetConstructionAccountBalance firstBcab, BudgetConstructionAccountBalance secondBcab) {
        if (isSameAccountObjectDetailEntryForLevel(firstBcab, secondBcab) && firstBcab.getFinancialObjectCode().equals(secondBcab.getFinancialObjectCode()) && firstBcab.getFinancialSubObjectCode().equals(secondBcab.getFinancialSubObjectCode())) {
            return true;
        }
        else
            return false;
    }
    
    
    
    private boolean isSameAccountObjectDetailEntryForLevel(BudgetConstructionAccountBalance firstBcab, BudgetConstructionAccountBalance secondBcab) {
        if (isSameAccountObjectDetailEntryForGexpAndType(firstBcab, secondBcab) && firstBcab.getFinancialLevelSortCode().equals(secondBcab.getFinancialLevelSortCode())) {
            return true;
        }
        else
            return false;
    }

    private boolean isSameAccountObjectDetailEntryForGexpAndType(BudgetConstructionAccountBalance firstBcab, BudgetConstructionAccountBalance secondBcab) {
        if (isSameAccountObjectDetailEntryForAccountTotal(firstBcab, secondBcab) && firstBcab.getIncomeExpenseCode().equals(secondBcab.getIncomeExpenseCode())) {
            return true;
        }

        else
            return false;
    }
    
    private boolean isSameAccountObjectDetailEntryForAccountTotal(BudgetConstructionAccountBalance firstBcab, BudgetConstructionAccountBalance secondBcab) {
        if (firstBcab.getChartOfAccountsCode().equals(secondBcab.getChartOfAccountsCode()) && firstBcab.getAccountNumber().equals(secondBcab.getAccountNumber()) && firstBcab.getSubAccountNumber().equals(secondBcab.getSubAccountNumber())) {
            return true;
        }

        else
            return false;
    }
    
    private boolean isSameAccountObjectDetailEntryForSubFundTotal(BudgetConstructionAccountBalance firstBcab, BudgetConstructionAccountBalance secondBcab) {
        if (firstBcab.getOrganizationChartOfAccountsCode().equals(secondBcab.getOrganizationChartOfAccountsCode()) && firstBcab.getOrganizationCode().equals(secondBcab.getOrganizationCode()) && firstBcab.getSubFundGroupCode().equals(secondBcab.getSubFundGroupCode())) {
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

        // mode 1 is for getting a list of level
        // mode 2 is for getting a list of cons
        // mode 3 is for getting a list of gexp and type
        // mode 4 is for getting a list of total

        int count = 0;
        BudgetConstructionAccountBalance accountObjectDetailEntry = null;
        BudgetConstructionAccountBalance accountObjectDetailEntryAux = null;
        List returnList = new ArrayList();
        if ((list != null) && (list.size() > 0)) {
            accountObjectDetailEntry = (BudgetConstructionAccountBalance) list.get(count);
            accountObjectDetailEntryAux = (BudgetConstructionAccountBalance) list.get(count);
            returnList.add(accountObjectDetailEntry);
            count++;
            while (count < list.size()) {
                accountObjectDetailEntry = (BudgetConstructionAccountBalance) list.get(count);
                switch (mode) {
                    case 1: {
                        if (!isSameAccountObjectDetailEntryForObject(accountObjectDetailEntry, accountObjectDetailEntryAux)) {
                            returnList.add(accountObjectDetailEntry);
                            accountObjectDetailEntryAux = accountObjectDetailEntry;
                        }
                    }
                    case 2: {
                        if (!isSameAccountObjectDetailEntryForLevel(accountObjectDetailEntry, accountObjectDetailEntryAux)) {
                            returnList.add(accountObjectDetailEntry);
                            accountObjectDetailEntryAux = accountObjectDetailEntry;
                        }
                    }
                    case 3: {
                        if (!isSameAccountObjectDetailEntryForGexpAndType(accountObjectDetailEntry, accountObjectDetailEntryAux)) {
                            returnList.add(accountObjectDetailEntry);
                            accountObjectDetailEntryAux = accountObjectDetailEntry;
                        }
                    }
                    case 4: {
                        if (!isSameAccountObjectDetailEntryForAccountTotal(accountObjectDetailEntry, accountObjectDetailEntryAux)) {
                            returnList.add(accountObjectDetailEntry);
                            accountObjectDetailEntryAux = accountObjectDetailEntry;
                        }
                    }
                    case 5: {
                        if (!isSameAccountObjectDetailEntryForSubFundTotal(accountObjectDetailEntry, accountObjectDetailEntryAux)) {
                            returnList.add(accountObjectDetailEntry);
                            accountObjectDetailEntryAux = accountObjectDetailEntry;
                        }
                    }
                }
                count++;
            }
        }
        return returnList;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        returnList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
        returnList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.INCOME_EXPENSE_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_LEVEL_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        return returnList;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setBudgetConstructionAccountObjectDetailReportDao(BudgetConstructionAccountObjectDetailReportDao budgetConstructionAccountObjectDetailReportDao) {
        this.budgetConstructionAccountObjectDetailReportDao = budgetConstructionAccountObjectDetailReportDao;
    }

    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }

}
