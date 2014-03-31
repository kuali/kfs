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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

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

        WorkflowDocument wfDocument = document.getDocumentHeader().getWorkflowDocument();

        Set<String> editModes = super.getEditModes(document);
        addFullEntryEditMode(document, editModes);
        editModes.remove(TemConstants.EditModes.CHECK_AMOUNT_ENTRY);
        editModes.add(TemConstants.TravelEditMode.ADVANCE_PAYMENT_ENTRY);
        if (shouldAllowBlanketTravelEntry(document)) {
            editModes.add(TemConstants.EditModes.BLANKET_TRAVEL_ENTRY);
        }
        if (!wfDocument.isInitiated() && !wfDocument.isSaved()) {
            editModes.add(TemConstants.EditModes.BLANKET_TRAVEL_VIEW);
        }

        if (document instanceof TravelAuthorizationDocument && ((TravelAuthorizationDocument)document).shouldProcessAdvanceForDocument() && isAtTravelerNode(wfDocument) || wfDocument.isInitiated() || wfDocument.isSaved()) {
            editModes.add(TemConstants.TravelEditMode.ADVANCE_POLICY_ENTRY);
        }
        if (document instanceof TravelAuthorizationDocument && ((TravelAuthorizationDocument)document).shouldProcessAdvanceForDocument() && isAtTravelNode(wfDocument)) {
            editModes.add(TemConstants.TravelEditMode.CLEAR_ADVANCE_MODE);
        }

        final Set<String> nodeNames = document.getDocumentHeader().getWorkflowDocument().getNodeNames();
        if (wfDocument.isInitiated() || wfDocument.isSaved() || (nodeNames != null && !nodeNames.isEmpty() && (nodeNames.contains(TemWorkflowConstants.RouteNodeNames.TAX) || nodeNames.contains(TemWorkflowConstants.RouteNodeNames.AP_TRAVEL)))) {
            editModes.add(TemConstants.EditModes.EXPENSE_TAXABLE_MODE);
        }

        if (wfDocument.isInitiated() || wfDocument.isSaved() || (nodeNames != null && !nodeNames.isEmpty() && nodeNames.contains(TemWorkflowConstants.RouteNodeNames.AP_TRAVEL))) {
            editModes.add(TemConstants.EditModes.EXPENSE_LIMIT_ENTRY);
        }

        return editModes;
    }

    /**
     * Determines whether blanket travel selection will be given at all on the travel authorization document.  If a trip type is not
     * selected, or the trip type does not allow blanket travel, then the blanket travel indicator will not be shown; otherwise, if
     * there is a trip type and it allows blanket travel, the selection option will be shown
     * @return true if the blanket travel selection should be shown, false otherwise
     */
    protected boolean shouldAllowBlanketTravelEntry(Document document) {
        if (!(document instanceof TravelAuthorizationDocument)) {
            return false; // also, you're using the wrong damn authorizer
        }
        final TravelAuthorizationDocument travelAuthorization = (TravelAuthorizationDocument)document;
        if ((!travelAuthorization.getDocumentHeader().getWorkflowDocument().isInitiated() && !travelAuthorization.getDocumentHeader().getWorkflowDocument().isSaved()) || StringUtils.isBlank(travelAuthorization.getTripTypeCode())) {
            return false;
        }
        if (ObjectUtils.isNull(travelAuthorization.getTripType())) {
            travelAuthorization.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        }
        return !ObjectUtils.isNull(travelAuthorization.getTripType()) && travelAuthorization.getTripType().isBlanketTravel();

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
     * Determines if the current workflow document is at the Traveler node
     * @param workflowDocument the workflow document to check the node of
     * @return true if the document is at the Traveler node, false otherwise
     */
    public boolean isAtTravelerNode(WorkflowDocument workflowDocument) {
        return workflowDocument.getCurrentNodeNames().contains(TemWorkflowConstants.RouteNodeNames.TRAVELER);
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
        if (canNewReimbursement(travelAuth)) {
            actions.add(TemConstants.TravelAuthorizationActions.CAN_NEW_REIMBURSEMENT);
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
        if (can) {
            //If there are TR's that are not canceled or disapproved, disabled amend
            final TravelDocumentService travelDocumentService = SpringContext.getBean(TravelDocumentService.class);
            final List<Document> trRelatedDocumentList = travelDocumentService.getDocumentsRelatedTo(document, TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
            for (Document trDocument : trRelatedDocumentList) {
                can &= trDocument.getDocumentHeader().getWorkflowDocument().isCanceled() || trDocument.getDocumentHeader().getWorkflowDocument().isDisapproved();
            }
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
     * @return true if the authorization can be closed, false otherwise
     */
    public boolean canCloseAuthorization(TravelAuthorizationDocument document) {
        return isOpen(document) && (isFinalOrProcessed(document)) && hasReimbursements(document) && !hasEnrouteReimbursements(document);
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
     * Determines if a reimbursement can be initiated for this document. This is done for {@link TravelAuthorizationDocument} instances
     * that have a workflow document status of FINAL or PROCESSED and on documents that do not have a workflow
     * App Doc Status of REIMB_HELD, CANCELLED, PEND_AMENDMENT, CLOSED, or RETIRED_VERSION. Also checks if the person has permission to
     * initiate the target documents. Will not show the new Reimbursement link if a TA already has a TR enroute.
     *
     * If the document is a TAC, the workflow document status must be FINAL and the App Doc Status must be CLOSED.
     *
     * check status of document and don't create if the status is not final or processed
     *
     * @param document
     * @return
     */
    public boolean canNewReimbursement(TravelAuthorizationDocument document) {
        final String documentType = document.getDocumentTypeName();

        boolean documentStatusCheck = isFinalOrProcessed(document);

        final String appDocStatus = document.getApplicationDocumentStatus();
        boolean appDocStatusCheck = (!appDocStatus.equals(TravelAuthorizationStatusCodeKeys.REIMB_HELD)
                                    && !appDocStatus.equals(TravelAuthorizationStatusCodeKeys.CANCELLED)
                                    && !appDocStatus.equals(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT)
                                    && !appDocStatus.equals(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION)
                                    && !appDocStatus.equals(TravelAuthorizationStatusCodeKeys.CLOSED));

        boolean statusCheck = documentStatusCheck && appDocStatusCheck;

        Person user = GlobalVariables.getUserSession().getPerson();
        boolean hasInitAccess = false;
        if (getTemRoleService().canAccessTravelDocument(document, user) && !ObjectUtils.isNull(document.getTraveler()) && document.getTemProfileId() != null && !ObjectUtils.isNull(document.getTemProfile())){
            //check if user also can init other docs
            hasInitAccess = user.getPrincipalId().equals(document.getTraveler().getPrincipalId()) || getTemRoleService().isTravelDocumentArrangerForProfile(documentType, user.getPrincipalId(), document.getTemProfileId()) || getTemRoleService().isTravelArranger(user, document.getTemProfile().getHomeDepartment() , document.getTemProfileId().toString(), documentType);
        }

        boolean checkRelatedDocs = true;
        if (documentType.equals(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)) {

            //check whether there is already an ENROUTE TR
            List<Document> docs = getTravelDocumentService().getDocumentsRelatedTo(document, documentType);
            for (Document doc : docs) {
                TravelReimbursementDocument trDoc = (TravelReimbursementDocument)doc;
                if (trDoc.getDocumentHeader().getWorkflowDocument().isEnroute()) {
                    checkRelatedDocs &= false;
                }
            }

            //a TR document can be processed against a closed TA. If the TAC is Final/Closed display the TR link.
            if (document.getDocumentTypeName().equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT)) {
                documentStatusCheck = document.getDocumentHeader().getWorkflowDocument().isFinal();
                appDocStatusCheck = appDocStatus.equals(TravelAuthorizationStatusCodeKeys.CLOSED);
                statusCheck = documentStatusCheck && appDocStatusCheck;
            }
        }

        return statusCheck && hasInitAccess && checkRelatedDocs;
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

    /**
     * Determines if the given travel authorization document has any enroute reimbursements
     * @param document the travel authorization to find enroute reimbursements for
     * @return true if there are any enroute reimbursements, false otherwise
     */
    protected boolean hasEnrouteReimbursements(TravelAuthorizationDocument document) {
        List<TravelReimbursementDocument> reimbursements = getTravelDocumentService().findReimbursementDocuments(document.getTravelDocumentIdentifier());
        for (TravelReimbursementDocument reimbursement : reimbursements) {
            if (StringUtils.equals(KFSConstants.DocumentStatusCodes.ENROUTE, reimbursement.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode())) {
                return true;
            }
        }
        return false;
    }


}
