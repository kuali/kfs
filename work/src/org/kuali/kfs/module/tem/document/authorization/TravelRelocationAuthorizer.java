/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.authorization;


public class TravelRelocationAuthorizer extends TravelArrangeableAuthorizer implements ReturnToFiscalOfficerAuthorizer {

    //RELO does not utilize the document specific permissions
//
//    public boolean canClose(final TravelDocument reloDoc, final Person user) {
//        return getActionPermission(reloDoc, user, TemConstants.Permission.CLOSE_RELO, true);
//    }
//
//    public boolean canCancel(final TravelDocument reloDoc, final Person user) {
//        return getActionPermission(reloDoc, user, TemConstants.Permission.CANCEL_RELO, true);
//    }
//
//    protected boolean getActionPermission(final TravelDocument travelDocument, final Person user, final String action, final boolean canInitiatorAct){
//        boolean success = false;
//
//        final TravelRelocationDocument reloDoc = (TravelRelocationDocument) travelDocument;
//
//        //first check to see if the user is either the initiator and if the initiator can perform this action
//        String initiator = reloDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId();
//        if(initiator.equals(user.getPrincipalId())) {
//            success = true && canInitiatorAct;
//        }
//
//
//        // Check to see if they are a fiscal officer and that the fiscal officer role has this permission
//        if (isResponsibleForAccountsOn(reloDoc, user) && isFiscalOfficerAuthorizedTo(action)) {
//            return true;
//        }
//
//        final String nameSpaceCode = TemConstants.PARAM_NAMESPACE;
//        final Map<String,String> permissionDetails = new HashMap<String,String>();
//        permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME,
//                org.kuali.kfs.module.tem.TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT);
//
//        //Return true if they have the correct permissions or they are the initiator and the initiator can perform this action.
//        return getIdentityManagementService().isAuthorized(user.getPrincipalId(), nameSpaceCode, action, permissionDetails, null) || success;
//
//    }
//
//    public boolean canCertify(final TravelRelocationDocument relocation, Person user) {
//        if (user.getPrincipalId().equals(relocation.getTraveler().getPrincipalId())
//            || !isEmployee(relocation.getTraveler())) {
//            return true;
//        }
//        return false;
//    }
//
//    protected boolean isFiscalOfficerAuthorizedTo(final String action) {
//        return isFiscalOfficerAuthorizedTo(action, TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT);
//    }

}
