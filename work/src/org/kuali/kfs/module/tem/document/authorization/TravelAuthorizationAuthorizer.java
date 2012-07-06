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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.identity.TemKimAttributes;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_ORG;

public class TravelAuthorizationAuthorizer extends AccountingDocumentAuthorizerBase implements ReturnToFiscalOfficerAuthorizer {
    private PermissionService permissionService;
    private RoleService roleService;
    private TravelDocumentService travelDocumentService;
    
    public boolean canClose(final TravelDocument taDoc, final Person user) {
        return getActionPermission(taDoc, user, TemConstants.PermissionNames.CLOSE_TA, true);
    }
    
    public boolean canAmend(final TravelDocument taDoc, final Person user) {
        return getActionPermission(taDoc, user, TemConstants.PermissionNames.AMEND_TA, true);
    }

    @Override
    public boolean canReturn(final TravelDocument travelDocument, final Person user) {
        if(ObjectUtils.isNull(user)) {
            return false;
        }
        
        KualiWorkflowDocument workflowDocument = travelDocument.getDocumentHeader().getWorkflowDocument();
        
        //first check to see if the user is either the initiator or is a fiscal officer for this doc      
        //initiator cannot Hold their own doc       
        String initiator = workflowDocument.getRouteHeader().getInitiatorPrincipalId();
        if(initiator.equals(user.getPrincipalId())) {
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
                TemConstants.PermissionNames.RETURN_TO_FO, permissionDetails, null);
        
    }
    
    public boolean canCalculate(TravelAuthorizationDocument taDoc, Person user) {
        if(ObjectUtils.isNull(user)) {
            return false;
        }
        
        KualiWorkflowDocument workflowDocument = taDoc.getDocumentHeader().getWorkflowDocument();
        
        //first check to see if the user is either the initiator or is a fiscal officer for this doc      
        //initiator cannot Hold their own doc       
        String initiator = workflowDocument.getRouteHeader().getInitiatorPrincipalId();
        if(initiator.equals(user.getPrincipalId())
                || getTravelDocumentService().isResponsibleForAccountsOn(taDoc, user.getPrincipalId())) {
            return true;
        }
        else{
            return false;
        }
    }
    
    public boolean canHold(TravelAuthorizationDocument taDoc, Person user) {
        return getActionPermission(taDoc, user, TemConstants.PermissionNames.HOLD_TA, false);
    }
    
    public boolean canRemoveHold(final TravelDocument taDoc, final Person user) {
        return getActionPermission(taDoc, user, TemConstants.PermissionNames.REMOVE_HOLD_TA, false);
    }
    
    public boolean canCancel(final TravelDocument taDoc, final Person user) {
        return getActionPermission(taDoc, user, TemConstants.PermissionNames.CANCEL_TA, true);
    }
    public boolean hideButtons(final TravelDocument taDoc, final Person user) {
        return getActionPermission(taDoc, user, TemConstants.PermissionNames.HIDE_BUTTONS, false);
    }
    
    public boolean canCopy(TravelDocument travelDocument, Person user) {
        if(ObjectUtils.isNull(user)) {
            return false;
        }
        
        KualiWorkflowDocument workflowDocument = travelDocument.getDocumentHeader().getWorkflowDocument();
        //first check to see if the user is the initiator of this doc      
        //initiator cannot copy doc enroute     
        String initiator = workflowDocument.getRouteHeader().getInitiatorPrincipalId();
        if(!initiator.equals(user.getPrincipalId())) {
            return false;
        }

        return true;
    }
    
    public boolean canSave(TravelDocument travelDocument, Person user) {
        if(ObjectUtils.isNull(user)) {
            return false;
        }
        
        KualiWorkflowDocument workflowDocument = travelDocument.getDocumentHeader().getWorkflowDocument();
        
        //first check to see if the user is either the initiator or is a fiscal officer for this doc      
        //Only initiator and FO can save doc enroute;
        String initiator = workflowDocument.getRouteHeader().getInitiatorPrincipalId();
        return initiator.equals(user.getPrincipalId()) || getTravelDocumentService().isResponsibleForAccountsOn(travelDocument, user.getPrincipalId());
    }
    
    protected boolean getActionPermission(final TravelDocument travelDocument, final Person user, final String action, final boolean canInitiatorAct){
        boolean success = false;
        
        //first check to see if the user is either the initiator and if the initiator can perform this action
        String initiator = travelDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId();
        if(initiator.equals(user.getPrincipalId())) {
            success = true && canInitiatorAct;
        }
                
        // Check to see if they are a fiscal officer and that the fiscal officer role has this permission
        if (getTravelDocumentService().isResponsibleForAccountsOn(travelDocument, user.getPrincipalId()) && isFiscalOfficerAuthorizedTo(action)) {
            return true;
        }
        
        final String nameSpaceCode = TemConstants.PARAM_NAMESPACE;
        final AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME,
                org.kuali.kfs.module.tem.TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);

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
        final String nameSpaceCode = TemConstants.PARAM_NAMESPACE;
        final AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME,
                org.kuali.kfs.module.tem.TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);

        final String fiscalOfficerRoleId = getRoleService().getRoleIdByName("KFS-SYS", KFSConstants.SysKimConstants.FISCAL_OFFICER_KIM_ROLE_NAME);
        final List<String> roles = getPermissionService().getRoleIdsForPermission(nameSpaceCode, action, permissionDetails);
        return (roles != null && roles.size() > 0 && roles.contains(fiscalOfficerRoleId));
    }


    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase#addRoleQualification(org.kuali.rice.kns.bo.BusinessObject, java.util.Map)
     */
    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        TravelDocumentBase document = (TravelDocumentBase)businessObject;
        // add the document amount
        if (ObjectUtils.isNotNull(document.getProfileId())  ) {
            attributes.put(TemKimAttributes.PROFILE_PRINCIPAL_ID, document.getProfileId().toString());
        }
    }

    protected final PermissionService getPermissionService() {
        if (permissionService == null) {
            permissionService = SpringContext.getBean(PermissionService.class);
        }
        return permissionService;
    }
    
    protected RoleService getRoleService() {
        if ( roleService == null ) {
            roleService = SpringContext.getBean(RoleManagementService.class);
        }

        return roleService;
    }
    
    protected TravelDocumentService getTravelDocumentService() {
        if ( travelDocumentService == null ) {
            travelDocumentService = SpringContext.getBean(TravelDocumentService.class);
        }

        return travelDocumentService;
    }
}
