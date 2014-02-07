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
package org.kuali.kfs.integration.cg;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Integration interface for Award (specific to CGB functionality)
 */
public interface ContractsAndGrantsBillingAward extends ContractsAndGrantsAward {

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    @Override
    public Long getProposalNumber();

    /**
     * Gets the proposal attribute.
     *
     * @return Returns the proposal object.
     */
    @Override
    public ContractAndGrantsProposal getProposal();

    /**
     * Gets the awardInquiryTitle attribute.
     *
     * @return Returns the awardInquiryTitle.
     */
    @Override
    public String getAwardInquiryTitle();

    /**
     * Gets the awardBeginningDate attribute.
     *
     * @return Returns the awardBeginningDate.
     */
    public Date getAwardBeginningDate();

    /**
     * Gets the awardEndingDate attribute.
     *
     * @return Returns the awardEndingDate.
     */
    public Date getAwardEndingDate();

    /**
     * Gets the lastBilledDate attribute.
     *
     * @return Returns the lastBilledDate.
     */
    public Date getLastBilledDate();

    /**
     * Gets the awardTotalAmount attribute.
     *
     * @return Returns the awardTotalAmount.
     */
    public KualiDecimal getAwardTotalAmount();

    /**
     * Gets the awardAddendumNumber attribute.
     *
     * @return Returns the awardAddendumNumber.
     */
    public String getAwardAddendumNumber();

    /**
     * Gets the awardAllocatedUniversityComputingServicesAmount attribute.
     *
     * @return Returns the awardAllocatedUniversityComputingServicesAmount.
     */
    public KualiDecimal getAwardAllocatedUniversityComputingServicesAmount();

    /**
     * Gets the federalPassThroughFundedAmount attribute.
     *
     * @return Returns the federalPassThroughFundedAmount.
     */
    public KualiDecimal getFederalPassThroughFundedAmount();

    /**
     * Gets the awardEntryDate attribute.
     *
     * @return Returns the awardEntryDate.
     */
    public Date getAwardEntryDate();

    /**
     * Gets the agencyFuture1Amount attribute.
     *
     * @return Returns the agencyFuture1Amount.
     */
    public KualiDecimal getAgencyFuture1Amount();

    /**
     * Gets the agencyFuture2Amount attribute.
     *
     * @return Returns the agencyFuture2Amount.
     */
    public KualiDecimal getAgencyFuture2Amount();

    /**
     * Gets the agencyFuture3Amount attribute.
     *
     * @return Returns the agencyFuture3Amount.
     */
    public KualiDecimal getAgencyFuture3Amount();

    /**
     * Gets the awardDocumentNumber attribute.
     *
     * @return Returns the awardDocumentNumber.
     */
    public String getAwardDocumentNumber();

    /**
     * Gets the awardLastUpdateDate attribute.
     *
     * @return Returns the awardLastUpdateDate.
     */
    public Timestamp getAwardLastUpdateDate();

    /**
     * Gets the federalPassThroughIndicator attribute.
     *
     * @return Returns the federalPassThroughIndicator.
     */
    public boolean getFederalPassThroughIndicator();

    /**
     * Gets the oldProposalNumber attribute.
     *
     * @return Returns the oldProposalNumber.
     */
    public String getOldProposalNumber();

    /**
     * Gets the awardDirectCostAmount attribute.
     *
     * @return Returns the awardDirectCostAmount.
     */
    public KualiDecimal getAwardDirectCostAmount();

    /**
     * Gets the awardIndirectCostAmount attribute.
     *
     * @return Returns the awardIndirectCostAmount.
     */
    public KualiDecimal getAwardIndirectCostAmount();

    /**
     * Gets the federalFundedAmount attribute.
     *
     * @return Returns the federalFundedAmount.
     */
    public KualiDecimal getFederalFundedAmount();

    /**
     * Gets the awardCreateTimestamp attribute.
     *
     * @return Returns the awardCreateTimestamp.
     */
    public Timestamp getAwardCreateTimestamp();

