/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.kra.service;

import java.util.List;

import org.kuali.module.kra.bo.BudgetAdHocOrg;
import org.kuali.module.kra.bo.BudgetAdHocPermission;
import org.kuali.module.kra.budget.bo.BudgetPermissionType;

/**
 * This interface defines methods that a BudgetPermissionsService must provide
 * 
 * 
 */
public interface ResearchDocumentPermissionsService {
    
    /**
     * Retrieve the BudgetAdHocPermission for the given documentNumber and personUniversalIdentifier.
     * 
     * @param documentNumber
     * @param personUniversalIdentifier
     * @return BudgetAdHocPermission
     */
    public BudgetAdHocPermission getBudgetAdHocPermission(String documentNumber, String personUniversalIdentifier);
    
    /**
     * Retrieve the ad-hoc orgs for the given documentNumber and budgetPermissionCode.
     * 
     * @param documentNumber
     * @param budgetPermissionCode
     * @return List<BudgetAdHocOrg>
     */
    public List<BudgetAdHocOrg> getBudgetAdHocOrgs(String documentNumber, String budgetPermissionCode);
    
    /**
     * Get the list of Budget permission types.
     * 
     * @return List<BudgetPermissionType>
     */
    public List<BudgetPermissionType> getBudgetPermissionTypes();
    
    /**
     * Check whether given user is in the org routing hierarchy.
     * 
     * @param String orgXml
     * @param String uuid
     * @return boolean
     */
    public boolean isUserInOrgHierarchy(String orgXml, String uuid);
}
