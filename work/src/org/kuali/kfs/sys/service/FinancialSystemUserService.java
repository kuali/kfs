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
package org.kuali.kfs.sys.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.rice.kim.api.identity.Person;

public interface FinancialSystemUserService {
    boolean isActiveFinancialSystemUser(String principalId);
  //  boolean isActiveFinancialSystemUser(Person person);

    /**
     * If the specified person has the KFS-SYS User role for the specified namespace, this will return the organization associated with that assignment.
     * If the specified person does not have the KFS-SYS User role for the specified namespace but does with a blank namespace, this will return the organization associated with that assignment.
     * If the person does not have the KFS-SYS User role at all, this method will derive the organization from the Person Record.
     */
    ChartOrgHolder getPrimaryOrganization(Person person, String namespaceCode);
    ChartOrgHolder getPrimaryOrganization(String principalId, String namespaceCode);

    Collection<String> getPrincipalIdsForFinancialSystemOrganizationUsers( String namespaceCode, ChartOrgHolder chartOrg );
    Collection<String> getPrincipalIdsForFinancialSystemOrganizationUsers( String namespaceCode, List<ChartOrgHolder> chartOrgs );

    /**
     * KFSCNTRB-1344
     * Gets the name of the person with the specified EmployeeID.
     * Note: This method is added here as a place-holder before Rice provides some method like IdentityService.getPersonNameByEmployeeId.
     * It allows institutions to override the implementation to speed up Person name retrieval if needed.
     * @param employeeId of the Person
     * @return name of the person
     */
    //TODO This method can be replaced if Rice adds method IdentityService.getPersonNameByEmployeeId.
    public String getPersonNameByEmployeeId(String employeeId);
}
