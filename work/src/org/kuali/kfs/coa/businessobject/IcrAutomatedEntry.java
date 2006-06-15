/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.Options;
import org.kuali.core.util.KualiPercent;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class IcrAutomatedEntry extends BusinessObjectBase {

    /**
     * Default no-arg constructor.
     */
    public IcrAutomatedEntry() {
        // initialize the object fiscal year to the current fiscal year
        universityFiscalYear = SpringServiceLocator.getDateTimeService().getCurrentFiscalYear();
    }

    private Integer universityFiscalYear;
    private String financialIcrSeriesIdentifier;
    private String balanceTypeCode;
    private Integer awardIndrCostRcvyEntryNbr;
    private String transactionDebitIndicator;
    private KualiPercent awardIndrCostRcvyRatePct;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String offsetBalanceSheetObjectCodeNumber;

    private Options universityFiscal;
    private BalanceTyp financialBalanceTyp;

    /*
     * Don't use reference objects because Chart, Account, Sub-Account, etc. contain special characters. RO 2/8/06
     * 
     * private Chart chartOfAccounts; private Account account; private SubAccount subAccount; private ObjectCode financialObject;
     * private SubObjCd financialSubObject; private ObjectCode offsetBalanceSheetObjectCode;
     * 
     */

    /*
     * public Account getAccount() { return account; }
     * 
     * 
     * public void setAccount(Account account) { this.account = account; }
     * 
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


    public KualiPercent getAwardIndrCostRcvyRatePct() {
        return awardIndrCostRcvyRatePct;
    }


    public void setAwardIndrCostRcvyRatePct(KualiPercent awardIndrCostRcvyRatePct) {
        this.awardIndrCostRcvyRatePct = awardIndrCostRcvyRatePct;
    }


    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }


    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }

    /*
     * public Chart getChartOfAccounts() { return chartOfAccounts; }
     * 
     * 
     * public void setChartOfAccounts(Chart chartOfAccounts) { this.chartOfAccounts = chartOfAccounts; }
     * 
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
     * public ObjectCode getFinancialObject() { return financialObject; }
     * 
     * 
     * public void setFinancialObject(ObjectCode financialObject) { this.financialObject = financialObject; }
     * 
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }


    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /*
     * public SubObjCd getFinancialSubObject() { return financialSubObject; }
     * 
     * 
     * public void setFinancialSubObject(SubObjCd financialSubObject) { this.financialSubObject = financialSubObject; }
     */

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }


    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /*
     * public ObjectCode getOffsetBalanceSheetObjectCode() { return offsetBalanceSheetObjectCode; }
     * 
     * 
     * public void setOffsetBalanceSheetObjectCode(ObjectCode offsetBalanceSheetObjectCode) { this.offsetBalanceSheetObjectCode =
     * offsetBalanceSheetObjectCode; }
     * 
     */
    public String getOffsetBalanceSheetObjectCodeNumber() {
        return offsetBalanceSheetObjectCodeNumber;
    }


    public void setOffsetBalanceSheetObjectCodeNumber(String offsetBalanceSheetObjectCodeNumber) {
        this.offsetBalanceSheetObjectCodeNumber = offsetBalanceSheetObjectCodeNumber;
    }


    /*
     * public SubAccount getSubAccount() { return subAccount; }
     * 
     * 
     * public void setSubAccount(SubAccount subAccount) { this.subAccount = subAccount; }
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

}