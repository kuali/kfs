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

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.validation.event.AddActualExpenseDetailLineEvent;
import org.kuali.kfs.module.tem.document.validation.event.AddActualExpenseLineEvent;
import org.kuali.kfs.module.tem.document.validation.event.AddImportedExpenseDetailLineEvent;
import org.kuali.kfs.module.tem.document.validation.event.AddImportedExpenseLineEvent;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * On Travel Authorization documents, gives a warning or error if the document is a blanket document
 */
public class TravelAuthBlanketTripExpenseValidation extends GenericValidation {

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
                if (isWarningEvent(event)) {
                    GlobalVariables.getMessageMap().putWarning(event.getErrorPathPrefix()+"."+TemPropertyConstants.EXPENSE_TYPE_CODE, TemKeyConstants.ERROR_TRAVEL_DOCUMENT_EXPENSES_ON_BLANKET_TRAVEL);
                } else {
                    if ((travelAuth.getActualExpenses() != null && !travelAuth.getActualExpenses().isEmpty()) || (travelAuth.getImportedExpenses() != null && !travelAuth.getImportedExpenses().isEmpty())) {
                        GlobalVariables.getMessageMap().putError(TemPropertyConstants.ACTUAL_EXPENSES+"[0]."+TemPropertyConstants.EXPENSE_TYPE_CODE, TemKeyConstants.ERROR_TRAVEL_DOCUMENT_EXPENSES_ON_BLANKET_TRAVEL);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given event should only lead to a warning
     * @param event the event to check only to warn on
     * @return true if only a warning should occur, false otherwise
     */
    protected boolean isWarningEvent(AttributedDocumentEvent event) {
        // note: the imported expense line events should never occur but we're just being safe
        return event instanceof AddActualExpenseLineEvent || event instanceof AddActualExpenseDetailLineEvent || event instanceof AddImportedExpenseLineEvent || event instanceof AddImportedExpenseDetailLineEvent;
    }

}
