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

package org.kuali.kfs.module.external.kc.businessobject;

import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.kfs.integration.cg.ContractAndGrantsProposal;
import org.kuali.kfs.module.external.kc.dto.ProposalDTO;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * See functional documentation.
 */
public class Proposal implements ContractAndGrantsProposal {

    private Long proposalNumber;
    private boolean proposalFederalPassThroughIndicator;
    private String grantNumber;
    private String federalPassThroughAgencyNumber;
    private boolean active;

    private Award award;

    private Date proposalBeginningDate;
    private Date proposalEndingDate;
    private KualiDecimal proposalTotalAmount;
    private KualiDecimal proposalDirectCostAmount;
    private KualiDecimal proposalIndirectCostAmount;
    private Date proposalRejectedDate;
    private Timestamp proposalLastUpdateDate;
    private Date proposalDueDate;
    private KualiDecimal proposalTotalProjectAmount;
    private Date proposalSubmissionDate;
    private String oldProposalNumber;
    private Date proposalClosingDate;
    private String proposalAwardTypeCode;
    private String agencyNumber;
    private String proposalStatusCode;
    private String cfdaNumber;
    private String proposalFellowName;
    private String proposalPurposeCode;
    private String proposalProjectTitle;

    public Proposal(ProposalDTO kcProposal) {
        setProposalNumber(kcProposal.getProposalNumber() == null ? null : Long.valueOf(kcProposal.getProposalNumber()));
        setProposalBeginningDate(new Date(kcProposal.getRequestedStartDateTotal().getDate()));
        setProposalEndingDate(new Date(kcProposal.getRequestedEndDateTotal().getDate()));
        setProposalTotalAmount(kcProposal.getProposalTotalAmount());
        setProposalDirectCostAmount(kcProposal.getTotalDirectCostTotal());
        setProposalIndirectCostAmount(kcProposal.getTotalIndirectCostTotal());
        setProposalLastUpdateDate(new Timestamp(kcProposal.getProposalLastUpdateDate().getDate()));
        setProposalAwardTypeCode(kcProposal.getAwardTypeCode().toString());
        setAgencyNumber(kcProposal.getSponsorCode());
        setProposalStatusCode(kcProposal.getStatusCode().toString());
        setCfdaNumber(kcProposal.getCfdaNumber());
        setProposalProjectTitle(kcProposal.getTitle());
        setGrantNumber(kcProposal.getSponsorAwardNumber());
    }

