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
     * Gets the billingFrequencyCode attribute.
     *
     * @return Returns the billingFrequencyCode.
     */
    public String getBillingFrequencyCode();

    /**
     * Gets the awardProjectTitle attribute.
     *
     * @return Returns the awardProjectTitle.
     */
    public String getAwardProjectTitle();

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
     * Gets the excludedFromInvoicing attribute.
     *
     * @return Returns the excludedFromInvoicing.
     */
    public boolean isExcludedFromInvoicing();

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
     * Gets the excludedFromInvoicingReason attribute.
     *
     * @return Returns the excludedFromInvoicingReason.
     */
    public String getExcludedFromInvoicingReason();

    /**
     * Gets the instrumentTypeCode attribute.
     *
     * @return Returns the instrumentTypeCode.
     */
    public String getInstrumentTypeCode();

    /**
     * Gets the invoicingOptionCode attribute.
     *
     * @return Returns the invoicingOptionCode.
     */
    public String getInvoicingOptionCode();

    /**
     * Returns the module specific description for the invoicing option selected.
     * This will likely be different from CG and KC.
     *
     * @return
     */
    public String getInvoicingOptionDescription();

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
     * Sets the letterOfCreditFund attribute.
     *
     * We normally wouldn't put a setter in an interface, but we are struggling with an NPE
     * when doing an Award inquiry related to the fact that the code can't find a setter
     * for this attribute.
     *
     */
    public void setLetterOfCreditFund(ContractsAndGrantsLetterOfCreditFund letterOfCreditFund);

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
     * Gets the dunningCampaign attribute.
     *
     * @return Returns the dunningCampaign.
     */
    public String getDunningCampaign();

    /**
     * Gets stopWork indicator.
     *
     * @return Returns the stopWork indicator.
     */
    public boolean isStopWorkIndicator();

    /**
     * Gets stop work reason.
     *
     * @return Returns the stop work reason.
     */
    public String getStopWorkReason();
}
