/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * The Budget Adjustment's variation on the lines required for routing met validation - here, we just need
 * to make sure that the total number of accounting lines on the document is greater than 0.
 */
public class BudgetAdjustmentAccountingLinesRequiredForRoutingValidation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;

    /**
     * The BA document does not have to have source accounting lines. In the case of setting up a budget for a new account, only targets
     * line (increase section) are setup.
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean sucess = true;
        
        // check that both source and target are not empty, in which case is an error
        if (getAccountingDocumentForValidation().getSourceAccountingLines().isEmpty() && getAccountingDocumentForValidation().getTargetAccountingLines().isEmpty()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_NO_ACCOUNTING_LINES);
            return sucess&= false;
        }
        
        Document document = event.getDocument();
        int sourceSectionSize = getAccountingDocumentForValidation().getSourceAccountingLines().size();
        int targetSectionSize = getAccountingDocumentForValidation().getTargetAccountingLines().size();

        
        String documentTypeLabel= SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName()).getLabel();
         if(documentTypeLabel.equals(KFSConstants.YEAR_END_BUDGET_ADJUSTMENT_LABEL)){
            if ((sourceSectionSize == 0 && targetSectionSize < 2) || (targetSectionSize == 0 && sourceSectionSize < 2)) {
                GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_OPTIONAL_ONE_SIDED_DOCUMENT_REQUIRED_NUMBER_OF_ACCOUNTING_LINES_NOT_MET);
                sucess&= false;
            }
        }
        else{
            if(documentTypeLabel.equals(KFSConstants.BUDGET_REALLOCATION_LABEL)){
                if (getAccountingDocumentForValidation().getSourceAccountingLines().isEmpty()) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_SOURCE_SECTION_NO_ACCOUNTING_LINES,KFSConstants.FROM);
                    sucess&=false;
                }
                if (getAccountingDocumentForValidation().getTargetAccountingLines().isEmpty()) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.TARGET_ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_TARGET_SECTION_NO_ACCOUNTING_LINES,KFSConstants.TO);
                    sucess&=false;
                }
            }
        }

        return sucess;

       
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
