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

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.TicklersReport;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Data holder class for Ticklers Report.
 */
public class TicklersReportDetailDataHolder {

    private Date followupDate;
    private String activityCode;
    private Long proposalNumber;
    private String agencyNumber;
    private String accountNumber;

    private String invoiceNumber;
    private String agencyName;
    private String activityText;
    private String activityDescription;
    private Date activityDate;
    private String user;
    private BigDecimal invoiceAmount;
    private BigDecimal paymentAmount;
    private BigDecimal balanceDue;
    private String completed;

    /**
     * Default constructor.
     */
    public TicklersReportDetailDataHolder() {
    }

    /**
     * Constructor to initialize other values from given object.
     * 
     * @param tr TicklersReport object from which values to be set in data holder object.
     */
    public TicklersReportDetailDataHolder(TicklersReport tr) {
        this.followupDate = tr.getFollowupDate();
        this.activityCode = tr.getActivityCode();
        this.proposalNumber = tr.getProposalNumber();
        this.agencyNumber = tr.getAgencyNumber();
        this.invoiceNumber = tr.getInvoiceNumber();
        this.accountNumber = tr.getAccountNumber();
        this.agencyName = tr.getAgencyName();
        this.activityText = ObjectUtils.isNull(tr.getActivityText()) ? "" : tr.getActivityText();
        this.activityDescription = ObjectUtils.isNull(tr.getActivityDescription()) ? "" : tr.getActivityDescription();
        this.activityDate = tr.getActivityDate();
        this.user = tr.getUser();
        this.invoiceAmount = ((ObjectUtils.isNull(tr.getInvoiceAmount())) ? BigDecimal.ZERO : tr.getInvoiceAmount().bigDecimalValue());
        this.paymentAmount = ((ObjectUtils.isNull(tr.getPaymentAmount())) ? BigDecimal.ZERO : tr.getPaymentAmount().bigDecimalValue());
        this.balanceDue = ((ObjectUtils.isNull(tr.getBalanceDue())) ? BigDecimal.ZERO : tr.getBalanceDue().bigDecimalValue());
        this.completed = ((!tr.isCompleted()) ? KFSConstants.ParameterValues.STRING_NO : KFSConstants.ParameterValues.STRING_YES);
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
     * Gets the activityCode attribute.
     * 
     * @return Returns the activity code.
     */
    public String getActivityCode() {
        return activityCode;
    }

    /**
     * Sets the activityCode attribute.
     * 
     * @param activityCode The activity code to set.
     */
    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
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

    /**
     * Gets the activityText attribute.
     * 
     * @return Returns the activity comments.
     */
    public String getActivityText() {
        return activityText;
    }

    /**
     * Sets the activityText attribute.
     * 
     * @param activityText The activity comments to set.
     */
    public void setActivityText(String activityText) {
        this.activityText = activityText;
    }

    /**
     * Gets the activityDescription attribute.
     * 
     * @return Returns the activity description.
     */
    public String getActivityDescription() {
        return activityDescription;
    }

    /**
     * Sets the activityDescription attribute.
     * 
     * @param activityDescription The activity description to set.
     */
    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    /**
     * Gets the invoice amount.
     * 
     * @return Returns the invoice amount.
     */
    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * Sets the invoiceAmount attribute.
     * 
     * @param invoiceAmount The invoice amount to set.
     */
    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /**
     * Gets the paymentAmount attribute.
     * 
     * @return Returns the payment amount.
     */
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * Sets the paymentAmount attribute.
     * 
     * @param paymentAmount The payment amount to set.
     */
    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    /**
     * Gets the balanceDue attribute.
     * 
     * @return Returns the balanceDue attribute.
     */
    public BigDecimal getBalanceDue() {
        return balanceDue;
    }

    /**
     * Sets the balanceDue attribute.
     * 
     * @param balanceDue The balanceDue value to set.
     */
    public void setBalanceDue(BigDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }

    /**
     * Gets the completed attribute.
     * 
     * @return Returns the completed value.
     */
    public String getCompleted() {
        return completed;
    }

    /**
     * Sets the completed attribute.
     * 
     * @param completed The completed value to set.
     */
    public void setCompleted(String completed) {
        this.completed = completed;
    }

    /**
     * Gets activityDate attribute.
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
     * Gets the user attribute.
     * 
     * @return Returns the user attribute.
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the user attribute.
     * 
     * @param user The user attribute to set.
     */
    public void setUser(String user) {
        this.user = user;
    }
}
