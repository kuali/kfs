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
package org.kuali.module.purap.web.struts.form;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.purap.document.AssignContractManagerDocument;

import edu.iu.uis.eden.EdenConstants;

/**
 * Struts Action Form for Contract Manager Assignment Document.
 */
public class AssignContractManagerForm extends KualiTransactionalDocumentFormBase {

    /**
     * Constructs a AssignContractManagerForm instance
     */
    public AssignContractManagerForm() {
        super();
        setDocument(new AssignContractManagerDocument());
    }

    /**
     * Overrides the method in KualiDocumentFormBase to provide only FYI and ACK.
     * 
     * @see org.kuali.core.web.struts.form.KualiDocumentFormBase#getAdHocActionRequestCodes()
     */
    @Override
    public Map getAdHocActionRequestCodes() {
        Map adHocActionRequestCodes = new HashMap();
        if (getWorkflowDocument() != null) {
            if (getWorkflowDocument().isFYIRequested()) {
                adHocActionRequestCodes.put(EdenConstants.ACTION_REQUEST_FYI_REQ, EdenConstants.ACTION_REQUEST_FYI_REQ_LABEL);
            }
            else if (getWorkflowDocument().isAcknowledgeRequested()) {
                adHocActionRequestCodes.put(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
                adHocActionRequestCodes.put(EdenConstants.ACTION_REQUEST_FYI_REQ, EdenConstants.ACTION_REQUEST_FYI_REQ_LABEL);
            }
            else if (getWorkflowDocument().isApprovalRequested() || getWorkflowDocument().isCompletionRequested() || getWorkflowDocument().stateIsInitiated() || getWorkflowDocument().stateIsSaved()) {
                adHocActionRequestCodes.put(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
                adHocActionRequestCodes.put(EdenConstants.ACTION_REQUEST_FYI_REQ, EdenConstants.ACTION_REQUEST_FYI_REQ_LABEL);
            }
        }
        return adHocActionRequestCodes;
    }
}