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
