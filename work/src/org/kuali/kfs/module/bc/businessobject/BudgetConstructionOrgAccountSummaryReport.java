/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.bo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.web.comparator.StringValueComparator;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCConstants;

/**
 * Budget Construction Organization Account Summary Report Business Object.
 */

public class BudgetConstructionOrgAccountSummaryReport {

    // Header parts
    private String fiscalYear;
    private String orgChartOfAccountsCode;
    private String orgChartOfAccountDescription;
    private String chartOfAccountsCode;
    private String chartOfAccountDescription;
    private String organizationCode;
    private String organizationName;
    private String consHdr;
    private String fundGroupCode;
    private String fundGroupName;
    private String subFundGroupCode;
    private String subFundGroupDescription;
    private String baseFy;
    private String reqFy;
    private String header1;
    private String header2;
    private String header3;
    private String header4;
    private String header5;
    private String header6;

    // Body parts
    private String accountNumber;
    private String subAccountNumber;
    private String accountNameAndSubAccountName;
    private String incExpDesc;
    private BigDecimal baseAmount;
    private BigDecimal reqAmount;
    private BigDecimal amountChange;
    private BigDecimal percentChange = BigDecimal.ZERO;

    // Total parts
    private BigDecimal totalRevenueBaseAmount = BigDecimal.ZERO;
    private BigDecimal totalGrossBaseAmount = BigDecimal.ZERO;
    private BigDecimal totalTransferInBaseAmount = BigDecimal.ZERO;
    private BigDecimal totalNetTransferBaseAmount = BigDecimal.ZERO;

    private BigDecimal totalRevenueReqAmount = BigDecimal.ZERO;
    private BigDecimal totalGrossReqAmount = BigDecimal.ZERO;
    private BigDecimal totalTransferInReqAmount = BigDecimal.ZERO;
    private BigDecimal totalNetTransferReqAmount = BigDecimal.ZERO;

    private BigDecimal totalRevenueAmountChange = BigDecimal.ZERO;
    private BigDecimal totalGrossAmountChange = BigDecimal.ZERO;
    private BigDecimal totalTransferAmountChange = BigDecimal.ZERO;
    private BigDecimal totalNetTransferAmountChange = BigDecimal.ZERO;

    private BigDecimal totalRevenuePercentChange = BigDecimal.ZERO;
    private BigDecimal totalGrossPercentChange = BigDecimal.ZERO;
    private BigDecimal totalTransferInPercentChange = BigDecimal.ZERO;
    private BigDecimal totalNetTransferPercentChange = BigDecimal.ZERO;

    private BigDecimal revExpDifferenceBaseAmount = BigDecimal.ZERO;
    private BigDecimal revExpDifferenceReqAmount = BigDecimal.ZERO;
    private BigDecimal revExpDifferenceAmountChange = BigDecimal.ZERO;
    private BigDecimal revExpDifferencePercentChange = BigDecimal.ZERO;

    BudgetConstructionOrgAccountSummaryReport bcoasr;

    private Integer tempFiscalYear = new Integer(2008);