    /**
     * Gets the awardClosingDate attribute.
     *
     * @return Returns the awardClosingDate.
     */
    public Date getAwardClosingDate();

    /**
     * Gets the proposalAwardTypeCode attribute.
     *
     * @return Returns the proposalAwardTypeCode.
     */
    public String getProposalAwardTypeCode();

    /**
     * Gets the awardStatusCode attribute.
     *
     * @return Returns the awardStatusCode.
     */
    public String getAwardStatusCode();

    /**
     * Gets the letterOfCreditFundCode attribute.
     *
     * @return Returns the letterOfCreditFundCode.
     */
    public String getLetterOfCreditFundCode();

    /**
     * Gets the grantDescriptionCode attribute.
     *
     * @return Returns the grantDescriptionCode.
     */
    public String getGrantDescriptionCode();

    /**
     * Gets the agencyNumber attribute.
     *
     * @return Returns the agencyNumber.
     */
    public String getAgencyNumber();

    /**
     * Gets the letterOfCreditCreationType attribute.
     *
     * @return Returns the letterOfCreditCreationType.
     */
    public String getLetterOfCreditCreationType();

    /**
     * Gets the federalPassThroughAgencyNumber attribute.
     *
     * @return Returns the federalPassThroughAgencyNumber.
     */
    public String getFederalPassThroughAgencyNumber();

    /**
     * Gets the agencyAnalystName attribute.
     *
     * @return Returns the agencyAnalystName.
     */
    public String getAgencyAnalystName();

    /**
     * Gets the analystTelephoneNumber attribute.
     *
     * @return Returns the analystTelephoneNumber.
     */
    public String getAnalystTelephoneNumber();

    /**
     * Gets the preferredBillingFrequency attribute.
     *
     * @return Returns the preferredBillingFrequency.
     */
    public String getPreferredBillingFrequency();

    /**
     * Gets the preferredReportTemplate attribute.
     *
     * @return Returns the preferredReportTemplate.
     */
    public String getPreferredReportTemplate();

    /**
     * Gets the preferredReportFrequency attribute.
     *
     * @return Returns the preferredReportFrequency.
     */
    public String getPreferredReportFrequency();

    /**
     * Gets the awardProjectTitle attribute.
     *
     * @return Returns the awardProjectTitle.
     */
    public String getAwardProjectTitle();

    /**
     * Gets the awardCommentText attribute.
     *
     * @return Returns the awardCommentText.
     */
    public String getAwardCommentText();

    /**
     * Gets the awardPurposeCode attribute.
     *
     * @return Returns the awardPurposeCode.
     */
    public String getAwardPurposeCode();

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    public boolean isActive();

    /**
     * Gets the kimGroupNames attribute.
     *
     * @return Returns the kimGroupNames.
     */
    public String getKimGroupNames();

    /**
     * Gets the list of active award accounts.
     *
     * @return Returns the active awardAccounts.
     */
    public List<ContractsAndGrantsBillingAwardAccount> getActiveAwardAccounts();

    /**
     * Gets the agency attribute.
     *
     * @return Returns the agency.
     */
    public ContractsAndGrantsBillingAgency getAgency();

    /**
     * Gets the routingOrg attribute.
     *
     * @return Returns the routingOrg.
     */
    public String getRoutingOrg();

    /**
     * Gets the routingChart attribute.
     *
     * @return Returns the routingChart.
     */
    public String getRoutingChart();

    /**
     * Gets the stateTransfer attribute.
     *
     * @return Returns the stateTransfer.
     */
    public boolean isStateTransferIndicator();

    /**
     * Gets the suspendInvoicing attribute.
     *
     * @return Returns the suspendInvoicing.
     */
    public boolean isSuspendInvoicingIndicator();

    /**
     * Gets the additionalFormsRequired attribute.
     *
     * @return Returns the additionalFormsRequired.
     */
    public boolean isAdditionalFormsRequiredIndicator();

