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

import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * A branching validation which will redirect to a "validateTravelAdvance" composite validation if the travel advance looks to need validation
 */
public class TravelAdvanceBranchingValidation extends BranchingValidation {
    public final static String VALIDATE_TRAVEL_ADVANCE_BRANCH = "validateTravelAdvance";
    protected TravelAuthorizationDocument travelAuthorizationDocumentForValidation;

    /**
     * Returns "validateTravelAdvance" if the travelAuthorizationDocument (or child type) has a travel advance which seems to need authorization
     * @see org.kuali.kfs.sys.document.validation.BranchingValidation#determineBranch(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        if (!ObjectUtils.isNull(getTravelAuthorizationDocumentForValidation().getTravelAdvance()) && getTravelAuthorizationDocumentForValidation().getTravelAdvance().isAtLeastPartiallyFilledIn()) {
            return VALIDATE_TRAVEL_ADVANCE_BRANCH;
        }
        return null;
    }

    /**
     * @return the travel authorization document for validation
     */
    public TravelAuthorizationDocument getTravelAuthorizationDocumentForValidation() {
        return travelAuthorizationDocumentForValidation;
    }

    /**
     * Sets a travel authorization document to potentially validate the travel advance of
     * @param travelAuthorizationDocumentForValidation the travel authorization document to inspect
     */
    public void setTravelAuthorizationDocumentForValidation(TravelAuthorizationDocument travelAuthorizationDocumentForValidation) {
        this.travelAuthorizationDocumentForValidation = travelAuthorizationDocumentForValidation;
    }
}
