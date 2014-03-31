/*
 * Copyright 2010 The Kuali Foundation.
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
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.rice.krad.document.Document;


/**
 * Travel Reimbursement Document Presentation Controller
 *
 */
public class TravelRelocationDocumentPresentationController extends TravelDocumentPresentationController {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        addFullEntryEditMode(document, editModes);
        editModes.remove(TemConstants.EditModes.CHECK_AMOUNT_ENTRY);  // the check amount cannot be edited on moving & relocation documents

        final Set<String> nodeNames = document.getDocumentHeader().getWorkflowDocument().getNodeNames();
        if (document.getDocumentHeader().getWorkflowDocument().isInitiated() || document.getDocumentHeader().getWorkflowDocument().isSaved() || (nodeNames != null && !nodeNames.isEmpty() && (nodeNames.contains(TemWorkflowConstants.RouteNodeNames.TAX) || nodeNames.contains(TemWorkflowConstants.RouteNodeNames.MOVING_AND_RELOCATION_MANAGER)))) {
            editModes.add(TemConstants.EditModes.EXPENSE_TAXABLE_MODE);
        }

        if (isRootTravelDocument((TravelDocument)document)) {
            editModes.add(TemConstants.EditModes.REQUESTER_LOOKUP_MODE);
        }

        if (document.getDocumentHeader().getWorkflowDocument().isInitiated() || document.getDocumentHeader().getWorkflowDocument().isSaved() || (nodeNames != null && !nodeNames.isEmpty() && nodeNames.contains(TemWorkflowConstants.RouteNodeNames.MOVING_AND_RELOCATION_MANAGER))) {
            editModes.add(TemConstants.EditModes.EXPENSE_LIMIT_ENTRY);
        }

        return editModes;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getDocumentActions(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getDocumentActions(Document document) {
        TravelRelocationDocument travelRelocationDocument = (TravelRelocationDocument) document;
        Set<String> actions = super.getDocumentActions(document);
        if (canNewRelocation(travelRelocationDocument)) {
            actions.add(TemConstants.TravelAuthorizationActions.CAN_NEW_RELOCATION);
        }

        return actions;
    }

    /**
     * Determines if a relocation can be initiated for this document.
     * @param document
     * @return
     */
    public boolean canNewRelocation(TravelRelocationDocument document) {
        return document.isTripProgenitor() && !document.getDocumentHeader().getWorkflowDocument().isInitiated();
    }

    /**
     * @see org.kuali.kfs.module.tem.document.authorization.TravelDocumentPresentationController#getDocumentManagerApprovalNode()
     */
    @Override
    public String getDocumentManagerApprovalNode(){
        return TemWorkflowConstants.RouteNodeNames.MOVING_AND_RELOCATION_MANAGER;
    }

}
