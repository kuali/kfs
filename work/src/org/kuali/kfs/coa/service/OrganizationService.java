/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/service/OrganizationService.java,v $
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
package org.kuali.module.chart.service;

import java.util.List;

import org.kuali.module.chart.bo.Org;

/**
 * This interface defines methods that an Org Service must provide.
 * 
 * 
 */
public interface OrganizationService {

    /**
     * This method retrieves an organization instance by its composite primary keys (parameters passed in).
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return An Org instance.
     */
    public Org getByPrimaryId(String chartOfAccountsCode, String organizationCode);

    /**
     * Method is used by KualiOrgReviewAttribute to enable caching of orgs for routing.
     * 
     * @see org.kuali.module.chart.service.OrganizationService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public Org getByPrimaryIdWithCaching(String chartOfAccountsCode, String organizationCode);

    /**
     * Saves an Org object instance.
     * 
     * @param organization
     */
    public void save(Org organization);

    /**
     * 
     * Retrieves a List of Accounts that are active, and are tied to this Org.
     * 
     * If there are no Accounts that meet this criteria, an empty list will be returned.
     * 
     * @param chartOfAccountsCode - chartCode for the Org you want Accounts for
     * @param organizationCode - orgCode for the Org you want Accounts for
     * @return A List of Accounts that are active, and tied to this Org
     * 
     */
    public List getActiveAccountsByOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * 
     * Retrieves a List of Orgs that are active, and that ReportTo this Org
     * 
     * If there are no Orgs that meet this criteria, an empty list will be returned.
     * 
     * @param chartOfAccountsCode - chartCode for the Org you want Child Orgs for
     * @param organizationCode - orgCode for the Org you want Child Orgs for
     * @return A List of Orgs that are active, and report to this Org
     * 
     */
    public List getActiveChildOrgs(String chartOfAccountsCode, String organizationCode);

    /**
     * 
     * Returns a list of active organizations with the given organization type code.
     * 
     * @param organizationTypeCode
     * @return
     */
    public List<Org> getActiveOrgsByType( String organizationTypeCode );
    
}