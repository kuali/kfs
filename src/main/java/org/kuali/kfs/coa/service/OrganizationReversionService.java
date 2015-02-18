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
import java.util.Map;

import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic;

/**
 * 
 * This service interface defines methods necessary for retrieving fully populated OrganizationReversion business objects from the database
 * that are necessary for transaction processing in the application. It also defines methods to retrieve org reversion categories
 */
public interface OrganizationReversionService {
    /**
     * Get an organization reversion record
     * 
     * @param fiscalYear Fiscal Year
     * @param chartCode Chart
     * @param organizationCode Organization code
     * @return Org Reversion record
     */
    public OrganizationReversion getByPrimaryId(Integer fiscalYear, String chartCode, String organizationCode);

    /**
     * Get org reversion categories
     * 
     * @return map of org reversion categories
     */
    public Map<String, OrganizationReversionCategoryLogic> getCategories();

    /**
     * List of reversion categories in order of the sort code
     * 
     * @return list of org reversion category codes
     */
    public List<OrganizationReversionCategory> getCategoryList();
    
    /**
     * Determines if the given category code represents an active category
     * @param categoryCode the category code to check
     * @return true if the given category code represents an active category; false otherwise
     */
    public boolean isCategoryActive(String categoryCode);
    
    /**
     * Determines if the given category name represents an active category
     * @param categoryName the category name to check
     * @return true if the given category name represents an active category; false otherwise
     */
    public boolean isCategoryActiveByName(String categoryName);
    
    /**
     * 
     * This method looks up the default object code to populate the Organization Reversion object code if the 
     * "carry Forward by Object Code" box is not checked.
     * @return system parameter default value for Organization Reversion detail
     */
    public String getOrganizationReversionDetaiFromSystemParameters();
}
