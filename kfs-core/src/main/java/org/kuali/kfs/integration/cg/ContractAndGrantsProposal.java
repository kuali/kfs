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
