/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.cg.bo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;

/**
 * 
 */
public class Award extends PersistableBusinessObjectBase {

    private Long proposalNumber;
    private Date awardBeginningDate;
    private Date awardEndingDate;

    /**
     * This field is for write-only to the database via OJB, not the corresponding property of this BO.
     * OJB uses reflection to read it, so the compiler warns because it doesn't know.
     * @see #getProposalTotalAmount
     * @see #setProposalTotalAmount
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private KualiDecimal awardTotalAmount;
    
    private String awardAddendumNumber;
    private KualiDecimal awardAllocatedUniversityComputingServicesAmount;
    private String agencyAwardNumber;
    private KualiDecimal federalPassThroughFundedAmount;
    private Date awardEntryDate;
    private KualiDecimal agencyFuture1Amount;
    private KualiDecimal agencyFuture2Amount;
    private KualiDecimal agencyFuture3Amount;
    private String awardDocumentNumber;
    private Date awardLastUpdateDate;
    private boolean federalPassThroughIndicator;
    private String oldProposalNumber;
    private KualiDecimal awardDirectCostAmount;
    private KualiDecimal awardIndirectCostAmount;
    private KualiDecimal federalFundedAmount;
    private Timestamp awardCreateTimestamp;
    private Date awardClosingDate;
    private String proposalAwardTypeCode;
    private String workgroupName;
    private String awardStatusCode;
    private String letterOfCreditFundGroupCode;
    private String grantDescriptionCode;
    private String agencyNumber;
    private String federalPassThroughAgencyNumber;
    private String agencyAnalystName;
    private String analystTelephoneNumber;
    private String awardProjectTitle;
    private String awardCommentText;
    private String awardPurposeCode;
    private List<AwardProjectDirector> awardProjectDirectors;
    private List<AwardAccount> awardAccounts;
    private List<AwardSubcontractor> awardSubcontractors;
    private List<AwardOrganization> awardOrganizations;

    private Proposal proposal;
    private ProposalAwardType proposalAwardType;
    private AwardStatus awardStatus;
    private LetterOfCreditFundGroup letterOfCreditFundGroup;
    private GrantDescription grantDescription;
    private Agency agency;
    private Agency federalPassThroughAgency;
    private ProposalPurpose awardPurpose;

    /**
     * Default constructor.
     */
    @SuppressWarnings({"unchecked"})  // todo: generify TypedArrayList and rename to something appropriate like AlwaysGettableArrayList
    
    public Award() {
        // Must use TypedArrayList because its get() method automatically grows the array for Struts.
        awardProjectDirectors = new TypedArrayList(AwardProjectDirector.class);
        awardAccounts = new TypedArrayList(AwardAccount.class);
        awardSubcontractors = new TypedArrayList(AwardSubcontractor.class);
        awardOrganizations = new TypedArrayList(AwardOrganization.class);
    }

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     * 
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     * 
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /**
     * Gets the awardBeginningDate attribute.
     * 
     * @return Returns the awardBeginningDate
     * 
     */
    public Date getAwardBeginningDate() {
        return awardBeginningDate;
    }

    /**
     * Sets the awardBeginningDate attribute.
     * 
     * @param awardBeginningDate The awardBeginningDate to set.
     * 
     */
    public void setAwardBeginningDate(Date awardBeginningDate) {
        this.awardBeginningDate = awardBeginningDate;
    }


    /**
     * Gets the awardEndingDate attribute.
     * 
     * @return Returns the awardEndingDate
     * 
     */
    public Date getAwardEndingDate() {
        return awardEndingDate;
    }

    /**
     * Sets the awardEndingDate attribute.
     * 
     * @param awardEndingDate The awardEndingDate to set.
     * 
     */
    public void setAwardEndingDate(Date awardEndingDate) {
        this.awardEndingDate = awardEndingDate;
    }


    /**
     * Gets the awardTotalAmount attribute.
     * 
     * @return Returns the awardTotalAmount
     * 
     */
    public KualiDecimal getAwardTotalAmount() {
        KualiDecimal direct = getAwardDirectCostAmount();
        KualiDecimal indirect = getAwardIndirectCostAmount();
        return ObjectUtils.isNull(direct) || ObjectUtils.isNull(indirect) ? null : direct.add(indirect);
    }

