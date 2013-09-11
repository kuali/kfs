/*
 * Copyright 2010 The Kuali Foundation.
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

import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_DELINQUENT_MSG_REQUIRED;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_AUTH_END_DATE_BEFORE_BEGIN;
import static org.kuali.kfs.sys.context.SpringContext.getBean;

import java.util.Date;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Validation that is run when a user modifies the trip detail information in a {@link TravelDocument}
 */
public class TravelUpdateTripDetailsValidation extends GenericValidation {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        TravelDocument doc = (TravelDocument) event.getDocument();
        Date beginDate = doc.getTripBegin();
        Date endDate = doc.getTripEnd();
        doc.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = doc.getTripType();

        boolean valid = true;

        int preCount = GlobalVariables.getMessageMap().getErrorCount();
        MessageMap errors = GlobalVariables.getMessageMap();
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

//        if (errors.containsKeyMatchingPattern(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.PER_DIEM_NAME + "." + TemPropertyConstants.TravelAuthorizationFields.TRIP_TYPE)
//                && errors.containsKeyMatchingPattern(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.TravelAuthorizationFields.TRIP_TYPE)){
//            errors.removeAllErrorMessagesForProperty(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.PER_DIEM_NAME + "." + TemPropertyConstants.TravelAuthorizationFields.TRIP_TYPE);
//        }
//        else if (errors.containsKeyMatchingPattern(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.PER_DIEM_NAME + "." + TemPropertyConstants.TravelAuthorizationFields.TRIP_TYPE)
//                && !doc.getTripTypeCode().isEmpty()){
//            errors.removeAllErrorMessagesForProperty(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.PER_DIEM_NAME + "." + TemPropertyConstants.TravelAuthorizationFields.TRIP_TYPE);
//        }

        int postCount = GlobalVariables.getMessageMap().getErrorCount();
        if (postCount > preCount) {
            valid = false;
        }

        if (endDate == null || beginDate == null) {
            getDictionaryValidationService().validateDocument(doc);
        }

        if (endDate != null && beginDate != null && endDate.compareTo(beginDate) < 0) {
            valid = false;
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.TRIP_BEGIN_DT, ERROR_TA_AUTH_END_DATE_BEFORE_BEGIN);
        }

        if (doc.getDocumentHeader().getWorkflowDocument().getDocumentTypeName().equals(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)) {
            TravelReimbursementDocument tr = (TravelReimbursementDocument) doc;
            if (tr.getDelinquentAction() != null) {
                if (tr.getDelinquentAction().equals(TemConstants.DELINQUENT_STOP) && !tr.getDelinquentTRException()) {
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.TRIP_END_DT, KFSKeyConstants.ERROR_CUSTOM, "Travel Reimbursement cannot be initiated because of delinquency.");
                }

                if (tr.getDocumentHeader().getExplanation() == null || tr.getDocumentHeader().getExplanation().trim().length() == 0) {
                    GlobalVariables.getMessageMap().putError("document.documentHeader.documentDescription", ERROR_DELINQUENT_MSG_REQUIRED);
                }
            }
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);


        if (doc.getTripType() != null && !doc.getTripType().getUsePerDiem() && doc.getPerDiemExpenses() != null && !doc.getPerDiemExpenses().isEmpty()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.PER_DIEM_EXP, KFSKeyConstants.ERROR_CUSTOM, "Per Diem entry is not allowed for this Trip Type [" + doc.getTripType().getCode() + "].");
            valid = false;
        }

        if (doc.getPrimaryDestinationName() == null || doc.getPrimaryDestinationName().trim().length() == 0) {
            GlobalVariables.getMessageMap().putError("document.primaryDestinationName", KFSKeyConstants.ERROR_CUSTOM, "Primary Destination is a required field.");
            valid = false;
        }

        return valid;
    }

    protected DictionaryValidationService getDictionaryValidationService() {
        return getBean(DictionaryValidationService.class);
    }

}
