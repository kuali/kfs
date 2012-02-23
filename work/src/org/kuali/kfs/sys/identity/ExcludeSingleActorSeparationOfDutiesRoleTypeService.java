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
package org.kuali.kfs.sys.identity;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.actiontaken.dao.ActionTakenDAO;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.dto.ActionTakenDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kim.bo.group.dto.GroupMembershipInfo;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.dao.KimRoleDao;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.impl.GroupServiceImpl;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.util.KimConstants.KimUIConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * KFSMI-4553
 * This class is look for the members of the defined role and remove the single actor if one exists 
 * so that document will not route to the initiator or single approver. 
 */
public class ExcludeSingleActorSeparationOfDutiesRoleTypeService  extends ExclusionRoleTypeServiceBase {
    private static final Logger LOG = Logger.getLogger( ExcludeSingleActorSeparationOfDutiesRoleTypeService .class );
    protected DocumentService documentService;
    protected WorkflowInfo workflowInfo = new WorkflowInfo();

    @Override
    public List<RoleMembershipInfo> doRoleQualifiersMatchQualification(AttributeSet qualification, List<RoleMembershipInfo> roleMemberList) {
        List<RoleMembershipInfo> membershipInfos = super.doRoleQualifiersMatchQualification(qualification, roleMemberList);
        String documentId = new String(qualification.get(KimAttributes.DOCUMENT_NUMBER));
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
        try {
           String principalId = workflowInfo.getDocumentInitiatorPrincipalId(Long.valueOf(documentId));
           ActionTakenDTO[] actionTakenDTOs = workflowInfo.getActionsTaken(Long.valueOf(documentId));
           for (ActionTakenDTO  actionTaken : actionTakenDTOs ) {
               if(principalId.equals(actionTaken.getPrincipalId())) {
                   approverOrInitiatorPrincipalId = principalId;
               }
           }

        } catch (WorkflowException wex) {
            throw new RuntimeException("Error in determining approver or initiator principal Id" + 
                    "for document number: "+documentId+" :"+wex.getLocalizedMessage(),wex);
        } 

        return approverOrInitiatorPrincipalId;
    }

    protected DocumentService getDocumentService() {
        if (documentService == null) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }
}
