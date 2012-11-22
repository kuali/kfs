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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelAuthTripDetailLodgingValidation extends GenericValidation {

    //@Override
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;
        TravelAuthorizationDocument taDocument = (TravelAuthorizationDocument)event.getDocument();
        
        // check for negative amounts
        for (PerDiemExpense estimate : taDocument.getPerDiemExpenses()) {
            if (estimate.getLodging() != null) {
                if (estimate.getLodging().isNegative()) {
                    GlobalVariables.getMessageMap().putError("document.perDiemExpenses", TemKeyConstants.ERROR_TA_NO_NEGATIVE_AMOUNT);
                    rulePassed = false;
                }
            }
        }

        return rulePassed;
    }

}