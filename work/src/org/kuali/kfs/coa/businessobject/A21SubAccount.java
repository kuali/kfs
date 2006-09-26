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
import org.kuali.module.chart.bo.codes.ICRTypeCode;

/**
 * @author Kuali Nervous System Team ()
 */
public class A21SubAccount extends BusinessObjectBase {

    private static final long serialVersionUID = 2983753447370117974L;

    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String subAccountTypeCode;
    private String indirectCostRecoveryTypeCode;
    private String financialIcrSeriesIdentifier;
    private String indirectCostRecoveryChartOfAccountsCode;
    private String indirectCostRecoveryAccountNumber;
    private boolean offCampusCode;
    private String costShareChartOfAccountCode;
    private String costShareSourceAccountNumber;
    private String costShareSourceSubAccountNumber;

    private Chart indirectCostRecoveryChartOfAccounts;
    private Account indirectCostRecoveryAccount;
    private Chart costShareChartOfAccount;
    private Account costShareAccount;
    private SubAccount costShareSourceSubAccount;
    private ICRTypeCode icrTypeCode;

    /**
     * 
     */
    public A21SubAccount() {
        super();
    }

    /**
     * Gets the serialVersionUID attribute.
     * 
     * @return Returns the serialVersionUID.
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the icrTypeCode attribute.
     * 
     * @return Returns the icrTypeCode.
     */
    public ICRTypeCode getIcrTypeCode() {
        return icrTypeCode;
    }

    /**
     * Sets the icrTypeCode attribute value.
     * 
     * @param icrTypeCode The icrTypeCode to set.
     */
    public void setIcrTypeCode(ICRTypeCode icrTypeCode) {
        this.icrTypeCode = icrTypeCode;
    }

    /**
     * Gets the indirectCostRecoveryAccount attribute.
     * 
     * @return Returns the indirectCostRecoveryAccount.
     */
    public Account getIndirectCostRecoveryAccount() {
        return indirectCostRecoveryAccount;
    }

    /**
     * Sets the indirectCostRecoveryAccount attribute value.
     * 
     * @param indirectCostRecoveryAccount The indirectCostRecoveryAccount to set.
     */
    public void setIndirectCostRecoveryAccount(Account indirectCostRecoveryAccount) {
        this.indirectCostRecoveryAccount = indirectCostRecoveryAccount;
    }

    /**
     * Gets the indirectCostRecoveryAccountNumber attribute.
     * 
     * @return Returns the indirectCostRecoveryAccountNumber.
     */
    public String getIndirectCostRecoveryAccountNumber() {
        return indirectCostRecoveryAccountNumber;
    }

    /**
     * Sets the indirectCostRecoveryAccountNumber attribute value.
     * 
     * @param indirectCostRecoveryAccountNumber The indirectCostRecoveryAccountNumber to set.
     */
    public void setIndirectCostRecoveryAccountNumber(String indirectCostRecoveryAccountNumber) {
        this.indirectCostRecoveryAccountNumber = indirectCostRecoveryAccountNumber;
    }

    /**
     * Gets the indirectCostRecoveryChartOfAccountsCode attribute.
     * 
     * @return Returns the indirectCostRecoveryChartOfAccountsCode.
     */
    public String getIndirectCostRecoveryChartOfAccountsCode() {
        return indirectCostRecoveryChartOfAccountsCode;
    }

    /**
     * Sets the indirectCostRecoveryChartOfAccountsCode attribute value.
     * 
     * @param indirectCostRecoveryChartOfAccountsCode The indirectCostRecoveryChartOfAccountsCode to set.
     */
    public void setIndirectCostRecoveryChartOfAccountsCode(String indirectCostRecoveryChartOfAccountsCode) {
        this.indirectCostRecoveryChartOfAccountsCode = indirectCostRecoveryChartOfAccountsCode;
    }

    /**
     * Gets the indirectCostRecoveryTypeCode attribute.
     * 
     * @return Returns the indirectCostRecoveryTypeCode.
     */
    public String getIndirectCostRecoveryTypeCode() {
        return indirectCostRecoveryTypeCode;
    }

    /**
     * Sets the indirectCostRecoveryTypeCode attribute value.
     * 
     * @param indirectCostRecoveryTypeCode The indirectCostRecoveryTypeCode to set.
     */
    public void setIndirectCostRecoveryTypeCode(String indirectCostRecoveryTypeCode) {
        this.indirectCostRecoveryTypeCode = indirectCostRecoveryTypeCode;
    }

    /**
     * Gets the financialIcrSeriesIdentifier attribute.
     * 
     * @return Returns the financialIcrSeriesIdentifier.
     */
    public String getFinancialIcrSeriesIdentifier() {
        return financialIcrSeriesIdentifier;
    }

