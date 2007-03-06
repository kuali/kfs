/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.module.chart.bo;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.bo.Options;

/**
 * 
 */
public class OrganizationReversion extends PersistableBusinessObjectBase {

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String organizationCode;
    private String budgetReversionChartOfAccountsCode;
    private String budgetReversionAccountNumber;
    private boolean carryForwardByObjectCodeIndicator;
    private String cashReversionFinancialChartOfAccountsCode;
    private String cashReversionAccountNumber;
    private Chart chartOfAccounts;
    private Account cashReversionAccount;
    private Account budgetReversionAccount;
    private Chart budgetReversionChartOfAccounts;
    private Chart cashReversionFinancialChartOfAccounts;
    private Options universityFiscal;
    private Org organization;
    private List<Org> organizations; // This is only used by the "global" document
    private List<OrganizationReversionDetail> organizationReversionDetail;

    /**
     * Default constructor.
     */
    public OrganizationReversion() {
        organizations = new TypedArrayList(Org.class);
        organizationReversionDetail = new TypedArrayList(OrganizationReversionDetail.class);
    }

    public List<OrganizationReversionDetail> getOrganizationReversionDetail() {
        return organizationReversionDetail;
    }

    public void addOrganizationReversionDetail(OrganizationReversionDetail ord) {
        organizationReversionDetail.add(ord);
    }

    public void setOrganizationReversionDetail(List<OrganizationReversionDetail> organizationReversionDetail) {
        this.organizationReversionDetail = organizationReversionDetail;
    }

