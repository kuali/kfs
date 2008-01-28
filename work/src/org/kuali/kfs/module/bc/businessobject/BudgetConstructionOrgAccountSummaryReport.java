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
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.web.comparator.StringValueComparator;
import org.kuali.kfs.KFSPropertyConstants;

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
            BudgetConstructionOrgAccountSummaryReportTotal bcoasrTotal = new BudgetConstructionOrgAccountSummaryReportTotal();

            buildReportsHeader(bcasEntry);
            buildReportsBody(bcasEntry);
            buildReportsTotal(bcasEntry, orgAccountSummaryReportTotalList);

            reportSet.add(bcoasr);
        }

        return reportSet;
    }

    public void buildReportsHeader(BudgetConstructionAccountSummary bcas) {

        String orgChartDesc = bcas.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = bcas.getOrganization().getOrganizationName();
        String chartDesc = bcas.getChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = bcas.getFundGroup().getName();
        String subFundGroupDes = bcas.getSubFundGroup().getSubFundGroupDescription();

        Integer prevFiscalyear = tempFiscalYear - 1;
        bcoasr.setFiscalYear(prevFiscalyear.toString() + " - " + tempFiscalYear.toString().substring(2, 4));
        bcoasr.setOrgChartOfAccountsCode(bcas.getOrganizationChartOfAccountsCode());
        if (orgChartDesc == null) {
            bcoasr.setOrgChartOfAccountDescription("Error getting account description");
        }
        else {
            bcoasr.setOrgChartOfAccountDescription(orgChartDesc);
        }

        bcoasr.setOrganizationCode(bcas.getOrganizationCode());
        if (orgName == null) {
            bcoasr.setOrganizationName("Error getting organization name");
        }
        else {
            bcoasr.setOrganizationName(orgName);
        }

        bcoasr.setChartOfAccountsCode(bcas.getChartOfAccountsCode());
        if (chartDesc == null) {
            bcoasr.setChartOfAccountDescription("Error getting chart description");
        }
        else {
            bcoasr.setChartOfAccountDescription(chartDesc);
        }

        bcoasr.setFundGroupCode(bcas.getFundGroupCode());
        if (subFundGroupName == null) {
            bcoasr.setFundGroupName("Error getting fund group name");
        }
        else {
            bcoasr.setFundGroupName(subFundGroupName);
        }

        bcoasr.setSubFundGroupCode(bcas.getSubFundGroupCode());
        if (subFundGroupName == null) {
            bcoasr.setSubFundGroupDescription("Error getting sub-fund group description");
        }
        else {
            bcoasr.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        bcoasr.setBaseFy(prevPrevFiscalyear.toString() + " - " + prevFiscalyear.toString().substring(2, 4));
        bcoasr.setReqFy(prevFiscalyear.toString() + " - " + tempFiscalYear.toString().substring(2, 4));

        bcoasr.setHeader1("Account/Sub");
        bcoasr.setHeader2("Account/Sub name");
        bcoasr.setHeader3("Base Amount");
        bcoasr.setHeader4("Req. Amount");
        bcoasr.setHeader5("Change");
        bcoasr.setHeader6("Change");
        bcoasr.setConsHdr("");
    }

    public void buildReportsBody(BudgetConstructionAccountSummary bcas) {
        bcoasr.setAccountNumber(bcas.getAccountNumber());
        bcoasr.setSubAccountNumber(bcas.getSubAccountNumber());
        boolean trExist = false;
        if (bcas.getSubAccountNumber().equals("-----")) {
            if (bcas.getAccount().getAccountName() == null) {
                bcoasr.setAccountNameAndSubAccountName("Error getting account description");
            }
            else
                bcoasr.setAccountNameAndSubAccountName(bcas.getAccount().getAccountName());
        }
        else {
            // TODO check when the subAccountNumber is not "-----"
            try {
                if (bcas.getSubAccount().getSubAccountName() == null) {
                    bcoasr.setAccountNameAndSubAccountName("Error getting sub-account description");
                }
                else
                    bcoasr.setAccountNameAndSubAccountName(bcas.getSubAccount().getSubAccountName());
            }
            catch (PersistenceBrokerException e) {
                bcoasr.setAccountNameAndSubAccountName("Error getting sub-account description");
            }
        }

        // build income expense description
        if (bcas.getIncomeExpenseCode().equals("A")) {
            bcoasr.setIncExpDesc("Revenue");
        }
        else if (bcas.getIncomeExpenseCode().equals("E")) {
            bcoasr.setIncExpDesc("Exp.(Gross)");
        }
        else if (bcas.getIncomeExpenseCode().equals("T")) {
            trExist = true;
            bcoasr.setIncExpDesc("Trnfr In");
        }
        else {
            if (trExist) {
                trExist = false;
                bcoasr.setIncExpDesc("Exp.(Net Trnfr)");
            }
            else {
                bcoasr.setIncExpDesc("Expenditure");
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
                    if (bcasListEntry.getIncomeExpenseCode().equals("A")) {
                        prev = false;
                        totalRevenueBaseAmount = totalRevenueBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                        totalRevenueReqAmount = totalRevenueReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals("E")) {
                        prev = false;
                        totalGrossBaseAmount = totalGrossBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                        totalGrossReqAmount = totalGrossReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());

                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals("T")) {
                        prev = true;
                        totalTransferInBaseAmount = totalTransferInBaseAmount.add(bcasListEntry.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                        totalTransferInReqAmount = totalTransferInReqAmount.add(bcasListEntry.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                    }
                    else if (bcasListEntry.getIncomeExpenseCode().equals("X")) {
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

    public boolean isSameAccountSummaryEntry(BudgetConstructionAccountSummary firstBcas, BudgetConstructionAccountSummary secondBcas) {

        if (firstBcas.getOrganizationChartOfAccountsCode().equals(secondBcas.getOrganizationChartOfAccountsCode()) && firstBcas.getOrganizationCode().equals(secondBcas.getOrganizationCode()) && firstBcas.getChartOfAccountsCode().equals(secondBcas.getChartOfAccountsCode()) && firstBcas.getSubFundGroupCode().equals(secondBcas.getSubFundGroupCode())) {
            return true;
        }
        else
            return false;
    }

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

    public String getAccountNameAndSubAccountName() {
        return accountNameAndSubAccountName;
    }

    public void setAccountNameAndSubAccountName(String accountNameAndSubAccountName) {
        this.accountNameAndSubAccountName = accountNameAndSubAccountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmountChange() {
        return amountChange;
    }

    public void setAmountChange(BigDecimal amountChange) {
        this.amountChange = amountChange;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
    }

    public String getBaseFy() {
        return baseFy;
    }

    public void setBaseFy(String baseFy) {
        this.baseFy = baseFy;
    }

    public String getChartOfAccountsCode() {
        return orgChartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.orgChartOfAccountsCode = chartOfAccountsCode;
    }

    public String getConsHdr() {
        return consHdr;
    }

    public void setConsHdr(String consHdr) {
        this.consHdr = consHdr;
    }

    public String getFinChartOfAccountDescription() {
        return orgChartOfAccountDescription;
    }

    public void setFinChartOfAccountDescription(String finChartOfAccountDescription) {
        this.orgChartOfAccountDescription = finChartOfAccountDescription;
    }

    public String getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getFundGroupCode() {
        return fundGroupCode;
    }

    public void setFundGroupCode(String fundGroupCode) {
        this.fundGroupCode = fundGroupCode;
    }

    public String getHeader1() {
        return header1;
    }

    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    public String getHeader2() {
        return header2;
    }

    public void setHeader2(String header2) {
        this.header2 = header2;
    }

    public String getHeader3() {
        return header3;
    }

    public void setHeader3(String header3) {
        this.header3 = header3;
    }

    public String getHeader4() {
        return header4;
    }

    public void setHeader4(String header4) {
        this.header4 = header4;
    }

    public String getHeader5() {
        return header5;
    }

    public void setHeader5(String header5) {
        this.header5 = header5;
    }

    public String getHeader6() {
        return header6;
    }

    public void setHeader6(String header6) {
        this.header6 = header6;
    }

    public String getIncExpDesc() {
        return incExpDesc;
    }

    public void setIncExpDesc(String incExpDesc) {
        this.incExpDesc = incExpDesc;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public BigDecimal getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(BigDecimal percentChange) {
        this.percentChange = percentChange;
    }

    public BigDecimal getReqAmount() {
        return reqAmount;
    }

    public void setReqAmount(BigDecimal reqAmount) {
        this.reqAmount = reqAmount;
    }

    public String getReqFy() {
        return reqFy;
    }

    public void setReqFy(String reqFy) {
        this.reqFy = reqFy;
    }

    public BigDecimal getRevExpDifferenceAmountChange() {
        return revExpDifferenceAmountChange;
    }

    public void setRevExpDifferenceAmountChange(BigDecimal revExpDifferenceAmountChange) {
        this.revExpDifferenceAmountChange = revExpDifferenceAmountChange;
    }

    public BigDecimal getRevExpDifferenceBaseAmount() {
        return revExpDifferenceBaseAmount;
    }

    public void setRevExpDifferenceBaseAmount(BigDecimal revExpDifferenceBaseAmount) {
        this.revExpDifferenceBaseAmount = revExpDifferenceBaseAmount;
    }

    public BigDecimal getRevExpDifferencePercentChange() {
        return revExpDifferencePercentChange;
    }

    public void setRevExpDifferencePercentChange(BigDecimal revExpDifferencePercentChange) {
        this.revExpDifferencePercentChange = revExpDifferencePercentChange;
    }

    public BigDecimal getRevExpDifferenceReqAmount() {
        return revExpDifferenceReqAmount;
    }

    public void setRevExpDifferenceReqAmount(BigDecimal revExpDifferenceReqAmount) {
        this.revExpDifferenceReqAmount = revExpDifferenceReqAmount;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }

    public String getSubFundGroupDescription() {
        return subFundGroupDescription;
    }

    public void setSubFundGroupDescription(String subFundGroupDescription) {
        this.subFundGroupDescription = subFundGroupDescription;
    }

    public BigDecimal getTotalGrossAmountChange() {
        return totalGrossAmountChange;
    }

    public void setTotalGrossAmountChange(BigDecimal totalGrossAmountChange) {
        this.totalGrossAmountChange = totalGrossAmountChange;
    }

    public BigDecimal getTotalGrossBaseAmount() {
        return totalGrossBaseAmount;
    }

    public void setTotalGrossBaseAmount(BigDecimal totalGrossBaseAmount) {
        this.totalGrossBaseAmount = totalGrossBaseAmount;
    }

    public BigDecimal getTotalGrossPercentChange() {
        return totalGrossPercentChange;
    }

    public void setTotalGrossPercentChange(BigDecimal totalGrossPercentChange) {
        this.totalGrossPercentChange = totalGrossPercentChange;
    }

    public BigDecimal getTotalGrossReqAmount() {
        return totalGrossReqAmount;
    }

    public void setTotalGrossReqAmount(BigDecimal totalGrossReqAmount) {
        this.totalGrossReqAmount = totalGrossReqAmount;
    }

    public BigDecimal getTotalNetTransferAmountChange() {
        return totalNetTransferAmountChange;
    }

    public void setTotalNetTransferAmountChange(BigDecimal totalNetTransferAmountChange) {
        this.totalNetTransferAmountChange = totalNetTransferAmountChange;
    }

    public BigDecimal getTotalNetTransferBaseAmount() {
        return totalNetTransferBaseAmount;
    }

    public void setTotalNetTransferBaseAmount(BigDecimal totalNetTransferBaseAmount) {
        this.totalNetTransferBaseAmount = totalNetTransferBaseAmount;
    }

    public BigDecimal getTotalNetTransferPercentChange() {
        return totalNetTransferPercentChange;
    }

    public void setTotalNetTransferPercentChange(BigDecimal totalNetTransferPercentChange) {
        this.totalNetTransferPercentChange = totalNetTransferPercentChange;
    }

    public BigDecimal getTotalNetTransferReqAmount() {
        return totalNetTransferReqAmount;
    }

    public void setTotalNetTransferReqAmount(BigDecimal totalNetTransferReqAmount) {
        this.totalNetTransferReqAmount = totalNetTransferReqAmount;
    }

    public BigDecimal getTotalRevenueAmountChange() {
        return totalRevenueAmountChange;
    }

    public void setTotalRevenueAmountChange(BigDecimal totalRevenueAmountChange) {
        this.totalRevenueAmountChange = totalRevenueAmountChange;
    }

    public BigDecimal getTotalRevenueBaseAmount() {
        return totalRevenueBaseAmount;
    }

    public void setTotalRevenueBaseAmount(BigDecimal totalRevenueBaseAmount) {
        this.totalRevenueBaseAmount = totalRevenueBaseAmount;
    }

    public BigDecimal getTotalRevenuePercentChange() {
        return totalRevenuePercentChange;
    }

    public void setTotalRevenuePercentChange(BigDecimal totalRevenuePercentChange) {
        this.totalRevenuePercentChange = totalRevenuePercentChange;
    }

    public BigDecimal getTotalRevenueReqAmount() {
        return totalRevenueReqAmount;
    }

    public void setTotalRevenueReqAmount(BigDecimal totalRevenueReqAmount) {
        this.totalRevenueReqAmount = totalRevenueReqAmount;
    }

    public BigDecimal getTotalTransferAmountChange() {
        return totalTransferAmountChange;
    }

    public void setTotalTransferAmountChange(BigDecimal totalTransferAmountChange) {
        this.totalTransferAmountChange = totalTransferAmountChange;
    }

    public BigDecimal getTotalTransferInBaseAmount() {
        return totalTransferInBaseAmount;
    }

    public void setTotalTransferInBaseAmount(BigDecimal totalTransferInBaseAmount) {
        this.totalTransferInBaseAmount = totalTransferInBaseAmount;
    }

    public BigDecimal getTotalTransferInPercentChange() {
        return totalTransferInPercentChange;
    }

    public void setTotalTransferInPercentChange(BigDecimal totalTransferInPercentChange) {
        this.totalTransferInPercentChange = totalTransferInPercentChange;
    }

    public BigDecimal getTotalTransferInReqAmount() {
        return totalTransferInReqAmount;
    }

    public void setTotalTransferInReqAmount(BigDecimal totalTransferInReqAmount) {
        this.totalTransferInReqAmount = totalTransferInReqAmount;
    }

    public String getChartOfAccountDescription() {
        return chartOfAccountDescription;
    }

    public void setChartOfAccountDescription(String chartOfAccountDescription) {
        this.chartOfAccountDescription = chartOfAccountDescription;
    }

    public String getOrgChartOfAccountDescription() {
        return orgChartOfAccountDescription;
    }

    public void setOrgChartOfAccountDescription(String orgChartOfAccountDescription) {
        this.orgChartOfAccountDescription = orgChartOfAccountDescription;
    }

    public String getOrgChartOfAccountsCode() {
        return orgChartOfAccountsCode;
    }

    public void setOrgChartOfAccountsCode(String orgChartOfAccountsCode) {
        this.orgChartOfAccountsCode = orgChartOfAccountsCode;
    }

    public String getFundGroupName() {
        return fundGroupName;
    }

    public void setFundGroupName(String fundGroupName) {
        this.fundGroupName = fundGroupName;
    }
}
