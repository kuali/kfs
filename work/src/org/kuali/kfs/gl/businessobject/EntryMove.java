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
public class EntryMove extends BusinessObjectBase {

	private String personUniversalIdentifier;
	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialObjectCode;
	private String financialSubObjectCode;
	private String financialBalanceTypeCode;
	private String financialObjectTypeCode;
	private String universityFiscalPeriodCode;
	private String financialDocumentTypeCode;
	private String financialSystemOriginationCode;
	private String financialDocumentNumber;
	private Integer transactionLedgerEntrySequenceNumber;
	private String transactionLedgerEntryDescription;
	private KualiDecimal transactionLedgerEntryAmount;
	private String transactionDebitCreditCode;
	private Date transactionDate;
	private String organizationDocumentNumber;
	private String projectCode;
	private String organizationReferenceId;
	private String referenceFinancialDocumentTypeCode;
	private String referenceFinancialSystemOriginationCode;
	private String referenceFinancialDocumentNumber;
	private Date financialDocumentReversalDate;
	private String transactionEncumbranceUpdateCode;
	private Date transactionPostingDate;
	private Date transactionDateTimeStamp;
	private String budgetYear;

    private ObjectCode financialObject;
	private Account account;
	private Chart chartOfAccounts;

	/**
	 * Default constructor.
	 */
	public EntryMove() {

	}

	/**
	 * Gets the personUniversalIdentifier attribute.
	 * 
	 * @return - Returns the personUniversalIdentifier
	 * 
	 */
	public String getPersonUniversalIdentifier() { 
		return personUniversalIdentifier;
	}

