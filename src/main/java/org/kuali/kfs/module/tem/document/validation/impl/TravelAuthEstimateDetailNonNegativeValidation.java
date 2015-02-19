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

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class TravelAuthEstimateDetailNonNegativeValidation extends GenericValidation {

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
