/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.cg.bo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Award extends BusinessObjectBase {

    private Long proposalNumber;
    private Timestamp awardBeginningDate;
    private Timestamp awardEndingDate;
    private KualiDecimal awardTotalAmount;
    private String awardAddendumNumber;
    private KualiDecimal awardAllocatedUniversityComputingServicesAmount;
    private String agencyAwardNumber;
    private KualiDecimal federalPassThroughFundedAmount;
    private Timestamp awardEntryDate;
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
    private Timestamp awardClosingDate;
    private String proposalAwardTypeCode;
    private String financialSystemWorkgroupId;
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
    private List awardProjectDirector;
    private List awardAccount;
    private List awardSubcontractor;
    private List awardOrganization;
    private List awardDiary;

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
    public Award() {
        awardProjectDirector = new ArrayList();
        awardAccount = new ArrayList();
        awardSubcontractor = new ArrayList();
        awardOrganization = new ArrayList();
        awardDiary = new ArrayList();
    }

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return - Returns the proposalNumber
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
     * @return - Returns the awardBeginningDate
     * 
     */
    public Timestamp getAwardBeginningDate() {
        return awardBeginningDate;
    }

    /**
     * Sets the awardBeginningDate attribute.
     * 
     * @param awardBeginningDate The awardBeginningDate to set.
     * 
     */
    public void setAwardBeginningDate(Timestamp awardBeginningDate) {
        this.awardBeginningDate = awardBeginningDate;
    }


    /**
     * Gets the awardEndingDate attribute.
     * 
     * @return - Returns the awardEndingDate
     * 
     */
    public Timestamp getAwardEndingDate() {
        return awardEndingDate;
    }

    /**
     * Sets the awardEndingDate attribute.
     * 
     * @param awardEndingDate The awardEndingDate to set.
     * 
     */
    public void setAwardEndingDate(Timestamp awardEndingDate) {
        this.awardEndingDate = awardEndingDate;
    }


    /**
     * Gets the awardTotalAmount attribute.
     * 
     * @return - Returns the awardTotalAmount
     * 
     */
    public KualiDecimal getAwardTotalAmount() {
        return awardTotalAmount;
    }

    /**
     * Sets the awardTotalAmount attribute.
     * 
     * @param awardTotalAmount The awardTotalAmount to set.
     * 
     */
    public void setAwardTotalAmount(KualiDecimal awardTotalAmount) {
        this.awardTotalAmount = awardTotalAmount;
    }


    /**
     * Gets the awardAddendumNumber attribute.
     * 
     * @return - Returns the awardAddendumNumber
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
     * @return - Returns the awardAllocatedUniversityComputingServicesAmount
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
     * @return - Returns the agencyAwardNumber
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
     * @return - Returns the federalPassThroughFundedAmount
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
     * @return - Returns the awardEntryDate
     * 
     */
    public Timestamp getAwardEntryDate() {
        return awardEntryDate;
    }

    /**
     * Sets the awardEntryDate attribute.
     * 
     * @param awardEntryDate The awardEntryDate to set.
     * 
     */
    public void setAwardEntryDate(Timestamp awardEntryDate) {
        this.awardEntryDate = awardEntryDate;
    }


    /**
     * Gets the agencyFuture1Amount attribute.
     * 
     * @return - Returns the agencyFuture1Amount
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
     * @return - Returns the agencyFuture2Amount
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
     * @return - Returns the agencyFuture3Amount
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
     * @return - Returns the awardDocumentNumber
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
     * @return - Returns the awardLastUpdateDate
     * 
     */
    public Timestamp getAwardLastUpdateDate() {
        return awardLastUpdateDate;
    }

    /**
     * Sets the awardLastUpdateDate attribute.
     * 
     * @param awardLastUpdateDate The awardLastUpdateDate to set.
     * 
     */
    public void setAwardLastUpdateDate(Timestamp awardLastUpdateDate) {
        this.awardLastUpdateDate = awardLastUpdateDate;
    }


    /**
     * Gets the federalPassThroughIndicator attribute.
     * 
     * @return - Returns the federalPassThroughIndicator
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
     * @return - Returns the oldProposalNumber
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
     * @return - Returns the awardDirectCostAmount
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
     * @return - Returns the awardIndirectCostAmount
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
     * @return - Returns the federalFundedAmount
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
     * @return - Returns the awardCreateTimestamp
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
     * @return - Returns the awardClosingDate
     * 
     */
    public Timestamp getAwardClosingDate() {
        return awardClosingDate;
    }

    /**
     * Sets the awardClosingDate attribute.
     * 
     * @param awardClosingDate The awardClosingDate to set.
     * 
     */
    public void setAwardClosingDate(Timestamp awardClosingDate) {
        this.awardClosingDate = awardClosingDate;
    }


    /**
     * Gets the proposalAwardTypeCode attribute.
     * 
     * @return - Returns the proposalAwardTypeCode
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
     * Gets the financialSystemWorkgroupId attribute.
     * 
     * @return - Returns the financialSystemWorkgroupId
     * 
     */
    public String getFinancialSystemWorkgroupId() {
        return financialSystemWorkgroupId;
    }

    /**
     * Sets the financialSystemWorkgroupId attribute.
     * 
     * @param financialSystemWorkgroupId The financialSystemWorkgroupId to set.
     * 
     */
    public void setFinancialSystemWorkgroupId(String financialSystemWorkgroupId) {
        this.financialSystemWorkgroupId = financialSystemWorkgroupId;
    }


    /**
     * Gets the awardStatusCode attribute.
     * 
     * @return - Returns the awardStatusCode
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
     * @return - Returns the letterOfCreditFundGroupCode
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
     * @return - Returns the grantDescriptionCode
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
     * @return - Returns the agencyNumber
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
     * @return - Returns the federalPassThroughAgencyNumber
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
     * @return - Returns the agencyAnalystName
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
     * @return - Returns the analystTelephoneNumber
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
     * @return - Returns the awardProjectTitle
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
     * @return - Returns the awardCommentText
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
     * @return - Returns the awardPurposeCode
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
     * @return - Returns the proposal
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
     * @return - Returns the proposalAwardType
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
     * @return - Returns the awardStatus
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
     * @return - Returns the letterOfCreditFundGroup
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
     * @return - Returns the grantDescription
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
     * @return - Returns the agency
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
     * @return - Returns the federalPassThroughAgency
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
     * @return - Returns the awardPurpose
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
     * Gets the awardProjectDirector list.
     * 
     * @return - Returns the awardProjectDirector list
     * 
     */
    public List getAwardProjectDirector() {
        return awardProjectDirector;
    }

    /**
     * Sets the awardProjectDirector list.
     * 
     * @param awardProjectDirector The awardProjectDirector list to set.
     * 
     */
    public void setAwardProjectDirector(List awardProjectDirector) {
        this.awardProjectDirector = awardProjectDirector;
    }

    /**
     * @return Returns the awardAccount.
     */
    public List getAwardAccount() {
        return awardAccount;
    }

    /**
     * @param awardAccount The awardAccount to set.
     */
    public void setAwardAccount(List awardAccount) {
        this.awardAccount = awardAccount;
    }

    /**
     * @return Returns the awardDiary.
     */
    public List getAwardDiary() {
        return awardDiary;
    }

    /**
     * @param awardDiary The awardDiary to set.
     */
    public void setAwardDiary(List awardDiary) {
        this.awardDiary = awardDiary;
    }

    /**
     * @return Returns the awardOrganization.
     */
    public List getAwardOrganization() {
        return awardOrganization;
    }

    /**
     * @param awardOrganization The awardOrganization to set.
     */
    public void setAwardOrganization(List awardOrganization) {
        this.awardOrganization = awardOrganization;
    }

    /**
     * @return Returns the awardSubcontractor.
     */
    public List getAwardSubcontractor() {
        return awardSubcontractor;
    }

    /**
     * @param awardSubcontractor The awardSubcontractor to set.
     */
    public void setAwardSubcontractor(List awardSubcontractor) {
        this.awardSubcontractor = awardSubcontractor;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        return m;
    }

}
