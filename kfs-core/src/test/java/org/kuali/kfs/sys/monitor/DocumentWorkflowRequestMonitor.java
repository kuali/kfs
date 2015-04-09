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