    /**
     * Sets the financialIcrSeriesIdentifier attribute value.
     * 
     * @param financialIcrSeriesIdentifier The financialIcrSeriesIdentifier to set.
     */
    public void setFinancialIcrSeriesIdentifier(String financialIcrSeriesIdentifier) {
        this.financialIcrSeriesIdentifier = financialIcrSeriesIdentifier;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the subAccountTypeCode attribute.
     * 
     * @return Returns the subAccountTypeCode.
     */
    public String getSubAccountTypeCode() {
        return subAccountTypeCode;
    }

    /**
     * Sets the subAccountTypeCode attribute value.
     * 
     * @param subAccountTypeCode The subAccountTypeCode to set.
     */
    public void setSubAccountTypeCode(String subAccountTypeCode) {
        this.subAccountTypeCode = subAccountTypeCode;
    }

    /**
     * Gets the costShareAccount attribute.
     * 
     * @return Returns the costShareAccount.
     */
    public Account getCostShareAccount() {
        return costShareAccount;
    }

    /**
     * Sets the costShareAccount attribute value.
     * 
     * @param costShareAccount The costShareAccount to set.
     */
    public void setCostShareAccount(Account costShareAccount) {
        this.costShareAccount = costShareAccount;
    }

    /**
     * Gets the costShareChartOfAccount attribute.
     * 
     * @return Returns the costShareChartOfAccount.
     */
    public Chart getCostShareChartOfAccount() {
        return costShareChartOfAccount;
    }

    /**
     * Sets the costShareChartOfAccount attribute value.
     * 
     * @param costShareChartOfAccount The costShareChartOfAccount to set.
     */
    public void setCostShareChartOfAccount(Chart costShareChartOfAccount) {
        this.costShareChartOfAccount = costShareChartOfAccount;
    }

    /**
     * Gets the costShareChartOfAccountCode attribute.
     * 
     * @return Returns the costShareChartOfAccountCode.
     */
    public String getCostShareChartOfAccountCode() {
        return costShareChartOfAccountCode;
    }

    /**
     * Sets the costShareChartOfAccountCode attribute value.
     * 
     * @param costShareChartOfAccountCode The costShareChartOfAccountCode to set.
     */
    public void setCostShareChartOfAccountCode(String costShareChartOfAccountCode) {
        this.costShareChartOfAccountCode = costShareChartOfAccountCode;
    }

    /**
     * Gets the costShareSourceAccountNumber attribute.
     * 
     * @return Returns the costShareSourceAccountNumber.
     */
    public String getCostShareSourceAccountNumber() {
        return costShareSourceAccountNumber;
    }

    /**
     * Sets the costShareSourceAccountNumber attribute value.
     * 
     * @param costShareSourceAccountNumber The costShareSourceAccountNumber to set.
     */
    public void setCostShareSourceAccountNumber(String costShareSourceAccountNumber) {
        this.costShareSourceAccountNumber = costShareSourceAccountNumber;
    }

    /**
     * Gets the costShareSourceSubAccount attribute.
     * 
     * @return Returns the costShareSourceSubAccount.
     */
    public SubAccount getCostShareSourceSubAccount() {
        return costShareSourceSubAccount;
    }

    /**
     * Sets the costShareSourceSubAccount attribute value.
     * 
     * @param costShareSourceSubAccount The costShareSourceSubAccount to set.
     */
    public void setCostShareSourceSubAccount(SubAccount costShareSourceSubAccount) {
        this.costShareSourceSubAccount = costShareSourceSubAccount;
    }

    /**
     * Gets the costShareSourceSubAccountNumber attribute.
     * 
     * @return Returns the costShareSourceSubAccountNumber.
     */
    public String getCostShareSourceSubAccountNumber() {
        return costShareSourceSubAccountNumber;
    }

    /**
     * Sets the costShareSourceSubAccountNumber attribute value.
     * 
     * @param costShareSourceSubAccountNumber The costShareSourceSubAccountNumber to set.
     */
    public void setCostShareSourceSubAccountNumber(String costShareSourceSubAccountNumber) {
        this.costShareSourceSubAccountNumber = costShareSourceSubAccountNumber;
    }

    /**
     * Gets the indirectCostRecoveryChartOfAccounts attribute.
     * 
     * @return Returns the indirectCostRecoveryChartOfAccounts.
     */
    public Chart getIndirectCostRecoveryChartOfAccounts() {
        return indirectCostRecoveryChartOfAccounts;
    }

    /**
     * Sets the indirectCostRecoveryChartOfAccounts attribute value.
     * 
     * @param indirectCostRecoveryChartOfAccounts The indirectCostRecoveryChartOfAccounts to set.
     */
    public void setIndirectCostRecoveryChartOfAccounts(Chart indirectCostRecoveryChartOfAccounts) {
        this.indirectCostRecoveryChartOfAccounts = indirectCostRecoveryChartOfAccounts;
    }

    /**
     * Gets the offCampusCode attribute.
     * 
     * @return Returns the offCampusCode.
     */
    public boolean getOffCampusCode() {
        return offCampusCode;
    }

    /**
     * Sets the offCampusCode attribute value.
     * 
     * @param offCampusCode The offCampusCode to set.
     */
    public void setOffCampusCode(boolean offCampusCode) {
        this.offCampusCode = offCampusCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("chartOfAccountsCode", getChartOfAccountsCode());
        map.put("accountNumber", getAccountNumber());
        map.put("subAccountNumber", getSubAccountNumber());
        return map;
    }


}