    /**
     * Gets the additionalFormsDescription attribute.
     *
     * @return Returns the additionalFormsDescription.
     */
    public String getAdditionalFormsDescription();

    /**
     * Gets the suspensionReason attribute.
     *
     * @return Returns the suspensionReason.
     */
    public String getSuspensionReason();

    /**
     * Gets the contractGrantType attribute.
     *
     * @return Returns the contractGrantType.
     */
    public String getContractGrantType();

    /**
     * Gets the invoicingOptions attribute.
     *
     * @return Returns the invoicingOptions.
     */
    public String getInvoicingOptions();

    /**
     * Gets the minInvoiceAmount attribute.
     *
     * @return Returns the minInvoiceAmount.
     */
    public KualiDecimal getMinInvoiceAmount();

    /**
     * Gets the autoApprove attribute.
     *
     * @return Returns the autoApprove.
     */
    public boolean getAutoApproveIndicator();

    /**
     * Gets the lookupPersonUniversalIdentifier attribute.
     *
     * @return Returns the lookupPersonUniversalIdentifier.
     */
    public String getLookupPersonUniversalIdentifier();

    /**
     * Gets the lookupPerson attribute.
     *
     * @return Returns the lookupPerson.
     */
    public Person getLookupPerson();

    /**
     * Gets the lookupFundMgrPersonUniversalIdentifier attribute.
     *
     * @return Returns the lookupFundMgrPersonUniversalIdentifier.
     */
    public String getLookupFundMgrPersonUniversalIdentifier();

    /**
     * Gets the lookupFundMgrPerson attribute.
     *
     * @return Returns the lookupFundMgrPerson.
     */
    public Person getLookupFundMgrPerson();

    /**
     * Gets the userLookupRoleNamespaceCode attribute.
     *
     * @return Returns the userLookupRoleNamespaceCode.
     */
    public String getUserLookupRoleNamespaceCode();

    /**
     * Gets the letterOfCreditFund attribute.
     *
     * @return Returns the letterOfCreditFund.
     */
    public ContractsAndGrantsLetterOfCreditFund getLetterOfCreditFund();

    /**
     * Gets the userLookupRoleName attribute.
     *
     * @return Returns the userLookupRoleName.
     */
    public String getUserLookupRoleName();

    /**
     * Gets the awardPrimaryFundManager attribute.
     *
     * @return Returns the awardPrimaryFundManager.
     */
    public ContractsAndGrantsFundManager getAwardPrimaryFundManager();

    /**
     * Gets the billingFrequency attribute.
     *
     * @return Returns the billingFrequency.
     */
    public ContractsAndGrantsBillingFrequency getBillingFrequency();

    /**
     * Gets the awardPrimaryProjectDirector attribute.
     *
     * @return Returns the awardPrimaryProjectDirector.
     */
    public ContractsAndGrantsProjectDirector getAwardPrimaryProjectDirector();

    /**
     * Gets the primaryAwardOrganization attribute.
     *
     * @return Returns the primaryAwardOrganization.
     */
    public ContractsAndGrantsOrganization getPrimaryAwardOrganization();

    /**
     * Gets the fundingExpirationDate attribute.
     *
     * @return Returns the fundingExpirationDate.
     */
    public Date getFundingExpirationDate();

    /**
     * Gets the commentText attribute.
     *
     * @return Returns the commentText.
     */
    public String getCommentText();

    /**
     * Gets the activeAwardInvoiceAccounts attribute.
     *
     * @return Returns the activeAwardInvoiceAccounts.
     */
    public List<ContractsGrantsAwardInvoiceAccountInformation> getActiveAwardInvoiceAccounts();

    /**
     * Gets the dunningCampaign attribute.
     *
     * @return Returns the dunningCampaign.
     */
    public String getDunningCampaign();

    /**
     * Gets stopWork attribute.
     *
     * @return Returns the stopWork.
     */
    public boolean isStopWorkIndicator();
}