    public Proposal() {

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
     * Gets the proposalFederalPassThroughIndicator attribute.
     *
     * @return Returns the proposalFederalPassThroughIndicator
     */
    @Override
    public boolean getProposalFederalPassThroughIndicator() {
        return proposalFederalPassThroughIndicator;
    }

    /**
     * Sets the proposalFederalPassThroughIndicator attribute.
     *
     * @param proposalFederalPassThroughIndicator The proposalFederalPassThroughIndicator to set.
     */
    public void setProposalFederalPassThroughIndicator(boolean proposalFederalPassThroughIndicator) {
        this.proposalFederalPassThroughIndicator = proposalFederalPassThroughIndicator;
    }

    /**
     * Gets the grantNumber attribute.
     *
     * @return Returns the grantNumber
     */
    @Override
    public String getGrantNumber() {
        return grantNumber;
    }

    /**
     * Sets the grantNumber attribute.
     *
     * @param grantNumber The grantNumber to set.
     */
    public void setGrantNumber(String grantNumber) {
        this.grantNumber = grantNumber;
    }

    /**
     * Gets the federalPassThroughAgencyNumber attribute.
     *
     * @return Returns the federalPassThroughAgencyNumber
     */
    @Override
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

    public void prepareForWorkflow() {}

    @Override
    public void refresh() {}
    @Override
    public Award getAward() {
        return award;
    }
    public void setAward(Award award) {
        this.award = award;
    }
    @Override
    public Date getProposalBeginningDate() {
        return proposalBeginningDate;
    }
    public void setProposalBeginningDate(Date proposalBeginningDate) {
        this.proposalBeginningDate = proposalBeginningDate;
    }
    @Override
    public Date getProposalEndingDate() {
        return proposalEndingDate;
    }
    public void setProposalEndingDate(Date proposalEndingDate) {
        this.proposalEndingDate = proposalEndingDate;
    }
    @Override
    public KualiDecimal getProposalTotalAmount() {
        return proposalTotalAmount;
    }
    public void setProposalTotalAmount(KualiDecimal proposalTotalAmount) {
        this.proposalTotalAmount = proposalTotalAmount;
    }
    @Override
    public KualiDecimal getProposalDirectCostAmount() {
        return proposalDirectCostAmount;
    }
    public void setProposalDirectCostAmount(KualiDecimal proposalDirectCostAmount) {
        this.proposalDirectCostAmount = proposalDirectCostAmount;
    }
    @Override
    public KualiDecimal getProposalIndirectCostAmount() {
        return proposalIndirectCostAmount;
    }
    public void setProposalIndirectCostAmount(KualiDecimal proposalIndirectCostAmount) {
        this.proposalIndirectCostAmount = proposalIndirectCostAmount;
    }
    @Override
    public Date getProposalRejectedDate() {
        return proposalRejectedDate;
    }
    public void setProposalRejectedDate(Date proposalRejectedDate) {
        this.proposalRejectedDate = proposalRejectedDate;
    }
    @Override
    public Timestamp getProposalLastUpdateDate() {
        return proposalLastUpdateDate;
    }
    public void setProposalLastUpdateDate(Timestamp proposalLastUpdateDate) {
        this.proposalLastUpdateDate = proposalLastUpdateDate;
    }
    @Override
    public Date getProposalDueDate() {
        return proposalDueDate;
    }
    public void setProposalDueDate(Date proposalDueDate) {
        this.proposalDueDate = proposalDueDate;
    }
    @Override
    public KualiDecimal getProposalTotalProjectAmount() {
        return proposalTotalProjectAmount;
    }
    public void setProposalTotalProjectAmount(KualiDecimal proposalTotalProjectAmount) {
        this.proposalTotalProjectAmount = proposalTotalProjectAmount;
    }
    @Override
    public Date getProposalSubmissionDate() {
        return proposalSubmissionDate;
    }
    public void setProposalSubmissionDate(Date proposalSubmissionDate) {
        this.proposalSubmissionDate = proposalSubmissionDate;
    }
    @Override
    public String getOldProposalNumber() {
        return oldProposalNumber;
    }
    public void setOldProposalNumber(String oldProposalNumber) {
        this.oldProposalNumber = oldProposalNumber;
    }
    @Override
    public Date getProposalClosingDate() {
        return proposalClosingDate;
    }
    public void setProposalClosingDate(Date proposalClosingDate) {
        this.proposalClosingDate = proposalClosingDate;
    }
    @Override
    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }
    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }
    @Override
    public String getAgencyNumber() {
        return agencyNumber;
    }
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }
    @Override
    public String getProposalStatusCode() {
        return proposalStatusCode;
    }
    public void setProposalStatusCode(String proposalStatusCode) {
        this.proposalStatusCode = proposalStatusCode;
    }
    @Override
    public String getCfdaNumber() {
        return cfdaNumber;
    }
    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }
    @Override
    public String getProposalFellowName() {
        return proposalFellowName;
    }
    public void setProposalFellowName(String proposalFellowName) {
        this.proposalFellowName = proposalFellowName;
    }
    @Override
    public String getProposalPurposeCode() {
        return proposalPurposeCode;
    }
    public void setProposalPurposeCode(String proposalPurposeCode) {
        this.proposalPurposeCode = proposalPurposeCode;
    }
    @Override
    public String getProposalProjectTitle() {
        return proposalProjectTitle;
    }
    public void setProposalProjectTitle(String proposalProjectTitle) {
        this.proposalProjectTitle = proposalProjectTitle;
    }
}