    /**
     * Does nothing.  This property is determined by the direct and indirect cost amounts.
     * This setter is here only because without it, the maintenance framework won't display this attribute.
     * 
     * @param awardTotalAmount The awardTotalAmount to set.
     * 
     */
    public void setAwardTotalAmount(KualiDecimal awardTotalAmount) {
        // do nothing
    }

    /**
     * OJB calls this method as the first operation before this BO is inserted into the database.
     * The database contains CGAWD_TOT_AMT, a denormalized column that
     * Kuali does not use but needs to maintain with this method because OJB bypasses the getter.
     * 
     * @param persistenceBroker from OJB
     * @throws PersistenceBrokerException
     */
    @Override
    public void beforeInsert(PersistenceBroker persistenceBroker)
        throws PersistenceBrokerException
    {
        super.beforeInsert(persistenceBroker);
        awardTotalAmount = getAwardTotalAmount();
    }

    /**
     * OJB calls this method as the first operation before this BO is updated to the database.
     * The database contains CGAWD_TOT_AMT, a denormalized column that
     * Kuali does not use but needs to maintain with this method because OJB bypasses the getter.
     * 
     * @param persistenceBroker from OJB
     * @throws PersistenceBrokerException
     */
    @Override
    public void beforeUpdate(PersistenceBroker persistenceBroker)
        throws PersistenceBrokerException
    {
        super.beforeUpdate(persistenceBroker);
        awardTotalAmount = getAwardTotalAmount();
    }


    /**
     * Gets the awardAddendumNumber attribute.
     * 
     * @return Returns the awardAddendumNumber
     * 
     */
    public String getAwardAddendumNumber() {
        return awardAddendumNumber;
    }

    /**
     * Sets the awardAddendumNumber attribute.
     * 
     * @param awardAddendumNumber The awardAddendumNumber to set.
     * 
     */
    public void setAwardAddendumNumber(String awardAddendumNumber) {
        this.awardAddendumNumber = awardAddendumNumber;
    }


    /**
     * Gets the awardAllocatedUniversityComputingServicesAmount attribute.
     * 
     * @return Returns the awardAllocatedUniversityComputingServicesAmount
     * 
     */
    public KualiDecimal getAwardAllocatedUniversityComputingServicesAmount() {
        return awardAllocatedUniversityComputingServicesAmount;
    }

    /**
     * Sets the awardAllocatedUniversityComputingServicesAmount attribute.
     * 
     * @param awardAllocatedUniversityComputingServicesAmount The awardAllocatedUniversityComputingServicesAmount to set.
     * 
     */
    public void setAwardAllocatedUniversityComputingServicesAmount(KualiDecimal awardAllocatedUniversityComputingServicesAmount) {
        this.awardAllocatedUniversityComputingServicesAmount = awardAllocatedUniversityComputingServicesAmount;
    }


    /**
     * Gets the agencyAwardNumber attribute.
     * 
     * @return Returns the agencyAwardNumber
     * 
     */
    public String getAgencyAwardNumber() {
        return agencyAwardNumber;
    }

    /**
     * Sets the agencyAwardNumber attribute.
     * 
     * @param agencyAwardNumber The agencyAwardNumber to set.
     * 
     */
    public void setAgencyAwardNumber(String agencyAwardNumber) {
        this.agencyAwardNumber = agencyAwardNumber;
    }


    /**
     * Gets the federalPassThroughFundedAmount attribute.
     * 
     * @return Returns the federalPassThroughFundedAmount
     * 
     */
    public KualiDecimal getFederalPassThroughFundedAmount() {
        return federalPassThroughFundedAmount;
    }

    /**
     * Sets the federalPassThroughFundedAmount attribute.
     * 
     * @param federalPassThroughFundedAmount The federalPassThroughFundedAmount to set.
     * 
     */
    public void setFederalPassThroughFundedAmount(KualiDecimal federalPassThroughFundedAmount) {
        this.federalPassThroughFundedAmount = federalPassThroughFundedAmount;
    }


    /**
     * Gets the awardEntryDate attribute.
     * 
     * @return Returns the awardEntryDate
     * 
     */
    public Date getAwardEntryDate() {
        return awardEntryDate;
    }

