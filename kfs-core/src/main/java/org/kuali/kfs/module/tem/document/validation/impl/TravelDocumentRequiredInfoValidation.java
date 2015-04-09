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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelDocumentRequiredInfoValidation extends GenericValidation{
    protected TravelExpenseService travelExpenseService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        TravelDocument document = (TravelDocument)event.getDocument();

        //Check to see if receipt required
        if(isReceiptRequired(document)){
            //Check to see if missing receipt selected
            if(isMissingReceiptSelected(document)){
                //Check to see if notes entered
                if(!isNotesEnteredForTheMissingReceipts(document)){
                    valid = false;
                    GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(TravelAuthorizationFields.TRAVEL_EXPENSE_NOTES, TemKeyConstants.ERROR_RECEIPT_NOTES_REQUIRED);
                }
            }
            //Check to see if receipt attached.
            else if(!isReceiptAttached(document)){
                valid = false;
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(TemPropertyConstants.ATTACHMENT_FILE, TemKeyConstants.ERROR_ATTACHMENT_REQUIRED);
            }
        }

        return valid;
    }

    private boolean isReceiptRequired(TravelDocument document){

        for(ActualExpense actualExpense: document.getActualExpenses()){
            ExpenseTypeObjectCode expenseTypeCode = actualExpense.getExpenseTypeObjectCode();
            if(expenseTypeCode!=null && expenseTypeCode.isReceiptRequired()
                    && getTravelExpenseService().isTravelExpenseExceedReceiptRequirementThreshold(actualExpense)){
                return true;
            }
        }

        for(ImportedExpense importedExpense: document.getImportedExpenses()){
            if(importedExpense.getReceiptRequired() != null && importedExpense.getReceiptRequired()){
                return true;
            }
        }

        return false;
    }

    private boolean isReceiptAttached(TravelDocument document){
        for(Note note: document.getNotes()){
            if(note.getAttachment() != null && StringUtils.equalsIgnoreCase(note.getAttachment().getAttachmentTypeCode(), TemConstants.AttachmentTypeCodes.ATTACHMENT_TYPE_RECEIPT)){
                return true;
            }
        }
        return false;
    }

    private boolean isMissingReceiptSelected(TravelDocument document){
        for(ActualExpense actualExpense: document.getActualExpenses()){
            ExpenseTypeObjectCode expenseTypeCode = actualExpense.getExpenseTypeObjectCode();
            if(expenseTypeCode.isReceiptRequired() && getTravelExpenseService().isTravelExpenseExceedReceiptRequirementThreshold(actualExpense) && actualExpense.getMissingReceipt() != null && actualExpense.getMissingReceipt()){
                return true;
            }
        }

        for(ImportedExpense importedExpense: document.getImportedExpenses()){
            if(importedExpense.getReceiptRequired() != null && importedExpense.getReceiptRequired() && importedExpense.getMissingReceipt() != null && importedExpense.getMissingReceipt()){
                return true;
            }
        }

        return false;
    }

    private boolean isNotesEnteredForTheMissingReceipts(TravelDocument document){
        for(ActualExpense actualExpense: document.getActualExpenses()){
            if(actualExpense.getMissingReceipt() != null && actualExpense.getMissingReceipt()){
                if(actualExpense.getNotes() == null || actualExpense.getNotes().length() == 0){
                    return false;
                }
            }
        }

        for(ImportedExpense importedExpense: document.getImportedExpenses()){
            if(importedExpense.getMissingReceipt() != null && importedExpense.getMissingReceipt()){
                if(importedExpense.getNotes() == null || importedExpense.getNotes().length() == 0){
                    return false;
                }
            }
        }

        return true;
    }

    public TravelExpenseService getTravelExpenseService() {
        return travelExpenseService;
    }

    public void setTravelExpenseService(TravelExpenseService travelExpenseService) {
        this.travelExpenseService = travelExpenseService;
    }
}
