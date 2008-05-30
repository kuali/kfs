/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.module.chart.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * 
 */
public class IcrAutomatedEntry extends PersistableBusinessObjectBase {

    /**
     * Default no-arg constructor.
     */
    public IcrAutomatedEntry() {
        // initialize the object fiscal year to the current fiscal year
        universityFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }

    private Integer universityFiscalYear;
    private String financialIcrSeriesIdentifier;
    private String balanceTypeCode;
    private Integer awardIndrCostRcvyEntryNbr;
    private String transactionDebitIndicator;
    private BigDecimal awardIndrCostRcvyRatePct;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String offsetBalanceSheetObjectCodeNumber;
    private boolean active;

    private Options universityFiscal;
    private BalanceTyp financialBalanceTyp;

    /*
     * Don't use reference objects because Chart, Account, Sub-Account, etc. contain special characters. RO 2/8/06 private Chart
     * chartOfAccounts; private Account account; private SubAccount subAccount; private ObjectCode financialObject; private SubObjCd
     * financialSubObject; private ObjectCode offsetBalanceSheetObjectCode;
     */

    /*
     * public Account getAccount() { return account; } public void setAccount(Account account) { this.account = account; }
     */
    public String getAccountNumber() {
        return accountNumber;
    }


    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public Integer getAwardIndrCostRcvyEntryNbr() {
        return awardIndrCostRcvyEntryNbr;
    }


    public void setAwardIndrCostRcvyEntryNbr(Integer awardIndrCostRcvyEntryNbr) {
        this.awardIndrCostRcvyEntryNbr = awardIndrCostRcvyEntryNbr;
    }


    public BigDecimal getAwardIndrCostRcvyRatePct() {
        return awardIndrCostRcvyRatePct;
    }


    public void setAwardIndrCostRcvyRatePct(BigDecimal awardIndrCostRcvyRatePct) {
        this.awardIndrCostRcvyRatePct = awardIndrCostRcvyRatePct;
    }


    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }


    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }

    /*
     * public Chart getChartOfAccounts() { return chartOfAccounts; } public void setChartOfAccounts(Chart chartOfAccounts) {
     * this.chartOfAccounts = chartOfAccounts; }
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    public BalanceTyp getFinancialBalanceTyp() {
        return financialBalanceTyp;
    }


    public void setFinancialBalanceTyp(BalanceTyp financialBalanceTyp) {
        this.financialBalanceTyp = financialBalanceTyp;
    }


    public String getFinancialIcrSeriesIdentifier() {
        return financialIcrSeriesIdentifier;
    }


    public void setFinancialIcrSeriesIdentifier(String financialIcrSeriesIdentifier) {
        this.financialIcrSeriesIdentifier = financialIcrSeriesIdentifier;
    }


    /*
     * public ObjectCode getFinancialObject() { return financialObject; } public void setFinancialObject(ObjectCode financialObject) {
     * this.financialObject = financialObject; }
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }


    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /*
     * public SubObjCd getFinancialSubObject() { return financialSubObject; } public void setFinancialSubObject(SubObjCd
     * financialSubObject) { this.financialSubObject = financialSubObject; }
     */

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }


    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /*
     * public ObjectCode getOffsetBalanceSheetObjectCode() { return offsetBalanceSheetObjectCode; } public void
     * setOffsetBalanceSheetObjectCode(ObjectCode offsetBalanceSheetObjectCode) { this.offsetBalanceSheetObjectCode =
     * offsetBalanceSheetObjectCode; }
     */
    public String getOffsetBalanceSheetObjectCodeNumber() {
        return offsetBalanceSheetObjectCodeNumber;
    }


    public void setOffsetBalanceSheetObjectCodeNumber(String offsetBalanceSheetObjectCodeNumber) {
        this.offsetBalanceSheetObjectCodeNumber = offsetBalanceSheetObjectCodeNumber;
    }


    /*
     * public SubAccount getSubAccount() { return subAccount; } public void setSubAccount(SubAccount subAccount) { this.subAccount =
     * subAccount; }
     */

    public String getSubAccountNumber() {
        return subAccountNumber;
    }


    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    public String getTransactionDebitIndicator() {
        return transactionDebitIndicator;
    }


    public void setTransactionDebitIndicator(String transactionDebitIndicator) {
        this.transactionDebitIndicator = transactionDebitIndicator;
    }


    public Options getUniversityFiscal() {
        return universityFiscal;
    }


    public void setUniversityFiscal(Options universityFiscal) {
        this.universityFiscal = universityFiscal;
    }


    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }


    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {

        LinkedHashMap m = new LinkedHashMap();
        m.put("universityFiscal", this.universityFiscalYear);
        m.put("financialIcrSeriesIdentifier", this.financialIcrSeriesIdentifier);
        m.put("financialBalanceTypeCode", this.balanceTypeCode);
        m.put("awardIndrCostRcvyEntryNbr", this.awardIndrCostRcvyEntryNbr);

        return m;
    }


    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        this.active = active;
    }

}