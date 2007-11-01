/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.kra.routingform.bo.ResearchRiskType;

/**
 * Represents a relationship between a {@link Proposal} and a {@link ResearchRisk}.
 */
public class ProposalResearchRisk extends PersistableBusinessObjectBase {

    private String researchRiskTypeCode;
    private Long proposalNumber;
    private boolean researchRiskTypeIndicator;

    private Proposal proposal;
    private ResearchRiskType researchRiskType;

    /**
     * Default constructor.
     */
    public ProposalResearchRisk() {
        super();
    }

    @Override
    protected LinkedHashMap toStringMapper() {

        LinkedHashMap m = new LinkedHashMap();
        m.put("proposalNumber", proposalNumber);
        m.put("researchRiskTypeCode", researchRiskTypeCode);
        m.put("researchRiskTypeIndicator", Boolean.toString(researchRiskTypeIndicator));

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
     * Not sure what this is.
     * 
     * @return true or false
     */
    public boolean isResearchRiskTypeIndicator() {
        return researchRiskTypeIndicator;
    }

    /**
     * Not sure what this is.
     * 
     * @param researchRiskTypeIndicator
     */
    public void setResearchRiskTypeIndicator(boolean researchRiskTypeIndicator) {
        this.researchRiskTypeIndicator = researchRiskTypeIndicator;
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
