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

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelAuthorizationAuthorizer extends TravelArrangeableAuthorizer {

    /**
     *  check permission to close
     *
     * @param taDoc
     * @param user
     * @return
     */
    public boolean canClose(final TravelDocument taDoc, final Person user) {
        return getActionPermission(taDoc, user, TemConstants.Permission.CLOSE_TA, true);
    }

    /**
     * Check permission for amend
     *
     * @param taDoc
     * @param user
     * @return
     */
    public boolean canAmend(final TravelDocument taDoc, final Person user) {
        return getActionPermission(taDoc, user, TemConstants.Permission.AMEND_TA, true);
    }

    /**
     *
     * @param travelDocument
     * @param user
     * @return
     */
    public boolean canHold(TravelAuthorizationDocument travelDocument, Person user) {
        return getActionPermission(travelDocument, user, TemConstants.Permission.HOLD_TA, false);
    }

    /**
     *
     * @param travelDocument
     * @param user
     * @return
     */
    public boolean canRemoveHold(final TravelAuthorizationDocument travelDocument, final Person user) {
        return getActionPermission(travelDocument, user, TemConstants.Permission.REMOVE_HOLD_TA, false);
    }

    /**
     *
     * @param travelDocument
     * @param user
     * @return
     */
    public boolean canCancel(final TravelAuthorizationDocument travelDocument, final Person user) {
        return getActionPermission(travelDocument, user, TemConstants.Permission.CANCEL_TA, true);
    }

    /**
     *
     * @param travelDocument
     * @param user
     * @return
     */
    public boolean hideButtons(final TravelAuthorizationDocument travelDocument, final Person user) {
        return getActionPermission(travelDocument, user, TemConstants.Permission.HIDE_BUTTONS, false);
    }

    /**
     * Initiator is not allow to copy document
     *
     * @param travelDocument
     * @param user
     * @return
     */
    public boolean canCopy(TravelAuthorizationDocument travelDocument, Person user) {
        if(ObjectUtils.isNull(user)) {
            return false;
        }
        //if user is initiator or the arrange, do not allow to copy
        return getTravelService().isUserInitiatorOrArranger(travelDocument, user);
    }

    /**
     *
     * @param travelDocument
     * @param user
     * @param action
     * @param canInitiatorAct
     * @return
     */
    protected boolean getActionPermission(final TravelDocument travelDocument, final Person user, final String permission, final boolean canInitiatorAct){
        boolean success = false;

        //first check to see if the user is either the initiator (or the arranger) and if the initiator can perform this action
        if(getTravelService().isUserInitiatorOrArranger(travelDocument, user)) {
            success = true && canInitiatorAct;
        }

        // Check to see if they are a fiscal officer and that the fiscal officer role has this permission
        if (getTravelDocumentService().isResponsibleForAccountsOn(travelDocument, user.getPrincipalId()) && isFiscalOfficerAuthorizedTo(permission)) {
            return true;
        }

        final String nameSpaceCode = TemConstants.PARAM_NAMESPACE;
        final Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KFSPropertyConstants.DOCUMENT_TYPE_NAME, TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);

        //Return true if they have the correct permissions or they are the initiator and the initiator can perform this action.
        return getPermissionService().isAuthorized(user.getPrincipalId(), nameSpaceCode, permission, permissionDetails) || success;
    }

    /**
     * Determine if the Fiscal Officer Role has permission named by <code>action</code>
     *
     * @param action or name of the permission to check for Fiscal Officer authorization on. This is usually, "Amend TA", "Close TA", "Cancel TA", "Hold TA", or "Unhold TA"
     * @boolean true if fiscal officer has rights or false otherwise
     */
    protected boolean isFiscalOfficerAuthorizedTo(final String action) {
        return isFiscalOfficerAuthorizedTo(action, TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
    }

}
