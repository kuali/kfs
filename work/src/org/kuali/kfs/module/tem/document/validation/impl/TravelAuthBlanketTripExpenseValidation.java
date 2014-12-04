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

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * On Travel Authorization documents, gives a warning or error if the document is a blanket document
 */
public class TravelAuthBlanketTripExpenseValidation extends GenericValidation {
    protected List<Class<? extends AttributedDocumentEvent>> alwaysErrorEvents;

    /**
     * Checks if the event is a travel authorization and is set to blanket travel; then - if the event is adding an expense, then warns about it;
     * otherwise, checks if there are any actual or imported (imported shouldn't happen but we're trying to be safe) events on the document and if so,
     * gives an error about it
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        if (event.getDocument() instanceof TravelAuthorizationDocument) {
            final TravelAuthorizationDocument travelAuth = (TravelAuthorizationDocument)event.getDocument();
            if (travelAuth.getBlanketTravel().booleanValue()) {
                if (isAlwaysErrorEvent(event) || (travelAuth.getActualExpenses() != null && !travelAuth.getActualExpenses().isEmpty()) || (travelAuth.getImportedExpenses() != null && !travelAuth.getImportedExpenses().isEmpty())) {
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.ACTUAL_EXPENSES+"[0]."+TemPropertyConstants.EXPENSE_TYPE_CODE, TemKeyConstants.ERROR_TRAVEL_DOCUMENT_EXPENSES_ON_BLANKET_TRAVEL);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if we should always error out on the given event
     * @param event the event to error out on
     * @return true if the event should always error out on, false otherwise
     */
    protected boolean isAlwaysErrorEvent(AttributedDocumentEvent event) {
        if (alwaysErrorEvents != null && !alwaysErrorEvents.isEmpty()) {
            return alwaysErrorEvents.contains(event.getClass());
        }
        return false;
    }

    public List<Class<? extends AttributedDocumentEvent>> getAlwaysErrorEvents() {
        return alwaysErrorEvents;
    }

    public void setAlwaysErrorEvents(List<Class<? extends AttributedDocumentEvent>> alwaysErrorEvents) {
        this.alwaysErrorEvents = alwaysErrorEvents;
    }
}
