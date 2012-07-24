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
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgAccountSummaryReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgAccountSummaryReportTotal;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionAccountSummaryReportDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionAccountSummaryReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.report.BudgetConstructionReportHelper;
import org.kuali.kfs.module.bc.util.BudgetConstructionUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionAccountSummaryReportServiceImpl implements BudgetConstructionAccountSummaryReportService {

    protected BudgetConstructionAccountSummaryReportDao budgetConstructionAccountSummaryReportDao;
    protected ConfigurationService kualiConfigurationService;
    protected BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;
    protected boolean trExist = false;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetReportsControlListService#updateRepotsAccountSummaryTable(java.lang.String)
     */
    @Override
    public void updateReportsAccountSummaryTable(String principalName) {
        String expenditureINList = BudgetConstructionUtils.getExpenditureINList();
        String revenueINList = BudgetConstructionUtils.getRevenueINList();
        budgetConstructionAccountSummaryReportDao.cleanReportsAccountSummaryTable(principalName);
        budgetConstructionAccountSummaryReportDao.updateReportsAccountSummaryTable(principalName, revenueINList, expenditureINList);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetReportsControlListService#updateRepotsAccountSummaryTableWithConsolidation(java.lang.String)
     */
    @Override
    public void updateReportsAccountSummaryTableWithConsolidation(String principalName) {
        String expenditureINList = BudgetConstructionUtils.getExpenditureINList();
        String revenueINList = BudgetConstructionUtils.getRevenueINList();
        budgetConstructionAccountSummaryReportDao.cleanReportsAccountSummaryTable(principalName);
        budgetConstructionAccountSummaryReportDao.updateReportsAccountSummaryTableWithConsolidation(principalName, revenueINList, expenditureINList);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionAccountSummaryReportService#updateReportsAccountSummaryTable(java.lang.String,
     *      boolean)
     */
    @Override
    public void updateReportsAccountSummaryTable(String principalName, boolean consolidated) {
        if (consolidated) {
            updateReportsAccountSummaryTableWithConsolidation(principalName);
        }
        else {
            updateReportsAccountSummaryTable(principalName);
        }
    }

    /**
     * sets budgetConstructionAccountSummaryReportDao
     *
     * @param budgetConstructionAccountSummaryReportDao
     */
    public void setBudgetConstructionAccountSummaryReportDao(BudgetConstructionAccountSummaryReportDao budgetConstructionAccountSummaryReportDao) {
        this.budgetConstructionAccountSummaryReportDao = budgetConstructionAccountSummaryReportDao;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionAccountSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    @Override
    public Collection<BudgetConstructionOrgAccountSummaryReport> buildReports(Integer universityFiscalYear, String principalName, boolean consolidated) {
        Collection<BudgetConstructionOrgAccountSummaryReport> reportSet = new ArrayList();
        Collection<BudgetConstructionAccountSummary> accountSummaryList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionAccountSummary.class, principalName, buildOrderByList());

        // Making a list with same organizationChartOfAccountsCode, organizationCode, chartOfAccountsCode, subFundGroupCode
        List totalList = BudgetConstructionReportHelper.deleteDuplicated((List) accountSummaryList, fieldsForTotal());

        // Calculate Total Section
        List<BudgetConstructionOrgAccountSummaryReportTotal> orgAccountSummaryReportTotalList = calculateTotal((List) accountSummaryList, totalList);

        // builds report
        for (BudgetConstructionAccountSummary accountSummaryEntry : accountSummaryList) {
            BudgetConstructionOrgAccountSummaryReport orgAccountSummaryReportEntry = new BudgetConstructionOrgAccountSummaryReport();
            buildReportsHeader(universityFiscalYear, orgAccountSummaryReportEntry, accountSummaryEntry, consolidated);
            buildReportsBody(orgAccountSummaryReportEntry, accountSummaryEntry);
            buildReportsTotal(orgAccountSummaryReportEntry, accountSummaryEntry, orgAccountSummaryReportTotalList);
            reportSet.add(orgAccountSummaryReportEntry);
        }

        return reportSet;
    }

    /**
     * builds report Header
     *
     * @param BudgetConstructionAccountSummary bcas
     */
    protected void buildReportsHeader(Integer universityFiscalYear, BudgetConstructionOrgAccountSummaryReport orgAccountSummaryReportEntry, BudgetConstructionAccountSummary accountSummary, boolean consolidated) {
        String orgChartDesc = accountSummary.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = accountSummary.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = accountSummary.getOrganization().getOrganizationName();
        String reportChartDesc = accountSummary.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = accountSummary.getFundGroup().getName();
        String subFundGroupDes = accountSummary.getSubFundGroup().getSubFundGroupDescription();
        Integer prevFiscalyear = universityFiscalYear - 1;
        orgAccountSummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgAccountSummaryReportEntry.setOrgChartOfAccountsCode(accountSummary.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            orgAccountSummaryReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgAccountSummaryReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }

        orgAccountSummaryReportEntry.setOrganizationCode(accountSummary.getOrganizationCode());
        if (orgName == null) {
            orgAccountSummaryReportEntry.setOrganizationName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgAccountSummaryReportEntry.setOrganizationName(orgName);
        }

        orgAccountSummaryReportEntry.setChartOfAccountsCode(accountSummary.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgAccountSummaryReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgAccountSummaryReportEntry.setChartOfAccountDescription(chartDesc);
        }

        orgAccountSummaryReportEntry.setFundGroupCode(accountSummary.getFundGroupCode());
        if (subFundGroupName == null) {
            orgAccountSummaryReportEntry.setFundGroupName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            orgAccountSummaryReportEntry.setFundGroupName(subFundGroupName);
        }

        orgAccountSummaryReportEntry.setSubFundGroupCode(accountSummary.getSubFundGroupCode());
        if (subFundGroupName == null) {
            orgAccountSummaryReportEntry.setSubFundGroupDescription(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_SUBFUNDGROUP_DESCRIPTION));
        }
        else {
            orgAccountSummaryReportEntry.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgAccountSummaryReportEntry.setBaseFy(prevPrevFiscalyear.toString() + "-" + prevFiscalyear.toString().substring(2, 4));
        orgAccountSummaryReportEntry.setReqFy(prevFiscalyear.toString() + "-" + universityFiscalYear.toString().substring(2, 4));
        orgAccountSummaryReportEntry.setHeader1(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_ACCOUNT_SUB));
        orgAccountSummaryReportEntry.setHeader2(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_ACCOUNT_SUB_NAME));
        orgAccountSummaryReportEntry.setHeader3(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_BASE_AMOUNT));
        orgAccountSummaryReportEntry.setHeader4(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_REQ_AMOUNT));
        orgAccountSummaryReportEntry.setHeader5(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgAccountSummaryReportEntry.setHeader6(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        if (consolidated) {
            orgAccountSummaryReportEntry.setConsHdr(BCConstants.Report.CONSOLIIDATED);
        }
        else {
            orgAccountSummaryReportEntry.setConsHdr(BCConstants.Report.BLANK);
        }
    }

    /**
     * builds report body
     *
     * @param BudgetConstructionAccountSummary bcas
     */
    protected void buildReportsBody(BudgetConstructionOrgAccountSummaryReport orgAccountSummaryReportEntry, BudgetConstructionAccountSummary accountSummary) {
        orgAccountSummaryReportEntry.setAccountNumber(accountSummary.getAccountNumber());
        orgAccountSummaryReportEntry.setSubAccountNumber(accountSummary.getSubAccountNumber());

        if (accountSummary.getSubAccountNumber().equals(KFSConstants.getDashSubAccountNumber())) {
            if (accountSummary.getAccount().getAccountName() == null) {
                orgAccountSummaryReportEntry.setAccountNameAndSubAccountName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_ACCOUNT_DESCRIPTION));
            }
            else {
                orgAccountSummaryReportEntry.setAccountNameAndSubAccountName(accountSummary.getAccount().getAccountName());
            }
        }
        else {
            try {
                if (accountSummary.getSubAccount().getSubAccountName() == null) {
                    orgAccountSummaryReportEntry.setAccountNameAndSubAccountName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_SUB_ACCOUNT_DESCRIPTION));
                }
                else {
                    orgAccountSummaryReportEntry.setAccountNameAndSubAccountName(accountSummary.getSubAccount().getSubAccountName());
                }
            }
            catch (PersistenceBrokerException e) {
                orgAccountSummaryReportEntry.setAccountNameAndSubAccountName(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.ERROR_REPORT_GETTING_SUB_ACCOUNT_DESCRIPTION));
            }
        }

        // build income expense description
        if (accountSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
            orgAccountSummaryReportEntry.setIncExpDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_REVENUE));
        }
        else if (accountSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_E)) {
            orgAccountSummaryReportEntry.setIncExpDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_EXP_GROSS));
        }
        else if (accountSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_T)) {
            trExist = true;
            orgAccountSummaryReportEntry.setIncExpDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_TRNFR_IN));
        }
        else {
            if (trExist) {
                trExist = false;
                orgAccountSummaryReportEntry.setIncExpDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_EXP_NET_TRNFR));
            }
            else {
                orgAccountSummaryReportEntry.setIncExpDesc(kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_EXPENDITURE));
            }
        }

        Integer beginingBalanceLineAmt = 0;
        Integer accountLineAnnualBalAmt = 0;
        if (accountSummary.getFinancialBeginningBalanceLineAmount() != null) {
            beginingBalanceLineAmt = BudgetConstructionReportHelper.convertKualiInteger(accountSummary.getFinancialBeginningBalanceLineAmount());
        }

        if (accountSummary.getFinancialBeginningBalanceLineAmount() != null) {
            accountLineAnnualBalAmt = BudgetConstructionReportHelper.convertKualiInteger(accountSummary.getAccountLineAnnualBalanceAmount());
        }

        orgAccountSummaryReportEntry.setBaseAmount(beginingBalanceLineAmt);
        orgAccountSummaryReportEntry.setReqAmount(accountLineAnnualBalAmt);
        orgAccountSummaryReportEntry.setAmountChange(accountLineAnnualBalAmt - beginingBalanceLineAmt);

        orgAccountSummaryReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(orgAccountSummaryReportEntry.getAmountChange(), beginingBalanceLineAmt));

    }

    /**
     * builds report total
     *
     * @param BudgetConstructionAccountSummary bcas
     * @param List reportTotalList
     */
    protected void buildReportsTotal(BudgetConstructionOrgAccountSummaryReport orgAccountSummaryReportEntry, BudgetConstructionAccountSummary accountSummary, List reportTotalList) {
        Iterator totalListIter = reportTotalList.iterator();
        while (totalListIter.hasNext()) {
            BudgetConstructionOrgAccountSummaryReportTotal bcasTotalEntry = (BudgetConstructionOrgAccountSummaryReportTotal) totalListIter.next();
            if (BudgetConstructionReportHelper.isSameEntry(accountSummary, bcasTotalEntry.getBcas(), fieldsForTotal())) {
                BigDecimal percentChange = BigDecimal.ZERO;
                orgAccountSummaryReportEntry.setTotalRevenueBaseAmount(bcasTotalEntry.getTotalRevenueBaseAmount());
                orgAccountSummaryReportEntry.setTotalGrossBaseAmount(bcasTotalEntry.getTotalGrossBaseAmount());
                orgAccountSummaryReportEntry.setTotalTransferInBaseAmount(bcasTotalEntry.getTotalTransferInBaseAmount());
                orgAccountSummaryReportEntry.setTotalNetTransferBaseAmount(bcasTotalEntry.getTotalNetTransferBaseAmount());

                orgAccountSummaryReportEntry.setTotalRevenueReqAmount(bcasTotalEntry.getTotalRevenueReqAmount());
                orgAccountSummaryReportEntry.setTotalGrossReqAmount(bcasTotalEntry.getTotalGrossReqAmount());
                orgAccountSummaryReportEntry.setTotalTransferInReqAmount(bcasTotalEntry.getTotalTransferInReqAmount());
                orgAccountSummaryReportEntry.setTotalNetTransferReqAmount(bcasTotalEntry.getTotalNetTransferReqAmount());

                orgAccountSummaryReportEntry.setTotalRevenueAmountChange(orgAccountSummaryReportEntry.getTotalRevenueReqAmount() - orgAccountSummaryReportEntry.getTotalRevenueBaseAmount());
                percentChange = BudgetConstructionReportHelper.calculatePercent(orgAccountSummaryReportEntry.getTotalRevenueAmountChange(), orgAccountSummaryReportEntry.getTotalRevenueBaseAmount());
                orgAccountSummaryReportEntry.setTotalRevenuePercentChange(percentChange);

                orgAccountSummaryReportEntry.setTotalGrossAmountChange(orgAccountSummaryReportEntry.getTotalGrossReqAmount() - orgAccountSummaryReportEntry.getTotalGrossBaseAmount());
                percentChange = BudgetConstructionReportHelper.calculatePercent(orgAccountSummaryReportEntry.getTotalGrossAmountChange(), orgAccountSummaryReportEntry.getTotalGrossBaseAmount());
                orgAccountSummaryReportEntry.setTotalGrossPercentChange(percentChange);

                orgAccountSummaryReportEntry.setTotalTransferAmountChange(orgAccountSummaryReportEntry.getTotalTransferInReqAmount() - orgAccountSummaryReportEntry.getTotalTransferInBaseAmount());
                percentChange = BudgetConstructionReportHelper.calculatePercent(orgAccountSummaryReportEntry.getTotalTransferAmountChange(), orgAccountSummaryReportEntry.getTotalTransferInBaseAmount());
                orgAccountSummaryReportEntry.setTotalTransferInPercentChange(percentChange);

                orgAccountSummaryReportEntry.setTotalNetTransferAmountChange(orgAccountSummaryReportEntry.getTotalNetTransferReqAmount() - orgAccountSummaryReportEntry.getTotalNetTransferBaseAmount());
                percentChange = BudgetConstructionReportHelper.calculatePercent(orgAccountSummaryReportEntry.getTotalNetTransferAmountChange(), orgAccountSummaryReportEntry.getTotalNetTransferBaseAmount());
                orgAccountSummaryReportEntry.setTotalNetTransferPercentChange(percentChange);

                orgAccountSummaryReportEntry.setRevExpDifferenceBaseAmount(orgAccountSummaryReportEntry.getTotalRevenueBaseAmount() - orgAccountSummaryReportEntry.getTotalNetTransferBaseAmount());
                orgAccountSummaryReportEntry.setRevExpDifferenceReqAmount(orgAccountSummaryReportEntry.getTotalRevenueReqAmount() - orgAccountSummaryReportEntry.getTotalNetTransferReqAmount());
                orgAccountSummaryReportEntry.setRevExpDifferenceAmountChange(orgAccountSummaryReportEntry.getRevExpDifferenceReqAmount() - orgAccountSummaryReportEntry.getRevExpDifferenceBaseAmount());
                percentChange = BudgetConstructionReportHelper.calculatePercent(orgAccountSummaryReportEntry.getRevExpDifferenceAmountChange(), orgAccountSummaryReportEntry.getRevExpDifferenceBaseAmount());
                orgAccountSummaryReportEntry.setRevExpDifferencePercentChange(percentChange);
            }
        }
    }

    /**
     * Calculates total part of report
     *
     * @param List bcasList
     * @param List simpleList
     */
    protected List calculateTotal(List bcasList, List simpleList) {

        Integer totalRevenueBaseAmount = 0;
        Integer totalGrossBaseAmount = 0;
        Integer totalTransferInBaseAmount = 0;
        Integer totalNetTransferBaseAmount = 0;
        Integer totalRevenueReqAmount = 0;
        Integer totalGrossReqAmount = 0;
        Integer totalTransferInReqAmount = 0;
        Integer totalNetTransferReqAmount = 0;

        List returnList = new ArrayList();
        Iterator simpleListIterator = simpleList.iterator();
        boolean prev = false;
        while (simpleListIterator.hasNext()) {
            BudgetConstructionAccountSummary simpleBcasEntry = (BudgetConstructionAccountSummary) simpleListIterator.next();
            Iterator bcasListIterator = bcasList.iterator();
            while (bcasListIterator.hasNext()) {
                BudgetConstructionAccountSummary bcasListEntry = (BudgetConstructionAccountSummary) bcasListIterator.next();
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcasEntry, bcasListEntry, fieldsForTotal())) {
                    if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
                        prev = false;
                        totalRevenueBaseAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getFinancialBeginningBalanceLineAmount());
                        totalRevenueReqAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getAccountLineAnnualBalanceAmount());
                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_E)) {
                        prev = false;
                        totalGrossBaseAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getFinancialBeginningBalanceLineAmount());
                        totalGrossReqAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getAccountLineAnnualBalanceAmount());

                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_T)) {
                        prev = true;
                        totalTransferInBaseAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getFinancialBeginningBalanceLineAmount());
                        totalTransferInReqAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getAccountLineAnnualBalanceAmount());
                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_X)) {
                        totalNetTransferBaseAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getFinancialBeginningBalanceLineAmount());
                        totalNetTransferReqAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getAccountLineAnnualBalanceAmount());
                        if (!prev) {
                            prev = false;
                            totalGrossBaseAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getFinancialBeginningBalanceLineAmount());
                            totalGrossReqAmount += BudgetConstructionReportHelper.convertKualiInteger(bcasListEntry.getAccountLineAnnualBalanceAmount());
                        }
                    }
                }
            }
            BudgetConstructionOrgAccountSummaryReportTotal bcoasrTotal = new BudgetConstructionOrgAccountSummaryReportTotal();
            bcoasrTotal.setBcas(simpleBcasEntry);
            bcoasrTotal.setTotalGrossBaseAmount(totalGrossBaseAmount);
            bcoasrTotal.setTotalGrossReqAmount(totalGrossReqAmount);
            bcoasrTotal.setTotalNetTransferBaseAmount(totalNetTransferBaseAmount);
            bcoasrTotal.setTotalNetTransferReqAmount(totalNetTransferReqAmount);
            bcoasrTotal.setTotalRevenueBaseAmount(totalRevenueBaseAmount);
            bcoasrTotal.setTotalRevenueReqAmount(totalRevenueReqAmount);
            bcoasrTotal.setTotalTransferInBaseAmount(totalTransferInBaseAmount);
            bcoasrTotal.setTotalTransferInReqAmount(totalTransferInReqAmount);
            returnList.add(bcoasrTotal);
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

    protected List<String> fieldsForTotal() {

        List<String> fieldList = new ArrayList();
        fieldList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        fieldList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        fieldList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        fieldList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);

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

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }
}
