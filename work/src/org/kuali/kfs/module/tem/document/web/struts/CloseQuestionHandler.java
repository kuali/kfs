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
import static org.kuali.kfs.module.tem.TemConstants.CLOSE_TA_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.CONFIRM_CLOSE_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.CONFIRM_CLOSE_QUESTION_TEXT;
import static org.kuali.kfs.sys.KFSConstants.BLANK_SPACE;
import static org.kuali.kfs.sys.KFSConstants.MAPPING_BASIC;
import static org.kuali.kfs.sys.KFSConstants.NOTE_TEXT_PROPERTY_NAME;

import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.document.TravelAuthorizationCloseDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.util.MessageUtils;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 *
 */
public class CloseQuestionHandler implements QuestionHandler<TravelDocument> {
    private DataDictionaryService dataDictionaryService;
    private TravelAuthorizationService travelAuthorizationService;

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
        TravelAuthorizationDocument document = (TravelAuthorizationDocument)asker.getDocument();

        try {
            // Below used as a place holder to allow code to specify actionForward to return if not a 'success question'
            T returnActionForward = (T) ((StrutsInquisitor) asker).getMapping().findForward(MAPPING_BASIC);
            TravelAuthorizationForm form = (TravelAuthorizationForm) ((StrutsInquisitor) asker).getForm();

            TravelAuthorizationCloseDocument tacDocument = travelAuthorizationService.closeAuthorization(document, form.getAnnotation(), GlobalVariables.getUserSession().getPrincipalName());

            form.setDocTypeName(TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT);
            form.setDocument(tacDocument);

            if (ObjectUtils.isNotNull(returnActionForward)) {
                return returnActionForward;
            }
            else   {
                return (T) asker.confirm(CLOSE_TA_QUESTION, MessageUtils.getMessage(CONFIRM_CLOSE_QUESTION_TEXT), true, "Could not get reimbursement total for travel id ", tacDocument.getTravelDocumentIdentifier().toString(),"","");
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
        T retval = (T) asker.confirm(CLOSE_TA_QUESTION, CONFIRM_CLOSE_QUESTION_TEXT, false);
        return retval;
    }

    /**
     *
     * @param notePrefix
     * @param reason
     * @return
     */
    public String getReturnToFiscalOfficerNote(final String notePrefix, String reason) {
        String noteText = "";
        // Have to check length on value entered.
        final String introNoteMessage = notePrefix + BLANK_SPACE;

        // Build out full message.
        noteText = introNoteMessage + reason;
        final int noteTextLength = noteText.length();

        // Get note text max length from DD.
        final int noteTextMaxLength = dataDictionaryService.getAttributeMaxLength(Note.class, NOTE_TEXT_PROPERTY_NAME).intValue();

        if (isBlank(reason) || (noteTextLength > noteTextMaxLength)) {
            // Figure out exact number of characters that the user can enter.
            int reasonLimit = noteTextMaxLength - noteTextLength;

            if (ObjectUtils.isNull(reason)) {
                // Prevent a NPE by setting the reason to a blank string.
                reason = "";
            }
        }
        return noteText;
    }

    public void setDataDictionaryService(final DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setTravelAuthorizationService(TravelAuthorizationService travelAuthorizationService) {
        this.travelAuthorizationService = travelAuthorizationService;
    }

}