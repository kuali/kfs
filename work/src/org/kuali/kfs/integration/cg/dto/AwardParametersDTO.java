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
 * Integration class for AwardParametersDTO
 */
public class AwardParametersDTO implements Serializable {

    private static final long serialVersionUID = 8417796622708399543L;

    protected String principalId;
    protected String awardId;
    protected String awardStatusCode;
    protected String awardProjectTitle;
    protected String awardPurposeCode;
    protected String proposalAwardTypeCode;
    protected String agencyNumber;
    protected String awardDocumentNumber;
    protected Date projectStartDate;
    protected Date projectEndDate;
    protected Date proposalSubmissionDate;
    protected String proposalDirectCostAmount;
    protected String proposalIndirectCostAmount;
    protected String proposalPrimaryProjectDirectorId;
    protected String unit;

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getAwardId() {
        return awardId;
    }

    public void setAwardId(String awardId) {
        this.awardId = awardId;
    }

    public String getAwardStatusCode() {
        return awardStatusCode;
    }

    public void setAwardStatusCode(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }

    public String getAwardProjectTitle() {
        return awardProjectTitle;
    }

    public void setAwardProjectTitle(String awardProjectTitle) {
        this.awardProjectTitle = awardProjectTitle;
    }

    public String getAwardPurposeCode() {
        return awardPurposeCode;
    }

    public void setAwardPurposeCode(String awardPurposeCode) {
        this.awardPurposeCode = awardPurposeCode;
    }

    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }

    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }

    public String getAgencyNumber() {
        return agencyNumber;
    }

    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    public String getAwardDocumentNumber() {
        return awardDocumentNumber;
    }

    public void setAwardDocumentNumber(String awardDocumentNumber) {
        this.awardDocumentNumber = awardDocumentNumber;
    }

    public Date getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(Date projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public Date getProjectEndDate() {
        return projectEndDate;
    }

    public void setProjectEndDate(Date projectEndDate) {
        this.projectEndDate = projectEndDate;
    }

    public Date getProposalSubmissionDate() {
        return proposalSubmissionDate;
    }

    public void setProposalSubmissionDate(Date proposalSubmissionDate) {
        this.proposalSubmissionDate = proposalSubmissionDate;
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

    public String getProposalPrimaryProjectDirectorId() {
        return proposalPrimaryProjectDirectorId;
    }

    public void setProposalPrimaryProjectDirectorId(String proposalPrimaryProjectDirectorId) {
        this.proposalPrimaryProjectDirectorId = proposalPrimaryProjectDirectorId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
