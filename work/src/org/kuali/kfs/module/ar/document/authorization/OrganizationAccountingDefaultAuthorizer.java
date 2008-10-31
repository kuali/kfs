/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Document;

public class OrganizationAccountingDefaultAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    @Override
    @SuppressWarnings("unchecked")
    public Map getEditMode(Document document, Person user) {
        Map editModes = new HashMap();
     //   try {
        OrganizationAccountingDefault orgAcctDefault = (OrganizationAccountingDefault)document.getDocumentBusinessObject();
        ChartOrgHolder chartOrg = org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.KNSAuthorizationService.class).getOrganizationByModuleId(KFSConstants.Modules.CHART);
        Org userOrg = chartOrg.getOrganization();
        Org docOrg = orgAcctDefault.getOrganization();
        
        if (ObjectUtils.equals(userOrg, docOrg) || isUserInArSupervisorGroup(user) ){
            editModes.put(AuthorizationConstants.EditMode.FULL_ENTRY, "TRUE");
        } 
        
        //} catch ()
        return editModes;
    }

   /**
    * Checks if the current user is a member of the ar supervisor workgroup.
    * 
    * @return true if user is in group
    */
   private boolean isUserInArSupervisorGroup(Person user) {
       return org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupByName(org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, ArConstants.AR_SUPERVISOR_GROUP_NAME).getGroupId());
   }

   
}

