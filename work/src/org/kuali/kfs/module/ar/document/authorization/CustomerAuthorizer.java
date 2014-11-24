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
package org.kuali.kfs.module.ar.document.authorization;

import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;

public class CustomerAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

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

