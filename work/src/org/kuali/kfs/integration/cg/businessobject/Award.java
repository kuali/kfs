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

package org.kuali.kfs.integration.cg.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractAndGrantsProposal;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingFrequency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager;
import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFund;
import org.kuali.kfs.integration.cg.ContractsAndGrantsOrganization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsProjectDirector;
import org.kuali.kfs.integration.cg.ContractsGrantsAwardInvoiceAccountInformation;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a financial award object.
 */
public class Award implements ContractsAndGrantsCGBAward, ContractsAndGrantsAward {
    private static final String AWARD_INQUIRY_TITLE_PROPERTY = "message.inquiry.award.title";
    private Long proposalNumber;
    private String awardId;
    private Date awardBeginningDate;
    private Date awardEndingDate;
    private Date lastBilledDate;

    /**
     * This field is for write-only to the database via OJB, not the corresponding property of this BO. OJB uses reflection to read
     * it, so the compiler warns because it doesn't know.
     *
     * @see #getAwardTotalAmount
     * @see #setAwardTotalAmount
     */
    @SuppressWarnings("unused")
    protected KualiDecimal awardTotalAmount;

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
    private String agencyNumber;
    private String letterOfCreditCreationType; // To create LOC
    private String federalPassThroughAgencyNumber;
    private String agencyAnalystName;
    private String analystTelephoneNumber;

    private String preferredBillingFrequency;
    private ContractsAndGrantsBillingFrequency billingFrequency;
    private String preferredReportTemplate;

    // private String preferredBillingFormat;
    private String preferredReportFrequency;


    private String awardProjectTitle;
    private String awardCommentText;
    private String awardPurposeCode;
    private boolean active;
    private String kimGroupNames;
    private ContractsAndGrantsFundManager awardPrimaryFundManager;


    private ContractAndGrantsProposal proposal;


    private ContractsAndGrantsLetterOfCreditFund letterOfCreditFund;

    private ContractsAndGrantsCGBAgency agency;
    private ContractsAndGrantsCGBAgency federalPassThroughAgency;

    private ContractsAndGrantsProjectDirector awardPrimaryProjectDirector;
    private ContractsAndGrantsOrganization primaryAwardOrganization;
    private String routingOrg;
    private String routingChart;

    private boolean stateTransferIndicator;
    private boolean suspendInvoicingIndicator;
    private boolean additionalFormsRequiredIndicator;
    private String additionalFormsDescription;
    private String suspensionReason;
    private String contractGrantType;

    private String invoicingOptions;

    private KualiDecimal minInvoiceAmount = KualiDecimal.ZERO;

    private boolean autoApproveIndicator;


    private Date fundingExpirationDate;
    private String drawNumber;
    private String commentText;
    private boolean stopWorkIndicator;
    private String dunningCampaign;


    /** Dummy value used to facilitate lookups */
    private transient String lookupPersonUniversalIdentifier;
    private transient Person lookupPerson;
    private final String userLookupRoleNamespaceCode = KFSConstants.ParameterNamespaces.KFS;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.CONTRACTS_AND_GRANTS_PROJECT_DIRECTOR;

    private transient String lookupFundMgrPersonUniversalIdentifier;
    private transient Person lookupFundMgrPerson;