    public OrganizationReversionDetail getOrganizationReversionDetail(String categoryCode) {
        for (Iterator iter = organizationReversionDetail.iterator(); iter.hasNext();) {
            OrganizationReversionDetail element = (OrganizationReversionDetail) iter.next();
            if (element.getOrganizationReversionCategoryCode().equals(categoryCode)) {
                return element;
            }
        }
        return null;
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
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     * 
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     * 
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the budgetReversionChartOfAccountsCode attribute.
     * 
     * @return Returns the budgetReversionChartOfAccountsCode
     * 
     */
    public String getBudgetReversionChartOfAccountsCode() {
        return budgetReversionChartOfAccountsCode;
    }

    /**
     * Sets the budgetReversionChartOfAccountsCode attribute.
     * 
     * @param budgetReversionChartOfAccountsCode The budgetReversionChartOfAccountsCode to set.
     * 
     */
    public void setBudgetReversionChartOfAccountsCode(String budgetReversionChartOfAccountsCode) {
        this.budgetReversionChartOfAccountsCode = budgetReversionChartOfAccountsCode;
    }


    /**
     * Gets the budgetReversionAccountNumber attribute.
     * 
     * @return Returns the budgetReversionAccountNumber
     * 
     */
    public String getBudgetReversionAccountNumber() {
        return budgetReversionAccountNumber;
    }

    /**
     * Sets the budgetReversionAccountNumber attribute.
     * 
     * @param budgetReversionAccountNumber The budgetReversionAccountNumber to set.
     * 
     */
    public void setBudgetReversionAccountNumber(String budgetReversionAccountNumber) {
        this.budgetReversionAccountNumber = budgetReversionAccountNumber;
    }


    /**
     * Gets the carryForwardByObjectCodeIndicator attribute.
     * 
     * @return Returns the carryForwardByObjectCodeIndicator
     * 
     */
    public boolean isCarryForwardByObjectCodeIndicator() {
        return carryForwardByObjectCodeIndicator;
    }


    /**
     * Sets the carryForwardByObjectCodeIndicator attribute.
     * 
     * @param carryForwardByObjectCodeIndicator The carryForwardByObjectCodeIndicator to set.
     * 
     */
    public void setCarryForwardByObjectCodeIndicator(boolean carryForwardByObjectCodeIndicator) {
        this.carryForwardByObjectCodeIndicator = carryForwardByObjectCodeIndicator;
    }


    /**
     * Gets the cashReversionFinancialChartOfAccountsCode attribute.
     * 
     * @return Returns the cashReversionFinancialChartOfAccountsCode
     * 
     */
    public String getCashReversionFinancialChartOfAccountsCode() {
        return cashReversionFinancialChartOfAccountsCode;
    }

    /**
     * Sets the cashReversionFinancialChartOfAccountsCode attribute.
     * 
     * @param cashReversionFinancialChartOfAccountsCode The cashReversionFinancialChartOfAccountsCode to set.
     * 
     */
    public void setCashReversionFinancialChartOfAccountsCode(String cashReversionFinancialChartOfAccountsCode) {
        this.cashReversionFinancialChartOfAccountsCode = cashReversionFinancialChartOfAccountsCode;
    }


    /**
     * Gets the cashReversionAccountNumber attribute.
     * 
     * @return Returns the cashReversionAccountNumber
     * 
     */
    public String getCashReversionAccountNumber() {
        return cashReversionAccountNumber;
    }

    /**
     * Sets the cashReversionAccountNumber attribute.
     * 
     * @param cashReversionAccountNumber The cashReversionAccountNumber to set.
     * 
     */
    public void setCashReversionAccountNumber(String cashReversionAccountNumber) {
        this.cashReversionAccountNumber = cashReversionAccountNumber;
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
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     * 
     */
    public List<Org> getOrganizations() {
        return organizations;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganizations(List<Org> organization) {
        this.organizations = organization;
    }

    /**
     * Gets the cashReversionAccount attribute.
     * 
     * @return Returns the cashReversionAccount
     * 
     */
    public Account getCashReversionAccount() {
        return cashReversionAccount;
    }

    /**
     * Sets the cashReversionAccount attribute.
     * 
     * @param cashReversionAccount The cashReversionAccount to set.
     * @deprecated
     */
    public void setCashReversionAccount(Account cashReversionAccount) {
        this.cashReversionAccount = cashReversionAccount;
    }

    /**
     * Gets the budgetReversionAccount attribute.
     * 
     * @return Returns the budgetReversionAccount
     * 
     */
    public Account getBudgetReversionAccount() {
        return budgetReversionAccount;
    }

    /**
     * Sets the budgetReversionAccount attribute.
     * 
     * @param budgetReversionAccount The budgetReversionAccount to set.
     * @deprecated
     */
    public void setBudgetReversionAccount(Account budgetReversionAccount) {
        this.budgetReversionAccount = budgetReversionAccount;
    }

    /**
     * Gets the budgetReversionChartOfAccounts attribute.
     * 
     * @return Returns the budgetReversionChartOfAccounts
     * 
     */
    public Chart getBudgetReversionChartOfAccounts() {
        return budgetReversionChartOfAccounts;
    }

    /**
     * Sets the budgetReversionChartOfAccounts attribute.
     * 
     * @param budgetReversionChartOfAccounts The budgetReversionChartOfAccounts to set.
     * @deprecated
     */
    public void setBudgetReversionChartOfAccounts(Chart budgetReversionChartOfAccounts) {
        this.budgetReversionChartOfAccounts = budgetReversionChartOfAccounts;
    }

    /**
     * Gets the cashReversionFinancialChartOfAccounts attribute.
     * 
     * @return Returns the cashReversionFinancialChartOfAccounts
     * 
     */
    public Chart getCashReversionFinancialChartOfAccounts() {
        return cashReversionFinancialChartOfAccounts;
    }

    /**
     * Sets the cashReversionFinancialChartOfAccounts attribute.
     * 
     * @param cashReversionFinancialChartOfAccounts The cashReversionFinancialChartOfAccounts to set.
     * @deprecated
     */
    public void setCashReversionFinancialChartOfAccounts(Chart cashReversionFinancialChartOfAccounts) {
        this.cashReversionFinancialChartOfAccounts = cashReversionFinancialChartOfAccounts;
    }


    /**
     * Gets the universityFiscal attribute.
     * 
     * @return Returns the universityFiscal.
     */
    public Options getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute value.
     * 
     * @param universityFiscal The universityFiscal to set.
     */
    public void setUniversityFiscal(Options universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        return m;
    }

    public Org getOrganization() {
        return organization;
    }

    public void setOrganization(Org organization) {
        this.organization = organization;
    }
    
    /**
     * 
     * This method (a hack by any other name...) returns a string so that an
     * organization reversion can have a link to view its own inquiry page
     * after a look up
     * @return the String "View Organization Reversion"
     */
    public String getOrganizationReversionViewer() {
        return "View Organization Reversion";
    }
}
