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
    
    public Proposal() {
    	
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
     * Gets the proposalFederalPassThroughIndicator attribute.
     * 
     * @return Returns the proposalFederalPassThroughIndicator
     */
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

    public void refresh() {}
    
    
    public Award getAward() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setAward(Award award) {
        // TODO Auto-generated method stub
        this.award = award;
    }

    public Date getProposalBeginningDate() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Date getProposalEndingDate() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public KualiDecimal getProposalTotalAmount() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public KualiDecimal getProposalDirectCostAmount() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public KualiDecimal getProposalIndirectCostAmount() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Date getProposalRejectedDate() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Timestamp getProposalLastUpdateDate() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Date getProposalDueDate() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public KualiDecimal getProposalTotalProjectAmount() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Date getProposalSubmissionDate() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getOldProposalNumber() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Date getProposalClosingDate() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getProposalAwardTypeCode() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getAgencyNumber() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getProposalStatusCode() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getCfdaNumber() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getProposalFellowName() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getProposalPurposeCode() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getProposalProjectTitle() {
        // TODO Auto-generated method stub
        return null;
    }

}

