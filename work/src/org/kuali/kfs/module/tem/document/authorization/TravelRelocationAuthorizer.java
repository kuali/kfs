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

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys.AWAIT_ORG;

public class TravelRelocationAuthorizer extends TravelDocumentAuthorizer implements ReturnToFiscalOfficerAuthorizer {
    
    public boolean canClose(final TravelDocument reloDoc, final Person user) {
        return getActionPermission(reloDoc, user, TemConstants.PermissionNames.CLOSE_RELO, true);
    }
    
    public boolean canCancel(final TravelDocument reloDoc, final Person user) {
        return getActionPermission(reloDoc, user, TemConstants.PermissionNames.CANCEL_RELO, true);
    }
    
    protected boolean getActionPermission(final TravelDocument travelDocument, final Person user, final String action, final boolean canInitiatorAct){
        boolean success = false;
        
        final TravelRelocationDocument reloDoc = (TravelRelocationDocument) travelDocument;

        //first check to see if the user is either the initiator and if the initiator can perform this action
        String initiator = reloDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId();
        if(initiator.equals(user.getPrincipalId())) {
            success = true && canInitiatorAct;
        }
        
        
        // Check to see if they are a fiscal officer and that the fiscal officer role has this permission
        if (isResponsibleForAccountsOn(reloDoc, user) && isFiscalOfficerAuthorizedTo(action)) {
            return true;
        }
        
        final String nameSpaceCode = TemConstants.PARAM_NAMESPACE;
        final AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME,
                org.kuali.kfs.module.tem.TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT);

        //Return true if they have the correct permissions or they are the initiator and the initiator can perform this action.
        return getIdentityManagementService().isAuthorized(user.getPrincipalId(), nameSpaceCode, action, permissionDetails, null) || success;
        
    }
    
    /**
     * Determines if the user is a fiscal officer on {@link Account} instances tied to the {@link TravelRelocationDocument}
     * instance
     *
     * @param relocation check for fiscal officer status on
     * @param user to that is a fiscal officer
     * @return if the <code>user</code> is a fiscal officer on accounts tied to the {@link TravelRelocationDocument}
     */
    protected boolean isResponsibleForAccountsOn(final TravelRelocationDocument relocation, final Person user) {
        final List<String> accounts = findAccountsResponsibleFor(relocation.getSourceAccountingLines(), user);
        return (accounts != null && accounts.size() > 0);
    }
    
    /**
     * Looks up accounts from {@link List} of {@link SourceAccountingLine} instances to determine if {@link Person} <code>user</code>
     * is a fiscal officer on any of those
     * 
     * @param lines or {@link List} of {@link SourceAccountingLine} instances 
     * @param user is a {@link Person} that might be a fiscal officer on accounts in <code>lines</code>
     * @return a {@link List} of account numbers the {@link Person} is a fiscal officer on
     */
    protected List<String> findAccountsResponsibleFor(final List<SourceAccountingLine> lines, final Person user) {
        final List<String> retval = new ArrayList<String>();
        for (final AccountingLine line : lines) {
            if (line.getAccount().getAccountFiscalOfficerUser().getPrincipalId().equals(user.getPrincipalId()) 
                && !retval.contains(line.getAccountNumber())) {
                retval.add(line.getAccountNumber());
            }
        }
        return retval;
    }
    
    /**
     * Determine if the Fiscal Officer Role has permission named by <code>action</code>
     * 
     * @param action or name of the permission to check for Fiscal Officer authorization on. This is usually, ""Close RELO", "Cancel RELO"
     * @boolean true if fiscal officer has rights or false otherwise
     */
    protected boolean isFiscalOfficerAuthorizedTo(final String action) {
        //Get Permissions and check against supplied action
        final String nameSpaceCode = TemConstants.PARAM_NAMESPACE;
        final AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME,
                org.kuali.kfs.module.tem.TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT);

        final String fiscalOfficerRoleId = getRoleService().getRoleIdByName("KFS-SYS", KFSConstants.SysKimConstants.FISCAL_OFFICER_KIM_ROLE_NAME);
        final List<String> roles = getPermissionService().getRoleIdsForPermission(nameSpaceCode, action, permissionDetails);
        return (roles != null && roles.size() > 0 && roles.contains(fiscalOfficerRoleId));
    }
    
    public boolean canCertify(final TravelRelocationDocument relocation, Person user) {
        if (user.getPrincipalId().equals(relocation.getTraveler().getPrincipalId())
            || !isEmployee(relocation.getTraveler())) {
            return true;
        }
        return false;
    }
    
    protected boolean isEmployee(final TravelerDetail traveler) {
        if (traveler == null) {
            return false;
        }
        return getTravelerService().isEmployee(traveler);
    }
    
    protected TravelerService getTravelerService() {
        return SpringContext.getBean(TravelerService.class);
    }
    

    @Override
    public boolean canReturnToFisicalOfficer(TravelDocument travelDocument, Person user) {
        if(ObjectUtils.isNull(user)) {
            return false;
        }
        final TravelRelocationDocument reloDoc = (TravelRelocationDocument) travelDocument;
        
        KualiWorkflowDocument workflowDocument = reloDoc.getDocumentHeader().getWorkflowDocument();
        
        //first check to see if the user is either the initiator or is a fiscal officer for this doc      
        //initiator cannot Hold their own doc       
        String initiator = workflowDocument.getRouteHeader().getInitiatorPrincipalId();
        if(initiator.equals(user.getPrincipalId())) {
            return false;
        }
        
        //now check to see if they are a Fiscal Officer
        if (isResponsibleForAccountsOn(reloDoc, user)) {
            return false;
        }
        
        //check if the doc can be routed      
        String docRouteStatus = workflowDocument.getRouteHeader().getDocRouteStatus();
        if(workflowDocument.stateIsFinal() || docRouteStatus.equals(KEWConstants.ROUTE_HEADER_PROCESSED_CD) || 
                workflowDocument.getRouteHeader().getAppDocStatus().equals(AWAIT_ORG) || !workflowDocument.getRouteHeader().isApproveRequested()){
            return false;
        }
        
        String nameSpaceCode = org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME,
                org.kuali.kfs.module.tem.TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT);
        
        return getIdentityManagementService().isAuthorized(user.getPrincipalId(), nameSpaceCode,
                TemConstants.PermissionNames.RETURN_TO_FO, permissionDetails, null);
    }

}