    /**
     * Default no-args constructor.
     */
    public Award() {
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
     * Gets the awardInquiryTitle attribute.
     *
     * @return a String to represent this field on the inquiry
     */
    @Override
    public String getAwardInquiryTitle() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(AWARD_INQUIRY_TITLE_PROPERTY);
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward#getProposal()
     */
    @Override
    public ContractAndGrantsProposal getProposal() {
        return null;
    }

    /**
     * Gets the awardId attribute.
     *
     * @return Returns the awardId.
     */
    @Override
    public String getAwardId() {
        return awardId;
    }

    /**
     * Sets the awardId attribute value.
     *
     * @param awardId The awardId to set.
     */
    public void setAwardId(String awardId) {
        this.awardId = awardId;
    }

    /**
     * Gets the awardBeginningDate attribute.
     *
     * @return Returns the awardBeginningDate.
     */
    @Override
    public Date getAwardBeginningDate() {
        return awardBeginningDate;
    }

    /**
     * Sets the awardBeginningDate attribute value.
     *
     * @param awardBeginningDate The awardBeginningDate to set.
     */
    public void setAwardBeginningDate(Date awardBeginningDate) {
        this.awardBeginningDate = awardBeginningDate;
    }

    /**
     * Gets the awardEndingDate attribute.
     *
     * @return Returns the awardEndingDate.
     */
    @Override
    public Date getAwardEndingDate() {
        return awardEndingDate;
    }

    /**
     * Sets the awardEndingDate attribute value.
     *
     * @param awardEndingDate The awardEndingDate to set.
     */
    public void setAwardEndingDate(Date awardEndingDate) {
        this.awardEndingDate = awardEndingDate;
    }

    /**
     * Gets the lastBilledDate attribute.
     *
     * @return Returns the lastBilledDate.
     */
    @Override
    public Date getLastBilledDate() {
        return lastBilledDate;
    }

    /**
     * Sets the lastBilledDate attribute value.
     *
     * @param lastBilledDate The lastBilledDate to set.
     */
    public void setLastBilledDate(Date lastBilledDate) {
        this.lastBilledDate = lastBilledDate;
    }

    /**
     * Gets the awardTotalAmount attribute.
     *
     * @return Returns the awardTotalAmount.
     */
    @Override
    public KualiDecimal getAwardTotalAmount() {
        KualiDecimal direct = getAwardDirectCostAmount();
        KualiDecimal indirect = getAwardIndirectCostAmount();
        return ObjectUtils.isNull(direct) || ObjectUtils.isNull(indirect) ? null : direct.add(indirect);
    }

    /**
     * Sets the awardTotalAmount attribute value.
     *
     * @param awardTotalAmount The awardTotalAmount to set.
     */
    public void setAwardTotalAmount(KualiDecimal awardTotalAmount) {
        this.awardTotalAmount = awardTotalAmount;
    }

    /**
     * Gets the awardAddendumNumber attribute.
     *
     * @return Returns the awardAddendumNumber.
     */
    @Override
    public String getAwardAddendumNumber() {
        return awardAddendumNumber;
    }

    /**
     * Sets the awardAddendumNumber attribute value.
     *
     * @param awardAddendumNumber The awardAddendumNumber to set.
     */
    public void setAwardAddendumNumber(String awardAddendumNumber) {
        this.awardAddendumNumber = awardAddendumNumber;
    }

    /**
     * Gets the awardAllocatedUniversityComputingServicesAmount attribute.
     *
     * @return Returns the awardAllocatedUniversityComputingServicesAmount.
     */
    @Override
    public KualiDecimal getAwardAllocatedUniversityComputingServicesAmount() {
        return awardAllocatedUniversityComputingServicesAmount;
    }


    /**
     * Sets the awardAllocatedUniversityComputingServicesAmount attribute value.
     *
     * @param awardAllocatedUniversityComputingServicesAmount The awardAllocatedUniversityComputingServicesAmount to set.
     */
    public void setAwardAllocatedUniversityComputingServicesAmount(KualiDecimal awardAllocatedUniversityComputingServicesAmount) {
        this.awardAllocatedUniversityComputingServicesAmount = awardAllocatedUniversityComputingServicesAmount;
    }


    /**
     * Gets the federalPassThroughFundedAmount attribute.
     *
     * @return Returns the federalPassThroughFundedAmount.
     */
    @Override
    public KualiDecimal getFederalPassThroughFundedAmount() {
        return federalPassThroughFundedAmount;
    }


    /**
     * Sets the federalPassThroughFundedAmount attribute value.
     *
     * @param federalPassThroughFundedAmount The federalPassThroughFundedAmount to set.
     */
    public void setFederalPassThroughFundedAmount(KualiDecimal federalPassThroughFundedAmount) {
        this.federalPassThroughFundedAmount = federalPassThroughFundedAmount;
    }


    /**
     * Gets the awardEntryDate attribute.
     *
     * @return Returns the awardEntryDate.
     */
    @Override
    public Date getAwardEntryDate() {
        return awardEntryDate;
    }


    /**
     * Sets the awardEntryDate attribute value.
     *
     * @param awardEntryDate The awardEntryDate to set.
     */
    public void setAwardEntryDate(Date awardEntryDate) {
        this.awardEntryDate = awardEntryDate;
    }


    /**
     * Gets the agencyFuture1Amount attribute.
     *
     * @return Returns the agencyFuture1Amount.
     */
    @Override
    public KualiDecimal getAgencyFuture1Amount() {
        return agencyFuture1Amount;
    }


    /**
     * Sets the agencyFuture1Amount attribute value.
     *
     * @param agencyFuture1Amount The agencyFuture1Amount to set.
     */
    public void setAgencyFuture1Amount(KualiDecimal agencyFuture1Amount) {
        this.agencyFuture1Amount = agencyFuture1Amount;
    }


    /**
     * Gets the agencyFuture2Amount attribute.
     *
     * @return Returns the agencyFuture2Amount.
     */
    @Override
    public KualiDecimal getAgencyFuture2Amount() {
        return agencyFuture2Amount;
    }


    /**
     * Sets the agencyFuture2Amount attribute value.
     *
     * @param agencyFuture2Amount The agencyFuture2Amount to set.
     */
    public void setAgencyFuture2Amount(KualiDecimal agencyFuture2Amount) {
        this.agencyFuture2Amount = agencyFuture2Amount;
    }


    /**
     * Gets the agencyFuture3Amount attribute.
     *
     * @return Returns the agencyFuture3Amount.
     */
    @Override
    public KualiDecimal getAgencyFuture3Amount() {
        return agencyFuture3Amount;
    }


    /**
     * Sets the agencyFuture3Amount attribute value.
     *
     * @param agencyFuture3Amount The agencyFuture3Amount to set.
     */
    public void setAgencyFuture3Amount(KualiDecimal agencyFuture3Amount) {
        this.agencyFuture3Amount = agencyFuture3Amount;
    }


    /**
     * Gets the awardDocumentNumber attribute.
     *
     * @return Returns the awardDocumentNumber.
     */
    @Override
    public String getAwardDocumentNumber() {
        return awardDocumentNumber;
    }


    /**
     * Sets the awardDocumentNumber attribute value.
     *
     * @param awardDocumentNumber The awardDocumentNumber to set.
     */
    public void setAwardDocumentNumber(String awardDocumentNumber) {
        this.awardDocumentNumber = awardDocumentNumber;
    }


    /**
     * Gets the awardLastUpdateDate attribute.
     *
     * @return Returns the awardLastUpdateDate.
     */
    @Override
    public Timestamp getAwardLastUpdateDate() {
        return awardLastUpdateDate;
    }


    /**
     * Sets the awardLastUpdateDate attribute value.
     *
     * @param awardLastUpdateDate The awardLastUpdateDate to set.
     */
    public void setAwardLastUpdateDate(Timestamp awardLastUpdateDate) {
        this.awardLastUpdateDate = awardLastUpdateDate;
    }


    /**
     * Gets the federalPassThroughIndicator attribute.
     *
     * @return Returns the federalPassThroughIndicator.
     */
    @Override
    public boolean getFederalPassThroughIndicator() {
        return federalPassThroughIndicator;
    }


    /**
     * Sets the federalPassThroughIndicator attribute value.
     *
     * @param federalPassThroughIndicator The federalPassThroughIndicator to set.
     */
    public void setFederalPassThroughIndicator(boolean federalPassThroughIndicator) {
        this.federalPassThroughIndicator = federalPassThroughIndicator;
    }


    /**
     * Gets the oldProposalNumber attribute.
     *
     * @return Returns the oldProposalNumber.
     */
    @Override
    public String getOldProposalNumber() {
        return oldProposalNumber;
    }


    /**
     * Sets the oldProposalNumber attribute value.
     *
     * @param oldProposalNumber The oldProposalNumber to set.
     */
    public void setOldProposalNumber(String oldProposalNumber) {
        this.oldProposalNumber = oldProposalNumber;
    }


    /**
     * Gets the awardDirectCostAmount attribute.
     *
     * @return Returns the awardDirectCostAmount.
     */
    @Override
    public KualiDecimal getAwardDirectCostAmount() {
        return awardDirectCostAmount;
    }


    /**
     * Sets the awardDirectCostAmount attribute value.
     *
     * @param awardDirectCostAmount The awardDirectCostAmount to set.
     */
    public void setAwardDirectCostAmount(KualiDecimal awardDirectCostAmount) {
        this.awardDirectCostAmount = awardDirectCostAmount;
    }


    /**
     * Gets the awardIndirectCostAmount attribute.
     *
     * @return Returns the awardIndirectCostAmount.
     */
    @Override
    public KualiDecimal getAwardIndirectCostAmount() {
        return awardIndirectCostAmount;
    }


    /**
     * Sets the awardIndirectCostAmount attribute value.
     *
     * @param awardIndirectCostAmount The awardIndirectCostAmount to set.
     */
    public void setAwardIndirectCostAmount(KualiDecimal awardIndirectCostAmount) {
        this.awardIndirectCostAmount = awardIndirectCostAmount;
    }


    /**
     * Gets the federalFundedAmount attribute.
     *
     * @return Returns the federalFundedAmount.
     */
    @Override
    public KualiDecimal getFederalFundedAmount() {
        return federalFundedAmount;
    }


    /**
     * Sets the federalFundedAmount attribute value.
     *
     * @param federalFundedAmount The federalFundedAmount to set.
     */
    public void setFederalFundedAmount(KualiDecimal federalFundedAmount) {
        this.federalFundedAmount = federalFundedAmount;
    }


    /**
     * Gets the awardCreateTimestamp attribute.
     *
     * @return Returns the awardCreateTimestamp.
     */
    @Override
    public Timestamp getAwardCreateTimestamp() {
        return awardCreateTimestamp;
    }


    /**
     * Sets the awardCreateTimestamp attribute value.
     *
     * @param awardCreateTimestamp The awardCreateTimestamp to set.
     */
    public void setAwardCreateTimestamp(Timestamp awardCreateTimestamp) {
        this.awardCreateTimestamp = awardCreateTimestamp;
    }


    /**
     * Gets the awardClosingDate attribute.
     *
     * @return Returns the awardClosingDate.
     */
    @Override
    public Date getAwardClosingDate() {
        return awardClosingDate;
    }


    /**
     * Sets the awardClosingDate attribute value.
     *
     * @param awardClosingDate The awardClosingDate to set.
     */
    public void setAwardClosingDate(Date awardClosingDate) {
        this.awardClosingDate = awardClosingDate;
    }


    /**
     * Gets the proposalAwardTypeCode attribute.
     *
     * @return Returns the proposalAwardTypeCode.
     */
    @Override
    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }


