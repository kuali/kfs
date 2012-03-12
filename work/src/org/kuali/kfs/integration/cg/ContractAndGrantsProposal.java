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

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;


public interface ContractAndGrantsProposal extends ExternalizableBusinessObject {

    public static final String PROPOSAL_CODE = "P";
    public static final String AWARD_CODE = "A";

    /**
     * Gets the award awarded to a proposal instance.
     * 
     * @return the award corresponding to a proposal instance if the proposal has been awarded.
     */
    public ContractsAndGrantsAward getAward();

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     */
    public Long getProposalNumber();

    /**
     * Gets the proposalBeginningDate attribute.
     * 
     * @return Returns the proposalBeginningDate
     */
    public Date getProposalBeginningDate();

    /**
     * Gets the proposalEndingDate attribute.
     * 
     * @return Returns the proposalEndingDate
     */
    public Date getProposalEndingDate();

    /**
     * Gets the proposalTotalAmount attribute.
     * 
     * @return Returns the proposalTotalAmount
     */
    public KualiDecimal getProposalTotalAmount();

    /**
     * Gets the proposalDirectCostAmount attribute.
     * 
     * @return Returns the proposalDirectCostAmount
     */
    public KualiDecimal getProposalDirectCostAmount();

    /**
     * Gets the proposalIndirectCostAmount attribute.
     * 
     * @return Returns the proposalIndirectCostAmount
     */
    public KualiDecimal getProposalIndirectCostAmount();

    /**
     * Gets the proposalRejectedDate attribute.
     * 
     * @return Returns the proposalRejectedDate
     */
    public Date getProposalRejectedDate();

    /**
     * Gets the proposalLastUpdateDate attribute.
     * 
     * @return Returns the proposalLastUpdateDate
     */
    public Timestamp getProposalLastUpdateDate();

    /**
     * Gets the proposalDueDate attribute.
     * 
     * @return Returns the proposalDueDate
     */
    public Date getProposalDueDate();

    /**
     * Gets the proposalTotalProjectAmount attribute.
     * 
     * @return Returns the proposalTotalProjectAmount
     */
    public KualiDecimal getProposalTotalProjectAmount();

    /**
     * Gets the proposalSubmissionDate attribute.
     * 
     * @return Returns the proposalSubmissionDate
     */
    public Date getProposalSubmissionDate();

    /**
     * Gets the proposalFederalPassThroughIndicator attribute.
     * 
     * @return Returns the proposalFederalPassThroughIndicator
     */
    public boolean getProposalFederalPassThroughIndicator();

    /**
     * Gets the oldProposalNumber attribute.
     * 
     * @return Returns the oldProposalNumber
     */
    public String getOldProposalNumber();

    /**
     * Gets the proposalClosingDate attribute.
     * 
     * @return Returns the proposalClosingDate
     */
    public Date getProposalClosingDate();

    /**
     * Gets the proposalAwardTypeCode attribute.
     * 
     * @return Returns the proposalAwardTypeCode
     */
    public String getProposalAwardTypeCode();

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber
     */
    public String getAgencyNumber();

    /**
     * Gets the proposalStatusCode attribute.
     * 
     * @return Returns the proposalStatusCode
     */
    public String getProposalStatusCode();

    /**
     * Gets the federalPassThroughAgencyNumber attribute.
     * 
     * @return Returns the federalPassThroughAgencyNumber
     */
    public String getFederalPassThroughAgencyNumber();

    /**
     * Gets the cfdaNumber attribute.
     * 
     * @return Returns the cfdaNumber
     */
    public String getCfdaNumber();

    /**
     * Gets the proposalFellowName attribute.
     * 
     * @return Returns the proposalFellowName
     */
    public String getProposalFellowName();

    /**
     * Gets the proposalPurposeCode attribute.
     * 
     * @return Returns the proposalPurposeCode
     */
    public String getProposalPurposeCode();

    /**
     * Gets the proposalProjectTitle attribute.
     * 
     * @return Returns the proposalProjectTitle
     */
    public String getProposalProjectTitle();

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive();
    
    public String getGrantNumber();
}
