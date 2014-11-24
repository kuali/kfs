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

import java.math.BigDecimal;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.validation.event.AddImportedExpenseDetailLineEvent;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelDocumentImportedExpenseDetailLineValidation extends GenericValidation {
    protected DictionaryValidationService dictionaryValidationService;

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

        if (success && !importedExpenseDetail.getCurrencyRate().equals(BigDecimal.ONE)){
            GlobalVariables.getMessageMap().putInfo(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.INFO_TEM_IMPORT_CURRENCY_CONVERSION);
        }
        return success;
    }

    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }
}
