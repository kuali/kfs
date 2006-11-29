/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/businessobject/BudgetConstructionControlList.java,v $
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

package org.kuali.module.budget.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class BudgetConstructionControlList extends BusinessObjectBase {

	private String personUniversalIdentifier;
	private String documentNumber;
	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private Integer hierarchyOrganizationLevelCode;
	private Integer selectedOrganizationLevelCode;
	private String selectedOrganizationChartOfAccountsCode;
	private String selectedOrganizationCode;
	private Integer selectedPullFlag;
	private String selectedSubFundGroupCode;

    private BudgetConstructionHeader budgetConstructionHeader;
    private Account account;
	private Chart chartOfAccounts;
	private Org selectedOrganization;
	private Chart selectedOrganizationChartOfAccounts;
    private SubAccount subAccount;
    private SubFundGroup selectedSubFundGroup;
    
	/**
	 * Default constructor.
	 */
	public BudgetConstructionControlList() {

	}

	/**
	 * Gets the personUniversalIdentifier attribute.
	 * 
	 * @return Returns the personUniversalIdentifier
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
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return Returns the universityFiscalYear
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
	 * @return Returns the chartOfAccountsCode
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
	 * @return Returns the accountNumber
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
	 * @return Returns the subAccountNumber
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
	 * Gets the hierarchyOrganizationLevelCode attribute.
	 * 
	 * @return Returns the hierarchyOrganizationLevelCode
	 * 
	 */
	public Integer getHierarchyOrganizationLevelCode() { 
		return hierarchyOrganizationLevelCode;
	}

	/**
	 * Sets the hierarchyOrganizationLevelCode attribute.
	 * 
	 * @param hierarchyOrganizationLevelCode The hierarchyOrganizationLevelCode to set.
	 * 
	 */
	public void setHierarchyOrganizationLevelCode(Integer hierarchyOrganizationLevelCode) {
		this.hierarchyOrganizationLevelCode = hierarchyOrganizationLevelCode;
	}


	/**
	 * Gets the selectedOrganizationLevelCode attribute.
	 * 
	 * @return Returns the selectedOrganizationLevelCode
	 * 
	 */
	public Integer getSelectedOrganizationLevelCode() { 
		return selectedOrganizationLevelCode;
	}

	/**
	 * Sets the selectedOrganizationLevelCode attribute.
	 * 
	 * @param selectedOrganizationLevelCode The selectedOrganizationLevelCode to set.
	 * 
	 */
	public void setSelectedOrganizationLevelCode(Integer selectedOrganizationLevelCode) {
		this.selectedOrganizationLevelCode = selectedOrganizationLevelCode;
	}


	/**
	 * Gets the selectedOrganizationChartOfAccountsCode attribute.
	 * 
	 * @return Returns the selectedOrganizationChartOfAccountsCode
	 * 
	 */
	public String getSelectedOrganizationChartOfAccountsCode() { 
		return selectedOrganizationChartOfAccountsCode;
	}

	/**
	 * Sets the selectedOrganizationChartOfAccountsCode attribute.
	 * 
	 * @param selectedOrganizationChartOfAccountsCode The selectedOrganizationChartOfAccountsCode to set.
	 * 
	 */
	public void setSelectedOrganizationChartOfAccountsCode(String selectedOrganizationChartOfAccountsCode) {
		this.selectedOrganizationChartOfAccountsCode = selectedOrganizationChartOfAccountsCode;
	}


	/**
	 * Gets the selectedOrganizationCode attribute.
	 * 
	 * @return Returns the selectedOrganizationCode
	 * 
	 */
	public String getSelectedOrganizationCode() { 
		return selectedOrganizationCode;
	}

	/**
	 * Sets the selectedOrganizationCode attribute.
	 * 
	 * @param selectedOrganizationCode The selectedOrganizationCode to set.
	 * 
	 */
	public void setSelectedOrganizationCode(String selectedOrganizationCode) {
		this.selectedOrganizationCode = selectedOrganizationCode;
	}


	/**
	 * Gets the selectedPullFlag attribute.
	 * 
	 * @return Returns the selectedPullFlag
	 * 
	 */
	public Integer getSelectedPullFlag() { 
		return selectedPullFlag;
	}

	/**
	 * Sets the selectedPullFlag attribute.
	 * 
	 * @param selectedPullFlag The selectedPullFlag to set.
	 * 
	 */
	public void setSelectedPullFlag(Integer selectedPullFlag) {
		this.selectedPullFlag = selectedPullFlag;
	}


	/**
	 * Gets the selectedSubFundGroupCode attribute.
	 * 
	 * @return Returns the selectedSubFundGroupCode
	 * 
	 */
	public String getSelectedSubFundGroupCode() { 
		return selectedSubFundGroupCode;
	}

	/**
	 * Sets the selectedSubFundGroupCode attribute.
	 * 
	 * @param selectedSubFundGroupCode The selectedSubFundGroupCode to set.
	 * 
	 */
	public void setSelectedSubFundGroupCode(String selectedSubFundGroupCode) {
		this.selectedSubFundGroupCode = selectedSubFundGroupCode;
	}


	/**
	 * Gets the account attribute.
	 * 
	 * @return Returns the account
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
	 * @return Returns the chartOfAccounts
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
	 * Gets the selectedOrganization attribute.
	 * 
	 * @return Returns the selectedOrganization
	 * 
	 */
	public Org getSelectedOrganization() { 
		return selectedOrganization;
	}

	/**
	 * Sets the selectedOrganization attribute.
	 * 
	 * @param selectedOrganization The selectedOrganization to set.
	 * @deprecated
	 */
	public void setSelectedOrganization(Org selectedOrganization) {
		this.selectedOrganization = selectedOrganization;
	}

	/**
	 * Gets the selectedOrganizationChartOfAccounts attribute.
	 * 
	 * @return Returns the selectedOrganizationChartOfAccounts
	 * 
	 */
	public Chart getSelectedOrganizationChartOfAccounts() { 
		return selectedOrganizationChartOfAccounts;
	}

	/**
	 * Sets the selectedOrganizationChartOfAccounts attribute.
	 * 
	 * @param selectedOrganizationChartOfAccounts The selectedOrganizationChartOfAccounts to set.
	 * @deprecated
	 */
	public void setSelectedOrganizationChartOfAccounts(Chart selectedOrganizationChartOfAccounts) {
		this.selectedOrganizationChartOfAccounts = selectedOrganizationChartOfAccounts;
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
     * Gets the selectedSubFundGroup attribute. 
     * @return Returns the selectedSubFundGroup.
     */
    public SubFundGroup getSelectedSubFundGroup() {
        return selectedSubFundGroup;
    }

    /**
     * Sets the selectedSubFundGroup attribute value.
     * @param selectedSubFundGroup The selectedSubFundGroup to set.
     * @deprecated
     */
    public void setSelectedSubFundGroup(SubFundGroup selectedSubFundGroup) {
        this.selectedSubFundGroup = selectedSubFundGroup;
    }

    /**
     * Gets the budgetConstructionHeader attribute. 
     * @return Returns the budgetConstructionHeader.
     */
    public BudgetConstructionHeader getBudgetConstructionHeader() {
        return budgetConstructionHeader;
    }

    /**
     * Sets the budgetConstructionHeader attribute value.
     * @param budgetConstructionHeader The budgetConstructionHeader to set.
     * @deprecated
     */
    public void setBudgetConstructionHeader(BudgetConstructionHeader budgetConstructionHeader) {
        this.budgetConstructionHeader = budgetConstructionHeader;
    }    
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        return m;
    }

}
