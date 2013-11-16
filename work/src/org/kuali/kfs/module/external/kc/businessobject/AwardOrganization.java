/*
 * Copyright 2006 The Kuali Foundation
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

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsOrganization;

/**
 * This class represents an association between an award and an organization. It's like a reference to the organization from the
 * award. This way an award can maintain a collection of these references instead of owning organizations directly.
 */
public class AwardOrganization implements ContractsAndGrantsOrganization {

    private String chartOfAccountsCode;
    private String organizationCode;
    private Long proposalNumber;
    private boolean awardPrimaryOrganizationIndicator;
    private boolean active = true;

    private Chart chartOfAccounts;
    private Organization organization;

    @Override
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }
    @Override
    public String getOrganizationCode() {
        return organizationCode;
    }
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }
    @Override
    public Long getProposalNumber() {
        return proposalNumber;
    }
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }
    @Override
    public boolean isAwardPrimaryOrganizationIndicator() {
        return awardPrimaryOrganizationIndicator;
    }
    public void setAwardPrimaryOrganizationIndicator(boolean awardPrimaryOrganizationIndicator) {
        this.awardPrimaryOrganizationIndicator = awardPrimaryOrganizationIndicator;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    @Override
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }
    @Override
    public Organization getOrganization() {
        return organization;
    }
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Override
    public void refresh() { }

}
