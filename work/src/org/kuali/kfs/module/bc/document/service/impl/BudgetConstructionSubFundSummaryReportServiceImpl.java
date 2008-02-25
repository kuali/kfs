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
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionAccountSummary;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountSummaryReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountSummaryReportTotal;
import org.kuali.module.budget.bo.BudgetConstructionOrgSubFundSummaryReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgSubFundSummaryReportTotal;
import org.kuali.module.budget.dao.BudgetConstructionAccountSummaryReportDao;
import org.kuali.module.budget.service.BudgetConstructionSubFundSummaryReportService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionSubFundSummaryReportServiceImpl implements BudgetConstructionSubFundSummaryReportService {

    BudgetConstructionAccountSummaryReportDao budgetConstructionAccountSummaryReportDao;

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateSubFundSummaryReport(java.lang.String)
     */
    public void updateSubFundSummaryReport(String personUserIdentifier) {
        budgetConstructionAccountSummaryReportDao.cleanReportsAccountSummaryTable(personUserIdentifier);
        budgetConstructionAccountSummaryReportDao.updateSubFundSummaryReport(personUserIdentifier);
    }


    /**
     * builds a report
     * 
     * @param Collection<BudgetConstructionAccountSummary> list
     */
    public Collection<BudgetConstructionOrgSubFundSummaryReport> buildReports(Integer universityFiscalYear, Collection<BudgetConstructionAccountSummary> subFundSummaryList) {
        Collection<BudgetConstructionOrgSubFundSummaryReport> reportSet = new ArrayList();
        List<BudgetConstructionOrgSubFundSummaryReportTotal> orgSubFundSummaryReportTotalList;
        BudgetConstructionOrgSubFundSummaryReport orgSubFundSummaryReportEntry;

        // Making a list with same organizationChartOfAccountsCode, organizationCode, chartOfAccountsCode, subFundGroupCode
        List simpleList = deleteDuplicated((List) subFundSummaryList);

        // Calculate Total Section
        orgSubFundSummaryReportTotalList = calculateTotal((List) subFundSummaryList, simpleList);
        for (BudgetConstructionAccountSummary subFundSummaryEntry : subFundSummaryList) {
            orgSubFundSummaryReportEntry = new BudgetConstructionOrgSubFundSummaryReport();
            buildReportsHeader(universityFiscalYear, orgSubFundSummaryReportEntry, subFundSummaryEntry);
            buildReportsBody(orgSubFundSummaryReportEntry, subFundSummaryEntry);
            buildReportsTotal(orgSubFundSummaryReportEntry, subFundSummaryEntry, orgSubFundSummaryReportTotalList);
            reportSet.add(orgSubFundSummaryReportEntry);
        }

        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionAccountSummary bcas
     */
    public void buildReportsHeader(Integer universityFiscalYear, BudgetConstructionOrgSubFundSummaryReport orgSubFundSummaryReportEntry, BudgetConstructionAccountSummary subFundSummaryList) {
        String orgChartDesc = subFundSummaryList.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = subFundSummaryList.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = subFundSummaryList.getOrganization().getOrganizationName();
        String reportChartDesc = subFundSummaryList.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = subFundSummaryList.getFundGroup().getName();
        String subFundGroupDes = subFundSummaryList.getSubFundGroup().getSubFundGroupDescription();
        Integer prevFiscalyear = universityFiscalYear - 1;
        orgSubFundSummaryReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgSubFundSummaryReportEntry.setOrgChartOfAccountsCode(subFundSummaryList.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            orgSubFundSummaryReportEntry.setOrgChartOfAccountDescription(BCConstants.Report.ERROR_GETTING_CHART_DESCRIPTION);
        }
        else {
            orgSubFundSummaryReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }

        orgSubFundSummaryReportEntry.setOrganizationCode(subFundSummaryList.getOrganizationCode());
        if (orgName == null) {
            orgSubFundSummaryReportEntry.setOrganizationName(BCConstants.Report.ERROR_GETTING_ORGANIZATION_NAME);
        }
        else {
            orgSubFundSummaryReportEntry.setOrganizationName(orgName);
        }

        orgSubFundSummaryReportEntry.setChartOfAccountsCode(subFundSummaryList.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgSubFundSummaryReportEntry.setChartOfAccountDescription(BCConstants.Report.ERROR_GETTING_CHART_DESCRIPTION);
        }
        else {
            orgSubFundSummaryReportEntry.setChartOfAccountDescription(chartDesc);
        }

        orgSubFundSummaryReportEntry.setFundGroupCode(subFundSummaryList.getFundGroupCode());
        if (subFundGroupName == null) {
            orgSubFundSummaryReportEntry.setFundGroupName(BCConstants.Report.ERROR_GETTING_FUNDGROUP_NAME);
        }
        else {
            orgSubFundSummaryReportEntry.setFundGroupName(subFundGroupName);
        }

        orgSubFundSummaryReportEntry.setSubFundGroupCode(subFundSummaryList.getSubFundGroupCode());
        if (subFundGroupName == null) {
            orgSubFundSummaryReportEntry.setSubFundGroupDescription(BCConstants.Report.ERROR_GETTING_SUBFUNDGROUP_DESCRIPTION);
        }
        else {
            orgSubFundSummaryReportEntry.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgSubFundSummaryReportEntry.setBaseFy(prevPrevFiscalyear.toString() + " - " + prevFiscalyear.toString().substring(2, 4));
        orgSubFundSummaryReportEntry.setReqFy(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgSubFundSummaryReportEntry.setHeader1(BCConstants.Report.HEADER_SUBFUND);
        orgSubFundSummaryReportEntry.setHeader2(BCConstants.Report.HEADER_SUBFUND_DESCRIPTION);
        orgSubFundSummaryReportEntry.setHeader3(BCConstants.Report.HEADER_BASE_AMOUNT);
        orgSubFundSummaryReportEntry.setHeader4(BCConstants.Report.HEADER_REQ_AMOUNT);
        orgSubFundSummaryReportEntry.setHeader5(BCConstants.Report.HEADER_CHANGE);
        orgSubFundSummaryReportEntry.setHeader6(BCConstants.Report.HEADER_CHANGE);
        orgSubFundSummaryReportEntry.setConsHdr("");
    }

    /**
     * builds report body
     * 
     * @param BudgetConstructionAccountSummary bcas
     */
    public void buildReportsBody(BudgetConstructionOrgSubFundSummaryReport orgSubFundSummaryReportEntry, BudgetConstructionAccountSummary subFundSummary) {
        boolean trExist = false;

        /*
         * orgSubFundSummaryReportEntry.setAccountNumber(subFundSummary.getAccountNumber());
         * orgSubFundSummaryReportEntry.setSubAccountNumber(subFundSummary.getSubAccountNumber()); if
         * (subFundSummary.getSubAccountNumber().equals("-----")) { if (subFundSummary.getAccount().getAccountName() == null) {
         * orgSubFundSummaryReportEntry.setAccountNameAndSubAccountName(BCConstants.Report.ERROR_GETTING_ACCOUNT_DESCRIPTION); }
         * else orgSubFundSummaryReportEntry.setAccountNameAndSubAccountName(subFundSummary.getAccount().getAccountName()); } else { //
         * TODO check when the subAccountNumber is not "-----" try { if (subFundSummary.getSubAccount().getSubAccountName() == null) {
         * orgSubFundSummaryReportEntry.setAccountNameAndSubAccountName(BCConstants.Report.ERROR_GETTING_SUB_ACCOUNT_DESCRIPTION); }
         * else orgSubFundSummaryReportEntry.setAccountNameAndSubAccountName(subFundSummary.getSubAccount().getSubAccountName()); }
         * catch (PersistenceBrokerException e) {
         * orgSubFundSummaryReportEntry.setAccountNameAndSubAccountName(BCConstants.Report.ERROR_GETTING_SUB_ACCOUNT_DESCRIPTION); } }
         */

        // build income expense description
        if (subFundSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
            orgSubFundSummaryReportEntry.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_REVENUE);
        }
        else if (subFundSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_E)) {
            orgSubFundSummaryReportEntry.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_EXP_GROSS);
        }
        else if (subFundSummary.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_T)) {
            trExist = true;
            orgSubFundSummaryReportEntry.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_TRNFR_IN);
        }
        else {
            if (trExist) {
                trExist = false;
                orgSubFundSummaryReportEntry.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_EXP_NET_TRNFR);
            }
            else {
                orgSubFundSummaryReportEntry.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_EXPENDITURE);
            }
        }

        BigDecimal beginingBalanceLineAmt = BigDecimal.ZERO;
        BigDecimal accountLineAnnualBalAmt = BigDecimal.ZERO;
        if (subFundSummary.getFinancialBeginningBalanceLineAmount() != null) {
            beginingBalanceLineAmt = subFundSummary.getFinancialBeginningBalanceLineAmount().bigDecimalValue();
        }

        if (subFundSummary.getFinancialBeginningBalanceLineAmount() != null) {
            accountLineAnnualBalAmt = subFundSummary.getAccountLineAnnualBalanceAmount().bigDecimalValue();
        }

        orgSubFundSummaryReportEntry.setBaseAmount(beginingBalanceLineAmt);
        orgSubFundSummaryReportEntry.setReqAmount(accountLineAnnualBalAmt);
        orgSubFundSummaryReportEntry.setAmountChange(accountLineAnnualBalAmt.subtract(beginingBalanceLineAmt));

        if (!beginingBalanceLineAmt.equals(BigDecimal.ZERO)) {
            orgSubFundSummaryReportEntry.setPercentChange(orgSubFundSummaryReportEntry.getAmountChange().divide(beginingBalanceLineAmt, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        }
    }

    /**
     * builds report total
     * 
     * @param BudgetConstructionAccountSummary bcas
     * @param List reportTotalList
     */
    public void buildReportsTotal(BudgetConstructionOrgSubFundSummaryReport orgSubFundSummaryReportEntry, BudgetConstructionAccountSummary subFundSummaryList, List reportTotalList) {
        Iterator totalListIter = reportTotalList.iterator();
        while (totalListIter.hasNext()) {
            BudgetConstructionOrgSubFundSummaryReportTotal subFundTotalEntry = (BudgetConstructionOrgSubFundSummaryReportTotal) totalListIter.next();
            if (isSameSubFundSummaryEntry(subFundSummaryList, subFundTotalEntry.getBcas())) {
                orgSubFundSummaryReportEntry.setSubFundTotalRevenueBaseAmount(subFundTotalEntry.getSubFundTotalRevenueBaseAmount());
                orgSubFundSummaryReportEntry.setSubFundTotalRevenueReqAmount(subFundTotalEntry.getSubFundTotalRevenueReqAmount());
                
                orgSubFundSummaryReportEntry.setTotalRevenueBaseAmount(subFundTotalEntry.getTotalRevenueBaseAmount());
                orgSubFundSummaryReportEntry.setTotalGrossBaseAmount(subFundTotalEntry.getTotalGrossBaseAmount());
                orgSubFundSummaryReportEntry.setTotalTransferInBaseAmount(subFundTotalEntry.getTotalTransferInBaseAmount());
                orgSubFundSummaryReportEntry.setTotalNetTransferBaseAmount(subFundTotalEntry.getTotalNetTransferBaseAmount());

                orgSubFundSummaryReportEntry.setTotalRevenueReqAmount(subFundTotalEntry.getTotalRevenueReqAmount());
                orgSubFundSummaryReportEntry.setTotalGrossReqAmount(subFundTotalEntry.getTotalGrossReqAmount());
                orgSubFundSummaryReportEntry.setTotalTransferInReqAmount(subFundTotalEntry.getTotalTransferInReqAmount());
                orgSubFundSummaryReportEntry.setTotalNetTransferReqAmount(subFundTotalEntry.getTotalNetTransferReqAmount());

                orgSubFundSummaryReportEntry.setTotalRevenueAmountChange(orgSubFundSummaryReportEntry.getTotalRevenueReqAmount().subtract(orgSubFundSummaryReportEntry.getTotalRevenueBaseAmount()));
                if (!orgSubFundSummaryReportEntry.getTotalRevenueBaseAmount().equals(BigDecimal.ZERO)) {
                    orgSubFundSummaryReportEntry.setTotalRevenuePercentChange(orgSubFundSummaryReportEntry.getTotalRevenueAmountChange().divide(orgSubFundSummaryReportEntry.getTotalRevenueBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }

                orgSubFundSummaryReportEntry.setTotalGrossAmountChange(orgSubFundSummaryReportEntry.getTotalGrossReqAmount().subtract(orgSubFundSummaryReportEntry.getTotalGrossBaseAmount()));

                if (!orgSubFundSummaryReportEntry.getTotalGrossBaseAmount().equals(BigDecimal.ZERO)) {
                    orgSubFundSummaryReportEntry.setTotalGrossPercentChange(orgSubFundSummaryReportEntry.getTotalGrossAmountChange().divide(orgSubFundSummaryReportEntry.getTotalGrossBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }

                orgSubFundSummaryReportEntry.setTotalTransferAmountChange(orgSubFundSummaryReportEntry.getTotalTransferInReqAmount().subtract(orgSubFundSummaryReportEntry.getTotalTransferInBaseAmount()));

                if (!orgSubFundSummaryReportEntry.getTotalTransferInBaseAmount().equals(BigDecimal.ZERO)) {
                    orgSubFundSummaryReportEntry.setTotalTransferInPercentChange(orgSubFundSummaryReportEntry.getTotalTransferAmountChange().divide(orgSubFundSummaryReportEntry.getTotalTransferInBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }

                orgSubFundSummaryReportEntry.setTotalNetTransferAmountChange(orgSubFundSummaryReportEntry.getTotalNetTransferReqAmount().subtract(orgSubFundSummaryReportEntry.getTotalNetTransferBaseAmount()));
                if (!orgSubFundSummaryReportEntry.getTotalNetTransferBaseAmount().equals(BigDecimal.ZERO)) {
                    orgSubFundSummaryReportEntry.setTotalNetTransferPercentChange(orgSubFundSummaryReportEntry.getTotalNetTransferAmountChange().divide(orgSubFundSummaryReportEntry.getTotalNetTransferBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }

                orgSubFundSummaryReportEntry.setRevExpDifferenceBaseAmount(orgSubFundSummaryReportEntry.getTotalRevenueBaseAmount().subtract(orgSubFundSummaryReportEntry.getTotalNetTransferBaseAmount()));
                orgSubFundSummaryReportEntry.setRevExpDifferenceReqAmount(orgSubFundSummaryReportEntry.getTotalRevenueReqAmount().subtract(orgSubFundSummaryReportEntry.getTotalNetTransferReqAmount()));
                orgSubFundSummaryReportEntry.setRevExpDifferenceAmountChange(orgSubFundSummaryReportEntry.getRevExpDifferenceReqAmount().subtract(orgSubFundSummaryReportEntry.getRevExpDifferenceBaseAmount()));

                if (!orgSubFundSummaryReportEntry.getRevExpDifferenceBaseAmount().equals(BigDecimal.ZERO)) {
                    orgSubFundSummaryReportEntry.setRevExpDifferencePercentChange(orgSubFundSummaryReportEntry.getRevExpDifferenceAmountChange().divide(orgSubFundSummaryReportEntry.getRevExpDifferenceBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
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

        BigDecimal subFundTotalRevenueBaseAmount = BigDecimal.ZERO;
        BigDecimal subFundTotalRevenueReqAmount = BigDecimal.ZERO;
    
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
        /*BigDecimal revExpDifferenceBaseAmount = BigDecimal.ZERO;
        BigDecimal revExpDifferenceReqAmount = BigDecimal.ZERO;
        BigDecimal revExpDifferenceAmountChange = BigDecimal.ZERO;
        BigDecimal revExpDifferencePercentChange = BigDecimal.ZERO;*/

        List returnList = new ArrayList();
        Iterator simpleListIterator = simpleList.iterator();
        while (simpleListIterator.hasNext()) {
            BudgetConstructionOrgSubFundSummaryReportTotal bcSubFundTotal = new BudgetConstructionOrgSubFundSummaryReportTotal();
            BudgetConstructionAccountSummary simpleBcasEntry = (BudgetConstructionAccountSummary) simpleListIterator.next();
            Iterator bcasListIterator = bcasList.iterator();
            while (bcasListIterator.hasNext()) {
                BudgetConstructionAccountSummary bcasListEntry = (BudgetConstructionAccountSummary) bcasListIterator.next();
                if (isSameSubFundSummaryEntry(simpleBcasEntry, bcasListEntry)) {
                    if (isSameSubFundSummaryEntryWithoutSubFundCode(simpleBcasEntry, bcasListEntry)) {
                        if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
                            subFundTotalRevenueBaseAmount = subFundTotalRevenueBaseAmount.subtract(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                            subFundTotalRevenueReqAmount = subFundTotalRevenueReqAmount.subtract(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                            totalRevenueBaseAmount = totalRevenueBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                            totalRevenueReqAmount = totalRevenueReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                        }
                        else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_E)) {
                            totalGrossBaseAmount = totalGrossBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                            totalGrossReqAmount = totalGrossReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());

                        }
                        else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_T)) {
                            totalTransferInBaseAmount = totalTransferInBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                            totalTransferInReqAmount = totalTransferInReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                        }
                        else if (bcasListEntry.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_X)) {
                            subFundTotalRevenueBaseAmount = subFundTotalRevenueBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                            subFundTotalRevenueReqAmount = subFundTotalRevenueReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                            totalNetTransferBaseAmount = totalNetTransferBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                            totalNetTransferReqAmount = totalNetTransferReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                        }
                    }
                    bcSubFundTotal.setBcas(simpleBcasEntry);
                    bcSubFundTotal.setSubFundTotalRevenueBaseAmount(subFundTotalRevenueBaseAmount);
                    bcSubFundTotal.setSubFundTotalRevenueReqAmount(subFundTotalRevenueReqAmount);
                    subFundTotalRevenueBaseAmount = BigDecimal.ZERO;
                    subFundTotalRevenueReqAmount = BigDecimal.ZERO;
                
                }
            }

            

            
            bcSubFundTotal.setTotalGrossBaseAmount(totalGrossBaseAmount);
            bcSubFundTotal.setTotalGrossReqAmount(totalGrossReqAmount);
            bcSubFundTotal.setTotalNetTransferBaseAmount(totalNetTransferBaseAmount);
            bcSubFundTotal.setTotalNetTransferReqAmount(totalNetTransferReqAmount);
            bcSubFundTotal.setTotalRevenueBaseAmount(totalRevenueBaseAmount);
            bcSubFundTotal.setTotalRevenueReqAmount(totalRevenueReqAmount);
            bcSubFundTotal.setTotalTransferInBaseAmount(totalTransferInBaseAmount);
            bcSubFundTotal.setTotalTransferInReqAmount(totalTransferInReqAmount);
            returnList.add(bcSubFundTotal);
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
     * Checks wheather or not the entry of subFund is same with OrgChartOfAccountCode, organizationCode, fundGroupCode, and
     * subFundGroupCode
     * 
     * @param BudgetConstructionAccountSummary firstBcas
     * @param BudgetConstructionAccountSummary secondBcas
     * @return true if the given entries are same; otherwise, return false
     */
    public boolean isSameSubFundSummaryEntry(BudgetConstructionAccountSummary firstBcas, BudgetConstructionAccountSummary secondBcas) {
        if (firstBcas.getOrganizationChartOfAccountsCode().equals(secondBcas.getOrganizationChartOfAccountsCode()) && firstBcas.getOrganizationCode().equals(secondBcas.getOrganizationCode()) && firstBcas.getFundGroupCode().equals(secondBcas.getFundGroupCode()) && firstBcas.getSubFundGroupCode().equals(secondBcas.getSubFundGroupCode())) {
            return true;
        }
        else
            return false;
    }

    /**
     * Checks wheather or not the entry of subFund is same with OrgChartOfAccountCode, organizationCode, and fundGroupCode
     * 
     * @param BudgetConstructionAccountSummary firstBcas
     * @param BudgetConstructionAccountSummary secondBcas
     * @return true if the given entries are same; otherwise, return false
     */
    public boolean isSameSubFundSummaryEntryWithoutSubFundCode(BudgetConstructionAccountSummary firstBcas, BudgetConstructionAccountSummary secondBcas) {
        if (firstBcas.getOrganizationChartOfAccountsCode().equals(secondBcas.getOrganizationChartOfAccountsCode()) && firstBcas.getOrganizationCode().equals(secondBcas.getOrganizationCode()) && firstBcas.getFundGroupCode().equals(secondBcas.getFundGroupCode())) {
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

                if (!isSameSubFundSummaryEntry(accountSummaryEntry, accountSummaryEntryAux)) {
                    returnList.add(accountSummaryEntry);
                    accountSummaryEntryAux = accountSummaryEntry;
                }
                count++;
            }
        }

        return returnList;
    }

    public void setBudgetConstructionAccountSummaryReportDao(BudgetConstructionAccountSummaryReportDao budgetConstructionAccountSummaryReportDao) {
        this.budgetConstructionAccountSummaryReportDao = budgetConstructionAccountSummaryReportDao;
    }


}
