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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * Milestone Report
 */
public class ContractsGrantsMilestoneReport extends TransientBusinessObjectBase implements MutableInactivatable {

    private Long proposalNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private Long milestoneNumber;
    private KualiDecimal milestoneAmount;
    private Date milestoneExpectedCompletionDate;
    private boolean billed = false;
    private boolean active;
    private ContractsAndGrantsAward award;
    private Account account;
    private Chart chart;

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute value.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the milestoneNumber attribute.
     *
     * @return Returns the milestoneNumber.
     */
    public Long getMilestoneNumber() {
        return milestoneNumber;
    }

    /**
     * Sets the milestoneNumber attribute value.
     *
     * @param milestoneNumber The milestoneNumber to set.
     */
    public void setMilestoneNumber(Long milestoneNumber) {
        this.milestoneNumber = milestoneNumber;
    }

    /**
     * Gets the milestoneAmount attribute.
     *
     * @return Returns the milestoneAmount.
     */
    public KualiDecimal getMilestoneAmount() {
        return milestoneAmount;
    }

    /**
     * Sets the milestoneAmount attribute value.
     *
     * @param milestoneAmount The milestoneAmount to set.
     */
    public void setMilestoneAmount(KualiDecimal milestoneAmount) {
        this.milestoneAmount = milestoneAmount;
    }

    public boolean isBilled() {
        return billed;
    }

    public void setBilled(boolean billed) {
        this.billed = billed;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the milestoneExpectedCompletionDate attribute.
     *
     * @return Returns the milestoneExpectedCompletionDate.
     */
    public Date getMilestoneExpectedCompletionDate() {
        return milestoneExpectedCompletionDate;
    }

    /**
     * Sets the milestoneExpectedCompletionDate attribute value.
     *
     * @param milestoneExpectedCompletionDate The milestoneExpectedCompletionDate to set.
     */
    public void setMilestoneExpectedCompletionDate(Date milestoneExpectedCompletionDate) {
        this.milestoneExpectedCompletionDate = milestoneExpectedCompletionDate;
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
     * Gets the award attribute.
     *
     * @return Returns the award
     */

    public ContractsAndGrantsAward getAward() {
        return award;
    }

    /**
     * Sets the award attribute.
     *
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsAward award) {
        this.award = award;
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
     */
    public void setAccount(Account account) {
        this.account = account;
    }


    /**
     * Gets the chart attribute.
     *
     * @return Returns the chart
     */

    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute.
     *
     * @param chart The chart to set.
     */
    public void setChart(Chart chart) {
        this.chart = chart;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.proposalNumber != null) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }
        if (this.milestoneNumber != null) {
            m.put("milestoneNumber", this.milestoneNumber.toString());
        }
        if (this.milestoneAmount != null) {
            m.put("milestoneAmount", this.milestoneAmount.toString());
        }
        if (this.milestoneExpectedCompletionDate != null) {
            m.put("milestoneExpectedCompletionDate", this.milestoneExpectedCompletionDate.toString());
        }
        m.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.accountNumber);
        m.put("billed", this.billed);
        return m;
    }


}
