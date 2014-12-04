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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that at least one of the per diem expenses on a document matches the location of the primary destination on the document
 */
public class TravelDocumentPerDiemPrimaryDestinationMatchValidation extends GenericValidation {

    /**
     * Loops through any per diem expenses, making sure at that least one matches the primary destination of the document
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        final TravelDocument travelDoc = (TravelDocument)event.getDocument();
        final Integer primaryDestId = travelDoc.getPrimaryDestinationId();

        if (primaryDestId != null) {
            if (travelDoc.getPerDiemExpenses() != null && !travelDoc.getPerDiemExpenses().isEmpty()) {
                for (PerDiemExpense perDiemExpense : travelDoc.getPerDiemExpenses()) {
                    if (primaryDestId == TemConstants.CUSTOM_PRIMARY_DESTINATION_ID) { // if the primary destination for the trip is custom, we need to make sure at least one per diem matches all of the name information
                        if (perDiemExpense.getPrimaryDestinationId() == TemConstants.CUSTOM_PRIMARY_DESTINATION_ID &&
                                StringUtils.equals(perDiemExpense.getCountryStateText(), travelDoc.getPrimaryDestinationCountryState()) &&
                                StringUtils.equals(perDiemExpense.getPrimaryDestination(), travelDoc.getPrimaryDestinationName()) &&
                                StringUtils.equals(perDiemExpense.getCounty(), travelDoc.getPrimaryDestinationCounty())) {
                            return true;
                        }
                    } else { // if not custom, then we can just check the primary destination id
                        if (primaryDestId.equals(perDiemExpense.getPrimaryDestinationId())) {
                            return true; // skip out loop - we're fine
                        }
                    }
                }
                // still here?  then we didn't find a match...
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.PER_DIEM_EXPENSES+"[0]."+TemPropertyConstants.PRIMARY_DESTINATION_ID, TemKeyConstants.ERROR_TRAVEL_DOC_PRI_DEST_PER_DIEM_NO_MATCH, travelDoc.getPrimaryDestinationName());
                return false;
            }
        }
        return true;
    }
}
