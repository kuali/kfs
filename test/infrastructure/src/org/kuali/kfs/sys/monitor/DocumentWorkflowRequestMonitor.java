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
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

public class DocumentWorkflowRequestMonitor extends ChangeMonitor {

    final String docHeaderId;
    final Person user;
    final ActionRequestType actionRequested;

    public DocumentWorkflowRequestMonitor(String docHeaderId, Person user, ActionRequestType actionRequested) {
        this.docHeaderId = docHeaderId;
        this.user = user;
        this.actionRequested = actionRequested;
    }

    public boolean valueChanged() throws WorkflowException {
        WorkflowDocument document = SpringContext.getBean(WorkflowDocumentService.class).loadWorkflowDocument(docHeaderId, user);
        if ( ActionRequestType.COMPLETE.equals(actionRequested)) {
            return document.isCompletionRequested();
        }
        else if (ActionRequestType.APPROVE.equals(actionRequested)) {
            return document.isApprovalRequested();
        }
        else if (ActionRequestType.ACKNOWLEDGE.equals(actionRequested)) {
            return document.isAcknowledgeRequested();
        }
        else if (ActionRequestType.FYI.equals(actionRequested)) {
            return document.isFYIRequested();
        }
        return false;
    }
}

