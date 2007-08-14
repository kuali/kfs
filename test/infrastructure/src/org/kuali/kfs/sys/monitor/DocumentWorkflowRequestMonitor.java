/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.test.monitor;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.context.SpringContext;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.WorkflowException;

public class DocumentWorkflowRequestMonitor extends ChangeMonitor {

    private final Long docHeaderId;
    private final UniversalUser user;
    private final String actionRequestedCode;

    public DocumentWorkflowRequestMonitor(Long docHeaderId, UniversalUser user, String actionRequestedCode) {
        this.docHeaderId = docHeaderId;
        this.user = user;
        this.actionRequestedCode = actionRequestedCode;
    }

    public boolean valueChanged()
        throws WorkflowException
    {
        KualiWorkflowDocument document = SpringContext.getBean(WorkflowDocumentService.class).createWorkflowDocument(docHeaderId, user);
        if (EdenConstants.ACTION_REQUEST_COMPLETE_REQ.equals(actionRequestedCode)) {
            return document.isCompletionRequested();
        }
        else if (EdenConstants.ACTION_REQUEST_APPROVE_REQ.equals(actionRequestedCode)) {
            return document.isApprovalRequested();
        }
        else if (EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(actionRequestedCode)) {
            return document.isAcknowledgeRequested();
        }
        else if (EdenConstants.ACTION_REQUEST_FYI_REQ.equals(actionRequestedCode)) {
            return document.isFYIRequested();
        }
        return false;
    }
}
