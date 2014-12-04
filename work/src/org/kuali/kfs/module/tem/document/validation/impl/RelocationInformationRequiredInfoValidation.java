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

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelRelocationFields;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class RelocationInformationRequiredInfoValidation extends GenericValidation {
    public static final String USA_COUNTRY_CODE = "US";
    public static final String ATTACHMENT_TYPE_CODE_RECEIPT = "RECEIPT";

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;

        TravelRelocationDocument document = (TravelRelocationDocument) event.getDocument();

        // Check to see if from country is selected. If selected from state is required field
        if (document.getFromCountryCode() != null && document.getFromCountryCode().equals(USA_COUNTRY_CODE)) {
            valid = !(document.getFromStateCode() == null || document.getFromStateCode().equals(""));

            if (!valid) {
                GlobalVariables.getMessageMap().putError(TravelRelocationFields.FROM_STATE, TemKeyConstants.ERROR_RELO_FROM_STATE_REQUIRED);
                return valid;
            }
        }

        // Check to see if to country is selected. If selected to state is required field
        if (document.getToCountryCode() != null && document.getToCountryCode().equals(USA_COUNTRY_CODE)) {
            valid = !(document.getToStateCode() == null || document.getToStateCode().equals(""));

            if (!valid) {
                GlobalVariables.getMessageMap().putError(TravelRelocationFields.TO_STATE, TemKeyConstants.ERROR_RELO_TO_STATE_REQUIRED);
                return valid;
            }
        }           

        if (document.getDocumentGrandTotal().isLessEqual(KualiDecimal.ZERO)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.TRVL_AUTH_TOTAL_ESTIMATE, TemKeyConstants.ERROR_DOCUMENT_TOTAL_ESTIMATED);
            valid = false;
        }
        
        return valid;
    }

}
