/*
 * Copyright 2005 The Kuali Foundation
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
