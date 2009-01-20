/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.rice.kim.bo.Person;

public interface FinancialSystemUserService {

    /**
     * determine whether the given person is active in financial system
     * 
     * @param person the given person
     * @return true if the given person is active in financial system; otherwsie, false
     */
    boolean isActiveFinancialSystemUser(Person person);

    /**
     * get the chart and organization within the specified namespace that the given person belongs to
     * 
     * @param person the given person
     * @param namespaceCode the specified namespace
     * @return the chart and organization within the specified namespace that the given person belongs to
     */
    ChartOrgHolder getOrganizationByNamespaceCode(Person person, String namespaceCode);

    /**
     * get the chart and organization within the specified namespace that the given principalId belongs to
     * 
     * @param principalId the given person
     * @param namespaceCode the specified namespace
     * @return the chart and organization within the specified namespace that the given person belongs to
     */
    ChartOrgHolder getOrganizationByNamespaceCode(String principalId, String namespaceCode);
    
    Collection<String> getPrincipalIdsForOrganizationUsers( String namespaceCode, ChartOrgHolder chartOrg );
    
    Collection<String> getPrincipalIdsForOrganizationUsers( String namespaceCode, List<ChartOrgHolder> chartOrgs );
}
