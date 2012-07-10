/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.authorization;

import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;

public class CustomerAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canRecall(org.kuali.rice.krad.document.Document, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean canRecall(Document document, Person user) {
        if (!document.getDocumentHeader().getWorkflowDocument().isApprovalRequested()) return false;
        return super.canRecall(document, user);
    }

    // TODO this is commented out because the old logic doesnt match up the new logic at 
    //      https://test.kuali.org/confluence/display/KULAR/Document+Types%2C+Approvals 

    // the rules should be:  persons belonging to AR Biller or AR Processor roles can 
    // initiate these documents, both Create and Maintain (ie, New and Edit).
    //
    // All approvals is done by AR_ROLE_MAINTAINERS (or whatever the KIM group/role equiv of htat is)
    //
    // Furthermore, System Super Users should be able to do everything, including BlanketApprove.
    
///**
// * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.rice.krad.document.Document, org.kuali.rice.kim.api.identity.Person)
// */
//@Override
//public FinancialSystemDocumentActionFlags getDocumentActions(Document document) {
//    FinancialSystemDocumentActionFlags actionFlags = super.getDocumentActionFlags(document);
//
//    MaintenanceDocument maintDocument = (MaintenanceDocument) document;
//    String maintenanceAction = maintDocument.getNewMaintainableObject().getMaintenanceAction();
//
//    //  this is used for batch processing of customer records
//    if (KFSConstants.SYSTEM_USER.equalsIgnoreCase(user.getPrincipalName())) {
//        actionFlags.setCanApprove(true);
//        actionFlags.setCanBlanketApprove(true);
//        actionFlags.setCanAdHocRoute(true);
//        actionFlags.setCanRoute(true);
//        return actionFlags;
//    }
//
//    // if user is not AR SUPERVISOR he cannot approve the customer creation document
//    if (KRADConstants.MAINTENANCE_NEW_ACTION.equalsIgnoreCase(maintenanceAction) && !ARUtil.isUserInArSupervisorGroup(user)) {
//
//        actionFlags.setCanApprove(false);
//        actionFlags.setCanBlanketApprove(false);
//
//    }
//
//    // if ((maintenanceAction.equalsIgnoreCase(KRADConstants.MAINTENANCE_EDIT_ACTION) ||
//    // maintenanceAction.equalsIgnoreCase(KRADConstants.MAINTENANCE_COPY_ACTION)) && !isUserInArSupervisorGroup(user)) {
//    //
//    // actionFlags.setCanRoute(false);
//    // actionFlags.setCanSave(false);
//    // actionFlags.setCanCancel(false);
//    //
//    //        }
//    return actionFlags;
//}
}

