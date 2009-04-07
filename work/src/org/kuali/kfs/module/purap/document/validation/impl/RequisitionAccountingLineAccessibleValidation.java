/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class RequisitionAccountingLineAccessibleValidation extends PurchasingAccountsPayableAccountingLineAccessibleValidation {

    /**
     * Validates that the given accounting line is accessible for editing by the current user.
     * <strong>This method expects a document as the first parameter and an accounting line as the second</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        KualiWorkflowDocument workflowDocument = event.getDocument().getDocumentHeader().getWorkflowDocument();
        List currentRouteLevels = getCurrentRouteLevels(workflowDocument);
        RequisitionDocument requisitionDocument = (RequisitionDocument) event.getDocument();
        if (requisitionDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.CONTENT_REVIEW) ||
            requisitionDocument.getStatusCode().equals(PurapConstants.RequisitionStatuses.IN_PROCESS)) {
            // DO NOTHING: do not check that user owns acct lines; at this level, approvers can edit all detail on REQ

            return true;
        }
        else {

            return super.validate(event);
        }
    }
    
}

