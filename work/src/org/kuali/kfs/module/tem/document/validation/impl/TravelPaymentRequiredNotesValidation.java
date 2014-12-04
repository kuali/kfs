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
