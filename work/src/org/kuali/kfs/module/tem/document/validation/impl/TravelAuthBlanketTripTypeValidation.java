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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelAuthBlanketTripTypeValidation extends GenericValidation {

    //@Override
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;
        TravelAuthorizationDocument taDocument = (TravelAuthorizationDocument)event.getDocument();
        if (!ObjectUtils.isNull(taDocument.getTripType())) {
            if (taDocument.isBlanketTravel()) {
             // If the user selects Blanket Trip Type, airfare amount and the Trip Detail Estimate should not be completed. (Note:
                // Blanket Travel implies in-state travel)
                if (!ObjectUtils.isNull(taDocument.getPerDiemExpenses()) && !taDocument.getPerDiemExpenses().isEmpty()) {
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.PER_DIEM_EXPENSES, TemKeyConstants.ERROR_TA_BLANKET_TYPE_NO_ESTIMATE);
                    taDocument.logErrors();
                    rulePassed = false;
                }
                if (!ObjectUtils.isNull(taDocument.getActualExpenses()) && !taDocument.getActualExpenses().isEmpty()) {
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.NEW_ACTUAL_EXPENSE_LINE, TemKeyConstants.ERROR_TA_BLANKET_TYPE_NO_EXPENSES);
                    taDocument.logErrors();
                    rulePassed = false;
                }
                if (!taDocument.getTripType().isBlanketTravel() ) {
                    rulePassed = false;
                    GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." +TemPropertyConstants.TRIP_TYPE_CODE, TemKeyConstants.ERROR_TA_TRIP_TYPE_BLANKET_NOT_ALLOWED);
                }
                if (StringUtils.isBlank(taDocument.getTemProfile().getDefaultChartCode()) || StringUtils.isBlank(taDocument.getTemProfile().getDefaultAccount())) {
                    rulePassed = false;
                    GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." +TemPropertyConstants.BLANKET_IND, TemKeyConstants.ERROR_TA_PROFILE_NOT_COMPLETE_FOR_BLANKET_TRAVEL);
                }
            }
        }

        return rulePassed;
    }
}