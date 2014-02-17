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
