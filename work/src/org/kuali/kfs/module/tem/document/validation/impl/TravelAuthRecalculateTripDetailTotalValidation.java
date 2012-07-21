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

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class TravelAuthRecalculateTripDetailTotalValidation extends GenericValidation {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        TravelAuthorizationDocument doc = (TravelAuthorizationDocument)event.getDocument();
        List<PerDiemExpense> estimates = doc.getPerDiemExpenses();
        boolean valid = true;
        
        for(int i = 0; i < estimates.size(); i++) {
            PerDiemExpense estimate = estimates.get(i);
            if(estimate.getLodging() != null) {
                KualiDecimal lodging = estimate.getLodging();
                if(lodging.isLessThan(KualiDecimal.ZERO)) {
                    GlobalVariables.getMessageMap().putError("document.perDiemExpenses[" + i + "].lodging", KFSKeyConstants.ERROR_NEGATIVE_AMOUNT, "Lodging");
                    valid = false;
                }
            }
            
            if(estimate.getMiles() != null) {
                Integer miles = estimate.getMiles();
                if(miles.intValue() < 0) {
                    GlobalVariables.getMessageMap().putError("document.perDiemExpenses[" + i + "].miles", KFSKeyConstants.ERROR_NEGATIVE_AMOUNT, "Miles");
                    valid = false;
                }
            }
        }
                
        if(doc.getPerDiemAdjustment() != null) {
            KualiDecimal perDiemAdjustment = doc.getPerDiemAdjustment();
            if(perDiemAdjustment.isLessThan(KualiDecimal.ZERO)) {
                GlobalVariables.getMessageMap().putError("document.perDiemAdjustment", KFSKeyConstants.ERROR_NEGATIVE_AMOUNT, "Manual Per Diem Adjustment");
                valid = false;
            }
        }
        
        return valid;
    }

}