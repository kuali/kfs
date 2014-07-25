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

import org.kuali.kfs.module.cg.businessobject.AwardFundManager;

/**
 * Fixture class for CG AwardAccount
 */
public enum ARAwardFundManagerFixture {

    AWD_FND_MGR1("1137304513", new Long(11), true, null);

    private String principalId;
    private Long proposalNumber;
    private boolean awardPrimaryFundManagerIndicator;
    private String awardFundManagerProjectTitle;

    private ARAwardFundManagerFixture(String principalId, Long proposalNumber, boolean awardPrimaryFundManagerIndicator, String awardFundManagerProjectTitle) {
        this.principalId = principalId;
        this.proposalNumber = proposalNumber;
        this.awardPrimaryFundManagerIndicator = awardPrimaryFundManagerIndicator;
        this.awardFundManagerProjectTitle = awardFundManagerProjectTitle;
    }

    public AwardFundManager createAwardFundManager() {
        AwardFundManager awardFundManager = new AwardFundManager();

        awardFundManager.setPrincipalId(this.principalId);
        awardFundManager.setProposalNumber(this.proposalNumber);
        awardFundManager.setAwardPrimaryFundManagerIndicator(this.awardPrimaryFundManagerIndicator);
        awardFundManager.setAwardFundManagerProjectTitle(this.awardFundManagerProjectTitle);

        return awardFundManager;
    }
}
