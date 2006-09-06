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

package org.kuali.module.budget.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.SubAccount;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetConstructionAccountSelect extends BusinessObjectBase {

	private String personUniversalIdentifier;
	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialDocumentNumber;
	private Integer organizationLevelCode;
	private String organizationChartOfAccountsCode;
	private String organizationCode;
	private String financialDocumentStatusCode;
	private String financialDocumentInitiatorIdentifier;
	private Date financialDocumentCreateDate;

    private BudgetConstructionHeader budgetConstructionHeader;
	private Account account;
	private Chart chartOfAccounts;
	private Chart organizationChartOfAccounts;
    private SubAccount subAccount;
    private Org organization;
   
	/**
	 * Default constructor.
	 */
	public BudgetConstructionAccountSelect() {

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
	 * @param - personUniversalIdentifier The personUniversalIdentifier to set.
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
	 * @param - financialDocumentNumber The financialDocumentNumber to set.
	 * 
	 */
	public void setFinancialDocumentNumber(String financialDocumentNumber) {
		this.financialDocumentNumber = financialDocumentNumber;
	}


	/**
	 * Gets the organizationLevelCode attribute.
	 * 
	 * @return - Returns the organizationLevelCode
	 * 
	 */
	public Integer getOrganizationLevelCode() { 
		return organizationLevelCode;
	}

	/**
	 * Sets the organizationLevelCode attribute.
	 * 
	 * @param - organizationLevelCode The organizationLevelCode to set.
	 * 
	 */
	public void setOrganizationLevelCode(Integer organizationLevelCode) {
		this.organizationLevelCode = organizationLevelCode;
	}


	/**
	 * Gets the organizationChartOfAccountsCode attribute.
	 * 
	 * @return - Returns the organizationChartOfAccountsCode
	 * 
	 */
	public String getOrganizationChartOfAccountsCode() { 
		return organizationChartOfAccountsCode;
	}

	/**
	 * Sets the organizationChartOfAccountsCode attribute.
	 * 
	 * @param - organizationChartOfAccountsCode The organizationChartOfAccountsCode to set.
	 * 
	 */
	public void setOrganizationChartOfAccountsCode(String organizationChartOfAccountsCode) {
		this.organizationChartOfAccountsCode = organizationChartOfAccountsCode;
	}


	/**
	 * Gets the organizationCode attribute.
	 * 
	 * @return - Returns the organizationCode
	 * 
	 */
	public String getOrganizationCode() { 
		return organizationCode;
	}

	/**
	 * Sets the organizationCode attribute.
	 * 
	 * @param - organizationCode The organizationCode to set.
	 * 
	 */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}


	/**
	 * Gets the financialDocumentStatusCode attribute.
	 * 
	 * @return - Returns the financialDocumentStatusCode
	 * 
	 */
	public String getFinancialDocumentStatusCode() { 
		return financialDocumentStatusCode;
	}

	/**
	 * Sets the financialDocumentStatusCode attribute.
	 * 
	 * @param - financialDocumentStatusCode The financialDocumentStatusCode to set.
	 * 
	 */
	public void setFinancialDocumentStatusCode(String financialDocumentStatusCode) {
		this.financialDocumentStatusCode = financialDocumentStatusCode;
	}


	/**
	 * Gets the financialDocumentInitiatorIdentifier attribute.
	 * 
	 * @return - Returns the financialDocumentInitiatorIdentifier
	 * 
	 */
	public String getFinancialDocumentInitiatorIdentifier() { 
		return financialDocumentInitiatorIdentifier;
	}

	/**
	 * Sets the financialDocumentInitiatorIdentifier attribute.
	 * 
	 * @param - financialDocumentInitiatorIdentifier The financialDocumentInitiatorIdentifier to set.
	 * 
	 */
	public void setFinancialDocumentInitiatorIdentifier(String financialDocumentInitiatorIdentifier) {
		this.financialDocumentInitiatorIdentifier = financialDocumentInitiatorIdentifier;
	}


	/**
	 * Gets the financialDocumentCreateDate attribute.
	 * 
	 * @return - Returns the financialDocumentCreateDate
	 * 
	 */
	public Date getFinancialDocumentCreateDate() { 
		return financialDocumentCreateDate;
	}

	/**
	 * Sets the financialDocumentCreateDate attribute.
	 * 
	 * @param - financialDocumentCreateDate The financialDocumentCreateDate to set.
	 * 
	 */
	public void setFinancialDocumentCreateDate(Date financialDocumentCreateDate) {
		this.financialDocumentCreateDate = financialDocumentCreateDate;
	}


	/**
	 * Gets the budgetConstructionHeader attribute.
	 * 
	 * @return - Returns the budgetConstructionHeader
	 * 
	 */
	public BudgetConstructionHeader getBudgetConstructionHeader() { 
		return budgetConstructionHeader;
	}

	/**
	 * Sets the budgetConstructionHeader attribute.
	 * 
	 * @param - budgetConstructionHeader The budgetConstructionHeader to set.
	 * @deprecated
	 */
	public void setBudgetConstructionHeader(BudgetConstructionHeader budgetConstructionHeader) {
		this.budgetConstructionHeader = budgetConstructionHeader;
	}

	/**
	 * Gets the organization attribute.
	 * 
	 * @return - Returns the organization
	 * 
	 */
	public Org getOrganization() { 
		return organization;
	}

	/**
	 * Sets the organization attribute.
	 * 
	 * @param - organization The organization to set.
	 * @deprecated
	 */
	public void setOrganization(Org organization) {
		this.organization = organization;
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
	 * Gets the organizationChartOfAccounts attribute.
	 * 
	 * @return - Returns the organizationChartOfAccounts
	 * 
	 */
	public Chart getOrganizationChartOfAccounts() { 
		return organizationChartOfAccounts;
	}

	/**
	 * Sets the organizationChartOfAccounts attribute.
	 * 
	 * @param - organizationChartOfAccounts The organizationChartOfAccounts to set.
	 * @deprecated
	 */
	public void setOrganizationChartOfAccounts(Chart organizationChartOfAccounts) {
		this.organizationChartOfAccounts = organizationChartOfAccounts;
	}

    /**
     * Gets the subAccount attribute. 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
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
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        return m;
    }


}
