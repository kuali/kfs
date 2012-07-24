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
import java.util.List;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgSubFundSummaryReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgSubFundSummaryReportTotal;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionAccountSummaryReportDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionSubFundSummaryReportService;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.module.bc.util.BudgetConstructionUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionSubFundSummaryReportServiceImpl implements BudgetConstructionSubFundSummaryReportService {

    protected BudgetConstructionAccountSummaryReportDao budgetConstructionAccountSummaryReportDao;
    protected ConfigurationService kualiConfigurationService;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    protected boolean trExist = false;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetReportsControlListService#updateSubFundSummaryReport(java.lang.String)
     */
    public void updateSubFundSummaryReport(String principalName) {
        String expenditureINList = BudgetConstructionUtils.getExpenditureINList();
        String revenueINList = BudgetConstructionUtils.getRevenueINList();
        budgetConstructionAccountSummaryReportDao.cleanReportsAccountSummaryTable(principalName);
        budgetConstructionAccountSummaryReportDao.updateSubFundSummaryReport(principalName, revenueINList, expenditureINList);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionSubFundSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    public Collection<BudgetConstructionOrgSubFundSummaryReport> buildReports(Integer universityFiscalYear, String principalName) {
        Collection<BudgetConstructionOrgSubFundSummaryReport> reportSet = new ArrayList();

        // build order list
        Collection<BudgetConstructionAccountSummary> subFundSummaryList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionAccountSummary.class, principalName, buildOrderByList());

        // Making a list with same organizationChartOfAccountsCode, organizationCode, chartOfAccountsCode, subFundGroupCode
        List subTotalList = BudgetConstructionReportHelper.deleteDuplicated((List) subFundSummaryList, fieldsForSubTotal());
        List totalList = BudgetConstructionReportHelper.deleteDuplicated((List) subFundSummaryList, fieldsForTotal());

        // Calculate Total Section
        List<BudgetConstructionOrgSubFundSummaryReportTotal> orgSubFundSummaryReportSubTotalList = calculateSubTotal((List) subFundSummaryList, subTotalList);
        List<BudgetConstructionOrgSubFundSummaryReportTotal> orgSubFundSummaryReportTotalList = calculateTotal((List) subFundSummaryList, totalList);

        for (BudgetConstructionAccountSummary subFundSummaryEntry : subFundSummaryList) {
            BudgetConstructionOrgSubFundSummaryReport orgSubFundSummaryReportEntry = new BudgetConstructionOrgSubFundSummaryReport();
            buildReportsHeader(universityFiscalYear, orgSubFundSummaryReportEntry, subFundSummaryEntry);
            buildReportsBody(orgSubFundSummaryReportEntry, subFundSummaryEntry);
            buildReportsTotal(orgSubFundSummaryReportEntry, subFundSummaryEntry, orgSubFundSummaryReportSubTotalList, orgSubFundSummaryReportTotalList);
            reportSet.add(orgSubFundSummaryReportEntry);
        }

        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param universityFiscalYear
     * @param orgSubFundSummaryReportEntry
     * @param subFundSummaryList
     */
    public void buildReportsHeader(Integer universityFiscalYear, BudgetConstructionOrgSubFundSummaryReport orgSubFundSummaryReportEntry, BudgetConstructionAccountSummary subFundSummaryList) {
        String orgChartDesc = subFundSummaryList.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = subFundSummaryList.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = subFundSummaryList.getOrganization().getOrganizationName();
        String reportChartDesc = subFundSummaryList.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = subFundSummaryList.getFundGroup().getName();
        String subFundGroupDes = subFundSummaryList.getSubFundGroup().getSubFundGroupDescription();
        Integer prevFiscalyear = universityFiscalYear - 1;
        orgSubFundSummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgSubFundSummaryReportEntry.setOrgChartOfAccountsCode(subFundSummaryList.getOrganizationChartOfAccountsCode());
        if (orgChartDesc == null) {
            orgSubFundSummaryReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgSubFundSummaryReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }
        orgSubFundSummaryReportEntry.setOrganizationCode(subFundSummaryList.getOrganizationCode());
        if (orgName == null) {
            orgSubFundSummaryReportEntry.setOrganizationName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgSubFundSummaryReportEntry.setOrganizationName(orgName);
        }
        orgSubFundSummaryReportEntry.setChartOfAccountsCode(subFundSummaryList.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgSubFundSummaryReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgSubFundSummaryReportEntry.setChartOfAccountDescription(chartDesc);
        }
        orgSubFundSummaryReportEntry.setFundGroupCode(subFundSummaryList.getFundGroupCode());
        if (subFundGroupName == null) {
            orgSubFundSummaryReportEntry.setFundGroupName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            orgSubFundSummaryReportEntry.setFundGroupName(subFundGroupName);
        }
        orgSubFundSummaryReportEntry.setSubFundGroupCode(subFundSummaryList.getSubFundGroupCode());
        if (subFundGroupName == null) {
            orgSubFundSummaryReportEntry.setSubFundGroupDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_SUBFUNDGROUP_DESCRIPTION));
        }
        else {
            orgSubFundSummaryReportEntry.setSubFundGroupDescription(subFundGroupDes);
        }
        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgSubFundSummaryReportEntry.setBaseFy(prevPrevFiscalyear.toString() + "-" + prevFiscalyear.toString().substring(2, 4));
        orgSubFundSummaryReportEntry.setReqFy(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgSubFundSummaryReportEntry.setHeader1(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_SUBFUND));
        orgSubFundSummaryReportEntry.setHeader2(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_SUBFUND_DESCRIPTION));
        orgSubFundSummaryReportEntry.setHeader3(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_BASE_AMOUNT));
        orgSubFundSummaryReportEntry.setHeader4(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_REQ_AMOUNT));
        orgSubFundSummaryReportEntry.setHeader5(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgSubFundSummaryReportEntry.setHeader6(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgSubFundSummaryReportEntry.setConsHdr("");
    }

    /**
     * builds report body
     * 
     * @param orgSubFundSummaryReportEntry
     * @param subFundSummary
     */
    public void buildReportsBody(BudgetConstructionOrgSubFundSummaryReport orgSubFundSummaryReportEntry, BudgetConstructionAccountSummary subFundSummary) {

        // build income expense description
        if (subFundSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
            orgSubFundSummaryReportEntry.setIncExpDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_REVENUE));
        }
        else if (subFundSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_E)) {
            orgSubFundSummaryReportEntry.setIncExpDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_EXP_GROSS));
        }
        else if (subFundSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_T)) {
            trExist = true;
            orgSubFundSummaryReportEntry.setIncExpDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_TRNFR_IN));
        }
        else {
            if (trExist) {
                trExist = false;
                orgSubFundSummaryReportEntry.setIncExpDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_EXP_NET_TRNFR));
            }
            else {
                orgSubFundSummaryReportEntry.setIncExpDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_EXPENDITURE));
            }
        }
        orgSubFundSummaryReportEntry.setBaseAmount(BudgetConstructionReportHelper.convertKualiInteger(subFundSummary.getFinancialBeginningBalanceLineAmount()));
        orgSubFundSummaryReportEntry.setReqAmount(BudgetConstructionReportHelper.convertKualiInteger(subFundSummary.getAccountLineAnnualBalanceAmount()));
        orgSubFundSummaryReportEntry.setAmountChange(orgSubFundSummaryReportEntry.getReqAmount() - orgSubFundSummaryReportEntry.getBaseAmount());
        orgSubFundSummaryReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(orgSubFundSummaryReportEntry.getAmountChange(), orgSubFundSummaryReportEntry.getBaseAmount()));
    }

    /**
     * builds report total
     * 
     * @param orgSubFundSummaryReportEntry
     * @param subFundSummaryList
     * @param reportTotalList
     */
    public void buildReportsTotal(BudgetConstructionOrgSubFundSummaryReport orgSubFundSummaryReportEntry, BudgetConstructionAccountSummary subFundSummaryList, List<BudgetConstructionOrgSubFundSummaryReportTotal> reportSubTotalList, List<BudgetConstructionOrgSubFundSummaryReportTotal> reportTotalList) {
        for (BudgetConstructionOrgSubFundSummaryReportTotal subTotalEntry : reportSubTotalList) {
            if (BudgetConstructionReportHelper.isSameEntry(subFundSummaryList, subTotalEntry.getBcas(), fieldsForSubTotal())) {
                orgSubFundSummaryReportEntry.setSubFundTotalRevenueBaseAmount(subTotalEntry.getSubFundTotalRevenueBaseAmount());
                orgSubFundSummaryReportEntry.setSubFundTotalRevenueReqAmount(subTotalEntry.getSubFundTotalRevenueReqAmount());
                orgSubFundSummaryReportEntry.setSubFundTotalRevenueAmountChange(subTotalEntry.getSubFundTotalRevenueReqAmount() - subTotalEntry.getSubFundTotalRevenueBaseAmount());
                BigDecimal percentChange = BigDecimal.ZERO;
                percentChange = BudgetConstructionReportHelper.calculatePercent(orgSubFundSummaryReportEntry.getSubFundTotalRevenueAmountChange(), orgSubFundSummaryReportEntry.getSubFundTotalRevenueBaseAmount());
                orgSubFundSummaryReportEntry.setSubFundTotalRevenuePercentChange(percentChange);
            }


            for (BudgetConstructionOrgSubFundSummaryReportTotal totalEntry : reportTotalList) {
                if (BudgetConstructionReportHelper.isSameEntry(subFundSummaryList, totalEntry.getBcas(), fieldsForTotal())) {
                    BigDecimal percentChange = BigDecimal.ZERO;
                    orgSubFundSummaryReportEntry.setTotalRevenueBaseAmount(totalEntry.getTotalRevenueBaseAmount());
                    orgSubFundSummaryReportEntry.setTotalGrossBaseAmount(totalEntry.getTotalGrossBaseAmount());
                    orgSubFundSummaryReportEntry.setTotalTransferInBaseAmount(totalEntry.getTotalTransferInBaseAmount());
                    orgSubFundSummaryReportEntry.setTotalNetTransferBaseAmount(totalEntry.getTotalNetTransferBaseAmount());

                    orgSubFundSummaryReportEntry.setTotalRevenueReqAmount(totalEntry.getTotalRevenueReqAmount());
                    orgSubFundSummaryReportEntry.setTotalGrossReqAmount(totalEntry.getTotalGrossReqAmount());
                    orgSubFundSummaryReportEntry.setTotalTransferInReqAmount(totalEntry.getTotalTransferInReqAmount());
                    orgSubFundSummaryReportEntry.setTotalNetTransferReqAmount(totalEntry.getTotalNetTransferReqAmount());
                    orgSubFundSummaryReportEntry.setTotalRevenueAmountChange(orgSubFundSummaryReportEntry.getTotalRevenueReqAmount() - orgSubFundSummaryReportEntry.getTotalRevenueBaseAmount());
                    percentChange = BudgetConstructionReportHelper.calculatePercent(orgSubFundSummaryReportEntry.getTotalRevenueAmountChange(), orgSubFundSummaryReportEntry.getTotalRevenueBaseAmount());
                    orgSubFundSummaryReportEntry.setTotalRevenuePercentChange(percentChange);

                    orgSubFundSummaryReportEntry.setTotalGrossAmountChange(orgSubFundSummaryReportEntry.getTotalGrossReqAmount() - orgSubFundSummaryReportEntry.getTotalGrossBaseAmount());
                    percentChange = BudgetConstructionReportHelper.calculatePercent(orgSubFundSummaryReportEntry.getTotalGrossAmountChange(), orgSubFundSummaryReportEntry.getTotalGrossBaseAmount());
                    orgSubFundSummaryReportEntry.setTotalGrossPercentChange(percentChange);

                    orgSubFundSummaryReportEntry.setTotalTransferAmountChange(orgSubFundSummaryReportEntry.getTotalTransferInReqAmount() - orgSubFundSummaryReportEntry.getTotalTransferInBaseAmount());
                    percentChange = BudgetConstructionReportHelper.calculatePercent(orgSubFundSummaryReportEntry.getTotalTransferAmountChange(), orgSubFundSummaryReportEntry.getTotalTransferInBaseAmount());
                    orgSubFundSummaryReportEntry.setTotalTransferInPercentChange(percentChange);


                    orgSubFundSummaryReportEntry.setTotalNetTransferAmountChange(orgSubFundSummaryReportEntry.getTotalNetTransferReqAmount() - orgSubFundSummaryReportEntry.getTotalNetTransferBaseAmount());
                    percentChange = BudgetConstructionReportHelper.calculatePercent(orgSubFundSummaryReportEntry.getTotalNetTransferAmountChange(), orgSubFundSummaryReportEntry.getTotalNetTransferBaseAmount());
                    orgSubFundSummaryReportEntry.setTotalNetTransferPercentChange(percentChange);

                    orgSubFundSummaryReportEntry.setRevExpDifferenceBaseAmount(orgSubFundSummaryReportEntry.getTotalRevenueBaseAmount() - orgSubFundSummaryReportEntry.getTotalNetTransferBaseAmount());
                    orgSubFundSummaryReportEntry.setRevExpDifferenceReqAmount(orgSubFundSummaryReportEntry.getTotalRevenueReqAmount() - orgSubFundSummaryReportEntry.getTotalNetTransferReqAmount());
                    orgSubFundSummaryReportEntry.setRevExpDifferenceAmountChange(orgSubFundSummaryReportEntry.getRevExpDifferenceReqAmount() - orgSubFundSummaryReportEntry.getRevExpDifferenceBaseAmount());
                    percentChange = BudgetConstructionReportHelper.calculatePercent(orgSubFundSummaryReportEntry.getRevExpDifferenceAmountChange(), orgSubFundSummaryReportEntry.getRevExpDifferenceBaseAmount());
                    orgSubFundSummaryReportEntry.setRevExpDifferencePercentChange(percentChange);
                }
            }
        }
    }


    /**
     * Calculates total part of report
     * 
     * @param List bcasList
     * @param List simpleList
     */
    public List calculateSubTotal(List<BudgetConstructionAccountSummary> bcasList, List<BudgetConstructionAccountSummary> subTotalList) {
        Integer subFundTotalRevenueBaseAmount = 0;
        Integer subFundTotalRevenueReqAmount = 0;

        List returnList = new ArrayList();
        for (BudgetConstructionAccountSummary simpleBcasEntry : subTotalList) {
            BudgetConstructionOrgSubFundSummaryReportTotal bcSubFundTotal = new BudgetConstructionOrgSubFundSummaryReportTotal();
            for (BudgetConstructionAccountSummary bcasListEntry : bcasList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcasEntry, bcasListEntry, fieldsForSubTotal())) {
                    if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
                        subFundTotalRevenueBaseAmount -= BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getFinancialBeginningBalanceLineAmount());
                        subFundTotalRevenueReqAmount -= BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getAccountLineAnnualBalanceAmount());
                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_X)) {
                        subFundTotalRevenueBaseAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getFinancialBeginningBalanceLineAmount());
                        subFundTotalRevenueReqAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getAccountLineAnnualBalanceAmount());
                    }
                }
            }
            bcSubFundTotal.setBcas(simpleBcasEntry);
            bcSubFundTotal.setSubFundTotalRevenueBaseAmount(subFundTotalRevenueBaseAmount);
            bcSubFundTotal.setSubFundTotalRevenueReqAmount(subFundTotalRevenueReqAmount);
            subFundTotalRevenueBaseAmount = 0;
            subFundTotalRevenueReqAmount = 0;
            returnList.add(bcSubFundTotal);

        }
        return returnList;
    }


    /**
     * Calculates total part of report
     * 
     * @param List bcasList
     * @param List simpleList
     */
    public List calculateTotal(List<BudgetConstructionAccountSummary> bcasList, List<BudgetConstructionAccountSummary> totalList) {
        Integer totalRevenueBaseAmount = 0;
        Integer totalGrossBaseAmount = 0;
        Integer totalTransferInBaseAmount = 0;
        Integer totalNetTransferBaseAmount = 0;
        Integer totalRevenueReqAmount = 0;
        Integer totalGrossReqAmount = 0;
        Integer totalTransferInReqAmount = 0;
        Integer totalNetTransferReqAmount = 0;

        List returnList = new ArrayList();
        for (BudgetConstructionAccountSummary simpleBcasEntry : totalList) {
            BudgetConstructionOrgSubFundSummaryReportTotal bcSubFundTotal = new BudgetConstructionOrgSubFundSummaryReportTotal();
            for (BudgetConstructionAccountSummary bcasListEntry : bcasList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcasEntry, bcasListEntry, fieldsForTotal())) {
                    if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
                        totalRevenueBaseAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getFinancialBeginningBalanceLineAmount());
                        totalRevenueReqAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getAccountLineAnnualBalanceAmount());
                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_E)) {
                        totalGrossBaseAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getFinancialBeginningBalanceLineAmount());
                        totalGrossReqAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getAccountLineAnnualBalanceAmount());

                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_T)) {
                        totalTransferInBaseAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getFinancialBeginningBalanceLineAmount());
                        totalTransferInReqAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getAccountLineAnnualBalanceAmount());
                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_X)) {
                        totalNetTransferBaseAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getFinancialBeginningBalanceLineAmount());
                        totalNetTransferReqAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getAccountLineAnnualBalanceAmount());
                    }
                }
            }
            bcSubFundTotal.setBcas(simpleBcasEntry);
            bcSubFundTotal.setTotalGrossBaseAmount(totalGrossBaseAmount);
            bcSubFundTotal.setTotalGrossReqAmount(totalGrossReqAmount);
            bcSubFundTotal.setTotalNetTransferBaseAmount(totalNetTransferBaseAmount);
            bcSubFundTotal.setTotalNetTransferReqAmount(totalNetTransferReqAmount);
            bcSubFundTotal.setTotalRevenueBaseAmount(totalRevenueBaseAmount);
            bcSubFundTotal.setTotalRevenueReqAmount(totalRevenueReqAmount);
            bcSubFundTotal.setTotalTransferInBaseAmount(totalTransferInBaseAmount);
            bcSubFundTotal.setTotalTransferInReqAmount(totalTransferInReqAmount);
            returnList.add(bcSubFundTotal);
            totalGrossBaseAmount = 0;
            totalGrossReqAmount = 0;
            totalNetTransferBaseAmount = 0;
            totalNetTransferReqAmount = 0;
            totalRevenueBaseAmount = 0;
            totalRevenueReqAmount = 0;
            totalTransferInBaseAmount = 0;
            totalTransferInReqAmount = 0;
        }
        return returnList;
    }

    protected List<String> fieldsForSubTotal() {
        List<String> fieldList = fieldsForTotal();
        fieldList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
        return fieldList;
    }


    protected List<String> fieldsForTotal() {
        List<String> fieldList = new ArrayList();
        fieldList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        fieldList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        fieldList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        fieldList.add(KFSPropertyConstants.FUND_GROUP_CODE);
        return fieldList;
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
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.SUB_FUND_SORT_CODE);
        returnList.add(KFSPropertyConstants.FUND_GROUP_CODE);
        returnList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
        returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.INCOME_EXPENSE_CODE);
        return returnList;
    }


    /**
     * sets budgetConstructionAccountSummaryReportDao
     * 
     * @param budgetConstructionAccountSummaryReportDao
     */
    public void setBudgetConstructionAccountSummaryReportDao(BudgetConstructionAccountSummaryReportDao budgetConstructionAccountSummaryReportDao) {
        this.budgetConstructionAccountSummaryReportDao = budgetConstructionAccountSummaryReportDao;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }
}
