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