    /**
     * builds a report
     * 
     * @param Collection<BudgetConstructionAccountSummary> list
     */
    public Collection<BudgetConstructionOrgAccountSummaryReport> buildReports(Collection<BudgetConstructionAccountSummary> list) {
        Collection<BudgetConstructionOrgAccountSummaryReport> reportSet = new ArrayList();
        List<BudgetConstructionOrgAccountSummaryReportTotal> orgAccountSummaryReportTotalList;
        // NumericValueComparator numericComparator = new NumericValueComparator();
        StringValueComparator stringComparator = new StringValueComparator();
        Collections.sort((List) list, new BeanComparator(KFSPropertyConstants.INCOME_EXPENSE_CODE, stringComparator));
        Collections.sort((List) list, new BeanComparator(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, stringComparator));
        Collections.sort((List) list, new BeanComparator(KFSPropertyConstants.ACCOUNT_NUMBER, stringComparator));
        Collections.sort((List) list, new BeanComparator(KFSPropertyConstants.SUB_FUND_GROUP_CODE, stringComparator));
        Collections.sort((List) list, new BeanComparator(KFSPropertyConstants.FUND_GROUP_CODE, stringComparator));
        Collections.sort((List) list, new BeanComparator(KFSPropertyConstants.SUB_FUND_SORT_CODE, stringComparator));
        Collections.sort((List) list, new BeanComparator(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, stringComparator));
        Collections.sort((List) list, new BeanComparator(KFSPropertyConstants.ORGANIZATION_CODE, stringComparator));
        Collections.sort((List) list, new BeanComparator(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE, stringComparator));

        // Making a list with same organizationChartOfAccountsCode, organizationCode, chartOfAccountsCode, subFundGroupCode
        List simpleList = deleteDuplicated((List) list);
        // Calculate Total Section
        orgAccountSummaryReportTotalList = calculateTotal((List) list, simpleList);
        for (BudgetConstructionAccountSummary bcasEntry : list) {
            bcoasr = new BudgetConstructionOrgAccountSummaryReport();
            buildReportsHeader(bcasEntry);
            buildReportsBody(bcasEntry);
            buildReportsTotal(bcasEntry, orgAccountSummaryReportTotalList);
            reportSet.add(bcoasr);
        }

        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionAccountSummary bcas
     */
    public void buildReportsHeader(BudgetConstructionAccountSummary bcas) {
        String orgChartDesc = bcas.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = bcas.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = bcas.getOrganization().getOrganizationName();
        String reportChartDesc = bcas.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = bcas.getFundGroup().getName();
        String subFundGroupDes = bcas.getSubFundGroup().getSubFundGroupDescription();
        Integer prevFiscalyear = tempFiscalYear - 1;
        bcoasr.setFiscalYear(prevFiscalyear.toString() + " - " + tempFiscalYear.toString().substring(2, 4));
        bcoasr.setOrgChartOfAccountsCode(bcas.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            bcoasr.setOrgChartOfAccountDescription(BCConstants.Report.ERROR_GETTING_CHART_DESCRIPTION);
        }
        else {
            bcoasr.setOrgChartOfAccountDescription(orgChartDesc);
        }

        bcoasr.setOrganizationCode(bcas.getOrganizationCode());
        if (orgName == null) {
            bcoasr.setOrganizationName(BCConstants.Report.ERROR_GETTING_ORGANIZATION_NAME);
        }
        else {
            bcoasr.setOrganizationName(orgName);
        }

        bcoasr.setChartOfAccountsCode(bcas.getChartOfAccountsCode());
        if (chartDesc == null) {
            bcoasr.setChartOfAccountDescription(BCConstants.Report.ERROR_GETTING_CHART_DESCRIPTION);
        }
        else {
            bcoasr.setChartOfAccountDescription(chartDesc);
        }

        bcoasr.setFundGroupCode(bcas.getFundGroupCode());
        if (subFundGroupName == null) {
            bcoasr.setFundGroupName(BCConstants.Report.ERROR_GETTING_FUNDGROUP_NAME);
        }
        else {
            bcoasr.setFundGroupName(subFundGroupName);
        }

        bcoasr.setSubFundGroupCode(bcas.getSubFundGroupCode());
        if (subFundGroupName == null) {
            bcoasr.setSubFundGroupDescription(BCConstants.Report.ERROR_GETTING_SUBFUNDGROUP_DESCRIPTION);
        }
        else {
            bcoasr.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        bcoasr.setBaseFy(prevPrevFiscalyear.toString() + " - " + prevFiscalyear.toString().substring(2, 4));
        bcoasr.setReqFy(prevFiscalyear.toString() + " - " + tempFiscalYear.toString().substring(2, 4));
        bcoasr.setHeader1(BCConstants.Report.HEADER_ACCOUNT_SUB);
        bcoasr.setHeader2(BCConstants.Report.HEADER_ACCOUNT_SUB_NAME);
        bcoasr.setHeader3(BCConstants.Report.HEADER_BASE_AMOUNT);
        bcoasr.setHeader4(BCConstants.Report.HEADER_REQ_AMOUNT);
        bcoasr.setHeader5(BCConstants.Report.HEADER_CHANGE);
        bcoasr.setHeader6(BCConstants.Report.HEADER_CHANGE);
        bcoasr.setConsHdr("");
    }

    /**
     * builds report body
     * 
     * @param BudgetConstructionAccountSummary bcas
     */
    public void buildReportsBody(BudgetConstructionAccountSummary bcas) {
        bcoasr.setAccountNumber(bcas.getAccountNumber());
        bcoasr.setSubAccountNumber(bcas.getSubAccountNumber());
        boolean trExist = false;
        if (bcas.getSubAccountNumber().equals("-----")) {
            if (bcas.getAccount().getAccountName() == null) {
                bcoasr.setAccountNameAndSubAccountName(BCConstants.Report.ERROR_GETTING_ACCOUNT_DESCRIPTION);
            }
            else
                bcoasr.setAccountNameAndSubAccountName(bcas.getAccount().getAccountName());
        }
        else {
            // TODO check when the subAccountNumber is not "-----"
            try {
                if (bcas.getSubAccount().getSubAccountName() == null) {
                    bcoasr.setAccountNameAndSubAccountName(BCConstants.Report.ERROR_GETTING_SUB_ACCOUNT_DESCRIPTION);
                }
                else
                    bcoasr.setAccountNameAndSubAccountName(bcas.getSubAccount().getSubAccountName());
            }
            catch (PersistenceBrokerException e) {
                bcoasr.setAccountNameAndSubAccountName(BCConstants.Report.ERROR_GETTING_SUB_ACCOUNT_DESCRIPTION);
            }
        }

        // build income expense description
        if (bcas.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
            bcoasr.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_REVENUE);
        }
        else if (bcas.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_E)) {
            bcoasr.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_EXP_GROSS);
        }
        else if (bcas.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_T)) {
            trExist = true;
            bcoasr.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_TRNFR_IN);
        }
        else {
            if (trExist) {
                trExist = false;
                bcoasr.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_EXP_NET_TRNFR);
            }
            else {
                bcoasr.setIncExpDesc(BCConstants.Report.INCOME_EXP_DESC_EXPENDITURE);
            }
        }

        BigDecimal beginingBalanceLineAmt = BigDecimal.ZERO;
        BigDecimal accountLineAnnualBalAmt = BigDecimal.ZERO;
        if (bcas.getFinancialBeginningBalanceLineAmount() != null) {
            beginingBalanceLineAmt = bcas.getFinancialBeginningBalanceLineAmount().bigDecimalValue();
        }

        if (bcas.getFinancialBeginningBalanceLineAmount() != null) {
            accountLineAnnualBalAmt = bcas.getAccountLineAnnualBalanceAmount().bigDecimalValue();
        }

        bcoasr.setBaseAmount(beginingBalanceLineAmt);
        bcoasr.setReqAmount(accountLineAnnualBalAmt);
        bcoasr.setAmountChange(accountLineAnnualBalAmt.subtract(beginingBalanceLineAmt));

        if (!beginingBalanceLineAmt.equals(BigDecimal.ZERO)) {
            bcoasr.setPercentChange(bcoasr.getAmountChange().divide(beginingBalanceLineAmt, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        }
    }

    /**
     * builds report total
     * 
     * @param BudgetConstructionAccountSummary bcas
     * @param List reportTotalList
     */
    public void buildReportsTotal(BudgetConstructionAccountSummary bcas, List reportTotalList) {
        Iterator totalListIter = reportTotalList.iterator();
        while (totalListIter.hasNext()) {
            BudgetConstructionOrgAccountSummaryReportTotal bcasTotalEntry = (BudgetConstructionOrgAccountSummaryReportTotal) totalListIter.next();
            if (isSameAccountSummaryEntry(bcas, bcasTotalEntry.getBcas())) {
                bcoasr.setTotalRevenueBaseAmount(bcasTotalEntry.getTotalRevenueBaseAmount());
                bcoasr.setTotalGrossBaseAmount(bcasTotalEntry.getTotalGrossBaseAmount());
                bcoasr.setTotalTransferInBaseAmount(bcasTotalEntry.getTotalTransferInBaseAmount());
                bcoasr.setTotalNetTransferBaseAmount(bcasTotalEntry.getTotalNetTransferBaseAmount());

                bcoasr.setTotalRevenueReqAmount(bcasTotalEntry.getTotalRevenueReqAmount());
                bcoasr.setTotalGrossReqAmount(bcasTotalEntry.getTotalGrossReqAmount());
                bcoasr.setTotalTransferInReqAmount(bcasTotalEntry.getTotalTransferInReqAmount());
                bcoasr.setTotalNetTransferReqAmount(bcasTotalEntry.getTotalNetTransferReqAmount());

                bcoasr.setTotalRevenueAmountChange(bcoasr.getTotalRevenueReqAmount().subtract(bcoasr.getTotalRevenueBaseAmount()));
                if (!bcoasr.getTotalRevenueBaseAmount().equals(BigDecimal.ZERO)) {
                    bcoasr.setTotalRevenuePercentChange(bcoasr.getTotalRevenueAmountChange().divide(bcoasr.getTotalRevenueBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }

                bcoasr.setTotalGrossAmountChange(bcoasr.getTotalGrossReqAmount().subtract(bcoasr.getTotalGrossBaseAmount()));

                if (!bcoasr.getTotalGrossBaseAmount().equals(BigDecimal.ZERO)) {
                    bcoasr.setTotalGrossPercentChange(bcoasr.getTotalGrossAmountChange().divide(bcoasr.getTotalGrossBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }

                bcoasr.setTotalTransferAmountChange(bcoasr.getTotalTransferInReqAmount().subtract(bcoasr.getTotalTransferInBaseAmount()));

                if (!bcoasr.getTotalTransferInBaseAmount().equals(BigDecimal.ZERO)) {
                    bcoasr.setTotalTransferInPercentChange(bcoasr.getTotalTransferAmountChange().divide(bcoasr.getTotalTransferInBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }

                bcoasr.setTotalNetTransferAmountChange(bcoasr.getTotalNetTransferReqAmount().subtract(bcoasr.getTotalNetTransferBaseAmount()));
                if (!bcoasr.getTotalNetTransferBaseAmount().equals(BigDecimal.ZERO)) {
                    bcoasr.setTotalNetTransferPercentChange(bcoasr.getTotalNetTransferAmountChange().divide(bcoasr.getTotalNetTransferBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }

                bcoasr.setRevExpDifferenceBaseAmount(bcoasr.getTotalRevenueBaseAmount().subtract(bcoasr.getTotalNetTransferBaseAmount()));
                bcoasr.setRevExpDifferenceReqAmount(bcoasr.getTotalRevenueReqAmount().subtract(bcoasr.getTotalNetTransferReqAmount()));
                bcoasr.setRevExpDifferenceAmountChange(bcoasr.getRevExpDifferenceReqAmount().subtract(bcoasr.getRevExpDifferenceBaseAmount()));

                if (!bcoasr.getRevExpDifferenceBaseAmount().equals(BigDecimal.ZERO)) {
                    bcoasr.setRevExpDifferencePercentChange(bcoasr.getRevExpDifferenceAmountChange().divide(bcoasr.getRevExpDifferenceBaseAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
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
        BudgetConstructionAccountSummary bcas = null;
        BudgetConstructionAccountSummary bcasAux = null;
        List returnList = new ArrayList();
        if ((list != null) && (list.size() > 0)) {
            bcas = (BudgetConstructionAccountSummary) list.get(count);
            bcasAux = (BudgetConstructionAccountSummary) list.get(count);
            returnList.add(bcas);
            count++;
            while (count < list.size()) {
                bcas = (BudgetConstructionAccountSummary) list.get(count);

                if (!(bcas.getOrganizationChartOfAccountsCode().equals(bcasAux.getOrganizationChartOfAccountsCode()) && bcas.getOrganizationCode().equals(bcasAux.getOrganizationCode()) && bcas.getChartOfAccountsCode().equals(bcasAux.getChartOfAccountsCode()) && bcas.getSubFundGroupCode().equals(bcasAux.getSubFundGroupCode()))) {
                    returnList.add(bcas);
                    bcasAux = bcas;
                }
                count++;
            }
        }

        return returnList;
    }

    /**
     * Gets the accountNameAndSubAccountName
     * 
     * @return Returns the accountNameAndSubAccountName.
     */
    public String getAccountNameAndSubAccountName() {
        return accountNameAndSubAccountName;
    }

    /**
     * Sets the accountNameAndSubAccountName
     * 
     * @param accountNameAndSubAccountName The accountNameAndSubAccountName to set.
     */
    public void setAccountNameAndSubAccountName(String accountNameAndSubAccountName) {
        this.accountNameAndSubAccountName = accountNameAndSubAccountName;
    }

    /**
     * Gets the accountNumber
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the amountChange
     * 
     * @return Returns the amountChange.
     */
    public BigDecimal getAmountChange() {
        return amountChange;
    }

    /**
     * Sets the amountChange
     * 
     * @param amountChange The amountChange to set.
     */
    public void setAmountChange(BigDecimal amountChange) {
        this.amountChange = amountChange;
    }

    /**
     * Gets the baseAmount
     * 
     * @return Returns the baseAmount.
     */
    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    /**
     * Sets the baseAmount
     * 
     * @param baseAmount The baseAmount to set.
     */
    public void setBaseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
    }

    /**
     * Gets the baseFy
     * 
     * @return Returns the baseFy.
     */
    public String getBaseFy() {
        return baseFy;
    }

    /**
     * Sets the baseFy
     * 
     * @param baseFy The baseFy to set.
     */
    public void setBaseFy(String baseFy) {
        this.baseFy = baseFy;
    }

    /**
     * Gets the consHdr
     * 
     * @return Returns the consHdr.
     */
    public String getConsHdr() {
        return consHdr;
    }

    /**
     * Sets the consHdr
     * 
     * @param consHdr The consHdr to set.
     */
    public void setConsHdr(String consHdr) {
        this.consHdr = consHdr;
    }

    /**
     * Gets the fiscalYear
     * 
     * @return Returns the fiscalYear.
     */
    public String getFiscalYear() {
        return fiscalYear;
    }

    /**
     * Sets the fiscalYear
     * 
     * @param fiscalYear The fiscalYear to set.
     */
    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    /**
     * Gets the fundGroupCode
     * 
     * @return Returns the fundGroupCode.
     */
    public String getFundGroupCode() {
        return fundGroupCode;
    }

    /**
     * Sets the fundGroupCode
     * 
     * @param fundGroupCode The fundGroupCode to set.
     */
    public void setFundGroupCode(String fundGroupCode) {
        this.fundGroupCode = fundGroupCode;
    }

    /**
     * Gets the header1
     * 
     * @return Returns the header1.
     */
    public String getHeader1() {
        return header1;
    }

    /**
     * Sets the header1
     * 
     * @param header1 The header1 to set.
     */
    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    /**
     * Gets the header2
     * 
     * @return Returns the header2.
     */
    public String getHeader2() {
        return header2;
    }

    /**
     * Sets the header2
     * 
     * @param header2 The header2 to set.
     */
    public void setHeader2(String header2) {
        this.header2 = header2;
    }

    /**
     * Gets the header3
     * 
     * @return Returns the header3.
     */
    public String getHeader3() {
        return header3;
    }

    /**
     * Sets the header3
     * 
     * @param header3 The header3 to set.
     */
    public void setHeader3(String header3) {
        this.header3 = header3;
    }

    /**
     * Gets the header4
     * 
     * @return Returns the header4.
     */
    public String getHeader4() {
        return header4;
    }

    /**
     * Sets the header4
     * 
     * @param header4 The header4 to set.
     */
    public void setHeader4(String header4) {
        this.header4 = header4;
    }

    /**
     * Gets the header5
     * 
     * @return Returns the header5.
     */
    public String getHeader5() {
        return header5;
    }

    /**
     * Sets the header5
     * 
     * @param header5 The header5 to set.
     */
    public void setHeader5(String header5) {
        this.header5 = header5;
    }

    /**
     * Gets the header6
     * 
     * @return Returns the header6.
     */
    public String getHeader6() {
        return header6;
    }

    /**
     * Sets the header6
     * 
     * @param header6 The header6 to set.
     */
    public void setHeader6(String header6) {
        this.header6 = header6;
    }

    /**
     * Gets the incExpDesc
     * 
     * @return Returns the incExpDesc.
     */
    public String getIncExpDesc() {
        return incExpDesc;
    }

    /**
     * Sets the incExpDesc
     * 
     * @param incExpDesc The incExpDesc to set.
     */
    public void setIncExpDesc(String incExpDesc) {
        this.incExpDesc = incExpDesc;
    }

    /**
     * Gets the organizationCode
     * 
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the organizationName
     * 
     * @return Returns the organizationName.
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * Sets the organizationName
     * 
     * @param organizationName The organizationName to set.
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * Gets the percentChange
     * 
     * @return Returns the percentChange.
     */
    public BigDecimal getPercentChange() {
        return percentChange;
    }

    /**
     * Sets the percentChange
     * 
     * @param percentChange The percentChange to set.
     */
    public void setPercentChange(BigDecimal percentChange) {
        this.percentChange = percentChange;
    }

    /**
     * Gets the reqAmount
     * 
     * @return Returns the reqAmount.
     */
    public BigDecimal getReqAmount() {
        return reqAmount;
    }

    /**
     * Sets the reqAmount
     * 
     * @param reqAmount The reqAmount to set.
     */
    public void setReqAmount(BigDecimal reqAmount) {
        this.reqAmount = reqAmount;
    }

    /**
     * Gets the reqFy
     * 
     * @return Returns the reqFy.
     */
    public String getReqFy() {
        return reqFy;
    }

    /**
     * Sets the reqFy
     * 
     * @param reqFy The reqFy to set.
     */
    public void setReqFy(String reqFy) {
        this.reqFy = reqFy;
    }

    /**
     * Gets the revExpDifferenceAmountChange
     * 
     * @return Returns the revExpDifferenceAmountChange.
     */
    public BigDecimal getRevExpDifferenceAmountChange() {
        return revExpDifferenceAmountChange;
    }

    /**
     * Sets the revExpDifferenceAmountChange
     * 
     * @param revExpDifferenceAmountChange The revExpDifferenceAmountChange to set.
     */
    public void setRevExpDifferenceAmountChange(BigDecimal revExpDifferenceAmountChange) {
        this.revExpDifferenceAmountChange = revExpDifferenceAmountChange;
    }

    /**
     * Gets the revExpDifferenceBaseAmount
     * 
     * @return Returns the revExpDifferenceBaseAmount.
     */
    public BigDecimal getRevExpDifferenceBaseAmount() {
        return revExpDifferenceBaseAmount;
    }

    /**
     * Sets the revExpDifferenceBaseAmount
     * 
     * @param revExpDifferenceBaseAmount The revExpDifferenceBaseAmount to set.
     */
    public void setRevExpDifferenceBaseAmount(BigDecimal revExpDifferenceBaseAmount) {
        this.revExpDifferenceBaseAmount = revExpDifferenceBaseAmount;
    }

    /**
     * Gets the revExpDifferencePercentChange
     * 
     * @return Returns the revExpDifferencePercentChange.
     */
    public BigDecimal getRevExpDifferencePercentChange() {
        return revExpDifferencePercentChange;
    }

    /**
     * Sets the revExpDifferencePercentChange
     * 
     * @param revExpDifferencePercentChange The revExpDifferencePercentChange to set.
     */
    public void setRevExpDifferencePercentChange(BigDecimal revExpDifferencePercentChange) {
        this.revExpDifferencePercentChange = revExpDifferencePercentChange;
    }

    /**
     * Gets the revExpDifferenceReqAmount
     * 
     * @return Returns the revExpDifferenceReqAmount.
     */
    public BigDecimal getRevExpDifferenceReqAmount() {
        return revExpDifferenceReqAmount;
    }

    /**
     * Sets the revExpDifferenceReqAmount
     * 
     * @param revExpDifferenceReqAmount The revExpDifferenceReqAmount to set.
     */
    public void setRevExpDifferenceReqAmount(BigDecimal revExpDifferenceReqAmount) {
        this.revExpDifferenceReqAmount = revExpDifferenceReqAmount;
    }

    /**
     * Gets the subAccountNumber
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the subFundGroupCode
     * 
     * @return Returns the subFundGroupCode.
     */
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    /**
     * Sets the subFundGroupCode
     * 
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }

    /**
     * Gets the subFundGroupDescription
     * 
     * @return Returns the subFundGroupDescription.
     */
    public String getSubFundGroupDescription() {
        return subFundGroupDescription;
    }

    /**
     * Sets the subFundGroupDescription
     * 
     * @param subFundGroupDescription The subFundGroupDescription to set.
     */
    public void setSubFundGroupDescription(String subFundGroupDescription) {
        this.subFundGroupDescription = subFundGroupDescription;
    }

    /**
     * Gets the totalGrossAmountChange
     * 
     * @return Returns the totalGrossAmountChange.
     */
    public BigDecimal getTotalGrossAmountChange() {
        return totalGrossAmountChange;
    }

    /**
     * Sets the totalGrossAmountChange
     * 
     * @param totalGrossAmountChange The totalGrossAmountChange to set.
     */
    public void setTotalGrossAmountChange(BigDecimal totalGrossAmountChange) {
        this.totalGrossAmountChange = totalGrossAmountChange;
    }

    /**
     * Gets the totalGrossBaseAmount
     * 
     * @return Returns the totalGrossBaseAmount.
     */
    public BigDecimal getTotalGrossBaseAmount() {
        return totalGrossBaseAmount;
    }

    /**
     * Sets the totalGrossBaseAmount
     * 
     * @param totalGrossBaseAmount The totalGrossBaseAmount to set.
     */
    public void setTotalGrossBaseAmount(BigDecimal totalGrossBaseAmount) {
        this.totalGrossBaseAmount = totalGrossBaseAmount;
    }

    /**
     * Gets the totalGrossPercentChange
     * 
     * @return Returns the totalGrossPercentChange.
     */
    public BigDecimal getTotalGrossPercentChange() {
        return totalGrossPercentChange;
    }

    /**
     * Sets the totalGrossPercentChange
     * 
     * @param totalGrossPercentChange The totalGrossPercentChange to set.
     */
    public void setTotalGrossPercentChange(BigDecimal totalGrossPercentChange) {
        this.totalGrossPercentChange = totalGrossPercentChange;
    }

    /**
     * Gets the totalGrossReqAmount
     * 
     * @return Returns the totalGrossReqAmount.
     */
    public BigDecimal getTotalGrossReqAmount() {
        return totalGrossReqAmount;
    }

    /**
     * Sets the totalGrossReqAmount
     * 
     * @param totalGrossReqAmount The totalGrossReqAmount to set.
     */
    public void setTotalGrossReqAmount(BigDecimal totalGrossReqAmount) {
        this.totalGrossReqAmount = totalGrossReqAmount;
    }

    /**
     * Gets the totalNetTransferAmountChange
     * 
     * @return Returns the totalNetTransferAmountChange.
     */
    public BigDecimal getTotalNetTransferAmountChange() {
        return totalNetTransferAmountChange;
    }

    /**
     * Sets the totalNetTransferAmountChange
     * 
     * @param totalNetTransferAmountChange The totalNetTransferAmountChange to set.
     */
    public void setTotalNetTransferAmountChange(BigDecimal totalNetTransferAmountChange) {
        this.totalNetTransferAmountChange = totalNetTransferAmountChange;
    }

    /**
     * Gets the totalNetTransferBaseAmount
     * 
     * @return Returns the totalNetTransferBaseAmount.
     */
    public BigDecimal getTotalNetTransferBaseAmount() {
        return totalNetTransferBaseAmount;
    }

    /**
     * Sets the totalNetTransferBaseAmount
     * 
     * @param totalNetTransferBaseAmount The totalNetTransferBaseAmount to set.
     */
    public void setTotalNetTransferBaseAmount(BigDecimal totalNetTransferBaseAmount) {
        this.totalNetTransferBaseAmount = totalNetTransferBaseAmount;
    }

    /**
     * Gets the totalNetTransferPercentChange
     * 
     * @return Returns the totalNetTransferPercentChange.
     */
    public BigDecimal getTotalNetTransferPercentChange() {
        return totalNetTransferPercentChange;
    }

    /**
     * Sets the totalNetTransferPercentChange
     * 
     * @param totalNetTransferPercentChange The totalNetTransferPercentChange to set.
     */
    public void setTotalNetTransferPercentChange(BigDecimal totalNetTransferPercentChange) {
        this.totalNetTransferPercentChange = totalNetTransferPercentChange;
    }

    /**
     * Gets the totalNetTransferReqAmount
     * 
     * @return Returns the totalNetTransferReqAmount.
     */
    public BigDecimal getTotalNetTransferReqAmount() {
        return totalNetTransferReqAmount;
    }

    /**
     * Sets the totalNetTransferReqAmount
     * 
     * @param totalNetTransferReqAmount The totalNetTransferReqAmount to set.
     */
    public void setTotalNetTransferReqAmount(BigDecimal totalNetTransferReqAmount) {
        this.totalNetTransferReqAmount = totalNetTransferReqAmount;
    }

    /**
     * Gets the totalRevenueAmountChange
     * 
     * @return Returns the totalRevenueAmountChange.
     */
    public BigDecimal getTotalRevenueAmountChange() {
        return totalRevenueAmountChange;
    }

    /**
     * Sets the totalRevenueAmountChange
     * 
     * @param totalRevenueAmountChange The totalRevenueAmountChange to set.
     */
    public void setTotalRevenueAmountChange(BigDecimal totalRevenueAmountChange) {
        this.totalRevenueAmountChange = totalRevenueAmountChange;
    }

    /**
     * Gets the totalRevenueBaseAmount
     * 
     * @return Returns the totalRevenueBaseAmount.
     */
    public BigDecimal getTotalRevenueBaseAmount() {
        return totalRevenueBaseAmount;
    }

    /**
     * Sets the totalRevenueBaseAmount
     * 
     * @param totalRevenueBaseAmount The totalRevenueBaseAmount to set.
     */
    public void setTotalRevenueBaseAmount(BigDecimal totalRevenueBaseAmount) {
        this.totalRevenueBaseAmount = totalRevenueBaseAmount;
    }

    /**
     * Gets the totalRevenuePercentChange
     * 
     * @return Returns the totalRevenuePercentChange.
     */
    public BigDecimal getTotalRevenuePercentChange() {
        return totalRevenuePercentChange;
    }

    /**
     * Sets the totalRevenuePercentChange
     * 
     * @param totalRevenuePercentChange The totalRevenuePercentChange to set.
     */
    public void setTotalRevenuePercentChange(BigDecimal totalRevenuePercentChange) {
        this.totalRevenuePercentChange = totalRevenuePercentChange;
    }

    /**
     * Gets the payrollEndDateFiscalPeriod
     * 
     * @return Returns the payrollEndDateFiscalPeriod.
     */
    public BigDecimal getTotalRevenueReqAmount() {
        return totalRevenueReqAmount;
    }

    /**
     * Sets the payrollEndDateFiscalPeriod
     * 
     * @param payrollEndDateFiscalPeriod The payrollEndDateFiscalPeriod to set.
     */
    public void setTotalRevenueReqAmount(BigDecimal totalRevenueReqAmount) {
        this.totalRevenueReqAmount = totalRevenueReqAmount;
    }

    /**
     * Gets the totalTransferAmountChange
     * 
     * @return Returns the totalTransferAmountChange.
     */
    public BigDecimal getTotalTransferAmountChange() {
        return totalTransferAmountChange;
    }

    /**
     * Sets the totalTransferAmountChange
     * 
     * @param totalTransferAmountChange The totalTransferAmountChange to set.
     */
    public void setTotalTransferAmountChange(BigDecimal totalTransferAmountChange) {
        this.totalTransferAmountChange = totalTransferAmountChange;
    }

    /**
     * Gets the totalTransferInBaseAmount
     * 
     * @return Returns the totalTransferInBaseAmount.
     */
    public BigDecimal getTotalTransferInBaseAmount() {
        return totalTransferInBaseAmount;
    }

    /**
     * Sets the totalTransferInBaseAmount
     * 
     * @param totalTransferInBaseAmount The totalTransferInBaseAmount to set.
     */
    public void setTotalTransferInBaseAmount(BigDecimal totalTransferInBaseAmount) {
        this.totalTransferInBaseAmount = totalTransferInBaseAmount;
    }

    /**
     * Gets the totalTransferInPercentChange
     * 
     * @return Returns the totalTransferInPercentChange.
     */
    public BigDecimal getTotalTransferInPercentChange() {
        return totalTransferInPercentChange;
    }

    /**
     * Sets the totalTransferInPercentChange
     * 
     * @param totalTransferInPercentChange The totalTransferInPercentChange to set.
     */
    public void setTotalTransferInPercentChange(BigDecimal totalTransferInPercentChange) {
        this.totalTransferInPercentChange = totalTransferInPercentChange;
    }

    /**
     * Gets the totalTransferInReqAmount
     * 
     * @return Returns the totalTransferInReqAmount.
     */
    public BigDecimal getTotalTransferInReqAmount() {
        return totalTransferInReqAmount;
    }

    /**
     * Sets the totalTransferInReqAmount
     * 
     * @param totalTransferInReqAmount The totalTransferInReqAmount to set.
     */
    public void setTotalTransferInReqAmount(BigDecimal totalTransferInReqAmount) {
        this.totalTransferInReqAmount = totalTransferInReqAmount;
    }

    /**
     * Gets the fundGroupName
     * 
     * @return Returns the fundGroupName.
     */
    public String getFundGroupName() {
        return fundGroupName;
    }

    /**
     * Sets the fundGroupName
     * 
     * @param fundGroupName The fundGroupName to set.
     */
    public void setFundGroupName(String fundGroupName) {
        this.fundGroupName = fundGroupName;
    }

    /**
     * Gets the chartOfAccountDescription
     * 
     * @return Returns the chartOfAccountDescription.
     */
    public String getChartOfAccountDescription() {
        return chartOfAccountDescription;
    }

    /**
     * Sets the chartOfAccountDescription
     * 
     * @param chartOfAccountDescription The chartOfAccountDescription to set.
     */
    public void setChartOfAccountDescription(String chartOfAccountDescription) {
        this.chartOfAccountDescription = chartOfAccountDescription;
    }

    /**
     * Gets the chartOfAccountsCode
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the orgChartOfAccountDescription
     * 
     * @return Returns the orgChartOfAccountDescription.
     */
    public String getOrgChartOfAccountDescription() {
        return orgChartOfAccountDescription;
    }

    /**
     * Sets the orgChartOfAccountDescription
     * 
     * @param orgChartOfAccountDescription The orgChartOfAccountDescription to set.
     */
    public void setOrgChartOfAccountDescription(String orgChartOfAccountDescription) {
        this.orgChartOfAccountDescription = orgChartOfAccountDescription;
    }

    /**
     * Gets the orgChartOfAccountsCode
     * 
     * @return Returns the orgChartOfAccountsCode.
     */
    public String getOrgChartOfAccountsCode() {
        return orgChartOfAccountsCode;
    }

    /**
     * Sets the orgChartOfAccountsCode
     * 
     * @param orgChartOfAccountsCode The orgChartOfAccountsCode to set.
     */
    public void setOrgChartOfAccountsCode(String orgChartOfAccountsCode) {
        this.orgChartOfAccountsCode = orgChartOfAccountsCode;
    }

}
