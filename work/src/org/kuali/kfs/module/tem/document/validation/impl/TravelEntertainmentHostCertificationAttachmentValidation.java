/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelEntertainmentHostCertificationAttachmentValidation extends GenericValidation {
    protected boolean warningOnly = true;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;

        GlobalVariables.getMessageMap().clearErrorPath();
        
        TravelEntertainmentDocument document = (TravelEntertainmentDocument) event.getDocument();
        boolean entertainmentHostAttached = false;
        List<Note> notes = document.getNotes();
        for (Note note : notes) {
            if (ObjectUtils.isNotNull(note.getAttachment()) && TemConstants.AttachmentTypeCodes.ATTACHMENT_TYPE_ENT_HOST_CERT.equals(note.getAttachment().getAttachmentTypeCode())) {
                entertainmentHostAttached = true;
                break;
            }
        }


        // if host is not as payee than entertainment host certification is required ; otherwise not
        if (!document.getHostAsPayee() && !entertainmentHostAttached) {
            valid = addError();
        }

        return valid;
    }

    /**
     * Adds an error
     *
     *
     * @param warningOnly whether error should be a true error or a warning only
     * @return true if rule suceeded, false otherwise
     */
    protected boolean addError() {
        boolean success = true;
        if (warningOnly) {
            GlobalVariables.getMessageMap().putWarning(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.EntertainmentFields.HOST_NAME, TemKeyConstants.HOST_CERTIFICATION_REQUIRED_IND);
        } else {
            success = false;
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.EntertainmentFields.HOST_NAME, TemKeyConstants.HOST_CERTIFICATION_REQUIRED_IND);
            final String matchingErrorPath = StringUtils.join(GlobalVariables.getMessageMap().getErrorPath(), ".") + "." + TemPropertyConstants.EntertainmentFields.HOST_NAME;
            GlobalVariables.getMessageMap().removeAllWarningMessagesForProperty(matchingErrorPath);
        }
        return success;
    }


    public boolean isWarningOnly() {
        return warningOnly;
    }

    public void setWarningOnly(boolean warningOnly) {
        this.warningOnly = warningOnly;
    }


}
