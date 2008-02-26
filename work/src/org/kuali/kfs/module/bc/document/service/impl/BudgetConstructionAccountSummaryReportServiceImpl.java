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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.web.comparator.StringValueComparator;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionAccountSummary;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountSummaryReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountSummaryReportTotal;
import org.kuali.module.budget.dao.BudgetConstructionAccountSummaryReportDao;
import org.kuali.module.budget.service.BudgetConstructionAccountSummaryReportService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionAccountSummaryReportServiceImpl implements BudgetConstructionAccountSummaryReportService {

    BudgetConstructionAccountSummaryReportDao budgetConstructionAccountSummaryReportDao;
    
    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateRepotsAccountSummaryTable(java.lang.String)
     */
    public void updateReportsAccountSummaryTable(String personUserIdentifier) {
        budgetConstructionAccountSummaryReportDao.cleanReportsAccountSummaryTable(personUserIdentifier);
        budgetConstructionAccountSummaryReportDao.updateReportsAccountSummaryTable(personUserIdentifier);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateRepotsAccountSummaryTableWithConsolidation(java.lang.String)
     */
    public void updateReportsAccountSummaryTableWithConsolidation(String personUserIdentifier) {
        budgetConstructionAccountSummaryReportDao.cleanReportsAccountSummaryTable(personUserIdentifier);
        budgetConstructionAccountSummaryReportDao.updateReportsAccountSummaryTableWithConsolidation(personUserIdentifier);
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
     * @see org.kuali.module.budget.service.BudgetConstructionAccountSummaryReportService#buildReports(java.lang.Integer, java.util.Collection)
     */
    public Collection<BudgetConstructionOrgAccountSummaryReport> buildReports(Integer universityFiscalYear, Collection<BudgetConstructionAccountSummary> accountSummaryList) {
        Collection<BudgetConstructionOrgAccountSummaryReport> reportSet = new ArrayList();
        List<BudgetConstructionOrgAccountSummaryReportTotal> orgAccountSummaryReportTotalList;
        BudgetConstructionOrgAccountSummaryReport orgAccountSummaryReportEntry;
        
        // Making a list with same organizationChartOfAccountsCode, organizationCode, chartOfAccountsCode, subFundGroupCode
        List simpleList = deleteDuplicated((List) accountSummaryList);
        
        // Calculate Total Section
        orgAccountSummaryReportTotalList = calculateTotal((List) accountSummaryList, simpleList);
        for (BudgetConstructionAccountSummary accountSummaryEntry : accountSummaryList) {
            orgAccountSummaryReportEntry = new BudgetConstructionOrgAccountSummaryReport();
            buildReportsHeader(universityFiscalYear, orgAccountSummaryReportEntry, accountSummaryEntry);
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
    public void buildReportsHeader(Integer universityFiscalYear, BudgetConstructionOrgAccountSummaryReport orgAccountSummaryReportEntry, BudgetConstructionAccountSummary accountSummary) {
        String orgChartDesc = accountSummary.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = accountSummary.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = accountSummary.getOrganization().getOrganizationName();
        String reportChartDesc = accountSummary.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = accountSummary.getFundGroup().getName();
        String subFundGroupDes = accountSummary.getSubFundGroup().getSubFundGroupDescription();
        Integer prevFiscalyear = universityFiscalYear - 1;
        orgAccountSummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgAccountSummaryReportEntry.setOrgChartOfAccountsCode(accountSummary.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            orgAccountSummaryReportEntry.setOrgChartOfAccountDescription(BCConstants.Report.ERROR_GETTING_CHART_DESCRIPTION);
        }
        else {
            orgAccountSummaryReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }

        orgAccountSummaryReportEntry.setOrganizationCode(accountSummary.getOrganizationCode());
        if (orgName == null) {
            orgAccountSummaryReportEntry.setOrganizationName(BCConstants.Report.ERROR_GETTING_ORGANIZATION_NAME);
        }
        else {
            orgAccountSummaryReportEntry.setOrganizationName(orgName);
        }

        orgAccountSummaryReportEntry.setChartOfAccountsCode(accountSummary.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgAccountSummaryReportEntry.setChartOfAccountDescription(BCConstants.Report.ERROR_GETTING_CHART_DESCRIPTION);
        }
        else {
            orgAccountSummaryReportEntry.setChartOfAccountDescription(chartDesc);
        }

        orgAccountSummaryReportEntry.setFundGroupCode(accountSummary.getFundGroupCode());
        if (subFundGroupName == null) {
            orgAccountSummaryReportEntry.setFundGroupName(BCConstants.Report.ERROR_GETTING_FUNDGROUP_NAME);
        }
        else {
            orgAccountSummaryReportEntry.setFundGroupName(subFundGroupName);
        }

        orgAccountSummaryReportEntry.setSubFundGroupCode(accountSummary.getSubFundGroupCode());
        if (subFundGroupName == null) {
            orgAccountSummaryReportEntry.setSubFundGroupDescription(BCConstants.Report.ERROR_GETTING_SUBFUNDGROUP_DESCRIPTION);
        }
        else {
            orgAccountSummaryReportEntry.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgAccountSummaryReportEntry.setBaseFy(prevPrevFiscalyear.toString() + " - " + prevFiscalyear.toString().substring(2, 4));
        orgAccountSummaryReportEntry.setReqFy(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgAccountSummaryReportEntry.setHeader1(BCConstants.Report.HEADER_ACCOUNT_SUB);
        orgAccountSummaryReportEntry.setHeader2(BCConstants.Report.HEADER_ACCOUNT_SUB_NAME);
        orgAccountSummaryReportEntry.setHeader3(BCConstants.Report.HEADER_BASE_AMOUNT);
        orgAccountSummaryReportEntry.setHeader4(BCConstants.Report.HEADER_REQ_AMOUNT);
        orgAccountSummaryReportEntry.setHeader5(BCConstants.Report.HEADER_CHANGE);
        orgAccountSummaryReportEntry.setHeader6(BCConstants.Report.HEADER_CHANGE);
        orgAccountSummaryReportEntry.setConsHdr("");
    }

    /**
     * builds report body
     * 
     * @param BudgetConstructionAccountSummary bcas
     */
    public void buildReportsBody(BudgetConstructionOrgAccountSummaryReport orgAccountSummaryReportEntry, BudgetConstructionAccountSummary accountSummary) {
        orgAccountSummaryReportEntry.setAccountNumber(accountSummary.getAccountNumber());
        orgAccountSummaryReportEntry.setSubAccountNumber(accountSummary.getSubAccountNumber());
        boolean trExist = false;
        if (accountSummary.getSubAccountNumber().equals("-----")) {
            if (accountSummary.getAccount().getAccountName() == null) {
                orgAccountSummaryReportEntry.setAccountNameAndSubAccountName(BCConstants.Report.ERROR_GETTING_ACCOUNT_DESCRIPTION);
            }
            else
                orgAccountSummaryReportEntry.setAccountNameAndSubAccountName(accountSummary.getAccount().getAccountName());
        }
        else {
            // TODO check when the subAccountNumber is not "-----"
            try {
                if (accountSummary.getSubAccount().getSubAccountName() == null) {
                    orgAccountSummaryReportEntry.setAccountNameAndSubAccountName(BCConstants.Report.ERROR_GETTING_SUB_ACCOUNT_DESCRIPTION);
                }
                else
                    orgAccountSummaryReportEntry.setAccountNameAndSubAccountName(accountSummary.getSubAccount().getSubAccountName());
            }
            catch (PersistenceBrokerException e) {
                orgAccountSummaryReportEntry.setAccountNameAndSubAccountName(BCConstants.Report.ERROR_GETTING_SUB_ACCOUNT_DESCRIPTION);
            }
        }

        // build income expense description
        if (accountSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
            orgAccountSummaryReportEntry.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_REVENUE);
        }
        else if (accountSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_E)) {
            orgAccountSummaryReportEntry.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_EXP_GROSS);
        }
        else if (accountSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_T)) {
            trExist = true;
            orgAccountSummaryReportEntry.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_TRNFR_IN);
        }
        else {
            if (trExist) {
                trExist = false;
                orgAccountSummaryReportEntry.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_EXP_NET_TRNFR);
            }
            else {
                orgAccountSummaryReportEntry.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_EXPENDITURE);
            }
        }

        BigDecimal beginingBalanceLineAmt = BigDecimal.ZERO;
        BigDecimal accountLineAnnualBalAmt = BigDecimal.ZERO;
        if (accountSummary.getFinancialBeginningBalanceLineAmount() != null) {
            beginingBalanceLineAmt = accountSummary.getFinancialBeginningBalanceLineAmount().bigDecimalValue();
        }

        if (accountSummary.getFinancialBeginningBalanceLineAmount() != null) {
            accountLineAnnualBalAmt = accountSummary.getAccountLineAnnualBalanceAmount().bigDecimalValue();
        }

        orgAccountSummaryReportEntry.setBaseAmount(beginingBalanceLineAmt);
        orgAccountSummaryReportEntry.setReqAmount(accountLineAnnualBalAmt);
        orgAccountSummaryReportEntry.setAmountChange(accountLineAnnualBalAmt.subtract(beginingBalanceLineAmt));

        if (!beginingBalanceLineAmt.equals(BigDecimal.ZERO)) {
            orgAccountSummaryReportEntry.setPercentChange(orgAccountSummaryReportEntry.getAmountChange().divide(beginingBalanceLineAmt, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        }
    }

    /**
     * builds report total
     * 
     * @param BudgetConstructionAccountSummary bcas
     * @param List reportTotalList
     */
    public void buildReportsTotal(BudgetConstructionOrgAccountSummaryReport orgAccountSummaryReportEntry, BudgetConstructionAccountSummary accountSummary, List reportTotalList) {
        Iterator totalListIter = reportTotalList.iterator();
        while (totalListIter.hasNext()) {
            BudgetConstructionOrgAccountSummaryReportTotal bcasTotalEntry = (BudgetConstructionOrgAccountSummaryReportTotal) totalListIter.next();
            if (isSameAccountSummaryEntry(accountSummary, bcasTotalEntry.getBcas())) {
                orgAccountSummaryReportEntry.setTotalRevenueBaseAmount(bcasTotalEntry.getTotalRevenueBaseAmount());
                orgAccountSummaryReportEntry.setTotalGrossBaseAmount(bcasTotalEntry.getTotalGrossBaseAmount());
                orgAccountSummaryReportEntry.setTotalTransferInBaseAmount(bcasTotalEntry.getTotalTransferInBaseAmount());
                orgAccountSummaryReportEntry.setTotalNetTransferBaseAmount(bcasTotalEntry.getTotalNetTransferBaseAmount());

                orgAccountSummaryReportEntry.setTotalRevenueReqAmount(bcasTotalEntry.getTotalRevenueReqAmount());
                orgAccountSummaryReportEntry.setTotalGrossReqAmount(bcasTotalEntry.getTotalGrossReqAmount());
                orgAccountSummaryReportEntry.setTotalTransferInReqAmount(bcasTotalEntry.getTotalTransferInReqAmount());
                orgAccountSummaryReportEntry.setTotalNetTransferReqAmount(bcasTotalEntry.getTotalNetTransferReqAmount());

                orgAccountSummaryReportEntry.setTotalRevenueAmountChange(orgAccountSummaryReportEntry.getTotalRevenueReqAmount().subtract(orgAccountSummaryReportEntry.getTotalRevenueBaseAmount()));
                if (!orgAccountSummaryReportEntry.getTotalRevenueBaseAmount().equals(BigDecimal.ZERO)) {
                    orgAccountSummaryReportEntry.setTotalRevenuePercentChange(orgAccountSummaryReportEntry.getTotalRevenueAmountChange().divide(orgAccountSummaryReportEntry.getTotalRevenueBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }

                orgAccountSummaryReportEntry.setTotalGrossAmountChange(orgAccountSummaryReportEntry.getTotalGrossReqAmount().subtract(orgAccountSummaryReportEntry.getTotalGrossBaseAmount()));

                if (!orgAccountSummaryReportEntry.getTotalGrossBaseAmount().equals(BigDecimal.ZERO)) {
                    orgAccountSummaryReportEntry.setTotalGrossPercentChange(orgAccountSummaryReportEntry.getTotalGrossAmountChange().divide(orgAccountSummaryReportEntry.getTotalGrossBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }

                orgAccountSummaryReportEntry.setTotalTransferAmountChange(orgAccountSummaryReportEntry.getTotalTransferInReqAmount().subtract(orgAccountSummaryReportEntry.getTotalTransferInBaseAmount()));

                if (!orgAccountSummaryReportEntry.getTotalTransferInBaseAmount().equals(BigDecimal.ZERO)) {
                    orgAccountSummaryReportEntry.setTotalTransferInPercentChange(orgAccountSummaryReportEntry.getTotalTransferAmountChange().divide(orgAccountSummaryReportEntry.getTotalTransferInBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }

                orgAccountSummaryReportEntry.setTotalNetTransferAmountChange(orgAccountSummaryReportEntry.getTotalNetTransferReqAmount().subtract(orgAccountSummaryReportEntry.getTotalNetTransferBaseAmount()));
                if (!orgAccountSummaryReportEntry.getTotalNetTransferBaseAmount().equals(BigDecimal.ZERO)) {
                    orgAccountSummaryReportEntry.setTotalNetTransferPercentChange(orgAccountSummaryReportEntry.getTotalNetTransferAmountChange().divide(orgAccountSummaryReportEntry.getTotalNetTransferBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }

                orgAccountSummaryReportEntry.setRevExpDifferenceBaseAmount(orgAccountSummaryReportEntry.getTotalRevenueBaseAmount().subtract(orgAccountSummaryReportEntry.getTotalNetTransferBaseAmount()));
                orgAccountSummaryReportEntry.setRevExpDifferenceReqAmount(orgAccountSummaryReportEntry.getTotalRevenueReqAmount().subtract(orgAccountSummaryReportEntry.getTotalNetTransferReqAmount()));
                orgAccountSummaryReportEntry.setRevExpDifferenceAmountChange(orgAccountSummaryReportEntry.getRevExpDifferenceReqAmount().subtract(orgAccountSummaryReportEntry.getRevExpDifferenceBaseAmount()));

                if (!orgAccountSummaryReportEntry.getRevExpDifferenceBaseAmount().equals(BigDecimal.ZERO)) {
                    orgAccountSummaryReportEntry.setRevExpDifferencePercentChange(orgAccountSummaryReportEntry.getRevExpDifferenceAmountChange().divide(orgAccountSummaryReportEntry.getRevExpDifferenceBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
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
    public List calculateTotal(List bcasList, List simpleList) {
        
        BigDecimal totalRevenueBaseAmount = BigDecimal.ZERO;
        BigDecimal totalGrossBaseAmount = BigDecimal.ZERO;
        BigDecimal totalTransferInBaseAmount = BigDecimal.ZERO;
        BigDecimal totalNetTransferBaseAmount = BigDecimal.ZERO;
        BigDecimal totalRevenueReqAmount = BigDecimal.ZERO;
        BigDecimal totalGrossReqAmount = BigDecimal.ZERO;
        BigDecimal totalTransferInReqAmount = BigDecimal.ZERO;
        BigDecimal totalNetTransferReqAmount = BigDecimal.ZERO;
        BigDecimal totalRevenueAmountChange = BigDecimal.ZERO;
        BigDecimal totalGrossAmountChange = BigDecimal.ZERO;
        BigDecimal totalTransferAmountChange = BigDecimal.ZERO;
        BigDecimal totalNetTransferAmountChange = BigDecimal.ZERO;
        BigDecimal totalRevenuePercentChange = BigDecimal.ZERO;
        BigDecimal totalGrossPercentChange = BigDecimal.ZERO;
        BigDecimal totalTransferInPercentChange = BigDecimal.ZERO;
        BigDecimal totalNetTransferPercentChange = BigDecimal.ZERO;
        BigDecimal revExpDifferenceBaseAmount = BigDecimal.ZERO;
        BigDecimal revExpDifferenceReqAmount = BigDecimal.ZERO;
        BigDecimal revExpDifferenceAmountChange = BigDecimal.ZERO;
        BigDecimal revExpDifferencePercentChange = BigDecimal.ZERO;

        List returnList = new ArrayList();
        Iterator simpleListIterator = simpleList.iterator();
        boolean prev = false;
        while (simpleListIterator.hasNext()) {
            BudgetConstructionAccountSummary simpleBcasEntry = (BudgetConstructionAccountSummary) simpleListIterator.next();
            Iterator bcasListIterator = bcasList.iterator();
            while (bcasListIterator.hasNext()) {
                BudgetConstructionAccountSummary bcasListEntry = (BudgetConstructionAccountSummary) bcasListIterator.next();
                if (isSameAccountSummaryEntry(simpleBcasEntry, bcasListEntry)) {
                    if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
                        prev = false;
                        totalRevenueBaseAmount = totalRevenueBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                        totalRevenueReqAmount = totalRevenueReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_E)) {
                        prev = false;
                        totalGrossBaseAmount = totalGrossBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                        totalGrossReqAmount = totalGrossReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());

                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_T)) {
                        prev = true;
                        totalTransferInBaseAmount = totalTransferInBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                        totalTransferInReqAmount = totalTransferInReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_X)) {
                        totalNetTransferBaseAmount = totalNetTransferBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                        totalNetTransferReqAmount = totalNetTransferReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                        if (!prev) {
                            prev = false;
                            totalGrossBaseAmount = totalGrossBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                            totalGrossReqAmount = totalGrossReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());
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
            totalGrossBaseAmount = BigDecimal.ZERO;
            totalGrossReqAmount = BigDecimal.ZERO;
            totalNetTransferBaseAmount = BigDecimal.ZERO;
            totalNetTransferReqAmount = BigDecimal.ZERO;
            totalRevenueBaseAmount = BigDecimal.ZERO;
            totalRevenueReqAmount = BigDecimal.ZERO;
            totalTransferInBaseAmount = BigDecimal.ZERO;
            totalTransferInReqAmount = BigDecimal.ZERO;
        }

        return returnList;
    }

    /**
     * Checks wheather or not the entry of account is same
     * 
     * @param BudgetConstructionAccountSummary firstBcas
     * @param BudgetConstructionAccountSummary secondBcas
     * @return true if the given entries are same; otherwise, return false
     */
    public boolean isSameAccountSummaryEntry(BudgetConstructionAccountSummary firstBcas, BudgetConstructionAccountSummary secondBcas) {
        if (firstBcas.getOrganizationChartOfAccountsCode().equals(secondBcas.getOrganizationChartOfAccountsCode()) && firstBcas.getOrganizationCode().equals(secondBcas.getOrganizationCode()) && firstBcas.getChartOfAccountsCode().equals(secondBcas.getChartOfAccountsCode()) && firstBcas.getSubFundGroupCode().equals(secondBcas.getSubFundGroupCode())) {
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
    public List deleteDuplicated(List list) {
        int count = 0;
        BudgetConstructionAccountSummary accountSummaryEntry = null;
        BudgetConstructionAccountSummary accountSummaryEntryAux = null;
        List returnList = new ArrayList();
        if ((list != null) && (list.size() > 0)) {
            accountSummaryEntry = (BudgetConstructionAccountSummary) list.get(count);
            accountSummaryEntryAux = (BudgetConstructionAccountSummary) list.get(count);
            returnList.add(accountSummaryEntry);
            count++;
            while (count < list.size()) {
                accountSummaryEntry = (BudgetConstructionAccountSummary) list.get(count);

                if (!isSameAccountSummaryEntry(accountSummaryEntry, accountSummaryEntryAux)) {
                    returnList.add(accountSummaryEntry);
                    accountSummaryEntryAux = accountSummaryEntry;
                }
                count++;
            }
        }

        return returnList;
    }
}
