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
