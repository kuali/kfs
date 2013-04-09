/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sys.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.sys.service.FinancialSystemWorkflowHelperService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;

public class FinancialSystemWorkflowHelperServiceImpl implements FinancialSystemWorkflowHelperService {

    WorkflowDocumentService workflowDocumentService;

    @Override
    public boolean isAdhocApprovalRequestedForPrincipal(WorkflowDocument workflowDocument, String principalId ) {
        if (workflowDocument.isApprovalRequested() ) {
            Set<String> currentNodes = workflowDocument.getCurrentNodeNames();
            if ( CollectionUtils.isNotEmpty(currentNodes) ) {
                for ( String currentNode : currentNodes ) {
                    List<ActionRequest> requests = workflowDocumentService.getActionRequestsForPrincipalAtNode(workflowDocument.getDocumentId(), currentNode, principalId );
                    if ( requests != null ) {
                        for ( ActionRequest ar : requests ) {
                            if ( ar.isActivated() && ar.isCurrent() && ar.isApprovalRequest() && ar.isAdHocRequest() ) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    @Override
    public String getApplicationDocumentStatus(String documentNumber) {
        Document workflowDocument = workflowDocumentService.getDocument(documentNumber);
        if(workflowDocument!= null){
            return workflowDocument.getApplicationDocumentStatus();
        }
        return "";
    }

}
