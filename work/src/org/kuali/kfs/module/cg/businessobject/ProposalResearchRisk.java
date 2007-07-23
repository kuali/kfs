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

import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.kra.routingform.bo.ResearchRiskType;

public class ProposalResearchRisk extends PersistableBusinessObjectBase implements Inactivateable {
    
    private String researchRiskTypeCode;
    private Long proposalNumber;
    private boolean active;
    
    private Proposal proposal;
    private ResearchRiskType researchRiskType;
    
    public ProposalResearchRisk() {
        super();
    }
        
    @Override
    protected LinkedHashMap toStringMapper() {
        
        LinkedHashMap m = new LinkedHashMap();
        m.put("proposalNumber", proposalNumber);
        m.put("researchRiskTypeCode", researchRiskTypeCode);
        m.put("active", Boolean.toString(active));
        
        return m;
    }

    public Proposal getProposal() {
        return proposal;
    }


    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }


    public ResearchRiskType getResearchRiskType() {
        return researchRiskType;
    }


    public void setResearchRiskType(ResearchRiskType researchRiskType) {
        this.researchRiskType = researchRiskType;
    }


    public boolean getActive() {
        return isActive();
    }
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean isActive) {
        this.active = isActive;
    }

    public Long getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public String getResearchRiskTypeCode() {
        return researchRiskTypeCode;
    }

    public void setResearchRiskTypeCode(String researchRiskTypeCode) {
        this.researchRiskTypeCode = researchRiskTypeCode;
    }
    
}
