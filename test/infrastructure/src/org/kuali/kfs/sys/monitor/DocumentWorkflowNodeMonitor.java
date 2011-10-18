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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

/**
 * Watches the workflow document and indicates valueChanged when either the status or the current node changes.
 */
public class DocumentWorkflowNodeMonitor extends ChangeMonitor {

    private Long docHeaderId;
    private String initiatorPrincipalIdId;
    private String desiredNodeName;

    public DocumentWorkflowNodeMonitor(KualiWorkflowDocument document, String desiredNodeName) throws WorkflowException {
        this.docHeaderId = document.getRouteHeaderId();
        this.initiatorPrincipalIdId = document.getInitiatorPrincipalId();
        this.desiredNodeName = desiredNodeName;
    }

    public boolean valueChanged() throws Exception {
        KualiWorkflowDocument document = SpringContext.getBean(WorkflowDocumentService.class).createWorkflowDocument(docHeaderId, SpringContext.getBean(PersonService.class).getPerson(initiatorPrincipalIdId));
        String currentNodeName = document.getCurrentRouteNodeNames();
        // currently in Kuali there is no parallel branching so we can only ever be at one node
        return StringUtils.equals( desiredNodeName, currentNodeName);
    }

}

