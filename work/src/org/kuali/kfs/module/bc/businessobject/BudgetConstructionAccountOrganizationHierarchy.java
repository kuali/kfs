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

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionAccountOrganizationHierarchy extends PersistableBusinessObjectBase {

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private Integer organizationLevelCode;
    private String organizationChartOfAccountsCode;
    private String organizationCode;
    private Chart chartOfAccounts;
    private Account account;
    private Organization organization;
    private Chart organizationChartOfAccounts;

    /**
     * Default constructor.
     */
    public BudgetConstructionAccountOrganizationHierarchy() {

    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
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
     * Gets the organizationLevelCode attribute.
     * 
     * @return Returns the organizationLevelCode
     */
    public Integer getOrganizationLevelCode() {
        return organizationLevelCode;
    }

    /**
     * Sets the organizationLevelCode attribute.
     * 
     * @param organizationLevelCode The organizationLevelCode to set.
     */
    public void setOrganizationLevelCode(Integer organizationLevelCode) {
        this.organizationLevelCode = organizationLevelCode;
    }


    /**
     * Gets the organizationChartOfAccountsCode attribute.
     * 
     * @return Returns the organizationChartOfAccountsCode
     */
    public String getOrganizationChartOfAccountsCode() {
        return organizationChartOfAccountsCode;
    }

    /**
     * Sets the organizationChartOfAccountsCode attribute.
     * 
     * @param organizationChartOfAccountsCode The organizationChartOfAccountsCode to set.
     */
    public void setOrganizationChartOfAccountsCode(String organizationChartOfAccountsCode) {
        this.organizationChartOfAccountsCode = organizationChartOfAccountsCode;
    }


    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
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
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the organizationChartOfAccounts attribute.
     * 
     * @return Returns the organizationChartOfAccounts
     */
    public Chart getOrganizationChartOfAccounts() {
        return organizationChartOfAccounts;
    }

    /**
     * Sets the organizationChartOfAccounts attribute.
     * 
     * @param organizationChartOfAccounts The organizationChartOfAccounts to set.
     * @deprecated
     */
    public void setOrganizationChartOfAccounts(Chart organizationChartOfAccounts) {
        this.organizationChartOfAccounts = organizationChartOfAccounts;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        if (this.organizationLevelCode != null) {
            m.put("organizationLevelCode", this.organizationLevelCode.toString());
        }
        return m;
    }
}
