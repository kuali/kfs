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
import static org.kuali.kfs.module.tem.TemConstants.CLOSE_TA_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.CONFIRM_CLOSE_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.CONFIRM_CLOSE_QUESTION_TEXT;
import static org.kuali.kfs.module.tem.TemKeyConstants.TA_MESSAGE_CLOSE_DOCUMENT_TEXT;
import static org.kuali.kfs.sys.KFSConstants.BLANK_SPACE;
import static org.kuali.kfs.sys.KFSConstants.MAPPING_BASIC;
import static org.kuali.kfs.sys.KFSConstants.NOTE_TEXT_PROPERTY_NAME;
import static org.kuali.rice.kns.util.ObjectUtils.isNotNull;
import static org.kuali.rice.kns.util.ObjectUtils.isNull;

import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.document.TravelAuthorizationCloseDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class CloseQuestionHandler implements QuestionHandler<TravelDocument> {
    private KualiConfigurationService kualiConfigurationService;
    private DataDictionaryService dataDictionaryService;
    private TravelDocumentService travelDocumentService;
    private DocumentService documentService;
    private DocumentDao documentDao;
    private AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    
    @Override
    public <T> T handleResponse(final Inquisitive<TravelDocument,?> asker) throws Exception {
        if (asker.denied(CLOSE_TA_QUESTION)) {
            return (T) asker.back();
        }
        else if (asker.confirmed(CONFIRM_CLOSE_QUESTION)) {
            return (T) asker.end();
            // This is the case when the user clicks on "OK" in the end.
            // After we inform the user that the close has been rerouted, we'll redirect to the portal page.
        }
        TravelDocument document = asker.getDocument();
     
        // String previousDocumentId = ((StrutsInquisitor) asker).getForm().getDocId();
        String previousDocumentId = null;
        try {
            // Below used as a place holder to allow code to specify actionForward to return if not a 'success question'
            T returnActionForward = null;
            String newStatus = null;

            returnActionForward = (T) ((StrutsInquisitor) asker).getMapping().findForward(MAPPING_BASIC);
            newStatus = TravelAuthorizationStatusCodeKeys.RETIRED_VERSION;
            
            String message = getMessageFrom(TA_MESSAGE_CLOSE_DOCUMENT_TEXT);
            String user = GlobalVariables.getUserSession().getPerson().getLastName() + ", " + GlobalVariables.getUserSession().getPerson().getFirstName();
            String note = replace(message, "{0}", user);
            
            final Note newNote = getDocumentService().createNoteFromDocument(document, note);
            final Note newNoteTAC = getDocumentService().createNoteFromDocument(document, note);
            getDocumentService().addNoteToDocument(document, newNote); 
            ((TravelDocumentBase) document).updateAppDocStatus(newStatus);
            getDocumentDao().save(document);
            
            String headerID = document.getDocumentHeader().getDocumentNumber();
            TravelAuthorizationCloseDocument tacDocument = ((TravelAuthorizationDocument) document).toCopyTAC();
            getDocumentService().addNoteToDocument(tacDocument, newNoteTAC);            
            getTravelDocumentService().adjustEncumbranceForClose(tacDocument);                        
            //getDocumentService().saveDocument(tacDocument);
            
            TravelAuthorizationForm form = (TravelAuthorizationForm) ((StrutsInquisitor) asker).getForm();
            form.setDocTypeName(TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT);
            form.setDocument(tacDocument);
                        
            // add relationship
            String relationDescription = "TA - TAC";
            accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(document.getDocumentNumber(), tacDocument.getDocumentNumber(), relationDescription));            
            
            tacDocument.updateAppDocStatus(TravelAuthorizationStatusCodeKeys.CLOSED);
            getDocumentService().routeDocument(tacDocument, form.getAnnotation(), null);
            
            if (isNotNull(returnActionForward)) {
                return returnActionForward;
            }
            else   {
                message = getMessageFrom(CONFIRM_CLOSE_QUESTION_TEXT);
                return (T) asker.confirm(CLOSE_TA_QUESTION, message, true, "Could not get reimbursement total for travel id ", tacDocument.getTravelDocumentIdentifier().toString(),"","");
            }
        }
        catch (ValidationException ve) {
            throw ve;
        }
    }

    @Override
    public <T> T askQuestion(final Inquisitive<TravelDocument,?> asker) throws Exception {
        T retval = (T) asker.confirm(CLOSE_TA_QUESTION, CONFIRM_CLOSE_QUESTION_TEXT, false);
        return retval;
 
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
     * Sets the kualiConfigurationService attribute.
     * 
     * @return Returns the kualiConfigurationService.
     */
    public void setConfigurationService(final KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Gets the kualiConfigurationService attribute.
     * 
     * @return Returns the kualiConfigurationService.
     */
    protected KualiConfigurationService getConfigurationService() {
        return kualiConfigurationService;
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

    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    public AccountingDocumentRelationshipService getAccountingDocumentRelationshipService() {
        return accountingDocumentRelationshipService;
    }

    public void setAccountingDocumentRelationshipService(AccountingDocumentRelationshipService accountingDocumentRelationshipService) {
        this.accountingDocumentRelationshipService = accountingDocumentRelationshipService;
    }
}