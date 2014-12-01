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
package org.kuali.kfs.sys.identity;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * KFSMI-4553
 * This class is look for the members of the defined role and remove the single actor if one exists
 * so that document will not route to the initiator or single approver.
 */
public class ExcludeSingleActorSeparationOfDutiesRoleTypeService  extends ExclusionRoleTypeServiceBase {
    private static final Logger LOG = Logger.getLogger( ExcludeSingleActorSeparationOfDutiesRoleTypeService .class );
    protected volatile DocumentService documentService;
    protected volatile WorkflowDocumentService workflowDocumentService;

    @Override
    public List<RoleMembership> getMatchingRoleMemberships(Map<String, String> qualification, List<RoleMembership> roleMemberList) {
        List<RoleMembership> membershipInfos = super.getMatchingRoleMemberships(qualification, roleMemberList);
        String documentId = new String(qualification.get(KimConstants.AttributeConstants.DOCUMENT_NUMBER));
        String approverOrInitiator = getApproverOrInitiator(documentId);
        if(ObjectUtils.isNotNull(approverOrInitiator )) {
            return excludePrincipalAsNeeded(approverOrInitiator, qualification, membershipInfos);
        }

        return membershipInfos;
    }

    /**
     *
     * This method return initiator or approver principal Id
     * @param documentId
     * @return
     */
    private String getApproverOrInitiator(String documentId) {
        String approverOrInitiatorPrincipalId = null;

       String principalId = getWorkflowDocumentService().getDocumentInitiatorPrincipalId(documentId);
       List<ActionTaken> actionTakenDTOs = getWorkflowDocumentService().getActionsTaken(documentId);
       for (ActionTaken  actionTaken : actionTakenDTOs ) {
           if(principalId.equals(actionTaken.getPrincipalId())) {
               approverOrInitiatorPrincipalId = principalId;
           }
       }

        return approverOrInitiatorPrincipalId;
    }

    protected DocumentService getDocumentService() {
        if (documentService == null) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }

    protected WorkflowDocumentService getWorkflowDocumentService() {
        if (workflowDocumentService == null) {
            workflowDocumentService = KewApiServiceLocator.getWorkflowDocumentService();
        }
        return workflowDocumentService;
    }
}
