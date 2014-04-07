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

import static org.kuali.kfs.module.tem.TemConstants.CANCEL_NOTE_PREFIX;
import static org.kuali.kfs.module.tem.TemConstants.CANCEL_TA_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.CANCEL_TA_TEXT;
import static org.kuali.kfs.module.tem.TemConstants.CONFIRM_CANCEL_QUESTION;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_REASON_PASTLIMIT;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_REASON_REQUIRED;
import static org.kuali.kfs.module.tem.TemKeyConstants.TA_QUESTION_DOCUMENT;
import static org.kuali.kfs.sys.KFSConstants.BLANK_SPACE;
import static org.kuali.kfs.sys.KFSConstants.MAPPING_BASIC;
import static org.kuali.kfs.sys.KFSConstants.NOTE_TEXT_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelEncumbranceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 *
 */
public class CancelQuestionHandler implements QuestionHandler<TravelDocument> {

    private ConfigurationService ConfigurationService;
    private TravelEncumbranceService travelEncumbranceService;
    private DocumentService documentService;
    private TravelDocumentService travelDocumentService;
    private DataDictionaryService dataDictionaryService;


    @Override
    public <T> T handleResponse(final Inquisitive<TravelDocument,?> asker) throws Exception {
        if (asker.denied(CANCEL_TA_QUESTION)) {
            return (T) asker.back();
        }
        else if (asker.confirmed(CONFIRM_CANCEL_QUESTION)) {
            return (T) asker.end();
        }
        TravelAuthorizationDocument taDocument = (TravelAuthorizationDocument)asker.getDocument();

        String note = createNote(asker.getReason(), taDocument.getDocumentNumber());
        final StringBuilder noteText = new StringBuilder(note);

        int noteTextLength = noteText.length();

        // Get note text max length from DD.
        int noteTextMaxLength = getDataDictionaryService().getAttributeMaxLength(Note.class, NOTE_TEXT_PROPERTY_NAME).intValue();
        if (StringUtils.isBlank(asker.getReason()) || (noteTextLength > noteTextMaxLength)) {
            // Figure out exact number of characters that the user can enter.
            int reasonLimit = noteTextMaxLength - noteTextLength;
            reasonLimit = reasonLimit<0?reasonLimit*-1:reasonLimit;
            String message = getMessageFrom(TA_QUESTION_DOCUMENT);
            String question = StringUtils.replace(message, "{0}", CANCEL_TA_TEXT);
            if (StringUtils.isBlank(asker.getReason())){
                return (T) asker.confirm(CANCEL_TA_QUESTION, question, true, ERROR_TA_REASON_REQUIRED,QUESTION_REASON_ATTRIBUTE_NAME,CANCEL_TA_TEXT);
            }
            else {
                return (T) asker.confirm(CANCEL_TA_QUESTION, question, true, ERROR_TA_REASON_PASTLIMIT, QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
            }
        }

        try {
            // Below used as a place holder to allow code to specify actionForward to return if not a 'success question'
            T returnActionForward = (T) ((StrutsInquisitor) asker).getMapping().findForward(MAPPING_BASIC);

            taDocument.refreshReferenceObject(KFSPropertyConstants.GENERAL_LEDGER_PENDING_ENTRIES);

            final Note reasonNote = getDocumentService().createNoteFromDocument(taDocument, noteText.toString());
            reasonNote.setNoteText(noteText.toString());
            taDocument.addNote(reasonNote);

            final Note cancelNote = getDocumentService().createNoteFromDocument(taDocument, TemConstants.TA_CANCELLED_MESSAGE);
            Principal systemUser = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER);
            cancelNote.setAuthorUniversalIdentifier(systemUser.getPrincipalId());
            taDocument.addNote(cancelNote);

            taDocument.updateAndSaveAppDocStatus(TravelAuthorizationStatusCodeKeys.CANCELLED);
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

                String message = getMessageFrom(TA_QUESTION_DOCUMENT);
                String question = StringUtils.replace(message, "{0}", CANCEL_TA_TEXT);
                return (T) asker.confirm(CANCEL_TA_QUESTION, question, true, "temSingleConfirmationQuestion", CANCEL_TA_QUESTION, "");
                //return (T) asker.confirm(CANCEL_TA_QUESTION, MessageUtils.getMessage(CONFIRM_CANCEL_QUESTION_TEXT), true, "","","","");
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
        final String key      = getMessageFrom(TA_QUESTION_DOCUMENT);
        final String question = StringUtils.replace(key, "{0}", CANCEL_TA_TEXT);
        T retval = (T) asker.confirm(CANCEL_TA_QUESTION, question, true);
        return retval;
    }

    public String getMessageFrom(final String messageType) {
        return getConfigurationService().getPropertyValueAsString(messageType);
    }

    private String createNote(String reason, String documentNumber) {
        String introNoteMessage = CANCEL_NOTE_PREFIX + BLANK_SPACE;
        return introNoteMessage + reason;
    }

    /**
     * Sets the ConfigurationService attribute.
     *
     * @return Returns the ConfigurationService.
     */
    public void setConfigurationService(final ConfigurationService ConfigurationService) {
        this.ConfigurationService = ConfigurationService;
    }

    /**
     * Gets the ConfigurationService attribute.
     *
     * @return Returns the ConfigurationService.
     */
    protected ConfigurationService getConfigurationService() {
        return ConfigurationService;
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

    /**
     * Sets the dataDictionaryService attribute.
     *
     * @return Returns the dataDictionaryService.
     */
    public void setDataDictionaryService(final DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the dataDictionaryService attribute.
     *
     * @return Returns the dataDictionaryService.
     */
    protected DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }


}