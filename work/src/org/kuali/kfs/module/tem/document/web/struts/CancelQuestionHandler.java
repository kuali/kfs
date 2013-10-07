/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.TemConstants.CANCEL_TA_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.CONFIRM_CANCEL_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.CONFIRM_CANCEL_QUESTION_TEXT;
import static org.kuali.kfs.sys.KFSConstants.MAPPING_BASIC;

import java.util.ArrayList;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelEncumbranceService;
import org.kuali.kfs.module.tem.util.MessageUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 *
 */
public class CancelQuestionHandler implements QuestionHandler<TravelDocument> {

    private TravelEncumbranceService travelEncumbranceService;
    private DocumentService documentService;
    private TravelDocumentService travelDocumentService;

    @Override
    public <T> T handleResponse(final Inquisitive<TravelDocument,?> asker) throws Exception {
        if (asker.denied(CANCEL_TA_QUESTION)) {
            return (T) asker.back();
        }
        else if (asker.confirmed(CONFIRM_CANCEL_QUESTION)) {
            return (T) asker.end();
            // This is the case when the user clicks on "OK" in the end.
            // After we inform the user that the close has been rerouted, we'll redirect to the portal page.
        }
        TravelAuthorizationDocument taDocument = (TravelAuthorizationDocument)asker.getDocument();

        try {
            // Below used as a place holder to allow code to specify actionForward to return if not a 'success question'
            T returnActionForward = (T) ((StrutsInquisitor) asker).getMapping().findForward(MAPPING_BASIC);

            taDocument.refreshReferenceObject(KFSPropertyConstants.GENERAL_LEDGER_PENDING_ENTRIES);

            final Note newNote = getDocumentService().createNoteFromDocument(taDocument, TemConstants.TA_CANCELLED_MESSAGE);
            taDocument.addNote(newNote);

            taDocument.updateAppDocStatus(TravelAuthorizationStatusCodeKeys.CANCELLED);
            getTravelEncumbranceService().liquidateEncumbranceForCancelTA(taDocument);
            SpringContext.getBean(DocumentService.class).saveDocument(taDocument, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
            taDocument.refreshReferenceObject(KFSPropertyConstants.GENERAL_LEDGER_PENDING_ENTRIES);

            // send FYI for to initiator and traveler
            getTravelDocumentService().addAdHocFYIRecipient(taDocument, taDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
            getTravelDocumentService().addAdHocFYIRecipient(taDocument, taDocument.getTraveler().getPrincipalId());

            SpringContext.getBean(WorkflowDocumentService.class).acknowledge(taDocument.getDocumentHeader().getWorkflowDocument(), null, new ArrayList<AdHocRouteRecipient>(taDocument.getAdHocRoutePersons()));

            if (ObjectUtils.isNotNull(returnActionForward)) {
                return returnActionForward;
            }
            else   {
                return (T) asker.confirm(CANCEL_TA_QUESTION, MessageUtils.getMessage(CONFIRM_CANCEL_QUESTION_TEXT), true, "","","","");
            }
        }
        catch (ValidationException ve) {
            throw ve;
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.QuestionHandler#askQuestion(org.kuali.kfs.module.tem.document.web.struts.Inquisitive)
     */
    @Override
    public <T> T askQuestion(final Inquisitive<TravelDocument,?> asker) throws Exception {
        T retval = (T) asker.confirm(CANCEL_TA_QUESTION, CONFIRM_CANCEL_QUESTION_TEXT, false);
        return retval;
    }

    /**
     * Gets the travelEncumbranceService attribute.
     *
     * @return Returns the travelEncumbranceService
     */

    public TravelEncumbranceService getTravelEncumbranceService() {
        return travelEncumbranceService;
    }

    /**
     * Sets the travelEncumbranceService attribute.
     *
     * @param travelEncumbranceService The travelEncumbranceService to set.
     */
    public void setTravelEncumbranceService(TravelEncumbranceService travelEncumbranceService) {
        this.travelEncumbranceService = travelEncumbranceService;
    }

    /**
     * Gets the documentService attribute.
     *
     * @return Returns the documentService
     */

    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the travelDocumentService attribute.
     *
     * @return Returns the travelDocumentService
     */

    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    /**
     * Sets the travelDocumentService attribute.
     *
     * @param travelDocumentService The travelDocumentService to set.
     */
    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }




}