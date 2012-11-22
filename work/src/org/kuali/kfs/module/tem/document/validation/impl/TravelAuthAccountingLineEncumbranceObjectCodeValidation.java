/*
 * Copyright 2012 The Kuali Foundation.
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
