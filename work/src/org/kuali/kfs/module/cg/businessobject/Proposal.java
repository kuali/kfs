/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/Proposal.java,v $
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * 
 */
public class Proposal extends BusinessObjectBase {

    private Long proposalNumber;
    private Timestamp proposalBeginningDate;
    private Timestamp proposalEndingDate;
    private KualiDecimal proposalTotalAmount;
    private KualiDecimal proposalDirectCostAmount;
    private KualiDecimal proposalIndirectCostAmount;
    private Timestamp proposalRejectedDate;
    private Timestamp proposalLastUpdateDate;
    private Timestamp proposalDueDate;
    private KualiDecimal proposalTotalProjectAmount;
    private Timestamp proposalSubmissionDate;
    private boolean proposalFederalPassThroughIndicator;
    private String oldProposalNumber;
    private String grantNumber;
    private Timestamp proposalClosingDate;
    private String proposalAwardTypeCode;
    private String agencyNumber;
    private String proposalStatusCode;
    private String federalPassThroughAgencyNumber;
    private String cfdaNumber;
    private String proposalFellowName;
    private String proposalPurposeCode;
    private String proposalProjectTitle;
    private List proposalSubcontractor;
    private List proposalOrganization;
    private List proposalProjectDirector;
    private List proposalDiary;

    private ResearchType researchType;
    private ProposalAwardType proposalAwardType;
    private Agency agency;
    private ProposalStatus proposalStatus;
    private Agency federalPassThroughAgency;
    private ProposalPurpose proposalPurpose;

