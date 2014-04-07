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
