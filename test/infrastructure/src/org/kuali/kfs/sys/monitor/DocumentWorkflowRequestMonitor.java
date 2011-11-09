/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.sys.monitor;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;

public class DocumentWorkflowRequestMonitor extends ChangeMonitor {

    private final Long docHeaderId;
    private final Person user;
    private final String actionRequestedCode;

    public DocumentWorkflowRequestMonitor(Long docHeaderId, Person user, String actionRequestedCode) {
        this.docHeaderId = docHeaderId;
        this.user = user;
        this.actionRequestedCode = actionRequestedCode;
    }

    public boolean valueChanged() throws WorkflowException {
        WorkflowDocument document = SpringContext.getBean(WorkflowDocumentService.class).createWorkflowDocument(docHeaderId, user);
        if (KewApiConstants.ACTION_REQUEST_COMPLETE_REQ.equals(actionRequestedCode)) {
            return document.isCompletionRequested();
        }
        else if (KewApiConstants.ACTION_REQUEST_APPROVE_REQ.equals(actionRequestedCode)) {
            return document.isApprovalRequested();
        }
        else if (KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(actionRequestedCode)) {
            return document.isAcknowledgeRequested();
        }
        else if (KewApiConstants.ACTION_REQUEST_FYI_REQ.equals(actionRequestedCode)) {
            return document.isFYIRequested();
        }
        return false;
    }
}

