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
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.web.comparator.StringValueComparator;

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

    private boolean trExist = false;
    
    BudgetConstructionOrgAccountSummaryReport bcosr;
    private Integer tempFiscalYear = new Integer(2008);

    public Collection<BudgetConstructionOrgAccountSummaryReport> buildReports(Collection<BudgetConstructionAccountSummary> list) {
        Collection<BudgetConstructionOrgAccountSummaryReport> reportSet = new ArrayList();
        StringValueComparator comparator = new StringValueComparator();
        Collections.sort((List) list, new BeanComparator("subFundGroupCode", comparator));
      
        for (BudgetConstructionAccountSummary bcasEntry : list) {
            bcosr = new BudgetConstructionOrgAccountSummaryReport();
            
            buildReportsHeader(bcasEntry);
            buildReportsBody(bcasEntry);
            buildReportsTotal(bcasEntry);

            reportSet.add(bcosr);
            
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
        bcosr.setFiscalYear(prevFiscalyear.toString() + " - " + tempFiscalYear.toString().substring(2,4));
        bcosr.setOrgChartOfAccountsCode(bcas.getOrganizationChartOfAccountsCode());
        if (orgChartDesc == null) {
            bcosr.setOrgChartOfAccountDescription("Error getting account description");
        }
        else {
            bcosr.setOrgChartOfAccountDescription(orgChartDesc);
        }

        bcosr.setOrganizationCode(bcas.getOrganizationCode());
        if (orgName == null) {
            bcosr.setOrganizationName("Error getting organization name");
        }
        else {
            bcosr.setOrganizationName(orgName);
        }

        bcosr.setChartOfAccountsCode(bcas.getChartOfAccountsCode());
        if (chartDesc == null) {
            bcosr.setChartOfAccountDescription("Error getting chart description");
        }
        else {
            bcosr.setChartOfAccountDescription(chartDesc);
        }

        bcosr.setFundGroupCode(bcas.getFundGroupCode());
        if (subFundGroupName == null) {
            bcosr.setFundGroupName("Error getting fund group name");
        }
        else {
            bcosr.setFundGroupName(subFundGroupName);
        }

        bcosr.setSubFundGroupCode(bcas.getSubFundGroupCode());
        if (subFundGroupName == null) {
            bcosr.setSubFundGroupDescription("Error getting sub-fund group description");
        }
        else {
            bcosr.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        bcosr.setBaseFy(prevPrevFiscalyear.toString() + " - " + prevFiscalyear.toString().substring(2, 4));
        bcosr.setReqFy(prevFiscalyear.toString() + " - " + tempFiscalYear.toString().substring(2, 4));

        bcosr.setHeader1("Account/Sub");
        bcosr.setHeader2("Account/Sub name");
        bcosr.setHeader3("Base Amount");
        bcosr.setHeader4("Req. Amount");
        bcosr.setHeader5("Change");
        bcosr.setHeader6("Change");
        bcosr.setConsHdr("");

    }

    public void buildReportsBody(BudgetConstructionAccountSummary bcas) {
        bcosr.setAccountNumber(bcas.getAccountNumber());
        bcosr.setSubAccountNumber(bcas.getSubAccountNumber());

        if (bcas.getSubAccountNumber().equals("-----")) {
            if (bcas.getAccount().getAccountName() == null) {
                bcosr.setAccountNameAndSubAccountName("Error getting account description");
            }
            else
                bcosr.setAccountNameAndSubAccountName(bcas.getAccount().getAccountName());
        }
        else {
            // TODO check when the subAccountNumber is not "-----"
            try {
                if (bcas.getSubAccount().getSubAccountName() == null) {
                    bcosr.setAccountNameAndSubAccountName("Error getting sub-account description");
                }
                else
                    bcosr.setAccountNameAndSubAccountName(bcas.getSubAccount().getSubAccountName());
            }
            catch (PersistenceBrokerException e) {
                bcosr.setAccountNameAndSubAccountName("Error getting sub-account description");
            }
        }


        // build income expense description
        if (bcas.getIncomeExpenseCode().equals("A")) {
            bcosr.setIncExpDesc("Revenue");
            
            totalRevenueBaseAmount = totalRevenueBaseAmount.add(bcas.getAccountLineAnnualBalanceAmount().bigDecimalValue());
            totalRevenueReqAmount = totalRevenueReqAmount.add(bcas.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
            
        }
        else if (bcas.getIncomeExpenseCode().equals("E")) {
            bcosr.setIncExpDesc("Exp.(Gross)");
            
            totalGrossBaseAmount = totalGrossBaseAmount.add(bcas.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
            totalGrossReqAmount = totalGrossReqAmount.add(bcas.getAccountLineAnnualBalanceAmount().bigDecimalValue());
            
        }
        else if (bcas.getIncomeExpenseCode().equals("T")) {
            trExist = true;
            bcosr.setIncExpDesc("Trnfr In");
            
            totalTransferInBaseAmount = totalTransferInBaseAmount.add(bcas.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
            totalTransferInReqAmount = totalTransferInReqAmount.add(bcas.getAccountLineAnnualBalanceAmount().bigDecimalValue());
            
        }
        else {
            if (trExist) {
                trExist = false;
                bcosr.setIncExpDesc("Exp.(Net Trnfr)");
                
                totalNetTransferBaseAmount = totalNetTransferBaseAmount.add(bcas.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                totalNetTransferReqAmount = totalNetTransferReqAmount.add(bcas.getAccountLineAnnualBalanceAmount().bigDecimalValue());
            }
            else {
                bcosr.setIncExpDesc("Expenditure");
                
                totalGrossBaseAmount = totalGrossBaseAmount.add(bcas.getFinancialBeginningBalanceLineAmount().bigDecimalValue());
                totalGrossReqAmount = totalGrossReqAmount.add(bcas.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                
            }
        }
        
        
        BigDecimal beginingBalanceLineAmt = BigDecimal.ZERO;
        BigDecimal accountLineAnnualBalAmt = BigDecimal.ZERO;
        if(bcas.getFinancialBeginningBalanceLineAmount() != null){
            beginingBalanceLineAmt = bcas.getFinancialBeginningBalanceLineAmount().bigDecimalValue();
        }
        
        if(bcas.getFinancialBeginningBalanceLineAmount() != null){
            beginingBalanceLineAmt = bcas.getAccountLineAnnualBalanceAmount().bigDecimalValue();
        }
        
        bcosr.setBaseAmount(beginingBalanceLineAmt);
        bcosr.setReqAmount(accountLineAnnualBalAmt);
        bcosr.setAmountChange(accountLineAnnualBalAmt.subtract(beginingBalanceLineAmt));
    
        if (!beginingBalanceLineAmt.equals(BigDecimal.ZERO)) {
            bcosr.setPercentChange(bcosr.getAmountChange().divide(beginingBalanceLineAmt, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        }

    }

    public void buildReportsTotal(BudgetConstructionAccountSummary bcas) {
        
        totalRevenueAmountChange = totalRevenueReqAmount.subtract(totalRevenueBaseAmount);
        if (!totalRevenueBaseAmount.equals(BigDecimal.ZERO)){
            totalRevenuePercentChange = totalRevenueAmountChange.divide(totalRevenueBaseAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        }
        
        totalGrossAmountChange = totalRevenueReqAmount.subtract(totalGrossBaseAmount);
        
        if (!totalGrossBaseAmount.equals(BigDecimal.ZERO)){
            totalGrossPercentChange = totalGrossAmountChange.divide(totalGrossBaseAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        }
        
        totalTransferAmountChange = totalTransferInReqAmount.subtract(totalTransferInBaseAmount);
        
        if (!totalTransferInBaseAmount.equals(BigDecimal.ZERO)){
            totalTransferInPercentChange = totalTransferAmountChange.divide(totalTransferInBaseAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        }
        
        totalNetTransferAmountChange = totalNetTransferReqAmount.subtract(totalNetTransferBaseAmount);
        
        if (!totalNetTransferBaseAmount.equals(BigDecimal.ZERO)){
            totalNetTransferPercentChange = totalNetTransferAmountChange.divide(totalNetTransferBaseAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        }
        
        revExpDifferenceBaseAmount = totalRevenueBaseAmount.subtract(totalNetTransferBaseAmount);
        revExpDifferenceReqAmount = totalRevenueReqAmount.subtract(totalNetTransferReqAmount);
        revExpDifferenceAmountChange = revExpDifferenceReqAmount.subtract(revExpDifferenceBaseAmount);
        
        if (!revExpDifferenceBaseAmount.equals(BigDecimal.ZERO)){
            revExpDifferencePercentChange = revExpDifferenceAmountChange.divide(revExpDifferenceBaseAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        }
        
        
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
