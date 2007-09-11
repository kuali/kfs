/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;

/**
 * This class represents an association between an award and an account. It's
 * like a reference to the account from the award. This way an award can
 * maintain a collection of these references instead of owning accounts 
 * directly.
 */
public class AwardAccount extends PersistableBusinessObjectBase implements CGProjectDirector, Inactivateable {

    private Long proposalNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String personUniversalIdentifier;
    private boolean active;
    
    private Account account;
    private Chart chartOfAccounts;
    private ProjectDirector projectDirector;
    private Award award;

    /**
     * Default constructor.
     */
    public AwardAccount() {
        // Struts needs this instance to populate the secondary key, personUserIdentifier.
        projectDirector = new ProjectDirector();
    }

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
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
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * Sets the personUniversalIdentifier attribute.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
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
     * @deprecated Setter is required by OJB, but should not be used to modify 
     * this attribute. This attribute is set on the initial creation of the 
     * object and should not be changed.
     */
    @Deprecated
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
     * @deprecated Setter is required by OJB, but should not be used to modify
     * this attribute. This attribute is set on the initial creation of the 
     * object and should not be changed.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the project director attribute.
     * 
     * @return Returns the projectDirector.
     */
    public ProjectDirector getProjectDirector() {
        return projectDirector;
    }

    /**
     * Sets the project director attribute
     * 
     * @param projectDirector The projectDirector to set.
     * @deprecated Setter is required by OJB, but should not be used to modify 
     * this attribute. This attribute is set on the initial creation of the 
     * object and should not be changed.
     */
    @Deprecated
    public void setProjectDirector(ProjectDirector projectDirector) {
        this.projectDirector = projectDirector;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        return m;
    }

    /**
     * This method returns the Award object associated with this AwardAccount.
     * 
     * @return The Award object associated with this AwardAccount.
     */
    public Award getAward() {
        return award;
    }

    /**
     * This method sets the associated award to the value provided.
     * 
     * @param award Value to be assigned to the associated Award object.
     */
    public void setAward(Award award) {
        this.award = award;
    }

    /**
     * 
     * @see org.kuali.core.bo.Inactivateable#isActive()
     */
    public boolean isActive() {
        return account.isAccountClosedIndicator();
    }

    /**
     * 
     * @see org.kuali.core.bo.Inactivateable#setActive(boolean)
     */
    public void setActive(boolean active) {
        account.setAccountClosedIndicator(active);
    }
}
