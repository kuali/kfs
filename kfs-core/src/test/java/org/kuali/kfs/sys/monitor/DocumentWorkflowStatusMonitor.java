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
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * DocumentWorkflowStatusMonitor
 */
public class DocumentWorkflowStatusMonitor extends ChangeMonitor {
    final private String documentNumber;
    final private DocumentStatus[] desiredWorkflowStates;

    public DocumentWorkflowStatusMonitor(String docHeaderId, DocumentStatus... desiredWorkflowStates) {
        this.documentNumber = docHeaderId;
        this.desiredWorkflowStates = desiredWorkflowStates;
    }

    @Override
    public boolean valueChanged() throws Exception {
        WorkflowDocument document = SpringContext.getBean(WorkflowDocumentService.class).loadWorkflowDocument(documentNumber, UserNameFixture.kfs.getPerson() );

        DocumentStatus currentStatus = document.getStatus();

        for ( DocumentStatus desiredState : desiredWorkflowStates ) {
            if (desiredState.equals(currentStatus)) {
                return true;
            }
        }
        return false;
    }
}
