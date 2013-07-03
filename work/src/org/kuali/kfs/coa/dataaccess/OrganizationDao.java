/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.coa.dataaccess;

import java.util.List;

import org.kuali.kfs.coa.businessobject.Organization;

/**
 * This interface defines data access methods for {@link Org}
 */
public interface OrganizationDao {
    /**
     * This method retrieves a {@link Org} based on primary keys
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return an {@link Org} based on primary keys
     */
    public Organization getByPrimaryId(String chartOfAccountsCode, String organizationCode);

    /**
     * This method saves a specific {@link Org}
     * 
     * @param organization
     */
    public void save(Organization organization);

    /**
     * This method retrieves a list of active {@link Org}s defined by their chart and organization code
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return a list of active {@link Org}s by chart and organization code
     */
    public List getActiveAccountsByOrg(String chartOfAccountsCode, String organizationCode);

    /**
     * This method retrieves a list of active child {@link Org}s based on their parent's organization code and chart code
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return a list of active {@link Org}s by their parent's chart and organization code
     */
    public List getActiveChildOrgs(String chartOfAccountsCode, String organizationCode);

    /**
     * Returns a list of active organizations with the given organization type code.
     * 
     * @param organizationType
     * @return a list of active {@link Org}s based on their organization type code
     */
    public List<Organization> getActiveOrgsByType(String organizationTypeCode);

    /**
     * This method retrieves a list of root organization codes (as a string array) based on their root chart and reports to org type
     * code
     * 
     * @param rootChart
     * @param selfReportsOrgTypeCode
     * @return a string array of root org codes based on root chart and reports to org type code
     */
    public String[] getRootOrganizationCode(String rootChart, String selfReportsOrgTypeCode);

}
