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

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Represents a relationship between a {@link Proposal} and an {@Org}.
 */
public class ProposalOrganization extends PersistableBusinessObjectBase implements Primaryable, MutableInactivatable {
    private String chartOfAccountsCode;
    private String organizationCode;
    private Long proposalNumber;
    private boolean proposalPrimaryOrganizationIndicator;
    private boolean active = true;

    private Organization organization;
    private Chart chartOfAccounts;

    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the organizationCode attribute.
     *
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     *
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * @return Returns the proposalPrimaryOrganizationIndicator.
     */
    public boolean isProposalPrimaryOrganizationIndicator() {
        return proposalPrimaryOrganizationIndicator;
    }

    /**
     * @see Primaryable#isPrimary()
     */
    @Override
    public boolean isPrimary() {
        return isProposalPrimaryOrganizationIndicator();
    }

    /**
     * @param proposalPrimaryOrganizationIndicator The proposalPrimaryOrganizationIndicator to set.
     */
    public void setProposalPrimaryOrganizationIndicator(boolean proposalPrimaryOrganizationIndicator) {
        this.proposalPrimaryOrganizationIndicator = proposalPrimaryOrganizationIndicator;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active attribute.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active true if the instance is active, false otherwise
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the organization attribute.
     *
     * @return Returns the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     *
     * @param organization The organization to set.
     * @deprecated
     */
    @Deprecated
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the chartOfAccounts attribute.
     *
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.chartOfAccountsCode);
        m.put(KFSPropertyConstants.ORGANIZATION_CODE, this.organizationCode);
        if (this.proposalNumber != null) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }
        return m;
    }

    /**
     * This can be displayed by Proposal.xml lookup results.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        // todo: get "primary" and "secondary" from ApplicationResources.properties via KFSKeyConstants?
        return getChartOfAccountsCode() + "-" + getOrganizationCode() + " " + (isProposalPrimaryOrganizationIndicator() ? "primary" : "secondary");
    }

}
