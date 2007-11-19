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

import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.context.SpringContext;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Watches the workflow document and indicates valueChanged when either the status or the current node changes.
 */
public class DocumentWorkflowNodeMonitor extends ChangeMonitor {

    private Long docHeaderId;
    private String networkId;
    private String desiredNodeName;

    public DocumentWorkflowNodeMonitor(KualiWorkflowDocument document, String desiredNodeName) throws WorkflowException {
        this.docHeaderId = document.getRouteHeaderId();
        this.networkId = document.getInitiatorNetworkId();
        this.desiredNodeName = desiredNodeName;
    }

    public boolean valueChanged() throws Exception {
        KualiWorkflowDocument document = SpringContext.getBean(WorkflowDocumentService.class).createWorkflowDocument(docHeaderId, SpringContext.getBean(UniversalUserService.class).getUniversalUser(new AuthenticationUserId(networkId)));
        String currentNodeName = null;
        if (document.getNodeNames().length > 0) {
            currentNodeName = document.getNodeNames()[0];
        }
        // currently in Kuali there is no parallel branching so we can only ever be at one node
        return desiredNodeName.equals(currentNodeName);
    }

}
