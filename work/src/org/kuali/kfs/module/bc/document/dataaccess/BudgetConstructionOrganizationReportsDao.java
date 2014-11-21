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
package org.kuali.kfs.module.bc.document.dataaccess;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BudgetConstructionOrganizationReportsDao {

    /**
     * gets Collection with searchCriteria
     * 
     * @param cls
     * @param searchCriteria
     * @return
     */
    public Collection getBySearchCriteria(Class cls, Map searchCriteria);
    
    /**
     * gets Collection with searchCriteria and OrderByList
     * 
     * @param cls
     * @param searchCriteria
     * @param orderList
     * @return
     */
    public Collection getBySearchCriteriaWithOrderByList(Class cls, Map searchCriteria, List<String> orderList);
    
    /**
     * This method returns active organizations that report to the passed in organization.
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List getActiveChildOrgs(String chartOfAccountsCode, String organizationCode);

    /**
     * This method returns whether or not an organization is a leaf in the reports to tree
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public boolean isLeafOrg(String chartOfAccountsCode, String organizationCode);

}
