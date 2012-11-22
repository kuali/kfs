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
package org.kuali.kfs.module.tem.document.validation.event;

import org.kuali.kfs.module.tem.businessobject.Attendee;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * Event triggered when an {@link OtherExpense} instance or an {@link ActualExpense} instance
 * is added to a {@link Document}
 */
public class AddAttendeeLineEvent<E extends Attendee> extends AttributedDocumentEventBase implements AttendeeLineEvent<E> {

    private final E attendee;

    /**
     * Constructs an AddActualExpenseLineEvent with the given errorPathPrefix, document, and otherExpense.
     *
     * @param errorPathPrefix
     * @param document
     * @param groupTraveler
     */
    public AddAttendeeLineEvent(String errorPathPrefix, Document document, E attendee) {
        super("adding attendee to document " + getDocumentId(document), errorPathPrefix, document);
        this.attendee = attendee;
    }


    /**
     * Overridden to call parent and then clean up the error messages.
     * @see org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
     */
    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        boolean result = super.invokeRuleMethod(rule);
        cleanErrorMessages();
        return result;
    }

    /**
     * Logic to replace generic amount error messages, especially those where extraordinarily large amounts caused format errors
     */
    public void cleanErrorMessages() {

    }


    @Override
    public E getAttendee() {
        // TODO Auto-generated method stub
        return attendee;
    }

}
