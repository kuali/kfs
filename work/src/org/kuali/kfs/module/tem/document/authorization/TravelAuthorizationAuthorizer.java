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

import static org.kuali.kfs.module.tem.TemConstants.TravelStatusCodeKeys.AWAIT_ORG;

import java.util.List;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

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
     * @see org.kuali.kfs.module.tem.document.authorization.ReturnToFiscalOfficerAuthorizer#canReturnToFisicalOfficer(org.kuali.kfs.module.tem.document.TravelDocument, org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean canReturnToFisicalOfficer(final TravelDocument travelDocument, final Person user) {
        if(ObjectUtils.isNull(user)) {
            return false;
        }
        
        KualiWorkflowDocument workflowDocument = travelDocument.getDocumentHeader().getWorkflowDocument();
        if (getTravelService().isUserInitiatorOrArranger(travelDocument, user)){
            return false;
        }
        
        //now check to see if they are a Fiscal Officer
        if (getTravelDocumentService().isResponsibleForAccountsOn(travelDocument, user.getPrincipalId())) {
            return false;
        }
        
        //check if the doc can be routed      
        String docRouteStatus = workflowDocument.getRouteHeader().getDocRouteStatus();
        if(workflowDocument.stateIsFinal() 
                || docRouteStatus.equals(KEWConstants.ROUTE_HEADER_PROCESSED_CD) 
                || workflowDocument.getRouteHeader().getAppDocStatus().equals(AWAIT_ORG) 
                || !workflowDocument.getRouteHeader().isApproveRequested()){
            return false;
        }
        
        String nameSpaceCode = org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME,
                org.kuali.kfs.module.tem.TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
        
        return getIdentityManagementService().isAuthorized(user.getPrincipalId(), nameSpaceCode,
                TemConstants.Permission.RETURN_TO_FO, permissionDetails, null);
        
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
    protected boolean getActionPermission(final TravelDocument travelDocument, final Person user, final String action, final boolean canInitiatorAct){
        boolean success = false;
        
        //first check to see if the user is either the initiator (or the arranger) and if the initiator can perform this action
        if(getTravelService().isUserInitiatorOrArranger(travelDocument, user)) {
            success = true && canInitiatorAct;
        }
                
        // Check to see if they are a fiscal officer and that the fiscal officer role has this permission
        if (getTravelDocumentService().isResponsibleForAccountsOn(travelDocument, user.getPrincipalId()) && isFiscalOfficerAuthorizedTo(action)) {
            return true;
        }
        
        final String nameSpaceCode = TemConstants.PARAM_NAMESPACE;
        final AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME, org.kuali.kfs.module.tem.TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);

        //Return true if they have the correct permissions or they are the initiator and the initiator can perform this action.
        return getIdentityManagementService().isAuthorized(user.getPrincipalId(), nameSpaceCode, action, permissionDetails, null) || success;       
    }

    /**
     * Determine if the Fiscal Officer Role has permission named by <code>action</code>
     * 
     * @param action or name of the permission to check for Fiscal Officer authorization on. This is usually, "Amend TA", "Close TA", "Cancel TA", "Hold TA", or "Unhold TA"
     * @boolean true if fiscal officer has rights or false otherwise
     */
    protected boolean isFiscalOfficerAuthorizedTo(final String action) {
        
        //Get Permissions and check against supplied action
        final AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME, TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);

        final String fiscalOfficerRoleId = getRoleService().getRoleIdByName(KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimConstants.FISCAL_OFFICER_KIM_ROLE_NAME);
        final List<String> roles = getPermissionService().getRoleIdsForPermission(TemConstants.PARAM_NAMESPACE, action, permissionDetails);
        return (roles != null && roles.size() > 0 && roles.contains(fiscalOfficerRoleId));
    }
    
}
