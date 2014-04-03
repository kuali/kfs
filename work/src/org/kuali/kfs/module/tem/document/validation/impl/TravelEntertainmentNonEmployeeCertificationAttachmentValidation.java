/*
 * Copyright 2014 The Kuali Foundation.
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
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelEntertainmentNonEmployeeCertificationAttachmentValidation extends GenericValidation  {
protected ParameterService parameterService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;


        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        final boolean nonEmployeeCertificationRequired = parameterService.getParameterValueAsBoolean(TravelEntertainmentDocument.class, TemConstants.TravelEntertainmentParameters.NON_EMPLOYEE_CERTIFICATION_REQUIRED_IND);

        TravelEntertainmentDocument document = (TravelEntertainmentDocument) event.getDocument();
        boolean nonEmployeeFormAttached = false;
        List<Note> notes = document.getNotes();
        for (Note note : notes) {
            if (ObjectUtils.isNotNull(note.getAttachment()) && TemConstants.AttachmentTypeCodes.NON_EMPLOYEE_FORM.equals(note.getAttachment().getAttachmentTypeCode())) {
                nonEmployeeFormAttached = true;
                break;
            }
        }


        // if host is non-employee and NON_EMPLOYEE_CERTIFICATION_REQUIRED_IND set to "Y" than entertainment host certification is required ; otherwise not
        if (nonEmployeeCertificationRequired && document.IsHostNonEmployee() && !nonEmployeeFormAttached) {
            valid = false;
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.EntertainmentFields.FROM_DOCUMENT_NUMBER, TemKeyConstants.NON_EMPLOYEE_CERTIFICATION_REQUIRED_IND);
            final String matchingErrorPath = StringUtils.join(GlobalVariables.getMessageMap().getErrorPath(), ".") + "." + TemPropertyConstants.EntertainmentFields.FROM_DOCUMENT_NUMBER;
            GlobalVariables.getMessageMap().removeAllWarningMessagesForProperty(matchingErrorPath);
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        return valid;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }



}
