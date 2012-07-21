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

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.validation.event.AddActualExpenseDetailLineEvent;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class TravelDocumentActualExpenseDetailLineValidation extends TEMDocumentExpenseLineValidation {
   
    public TravelDocumentActualExpenseDetailLineValidation() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        AddActualExpenseDetailLineEvent<ActualExpense> addActualExpenseDetailEvent = (AddActualExpenseDetailLineEvent<ActualExpense>) event;
        TravelDocument travelDocument = (TravelDocument) addActualExpenseDetailEvent.getDocument();
        String[] tempStr = addActualExpenseDetailEvent.getErrorPathPrefix().split("\\[");
        String temp = tempStr[1];
        temp = tempStr[1].split("\\]")[0];
        int index = Integer.parseInt(temp);
        ActualExpense actualExpense = travelDocument.getActualExpenses().get(index);
        ActualExpense actualExpenseDetail = addActualExpenseDetailEvent.getExpenseLine();
        actualExpenseDetail.setTravelCompanyCodeName(actualExpense.getTravelCompanyCodeName());
        List errors = GlobalVariables.getMessageMap().getErrorPath();
        
        return validateDetail(actualExpense, actualExpenseDetail, travelDocument);
    }

    public boolean validateDetail(ActualExpense actualExpense, ActualExpense actualExpenseDetail, TravelDocument travelDocument){
        boolean success = getDictionaryValidationService().isBusinessObjectValid(actualExpenseDetail, "");

        if (success) {
            if (actualExpense.isMileage()) {
                success = validateMileageRules(actualExpenseDetail, travelDocument);
            }
            else {
                if (actualExpenseDetail.getExpenseAmount().isLessEqual(KualiDecimal.ZERO)) {
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_TEM_DETAIL_LESS_THAN_ZERO);
                    return false;
                }
                //Will be true if this method occurs in the routing validation
                if (actualExpense.getExpenseDetails().contains(actualExpenseDetail)){
                    return success;
                }
                /*
                 * Determine if the detail is an amount that doesn't go over the threshold 
                 */
                KualiDecimal total = KualiDecimal.ZERO;
                for (TEMExpense detail : actualExpense.getExpenseDetails()) {
                    total = total.add(detail.getExpenseAmount()); 
                }
                KualiDecimal remainder = actualExpense.getExpenseAmount().subtract(total);
                if (actualExpenseDetail.getExpenseAmount().isGreaterThan(remainder)) {
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_TEM_DETAIL_GREATER_THAN_EXPENSE);
                    return false;
                }
            }
        }

        if (success && !actualExpenseDetail.getCurrencyRate().equals(new KualiDecimal(1))) {
            GlobalVariables.getMessageMap().putInfo(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.INFO_TEM_IMPORT_CURRENCY_CONVERSION);
        }
        
        return success;
    }
    
    /**
     * This method validates following rules 1.Validates whether miles & mileage rate / other mileage rate is entered 2.Validates
     * other mileage rate with the max rate configured in mileage table, if other mileage rate is specified
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    public boolean validateMileageRules(ActualExpense actualExpense, TravelDocument document) {
        boolean success = true;

        // Check to see if miles & mileage rate/other mileage rate is entered
        success = (ObjectUtils.isNotNull(actualExpense.getMiles()) && actualExpense.getMiles() > 0 && (ObjectUtils.isNotNull(actualExpense.getMileageRateId()) || (ObjectUtils.isNotNull(actualExpense.getMileageOtherRate()) && actualExpense.getMileageOtherRate().isGreaterThan(KualiDecimal.ZERO))));
        if (success) {
            if (ObjectUtils.isNotNull(actualExpense.getMileageOtherRate())) {
                KualiDecimal maxMileageRate = getMaxMileageRate();
                if (actualExpense.getMileageOtherRate().isGreaterThan(maxMileageRate)) {
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEM_ACTUAL_EXPENSE_MILES, TemKeyConstants.ERROR_ACTUAL_EXPENSE_OTHER_MILEAGE_RATE_EXCEED, actualExpense.getMileageOtherRate().toString(), maxMileageRate.toString());
                    success = false;
                }
            }

        }
        else {
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEM_ACTUAL_EXPENSE_MILES, KFSKeyConstants.ERROR_REQUIRED, "Miles & mileage rate / other mileage rate");
        }

        return success;
    }

}
