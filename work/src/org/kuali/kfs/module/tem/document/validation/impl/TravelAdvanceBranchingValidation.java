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
