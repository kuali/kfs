/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelEntertainmentDocumentPreRules extends PromptBeforeValidationBase{

    @SuppressWarnings("rawtypes")
    @Override
    public boolean doPrompts(Document document) {

        TravelEntertainmentDocument entDoc = (TravelEntertainmentDocument) document;
        boolean attendeelistAttached = false;
        boolean nonEmployeeFormAttached = false;
        boolean hostCertificationAttached = false;

        List<Note> notes = entDoc.getNotes();
        for (Note note  : notes) {
            if (ObjectUtils.isNotNull(note.getAttachment())&& TemConstants.AttachmentTypeCodes.ATTACHMENT_TYPE_ATTENDEE_LIST.equals(note.getAttachment().getAttachmentTypeCode())) {
                attendeelistAttached = true;
            }
            if (ObjectUtils.isNotNull(note.getAttachment())&& TemConstants.AttachmentTypeCodes.NON_EMPLOYEE_FORM.equals(note.getAttachment().getAttachmentTypeCode())) {
                nonEmployeeFormAttached = true;
            }

        }

        boolean shouldAskQuestion = false;
        String question = "";
        String proceed=SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TemKeyConstants.TEM_ENT_QUESTION_PROCEED);
        if(entDoc.getAttendeeListAttached()!=null&&entDoc.getAttendeeListAttached()&&!attendeelistAttached){
            question = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TemKeyConstants.TEM_ENT_DOC_ATTENDEE_LIST_QUESTION);
            shouldAskQuestion = true;
        }
        else if (entDoc.getNonEmployeeCertified()!=null&&entDoc.getNonEmployeeCertified()&&!nonEmployeeFormAttached){
            question = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TemKeyConstants.TEM_ENT_NON_EMPLOYEE_FORM_QUESTION);
            shouldAskQuestion = true;
        }


        if (shouldAskQuestion) {
            boolean userClickedYes = super.askOrAnalyzeYesNoQuestion("ENT_WARNING", question + proceed);
            if (!userClickedYes) {
                this.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
            }
            return userClickedYes;
        }
        else {
            //no question necessary- continue as normal
            return true;
        }
    }
}
