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

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelAuthAccountingLineEncumbranceObjectCodeValidation extends GenericValidation {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;
        TravelAuthorizationDocument authorizationDocument = (TravelAuthorizationDocument) event.getDocument();

        TripType trip = authorizationDocument.getTripType();
        if (authorizationDocument.isTripGenerateEncumbrance()) {
            
            //check in each of the encumbrance source accounting line if the object code matches the one defined in the trip
            for (TemSourceAccountingLine line : authorizationDocument.getEncumbranceSourceAccountingLines()){
                if (!line.getFinancialObjectCode().equals(trip.getEncumbranceObjCode()) && rulePassed) {
                    
                    int index = line.getSequenceNumber().intValue()-1;
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + index + "]." + TravelAuthorizationFields.FIN_OBJ_CD, TemKeyConstants.ERROR_TA_ENCUMBRANCE_OBJ_CD_INVALID,
                            trip.getEncumbranceObjCode(), trip.getName());
                    rulePassed = false;
                }
            }
        }

        return rulePassed;
    }
}
