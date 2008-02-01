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
package org.kuali.module.ar.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.KualiGroupService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.OrganizationAccountingDefault;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;

public class OrganizationAccountingDefaultAuthorizer extends MaintenanceDocumentAuthorizerBase {
    
    
    @Override
    public Map getEditMode(Document document, UniversalUser user) {
        Map editModes = new HashMap();
     //   try {
        OrganizationAccountingDefault orgAcctDefault = (OrganizationAccountingDefault)document.getDocumentBusinessObject();
        ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        Org userOrg = currentUser.getOrganization();
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
   private boolean isUserInArSupervisorGroup(UniversalUser user) {
       boolean retVal = false;
           try {
               retVal = SpringContext.getBean(KualiGroupService.class).getByGroupName(ArConstants.AR_SUPERVISOR_GROUP_NAME).hasMember(user);
           } catch (GroupNotFoundException gnfe) {
               
           }
     
       return retVal;
   }

   
}