	/**
	 * Sets the personUniversalIdentifier attribute.
	 * 
	 * @param personUniversalIdentifier The personUniversalIdentifier to set.
	 * 
	 */
	public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
		this.personUniversalIdentifier = personUniversalIdentifier;
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
	 * @param universityFiscalYear The universityFiscalYear to set.
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
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
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
	 * @param accountNumber The accountNumber to set.
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
	 * @param subAccountNumber The subAccountNumber to set.
	 * 
	 */
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
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
	 * @param financialObjectCode The financialObjectCode to set.
	 * 
	 */
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}


	/**
	 * Gets the financialSubObjectCode attribute.
	 * 
	 * @return - Returns the financialSubObjectCode
	 * 
	 */
	public String getFinancialSubObjectCode() { 
		return financialSubObjectCode;
	}

	/**
	 * Sets the financialSubObjectCode attribute.
	 * 
	 * @param financialSubObjectCode The financialSubObjectCode to set.
	 * 
	 */
	public void setFinancialSubObjectCode(String financialSubObjectCode) {
		this.financialSubObjectCode = financialSubObjectCode;
	}


	/**
	 * Gets the financialBalanceTypeCode attribute.
	 * 
	 * @return - Returns the financialBalanceTypeCode
	 * 
	 */
	public String getFinancialBalanceTypeCode() { 
		return financialBalanceTypeCode;
	}

	/**
	 * Sets the financialBalanceTypeCode attribute.
	 * 
	 * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
	 * 
	 */
	public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
		this.financialBalanceTypeCode = financialBalanceTypeCode;
	}


	/**
	 * Gets the financialObjectTypeCode attribute.
	 * 
	 * @return - Returns the financialObjectTypeCode
	 * 
	 */
	public String getFinancialObjectTypeCode() { 
		return financialObjectTypeCode;
	}

	/**
	 * Sets the financialObjectTypeCode attribute.
	 * 
	 * @param financialObjectTypeCode The financialObjectTypeCode to set.
	 * 
	 */
	public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
		this.financialObjectTypeCode = financialObjectTypeCode;
	}


	/**
	 * Gets the universityFiscalPeriodCode attribute.
	 * 
	 * @return - Returns the universityFiscalPeriodCode
	 * 
	 */
	public String getUniversityFiscalPeriodCode() { 
		return universityFiscalPeriodCode;
	}

	/**
	 * Sets the universityFiscalPeriodCode attribute.
	 * 
	 * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
	 * 
	 */
	public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
		this.universityFiscalPeriodCode = universityFiscalPeriodCode;
	}


	/**
	 * Gets the financialDocumentTypeCode attribute.
	 * 
	 * @return - Returns the financialDocumentTypeCode
	 * 
	 */
	public String getFinancialDocumentTypeCode() { 
		return financialDocumentTypeCode;
	}

	/**
	 * Sets the financialDocumentTypeCode attribute.
	 * 
	 * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
	 * 
	 */
	public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
		this.financialDocumentTypeCode = financialDocumentTypeCode;
	}


	/**
	 * Gets the financialSystemOriginationCode attribute.
	 * 
	 * @return - Returns the financialSystemOriginationCode
	 * 
	 */
	public String getFinancialSystemOriginationCode() { 
		return financialSystemOriginationCode;
	}

	/**
	 * Sets the financialSystemOriginationCode attribute.
	 * 
	 * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
	 * 
	 */
	public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
		this.financialSystemOriginationCode = financialSystemOriginationCode;
	}


	/**
	 * Gets the financialDocumentNumber attribute.
	 * 
	 * @return - Returns the financialDocumentNumber
	 * 
	 */
	public String getFinancialDocumentNumber() { 
		return financialDocumentNumber;
	}

	/**
	 * Sets the financialDocumentNumber attribute.
	 * 
	 * @param financialDocumentNumber The financialDocumentNumber to set.
	 * 
	 */
	public void setFinancialDocumentNumber(String financialDocumentNumber) {
		this.financialDocumentNumber = financialDocumentNumber;
	}


	/**
	 * Gets the transactionLedgerEntrySequenceNumber attribute.
	 * 
	 * @return - Returns the transactionLedgerEntrySequenceNumber
	 * 
	 */
	public Integer getTransactionLedgerEntrySequenceNumber() { 
		return transactionLedgerEntrySequenceNumber;
	}

	/**
	 * Sets the transactionLedgerEntrySequenceNumber attribute.
	 * 
	 * @param transactionLedgerEntrySequenceNumber The transactionLedgerEntrySequenceNumber to set.
	 * 
	 */
	public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
		this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
	}


	/**
	 * Gets the transactionLedgerEntryDescription attribute.
	 * 
	 * @return - Returns the transactionLedgerEntryDescription
	 * 
	 */
	public String getTransactionLedgerEntryDescription() { 
		return transactionLedgerEntryDescription;
	}

	/**
	 * Sets the transactionLedgerEntryDescription attribute.
	 * 
	 * @param transactionLedgerEntryDescription The transactionLedgerEntryDescription to set.
	 * 
	 */
	public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
		this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
	}


	/**
	 * Gets the transactionLedgerEntryAmount attribute.
	 * 
	 * @return - Returns the transactionLedgerEntryAmount
	 * 
	 */
	public KualiDecimal getTransactionLedgerEntryAmount() { 
		return transactionLedgerEntryAmount;
	}

	/**
	 * Sets the transactionLedgerEntryAmount attribute.
	 * 
	 * @param transactionLedgerEntryAmount The transactionLedgerEntryAmount to set.
	 * 
	 */
	public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
		this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
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
	 * @param transactionDebitCreditCode The transactionDebitCreditCode to set.
	 * 
	 */
	public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
		this.transactionDebitCreditCode = transactionDebitCreditCode;
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
	 * @param transactionDate The transactionDate to set.
	 * 
	 */
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}


	/**
	 * Gets the organizationDocumentNumber attribute.
	 * 
	 * @return - Returns the organizationDocumentNumber
	 * 
	 */
	public String getOrganizationDocumentNumber() { 
		return organizationDocumentNumber;
	}

	/**
	 * Sets the organizationDocumentNumber attribute.
	 * 
	 * @param organizationDocumentNumber The organizationDocumentNumber to set.
	 * 
	 */
	public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
		this.organizationDocumentNumber = organizationDocumentNumber;
	}


	/**
	 * Gets the projectCode attribute.
	 * 
	 * @return - Returns the projectCode
	 * 
	 */
	public String getProjectCode() { 
		return projectCode;
	}

	/**
	 * Sets the projectCode attribute.
	 * 
	 * @param projectCode The projectCode to set.
	 * 
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}


	/**
	 * Gets the organizationReferenceId attribute.
	 * 
	 * @return - Returns the organizationReferenceId
	 * 
	 */
	public String getOrganizationReferenceId() { 
		return organizationReferenceId;
	}

	/**
	 * Sets the organizationReferenceId attribute.
	 * 
	 * @param organizationReferenceId The organizationReferenceId to set.
	 * 
	 */
	public void setOrganizationReferenceId(String organizationReferenceId) {
		this.organizationReferenceId = organizationReferenceId;
	}


	/**
	 * Gets the referenceFinancialDocumentTypeCode attribute.
	 * 
	 * @return - Returns the referenceFinancialDocumentTypeCode
	 * 
	 */
	public String getReferenceFinancialDocumentTypeCode() { 
		return referenceFinancialDocumentTypeCode;
	}

	/**
	 * Sets the referenceFinancialDocumentTypeCode attribute.
	 * 
	 * @param referenceFinancialDocumentTypeCode The referenceFinancialDocumentTypeCode to set.
	 * 
	 */
	public void setReferenceFinancialDocumentTypeCode(String referenceFinancialDocumentTypeCode) {
		this.referenceFinancialDocumentTypeCode = referenceFinancialDocumentTypeCode;
	}


	/**
	 * Gets the referenceFinancialSystemOriginationCode attribute.
	 * 
	 * @return - Returns the referenceFinancialSystemOriginationCode
	 * 
	 */
	public String getReferenceFinancialSystemOriginationCode() { 
		return referenceFinancialSystemOriginationCode;
	}

	/**
	 * Sets the referenceFinancialSystemOriginationCode attribute.
	 * 
	 * @param referenceFinancialSystemOriginationCode The referenceFinancialSystemOriginationCode to set.
	 * 
	 */
	public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
		this.referenceFinancialSystemOriginationCode = referenceFinancialSystemOriginationCode;
	}


	/**
	 * Gets the referenceFinancialDocumentNumber attribute.
	 * 
	 * @return - Returns the referenceFinancialDocumentNumber
	 * 
	 */
	public String getReferenceFinancialDocumentNumber() { 
		return referenceFinancialDocumentNumber;
	}

	/**
	 * Sets the referenceFinancialDocumentNumber attribute.
	 * 
	 * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
	 * 
	 */
	public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
		this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
	}


	/**
	 * Gets the financialDocumentReversalDate attribute.
	 * 
	 * @return - Returns the financialDocumentReversalDate
	 * 
	 */
	public Date getFinancialDocumentReversalDate() { 
		return financialDocumentReversalDate;
	}

	/**
	 * Sets the financialDocumentReversalDate attribute.
	 * 
	 * @param financialDocumentReversalDate The financialDocumentReversalDate to set.
	 * 
	 */
	public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
		this.financialDocumentReversalDate = financialDocumentReversalDate;
	}


	/**
	 * Gets the transactionEncumbranceUpdateCode attribute.
	 * 
	 * @return - Returns the transactionEncumbranceUpdateCode
	 * 
	 */
	public String getTransactionEncumbranceUpdateCode() { 
		return transactionEncumbranceUpdateCode;
	}

	/**
	 * Sets the transactionEncumbranceUpdateCode attribute.
	 * 
	 * @param transactionEncumbranceUpdateCode The transactionEncumbranceUpdateCode to set.
	 * 
	 */
	public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
		this.transactionEncumbranceUpdateCode = transactionEncumbranceUpdateCode;
	}


	/**
	 * Gets the transactionPostingDate attribute.
	 * 
	 * @return - Returns the transactionPostingDate
	 * 
	 */
	public Date getTransactionPostingDate() { 
		return transactionPostingDate;
	}

	/**
	 * Sets the transactionPostingDate attribute.
	 * 
	 * @param transactionPostingDate The transactionPostingDate to set.
	 * 
	 */
	public void setTransactionPostingDate(Date transactionPostingDate) {
		this.transactionPostingDate = transactionPostingDate;
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
	 * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
	 * 
	 */
	public void setTransactionDateTimeStamp(Date transactionDateTimeStamp) {
		this.transactionDateTimeStamp = transactionDateTimeStamp;
	}


	/**
	 * Gets the budgetYear attribute.
	 * 
	 * @return - Returns the budgetYear
	 * 
	 */
	public String getBudgetYear() { 
		return budgetYear;
	}

	/**
	 * Sets the budgetYear attribute.
	 * 
	 * @param budgetYear The budgetYear to set.
	 * 
	 */
	public void setBudgetYear(String budgetYear) {
		this.budgetYear = budgetYear;
	}


	/**
	 * Gets the financialObject attribute.
	 * 
	 * @return - Returns the financialObject
	 * 
	 */
	public ObjectCode getFinancialObject() { 
		return financialObject;
	}

	/**
	 * Sets the financialObject attribute.
	 * 
	 * @param financialObject The financialObject to set.
	 * @deprecated
	 */
	public void setFinancialObject(ObjectCode financialObject) {
		this.financialObject = financialObject;
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
	 * @param account The account to set.
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
	 * @param chartOfAccounts The chartOfAccounts to set.
	 * @deprecated
	 */
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        m.put("financialBalanceTypeCode", this.financialBalanceTypeCode);
        m.put("financialObjectTypeCode", this.financialObjectTypeCode);
        m.put("universityFiscalPeriodCode", this.universityFiscalPeriodCode);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("financialSystemOriginationCode", this.financialSystemOriginationCode);
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        if (this.transactionLedgerEntrySequenceNumber != null) {
            m.put("transactionLedgerEntrySequenceNumber", this.transactionLedgerEntrySequenceNumber.toString());
        }
	    return m;
    }
}
