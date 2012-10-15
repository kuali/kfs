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
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.util.GlobalVariables;

public class TravelDocumentRequiredInfoValidation extends GenericValidation{
    public static final String ATTACHMENT_TYPE_CODE_RECEIPT = "RECEIPT";
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
            TemTravelExpenseTypeCode expenseTypeCode = actualExpense.getTravelExpenseTypeCode();
            if(expenseTypeCode!=null && expenseTypeCode.getReceiptRequired() != null && expenseTypeCode.getReceiptRequired()
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
        for(Note note: (List<Note>)document.getBoNotes()){
            if(note.getAttachment() != null && note.getAttachment().getAttachmentTypeCode().equalsIgnoreCase(ATTACHMENT_TYPE_CODE_RECEIPT)){
                return true;
            }
        }
        return false;
    }
    
    private boolean isMissingReceiptSelected(TravelDocument document){
        for(ActualExpense actualExpense: document.getActualExpenses()){
            TemTravelExpenseTypeCode expenseTypeCode = actualExpense.getTravelExpenseTypeCode();
            if(expenseTypeCode.getReceiptRequired() != null && expenseTypeCode.getReceiptRequired() && getTravelExpenseService().isTravelExpenseExceedReceiptRequirementThreshold(actualExpense) && actualExpense.getMissingReceipt() != null && actualExpense.getMissingReceipt()){
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
    
    /**
     * 
     * @return
     */
    private TravelExpenseService getTravelExpenseService(){
        return SpringContext.getBean(TravelExpenseService.class);
    }
    
}
