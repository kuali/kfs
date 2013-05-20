/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Represents a relationship between a {@link Proposal} and a {@link ResearchRisk}.
 */
public class ProposalResearchRisk extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String researchRiskTypeCode;
    private Long proposalNumber;
    private boolean active;

    private Proposal proposal;
    private ResearchRiskType researchRiskType;

    /**
     * Default constructor.
     */
    public ProposalResearchRisk() {
        super();
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {

        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        m.put("researchRiskTypeCode", researchRiskTypeCode);
        m.put(KFSPropertyConstants.ACTIVE, Boolean.toString(active));

        return m;
    }

    /**
     * Gets the {@link Proposal}.
     * 
     * @return
     */
    public Proposal getProposal() {
        return proposal;
    }

    /**
     * Sets the {@link Proposal}.
     * 
     * @param proposal
     */
    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    /**
     * Gets the {@link ResearchRiskType} of the risk associated with the {@link Proposal}.
     * 
     * @return the {@link ResearchRiskType}.
     */
    public ResearchRiskType getResearchRiskType() {
        return researchRiskType;
    }

    /**
     * Sets the {@link ResearchRiskType} associated with the {@link Proposal}.
     * 
     * @param researchRiskType
     */
    public void setResearchRiskType(ResearchRiskType researchRiskType) {
        this.researchRiskType = researchRiskType;
    }

    /**
     * Returns whether or not this object is active.
     * 
     * @return true or false
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active indicator for this object.
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the key of the {@link Proposal} related to the {@link ResearchRisk}.
     * 
     * @return the id of the {@link Proposal} related to the {@link ResearchRisk}.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the key of the {@link Proposal} related to the {@link ResearchRisk}.
     * 
     * @param the id of the {@link Proposal} related to the {@link ResearchRisk}.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the code of the {@link ResearchRiskType} associated to the {@link Proposal}.
     * 
     * @return the code of the {@link ResearchRiskType} associated to the {@link Proposal}.
     */
    public String getResearchRiskTypeCode() {
        return researchRiskTypeCode;
    }

    /**
     * Gets the code of the {@link ResearchRiskType} associated to the {@link Proposal}.
     * 
     * @param the code of the type of the {@link ResearchRiskType} associated to the {@link Proposal}.
     */
    public void setResearchRiskTypeCode(String researchRiskTypeCode) {
        this.researchRiskTypeCode = researchRiskTypeCode;
    }

}
