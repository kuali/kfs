/*
 * Copyright 2006-2009 The Kuali Foundation
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

package org.kuali.kfs.module.external.kc.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingFrequency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFund;
import org.kuali.kfs.integration.cg.ContractsAndGrantsOrganization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsProjectDirector;
import org.kuali.kfs.module.external.kc.dto.AwardDTO;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Defines a financial award object.
 */
public class Award implements ContractsAndGrantsBillingAward {
    private static final String AWARD_INQUIRY_TITLE_PROPERTY = "message.inquiry.award.title";

    private Long proposalNumber;
    private String awardNumber;
    private String agencyNumber;
    private String primeAgencyNumber;
    private String awardTitle;
    private String grantNumber;
    private String cfdaNumber;

    private Proposal proposal;
    private Agency agency;
    private Agency primeAgency;
    private List<AwardAccount> awardAccounts;

    //BillingAward fields
    private String awardId;
    private Date awardBeginningDate;
    private Date awardEndingDate;
    private Date lastBilledDate;
    private KualiDecimal awardTotalAmount;
    private String awardAddendumNumber;
    private KualiDecimal awardAllocatedUniversityComputingServicesAmount;
    private KualiDecimal federalPassThroughFundedAmount;
    private Date awardEntryDate;
    private KualiDecimal agencyFuture1Amount;
    private KualiDecimal agencyFuture2Amount;
    private KualiDecimal agencyFuture3Amount;
    private String awardDocumentNumber;
    private Timestamp awardLastUpdateDate;
    private boolean federalPassThroughIndicator;
    private String oldProposalNumber;
    private KualiDecimal awardDirectCostAmount;
    private KualiDecimal awardIndirectCostAmount;
    private KualiDecimal federalFundedAmount;
    private Timestamp awardCreateTimestamp;
    private Date awardClosingDate;
    private String proposalAwardTypeCode;
    private String awardStatusCode;
    private String letterOfCreditFundCode;
    private String grantDescriptionCode;
    private String letterOfCreditCreationType;
    private String federalPassThroughAgencyNumber;
    private String agencyAnalystName;
    private String analystTelephoneNumber;
    private String preferredBillingFrequency;
    private String preferredReportTemplate;
    private String preferredReportFrequency;
    private String awardProjectTitle;
    private String awardCommentText;
    private String awardPurposeCode;
    private boolean active;
    private String kimGroupNames;
    private List<ContractsAndGrantsBillingAwardAccount> activeAwardAccounts;
    private String routingOrg;
    private String routingChart;
    private boolean stateTransferIndicator;
    private boolean suspendInvoicingIndicator;
    private boolean additionalFormsRequiredIndicator;
    private String additionalFormsDescription;
    private String suspensionReason;
    private String contractGrantType;
    private String invoicingOptions;
    private KualiDecimal minInvoiceAmount;
    private boolean autoApproveIndicator;
    private String lookupPersonUniversalIdentifier;
    private Person lookupPerson;
    private String lookupFundMgrPersonUniversalIdentifier;
    private Person lookupFundMgrPerson;
    private String userLookupRoleNamespaceCode;
    private ContractsAndGrantsLetterOfCreditFund letterOfCreditFund;
    private String userLookupRoleName;
    private AwardFundManager awardPrimaryFundManager;
    private ContractsAndGrantsBillingFrequency billingFrequency;
    private ContractsAndGrantsProjectDirector awardPrimaryProjectDirector;
    private ContractsAndGrantsOrganization primaryAwardOrganization;
    private Date fundingExpirationDate;
    private String commentText;
    private String dunningCampaign;
    private boolean stopWorkIndicator;

    public Award(AwardDTO kcAward) {
        setAwardNumber(kcAward.getAwardId());
        this.setAwardBeginningDate(kcAward.getAwardStartDate());
        this.setAwardEndingDate(kcAward.getAwardEndDate());
        this.setAwardTotalAmount(kcAward.getAwardTotalAmount());
        this.setAwardDirectCostAmount(kcAward.getAwardDirectCostAmount());
        this.setAwardIndirectCostAmount(kcAward.getAwardIndirectCostAmount());
        this.setAwardDocumentNumber(kcAward.getAwardDocumentNumber());
        this.setAwardLastUpdateDate(kcAward.getAwardLastUpdateDate());
        this.setAwardCreateTimestamp(kcAward.getAwardCreateTimestamp());
        this.setProposalAwardTypeCode(kcAward.getProposalAwardTypeCode());
        this.setAwardStatusCode(kcAward.getAwardStatusCode());
        this.setAgencyNumber(kcAward.getSponsorCode());
        this.setAwardTitle(kcAward.getTitle());
        this.setAwardCommentText(kcAward.getAwardCommentText());
        this.setAgency(new Agency(kcAward.getSponsor()));
        this.setCommentText(kcAward.getCommentText());
        this.setProposal(new Proposal(kcAward.getProposal()));
        this.getProposal().setAward(this);
        if (StringUtils.isNotEmpty(kcAward.getFundManagerId())) {
            awardPrimaryFundManager = new AwardFundManager(proposalNumber, kcAward.getFundManagerId());
        }
    }

