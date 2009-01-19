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
package org.kuali.kfs.module.ar.service;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.rice.kim.bo.Person;

/**
 * Service to handle AR specific authorization questions.
 * 
 */
public interface AccountsReceivableAuthorizationService {

    /**
     * 
     * Determines whether the passed in person belongs to a Billing Organization.
     * @param person Must be non-null and have a valid principalId
     * @return
     */
    public boolean personBelongsToBillingOrg(Person person);
    
    
    public boolean personBelongsToProcessingOrg(Person person);
    
    /**
     * 
     * Determines whether the current session user belongs to a Billing Organization.
     * 
     * Will throw an IllegalArgumentException if there is no current session or session user.
     * @return
     */
    public boolean currentUserBelongsToBillingOrg();

    public boolean currentUserBelongsToProcessingOrg();
    
    /**
     * 
     * Determine the passed-in person's Home Org, for the purposes of AR.
     * @param person
     * @return
     */
    public Organization personHomeOrg(Person person);
    
    /**
     * 
     * Determine the current user's Home Org, for the purposes of AR.
     * @return
     */
    public Organization currentUserHomeOrg();
    
    public boolean isOrgABillingOrg(Organization org);

    public boolean isOrgABillingOrg(String chartOfAccountsCode, String organizationCode);
    
    public boolean isOrgAProcessingOrgInCurrentFiscalYear(Organization org);

    public boolean isOrgAProcessingOrgInCurrentFiscalYear(String chartOfAccountsCode, String organizationCode);
    
    public boolean getBillingOrgsInProcessingOrg(Organization org);
    
    public boolean getBillingOrgsInProcessingOrg(String chartOfAccountsCode, String organizationCode);
    
}