    /**
     * Sets the awardEntryDate attribute.
     * 
     * @param awardEntryDate The awardEntryDate to set.
     * 
     */
    public void setAwardEntryDate(Date awardEntryDate) {
        this.awardEntryDate = awardEntryDate;
    }


    /**
     * Gets the agencyFuture1Amount attribute.
     * 
     * @return Returns the agencyFuture1Amount
     * 
     */
    public KualiDecimal getAgencyFuture1Amount() {
        return agencyFuture1Amount;
    }

    /**
     * Sets the agencyFuture1Amount attribute.
     * 
     * @param agencyFuture1Amount The agencyFuture1Amount to set.
     * 
     */
    public void setAgencyFuture1Amount(KualiDecimal agencyFuture1Amount) {
        this.agencyFuture1Amount = agencyFuture1Amount;
    }


    /**
     * Gets the agencyFuture2Amount attribute.
     * 
     * @return Returns the agencyFuture2Amount
     * 
     */
    public KualiDecimal getAgencyFuture2Amount() {
        return agencyFuture2Amount;
    }

    /**
     * Sets the agencyFuture2Amount attribute.
     * 
     * @param agencyFuture2Amount The agencyFuture2Amount to set.
     * 
     */
    public void setAgencyFuture2Amount(KualiDecimal agencyFuture2Amount) {
        this.agencyFuture2Amount = agencyFuture2Amount;
    }


    /**
     * Gets the agencyFuture3Amount attribute.
     * 
     * @return Returns the agencyFuture3Amount
     * 
     */
    public KualiDecimal getAgencyFuture3Amount() {
        return agencyFuture3Amount;
    }

    /**
     * Sets the agencyFuture3Amount attribute.
     * 
     * @param agencyFuture3Amount The agencyFuture3Amount to set.
     * 
     */
    public void setAgencyFuture3Amount(KualiDecimal agencyFuture3Amount) {
        this.agencyFuture3Amount = agencyFuture3Amount;
    }


    /**
     * Gets the awardDocumentNumber attribute.
     * 
     * @return Returns the awardDocumentNumber
     * 
     */
    public String getAwardDocumentNumber() {
        return awardDocumentNumber;
    }

    /**
     * Sets the awardDocumentNumber attribute.
     * 
     * @param awardDocumentNumber The awardDocumentNumber to set.
     * 
     */
    public void setAwardDocumentNumber(String awardDocumentNumber) {
        this.awardDocumentNumber = awardDocumentNumber;
    }


    /**
     * Gets the awardLastUpdateDate attribute.
     * 
     * @return Returns the awardLastUpdateDate
     * 
     */
    public Date getAwardLastUpdateDate() {
        return awardLastUpdateDate;
    }

    /**
     * Sets the awardLastUpdateDate attribute.
     * 
     * @param awardLastUpdateDate The awardLastUpdateDate to set.
     * 
     */
    public void setAwardLastUpdateDate(Date awardLastUpdateDate) {
        this.awardLastUpdateDate = awardLastUpdateDate;
    }


    /**
     * Gets the federalPassThroughIndicator attribute.
     * 
     * @return Returns the federalPassThroughIndicator
     * 
     */
    public boolean getFederalPassThroughIndicator() {
        return federalPassThroughIndicator;
    }

    /**
     * Sets the federalPassThroughIndicator attribute.
     * 
     * @param federalPassThroughIndicator The federalPassThroughIndicator to set.
     * 
     */
    public void setFederalPassThroughIndicator(boolean federalPassThroughIndicator) {
        this.federalPassThroughIndicator = federalPassThroughIndicator;
    }


    /**
     * Gets the oldProposalNumber attribute.
     * 
     * @return Returns the oldProposalNumber
     * 
     */
    public String getOldProposalNumber() {
        return oldProposalNumber;
    }

    /**
     * Sets the oldProposalNumber attribute.
     * 
     * @param oldProposalNumber The oldProposalNumber to set.
     * 
     */
    public void setOldProposalNumber(String oldProposalNumber) {
        this.oldProposalNumber = oldProposalNumber;
    }


    /**
     * Gets the awardDirectCostAmount attribute.
     * 
     * @return Returns the awardDirectCostAmount
     * 
     */
    public KualiDecimal getAwardDirectCostAmount() {
        return awardDirectCostAmount;
    }

