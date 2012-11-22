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
import static org.kuali.kfs.module.tem.TemConstants.CONFIRM_AMENDMENT_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.RETURN_TO_FO_NOTE_PREFIX;
import static org.kuali.kfs.module.tem.TemConstants.RETURN_TO_FO_QUESTION;
import static org.kuali.kfs.module.tem.TemConstants.RETURN_TO_FO_TEXT;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_REASON_PASTLIMIT;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_REASON_REQUIRED;
import static org.kuali.kfs.module.tem.TemKeyConstants.TA_QUESTION_DOCUMENT;
import static org.kuali.kfs.sys.KFSConstants.BLANK_SPACE;
import static org.kuali.kfs.sys.KFSConstants.NOTE_TEXT_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME;
import static org.kuali.rice.kns.util.ObjectUtils.isNull;

import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class ReturnToFiscalOfficerQuestionHandler implements QuestionHandler<TravelDocument> {
    private ConfigurationService ConfigurationService;
    private DataDictionaryService dataDictionaryService;
    private TravelDocumentService travelDocumentService;
    
    @Override
    public <T> T handleResponse(final Inquisitive<TravelDocument,?> asker) throws Exception {
        if (asker.denied(RETURN_TO_FO_QUESTION)) {
            return (T) asker.back();
        }
        else if (asker.confirmed(CONFIRM_AMENDMENT_QUESTION)) {
            return (T) asker.end();
            // This is the case when the user clicks on "OK" in the end.
            // After we inform the user that the close has been rerouted, we'll redirect to the portal page.
        }

        final String noteStr = getReturnToFiscalOfficerNote(RETURN_TO_FO_NOTE_PREFIX, asker.getReason());
        final String message = getReturnToFiscalOfficerQuestion(RETURN_TO_FO_TEXT);

        int noteTextMaxLength = getDataDictionaryService().getAttributeMaxLength(Note.class, NOTE_TEXT_PROPERTY_NAME).intValue();
        String question = replace(message, "{0}", RETURN_TO_FO_TEXT);
        if (isBlank(asker.getReason())) {
            return (T) asker.confirm(RETURN_TO_FO_QUESTION, question, true, ERROR_TA_REASON_REQUIRED, QUESTION_REASON_ATTRIBUTE_NAME, RETURN_TO_FO_TEXT);
        }else if(noteStr.length() > noteTextMaxLength){
            return (T) asker.confirm(RETURN_TO_FO_QUESTION, question, true, ERROR_TA_REASON_PASTLIMIT, QUESTION_REASON_ATTRIBUTE_NAME, new Integer(noteStr.length() - noteTextMaxLength).toString());
        }

        final TravelDocument document = asker.getDocument();
        getTravelDocumentService().routeToFiscalOfficer(document, noteStr);
        return (T) asker.finish();
    }

    @Override
    public <T> T askQuestion(final Inquisitive<TravelDocument,?> asker) throws Exception {
        //final Object question = asker.getQuestion();
        //final String reason   = asker.getReason();
        final String message  = getReturnToFiscalOfficerQuestion(RETURN_TO_FO_TEXT);
        
        T retval = (T) asker.confirm(RETURN_TO_FO_QUESTION, message, true);
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
     * Sets the travelDocumentService attribute.
     * 
     * @return Returns the travelDocumentService.
     */
    public void setTravelDocumentService(final TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    /**
     * Gets the travelDocumentService attribute.
     * 
     * @return Returns the travelDocumentService.
     */
    protected TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
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