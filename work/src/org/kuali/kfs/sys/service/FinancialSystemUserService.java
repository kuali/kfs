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

import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.businessobject.FinancialSystemUserPrimaryOrganization;

public interface FinancialSystemUserService extends UniversalUserService {

    /**
     * Retrieves a UniversalUser based on the payroll ID
     * 
     * @param personPayrollIdentifier
     * @return a universal user with the payroll ID, null if it can't be found
     */
    public UniversalUser getUniversalUserByPersonPayrollIdentifier(String personPayrollIdentifier);

    public FinancialSystemUser getFinancialSystemUser(String personUniversalIdentifier);
    
    public FinancialSystemUser getFinancialSystemUser(UserId userId) throws UserNotFoundException;
    
    /**
     * Converts a given universal user into a FinancialSystemUser object. If the object passed in is a FinancialSystemUser it is simply returned.
     */
    public FinancialSystemUser convertUniversalUserToFinancialSystemUser(UniversalUser user);

    /** Checks if the user is an active KFS User.  Performs the conversion from UniversalUser to FinancialSystemUser automatically.
     * 
     */
    public boolean isActiveFinancialSystemUser( UniversalUser user );    
    
    /** Get the default organization for the given module for the currently logged in user. */
    public ChartOrgHolder getOrganizationByModuleId(String moduleId);

    /** Get the user's default organization for the given module.  If no record exists for that module,
     * then the default from their 
     * 
     * @param user
     * @param moduleId
     * @return
     */
    public ChartOrgHolder getOrganizationByModuleId( UniversalUser user, String moduleId );

    /** Get the security organizations for the given module for the currently logged in user. */
    public List<? extends ChartOrgHolder> getSecurityOrganizationsByModuleId( String moduleId );
    
    /** Get the user's security organizations for the given module.
     * 
     * @param user
     * @param moduleId
     * @return
     */
    public List<? extends ChartOrgHolder> getSecurityOrganizationsByModuleId( UniversalUser user, String moduleId );
    
    public boolean isAdministratorUser(FinancialSystemUser user);
    
    public boolean isActiveForModule( FinancialSystemUser user, String moduleId );
    public boolean isActiveForAnyModule( FinancialSystemUser user );
}
