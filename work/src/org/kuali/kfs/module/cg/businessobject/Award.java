/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.cg.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.integration.cg.ContractsGrantsAwardInvoiceAccountInformation;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
import java.util.ArrayList;

/**
 * Defines a financial award object.
 */
public class Award extends PersistableBusinessObjectBase implements MutableInactivatable, ContractsAndGrantsCGBAward, ContractsAndGrantsAward {
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
    private String letterOfCreditFundGroupCode;
    private String grantDescriptionCode;
    private String agencyNumber;
    private String locCreationType; // To create LOC
    private String federalPassThroughAgencyNumber;
    private String agencyAnalystName;
    private String analystTelephoneNumber;
    private String preferredBillingFrequency;
    private BillingFrequency billingFrequency;
    private String preferredReportTemplate;
    private FinancialFormTemplate financialFormTemplate;
    private String preferredReportFrequency;
    private FinancialReportFrequencies financialReportFrequencies;
    private String awardProjectTitle;
    private String awardCommentText;
    private String awardPurposeCode;
    private boolean active;
    private String kimGroupNames;
    private List<AwardProjectDirector> awardProjectDirectors;
    private AwardProjectDirector awardPrimaryProjectDirector;
    private List<AwardFundManager> awardFundManagers;
    private AwardFundManager awardPrimaryFundManager;
    private List<AwardAccount> awardAccounts;
    private List<AwardInvoiceAccount> awardInvoiceAccounts;
    private List<AwardSubcontractor> awardSubcontractors;
    private List<AwardOrganization> awardOrganizations;
    private List<Milestone> milestones;
    private List<Bill> bills;


    private Proposal proposal;
    private ProposalAwardType proposalAwardType;
    private AwardStatus awardStatus;
    private LetterOfCreditFund letterOfCreditFund;
    private LetterOfCreditFundGroup letterOfCreditFundGroup;
    private GrantDescription grantDescription;
    private Agency agency;
    private Agency federalPassThroughAgency;
    private ProposalPurpose awardPurpose;
    private AwardOrganization primaryAwardOrganization;
    private String routingOrg;
    private String routingChart;

    private boolean stateTransfer;
    private boolean suspendInvoicing;
    private boolean additionalFormsRequired;
    private String additionalFormsDescription;
    private String suspensionReason;
    private String contractGrantType;
    private String awardsourceOfFundsCode;
    private SourceOfFunds sourceOfFunds;
    private String invoicingOptions;

    private KualiDecimal minInvoiceAmount = KualiDecimal.ZERO;

    private boolean autoApprove;

    private MilestoneSchedule milestoneSchedule;

