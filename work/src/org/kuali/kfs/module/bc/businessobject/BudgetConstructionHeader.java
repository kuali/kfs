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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.SubAccount;

/**
 * @author Kuali Nervous System Team ()
 */
public class BudgetConstructionHeader extends BusinessObjectBase {

    private String financialDocumentNumber;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private Integer organizationLevelCode;
    private String organizationLevelChartOfAccountsCode;
    private String organizationLevelOrganizationCode;
    private String budgetLockUserIdentifier;
    private String budgetTransactionLockUserIdentifier;

    private Chart chartOfAccounts;
    private Account account;
    private SubAccount subAccount;
    private UniversalUser budgetLockUser;
    private UniversalUser budgetTransactionLockUser;
    private Org organizationLevelOrganization;

    private List budgetConstructionAccountSelected;

    /**
     * Default constructor.
     */
    public BudgetConstructionHeader() {
        budgetConstructionAccountSelected = new ArrayList();

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
     * Gets the organizationLevelChartOfAccountsCode attribute.
     * 
     * @return - Returns the organizationLevelChartOfAccountsCode
     * 
     */
    public String getOrganizationLevelChartOfAccountsCode() {
        return organizationLevelChartOfAccountsCode;
    }

    /**
     * Sets the organizationLevelChartOfAccountsCode attribute.
     * 
     * @param - organizationLevelChartOfAccountsCode The organizationLevelChartOfAccountsCode to set.
     * 
     */
    public void setOrganizationLevelChartOfAccountsCode(String organizationLevelChartOfAccountsCode) {
        this.organizationLevelChartOfAccountsCode = organizationLevelChartOfAccountsCode;
    }


    /**
     * Gets the organizationLevelOrganizationCode attribute.
     * 
     * @return - Returns the organizationLevelOrganizationCode
     * 
     */
    public String getOrganizationLevelOrganizationCode() {
        return organizationLevelOrganizationCode;
    }

    /**
     * Sets the organizationLevelOrganizationCode attribute.
     * 
     * @param - organizationLevelOrganizationCode The organizationLevelOrganizationCode to set.
     * 
     */
    public void setOrganizationLevelOrganizationCode(String organizationLevelOrganizationCode) {
        this.organizationLevelOrganizationCode = organizationLevelOrganizationCode;
    }


    /**
     * Gets the budgetLockUserIdentifier attribute.
     * 
     * @return - Returns the budgetLockUserIdentifier
     * 
     */
    public String getBudgetLockUserIdentifier() {
        return budgetLockUserIdentifier;
    }

    /**
     * Sets the budgetLockUserIdentifier attribute.
     * 
     * @param - budgetLockUserIdentifier The budgetLockUserIdentifier to set.
     * 
     */
    public void setBudgetLockUserIdentifier(String budgetLockUserIdentifier) {
        this.budgetLockUserIdentifier = budgetLockUserIdentifier;
    }


    /**
     * Gets the budgetTransactionLockUserIdentifier attribute.
     * 
     * @return - Returns the budgetTransactionLockUserIdentifier
     * 
     */
    public String getBudgetTransactionLockUserIdentifier() {
        return budgetTransactionLockUserIdentifier;
    }

    /**
     * Sets the budgetTransactionLockUserIdentifier attribute.
     * 
     * @param - budgetTransactionLockUserIdentifier The budgetTransactionLockUserIdentifier to set.
     * 
     */
    public void setBudgetTransactionLockUserIdentifier(String budgetTransactionLockUserIdentifier) {
        this.budgetTransactionLockUserIdentifier = budgetTransactionLockUserIdentifier;
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
    
    public UniversalUser getBudgetLockUser() {
        budgetLockUser = SpringServiceLocator.getKualiUserService().updateUniversalUserIfNecessary(budgetLockUserIdentifier, budgetLockUser);
        return budgetLockUser;
    }

    /**
     * Sets the budgetLockUser attribute.
     * 
     * @param - budgetLockUser The budgetLockUser to set.
     * @deprecated
     */
    public void setBudgetLockUser(UniversalUser budgetLockUser) {
        this.budgetLockUser = budgetLockUser;
    }

    /**
     * Gets the budgetConstructionAccountSelected list.
     * 
     * @return - Returns the budgetConstructionAccountSelected list
     * 
     */
    public List getBudgetConstructionAccountSelected() {
        return budgetConstructionAccountSelected;
    }

    /**
     * Sets the budgetConstructionAccountSelected list.
     * 
     * @param - budgetConstructionAccountSelected The budgetConstructionAccountSelected list to set.
     * 
     */
    public void setBudgetConstructionAccountSelected(List budgetConstructionAccountSelected) {
        this.budgetConstructionAccountSelected = budgetConstructionAccountSelected;
    }

    public UniversalUser getBudgetTransactionLockUser() {
        budgetTransactionLockUser = SpringServiceLocator.getKualiUserService().updateUniversalUserIfNecessary(budgetTransactionLockUserIdentifier, budgetTransactionLockUser);
        return budgetTransactionLockUser;
    }

    /**
     * Sets the budgetTransactionLockUser attribute value.
     * 
     * @param budgetTransactionLockUser The budgetTransactionLockUser to set.
     * @deprecated
     */
    public void setBudgetTransactionLockUser(UniversalUser budgetTransactionLockUser) {
        this.budgetTransactionLockUser = budgetTransactionLockUser;
    }

    /**
     * Gets the organizationLevelOrganization attribute.
     * 
     * @return Returns the organizationLevelOrganization.
     */
    public Org getOrganizationLevelOrganization() {
        return organizationLevelOrganization;
    }

    /**
     * Sets the organizationLevelOrganization attribute value.
     * 
     * @param organizationLevelOrganization The organizationLevelOrganization to set.
     * @deprecated
     */
    public void setOrganizationLevelOrganization(Org organizationLevelOrganization) {
        this.organizationLevelOrganization = organizationLevelOrganization;
    }

    /**
     * Gets the subAccount attribute.
     * 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * 
     * @param subAccount The subAccount to set.
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        return m;
    }

}
