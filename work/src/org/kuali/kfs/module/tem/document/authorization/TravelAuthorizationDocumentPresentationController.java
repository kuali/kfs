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

import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;

/**
 * Travel Reimbursement Document Presentation Controller
 *
 */
public class TravelAuthorizationDocumentPresentationController extends TravelAuthorizationFamilyDocumentPresentationController {
    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        addFullEntryEditMode(document, editModes);
        editModes.remove(TemConstants.EditModes.CHECK_AMOUNT_ENTRY);
        editModes.add(TemConstants.TravelEditMode.ADVANCE_PAYMENT_ENTRY);
        if (document instanceof TravelAuthorizationDocument && ((TravelAuthorizationDocument)document).shouldProcessAdvanceForDocument() && isAtTravelNode(document.getDocumentHeader().getWorkflowDocument())) {
            editModes.add(TemConstants.TravelEditMode.CLEAR_ADVANCE_MODE);
            editModes.add(TemConstants.TravelEditMode.ADVANCE_POLICY_ENTRY);
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
     * Overridden to allow copy on default, if the document is processed or final
     * @see org.kuali.rice.krad.document.DocumentPresentationControllerBase#canCopy(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCopy(Document document) {
        return (document.getDocumentHeader().getWorkflowDocument().isProcessed() || document.getDocumentHeader().getWorkflowDocument().isFinal());
    }

    /**
     * Overridden to handle travel authorization specific actions, such as amend, hold, remove hold, close TA, cancel TA, and pay vendor
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getDocumentActions(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getDocumentActions(Document document) {
        Set<String> actions = super.getDocumentActions(document);
        final TravelAuthorizationDocument travelAuth = (TravelAuthorizationDocument)document;
        if (canAmend(travelAuth)) {
            actions.add(TemConstants.TravelAuthorizationActions.CAN_AMEND);
        }
        if (canHold(travelAuth)) {
            actions.add(TemConstants.TravelAuthorizationActions.CAN_HOLD);
        }
        if (canRemoveHold(travelAuth)) {
            actions.add(TemConstants.TravelAuthorizationActions.CAN_REMOVE_HOLD);
        }
        if (canCloseAuthorization(travelAuth)) {
            actions.add(TemConstants.TravelAuthorizationActions.CAN_CLOSE_TA);
        }
        if (canCancelAuthorization(travelAuth)) {
            actions.add(TemConstants.TravelAuthorizationActions.CAN_CANCEL_TA);
        }
        if (canPayVendor(travelAuth)) {
            actions.add(TemConstants.TravelAuthorizationActions.CAN_PAY_VENDOR);
        }
        return actions;
    }

    /**
     * Determines whether the authorization is in a state where it can be amended
     * @param document the authorization to test
     * @return true if the authorization can be amended, false otherwise
     */
    public boolean canAmend(TravelAuthorizationDocument document) {
        boolean can = isOpen(document) && (isFinalOrProcessed(document));
        final DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        java.util.Date today = dateTimeService.getCurrentDate();

        if (document.getTripBegin() != null) {
            can &= today.before(document.getTripBegin());
        }

        if (can) {
            //If there are TR's, disabled amend
            final TravelDocumentService travelDocumentService = SpringContext.getBean(TravelDocumentService.class);
            final List<Document> trRelatedDocumentList = travelDocumentService.getDocumentsRelatedTo(document, TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
            can = trRelatedDocumentList == null || trRelatedDocumentList.isEmpty();
        }

        return can;
    }

    /**
     * Determines if the authorization can be held
     * @param document the authorization to check
     * @return true if the authorization can be held, false otherwise
     */
    public boolean canHold(TravelAuthorizationDocument document) {
        return isOpen(document) && (isFinalOrProcessed(document));
    }

    /**
     * Determines if the authorization can remove a hold on it
     * @param document the authorization to check
     * @return true if the authorization is held and that hold can be removed, false otherwise
     */
    public boolean canRemoveHold(TravelAuthorizationDocument document) {
        return isHeld(document) && (isFinalOrProcessed(document));
    }

    /**
     * Determines if the authorization can be closed
     * @param document the authorization to check
     * @return true if the authroziation can be closed, false otherwise
     */
    public boolean canCloseAuthorization(TravelAuthorizationDocument document) {
        return isOpen(document) && (isFinalOrProcessed(document)) && hasReimbursements(document);
    }

    /**
     * Determines if the authorization can be canceled.  Default logic verifies that the authorization is open and has not yet been reimbursed
     * @param document the authorization to check
     * @return true if the authorization can be canceled, false otherwise
     */
    public boolean canCancelAuthorization(TravelAuthorizationDocument document) {
        boolean can = isOpen(document) && (isFinalOrProcessed(document));

        // verify that there are no reimbursements out there for this doc
        if (can) {
            if (hasReimbursements(document)) {
                can = false;
            }
        }
        return can;
    }

    /**
     * is this document on hold for reimbursement workflow state?
     *
     * @param document the travel authorization to check
     * @return true if the document is in held status, false otherwise
     */
    protected boolean isHeld(TravelAuthorizationDocument document) {
        return TemConstants.TravelAuthorizationStatusCodeKeys.REIMB_HELD.equals(document.getAppDocStatus());
    }

    /**
     * Determines if the given TravelAuthorizationDocument has reimbursements or not
     * @param document the authorization to check
     * @return true if the authorization has reimbursements against it, false otherwise
     */
    protected boolean hasReimbursements(TravelAuthorizationDocument document) {
        List<TravelReimbursementDocument> reimbursements = getTravelDocumentService().findReimbursementDocuments(document.getTravelDocumentIdentifier());
        return reimbursements != null && !reimbursements.isEmpty();
    }


}
