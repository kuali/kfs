/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.ar.rules;

import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.module.ar.document.CustomerCreditMemoDocument;

/**
 * This class holds the business rules for the AR Credit Memo Document
 */
public class CustomerCreditMemoDocumentRule extends AccountingDocumentRuleBase {
  
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        CustomerCreditMemoDocument cmDocument = (CustomerCreditMemoDocument)document;
        isValid = checkReferenceInvoiceNumber(cmDocument);
           
        
        return isValid;
    }
    
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        CustomerCreditMemoDocument cmDocument = (CustomerCreditMemoDocument)document;
        if (isValid) {
            
        }
        return isValid;
    }
    
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = super.processCustomApproveDocumentBusinessRules(approveEvent);
        CustomerCreditMemoDocument cmDocument = (CustomerCreditMemoDocument)approveEvent.getDocument();
        if (isValid) {
            
        }
        return isValid;
    }
    
    
    private boolean checkReferenceInvoiceNumber(CustomerCreditMemoDocument document) {
        boolean isValid = false;
        return isValid;
        
    }
}
