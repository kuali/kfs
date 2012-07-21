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

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelRelocationFields;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

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
