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
package org.kuali.kfs.module.tem.document.validation.event;

import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class AddEmergencyContactLineEvent extends AttributedDocumentEventBase implements EmergencyContactLineEvent {

    private final TravelerDetailEmergencyContact emergencyContact;

    /**
     * Constructs an AddGroupTravelLineEvent with the given errorPathPrefix, document, and travelAdvance.
     *
     * @param errorPathPrefix
     * @param document
     * @param emergencyContact
     */
    public AddEmergencyContactLineEvent(String errorPathPrefix, Document document, TravelerDetailEmergencyContact emergencyContact) {
        super("adding emergencyContactLine to document " + getDocumentId(document), errorPathPrefix, document);
        this.emergencyContact = emergencyContact;
    }

    @Override
    public TravelerDetailEmergencyContact getEmergencyContact() {
        return emergencyContact;
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

}
