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
        //	initialize the object fiscal year to the current fiscal year
        universityFiscalYear = SpringServiceLocator.getDateTimeService().getCurrentFiscalYear();
    }

    private Integer universityFiscalYear; // used to deal with mixed anonymous keys
    private String financialIcrSeriesIdentifier;
    private String balanceTypeCode; // used to deal with mixed anonymous keys
    private Integer awardIndrCostRcvyEntryNbr;
    private String transactionDebitIndicator;
    private Integer awardIndrCostRcvyRatePct;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String offsetBalanceSheetObjectCodeNumber;


    private Chart chartOfAccounts;
    private Account account;
    private SubAccount subAccount;
    private ObjectCode financialObject;
    private SubObjCd financialSubObject;
    private Options universityFiscal;
    private BalanceTyp financialBalanceTyp;
    private ObjectCode offsetBalanceSheetObjectCode;

    /**
     * 
     * This method gets the string representation of the fiscal year.
     * @deprecated - Do not use, only here for OJB anonymous key issue
     * @return
     *  
     */
    private String getUniversityFiscalYear() {
        return getUniversityFiscal().getUniversityFiscalYear().toString();
    }

    /**
     * 
     * This method gets the string represetnation of the BalanceTyp code
     * 
     * @return
     *  
     */
    private String getBalanceTypeCode() {
        return getFinancialBalanceTyp().getCode();
    }

    /**
     * Gets the financialIcrSeriesIdentifier attribute.
     * 
     * @return - Returns the financialIcrSeriesIdentifier
     *  
     */
    public String getFinancialIcrSeriesIdentifier() {
        return financialIcrSeriesIdentifier;
    }

    /**
     * Sets the financialIcrSeriesIdentifier attribute.
     * 
     * @param - financialIcrSeriesIdentifier The financialIcrSeriesIdentifier to set.
     *  
     */
    public void setFinancialIcrSeriesIdentifier(String financialIcrSeriesIdentifier) {
        this.financialIcrSeriesIdentifier = financialIcrSeriesIdentifier;
    }

    /**
     * Gets the awardIndrCostRcvyEntryNbr attribute.
     * 
     * @return - Returns the awardIndrCostRcvyEntryNbr
     *  
     */
    public Integer getAwardIndrCostRcvyEntryNbr() {
        return awardIndrCostRcvyEntryNbr;
    }

    /**
     * Sets the awardIndrCostRcvyEntryNbr attribute.
     * 
     * @param - awardIndrCostRcvyEntryNbr The awardIndrCostRcvyEntryNbr to set.
     *  
     */
    public void setAwardIndrCostRcvyEntryNbr(Integer awardIndrCostRcvyEntryNbr) {
        this.awardIndrCostRcvyEntryNbr = awardIndrCostRcvyEntryNbr;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return - Returns the financialObjectCode
     *  
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param - financialObjectCode The financialObjectCode to set.
     * @deprecated
     */
    public void setFinancialObject(ObjectCode financialObjectCode) {
        this.financialObject = financialObjectCode;
    }

    /**
     * Gets the transactionDebitIndicator attribute.
     * 
     * @return - Returns the transactionDebitIndicator
     *  
     */
    public String getTransactionDebitIndicator() {
        return transactionDebitIndicator;
    }

    /**
     * Sets the transactionDebitIndicator attribute.
     * 
     * @param - transactionDebitIndicator The transactionDebitIndicator to set.
     *  
     */
    public void setTransactionDebitIndicator(String transactionDebitIndicator) {
        this.transactionDebitIndicator = transactionDebitIndicator;
    }

    /**
     * Gets the awardIndrCostRcvyRatePct attribute.
     * 
     * @return - Returns the awardIndrCostRcvyRatePct
     *  
     */
    public Integer getAwardIndrCostRcvyRatePct() {
        return awardIndrCostRcvyRatePct;
    }

    /**
     * Sets the awardIndrCostRcvyRatePct attribute.
     * 
     * @param - awardIndrCostRcvyRatePct The awardIndrCostRcvyRatePct to set.
     *  
     */
    public void setAwardIndrCostRcvyRatePct(Integer awardIndrCostRcvyRatePct) {
        this.awardIndrCostRcvyRatePct = awardIndrCostRcvyRatePct;
    }

    /**
     * Gets the universityFiscal attribute.
     * 
     * @return - Returns the universityFiscal
     *  
     */
    public Options getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute.
     * 
     * @param - universityFiscal The universityFiscal to set.
     * @deprecated
     */
    public void setUniversityFiscal(Options universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

    /**
     * Gets the financialBalanceTyp attribute.
     * 
     * @return - Returns the financialBalanceType
     *  
     */
    public BalanceTyp getFinancialBalanceTyp() {
        return financialBalanceTyp;
    }

    /**
     * Sets the financialBalanceTyp attribute.
     * 
     * @param - financialBalanceTyp The financialBalanceTyp to set.
     * @deprecated
     */
    public void setFinancialBalanceType(BalanceTyp financialBalanceTyp) {
        this.financialBalanceTyp = financialBalanceTyp;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return - Returns the financialSubObjectCode
     *  
     */
    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     * 
     * @param - financialSubObjectCode The financialSubObjectCode to set.
     * @deprecated
     */
    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return - Returns the chartOfAccounts
     *  
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param - chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the account attribute.
     * 
     * @return - Returns the account
     *  
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param - account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the subAccount attribute.
     * 
     * @return - Returns the subAccount
     *  
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute.
     * 
     * @param - subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * @return Returns the offsetBalanceSheetObjectCode.
     */
    public ObjectCode getOffsetBalanceSheetObjectCode() {
        return offsetBalanceSheetObjectCode;
    }

    /**
     * @param offsetBalanceSheetObjectCode The offsetBalanceSheetObjectCode to set.
     * @deprecated
     */
    public void setOffsetBalanceSheetObjectCode(ObjectCode offsetBalanceSheetObjectCode) {
        this.offsetBalanceSheetObjectCode = offsetBalanceSheetObjectCode;
    }

    /**
     * @return Returns the offsetBalanceSheetObjectCodeNumber.
     */
    public String getOffsetBalanceSheetObjectCodeNumber() {
        return offsetBalanceSheetObjectCodeNumber;
    }

    /**
     * @param offsetBalanceSheetObjectCodeNumber The offsetBalanceSheetObjectCodeNumber to set.
     */
    public void setOffsetBalanceSheetObjectCodeNumber(String offsetBalanceSheetObjectCodeNumber) {
        this.offsetBalanceSheetObjectCodeNumber = offsetBalanceSheetObjectCodeNumber;
    }

    /**
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * @param balanceTypeCode The balanceTypeCode to set.
     */
    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }

    /**
     * @param financialBalanceTyp The financialBalanceTyp to set.
     * @deprecated
     */
    public void setFinancialBalanceTyp(BalanceTyp financialBalanceTyp) {
        this.financialBalanceTyp = financialBalanceTyp;
    }

    /**
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
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