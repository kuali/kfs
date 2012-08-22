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

import java.util.List;

import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSPropertyConstants;

public class TEMDocumentExpenseRoutingValidation extends GenericValidation {
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        TravelDocument travelDocument = (TravelDocument) event.getDocument();
        
        List errorPath = GlobalVariables.getMessageMap().getErrorPath();
        TravelDocumentActualExpenseLineValidation validation = new TravelDocumentActualExpenseLineValidation();
        int counter = 0;
        for (ActualExpense actualExpense : travelDocument.getActualExpenses()) {
            GlobalVariables.getMessageMap().addToErrorPath(KNSPropertyConstants.DOCUMENT);
            GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.ACTUAL_EXPENSES + "[" + counter + "]");
            success = validation.validateGeneralRules(actualExpense, travelDocument) 
                        && validation.validateAirfareRules(actualExpense, travelDocument) 
                        && validation.validateRentalCarRules(actualExpense, travelDocument)
                        && validation.validateLodgingRules(actualExpense, travelDocument) 
                        && validation.validateLodgingAllowanceRules(actualExpense, travelDocument) 
                        && validation.validatePerDiemRules(actualExpense, travelDocument) 
                        && validation.validateMaximumAmountRules(actualExpense, travelDocument);

            if (success){
                TravelDocumentActualExpenseDetailLineValidation detailValidation = new TravelDocumentActualExpenseDetailLineValidation();
                int detailCounter = 0;
                for (TEMExpense detail : actualExpense.getExpenseDetails()){
                    GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.EXPENSES_DETAILS + "[" + detailCounter + "]");
                    ActualExpense actualExpenseDetail = (ActualExpense) detail;
                    detailValidation.validateDetail(actualExpense, actualExpenseDetail, travelDocument);
                    GlobalVariables.getMessageMap().removeFromErrorPath(TemPropertyConstants.EXPENSES_DETAILS + "[" + detailCounter + "]");
                    detailCounter++;
                }
            }
            GlobalVariables.getMessageMap().clearErrorPath();
            counter++;
        }
        return success;
    }

}
