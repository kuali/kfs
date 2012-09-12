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
    
    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        TravelDocument travelDocument = (TravelDocument) event.getDocument();
        
        List<String> errorPath = GlobalVariables.getMessageMap().getErrorPath();
        
        //reset to start off from document
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KNSPropertyConstants.DOCUMENT);
        
        TravelDocumentActualExpenseLineValidation validation = new TravelDocumentActualExpenseLineValidation();
        int counter = 0;
        final boolean isWarning = false;
        
        for (ActualExpense actualExpense : travelDocument.getActualExpenses()) {
            String path = TemPropertyConstants.ACTUAL_EXPENSES + "[" + counter + "]";
            GlobalVariables.getMessageMap().addToErrorPath(path);

            success = validation.validateExpenses(actualExpense, travelDocument, isWarning); 

            if (success){
                TravelDocumentActualExpenseDetailLineValidation detailValidation = new TravelDocumentActualExpenseDetailLineValidation();
                int detailCounter = 0;
                for (TEMExpense detail : actualExpense.getExpenseDetails()){
                    String detailPath = TemPropertyConstants.EXPENSES_DETAILS + "[" + detailCounter++ + "]";
                    GlobalVariables.getMessageMap().addToErrorPath(detailPath);
                    
                    success &= detailValidation.validateDetail(actualExpense, (ActualExpense) detail, travelDocument);

                    GlobalVariables.getMessageMap().removeFromErrorPath(detailPath);
                }
            }
            GlobalVariables.getMessageMap().removeFromErrorPath(path);
            counter++;
        }
        
        //reset the error path
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().getErrorPath().addAll(errorPath);
        
        return success;
    }

}
