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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.util.ARUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.authorization.FinancialSystemDocumentActionFlags;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.util.KNSConstants;

public class CustomerAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    // TODO remove - replaced by kim
//    @Override
//    public void canInitiate(String documentTypeName, Person user) {
//
//        super.canInitiate(documentTypeName, user);
//        
//        // if organization doesn't exist
//        if (!ARUtil.isUserInArBillingOrg(user)) {
//            throw new DocumentInitiationAuthorizationException(ArKeyConstants.ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG, 
//                    new String[] { "(Users in an AR Billing Org)", "Customer Maintenance" });
//
//        }
//    }

// TODO remove - replaced by kim create / maintain template
    /**
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
//    @Override
//    @SuppressWarnings("unchecked")
//    public Map getEditMode(Document document, Person user) {
//        Map editModes = super.getEditMode(document, user);
//
//        MaintenanceDocument maintDocument = (MaintenanceDocument) document;
//        String maintenanceAction = maintDocument.getNewMaintainableObject().getMaintenanceAction();
//        if (maintenanceAction.equalsIgnoreCase(KNSConstants.MAINTENANCE_EDIT_ACTION) || maintenanceAction.equalsIgnoreCase(KNSConstants.MAINTENANCE_COPY_ACTION)) {
//            if (!ARUtil.isUserInArSupervisorGroup(user)) {
//                editModes.clear();
//                editModes.put(AuthorizationConstants.EditMode.UNVIEWABLE, "TRUE");
//            }
//        }
//
//        return editModes;
//    }


 // TODO fix for kim - and let's let permissions take care of what the system user can do
//     /**
//     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
//     */
//    @Override
//    public FinancialSystemDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        FinancialSystemDocumentActionFlags actionFlags = super.getDocumentActionFlags(document, user);
//
//        MaintenanceDocument maintDocument = (MaintenanceDocument) document;
//        String maintenanceAction = maintDocument.getNewMaintainableObject().getMaintenanceAction();
//
//        //  this is used for batch processing of customer records
//        if (KFSConstants.SYSTEM_USER.equalsIgnoreCase(user.getPrincipalName())) {
//           actionFlags.setCanApprove(true);
//           actionFlags.setCanBlanketApprove(true);
//           actionFlags.setCanAdHocRoute(true);
//           actionFlags.setCanRoute(true);
//           return actionFlags;
//        }
//        
//        // if user is not AR SUPERVISOR he cannot approve the customer creation document
//        if (KNSConstants.MAINTENANCE_NEW_ACTION.equalsIgnoreCase(maintenanceAction) && !ARUtil.isUserInArSupervisorGroup(user)) {
//
//            actionFlags.setCanApprove(false);
//            actionFlags.setCanBlanketApprove(false);
//
//        }
//
//        // if ((maintenanceAction.equalsIgnoreCase(KNSConstants.MAINTENANCE_EDIT_ACTION) ||
//        // maintenanceAction.equalsIgnoreCase(KNSConstants.MAINTENANCE_COPY_ACTION)) && !isUserInArSupervisorGroup(user)) {
//        //
//        // actionFlags.setCanRoute(false);
//        // actionFlags.setCanSave(false);
//        // actionFlags.setCanCancel(false);
//        //
//        //        }
//        return actionFlags;
//    }

}

