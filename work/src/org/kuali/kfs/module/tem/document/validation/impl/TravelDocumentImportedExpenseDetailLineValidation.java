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
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.validation.event.AddImportedExpenseDetailLineEvent;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class TravelDocumentImportedExpenseDetailLineValidation extends GenericValidation {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        AddImportedExpenseDetailLineEvent<ImportedExpense> addImportedExpenseDetailEvent = (AddImportedExpenseDetailLineEvent<ImportedExpense>) event;
        TravelDocument travelDocument = (TravelDocument) addImportedExpenseDetailEvent.getDocument();
        String[] tempStr = addImportedExpenseDetailEvent.getErrorPathPrefix().split("\\[");
        String temp = tempStr[1];
        temp = tempStr[1].split("\\]")[0];
        int index = Integer.parseInt(temp);
        ImportedExpense importedExpense = travelDocument.getImportedExpenses().get(index);
        ImportedExpense importedExpenseDetail = addImportedExpenseDetailEvent.getExpenseLine();
        importedExpenseDetail.setTravelCompanyCodeName(importedExpense.getTravelCompanyCodeName());
        List errors = GlobalVariables.getMessageMap().getErrorPath();
        boolean success = true;
        success = getDictionaryValidationService().isBusinessObjectValid(importedExpenseDetail, "");
        
        if (success){
            if (importedExpenseDetail.getExpenseAmount().isLessEqual(KualiDecimal.ZERO)){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_TEM_DETAIL_LESS_THAN_ZERO);
                return false;
            }
            
            if (importedExpenseDetail.getCurrencyRate().equals(new KualiDecimal(1))){
                /*
                 * Determine if the detail is an amount that doesn't go over the threshold (taking a buffer into account for conversions)
                 */
                KualiDecimal total = importedExpense.getTotalDetailExpenseAmount();
                KualiDecimal remainder = importedExpense.getConvertedAmount().subtract(total);
                if (importedExpenseDetail.getConvertedAmount().isGreaterThan(remainder)){
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_TEM_DETAIL_GREATER_THAN_EXPENSE);
                    return false;
                }
            }
            
        }
        
        if (success && !importedExpenseDetail.getCurrencyRate().equals(new KualiDecimal(1))){
            GlobalVariables.getMessageMap().putInfo(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.INFO_TEM_IMPORT_CURRENCY_CONVERSION);
        }
        return success;
    }

    public DictionaryValidationService getDictionaryValidationService() {
        return SpringContext.getBean(DictionaryValidationService.class);
    }
}
