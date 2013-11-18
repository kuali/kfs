/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.integration.cg.businessobject.Proposal;

/**
 * Fixture class for CG Proposal
 */
public enum ARProposalFixture {

    CG_PRPSL1(new Long(11));


    private Long proposalNumber;


    private ARProposalFixture(Long proposalNumber) {

        this.proposalNumber = proposalNumber;
    }

    public Proposal createProposal() {
        Proposal proposal = new Proposal();
        proposal.setProposalNumber(this.proposalNumber);
        return proposal;
    }
}
