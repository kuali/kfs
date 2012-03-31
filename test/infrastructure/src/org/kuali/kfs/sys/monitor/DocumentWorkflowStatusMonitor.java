/*
 * Copyright 2005-2006 The Kuali Foundation
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
