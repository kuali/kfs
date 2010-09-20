/*
 * Copyright 2006 The Kuali Foundation
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
