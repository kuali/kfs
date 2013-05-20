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

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TravelPayment;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Validates that when certain conditions exist, notes are present on the document to explain the conditions
 */
public class TravelPaymentRequiredNotesValidation extends GenericValidation {
    protected TravelDocument travelDocumentForValidation;
    protected TravelPayment travelPaymentForValidation;

    /**
     * Verifies that if special handling or exception attached are checked, notes for them are present.
     * @param event the event triggering this validation
     * @return true if the validation successfully verified the status of the document, false otherwise
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;
        MessageMap errors = GlobalVariables.getMessageMap();

        final boolean noteless = hasNoNotes();
        /* if special handling indicated, must be a note explaining why */
        if (getTravelPaymentForValidation().isSpecialHandlingCode() && noteless) {
            errors.putErrorWithoutFullErrorPath(TemConstants.GENERAL_TRAVEL_PAYMENT_TAB_KEY, KFSKeyConstants.ERROR_SPECIAL_HANDLING_NOTE_MISSING);
            isValid = false;
        }

        /* if exception attached indicated, must be a note explaining why */
        if (getTravelPaymentForValidation().isExceptionAttachedIndicator() && noteless) {
            errors.putErrorWithoutFullErrorPath(TemConstants.GENERAL_TRAVEL_PAYMENT_TAB_KEY, KFSKeyConstants.ERROR_EXCEPTION_ATTACHED_NOTE_MISSING);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Return true if disbursement voucher does not have any notes
     *
     * @param document submitted disbursement voucher document
     * @return whether the given document has no notes
     */
    protected boolean hasNoNotes() {
        final List<Note> notes = getTravelDocumentForValidation().getNotes();
        return (ObjectUtils.isNull(notes) || notes.isEmpty());
    }

    /**
     * @return the travel document which is being validated
     */
    public TravelDocument getTravelDocumentForValidation() {
        return travelDocumentForValidation;
    }

    /**
     * Sets the travel document which is being validated
     * @param travelDocumentForValidation the travel document which is about to get itself all validated
     */
    public void setTravelDocumentForValidation(TravelDocument travelDocumentForValidation) {
        this.travelDocumentForValidation = travelDocumentForValidation;
    }

    /**
     * @return the travel payment which should be validated
     */
    public TravelPayment getTravelPaymentForValidation() {
        return travelPaymentForValidation;
    }

    /**
     * Sets the travel payment which should be validated
     * @param travelPaymentForValidation the travel payment which should be validated
     */
    public void setTravelPaymentForValidation(TravelPayment travelPaymentForValidation) {
        this.travelPaymentForValidation = travelPaymentForValidation;
    }
}