    /**
     * Sets the proposalAwardTypeCode attribute value.
     *
     * @param proposalAwardTypeCode The proposalAwardTypeCode to set.
     */
    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }


    /**
     * Gets the awardStatusCode attribute.
     *
     * @return Returns the awardStatusCode.
     */
    @Override
    public String getAwardStatusCode() {
        return awardStatusCode;
    }


    /**
     * Sets the awardStatusCode attribute value.
     *
     * @param awardStatusCode The awardStatusCode to set.
     */
    public void setAwardStatusCode(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }


    /**
     * Gets the letterOfCreditFundCode attribute.
     *
     * @return Returns the letterOfCreditFundCode.
     */
    @Override
    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }


    /**
     * Sets the letterOfCreditFundCode attribute value.
     *
     * @param letterOfCreditFundCode The letterOfCreditFundCode to set.
     */
    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }


    /**
     * Gets the grantDescriptionCode attribute.
     *
     * @return Returns the grantDescriptionCode.
     */
    @Override
    public String getGrantDescriptionCode() {
        return grantDescriptionCode;
    }


    /**
     * Sets the grantDescriptionCode attribute value.
     *
     * @param grantDescriptionCode The grantDescriptionCode to set.
     */
    public void setGrantDescriptionCode(String grantDescriptionCode) {
        this.grantDescriptionCode = grantDescriptionCode;
    }


    /**
     * Gets the agencyNumber attribute.
     *
     * @return Returns the agencyNumber.
     */
    @Override
    public String getAgencyNumber() {
        return agencyNumber;
    }


    /**
     * Sets the agencyNumber attribute value.
     *
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }


    /**
     * Gets the letterOfCreditCreationType attribute.
     *
     * @return Returns the letterOfCreditCreationType.
     */
    @Override
    public String getLetterOfCreditCreationType() {
        return letterOfCreditCreationType;
    }


    /**
     * Sets the letterOfCreditCreationType attribute value.
     *
     * @param letterOfCreditCreationType The letterOfCreditCreationType to set.
     */
    public void setLetterOfCreditCreationType(String letterOfCreditCreationType) {
        this.letterOfCreditCreationType = letterOfCreditCreationType;
    }


    /**
     * Gets the federalPassThroughAgencyNumber attribute.
     *
     * @return Returns the federalPassThroughAgencyNumber.
     */
    @Override
    public String getFederalPassThroughAgencyNumber() {
        return federalPassThroughAgencyNumber;
    }


    /**
     * Sets the federalPassThroughAgencyNumber attribute value.
     *
     * @param federalPassThroughAgencyNumber The federalPassThroughAgencyNumber to set.
     */
    public void setFederalPassThroughAgencyNumber(String federalPassThroughAgencyNumber) {
        this.federalPassThroughAgencyNumber = federalPassThroughAgencyNumber;
    }


    /**
     * Gets the agencyAnalystName attribute.
     *
     * @return Returns the agencyAnalystName.
     */
    @Override
    public String getAgencyAnalystName() {
        return agencyAnalystName;
    }


    /**
     * Sets the agencyAnalystName attribute value.
     *
     * @param agencyAnalystName The agencyAnalystName to set.
     */
    public void setAgencyAnalystName(String agencyAnalystName) {
        this.agencyAnalystName = agencyAnalystName;
    }


    /**
     * Gets the analystTelephoneNumber attribute.
     *
     * @return Returns the analystTelephoneNumber.
     */
    @Override
    public String getAnalystTelephoneNumber() {
        return analystTelephoneNumber;
    }


    /**
     * Sets the analystTelephoneNumber attribute value.
     *
     * @param analystTelephoneNumber The analystTelephoneNumber to set.
     */
    public void setAnalystTelephoneNumber(String analystTelephoneNumber) {
        this.analystTelephoneNumber = analystTelephoneNumber;
    }


    /**
     * Gets the preferredBillingFrequency attribute.
     *
     * @return Returns the preferredBillingFrequency.
     */
    @Override
    public String getPreferredBillingFrequency() {
        return preferredBillingFrequency;
    }


    /**
     * Sets the preferredBillingFrequency attribute value.
     *
     * @param preferredBillingFrequency The preferredBillingFrequency to set.
     */
    public void setPreferredBillingFrequency(String preferredBillingFrequency) {
        this.preferredBillingFrequency = preferredBillingFrequency;
    }


    /**
     * Gets the preferredReportTemplate attribute.
     *
     * @return Returns the preferredReportTemplate.
     */
    @Override
    public String getPreferredReportTemplate() {
        return preferredReportTemplate;
    }


    /**
     * Sets the preferredReportTemplate attribute value.
     *
     * @param preferredReportTemplate The preferredReportTemplate to set.
     */
    public void setPreferredReportTemplate(String preferredReportTemplate) {
        this.preferredReportTemplate = preferredReportTemplate;
    }


    /**
     * Gets the preferredReportFrequency attribute.
     *
     * @return Returns the preferredReportFrequency.
     */
    @Override
    public String getPreferredReportFrequency() {
        return preferredReportFrequency;
    }


    /**
     * Sets the preferredReportFrequency attribute value.
     *
     * @param preferredReportFrequency The preferredReportFrequency to set.
     */
    public void setPreferredReportFrequency(String preferredReportFrequency) {
        this.preferredReportFrequency = preferredReportFrequency;
    }


    /**
     * Gets the awardProjectTitle attribute.
     *
     * @return Returns the awardProjectTitle.
     */
    @Override
    public String getAwardProjectTitle() {
        return awardProjectTitle;
    }


    /**
     * Sets the awardProjectTitle attribute value.
     *
     * @param awardProjectTitle The awardProjectTitle to set.
     */
    public void setAwardProjectTitle(String awardProjectTitle) {
        this.awardProjectTitle = awardProjectTitle;
    }


    /**
     * Gets the awardCommentText attribute.
     *
     * @return Returns the awardCommentText.
     */
    @Override
    public String getAwardCommentText() {
        return awardCommentText;
    }


    /**
     * Sets the awardCommentText attribute value.
     *
     * @param awardCommentText The awardCommentText to set.
     */
    public void setAwardCommentText(String awardCommentText) {
        this.awardCommentText = awardCommentText;
    }


    /**
     * Gets the awardPurposeCode attribute.
     *
     * @return Returns the awardPurposeCode.
     */
    @Override
    public String getAwardPurposeCode() {
        return awardPurposeCode;
    }


    /**
     * Sets the awardPurposeCode attribute value.
     *
     * @param awardPurposeCode The awardPurposeCode to set.
     */
    public void setAwardPurposeCode(String awardPurposeCode) {
        this.awardPurposeCode = awardPurposeCode;
    }


    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the kimGroupNames attribute.
     *
     * @return Returns the kimGroupNames.
     */
    @Override
    public String getKimGroupNames() {
        return kimGroupNames;
    }


    /**
     * Sets the kimGroupNames attribute value.
     *
     * @param kimGroupNames The kimGroupNames to set.
     */
    public void setKimGroupNames(String kimGroupNames) {
        this.kimGroupNames = kimGroupNames;
    }


    /**
     * Gets the list of active award accounts. The integration object is used here - as this would be referred only from AR module.
     *
     * @return Returns the active awardAccounts.
     */
    @Override
    public List<ContractsAndGrantsCGBAwardAccount> getActiveAwardAccounts() {
        List<ContractsAndGrantsCGBAwardAccount> activeAwardAccounts = new ArrayList<ContractsAndGrantsCGBAwardAccount>();
        return activeAwardAccounts;
    }


    /**
     * Gets the agency attribute.
     *
     * @return Returns the agency.
     */
    @Override
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
     * Gets the routingOrg attribute.
     *
     * @return Returns the routingOrg.
     */
    @Override
    public String getRoutingOrg() {
        return routingOrg;
    }


    /**
     * Sets the routingOrg attribute value.
     *
     * @param routingOrg The routingOrg to set.
     */
    public void setRoutingOrg(String routingOrg) {
        this.routingOrg = routingOrg;
    }


    /**
     * Gets the routingChart attribute.
     *
     * @return Returns the routingChart.
     */
    @Override
    public String getRoutingChart() {
        return routingChart;
    }


    /**
     * Sets the routingChart attribute value.
     *
     * @param routingChart The routingChart to set.
     */
    public void setRoutingChart(String routingChart) {
        this.routingChart = routingChart;
    }


    /**
     * Gets the stateTransferIndicator attribute.
     *
     * @return Returns the stateTransferIndicator.
     */
    @Override
    public boolean isStateTransferIndicator() {
        return stateTransferIndicator;
    }


    /**
     * Sets the stateTransferIndicator attribute value.
     *
     * @param stateTransferIndicator The stateTransferIndicator to set.
     */
    public void setStateTransferIndicator(boolean stateTransferIndicator) {
        this.stateTransferIndicator = stateTransferIndicator;
    }


    /**
     * Gets the suspendInvoicingIndicator attribute.
     *
     * @return Returns the suspendInvoicingIndicator.
     */
    @Override
    public boolean isSuspendInvoicingIndicator() {
        return suspendInvoicingIndicator;
    }


    /**
     * Sets the suspendInvoicingIndicator attribute value.
     *
     * @param suspendInvoicingIndicator The suspendInvoicingIndicator to set.
     */
    public void setSuspendInvoicingIndicator(boolean suspendInvoicingIndicator) {
        this.suspendInvoicingIndicator = suspendInvoicingIndicator;
    }


    /**
     * Gets the additionalFormsRequiredIndicator attribute.
     *
     * @return Returns the additionalFormsRequiredIndicator.
     */
    @Override
    public boolean isAdditionalFormsRequiredIndicator() {
        return additionalFormsRequiredIndicator;
    }


    /**
     * Sets the additionalFormsRequiredIndicator attribute value.
     *
     * @param additionalFormsRequiredIndicator The additionalFormsRequiredIndicator to set.
     */
    public void setAdditionalFormsRequiredIndicator(boolean additionalFormsRequiredIndicator) {
        this.additionalFormsRequiredIndicator = additionalFormsRequiredIndicator;
    }


    /**
     * Gets the additionalFormsDescription attribute.
     *
     * @return Returns the additionalFormsDescription.
     */
    @Override
    public String getAdditionalFormsDescription() {
        return additionalFormsDescription;
    }


    /**
     * Sets the additionalFormsDescription attribute value.
     *
     * @param additionalFormsDescription The additionalFormsDescription to set.
     */
    public void setAdditionalFormsDescription(String additionalFormsDescription) {
        this.additionalFormsDescription = additionalFormsDescription;
    }


    /**
     * Gets the suspensionReason attribute.
     *
     * @return Returns the suspensionReason.
     */
    @Override
    public String getSuspensionReason() {
        return suspensionReason;
    }


    /**
     * Sets the suspensionReason attribute value.
     *
     * @param suspensionReason The suspensionReason to set.
     */
    public void setSuspensionReason(String suspensionReason) {
        this.suspensionReason = suspensionReason;
    }


    /**
     * Gets the contractGrantType attribute.
     *
     * @return Returns the contractGrantType.
     */
    @Override
    public String getContractGrantType() {
        return contractGrantType;
    }


    /**
     * Sets the contractGrantType attribute value.
     *
     * @param contractGrantType The contractGrantType to set.
     */
    public void setContractGrantType(String contractGrantType) {
        this.contractGrantType = contractGrantType;
    }

    /**
     * Gets the invoicingOptions attribute.
     *
     * @return Returns the invoicingOptions.
     */
    @Override
    public String getInvoicingOptions() {
        return invoicingOptions;
    }


    /**
     * Sets the invoicingOptions attribute value.
     *
     * @param invoicingOptions The invoicingOptions to set.
     */
    public void setInvoicingOptions(String invoicingOptions) {
        this.invoicingOptions = invoicingOptions;
    }


    /**
     * Gets the minInvoiceAmount attribute.
     *
     * @return Returns the minInvoiceAmount.
     */
    @Override
    public KualiDecimal getMinInvoiceAmount() {
        return minInvoiceAmount;
    }

    /**
     * Sets the minInvoiceAmount attribute value.
     *
     * @param minInvoiceAmount The minInvoiceAmount to set.
     */
    public void setMinInvoiceAmount(KualiDecimal minInvoiceAmount) {
        this.minInvoiceAmount = minInvoiceAmount;
    }

    /**
     * Gets the autoApproveIndicator attribute.
     *
     * @return Returns the autoApproveIndicator.
     */
    @Override
    public boolean getAutoApproveIndicator() {
        return autoApproveIndicator;
    }

    /**
     * Sets the autoApproveIndicator attribute value.
     *
     * @param autoApproveIndicator The autoApproveIndicator to set.
     */
    public void setAutoApproveIndicator(boolean autoApproveIndicator) {
        this.autoApproveIndicator = autoApproveIndicator;
    }

    /**
     * Gets the lookupPersonUniversalIdentifier attribute.
     *
     * @return Returns the lookupPersonUniversalIdentifier.
     */
    @Override
    public String getLookupPersonUniversalIdentifier() {
        return lookupPersonUniversalIdentifier;
    }

    /**
     * Sets the lookupPersonUniversalIdentifier attribute value.
     *
     * @param lookupPersonUniversalIdentifier The lookupPersonUniversalIdentifier to set.
     */
    public void setLookupPersonUniversalIdentifier(String lookupPersonUniversalIdentifier) {
        this.lookupPersonUniversalIdentifier = lookupPersonUniversalIdentifier;
    }

    /**
     * Gets the lookupPerson attribute.
     *
     * @return Returns the lookupPerson.
     */
    @Override
    public Person getLookupPerson() {
        return lookupPerson;
    }

    /**
     * Sets the lookupPerson attribute value.
     *
     * @param lookupPerson The lookupPerson to set.
     */
    public void setLookupPerson(Person lookupPerson) {
        this.lookupPerson = lookupPerson;
    }


    /**
     * Gets the lookupFundMgrPersonUniversalIdentifier attribute.
     *
     * @return Returns the lookupFundMgrPersonUniversalIdentifier.
     */
    @Override
    public String getLookupFundMgrPersonUniversalIdentifier() {
        return lookupFundMgrPersonUniversalIdentifier;
    }

    /**
     * Sets the lookupFundMgrPersonUniversalIdentifier attribute value.
     *
     * @param lookupFundMgrPersonUniversalIdentifier The lookupFundMgrPersonUniversalIdentifier to set.
     */
    public void setLookupFundMgrPersonUniversalIdentifier(String lookupFundMgrPersonUniversalIdentifier) {
        this.lookupFundMgrPersonUniversalIdentifier = lookupFundMgrPersonUniversalIdentifier;
    }

    /**
     * Gets the lookupFundMgrPerson attribute.
     *
     * @return Returns the lookupFundMgrPerson.
     */
    @Override
    public Person getLookupFundMgrPerson() {
        return lookupFundMgrPerson;
    }

    /**
     * Sets the lookupFundMgrPerson attribute value.
     *
     * @param lookupFundMgrPerson The lookupFundMgrPerson to set.
     */
    public void setLookupFundMgrPerson(Person lookupFundMgrPerson) {
        this.lookupFundMgrPerson = lookupFundMgrPerson;
    }

    /**
     * Gets the userLookupRoleNamespaceCode attribute.
     *
     * @return Returns the userLookupRoleNamespaceCode.
     */
    @Override
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    /**
     * Gets the userLookupRoleName attribute.
     *
     * @return Returns the userLookupRoleName.
     */
    @Override
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

    /**
     * Gets the letterOfCreditFund attribute.
     *
     * @return Returns the letterOfCreditFund.
     */
    @Override
    public ContractsAndGrantsLetterOfCreditFund getLetterOfCreditFund() {
        return letterOfCreditFund;
    }

    /**
     * Sets the letterOfCreditFund attribute value.
     *
     * @param letterOfCreditFund The letterOfCreditFund to set.
     */
    public void setLetterOfCreditFund(ContractsAndGrantsLetterOfCreditFund letterOfCreditFund) {
        this.letterOfCreditFund = letterOfCreditFund;
    }

    /**
     * Gets the awardPrimaryFundManager attribute.
     *
     * @return Returns the awardPrimaryFundManager.
     */
    @Override
    public ContractsAndGrantsFundManager getAwardPrimaryFundManager() {
        return awardPrimaryFundManager;
    }

    /**
     * Sets the awardPrimaryFundManager attribute value.
     *
     * @param awardPrimaryFundManager The awardPrimaryFundManager to set.
     */
    public void setAwardPrimaryFundManager(ContractsAndGrantsFundManager awardPrimaryFundManager) {
        this.awardPrimaryFundManager = awardPrimaryFundManager;
    }

    /**
     * Gets the billingFrequency attribute.
     *
     * @return Returns the billingFrequency.
     */
    @Override
    public ContractsAndGrantsBillingFrequency getBillingFrequency() {
        return billingFrequency;
    }

    /**
     * Sets the billingFrequency attribute value.
     *
     * @param billingFrequency The billingFrequency to set.
     */
    public void setBillingFrequency(ContractsAndGrantsBillingFrequency billingFrequency) {
        this.billingFrequency = billingFrequency;
    }

    /**
     * This method gets the primary award organization.
     *
     * @return The award organization object marked as primary in the award organizations collection.
     */
    @Override
    public ContractsAndGrantsOrganization getPrimaryAwardOrganization() {

        return primaryAwardOrganization;
    }

    /**
     * This method sets the primary award organization.
     *
     * @param primaryAwardOrganization
     */
    public void setPrimaryAwardOrganization(ContractsAndGrantsOrganization primaryAwardOrganization) {
        this.primaryAwardOrganization = primaryAwardOrganization;
        this.routingChart = primaryAwardOrganization.getChartOfAccountsCode();
        this.routingOrg = primaryAwardOrganization.getOrganizationCode();
    }

    /**
     *
     * @see org.kuali.rice.krad.bo.BusinessObject#prepareForWorkflow()
     */
    public void prepareForWorkflow1() {
    }

    /**
     *
     * @see org.kuali.rice.krad.bo.BusinessObject#refresh()
     */
    @Override
    public void refresh() {
    }

    /**
     * Gets the awardPrimaryProjectDirector attribute.
     *
     * @return Returns the awardPrimaryProjectDirector.
     */
    @Override
    public ContractsAndGrantsProjectDirector getAwardPrimaryProjectDirector() {
        return awardPrimaryProjectDirector;
    }

    /**
     * Sets the awardPrimaryProjectDirector attribute value.
     *
     * @param awardPrimaryProjectDirector The awardPrimaryProjectDirector to set.
     */
    public void setAwardPrimaryProjectDirector(ContractsAndGrantsProjectDirector awardPrimaryProjectDirector) {
        this.awardPrimaryProjectDirector = awardPrimaryProjectDirector;
    }

    /**
     * Gets the fundingExpirationDate attribute.
     *
     * @return Returns the fundingExpirationDate.
     */
    @Override
    public Date getFundingExpirationDate() {
        return fundingExpirationDate;
    }

    /**
     * Sets the fundingExpirationDate attribute value.
     *
     * @param fundingExpirationDate The fundingExpirationDate to set.
     */
    public void setFundingExpirationDate(Date fundingExpirationDate) {
        this.fundingExpirationDate = fundingExpirationDate;
    }

    /**
     * Gets the drawNumber attribute.
     *
     * @return Returns the drawNumber.
     */
    @Override
    public String getDrawNumber() {
        return drawNumber;
    }

    /**
     * Sets the drawNumber attribute value.
     *
     * @param drawNumber The drawNumber to set.
     */
    public void setDrawNumber(String drawNumber) {
        this.drawNumber = drawNumber;
    }

    /**
     * Gets the commentText attribute.
     *
     * @return Returns the commentText.
     */
    @Override
    public String getCommentText() {
        return commentText;
    }

    /**
     * Sets the commentText attribute value.
     *
     * @param commentText The commentText to set.
     */
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    /**
     * Sets the proposal attribute value.
     *
     * @param proposal The proposal to set.
     */
    public void setProposal(ContractAndGrantsProposal proposal) {
        this.proposal = proposal;
    }

    /**
     * Gets the federalPassThroughAgency attribute.
     *
     * @return Returns the federalPassThroughAgency.
     */
    public ContractsAndGrantsCGBAgency getFederalPassThroughAgency() {
        return federalPassThroughAgency;
    }

    /**
     * Sets the federalPassThroughAgency attribute value.
     *
     * @param federalPassThroughAgency The federalPassThroughAgency to set.
     */
    public void setFederalPassThroughAgency(ContractsAndGrantsCGBAgency federalPassThroughAgency) {
        this.federalPassThroughAgency = federalPassThroughAgency;
    }

    /**
     * Gets the list of active award invoice accounts. The integration object is used here - as this would be referred only from AR
     * module.
     *
     * @return Returns the active awardInvoiceAccounts.
     */
    @Override
    public List<ContractsGrantsAwardInvoiceAccountInformation> getActiveAwardInvoiceAccounts() {
        List<ContractsGrantsAwardInvoiceAccountInformation> awardInvoiceAccounts = new ArrayList<ContractsGrantsAwardInvoiceAccountInformation>();
        return awardInvoiceAccounts;
    }

    /**
     * Gets the stopWorkIndicator attribute.
     *
     * @return Returns the stopWorkIndicator.
     */
    @Override
    public boolean isStopWorkIndicator() {
        return stopWorkIndicator;
    }

    /**
     * Sets the stopWorkIndicator attribute value.
     *
     * @param stopWorkIndicator The stopWorkIndicator to set.
     */
    public void setStopWorkIndicator(boolean stopWorkIndicator) {
        this.stopWorkIndicator = stopWorkIndicator;
    }

    /**
     * Gets the dunningCampaign attribute.
     *
     * @return Returns the dunningCampaign.
     */
    @Override
    public String getDunningCampaign() {
        return dunningCampaign;
    }

    /**
     * Sets the dunningCampaign attribute value.
     *
     * @param dunningCampaign The dunningCampaign to set.
     */
    public void setDunningCampaign(String dunningCampaign) {
        this.dunningCampaign = dunningCampaign;
    }
}
