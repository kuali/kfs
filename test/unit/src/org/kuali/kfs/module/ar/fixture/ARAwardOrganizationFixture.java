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
