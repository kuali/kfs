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

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPullup;

/**
 * This interface defines methods that manipulate objects used by the Organization Selection screens. Manipulated objects include
 * BudgetConstructionPullup with methods that populate and depopulate the associated table for a specific user.
 */
public interface BudgetOrganizationTreeService {

    /**
     * This method populates BudgetConstructionPullup with rows that represent the subtree of the passed in point of view
     * organization for a user. All organizations reporting to the point of view are inserted.
     * 
     * @param principalName
     * @param chartOfAccountsCode
     * @param organizationCode
     */
    public void buildPullup(String principalName, String chartOfAccountsCode, String organizationCode);

    /**
     * This method populates BudgetConstructionPullup with rows that represent the subtree of the passed in point of view
     * organization for a user. All organizations reporting to the point of view are inserted.
     * This uses raw SQL
     * 
     * @param principalName
     * @param chartOfAccountsCode
     * @param organizationCode
     */
    public void buildPullupSql(String principalName, String chartOfAccountsCode, String organizationCode);

    /**
     * This method depopulates BudgetConstructionPullup of any rows associated with the user
     * 
     * @param principalName
     */
    public void cleanPullup(String principalName);

    /**
     * This method returns a list of child organizations for the passed in organization and user
     * 
     * @param principalId
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List<BudgetConstructionPullup> getPullupChildOrgs(String principalId, String chartOfAccountsCode, String organizationCode);

    /**
     * This method resets the pullflag for the BudgetConstructionPullup set of records owned by the user
     * 
     * @param principalId
     */
    public void resetPullFlag(String principalId);

    /**
     * This method returns a list of selected BudgetConstructionPullup rows for the user.
     * 
     * @param principalId
     * @return
     */
    public List<BudgetConstructionPullup> getSelectedOrgs(String principalId);
}

