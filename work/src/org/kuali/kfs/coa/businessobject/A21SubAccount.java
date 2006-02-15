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
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class A21SubAccount extends BusinessObjectBase {

	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String subAccountTypeCode;
	private String indirectCostRecoveryTypeCode;
	private String financialIcrSeriesIdentifier;
	private String indirectCostRecoveryChartOfAccountsCode;
	private String indirectCostRecoveryAccountNumber;
	private String offCampusCode;
	private String costShareChartOfAccountCode;
	private String costShareSourceAccountNumber;
	private String costShareSourceSubAccountNumber;

    private Chart chart;
    private Account account;
    private SubAccount subAccount;
    private Chart indirectCostRecoveryChartOfAccounts;
    private Account indirectCostRecoveryAccount;
	private Campus campus;
    private Chart costShareChartOfAccount;
    private Account costShareSourceAccount;
    private SubAccount costShareSourceSubAccount;
    private ICRTypeCode indirectCostRecoveryType;
    
	/**
	 * Default constructor.
	 */
	public A21SubAccount() {
        super();
	}

	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return - Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param - chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the accountNumber attribute.
	 * 
	 * @return - Returns the accountNumber
	 * 
	 */
	public String getAccountNumber() { 
		return accountNumber;
	}

	/**
	 * Sets the accountNumber attribute.
	 * 
	 * @param - accountNumber The accountNumber to set.
	 * 
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	/**
	 * Gets the subAccountNumber attribute.
	 * 
	 * @return - Returns the subAccountNumber
	 * 
	 */
	public String getSubAccountNumber() { 
		return subAccountNumber;
	}

	/**
	 * Sets the subAccountNumber attribute.
	 * 
	 * @param - subAccountNumber The subAccountNumber to set.
	 * 
	 */
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}


	/**
	 * Gets the subAccountTypeCode attribute.
	 * 
	 * @return - Returns the subAccountTypeCode
	 * 
	 */
	public String getSubAccountTypeCode() { 
		return subAccountTypeCode;
	}

	/**
	 * Sets the subAccountTypeCode attribute.
	 * 
	 * @param - subAccountTypeCode The subAccountTypeCode to set.
	 * 
	 */
	public void setSubAccountTypeCode(String subAccountTypeCode) {
		this.subAccountTypeCode = subAccountTypeCode;
	}


	/**
	 * Gets the indirectCostRecoveryTypeCode attribute.
	 * 
	 * @return - Returns the indirectCostRecoveryTypeCode
	 * 
	 */
	public String getIndirectCostRecoveryTypeCode() { 
		return indirectCostRecoveryTypeCode;
	}

	/**
	 * Sets the indirectCostRecoveryTypeCode attribute.
	 * 
	 * @param - indirectCostRecoveryTypeCode The indirectCostRecoveryTypeCode to set.
	 * 
	 */
	public void setIndirectCostRecoveryTypeCode(String indirectCostRecoveryTypeCode) {
		this.indirectCostRecoveryTypeCode = indirectCostRecoveryTypeCode;
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
	 * Gets the indirectCostRecoveryChartOfAccountsCode attribute.
	 * 
	 * @return - Returns the indirectCostRecoveryChartOfAccountsCode
	 * 
	 */
	public String getIndirectCostRecoveryChartOfAccountsCode() { 
		return indirectCostRecoveryChartOfAccountsCode;
	}

	/**
	 * Sets the indirectCostRecoveryChartOfAccountsCode attribute.
	 * 
	 * @param - indirectCostRecoveryChartOfAccountsCode The indirectCostRecoveryChartOfAccountsCode to set.
	 * 
	 */
	public void setIndirectCostRecoveryChartOfAccountsCode(String indirectCostRecoveryChartOfAccountsCode) {
		this.indirectCostRecoveryChartOfAccountsCode = indirectCostRecoveryChartOfAccountsCode;
	}


	/**
	 * Gets the indirectCostRecoveryAccountNumber attribute.
	 * 
	 * @return - Returns the indirectCostRecoveryAccountNumber
	 * 
	 */
	public String getIndirectCostRecoveryAccountNumber() { 
		return indirectCostRecoveryAccountNumber;
	}

	/**
	 * Sets the indirectCostRecoveryAccountNumber attribute.
	 * 
	 * @param - indirectCostRecoveryAccountNumber The indirectCostRecoveryAccountNumber to set.
	 * 
	 */
	public void setIndirectCostRecoveryAccountNumber(String indirectCostRecoveryAccountNumber) {
		this.indirectCostRecoveryAccountNumber = indirectCostRecoveryAccountNumber;
	}


	/**
	 * Gets the offCampusCode attribute.
	 * 
	 * @return - Returns the offCampusCode
	 * 
	 */
	public String getOffCampusCode() { 
		return offCampusCode;
	}

	/**
	 * Sets the offCampusCode attribute.
	 * 
	 * @param - offCampusCode The offCampusCode to set.
	 * 
	 */
	public void setOffCampusCode(String offCampusCode) {
		this.offCampusCode = offCampusCode;
	}


	/**
	 * Gets the costShareChartOfAccountCode attribute.
	 * 
	 * @return - Returns the costShareChartOfAccountCode
	 * 
	 */
	public String getCostShareChartOfAccountCode() { 
		return costShareChartOfAccountCode;
	}

	/**
	 * Sets the costShareChartOfAccountCode attribute.
	 * 
	 * @param - costShareChartOfAccountCode The costShareChartOfAccountCode to set.
	 * 
	 */
	public void setCostShareChartOfAccountCode(String costShareChartOfAccountCode) {
		this.costShareChartOfAccountCode = costShareChartOfAccountCode;
	}


	/**
	 * Gets the costShareSourceAccountNumber attribute.
	 * 
	 * @return - Returns the costShareSourceAccountNumber
	 * 
	 */
	public String getCostShareSourceAccountNumber() { 
		return costShareSourceAccountNumber;
	}

	/**
	 * Sets the costShareSourceAccountNumber attribute.
	 * 
	 * @param - costShareSourceAccountNumber The costShareSourceAccountNumber to set.
	 * 
	 */
	public void setCostShareSourceAccountNumber(String costShareSourceAccountNumber) {
		this.costShareSourceAccountNumber = costShareSourceAccountNumber;
	}


	/**
	 * Gets the costShareSourceSubAccountNumber attribute.
	 * 
	 * @return - Returns the costShareSourceSubAccountNumber
	 * 
	 */
	public String getCostShareSourceSubAccountNumber() { 
		return costShareSourceSubAccountNumber;
	}

	/**
	 * Sets the costShareSourceSubAccountNumber attribute.
	 * 
	 * @param - costShareSourceSubAccountNumber The costShareSourceSubAccountNumber to set.
	 * 
	 */
	public void setCostShareSourceSubAccountNumber(String costShareSourceSubAccountNumber) {
		this.costShareSourceSubAccountNumber = costShareSourceSubAccountNumber;
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
	 * Gets the chart attribute.
	 * 
	 * @return - Returns the chart
	 * 
	 */
	public Chart getChart() { 
		return chart;
	}

	/**
	 * Sets the chart attribute.
	 * 
	 * @param - chart The chart to set.
	 * @deprecated
	 */
	public void setChart(Chart chart) {
		this.chart = chart;
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
	 * Gets the indirectCostRecoveryAccount attribute.
	 * 
	 * @return - Returns the indirectCostRecoveryAccount
	 * 
	 */
	public Account getIndirectCostRecoveryAccount() { 
		return indirectCostRecoveryAccount;
	}

	/**
	 * Sets the indirectCostRecoveryAccount attribute.
	 * 
	 * @param - indirectCostRecoveryAccount The indirectCostRecoveryAccount to set.
	 * @deprecated
	 */
	public void setIndirectCostRecoveryAccount(Account indirectCostRecoveryAccount) {
		this.indirectCostRecoveryAccount = indirectCostRecoveryAccount;
	}

	/**
	 * Gets the indirectCostRecoveryChartOfAccounts attribute.
	 * 
	 * @return - Returns the indirectCostRecoveryChartOfAccounts
	 * 
	 */
	public Chart getIndirectCostRecoveryChartOfAccounts() { 
		return indirectCostRecoveryChartOfAccounts;
	}

	/**
	 * Sets the indirectCostRecoveryChartOfAccounts attribute.
	 * 
	 * @param - indirectCostRecoveryChartOfAccounts The indirectCostRecoveryChartOfAccounts to set.
	 * @deprecated
	 */
	public void setIndirectCostRecoveryChartOfAccounts(Chart indirectCostRecoveryChartOfAccounts) {
		this.indirectCostRecoveryChartOfAccounts = indirectCostRecoveryChartOfAccounts;
	}

	/**
	 * Gets the campus attribute.
	 * 
	 * @return - Returns the campus
	 * 
	 */
	public Campus getCampus() { 
		return campus;
	}

	/**
	 * Sets the campus attribute.
	 * 
	 * @param - campus The campus to set.
	 * @deprecated
	 */
	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	/**
	 * Gets the costShareSourceSubAccount attribute.
	 * 
	 * @return - Returns the costShareSourceSubAccount
	 * 
	 */
	public SubAccount getCostShareSourceSubAccount() { 
		return costShareSourceSubAccount;
	}

	/**
	 * Sets the costShareSourceSubAccount attribute.
	 * 
	 * @param - costShareSourceSubAccount The costShareSourceSubAccount to set.
	 * @deprecated
	 */
	public void setCostShareSourceSubAccount(SubAccount costShareSourceSubAccount) {
		this.costShareSourceSubAccount = costShareSourceSubAccount;
	}

	/**
	 * Gets the costShareChartOfAccount attribute.
	 * 
	 * @return - Returns the costShareChartOfAccount
	 * 
	 */
	public Chart getCostShareChartOfAccount() { 
		return costShareChartOfAccount;
	}

	/**
	 * Sets the costShareChartOfAccount attribute.
	 * 
	 * @param - costShareChartOfAccount The costShareChartOfAccount to set.
	 * @deprecated
	 */
	public void setCostShareChartOfAccount(Chart costShareChartOfAccount) {
		this.costShareChartOfAccount = costShareChartOfAccount;
	}

    /**
     * @return Returns the costShareSourceAccount.
     */
    public Account getCostShareSourceAccount() {
        return costShareSourceAccount;
    }

    /**
     * @param costShareSourceAccount The costShareSourceAccount to set.
     * @deprecated
     */
    public void setCostShareSourceAccount(Account costShareSourceAccount) {
        this.costShareSourceAccount = costShareSourceAccount;
    }

    /**
     * @return Returns the indirectCostRecoveryType.
     */
    public ICRTypeCode getIndirectCostRecoveryType() {
        return indirectCostRecoveryType;
    }

    /**
     * @param indirectCostRecoveryType The indirectCostRecoveryType to set.
     * @deprecated
     */
    public void setIndirectCostRecoveryType(ICRTypeCode indirectCostRecoveryType) {
        this.indirectCostRecoveryType = indirectCostRecoveryType;
    }
  
	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
	    return m;
    }


}