    private Date fundingExpirationDate;
    private String drawNumber;
    private String commentText;
    private String dunningCampaign;
    private boolean stopWork;

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
        // Must use ArrayList because its get() method automatically grows the array for Struts.
        awardProjectDirectors = new ArrayList<AwardProjectDirector>();
        awardFundManagers = new ArrayList<AwardFundManager>();
        awardAccounts = new ArrayList<AwardAccount>();
        awardSubcontractors = new ArrayList<AwardSubcontractor>();
        awardOrganizations = new ArrayList<AwardOrganization>();
        milestones = new ArrayList<Milestone>();
        bills = new ArrayList<Bill>();
        awardInvoiceAccounts = new ArrayList<AwardInvoiceAccount>();
    }

    /**
     * Gets the suspensionReason attribute.
     * 
     * @return Returns the suspensionReason.
     */
    public String getSuspensionReason() {
        return suspensionReason;
    }

    /**
     * Gets the stateTransfer attribute.
     * 
     * @return Returns the stateTransfer.
     */
    public boolean isStateTransfer() {
        return stateTransfer;
    }

    /**
     * Sets the stateTransfer attribute value.
     * 
     * @param stateTransfer The stateTransfer to set.
     */
    public void setStateTransfer(boolean stateTransfer) {
        this.stateTransfer = stateTransfer;
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
     * Creates a collection of lists within this award object that should be aware of when the deletion of one of their elements
     * occurs. This collection is used to refresh the display upon deletion of an element to ensure that the deleted element is not
     * longer visible on the interface.
     * 
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    
    public List buildListOfDeletionAwareLists() {
        List<Collection<PersistableBusinessObject>> managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getAwardAccounts());
        managedLists.add(getAwardOrganizations());
        managedLists.add(getAwardProjectDirectors());
        managedLists.add(getAwardFundManagers());
        managedLists.add(getAwardSubcontractors());
        managedLists.add(getAwardInvoiceAccounts());
        return managedLists;
    }

    /**
     * Constructs an Award.
     * 
     * @param proposal The associated proposal that the award will be linked to.
     */
    public Award(Proposal proposal) {
        this();
        populateFromProposal(proposal);
    }

    /**
     * This method takes all the applicable attributes from the associated proposal object and sets those attributes into their
     * corresponding award attributes.
     * 
     * @param proposal The associated proposal that the award will be linked to.
     */
    public void populateFromProposal(Proposal proposal) {
        if (ObjectUtils.isNotNull(proposal)) {
            setProposalNumber(proposal.getProposalNumber());
            setAgencyNumber(proposal.getAgencyNumber());
            setAwardProjectTitle(proposal.getProposalProjectTitle());
            setAwardDirectCostAmount(proposal.getProposalDirectCostAmount());
            setAwardIndirectCostAmount(proposal.getProposalIndirectCostAmount());
            setProposalAwardTypeCode(proposal.getProposalAwardTypeCode());
            setFederalPassThroughIndicator(proposal.getProposalFederalPassThroughIndicator());
            setFederalPassThroughAgencyNumber(proposal.getFederalPassThroughAgencyNumber());
            setAwardPurposeCode(proposal.getProposalPurposeCode());

            // copy proposal organizations to award organizations
            getAwardOrganizations().clear();
            for (ProposalOrganization pOrg : proposal.getProposalOrganizations()) {
                AwardOrganization awardOrg = new AwardOrganization();
                // newCollectionRecord is set to true to allow deletion of this record after being populated from proposal
                awardOrg.setNewCollectionRecord(true);
                awardOrg.setProposalNumber(pOrg.getProposalNumber());
                awardOrg.setChartOfAccountsCode(pOrg.getChartOfAccountsCode());
                awardOrg.setOrganizationCode(pOrg.getOrganizationCode());
                awardOrg.setAwardPrimaryOrganizationIndicator(pOrg.isProposalPrimaryOrganizationIndicator());
                awardOrg.setActive(pOrg.isActive());
                getAwardOrganizations().add(awardOrg);
            }

            // copy proposal subcontractors to award subcontractors
            getAwardSubcontractors().clear();
            int awardSubcontractAmendment = 1;
            for (ProposalSubcontractor pSubcontractor : proposal.getProposalSubcontractors()) {
                AwardSubcontractor awardSubcontractor = new AwardSubcontractor();
                // newCollectionRecord is set to true to allow deletion of this record after being populated from proposal
                awardSubcontractor.setNewCollectionRecord(true);
                awardSubcontractor.setProposalNumber(pSubcontractor.getProposalNumber());
                awardSubcontractor.setAwardSubcontractorNumber(pSubcontractor.getProposalSubcontractorNumber());
                
                // Since we might possibly pulled multiples of same subcontractor from the proposal, we cannot set them all to 1s.
                // Increment the amendment number for every subcontractor from the proposal 
                awardSubcontractor.setAwardSubcontractorAmendmentNumber(String.valueOf(awardSubcontractAmendment++));
                awardSubcontractor.setSubcontractorAmount(pSubcontractor.getProposalSubcontractorAmount());
                awardSubcontractor.setAwardSubcontractorDescription(pSubcontractor.getProposalSubcontractorDescription());
                awardSubcontractor.setSubcontractorNumber(pSubcontractor.getSubcontractorNumber());
                awardSubcontractor.setActive(pSubcontractor.isActive());
                getAwardSubcontractors().add(awardSubcontractor);
            }

            // copy proposal project directors to award propject directors
            getAwardProjectDirectors().clear();
            for (ProposalProjectDirector pDirector : proposal.getProposalProjectDirectors()) {
                AwardProjectDirector awardDirector = new AwardProjectDirector();
                // newCollectionRecord is set to true to allow deletion of this record after being populated from proposal
                awardDirector.setNewCollectionRecord(true);
                awardDirector.setProposalNumber(pDirector.getProposalNumber());
                awardDirector.setAwardPrimaryProjectDirectorIndicator(pDirector.isProposalPrimaryProjectDirectorIndicator());
                awardDirector.setAwardProjectDirectorProjectTitle(pDirector.getProposalProjectDirectorProjectTitle());
                awardDirector.setPrincipalId(pDirector.getPrincipalId());
                awardDirector.setActive(pDirector.isActive());
                getAwardProjectDirectors().add(awardDirector);
            }
        }
    }

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     */
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
     * Gets the awardBeginningDate attribute.
     * 
     * @return Returns the awardBeginningDate
     */
    public Date getAwardBeginningDate() {
        return awardBeginningDate;
    }

    /**
     * Sets the awardBeginningDate attribute.
     * 
     * @param awardBeginningDate The awardBeginningDate to set.
     */
    public void setAwardBeginningDate(Date awardBeginningDate) {
        this.awardBeginningDate = awardBeginningDate;
    }

    /**
     * Gets the awardEndingDate attribute.
     * 
     * @return Returns the awardEndingDate
     */
    public Date getAwardEndingDate() {
        return awardEndingDate;
    }

    /**
     * Gets the kimGroupNames attribute.
     * 
     * @return Returns the kimGroupNames.
     */
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
     * Sets the awardEndingDate attribute.
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
     * @return Returns the awardTotalAmount
     */
    public KualiDecimal getAwardTotalAmount() {
        KualiDecimal direct = getAwardDirectCostAmount();
        KualiDecimal indirect = getAwardIndirectCostAmount();
        return ObjectUtils.isNull(direct) || ObjectUtils.isNull(indirect) ? null : direct.add(indirect);
    }

    /**
     * Does nothing. This property is determined by the direct and indirect cost amounts. This setter is here only because without
     * it, the maintenance framework won't display this attribute.
     * 
     * @param awardTotalAmount The awardTotalAmount to set.
     * @deprecated Should not be used. See method description above.
     */
    @Deprecated
    public void setAwardTotalAmount(KualiDecimal awardTotalAmount) {
        // do nothing
    }

    /**
     * OJB calls this method as the first operation before this BO is inserted into the database. The database contains
     * CGAWD_TOT_AMT, a denormalized column that Kuali does not use but needs to maintain with this method because OJB bypasses the
     * getter.
     * 
     * @param persistenceBroker from OJB
     * @throws PersistenceBrokerException Thrown by call to super.prePersist();
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    @Override protected void prePersist() {
        super.prePersist();
        awardTotalAmount = getAwardTotalAmount();
    }

    /**
     * OJB calls this method as the first operation before this BO is updated to the database. The database contains CGAWD_TOT_AMT,
     * a denormalized column that Kuali does not use but needs to maintain with this method because OJB bypasses the getter.
     * 
     * @param persistenceBroker from OJB
     * @throws PersistenceBrokerException Thrown by call to super.preUpdate();
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    @Override protected void preUpdate() {
        super.preUpdate();
        awardTotalAmount = getAwardTotalAmount();
    }

    /**
     * Gets the awardAddendumNumber attribute.
     * 
     * @return Returns the awardAddendumNumber
     */
    public String getAwardAddendumNumber() {
        return awardAddendumNumber;
    }

    /**
     * Sets the awardAddendumNumber attribute.
     * 
     * @param awardAddendumNumber The awardAddendumNumber to set.
     */
    public void setAwardAddendumNumber(String awardAddendumNumber) {
        this.awardAddendumNumber = awardAddendumNumber;
    }

    /**
     * Gets the awardAllocatedUniversityComputingServicesAmount attribute.
     * 
     * @return Returns the awardAllocatedUniversityComputingServicesAmount
     */
    public KualiDecimal getAwardAllocatedUniversityComputingServicesAmount() {
        return awardAllocatedUniversityComputingServicesAmount;
    }

    /**
     * Sets the awardAllocatedUniversityComputingServicesAmount attribute.
     * 
     * @param awardAllocatedUniversityComputingServicesAmount The awardAllocatedUniversityComputingServicesAmount to set.
     */
    public void setAwardAllocatedUniversityComputingServicesAmount(KualiDecimal awardAllocatedUniversityComputingServicesAmount) {
        this.awardAllocatedUniversityComputingServicesAmount = awardAllocatedUniversityComputingServicesAmount;
    }

    /**
     * Gets the federalPassThroughFundedAmount attribute.
     * 
     * @return Returns the federalPassThroughFundedAmount
     */
    public KualiDecimal getFederalPassThroughFundedAmount() {
        return federalPassThroughFundedAmount;
    }

    /**
     * Sets the federalPassThroughFundedAmount attribute.
     * 
     * @param federalPassThroughFundedAmount The federalPassThroughFundedAmount to set.
     */
    public void setFederalPassThroughFundedAmount(KualiDecimal federalPassThroughFundedAmount) {
        this.federalPassThroughFundedAmount = federalPassThroughFundedAmount;
    }

    /**
     * Gets the awardEntryDate attribute.
     * 
     * @return Returns the awardEntryDate
     */
    public Date getAwardEntryDate() {
        return awardEntryDate;
    }

    /**
     * Sets the awardEntryDate attribute.
     * 
     * @param awardEntryDate The awardEntryDate to set.
     */
    public void setAwardEntryDate(Date awardEntryDate) {
        this.awardEntryDate = awardEntryDate;
    }

    /**
     * Gets the agencyFuture1Amount attribute.
     * 
     * @return Returns the agencyFuture1Amount
     */
    public KualiDecimal getAgencyFuture1Amount() {
        return agencyFuture1Amount;
    }

    /**
     * Sets the agencyFuture1Amount attribute.
     * 
     * @param agencyFuture1Amount The agencyFuture1Amount to set.
     */
    public void setAgencyFuture1Amount(KualiDecimal agencyFuture1Amount) {
        this.agencyFuture1Amount = agencyFuture1Amount;
    }


    /**
     * Gets the agencyFuture2Amount attribute.
     * 
     * @return Returns the agencyFuture2Amount
     */
    public KualiDecimal getAgencyFuture2Amount() {
        return agencyFuture2Amount;
    }

    /**
     * Sets the agencyFuture2Amount attribute.
     * 
     * @param agencyFuture2Amount The agencyFuture2Amount to set.
     */
    public void setAgencyFuture2Amount(KualiDecimal agencyFuture2Amount) {
        this.agencyFuture2Amount = agencyFuture2Amount;
    }

    /**
     * Gets the agencyFuture3Amount attribute.
     * 
     * @return Returns the agencyFuture3Amount
     */
    public KualiDecimal getAgencyFuture3Amount() {
        return agencyFuture3Amount;
    }

    /**
     * Sets the agencyFuture3Amount attribute.
     * 
     * @param agencyFuture3Amount The agencyFuture3Amount to set.
     */
    public void setAgencyFuture3Amount(KualiDecimal agencyFuture3Amount) {
        this.agencyFuture3Amount = agencyFuture3Amount;
    }

    /**
     * Gets the awardDocumentNumber attribute.
     * 
     * @return Returns the awardDocumentNumber
     */
    public String getAwardDocumentNumber() {
        return awardDocumentNumber;
    }

    /**
     * Sets the awardDocumentNumber attribute.
     * 
     * @param awardDocumentNumber The awardDocumentNumber to set.
     */
    public void setAwardDocumentNumber(String awardDocumentNumber) {
        this.awardDocumentNumber = awardDocumentNumber;
    }

    /**
     * Gets the awardLastUpdateDate attribute.
     * 
     * @return Returns the awardLastUpdateDate
     */
    public Timestamp getAwardLastUpdateDate() {
        return awardLastUpdateDate;
    }

    /**
     * Sets the awardLastUpdateDate attribute.
     * 
     * @param awardLastUpdateDate The awardLastUpdateDate to set.
     */
    public void setAwardLastUpdateDate(Timestamp awardLastUpdateDate) {
        this.awardLastUpdateDate = awardLastUpdateDate;
    }

    /**
     * Gets the federalPassThroughIndicator attribute.
     * 
     * @return Returns the federalPassThroughIndicator
     */
    public boolean getFederalPassThroughIndicator() {
        return federalPassThroughIndicator;
    }

    /**
     * Sets the federalPassThroughIndicator attribute.
     * 
     * @param federalPassThroughIndicator The federalPassThroughIndicator to set.
     */
    public void setFederalPassThroughIndicator(boolean federalPassThroughIndicator) {
        this.federalPassThroughIndicator = federalPassThroughIndicator;
    }

    /**
     * Gets the oldProposalNumber attribute.
     * 
     * @return Returns the oldProposalNumber
     */
    public String getOldProposalNumber() {
        return oldProposalNumber;
    }

    /**
     * Sets the oldProposalNumber attribute.
     * 
     * @param oldProposalNumber The oldProposalNumber to set.
     */
    public void setOldProposalNumber(String oldProposalNumber) {
        this.oldProposalNumber = oldProposalNumber;
    }

    /**
     * Gets the awardDirectCostAmount attribute.
     * 
     * @return Returns the awardDirectCostAmount
     */
    public KualiDecimal getAwardDirectCostAmount() {
        return awardDirectCostAmount;
    }

    /**
     * Sets the awardDirectCostAmount attribute.
     * 
     * @param awardDirectCostAmount The awardDirectCostAmount to set.
     */
    public void setAwardDirectCostAmount(KualiDecimal awardDirectCostAmount) {
        this.awardDirectCostAmount = awardDirectCostAmount;
    }

    /**
     * Gets the awardIndirectCostAmount attribute.
     * 
     * @return Returns the awardIndirectCostAmount
     */
    public KualiDecimal getAwardIndirectCostAmount() {
        return awardIndirectCostAmount;
    }

    /**
     * Sets the awardIndirectCostAmount attribute.
     * 
     * @param awardIndirectCostAmount The awardIndirectCostAmount to set.
     */
    public void setAwardIndirectCostAmount(KualiDecimal awardIndirectCostAmount) {
        this.awardIndirectCostAmount = awardIndirectCostAmount;
    }

    /**
     * Gets the federalFundedAmount attribute.
     * 
     * @return Returns the federalFundedAmount
     */
    public KualiDecimal getFederalFundedAmount() {
        return federalFundedAmount;
    }

    /**
     * Sets the federalFundedAmount attribute.
     * 
     * @param federalFundedAmount The federalFundedAmount to set.
     */
    public void setFederalFundedAmount(KualiDecimal federalFundedAmount) {
        this.federalFundedAmount = federalFundedAmount;
    }

    /**
     * Gets the awardCreateTimestamp attribute.
     * 
     * @return Returns the awardCreateTimestamp
     */
    public Timestamp getAwardCreateTimestamp() {
        return awardCreateTimestamp;
    }

    /**
     * Sets the awardCreateTimestamp attribute.
     * 
     * @param awardCreateTimestamp The awardCreateTimestamp to set.
     */
    public void setAwardCreateTimestamp(Timestamp awardCreateTimestamp) {
        this.awardCreateTimestamp = awardCreateTimestamp;
    }

    /**
     * Gets the awardClosingDate attribute.
     * 
     * @return Returns the awardClosingDate
     */
    public Date getAwardClosingDate() {
        return awardClosingDate;
    }

    /**
     * Sets the awardClosingDate attribute.
     * 
     * @param awardClosingDate The awardClosingDate to set.
     */
    public void setAwardClosingDate(Date awardClosingDate) {
        this.awardClosingDate = awardClosingDate;
    }

    /**
     * Gets the proposalAwardTypeCode attribute.
     * 
     * @return Returns the proposalAwardTypeCode
     */
    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }

    /**
     * Sets the proposalAwardTypeCode attribute.
     * 
     * @param proposalAwardTypeCode The proposalAwardTypeCode to set.
     */
    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }

    /**
     * Gets the awardStatusCode attribute.
     * 
     * @return Returns the awardStatusCode
     */
    public String getAwardStatusCode() {
        return awardStatusCode;
    }

    /**
     * Sets the awardStatusCode attribute.
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
     * @return Returns the grantDescriptionCode
     */
    public String getGrantDescriptionCode() {
        return grantDescriptionCode;
    }

    /**
     * Sets the grantDescriptionCode attribute.
     * 
     * @param grantDescriptionCode The grantDescriptionCode to set.
     */
    public void setGrantDescriptionCode(String grantDescriptionCode) {
        this.grantDescriptionCode = grantDescriptionCode;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the federalPassThroughAgencyNumber attribute.
     * 
     * @return Returns the federalPassThroughAgencyNumber
     */
    public String getFederalPassThroughAgencyNumber() {
        return federalPassThroughAgencyNumber;
    }

    /**
     * Sets the federalPassThroughAgencyNumber attribute.
     * 
     * @param federalPassThroughAgencyNumber The federalPassThroughAgencyNumber to set.
     */
    public void setFederalPassThroughAgencyNumber(String federalPassThroughAgencyNumber) {
        this.federalPassThroughAgencyNumber = federalPassThroughAgencyNumber;
    }

    /**
     * Gets the agencyAnalystName attribute.
     * 
     * @return Returns the agencyAnalystName
     */
    public String getAgencyAnalystName() {
        return agencyAnalystName;
    }

    /**
     * Sets the agencyAnalystName attribute.
     * 
     * @param agencyAnalystName The agencyAnalystName to set.
     */
    public void setAgencyAnalystName(String agencyAnalystName) {
        this.agencyAnalystName = agencyAnalystName;
    }

    /**
     * Gets the analystTelephoneNumber attribute.
     * 
     * @return Returns the analystTelephoneNumber
     */
    public String getAnalystTelephoneNumber() {
        return analystTelephoneNumber;
    }

    /**
     * Sets the analystTelephoneNumber attribute.
     * 
     * @param analystTelephoneNumber The analystTelephoneNumber to set.
     */
    public void setAnalystTelephoneNumber(String analystTelephoneNumber) {
        this.analystTelephoneNumber = analystTelephoneNumber;
    }

    /**
     * Gets the awardProjectTitle attribute.
     * 
     * @return Returns the awardProjectTitle
     */
    public String getAwardProjectTitle() {
        return awardProjectTitle;
    }

    /**
     * Sets the awardProjectTitle attribute.
     * 
     * @param awardProjectTitle The awardProjectTitle to set.
     */
    public void setAwardProjectTitle(String awardProjectTitle) {
        this.awardProjectTitle = awardProjectTitle;
    }

    /**
     * Gets the awardCommentText attribute.
     * 
     * @return Returns the awardCommentText
     */
    public String getAwardCommentText() {
        return awardCommentText;
    }

    /**
     * Sets the awardCommentText attribute.
     * 
     * @param awardCommentText The awardCommentText to set.
     */
    public void setAwardCommentText(String awardCommentText) {
        this.awardCommentText = awardCommentText;
    }

    /**
     * Gets the awardPurposeCode attribute.
     * 
     * @return Returns the awardPurposeCode
     */
    public String getAwardPurposeCode() {
        return awardPurposeCode;
    }

    /**
     * Sets the awardPurposeCode attribute.
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
     * Gets the proposal attribute.
     * 
     * @return Returns the proposal
     */
    public Proposal getProposal() {
        return proposal;
    }

    /**
     * Sets the proposal attribute.
     * 
     * @param proposal The proposal to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    /**
     * Gets the proposalAwardType attribute.
     * 
     * @return Returns the proposalAwardType
     */
    public ProposalAwardType getProposalAwardType() {
        return proposalAwardType;
    }

    /**
     * Sets the proposalAwardType attribute.
     * 
     * @param proposalAwardType The proposalAwardType to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setProposalAwardType(ProposalAwardType proposalAwardType) {
        this.proposalAwardType = proposalAwardType;
    }

    /**
     * Gets the awardStatus attribute.
     * 
     * @return Returns the awardStatus
     */
    public AwardStatus getAwardStatus() {
        return awardStatus;
    }

    /**
     * Sets the awardStatus attribute.
     * 
     * @param awardStatus The awardStatus to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setAwardStatus(AwardStatus awardStatus) {
        this.awardStatus = awardStatus;
    }

    /**
     * Gets the letterOfCreditFund attribute.
     * 
     * @return Returns the letterOfCreditFund
     */
    public LetterOfCreditFund getLetterOfCreditFund() {
        return letterOfCreditFund;
    }

    /**
     * Sets the letterOfCreditFund attribute.
     * 
     * @param letterOfCreditFund The letterOfCreditFund to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setLetterOfCreditFund(LetterOfCreditFund letterOfCreditFund) {
        this.letterOfCreditFund = letterOfCreditFund;
    }

    /**
     * Gets the letterOfCreditFundGroup attribute.
     * 
     * @return Returns the letterOfCreditFundGroup
     */
    public LetterOfCreditFundGroup getLetterOfCreditFundGroup() {
        return letterOfCreditFundGroup;
    }

    /**
     * Sets the letterOfCreditFundGroup attribute.
     * 
     * @param letterOfCreditFundGroup The letterOfCreditFundGroup to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setLetterOfCreditFundGroup(LetterOfCreditFundGroup letterOfCreditFundGroup) {
        this.letterOfCreditFundGroup = letterOfCreditFundGroup;
    }

    /**
     * Gets the grantDescription attribute.
     * 
     * @return Returns the grantDescription
     */
    public GrantDescription getGrantDescription() {
        return grantDescription;
    }

    /**
     * Sets the grantDescription attribute.
     * 
     * @param grantDescription The grantDescription to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setGrantDescription(GrantDescription grantDescription) {
        this.grantDescription = grantDescription;
    }

    /**
     * Gets the agency attribute.
     * 
     * @return Returns the agency
     */
    public Agency getAgency() {
        return agency;
    }

    /**
     * Sets the agency attribute.
     * 
     * @param agency The agency to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    /**
     * Gets the federalPassThroughAgency attribute.
     * 
     * @return Returns the federalPassThroughAgency
     */
    public Agency getFederalPassThroughAgency() {
        return federalPassThroughAgency;
    }

    /**
     * Sets the federalPassThroughAgency attribute.
     * 
     * @param federalPassThroughAgency The federalPassThroughAgency to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setFederalPassThroughAgency(Agency federalPassThroughAgency) {
        this.federalPassThroughAgency = federalPassThroughAgency;
    }

    /**
     * Gets the awardPurpose attribute.
     * 
     * @return Returns the awardPurpose
     */
    public ProposalPurpose getAwardPurpose() {
        return awardPurpose;
    }

    /**
     * Sets the awardPurpose attribute.
     * 
     * @param awardPurpose The awardPurpose to set.
     * @deprecated Setter is required by OJB, but should not be used to modify this attribute. This attribute is set on the initial
     *             creation of the object and should not be changed.
     */
    @Deprecated
    public void setAwardPurpose(ProposalPurpose awardPurpose) {
        this.awardPurpose = awardPurpose;
    }

    /**
     * Gets the awardProjectDirectors list.
     * 
     * @return Returns the awardProjectDirectors list
     */
    public List<AwardProjectDirector> getAwardProjectDirectors() {
        return awardProjectDirectors;
    }

    /**
     * Sets the awardProjectDirectors list.
     * 
     * @param awardProjectDirectors The awardProjectDirectors list to set.
     */
    public void setAwardProjectDirectors(List<AwardProjectDirector> awardProjectDirectors) {
        this.awardProjectDirectors = awardProjectDirectors;
    }

    /**
     * Gets the awardFundManagers list.
     * 
     * @return Returns the awardFundManagers list
     */
    public List<AwardFundManager> getAwardFundManagers() {
        return awardFundManagers;
    }

    /**
     * Sets the awardFundManagers list.
     * 
     * @param awardFundManagers The awardFundManagers list to set.
     */
    public void setAwardFundManagers(List<AwardFundManager> awardFundManagers) {
        this.awardFundManagers = awardFundManagers;
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
     * Gets the list of active award accounts. The integration object is used here - as this would be referred only from AR module.
     * 
     * @return Returns the active awardAccounts.
     */
    public List<ContractsAndGrantsCGBAwardAccount> getActiveAwardAccounts() {
        List<ContractsAndGrantsCGBAwardAccount> activeAwardAccounts = new ArrayList<ContractsAndGrantsCGBAwardAccount>();
        List<AwardAccount> awdAccts = new ArrayList<AwardAccount>();
        for (AwardAccount awardAccount : awardAccounts) {
            if (awardAccount.isActive()) {
                awdAccts.add(awardAccount);
            }
        }
        activeAwardAccounts = new ArrayList<ContractsAndGrantsCGBAwardAccount>(awdAccts);
        return activeAwardAccounts;
    }


    /**
     * Sets the awardAccounts list.
     * 
     * @param awardAccounts The awardAccounts to set.
     */
    public void setAwardAccounts(List<AwardAccount> awardAccounts) {
        this.awardAccounts = awardAccounts;
    }

    /**
     * Gets the awardOrganizations list.
     * 
     * @return Returns the awardOrganizations.
     */
    public List<AwardOrganization> getAwardOrganizations() {
        return awardOrganizations;
    }

    /**
     * Sets the awardOrganizations list.
     * 
     * @param awardOrganizations The awardOrganizations to set.
     */
    public void setAwardOrganizations(List<AwardOrganization> awardOrganizations) {
        this.awardOrganizations = awardOrganizations;
    }

    /**
     * Gets the awardSubcontractors list.
     * 
     * @return Returns the awardSubcontractors.
     */
    public List<AwardSubcontractor> getAwardSubcontractors() {
        return awardSubcontractors;
    }

    /**
     * Sets the awardSubcontractors list.
     * 
     * @param awardSubcontractors The awardSubcontractors to set.
     */
    public void setAwardSubcontractors(List<AwardSubcontractor> awardSubcontractors) {
        this.awardSubcontractors = awardSubcontractors;
    }

    /**
     * This method gets the primary award organization.
     * 
     * @return The award organization object marked as primary in the award organizations collection.
     */
    public AwardOrganization getPrimaryAwardOrganization() {
        for (AwardOrganization ao : awardOrganizations) {
            if (ao != null && ao.isAwardPrimaryOrganizationIndicator()) {
                setPrimaryAwardOrganization(ao);
                break;
            }
        }

        return primaryAwardOrganization;
    }

    /**
     * This method sets the primary award organization.
     * 
     * @param primaryAwardOrganization
     */
    public void setPrimaryAwardOrganization(AwardOrganization primaryAwardOrganization) {
        this.primaryAwardOrganization = primaryAwardOrganization;
        this.routingChart = primaryAwardOrganization.getChartOfAccountsCode();
        this.routingOrg = primaryAwardOrganization.getOrganizationCode();
    }

    /**
     * Sums the total for all award subcontractors
     * 
     * @return Returns the total of all the award subcontractor's amounts
     */
    public KualiDecimal getAwardSubcontractorsTotalAmount() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (AwardSubcontractor subcontractor : getAwardSubcontractors()) {
            KualiDecimal amount = subcontractor.getSubcontractorAmount();
            if (ObjectUtils.isNotNull(amount)) {
                total = total.add(amount);
            }
        }
        return total;
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.AlternateOrgReviewRouting#getRoutingChart()
     */
    public String getRoutingChart() {
        return routingChart;
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.AlternateOrgReviewRouting#setRoutingChart(java.lang.String)
     */
    public void setRoutingChart(String routingChart) {
        this.routingChart = routingChart;
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.AlternateOrgReviewRouting#getRoutingOrg()
     */
    public String getRoutingOrg() {
        return routingOrg;
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.AlternateOrgReviewRouting#setRoutingOrg(java.lang.String)
     */
    public void setRoutingOrg(String routingOrg) {
        this.routingOrg = routingOrg;
    }

    /**
     * Gets the lookup {@link Person}.
     * 
     * @return the lookup {@link Person}
     */
    public Person getLookupPerson() {
        return lookupPerson;
    }

    /**
     * Sets the lookup {@link Person}
     * 
     * @param lookupPerson
     */
    public void setLookupPerson(Person lookupPerson) {
        this.lookupPerson = lookupPerson;
    }

    /**
     * Gets the universal user id of the lookup person.
     * 
     * @return the id of the lookup person
     */
    public String getLookupPersonUniversalIdentifier() {
        lookupPerson = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(lookupPersonUniversalIdentifier, lookupPerson);
        return lookupPersonUniversalIdentifier;
    }

    /**
     * Sets the universal user id of the lookup person
     * 
     * @param lookupPersonId the id of the lookup person
     */
    public void setLookupPersonUniversalIdentifier(String lookupPersonId) {
        this.lookupPersonUniversalIdentifier = lookupPersonId;
    }

    /**
     * Gets the userLookupRoleNamespaceCode list.
     * 
     * @return Returns the userLookupRoleNamespaceCode.
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    /**
     * Sets the universal user id of the lookup person
     * 
     * @param lookupPersonId the id of the lookup person
     */
    public void setUserLookupRoleNamespaceCode(String userLookupRoleNamespaceCode) {
    }

    /**
     * Gets the userLookupRoleName list.
     * 
     * @return Returns the userLookupRoleName.
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

    /**
     * Sets the userLookupRoleName of the lookup person
     * 
     * @param userLookupRoleName
     */
    public void setUserLookupRoleName(String userLookupRoleName) {
    }

    /**
     * @return a String to represent this field on the inquiry
     */
    public String getAwardInquiryTitle() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(AWARD_INQUIRY_TITLE_PROPERTY);
    }

    /**
     * Pretends to set the inquiry title
     */
    public void setAwardInquiryTitle(String inquiryTitle) {
        // ain't nothing to do
    }

    /**
     * Gets the preferredBillingFrequency attribute.
     * 
     * @return Returns the preferredBillingFrequency.
     */
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
     * Gets the preferredReportFrequency attribute.
     * 
     * @return Returns the preferredReportFrequency.
     */
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
     * Gets the suspendInvoicing attribute.
     * 
     * @return Returns the suspendInvoicing.
     */
    public boolean isSuspendInvoicing() {
        return suspendInvoicing;
    }

    /**
     * Sets the suspendInvoicing attribute value.
     * 
     * @param suspendInvoicing The suspendInvoicing to set.
     */
    public void setSuspendInvoicing(boolean suspendInvoicing) {
        this.suspendInvoicing = suspendInvoicing;
    }

    /**
     * Gets the additionalFormsRequired attribute.
     * 
     * @return Returns the additionalFormsRequired.
     */
    public boolean isAdditionalFormsRequired() {
        return additionalFormsRequired;
    }

    /**
     * Sets the additionalFormsRequired attribute value.
     * 
     * @param additionalFormsRequired The additionalFormsRequired to set.
     */
    public void setAdditionalFormsRequired(boolean additionalFormsRequired) {
        this.additionalFormsRequired = additionalFormsRequired;
    }

    /**
     * Gets the additionalFormsDescription attribute.
     * 
     * @return Returns the additionalFormsDescription.
     */
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
     * Gets the contractGrantType attribute.
     * 
     * @return Returns the contractGrantType.
     */
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
     * Gets the preferredReportTemplate attribute.
     * 
     * @return Returns the preferredReportTemplate.
     */
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
     * Gets the awardsourceOfFundsCode attribute.
     * 
     * @return Returns the awardsourceOfFundsCode.
     */
    public String getAwardsourceOfFundsCode() {
        return awardsourceOfFundsCode;
    }

    /**
     * Sets the awardsourceOfFundsCode attribute value.
     * 
     * @param awardsourceOfFundsCode The awardsourceOfFundsCode to set.
     */
    public void setAwardsourceOfFundsCode(String awardsourceOfFundsCode) {
        this.awardsourceOfFundsCode = awardsourceOfFundsCode;
    }

    /**
     * Gets the sourceOfFunds attribute.
     * 
     * @return Returns the sourceOfFunds.
     */
    public SourceOfFunds getSourceOfFunds() {
        return sourceOfFunds;
    }

    /**
     * Sets the sourceOfFunds attribute value.
     * 
     * @param sourceOfFunds The sourceOfFunds to set.
     */
    public void setSourceOfFunds(SourceOfFunds sourceOfFunds) {
        this.sourceOfFunds = sourceOfFunds;
    }

    /**
     * Gets the billingFrequency attribute.
     * 
     * @return Returns the billingFrequency.
     */
    public BillingFrequency getBillingFrequency() {
        return billingFrequency;
    }

    /**
     * Sets the billingFrequency attribute value.
     * 
     * @param billingFrequency The billingFrequency to set.
     */
    public void setBillingFrequency(BillingFrequency billingFrequency) {
        this.billingFrequency = billingFrequency;
    }

    /**
     * Gets the financialFormTemplate attribute.
     * 
     * @return Returns the financialFormTemplate.
     */
    public FinancialFormTemplate getFinancialFormTemplate() {
        return financialFormTemplate;
    }

    /**
     * Sets the financialFormTemplate attribute value.
     * 
     * @param financialFormTemplate The financialFormTemplate to set.
     */
    public void setFinancialFormTemplate(FinancialFormTemplate financialFormTemplate) {
        this.financialFormTemplate = financialFormTemplate;
    }

    /**
     * Gets the financialReportFrequencies attribute.
     * 
     * @return Returns the financialReportFrequencies.
     */
    public FinancialReportFrequencies getFinancialReportFrequencies() {
        return financialReportFrequencies;
    }

    /**
     * Sets the financialReportFrequencies attribute value.
     * 
     * @param financialReportFrequencies The financialReportFrequencies to set.
     */
    public void setFinancialReportFrequencies(FinancialReportFrequencies financialReportFrequencies) {
        this.financialReportFrequencies = financialReportFrequencies;
    }

    public List<Milestone> getMilestones() {
        // To get completed milestones only - Milestones that have a completion date filled
        List<Milestone> milestonesCompleted = new ArrayList<Milestone>();
        for (Milestone mlstn : milestones) {
            if (mlstn.getMilestoneActualCompletionDate() != null) {
                milestonesCompleted.add(mlstn);
            }
        }

        return milestonesCompleted;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

    /**
     * Gets the awardId attribute.
     * 
     * @return Returns the awardId.
     */
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
     * Gets the autoApprove attribute.
     * 
     * @return Returns the autoApprove.
     */
    public boolean getAutoApprove() {
        return autoApprove;
    }

    /**
     * Sets the autoApprove attribute value.
     * 
     * @param autoApprove The autoApprove to set.
     */
    public void setAutoApprove(boolean autoApprove) {
        this.autoApprove = autoApprove;
    }

    /**
     * Gets the minInvoiceAmount attribute.
     * 
     * @return Returns the minInvoiceAmount.
     */
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
     * Gets the invoicingOptions attribute.
     * 
     * @return Returns the invoicingOptions.
     */
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
     * Gets the milestoneSchedule attribute.
     * 
     * @return Returns the milestoneSchedule.
     */
    public MilestoneSchedule getMilestoneSchedule() {
        if (milestoneSchedule != null) {
            milestoneSchedule.setProposalNumber(proposalNumber);
        }
        return milestoneSchedule;
    }

    /**
     * Sets the milestoneSchedule attribute value.
     * 
     * @param milestoneSchedule The milestoneSchedule to set.
     */
    public void setMilestoneSchedule(MilestoneSchedule milestoneSchedule) {
        this.milestoneSchedule = milestoneSchedule;
    }


    public AwardProjectDirector getAwardPrimaryProjectDirector() {
        for (AwardProjectDirector awdProjMgr : awardProjectDirectors) {
            if (awdProjMgr != null && awdProjMgr.isAwardPrimaryProjectDirectorIndicator()) {
                return awdProjMgr;
            }
        }

        return null;
    }

    public void setAwardPrimaryProjectDirector(AwardProjectDirector awardPrimaryProjectDirector) {
        this.awardPrimaryProjectDirector = awardPrimaryProjectDirector;
    }

    /**
     * Gets the awardPrimaryFundManager attribute. This field would not be persisted into the DB, just for display purposes.
     * 
     * @return Returns the awardPrimaryFundManager.
     */
    public AwardFundManager getAwardPrimaryFundManager() {
        for (AwardFundManager awdFundMgr : awardFundManagers) {
            if (awdFundMgr != null && awdFundMgr.isAwardPrimaryFundManagerIndicator()) {
                return awdFundMgr;
            }
        }
        return null;
    }

    /**
     * Sets the awardPrimaryFundManager attribute value.
     * 
     * @param awardPrimaryFundManager The awardPrimaryFundManager to set.
     */
    public void setAwardPrimaryFundManager(AwardFundManager awardPrimaryFundManager) {
        this.awardPrimaryFundManager = awardPrimaryFundManager;
    }

    /**
     * Gets the awardInvoiceAccounts attribute.
     * 
     * @return Returns the awardInvoiceAccounts.
     */
    public List<AwardInvoiceAccount> getAwardInvoiceAccounts() {
        return awardInvoiceAccounts;
    }

    /**
     * Sets the awardInvoiceAccounts attribute value.
     * 
     * @param awardInvoiceAccounts The awardInvoiceAccounts to set.
     */
    public void setAwardInvoiceAccounts(List<AwardInvoiceAccount> awardInvoiceAccounts) {
        this.awardInvoiceAccounts = awardInvoiceAccounts;
    }

    /**
     * Gets the lookupFundMgrPersonUniversalIdentifier attribute.
     * 
     * @return Returns the lookupFundMgrPersonUniversalIdentifier.
     */
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
     * Gets the locCreationType attribute.
     * 
     * @return Returns the locCreationType.
     */
    public String getLocCreationType() {
        return locCreationType;
    }

    /**
     * Sets the locCreationType attribute value.
     * 
     * @param locCreationType The locCreationType to set.
     */
    public void setLocCreationType(String locCreationType) {
        this.locCreationType = locCreationType;
    }


    /**
     * Gets the fundingExpirationDate attribute.
     * 
     * @return Returns the fundingExpirationDate
     */
    public Date getFundingExpirationDate() {
        return fundingExpirationDate;
    }

    /**
     * Sets the fundingExpirationDate attribute.
     * 
     * @param awardEntryDate The fundingExpirationDate to set.
     */
    public void setFundingExpirationDate(Date fundingExpirationDate) {
        this.fundingExpirationDate = fundingExpirationDate;
    }

    /**
     * Gets the drawNumber attribute.
     * 
     * @return Returns the drawNumber
     */
    public String getDrawNumber() {
        return drawNumber;
    }


    /**
     * Sets the drawNumber attribute.
     * 
     * @param drawNumber The drawNumber to set.
     */

    public void setDrawNumber(String drawNumber) {
        this.drawNumber = drawNumber;
    }

    /**
     * Gets the bills attribute.
     * 
     * @return Returns the bills.
     */
    public List<Bill> getBills() {
        return bills;

    }

    /**
     * Sets the bills attribute value.
     * 
     * @param bills The bills to set.
     */

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    /**
     * Gets the commentText attribute.
     * 
     * @return Returns the commentText.
     */
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
     * Gets the dunningCampaign attribute.
     * 
     * @return Returns the dunningCampaign.
     */
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

    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }


    /**
     * Gets the stopWork attribute.
     * 
     * @return Returns the stopWork.
     */
    public boolean isStopWork() {
        return stopWork;
    }

    /**
     * Sets the stopWork attribute value.
     * 
     * @param stopWork The stopWork to set.
     */
    public void setStopWork(boolean stopWork) {
        this.stopWork = stopWork;
    }

    /**
     * Gets the list of active award invoice accounts. The integration object is used here - as this would be referred only from AR
     * module.
     * 
     * @return Returns the active awardInvoiceAccounts.
     */
    public List<ContractsGrantsAwardInvoiceAccountInformation> getActiveAwardInvoiceAccounts() {
        List<ContractsGrantsAwardInvoiceAccountInformation> activeAwardInvoiceAccounts = new ArrayList<ContractsGrantsAwardInvoiceAccountInformation>();
        List<AwardInvoiceAccount> awdInvAccts = new ArrayList<AwardInvoiceAccount>();
        for (AwardInvoiceAccount awardInvAccount : awardInvoiceAccounts) {
            if (awardInvAccount.isActive()) {
                awdInvAccts.add(awardInvAccount);
            }
        }
        activeAwardInvoiceAccounts = new ArrayList<ContractsGrantsAwardInvoiceAccountInformation>(awdInvAccts);
        return activeAwardInvoiceAccounts;

    }


    /**
     * This method maps the proposal number into a hash map with "proposalNumber" as the identifier.
     * 
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (ObjectUtils.isNotNull(this.proposalNumber)) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }
        m.put("awardId", this.awardId);

        return m;
    }
}
