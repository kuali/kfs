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

package org.kuali.module.financial.document;

import java.sql.Timestamp;

import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ProcurementCardDocument extends TransactionalDocumentBase {
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private Timestamp transactionCycleStartDate;
	private Timestamp transactionCycleEndDate;
	private String transactionCreditCardNumber;
	private String fdocCardHolderName;
	
	private Account subAccount;
	private Chart chartOfAccounts;

	/**
	 * Default no-arg constructor.
	 */
	public ProcurementCardDocument() {
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
	 * Gets the transactionCycleStartDate attribute.
	 * 
	 * @return - Returns the transactionCycleStartDate
	 * 
	 */
	public Timestamp getTransactionCycleStartDate() { 
		return transactionCycleStartDate;
	}
	

	/**
	 * Sets the transactionCycleStartDate attribute.
	 * 
	 * @param - transactionCycleStartDate The transactionCycleStartDate to set.
	 * 
	 */
	public void setTransactionCycleStartDate(Timestamp transactionCycleStartDate) {
		this.transactionCycleStartDate = transactionCycleStartDate;
	}

	/**
	 * Gets the transactionCycleEndDate attribute.
	 * 
	 * @return - Returns the transactionCycleEndDate
	 * 
	 */
	public Timestamp getTransactionCycleEndDate() { 
		return transactionCycleEndDate;
	}
	

	/**
	 * Sets the transactionCycleEndDate attribute.
	 * 
	 * @param - transactionCycleEndDate The transactionCycleEndDate to set.
	 * 
	 */
	public void setTransactionCycleEndDate(Timestamp transactionCycleEndDate) {
		this.transactionCycleEndDate = transactionCycleEndDate;
	}

	/**
	 * Gets the transactionCreditCardNumber attribute.
	 * 
	 * @return - Returns the transactionCreditCardNumber
	 * 
	 */
	public String getTransactionCreditCardNumber() { 
		return transactionCreditCardNumber;
	}
	

	/**
	 * Sets the transactionCreditCardNumber attribute.
	 * 
	 * @param - transactionCreditCardNumber The transactionCreditCardNumber to set.
	 * 
	 */
	public void setTransactionCreditCardNumber(String transactionCreditCardNumber) {
		this.transactionCreditCardNumber = transactionCreditCardNumber;
	}

	/**
	 * Gets the fdocCardHolderName attribute.
	 * 
	 * @return - Returns the fdocCardHolderName
	 * 
	 */
	public String getFdocCardHolderName() { 
		return fdocCardHolderName;
	}
	

	/**
	 * Sets the fdocCardHolderName attribute.
	 * 
	 * @param - fdocCardHolderName The fdocCardHolderName to set.
	 * 
	 */
	public void setFdocCardHolderName(String fdocCardHolderName) {
		this.fdocCardHolderName = fdocCardHolderName;
	}

	/**
	 * Gets the subAccount attribute.
	 * 
	 * @return - Returns the subAccount
	 * 
	 */
	public Account getSubAccount() { 
		return subAccount;
	}
	

	/**
	 * Sets the subAccount attribute.
	 * 
	 * @param - subAccount The subAccount to set.
	 * @deprecated
	 */
	public void setSubAccount(Account subAccount) {
		this.subAccount = subAccount;
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
}
