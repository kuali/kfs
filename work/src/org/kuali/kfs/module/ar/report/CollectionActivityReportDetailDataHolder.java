/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report;

import java.util.Date;

import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;
import org.kuali.kfs.sys.KFSConstants;

/**
 * Data holder class for Collection Activity Report.
 */
public class CollectionActivityReportDetailDataHolder {

    private Date followupDate;
    private String activityType;
    private String activityComment;
    private Long proposalNumber;
    private String agencyNumber;

    private String accountNumber;
    private String invoiceNumber;
    private Date activityDate;
    private String agencyName;
    private String activityComplete;
    private Date completedDate;
    private String userPrincipalId;

    /**
     * Default constructor.
     */
    public CollectionActivityReportDetailDataHolder() {
    }

    /**
     * Constructor to initialize other values from given object.
     * 
     * @param cr ColletionActivityReport object from which values to be set in data holder object.
     */
    public CollectionActivityReportDetailDataHolder(CollectionActivityReport cr) {
        this.accountNumber = cr.getAccountNumber();
        this.invoiceNumber = cr.getInvoiceNumber();
        this.activityDate = cr.getActivityDate();
        this.followupDate = cr.getFollowupDate();
        this.activityType = cr.getActivityType();
        this.activityComment = cr.getActivityComment();
        this.completedDate = cr.getCompletedDate();
        this.activityComplete = (cr.isCompleted()) ? KFSConstants.ParameterValues.STRING_YES : KFSConstants.ParameterValues.STRING_NO;
        this.userPrincipalId = cr.getUserPrincipalId();
    }

    /**
     * Gets the followupDate attribute.
     * 
     * @return Returns the followup date.
     */
    public Date getFollowupDate() {
        return followupDate;
    }

    /**
     * Sets the followupDate attribute.
     * 
     * @param followupDate The followup date to set.
     */
    public void setFollowupDate(Date followupDate) {
        this.followupDate = followupDate;
    }

    /**
     * Gets the activityType attribute.
     * 
     * @return Returns the activityType.
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
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
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
     * Gets the activityDate attribute.
     * 
     * @return Returns the activityDate.
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
     * Gets the activityComplete attribute.
     * 
     * @return Returns the activityComplete.
     */
    public String getActivityComplete() {
        return activityComplete;
    }

    /**
     * Sets the activityComplete attribute.
     * 
     * @param activityComplete The activityComplete to set.
     */
    public void setActivityComplete(String activityComplete) {
        this.activityComplete = activityComplete;
    }

    /**
     * Gets the completedDate attribute.
     * 
     * @return Returns the completedDate.
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
     * @return Returns the userPrincipalId.
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
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposal number.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposal number to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agency number.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agency number to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the invoiceNumber attribute.
     * 
     * @return Returns the invoice number.
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoiceNumber attribute.
     * 
     * @param invoiceNumber The invoice number to set.
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the agencyName attribute.
     * 
     * @return Returns tha agency name.
     */
    public String getAgencyName() {
        return agencyName;
    }

    /**
     * Sets the agencyName attribute.
     * 
     * @param agencyName The agency name to set.
     */
    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }
}
