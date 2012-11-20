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
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.validation.event.AddActualExpenseDetailLineEvent;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class TravelDocumentActualExpenseDetailLineValidation extends TEMDocumentExpenseLineValidation {
   
    public TravelDocumentActualExpenseDetailLineValidation() {
        super();
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
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
        
        boolean success = validateDetail(actualExpense, actualExpenseDetail, travelDocument);
        return success;
    }

    /**
     * Validate expense detail rules
     * 
     * 1. expense detail mileage rule
     * 2. expense detail amount is non-zero
     * 3. expense detail does not exceed parent (threshold) for non-mileage expense
     * 
     * @param actualExpense
     * @param actualExpenseDetail
     * @param travelDocument
     * @return
     */
    public boolean validateDetail(ActualExpense actualExpense, ActualExpense actualExpenseDetail, TravelDocument travelDocument){
        boolean success = getDictionaryValidationService().isBusinessObjectValid(actualExpenseDetail, "");
        
        if (success) {
            //validate mileage
            success &= validateMileageRules(actualExpenseDetail, travelDocument);
            
            //validate expense detail amount is negative
            if (actualExpenseDetail.getExpenseAmount().isNegative()) {
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_TEM_DETAIL_LESS_THAN_ZERO);
                success = false;
            }
            
            //for non-mileage expense detail
            if (!actualExpenseDetail.isMileage()){
                // Determine if the detail is an amount that doesn't go over the threshold 
                if (actualExpense.getExpenseAmount().isLessThan(actualExpense.getTotalDetailExpenseAmount())) {
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_TEM_DETAIL_GREATER_THAN_EXPENSE);
                    success = false;
                }
            }
        }

        //info for non-one currency
        if (success && !actualExpenseDetail.getCurrencyRate().equals(new KualiDecimal(1))) {
            GlobalVariables.getMessageMap().putInfo(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.INFO_TEM_IMPORT_CURRENCY_CONVERSION);
        }
        
        return success;
    }
    
    /**
     * This method validates following rules 
     * 
     * 1.Validates whether miles & mileage rate / other mileage rate is entered 
     * 2.Validates other mileage rate with the max rate configured in mileage table, if other mileage rate is specified
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    protected boolean validateMileageRules(ActualExpense expenseDetail, TravelDocument document) {
        boolean valid = true;
        if (expenseDetail.isMileage()) {
            // Check to see if miles & mileage rate/other mileage rate is entered
            valid = (ObjectUtils.isNotNull(expenseDetail.getMiles()) && expenseDetail.getMiles() > 0 && (ObjectUtils.isNotNull(expenseDetail.getMileageRateId()) || (ObjectUtils.isNotNull(expenseDetail.getMileageOtherRate()) && expenseDetail.getMileageOtherRate().isGreaterThan(KualiDecimal.ZERO))));
            if (valid) {
                if (ObjectUtils.isNotNull(expenseDetail.getMileageOtherRate())) {
                    KualiDecimal maxMileageRate = getMaxMileageRate();
                    if (expenseDetail.getMileageOtherRate().isGreaterThan(maxMileageRate)) {
                        GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEM_ACTUAL_EXPENSE_MILES, TemKeyConstants.ERROR_ACTUAL_EXPENSE_OTHER_MILEAGE_RATE_EXCEED, expenseDetail.getMileageOtherRate().toString(), maxMileageRate.toString());
                        valid = false;
                    }
                }

            }
            else {
                String label = getDataDictionaryService().getAttributeLabel(ActualExpense.class, TemPropertyConstants.TEM_ACTUAL_EXPENSE_MILES) + ", " + getDataDictionaryService().getAttributeLabel(ActualExpense.class, TemPropertyConstants.TEM_ACTUAL_EXPENSE_MILE_RATE) + " or " + getDataDictionaryService().getAttributeLabel(ActualExpense.class, TemPropertyConstants.TEM_ACTUAL_EXPENSE_MILE_OTHER_RATE);
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEM_ACTUAL_EXPENSE_MILES, KFSKeyConstants.ERROR_REQUIRED, label);
            }
        }
        return valid;
    }
    
    /**
     * 
     * @return
     */
    public final DataDictionaryService getDataDictionaryService() {
        return SpringContext.getBean(DataDictionaryService.class);
    }

}
