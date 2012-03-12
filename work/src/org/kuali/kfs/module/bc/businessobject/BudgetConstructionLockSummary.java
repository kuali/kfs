/*
 * Copyright 2008 The Kuali Foundation
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
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Holds information about a budget lock.
 */
public class BudgetConstructionLockSummary extends PersistableBusinessObjectBase {
    private String lockType;
    private String lockUserId;
    private String documentNumber;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String positionNumber;
    private String positionDescription;

    private Person lockUser;
    private Chart chart;
    private Account account;
    private SubAccount subAccount;
    private BudgetConstructionPosition budgetConstructionPosition;
    private SystemOptions options;

    /**
     * Default Constructor.
     */
    public BudgetConstructionLockSummary() {

    }

    /**
     * Gets the lockType attribute.
     * 
     * @return Returns the lockType.
     */
    public String getLockType() {
        return lockType;
    }

    /**
     * Sets the lockType attribute value.
     * 
     * @param lockType The lockType to set.
     */
    public void setLockType(String lockType) {
        this.lockType = lockType;
    }

    /**
     * Gets the lockUserId attribute.
     * 
     * @return Returns the lockUserId.
     */
    public String getLockUserId() {
        return lockUserId;
    }

    /**
     * Sets the lockUserId attribute value.
     * 
     * @param lockUserId The lockUserId to set.
     */
    public void setLockUserId(String lockUserId) {
        this.lockUserId = lockUserId;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the positionDescription attribute.
     * 
     * @return Returns the positionDescription.
     */
    public String getPositionDescription() {
        return positionDescription;
    }

    /**
     * Sets the positionDescription attribute value.
     * 
     * @param positionDescription The positionDescription to set.
     */
    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute value.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the lockUser attribute.
     * 
     * @return Returns the lockUser.
     */
    public Person getLockUser() {
        if (lockUserId != null) {
            lockUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName(lockUserId);
            if (lockUser == null) {
                throw new RuntimeException("Could not find lock user " + lockUserId);
            }
        }

        return lockUser;
    }

    /**
     * Sets the lockUser attribute value.
     * 
     * @param lockUser The lockUser to set.
     */
    public void setLockUser(Person lockUser) {
        this.lockUser = lockUser;
    }

    /**
     * Gets the chart attribute.
     * 
     * @return Returns the chart.
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute value.
     * 
     * @param chart The chart to set.
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     * 
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
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
     * Gets the budgetConstructionPosition attribute.
     * 
     * @return Returns the budgetConstructionPosition.
     */
    public BudgetConstructionPosition getBudgetConstructionPosition() {
        return budgetConstructionPosition;
    }

    /**
     * Sets the budgetConstructionPosition attribute value.
     * 
     * @param budgetConstructionPosition The budgetConstructionPosition to set.
     */
    public void setBudgetConstructionPosition(BudgetConstructionPosition budgetConstructionPosition) {
        this.budgetConstructionPosition = budgetConstructionPosition;
    }

    /**
     * Gets the options attribute.
     * 
     * @return Returns the options.
     */
    public SystemOptions getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * 
     * @param options The options to set.
     */
    public void setOptions(SystemOptions options) {
        this.options = options;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("lockType", this.lockType);
        m.put("lockUserId", this.lockUserId);

        return m;
    }

}

