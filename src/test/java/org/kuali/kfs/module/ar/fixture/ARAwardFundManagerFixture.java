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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.cg.businessobject.AwardFundManager;

/**
 * Fixture class for CG AwardAccount
 */
public enum ARAwardFundManagerFixture {

    AWD_FND_MGR1("1137304513", new Long(11), true, null);

    private String principalId;
    private Long proposalNumber;
    private boolean primaryFundManagerIndicator;
    private String projectTitle;

    private ARAwardFundManagerFixture(String principalId, Long proposalNumber, boolean primaryFundManagerIndicator, String projectTitle) {
        this.principalId = principalId;
        this.proposalNumber = proposalNumber;
        this.primaryFundManagerIndicator = primaryFundManagerIndicator;
        this.projectTitle = projectTitle;
    }

    public AwardFundManager createAwardFundManager() {
        AwardFundManager awardFundManager = new AwardFundManager();

        awardFundManager.setPrincipalId(this.principalId);
        awardFundManager.setProposalNumber(this.proposalNumber);
        awardFundManager.setPrimaryFundManagerIndicator(this.primaryFundManagerIndicator);
        awardFundManager.setProjectTitle(this.projectTitle);

        return awardFundManager;
    }
}
