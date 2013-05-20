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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * The transient class for Ticklers Report.
 */
public class TicklersReport extends TransientBusinessObjectBase {

    private String principalId;
    private Date followupDate;
    private String activityCode;
    private Long proposalNumber;
    private String agencyNumber;
    private boolean completed;

    private String invoiceNumber;
    private String accountNumber;
    private String agencyName;
    private String activityText;
    private Date activityDate;
    private String activityDescription;
    private String user;
    private KualiDecimal invoiceAmount = KualiDecimal.ZERO;
    private KualiDecimal paymentAmount = KualiDecimal.ZERO;
    private KualiDecimal balanceDue = KualiDecimal.ZERO;

    private Person collector;
    private final String userLookupRoleNamespaceCode = KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR;
    private static final String COLLECTION_ACTIVITY_TITLE_PROPERTY = ArKeyConstants.CollectionActivityDocumentConstants.COLLECTION_ACTIVITY_TITLE_PROPERTY;
    private String collectionActivityInquiryTitle;
    private ContractsAndGrantsCGBAward award;
    private ContractsAndGrantsCGBAgency agency;

    /**
     * Default constructor.
     */
    public TicklersReport() {
        super();
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
     * @param principalId The prinicipal id to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * Gets the userLookupRoleNamespaceCode attribute.
     * 
     * @return Returns lookup role namespace code.
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    /**
     * Gets the userLookupRoleName attribute.
     * 
     * @return Returns the lookup role name.
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

    /**
     * Gets the collector attribute.
     * 
     * @return Returns the collector object.
     */
    public Person getCollector() {
        collector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, collector);
        return collector;
    }

    /**
     * Sets the collector attribute.
     * 
     * @param collector The collector attribute to set.
     */
    public void setCollector(Person collector) {
        this.collector = collector;
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
     * Gets the completed attribute.
     * 
     * @return Returns the completed attribute.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets the completed attribute.
     * 
     * @param completed The completed attribute to set.
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
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
     * @return Returns the accountNumber attribute.
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
    public KualiDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * Sets the invoiceAmount attribute.
     * 
     * @param invoiceAmount The invoice amount to set.
     */
    public void setInvoiceAmount(KualiDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /**
     * Gets the paymentAmount attribute.
     * 
     * @return Returns the payment amount.
     */
    public KualiDecimal getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * Sets the paymentAmount attribute.
     * 
     * @param paymentAmount The payment amount to set.
     */
    public void setPaymentAmount(KualiDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    /**
     * Gets the balanceDue attribute.
     * 
     * @return Returns the balanceDue attribute.
     */
    public KualiDecimal getBalanceDue() {
        return balanceDue;
    }

    /**
     * Sets the balanceDue attribute.
     * 
     * @param balanceDue The balanceDue value to set.
     */
    public void setBalanceDue(KualiDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }

    /**
     * Gets the collectionActivityInquiryTitle attribute.
     * 
     * @return Returns the collectionActivityInquiryTitle.
     */
    public String getCollectionActivityInquiryTitle() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(COLLECTION_ACTIVITY_TITLE_PROPERTY);
    }

    /**
     * Sets the collectionActivityInquiryTitle attribute value.
     * 
     * @param collectionActivityInquiryTitle The collectionActivityInquiryTitle to set.
     */
    public void setCollectionActivityInquiryTitle(String collectionActivityInquiryTitle) {
        this.collectionActivityInquiryTitle = collectionActivityInquiryTitle;
    }


    /**
     * Gets the award attribute.
     * 
     * @return Returns the award.
     */
    public ContractsAndGrantsCGBAward getAward() {
        return award;
    }

    /**
     * Sets the award attribute value.
     * 
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsCGBAward award) {
        this.award = award;
    }

    /**
     * Gets the agency attribute.
     * 
     * @return Returns the agency.
     */
    public ContractsAndGrantsCGBAgency getAgency() {
        return agency;
    }

    /**
     * Sets the agency attribute value.
     * 
     * @param agency The agency to set.
     */
    public void setAgency(ContractsAndGrantsCGBAgency agency) {
        this.agency = agency;
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

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("proposalNumber", this.proposalNumber);
        return m;
    }

}
