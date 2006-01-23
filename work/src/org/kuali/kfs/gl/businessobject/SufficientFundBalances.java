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

package org.kuali.module.gl.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class SufficientFundBalances extends BusinessObjectBase {

	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String financialObjectCode;
	private String accountSufficientFundsCode;
	private KualiDecimal currentBudgetBalanceAmount;
	private KualiDecimal accountActualExpenditureAmt;
	private KualiDecimal accountEncumbranceAmount;
	private Date transactionDateTimeStamp;
	private ObjectCode objectCode;
	private Chart chart;
	private Account account;

	/**
	 * Default constructor.
	 */
	public SufficientFundBalances() {

	}

	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return - Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param - universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
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
	 * Gets the financialObjectCode attribute.
	 * 
	 * @return - Returns the financialObjectCode
	 * 
	 */
	public String getFinancialObjectCode() { 
		return financialObjectCode;
	}

	/**
	 * Sets the financialObjectCode attribute.
	 * 
	 * @param - financialObjectCode The financialObjectCode to set.
	 * 
	 */
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}


	/**
	 * Gets the accountSufficientFundsCode attribute.
	 * 
	 * @return - Returns the accountSufficientFundsCode
	 * 
	 */
	public String getAccountSufficientFundsCode() { 
		return accountSufficientFundsCode;
	}

	/**
	 * Sets the accountSufficientFundsCode attribute.
	 * 
	 * @param - accountSufficientFundsCode The accountSufficientFundsCode to set.
	 * 
	 */
	public void setAccountSufficientFundsCode(String accountSufficientFundsCode) {
		this.accountSufficientFundsCode = accountSufficientFundsCode;
	}


	/**
	 * Gets the currentBudgetBalanceAmount attribute.
	 * 
	 * @return - Returns the currentBudgetBalanceAmount
	 * 
	 */
	public KualiDecimal getCurrentBudgetBalanceAmount() { 
		return currentBudgetBalanceAmount;
	}

	/**
	 * Sets the currentBudgetBalanceAmount attribute.
	 * 
	 * @param - currentBudgetBalanceAmount The currentBudgetBalanceAmount to set.
	 * 
	 */
	public void setCurrentBudgetBalanceAmount(KualiDecimal currentBudgetBalanceAmount) {
		this.currentBudgetBalanceAmount = currentBudgetBalanceAmount;
	}


	/**
	 * Gets the accountActualExpenditureAmt attribute.
	 * 
	 * @return - Returns the accountActualExpenditureAmt
	 * 
	 */
	public KualiDecimal getAccountActualExpenditureAmt() { 
		return accountActualExpenditureAmt;
	}

	/**
	 * Sets the accountActualExpenditureAmt attribute.
	 * 
	 * @param - accountActualExpenditureAmt The accountActualExpenditureAmt to set.
	 * 
	 */
	public void setAccountActualExpenditureAmt(KualiDecimal accountActualExpenditureAmt) {
		this.accountActualExpenditureAmt = accountActualExpenditureAmt;
	}


	/**
	 * Gets the accountEncumbranceAmount attribute.
	 * 
	 * @return - Returns the accountEncumbranceAmount
	 * 
	 */
	public KualiDecimal getAccountEncumbranceAmount() { 
		return accountEncumbranceAmount;
	}

	/**
	 * Sets the accountEncumbranceAmount attribute.
	 * 
	 * @param - accountEncumbranceAmount The accountEncumbranceAmount to set.
	 * 
	 */
	public void setAccountEncumbranceAmount(KualiDecimal accountEncumbranceAmount) {
		this.accountEncumbranceAmount = accountEncumbranceAmount;
	}


	/**
	 * Gets the transactionDateTimeStamp attribute.
	 * 
	 * @return - Returns the transactionDateTimeStamp
	 * 
	 */
	public Date getTransactionDateTimeStamp() { 
		return transactionDateTimeStamp;
	}

	/**
	 * Sets the transactionDateTimeStamp attribute.
	 * 
	 * @param - transactionDateTimeStamp The transactionDateTimeStamp to set.
	 * 
	 */
	public void setTransactionDateTimeStamp(Date transactionDateTimeStamp) {
		this.transactionDateTimeStamp = transactionDateTimeStamp;
	}


	/**
	 * Gets the objectCode attribute.
	 * 
	 * @return - Returns the objectCode
	 * 
	 */
	public ObjectCode getObjectCode() { 
		return objectCode;
	}

	/**
	 * Sets the objectCode attribute.
	 * 
	 * @param - objectCode The objectCode to set.
	 * @deprecated
	 */
	public void setObjectCode(ObjectCode objectCode) {
		this.objectCode = objectCode;
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
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("universityFiscalYear", this.universityFiscalYear.toString());
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
	    return m;
    }
}
