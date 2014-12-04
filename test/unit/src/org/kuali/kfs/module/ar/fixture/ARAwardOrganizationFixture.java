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

import org.kuali.kfs.module.cg.businessobject.AwardOrganization;

/**
 * Fixture class for AR AwardOrganization
 */
public enum ARAwardOrganizationFixture {

    AWD_ORG1("BL", "PSY", new Long(11), true),
    AWD_ORG2("BL", "AAAM", new Long(11), true),
    AWD_ORG3("UA", "VPIT", new Long(11), true),
    AWD_ORG4("BL", "MOTR", new Long(11), true);

    private String chartOfAccountsCode;
    private String organizationCode;
    private Long proposalNumber;
    private boolean awardPrimaryOrganizationIndicator;
    private boolean active = true;

    private ARAwardOrganizationFixture(String chartOfAccountsCode, String organizationCode, Long proposalNumber, boolean awardPrimaryOrganizationIndicator) {
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.organizationCode = organizationCode;
        this.proposalNumber = proposalNumber;
        this.awardPrimaryOrganizationIndicator = awardPrimaryOrganizationIndicator;
    }

    public AwardOrganization createAwardOrganization() {
        AwardOrganization awardOrganization = new AwardOrganization();
        awardOrganization.setChartOfAccountsCode(chartOfAccountsCode);
        awardOrganization.setOrganizationCode(organizationCode);
        awardOrganization.setProposalNumber(proposalNumber);
        awardOrganization.setAwardPrimaryOrganizationIndicator(awardPrimaryOrganizationIndicator);
        awardOrganization.setActive(active);
        return awardOrganization;
    }
}
