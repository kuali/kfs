/*
 * Copyright 2011 The Kuali Foundation.
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
