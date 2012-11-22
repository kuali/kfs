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

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.replace;
import static org.kuali.kfs.module.tem.TemConstants.AMENDMENT_TA_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.AMEND_NOTE_PREFIX;
import static org.kuali.kfs.module.tem.TemConstants.AMEND_NOTE_SUFFIX;
import static org.kuali.kfs.module.tem.TemConstants.AMEND_TA_TEXT;
import static org.kuali.kfs.module.tem.TemConstants.CONFIRM_AMENDMENT_QUESTION;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_REASON_PASTLIMIT;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_REASON_REQUIRED;
import static org.kuali.kfs.module.tem.TemKeyConstants.TA_QUESTION_DOCUMENT;
import static org.kuali.kfs.sys.KFSConstants.BLANK_SPACE;
import static org.kuali.kfs.sys.KFSConstants.MAPPING_BASIC;
import static org.kuali.kfs.sys.KFSConstants.NOTE_TEXT_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME;

import java.util.List;

import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class AmendQuestionHandler implements QuestionHandler<TravelDocument> {
    private ConfigurationService ConfigurationService;
    private DataDictionaryService dataDictionaryService;
    private TravelService travelService;
    private TravelDocumentService travelDocumentService;
    private DocumentService documentService;
    private DocumentDao documentDao;
    private AccountingDocumentRelationshipService accountingDocumentRelationshipService;

    @Override
    public <T> T handleResponse(final Inquisitive<TravelDocument,?> asker) throws Exception {
        if (asker.denied(AMENDMENT_TA_QUESTION)) {
            return (T) asker.back();
        }
        else if (asker.confirmed(CONFIRM_AMENDMENT_QUESTION)) {
            return (T) asker.end();
            // This is the case when the user clicks on "OK" in the end.
            // After we inform the user that the close has been rerouted, we'll redirect to the portal page.
        }
        TravelDocument document = asker.getDocument();

        // Build out full message.
        String note = createNote(asker.getReason(), document.getDocumentNumber());
        final StringBuilder noteText = new StringBuilder(note);

        int noteTextLength = noteText.length();

        // Get note text max length from DD.
        int noteTextMaxLength = getDataDictionaryService().getAttributeMaxLength(Note.class, NOTE_TEXT_PROPERTY_NAME).intValue();
        if (isBlank(asker.getReason()) || (noteTextLength > noteTextMaxLength)) {
            // Figure out exact number of characters that the user can enter.
            int reasonLimit = noteTextMaxLength - noteTextLength;
            reasonLimit = reasonLimit<0?reasonLimit*-1:reasonLimit;
            String message = getMessageFrom(TA_QUESTION_DOCUMENT);
            String question = replace(message, "{0}", AMEND_TA_TEXT);
            if (isBlank(asker.getReason())){
                return (T) asker.confirm(AMENDMENT_TA_QUESTION, question, true, ERROR_TA_REASON_REQUIRED,QUESTION_REASON_ATTRIBUTE_NAME,AMEND_TA_TEXT);
            }
            else {
                return (T) asker.confirm(AMENDMENT_TA_QUESTION, question, true, ERROR_TA_REASON_PASTLIMIT, QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
            }
        }

        // String previousDocumentId = ((StrutsInquisitor) asker).getForm().getDocId();
        String previousDocumentId = null;
        try {
            // Below used as a place holder to allow code to specify actionForward to return if not a 'success question'
            T returnActionForward = null;
            returnActionForward = (T) ((StrutsInquisitor) asker).getMapping().findForward(MAPPING_BASIC);

            final Note newNote = getDocumentService().createNoteFromDocument(document, noteText.toString());
            newNote.setNoteText(noteText.toString());
            document.updateAppDocStatus(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT);
            getDocumentDao().save(document);
            String headerID = document.getDocumentHeader().getDocumentNumber();

            TravelAuthorizationAmendmentDocument taaDocument = ((TravelAuthorizationDocument) document).toCopyTAA();
            getDocumentService().addNoteToDocument(taaDocument, newNote);
            Note secondNote = getDocumentService().createNoteFromDocument(document, getMessageFrom(TemKeyConstants.TA_MESSAGE_AMEND_DOCUMENT_TEXT));
            getDocumentService().addNoteToDocument(document, secondNote);
            getDocumentDao().save(document);

            TravelAuthorizationForm form = (TravelAuthorizationForm) ((StrutsInquisitor) asker).getForm();
            form.setDocTypeName(TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);
            form.setDocument(taaDocument);

             taaDocument.setAppDocStatus(TravelAuthorizationStatusCodeKeys.CHANGE_IN_PROCESS);

            //save the TAA document once so it will not be lost
            getDocumentService().saveDocument(taaDocument);

            // add relationship
            String documentType = document instanceof TravelAuthorizationAmendmentDocument ? TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT : TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT;
            String relationDescription = documentType + " - " + TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT;
            accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(document.getDocumentNumber(), taaDocument.getDocumentNumber(), relationDescription));

            // add an additional relationship to the original TA
            if (documentType.equals(TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT)){
                relationDescription = TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT + " - " + TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT;

                List<Document> travelAuthDocs = travelDocumentService.getDocumentsRelatedTo(document, TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
                //there should only be one travel auth document
                accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(travelAuthDocs.get(0).getDocumentNumber(), taaDocument.getDocumentNumber(), relationDescription));
            }

            if (ObjectUtils.isNotNull(returnActionForward)) {
                return returnActionForward;
            }
            else {
                String message = getMessageFrom(TA_QUESTION_DOCUMENT);
                String question = replace(message, "{0}", AMEND_TA_TEXT);
                return (T) asker.confirm(AMENDMENT_TA_QUESTION, question, true, "temSingleConfirmationQuestion", AMENDMENT_TA_QUESTION, "");
            }
        }
        catch (ValidationException ve) {
            throw ve;
        }
    }


    private String createNote(String reason, String documentNumber) {
        String introNoteMessage = AMEND_NOTE_PREFIX + BLANK_SPACE;
        String suffix = replace(AMEND_NOTE_SUFFIX, "{0}", documentNumber);
        return introNoteMessage + reason + BLANK_SPACE + suffix;
    }


    @Override
    public <T> T askQuestion(final Inquisitive<TravelDocument,?> asker) throws Exception {
        final String key      = getMessageFrom(TA_QUESTION_DOCUMENT);
        final String question = replace(key, "{0}", AMEND_TA_TEXT);

        T retval = (T) asker.confirm(AMENDMENT_TA_QUESTION, question,true);
        return retval;

    }

    public String getReturnToFiscalOfficerQuestion(final String operation) {
        String message = "";
        //final String key = getConfigurationService().getPropertyString(TR_FISCAL_OFFICER_QUESTION);
        final String key = getConfigurationService().getPropertyString(TA_QUESTION_DOCUMENT);
        message = replace(key, "{0}", operation);
        // Ask question if not already asked.
        return message;
    }

    public String getMessageFrom(final String messageType) {
        return getConfigurationService().getPropertyString(messageType);
    }

    public String getReturnToFiscalOfficerNote(final String notePrefix, String reason) {
        String noteText = "";
        // Have to check length on value entered.
        final String introNoteMessage = notePrefix + BLANK_SPACE;

        // Build out full message.
        noteText = introNoteMessage + reason;
        final int noteTextLength = noteText.length();

        // Get note text max length from DD.
        final int noteTextMaxLength = getDataDictionaryService().getAttributeMaxLength(Note.class, NOTE_TEXT_PROPERTY_NAME).intValue();

        if (isBlank(reason) || (noteTextLength > noteTextMaxLength)) {
            // Figure out exact number of characters that the user can enter.
            int reasonLimit = noteTextMaxLength - noteTextLength;

            if (isNull(reason)) {
                // Prevent a NPE by setting the reason to a blank string.
                reason = "";
            }
        }
        return noteText;
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
     * Sets the travelService attribute.
     *
     * @return Returns the travelService.
     */
    public void setTravelService(final TravelService travelService) {
        this.travelService = travelService;
    }

    /**
     * Gets the travelService attribute.
     *
     * @return Returns the travelService.
     */
    protected TravelService getTravelService() {
        return travelService;
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

    public DocumentDao getDocumentDao() {
        return documentDao;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    /**
     * Sets the documentService attribute.
     *
     * @return Returns the documentService.
     */
    public void setDocumentService(final DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the documentService attribute.
     *
     * @return Returns the documentService.
     */
    protected DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }


    public AccountingDocumentRelationshipService getAccountingDocumentRelationshipService() {
        return accountingDocumentRelationshipService;
    }


    public void setAccountingDocumentRelationshipService(AccountingDocumentRelationshipService accountingDocumentRelationshipService) {
        this.accountingDocumentRelationshipService = accountingDocumentRelationshipService;
    }


    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }
}