    /**
     * Default no-args constructor.
     */
    public Award() {
        awardAccounts = new ArrayList<AwardAccount>();
    }

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber
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

    /**
     * @return a String to represent this field on the inquiry
     */
    @Override
    public String getAwardInquiryTitle() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(AWARD_INQUIRY_TITLE_PROPERTY);
    }


    @Override
    public Proposal getProposal() {
        return proposal;
    }


    public void prepareForWorkflow() {}


    @Override
    public void refresh() {}


    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }


    @Override
    public Agency getAgency() {
        return agency;
    }


    public void setAgency(Agency agency) {
        this.agency = agency;
    }


    /**
     * Gets the awardAccounts list.
     *
     * @return Returns the awardAccounts.
     */
    public List<AwardAccount> getAwardAccounts() {
        return awardAccounts;
    }

    /**
     * Sets the awardAccounts list.
     *
     * @param awardAccounts The awardAccounts to set.
     */
    public void setAwardAccounts(List<AwardAccount> awardAccounts) {
        this.awardAccounts = awardAccounts;
    }

    @Override
    public String getAgencyNumber() {
        return agencyNumber;
    }

    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    public String getAwardTitle() {
        return awardTitle;
    }

    public void setAwardTitle(String awardTitle) {
        this.awardTitle = awardTitle;
    }

    public String getPrimeAgencyNumber() {
        return primeAgencyNumber;
    }

    public void setPrimeAgencyNumber(String primeAgencyNumber) {
        this.primeAgencyNumber = primeAgencyNumber;
    }

    public Agency getPrimeAgency() {
        return primeAgency;
    }

    public void setPrimeAgency(Agency primeAgency) {
        this.primeAgency = primeAgency;
    }

    public String getAwardNumber() {
        return awardNumber;
    }

    public void setAwardNumber(String awardNumber) {
        this.awardNumber = awardNumber;
    }

    public String getGrantNumber() {
        return grantNumber;
    }

    public void setGrantNumber(String grantNumber) {
        this.grantNumber = grantNumber;
    }


    /**
     * Gets the cfdaNumber attribute.
     *
     * @return Returns the cfdaNumber
     */
    public String getCfdaNumber() {
        return cfdaNumber;
    }

    /**
     * Sets the cfdaNumber attribute.
     *
     * @param cfdaNumber The cfdaNumber to set.
     */
    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }

    @Override
    public String getAwardId() {
        return awardId;
    }

    public void setAwardId(String awardId) {
        this.awardId = awardId;
    }

    @Override
    public Date getAwardBeginningDate() {
        return awardBeginningDate;
    }

    public void setAwardBeginningDate(Date awardBeginningDate) {
        this.awardBeginningDate = awardBeginningDate;
    }

    @Override
    public Date getAwardEndingDate() {
        return awardEndingDate;
    }

    public void setAwardEndingDate(Date awardEndingDate) {
        this.awardEndingDate = awardEndingDate;
    }

    @Override
    public Date getLastBilledDate() {
        return lastBilledDate;
    }

    public void setLastBilledDate(Date lastBilledDate) {
        this.lastBilledDate = lastBilledDate;
    }

    @Override
    public KualiDecimal getAwardTotalAmount() {
        return awardTotalAmount;
    }

    public void setAwardTotalAmount(KualiDecimal awardTotalAmount) {
        this.awardTotalAmount = awardTotalAmount;
    }

    @Override
    public String getAwardAddendumNumber() {
        return awardAddendumNumber;
    }

    public void setAwardAddendumNumber(String awardAddendumNumber) {
        this.awardAddendumNumber = awardAddendumNumber;
    }

    @Override
    public KualiDecimal getAwardAllocatedUniversityComputingServicesAmount() {
        return awardAllocatedUniversityComputingServicesAmount;
    }

    public void setAwardAllocatedUniversityComputingServicesAmount(KualiDecimal awardAllocatedUniversityComputingServicesAmount) {
        this.awardAllocatedUniversityComputingServicesAmount = awardAllocatedUniversityComputingServicesAmount;
    }

    @Override
    public KualiDecimal getFederalPassThroughFundedAmount() {
        return federalPassThroughFundedAmount;
    }

    public void setFederalPassThroughFundedAmount(KualiDecimal federalPassThroughFundedAmount) {
        this.federalPassThroughFundedAmount = federalPassThroughFundedAmount;
    }

    @Override
    public Date getAwardEntryDate() {
        return awardEntryDate;
    }

    public void setAwardEntryDate(Date awardEntryDate) {
        this.awardEntryDate = awardEntryDate;
    }

    @Override
    public KualiDecimal getAgencyFuture1Amount() {
        return agencyFuture1Amount;
    }

    public void setAgencyFuture1Amount(KualiDecimal agencyFuture1Amount) {
        this.agencyFuture1Amount = agencyFuture1Amount;
    }

    @Override
    public KualiDecimal getAgencyFuture2Amount() {
        return agencyFuture2Amount;
    }

    public void setAgencyFuture2Amount(KualiDecimal agencyFuture2Amount) {
        this.agencyFuture2Amount = agencyFuture2Amount;
    }

    @Override
    public KualiDecimal getAgencyFuture3Amount() {
        return agencyFuture3Amount;
    }

    public void setAgencyFuture3Amount(KualiDecimal agencyFuture3Amount) {
        this.agencyFuture3Amount = agencyFuture3Amount;
    }

    @Override
    public String getAwardDocumentNumber() {
        return awardDocumentNumber;
    }

    public void setAwardDocumentNumber(String awardDocumentNumber) {
        this.awardDocumentNumber = awardDocumentNumber;
    }

    @Override
    public Timestamp getAwardLastUpdateDate() {
        return awardLastUpdateDate;
    }

    public void setAwardLastUpdateDate(Timestamp awardLastUpdateDate) {
        this.awardLastUpdateDate = awardLastUpdateDate;
    }

    public boolean isFederalPassThroughIndicator() {
        return federalPassThroughIndicator;
    }
    @Override
    public boolean getFederalPassThroughIndicator() {
        return federalPassThroughIndicator;
    }

    public void setFederalPassThroughIndicator(boolean federalPassThroughIndicator) {
        this.federalPassThroughIndicator = federalPassThroughIndicator;
    }

    @Override
    public String getOldProposalNumber() {
        return oldProposalNumber;
    }

    public void setOldProposalNumber(String oldProposalNumber) {
        this.oldProposalNumber = oldProposalNumber;
    }

    @Override
    public KualiDecimal getAwardDirectCostAmount() {
        return awardDirectCostAmount;
    }

    public void setAwardDirectCostAmount(KualiDecimal awardDirectCostAmount) {
        this.awardDirectCostAmount = awardDirectCostAmount;
    }

    @Override
    public KualiDecimal getAwardIndirectCostAmount() {
        return awardIndirectCostAmount;
    }

    public void setAwardIndirectCostAmount(KualiDecimal awardIndirectCostAmount) {
        this.awardIndirectCostAmount = awardIndirectCostAmount;
    }

    @Override
    public KualiDecimal getFederalFundedAmount() {
        return federalFundedAmount;
    }

    public void setFederalFundedAmount(KualiDecimal federalFundedAmount) {
        this.federalFundedAmount = federalFundedAmount;
    }

    @Override
    public Timestamp getAwardCreateTimestamp() {
        return awardCreateTimestamp;
    }

    public void setAwardCreateTimestamp(Timestamp awardCreateTimestamp) {
        this.awardCreateTimestamp = awardCreateTimestamp;
    }

    @Override
    public Date getAwardClosingDate() {
        return awardClosingDate;
    }

    public void setAwardClosingDate(Date awardClosingDate) {
        this.awardClosingDate = awardClosingDate;
    }

    @Override
    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }

    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }

    @Override
    public String getAwardStatusCode() {
        return awardStatusCode;
    }

    public void setAwardStatusCode(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }

    @Override
    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }

    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }

    @Override
    public String getGrantDescriptionCode() {
        return grantDescriptionCode;
    }

    public void setGrantDescriptionCode(String grantDescriptionCode) {
        this.grantDescriptionCode = grantDescriptionCode;
    }

    @Override
    public String getLetterOfCreditCreationType() {
        return letterOfCreditCreationType;
    }

    public void setLetterOfCreditCreationType(String letterOfCreditCreationType) {
        this.letterOfCreditCreationType = letterOfCreditCreationType;
    }

    @Override
    public String getFederalPassThroughAgencyNumber() {
        return federalPassThroughAgencyNumber;
    }

    public void setFederalPassThroughAgencyNumber(String federalPassThroughAgencyNumber) {
        this.federalPassThroughAgencyNumber = federalPassThroughAgencyNumber;
    }

    @Override
    public String getAgencyAnalystName() {
        return agencyAnalystName;
    }

    public void setAgencyAnalystName(String agencyAnalystName) {
        this.agencyAnalystName = agencyAnalystName;
    }

    @Override
    public String getAnalystTelephoneNumber() {
        return analystTelephoneNumber;
    }

    public void setAnalystTelephoneNumber(String analystTelephoneNumber) {
        this.analystTelephoneNumber = analystTelephoneNumber;
    }

    @Override
    public String getPreferredBillingFrequency() {
        return preferredBillingFrequency;
    }

    public void setPreferredBillingFrequency(String preferredBillingFrequency) {
        this.preferredBillingFrequency = preferredBillingFrequency;
    }

    @Override
    public String getPreferredReportTemplate() {
        return preferredReportTemplate;
    }

    public void setPreferredReportTemplate(String preferredReportTemplate) {
        this.preferredReportTemplate = preferredReportTemplate;
    }

    @Override
    public String getPreferredReportFrequency() {
        return preferredReportFrequency;
    }

    public void setPreferredReportFrequency(String preferredReportFrequency) {
        this.preferredReportFrequency = preferredReportFrequency;
    }

    @Override
    public String getAwardProjectTitle() {
        return awardProjectTitle;
    }

    public void setAwardProjectTitle(String awardProjectTitle) {
        this.awardProjectTitle = awardProjectTitle;
    }

    @Override
    public String getAwardCommentText() {
        return awardCommentText;
    }

    public void setAwardCommentText(String awardCommentText) {
        this.awardCommentText = awardCommentText;
    }

    @Override
    public String getAwardPurposeCode() {
        return awardPurposeCode;
    }

    public void setAwardPurposeCode(String awardPurposeCode) {
        this.awardPurposeCode = awardPurposeCode;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getKimGroupNames() {
        return kimGroupNames;
    }

    public void setKimGroupNames(String kimGroupNames) {
        this.kimGroupNames = kimGroupNames;
    }

    @Override
    public String getRoutingOrg() {
        return routingOrg;
    }

    public void setRoutingOrg(String routingOrg) {
        this.routingOrg = routingOrg;
    }

    @Override
    public String getRoutingChart() {
        return routingChart;
    }

    public void setRoutingChart(String routingChart) {
        this.routingChart = routingChart;
    }

    @Override
    public boolean isAdditionalFormsRequiredIndicator() {
        return additionalFormsRequiredIndicator;
    }

    public void setAdditionalFormsRequiredIndicator(boolean additionalFormsRequiredIndicator) {
        this.additionalFormsRequiredIndicator = additionalFormsRequiredIndicator;
    }

    @Override
    public String getAdditionalFormsDescription() {
        return additionalFormsDescription;
    }

    public void setAdditionalFormsDescription(String additionalFormsDescription) {
        this.additionalFormsDescription = additionalFormsDescription;
    }

    @Override
    public String getSuspensionReason() {
        return suspensionReason;
    }

    public void setSuspensionReason(String suspensionReason) {
        this.suspensionReason = suspensionReason;
    }

    @Override
    public String getContractGrantType() {
        return contractGrantType;
    }

    public void setContractGrantType(String contractGrantType) {
        this.contractGrantType = contractGrantType;
    }

    @Override
    public String getInvoicingOptions() {
        return invoicingOptions;
    }

    public void setInvoicingOptions(String invoicingOptions) {
        this.invoicingOptions = invoicingOptions;
    }

    @Override
    public KualiDecimal getMinInvoiceAmount() {
        return minInvoiceAmount;
    }

    public void setMinInvoiceAmount(KualiDecimal minInvoiceAmount) {
        this.minInvoiceAmount = minInvoiceAmount;
    }

    public boolean isAutoApproveIndicator() {
        return autoApproveIndicator;
    }
    @Override
    public boolean getAutoApproveIndicator() {
        return autoApproveIndicator;
    }

    public void setAutoApproveIndicator(boolean autoApproveIndicator) {
        this.autoApproveIndicator = autoApproveIndicator;
    }

    @Override
    public String getLookupPersonUniversalIdentifier() {
        return lookupPersonUniversalIdentifier;
    }

    public void setLookupPersonUniversalIdentifier(String lookupPersonUniversalIdentifier) {
        this.lookupPersonUniversalIdentifier = lookupPersonUniversalIdentifier;
    }

    @Override
    public Person getLookupPerson() {
        return lookupPerson;
    }

    public void setLookupPerson(Person lookupPerson) {
        this.lookupPerson = lookupPerson;
    }

    @Override
    public String getLookupFundMgrPersonUniversalIdentifier() {
        return lookupFundMgrPersonUniversalIdentifier;
    }

    public void setLookupFundMgrPersonUniversalIdentifier(String lookupFundMgrPersonUniversalIdentifier) {
        this.lookupFundMgrPersonUniversalIdentifier = lookupFundMgrPersonUniversalIdentifier;
    }

    @Override
    public Person getLookupFundMgrPerson() {
        return lookupFundMgrPerson;
    }

    public void setLookupFundMgrPerson(Person lookupFundMgrPerson) {
        this.lookupFundMgrPerson = lookupFundMgrPerson;
    }

    @Override
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    public void setUserLookupRoleNamespaceCode(String userLookupRoleNamespaceCode) {
        this.userLookupRoleNamespaceCode = userLookupRoleNamespaceCode;
    }

    @Override
    public ContractsAndGrantsLetterOfCreditFund getLetterOfCreditFund() {
        return letterOfCreditFund;
    }

    public void setLetterOfCreditFund(ContractsAndGrantsLetterOfCreditFund letterOfCreditFund) {
        this.letterOfCreditFund = letterOfCreditFund;
    }

    @Override
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

    public void setUserLookupRoleName(String userLookupRoleName) {
        this.userLookupRoleName = userLookupRoleName;
    }

    @Override
    public AwardFundManager getAwardPrimaryFundManager() {
        return awardPrimaryFundManager;
    }

    public void setAwardPrimaryFundManager(AwardFundManager awardPrimaryFundManager) {
        this.awardPrimaryFundManager = awardPrimaryFundManager;
    }

    @Override
    public ContractsAndGrantsBillingFrequency getBillingFrequency() {
        return billingFrequency;
    }

    public void setBillingFrequency(ContractsAndGrantsBillingFrequency billingFrequency) {
        this.billingFrequency = billingFrequency;
    }

    @Override
    public ContractsAndGrantsProjectDirector getAwardPrimaryProjectDirector() {
        return awardPrimaryProjectDirector;
    }

    public void setAwardPrimaryProjectDirector(ContractsAndGrantsProjectDirector awardPrimaryProjectDirector) {
        this.awardPrimaryProjectDirector = awardPrimaryProjectDirector;
    }

    @Override
    public ContractsAndGrantsOrganization getPrimaryAwardOrganization() {
        return primaryAwardOrganization;
    }

    public void setPrimaryAwardOrganization(ContractsAndGrantsOrganization primaryAwardOrganization) {
        this.primaryAwardOrganization = primaryAwardOrganization;
    }

    @Override
    public Date getFundingExpirationDate() {
        return fundingExpirationDate;
    }

    public void setFundingExpirationDate(Date fundingExpirationDate) {
        this.fundingExpirationDate = fundingExpirationDate;
    }

    @Override
    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    @Override
    public String getDunningCampaign() {
        return dunningCampaign;
    }

    public void setDunningCampaign(String dunningCampaign) {
        this.dunningCampaign = dunningCampaign;
    }

    @Override
    public boolean isStopWorkIndicator() {
        return stopWorkIndicator;
    }

    public void setStopWorkIndicator(boolean stopWorkIndicator) {
        this.stopWorkIndicator = stopWorkIndicator;
    }

    @Override
    public List<ContractsAndGrantsBillingAwardAccount> getActiveAwardAccounts() {
        return activeAwardAccounts;
    }

    public void setActiveAwardAccounts(List<ContractsAndGrantsBillingAwardAccount> activeAwardAccounts) {
        this.activeAwardAccounts = activeAwardAccounts;
    }

    @Override
    public boolean isStateTransferIndicator() {
        return stateTransferIndicator;
    }

    public void setStateTransferIndicator(boolean stateTransferIndicator) {
        this.stateTransferIndicator = stateTransferIndicator;
    }

    @Override
    public boolean isSuspendInvoicingIndicator() {
        return suspendInvoicingIndicator;
    }

    public void setSuspendInvoicingIndicator(boolean suspendInvoicingIndicator) {
        this.suspendInvoicingIndicator = suspendInvoicingIndicator;
    }

}

