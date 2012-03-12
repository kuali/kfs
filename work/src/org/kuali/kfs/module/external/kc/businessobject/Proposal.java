/*
 * Copyright 2006-2009 The Kuali Foundation
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

