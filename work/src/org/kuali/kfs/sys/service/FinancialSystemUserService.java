/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.rice.kim.api.identity.Person;

public interface FinancialSystemUserService {
    boolean isActiveFinancialSystemUser(String principalId);
    boolean isActiveFinancialSystemUser(Person person);

    /**
     * If the specified person has the KFS-SYS User role for the specified namespace, this will return the organization associated with that assignment.
     * If the specified person does not have the KFS-SYS User role for the specified namespace but does with a blank namespace, this will return the organization associated with that assignment.
     * If the person does not have the KFS-SYS User role at all, this method will derive the organization from the Person Record.
     */
    ChartOrgHolder getPrimaryOrganization(Person person, String namespaceCode);
    ChartOrgHolder getPrimaryOrganization(String principalId, String namespaceCode);
    
    Collection<String> getPrincipalIdsForFinancialSystemOrganizationUsers( String namespaceCode, ChartOrgHolder chartOrg );
    Collection<String> getPrincipalIdsForFinancialSystemOrganizationUsers( String namespaceCode, List<ChartOrgHolder> chartOrgs );
}
