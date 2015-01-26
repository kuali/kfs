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

import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * This class is used to generate the collection activity report
 */

public class CollectionActivityReport extends TransientBusinessObjectBase implements CollectionsReport {

    private Long eventId;
    private String principalId;
    private Long proposalNumber;
    private String agencyNumber;

    private String invoiceNumber;
    private String agencyName;
    private String chartOfAccountsCode;
    private String accountNumber;

    private String activityType;
    private String activityComment;
    private Date activityDate;
    private Date followupDate;
    private Date completedDate;
    private String userPrincipalId;

    private Person collector;
    private final String userLookupRoleNamespaceCode = KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR;
    private ContractsAndGrantsAward award;
    private ContractsAndGrantsAgency agency;
    private Account account;
    private Chart chart;


    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        m.put("agencyNumber", this.agencyNumber);
        m.put("proposalNumber", this.proposalNumber);
        m.put("invoiceNumber", this.invoiceNumber);
        return m;
    }

    @Override
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the principalId attribute.
     *
     * @return Return the principal id of collector.
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     *
     * @param principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Return the proposalNumber.
     */
    @Override
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     *
     * @param proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the agencyNumber attribute.
     *
     * @return Return the agencyNumber.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     *
     * @param agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the invoiceNumber attribute.
     *
     * @return Return the invoiceNumber.
     */
    @Override
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoiceNumber attribute.
     *
     * @param invoiceNumber to set.
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the agencyName attribute.
     *
     * @return Return the agencyName of agency.
     */
    public String getAgencyName() {
        return agencyName;
    }

    /**
     * Sets the agencyName attribute.
     *
     * @param agencyName to set.
     */
    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    /**
     * Gets the collector attribute.
     *
     * @return Return the collector.
     */
    public Person getCollector() {
        return collector;
    }

    /**
     * Sets the collector attribute.
     *
     * @param collector The collector to set.
     */
    public void setCollector(Person collector) {
        this.collector = collector;
    }

    /**
     * Gets the award attribute.
     *
     * @return Return the award.
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
     * Gets the agency attribute.
     *
     * @return Return the agency.
     */
    public ContractsAndGrantsAgency getAgency() {
        return agency;
    }

    /**
     * Sets the agency attribute.
     *
     * @param agency The agency to set.
     */
    public void setAgency(ContractsAndGrantsAgency agency) {
        this.agency = agency;
    }

    /**
     * Gets the userLookupRoleNamespaceCode attribute.
     *
     * @return Return the userLookupRoleNamespaceCode
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    /**
     * Gets the userLookupRoleName attribute.
     *
     * @return Return the userLookupRoleName
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

    /**
     * Gets the accountNumber attribute.
     *
     * @return Return the accountNumber
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
     * Gets the account attribute.
     *
     * @return Return the account
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
     * Gets the activityType attribute.
     *
     * @return Return the activityType
     */
    public String getActivityType() {
        return activityType;
    }

    /**
     * Sets the activityType attribute.
     *
     * @param activityType The activityType to set.
     */
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    /**
     * Gets the activityComment attribute.
     *
     * @return Returns the activityComment.
     */
    public String getActivityComment() {
        return activityComment;
    }

    /**
     * Sets the activityComment attribute.
     *
     * @param activityComment The activityComment to set.
     */
    public void setActivityComment(String activityComment) {
        this.activityComment = activityComment;
    }

    /**
     * Gets the activityDate attribute.
     *
     * @return Return the activityDate
     */
    public Date getActivityDate() {
        return activityDate;
    }

    /**
     * Sets the activityDate attribute.
     *
     * @param activityDate The activityDate to set.
     */
    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    /**
     * Gets the followupDate attribute.
     *
     * @return Return the followupDate
     */
    public Date getFollowupDate() {
        return followupDate;
    }

    /**
     * Sets the followupDate attribute.
     *
     * @param followupDate The followupDate to set.
     */
    public void setFollowupDate(Date followupDate) {
        this.followupDate = followupDate;
    }

    /**
     * Gets the completedDate attribute.
     *
     * @return Return the completedDate
     */
    public Date getCompletedDate() {
        return completedDate;
    }

    /**
     * Sets the completedDate attribute.
     *
     * @param completedDate The completedDate to set.
     */
    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    /**
     * Gets the userPrincipalId attribute.
     *
     * @return Return the userPrincipalId.
     */
    public String getUserPrincipalId() {
        return userPrincipalId;
    }

    /**
     * Sets the userPrincipalId attribute.
     *
     * @param userPrincipalId The userPrincipalId to set.
     */
    public void setUserPrincipalId(String userPrincipalId) {
        this.userPrincipalId = userPrincipalId;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Return the chartOfAccountsCode.
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
}
