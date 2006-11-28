/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/businessobject/BudgetConstructionAccountReports.java,v $
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

/**
 * 
 */
public class BudgetConstructionAccountReports extends BusinessObjectBase {

	private String chartOfAccountsCode;
	private String accountNumber;
	private String reportsToChartOfAccountsCode;
	private String reportsToOrganizationCode;

    private Account account;
    private Chart chartOfAccounts;
	private Org reportsToOrganization;
	private Chart reportsToChartOfAccounts;

	/**
	 * Default constructor.
	 */
	public BudgetConstructionAccountReports() {

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
	 * Gets the reportsToChartOfAccountsCode attribute.
	 * 
	 * @return Returns the reportsToChartOfAccountsCode
	 * 
	 */
	public String getReportsToChartOfAccountsCode() { 
		return reportsToChartOfAccountsCode;
	}

	/**
	 * Sets the reportsToChartOfAccountsCode attribute.
	 * 
	 * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
	 * 
	 */
	public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
		this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
	}


	/**
	 * Gets the reportsToOrganizationCode attribute.
	 * 
	 * @return Returns the reportsToOrganizationCode
	 * 
	 */
	public String getReportsToOrganizationCode() { 
		return reportsToOrganizationCode;
	}

	/**
	 * Sets the reportsToOrganizationCode attribute.
	 * 
	 * @param reportsToOrganizationCode The reportsToOrganizationCode to set.
	 * 
	 */
	public void setReportsToOrganizationCode(String reportsToOrganizationCode) {
		this.reportsToOrganizationCode = reportsToOrganizationCode;
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
	 * Gets the reportsToOrganization attribute.
	 * 
	 * @return Returns the reportsToOrganization
	 * 
	 */
	public Org getReportsToOrganization() { 
		return reportsToOrganization;
	}

	/**
	 * Sets the reportsToOrganization attribute.
	 * 
	 * @param reportsToOrganization The reportsToOrganization to set.
	 * @deprecated
	 */
	public void setReportsToOrganization(Org reportsToOrganization) {
		this.reportsToOrganization = reportsToOrganization;
	}

	/**
	 * Gets the reportsToChartOfAccounts attribute.
	 * 
	 * @return Returns the reportsToChartOfAccounts
	 * 
	 */
	public Chart getReportsToChartOfAccounts() { 
		return reportsToChartOfAccounts;
	}

	/**
	 * Sets the reportsToChartOfAccounts attribute.
	 * 
	 * @param reportsToChartOfAccounts The reportsToChartOfAccounts to set.
	 * @deprecated
	 */
	public void setReportsToChartOfAccounts(Chart reportsToChartOfAccounts) {
		this.reportsToChartOfAccounts = reportsToChartOfAccounts;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
	    return m;
    }
}
