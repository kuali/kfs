/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.module.bc.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionAccountReports extends PersistableBusinessObjectBase {

    private String chartOfAccountsCode;
    private String accountNumber;
    private String reportsToChartOfAccountsCode;
    private String reportsToOrganizationCode;

    private Account account;
    private Chart chartOfAccounts;
    private Chart reportsToChartOfAccounts;
    private BudgetConstructionOrganizationReports budgetConstructionOrganizationReports;

    private List budgetConstructionAccountOrganizationHierarchy;

    /**
     * Default constructor.
     */
    public BudgetConstructionAccountReports() {

    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the reportsToChartOfAccountsCode attribute.
     * 
     * @return Returns the reportsToChartOfAccountsCode
     */
    public String getReportsToChartOfAccountsCode() {
        return reportsToChartOfAccountsCode;
    }

    /**
     * Sets the reportsToChartOfAccountsCode attribute.
     * 
     * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
     */
    public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
        this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
    }


    /**
     * Gets the reportsToOrganizationCode attribute.
     * 
     * @return Returns the reportsToOrganizationCode
     */
    public String getReportsToOrganizationCode() {
        return reportsToOrganizationCode;
    }

    /**
     * Sets the reportsToOrganizationCode attribute.
     * 
     * @param reportsToOrganizationCode The reportsToOrganizationCode to set.
     */
    public void setReportsToOrganizationCode(String reportsToOrganizationCode) {
        this.reportsToOrganizationCode = reportsToOrganizationCode;
    }


    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
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
     * Gets the reportsToChartOfAccounts attribute.
     * 
     * @return Returns the reportsToChartOfAccounts
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
     * Gets the budgetConstructionOrganizationReports attribute.
     * 
     * @return Returns the budgetConstructionOrganizationReports.
     */
    public BudgetConstructionOrganizationReports getBudgetConstructionOrganizationReports() {
        return budgetConstructionOrganizationReports;
    }

    /**
     * Sets the budgetConstructionOrganizationReports attribute value.
     * 
     * @param budgetConstructionOrganizationReports The budgetConstructionOrganizationReports to set.
     * @deprecated
     */
    public void setBudgetConstructionOrganizationReports(BudgetConstructionOrganizationReports budgetConstructionOrganizationReports) {
        this.budgetConstructionOrganizationReports = budgetConstructionOrganizationReports;
    }

    /**
     * Gets the budgetConstructionAccountOrganizationHierarchy attribute.
     * 
     * @return Returns the budgetConstructionAccountOrganizationHierarchy.
     */
    public List<BudgetConstructionAccountOrganizationHierarchy> getBudgetConstructionAccountOrganizationHierarchy() {
        return budgetConstructionAccountOrganizationHierarchy;
    }

    /**
     * Sets the budgetConstructionAccountOrganizationHierarchy attribute value.
     * 
     * @param budgetConstructionAccountOrganizationHierarchy The budgetConstructionAccountOrganizationHierarchy to set.
     */
    public void setBudgetConstructionAccountOrganizationHierarchy(List<BudgetConstructionAccountOrganizationHierarchy> budgetConstructionAccountOrganizationHierarchy) {
        this.budgetConstructionAccountOrganizationHierarchy = budgetConstructionAccountOrganizationHierarchy;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        return m;
    }
}
