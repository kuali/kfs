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

import java.util.Iterator;
import java.util.Set;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * Watches the workflow document and indicates valueChanged when either the status or the current node changes.
 */
public class DocumentWorkflowNodeMonitor extends ChangeMonitor {

    private String docHeaderId;
    private String initiatorPrincipalIdId;
    private String desiredNodeName;

    public DocumentWorkflowNodeMonitor(WorkflowDocument document, String desiredNodeName) throws WorkflowException {
        this.docHeaderId = document.getDocumentId();
        this.initiatorPrincipalIdId = document.getInitiatorPrincipalId();
        this.desiredNodeName = desiredNodeName;
    }

    public boolean valueChanged() throws Exception {
        WorkflowDocument document = SpringContext.getBean(WorkflowDocumentService.class).loadWorkflowDocument(docHeaderId, SpringContext.getBean(PersonService.class).getPerson(initiatorPrincipalIdId));
        return WorkflowTestUtils.isAtNode(document, desiredNodeName);
    }

}