    /**
     * Sets the awardDirectCostAmount attribute.
     * 
     * @param awardDirectCostAmount The awardDirectCostAmount to set.
     * 
     */
    public void setAwardDirectCostAmount(KualiDecimal awardDirectCostAmount) {
        this.awardDirectCostAmount = awardDirectCostAmount;
    }


    /**
     * Gets the awardIndirectCostAmount attribute.
     * 
     * @return Returns the awardIndirectCostAmount
     * 
     */
    public KualiDecimal getAwardIndirectCostAmount() {
        return awardIndirectCostAmount;
    }

    /**
     * Sets the awardIndirectCostAmount attribute.
     * 
     * @param awardIndirectCostAmount The awardIndirectCostAmount to set.
     * 
     */
    public void setAwardIndirectCostAmount(KualiDecimal awardIndirectCostAmount) {
        this.awardIndirectCostAmount = awardIndirectCostAmount;
    }


    /**
     * Gets the federalFundedAmount attribute.
     * 
     * @return Returns the federalFundedAmount
     * 
     */
    public KualiDecimal getFederalFundedAmount() {
        return federalFundedAmount;
    }

    /**
     * Sets the federalFundedAmount attribute.
     * 
     * @param federalFundedAmount The federalFundedAmount to set.
     * 
     */
    public void setFederalFundedAmount(KualiDecimal federalFundedAmount) {
        this.federalFundedAmount = federalFundedAmount;
    }


    /**
     * Gets the awardCreateTimestamp attribute.
     * 
     * @return Returns the awardCreateTimestamp
     * 
     */
    public Timestamp getAwardCreateTimestamp() {
        return awardCreateTimestamp;
    }

    /**
     * Sets the awardCreateTimestamp attribute.
     * 
     * @param awardCreateTimestamp The awardCreateTimestamp to set.
     * 
     */
    public void setAwardCreateTimestamp(Timestamp awardCreateTimestamp) {
        this.awardCreateTimestamp = awardCreateTimestamp;
    }


    /**
     * Gets the awardClosingDate attribute.
     * 
     * @return Returns the awardClosingDate
     * 
     */
    public Date getAwardClosingDate() {
        return awardClosingDate;
    }

    /**
     * Sets the awardClosingDate attribute.
     * 
     * @param awardClosingDate The awardClosingDate to set.
     * 
     */
    public void setAwardClosingDate(Date awardClosingDate) {
        this.awardClosingDate = awardClosingDate;
    }


