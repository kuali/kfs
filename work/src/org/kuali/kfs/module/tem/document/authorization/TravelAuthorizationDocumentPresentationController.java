/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;

/**
 * Travel Reimbursement Document Presentation Controller
 *
 */
public class TravelAuthorizationDocumentPresentationController extends TravelDocumentPresentationController{

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        addFullEntryEditMode(document, editModes);
        editModes.remove(TemConstants.EditModes.CHECK_AMOUNT_ENTRY);
        editModes.add(TemConstants.TravelEditMode.ADVANCE_PAYMENT_ENTRY);
        editModes.add(TemConstants.TravelEditMode.ADVANCE_POLICY_ENTRY);
        if (document instanceof TravelAuthorizationDocument && ((TravelAuthorizationDocument)document).shouldProcessAdvanceForDocument() && isAtTravelNode(document.getDocumentHeader().getWorkflowDocument())) {
            editModes.add(TemConstants.TravelEditMode.CLEAR_ADVANCE_MODE);
        }
        return editModes;
    }

    /**
     * Determines if the current workflow document is at the Travel node
     * @param workflowDocument the workflow document to check the node of
     * @return true if the document is at the Travel node, false otherwise
     */
    public boolean isAtTravelNode(WorkflowDocument workflowDocument) {
        return workflowDocument.getCurrentNodeNames().contains(TemWorkflowConstants.RouteNodeNames.AP_TRAVEL);
    }

    /**
     * Overridden to allow copy on default
     * @see org.kuali.rice.krad.document.DocumentPresentationControllerBase#canCopy(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCopy(Document document) {
        return (document.getDocumentHeader().getWorkflowDocument().isProcessed() || document.getDocumentHeader().getWorkflowDocument().isFinal());
    }
}
