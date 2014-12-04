/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        }

        boolean shouldAskQuestion = false;
        String question = "";
        String proceed=SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TemKeyConstants.TEM_ENT_QUESTION_PROCEED);
        if(entDoc.getAttendeeListAttached()!=null&&entDoc.getAttendeeListAttached()&&!attendeelistAttached){
            question = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TemKeyConstants.TEM_ENT_DOC_ATTENDEE_LIST_QUESTION);
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