    /**
     * Gets the proposalAwardTypeCode attribute.
     * 
     * @return Returns the proposalAwardTypeCode
     * 
     */
    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }

    /**
     * Sets the proposalAwardTypeCode attribute.
     * 
     * @param proposalAwardTypeCode The proposalAwardTypeCode to set.
     * 
     */
    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }


    /**
     * Gets the workgroupName attribute.
     * 
     * @return Returns the workgroupName
     * 
     */
    public String getWorkgroupName() {
        return workgroupName;
    }

    /**
     * Sets the workgroupName attribute.
     * 
     * @param workgroupName The workgroupName to set.
     * 
     */
    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }


    /**
     * Gets the awardStatusCode attribute.
     * 
     * @return Returns the awardStatusCode
     * 
     */
    public String getAwardStatusCode() {
        return awardStatusCode;
    }

    /**
     * Sets the awardStatusCode attribute.
     * 
     * @param awardStatusCode The awardStatusCode to set.
     * 
     */
    public void setAwardStatusCode(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }


    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     * 
     * @return Returns the letterOfCreditFundGroupCode
     * 
     */
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    /**
     * Sets the letterOfCreditFundGroupCode attribute.
     * 
     * @param letterOfCreditFundGroupCode The letterOfCreditFundGroupCode to set.
     * 
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }


    /**
     * Gets the grantDescriptionCode attribute.
     * 
     * @return Returns the grantDescriptionCode
     * 
     */
    public String getGrantDescriptionCode() {
        return grantDescriptionCode;
    }

    /**
     * Sets the grantDescriptionCode attribute.
     * 
     * @param grantDescriptionCode The grantDescriptionCode to set.
     * 
     */
    public void setGrantDescriptionCode(String grantDescriptionCode) {
        this.grantDescriptionCode = grantDescriptionCode;
    }


    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber
     * 
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agencyNumber to set.
     * 
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }


    /**
     * Gets the federalPassThroughAgencyNumber attribute.
     * 
     * @return Returns the federalPassThroughAgencyNumber
     * 
     */
    public String getFederalPassThroughAgencyNumber() {
        return federalPassThroughAgencyNumber;
    }

    /**
     * Sets the federalPassThroughAgencyNumber attribute.
     * 
     * @param federalPassThroughAgencyNumber The federalPassThroughAgencyNumber to set.
     * 
     */
    public void setFederalPassThroughAgencyNumber(String federalPassThroughAgencyNumber) {
        this.federalPassThroughAgencyNumber = federalPassThroughAgencyNumber;
    }


    /**
     * Gets the agencyAnalystName attribute.
     * 
     * @return Returns the agencyAnalystName
     * 
     */
    public String getAgencyAnalystName() {
        return agencyAnalystName;
    }

    /**
     * Sets the agencyAnalystName attribute.
     * 
     * @param agencyAnalystName The agencyAnalystName to set.
     * 
     */
    public void setAgencyAnalystName(String agencyAnalystName) {
        this.agencyAnalystName = agencyAnalystName;
    }


    /**
     * Gets the analystTelephoneNumber attribute.
     * 
     * @return Returns the analystTelephoneNumber
     * 
     */
    public String getAnalystTelephoneNumber() {
        return analystTelephoneNumber;
    }

    /**
     * Sets the analystTelephoneNumber attribute.
     * 
     * @param analystTelephoneNumber The analystTelephoneNumber to set.
     * 
     */
    public void setAnalystTelephoneNumber(String analystTelephoneNumber) {
        this.analystTelephoneNumber = analystTelephoneNumber;
    }


    /**
     * Gets the awardProjectTitle attribute.
     * 
     * @return Returns the awardProjectTitle
     * 
     */
    public String getAwardProjectTitle() {
        return awardProjectTitle;
    }

    /**
     * Sets the awardProjectTitle attribute.
     * 
     * @param awardProjectTitle The awardProjectTitle to set.
     * 
     */
    public void setAwardProjectTitle(String awardProjectTitle) {
        this.awardProjectTitle = awardProjectTitle;
    }


    /**
     * Gets the awardCommentText attribute.
     * 
     * @return Returns the awardCommentText
     * 
     */
    public String getAwardCommentText() {
        return awardCommentText;
    }

    /**
     * Sets the awardCommentText attribute.
     * 
     * @param awardCommentText The awardCommentText to set.
     * 
     */
    public void setAwardCommentText(String awardCommentText) {
        this.awardCommentText = awardCommentText;
    }


    /**
     * Gets the awardPurposeCode attribute.
     * 
     * @return Returns the awardPurposeCode
     * 
     */
    public String getAwardPurposeCode() {
        return awardPurposeCode;
    }

    /**
     * Sets the awardPurposeCode attribute.
     * 
     * @param awardPurposeCode The awardPurposeCode to set.
     * 
     */
    public void setAwardPurposeCode(String awardPurposeCode) {
        this.awardPurposeCode = awardPurposeCode;
    }


    /**
     * Gets the proposal attribute.
     * 
     * @return Returns the proposal
     * 
     */
    public Proposal getProposal() {
        return proposal;
    }

    /**
     * Sets the proposal attribute.
     * 
     * @param proposal The proposal to set.
     * @deprecated
     */
    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    /**
     * Gets the proposalAwardType attribute.
     * 
     * @return Returns the proposalAwardType
     * 
     */
    public ProposalAwardType getProposalAwardType() {
        return proposalAwardType;
    }

    /**
     * Sets the proposalAwardType attribute.
     * 
     * @param proposalAwardType The proposalAwardType to set.
     * @deprecated
     */
    public void setProposalAwardType(ProposalAwardType proposalAwardType) {
        this.proposalAwardType = proposalAwardType;
    }

    /**
     * Gets the awardStatus attribute.
     * 
     * @return Returns the awardStatus
     * 
     */
    public AwardStatus getAwardStatus() {
        return awardStatus;
    }

    /**
     * Sets the awardStatus attribute.
     * 
     * @param awardStatus The awardStatus to set.
     * @deprecated
     */
    public void setAwardStatus(AwardStatus awardStatus) {
        this.awardStatus = awardStatus;
    }

    /**
     * Gets the letterOfCreditFundGroup attribute.
     * 
     * @return Returns the letterOfCreditFundGroup
     * 
     */
    public LetterOfCreditFundGroup getLetterOfCreditFundGroup() {
        return letterOfCreditFundGroup;
    }

    /**
     * Sets the letterOfCreditFundGroup attribute.
     * 
     * @param letterOfCreditFundGroup The letterOfCreditFundGroup to set.
     * @deprecated
     */
    public void setLetterOfCreditFundGroup(LetterOfCreditFundGroup letterOfCreditFundGroup) {
        this.letterOfCreditFundGroup = letterOfCreditFundGroup;
    }

    /**
     * Gets the grantDescription attribute.
     * 
     * @return Returns the grantDescription
     * 
     */
    public GrantDescription getGrantDescription() {
        return grantDescription;
    }

    /**
     * Sets the grantDescription attribute.
     * 
     * @param grantDescription The grantDescription to set.
     * @deprecated
     */
    public void setGrantDescription(GrantDescription grantDescription) {
        this.grantDescription = grantDescription;
    }

    /**
     * Gets the agency attribute.
     * 
     * @return Returns the agency
     * 
     */
    public Agency getAgency() {
        return agency;
    }

    /**
     * Sets the agency attribute.
     * 
     * @param agency The agency to set.
     * @deprecated
     */
    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    /**
     * Gets the federalPassThroughAgency attribute.
     * 
     * @return Returns the federalPassThroughAgency
     * 
     */
    public Agency getFederalPassThroughAgency() {
        return federalPassThroughAgency;
    }

    /**
     * Sets the federalPassThroughAgency attribute.
     * 
     * @param federalPassThroughAgency The federalPassThroughAgency to set.
     * @deprecated
     */
    public void setFederalPassThroughAgency(Agency federalPassThroughAgency) {
        this.federalPassThroughAgency = federalPassThroughAgency;
    }

    /**
     * Gets the awardPurpose attribute.
     * 
     * @return Returns the awardPurpose
     * 
     */
    public ProposalPurpose getAwardPurpose() {
        return awardPurpose;
    }

    /**
     * Sets the awardPurpose attribute.
     * 
     * @param awardPurpose The awardPurpose to set.
     * @deprecated
     */
    public void setAwardPurpose(ProposalPurpose awardPurpose) {
        this.awardPurpose = awardPurpose;
    }

    /**
     * Gets the awardProjectDirectors list.
     * 
     * @return Returns the awardProjectDirectors list
     * 
     */
    public List<AwardProjectDirector> getAwardProjectDirectors() {
        return awardProjectDirectors;
    }

    /**
     * Sets the awardProjectDirectors list.
     * 
     * @param awardProjectDirectors The awardProjectDirectors list to set.
     * 
     */
    public void setAwardProjectDirectors(List<AwardProjectDirector> awardProjectDirectors) {
        this.awardProjectDirectors = awardProjectDirectors;
    }

    /**
     * @return Returns the awardAccounts.
     */
    public List<AwardAccount> getAwardAccounts() {
        return awardAccounts;
    }

    /**
     * @param awardAccounts The awardAccounts to set.
     */
    public void setAwardAccounts(List<AwardAccount> awardAccounts) {
        this.awardAccounts = awardAccounts;
    }

    /**
     * @return Returns the awardOrganizations.
     */
    public List<AwardOrganization> getAwardOrganizations() {
        return awardOrganizations;
    }

    /**
     * @param awardOrganizations The awardOrganizations to set.
     */
    public void setAwardOrganizations(List<AwardOrganization> awardOrganizations) {
        this.awardOrganizations = awardOrganizations;
    }

    /**
     * @return Returns the awardSubcontractors.
     */
    public List<AwardSubcontractor> getAwardSubcontractors() {
        return awardSubcontractors;
    }

    /**
     * @param awardSubcontractors The awardSubcontractors to set.
     */
    public void setAwardSubcontractors(List<AwardSubcontractor> awardSubcontractors) {
        this.awardSubcontractors = awardSubcontractors;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        return m;
    }

}
