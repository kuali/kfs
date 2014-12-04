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
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active indicator for this object.
     *
     * @param active
     */
    @Override
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