    /**
     * Default constructor.
     */
    public Proposal() {
        proposalSubcontractor = new ArrayList();
        proposalOrganization = new ArrayList();
        proposalProjectDirector = new ArrayList();
        proposalDiary = new ArrayList();
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
     * Gets the proposalBeginningDate attribute.
     * 
     * @return Returns the proposalBeginningDate
     * 
     */
    public Timestamp getProposalBeginningDate() {
        return proposalBeginningDate;
    }

    /**
     * Sets the proposalBeginningDate attribute.
     * 
     * @param proposalBeginningDate The proposalBeginningDate to set.
     * 
     */
    public void setProposalBeginningDate(Timestamp proposalBeginningDate) {
        this.proposalBeginningDate = proposalBeginningDate;
    }


    /**
     * Gets the proposalEndingDate attribute.
     * 
     * @return Returns the proposalEndingDate
     * 
     */
    public Timestamp getProposalEndingDate() {
        return proposalEndingDate;
    }

    /**
     * Sets the proposalEndingDate attribute.
     * 
     * @param proposalEndingDate The proposalEndingDate to set.
     * 
     */
    public void setProposalEndingDate(Timestamp proposalEndingDate) {
        this.proposalEndingDate = proposalEndingDate;
    }


    /**
     * Gets the proposalTotalAmount attribute.
     * 
     * @return Returns the proposalTotalAmount
     * 
     */
    public KualiDecimal getProposalTotalAmount() {
        return proposalTotalAmount;
    }

    /**
     * Sets the proposalTotalAmount attribute.
     * 
     * @param proposalTotalAmount The proposalTotalAmount to set.
     * 
     */
    public void setProposalTotalAmount(KualiDecimal proposalTotalAmount) {
        this.proposalTotalAmount = proposalTotalAmount;
    }


    /**
     * Gets the proposalDirectCostAmount attribute.
     * 
     * @return Returns the proposalDirectCostAmount
     * 
     */
    public KualiDecimal getProposalDirectCostAmount() {
        return proposalDirectCostAmount;
    }

    /**
     * Sets the proposalDirectCostAmount attribute.
     * 
     * @param proposalDirectCostAmount The proposalDirectCostAmount to set.
     * 
     */
    public void setProposalDirectCostAmount(KualiDecimal proposalDirectCostAmount) {
        this.proposalDirectCostAmount = proposalDirectCostAmount;
    }


    /**
     * Gets the proposalIndirectCostAmount attribute.
     * 
     * @return Returns the proposalIndirectCostAmount
     * 
     */
    public KualiDecimal getProposalIndirectCostAmount() {
        return proposalIndirectCostAmount;
    }

    /**
     * Sets the proposalIndirectCostAmount attribute.
     * 
     * @param proposalIndirectCostAmount The proposalIndirectCostAmount to set.
     * 
     */
    public void setProposalIndirectCostAmount(KualiDecimal proposalIndirectCostAmount) {
        this.proposalIndirectCostAmount = proposalIndirectCostAmount;
    }


    /**
     * Gets the proposalRejectedDate attribute.
     * 
     * @return Returns the proposalRejectedDate
     * 
     */
    public Timestamp getProposalRejectedDate() {
        return proposalRejectedDate;
    }

    /**
     * Sets the proposalRejectedDate attribute.
     * 
     * @param proposalRejectedDate The proposalRejectedDate to set.
     * 
     */
    public void setProposalRejectedDate(Timestamp proposalRejectedDate) {
        this.proposalRejectedDate = proposalRejectedDate;
    }


    /**
     * Gets the proposalLastUpdateDate attribute.
     * 
     * @return Returns the proposalLastUpdateDate
     * 
     */
    public Timestamp getProposalLastUpdateDate() {
        return proposalLastUpdateDate;
    }

    /**
     * Sets the proposalLastUpdateDate attribute.
     * 
     * @param proposalLastUpdateDate The proposalLastUpdateDate to set.
     * 
     */
    public void setProposalLastUpdateDate(Timestamp proposalLastUpdateDate) {
        this.proposalLastUpdateDate = proposalLastUpdateDate;
    }


    /**
     * Gets the proposalDueDate attribute.
     * 
     * @return Returns the proposalDueDate
     * 
     */
    public Timestamp getProposalDueDate() {
        return proposalDueDate;
    }

    /**
     * Sets the proposalDueDate attribute.
     * 
     * @param proposalDueDate The proposalDueDate to set.
     * 
     */
    public void setProposalDueDate(Timestamp proposalDueDate) {
        this.proposalDueDate = proposalDueDate;
    }


    /**
     * Gets the proposalTotalProjectAmount attribute.
     * 
     * @return Returns the proposalTotalProjectAmount
     * 
     */
    public KualiDecimal getProposalTotalProjectAmount() {
        return proposalTotalProjectAmount;
    }

    /**
     * Sets the proposalTotalProjectAmount attribute.
     * 
     * @param proposalTotalProjectAmount The proposalTotalProjectAmount to set.
     * 
     */
    public void setProposalTotalProjectAmount(KualiDecimal proposalTotalProjectAmount) {
        this.proposalTotalProjectAmount = proposalTotalProjectAmount;
    }


    /**
     * Gets the proposalSubmissionDate attribute.
     * 
     * @return Returns the proposalSubmissionDate
     * 
     */
    public Timestamp getProposalSubmissionDate() {
        return proposalSubmissionDate;
    }

    /**
     * Sets the proposalSubmissionDate attribute.
     * 
     * @param proposalSubmissionDate The proposalSubmissionDate to set.
     * 
     */
    public void setProposalSubmissionDate(Timestamp proposalSubmissionDate) {
        this.proposalSubmissionDate = proposalSubmissionDate;
    }


    /**
     * Gets the proposalFederalPassThroughIndicator attribute.
     * 
     * @return Returns the proposalFederalPassThroughIndicator
     * 
     */
    public boolean getProposalFederalPassThroughIndicator() {
        return proposalFederalPassThroughIndicator;
    }

    /**
     * Sets the proposalFederalPassThroughIndicator attribute.
     * 
     * @param proposalFederalPassThroughIndicator The proposalFederalPassThroughIndicator to set.
     * 
     */
    public void setProposalFederalPassThroughIndicator(boolean proposalFederalPassThroughIndicator) {
        this.proposalFederalPassThroughIndicator = proposalFederalPassThroughIndicator;
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
     * Gets the grantNumber attribute.
     * 
     * @return Returns the grantNumber
     * 
     */
    public String getGrantNumber() {
        return grantNumber;
    }

    /**
     * Sets the grantNumber attribute.
     * 
     * @param grantNumber The grantNumber to set.
     * 
     */
    public void setGrantNumber(String grantNumber) {
        this.grantNumber = grantNumber;
    }


    /**
     * Gets the proposalClosingDate attribute.
     * 
     * @return Returns the proposalClosingDate
     * 
     */
    public Timestamp getProposalClosingDate() {
        return proposalClosingDate;
    }

    /**
     * Sets the proposalClosingDate attribute.
     * 
     * @param proposalClosingDate The proposalClosingDate to set.
     * 
     */
    public void setProposalClosingDate(Timestamp proposalClosingDate) {
        this.proposalClosingDate = proposalClosingDate;
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
     * Gets the proposalStatusCode attribute.
     * 
     * @return Returns the proposalStatusCode
     * 
     */
    public String getProposalStatusCode() {
        return proposalStatusCode;
    }

    /**
     * Sets the proposalStatusCode attribute.
     * 
     * @param proposalStatusCode The proposalStatusCode to set.
     * 
     */
    public void setProposalStatusCode(String proposalStatusCode) {
        this.proposalStatusCode = proposalStatusCode;
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
     * Gets the cfdaNumber attribute.
     * 
     * @return Returns the cfdaNumber
     * 
     */
    public String getCfdaNumber() {
        return cfdaNumber;
    }

    /**
     * Sets the cfdaNumber attribute.
     * 
     * @param cfdaNumber The cfdaNumber to set.
     * 
     */
    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }


    /**
     * Gets the proposalFellowName attribute.
     * 
     * @return Returns the proposalFellowName
     * 
     */
    public String getProposalFellowName() {
        return proposalFellowName;
    }

    /**
     * Sets the proposalFellowName attribute.
     * 
     * @param proposalFellowName The proposalFellowName to set.
     * 
     */
    public void setProposalFellowName(String proposalFellowName) {
        this.proposalFellowName = proposalFellowName;
    }


    /**
     * Gets the proposalPurposeCode attribute.
     * 
     * @return Returns the proposalPurposeCode
     * 
     */
    public String getProposalPurposeCode() {
        return proposalPurposeCode;
    }

    /**
     * Sets the proposalPurposeCode attribute.
     * 
     * @param proposalPurposeCode The proposalPurposeCode to set.
     * 
     */
    public void setProposalPurposeCode(String proposalPurposeCode) {
        this.proposalPurposeCode = proposalPurposeCode;
    }


    /**
     * Gets the proposalProjectTitle attribute.
     * 
     * @return Returns the proposalProjectTitle
     * 
     */
    public String getProposalProjectTitle() {
        return proposalProjectTitle;
    }

    /**
     * Sets the proposalProjectTitle attribute.
     * 
     * @param proposalProjectTitle The proposalProjectTitle to set.
     * 
     */
    public void setProposalProjectTitle(String proposalProjectTitle) {
        this.proposalProjectTitle = proposalProjectTitle;
    }


    /**
     * Gets the researchType attribute.
     * 
     * @return Returns the researchType
     * 
     */
    public ResearchType getResearchType() {
        return researchType;
    }

    /**
     * Sets the researchType attribute.
     * 
     * @param researchType The researchType to set.
     * @deprecated
     */
    public void setResearchType(ResearchType researchType) {
        this.researchType = researchType;
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
     * Gets the proposalStatus attribute.
     * 
     * @return Returns the proposalStatus
     * 
     */
    public ProposalStatus getProposalStatus() {
        return proposalStatus;
    }

    /**
     * Sets the proposalStatus attribute.
     * 
     * @param proposalStatus The proposalStatus to set.
     * @deprecated
     */
    public void setProposalStatus(ProposalStatus proposalStatus) {
        this.proposalStatus = proposalStatus;
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
     * Gets the proposalPurpose attribute.
     * 
     * @return Returns the proposalPurpose
     * 
     */
    public ProposalPurpose getProposalPurpose() {
        return proposalPurpose;
    }

    /**
     * Sets the proposalPurpose attribute.
     * 
     * @param proposalPurpose The proposalPurpose to set.
     * @deprecated
     */
    public void setProposalPurpose(ProposalPurpose proposalPurpose) {
        this.proposalPurpose = proposalPurpose;
    }

    /**
     * Gets the proposalSubcontractor list.
     * 
     * @return Returns the proposalSubcontractor list
     * 
     */
    public List getProposalSubcontractor() {
        return proposalSubcontractor;
    }

    /**
     * Sets the proposalSubcontractor list.
     * 
     * @param proposalSubcontractor The proposalSubcontractor list to set.
     * 
     */
    public void setProposalSubcontractor(List proposalSubcontractor) {
        this.proposalSubcontractor = proposalSubcontractor;
    }

    /**
     * @return Returns the proposalOrganization.
     */
    public List getProposalOrganization() {
        return proposalOrganization;
    }

    /**
     * @param proposalOrganization The proposalOrganization to set.
     */
    public void setProposalOrganization(List proposalOrganization) {
        this.proposalOrganization = proposalOrganization;
    }

    /**
     * @return Returns the proposalProjectDirector.
     */
    public List getProposalProjectDirector() {
        return proposalProjectDirector;
    }

    /**
     * @param proposalProjectDirector The proposalProjectDirector to set.
     */
    public void setProposalProjectDirector(List proposalProjectDirector) {
        this.proposalProjectDirector = proposalProjectDirector;
    }

    /**
     * @return Returns the proposalDiary.
     */
    public List getProposalDiary() {
        return proposalDiary;
    }

    /**
     * @param proposalDiary The proposalDiary to set.
     */
    public void setProposalDiary(List proposalDiary) {
        this.proposalDiary = proposalDiary;
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
