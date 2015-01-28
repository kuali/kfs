/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.integration.cg.businessobject;

import java.sql.Date;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class represents an association between an award and an account. It's like a reference to the account from the award. This
 * way an award can maintain a collection of these references instead of owning accounts directly.
 */
public class AwardAccount implements ContractsAndGrantsBillingAwardAccount, MutableInactivatable {

    private Long proposalNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String principalId;
    private Date currentLastBilledDate;
    private Date previousLastBilledDate;
    private boolean finalBilledIndicator;
    private boolean active = true;

    private Account account;
    private Chart chartOfAccounts;
    private Person projectDirector;

    /**
     * Gets the finalBilledIndicator attribute.
     *
     * @return Returns the finalBilledIndicator.
     */

    @Override
    public boolean isFinalBilledIndicator() {
        return finalBilledIndicator;
    }

    /**
     * Sets the finalBilledIndicator attribute value.
     *
     * @param finalBilledIndicator The finalBilledIndicator to set.
     */
    public void setFinalBilledIndicator(boolean finalBilledIndicator) {
        this.finalBilledIndicator = finalBilledIndicator;
    }

    /**
     * Gets the currentLastBilledDate attribute.
     *
     * @return Returns the currentLastBilledDate.
     */

    @Override
    public Date getCurrentLastBilledDate() {
        return currentLastBilledDate;
    }

    /**
     * Sets the currentLastBilledDate attribute value.
     *
     * @param currentLastBilledDate The currentLastBilledDate to set.
     */
    public void setCurrentLastBilledDate(Date currentLastBilledDate) {
        this.currentLastBilledDate = currentLastBilledDate;
    }

    /**
     * Gets the previousLastBilledDate attribute.
     *
     * @return Returns the previousLastBilledDate.
     */

    @Override
    public Date getPreviousLastBilledDate() {
        return previousLastBilledDate;
    }

    /**
     * Sets the previousLastBilledDate attribute value.
     *
     * @param previousLastBilledDate The previousLastBilledDate to set.
     */
    public void setPreviousLastBilledDate(Date previousLastBilledDate) {
        this.previousLastBilledDate = previousLastBilledDate;
    }

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getProposalNumber()
     */
    @Override
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


    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getChartOfAccountsCode()
     */
    @Override
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


    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getAccountNumber()
     */
    @Override
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

    /***
     * @see org.kuali.kfs.integration.businessobject.cg.ContractsAndGrantsAccountAwardInformation#getPrincipalId()
     */
    @Override
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     *
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public Person getProjectDirector() {
        return projectDirector;
    }

    public void setProjectDirector(Person projectDirector) {
        this.projectDirector = projectDirector;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsAccountAwardInformation#getProjectDirectorName()
     */
    @Override
    public String getProjectDirectorName() {
        if (!ObjectUtils.isNull(getProjectDirector())) {
            return getProjectDirector().getName();
        }
        return null;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    @Override
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    @Override
    public void refresh() {
    }

}
