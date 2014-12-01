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
package org.kuali.kfs.module.bc.document.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;

/**
 * This interface defines methods that an Budget Construction Organization Reports Service must provide.
 */
public interface BudgetConstructionOrganizationReportsService {

    /**
     * This method retrieves a Budget Construction Organization Reports instance by its composite primary keys (parameters passed
     * in).
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return a Budget Construction Organization Reports instance.
     */
    public BudgetConstructionOrganizationReports getByPrimaryId(String chartOfAccountsCode, String organizationCode);

    public List getBySearchCriteria(Class cls, Map searchCriteria);

    public List getBySearchCriteriaOrderByList(Class cls, Map searchCriteria, List<String> orderList);

    /**
     * This method returns a list of child BC organization reports objects for the passed in org.
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List getActiveChildOrgs(String chartOfAccountsCode, String organizationCode);

    /**
     * This method returns if an org is a leaf
     * 
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public boolean isLeafOrg(String chartOfAccountsCode, String organizationCode);
}
