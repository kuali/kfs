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

package org.kuali.module.financial.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ProcurementCardTransaction extends BusinessObjectBase {

	private String transactionIdentifier;
	private Integer transactionSequenceRowNumber;
	private String transactionCreditCardNumber;
	private KualiDecimal financialDocumentTotalAmount;
	private String transactionDebitCreditCode;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String accountTypeCode;
	private String subAccountNumber;
	private Date transactionCycleStartDate;
	private Date transactionCycleEndDate;
	private String financialDocumentCardHolderName;
	private Date transactionDate;
	private String transactionReferenceNumber;
	private String transactionVendorName;
	private String transactionMerchantCategoryCode;

    private Account account;
	private Chart chartOfAccounts;
	
	//private static Integer incrementSequenceNum = new Integer(0);

	/**
	 * Default constructor.
	 */
	public ProcurementCardTransaction() {

	}

	/**
	 * Gets the transactionIdentifier attribute.
	 * 
	 * @return - Returns the transactionIdentifier
	 * 
	 */
	public String getTransactionIdentifier() { 
		return transactionIdentifier;
	}

	/**
	 * Sets the transactionIdentifier attribute.
	 * 
	 * @param - transactionIdentifier The transactionIdentifier to set.
	 * 
	 */
	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}


	/**
	 * Gets the transactionSequenceRowNumber attribute.
	 * 
	 * @return - Returns the transactionSequenceRowNumber
	 * 
	 */
	public Integer getTransactionSequenceRowNumber() { 
		return transactionSequenceRowNumber;
	}

	/**
	 * Sets the transactionSequenceRowNumber attribute.
	 * 
	 * @param - transactionSequenceRowNumber The transactionSequenceRowNumber to set.
	 * 
	 */
	public void setTransactionSequenceRowNumber(Integer transactionSequenceRowNumber) {
//		this.transactionSequenceRowNumber = new Integer(incrementSequenceNum.intValue() + 1);
//		incrementSequenceNum = this.transactionSequenceRowNumber;
		this.transactionSequenceRowNumber = transactionSequenceRowNumber;
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
	 * Gets the financialDocumentTotalAmount attribute.
	 * 
	 * @return - Returns the financialDocumentTotalAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentTotalAmount() { 
		return financialDocumentTotalAmount;
	}

	/**
	 * Sets the financialDocumentTotalAmount attribute.
	 * 
	 * @param - financialDocumentTotalAmount The financialDocumentTotalAmount to set.
	 * 
	 */
	public void setFinancialDocumentTotalAmount(KualiDecimal financialDocumentTotalAmount) {
		this.financialDocumentTotalAmount = financialDocumentTotalAmount;
	}
	
	public void setFinancialDocumentTotalAmount(String financialDocumentTotalAmount) {
		this.financialDocumentTotalAmount = new KualiDecimal(financialDocumentTotalAmount);
	}


	/**
	 * Gets the transactionDebitCreditCode attribute.
	 * 
	 * @return - Returns the transactionDebitCreditCode
	 * 
	 */
	public String getTransactionDebitCreditCode() { 
		return transactionDebitCreditCode;
	}

	/**
	 * Sets the transactionDebitCreditCode attribute.
	 * 
	 * @param - transactionDebitCreditCode The transactionDebitCreditCode to set.
	 * 
	 */
	public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
		this.transactionDebitCreditCode = transactionDebitCreditCode;
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
	 * Gets the accountTypeCode attribute.
	 * 
	 * @return - Returns the accountTypeCode
	 * 
	 */
	public String getAccountTypeCode() { 
		return accountTypeCode;
	}

	/**
	 * Sets the accountTypeCode attribute.
	 * 
	 * @param - accountTypeCode The accountTypeCode to set.
	 * 
	 */
	public void setAccountTypeCode(String accountTypeCode) {
		this.accountTypeCode = accountTypeCode;
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
	public Date getTransactionCycleStartDate() { 
		return transactionCycleStartDate;
	}

	/**
	 * Sets the transactionCycleStartDate attribute.
	 * 
	 * @param - transactionCycleStartDate The transactionCycleStartDate to set.
	 * 
	 */
	public void setTransactionCycleStartDate(Date transactionCycleStartDate) {
		this.transactionCycleStartDate = transactionCycleStartDate;
	}


	/**
	 * Gets the transactionCycleEndDate attribute.
	 * 
	 * @return - Returns the transactionCycleEndDate
	 * 
	 */
	public Date getTransactionCycleEndDate() { 
		return transactionCycleEndDate;
	}

	/**
	 * Sets the transactionCycleEndDate attribute.
	 * 
	 * @param - transactionCycleEndDate The transactionCycleEndDate to set.
	 * 
	 */
	public void setTransactionCycleEndDate(Date transactionCycleEndDate) {
		this.transactionCycleEndDate = transactionCycleEndDate;
	}


	/**
	 * Gets the financialDocumentCardHolderName attribute.
	 * 
	 * @return - Returns the financialDocumentCardHolderName
	 * 
	 */
	public String getFinancialDocumentCardHolderName() { 
		return financialDocumentCardHolderName;
	}

	/**
	 * Sets the financialDocumentCardHolderName attribute.
	 * 
	 * @param - financialDocumentCardHolderName The financialDocumentCardHolderName to set.
	 * 
	 */
	public void setFinancialDocumentCardHolderName(String financialDocumentCardHolderName) {
		this.financialDocumentCardHolderName = financialDocumentCardHolderName;
	}


	/**
	 * Gets the transactionDate attribute.
	 * 
	 * @return - Returns the transactionDate
	 * 
	 */
	public Date getTransactionDate() { 
		return transactionDate;
	}

	/**
	 * Sets the transactionDate attribute.
	 * 
	 * @param - transactionDate The transactionDate to set.
	 * 
	 */
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}


	/**
	 * Gets the transactionReferenceNumber attribute.
	 * 
	 * @return - Returns the transactionReferenceNumber
	 * 
	 */
	public String getTransactionReferenceNumber() { 
		return transactionReferenceNumber;
	}

	/**
	 * Sets the transactionReferenceNumber attribute.
	 * 
	 * @param - transactionReferenceNumber The transactionReferenceNumber to set.
	 * 
	 */
	public void setTransactionReferenceNumber(String transactionReferenceNumber) {
		this.transactionReferenceNumber = transactionReferenceNumber;
	}


	/**
	 * Gets the transactionVendorName attribute.
	 * 
	 * @return - Returns the transactionVendorName
	 * 
	 */
	public String getTransactionVendorName() { 
		return transactionVendorName;
	}

	/**
	 * Sets the transactionVendorName attribute.
	 * 
	 * @param - transactionVendorName The transactionVendorName to set.
	 * 
	 */
	public void setTransactionVendorName(String transactionVendorName) {
		this.transactionVendorName = transactionVendorName;
	}


	/**
	 * Gets the transactionMerchantCategoryCode attribute.
	 * 
	 * @return - Returns the transactionMerchantCategoryCode
	 * 
	 */
	public String getTransactionMerchantCategoryCode() { 
		return transactionMerchantCategoryCode;
	}

	/**
	 * Sets the transactionMerchantCategoryCode attribute.
	 * 
	 * @param - transactionMerchantCategoryCode The transactionMerchantCategoryCode to set.
	 * 
	 */
	public void setTransactionMerchantCategoryCode(String transactionMerchantCategoryCode) {
		this.transactionMerchantCategoryCode = transactionMerchantCategoryCode;
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
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();
        m.put("transactionIdentifier", this.transactionIdentifier);
        if (this.transactionSequenceRowNumber != null) {
            m.put("transactionSequenceRowNumber", this.transactionSequenceRowNumber.toString());
        }
	    return m;
    }
}
