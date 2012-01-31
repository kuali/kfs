/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.integration.cg.dto;

import java.io.Serializable;
import java.util.Date;
/**
 * Integration class for ProposalParametersDTO
 */
public class ProposalParametersDTO implements Serializable {

    private static final long serialVersionUID = 8417796622708399543L;

    protected String principalId;
    protected String unit;
    protected String proposalStatusCode;
    protected String agencyNumber;
    protected String proposalProjectTitle;
    protected Date proposalBeginningDate;
    protected Date proposalEndingDate;
    protected String proposalDirectCostAmount;
    protected String proposalIndirectCostAmount;
    protected Date proposalSubmissionDate;
    protected String proposalAwardTypeCode;
    protected String proposalPurposeCode;

    protected String proposalPrimaryProjectDirectorId;

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProposalStatusCode() {
        return proposalStatusCode;
    }

    public void setProposalStatusCode(String proposalStatusCode) {
        this.proposalStatusCode = proposalStatusCode;
    }

    public String getAgencyNumber() {
        return agencyNumber;
    }

    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    public String getProposalProjectTitle() {
        return proposalProjectTitle;
    }

    public void setProposalProjectTitle(String proposalProjectTitle) {
        this.proposalProjectTitle = proposalProjectTitle;
    }

    public Date getProposalBeginningDate() {
        return proposalBeginningDate;
    }

    public void setProposalBeginningDate(Date proposalBeginningDate) {
        this.proposalBeginningDate = proposalBeginningDate;
    }

    public Date getProposalEndingDate() {
        return proposalEndingDate;
    }

    public void setProposalEndingDate(Date proposalEndingDate) {
        this.proposalEndingDate = proposalEndingDate;
    }

    public String getProposalDirectCostAmount() {
        return proposalDirectCostAmount;
    }

    public void setProposalDirectCostAmount(String proposalDirectCostAmount) {
        this.proposalDirectCostAmount = proposalDirectCostAmount;
    }

    public String getProposalIndirectCostAmount() {
        return proposalIndirectCostAmount;
    }

    public void setProposalIndirectCostAmount(String proposalIndirectCostAmount) {
        this.proposalIndirectCostAmount = proposalIndirectCostAmount;
    }

    public Date getProposalSubmissionDate() {
        return proposalSubmissionDate;
    }

    public void setProposalSubmissionDate(Date proposalSubmissionDate) {
        this.proposalSubmissionDate = proposalSubmissionDate;
    }

    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }

    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }

    public String getProposalPurposeCode() {
        return proposalPurposeCode;
    }

    public void setProposalPurposeCode(String proposalPurposeCode) {
        this.proposalPurposeCode = proposalPurposeCode;
    }

    public String getProposalPrimaryProjectDirectorId() {
        return proposalPrimaryProjectDirectorId;
    }

    public void setProposalPrimaryProjectDirectorId(String proposalPrimaryProjectDirectorId) {
        this.proposalPrimaryProjectDirectorId = proposalPrimaryProjectDirectorId;
    }


}