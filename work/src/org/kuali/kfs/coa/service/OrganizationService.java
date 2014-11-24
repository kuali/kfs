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
package org.kuali.kfs.coa.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Organization;

/**
 * This interface defines methods that an Org Service must provide.
 */
public interface OrganizationService {
    /**
     * This method retrieves an organization instance by its composite primary keys (parameters passed in).
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return An Org instance.
     */
    public Organization getByPrimaryId(String chartOfAccountsCode, String organizationCode);

    /**
     * Method is used by KualiOrgReviewAttribute to enable caching of orgs for routing.
     * 
     * @see org.kuali.kfs.coa.service.OrganizationService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public Organization getByPrimaryIdWithCaching(String chartOfAccountsCode, String organizationCode);

    /**
     * Retrieves a List of Accounts that are active, and are tied to this Org. If there are no Accounts that meet this criteria, an
     * empty list will be returned.
     * 
     * @param chartOfAccountsCode - chartCode for the Org you want Accounts for
     * @param organizationCode - orgCode for the Org you want Accounts for
     * @return A List of Accounts that are active, and tied to this Org
     */
    public List<Account> getActiveAccountsByOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * Retrieves a List of Orgs that are active, and that ReportTo this Org If there are no Orgs that meet this criteria, an empty
     * list will be returned.
     * 
     * @param chartOfAccountsCode - chartCode for the Org you want Child Orgs for
     * @param organizationCode - orgCode for the Org you want Child Orgs for
     * @return A List of Orgs that are active, and report to this Org
     */
    public List<Organization> getActiveChildOrgs(String chartOfAccountsCode, String organizationCode);
    
    /**
     * Returns a list of active organizations with the given organization type code.
     * 
     * @param organizationTypeCode
     * @return
     */
    public List<Organization> getActiveOrgsByType(String organizationTypeCode);

    
    /**
     * Returns a list of active financial processing organizations.
     * 
     * @return A List of Orgs that are active and financial processing.
     */
    public List<Organization> getActiveFinancialOrgs();
    
    /**
     * returns the chart and organization of the ACTIVE root-level organization
     */
    public String[] getRootOrganizationCode();
    
    /**
     * This method traverses the hierarchy to see if the organization represented by the potentialChildChartCode and potentialChildOrganizationCode 
     * reports to the organization represented by the potentialParentChartCode and potentialParentOrganizationCode
     * 
     * @param potentialChildChartCode
     * @param potentialChildOrganizationCode
     * @param potentialParentChartCode
     * @param potentialParentOrganizationCode
     * @return boolean indicating whether the organization represented by the first two parameters reports to one represented by the last two parameters
     */
    public boolean isParentOrganization(String potentialChildChartCode, String potentialChildOrganizationCode, String potentialParentChartCode, String potentialParentOrganizationCode);
    
    /**
     * Flushes an internal cache used to resolve parent organizations.
     * 
     * Called from the KualiOrgMaintainable when an org is saved via the document.
     * 
     */
    public void flushParentOrgCache();
}
