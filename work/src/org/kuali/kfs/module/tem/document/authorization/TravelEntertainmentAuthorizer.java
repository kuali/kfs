/*
 * Copyright 2011 The Kuali Foundation.
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


public class TravelEntertainmentAuthorizer extends TravelArrangeableAuthorizer implements ReturnToFiscalOfficerAuthorizer {

    //ENT does not utilize the document specific permissions
//
//    /**
//     *
//     * @param document
//     * @param user
//     * @return
//     */
//    public boolean canCertify(final TravelEntertainmentDocument document, Person user) {
//        if (user.getPrincipalId().equals(document.getTraveler().getPrincipalId())
//            || !isEmployee(document.getTraveler())) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     *
//     * @param travelDocument
//     * @param user
//     * @param action
//     * @param canInitiatorAct
//     * @return
//     */
//    protected boolean getActionPermission(final TravelDocument travelDocument, final Person user, final String action, final boolean canInitiatorAct){
//        boolean success = false;
//
//        final TravelEntertainmentDocument document = (TravelEntertainmentDocument) travelDocument;
//
//        //first check to see if the user is either the initiator and if the initiator can perform this action
//        String initiator = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
//        if(initiator.equals(user.getPrincipalId())) {
//            success = true && canInitiatorAct;
//        }
//
//
//        // Check to see if they are a fiscal officer and that the fiscal officer role has this permission
//        if (isResponsibleForAccountsOn(document, user) && isFiscalOfficerAuthorizedTo(action)) {
//            return true;
//        }
//
//        final String nameSpaceCode = TemConstants.PARAM_NAMESPACE;
//        final Map<String,String> permissionDetails = new HashMap<String,String>();
//        permissionDetails.put(KFSPropertyConstants.DOCUMENT_TYPE_NAME,
//                org.kuali.kfs.module.tem.TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT);
//
//        //Return true if they have the correct permissions or they are the initiator and the initiator can perform this action.
//        return getPermissionService().isAuthorized(user.getPrincipalId(), nameSpaceCode, action, permissionDetails) || success;
//    }
//
//    protected boolean isFiscalOfficerAuthorizedTo(final String action) {
//        return isFiscalOfficerAuthorizedTo(action, TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT);
//    }

}
